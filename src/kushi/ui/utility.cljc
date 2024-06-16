(ns kushi.ui.utility
  (:require
   [kushi.utils :as util :refer [deep-merge]]))

(def disabled {:opacity :45%!important
               :cursor  :not-allowed!important})

(def combo-flex-utility-classes
  ;; Combinatorial flexbox utilities
  ;; ------------------------------------------------------
  [
   ;; TODO - Analyze performance tradeoffs with writing selectors like these:
   ;; first need to fix compiler to not prepend a "." in front of selector.
   ;; "[class^='flex-row-']" {:flex-direction  :row
   ;;                         :align-items     :center
   ;;                         :display         :flex}
   ;; "[class^='flex-row-']" {:flex-direction  :col
   ;;                         :display         :flex}
   ;; "[class$='c']"     {:justify-content :center}
   ;; "[class$='fs']"    {:justify-content :flex-start}
   ;; "[class$='fe']"    {:justify-content :flex-end}


   :flex-row-c        {:flex-direction  :row
                       :justify-content :center
                       :align-items     :center
                       :display         :flex}
   :flex-row-sa       {:flex-direction  :row
                       :justify-content :space-around
                       :align-items     :center
                       :display         :flex}
   :flex-col-se       {:flex-direction  :column
                       :justify-content :space-evenly
                       :display         :flex}
   :flex-row-fs       {:flex-direction  :row
                       :justify-content :flex-start
                       :align-items     :center
                       :display         :flex}
   :flex-col-c        {:flex-direction  :column
                       :justify-content :center
                       :display         :flex}
   :flex-row-se       {:flex-direction  :row
                       :justify-content :space-evenly
                       :align-items     :center
                       :display         :flex}
   :flex-col-fe       {:flex-direction  :column
                       :justify-content :flex-end
                       :display         :flex}
   :flex-col-fs       {:flex-direction  :column
                       :justify-content :flex-start
                       :display         :flex}
   :flex-row-fe       {:flex-direction  :row
                       :justify-content :flex-end
                       :align-items     :center
                       :display         :flex}
   :flex-col-sa       {:flex-direction  :column
                       :justify-content :space-around
                       :display         :flex}
   :flex-col-sb       {:flex-direction  :column
                       :justify-content :space-between
                       :display         :flex}
   :flex-row-sb       {:flex-direction  :row
                       :justify-content :space-between
                       :align-items     :center
                       :display         :flex}])


