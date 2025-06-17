(ns kushi.ui.icon
  (:require
   [clojure.string :as string]
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.core :refer (sx merge-attrs at)]
   [kushi.ui.defs :as defs]
   [kushi.ui.shared.theming :refer [component-attrs variant-basics]]
   [kushi.ui.core :refer (extract validate)]))

(defn- icon-name->snake-case-string [coll]
  (mapv #(cond
           (= % :<>)
           :<>
           (or (string? %) (keyword? %))
           (-> % name (string/replace #"-" "_"))
           :else %)
        coll))

(defn icon
  {:doc  "Icons provide compactness by visually indicating meaning using iconic symbols.
             
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

   :opts {:sizing       {:default :medium
                         :desc    "Corresponds to the font-size based on Kushi's font-size scale."
                         :demo    {:label           "Sizes"
                                   :attrs           {}
                                   :variant-labels? false
                                    ;; :x-variants [:weight]
                                   :args            [:star]
                                   :row-style       {:width           "100%"
                                                     :justify-content "space-between"}}}
          
          :weight       {:default :normal
                         :desc    "Corresponds to the font-weight based on Kushi's font-weight scale."
                         :demo    {:label           "Weights"
                                   :attrs           {}
                                   :attrs/display   {:sizing :xxxlarge}
                                   :variant-labels? false
                                   :args            [:star]
                                   :row-style       {:width           "100%"
                                                     :justify-content "space-between"}}}
          
          :colorway     {:default nil
                         :desc    "Colorway of the icon. Can also be a named color from Kushi's design system, e.g `:red`, `:purple`, `:gold`, etc."
                         :demo    [{:label           "Colorways"
                                    :attrs           {:sizing :xxxlarge}
                                    :args            [:star]
                                    :variant-labels? false
                                    :variant-scale   :colorway/named}
                                   {:label           "Colorways, filled icon"
                                    :attrs           {:icon-filled? true
                                                      :sizing       :xxxlarge}
                                    :args            [:star]
                                    :variant-labels? false
                                    :variant-scale   :colorway/named}]}
          
          :icon-filled? {:schema  boolean?
                         ;; :required? true
                         :default false
                         :desc    "Filled or not filled"
                         :demo    {:label         "Filled icon"
                                   :attrs         {}
                                   :attrs/display {:sizing :xxxlarge}
                                   :args          [:star]}}
          
          :icon-style   {:schema  (into #{} defs/icon-style)
                         :default :outlined
                         :desc    "Style of icon"
                         :demo    {:label "Icon styles"
                                   :attrs {:sizing :xxxlarge}
                                   :args  [:login]}}
          
          :inert?       {:schema  boolean?
                         :default false
                         :desc    "Determines whether the icon will feature hover and active styles"
                         :demo    {:label "Inert or interactive styling"
                                   :attrs {:sizing       :xxxlarge
                                           :icon-filled? true 
                                           :colorway     :positive}
                                   :args  [:star]}}}}
  [& args]
  (when ^boolean js/goog.DEBUG (validate args))
  (let [{:keys [opts attrs children]}                              
        (extract args [:icon-filled? 
                       :icon-style]) ; <-don't need to do this if these keys are present in variants-by-custom-opt-key or kushi.ui.core/kushi-ui-props
        
        {:keys [icon-style
                icon-filled?
                weight
                sizing
                colorway
                ns
                inert?]}
        opts

        [icon*]
        children]

    [:div
     (merge-attrs
      (sx ".kushi-icon"
          :.transition
          :position--relative
          :d--inline-flex
          :flex-direction--row
          :jc--c
          :ta--center
          :ai--c
          ;; TODO - use tokenized syntax here
          [:>span:fs "var(--mui-icon-relative-font-size, inherit)"]
          [:>span.material-symbols-icon-filled:font-variation-settings "'FILL' 1"]

          ;; [:>span {:transition-property        :all
          ;;          :transition-timing-function :$transition-timing-function
          ;;          :transition-duration        :$transition-duration}]

          :_svg:height--1em
          :_svg>path:fill--currentColor)
      {
       :data-ks-ui       :icon
       :data-ks-inert    (when (true? inert?) "")
       :data-ks-ns       ns
       :data-ks-surface  :transparent
       :data-ks-sizing   sizing
       :data-ks-weight   weight
       :data-ks-colorway colorway}
      attrs)
     (cond
       (and (vector? icon*) (= :svg (first icon*)))
       icon*

       (every? #(or (string? %) (keyword? %)) children)
       (let [icon-name  (icon-name->snake-case-string children)
             icon-font  "material-symbols"
             style      (if (contains? #{:outlined :rounded :sharp} icon-style)
                          icon-style
                          :outlined)
             icon-style (str icon-font "-" (name style))
             icon-fill  (when icon-filled? (str icon-font "-icon-filled"))]


         (into [:span {:class [icon-style icon-fill]}]
               icon-name)))]))
#_{
          :sizing         {:default :medium
                          :desc    "Corresponds to the font-size based on Kushi's font-size scale."
                          :demo    {:label           "Sizes"
                                    :attrs           {}
                                    :variant-labels? false
                                    ;; :x-variants [:weight]
                                    :args            [:star]
                                    :row-style       {:width           "100%"
                                                      :justify-content "space-between"}}}

          :weight       {:default :normal
                          :desc    "Corresponds to the font-weight based on Kushi's font-weight scale."
                          :demo    {:label           "Weights"
                                    :attrs           {}
                                    :attrs/display   {:sizing :xxxlarge}
                                    :variant-labels? false
                                    :args            [:star]
                                    :row-style       {:width           "100%"
                                                      :justify-content "space-between"}}}

          :colorway     {:default nil
                          :desc    "Colorway of the icon. Can also be a named color from Kushi's design system, e.g `:red`, `:purple`, `:gold`, etc."
                          :demo    [{:label           "Colorways"
                                     :attrs           {:sizing :xxxlarge}
                                     :variant-labels? false
                                     :args            [:star]}
                                    {:label           "Colorways, filled icon"
                                     :attrs           {:icon-filled? true
                                                       :sizing         :xxxlarge}
                                     :variant-labels? false
                                     :args            [:star]}]}

          :icon-filled? {:schema    boolean?
                          ;; :required? true
                          :default false
                          :desc    "Filled or not filled"
                          :demo    {:label         "Filled icon"
                                    :attrs         {}
                                    :attrs/display {:sizing :xxxlarge}
                                    :args          [:star]}}

          :icon-style   {:schema    (into #{} defs/icon-style)
                          :default :outlined
                          :desc    "Style of icon"
                          :demo    {:label         "Icon styles"
                                    :attrs         {:sizing :xxxlarge}
                                    :args          [:login]}}

          :inert?       {:schema    boolean?
                          :default false
                          :desc    "Determines whether the icon will feature hover and active styles"
                          :demo    {:label         "Inert or interactive styling"
                                    :attrs         {:sizing :xxxlarge
                                                    :icon-filled? true 
                                                    :colorway :positive}
                                    :args          [:star]}}
          
          }
