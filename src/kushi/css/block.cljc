(ns kushi.css.block
  (:require ;; for testing
 ;;  [taoensso.tufte :as tufte]
   [bling.core :refer [bling callout point-of-interest]]
   [bling.hifi :refer [hifi]]
   [clojure.spec.alpha :as s]
   [clojure.string :as str ]
   [clojure.walk :as walk :refer [postwalk prewalk]]
   [fireworks.core :refer [!? ? ?flop pprint]]
   [fireworks.sample]
   [fireworks.messaging]
   [kushi.css.defs :as defs]
   [kushi.css.hydrated :as hydrated]
   [kushi.css.specs :as specs]
   [kushi.util :as util :refer [keyed 
                                beautify-css
                                more-than-one?
                                spaces
                                partition-by-pred
                                vec-of-vecs?]]))


;; EEEEEEEEEEEEEEEEEEEEEERRRRRRRRRRRRRRRRR   RRRRRRRRRRRRRRRRR   
;; E::::::::::::::::::::ER::::::::::::::::R  R::::::::::::::::R  
;; E::::::::::::::::::::ER::::::RRRRRR:::::R R::::::RRRRRR:::::R 
;; EE::::::EEEEEEEEE::::ERR:::::R     R:::::RRR:::::R     R:::::R
;;   E:::::E       EEEEEE  R::::R     R:::::R  R::::R     R:::::R
;;   E:::::E               R::::R     R:::::R  R::::R     R:::::R
;;   E::::::EEEEEEEEEE     R::::RRRRRR:::::R   R::::RRRRRR:::::R 
;;   E:::::::::::::::E     R:::::::::::::RR    R:::::::::::::RR  
;;   E:::::::::::::::E     R::::RRRRRR:::::R   R::::RRRRRR:::::R 
;;   E::::::EEEEEEEEEE     R::::R     R:::::R  R::::R     R:::::R
;;   E:::::E               R::::R     R:::::R  R::::R     R:::::R
;;   E:::::E       EEEEEE  R::::R     R:::::R  R::::R     R:::::R
;; EE::::::EEEEEEEE:::::ERR:::::R     R:::::RRR:::::R     R:::::R
;; E::::::::::::::::::::ER::::::R     R:::::RR::::::R     R:::::R
;; E::::::::::::::::::::ER::::::R     R:::::RR::::::R     R:::::R
;; EEEEEEEEEEEEEEEEEEEEEERRRRRRRR     RRRRRRRRRRRRRRR     RRRRRRR
;;
;; -----------------------------------------------------------------------------
;; Warnings and Errors
;; -----------------------------------------------------------------------------

(declare ansi-colorized-css-block)

(defn- unwrap-quoted-symbol [x]
  (if (and (list? x)
           (= 2 (count x))
           (symbol? (second x)))
    (->> x second name (str "'") symbol)
    x))