(def base-classes
  [
   ;; Visual debugging utilities
   ;; ------------------------------------------------------
   :debug-grid            {:background-image      (str "repeating-linear-gradient(to bottom, transparent, transparent var(--debug-grid-size), var(--debug-grid-color) var(--debug-grid-size), var(--debug-grid-color) calc(var(--debug-grid-size) + 1px), transparent calc(var(--debug-grid-size) + 1px)), "
                                                       "repeating-linear-gradient(to right,  transparent, transparent var(--debug-grid-size), var(--debug-grid-color) var(--debug-grid-size), var(--debug-grid-color) calc(var(--debug-grid-size) + 1px), transparent calc(var(--debug-grid-size) + 1px))")}
   :debug-grid-8          {:background-image      (str "repeating-linear-gradient(to bottom, transparent, transparent 8px, var(--debug-grid-color) 8px, var(--debug-grid-color) calc(8px + 1px), transparent calc(8px + 1px)), "
                                                       "repeating-linear-gradient(to right,  transparent, transparent 8px, var(--debug-grid-color) 8px, var(--debug-grid-color) calc(8px + 1px), transparent calc(8px + 1px))")
                           :dark:background-image (str "repeating-linear-gradient(to bottom, transparent, transparent 8px, var(--debug-grid-color-inverse) 8px, var(--debug-grid-color-inverse) calc(8px + 1px), transparent calc(8px + 1px)), "
                                                       "repeating-linear-gradient(to right,  transparent, transparent 8px, var(--debug-grid-color-inverse) 8px, var(--debug-grid-color-inverse) calc(8px + 1px), transparent calc(8px + 1px))")}
   :debug-grid-16         {:background-image      (str "repeating-linear-gradient(to bottom, transparent, transparent 16px, var(--debug-grid-color) 16px, var(--debug-grid-color) calc(16px + 1px), transparent calc(16px + 1px)), "
                                                       "repeating-linear-gradient(to right,  transparent, transparent 16px, var(--debug-grid-color) 16px, var(--debug-grid-color) calc(16px + 1px), transparent calc(16px + 1px))")
                           :dark:background-image (str "repeating-linear-gradient(to bottom, transparent, transparent 16px, var(--debug-grid-color-inverse) 16px, var(--debug-grid-color-inverse) calc(16px + 1px), transparent calc(16px + 1px)), "
                                                       "repeating-linear-gradient(to right,  transparent, transparent 16px, var(--debug-grid-color-inverse) 16px, var(--debug-grid-color-inverse) calc(16px + 1px), transparent calc(16px + 1px))")}


   ;; TODO - after string-based selector is working, use something like this instead
  ;;  "[class^='debug-']" {:outline-color  :silver
  ;;                       :outline-style  :solid
  ;;                       :outline-width  :1px
  ;;                       :outline-offset :-1px}
  ;;  :debug-red {:outline-color :$red-500}
   

   :debug-red          {:outline-color :$red-500||red
                        :outline-style :solid
                        :outline-offset :-1px
                        :outline-width :1px}

   :debug-blue         {:outline-color  :$blue-500||blue
                        :outline-style  :solid
                        :outline-offset :-1px
                        :outline-width  :1px}

   :debug-green        {:outline-color  :$green-500||green
                        :outline-style  :solid
                        :outline-offset :-1px
                        :outline-width  :1px}

   :debug-yellow       {:outline-color  :$yellow-500||yellow
                        :outline-style  :solid
                        :outline-offset :-1px
                        :outline-width  :1px}

   :debug-orange       {:outline-color  :$orange-500||orange
                        :outline-style  :solid
                        :outline-offset :-1px
                        :outline-width  :1px}

   :debug-purple       {:outline-color  :$purple-500||purple
                        :outline-style  :solid
                        :outline-offset :-1px
                        :outline-width  :1px}

   :debug-magenta      {:outline-color  :$magenta-500||magenta
                        :outline-style  :solid
                        :outline-offset :-1px
                        :outline-width  :1px}

   :debug-gray         {:outline-color  :$gray-500||gray
                        :outline-style  :solid
                        :outline-offset :-1px
                        :outline-width  :1px}

   :debug-black        {:outline-color  "black"
                        :outline-style  :solid
                        :outline-offset :-1px
                        :outline-width  :1px}

   :debug-white       {:outline-color  "white"
                       :outline-style  :solid
                       :outline-offset :-1px
                       :outline-width  :1px}

   :wireframe         {:outline-color  :silver
                       :outline-style  :solid
                       :outline-width  :1px
                       :outline-offset :-1px}
   ;; End debugging utils 
   

   ;; Borders
   :outlined              {:outline-color :currentColor
                           :outline-style :solid
                           :outline-width :1px
                           :outline-offset :-1px}
   :bordered              {:border-color :currentColor
                           :border-style :solid
                           :border-width :1px}

    ;; Non-combo flex utility classes
   :shrink               {:flex-shrink 1}
   :no-shrink            {:flex-shrink 0}
   :grow                 {:flex-grow 1}
   :no-grow              {:flex-grow 0}

   ;; Position
   ;; ------------------------------------------------------
   :relative      {:position :relative}
   :absolute      {:position :absolute}
   :fixed         {:position :fixed}
   :sticky        {:position :sticky}

    ;; Combinatorial absolute and fixed positioning utilities
    ;; ------------------------------------------------------
   :absolute-centered            {:position           :absolute
                                  :inset-inline-start "50%"
                                  :inset-block-start  "50%"
                                  :translate          "-50% -50%"}

   :absolute-fill                {:position :absolute
                                  :top      0
                                  :right    0
                                  :bottom   0
                                  :left     0}

   :after-absolute-fill         {:after:content  "\"\""
                                 :after:position :absolute
                                 :after:top      0
                                 :after:right    0
                                 :after:bottom   0
                                 :after:left     0}

   :before-absolute-fill         {:before:content  "\"\""
                                  :before:position :absolute
                                  :before:top      0
                                  :before:right    0
                                  :before:bottom   0
                                  :before:left     0}

   :absolute-inline-start-inside {:position           :absolute
                                  :inset-inline-start "0%"
                                  :inset-inline-end   :unset
                                  :inset-block-start  "50%"
                                  :translate          "0px -50%"}

   :absolute-inline-end-inside   {:position           :absolute
                                  :inset-inline-start :unset
                                  :inset-inline-end   "0%"
                                  :inset-block-start  "50%"
                                  :translate          "0px -50%"}

   :absolute-block-start-inside  {:position           :absolute
                                  :inset-block-start  "0%"
                                  :inset-block-end    :unset
                                  :inset-inline-start "50%"
                                  :translate          "-50% 0px"}

   :absolute-block-end-inside    {:position           :absolute
                                  :inset-block-start  :unset
                                  :inset-block-end    "0%"
                                  :inset-inline-start "50%"
                                  :translate          "-50% 0px"}

   :fixed-fill                   {:position :fixed
                                  :top      0
                                  :right    0
                                  :bottom   0
                                  :left     0}

   :fixed-centered            {:position           :fixed
                               :inset-inline-start "50%"
                               :inset-block-start  "50%"
                               :translate          "-50% -50%"}

   :fixed-inline-start-inside {:position           :fixed
                               :inset-inline-start "0%"
                               :inset-inline-end   :unset
                               :inset-block-start  "50%"
                               :translate          "0px -50%"}

   :fixed-inline-end-inside   {:position           :fixed
                               :inset-inline-end   "0%"
                               :inset-inline-start :unset
                               :inset-block-start  "50%"
                               :translate          "0px -50%"}

   :fixed-block-start-inside  {:position           :fixed
                               :inset-block-start  "0%"
                               :inset-block-end    :unset
                               :inset-inline-start "50%"
                               :translate          "-50%"}

   :fixed-block-end-inside    {:position           :fixed
                               :inset-block-end    "0%"
                               :inset-block-start  :unset
                               :inset-inline-start "50%"
                               :translate          "-50%"}

    ;; Surfaces, buttons, containers
    ;; ------------------------------------------------------
   :bgi-cover             {:background-position "center center"
                           :background-repeat   :no-repeat
                           :width               "100%"}

   :bgi-contain           {:background-position "center center"
                           :background-size     :contain
                           :background-repeat   :no-repeat
                           :width               "100%"
                           :height              "100%"}


    ;; Combinatorial transition utility
    ;; ------------------------------------------------------
   :transition            {:transition-property        :all
                           :transition-timing-function :$timing-linear-curve
                           :transition-duration        :$fast}])

