(ns site.theme
  (:require
   [kushi.ui.typescale :refer [create-typescale]]))

(def font-loading
  {
   ;;  :add-default-sans-font-family? true
   ;;  :add-default-code-font-family? true
   ;;  :add-default-serif-font-family? true


   ;;  Google Fonts
   ;;  :google-fonts  [
   ;;                  "Roboto"
   ;;                  {:family "Public Sans"
   ;;                   :styles {:normal [100]
   ;;                            :italic [300]}}]


   ;;  Google Material Symbols
  ;;  :google-material-symbols ["Material Symbols Outlined"
  ;;                            "Material Symbols Rounded"
  ;;                            "Material Symbols Sharp"]

   ;; Because all three requests above (for Material Symbols font-families)
   ;; do not contain an entry for :axes, they will load fonts with all the variable axes included.
   ;; An example, the first element ("Material Symbols Outlined") in the :google-material-symbols vector is equivalent to this:
   ;;
   :google-material-symbols [{:family "Material Symbols Outlined"
                              :axes   {:opsz :20..48
                                       :wght :100..700
                                       :grad :-50..200
                                       :fill :0..1}}]

   ;; When you've made a design decision about the look and feel of icons
   ;; in your project, you can load the symbols font of your choice as a
   ;; static icon font instead of a variable one:

  ;;  :google-material-symbols [{:family "Material Symbols Outlined"
  ;;                             :axes   {:opsz 24
  ;;                                      :wght 400
  ;;                                      :grad 0
  ;;                                      :fill 0}}]
   })

(def my-theme
    {:design-tokens    [:$sans-serif-font-stack                  "Inter, system-ui, sans-serif"
                        :$primary-font-family                    :$sans-serif-font-stack
                        :$code-color                             :$accent-750
                        :$code-background-color                  :$accent-50
                        :$code-color-inverse                     :$accent-100
                        :$code-background-color-inverse          :$accent-900

                        :$fune-z-index                           1000
                        :$button-with-icon-padding-inline-offset :0.8em
                        :$button-border-width                    :1.5px
                        :$kushi-playground-mobile-header-height  :46px
                        :$tooltip-font-weight                    :$wee-bold
                        :$tooltip-color-inverse                  :$body-color
                        :$tooltip-border-radius                  :5px
                        :$tooltip-delay-duration                 :700ms
                        :$tooltip-auto-placement-y-threshold     0.1
                        :$tooltip-arrow-depth                    :4px
                        :$tooltip-text-on-click-duration         :2000ms


                        ;; these are set in tokens
                        ;; ok to comment out
                        :$modal-backdrop-color                   :$black-transparent-40
                        :$modal-transition-duration              :$xfast
                        :$modal-padding                          :2rem
                        :$modal-padding-inline                   :$modal-padding
                        :$modal-padding-block                    :$modal-padding

                        :$text-input-border-radius               :0.3em
                        :$fune-min-width                         :150px
                        :$fune-min-height                        :75px
                        :$popover-border-width                   :1px
                        :$popover-border-style                   :solid
                        :$popover-border-color                   :$neutral-300
                        :$popover-arrow-depth                    :7px
                        :$popover-border-color-inverse           :$neutral-500
                        :$popover-auto-dismiss-duration          :5000ms

                        ;; Toasts
                        ;; ------------------------------------------------------

                        ;; toast colors and images
                        :$toast-background-color                 :$fune-background-color
                        :$toast-background-color-inverse         :$fune-background-color-inverse
                        :$toast-background-image                 :none
                        :$toast-box-shadow                       :$fune-box-shadow
                        :$toast-box-shadow-inverse               :$fune-box-shadow-inverse
                        :$toast-border-width                     :$fune-border-width
                        :$toast-border-style                     :$fune-border-style
                        :$toast-border-color                     :$fune-border-color
                        :$toast-border-color-inverse             :$fune-border-color-inverse

                        ;; toast geometry
                        :$toast-min-width                        :$fune-min-width
                        :$toast-min-height                       :$fune-min-height
                        :$toast-border-radius                    :$fune-border-radius
                        :$toast-inset-inline                     :1.5rem
                        :$toast-inset-block                      :1.5rem

                        ;; toast choreography
                        :$toast-inset-inline-enter               :0
                        :$toast-inset-block-enter                :0
                        :$toast-z-index                          :$fune-z-index
                        :$toast-delay-duration                   :200ms
                        :$toast-initial-scale                    :$fune-initial-scale 
                        :$toast-transition-duration              :$fune-transition-duration 
                        :$toast-transition-timing-function       :$fune-transition-timing-function 
                        :$toast-auto-dismiss-duration            :5000ms
                        
                        :$toast-border-width                     :1px
                        :$toast-border-color                     :$gray-150
                        :$toast-border-color-inverse             :$gray-700]
     :typescale        (create-typescale {:size-limit "xxx" :shift 0})
     :type-scale-shift 1
     :font-loading     font-loading
     :ui               ["body"
                        ;; this should be :--primary-font-family ?
                        {
                         ;; :color       :$gray-950
                         }

                        "code, .code"
                        {:bgc :$code-background-color
                         :c   :$code-color}

                        ".dark code, .dark .code"
                        {:bgc :$code-background-color-inverse
                         :c   :$code-color-inverse}

   ;; Override .styled-scrollbars (defined in basetheme) so that scrollbars only appear on hover
   ;; Put this back in when you can use mq to isolate pointer devices
  ;;  ".styled-scrollbars:hover"
  ;;  {:scrollbar-color      :$scrollbar-thumb-color
  ;;   :dark:scrollbar-color :$scrollbar-thumb-color-inverse}
                        
  ;;  ".styled-scrollbars"
  ;;  {:scrollbar-color :transparent
  ;;   :dark:scrollbar-color :transparent
  ;;   :scrollbar-width :thin}
                        
  ;;  ".styled-scrollbars:hover::-webkit-scrollbar-thumb"
  ;;  {:background    :$scrollbar-thumb-color
  ;;   :dark:background :$scrollbar-thumb-color-inverse}
                        
  ;;  ".styled-scrollbars::-webkit-scrollbar-thumb"
  ;;  {:background    :transparent
  ;;   :dark:background :transparent
  ;;   :border-radius :9999px
  ;;   :border        "0px solid var(--scrollbar-background-color)"
  ;;   :dark:border   "0px solid var(--scrollbar-background-color-inverse)"}
                        

                        ]})
