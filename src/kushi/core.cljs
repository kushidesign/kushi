(ns ^:dev/always kushi.core
  (:require-macros [kushi.core])
  (:require
   [clojure.string :as string]))

(defn clean!
  "Removes all existing styles that were injected into #_kushi-dev_ style tag at dev time.
   Intended to be called by the projects main/core ns on every save/reload."
  []
  (js/console.log "cleaning!")
  (let [sheet (.-sheet (js/document.getElementById "_kushi-dev_"))
        rules (.-rules sheet)
        rules-len (.-length rules)]
    (doseq [idx (reverse (range rules-len))]
      (.deleteRule sheet idx))))

(defn cssfn? [x]
  (and (list? x)
       (= (first x) 'cssfn)
       (keyword? (second x))))

(declare cssfn)

(defn vec-in-cssfn [v]
  (string/join " " (map #(cond
                           (cssfn? %) (cssfn %)
                           (vector? %) (vec-in-cssfn %)
                           (keyword? %) (name %)
                           :else (str %))
                        v)))

(defn cssfn* [[_ nm & args]]
  (str (name nm)
       "("
       (string/join
        ", "
        (map #(cond
               (cssfn? %) (cssfn %)
               (vector? %) (vec-in-cssfn %)
               (keyword? %) (name %)
              ;;  (string? %) (str "\"" % "\"")
               :else (str %))
             args))
       ")"))

