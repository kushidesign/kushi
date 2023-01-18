(ns kushi.playground.demobox
  (:require
   [clojure.string :as string]
   [clojure.walk :as walk]
   [clojure.pprint :as pprint]
   [kushi.core :refer (sx merge-attrs)]
   [kushi.ui.dom :as dom]
   [kushi.ui.input.slider.css]
   [kushi.ui.input.checkbox.core :refer [checkbox]]
   [kushi.ui.snippet.core :refer (copy-to-clipboard-button)]
   [kushi.ui.title.core :refer (title)]
   [kushi.ui.input.slider.core :refer (slider)]
   [kushi.ui.dom :refer (copy-to-clipboard)]
   [kushi.ui.core :refer (defcom)]
   [kushi.ui.input.radio.core :refer (radio)]
   [kushi.playground.shared-styles]
   [kushi.playground.util :as util :refer-macros (keyed)]
   [reagent.core :as r] ))

(def variants-by-category
  {:size     [:xxxsmall :xxsmall :xsmall :small :medium :large :xlarge #_:xxlarge #_:xxxlarge]
   :kind     [:default :minimal :bordered :filled]
   :kind2    [:default :bordered :filled]
  ;;  :status   [:disabled]
   :shape    [:pill :rounded :sharp]
   :weight   [:thin
              :extra-light
              :light
              :normal
              :wee-bold
              :semi-bold
              :bold
              :extra-bold
              #_:heavy]
   :semantic [:neutral
              :accent
              :positive
              :negative
              :warning]})


(defn sx-attr+children [coll]
  (when (coll? coll)
    (let [[a & xs] coll
          attr     (cond (map? a) a
                         (and (list? a) (= (first a) 'sx)) a)]
      [attr (if attr xs coll)])))

(defn variant-control-click
  [{:keys [variant-category
           utility-class
           *controls-by-type
           component-id
           nm]}]
  (when-let [els (.querySelectorAll (js/document.getElementById component-id)
                                    (str ".kushi-" nm))]
    (doseq [el els]
      #_(keyed variant-category
             utility-class
             component-id
             nm)
      (let [cl (.-classList el)]
        (doseq [[_ class] @*controls-by-type
                :when     class]
          (.remove cl class))
        (swap! *controls-by-type
               assoc
               variant-category
               utility-class)
        (doseq [[_ class] @*controls-by-type
                :when     class]
          (.add cl class))))))


  (defn user-on-change
    [{:keys [variant-category
             *controls-by-type
             component-id
             nm
             flavors]}
     %]

    (let [val  (dom/etv->int %)
          step (name (nth flavors val))]
      (when-let [els (.querySelectorAll (js/document.getElementById component-id)
                                        (str ".kushi-" nm))]

        #_(js/console.log
         {:component-id      nm
          :nm                nm
          :component-e       (js/document.getElementById component-id)
          :els               els
          :el-count          (.-length els)
          :flavors           flavors
          :variant-category  variant-category
          :*controls-by-type @*controls-by-type})

        (let [class-to-remove (get @*controls-by-type variant-category)
              class-to-add    step]
          (doseq [el els]
            ;; (js/console.log "\n" (.-innerText el) (str " \"" (.-classList el) "\""))
            (when class-to-remove
              ;; (js/console.log "need to remove class: " class-to-remove)
              ;; (js/console.log "remove classes: " flavors)
              )
            (when class-to-add
              ;; (js/console.log "adding class: " class-to-add)
              )
            (when (and class-to-add class-to-remove)
              (apply dom/remove-class (concat [el] flavors))
              (dom/add-class el class-to-add))

            ;; (js/console.log (str "associng " step " with " variant-category " on *controls-by-type") )
            (swap! *controls-by-type
                   assoc
                   variant-category
                   step))))))


(defcom input-row
  (let [{:keys [label group-id group-role]} &opts]
    [:section
     (merge-attrs
      (sx 'kushi-input-row-wrapper
          :.flex-col-c
          :color--inherit
          :.small)
      &attrs)
     [:fieldset
      (sx
       'kushi-input-row
       :.flex-row-fs
       :ai--c
       {:id              group-id
        :role            group-role
        :aria-labelledby (str group-id "-label")})

      [title
       (sx :.meta-desc-label
           :min-width--90px
           {:id (str group-id "-label")})
       label]
      [:section
       (sx
        'kushi-input-section
        :.flex-row-fs
        :flex-grow--1
        :flex-wrap--wrap )
       &children]]]))



(defn variant-controls
  [{:keys [variants *controls-by-type nm defaults] :as m}]
  (into [:<>]
        (for [variant-category variants
              :let             [label       (name variant-category)
                                flavors     (get variants-by-category variant-category)
                                on-change   (partial user-on-change (merge m (keyed variant-category flavors)))
                                slider?     (contains? #{"size" "weight"} label)
                                slider-opts {:-labels-attrs     (sx {:style {:mbe "calc(10px + 0.5em)"
                                                                             :ws  :n}})
                                             :-steps            flavors
                                             :-step-marker      :dot
                                             :-label-size-class :small
                                             :on-change         on-change
                                             :default-value     (when-let [dv* (some-> label keyword defaults)]
                                                                  (if (keyword? dv*) (name dv*) (str dv*)))}]]
          [input-row
           (sx  {:-label      label
                 :-group-id   (str nm "-" (string/lower-case label))
                 :-group-role (when-not slider? "radiogroup")
                 :style       {:&_.kushi-input-section:mbs (when slider? :2.25em)}})

           (cond
             slider?
             [slider slider-opts]

             :else
             (into [:<>]
                   (for [utility-class* flavors
                         :let           [utility-class   (name utility-class*)
                                         label-id        (str utility-class "-label")
                                         default-checked (variant-category @*controls-by-type)
                                         m+              (merge m (keyed variant-category utility-class))]]
                     [radio
                      {:id           label-id
                       :-input-attrs {:id              utility-class
                                      :tabIndex        0
                                      :aria-labelledby label-id
                                      :value           utility-class
                                      :name            (str nm "-example:" (name variant-category))
                                      :default-checked (= utility-class default-checked)
                                      :on-click        (partial variant-control-click m+)}}
                      utility-class])))])))



(defn- current+preview-classes
  [{:keys [*controls-by-type
           defaults
           *hide-default-classes?]}]
  (let [controls-by-type* @*controls-by-type
        current-classes  (vals controls-by-type*)
        controls-by-type (reduce (fn [acc [k v]]
                                   (let [dv*              (k defaults)
                                         dv               (if (keyword? dv*) (name dv*) (str dv*))
                                         matches-default? (= v dv)]
                                     (cond
                                       (false? @*hide-default-classes?)
                                       (assoc acc k v)
                                       matches-default?
                                       acc
                                       :else
                                       (assoc acc k v))))
                                 {}
                                 controls-by-type*)
        preview-classes  (filter #(not= % "none") (vals controls-by-type))]
    (keyed current-classes preview-classes)))


(defn insert-utility-classes-into-snippet
  [coll
   {:keys [nm] :as m}]
  (let [{:keys [current-classes preview-classes]} (current+preview-classes m)
        ret (if (empty? current-classes)
              coll
              (walk/postwalk
               #(if (and (vector? %) (= (first %) (symbol nm)))
                  (let [[compo & more]               %
                        [attr children]              (sx-attr+children more)
                        utility-classes              (keep (fn [x] (keyword (str "." x))) preview-classes)
                        rest-of-attr                 (if (map? attr) [attr] (rest attr))
                        utility-classes+rest-of-attr (concat utility-classes rest-of-attr)
                        ret                          (into []
                                                           (remove nil?
                                                                   (concat [compo
                                                                            (when (seq utility-classes+rest-of-attr)
                                                                              (cons 'sx (concat utility-classes rest-of-attr)))]
                                                                           children)))]
                    #_(js/console.log
                     :postwalk
                     (keyed %
                            compo
                            more
                            attr
                            children
                            utility-classes
                            rest-of-attr utility-classes+rest-of-attr))
                    ret)
                  %)
               coll))]
    ret))


(defn insert-utility-classes-into-dom
  [{:keys [component-id utility-class-target *controls-by-type]}]
  (let [dom-el (dom/el-by-id component-id)]
    (when-let [els (.querySelectorAll dom-el (str ".kushi-" utility-class-target ":not(.material-icons)"))]
    ;; (js/console.log "utility-class-target" utility-class-target)
    ;; (js/console.log (js/document.getElementById component-id))
    ;; (js/console.log els)
      (doseq [el els]
        (let [cl (.-classList el)]
          ;; #_(js/console.log cl)
          ;; #_(js/console.log @*controls-by-type)
          (doseq [[category class] @*controls-by-type
                  :when            class]
          ;; (js/console.log el)
            (when-let [to-remove* (->> category keyword variants-by-category (map name))]
              (keyed component-id utility-class-target category class to-remove*)
              ; figure out controls by type
              (apply dom/remove-class el to-remove*)
              (.add cl (name class))))))
      (when (= utility-class-target "slider")
        (dom/set! (.querySelector dom-el "input") :value 0)))))


(defn demobox2
  [{:keys      [defaults
                content
                utility-class-target]
    variants   :controls
    m*         :meta
    stage-attr :stage
    :as m+}]
  (let [*editor                (atom nil)
        nm                     (util/meta->fname m*)
        utility-class-target   (or utility-class-target nm)
        snippet-id             (str nm "-snippet")
        init-w-defaults?       true
        *hide-default-classes? (r/atom true)
        *controls-by-type      (r/atom (reduce (fn [acc kw]
                                                 (assoc acc kw (when init-w-defaults?
                                                                 (when-let [init (kw defaults kw)]
                                                                   (if (or (keyword? init) (string? init))
                                                                     (name init)
                                                                     (str init))))))
                                               {}
                                               variants))
        component-id           (subs (str m*) 2)
        *example               (r/atom (-> content first :example))
        current-snippet        (r/reaction (-> @*example
                                               :quoted
                                               (insert-utility-classes-into-snippet
                                                (keyed *hide-default-classes?
                                                       *controls-by-type
                                                       nm
                                                       variants
                                                       defaults))
                                               pprint/pprint
                                               with-out-str
                                               (string/replace #"," "")))
        demobox-st             (keyed variants
                                      *controls-by-type
                                      component-id
                                      snippet-id
                                      *editor
                                      current-snippet
                                      nm
                                      utility-class-target
                                      defaults)
        ]
    (r/create-class
     {:display-name         "example"
      :component-did-mount  (fn [_]
                              (insert-utility-classes-into-dom (keyed component-id utility-class-target *controls-by-type)))
      :component-did-update (fn [_]
                              (insert-utility-classes-into-dom (keyed component-id utility-class-target *controls-by-type)))
      :reagent-render       (fn []
                              [:section
                               (sx
                                'kushi-playground-demobox
                                :&_.kushi-input-row-wrapper:bbe--0px:solid:#eee
                                :&_.kushi-input-row-wrapper:min-height--60px
                                :&_.kushi-input-row-wrapper:padding-block--0.25em
                                :&_.kushi-radio-button-wrapper:margin-inline--0:0.666em
                                :&_.kushi-radio-button-wrapper:margin-block--0.125em)

                               ;; Component preview section
                               ;; ------------------------------------
                               [:section
                                (merge-attrs
                                 stage-attr
                                 (sx :.flex-row-fs
                                     :ai--c
                                     :p--30px:15px
                                     :bw--1px
                                     :bs--solid
                                     :bc--$gray300
                                     :dark:bc--$gray800
                                     :mb--10px
                                     {:id component-id}))
                                (:evaled @*example)]


                               ;; Content control section
                               ;; ------------------------------------
                               [input-row
                                {:-label "Examples"
                                 :-nm    nm}
                                (for [{:keys [label example key]} content
                                      :let [id (if (vector? label)
                                                 (str (last label) ":" (-> example :quoted first name))
                                                 label)
                                            key (or key id)]]
                                  ^{:key key}
                                  [radio
                                   (sx
                                    ["has-ancestor(#mui-icon):mis" :1em]
                                    ["has-ancestor(#mui-icon):mbe" :0.5em]
                                    {:-input-attrs {:id              id
                                                    :value           id
                                                    :name            (str nm "-example:content")
                                                    :default-checked (= label (:examples defaults))
                                                    :on-click        #(do ()
                                                                          (reset! *example example))}})
                                   (if (keyword? label) (name label) label)])]


                               ;; Variant controls section
                               ;; ------------------------------------
                               [variant-controls (assoc demobox-st :nm (or utility-class-target nm))]

                               ;; Show utility class if checked = default
                               ;; ------------------------------------
                               [:section
                                (sx :margin-block--1.5rem:0.5rem)

                               ;; Code snippet section
                               ;; ------------------------------------
                                [:div
                                 (sx :.relative)
                                 [:div
                                  (sx :.codebox
                                      {:id snippet-id})
                                  [util/formatted-code @current-snippet]]
                                 [:div (sx :.absolute-fill)]
                                 [copy-to-clipboard-button
                                  (sx :.absolute
                                      :inset-block-start--0
                                      :inset-inline-end--0
                                      {:on-click #(copy-to-clipboard @current-snippet)})]]

                                ;; Leave this out for now
                                #_[:div (sx :mb--0.5rem :pis--5px)
                                 [checkbox {:value           (str  @*hide-default-classes?)
                                            :default-checked (str  @*hide-default-classes?)
                                            :on-change       #(reset! *hide-default-classes? (not @*hide-default-classes?))
                                            :-label-attrs    (sx
                                                              :.xsmall
                                                              :.normal
                                                              :c--$gray700
                                                              :dark:c--$gray300)}
                                  "Hide utility classes in snippet if equal to default"]]]])})))
