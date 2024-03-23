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
    ;; (typescale/create-type-scale {:size-limit "xxx" :shift 0})
    {:design-tokens    [:$sans-serif-font-stack                  "Inter, system-ui, sans-serif"
                        :$primary-font-family                    :$sans-serif-font-stack

                        :$button-with-icon-padding-inline-offset :0.8em
                        :$button-border-width                    :1.5px

                        :$kushi-playground-mobile-header-height  :46px
                        :$tooltip-border-radius                  :5px
                        :$tooltip-delay-duration                 :200ms
                        :$tooltip-auto-placement-y-threshold     0.1
                        :$tooltip-offset-start                   :10px
                        :$tooltip-offset                         :10px
                        :$tooltip-arrow-depth-min-px             :4px
                        :$modal-transition-duration              :$xxxfast
                        :$modal-backdrop-color                   :$black-transparent-40
                        ]

     :typescale        (create-typescale {:size-limit "xxx"
                                          :shift      0})
     :type-scale-shift 1

     :font-loading     font-loading

     :ui               ["body"
                        ;; this should be :--primary-font-family ?
                        {
                         ;; :color       :$gray-950
                         }

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