(defn cssfn [& args]
  (cssfn (cons 'cssfn args)))


(defn inject-stylesheet
  "Expects a map with the following keys: :rel, :href, and :cross-origin(optional).
   Appends stylesheet as <link> element to the document <head>.
   Only appends if link with identical attributes does not already exist in the document head.

   Examples:

   (inject-stylesheet {:rel \"preconnet\"
                       :href \"https://fonts.gstatic.com\"
                       :cross-origin \"anonymous\"})

   (inject-stylesheet {:rel \"preconnet\"
                       :href \"https://fonts.googleapis.com\"})

   (inject-stylesheet {:rel \"stylesheet\"
                       :href \"https://fonts.googleapis.com/css2?family=Rock+Salt&display=swap\"})"
  [m]
  (let [attr-selector* (map (fn [[k v]] (str "[" (name k) "=" "\"" v "\"" "]")) m)
        existing-link (js/document.querySelector (str "link" (string/join "" attr-selector*)))]
    (when-not existing-link
      (let [link (js/document.createElement "link")]
        (doseq [[attr val] m]
          (.setAttribute link (name attr) (name val)))
        (try
          (.appendChild js/document.head link)
          (catch :default e (js/console.warn
                             "kushi.core/s+:\n\nFailed attempt to inject stylesheet (or link):\n\n"
                             m
                             "\n\n¯\\_(ツ)_/¯")))))))
(defn- garden-mq-rule? [v]
  (and (map? v) (= :media (:identifier v))))

(defn inject-style-rules
  "Called at dev time for zippy previews."
  [css-rules selector]
  #_(js/console.log "css-rules" (str css-rules))
  (let [css-rules-as-indexed-seq (map-indexed vector css-rules)
        sheet (.-sheet (js/document.getElementById "_kushi-dev_"))
        selector-set (into #{} (->> sheet .-rules (map #(aget % "selectorText"))))]

    ;Inject rules only if selector is not already in the sheet
    (when-not (contains? selector-set selector)
      (doseq [[_ rule-css] css-rules-as-indexed-seq
              :let [updated-num-rules-idx (-> sheet .-rules .-length)]]
        (try
          (.insertRule sheet rule-css updated-num-rules-idx)
          (catch :default e (js/console.warn
                             "kushi.core/s+:\n\nFailed attempt to inject malformed css rule:\n\n"
                             rule-css
                             "\n\n¯\\_(ツ)_/¯"
                             e)))))))

(defn js-fmt-args
  [{:keys [invalid-args :styles-argument-display]}]
  (mapv #(let [bad? (contains? (into #{} invalid-args) %)
               q (when (string? %) "\"")]
           (str (if bad? (str "\n" "%c %c ")
                    "\n  ")
                (when bad? "%c")
                q (if bad? % "...") q
                (when bad? "%c")))
        styles-argument-display))

(def dict
  {:defclass {:expected (str "kushi.core/defclass expects a name (symbol),\n"
                             "followed by any number of the following:"
                             "\n\n  - Keyword representing style declaration, e.g.,"
                             "\n    :color--red"
                             "\n\n  - 2-element vector representing a style declaration, e.g.,"
                             "\n    [\"nth-child(2):color\" :blue]"
                             "\n\n  - Keyword representing an existing kushi class to be \"mixed-in\", e.g.,"
                             "\n    :bold-red-text")

              :learn-more "Look at kushi.core/defclass docs for more details."
              :find-source "Look at browser console for source map info."}

   :sx {:expected (str "kushi.core/sx expects:"
                       "\n\n"
                       "- Any number of the following:"
                       "\n\n  - Keyword representing style declaration, e.g.,"
                       "\n    :color--red"
                       "\n\n  - 2-element vector representing a style declaration, e.g.,"
                       "\n    [:color my-color]"
                       "\n\n  - Keyword representing a class, e.g."
                       "\n    :my-class"
                       "\n\n  - Valid conditional class expression"
                       "\n    (when my-condition :my-class)"
                       "\n\n"
                       "- An optional map of html attributes."
                       "\n  If present, this must be the last argument.")
        :learn-more "See kushi.core/sx docs for more details"}})

(defn warning-call-with-args
  [{:keys [fname classname] :as m}]
  (str "(" fname " "
       (when classname (name classname))
       (do
         (string/join (js-fmt-args m)))
       ")"))

(defn warning-header
  [{:keys [invalid-args fname]}]
  (str "Warning: %cInvalid argument" (when (< 1 (count invalid-args)) "s") "%c"  " to kushi.core/" fname "."))

(defn js-warning*
  [m]
  (when ^boolean js/goog.DEBUG
    (let [warning (string/join
                   "\n\n"
                   [(warning-header m)
                    (warning-call-with-args m)
                    (str (-> m :fname keyword dict :learn-more) "\n")])
          number-of-formats (count (re-seq #"%c" warning))]
      (to-array
       (concat
        [warning]
        ["color:black;font-weight:bold" "font-weight:normal" "font-weight:bold;color:#ffaa00" "font-weight:normal"]
        (interleave (repeat (/ (- number-of-formats 4) 2) "color:black;font-weight:bold")
                    (repeat (/ (- number-of-formats 4) 2) "color:default;font-weight:normal")))))))

(defn js-warning-sx [m*]
  (let [m (assoc m* :fname "sx")]
    (js-warning* m)))

(defn js-warning-defclass [m*]
  (let [m (assoc m* :fname "defclass")]
    (js-warning* m)))

(defn console-warning-number [compilation-warnings]
  (let [warning (string/join
                 "\n\n"
                 (map #(cond
                         (= (:warning-type %) :unitless-number)
                         (string/join
                          "\n\n"
                          [(str "Warning: %cInvalid value%c"
                                " of %c"
                                (:numeric-string %)
                                "%c for "
                                (:prop-hydrated %)
                                " in kushi.core/"
                                (name (:current-macro %)))
                           (str " Did you mean %c" (:numeric-string %) "px%c?\n")]))
                      compilation-warnings))
        number-of-formats (count (re-seq #"%c" warning))]
    (to-array
     (concat
      [warning]
      (interleave (repeat (/ number-of-formats 2) "color:black;font-weight:bold")
                  (repeat (/ number-of-formats 2) "color:default;font-weight:normal"))))))

(defn ns+
  "Creates a string that represents a fully namespaced-qualified
   identifier for an element within a component rendering function.
   String includes line number of parent function. This string is
   used as the value of a custom-data attribute, in order to help
   quickly identify the associated namespace of the specific element
   when inspecting output in an environment such as Chrome DevTools.

   Intended to be called with 1 or 2 arguments:
   1) A var-quoted name of the enclosing component rendering function.
   2) (Optional) A user-defined keyword, which is a semantic name associated with the html element.


   Example:

    (defn my-button [label]
      [:div
      (s+ {:cursor :pointer
           :text-align :center
           :border [[1 :solid :blue]}
          {:role :button
           :on-click #()
           :data-ns (ns+ #'my-button :outer)})
        [:span
         (s+ {:background :yellow}
             {:data-ns (ns+ #'my-button :inner})
         label]])"

  ([x]
   (when (= cljs.core/Var (type x))
     (ns+ x nil)))
  ([var-quoted-fn el-ident]
   (let [{ns* :ns name* :name line* :line} (meta var-quoted-fn)
         namespace* (when ns* (str ns* "/"))
         fn-name (when name* (str name*))
         el-ident-str (when el-ident (str (when fn-name "::") (name el-ident)))
         line-number (when line* (str ":" line*))]
     (str namespace* fn-name el-ident-str line-number))))