(def override-classes
  [;; General
   ;; ------------------------------------------------------
   :invisible     {:opacity 0}
   :hidden        {:visibility :hidden}
   :visible       {:visibility :visible}
   :collapse      {:visibility :collapse}


   ;; Content
   ;; ------------------------------------------------------
  ;;  :content-blank {:content "\"\""}
  ;;  :open-in-new   {:content "url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' height='24px' viewBox='0 0 24 24' width='24px' fill='%23000000'%3E%3Cpath d='M0 0h24v24H0V0z' fill='none'/%3E%3Cpath d='M19 19H5V5h7V3H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2v-7h-2v7zM14 3v2h3.59l-9.83 9.83 1.41 1.41L19 6.41V10h2V3h-7z'/%3E%3C/svg%3E\")"}

   ;; Cursor
   ;; ------------------------------------------------------
   :pointer       {:cursor :pointer}

   ;; Display
   ;; ------------------------------------------------------
   :block         {:display :block}
   :inline        {:display :inline}
   :inline-block  {:display :inline-block}
   :flex          {:display :flex}
   :inline-flex   {:display :inline-flex}
   :grid          {:display :grid}
   :inline-grid   {:display :inline-grid}
   :flow-root     {:display :flow-root}
   :contents      {:display :contents}

   ;; Type styling
   ;; ------------------------------------------------------
   :sans          {:font-family :$sans-serif-font-stack}
   :serif         {:font-family :$serif-font-stack}
   :fantasy       {:font-family :fantasy}
   :cursive       {:font-family :cursive}
   :italic        {:font-style :italic}
   :oblique       {:font-style :oblique}

   ;; Text capitalization
   ;; ------------------------------------------------------
   :uppercase     {:text-transform :uppercase}
   :lowercase     {:text-transform :lowercase}
   :capitalize    {:text-transform :capitalize}

   ;; Icon enhancement
   ;; ------------------------------------------------------
   :enhanceable   {:gap :$icon-enhanceable-gap}

   ;; Type sizing
   ;; ------------------------------------------------------
   :xxxsmall      {:fs :$xxxsmall}
   :xxsmall       {:fs :$xxsmall}
   :xsmall        {:fs :$xsmall}
   :small         {:fs :$small}
   :medium        {:fs :$medium}
   :large         {:fs :$large}
   :xlarge        {:fs :$xlarge}
   :xxlarge       {:fs :$xxlarge}
   :xxxlarge      {:fs :$xxxlarge}
   :xxxxlarge     {:fs :$xxxxlarge}

   ;; Type weight
   ;; ------------------------------------------------------
   :thin          {:fw                                    :$thin
                   :&_.kushi-icon:font-variation-settings "'wght' 100"
                   :&.kushi-icon:font-variation-settings  "'wght' 100"
                   }
   :extra-light   {:fw                                    :$extra-light
                   :&_.kushi-icon:font-variation-settings "'wght' 200"
                   :&.kushi-icon:font-variation-settings  "'wght' 200"
                   }
   :light         {:fw                                    :$light
                   :&_.kushi-icon:font-variation-settings "'wght' 300"
                   :&.kushi-icon:font-variation-settings  "'wght' 300"
                   }
   :normal        {:fw                                    :$normal
                   :&_.kushi-icon:font-variation-settings "'wght' 400"
                   :&.kushi-icon:font-variation-settings  "'wght' 400"
                   }
   :wee-bold      {:fw                                    :$wee-bold
                   :&_.kushi-icon:font-variation-settings "'wght' 500"
                   :&.kushi-icon:font-variation-settings  "'wght' 500"
                   }
   :semi-bold     {:fw                                    :$semi-bold
                   :&_.kushi-icon:font-variation-settings "'wght' 600"
                   :&.kushi-icon:font-variation-settings  "'wght' 600"
                   }
   :bold          {:fw                                    :$bold
                   :&_.kushi-icon:font-variation-settings "'wght' 700"
                   :&.kushi-icon:font-variation-settings  "'wght' 700"
                   }
   :extra-bold    {:fw                                    :$extra-bold
                   :&_.kushi-icon:font-variation-settings "'wght' 700"
                   :&.kushi-icon:font-variation-settings  "'wght' 700"
                   }
   :heavy         {:fw                                    :$heavy
                   :&_.kushi-icon:font-variation-settings "'wght' 700"
                   :&.kushi-icon:font-variation-settings  "'wght' 700"
                   }


   ;; Tracking (aka letter-spacing)
   ;; ------------------------------------------------------
   :xxxtight      {:letter-spacing :$xxxtight}
   :xxtight       {:letter-spacing :$xxtight}
   :xtight        {:letter-spacing :$xtight}
   :tight         {:letter-spacing :$tight}
   :default-tracking {:letter-spacing 0}
   :loose         {:letter-spacing :$loose}
   :xloose        {:letter-spacing :$xloose}
   :xxloose       {:letter-spacing :$xxloose}
   :xxxloose      {:letter-spacing :$xxxloose}


   ;; Animations
   ;; ------------------------------------------------------
   ;; TODO - change these to t-shirt sizing
   :instant       {:transition-duration :$instant}
   :xxxfast       {:transition-duration :$xxxfast}
   :xxfast        {:transition-duration :$xxfast}
   :xfast         {:transition-duration :$xfast}
   :fast          {:transition-duration :$fast}
   :moderate      {:transition-duration :$moderate}
   :slow          {:transition-duration :$slow}
   :xslow         {:transition-duration :$xslow}
   :xxslow        {:transition-duration :$xxslow}
   :xxxslow       {:transition-duration :$xxxslow}


   ;; Surfaces, panes, cards, containers 2D
   ;; ------------------------------------------------------
   :rounded-xxxsmall      {:border-radius :$rounded-xxxsmal}
   :rounded-xxsmall       {:border-radius :$rounded-xxsmall}
   :rounded-xsmall        {:border-radius :$rounded-xsmall}
   :rounded-small         {:border-radius :$rounded-small}
   :rounded-medium        {:border-radius :$rounded-medium}
   :rounded-large         {:border-radius :$rounded-large}
   :rounded-xlarge        {:border-radius :$rounded-xlarge}
   :rounded-xxlarge       {:border-radius :$rounded-xxlarge}
   :rounded-xxxlarge      {:border-radius :$rounded-xxxlarge}

   :rounded               {:border-radius :$rounded-medium}


   ;; Buttons, badges 2D 
   ;; ------------------------------------------------------
   :rounded-absolute-xxxsmall      {:border-radius :$rounded-absolute-xxxsmal}
   :rounded-absolute-xxsmall       {:border-radius :$rounded-absolute-xxsmall}
   :rounded-absolute-xsmall        {:border-radius :$rounded-absolute-xsmall}
   :rounded-absolute-small         {:border-radius :$rounded-absolute-small}
   :rounded-absolute-medium        {:border-radius :$rounded-absolute-medium}
   :rounded-absolute-large         {:border-radius :$rounded-absolute-large}
   :rounded-absolute-xlarge        {:border-radius :$rounded-absolute-xlarge}
   :rounded-absolute-xxlarge       {:border-radius :$rounded-absolute-xxlarge}
   :rounded-absolute-xxxlarge      {:border-radius :$rounded-absolute-xxxlarge}

   ;; This one is used for buttons, tags etc ... The roundedness is always relative to font-size
   :rounded-absolute               {:border-radius :$rounded-absolute-medium}


   :sharp         {:border-radius 0}
   :pill          {:border-radius :9999px}


   ;; Surfaces, buttons, containers 3D
   ;; ------------------------------------------------------
   :debossed      {:text-shadow "0 1px 2px hsl(0deg 0% 100% / 55%), 0 -1px 2px hsl(0deg 0% 0% / 27%)"}
   :embossed      {:text-shadow "0 -1px 2px hsl(0deg 0% 100% / 55%), 0 1px 2px hsl(0deg 0% 0% / 27%)"}

   ;; TODO convex 0-5 plus inverse
   :convex        {:background-image :$convex-1}
   :convex-0      {:background-image :$convex-0}
   :convex-1      {:background-image :$convex-1}
   :convex-2      {:background-image :$convex-2}
   :convex-3      {:background-image :$convex-3}
   :convex-4      {:background-image :$convex-4}
   :convex-5      {:background-image :$convex-5}

   :elevated-0    {:box-shadow      :$elevated-0}
   :elevated-1    {:box-shadow      :$elevated-1
                   :dark:box-shadow :$elevated-1-inverse}
   :elevated-2    {:box-shadow      :$elevated-2
                   :dark:box-shadow :$elevated-2-inverse}
   :elevated-3    {:box-shadow      :$elevated-3
                   :dark:box-shadow :$elevated-3-inverse}
   :elevated-4    {:box-shadow      :$elevated-4
                   :dark:box-shadow :$elevated-4-inverse}
   :elevated-5    {:box-shadow      :$elevated-5
                   :dark:box-shadow :$elevated-5-inverse}

   :elevated      {:box-shadow      :$elevated-4
                   :dark:box-shadow :$elevated-4-inverse}

   ;; Controls
   ;; ------------------------------------------------------
   :disabled      disabled])

