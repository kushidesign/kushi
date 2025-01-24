(ns kushi.css.hydrated
  (:require
   [clojure.spec.alpha :as s]
   [fireworks.core :refer [? !? ?> !?>]]
   [clojure.string :as string]
   [clojure.walk :refer [prewalk]]
   [kushi.css.defs :as defs]
   [kushi.css.shorthand :as shorthand]
   [kushi.css.specs :as specs]
   [kushi.css.util :refer [keyed
                           vec-of-vecs?
                           more-than-one?
                           partition-by-pred]]))

;; TODO - would there ever be any quoted backticks in css val?
(defn str+ [s]
  (if (string/index-of s "`") 
    (-> s
        (string/replace specs/css-custom-property-re
                        "var(--$1)")
        (string/replace specs/runtime-var-re
                        "var(--_$1)"))
    s))


(defn as-str [x]
  (str (if (or (keyword? x) (symbol? x)) (name x) x)))

(defn hydrated-css-var-fallback [s]
  (when s
    (str ", " 
         (if (string/starts-with? s "$")
           (str "var(--" (subs s 1) ")")
           s))))

(defn hydrated-css-var-with-fallbacks [s]
  (let [[a b c] 
        (take 3 (string/split s #"\|\|"))]
    (str "var(--"
         (subs a 1)
         (hydrated-css-var-fallback b)
         (hydrated-css-var-fallback c)
         ")")))

(defn hydrated-css-var [s]
  (let [css-comma-separated-coll
        (-> s
            (string/replace #"\|\|" "____*DOUBLE-BAR*____")
            (string/split #"\|")
            (->> (map #(string/replace % #"____\*DOUBLE-BAR\*____" "||"))))

        ret                   
        (->> css-comma-separated-coll
             (map 
              (fn [s]
                (map #(if (string/starts-with? % "$")
                        (hydrated-css-var-with-fallbacks %)
                        %)
                     (string/split s #":"))))
             (map #(string/join " " %))
             (string/join ", "))]
        ret))

(defn hydrated-val 
  [p v]
  (let [nv (as-str v)
        np (as-str p)]
    (if-let [m (and (not (re-find #"[-: ]" nv))
                    (get-in shorthand/shorthand-syntax
                            [:enums np]))]
      (get m nv nv)

      (let [runtime-vars-hydrated
            (str+ nv)

            ret
            (hydrated-css-var runtime-vars-hydrated)]
        ret))))

(defn hydrated-prop 
  [v]
  (let [as-str (name v)
        m      shorthand/shorthand-syntax]
    (if (and (not (string/index-of as-str "-"))
             (<= (count as-str) (:max-shorthand-len m)))
      (or (get-in m [1 as-str])
          (get-in m [2 as-str])
          (get-in m [3 as-str])
          as-str)
      as-str)))


(defn- functional-pseudo-kw [s]
  (some-> (re-find specs/functional-pseudo-re s)
          peek
          keyword))

(defn- pseudo? [s pseudos functional-pseudos]
  (let [as-kw (some-> s
                      (string/split #"[\+\>\~\.\[\#\*]")
                      (nth 0 nil)
                      keyword)]
    (or (some #(= % as-kw) pseudos)
        (when-let [as-kw (functional-pseudo-kw s)]
          (some #(= % as-kw) functional-pseudos)))))


(defn pseudo-mod-transform
  [{:keys [s t bunch?]}]
   (str (when-not bunch? "&")
        (when-not (string/starts-with? s ":") ":")
        (when (= t :pseudo-element) ":")
        s))


(def mod-transforms
  {:media-query    
   #(let [m       (-> % keyword defs/media)
          [[k v]] (when (map? m) (into [] m))]
      (str "@media(" (name k) ": " (name v) ")"))

   :dark-mode      
   (fn [_] ".dark &")

   :ancestor       
   #(let [[_ sel] (re-find specs/has-ancestor-re %)]
      (str sel " &"))

   :pseudo-class
   pseudo-mod-transform

   :pseudo-element
   pseudo-mod-transform

   ;; Prepends "&", unless selector has " &" appended, which makes it an 
   ;; ancestor selector in nested css
   :query-selector

   ;; TODO - need a regex that will remove underscores, but only if they are not
   ;;        inside single or double quotes
   #(let [guarded        (string/replace % #"--_" "____PRIVATE_CSSVAR____")
          no-underscores (str (when-not (string/ends-with? guarded " &") "&")
                              (string/replace guarded #"_" " "))]
      (string/replace no-underscores #"____PRIVATE_CSSVAR____" "--_"))

  ;; TODO - you can probably remove this
  ;;  #(str (when-not (string/ends-with? % " &") "&")
  ;;        (string/replace % #"^_" " "))
   
   })

(defn- string-starts-with-pseudo-class? [s]
  (-> s
      (string/split #"[ \:\.\+\>\~]" 2)
      first
      string/trim
      (pseudo? defs/pseudo-classes*
               defs/functional-pseudo-classes*)))

(defn modf
  [last-index prop? i s]
  (let [t (cond
            (and (zero? i)
                 (contains? defs/media (keyword s)))
            :media-query

            (and (or (zero? i) (= 1 i))
                 (= s "dark"))
            :dark-mode
            
            (and (= i last-index) prop?)
            nil

            (pseudo? s
                     defs/pseudo-classes*
                     defs/functional-pseudo-classes*)
            :pseudo-class

            (pseudo? s
                     defs/pseudo-elements*
                     defs/functional-pseudo-elements*)
            :pseudo-element

            (re-find specs/has-ancestor-re s)
            :ancestor

            :else
            :query-selector)]
    (!? (keyed [last-index prop? i s t]))
    (if t
      (let [;; The first branch of this `if` is a check to see
            ;; if we are dealing with something like:
            ;; "checked+.kushi-label>.emoji"

            ;; which would have been split from something like:
            ;; " .kushi-radio-input:checked+.kushi-label>.emoji"
            
            ;; If first bit is a css pseudoclass like ":checked", we need to
            ;; prepend a ":" 
            s (if (and (pos? i)
                       (string-starts-with-pseudo-class? s))
                (do 
                  (!? :result (str "adding a leading \":\" to " s))
                  (str ":" s))
                s)
            ]
        (with-meta (symbol s) {:mod-type t}))
      s)))


(defn nested-stack [stack v prop?]
  (let [new-v     (if prop? [[(peek stack) v]] v)
        new-stack (if prop? (pop stack) stack)]
    (reduce (fn [acc x]
              [x acc])
            new-v
            (reverse new-stack))))



(defn- stack-unbunched
 ;; TODO - revisit this example / docs. 
 "Given the following example:
  '(css
    :>p:last-child:c--blue
    :>p:last-child:after:c--orange
    [:>p:last-child:after:content \"\\\"my-content\\\"\"])

  Will return a data structure that will subsequently get transformed
  into the following nested css:
  .myns__L11C12 {
    &>p {
      &:last-child {
        color: blue;
        &::after {
          color: orange;
          content: \"my-content\";
        }
      }
    }
  }"
  [stack*]
  (mapv
   (fn [v]
     (if-let [t  (-> v meta :mod-type)]
       (let [f   (t mod-transforms)
             s   (name v)
             arg (if (contains? #{:pseudo-class :pseudo-element} t)
                   {:s      s
                    :t      t
                    :bunch? false}
                   s)
             ret (-> arg f symbol (with-meta {:mod-transformed? true}))]
         (when (string/starts-with? s ":checked")
           (!? (keyed [s arg ret f])))
         ret)
       (name v)))
   stack*))


;; -----------------------------------------------------------------------------
;; -----------------------------------------------------------------------------
;; "Bunching" reduction fns start 
;; -----------------------------------------------------------------------------
;;
;; This `stack-with-bunched` function below is an alternative to the above
;; `stack-unbunched`. Probably should not use this. Or mabye figure out how
;; to do this automatically when applicable?
;;
;; Given the following example, where the author chooses a little repetition
;; over more nested maps at the call site:
;; (css
;;   'foo
;;   :>p:last-child:c--blue
;;   :>p:last-child:after:c--orange
;;   {:>p:last-child:after:content "\"my-content\""})
;;
;; Will return a data structure that will subsequently get transformed
;; into the following nested css:
;; .foo {
;;   &>p:last-child {
;;     color: blue;
;;   }
;;   &>p:last-child::after {
;;     color: orange;
;;     content: "my-content";
;;   }
;; }

;; Contrast this with example in `stack-unbunched` comments 
;; There are some tradeoffs

(defn- bunched-stack-reducer
  [acc v]
  (let [t      (:mod-type (meta v))
        prev   (peek acc)
        bunch? (contains? #{:pseudo-class :pseudo-element :query-selector} t)]
    (cond
      (contains? #{:dark-mode :media-query} t)
      (conj acc v)

      ;; mod, and previous is mod
      (and bunch?
           (vector? prev))
      (conj (subvec acc 0 (-> acc count dec))
            (conj prev v))

      ;; mod, but previous is not mod
      bunch?
      (conj acc [v])

      :else
      (conj acc v))))

(defn- bunched-stack-stringify-reducer
  [acc v]
  (let [t   (-> v meta :mod-type)
        f   (t mod-transforms)
        s   (name v)
        arg (if (contains? #{:pseudo-class :pseudo-element} t)
              {:s s :t t :bunch? true}
              s)]
    (str acc (f arg))))

(defn- stack-with-bunched
  [stack*]
  (let [bunched (reduce bunched-stack-reducer [] stack*)
        ret     (mapv
                 #(if (vector? %)
                    (reduce bunched-stack-stringify-reducer "" %)
                    %)
                 bunched)]
    ret))

;; -----------------------------------------------------------------------------
;; "Bunching" reduction end 
;; -----------------------------------------------------------------------------
;; -----------------------------------------------------------------------------


(defn first-el-str-or-kw [x]
  (when (vector? x)
    (when-let [p (nth x 0 nil)]
      (when (s/valid? ::specs/s|kw p)
        p))))


(defn stack1 [x]
  (when-let [s (some-> x first-el-str-or-kw name)] 
    ;; We know it is not just a css prop if there is one of the following chars:
    ;; colon, underscore, period, or space.

    ;; Therefore it gets treated as a mod/mod-stack, and is split on the colon
    ;; character.

    ;; Will work for things like:
    ;; `{:.foo:last-child:c :red}`
    ;; `{:.foo:last-child {:c :red}}`
    ;; TODO - what to do about something css-vars like [":_.foo:--_x" "2"] ?
    (when-not (or (re-find #"^--_.*" s)
                  (s/valid? ::specs/at-rule s))
      (when (re-find #"[:_\. ]" s)
        (string/split s #":")))))

(defn stack2 [x]
  ;; In this approach, we determine whether it is a mod/mod-stack based on the 
  ;; shape of the value.
  ;; Necessary for something that would slip through the `stack1` check, e.g.:
  ;; `{:p {:c :red}}`
  ;; TODO - maybe fold this into the logic within stack1
  (when-let [[a b] (when (and (vector? x)
                              (= (count x) 2))
                     x)]
    (when (and (vector? b)
               (s/valid? ::specs/s|kw a)
               (not (s/valid? ::specs/at-rule a)))
      (string/split (name a) #":"))))


(defn- sort-mqs [coll]
  (sort-by (fn [[k]]
             (let [[mq] (string/split (name k) #":")]
               (get defs/index-by-media-query (keyword mq))))
           coll))


(defn- with-ordered-mqs [x] 
  (and (vec-of-vecs? x)
       (more-than-one? x)
       (let [[mq others]
             (partition-by-pred
              #(let [[_ k] (re-find #"^([^\s:]+):"
                                    (some-> % (nth 0) name))]
                 (when k
                   (or (get defs/media (keyword k))
                       (get defs/media k))))
              x)]
         (when (more-than-one? mq)
           (into [] (concat others (sort-mqs x)))))))

;; TODO make separate version for stack-with-bunched
(defn hydrated-stacks1
  "If x is vec and first el is string or keyword representing a 'stack' 
   string/split the 'stack' into a sequence"
  [x]
  (if-let [stack (or (stack1 x) (stack2 x))]
    (let [[_ v]         x
          prop?         (s/valid? ::specs/s|kw|num v)
          last-index    (-> stack count dec)
          f             (partial modf last-index prop?)
          stack*        (into [] (map-indexed f stack))
          ;; Currently using (stack-unbunched stack*) to create `nested-stack*`.
          ;; An alternate approach would be stack-with-bunched. 
          nested-stack* (stack-unbunched stack*)
          ret           (nested-stack nested-stack* v prop?)]
      (!?
       (keyed [x
               v
               prop?
               last-index
               f
               stack
               stack*
               nested-stack*
               ret]))
      ret)

    (if-let [mod (let [mod (when (vector? x) (nth x 0 nil))]
                   (when (-> mod meta :mod-transformed?) mod))]

      [mod (nth x 1 nil)]

      (if (s/valid? ::specs/semi-hydrated-style-vec x)

        ;; Just the prop and value, hydrated
        (let [hp (hydrated-prop (nth x 0 nil))]
          [hp
           (hydrated-val hp (nth x 1 nil))])

        ;; Return vector of hydrated-style-vecs
        ;; order media queries here
        ;; TODO - maybe order media queries in core/order-nested rules, then
        ;;        A/B test for perf.
        (or (with-ordered-mqs x) x)))))

(defn first-el-mod [v]
  (when (vector? v)
    (when-let [mod* (nth v 0 nil)]
      (when (-> mod* meta :mod-transformed?)
        mod*))))

(defn hydrated-stacks2
  [v]
  (let [mod (first-el-mod v)
        sec (when mod (nth v 1 nil))]
    (if-let [[mod sec] (when (and (first-el-mod sec)
                                  (nth sec 1 nil))
                         [mod sec])]
      [(str mod) [sec]]
      (if mod
        [(str mod) sec]
        v))))

(defn hydrated-stacks [flattened-to-vecs]
  (->> flattened-to-vecs
       (prewalk hydrated-stacks1)
       distinct
       vec
       (prewalk hydrated-stacks2)))
