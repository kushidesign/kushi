(ns ^{:kushi/layer "user-styles"} kushi.ui.icon.demo
  (:require
   [kushi.ui.icon :refer [icon]]
   [kushi.showcase.core
    :as showcase
    :refer [samples samples-with-variant samples-with-template]]))


(def icons-without-filled-variants
  ["search"
   "playlist-add"
   "expand"
   "compress"
   "arrow-back"
   "arrow-forward"
   "sort"
   "clear"
   "keyboard-return"
   "check"
   "find-replace"
   "open-in-new"
   "fingerprint"
   "refresh"
   "download"
   "menu"])

(def icons-with-filled-variants
  ["auto-awesome"
   "help"
   "info"
   "favorite"
   "settings"
   "filter-alt"
   "cloud-upload"
   "download"
   "delete"
   "cancel"
   "auto-awesome-motion"
   "archive"
   "sell"
   "visibility"
   "visibility-off"
   "report-problem"
   "check-circle"
   "error"
   "edit"
   "folder"
   "smartphone"
   "star"
   "add-circle"
   "expand-circle-down"])




(def demos
  [
   {:label   "Size"
    ;;  :label/modal "Colorways ..."
    :desc    "Text sizes from xxxsmall to xxxlarge"
    ;; :row-style {:border "1px solid red"}
    :samples (samples-with-variant
              {:variant         :text-size
               :variant-labels? false
              ;;  :variant-scale   :colorway/named
               :attrs           {}
               :args            [:star]})}


   {:label   "Colorways"
    ;;  :label/modal "Colorways ..."
    :desc    "Oh yeah"
    ;;  :row-style {}
    :samples (samples-with-variant
              {:variant         :colorway
               :variant-labels? false
               :variant-scale   :colorway/named
               :attrs           {:text-size :xxxlarge}
               :args            [:star]})}

    {:label   "Colorways, filled icon"
     :samples (samples-with-variant
               {:variant         :colorway
                :variant-labels? false
                :variant-scale   :colorway/named
                :attrs           {:icon-filled true
                                  :text-size       :xxxlarge}
                :args            [:star]})}

   #_{:opt  :text-size
    :demo {:label           "Text sizes"
           :attrs           {}
           :variant-labels? false
           ;; :x-variants [:text-weight]
           :args            [:star]
           :row-style       {:width           "100%"
                             :justify-content "space-between"}}}
   
   #_{:opt     :colorway 
    :demo    {:label           "Colorways"
               :attrs           {:text-size :xxxlarge}
               :args            [:star]
               :variant-labels? false
               :variant-scale   :colorway/named}

    #_[{:label           "Colorways"
               :attrs           {:text-size :xxxlarge}
               :args            [:star]
               :variant-labels? false
               :variant-scale   :colorway/named}
              {:label           "Colorways, filled icon"
               :attrs           {:icon-filled true
                                 :text-size       :xxxlarge}
               :args            [:star]
               :variant-labels? false
               :variant-scale   :colorway/named
               }]}


   #_{:label   "Colorways"
    ;;  :label/modal "Colorways ..."
    :desc    "Oh yeah"
    ;;  :row-style {}
    :samples (samples-from-variant
              {:variant         :text-size
               :variant-labels? false
               :variant-scale   :colorway/named
               :attrs           {:text-size :xxxlarge}
               :args            [:star]})}


   {:label   "Semantic colorways"
    :desc    "Examples of semantic coloring of icons"
    :samples (samples ["accent"
                       [icon {:colorway :accent
                              :text-size   :xxxlarge} :star]

                       "negative"
                       [icon {:colorway :negative
                              :text-size   :xxxlarge} :cancel]

                       "positive"
                       [icon {:colorway :positive
                              :text-size   :xxxlarge} :check-circle]

                       "warning"
                       [icon {:colorway :warning
                              :text-size   :xxxlarge} :warning]])}
   
   {:label   "Semantic colorways, solid surface, round shape"
    :desc    "Examples of semantic coloring of icons"
    :samples (samples ["accent"
                       [icon {:colorway :accent
                              :text-size   :xxxlarge
                              :icon-filled true
                              ;; :surface  :solid
                              :shape  :circle}
                        :star]

                       "negative"
                       [icon {:colorway    :negative
                              :text-size      :xxxlarge
                              :icon-filled true
                              :shape     :circle}
                        :cancel]

                       "positive"
                       [icon {:colorway :positive
                              :text-size   :xxxlarge
                              :icon-filled true
                              ;; :surface  :solid
                              :shape  :circle} 
                        :check-circle]

                       "warning"
                       [icon {:colorway :warning
                              :text-size   :xxxlarge
                              :icon-filled true
                              ;; :surface  :solid
                              :shape  :circle}
                        :warning]])}

   {:label   [:span "Various icons" [:span " (hover to view icon name)"]]
    ;;  :label/modal "Colorways ..."
    :desc    "Oh yeah"
    ;;  :row-style {}
    :samples (samples-with-template 
              {:template-fn   showcase/icons-with-tooltips ; <- overrides :template? 
               :row-style     {:gap "2rem"}
               :attrs         {:text-size :xxxlarge}
               :attrs/display {:text-size :xxxlarge
                               :text-weight :light}
               :args          [[:auto-awesome]
                               [:help]
                               [:info]
                               [:favorite]
                               [:settings]
                               [:filter-alt]
                               [:cloud-upload]
                               [:download]] })}

   ]

  ;; do a ui-demo macro call per thing?
  #_(ui-demo
   
   [

    {:opt  :text-size
     :demo {:label           "Text sizes"
            :attrs           {}
            :variant-labels? false
                                    ;; :x-variants [:text-weight]
            :args            [:star]
            :row-style       {:width           "100%"
                              :justify-content "space-between"}}}
    
    
    #_#_#_
    {:label   "Semantic colorways"
     :desc    "Examples of semantic coloring of icons"
     :require [[kushi.ui.icon :refer [icon]]]
     :samples ["accent"
               [icon {:colorway :accent
                      :text-size   :xxxlarge} :star]

               "negative"
               [icon {:colorway :negative
                      :text-size   :xxxlarge} :cancel]

               "positive"
               [icon {:colorway :positive
                      :text-size   :xxxlarge} :check-circle]

               "warning"
               [icon {:colorway :warning
                      :text-size   :xxxlarge} :warning]]}
    
    {:label   "Semantic colorways"
     :desc    "Examples of semantic coloring of icons"
     :require [[kushi.ui.icon :refer [icon]]]
     :samples [[icon {:colorway :accent
                      :text-size   :xxxlarge} :star]

               [icon {:colorway :negative
                      :text-size   :xxxlarge} :cancel]

               [icon {:colorway :positive
                      :text-size   :xxxlarge} :check-circle]

               [icon {:colorway :warning
                      :text-size   :xxxlarge} :warning]]}
    
    {:label         [:span "Various icons" [:span " (hover to view icon name)"]]
     :label/modal   "Various icons"
     :desc          "Examples of semantic coloring of icons"
     :require       '[[kushi.ui.icon :refer [icon]]]
     :row-style     {:flex-wrap :wrap
                     :gap       :2em}
     :attrs/display {:text-size :xxxlarge
                     :text-weight :light}
     :render-as     :icons-with-tooltips
     :samples       [:auto-awesome
                     :help
                     :info
                     :favorite
                     :settings
                     :filter-alt
                     :cloud-upload
                     :download
                     :delete
                     :cancel
                     :auto-awesome-motion
                     :archive
                     :sell
                     :visibility
                     :visibility-off
                     :warning
                     :check-circle
                     :error 
                     :edit
                     :folder
                     :smartphone
                     :add-circle
                     :expand-circle-down
                     :search
                     :playlist-add
                     :expand
                     :compress
                     :arrow-back
                     :arrow-forward 
                     :sort
                     :keyboard-return
                     :check
                     :find-replace
                     :open-in-new
                     :fingerprint
                     :refresh
                     :download
                     :menu]}]))