(def ui-theming-classes
  ;; Type weight
  ;; ------------------------------------------------------
  [:thin        {">.kushi-radio-input:outline-width" :$input-border-weight-thin
                 ">.kushi-checkbox-input:bw"         :$input-border-weight-thin}
   :extra-light {">.kushi-radio-input:outline-width" :$input-border-weight-extra-light
                 ">.kushi-checkbox-input:bw"         :$input-border-weight-extra-light}
   :light       {">.kushi-radio-input:outline-width" :$input-border-weight-light
                 ">.kushi-checkbox-input:bw"         :$input-border-weight-light}
   :normal      {">.kushi-radio-input:outline-width" :$input-border-weight-normal
                 ">.kushi-checkbox-input:bw"         :$input-border-weight-normal}
   :wee-bold    {">.kushi-radio-input:outline-width" :$input-border-weight-wee-bold
                 ">.kushi-checkbox-input:bw"         :$input-border-weight-wee-bold}
   :semi-bold   {">.kushi-radio-input:outline-width" :$input-border-weight-semi-bold
                 ">.kushi-checkbox-input:bw"         :$input-border-weight-semi-bold}
   :bold        {">.kushi-radio-input:outline-width" :$input-border-weight-bold
                 ">.kushi-checkbox-input:bw"         :$input-border-weight-bold}
   :extra-bold  {">.kushi-radio-input:outline-width" :$input-border-weight-extra-bold
                 ">.kushi-checkbox-input:bw"         :$input-border-weight-extra-bold}
   :heavy       {">.kushi-radio-input:outline-width" :$input-border-weight-heavy
                 ">.kushi-checkbox-input:bw"         :$input-border-weight-heavy}])

