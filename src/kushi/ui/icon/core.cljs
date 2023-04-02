(ns kushi.ui.icon.core
  (:require
   [clojure.string :as string]
   [kushi.core :refer (merge-attrs sx defclass)]
   [kushi.ui.core :refer (opts+children material-symbol-or-icon-span)]))

(defclass kushi-icon
  :.relative
  :d--inline-flex
  :flex-direction--row
  :jc--c
  :ta--center
  :ai--c
  [:>span:fs "var(--mui-icon-relative-font-size, inherit)"])

(defclass material-symbols-icon-filled
  [:font-variation-settings "'FILL' 1"])

(defn icon-name->snake-case-string [coll]
  (mapv #(cond
           (= % :<>)
           :<>
           (or (string? %) (keyword? %))
           (-> % name (string/replace #"-" "_"))
           :else %)
        coll))

(defn icon
  {:desc ["Icons in Kushi are pulled in via [Google's Material Symbols font for the web](https://developers.google.com/fonts/docs/material_symbols)."
          :br
          :br
          "Use [this page](https://fonts.google.com/icons?icon.set=Material+Symbols) to explore over 1000+ different icons."
          :br
          :br
          "This component expects a child argument which is a string or keyword that correspondes to the name of an existing mui icon. This string or keyword, by convention, should be kebab-case (it is internally converted to snake-case to work with Google Material Symbols icon font)"]
   :opts '[{:name    icon-style
            :pred    #{:outlined :rounded :sharp}
            :default :outlined
            :desc    "Controls the style of the [mui-icon](https://fonts.google.com/icons?icon.set=Material+Symbols)."}
           {:name    icon-filled?
            :pred  boolean?
            :default false
            :desc    "Use the filled (solid) version of the icon, if available"}
           {:name    icon-svg
            :pred  vector?
            :default false
            :desc    ["Pass a Material Symbols icon in `svg` (hiccup) to use in place of the Google Fonts Material Symbols font."
                      "Must use `:viewBox` attribute with values such as `\"0 0 24 24\"`."
                      "The `:width` and `:height` attributes of the `svg` do not need to be set."]}]}
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys [icon-svg icon-style icon-filled?]} opts]
    [:div
     (merge-attrs
      (sx
       'kushi-icon
       :&_svg:height--1em
       :&_svg>path:fill--currentColor
       {:data-kushi-ui :icon})
      attrs)
     (let [icon-name (icon-name->snake-case-string children)]
       (or
        icon-svg
        (material-symbol-or-icon-span
         {:icon-name    icon-name
          :icon-style   icon-style
          :icon-filled? icon-filled?})))]))
