(ns kushi.ui.icon
  (:require
   [clojure.string :as string]
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.core :refer [sx merge-attrs at]]
   [kushi.ui.util]
   [kushi.ui.defs :as defs]
   [kushi.ui.shared.theming :refer [component-attrs variant-basics]]
   [kushi.ui.core :refer [defui]]
   ))

(defn- icon-name->snake-case-string [coll]
  (mapv #(cond
           (= % :<>)
           :<>
           (or (string? %) (keyword? %))
           (-> % name (string/replace #"-" "_"))
           :else %)
        coll))

(defui icon
  {:doc   "Icons provide compactness by indicating meaning using iconic symbols.
             
   By default, icons in Kushi are pulled in via
   [Google's Material Symbols font for the web]
   (https://developers.google.com/fonts/docs/material_symbols).
   Use [this page](https://fonts.google.com/icons?icon.set=Material+Symbols)
   to explore over 1000+ different icons.

   This component expects a child argument which is a string, keyword, or vector.

   If the argument is a string or keyword, it should correspond to the name of
   an existing mui icon.  By convention, it should be kebab-case (it is
   internally converted to snake-case to work with Google Material Symbols icon
   font).

   If the argument is a vector, it should be a hiccup representation of an svg
   icon. This must use `:viewBox` attribute with values such as `\"0 0 24 24\"`.
   The `:width` and `:height` attributes of the `svg` do not need to be set. For
   example, you can pass a Material Symbols icon in hiccupized `svg` to use in
   place of the Google Fonts Material Symbols font. This will be more performant,
   as you will avoid any Flash Of Unstyled Text that is a possibility when using
   modern icon fonts.

   There are small number of mui icon svgs in the `kushi.ui.icon.mui.svg`
   namespace which can be used. For example, to use the svg version of the
   (plus-symbol-shaped) \"Add\" icon you could require this namespace
   `[kushi.ui.icon.mui.svg :as mui.svg]` and then in your component code do
   `[icon mui.svg/add]`. You can also create and utilize similar namespace in
   your own project with your own collection of icon `svg`s."

   :props/shared [[:colorway {:default nil}]
                  :shape
                  :size
                  :weight       
                  :position
                  :inert
                  :transition
                  :icon-style
                  :icon-filled]}
  [& args]
  (let [{:keys [icon-style icon-filled surface colorway]} &props
        [icon*]                                           &children]

    [:div
     (merge-attrs
      (sx ".ks-icon"
          :.surface-transparent
          :d--inline-flex
          :flex-direction--row
          :jc--c
          :ta--center
          :ai--c
          ;; TODO - use tokenized syntax here
          [:>span:fs "var(--mui-icon-relative-font-size, inherit)"]
          :>span:fw--inherit
          [:>span.material-symbols-icon-filled:font-variation-settings "'FILL' 1"]
          :_svg:height--1em
          :_svg>path:fill--currentColor)
      &attrs)
     (cond
       (and (vector? icon*) (= :svg (first icon*)))
       icon*

       ;; TODO - Use with another icon set
       (every? #(or (string? %) (keyword? %)) &children)
       (let [icon-name  (icon-name->snake-case-string &children)
             icon-font  "material-symbols"

                        ;; maybe don't need check here
             style      (if (contains? #{:outlined :rounded :sharp} icon-style)
                          icon-style
                          :outlined)
             icon-style (str icon-font "-" (name style))
             icon-fill  (when icon-filled (str icon-font "-icon-filled"))]
         (into [:span {:class [icon-style icon-fill]}]
               icon-name)))]))

