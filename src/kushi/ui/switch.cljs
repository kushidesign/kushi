;; (ns kushi.ui.switch
;;   (:require
;;    [kushi.core :refer (sx defcss merge-attrs)]
;;    [domo.core :as domo]
;;    [kushi.ui.util :as util]
;;    [kushi.ui.core :refer (extract)]
;;    ))

;; (defcss "@layer kushi-ui-styles .kushi-switch-track-content"
;;   :.flex-row-c
;;   :.semi-bold
;;   :.transition
;;   :.xxfast!
;;   :>*:fs--0.40em
;;   :c--white
;;   [:w "calc(100% - 50% + var(--switch-border-width))"]
;;   )

;; (defcss "@layer kushi-ui-styles .kushi-switch-thumb-content"
;;   :c--$neutral-foreground
;;   :d--none 
;;   :w--100%
;;   :h--100% 
;;   :.flex-col-c 
;;   :ai--c)

;; (defn- toggle-switch [%]
;;   (let [node* (domo/et %)
;;         node  (if (domo/has-class? node* "kushi-switch")
;;                 node*
;;                 (domo/nearest-ancestor node* ".kushi-switch"))]
;;     (domo/toggle-boolean-attribute node "aria-checked")))

;; (defn- track-content [opts x]
;;   (when x
;;     [:div opts (if (string? x) [:span x] x)]))

