(ns kushi.css.schemas
  (:require
   [bling.hifi]
   [bling.core :refer [bling print-bling callout]]
   [bling.explain :refer [explain-malli]]
   [fireworks.core :refer [? !?]]
   [malli.core :as m]
   [malli.generator :as mg]
   [malli.dev.pretty]
   [malli.error :as me]))

;; Helper function
(defn bookend [s]
  (str "^" s "$"))

;; Regex definitions (reused as-is)
(def unstacked-css-property-re-base
  "(?:-[a-zA-Z]+-)?[a-zA-Z_][a-zA-Z0-9_-]*")

(def css-var-prop-re-base
  "--[a-zA-Z0-9_-]+")

(def css-style-string-prop-val-separator-re-base
  ": ?")

(def css-style-string-val-re-base
  "[^;]+;?")

(def css-prop-for-style-attribute-re-base
  (str (bookend unstacked-css-property-re-base)
       "|"
       (bookend css-var-prop-re-base)))

(def css-prop-for-style-attribute-as-string-re-base
  (str unstacked-css-property-re-base
       "|"
       css-var-prop-re-base))

(def css-prop+value-for-style-attribute-as-string-re-base
  (str "(?:"
       css-prop-for-style-attribute-as-string-re-base
       ")"
       css-style-string-prop-val-separator-re-base
       css-style-string-val-re-base))


;; Malli schemas

(def style-string-for-style-attribute
  (let [re (re-pattern
            css-prop+value-for-style-attribute-as-string-re-base)]
    [:and
     string?
     [:fn #_{:error/message (str "Must satisfy css style-string regex:\n " (bling.hifi/hifi re))}
      #(re-find re %)]]))

(def css-prop-for-style-attribute
  [:and
   {:error/message "Value must be a string or keyword"}
   [:or {:error/message "Value must be a string or keyword"} string? keyword?]
   [:fn {:error/message (str "Must satisfy css property regex:\n "
                             (bling.hifi/hifi (re-pattern css-prop-for-style-attribute-re-base)))}
    #(re-find (re-pattern css-prop-for-style-attribute-re-base)
              (name %))]])

(def nameable
  [:or keyword? string? number?])

(def keyword-or-string?
  [:or keyword? string?])

(def css-value-for-style-attribute-style-map
  [:or nameable list?])

(def style-map-for-style-attribute
  [:map-of
   css-prop-for-style-attribute
   css-value-for-style-attribute-style-map])

(def sx2-form
  [:schema {:registry {::sx-map-default
                       [:map-of
                        keyword-or-string?
                        [:or
                         keyword-or-string?
                         [:ref ::sx-map-default]]]}}
   [:cat
    [:symbol {:value 'sx2}]
    [:+
     [:or
      [:symbol {:error/message "A symbol bound to an valid sx map"}]
      [:map
       {:error/message "A valid sx style map"}
       [:style {:optional true}
        [:or
         style-map-for-style-attribute
         style-string-for-style-attribute]]
       [::m/default
        [:ref ::sx-map-default]
        #_[:map-of
           [:or string? keyword?]
           [:ref ::sx-map-default]]]]]]]])

#_(println
   (-> [:map
        [:id :int]
        [:size [:enum {:error/message "should be: S|M|L"}
                "S" "M" "L"]]
        [:age [:fn {:error/fn (fn [{:keys [value]} _] (str value ", should be > 18"))}
               (fn [x] (and (int? x) (> x 18)))]]]
       (m/explain {:size "XL"
                   :age  10})
       (me/humanize
        #_{:errors (-> me/default-errors
                       (assoc ::m/missing-key {:error/fn (fn [{:keys [in]} _] (str "missing key " (last in)))}))})))

;; => {:id ["missing key :id"]
;;     :size ["should be: S|M|L"]
;;     :age ["10, should be > 18"]}

#_(? (m/explain [:cat
                 [:= 'sx2]
                 [:+
                  [:or
                   [:map-of :keyword :int]
                   :symbol]]]
                ['foo
                 {:a 1}
                 "hey"
                 {:b 2}]))



#_(? (me/humanize
      (m/explain sx2-form
                 '(sx2 #_{;; :style {12 "green"}
                          ;; 55     :gold
                          55     :blue
                          :color []}
                   "foo"
                       {:style {:buller :blue}
                        66 "guy"}))))
#_(explain-malli sx2-form
                 '(sx2 {;; :style {12 "green"}
                        ;; 55     :gold
                        55     :blue
                        ;; :color []
                        }
                       "foo"
                       {:style {:buller :blue}
                        66     "guy"})
                 {:file   "foo.cljs"
                  :line   11
                  :column 44})


(def sch
  [:tuple
   [:or string? number?]
   [:map [:foo :int] [:boo :int] [:bang [:or :string :keyword]]]])


(def v
  [:guh
   {:bang :bar}
   #_{"hey"       1
      ;; "b"              :foo
      ;; "adfasdfasdfsdf" 34
      :ff       111
      :asdfasdf 99
      :bang     22}])



#_(explain-malli
   sch
   v
   {:display-schema? true

    ;; :display-explain-data? true

    :success-message :bling.explain/explain-malli-success-verbose
    :file            "foo.cljs"
    :line            11
    :column          44})


#_(print-bling [:dark-yellow "◢◤"]
               [:dark-yellow "◢◤"]
               [:dark-yellow "◢◤"]
               [:dark-yellow "◢◤"])

#_(malli.dev.pretty/explain sch v)


#_(explain-malli
   sx2-form
   '(sx2 {;; :style {
          ;;         12 "green"
          ;;         :color :green}
          ;; 44 []
          ;; :color {:go []}
          :color :red}
         "foo"
         #_{:style {:color :red}})
   {:file    "foo.cljs"
    :line    11
    :column  44
    :spacing :compact})


#_(explain-malli
   [:tuple
    [:or :int :keyword]
    [:or :symbol :string]]
   [1]
   {:callout-opts {:label (bling [:bold.green "HIHI"])
                   :side-label nil}})


#_(callout {:colorway            :positive
            :label               "Foobar"
            :padding-top         0
            :padding-bottom      0}
           "hihi and then I want to say\nFroyo and that")

#_(explain-malli [:map
                  [:source-paths {:desc    "A vector of file paths representing namespaces to process with docstring."
                                  :example ["src/my_project/core.cljc"
                                            "src/my_project/api.cljc"]}
                   [:vector :string]]
                  [:exclude {:desc     "A vector of fully-qualified symbols representing functions to exclude from docstring generation"
                             :example  ['my-project/my-fn]
                             :optional true}
                   :boolean]

                  [:print-report? {:desc     "Prints a report with source path and updated-functions."
                                   :optional true
                                   :default  true}
                   :boolean]
                  [:cljfmt-options {:desc     "See `cljfmt` [Formatting Options](https://github.com/weavejester/cljfmt?tab=readme-ov-file#formatting-options)."
                                    :optional true
                                    :default  true}
                   :map]]
                 {:source-paths [222]})

#_(callout {:border-notches?   true
            :colorway          :error
            :label             "Malli Schema Error"
            :header-padding-left 1
            :label-theme       :minimal
            :margin-top        1
            :padding-bottom    1
            :padding-left      2
            :padding-top       1
            :side-label        "[unknown file]:236:1"
            :theme             :minimal}
           (friends {:prefix "WTF  " :emoji :flipping}))
