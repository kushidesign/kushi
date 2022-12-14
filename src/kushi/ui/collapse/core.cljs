(ns kushi.ui.collapse.core
  (:require
   [kushi.core :refer (sx merge-attrs) :refer-macros (sx)]
   [clojure.string :as string]
   [kushi.ui.collapse.header :refer (collapse-header-contents)]
   [kushi.ui.core :refer (defcom opts+children)]
   [kushi.ui.dom :as dom]))

;TODO refactor this out
(defcom collapse-body
  [:section
   (merge-attrs (sx 'kushi-collapse-body-wrapper :overflow--hidden) &attrs)
   [:div (sx
          'kushi-collapse-body
          :bbe--1px:solid:transparent
          :padding-block--0.25em:0.5em)
    &children]])

(defn toggle-class-on-ancestor [node root-class class]
  (let [root (.closest node (str "." (name root-class)))]
    (when root (.toggle (.-classList root) (name class)))))

(defn toggle-boolean-attribute [node attr]
  (let [aria-expanded? (.getAttribute node (name attr))
        newv (if (= aria-expanded? "false") true false)]
    (.setAttribute node (name attr) newv)))

(defn outer-height [el]
  (let [styles (js/window.getComputedStyle el)
        margin-top (js/parseFloat (.-marginTop styles))
        margin-bottom (js/parseFloat (.-marginBottom styles))
        ret  (+ margin-top margin-bottom (js/Math.ceil (.-offsetHeight el)))]
    ret))

(defn other-expanded-node
  [accordian-node clicked-node]
  (when accordian-node
    (when-let [open-node (.querySelector accordian-node "section>div[aria-expanded='true'][role='button']")]
      (when-not (= open-node clicked-node)
        [open-node (-> open-node .-nextSibling)]))))

(defn collapse-header
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys [icon-position]} opts]
    (let [on-click #(let [node                        (.closest (-> % .-target) "[aria-expanded][role='button']")
                          collapse                    (.-parentNode node)
                          accordian*                  (dom/grandparent node)
                          accordian                   (when (dom/has-class accordian* "kushi-accordian")
                                                        accordian*)
                          [open-node
                           open-exp-parent]           (other-expanded-node accordian node)
                          exp-parent                  (-> node .-nextSibling)
                          collapsed?                  (= "none"  (.-display (.-style exp-parent)))
                          _                           (when collapsed? (set! exp-parent.style.display "block"))
                          exp-inner                   (-> node .-nextSibling .-firstChild)
                          exp-inner-h                 (outer-height exp-inner)
                          aria-expanded?              (dom/attribute-true? node :aria-expanded)
                          height                      (str exp-inner-h "px")
                          ->height                    (if aria-expanded? "0px" height)
                          no-height?                  (and aria-expanded? (string/blank? exp-parent.style.height))
                          toggle-op                   (if aria-expanded? dom/remove-class dom/add-class)]
                      (toggle-op collapse :kushi-collapse-expanded)
                      (when no-height? (set! exp-parent.style.height height))
                      (js/window.requestAnimationFrame (fn []
                                                         (when open-exp-parent
                                                           (set! open-exp-parent.style.height "0px")
                                                           (toggle-boolean-attribute open-node :aria-expanded))
                                                         (set! exp-parent.style.height ->height)
                                                         (toggle-boolean-attribute node :aria-expanded)
                                                         (if-not collapsed?
                                                           (js/setTimeout (fn []
                                                                            (set! exp-parent.style.display "none"))
                                                                          200)
                                                           (js/setTimeout (fn []
                                                                            (set! exp-parent.style.height "auto"))
                                                                          210)))))]
      (into [:div
             (merge-attrs
              (sx
               'kushi-collapse-header
               :.pointer
               {:class         [:.flex-row-fs :.collapse-header]
                :style         {:ai                                            :center
                                :padding-block                                 :0.75em
                                :transition                                    :all:200ms:linear
                                :+section:transition-property                  :height
                                :+section:transition-timing-function           "cubic-bezier(0.23, 1, 0.32, 1)"
                                :+section:transition-duration                  :--kushi-collapse-transition-duration
                                "&[aria-expanded='false']+section:height"        :0px

                                "&[aria-expanded='false']+section:>*:transition" :opacity:200ms:linear:10ms
                                "&[aria-expanded='true']+section:>*:transition"  :opacity:200ms:linear:200ms

                                "&[aria-expanded='false']+section:>*:opacity"    0
                                "&[aria-expanded='true']+section:>*:opacity"     1}
                :tabIndex      0
                :role          :button
                :aria-expanded false
                :on-click      on-click
                :onKeyDown     #(when (or (= "Enter" (.-key %)) (= 13 (.-which %)) (= 13  (.-keyCode %)))
                                  (-> % .-target .click))})
              attrs)]
            children))))

(defn collapse
  {:desc ["A section of content which can be collapsed and expanded."]
   :opts '[{:name    label
            :type    :string
            :default nil
            :desc    "The text to display in the collapse header."}
           {:name    label-expanded
            :type    :string
            :default nil
            :desc    "The text to display in the collapse header when expanded. Optional."}
           {:name    mui-icon
            :type    :string
            :default "add"
            :desc    ["The [name of the Material Icon](https://fonts.google.com/icons?icon.set=Material+Icons) to use in the collapse header."
                      "Optional."]}
           {:name    mui-icon-expanded
            :type    :string
            :default "remove"
            :desc    ["The [name of the Material Icon](https://fonts.google.com/icons?icon.set=Material+Icons) to use in the collapse header when expanded."
                      "Optional."]}
           {:name    icon-position
            :type    #{:start :end}
            :default :start
            :desc    ["A value of `:start` will place the at the inline start of the header, preceding the label."
                      "A value of `:end` will place the icon at the inline end of the header, opposite the label."
                      "Optional."]}
           {:name    expanded?
            :type    :boolean
            :default false
            :desc    ["When a value of `true` is passed, the collapse is initially rendered in an expanded state."
                      "Optional"]}
           ]}
  [& args]
  (let [[opts attr & children]  (opts+children args)
        {:keys [header-attrs
                body-attrs
                expanded?
                on-click
                icon-position]} opts]
    [:section
     (merge-attrs
      (sx
       'kushi-collapse
       :.flex-col-fs
       (when expanded? :.kushi-collapse-expanded)
       :w--100%
       {:data-kushi-ui :collapse})
      attr)
     [collapse-header
      (merge-attrs header-attrs
                   (sx #_["[aria-expanded='false']+.kushi-collapse-body-wrapper:d" :none]
                    {:on-click       on-click
                     :aria-expanded  (if expanded? "true" "false")
                     :-icon-position icon-position}))
      [collapse-header-contents opts]]

     ;; collapse body
     [:section
      (merge-attrs (sx 'kushi-collapse-body-wrapper
                       :overflow--hidden
                       {:disabled true})
                   body-attrs
                   {:style {:display :none}})
      (into [:div (sx
                   'kushi-collapse-body
                   :bbe--1px:solid:transparent
                   :padding-block--0.25em:0.5em)]
            children)]]))


(defn accordian
  {:desc ["A wrapper for multiple instances of the `collapse` component."
          :br
          "When `collapse` components are children of the accordian component, they can only be expanded one at a time."]}
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys []}              opts]
    (into
     [:div
      (merge-attrs
       {:class [:kushi-accordian]}
       attrs)]
     children)))