;; (defn switch
;;   {:summary "Switches are used to toggle an individual option on or off."
;;    :desc "Switches can be custom styled via a variety of tokens in your theme.
          
;;           `--switch-width-ratio`
;;           Setting this to 1.5 will result in a switch that has the aspect ratio
;;           of 1.5:1 (width:height). The default value is 2.
          
;;           `--switch-border-width`
;;           The default value is `2px`. If customizing the value, it is
;;           recommended to use a `px` or `rem` value, especially if you are using
;;           the `:track-label-on` or `:track-label-off` options.
          
;;           `--switch-border-color`
;;           The default value is `transparent`, which will read as \"padding\"
;;           between the switch \"thumb\" and the switch \"track\", as by default
;;           the switch will have a solid background color in both the on and off
;;           states. You can supply a color value which will result in more of an
;;           \"outlined\" styling.
          
;;           `--switch-thumb-scale-factor`
;;           The default value is `1`. Setting this to a value greater than 1 will
;;           result in the thumb height being greater than the track height."
          
          
;;    :opts '[{:name    on?
;;             :schema    boolean?
;;             :default false
;;             :desc    "Use to control the initial on/off state of the switch"}
;;            {:name    disable-events?
;;             :schema    boolean?
;;             :default false
;;             :desc    "Set this to true if you would like to control the state of
;;                       the switch in a reactive manner via the `:on?` option"}
;;            {:name    thumb-attrs
;;             :schema    map?
;;             :default nil
;;             :desc    "HTML attributes map applied to the inner element, commonly
;;                       refered to as the \"handle\" or \"thumb\" of the switch."}
;;            {:name    thumb-label-off
;;             :schema    #{string? vector?}
;;             :default nil
;;             :desc    "String or element that will be placed in center of thumb,
;;                       when in the \"off\" position"}
;;            {:name    thumb-label-on
;;             :schema    #{string? vector?}
;;             :default nil
;;             :desc    "String or element that will be placed in center of thumb,
;;                       when in the \"on\" position"}
;;            {:name    track-label-off
;;             :schema    #{string? vector?}
;;             :default nil
;;             :desc    "String or element that will be placed in the track, when
;;                       in the \"off\" position"}
;;            {:name    track-label-on
;;             :schema    #{string? vector?}
;;             :default nil
;;             :desc    "String or element that will be placed in the track, when
;;                       in the \"on\" position"}
           
;;            {:name    colorway
;;             :schema    #{:neutral :accent :positive :negative :warning}
;;             :default nil
;;             :desc    "Colorway of the switch. Can also be a named color from
;;                       Kushi's design system, e.g `:red`, `:purple`, `:gold`,
;;                       etc."}
;;            ]}
;;   [& args]
;;   (let [[opts attrs & _]
;;         (extract args)

;;         {:keys [disable-events?
;;                 on?
;;                 colorway
;;                 thumb-attrs
;;                 thumb-label-off
;;                 thumb-label-on
;;                 track-label-on
;;                 track-label-off]}
;;         opts


;;         disabled?                  
;;         (util/html-attr? opts :disabled)
;;         ]
;;     [:button
;;      (merge-attrs
;;       (sx
;;        ".kushi-switch"
;;        {:--thumb-height "calc(var(--switch-thumb-scale-factor, 1) * (1em - (var(--switch-border-width) * 2)))"
;;         :--height       :1em}
;;        :.pill
;;        :.flex-row-fs
;;        :.no-shrink
;;        :.transition
;;        :transition-duration--$xxfast
;;        :position--relative
;;        :cursor--pointer

;;        ["_.kushi-switch-thumb-label-off:display" :flex]
;;        ["_.kushi-switch-thumb-label-on:display" :none]
;;        [".kushi-switch[aria-checked='true']_.kushi-switch-thumb-label-on:display" :flex]
;;        [".kushi-switch[aria-checked='true']_.kushi-switch-thumb-label-off:display" :none]

;;        [".kushi-switch_.kushi-switch-track-label-on:opacity" 0]
;;        [".kushi-switch[aria-checked='true']_.kushi-switch-track-label-off:opacity" 0]
;;        [".kushi-switch[aria-checked='true']_.kushi-switch-track-label-on:opacity" 1]

;;        [".kushi-switch[aria-checked='true']:jc" :fe]
;;        [:w "calc((var(--height) *  max(1.25, var(--switch-width-ratio, 2))) - var(--switch-border-width))"]
;;        :h--$height
;;        :bc--$switch-border-color
;;        :bw--$switch-border-width
;;        :bs--solid

;;        [".kushi-switch[aria-checked='false']:bgc" :$switch-off-background-color]
;;        [".kushi-switch[aria-checked='false']:hover:bgc" :$switch-off-background-color-hover]
       
;;        )

;;       {:disabled         disabled?
;;        :role             :switch
;;        :aria-checked     (if on? true false)
;;        :data-ks-ia       ""
;;        :data-ks-colorway colorway
;;        :data-ks-surface  "solid"}
      
;;       (domo/mouse-down-a11y #(when-not disable-events? (toggle-switch %)))

;;       attrs)

;;      [track-content
;;       (sx ".kushi-switch-track-label-on"
;;           :.absolute-inline-start-inside
;;           :.kushi-switch-track-content)
;;       track-label-on]

;;      [track-content
;;       (sx ".kushi-switch-track-label-off"
;;           :.absolute-inline-end-inside
;;           :.kushi-switch-track-content)
;;       track-label-off]

;;      [:div
;;       (merge-attrs
;;        {:data-ks-contour  :pill
;;         :data-ks-colorway colorway
;;         }
;;        (sx
;;         ".kushi-switch-thumb"
;;         :.transition
;;         [:--width :$thumb-height]
;;         :transition-duration--$xxfast
;;         :border-color--currentColor
;;         ["has-ancestor(.kushi-switch[aria-checked='false']):border-color" "color-mix(in srgb, currentColor, transparent)"]
;;         :cursor--pointer
;;         :bgc--$transparent-white-100
;;         :box-shadow--0:2px:6px:0:$transparent-black-15
;;         [:transform "translate(0, -50%)"]
;;         ["has-ancestor(.kushi-switch[aria-checked='true']):inset-inline-start"
;;          "calc(100% - var(--width))"]
;;         ["has-ancestor(.kushi-switch[disabled]):cursor"
;;          :not-allowed]
;;         :position--absolute
;;         :top--50%
;;         :inset-inline-start--0
;;         :h--$thumb-height
;;         :w--$width)
;;        thumb-attrs)
;;       [:div (sx ".kushi-switch-thumb-label-on"
;;                 :.kushi-switch-thumb-content
;;                 ["has-ancestor(.kushi-switch[disabled]):cursor" :not-allowed])
;;        thumb-label-on]
;;       [:div (sx ".kushi-switch-thumb-label-off"
;;                 :.kushi-switch-thumb-content
;;                 ["has-ancestor(.kushi-switch[disabled]):cursor" :not-allowed])
;;        thumb-label-off]
;;       ]]))


(ns kushi.ui.switch
  (:require
   [clojure.string :as string]
   [fireworks.core :refer [? !? ?> !?>]]
   [kushi.core :refer (sx defcss merge-attrs)]
   [domo.core :as domo]
   [kushi.ui.variants :refer [variants-by-custom-opt-key]]
   [kushi.ui.icon :refer (icon)]
   [kushi.ui.util :as util]
   [kushi.ui.thumb :refer (thumb)]
   [kushi.ui.core :refer (defui)]))

#_(defcss "@layer kushi-ui-styles .kushi-switch-track-content"
  :.flex-row-center
  :.semi-bold
  :.transition
  :.xxfast!
  :>*:fs--0.35em
  :c--white
  :dark:c--black
  :.kushi-switch-track-label-off:c--$foreground-color-neutral
  :dark:.kushi-switch-track-label-off:c--$foreground-color-neutral-dark-mode
  [:w "calc(100% - 50% + var(--switch-border-width))"]
  )

