(ns kushi.ui.tooltip.core
  (:require-macros [kushi.utils :refer (keyed)])
  (:require
   [kushi.core :refer (sx defclass merge-with-style) :refer-macros (sx)]
   [kushi.gui :refer (gui defcom opts+children)]
   [kushi.ui.util :refer (set-overlay-position! conditional-display?)]
   [par.core :refer [? !? ?+ !?+]]))

(defn tooltip+parent [e]
 (let [node (-> e .-target)
       tooltip (.querySelector node ".kushi-tooltip")]
    (when tooltip
      (when-let [parent (.closest node "[data-kushi-tooltip='true']")]
        [tooltip parent]
        ))))

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
     (if (? (conditional-display? tooltip))
       (do
         (expand-tooltip! tooltip parent)
         (js/setTimeout (fn [_] (remove-tooltip! parent)) ms))
       (js/console.warn "[kushi.ui.tooltip.core/add-temporary-tooltip!]\n\nIf you want to trigger a temporary tooltip, you must explicitly use a value of `false` for the `:display-on-hover` entry in the opts argument to the tooltip component")))))

(defn get-attr [m k] (some-> m :parts k first))
(defn get-children [m k] (some-> m :parts k rest))


(defn tooltip
  "A section of content which can be collapsed and expanded"
  [& args]
  (let [{:keys [opts children]}           (opts+children args)
        {:keys [display-on-hover? attrs]} opts]
    [:section
     (merge-with-style
      (sx :.absolute
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
          {:style {"has(ancestor([data-kushi-tooltip='true'][aria-expanded='true'])):opacity" 1
                   "has(ancestor([data-kushi-tooltip='true'][aria-expanded='true'])):width" :fit-content
                   "has(ancestor([data-kushi-tooltip='true'][aria-expanded='true'])):height" :auto
                   "has(ancestor([data-kushi-tooltip='true'][aria-expanded='true'])):padding" :7px:14px}
           :data-kushi-conditional-display (if (= false display-on-hover?) "true" "false")
           :id (gensym)
           :prefix :kushi-
           :ident :tooltip
           :kushi-ui? true})
      attrs)
     children]))