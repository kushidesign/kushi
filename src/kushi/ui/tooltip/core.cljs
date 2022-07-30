(ns kushi.ui.tooltip.core
  (:require-macros [kushi.utils :refer (keyed)])
  (:require
   [kushi.core :refer (sx merge-with-style defclass)]
   [kushi.ui.core :refer (defcom opts+children)]
   [kushi.ui.dom :refer (set-overlay-position! conditional-display?)]))


 (defclass kushi-tooltip-placement-inline
  {:top       :50%!important
   :bottom    :unset!important
   :transform "translateY(-50%)!important"
   :margin    :0!important})

(defclass kushi-tooltip-placement-inline-end
  :.kushi-tooltip-placement-inline
  {:left      "calc(100% + var(--overlay-placement-inline-offset, 12px))!important"
   :right     :unset!important})

(defclass kushi-tooltip-placement-inline-start
  :.kushi-tooltip-placement-inline
  {:right     "calc(100% + var(--overlay-placement-inline-offset, 12px))!important"
   :left      :unset!important})

(defclass kushi-tooltip-placement-block
  {:left      :50%!important
   :right     :unset!important
   :transform "translateX(-50%)!important"
   :margin    :0!important})

(defclass kushi-tooltip-placement-block-start
  :.kushi-tooltip-placement-block
  {:bottom    "calc(100% + var(--overlay-placement-block-offset, 6px))!important"
   :top       :unset!important})

(defclass kushi-tooltip-placement-block-end
  :.kushi-tooltip-placement-block
  {:top       "calc(100% + var(--overlay-placement-block-offset, 6px))!important"
   :bottom    :unset!important})

(defn tooltip+parent [e]
 (let [node (-> e .-currentTarget)
       tooltip (.querySelector node ".kushi-tooltip")]
    (when tooltip
      (when-let [parent (.closest node "[data-kushi-tooltip='true']")]
        [tooltip parent]))))

(defn expand-tooltip! [tooltip parent]
  (set-overlay-position! tooltip parent)
  (.setAttribute parent "aria-expanded" "true")
  (.setAttribute parent "aria-described-by" (.getAttribute tooltip "id")))

(defn remove-tooltip! [parent]
  (.setAttribute parent "aria-expanded" "false")
  (.removeAttribute parent "aria-described-by"))

(defn add-temporary-tooltip!
  ([e]
   (add-temporary-tooltip! e 2000))
  ([e ms]
   (when-let [[tooltip parent] (tooltip+parent e)]
     (if (conditional-display? tooltip)
       (do
         (expand-tooltip! tooltip parent)
         (js/setTimeout (fn [_] (remove-tooltip! parent)) ms))
       (js/console.warn "[kushi.ui.tooltip.core/add-temporary-tooltip!]\n\nIf you want to trigger a temporary tooltip, you must explicitly use a value of `false` for the `:display-on-hover` entry in the opts argument to the tooltip component")))))

(defn tooltip
  {:desc ["Tooltips provide additional context when hovering or clicking on an element."
          :br
          :br
          "Tooltips in Kushi have no arrow indicator and are placed automatically depending on the parent element's relative postition in the viewport."
          "Tooltips are placed above or below the parent element that they describe."
          :br
          :br
          "Overriding auto placement - inline, block, or both - is available via the options `:-block-offset` and `:-inline-offset`."
          :br
          :br
          "Forcing centered-aligned placement to the top, bottom, left, or right of the parent item can be done with the `:-placement` option."
          :br
          :br
          "Exact placement can be achieved via css."]
   :opts '[{:name    display-on-hover?
            :type    :boolean
            :default true
            :desc    "Setting to `false` will conditionalize display based on user event such as a click"}
           {:name    block-offset
            :type    #{:start :end}
            :default nil
            :desc    "Setting to either `:start` or `:end` will override vertical auto-placement"}
           {:name    inline-offset
            :type    #{:start :end}
            :default nil
            :desc    "Setting to either `:start` or `:end` will override horizontal auto-placement"}
           {:name    placement
            :type    #{:inline-start :inline-end :block-start :block-end}
            :default nil
            :desc    "Setting to one of the accepted values will override horizontal and vertical auto-placement. When used, any supplied values for `:-inline-offset` and `:-block-offset` will be ignored."}]}
  [& args]
  (let [[opts attr & children]      (opts+children args)
        {:keys [display-on-hover? inline-offset block-offset placement]} opts]
    [:section
     (let [placement-class (when placement (str "kushi-tooltip-placement-" (name placement)))]
       (merge-with-style
        (sx 'kushi-tooltip
            :.absolute
            :.mini
            :.rounded
            :top--0
            :bottom--unset
            :left--100%
            :right--unset
            :p--0
            :m--5px
            :bgc--black
            :c--white
            :ws--n
            :o--0
            :w--0
            :h--0
            :overflow--hidden
            :transition--opacity:0.2s:linear
          ;; maybe abstract into an :.overlay defclass(es) with decoration defclasses for tooltip vs popover
            :line-height--1.5
            {:class                              [placement-class]
             :style                              {"has(ancestor([data-kushi-tooltip='true'][aria-expanded='true'])):transition" :none
                                                  "has(ancestor([data-kushi-tooltip='true'][aria-expanded='true'])):opacity" 1
                                                  "has(ancestor([data-kushi-tooltip='true'][aria-expanded='true'])):width"   :fit-content
                                                  "has(ancestor([data-kushi-tooltip='true'][aria-expanded='true'])):height"  :auto
                                                  "has(ancestor([data-kushi-tooltip='true'][aria-expanded='true'])):padding" :0.75em:1.5em}
             :data-kushi-conditional-display     (if (= false display-on-hover?) "true" "false")
             :data-kushi-tooltip-position-block  block-offset
             :data-kushi-tooltip-position-inline inline-offset
             :id                                 (gensym)
             :data-kushi-ui                      :tooltip})
        #_(when (= placement :inline-end)
            {:style {:left      "calc(100% + 12px)!important"
                     :right     :unset!important
                     :top       :50%!important
                     :transform "translateY(-50%)!important"
                     :margin    :0!important}})
        attr))
     children]))