(def use-at-keyframes-body 
  (bling "You can use " [:bold 'kushi.core/at-keyframes] " to \n" 
         "create CSS @keyframes animations.\n"
         "\n"
         "Example:\n"
         "(" [:bold 'at-keyframes] " \"slider\"\n"
         "              [:from {:transform \"translateX(0%)\"\n"
         "                      :opacity   0}]\n"
         "              [:to {:transform \"translateX(100%)\"\n"
         "                    :opacity   1}])"
         "\n\n\n"
         "No css ruleset will be created."))

(defn generic-warning
  [{:keys [form header body unwrap-quoted-symbols?]}]
  (callout {:type          :warning
            :label-theme   :marquee
            :border-weight :bold
            :padding-top   1}
           header
           (point-of-interest
            (merge {:file ""
                    :type :warning
                    ;; :header header
                    ;; :body   body
                    }
                   (meta form)
                   {:form (if unwrap-quoted-symbols?
                            (apply list (map unwrap-quoted-symbol form))
                            form)}))
           body))

(defn bad-at-rule-name-warning [sel &form]
  (generic-warning 
   {:form   &form
    :header (bling "It seems you are trying to construct an\n"
                   [:bold (str "@" sel)] 
                   " rule and you forget a leading "
                   [:bold "\"@\"."])}))

(defn bad-at-keyframes-name-warning [sel &form]
  (generic-warning
   {:form   &form
    :header (bling
             "Bad @keyframes name:\n"
             [:bold (str "\"" sel "\"")])
    :body   (bling "When constructing an @keyframes rule with\n"
                   [:bold 'kushi.core/defcss] ", the first argument should be:\n"
                   "\"@keyframes <your-animation-name>\"\n"
                   (str ::specs/keyframe-selector))}))

(defn bad-at-layer-name-warning [sel &form]
  (generic-warning
   {:form   &form
    :header (bling
             "Bad @layer name:\n"
             [:bold (str "\"" sel "\"")])
    :body   (bling "When constructing an @layer rule with\n"
                   [:bold 'kushi.core/defcss] ", the first argument should be:\n"
                   "\"@layer <your-layer-name> <your-selector>\"\n\n"
                   (str ::specs/layer-selector))}))

(defn rule-selector-warning
  "Prints warning"
  [sel form]
  (let [[sym] form]
    (generic-warning 
     {:form   form
      :header (bling "Bad" (some->> sym (str " ")) " selector:\n"
                     [:bold sel])
      :body   (if (and (string? sel)
                       (str/starts-with? sel "@keyframes"))
                use-at-keyframes-body
                (let [reqs (case sym
                             at-rule
                             "- a string starting with \"@\""
                             (str "- a string"
                                  "\n"
                                  "- valid css selector"))]
                  (bling "The first argument to "
                         [:bold sym]
                         " must be:"
                         "\n"
                         reqs
                         "\n\n"
                         "No css ruleset will be created.")))})))

(defn bad-keyframe-warning-body []
  (bling "\n"
         [:italic "A CSS keyframe must be represented as a "]
         [:italic "two-element vector."]
         "\n"
         [:italic "The first element must be: "]
         "\n\n"
         [:italic "  One of "] (hifi #{:to :from "to" "from"})
         "\n" [:italic "  ~OR~"] "\n"
         "  A percentage value e.g. "
         (hifi :50%) " or " (hifi "50%")
         "\n\n\n"
         [:italic "The second element must be a valid style map such as:"]
         "\n\n"
         (hifi {:transform "translateX(100%)"
                :color "red"}
               {:margin-inline-start 2})
        ;;  [:neutral "{:transform \"translateX(100%)\""]
        ;;  "\n"
        ;;  [:neutral " :color     \"red\""]
        ;;  "\n"
        ;;  [:neutral " :red       \"red\""]
         "\n\n\n"
         [:italic "No keyframe animation will be created."]))

(defn bad-at-rule-arg-warning-body []
  (bling [:bold 'at-rule] " can be called 2 ways:\n\n"
         "1) With a selector and a "
         "single map:\n"
         "(" [:bold "at-rule"] " \"@font-face\"\n"
         "         {:font-family \"Trickster\"\n"
         "          :src         \"local(Trickster)\"})"
         "\n\n"
         "2) With a selector and one or more vectors:\n"
         "(" [:bold "at-rule"]
         " \"@supports not (color: oklch(50% .37 200))\"\n"
         "         [\".element\" {:color :red}]\n"
         "         [\".element2\" {:color :blue}]\"})"))

(defn- trimmed-pprint [x]
  (-> x
      fireworks.core/pprint
      with-out-str
      (str/replace #"\n$" "")))

(defn bad-at-rule-arg-warning
  "Prints warning for bad at-rule arg."
  [at-rule-args form]
  (let [keyframes? (-> form 
                       second
                       (str/starts-with? "@keyframes"))] 
    (generic-warning
     {:form   form
      :header (let [multiple? (< 1 (count at-rule-args))]
                (bling [:italic (str (if keyframes? 
                                       "Bad CSS keyframe"
                                       "Bad at-rule arg")
                                     (when multiple? "s")
                                     ":")]
                       "\n\n"
                       (if multiple? 
                         [:bold (str "  " (trimmed-pprint at-rule-args))]
                         [:bold (str "  " (trimmed-pprint
                                           (first at-rule-args)))])
                       "\n"))
      :body (if keyframes? 
              (bad-keyframe-warning-body)
              (bad-at-rule-arg-warning-body))})))


(defn bad-args-spec-details [spec-data]
  [[:italic (-> (? :data
                   {:theme "Neutral Light"}
                   (nth spec-data 0 nil))
                :formatted
                :string)]
   "\n"
   (-> (? :data
          {:theme             "Neutral Light"
           :display-metadata? false}
          (with-meta (apply hash-map (rest spec-data))
            {:fw/hide-brackets? true}))
       :formatted
       :string)
   "\n\n"])


(defn bad-style-map-warning
  "Prints warning"
  [{:keys [fname          
           invalid-args        
           &form]
    :as m}]
  (generic-warning
   {:form
    &form

    :unwrap-quoted-symbols?
    true

    :header
    (apply
     bling
     (concat [[:italic "Bad value for :style entry to "] fname ":"
              "\n\n"]
             (interpose "\n"
                        (map (fn [arg]
                               #_[:bold (unwrap-quoted-symbol arg)]
                               (bling.hifi/hifi (unwrap-quoted-symbol arg)
                                                {:margin-inline-start 4}))
                             invalid-args))

             ["\n\n"]
             (when-let [[_ prop val] 
                        (when (-> invalid-args first keyword?)
                          (re-find #"^([a-z]+)-(\$*[a-z0-9]+.*)" 
                                   (name (-> invalid-args first))))]
               ["\n"
                "\n"
                "Did you mean "
                [:bold (str ":" prop "--" val)]
                "?"
                "\n\n"])))
    :body   
    (apply
     bling
     (concat
      ["\n"
       [:italic "The value of the :style entry should be valid stylemap or style string"]
       "\n\n\n"
       [:italic "The value of the :style entry is validated with:"]
       "\n\n    "
       (bling.hifi/hifi ::specs/style-map-for-style-attribute)
       "\n\n"]))}))


(defn cssrule-args-warning
  "Prints warning"
  [{:keys [fname          
           invalid-args        
           &form]
    :as m}]
  (generic-warning
   {:form
    &form

    :unwrap-quoted-symbols?
    true

    :header
    (apply
     bling
     (concat ["Bad args to " [:italic fname] ":"
              "\n  "]
             (interpose "\n"
                        (map (fn [arg]
                               [:bold (str (unwrap-quoted-symbol arg))])
                             invalid-args))

             ["\n\n"]
             (when-let [[_ prop val] 
                        (when (-> invalid-args first keyword?)
                          (re-find #"^([a-z]+)-(\$*[a-z0-9]+.*)" 
                                   (name (-> invalid-args first))))]
               ["\n"
                "\n"
                "Did you mean "
                [:bold (str ":" prop "--" val)]
                "?"
                "\n\n"])))
    :body   
    (let [spec-data (s/form ::specs/valid-sx-arg)]
      (bling
       [:p
        [:br]
        [:italic (if (contains? #{"kushi.core/css-rule"} fname)
                   "All args beyond the first are validated with:"
                   "All args are validated with:")]
        [:br]
        [:bold (str ::specs/valid-sx-arg)]]
       
       (when false
         (bad-args-spec-details spec-data))
       
       [:br]

       [:p
        [:italic "The bad arguments will be discarded, and"]
        [:br]
        [:italic "the following css ruleset will be created"]
        [:br]
        [:italic "from the remaining valid arguments:"]]
       (ansi-colorized-css-block m)
       
       )
      #_(apply
         bling
         (concat
          ["\n"
           [:italic (if (contains? #{"kushi.core/css-rule"}
                                   fname)
                      "All args beyond the first are validated with:"
                      "All args are validated with:")]
           "\n  "
           [:bold (str ::specs/valid-sx-arg)]
           "\n\n"]
          
          (when false (bad-args-spec-details spec-data))
          
          [[:italic "The bad arguments will be discarded, and"]
           "\n"
           [:italic "the following css ruleset will be created"]
           "\n"
           [:italic "from the remaining valid arguments:"]
           "\n\n"]
          (ansi-colorized-css-block m)
          
          )))}))




;; -----------------------------------------------------------------------------
;; Utilities
;; -----------------------------------------------------------------------------




;; FFFFFFFFFFFFFFFFFFFFFFLLLLLLLLLLL       TTTTTTTTTTTTTTTTTTTTTTT
;; F::::::::::::::::::::FL:::::::::L       T:::::::::::::::::::::T
;; F::::::::::::::::::::FL:::::::::L       T:::::::::::::::::::::T
;; FF::::::FFFFFFFFF::::FLL:::::::LL       T:::::TT:::::::TT:::::T
;;   F:::::F       FFFFFF  L:::::L         TTTTTT  T:::::T  TTTTTT
;;   F:::::F               L:::::L                 T:::::T        
;;   F::::::FFFFFFFFFF     L:::::L                 T:::::T        
;;   F:::::::::::::::F     L:::::L                 T:::::T        
;;   F:::::::::::::::F     L:::::L                 T:::::T        
;;   F::::::FFFFFFFFFF     L:::::L                 T:::::T        
;;   F:::::F               L:::::L                 T:::::T        
;;   F:::::F               L:::::L         LLLLLL  T:::::T        
;; FF:::::::FF           LL:::::::LLLLLLLLL:::::LTT:::::::TT      
;; F::::::::FF           L::::::::::::::::::::::LT:::::::::T      
;; F::::::::FF           L::::::::::::::::::::::LT:::::::::T      
;; FFFFFFFFFFF           LLLLLLLLLLLLLLLLLLLLLLLLTTTTTTTTTTT      
;; -----------------------------------------------------------------------------
;; Flattening / Vectorizing
;; -----------------------------------------------------------------------------



(defn- map->vec [v]
  (if (map? v) (into [] v) v))


(defn- conformed-map* 
  "Expects a vector of vectors, the output of `(s/conform ::specs/sx-args args)`"
  [coll]
  (reduce (fn [m [k v]]
            (assoc m
                   k 
                   (conj (or (some-> m k) [])
                         v)))
          {}
          coll))


(defn top-level-maps->vecs
  [conformed-map]
  (some->> conformed-map
           :style-map
           (map #(into [] %))
           (apply concat)
           (apply conj [])
           (postwalk map->vec)))


(defn- pre-hydrated
  "This is for dealing with values that might be:
   - css vars like `$foo||10px`
   - css functions like `'(calc (+ 2px 3px))`
   - vectors (css comma separated values like `Arial, Helvetica, sans-serif`)
   - vectors of vectors (layered box-shadows)
   
   They need to be handled in the order below"
  [coll]
  (->> coll
       (postwalk hydrated/hydrated-css-var2)
       (prewalk hydrated/dequote-cssfn)
       (postwalk hydrated/hydrated-cssfn)
       (postwalk hydrated/hydrate-vectors-containing-css-value-vectors)
       (postwalk hydrated/hydrate-layered-values)))


(defn vectorized*
  [coll]
  (let [pre-hydrated  (pre-hydrated coll)
        conformed-map (conformed-map* pre-hydrated)
        vectorized    (top-level-maps->vecs conformed-map)]
    (!? (keyed [coll
                conformed-map 
                top-level-maps->vecs  
                vectorized]))              
    {:conformed-map conformed-map
     :vectorized    vectorized}))


;;         GGGGGGGGGGGGGRRRRRRRRRRRRRRRRR   PPPPPPPPPPPPPPPPP   
;;      GGG::::::::::::GR::::::::::::::::R  P::::::::::::::::P  
;;    GG:::::::::::::::GR::::::RRRRRR:::::R P::::::PPPPPP:::::P 
;;   G:::::GGGGGGGG::::GRR:::::R     R:::::RPP:::::P     P:::::P
;;  G:::::G       GGGGGG  R::::R     R:::::R  P::::P     P:::::P
;; G:::::G                R::::R     R:::::R  P::::P     P:::::P
;; G:::::G                R::::RRRRRR:::::R   P::::PPPPPP:::::P 
;; G:::::G    GGGGGGGGGG  R:::::::::::::RR    P:::::::::::::PP  
;; G:::::G    G::::::::G  R::::RRRRRR:::::R   P::::PPPPPPPPP    
;; G:::::G    GGGGG::::G  R::::R     R:::::R  P::::P            
;; G:::::G        G::::G  R::::R     R:::::R  P::::P            
;;  G:::::G       G::::G  R::::R     R:::::R  P::::P            
;;   G:::::GGGGGGGG::::GRR:::::R     R:::::RPP::::::PP          
;;    GG:::::::::::::::GR::::::R     R:::::RP::::::::P          
;;      GGG::::::GGG:::GR::::::R     R:::::RP::::::::P          
;;         GGGGGG   GGGGRRRRRRRR     RRRRRRRPPPPPPPPPP          
;; -----------------------------------------------------------------------------
;; Grouping
;; -----------------------------------------------------------------------------

(defn- sel-and-vec-of-vecs?2 [x]
  (boolean (and (vector? x)
                (string? (nth x 0 nil))
                (vec-of-vecs? (nth x 1 nil)))))

(defn- dupe-reduce [grouped]
  (reduce-kv (fn [acc k v]
               (->> v
                    (reduce (fn [acc [_ vc]] (apply conj acc vc)) [])
                    (vector k)
                    (conj acc)))
             []
             grouped))

(defn- lvfha-sorted* [coll]
  (into []
        (sort-by #(->> % 
                       first
                       (get defs/lvfha-pseudos-order-strs))
                 coll)))

(defn- feature-query-sorted* [coll]
  (let [[fq others]
        (partition-by-pred
         #(re-find #"^\@[a-z]" (some-> % (nth 0) name))
         coll)]
    (if (seq fq)
      (into [] (concat others fq))
      coll)))


;; Sorts lvfha and feature queries such as @supports
(defn- lvfha-order [coll all-nested-sels]
  (if (some #(contains? defs/lvfha-pseudos-strs %) all-nested-sels)
    (-> coll
        lvfha-sorted*
        feature-query-sorted*)
    coll))


(defn- group-shared*
  "Groups things for nesting.
   Postions css properties in front of other selector bits.
   Pseudo-classes are ordered according to defs/lvfha-pseudos-order."
  ;; TODO - make pseudo-ordering override-able.
  [v all-nested-sels dupe-nested-sels]

  (let [
        ;; debug?
        ;; (= v [:a :b])

        ;; If there are any duplicate selectors, partition them from others
        [dupe-vecs others]
        (partition-by-pred #(contains? dupe-nested-sels (nth % 0 nil)) v)

        ;; Partition nested and non-nested
        [others-nested others2]
        (partition-by-pred sel-and-vec-of-vecs?2 others)

        ;; Order nested and non-nested
        others
        (apply conj others2 others-nested)

        ;; Potentially group and reduce duplicates
        grouped-dupes
        (some->> dupe-vecs (group-by first) dupe-reduce)

        ;; Create new vec-of-vecs with non-dupes and grouped dupes
        ret*
        (!? 'ret* (apply conj others grouped-dupes))
        
        ;; Determine if there are selectors with lvfha pseudoclasses
        ;; Optionally resort based on selectors with lvfha pseudoclasses
        ret (lvfha-order ret* all-nested-sels)]

   #_(when debug? (? (keyed [dupe-nested-sels
                          dupe-vecs
                          others
                          grouped-dupes
                          ;; ret*
                          ;; ret
                           ])))
        ret))

(defn- order-nested-rules
  [v all-nested-sels nested-rules]
  (let [all-nested-sels (into #{} all-nested-sels)
        non-nested      (filter #(not (contains? all-nested-sels
                                                 (nth % 0 nil)))
                                v)
        ret*            (into [] (concat non-nested nested-rules))]
    (lvfha-order ret* all-nested-sels)))


(defn- group-shared
  [v]
  (let [debug? false #_(= v [:a :b])]
   (if-let [nested-rules (seq (filter sel-and-vec-of-vecs?2 v))]
     (let [all-nested-sels  (map first nested-rules)]
      ;;  (when debug? (!? all-nested-sels))
      ;;  (when debug? (!? (more-than-one? nested-rules)))
       #_(println "\n\n------------------------------")
       (if (more-than-one? nested-rules)
         (let [dupe-nested-sels (->> all-nested-sels
                                     frequencies
                                     (keep (fn [[sel n]] (when (> n 1) sel)))
                                     (into #{}))]
           ;; (when debug? (!? dupe-nested-sels))
           (if (seq dupe-nested-sels)
             (group-shared* v all-nested-sels dupe-nested-sels)
             (order-nested-rules v all-nested-sels nested-rules)))
         (order-nested-rules v all-nested-sels nested-rules)))

     (if (string? v)
       (util/double-quote-data-attr-selector-values v)
       v))))

(defn- css-block-str
  "Reduces nested vector representation of css-block into valid, potentially
   nested, serialized css rule block. Does not include outermost curly braces.
  
   Example:
    
   [[\"color\" \"blue\"]
    [\"&>p\" [[\"color\" \"red\"]
              [\"background-color\" \"blue\"]]]
   =>
   \"color: blue;
     &>p {
       color: red;
       background-color: blue;
     }\""
  ([coll]
   (css-block-str coll 2))
  ([coll indent]
   (reduce
    (fn [acc [k v]]
      (let [spc (spaces indent)]
        (str acc 
             (if (vector? v)
               (str spc k " {\n" (css-block-str v (+ indent 2)) spc "}\n")
               (str spc k ": " v ";\n")))))
    ""
    coll)))

(defn grouped-css-declarations [conformed-args]
  (let [{:keys [vectorized]}
        (!? (vectorized* conformed-args))
        ]
    (!? 'grouped-new
        (->> vectorized 
             (!? 'vectorized)
             hydrated/hydrated-stacks
             (? 'hydrated)
             (prewalk group-shared)
             (!? 'grouped)))))

(defn css-block* [conformed-args]
  (let [grouped (grouped-css-declarations conformed-args)]
    {:css-block     (str "{\n" (css-block-str grouped) "}")
     :nested-vector grouped}))


(defn nested-css-block
  "Returns a potentially nested block of css"
  [args &form &env fname sel]
  (let [{:keys [conformed-args
                invalid-args]}
        (specs/conformed-args args)

        ret                       
        (some->> conformed-args
                 kushi.css.block/css-block*
                 :css-block)]
    #_(keyed [args &form &env fname sel conformed-args invalid-args])
    (when (seq invalid-args)
      ;; (spec/explain ::specs/sx-args args)
      (kushi.css.block/cssrule-args-warning
       {:fname             fname
        :args              args
        :invalid-args      invalid-args
        :&form             &form
        :&env              &env
        :block             ret
        :display-selector? true
        :sel               sel}))
    ret))

(defn loc-id
  "Returns classname based on namespace and line + column.
   e.g. \"starter_browser__L41_C6\""
  [env form]
  (!? :result (some-> env :ns :name))
  (when-let [ns* (or (some-> env :ns :name (str/replace #"\." "_"))
                     "[unresolved ns]")]
    (let [fm (meta form)]
      (str ns* "__L" (:line fm) "_C" (:column fm)))))


(defn ansi-colorized-css-block
  [{:keys [args &form &env block display-selector? sel] :as m}]
  (let [sel   (when (or (not block) display-selector?)
                (or (some-> sel (str " "))
                    (str "." (loc-id &env &form) " ")))
        block (or block
                  (nested-css-block args
                                    &form
                                    &env
                                    "kushi.core/css-block"
                                    sel))]
    (beautify-css (str sel block))))