#_{
          :text-size         {:default :medium
                          :desc    "Corresponds to the font-size based on Kushi's font-size scale."
                          :demo    {:label           "Text sizes"
                                    :attrs           {}
                                    :variant-labels? false
                                    ;; :x-variants [:text-weight]
                                    :args            [:star]
                                    :row-style       {:width           "100%"
                                                      :justify-content "space-between"}}}
          
          :text-weight       {:default :normal
                          :desc    "Corresponds to the font-weight based on Kushi's font-weight scale."
                          :demo    {:label           "Weights"
                                    :attrs           {}
                                    :attrs/display   {:text-size :xxxlarge}
                                    :variant-labels? false
                                    :args            [:star]
                                    :row-style       {:width           "100%"
                                                      :justify-content "space-between"}}}
          
          :colorway {:default nil
                     :desc    "Colorway of the icon. Can also be a named color from Kushi's design system, e.g `:red`, `:purple`, `:gold`, etc."
                     :demo    [{:label           "Colorways"
                                  :attrs           {:text-size :xxxlarge}
                                  :args            [:star]
                                  :variant-labels? false
                                  :variant-scale   :colorway/named}
                               {:label           "Colorways, filled icon"
                                :attrs           {:icon-filled true
                                                  :text-size       :xxxlarge}
                                :args            [:star]
                                :variant-labels? false
                                :variant-scale   :colorway/named
                                }]}
          
          :icon-filled {:schema  boolean?
                         ;; :required? true
                         :default false
                         :desc    "Filled or not filled"
                         :demo    {:label         "Filled icon"
                                   :attrs         {}
                                   :attrs/display {:text-size :xxxlarge}
                                   :args          [:star]}}
          
          :icon-style   {:schema  (into #{} defs/icon-style)
                         :default :outlined
                         :desc    "Style of icon"
                         :demo    {:label "Icon styles"
                                   :attrs {:text-size :xxxlarge}
                                   :args  [:login]}}
          
          :inert       {:schema  boolean?
                         :default false
                         :desc    "Determines whether the icon will feature hover and active styles"
                         :demo    {:label "Inert or interactive styling"
                                   :attrs {:text-size       :xxxlarge
                                           :icon-filled? true 
                                           :colorway     :positive}
                                   :args  [:star]}}
          
          }
