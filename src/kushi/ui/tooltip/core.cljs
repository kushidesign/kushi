(ns kushi.ui.tooltip.core
  (:require-macros [kushi.utils :refer (keyed)])
  (:require
   [kushi.core :refer (sx merge-with-style) :refer-macros (sx)]
   [kushi.ui.core :refer (defcom defcom+ opts+children)]
   [kushi.ui.dom :refer (set-overlay-position! conditional-display?)]))

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
          "Tooltips in Kushi have no arrow indicator and are placed automatically depending on the parent element's relative postition in the viewport. "
          "Currently, tooltips are only placed above or below the parent element that they describe."]
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
            :desc    "Setting to either `:start` or `:end` will override horizontal auto-placement"}]}
  [& args]
  (let [[opts attr & children]      (opts+children args)
        {:keys [display-on-hover? inline-offset block-offset]} opts]
    [:section
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
          {:style                              {"has(ancestor([data-kushi-tooltip='true'][aria-expanded='true'])):opacity" 1
                                                "has(ancestor([data-kushi-tooltip='true'][aria-expanded='true'])):width"   :fit-content
                                                "has(ancestor([data-kushi-tooltip='true'][aria-expanded='true'])):height"  :auto
                                                "has(ancestor([data-kushi-tooltip='true'][aria-expanded='true'])):padding" :7px:14px}
           :data-kushi-conditional-display     (if (= false display-on-hover?) "true" "false")
           :data-kushi-tooltip-position-block  block-offset
           :data-kushi-tooltip-position-inline inline-offset
           :id                                 (gensym)
           :data-kushi-ui                      :tooltip})
      attr)
     children]))

(defcom+ tooltip2
  (let [{:keys [display-on-hover?]} &opts]
    [:section
     (merge-with-style
      (sx 'kushi-tooltip
          :._absolute
          :._mini
          :._rounded
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
          {:data-kushi-ui :tooltip
           :style {"has(ancestor([data-kushi-tooltip='true'][aria-expanded='true'])):opacity" 1
                   "has(ancestor([data-kushi-tooltip='true'][aria-expanded='true'])):width"   :fit-content
                   "has(ancestor([data-kushi-tooltip='true'][aria-expanded='true'])):height"  :auto
                   "has(ancestor([data-kushi-tooltip='true'][aria-expanded='true'])):padding" :7px:14px}
           :data-kushi-conditional-display (if (= false display-on-hover?) "true" "false")
           :id (gensym)})
      &attrs)
     &children]))
