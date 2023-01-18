(ns kushi.ui.utility
  (:require
   [kushi.utils :refer [deep-merge]]
   [kushi.utils :as util]))

(def combo-flex-utility-classes
  ;; Combinatorial flexbox utilities
  ;; ------------------------------------------------------
  [:flex-row-c        {:flex-direction  :row
                       :justify-content :center
                       :display         :flex}
   :flex-row-sa       {:flex-direction  :row
                       :justify-content :space-around
                       :display         :flex}
   :flex-col-se       {:flex-direction  :column
                       :justify-content :space-evenly
                       :display         :flex}
   :flex-row-fs       {:flex-direction  :row
                       :justify-content :flex-start
                       :display         :flex}
   :flex-col-c        {:flex-direction  :column
                       :justify-content :center
                       :display         :flex}
   :flex-row-se       {:flex-direction  :row
                       :justify-content :space-evenly
                       :display         :flex}
   :flex-col-fe       {:flex-direction  :column
                       :justify-content :flex-end
                       :display         :flex}
   :flex-col-fs       {:flex-direction  :column
                       :justify-content :flex-start
                       :display         :flex}
   :flex-row-fe       {:flex-direction  :row
                       :justify-content :flex-end
                       :display         :flex}
   :flex-col-sa       {:flex-direction  :column
                       :justify-content :space-around
                       :display         :flex}
   :flex-col-sb       {:flex-direction  :column
                       :justify-content :space-between
                       :display         :flex}
   :flex-row-sb       {:flex-direction  :row
                       :justify-content :space-between
                       :display         :flex}
   :flex-row-centered {:display         :flex
                       :justify-content :center
                       :align-content   :center
                       :justify-items   :center
                       :align-items     :center}
   :flex-col-centered {:display         :flex
                       :flex-direction  :col
                       :justify-content :center
                       :align-content   :center
                       :justify-items   :center
                       :align-items     :center}])


