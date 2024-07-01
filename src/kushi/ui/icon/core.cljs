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
  {:summary ["Icons provide compactness by visually indicating meaning using iconic symbols."]
   :desc    ["By default, icons in Kushi are pulled in via [Google's Material Symbols font for the web](https://developers.google.com/fonts/docs/material_symbols)."
             :br
             :br
             "Use [this page](https://fonts.google.com/icons?icon.set=Material+Symbols) to explore over 1000+ different icons."
             :br
             :br
             "This component expects a child argument which is a string, keyword, or vector."
             :br
             :br
             "If the argument is a string or keyword, it should correspond to the name of an existing mui icon.  By convention, it should be kebab-case (it is internally converted to snake-case to work with Google Material Symbols icon font)"
             :br
             :br
             "If the argument is a vector, it should be a hiccup representation of an svg icon."
             "This must use `:viewBox` attribute with values such as `\"0 0 24 24\"`."
             "The `:width` and `:height` attributes of the `svg` do not need to be set."
             "For example, you can pass a Material Symbols icon in hiccupized `svg` to use in place of the Google Fonts Material Symbols font."
             "The will be more performant, as you will avoid any Flash Of Unstyled Text that is a possibility when using modern icon fonts."
             "There are small number of mui icon svgs in the `kushi.ui.icon.mui.svg` namespace which can be used."
             "For example, to use the svg version of the (plus-symbol-shaped) \"Add\" icon you could require this namespace `[kushi.ui.icon.mui.svg :as mui.svg]` and then in your component code do `[icon mui.svg/add]`."
             "You can also create and utilize similar namespace in your own project with your own collection of icon `svg`s."
             ]
   :opts    '[{:name    icon-style
               :pred    #{:outlined :rounded :sharp}
               :default :outlined
               :desc    ["Controls the style of the [mui-icon](https://fonts.google.com/icons?icon.set=Material+Symbols)."
                         "This pertains only to icons from the Materials Symbols icon font, which uses variable font features."
                         "Note that the requested style variant must be present in the Material Symbols font you are pulling in."
                         "Please see `docs/src/site/theme.cljc` in the Kushi repo for configuration examples."]}
              {:name    icon-filled?
               :pred    boolean?
               :default false
               :desc    ["Use the filled (solid) version of the icon, if available."
                         "This pertains only to icons from the Materials Symbols icon font, which uses variable font features."
                         "Note that the requested axis for `fill` must be present in the Material Symbols font you are pulling in. Please see docs/src/site/theme.cljc in the Kushi repo for configuration examples."]}]}
  [& args]
  (let [[opts attrs & children]           (opts+children args)
        {:keys [icon-style icon-filled?]} opts
        [icon*]                           children]
    [:div
     (merge-attrs
      (sx
       'kushi-icon
       :&_svg:height--1em
       :&_svg>path:fill--currentColor
       {:data-kushi-ui :icon})
      attrs)
     (cond
       (and (vector? icon*) (= :svg (first icon*)))
       icon*

       (every? #(or (string? %) (keyword? %)) children)
       (let [icon-name (icon-name->snake-case-string children)]
         (material-symbol-or-icon-span
          {:icon-name    icon-name
           :icon-style   icon-style
           :icon-filled? icon-filled?})))]))
