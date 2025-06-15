(ns site.views2
  (:require
   [fireworks.core :refer [? !? ?> !?> pprint]]
   [domo.core :as domo]
  ;;  [bling.core :as bling :refer [bling print-bling callout point-of-interest]]
  ;;  [bling.hifi :refer [print-hifi hifi]]
  ;;  [bling.explain :refer [explain-malli]]
   [kushi.core :refer [?sx sx css merge-attrs at]]
   [kushi.playground.shared-styles]
   [kushi.ui.variants]
   [kushi.ui.button.core :refer [button]]
   [kushi.playground.showcase.core :refer [showcase uic-showcase-map2 uic-showcase-map]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.flex.core :as flex :refer [flex-row-start flex-col-start]]
   [kushi.ui.layout.core :refer [layout]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.icon.demo]
  ;;  [kushi.ui.label.core :refer [label]]
  ;;  [kushi.ui.radio.core :refer [radio]]
   [kushi.ui.spinner.core :refer [spinner]]
   [kushi.ui.util :as util]
   [clojure.string :as string]
   ;; [malli.core :as m]
   [kushi.ui.defs :as defs]))


(? :pp {:coll-limit 22} (range 20))


;; (sx "@keyframes wtf dd" 7)

(js/console.clear)

#_(pprint (? :data {1 2 3 :x}))



;; (? (:-colorway kushi.ui.variants/variants-by-custom-opt-key))
#_(defn my-radio [m]
  (let [id (str (:name m) "-radio-group_" (:value m) "-choice")]
    [flex-row-start
     {:class     (css :.pointer
                      :pb--0.33em
                      :pi--0.5em:0.75em
                      :w--fit-content)
      :-as       :section
      :-colorway :accent
      :-size     :xxlarge
      :-shape    :pill
      :-weight   :extra-light
      :-surface  :minimal}
     [radio (assoc m :id id)]

     ;; make this label component
     [label (merge-attrs
             (sx :pis--0.5em)
             {:-weight         :bold
              :-start-enhancer 8 #_[icon {:-weight :light} (:icon m)]
              :for             id})
      (string/capitalize (:value m))]]))

(defn main-view []
  (.setAttribute (domo/el-by-id "app")
                 "data-kushi-playground-active-path"
                 "components")

  ;; (? (m/validate [:cat
  ;;                 [:? [:map
  ;;                      [:foo {:optional true} :int]
  ;;                      [:bar {:optional true} :string]]]
  ;;                 [:* [:not :map]]]
  ;;                [{:foo 8} 2 3 4]))

  [:div (sx :m--100px)
   [button {:-start-enhancer :pets} "Click"]
   #_[icon {:-ns       (at)
          :-colorway :red
          :-size     :xxxlarge
          :-weight   :bold
          :-inert?   true
          :id        :foo}
    :star]]

  [:div (sx :m--100px)
   [button {:-colorway       :red 
            :-start-enhancer :pets
            :-surface        :solid}
    "Click"]]

  #_[showcase (uic-showcase-map2 kushi.ui.icon.core/icon kushi.ui.icon.demo/demos)]

  #_[showcase (uic-showcase-map2 kushi.ui.spinner.core/spinner #_kushi.ui.icon.demo/demos)]


  #_[showcase (uic-showcase-map2 kushi.ui.button.core/button #_kushi.ui.icon.demo/demos)]

  

  #_[:div 
  ;;  [icon
  ;;   (merge-attrs
  ;;    {:-start-enhancer [icon :phone]
  ;;     :-size           :xxxlarge
  ;;     :-weight         :thin}
  ;;    (sx :fs--98px))
  ;;   "star"]
     
     [button
      {:-start-enhancer 8 #_[icon :phone]
       :-size           :xxlarge
       :-weight         :bold}
      "Phone"]

     #_[flex-col-start (merge-attrs (sx :gap--1em :p--2rem)
                                    {:-as :section})
        [my-radio {:name  :baz
                   :icon  :email
                   :value "email"}]
        [my-radio {:name  :baz
                   :icon  :phone
                   :value "phone"}]]]

  #_[:div
     [button
      {
    ;; :-loading?     true
       :-end-enhancer #_[icon :east]
       [propeller]    }
      "Play"]
     
     [button
      {
    ;; :-loading?     true
       :-end-enhancer #_[icon :east]
       [donut]        }
      "Play"]
     
     [button
      {
    ;; :-loading?     true
       :-end-enhancer #_[icon :east]
       [thinking]     }
      "Play"]]

  ;; for pallette generation dev
  #_(js/setTimeout
     (fn []
       (dotimes [n (-> okstate deref :levels count)]
         (adjust-slider! {:pallette-idx pallette-idx 
                          :scale-key    :chroma-scale
                          :scale-idx    n}))
       #_(? (domo/qs "[data-scale='chroma'][data-level='450']"))
       )
     2000)

  ;; #_[showcase (? :pp (uic-showcase-map kushi.ui.button.core/button))]

  ;; #_[showcase (uic-showcase-map kushi.ui.spinner.core/spinner)]


  ;; #_[showcase (uic-showcase-map kushi.ui.radio.core/radio)]

;; ;; This will auto-generate children
  
  #_[radio-group 
     {:-radio-button-attrs {:name    :baz
                            :-size   :large
                            :-weight :bold}
      :-choices            ["Email" "Phone" "Mail"]}]
  

;; This will auto-generate...
  
  #_[:div (sx :.flex-col-fs :ai--fs :gap--0.75em)
     [:div (sx :.xxxlarge :.bold :.flex-row-fs :gap--0.5em)
      [radio-button {:name           :baz
                     :id             "baz-radio-group_email-choice"
                     :value          "email"
                     :-size          :xxxlarge
                     :-weight        :bold
                     :-label-attrs   {}
                     :-wrapper-attrs {}}]
      [:label {:for "baz-radio-group_email-choice"}
       "Email"]]
     

     [radio-button {:name    :baz
                    :value   "phone"
                    :-label  "phonnne"
                    :-size   :xxxlarge
                    :-weight :bold}]
     [radio-button {:name    :baz
                    :value   "mail"
                    :-label  "mailll"
                    :-size   :xxxlarge
                    :-weight :bold}]]


  #_[pane-samples]

  )