(def base-classes
  [
    ;; Visual debugging utilities
    ;; ------------------------------------------------------
   :debug-grid            {:background "transparent url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAgAAAAICAYAAADED76LAAAAFElEQVR4AWPAC97/9x0eCsAEPgwAVLshdpENIxcAAAAASUVORK5CYII=) repeat top left"}
   :debug-grid-16         {:background "transparent url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAMklEQVR4AWOgCLz/b0epAa6UGuBOqQHOQHLUgFEDnAbcBZ4UGwDOkiCnkIhdgNgNxAYAiYlD+8sEuo8AAAAASUVORK5CYII=) repeat top left "}
   :debug-grid-8-solid    {:background "white url(data:image/gif;base64,R0lGODdhCAAIAPEAAADw/wDx/////wAAACwAAAAACAAIAAACDZQvgaeb/lxbAIKA8y0AOw==) repeat top left"}
   :debug-grid-16-solid   {:background "white url(data:image/gif;base64,R0lGODdhEAAQAPEAAADw/wDx/xXy/////ywAAAAAEAAQAAACIZyPKckYDQFsb6ZqD85jZ2+BkwiRFKehhqQCQgDHcgwEBQA7) repeat top left"}
   :bordered              {:border-color :currentColor
                           :border-style :solid
                           :border-width "1px"}
   :wireframe             {:outline-color :silver
                           :outline-style :solid
                           :outline-width "1px"}

    ;; Combinatorial absolute positioning utilities
    ;; ------------------------------------------------------
   :fixed-fill            {:position :fixed
                           :top      0
                           :right    0
                           :bottom   0
                           :left     0}

   :absolute-fill         {:position :absolute
                           :top      0
                           :right    0
                           :bottom   0
                           :left     0}

   :absolute-centered     {:position  :absolute
                           :top       "50%"
                           :left      "50%"
                           :transform "translate(-50%, -50%)"}

   :absolute-inline-start {:position          :absolute
                           :inset-inline-end  "100%"
                           :inset-block-start "50%"
                           :transform         "translateY(-50%)"}

   :absolute-inline-end   {:position           :absolute
                           :inset-inline-start "100%"
                           :inset-block-start  "50%"
                           :transform          "translateY(-50%)"}

   :absolute-block-start  {:position          :absolute
                           :inset-block-end   "100%"
                           :inset-block-start "50%"
                           :transform         "translateX(-50%)"}

   :absolute-block-end    {:position           :absolute
                           :inset-block-start  "100%"
                           :inset-inset-start  "50%"
                           :transform          "translateX(-50%)"}

  ;; northwest
   :northwest-inside {:position :absolute :inset-block-start 0 :inset-inline-start 0  :transform "translate(0, 0)"}
   :northwest {:position :absolute :inset-block-start 0 :inset-inline-start 0  :transform "translate(-50%, -50%)"}
   :northwest-outside {:position :absolute :inset-block-start 0 :inset-inline-start 0  :transform "translate(-100%, -100%)"}

  ;; north
   :north-inside {:position :absolute :inset-block-start 0 :inset-inline-start :50%  :transform "translate(-50%, 0)"}
   :north {:position :absolute :inset-block-start 0 :inset-inline-start :50%  :transform "translate(-50%, -50%)"}
   :north-outside {:position :absolute :inset-block-start 0 :inset-inline-start :50%  :transform "translate(-50%, -100%)"}

  ;; east
   :east-inside {:position :absolute :inset-block-start :50% :inset-inline-end 0  :transform "translate(0, -50%)"}
   :east {:position :absolute :inset-block-start :50% :inset-inline-end 0  :transform "translate(50%, -50%)"}
   :east-outside {:position :absolute :inset-block-start :50% :inset-inline-end 0  :transform "translate(100%, -50%)"}

  ;; northeast
   :northeast-inside {:position :absolute :inset-block-start 0 :inset-inline-end 0  :transform "translate(0, 0)"}
   :northeast {:position :absolute :inset-block-start 0 :inset-inline-end 0  :transform "translate(50%, -50%)"}
   :northeast-outside {:position :absolute :inset-block-start 0 :inset-inline-end 0  :transform "translate(100%, -100%)"}

  ;; southwest
   :southwest-inside {:position :absolute :inset-block-end 0 :inset-inline-start 0  :transform "translate(0, 0)"}
   :southwest {:position :absolute :inset-block-end 0 :inset-inline-start 0  :transform "translate(-50%, 50%)"}
   :southwest-outside {:position :absolute :inset-block-end 0 :inset-inline-start 0  :transform "translate(-100%, 100%)"}

  ;; south
   :south-inside {:position :absolute :inset-block-end 0 :inset-inline-start :50%  :transform "translate(-50%, 0)"}
   :south {:position :absolute :inset-block-end 0 :inset-inline-start :50%  :transform "translate(-50%, 50%)"}
   :south-outside {:position :absolute :inset-block-end 0 :inset-inline-start :50%  :transform "translate(-50%, 100%)"}

  ;; southeast
   :southeast-inside {:position :absolute :inset-block-end 0 :inset-inline-end 0  :transform "translate(0, 0)"}
   :southeast {:position :absolute :inset-block-end 0 :inset-inline-end 0  :transform "translate(50%, 50%)"}
   :southeast-outside {:position :absolute :inset-block-end 0 :inset-inline-end 0  :transform "translate(100%, 100%)"}

  ;; west
   :west-inside {:position :absolute :inset-block-start :50% :inset-inline-start 0  :transform "translate(0, -50%)"}
   :west {:position :absolute :inset-block-start :50% :inset-inline-start 0  :transform "translate(-50%, -50%)"}
   :west-outside {:position :absolute :inset-block-start :50% :inset-inline-start 0  :transform "translate(-100%, -50%)"}


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
                           :transition-timing-function "cubic-bezier(0, 0, 1, 1)"
                           :transition-duration        :200ms}])

