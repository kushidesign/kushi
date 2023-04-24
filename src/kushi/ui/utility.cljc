(ns kushi.ui.utility
  (:require
   [kushi.utils :refer [deep-merge]]
   [kushi.utils :as util]))

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
                       :display         :flex}
                       ])


(def base-classes
  [
   ;; Visual debugging utilities
   ;; ------------------------------------------------------
   :debug-grid            {:background-image (str "repeating-linear-gradient(to bottom, transparent, transparent var(--kushi-debug-grid-size), var(--kushi-debug-grid-color) var(--kushi-debug-grid-size), var(--kushi-debug-grid-color) calc(var(--kushi-debug-grid-size) + 1px), transparent calc(var(--kushi-debug-grid-size) + 1px)), "
                                                  "repeating-linear-gradient(to right, transparent, transparent var(--kushi-debug-grid-size), var(--kushi-debug-grid-color) var(--kushi-debug-grid-size), var(--kushi-debug-grid-color) calc(var(--kushi-debug-grid-size) + 1px), transparent calc(var(--kushi-debug-grid-size) + 1px))")}
   :debug-grid-8          {:background-image (str "repeating-linear-gradient(to bottom, transparent, transparent 8px, var(--kushi-debug-grid-color) 8px, var(--kushi-debug-grid-color) calc(8px + 1px), transparent calc(8px + 1px)), "
                                                  "repeating-linear-gradient(to right, transparent, transparent 8px, var(--kushi-debug-grid-color) 8px, var(--kushi-debug-grid-color) calc(8px + 1px), transparent calc(8px + 1px))")
                           :dark:background-image (str "repeating-linear-gradient(to bottom, transparent, transparent 8px, var(--kushi-debug-grid-color-inverse) 8px, var(--kushi-debug-grid-color-inverse) calc(8px + 1px), transparent calc(8px + 1px)), "
                                                       "repeating-linear-gradient(to right, transparent, transparent 8px, var(--kushi-debug-grid-color-inverse) 8px, var(--kushi-debug-grid-color-inverse) calc(8px + 1px), transparent calc(8px + 1px))")}
   :debug-grid-16         {:background-image      (str "repeating-linear-gradient(to bottom, transparent, transparent 16px, var(--kushi-debug-grid-color) 16px, var(--kushi-debug-grid-color) calc(16px + 1px), transparent calc(16px + 1px)), "
                                                       "repeating-linear-gradient(to right, transparent, transparent 16px, var(--kushi-debug-grid-color) 16px, var(--kushi-debug-grid-color) calc(16px + 1px), transparent calc(16px + 1px))")
                           :dark:background-image (str "repeating-linear-gradient(to bottom, transparent, transparent 16px, var(--kushi-debug-grid-color-inverse) 16px, var(--kushi-debug-grid-color-inverse) calc(16px + 1px), transparent calc(16px + 1px)), "
                                                       "repeating-linear-gradient(to right, transparent, transparent 16px, var(--kushi-debug-grid-color-inverse) 16px, var(--kushi-debug-grid-color-inverse) calc(16px + 1px), transparent calc(16px + 1px))")}
   :bordered              {:border-color :currentColor
                           :border-style :solid
                           :border-width "1px"}


   ;; could do something like this
   ;; "[class^='bordered-']" {:border-style :solid :border-width "1px"}

   :bordered-red          {:border-color "var(--red-500, red)"
                           :border-style :solid
                           :border-width "1px"}

   :bordered-blue         {:border-color "var(--blue-500, blue)"
                           :border-style :solid
                           :border-width "1px"}

   :bordered-green        {:border-color "var(--green-500, green)"
                           :border-style :solid
                           :border-width "1px"}

   :bordered-yellow       {:border-color "var(--yellow-500, yellow)"
                           :border-style :solid
                           :border-width "1px"}

   :bordered-orange       {:border-color "var(--orange-500, orange)"
                           :border-style :solid
                           :border-width "1px"}

   :bordered-purple       {:border-color "var(--purple-500, purple)"
                           :border-style :solid
                           :border-width "1px"}

   :bordered-magenta      {:border-color "var(--magenta-500, magenta)"
                           :border-style :solid
                           :border-width "1px"}

   :bordered-gray         {:border-color "var(--gray-500, gray)"
                           :border-style :solid
                           :border-width "1px"}

   :bordered-black        {:border-color "black"
                           :border-style :solid
                           :border-width "1px"}

   :bordered-white       {:border-color "white"
                          :border-style :solid
                          :border-width "1px"}

   :wireframe             {:outline-color :silver
                           :outline-style :solid
                           :outline-width "1px"}

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

    ;; Combinatorial absolute and fixed positioning utilities
    ;; ------------------------------------------------------
   :absolute-fill         {:position :absolute
                           :top      0
                           :right    0
                           :bottom   0
                           :left     0}

   :after-absolute-centered     {:after:content            "\"\""
                                 :after:position           :absolute
                                 :after:inset-inline-start "50%"
                                 :after:inset-block-start  "50%"
                                 :after:transform          "translate(-50%, -50%)"}

   :after-absolute-inline-start {:after:content            "\"\""
                                 :after:position           :absolute
                                 :after:inset-inline-start "0%"
                                 :after:inset-inline-end   :unset
                                 :after:inset-block-start  "50%"
                                 :after:transform          "translateY(-50%)"}

   :after-absolute-inline-end   {:after:content            "\"\""
                                 :after:position           :absolute
                                 :after:inset-inline-end   "0%"
                                 :after:inset-inline-start :unset
                                 :after:inset-block-start  "50%"
                                 :after:transform          "translateY(-50%)"}

   :after-absolute-block-start  {:after:content            "\"\""
                                 :after:position           :absolute
                                 :after:inset-block-start  "0%"
                                 :after:inset-block-end    :unset
                                 :after:inset-inline-start "50%"
                                 :after:transform          "translateX(-50%)"}

   :after-absolute-block-end    {:after:content            "\"\""
                                 :after:position           :absolute
                                 :after:inset-block-end    "0%"
                                 :after:inset-block-start  :unset
                                 :after:inset-inline-start "50%"
                                 :after:transform          "translateX(-50%)"}

   :after-absolute-fill         {:after:content  "\"\""
                                 :after:position :absolute
                                 :after:top      0
                                 :after:right    0
                                 :after:bottom   0
                                 :after:left     0}

   :before-absolute-centered     {:before:content            "\"\""
                                  :before:position           :absolute
                                  :before:inset-inline-start "50%"
                                  :before:inset-block-start  "50%"
                                  :before:transform          "translate(-50%, -50%)"}

   :before-absolute-inline-start {:before:content            "\"\""
                                  :before:position           :absolute
                                  :before:inset-inline-end   :unset
                                  :before:inset-inline-start "0%"
                                  :before:inset-block-start  "50%"
                                  :before:transform          "translateY(-50%)"}

   :before-absolute-inline-end   {:before:content            "\"\""
                                  :before:position           :absolute
                                  :before:inset-inline-start :unset
                                  :before:inset-inline-end   "0%"
                                  :before:inset-block-start  "50%"
                                  :before:transform          "translateY(-50%)"}

   :before-absolute-block-start  {:before:content            "\"\""
                                  :before:position           :absolute
                                  :before:inset-block-end    :unset
                                  :before:inset-block-start  "0%"
                                  :before:inset-inline-start "50%"
                                  :before:transform          "translateX(-50%)"}

   :before-absolute-block-end    {:before:content            "\"\""
                                  :before:position           :absolute
                                  :before:inset-block-start  :unset
                                  :before:inset-block-end    "0%"
                                  :before:inset-inline-start "50%"
                                  :before:transform          "translateX(-50%)"}

   :before-absolute-fill         {:before:content  "\"\""
                                  :before:position :absolute
                                  :before:top      0
                                  :before:right    0
                                  :before:bottom   0
                                  :before:left     0}


   :absolute-centered     {:position           :absolute
                           :inset-inline-start "50%"
                           :inset-block-start  "50%"
                           :transform          "translate(-50%, -50%)"}

   :absolute-inline-start {:position           :absolute
                           :inset-inline-start "0%"
                           :inset-inline-end   :unset
                           :inset-block-start  "50%"
                           :transform          "translateY(-50%)"}

   :absolute-inline-end   {:position           :absolute
                           :inset-inline-start :unset
                           :inset-inline-end   "0%"
                           :inset-block-start  "50%"
                           :transform          "translateY(-50%)"}

   :absolute-block-start  {:position           :absolute
                           :inset-block-start  "0%"
                           :inset-block-end    :unset
                           :inset-inline-start "50%"
                           :transform          "translateX(-50%)"}

   :absolute-block-end    {:position           :absolute
                           :inset-block-start  :unset
                           :inset-block-end    "0%"
                           :inset-inline-start "50%"
                           :transform          "translateX(-50%)"}

   :fixed-fill            {:position :fixed
                           :top      0
                           :right    0
                           :bottom   0
                           :left     0}

   :fixed-centered     {:position           :fixed
                        :inset-inline-start "50%"
                        :inset-block-start  "50%"
                        :transform          "translate(-50%, -50%)"}

   :fixed-inline-start {:position           :fixed
                        :inset-inline-start "0%"
                        :inset-inline-end   :unset
                        :inset-block-start  "50%"
                        :transform          "translateY(-50%)"}

   :fixed-inline-end   {:position           :fixed
                        :inset-inline-end   "0%"
                        :inset-inline-start :unset
                        :inset-block-start  "50%"
                        :transform          "translateY(-50%)"}

   :fixed-block-start  {:position           :fixed
                        :inset-block-start  "0%"
                        :inset-block-end    :unset
                        :inset-inline-start "50%"
                        :transform          "translateX(-50%)"}

   :fixed-block-end    {:position           :fixed
                        :inset-block-end    "0%"
                        :inset-block-start  :unset
                        :inset-inline-start "50%"
                        :transform          "translateX(-50%)"}

   ;; northwest
   :northwest-inside {:position           :absolute
                      :inset-block-start  0
                      :inset-block-end    :unset
                      :inset-inline-start 0
                      :inset-inline-end   :unset
                      :transform          "translate(0, 0)"}
   :northwest {:position           :absolute
               :inset-block-start  0
               :inset-block-end    :unset
               :inset-inline-start 0
               :inset-inline-end   :unset
               :transform          "translate(-50%, -50%)"}
   :northwest-outside {:position           :absolute
                       :inset-block-start  0
                       :inset-block-end    :unset
                       :inset-inline-start 0
                       :inset-inline-end   :unset
                       :transform          "translate(-100%, -100%)"}

   ;; north
   :north-inside {:position           :absolute
                  :inset-block-start  0
                  :inset-block-end    :unset
                  :inset-inline-start :50%
                  :inset-inline-end   :unset
                  :transform          "translate(-50%, 0)"}
   :north {:position           :absolute
           :inset-block-start  0
           :inset-block-end    :unset
           :inset-inline-start :50%
           :inset-inline-end   :unset
           :transform          "translate(-50%, -50%)"}
   :north-outside {:position           :absolute
                   :inset-block-start  0
                   :inset-block-end    :unset
                   :inset-inline-start :50%
                   :inset-inline-end   :unset
                   :transform          "translate(-50%, -100%)"}

   ;; east
   :east-inside {:position           :absolute
                 :inset-block-start  :50%
                 :inset-block-end    :unset
                 :inset-inline-end   0
                 :inset-inline-start :unset
                 :transform          "translate(0, -50%)"}
   :east {:position           :absolute
          :inset-block-start  :50%
          :inset-block-end    :unset
          :inset-inline-end   0
          :inset-inline-start :unset
          :transform          "translate(50%, -50%)"}
   :east-outside {:position           :absolute
                  :inset-block-start  :50%
                  :inset-block-end    :unset
                  :inset-inline-end   0
                  :inset-inline-start :unset
                  :transform          "translate(100%, -50%)"}

   ;; northeast
   :northeast-inside  {:position           :absolute
                       :inset-block-start  0
                       :inset-block-end    :unset
                       :inset-inline-end   0
                       :inset-inline-start :unset
                       :transform          "translate(0, 0)"}
   :northeast         {:position           :absolute
                       :inset-block-start  0
                       :inset-block-end    :unset
                       :inset-inline-end   0
                       :inset-inline-start :unset
                       :transform          "translate(50%, -50%)"}
   :northeast-outside {:position           :absolute
                       :inset-block-start  0
                       :inset-block-end    :unset
                       :inset-inline-end   0
                       :inset-inline-start :unset
                       :transform          "translate(100%, -100%)"}

   ;; southwest
   :southwest-inside  {:position           :absolute
                       :inset-block-end    0
                       :inset-block-start  :unset
                       :inset-inline-start 0
                       :inset-inline-end   :unset
                       :transform          "translate(0, 0)"}
   :southwest         {:position           :absolute
                       :inset-block-end    0
                       :inset-block-start  :unset
                       :inset-inline-start 0
                       :inset-inline-end   :unset
                       :transform          "translate(-50%, 50%)"}
   :southwest-outside {:position           :absolute
                       :inset-block-end    0
                       :inset-block-start  :unset
                       :inset-inline-start 0
                       :inset-inline-end   :unset
                       :transform          "translate(-100%, 100%)"}

   ;; south
   :south-inside {:position           :absolute
                  :inset-block-end    0
                  :inset-block-start  :unset
                  :inset-inline-start :50%
                  :inset-inline-end   :unset
                  :transform          "translate(-50%, 0)"}
   :south {:position           :absolute
           :inset-block-end    0
           :inset-block-start  :unset
           :inset-inline-start :50%
           :inset-inline-end   :unset
           :transform          "translate(-50%, 50%)"}
   :south-outside {:position           :absolute
                   :inset-block-end    0
                   :inset-block-start  :unset
                   :inset-inline-start :50%
                   :inset-inline-end   :unset
                   :transform          "translate(-50%, 100%)"}

   ;; southeast
   :southeast-inside  {:position           :absolute
                       :inset-block-end    0
                       :inset-block-start  :unset
                       :inset-inline-end   0
                       :inset-inline-start :unset
                       :transform          "translate(0, 0)"}
   :southeast         {:position           :absolute
                       :inset-block-end    0
                       :inset-block-start  :unset
                       :inset-inline-end   0
                       :inset-inline-start :unset
                       :transform          "translate(50%, 50%)"}
   :southeast-outside {:position           :absolute
                       :inset-block-end    0
                       :inset-block-start  :unset
                       :inset-inline-end   0
                       :inset-inline-start :unset
                       :transform          "translate(100%, 100%)"}

   ;; west
   :west-inside  {:position           :absolute
                  :inset-block-start  :50%
                  :inset-block-end    :unset
                  :inset-inline-start 0
                  :inset-inline-end   :unset
                  :transform          "translate(0, -50%)"}
   :west         {:position           :absolute
                  :inset-block-start  :50%
                  :inset-block-end    :unset
                  :inset-inline-start 0
                  :inset-inline-end   :unset
                  :transform          "translate(-50%, -50%)"}
   :west-outside {:position           :absolute
                  :inset-block-start  :50%
                  :inset-block-end    :unset
                  :inset-inline-start 0
                  :inset-inline-end   :unset
                  :transform          "translate(-100%, -50%)"}

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
   :slow          {:transition-duration :$slow}
   :xslow         {:transition-duration :$extra-slow}
   :xxslow        {:transition-duration :$super-slow}
   :xxxslow       {:transition-duration :$ultra-slow}


   ;; Surfaces, buttons, containers 2D
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

   :sharp         {:border-radius 0}
   :pill          {:border-radius :9999px}


   ;; Surfaces, buttons, containers 3D
   ;; ------------------------------------------------------
   :debossed      {:text-shadow "0 1px 2px hsl(0deg 0% 100% / 55%), 0 -1px 2px hsl(0deg 0% 0% / 27%)"}
   :embossed      {:text-shadow "0 -1px 2px hsl(0deg 0% 100% / 55%), 0 1px 2px hsl(0deg 0% 0% / 27%)"}
   :convex        {:background-image "linear-gradient(180deg, hsl(0deg 0% 100% / 20%), transparent, hsl(0deg 0% 0% / 15%))"}
   :concave       {:background-image "linear-gradient(180deg, hsl(0deg 0% 0% / 15%), transparent, hsl(0deg 0% 100% / 20%))"}
   :elevated      {:box-shadow :$elevated}
   :elevated-0    {:box-shadow :$elevated-0}
   :elevated-1    {:box-shadow :$elevated-1}
   :elevated-2    {:box-shadow :$elevated-2}
   :elevated-3    {:box-shadow :$elevated-3}
   :elevated-4    {:box-shadow :$elevated-4}
   :elevated-5    {:box-shadow :$elevated-5}


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

(def utility-class-ks
  (mapcat util/kwargs-keys
          [combo-flex-utility-classes
           base-classes
           override-classes
           ui-theming-classes]))

(def utility-classes
  (apply deep-merge
         (map #(apply hash-map %)
              [combo-flex-utility-classes
               base-classes
               override-classes
               ui-theming-classes])))
