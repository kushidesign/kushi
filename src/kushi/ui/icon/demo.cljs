(ns ^{:kushi/layer "user-styles"} kushi.ui.icon.demo
  (:require
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.ui.icon :refer [icon]]
   ;; TODO should this be from showcase?
   [kushi.showcase.core
    :as showcase
    :refer [samples samples-with-variant samples-with-template]]
   [kushi.ui.tooltip :refer [tooltip-attrs]]
   [kushi.core :refer (sx merge-attrs)]
   [kushi.playground.component-examples :as component-examples]
   [kushi.playground.util :refer-macros [sx-call]]))


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


'{:label   "Colorways"
  ;;  :label/modal "Colorways ..."
 :desc    "Oh yeah"
  ;;  :row-style {}
 :samples (samples-from-variant
           {:variant         :sizing
            :variant-labels? false
            :variant-scale   :colorway/named
            :attrs           {:sizing :xxxlarge}
            :args            [:star]})}

'{:label   "Colorways"
  ;;  :label/modal "Colorways ..."
 :desc    "Oh yeah"
  ;;  :row-style {}
 :samples (samples-from-template 
           {:template-fn   myns/myfn            ; <- overrides :template? 
            :attrs         {:sizing :xxxlarge}
            :attrs/display {:sizing :xxxlarge
                            :weight :light}
            :args          [[:auto-awesome]
                            [:help]
                            [:info]
                            [:favorite]
                            [:settings]
                            [:filter-alt]
                            [:cloud-upload]
                            [:download]
                            '...]
            })}

;; for legacy
'{:label     "Semantic colorways"
  :desc      "Examples of semantic coloring of icons"
  :row-style {}
  :samples   (samples ["accent"
                       [icon {:colorway :accent
                              :sizing   :xxxlarge} :star]

                       "negative"
                       [icon {:colorway :negative
                              :sizing   :xxxlarge} :cancel]

                       "positive"
                       [icon {:colorway :positive
                              :sizing   :xxxlarge} :check-circle]

                       "warning"
                       [icon {:colorway :warning
                              :sizing   :xxxlarge} :warning]])}



(def demos
  [
   {:label   "Sizing"
    ;;  :label/modal "Colorways ..."
    :desc    "Sizes from xxxsmall to xxxlarge"
    ;; :row-style {:border "1px solid red"}
    :samples (samples-with-variant
              {:variant         :sizing
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
               :attrs           {:sizing :xxxlarge}
               :args            [:star]})}

    {:label   "Colorways, filled icon"
     :samples (samples-with-variant
               {:variant         :colorway
                :variant-labels? false
                :variant-scale   :colorway/named
                :attrs           {:icon-filled? true
                                  :sizing       :xxxlarge}
                :args            [:star]})}

   #_{:opt  :sizing
    :demo {:label           "Sizes"
           :attrs           {}
           :variant-labels? false
           ;; :x-variants [:weight]
           :args            [:star]
           :row-style       {:width           "100%"
                             :justify-content "space-between"}}}
   
   #_{:opt     :colorway 
    :demo    {:label           "Colorways"
               :attrs           {:sizing :xxxlarge}
               :args            [:star]
               :variant-labels? false
               :variant-scale   :colorway/named}

    #_[{:label           "Colorways"
               :attrs           {:sizing :xxxlarge}
               :args            [:star]
               :variant-labels? false
               :variant-scale   :colorway/named}
              {:label           "Colorways, filled icon"
               :attrs           {:icon-filled? true
                                 :sizing       :xxxlarge}
               :args            [:star]
               :variant-labels? false
               :variant-scale   :colorway/named
               }]}


   #_{:label   "Colorways"
    ;;  :label/modal "Colorways ..."
    :desc    "Oh yeah"
    ;;  :row-style {}
    :samples (samples-from-variant
              {:variant         :sizing
               :variant-labels? false
               :variant-scale   :colorway/named
               :attrs           {:sizing :xxxlarge}
               :args            [:star]})}


   {:label   "Semantic colorways"
    :desc    "Examples of semantic coloring of icons"
    :samples (samples ["accent"
                       [icon {:colorway :accent
                              :sizing   :xxxlarge} :star]

                       "negative"
                       [icon {:colorway :negative
                              :sizing   :xxxlarge} :cancel]

                       "positive"
                       [icon {:colorway :positive
                              :sizing   :xxxlarge} :check-circle]

                       "warning"
                       [icon {:colorway :warning
                              :sizing   :xxxlarge} :warning]])}
   
   
   {:label   [:span "Various icons" [:span " (hover to view icon name)"]]
    ;;  :label/modal "Colorways ..."
    :desc    "Oh yeah"
    ;;  :row-style {}
    :samples (samples-with-template 
              {:template-fn   showcase/icons-with-tooltips ; <- overrides :template? 
               :row-style     {:gap "2rem"}
               :attrs         {:sizing :xxxlarge}
               :attrs/display {:sizing :xxxlarge
                               :weight :light}
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

    {:opt  :sizing
     :demo {:label           "Sizes"
            :attrs           {}
            :variant-labels? false
                                    ;; :x-variants [:weight]
            :args            [:star]
            :row-style       {:width           "100%"
                              :justify-content "space-between"}}}
    
    
    #_#_#_
    {:label   "Semantic colorways"
     :desc    "Examples of semantic coloring of icons"
     :require [[kushi.ui.icon :refer [icon]]]
     :samples ["accent"
               [icon {:colorway :accent
                      :sizing   :xxxlarge} :star]

               "negative"
               [icon {:colorway :negative
                      :sizing   :xxxlarge} :cancel]

               "positive"
               [icon {:colorway :positive
                      :sizing   :xxxlarge} :check-circle]

               "warning"
               [icon {:colorway :warning
                      :sizing   :xxxlarge} :warning]]}
    
    {:label   "Semantic colorways"
     :desc    "Examples of semantic coloring of icons"
     :require [[kushi.ui.icon :refer [icon]]]
     :samples [[icon {:colorway :accent
                      :sizing   :xxxlarge} :star]

               [icon {:colorway :negative
                      :sizing   :xxxlarge} :cancel]

               [icon {:colorway :positive
                      :sizing   :xxxlarge} :check-circle]

               [icon {:colorway :warning
                      :sizing   :xxxlarge} :warning]]}
    
    {:label         [:span "Various icons" [:span " (hover to view icon name)"]]
     :label/modal   "Various icons"
     :desc          "Examples of semantic coloring of icons"
     :require       '[[kushi.ui.icon :refer [icon]]]
     :row-style     {:flex-wrap :wrap
                     :gap       :2em}
     :attrs/display {:sizing :xxxlarge
                     :weight :light}
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
          
          :colorway {:default nil
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
                                :variant-scale   :colorway/named
                                }]}
          
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
                                   :args  [:star]}}
          
          }