(def override-classes
  [;; General
   ;; ------------------------------------------------------
   :hidden        {:visibility :hidden}
   :visible       {:visibility :visible}
   :collapse      {:visibility :collapse}


   ;; Content
   ;; ------------------------------------------------------
   :content-blank {:content "\"\""}
   :open-in-new   {:content "url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' height='24px' viewBox='0 0 24 24' width='24px' fill='%23000000'%3E%3Cpath d='M0 0h24v24H0V0z' fill='none'/%3E%3Cpath d='M19 19H5V5h7V3H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2v-7h-2v7zM14 3v2h3.59l-9.83 9.83 1.41 1.41L19 6.41V10h2V3h-7z'/%3E%3C/svg%3E\")"}

   ;; Cursor
   ;; ------------------------------------------------------
   :pointer       {:cursor :pointer}

   ;; Position
   ;; ------------------------------------------------------
   :absolute      {:position :absolute}
   :fixed         {:position :fixed}
   :relative      {:position :relative}

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
   :sans-serif    {:font-family :$serif-font-stack}
   :italic        {:font-style :italic}
   :oblique       {:font-style :oblique}

   ;; Text capitalization
   ;; ------------------------------------------------------
   :uppercase     {:text-transform :uppercase}
   :lowercase     {:text-transform :lowercase}
   :capitalize    {:text-transform :capitalize}

   ;; Type sizing
   ;; ------------------------------------------------------
   :xxxsmall      {:fs :$text-xxxsmall}
   :xxsmall       {:fs :$text-xxsmall}
   :xsmall        {:fs :$text-xsmall}
   :small         {:fs :$text-small}
   :medium        {:fs :$text-medium}
   :large         {:fs :$text-large}
   :xlarge        {:fs :$text-xlarge}
   :xxlarge       {:fs :$text-xxlarge}
   :xxxlarge      {:fs :$text-xxxlarge}


   ;; Type weight
   ;; ------------------------------------------------------
   :thin          {:fw :$text-thin}
   :extra-light   {:fw :$text-extra-light}
   :light         {:fw :$text-light}
   :normal        {:fw :$text-normal}
   :wee-bold      {:fw :$text-wee-bold}
   :semi-bold     {:fw :$text-semi-bold}
   :bold          {:fw :$text-bold}
   :extra-bold    {:fw :$text-extra-bold}
   :heavy         {:fw :$text-heavy}


   ;; Tracking (aka letter-spacing)
   ;; ------------------------------------------------------
   :xxxtight      {:letter-spacing :$text-xxxtight}
   :xxtight       {:letter-spacing :$text-xxtight}
   :xtight        {:letter-spacing :$text-xtight}
   :tight         {:letter-spacing :$text-tight}
   :loose         {:letter-spacing :$text-loose}
   :xloose        {:letter-spacing :$text-xloose}
   :xxloose       {:letter-spacing :$text-xxloose}
   :xxxloose      {:letter-spacing :$text-xxxloose}


   ;; Animations
   ;; ------------------------------------------------------
   :instant       {:transition-duration :$duration-instant}
   :fast          {:transition-duration :$duration-fast}
   :slow          {:transition-duration :$duration-slow}
   :extra-slow    {:transition-duration :$duration-extra-slow}
   :super-slow    {:transition-duration :$duration-super-slow}
   :ultra-slow    {:transition-duration :$duration-ultra-slow}


   ;; Surfaces, buttons, containers 2D
   ;; ------------------------------------------------------
   :rounded       {:border-radius :$rounded}
   :sharp         {:border-radius 0}
   :pill          {:border-radius :9999px}


   ;; Surfaces, buttons, containers 3D
   ;; ------------------------------------------------------
   :debossed      {:text-shadow "0 1px 2px hsl(0deg 0% 100% / 55%), 0 -1px 2px hsl(0deg 0% 0% / 27%)"}
   :embossed      {:text-shadow "0 -1px 2px hsl(0deg 0% 100% / 55%), 0 1px 2px hsl(0deg 0% 0% / 27%)"}
   :convex        {:background-image "linear-gradient(180deg, hsl(0deg 0% 100% / 20%), transparent, hsl(0deg 0% 0% / 15%))"}
   :concave       {:background-image "linear-gradient(180deg, hsl(0deg 0% 0% / 15%), transparent, hsl(0deg 0% 100% / 20%))"}
   :elevated      {:box-shadow    :$elevated}


   ;; Controls
   ;; ------------------------------------------------------
   :disabled      {:o      :45%!important
                   :cursor :not-allowed!important}])

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
