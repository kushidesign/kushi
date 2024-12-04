(ns kushi.css.core
  (:require 
   [fireworks.core :refer [? !? ?> !?> pprint]]
   [kushi.css.defs :as defs]
   [kushi.css.hydrated :as hydrated]
   [kushi.css.specs :as specs]
   [kushi.css.util :refer [keyed]]
   [clojure.walk :as walk :refer [prewalk postwalk]]
   [clojure.string :as string :refer [replace] :rename {replace sr}]
   [clojure.spec.alpha :as s]
   ;; TODO conditionally require fireworks pprint for clj
   [bling.core :refer [bling callout point-of-interest stack-trace-preview]]
   [babashka.process :refer [shell]]
  ;; for testing
  ;;  [taoensso.tufte :as tufte]
   ))


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
  [{:keys [form header body]}]
  (callout {:type        :warning
            :padding-top 1}
           (point-of-interest
            (merge {:file   ""
                    :type   :warning
                    :header header
                    :body   body}
                   (meta form)
                   {:form form}))))

(defn bad-at-rule-name-warning [sel &form]
  (generic-warning 
   {:form   &form
    :header (bling
             "It seems you are trying to construct an\n"
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
                       (string/starts-with? sel "@keyframes"))
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

(def bad-keyframe-warning-body
  (bling "A css keyframe must be represented as a "
         "two-element vector."
         "\n\n"
         "The first element must be: "
         "\n"
         "- one of " [:neutral (str #{:to :from "to" "from"})]
         "\n" [:italic "OR"] "\n"
         "- A percentage from "
         [:neutral "0%-100%"]
         " as keyword or string, e.g. "
         [:neutral ":50%, \"50%\""]
         "\n\n"
         "The second element must be a valid style map such as:"
         "\n"
         [:neutral "{:transform \"translateX(100%)\""]
         "\n"
         [:neutral " :color     \"red\""]
         "\n"
         [:neutral " :red       \"red\""]
         "\n\n"
         "No keyframe animation will be created."))

(def bad-at-rule-arg-warning-body
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
      (string/replace #"\n$" "")))

(defn bad-at-rule-arg-warning
  "Prints warning for bad at-rule arg."
  [at-rule-args form]
  (let [keyframes? (-> form 
                       second
                       (string/starts-with? "@keyframes"))] 
    (generic-warning
     {:form   form
      :header (let [multiple? (< 1 (count at-rule-args))]
                (bling (str (if keyframes? 
                              "Bad CSS keyframe"
                              "Bad at-rule arg")
                            (when multiple? "s")
                            ":")
                       "\n"
                       (if multiple? 
                         (trimmed-pprint at-rule-args)
                         [:bold (trimmed-pprint
                                 (first at-rule-args))])))
      :body (if keyframes? 
              bad-keyframe-warning-body
              bad-at-rule-arg-warning-body)})))


(defn cssrule-args-warning
  "Prints warning"
  [{:keys [fname          
           invalid-args        
           &form]
    :as m}]
  (generic-warning
   {:form   &form
    :header (apply
             bling
             (concat ["Bad args to " [:italic fname] ":"
                      "\n"]
                     (interpose "\n"
                                (map (fn [arg] [:bold arg])
                                     invalid-args))))
    :body   (let [spec-data (s/form ::specs/valid-sx-arg)]
              (apply
               bling
               (concat
                [(if (contains? #{"kushi.core/css-rule"}
                                fname)
                   "All args beyond the first are validated with:"
                   "All args are validated with:")
                 "\n"
                 [:bold.italic (str ::specs/valid-sx-arg)]
                 "\n\n"
                 [:italic (-> (? :data
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
                 
                 "\n\n"
                 "The bad arguments will be discarded, and"
                 "\n"
                 "the following css ruleset will be created"
                 "\n"
                 "from the remaining valid arguments:"
                 "\n\n"]
                (ansi-colorized-css-block m))))}))

;; -----------------------------------------------------------------------------
;; Utilities
;; -----------------------------------------------------------------------------

(defn- partition-by-pred [pred coll]
  "Given a coll and a pred, returns a vector of two vectors. The first vector
   contains all the values from coll that satisfy the pred. The second vector
   contains all the values from the coll that do not satisfy the pred."
  (let [ret* (reduce (fn [acc v]
                       (let [k (if (pred v) :valid :invalid)]
                         (assoc acc k (conj (k acc) v))))
                     {:valid [] :invalid []}
                     coll)]
    [(:valid ret*) (:invalid ret*)]))

(defn- partition-by-spec
  "Given a coll and a spec, returns a vector of two vectors. The first vector
   contains all the values from coll that satisfy the spec. The second vector
   contains all the values from the coll that do not satisfy the spec."
  [spec coll]
  (let [ret* (reduce (fn [acc v]
                       (let [k (if (s/valid? spec v) :valid :invalid)]
                         (assoc acc k (conj (k acc) v))))
                     {:valid [] :invalid []}
                     coll)]
    [(:valid ret*) (:invalid ret*)]))


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


(defn split-on [re v]
  (string/split (name v) re))


(defn- unpack-pvs [coll]
  (reduce
   (fn [acc x]
     (if (vector? (nth x 0))
       (apply conj acc x)
       (conj acc x)))
   []
   coll))


(defn- map->vec [v]
  (if (map? v) (into [] v) v))


(defn conformed-map* 
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


(defn top-level-vecs->vecs
  [conformed-map]
  (some->> conformed-map
           :style-vec
           (postwalk map->vec)))


(defn vectorized*
  [coll]
  (let [conformed-map        (conformed-map* coll)
        untokenized          (->> conformed-map 
                                  :tokenized
                                  (map (partial split-on #"--")))
        top-level-maps->vecs (top-level-maps->vecs conformed-map)
        top-level-vecs->vecs (top-level-vecs->vecs conformed-map)
        ;; TODO - how do you sort here based on original order?
        ;; Maybe attach meta to vecs and do it by that?
        list-of-vecs         (concat top-level-maps->vecs
                                     top-level-vecs->vecs
                                     untokenized)
        vectorized           (unpack-pvs list-of-vecs)]

    (!? (keyed [coll
                conformed-map 
                untokenized   
                top-level-maps->vecs  
                list-of-vecs            
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

(defn- vec-of-vecs? [v]
  (and (vector? v)
       (every? vector? v)))


(defn- sel-and-vec-of-vecs?2 [x]
  (boolean (and (vector? x)
                (string? (nth x 0 nil))
                (vec-of-vecs? (nth x 1 nil)))))


(defn- more-than-one? [coll]
  (> (count coll) 1))


(defn- dupe-reduce [grouped]
  (reduce-kv (fn [acc k v]
               (->> v
                    (reduce (fn [acc [_ vc]] (apply conj acc vc)) [])
                    (vector k)
                    (conj acc)))
             []
             grouped))


(defn- lvfha-order [coll all-nested-sels]
  (if (some #(contains? defs/lvfha-pseudos-strs %) all-nested-sels)
    (into []
          (sort-by #(->> % 
                         first
                         (get defs/lvfha-pseudos-order-strs))
                   coll))
    coll))


(defn group-shared*
  "Groups things for nesting.
   Postions css properties in front of other selector bits.
   Pseudo-classes are ordered according to defs/lvfha-pseudos-order."
  ;; TODO - make pseudo-ordering override-able.
  [v all-nested-sels dupe-nested-sels]
  (let [
        ;; If there are any duplicate selectors, partition them from others
        [dupe-vecs others]
        (partition-by-pred #(contains? dupe-nested-sels (nth % 0 nil)) v)

        ;; Potentially group and reduce duplicates
        grouped-dupes
        (some->> dupe-vecs (group-by first) dupe-reduce)

        ;; Create new vec-of-vecs with non-dupes and grouped dupes
        ret*
        (!? 'ret* (apply conj others grouped-dupes))
        
        ;; Determine if there are selectors with lvfha pseudoclasses
        ;; Optionally resort based on selectors with lvfha pseudoclasses
        ret (lvfha-order ret* all-nested-sels)]

   #_(? (keyed [dupe-nested-sels
              dupe-vecs
              others
              grouped-dupes
              some-lvfha?
              ret*
              ret]))
        ret))


(defn- order-nested-rules
  [v all-nested-sels nested-rules]
  (let [all-nested-sels (into #{} all-nested-sels)
        non-nested      (filter #(not (contains? all-nested-sels
                                                 (nth % 0 nil)))
                                v)
        ret*            (into [] (concat non-nested nested-rules))]
    (lvfha-order ret* all-nested-sels)))


(defn group-shared
  [v]
  (if-let [nested-rules (seq (filter sel-and-vec-of-vecs?2 v))]
    (let [all-nested-sels  (map first nested-rules)]
      (if (more-than-one? nested-rules)
        (let [dupe-nested-sels (->> all-nested-sels
                                    frequencies
                                    (keep (fn [[sel n]] (when (> n 1) sel)))
                                    (into #{}))]
          (if (seq dupe-nested-sels)
            (group-shared* v all-nested-sels dupe-nested-sels)
            (order-nested-rules v all-nested-sels nested-rules)))
        (order-nested-rules v all-nested-sels nested-rules)))
    v))


;; HHHHHHHHH     HHHHHHHHHLLLLLLLLLLL             PPPPPPPPPPPPPPPPP   
;; H:::::::H     H:::::::HL:::::::::L             P::::::::::::::::P  
;; H:::::::H     H:::::::HL:::::::::L             P::::::PPPPPP:::::P 
;; HH::::::H     H::::::HHLL:::::::LL             PP:::::P     P:::::P
;;   H:::::H     H:::::H    L:::::L                 P::::P     P:::::P
;;   H:::::H     H:::::H    L:::::L                 P::::P     P:::::P
;;   H::::::HHHHH::::::H    L:::::L                 P::::PPPPPP:::::P 
;;   H:::::::::::::::::H    L:::::L                 P:::::::::::::PP  
;;   H:::::::::::::::::H    L:::::L                 P::::PPPPPPPPP    
;;   H::::::HHHHH::::::H    L:::::L                 P::::P            
;;   H:::::H     H:::::H    L:::::L                 P::::P            
;;   H:::::H     H:::::H    L:::::L         LLLLLL  P::::P            
;; HH::::::H     H::::::HHLL:::::::LLLLLLLLL:::::LPP::::::PP          
;; H:::::::H     H:::::::HL::::::::::::::::::::::LP::::::::P          
;; H:::::::H     H:::::::HL::::::::::::::::::::::LP::::::::P          
;; HHHHHHHHH     HHHHHHHHHLLLLLLLLLLLLLLLLLLLLLLLLPPPPPPPPPP          
;; -----------------------------------------------------------------------------
;; API Helpers
;; -----------------------------------------------------------------------------

(defn- loc-id
  "Returns classname based on namespace and line + column.
   e.g. \"starter_browser__41_6\""
  [env form]
  (when-let [ns* (some-> env :ns :name (sr #"\." "_"))]
    (let [fm (meta form)]
     (str ns* "__L" (:line fm) "_C" (:column fm)))))



;; -----------------------------------------------------------------------------
;; TODO - Use this version of loc-id to investigate weird diff between 
;; -----------------------------------------------------------------------------

;; (? (css :.foo :p--10px :c--red)) => "foo"
;; and
;; (? :pp (css :.foo :p--10px :c--red)) => "foo [\"__35_8\"]"


;; (defn- loc-id
;;   "Returns classname based on namespace and line + column.
;;    e.g. \"starter_browser__41_6\""
;;   [env form]
;;   (let [ns* (some-> env :ns :name (sr #"\." "_"))
;;         fm  (meta form)]
;;     (str ns* "__" (:line fm) "_" (:column fm))))

;; -----------------------------------------------------------------------------

(defn- user-classlist
  "Expects a conformed map based on `::specs/sx-args`. This map is the
   `:conformed` entry from return val of `kushi.css.flatten/vectorized*`.

   Returns a map like:
   {:class-kw '(...)
    :classes [...]}"
  ([m]
   (user-classlist m nil))
  ([{:keys [class-kw class-binding] :as m} loc-id]
   (let [class-kw-stringified (map specs/dot-kw->s class-kw)]
     {:class-binding class-binding
      :classes       (into []
                           (concat class-binding
                                   class-kw-stringified
                                   (some-> loc-id vector)))})))

(declare conformed-args)

(defn- classlist
  "Returns classlist vector of classnames as strings. Includes user-supplied
   classes, as well as auto-generated, namespace-derived classname from `css`
   macro."
  ([form args]
   (classlist {:ns {:name "some.test"}} form args))
  ([env form args]
   (let [loc-id (some-> env (loc-id form))
         m      (-> args
                    conformed-args
                    :conformed-args
                    vectorized*
                    :conformed-map)]
     (user-classlist m loc-id))))

(defn- spaces [n] (string/join (repeat n " ")))

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

(defn- nested-array-map
  "Takes a vector representation of a nested array map and returns a nested
   array map."
  [coll]
  (walk/postwalk
   #(if (and (vector? %)
             (every? (fn [x]
                       (and (vector? x)
                            (= (count x) 2)))
                     %))
      (apply array-map (sequence cat %))
      %)
   coll))


(defn- css-block* [conformed-args]
  (let [{:keys [vectorized
                conformed-map]}
        (vectorized* conformed-args)

        grouped                 
        (!? 'grouped-new
            (->> vectorized 
                 (!? 'vectorized)
                 hydrated/hydrated-stacks
                 (!? 'hydrated)
                 (prewalk group-shared)
                 (!? 'grouped)))]

    {:css-block     (str "{\n" (css-block-str grouped) "}")
     :nested-vector grouped
     ;; Leave this :nested-array-map out for now
     ;; :nested-array-map (nested-array-map grouped)
     :classes       (-> conformed-map
                        user-classlist
                        :classes)}))


(defn conformed-args 
  "Returns a vector of `[conformed-args invalid-args]`"
  [args]
  (let [conformed-args*           
        (s/conform ::specs/sx-args args)

        invalid-args?             
        (= conformed-args* :clojure.spec.alpha/invalid)

        [valid-args
         invalid-args]            
        (when invalid-args?
          (partition-by-spec ::specs/valid-sx-arg args))

        conformed-args            
        (if invalid-args?
          (s/conform ::specs/sx-args valid-args)
          conformed-args*)]
    (keyed [conformed-args invalid-args])))


(defn nested-css-block
  "Returns a potentially nested block of css"
  [args &form &env fname sel]
  (let [{:keys [conformed-args
                invalid-args]}
        (conformed-args args)

        ret                       
        (some->> conformed-args
                 css-block*
                 :css-block)]
    (!? (keyed [args &form &env fname sel invalid-args]))
    (when (seq invalid-args)
      (cssrule-args-warning
       {:fname             fname
        :args              args
        :invalid-args      invalid-args
        :&form             &form
        :&env              &env
        :block             ret
        :display-selector? true
        :sel               sel}))
    ret))


;; -----------------------------------------------------------------------------
;; Print debugging helpers
;; -----------------------------------------------------------------------------

(defn- print-as-def [{:keys [&form sym]}]
  (-> (cons (symbol (bling [:bold (str sym " \"" (second &form) "\"")]))
               (drop 2 &form))
         fireworks.core/pprint
         with-out-str
         (sr #"\n$" "")
         (sr #"\n" "\n ")))


(defn- print-as-fcall [{:keys [&form sym]}]
  (-> (rest &form)
      fireworks.core/pprint
      with-out-str
      (sr #"\n$" "")
      (sr #"^\(|\)$" "")
      (sr #"\n" (str "\n" (spaces (inc (count (name sym))))))
      (->> (bling "(" [:bold (name sym)] " " ))
      (str ")")))


(defn ansi-colorized-css-block
  [{:keys [args &form &env block display-selector? sel] :as m}]
  (let [sel   (when (or (not block)
                        display-selector?)
                (bling [:blue (or (some-> sel (str " "))
                                  (str "." (loc-id &env &form) " "))]))
        block (or block
                  (nested-css-block args
                                    &form
                                    &env
                                    "kushi.css.core/css-block"
                                    sel))
        blue  #(bling [:blue (second %)] " {")
        block (-> block 
                  (sr #";" #(bling [:gray %]))
                  (sr #"^([^\{]+) \{" blue)
                  (sr #"(\&[^ ]+) \{" blue)
                  (sr #"(.+): " #(bling [:magenta (second %)] [:gray ": "])))]
    (str sel block)))


(defn- print-css-block [{:keys [sym &form expands-to]
                         :as   m}]
  (callout 
   {:label       (let [{:keys [file line column]} (meta &form)]
                   (str file ":" line ":" column))
    :type        :info
    :padding-top 1}
   (bling (if (= sym '?defcss)
            (print-as-def m)
            (print-as-fcall m))
          "\n\n"
          [:italic.subtle.bold "Expands to:"]
          "\n"
          (with-out-str (pprint expands-to))
          "\n"
          [:italic.subtle.bold "Emits css ruleset:"]
          "\n"
          (ansi-colorized-css-block m))))


(defn double-nested-rule [nm blocks]
  (str nm
       " {\n"
       (string/replace (str "  " (string/join "\n" blocks)) #"\n" "\n  ")
       "\n}"))


(defn- classes+class-binding [args &form &env]
  (apply classlist 
         (if-not &env
           [&form args]
           [&env &form args])))


;; TODO optimize for speed
;; Maybe this is not needed if you can do post-write validation with lightningcss?
(defn- bad-at-rule-name?
  "Determines wheter user is trying to create an at-rule but supplied an at rule
   without the leading @."
  [sel]
  (boolean
   (and (string? sel)
        (not (string/blank? sel))
        (not (re-find #"^[\.\#\[\@\~\+\*]" sel))
        (< 3 (count sel))
        (re-find #"^[cfiklmnpsv]" (subs sel 0))
        (->> (string/split sel #" ")
             first
             (contains? defs/at-rules)))))




;;                AAA               PPPPPPPPPPPPPPPPP   IIIIIIIIII
;;               A:::A              P::::::::::::::::P  I::::::::I
;;              A:::::A             P::::::PPPPPP:::::P I::::::::I
;;             A:::::::A            PP:::::P     P:::::PII::::::II
;;            A:::::::::A             P::::P     P:::::P  I::::I  
;;           A:::::A:::::A            P::::P     P:::::P  I::::I  
;;          A:::::A A:::::A           P::::PPPPPP:::::P   I::::I  
;;         A:::::A   A:::::A          P:::::::::::::PP    I::::I  
;;        A:::::A     A:::::A         P::::PPPPPPPPP      I::::I  
;;       A:::::AAAAAAAAA:::::A        P::::P              I::::I  
;;      A:::::::::::::::::::::A       P::::P              I::::I  
;;     A:::::AAAAAAAAAAAAA:::::A      P::::P              I::::I  
;;    A:::::A             A:::::A   PP::::::PP          II::::::II
;;   A:::::A               A:::::A  P::::::::P          I::::::::I
;;  A:::::A                 A:::::A P::::::::P          I::::::::I
;; AAAAAAA                   AAAAAAAPPPPPPPPPP          IIIIIIIIII
;; -----------------------------------------------------------------------------
;; Public API
;; -----------------------------------------------------------------------------


(defmacro ^:public css-block-data
  "Returns a map with following keys:
   :nested-vector    ->  vector representation of nested css.
   :nested-css-block ->  pretty-printed css ruleset, no selector.
   :classes          ->  user supplied classes.
   :ns               ->  namespace of the callsite, a symbol.
   :file             ->  filename as string
   :line             ->  line number
   :column           ->  column number
   :end-line         ->  end line number
   :end-column       ->  end column number"
  [& args]
  (merge (css-block* args)
         (some->> &env :ns :name str symbol (hash-map :ns))
         (meta &form)))


;; Does this need to be a macro?
;; Maybe it just gets callsite info from analyzer fn which calls it.
(defmacro ^:public css-block
  "Returns a pretty-printed css rule block (no selector)."
  [& args]
  (nested-css-block args
                    &form
                    &env
                    "kushi.css.core/css-block"
                    nil))


(defn css-rule* [sel args &form &env]
  ;; Check if user supplied bad at-rule name, forgetting a leading "@".
  (if (bad-at-rule-name? sel)

   (bad-at-rule-name-warning sel &form)

   (if (s/valid? ::specs/at-selector sel)

     ;; CSS at-rule -----------------------------------------------
     (let [f (fn [sel args]
               (str sel 
                    " "
                    (nested-css-block args
                                      &form
                                      &env
                                      "kushi.css.core/at-rule"
                                      sel)))]
       (cond
         ;; @ keyframes ---------------------------
         (string/starts-with? sel "@keyframes")
         (if-not (s/valid? ::specs/keyframe-selector sel)
           (bad-at-keyframes-name-warning sel &form)
           (let [[vecs bad-vecs] (partition-by-spec ::specs/keyframe args)]
             (if (seq bad-vecs)
               (bad-at-rule-arg-warning bad-vecs &form)
               (let [blocks (for [[nested-sel m] vecs]
                              (f (name nested-sel) [m]))]
                 (double-nested-rule sel blocks)))))

         ;; @ layers ------------------------------
         (string/starts-with? sel "@layer")
         (if-not (s/valid? ::specs/layer-selector sel)
           (bad-at-layer-name-warning sel &form)
           (let [enable-css-layers?
                 ;; TODO - boolean you can pull out of the args to css-rule*
                 ;;        passed from within kushi.css.build/build, based on
                 ;;        user config option to enable actual CSS layers.
                 false

                 sel
                 (if enable-css-layers?
                   sel
                   (-> sel
                       (string/split #" ")
                       last))]
             (f sel args)))
         
         ;; CSS at-rule with nested css rules ------
         ;; TODO
         ;;  - created ::nested-css-rule spec
         ;;  - then use partition-by-spec to remove bad ones and warn
         (every? #(and (list? %) (= (first %) 'css-rule)) args)
         (let [blocks (for [[_ nested-sel & style-args] args]
                        (f nested-sel style-args))]
           (double-nested-rule sel blocks))
         
         ;; @ CSS rule with no nested rules --------
         :else
         (do (f sel args))))

    ;; Normal css-rule -------------------------------------------
     (if-not (s/valid? ::specs/css-selector sel)
       (rule-selector-warning (if (map? sel)
                                (:selector sel)
                                sel)
                              &form)
       (let [sel (if (map? sel) (:selector sel) sel)]
         (when-let [css-str (nested-css-block args
                                              &form
                                              &env
                                              "kushi.css.core/css-rule"
                                              sel)]
           (str sel " " css-str)))))))


(defmacro ^:public css-rule
  "Returns a serialized css ruleset, with selector and potentially nested css
   block."
  [sel & args]
  (css-rule* sel args &form &env))


(defmacro ^:public defcss
  "Used to define shared css rulesets.
   `sel` must be a valid css selector in the form of a string.
   `args` must be valid style args, same as the `css` and `sx` macros.
   The function call will be picked up in the analyzation phase of a build, then fed to `css-rule` to produce a css rule that will be written to disk.
   Expands to nil."
  [sel & args]
  nil)


(defmacro ^:public ?defcss
  "Tapping version of `defcss`"
  [sel & args]
  (if-not (or (s/valid? ::specs/css-selector sel)
              (s/valid? ::specs/at-selector sel))
    (rule-selector-warning sel &form)
    (let [block (css-rule* sel args &form &env)]
      (print-css-block (assoc (keyed [args &form &env block])
                              :sym
                              '?defcss))
      nil)))

;; TODO - For release builds we might want to elide the inclusion of the
;;        auto-generated classname (e.g. myns_foo__L20_C11), if that ruleset
;;        does not contain any rules. This happens when css or sx is called with
;;        only kushi utility or shared classes e.g. (sx :.absolute-centered).
;;        It is probably preferrable to include these in dev for debugging.
;;        This release build elision could be turned off with config option.
(defmacro ^:public css
  "Returns classlist string consisting of auto-generated classname and
   user-supplied classnames.
   
   Example of expansion in a component. Let's say the namespace is called
   foo.core, on line 100:

   100 | (defn my-component [text]
   101 |   [:div
   102 |    {:class (css :.absolute :c--red :fs--48px)}
   103 |    text])
   =>
   (defn my-component [text]
    [:div
     {:class \"absolute foo_core__L102_C11\"}
     text])
   
   The call to `css` produces the following class in the build's
   watch/analyze/css generation process:

   .foo_core__L102_C11 {
     color:     red;
     font-size: 48px;
   }"
  [& args]
  (let [
        ;; If calling from a test namespace, it might not resolve a
        ;; val for &env so we will call classlist with 2 args instead of 3.
        {:keys [classes class-binding]} (classes+class-binding args &form &env)]

    ;; If `classes` vector contains any symbols that are runtime bindings
    ;; intended to hold classnames (`class-bindings`) we will need to
    ;; string/join it at runtime, e.g.:
    ;; `[(when my-runtime-var "foo") my-classname "bar"]`
    ;;
    ;; If no conditional class forms, we can string/join it at compile time


    (if (seq class-binding) 
       `(kushi.css.core/class-str ~classes)
      (string/join " " classes))))


(defmacro ^:public ?css
  "Tapping version of `css`"
  [& args]
  (let [{:keys [classes class-binding]}
        (classes+class-binding args &form &env)

        expands-to
        (if (seq class-binding) 
          `{:class (kushi.css.core/class-str ~classes)}
          {:class (string/join " " classes)})]

    (print-css-block (assoc (keyed [args &form &env expands-to]) :sym '?css))
    (if (seq class-binding) 
      `(kushi.css.core/class-str ~classes)
      (string/join " " classes))))


;; TODO - For release builds we might want to elide the inclusion of the
;;        auto-generated classname (e.g. myns_foo__L20_C11), if that ruleset
;;        does not contain any rules. This happens when css or sx is called with
;;        only kushi utility or shared classes e.g. (sx :.absolute-centered).
;;        It is probably preferrable to include these in dev for debugging.
;;        This release build elision could be turned off with config option.
(defmacro ^:public sx
  "Returns a map with a :class string. Sugar for `{:class (css ...)}`, to avoid
   boilerplate when you are only applying styling to an element and therefore do
   not need to supply any html attributes other than :class."
  [& args]
  (let [{:keys [classes class-binding]}
        (classes+class-binding args &form &env)]
    (if (seq class-binding) 
      `{:class (kushi.css.core/class-str ~classes)}
      {:class (string/join " " classes)})))


;; TODO - maybe dry this up with ?css
(defmacro ^:public ?sx
  "Tapping version of `sx`"
  [& args]
  (let [{:keys [classes class-binding]}
        (classes+class-binding args &form &env)

        expands-to
        (if (seq class-binding) 
          `{:class (kushi.css.core/class-str ~classes)}
          {:class (string/join " " classes)})]

    (print-css-block (assoc (keyed [args &form &env expands-to]) :sym '?sx))
    (if (seq class-binding) 
      `{:class (kushi.css.core/class-str ~classes)}
      {:class (string/join " " classes)})))


(defn- css-vars-map*
  "Constructs a style map for assigning locals to css custom properties."
  [args]
  (reduce (fn [acc sym]
            (assoc acc (str "--" sym) sym))
          {}
          args))


(defmacro ^:public css-vars
  "Intended to construct a style str assigning locals to css custom properties.

   Let's say we have a namespace called foo.core, line 100:

   100 | (let [my-var1 \"blue\"
   101 |       my-var2 \"yellow\"]
   102 |   {:style (css-vars my-var1 my-var2)
   103 |    :class (css :c--$my-var1 :bgc--$my-var2)})
   =>
   {:style {\"--my-var1\" my-var1
            \"--my-var2\" my-var1}
    :class \"foo_core__L103_C11\"}
   
   The call to `css` produces the following class in the build's
   watch/analyze/css generation process:

   .foo_core__L103_C11 {
     color: --my-var1;
     background-color: --my-var2;
   }"
  [& args]
  (let [m (css-vars-map* args)]
    `(reduce-kv (fn [acc# k# v#] (str acc# k# ": " v# ";") ) "" ~m)))


(defmacro ^:public css-vars-map
  "Same as `css-vars`, but returns a map instead of a string."
  [& args]
  (css-vars-map* args))



;; -----------------------------------------------------------------------------
;; lightningcss ala-carte POC
;; 
;; When using modern css features such as nesting and color spacs such as okclh,
;; most projects targeting a broader web audience will need to use some degree
;; of post-processing (on authored CSS) to target older browsers. For the
;; typical cljs frontend workflow (assuming Shadow-cljs or Figwheel), a good way
;; to achieve this is to install `lightningcss-cli` locally in the project via
;; the package.json file, then configure a watcher (for dev) to transform the
;; project css that is either hand-written or generated by Kushi (or similar
;; tool).
;;
;; The POC below is an attempt at providing functionality that can be used
;; inline to transform Kushi-generated CSS with lightningcss. This means the
;; benefits of lightningcss can be leveraged ala-carte, without incorporating
;; extra tooling via a build process. One use for this would be generating
;; CSS on the server. Another use case would be generating CSS in JVM clojure or
;; babashka in the context of build systems that currently use a tool such as
;; garden (https://github.com/noprompt/garden).

;; See example usage in docstring of lightning.
;; Docs on lightningcss: https://lightningcss.dev/
;; -----------------------------------------------------------------------------

(def lightning-opts
  {:browserslist               true
   :bundle                     nil
   :css-modules                nil
   :css-modules-dashed-indents nil
   :css-modules-pattern        nil
   :custom-media               nil
   :outdir-dir                 nil
   :error-recovery             nil
   :minify                     true
   :output-file                nil
   :sourcemap                  nil
  ;;  :targets                    "\">= 0.25%\""
  ;;  :targets                    ">= 0.25%"
  ;;  :help                       nil
  ;;  :version                    nil
   })


(defn lightning
  "Transforms a string of CSS using lightningcss. An (optional) user config map
   is merged with kushi.css.core/lightning-opts, which is transformed into a
   list of flags that are fed to lightningcss.
   
   Assumes that the user has installed lightningcss-cli locally or globally via
   npm. Uses babaska.process/shell to shell out to lightningcss-cli via JVM
   clojure.

   Example:
   ```Clojure
   (ns your-ns.foo
     (:require [kushi.css.core :refer [css-rule lightning]]))

   (-> (css-rule \".bar\"
                 :c--red
                 :_.bar:c--green)
       lightning)
   ;; => \".foo{color:red}.foo .bar{color:green}\"
   ```

   Another example - same thing but overrides default minification:
   ```Clojure
   (-> (css-rule \".bar\"
                 :c--red
                 :_.bar:c--green)
       (lightning {:minify false}))
   ;; => 
   \".foo {
     color: red;
   }

   .foo .bar {
     color: green;
   }\"
   ```"
  ([css-str]
   (lightning css-str nil))
  ([css-str opts]
   (let [flags (some->> (merge lightning-opts
                               (when (or (nil? opts)
                                         (map? opts))
                                 opts))
                        (keep (fn [[flag v]] 
                                (when v 
                                  [(str "--" (name flag))
                                   (when-not (true? v) v)])))
                        (apply concat)
                        (remove nil?)
                        (into [{:in css-str :out :string}
                               "npx"
                               "lightningcss"]))]

     (or (try (:out (apply shell flags))
              (catch Exception e
                (let [body (bling "Error when shelling out to lightningcss."
                                  "\n\n"
                                  [:italic.subtle.bold "CSS:"]
                                  "\n"
                                  css-str
                                  "\n\n"
                                  [:italic.subtle.bold
                                   "Flags passed to lightningcss:\n"]
                                  (with-out-str (fireworks.core/pprint flags))
                                  "\n\n"
                                  [:italic.subtle.bold
                                   "The following css will be returned:\n"]
                                  css-str)] 
                  (callout
                   (merge opts
                          {:type        :error
                           :label       (str "ERROR: "
                                             (string/replace (type e)
                                                             #"^class "
                                                             "" )
                                             " (Caught)")
                           :padding-top 0})
                   (point-of-interest
                    (merge opts {:type :error
                                 :body body}))))))
         css-str))))