(def geometry-classes
  [:top-left-corner-outside
   {:position  :absolute,
    :top       "0%",
    :bottom    :unset,
    :left      "0%",
    :right     :unset,
    :translate "-100% -100%"}
   :top-left-corner
   {:position  :absolute,
    :top       "0%",
    :bottom    :unset,
    :left      "0%",
    :right     :unset,
    :translate "-50% -50%"}
   :top-left-corner-inside
   {:position  :absolute,
    :top       "0%",
    :bottom    :unset,
    :left      "0%",
    :right     :unset,
    :translate "0% 0%"}
   :top-left-outside
   {:position  :absolute,
    :top       "0%",
    :bottom    :unset,
    :left      "0%",
    :right     :unset,
    :translate "0% -100%"}
   :top-left
   {:position  :absolute,
    :top       "0%",
    :bottom    :unset,
    :left      "0%",
    :right     :unset,
    :translate "0% -50%"}
   :top-outside
   {:position  :absolute,
    :top       "0%",
    :bottom    :unset,
    :left      "50%",
    :right     :unset,
    :translate "-50% -100%"}
   :top
   {:position  :absolute,
    :top       "0%",
    :bottom    :unset,
    :left      "50%",
    :right     :unset,
    :translate "-50% -50%"}
   :top-inside
   {:position  :absolute,
    :top       "0%",
    :bottom    :unset,
    :left      "50%",
    :right     :unset,
    :translate "-50% 0%"}
   :top-right-outside
   {:position  :absolute,
    :top       "0%",
    :bottom    :unset,
    :left      :unset,
    :right     "0%",
    :translate "0% -100%"}
   :top-right
   {:position  :absolute,
    :top       "0%",
    :bottom    :unset,
    :left      :unset,
    :right     "0%",
    :translate "0% -50%"}
   :top-right-corner-outside
   {:position  :absolute,
    :top       "0%",
    :bottom    :unset,
    :left      :unset,
    :right     "0%",
    :translate "100% -100%"}
   :top-right-corner
   {:position  :absolute,
    :top       "0%",
    :bottom    :unset,
    :left      :unset,
    :right     "0%",
    :translate "50% -50%"}
   :top-right-corner-inside
   {:position  :absolute,
    :top       "0%",
    :bottom    :unset,
    :left      :unset,
    :right     "0%",
    :translate "0% 0%"}
   :right-top-outside
   {:position  :absolute,
    :top       "0%",
    :bottom    :unset,
    :left      :unset,
    :right     "0%",
    :translate "100% 0%"}
   :right-top
   {:position  :absolute,
    :top       "0%",
    :bottom    :unset,
    :left      :unset,
    :right     "0%",
    :translate "50% 0%"}
   :right-inside
   {:position  :absolute,
    :top       "50%",
    :bottom    :unset,
    :left      :unset,
    :right     "0%",
    :translate "0% -50%"}
   :right
   {:position  :absolute,
    :top       "50%",
    :bottom    :unset,
    :left      :unset,
    :right     "0%",
    :translate "50% -50%"}
   :right-outside
   {:position  :absolute,
    :top       "50%",
    :bottom    :unset,
    :left      :unset,
    :right     "0%",
    :translate "100% -50%"}
   :right-bottom-outside
   {:position  :absolute,
    :top       :unset,
    :bottom    "0%",
    :left      :unset,
    :right     "0%",
    :translate "100% 0%"}
   :right-bottom
   {:position  :absolute,
    :top       :unset,
    :bottom    "0%",
    :left      :unset,
    :right     "0%",
    :translate "50% 0%"}
   :bottom-right-corner-outside
   {:position  :absolute,
    :top       :unset,
    :bottom    "0%",
    :left      :unset,
    :right     "0%",
    :translate "100% 100%"}
   :bottom-right-corner
   {:position  :absolute,
    :top       :unset,
    :bottom    "0%",
    :left      :unset,
    :right     "0%",
    :translate "50% 50%"}
   :bottom-right-corner-inside
   {:position  :absolute,
    :top       :unset,
    :bottom    "0%",
    :left      :unset,
    :right     "0%",
    :translate "0% 0%"}
   :bottom-right-outside
   {:position  :absolute,
    :top       :unset,
    :bottom    "0%",
    :left      :unset,
    :right     "0%",
    :translate "0% 100%"}
   :bottom-right
   {:position  :absolute,
    :top       :unset,
    :bottom    "0%",
    :left      :unset,
    :right     "0%",
    :translate "0% 50%"}
   :bottom-inside
   {:position  :absolute,
    :top       :unset,
    :bottom    "0%",
    :left      "50%",
    :right     :unset,
    :translate "-50% 0%"}
   :bottom
   {:position  :absolute,
    :top       :unset,
    :bottom    "0%",
    :left      "50%",
    :right     :unset,
    :translate "-50% 50%"}
   :bottom-outside
   {:position  :absolute,
    :top       :unset,
    :bottom    "0%",
    :left      "50%",
    :right     :unset,
    :translate "-50% 100%"}
   :bottom-left-outside
   {:position  :absolute,
    :top       :unset,
    :bottom    "0%",
    :left      "0%",
    :right     :unset,
    :translate "0% 100%"}
   :bottom-left
   {:position  :absolute,
    :top       :unset,
    :bottom    "0%",
    :left      "0%",
    :right     :unset,
    :translate "0% 50%"}
   :bottom-left-corner-outside
   {:position  :absolute,
    :top       :unset,
    :bottom    "0%",
    :left      "0%",
    :right     :unset,
    :translate "-100% 100%"}
   :bottom-left-corner
   {:position  :absolute,
    :top       :unset,
    :bottom    "0%",
    :left      "0%",
    :right     :unset,
    :translate "-50% 50%"}
   :bottom-left-corner-inside
   {:position  :absolute,
    :top       :unset,
    :bottom    "0%",
    :left      "0%",
    :right     :unset,
    :translate "0% 0%"}
   :left-bottom-outside
   {:position  :absolute,
    :top       :unset,
    :bottom    "0%",
    :left      "0%",
    :right     :unset,
    :translate "-100% 0%"}
   :left-bottom
   {:position  :absolute,
    :top       :unset,
    :bottom    "0%",
    :left      "0%",
    :right     :unset,
    :translate "-50% 0%"}
   :left-inside
   {:position  :absolute,
    :top       "50%",
    :bottom    :unset,
    :left      "0%",
    :right     :unset,
    :translate "0% -50%"}
   :left
   {:position  :absolute,
    :top       "50%",
    :bottom    :unset,
    :left      "0%",
    :right     :unset,
    :translate "-50% -50%"}
   :left-outside
   {:position  :absolute,
    :top       "50%",
    :bottom    :unset,
    :left      "0%",
    :right     :unset,
    :translate "-100% -50%"}
   :left-top-outside
   {:position  :absolute,
    :top       "0%",
    :bottom    :unset,
    :left      "0%",
    :right     :unset,
    :translate "-100% 0%"}
   :left-top
   {:position  :absolute,
    :top       "0%",
    :bottom    :unset,
    :left      "0%",
    :right     :unset,
    :translate "-50% 0%"} ])

