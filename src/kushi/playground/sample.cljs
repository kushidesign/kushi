(ns kushi.playground.sample
  (:require
   [kushi.core :refer (sx defclass merge-attrs)]


  ;;  [kushi.ui.core :refer (opts+children defcom defcom)]
  ;;  [domo.core :as domo]
  ;;  [kushi.ui.input.slider.core :refer (slider)]
  ;;  [kushi.ui.button.core :refer (button)]
  ;;  [kushi.ui.collapse.core :refer (collapse accordion)]
  ;;  [kushi.ui.tooltip.core :refer (tooltip2 tooltip add-temporary-tooltip!)]


  ;;  [kushi.ui.modal.core :refer (modal close-kushi-modal open-kushi-modal)]
  ;;  [kushi.ui.snippet.core :refer (snippet copy-to-clipboard-button)]
  ;; ;; ;;  [kushi.ui.icon.core :refer (icon)]
  ;;  [clojure.pprint :refer [pprint]]
   ))

#_(defn sample []
    [collapse
     (sx 'collapse-wrapper-override
         :.xlarge
         :.semi-bold
         :ff--Inter
         :bbe--4px:solid:#efefef
         :&_p:m--5px
         {:-label         "Collapsable section label "
          :-icon-position :end
          :-icon          "plus"
          :-expanded?     true})
     [button "hi"]
     [:div (sx :.flex-row-fs)
      [:div
       [:p (sx :.xxxsmall) "XXXSmall"]
       [:p (sx :.xxsmall) "XXSmall"]
       [:p (sx :.xsmall) "XSmall"]
       [:p (sx :.small) "Small"]
       [:p (sx :.medium) "Medium"]
       [:p (sx :.large) "Normal"]
       [:p (sx :.xlarge) "XLarge"]
       [:p (sx :.huge) "Huge"]]
      [:div
       [:p (sx :.thin) "Thin"]
       [:p (sx :.extra-light) "Extra Light"]
       [:p (sx :.light) "Light"]
       [:p (sx :.normal) "Normal"]
       [:p (sx :.wee-bold) "Wee Bold"]
       [:p (sx :.semi-bold) "Semi Bold"]
       [:p (sx :.bold) "Bold"]
       [:p (sx :.extra-bold) "Extra Bold"]
       [:p (sx :.heavy) "Heavy"]]]])

(def bgc :teal)
(def bgcsm :orange)

(defclass tessst :c--red)


(defn sample []
  (let [steps [:A :B :C :D]]
    [:div (sx :.tessst
              {:id    :foo
               :style {:bgc   bgc
                       :$wtf :red
                       :color :$wtf} })
     "hi"]



    ;; [:div (sx {:style {:bgc bgc}} :p--10px) "hi"]
    #_[slider (sx  {:default-value       :D
                    :min                 1
                    :max                 3
                    :on-change           #(let [val  (domo/etv->int %)
                                                step (name (nth steps val))]
                                            #_(js/console.log step))
                    :-label-size-class   :small
                    :-label-block-offset "-45px"
                    :-steps              steps
                    :-header-attrs       (sx {:style {:mt                                                                 :200px
                                                      :padding-inline                                                     :4rem!important
                                                      "&_.kushi-slider-step-label:not(.kushi-slider-step-label-selected):o" 0.0}})})])



  #_[collapse
     {:-label    "collapsable section label"
      :-on-click #(js/alert "clicked")
      :onMouseEnter #(js/console.log  "entered")}
     [:p "child 1"]
     [:p "child 2"]]
  #_[button {:on-mouse-leave #(js/console.log "clicked")
             :on-mouse-enter #(js/console.log "clicked")
             :on-click #(js/console.log "clicked")} #_(merge-attrs (sx :.pill :c--red) (sx :c--green))
     "child 1" "child 2xxx"
     [tooltip #_{:-display-on-hover? false} "Blah Blah and blah blah blah"]]
  #_[:div (sx {:style {:c      :red
                       :bgc    bgc
                       :sm:bgc bgcsm}}) "Hi"]
  #_[modal {:-trigger [button (sx :.absolute :right--0 {:on-click open-kushi-modal})
                       "click to launch modal"
                       [tooltip2 (sx :.huge) #_{:-display-on-hover? false} "Blah Blah and blah blah blah"]]}
     [:div (sx :.flex-col-sa :h--100% :w--100%)
      [:p (sx :.huge :.normal) "modal content"]
      [button {:on-click close-kushi-modal}
       "submit and dismiss"]]])

#_(defcom sample
    (let [{:keys [foo]} &opts]
      [:div &attrs "hi from sample w defcom"
       [:div &children]
       [:div (str "ass-" (:foo &opts))]]))

;; defcom example
#_(defcom sample
    [:div (sx 'my-baseline :c--green) "ppink"]
    #_[:div (sx `foo:ui :m--1rem :.huge)
       [:div (sx 'wtf:ui :c--green) [:div "baz"]]
       [:div:! (sx 'sux:ui :c--blue)
        "bar " "foo "]]
    #_[:div (sx :m--1rem :.small)
       [:div (sx 'shit:ui :c--green)
        [:div:! "blow"]]
       [:div (sx 'sux:ui :c--blue)
        "gone " "baby "] "ppink"]
    #_[:div (sx :fs--1*) "hi"])

;; sizes cruft
#_[:div (sx :p--40px :ff--Inter) #_(sx 'main-view-wrapper :._xxxsmall :.huge :._purple :c--red {:class [ #_:._xxxsmall]
                                                                                                :style {:c :blue}})
   #_[:div (sx :p--40px :d--flex :ff--Inter)
      [:div
       [:p (sx :fs--xx-small) "sized"]
       [:p (sx :fs--x-small) "sized"]
       [:p (sx :fs--small) "sized"]
       [:p (sx :fs--medium) "sized"]
       [:p (sx :fs--large) "sized"]
       [:p (sx :fs--x-large) "sized"]
       [:p (sx :fs--xx-large) "sized"]
       [:p (sx :fs--xxx-large) "sized"]]
      [:div (sx :tt--u :ml--20px)
       [:p (sx :fs--xx-small) "sized"]
       [:p (sx :fs--x-small) "sized"]
       [:p (sx :fs--small) "sized"]
       [:p (sx :fs--medium) "sized"]
       [:p (sx :fs--large) "sized"]
       [:p (sx :fs--x-large) "sized"]
       [:p (sx :fs--xx-large) "sized"]
       [:p (sx :fs--xxx-large) "sized"]]]
   [sample]
   #_[sample (sx :c--red :bgc--aliceblue {:-foo 19}) " user child1 " " user child 2 "]]
