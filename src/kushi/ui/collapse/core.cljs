(ns kushi.ui.collapse.core
  (:require-macros [kushi.utils :refer (keyed)])
  (:require
   [kushi.core :refer (sx defclass merge-with-style) :refer-macros (sx)]
   [clojure.string :as string]
   [kushi.ui.collapse.header :refer (collapse-header-contents)]
   [kushi.ui.core :refer (gui defcom opts+children)]
   [kushi.ui.util :as util]
   ))

(def ? js/console.log)
;; Accordian with multiple expandable
;; Dual animation
;; positioning of icon
;; make :. work in class vectors

(defcom collapse-body
  [:section
   (sx
    :overflow--hidden)
   [:div:! (sx
            :bt--1px:solid:transparent
            :bb--1px:solid:transparent)]])

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
  #_(? {:styles styles
      :margin-top margin-top
      :margin-bottom margin-bottom
      :ret ret})
  ret))

(defclass collapse-header
  :.pointer
  :ai--center
  :p--10px:0px
  :transition--all:200ms:linear)

(defn other-expanded-node
  [accordian-node clicked-node]
  (when accordian-node
    (when-let [open-node (.querySelector accordian-node "section>div[aria-expanded='true'][role='button']")]
      (when-not (= open-node clicked-node)
        [open-node (-> open-node .-nextSibling)]))))


(defcom collapse-header
  (let [on-click #(let [node        (.closest (-> % .-target) "[aria-expanded][role='button']")
                        accordian*  (util/grandparent node)
                        accordian   (when-let [cl (util/has-class accordian* "kushi-accordian")]
                                      accordian*)
                        [open-node
                         open-exp-parent] (other-expanded-node accordian node)
                        exp-parent  (-> node .-nextSibling)
                        exp-inner   (-> node .-nextSibling .-firstChild)
                        exp-inner-h (outer-height exp-inner)
                        expanded?   (util/attribute-true? node :aria-expanded)
                        height      (str exp-inner-h "px")
                        ->height    (if expanded? "0px" height)
                        no-height?  (and expanded? (string/blank? exp-parent.style.height))]
                    (when no-height? (set! exp-parent.style.height height))
                    (js/window.requestAnimationFrame (fn []
                                                       (when open-exp-parent
                                                         (set! open-exp-parent.style.height "0px")
                                                         (toggle-boolean-attribute open-node :aria-expanded))
                                                       (set! exp-parent.style.height ->height)
                                                       (toggle-boolean-attribute node :aria-expanded))))]
    [:div
     (sx
      {:class         [:.flex-row-fs :.collapse-header]
       :style         {
                       :+section:transition-property                     :height
                       :+section:transition-timing-function              "cubic-bezier(0.23, 1, 0.32, 1)"
                       :+section:transition-duration                     :500ms
                       "&[aria-expanded='false']:+section:height"        :0px

                       "&[aria-expanded='false']:+section:>*:transition" :opacity:200ms:linear:10ms
                       "&[aria-expanded='true']:+section:>*:transition"  :opacity:200ms:linear:200ms

                       "&[aria-expanded='false']:+section:>*:opacity"    0
                       "&[aria-expanded='true']:+section:>*:opacity"     1}
       :role          :button
       :aria-expanded false
       :on-click      on-click
       :prefix        :kushi-
       :ident         :collapse-header
       :ui?         true
       })]))

(defn get-attr [m k] (some-> m :parts k first))
(defn get-children [m k] (some-> m :parts k rest))


(defn collapse
  "A section of content which can be collapsed and expanded"
  [& args]
  (let [[opts attr & children]  (opts+children args)
        {:keys [parts expanded? on-click]} opts]
    [:section
     (merge-with-style
      (sx
       'kushi-collapse:ui
       :.flex-col-fs
       :w--100%)
      attr)
     [collapse-header
      (merge-with-style
       (:header parts)
       (sx {:on-click on-click
            :aria-expanded (if expanded? "true" "false")}))
      #_[collapse-footer-contents {:label-text label-text :label-text-expanded label-text-expanded}]
      [collapse-header-contents opts]]
     [collapse-body (:body parts) children]]))

(defcom accordian
  [:div.kushi-accordian])