(def geometry-classes-fixed
  [:top-left-corner-outside-fixed
   {:position  :fixed,
    :top       "0%",
    :bottom    :unset,
    :left      "0%",
    :right     :unset,
    :translate "-100% -100%"}
   :top-left-corner-fixed
   {:position  :fixed,
    :top       "0%",
    :bottom    :unset,
    :left      "0%",
    :right     :unset,
    :translate "-50% -50%"}
   :top-left-corner-inside-fixed
   {:position  :fixed,
    :top       "0%",
    :bottom    :unset,
    :left      "0%",
    :right     :unset,
    :translate "0% 0%"}
   :top-left-outside-fixed
   {:position  :fixed,
    :top       "0%",
    :bottom    :unset,
    :left      "0%",
    :right     :unset,
    :translate "0% -100%"}
   :top-left-fixed
   {:position  :fixed,
    :top       "0%",
    :bottom    :unset,
    :left      "0%",
    :right     :unset,
    :translate "0% -50%"}
   :top-outside-fixed
   {:position  :fixed,
    :top       "0%",
    :bottom    :unset,
    :left      "50%",
    :right     :unset,
    :translate "-50% -100%"}
   :top-fixed
   {:position  :fixed,
    :top       "0%",
    :bottom    :unset,
    :left      "50%",
    :right     :unset,
    :translate "-50% -50%"}
   :top-inside-fixed
   {:position  :fixed,
    :top       "0%",
    :bottom    :unset,
    :left      "50%",
    :right     :unset,
    :translate "-50% 0%"}
   :top-right-outside-fixed
   {:position  :fixed,
    :top       "0%",
    :bottom    :unset,
    :left      :unset,
    :right     "0%",
    :translate "0% -100%"}
   :top-right-fixed
   {:position  :fixed,
    :top       "0%",
    :bottom    :unset,
    :left      :unset,
    :right     "0%",
    :translate "0% -50%"}
   :top-right-corner-outside-fixed
   {:position  :fixed,
    :top       "0%",
    :bottom    :unset,
    :left      :unset,
    :right     "0%",
    :translate "100% -100%"}
   :top-right-corner-fixed
   {:position  :fixed,
    :top       "0%",
    :bottom    :unset,
    :left      :unset,
    :right     "0%",
    :translate "50% -50%"}
   :top-right-corner-inside-fixed
   {:position  :fixed,
    :top       "0%",
    :bottom    :unset,
    :left      :unset,
    :right     "0%",
    :translate "0% 0%"}
   :right-top-outside-fixed
   {:position  :fixed,
    :top       "0%",
    :bottom    :unset,
    :left      :unset,
    :right     "0%",
    :translate "100% 0%"}
   :right-top-fixed
   {:position  :fixed,
    :top       "0%",
    :bottom    :unset,
    :left      :unset,
    :right     "0%",
    :translate "50% 0%"}
   :right-inside-fixed
   {:position  :fixed,
    :top       "50%",
    :bottom    :unset,
    :left      :unset,
    :right     "0%",
    :translate "0% -50%"}
   :right-fixed
   {:position  :fixed,
    :top       "50%",
    :bottom    :unset,
    :left      :unset,
    :right     "0%",
    :translate "50% -50%"}
   :right-outside-fixed
   {:position  :fixed,
    :top       "50%",
    :bottom    :unset,
    :left      :unset,
    :right     "0%",
    :translate "100% -50%"}
   :right-bottom-outside-fixed
   {:position  :fixed,
    :top       :unset,
    :bottom    "0%",
    :left      :unset,
    :right     "0%",
    :translate "100% 0%"}
   :right-bottom-fixed
   {:position  :fixed,
    :top       :unset,
    :bottom    "0%",
    :left      :unset,
    :right     "0%",
    :translate "50% 0%"}
   :bottom-right-corner-outside-fixed
   {:position  :fixed,
    :top       :unset,
    :bottom    "0%",
    :left      :unset,
    :right     "0%",
    :translate "100% 100%"}
   :bottom-right-corner-fixed
   {:position  :fixed,
    :top       :unset,
    :bottom    "0%",
    :left      :unset,
    :right     "0%",
    :translate "50% 50%"}
   :bottom-right-corner-inside-fixed
   {:position  :fixed,
    :top       :unset,
    :bottom    "0%",
    :left      :unset,
    :right     "0%",
    :translate "0% 0%"}
   :bottom-right-outside-fixed
   {:position  :fixed,
    :top       :unset,
    :bottom    "0%",
    :left      :unset,
    :right     "0%",
    :translate "0% 100%"}
   :bottom-right-fixed
   {:position  :fixed,
    :top       :unset,
    :bottom    "0%",
    :left      :unset,
    :right     "0%",
    :translate "0% 50%"}
   :bottom-inside-fixed
   {:position  :fixed,
    :top       :unset,
    :bottom    "0%",
    :left      "50%",
    :right     :unset,
    :translate "-50% 0%"}
   :bottom-fixed
   {:position  :fixed,
    :top       :unset,
    :bottom    "0%",
    :left      "50%",
    :right     :unset,
    :translate "-50% 50%"}
   :bottom-outside-fixed
   {:position  :fixed,
    :top       :unset,
    :bottom    "0%",
    :left      "50%",
    :right     :unset,
    :translate "-50% 100%"}
   :bottom-left-outside-fixed
   {:position  :fixed,
    :top       :unset,
    :bottom    "0%",
    :left      "0%",
    :right     :unset,
    :translate "0% 100%"}
   :bottom-left-fixed
   {:position  :fixed,
    :top       :unset,
    :bottom    "0%",
    :left      "0%",
    :right     :unset,
    :translate "0% 50%"}
   :bottom-left-corner-outside-fixed
   {:position  :fixed,
    :top       :unset,
    :bottom    "0%",
    :left      "0%",
    :right     :unset,
    :translate "-100% 100%"}
   :bottom-left-corner-fixed
   {:position  :fixed,
    :top       :unset,
    :bottom    "0%",
    :left      "0%",
    :right     :unset,
    :translate "-50% 50%"}
   :bottom-left-corner-inside-fixed
   {:position  :fixed,
    :top       :unset,
    :bottom    "0%",
    :left      "0%",
    :right     :unset,
    :translate "0% 0%"}
   :left-bottom-outside-fixed
   {:position  :fixed,
    :top       :unset,
    :bottom    "0%",
    :left      "0%",
    :right     :unset,
    :translate "-100% 0%"}
   :left-bottom-fixed
   {:position  :fixed,
    :top       :unset,
    :bottom    "0%",
    :left      "0%",
    :right     :unset,
    :translate "-50% 0%"}
   :left-inside-fixed
   {:position  :fixed,
    :top       "50%",
    :bottom    :unset,
    :left      "0%",
    :right     :unset,
    :translate "0% -50%"}
   :left-fixed
   {:position  :fixed,
    :top       "50%",
    :bottom    :unset,
    :left      "0%",
    :right     :unset,
    :translate "-50% -50%"}
   :left-outside-fixed
   {:position  :fixed,
    :top       "50%",
    :bottom    :unset,
    :left      "0%",
    :right     :unset,
    :translate "-100% -50%"}
   :left-top-outside-fixed
   {:position  :fixed,
    :top       "0%",
    :bottom    :unset,
    :left      "0%",
    :right     :unset,
    :translate "-100% 0%"}
   :left-top-fixed
   {:position  :fixed,
    :top       "0%",
    :bottom    :unset,
    :left      "0%",
    :right     :unset,
    :translate "-50% 0%"} ])

(def utility-class-ks
  (mapcat util/kwargs-keys
          [combo-flex-utility-classes
           base-classes
           geometry-classes
          ;;  geometry-classes-fixed
           override-classes
           ui-theming-classes]))

(def utility-classes
  (apply deep-merge
         (map #(apply hash-map %)
              [combo-flex-utility-classes
               base-classes
               geometry-classes
              ;;  geometry-classes-fixed
               override-classes
               ui-theming-classes])))