#_(defcss "@layer kushi-ui-styles .kushi-switch-thumb-content"
  :c--$neutral-foreground
  :d--none 
  :w--100%
  :h--100% 
  :.flex-col-c 
  :ai--c)

(defn- toggle-switch [%]
  (let [node* (domo/et %)
        node  (if (domo/has-class? node* "kushi-switch")
                node*
                (domo/nearest-ancestor node* ".kushi-switch"))]
    (domo/toggle-boolean-attribute node "aria-checked")))


(defui switch
  {:summary "Switches are used to toggle an individual option on or off."
   :desc "Switches can be custom styled via a variety of tokens in your theme.
          
          `--switch-width-ratio`
          Setting this to 1.5 will result in a switch that has the aspect ratio
          of 1.5:1 (width:height). The default value is 2.
          
          `--switch-border-width`
          The default value is `2px`. If customizing the value, it is
          recommended to use a `px` or `rem` value, especially if you are using
          the `:track-label-on` or `:track-label-off` options.
          
          `--switch-border-color`
          The default value is `transparent`, which will read as \"padding\"
          between the switch \"thumb\" and the switch \"track\", as by default
          the switch will have a solid background color in both the on and off
          states. You can supply a color value which will result in more of an
          \"outlined\" styling.
          
          `--switch-thumb-scale-factor`
          The default value is `1`. Setting this to a value greater than 1 will
          result in the thumb height being greater than the track height."
          
          
   :props/shared [:colorway
                  :sizing
                  :weight
                  [:contour {:default :pill}]]
   :props {:on?                {:schema   :boolean
                                :default  false
                                :desc     "Control the initial on/off state of the switch"
                                :data-ks? false}
           :disable-events?    {:schema  :boolean
                                :default false
                                :desc    "Set this to true if you would like to control the state of the switch in a reactive manner via the `:on?` option"}
           :thumb-props        {:schema  :map 
                                :default nil
                                :desc    "Map of props + HTML attributes applied to the thumb of the switch. See `kushi.ui.thumb` component docs."}
           :thumb-icon-props   {:schema  :map 
                                :default nil
                                :desc    "Map of props + HTML attributes applied to the optional icon on the thumb of the switch. See `kushi.ui.icon` component docs."}
           :thumb-label-off    {:schema   :string
                                :default  nil
                                :desc     "String that will be placed in center of thumb, when in the \"off\" position"
                                :data-ks? false}
           :thumb-label-on     {:schema   :string
                                :default  nil
                                :desc     "String that will be placed in center of thumb, when in the \"on\" position"
                                :data-ks? false}
           :thumb-icon-off     {:schema   :keyword
                                :default  nil
                                :desc     "Name of icon that will be placed in center of thumb, when in the \"off\" position"
                                :data-ks? false}
           :thumb-icon-on      {:schema   :keyword
                                :default  nil
                                :desc     "Name of icon that will be placed in center of thumb, when in the \"on\" position"
                                :data-ks? false}
           ;; track-inset-gap cannot be negative e.g. :-1px or "-1px"
           :track-inset-gap    {:schema   [:or :string :keyword]
                                :default  "1px"
                                :desc     "Size of the gap between the outer edge of thumb and the inner edge of the track."
                                :data-ks? false}
           :thumb-scale-factor {:schema   [:or :string :float]
                                :default  nil
                                :desc     "If set to 1, the thumb height will be 100% the height of the switch track, minus the value of the track-inset-gap"
                                :data-ks? false}
           :switch-width-ratio {:schema  :float
                                :default nil
                                :desc    "Width ratio of switch"}

          ;;  Track content, leave off for now, consider removing
          ;;  :track-label-off    {:schema  :string
          ;;                       :default nil
          ;;                       :desc    "String that will be placed in the track, when in the \"off\" position"}
          ;;  :track-label-on     {:schema  :string
          ;;                       :default nil
          ;;                       :desc    "String that will be placed in the track, when in the \"on\" position"}
          ;;  :track-icon-off     {:schema   :keyword
          ;;                       :default  nil
          ;;                       :desc     "Name of icon that will be placed in the track, when in the \"off\" position"
          ;;                       :data-ks? false}
          ;;  :track-icon-on      {:schema   :keyword
          ;;                       :default  nil
          ;;                       :desc     "Name of icon that will be placed in track, when in the \"on\" position"
          ;;                       :data-ks? false}
          ;;  :track-icon-props   {:schema  :map 
          ;;                       :default nil
          ;;                       :desc    "Map of props + HTML attributes applied to the optional icon in the track of the switch. See `kushi.ui.icon` component docs."}
           }}
  [& args]
  (let [{:keys [disable-events?
                on?
                colorway
                contour
                thumb-props
                thumb-label-off
                thumb-label-on
                thumb-icon-off
                thumb-icon-on
                thumb-icon-props
                thumb-scale-factor
                track-inset-gap
                switch-width-ratio
                ;; Track content, leave off for now, consider removing
                ;; track-label-on
                ;; track-label-off
                ;; track-icon-on
                ;; track-icon-off
                ;; track-icon-props
                ]
         :or   {contour :pill}}
        (!? &props)

        rounded-contour?
        (!? 'rounded-contour?
            (contains? (:contour/rounded+rounded-absolute variants-by-custom-opt-key)
                       contour))

        relative-rounded-contour?
        (!? 'relative-rounded-contour? 
            (contains? (:contour/rounded variants-by-custom-opt-key) contour))

        contour
        (if relative-rounded-contour?
          (!? (-> contour name (str "-absolute") keyword))
          contour)

        track-inset-gap
        (string/replace (util/as-str track-inset-gap) #"^\-" "")

        disabled?                  
        (util/html-attr? &attrs :disabled)
        
        ;; Track content, leave off for now, consider removing
        ;; track-icon-provided? 
        ;; (boolean (and track-icon-on track-icon-off))
        
        ;; track-label-provided? 
        ;; (boolean (and track-label-on track-label-off)) 
        
        ;; icon-in-track? 
        ;; track-icon-provided?
        
        ;; label-in-track? 
        ;; (and (not icon-in-track?) track-label-provided?)
        
        thumb-icon-provided? 
        (boolean (and thumb-icon-on thumb-icon-off))

        thumb-label-provided? 
        (boolean (and thumb-label-on thumb-label-off)) 

        icon-on-thumb? 
        thumb-icon-provided?

        label-on-thumb? 
        (and (not icon-on-thumb?) thumb-label-provided?)]

    [:button
     (merge-attrs
      (if (pos? thumb-scale-factor)
        {:style {"--thumb-scale-factor" thumb-scale-factor
                 "--track-inset-gap"   :0px
                 "overflow"             :visible}}
        {:style {"--thumb-scale-factor" 1}})

      (when track-inset-gap {:style {"--track-inset-gap" track-inset-gap}})

      (when switch-width-ratio {:style {"--switch-width-ratio" switch-width-ratio}})

      (sx
       ".kushi-switch"
       {:--thumb-height                        "calc(var(--thumb-scale-factor, var(--switch-thumb-scale-factor, 1)) * (1em - (var(--track-inset-gap, 1px) * 2)))"
        :--height                              :1em
        :--switch-track-inset-box-shadow-color :$transparent-black-05
        :flex-shrink                           0
        :overflow                              :clip
        :transition-duration                   :$xxfast
        :position                              :relative
        :cursor                                :pointer
        :width                                 "calc((var(--height) *  max(1.25, var(--switch-width-ratio, 1.8))) - var(--switch-border-width))"
        :height                                :$height
        :padding                               :$track-inset-gap||1px
        :box-shadow                            "inset 0 0 0.15em 0.01em var(--switch-track-inset-box-shadow-color)"
        :_.ks-thumb                            {:width                                                               :$thumb-height
                                                :height                                                              :$thumb-height
                                                "has-ancestor(.kushi-switch[disabled]):cursor"                         :not-allowed
                                                ;; "has-ancestor(.kushi-switch[aria-checked='false']):inset-inline-start" "0px" 
                                                ;; "has-ancestor(.kushi-switch[aria-checked='true']):inset-inline-start"  "calc(100% - var(--thumb-height))"
                                                }
        "[aria-checked='true']"                  {:_.ks-thumb:inset-inline-start          "calc(100% - var(--thumb-height) - var(--track-inset-gap) + (((var(--thumb-height) * var(--thumb-scale-factor, 1)) - var(--thumb-height)) / 2))"
                                                  :justify-content                        :flex-end
                                                  :_.kushi-switch-thumb-icon-on:display   :flex
                                                  :_.kushi-switch-thumb-icon-off:display  :none
                                                  :_.kushi-switch-thumb-label-on:display  :flex
                                                  :_.kushi-switch-thumb-label-off:display :none

                                                  ;; Track content, leave off for now, consider removing
                                                  ;; :_.kushi-switch-track-icon-off:opacity  0
                                                  ;; :_.kushi-switch-track-icon-on:opacity   1
                                                  ;; :_.kushi-switch-track-label-off:opacity 0
                                                  ;; :_.kushi-switch-track-label-on:opacity  1
                                                  }
        "[aria-checked='false']"                 {:_.ks-thumb:inset-inline-start          "calc(var(--track-inset-gap) - (((var(--thumb-height) * var(--thumb-scale-factor, 1)) - var(--thumb-height)) / 2))"
                                                  :_.kushi-switch-thumb-icon-on:display   :none
                                                  :_.kushi-switch-thumb-icon-off:display  :flex
                                                  :_.kushi-switch-thumb-label-on:display  :none
                                                  :_.kushi-switch-thumb-label-off:display :flex

                                                  ;; Track content, leave off for now, consider removing
                                                  ;; :_.kushi-switch-track-icon-off:opacity  1
                                                  ;; :_.kushi-switch-track-icon-on:opacity   0
                                                  ;; :_.kushi-switch-track-label-off:opacity 1
                                                  ;; :_.kushi-switch-track-label-on:opacity  0
                                                  ;; :_.kushi-switch-thumb-content:c         :$foreground-color-secondary
                                                  :dark:_.kushi-switch-thumb-content:c    :$foreground-color-secondary
                                                  :bgc                                    :$switch-off-background-color
                                                  :hover:bgc                              :$switch-off-background-color-hover
                                                  "[data-ks-inert]:bgc"                     :$switch-off-background-color
                                                  "[data-ks-inert]:hover:bgc"               :$switch-off-background-color-hover
                                                  :dark:bgc                               :$background-color-neutral-soft-3-dark-mode
                                                  :dark:hover:bgc                         :$background-color-neutral-soft-4-dark-mode
                                                  }

        :_.kushi-switch-thumb-content          {:display                                     :none
                                                :flex-direction                              :column
                                                :jc                                          :center
                                                :ai                                          :center
                                                :w                                           :100%
                                                :h                                           :100%
                                                :.kushi-switch-thumb-label                   {:fs :$switch-thumb-label-font-size||0.3em
                                                                                              :fw :$switch-thumb-label-font-weight||$semi-bold}
                                                :.kushi-switch-thumb-icon                    {:fs :$switch-thumb-icon-font-size||0.55em
                                                                                              :fw :$switch-thumb-icon-font-weight||$medium}}

        ;; Track content, leave off for now, consider removing
        ;; :_.kushi-switch-track-content          {:display                              :flex
        ;;                                         :flex-direction                       :row
        ;;                                         :jc                                   :center
        ;;                                         :fw                                   :$semi-bold
        ;;                                         ;;  :.transition
        ;;                                         ;;  :.xxfast!
        ;;                                         :c                                    :white
        ;;                                         :dark:c                               :black
        ;;                                         :.kushi-switch-track-label            {:fs :$switch-track-label-font-size||0.35em
        ;;                                                                                :fw :$switch-track-label-font-weight||$semi-bold}
        ;;                                         :.kushi-switch-track-icon             {:fs :$switch-track-icon-font-size||0.45em
        ;;                                                                                :fw :$switch-track-icon-font-weight||$medium}
        ;;                                         :.kushi-switch-track-label-off:c      :$foreground-color-neutral
        ;;                                         :dark:.kushi-switch-track-label-off:c :$foreground-color-neutral-dark-mode
        ;;                                         :w                                    "calc(100% - 50% + var(--switch-border-width))"}
        }

      ;;  [".kushi-switch[aria-checked='true']_.kushi-switch-thumb-icon-on:display" :flex]
      ;;  [".kushi-switch[aria-checked='true']_.kushi-switch-thumb-icon-off:display" :none]
       
      ;;  [".kushi-switch[aria-checked='false']_.kushi-switch-thumb-label-on:display" :none]
      ;;  [".kushi-switch[aria-checked='false']_.kushi-switch-thumb-label-off:display" :flex]
      ;;  [".kushi-switch[aria-checked='true']_.kushi-switch-thumb-label-on:display" :flex]
      ;;  [".kushi-switch[aria-checked='true']_.kushi-switch-thumb-label-off:display" :none]
       
      ;;  [".kushi-switch[aria-checked='false']_.kushi-switch-thumb-icon-on:display" :none]
      ;;  [".kushi-switch[aria-checked='false']_.kushi-switch-thumb-icon-off:display" :flex]
      ;;  [".kushi-switch[aria-checked='true']_.kushi-switch-thumb-icon-on:display" :flex]
      ;;  [".kushi-switch[aria-checked='true']_.kushi-switch-thumb-icon-off:display" :none]
       
      ;;  [".kushi-switch_.kushi-switch-track-label-on:opacity" 0]
      ;;  [".kushi-switch[aria-checked='true']_.kushi-switch-track-label-off:opacity" 0]
      ;;  [".kushi-switch[aria-checked='true']_.kushi-switch-track-label-on:opacity" 1]
      ;;  [".kushi-switch[aria-checked='false']_.kushi-switch-track-icon-off:opacity" 1]
      ;;  [".kushi-switch[aria-checked='false']_.kushi-switch-track-icon-on:opacity" 0]
       

       )

      {:disabled           disabled?
       :role               :switch
       :aria-checked       (if on? true false)
       :data-ks-ia         ""
       :data-ks-surface    "solid"
       :data-ks-display    "flex"
       :data-ks-fd         "row"
       :data-ks-jc         "flex-start"
       :data-ks-ai         "center"
       :data-ks-transition ""
       }

      (when disabled? {:data-ks-inert ""})
      
      (domo/mouse-down-a11y #(when-not disable-events? (toggle-switch %)))

      &attrs
      
      {:data-ks-contour (util/as-str contour)}
      )


     ;; Track content, leave off for now, consider removing
     #_(when icon-in-track? 
       (let [track-icon-props (merge {:icon-filled true} track-icon-props)]
         [:<>
          [:div {:class [:kushi-switch-track-content
                         :kushi-switch-track-icon
                         :kushi-switch-track-icon-on
                         :absolute-inline-start-inside]}
           [icon track-icon-props track-icon-on]]

          [:div {:class [:kushi-switch-track-content
                         :kushi-switch-track-icon
                         :kushi-switch-track-icon-off
                         :absolute-inline-end-inside]}
           [icon track-icon-props track-icon-off]]]))

     ;; Track content, leave off for now, consider removing
     #_(when label-in-track? 
       [:<>
        [:div {:class [:kushi-switch-track-content
                       :kushi-switch-track-label-on
                       :kushi-switch-track-label
                       :absolute-inline-start-inside]}
         [:span track-label-on]]

        [:div {:class [:kushi-switch-track-content
                       :kushi-switch-track-label-off
                       :kushi-switch-track-label
                       :absolute-inline-end-inside]}
         [:span track-label-off]]])

     (let [thumb-props      
           (!? (merge {:surface  :minimal-light-mode
                       :inert    true
                       :contour  contour
                       :position :absolute}
                      thumb-props
                      (when rounded-contour?
                        {:style (merge {:border-radius (str "calc(var(--" (name contour) ") - var(--track-inset-gap))")}
                                       (some-> thumb-props
                                               :style
                                               (util/maybe map?)))})))
           thumb-icon-props (merge {:icon-filled true} thumb-icon-props)]

       

       [thumb
        thumb-props
        (when label-on-thumb? 
          [:<> 
           [:div {:class [:kushi-switch-thumb-content
                          :kushi-switch-thumb-label
                          :kushi-switch-thumb-label-on]}
            thumb-label-on]
           [:div {:class [:kushi-switch-thumb-content
                          :kushi-switch-thumb-label
                          :kushi-switch-thumb-label-off]}
            thumb-label-off]])

        (when icon-on-thumb? 
          [:<> 
           [:div {:class [:kushi-switch-thumb-content
                          :kushi-switch-thumb-icon
                          :kushi-switch-thumb-icon-on]}
            [icon thumb-icon-props thumb-icon-on]]
           [:div {:class [:kushi-switch-thumb-content
                          :kushi-switch-thumb-icon
                          :kushi-switch-thumb-icon-off]}
            [icon thumb-icon-props thumb-icon-off]]])])

     #_[:div
      (merge-attrs
       {:data-ks-contour  :pill
        :data-ks-colorway colorway}
       (sx ".kushi-switch-thumb"
           :.transition
           [:--width :$thumb-height]
           :transition-duration--$xxfast
           :border-color--currentColor
           ["has-ancestor(.kushi-switch[aria-checked='false']):border-color" "color-mix(in srgb, currentColor, transparent)"]
           :cursor--pointer
           :bgc--$transparent-white-100
           :box-shadow--0:2px:6px:0:$transparent-black-15
           [:transform "translate(0, -50%)"]
           ["has-ancestor(.kushi-switch[aria-checked='true']):inset-inline-start"
            "calc(100% - var(--width))"]
           ["has-ancestor(.kushi-switch[disabled]):cursor"
            :not-allowed]
           :position--absolute
           :top--50%
           :inset-inline-start--0
           :h--$thumb-height
           :w--$width)
       thumb-attrs)
      [:div (sx ".kushi-switch-thumb-label-on"
                :.kushi-switch-thumb-content
                ["has-ancestor(.kushi-switch[disabled]):cursor" :not-allowed])
       thumb-label-on]
      [:div (sx ".kushi-switch-thumb-label-off"
                :.kushi-switch-thumb-content
                ["has-ancestor(.kushi-switch[disabled]):cursor" :not-allowed])
       thumb-label-off]
      ]]))
