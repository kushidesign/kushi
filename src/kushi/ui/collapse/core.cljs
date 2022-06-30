(ns kushi.ui.collapse.core
  (:require-macros [kushi.utils])
  (:require
   [kushi.core :refer (sx merge-with-style) :refer-macros (sx)]
   [clojure.string :as string]
   [kushi.ui.collapse.header :refer (collapse-header-contents)]
   [kushi.ui.core :refer (defcom opts+children)]
   [kushi.ui.dom :as util]))


;TODO refactor this out
(defcom collapse-body
  [:section
   (sx 'kushi-collapse-body-wrapper :overflow--hidden)
   [:div:! (sx
            'kushi-collapse-body
            :bbe--1px:solid:transparent
            :padding-block--0.25em:0.5em)]])

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
                          accordian*                  (util/grandparent node)
                          accordian                   (when-let [cl (util/has-class accordian* "kushi-accordian")]
                                                        accordian*)
                          [open-node
                           open-exp-parent]           (other-expanded-node accordian node)
                          exp-parent                  (-> node .-nextSibling)
                          exp-inner                   (-> node .-nextSibling .-firstChild)
                          exp-inner-h                 (outer-height exp-inner)
                          expanded?                   (util/attribute-true? node :aria-expanded)
                          height                      (str exp-inner-h "px")
                          ->height                    (if expanded? "0px" height)
                          no-height?                  (and expanded? (string/blank? exp-parent.style.height))]
                      (when no-height? (set! exp-parent.style.height height))
                      (js/window.requestAnimationFrame (fn []
                                                         (when open-exp-parent
                                                           (set! open-exp-parent.style.height "0px")
                                                           (toggle-boolean-attribute open-node :aria-expanded))
                                                         (set! exp-parent.style.height ->height)
                                                         (toggle-boolean-attribute node :aria-expanded))))]
      (into [:div
             (merge-with-style
              (sx
               'kushi-collapse-header
               :.pointer
               {:class         [:.flex-row-fs :.collapse-header]
                :style         {:ai                                             :center
                                :padding-block                                  :0.75em
                                :transition                                     :all:200ms:linear
                                :+section:transition-property                   :height
                                :+section:transition-timing-function            "cubic-bezier(0.23, 1, 0.32, 1)"
                                :+section:transition-duration                   :--kushi-collapse-transition-duration
                                "&[aria-expanded='false']:+section:height"        :0px

                                "&[aria-expanded='false']:+section:>*:transition" :opacity:200ms:linear:10ms
                                "&[aria-expanded='true']:+section:>*:transition"  :opacity:200ms:linear:200ms

                                "&[aria-expanded='false']:+section:>*:opacity"    0
                                "&[aria-expanded='true']:+section:>*:opacity"     1}
                :role          :button
                :aria-expanded false
                :on-click      on-click})
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
           {:name    icon
            :type    :string
            :default "add"
            :desc    ["The [name of the Material Icon](https://fonts.google.com) to use in the collapse header."
                      "Optional."]}
           {:name    icon-expanded
            :type    :string
            :default "remove"
            :desc    ["The [name of the Material Icon](https://fonts.google.com) to use in the collapse header when expanded."
                      "Optional."]}
           {:name    icon-position
            :type    #{:start :end}
            :default :start
            :desc    ["A value of `:start` will place the at the inline start of the header, preceding the label."
                      "A value of `:end` will place the icon at the inline end of the header, opposite the label."
                      "Optional."]}]}
   [& args]
  (let [[opts attr & children]                           (opts+children args)
        {:keys [header-attrs body-attrs expanded? on-click icon-position]} opts]
    [:section
     (merge-with-style
      (sx
       'kushi-collapse
       :.flex-col-fs
       :w--100%
       {:data-kushi-ui :collapse})
      attr)
     [collapse-header
      (merge-with-style
       header-attrs
       (sx {:on-click       on-click
            :aria-expanded  (if expanded? "true" "false")
            :-icon-position icon-position}))
      [collapse-header-contents opts]]
     [collapse-body (:body body-attrs) children]]))


(defn accordian
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys []}              opts]
    (into
     [:div
      (merge-with-style
       {:class [:kushi-accordian]}
       attrs)]
     children)))

