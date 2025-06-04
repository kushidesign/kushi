(ns kushi.ui.icon.core
  (:require
   [clojure.string :as string]
   [kushi.core :refer (sx merge-attrs)]
   [kushi.ui.core :refer (extract material-symbol-or-icon-span)]))

(defn icon-name->snake-case-string [coll]
  (mapv #(cond
           (= % :<>)
           :<>
           (or (string? %) (keyword? %))
           (-> % name (string/replace #"-" "_"))
           :else %)
        coll))

(defn icon
  {:summary "Icons provide compactness by visually indicating meaning using iconic symbols."
   :desc    "By default, icons in Kushi are pulled in via
             [Google's Material Symbols font for the web](https://developers.google.com/fonts/docs/material_symbols).

             Use
             [this page](https://fonts.google.com/icons?icon.set=Material+Symbols)
             to explore over 1000+ different icons.

             This component expects a child argument which is a string, keyword,
             or vector.

             If the argument is a string or keyword, it should correspond to the
             name of an existing mui icon.  By convention, it should be
             kebab-case (it is internally converted to snake-case to work with
             Google Material Symbols icon font).

             If the argument is a vector, it should be a hiccup representation
             of an svg icon. This must use `:viewBox` attribute with values such
             as `\"0 0 24 24\"`. The `:width` and `:height` attributes of the
             `svg` do not need to be set. For example, you can pass a Material
             Symbols icon in hiccupized `svg` to use in place of the Google
             Fonts Material Symbols font. This will be more performant, as you
             will avoid any Flash Of Unstyled Text that is a possibility when
             using modern icon fonts.

             There are small number of mui icon svgs in the
             `kushi.ui.icon.mui.svg` namespace which can be used.

             For example, to use the svg version of the (plus-symbol-shaped)
             \"Add\" icon you could require this namespace
             `[kushi.ui.icon.mui.svg :as mui.svg]` and then in your component
             code do `[icon mui.svg/add]`. You can also create and utilize
             similar namespace in your own project with your own collection of
             icon `svg`s."
   
  :opts
  '[
     size           
     {:pred    #{:xxxsmall
                 :xxsmall
                 :xsmall
                 :small
                 :medium
                 :large
                 :xlarge
                 :xxlarge
                 :xxxlarge}
      :default :medium
      :desc    "Corresponds to the font-size based on Kushi's font-size scale."
      :demo    {:label           "Sizes × Weights"
                :attrs           {}
                :x-variants      [weight]
                :args            [:star]
                :row-style       {:width "100%" :justify-content "space-between"}}}
   
     weight           
     {:pred    #{:thin
                 :extra-light
                 :light
                 :normal
                 :wee-bold
                 :semi-bold
                 :bold
                 :extra-bold
                 :heavy}
      :default :normal
      :desc    "Corresponds to the font-weight based on Kushi's font-weight scale."
      :demo    {:label           "Weights"
                :attrs           {}
                :attrs/display   {:-size :xxxlarge}
                :variant-labels? false
                :args            [:star]
                :row-style       {:width           "100%"
                                  :justify-content "space-between"}}}
   

     colorway       
     {:pred    #{:neutral :accent :positive :negative :warning}
      :default nil
      :desc    "Colorway of the spinner. Can also be a named color from Kushi's design system, e.g `:red`, `:purple`, `:gold`, etc."
      :demo    [{:label           "Colorways"
                 :attrs           {:-size :xxxlarge}
                 :variant-labels? false
                 :args            [:star]}
                {:label           "Colorways, filled icon"
                 :attrs           {:-icon-filled? :true :-size :xxxlarge}
                 :variant-labels? false
                 :args            [:star]}]}

     icon-filled?           
     {:pred    boolean?
      :default false
      :desc    "Filled or not filled"
      :demo    {:label           "Filled icon"
                :attrs           {}
                :attrs/display   {:-size :xxxlarge}
                :args            [:star]}}
   
     icon-style           
     {:pred    #{:rounded :outlined :sharp}
      :default :outlined
      :desc    "Style of icon"
      :demo    {:label           "Icon styles"
                :attrs/display   {:-size :xxxlarge}
                :args            [:login]}}]
   
   :demos   '[{:label   "Semantic colorways"
               :desc    "Examples of semantic coloring of icons"
               :require [[kushi.ui.icon.core :refer [icon]]]
               :samples [[icon {:-colorway :accent :-size :xxxlarge} :star]
                         [icon {:-colorway :negative :-size :xxxlarge} :cancel]
                         [icon {:-colorway :positive :-size :xxxlarge} :check-circle]
                         [icon {:-colorway :warning :-size :xxxlarge} :warning]
                         [icon {:-colorway :accent :-size :xxxlarge :-icon-filled? true} :star]
                         [icon {:-colorway :negative :-size :xxxlarge :-icon-filled? true} :cancel]
                         [icon {:-colorway :positive :-size :xxxlarge :-icon-filled? true} :check-circle]
                         [icon {:-colorway :warning :-size :xxxlarge :-icon-filled? true} :warning]]}
              
              {:label     "Various icons"
               :desc      "Examples of semantic coloring of icons"
               :require   [[kushi.ui.icon.core :refer [icon]]]
               :row-style {:flex-wrap :wrap :gap :1em}


              ;; TODO - implement this in showcase ns
              ;; :samples-fn kushi.playground.showcase.core/icon
              ;; :samples   [:auto-awesome :help :info :favorite]


               :samples   [[icon {:-size   :xlarge
                                  :-weight :light} :auto-awesome]
                           [icon {:-size   :xlarge
                                  :-weight :light} :help]
                           [icon {:-size   :xlarge
                                  :-weight :light} :info]
                           [icon {:-size   :xlarge
                                  :-weight :light} :favorite]
                           [icon {:-size   :xlarge
                                  :-weight :light} :settings]
                           [icon {:-size   :xlarge
                                  :-weight :light} :filter-alt]
                           [icon {:-size   :xlarge
                                  :-weight :light} :cloud-upload]
                           [icon {:-size   :xlarge
                                  :-weight :light} :download]
                           [icon {:-size   :xlarge
                                  :-weight :light} :delete]
                           [icon {:-size   :xlarge
                                  :-weight :light} :cancel]
                           [icon {:-size   :xlarge
                                  :-weight :light} :auto-awesome-motion]
                           [icon {:-size   :xlarge
                                  :-weight :light} :archive]
                           [icon {:-size   :xlarge
                                  :-weight :light} :sell]
                           [icon {:-size   :xlarge
                                  :-weight :light} :visibility]
                           [icon {:-size   :xlarge
                                  :-weight :light} :visibility-off]
                           [icon {:-size   :xlarge
                                  :-weight :light} :warning]
                           [icon {:-size   :xlarge
                                  :-weight :light} :check-circle]
                           [icon {:-size   :xlarge
                                  :-weight :light} :error]
                           [icon {:-size   :xlarge
                                  :-weight :light} :edit]
                           [icon {:-size   :xlarge
                                  :-weight :light} :folder]
                           [icon {:-size   :xlarge
                                  :-weight :light} :smartphone]
                           [icon {:-size   :xlarge
                                  :-weight :light} :add-circle]
                           [icon {:-size   :xlarge
                                  :-weight :light} :expand-circle-down]
                           [icon {:-size   :xlarge
                                  :-weight :light} :search]
                           [icon {:-size   :xlarge
                                  :-weight :light} :playlist-add]
                           [icon {:-size   :xlarge
                                  :-weight :light} :expand]
                           [icon {:-size   :xlarge
                                  :-weight :light} :compress]
                           [icon {:-size   :xlarge
                                  :-weight :light} :arrow-back]
                           [icon {:-size   :xlarge
                                  :-weight :light} :arrow-forward]
                           [icon {:-size   :xlarge
                                  :-weight :light} :sort]
                           [icon {:-size   :xlarge
                                  :-weight :light} :clear]
                           ]}
              ]}
  
  

  [& args]
  (let [{:keys [opts attrs children]}                          (extract args icon)

        {:keys [icon-style
                icon-filled?
                weight
                size
                colorway]} opts

        [icon*]                                                children]
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
      {:data-kushi-ui       :icon
       :data-kushi-size     size
       :data-kushi-weight   weight
       :data-kushi-colorway colorway}
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
               icon-name)
         ))]))

