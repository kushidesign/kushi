(ns kushi.ui.utility)

(def combo-flex-utility-classes
  ;; Combinatorial flexbox utilities
  ;; ------------------------------------------------------
  {:flex-row-c        {:flex-direction  :row
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
                       :align-items     :center}})


(def base-classes
  {
    ;; Visual debugging utilities
    ;; ------------------------------------------------------
   :debug-grid          {:background "transparent url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAgAAAAICAYAAADED76LAAAAFElEQVR4AWPAC97/9x0eCsAEPgwAVLshdpENIxcAAAAASUVORK5CYII=) repeat top left"}
   :debug-grid-16       {:background "transparent url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAMklEQVR4AWOgCLz/b0epAa6UGuBOqQHOQHLUgFEDnAbcBZ4UGwDOkiCnkIhdgNgNxAYAiYlD+8sEuo8AAAAASUVORK5CYII=) repeat top left "}
   :debug-grid-8-solid  {:background "white url(data:image/gif;base64,R0lGODdhCAAIAPEAAADw/wDx/////wAAACwAAAAACAAIAAACDZQvgaeb/lxbAIKA8y0AOw==) repeat top left"}
   :debug-grid-16-solid {:background "white url(data:image/gif;base64,R0lGODdhEAAQAPEAAADw/wDx/xXy/////ywAAAAAEAAQAAACIZyPKckYDQFsb6ZqD85jZ2+BkwiRFKehhqQCQgDHcgwEBQA7) repeat top left"}
   :bordered            {:border-color :silver
                         :border-style :solid
                         :border-width "1px"}
   :outlined            {:outline-color :silver
                         :outline-style :solid
                         :outline-width "1px"}

    ;; Combinatorial absolute positioning utilities
    ;; ------------------------------------------------------
   :fixed-fill          {:position :fixed
                         :top      0
                         :right    0
                         :bottom   0
                         :left     0}

   :absolute-fill       {:position :absolute
                         :top      0
                         :right    0
                         :bottom   0
                         :left     0}

   :absolute-centered   {:position  :absolute
                         :top       "50%"
                         :left      "50%"
                         :transform "translate(-50%, -50%)"}





    ;; Surfaces, buttons, containers
    ;; ------------------------------------------------------
   :bgi-cover           {:background-position "center center"
                         :background-repeat   :no-repeat
                         :width               "100%"}

   :bgi-contain         {:background-position "center center"
                         :background-size     :contain
                         :background-repeat   :no-repeat
                         :width               "100%"
                         :height              "100%"}


    ;; Combinatorial transition utility
    ;; ------------------------------------------------------
   :transition          {:transition-property        :all
                         :transition-timing-function "cubic-bezier(0, 0, 1, 1)"
                         :transition-duration        :200ms}})

(def override-classes
  (merge
   {;; General
    ;; ------------------------------------------------------
    :hidden            {:visibility :hidden}
    :visible           {:visibility :visible}
    :collapse          {:visibility :collapse}


    ;; Content
    ;; ------------------------------------------------------
    :content-blank     {:content "\"\""}


    ;; Cursor
    ;; ------------------------------------------------------
    :pointer           {:cursor :pointer}


    ;; Position
    ;; ------------------------------------------------------
    :absolute          {:position :absolute}
    :fixed             {:position :fixed}
    :relative          {:position :relative}

    ;; Display
    ;; ------------------------------------------------------
    :block             {:display :block}
    :inline            {:display :flex}
    :inline-block      {:display :inline-block}
    :flex              {:display :inline}
    :inline-flex       {:display :inline-flex}
    :grid              {:display :grid}
    :inline-grid       {:display :inline-grid}
    :flow-root         {:display :flow-root}
    :contents          {:display :contents}

    ;; Type styling
    ;; ------------------------------------------------------
    :italic            {:font-style :italic}
    :oblique           {:font-style :oblique}

    ;; Text capitalization
    ;; ------------------------------------------------------
    :uppercase         {:text-transform :uppercase}
    :lowercase         {:text-transform :lowercase}
    :capitalize        {:text-transform :capitalize}

    ;; Type sizing
    ;; ------------------------------------------------------
    :mini              {:fs :--text-mini}
    :xxsmall           {:fs :--text-xxsmall}
    :xsmall            {:fs :--text-xsmall}
    :small             {:fs :--text-small}
    :medium            {:fs :--text-medium}
    :large             {:fs :--text-large}
    :xlarge            {:fs :--text-xlarge}
    :xxlarge           {:fs :--text-xxlarge}
    :huge              {:fs :--text-huge}


    ;; Type weight
    ;; ------------------------------------------------------
    :thin              {:fw                               :--text-thin
                        ">.kushi-radio-input:outline-width" :--input-border-weight-thin
                        ">.kushi-checkbox-input:bw"         :--input-border-weight-thin}
    :extra-light       {:fw                               :--text-extra-light
                        ">.kushi-radio-input:outline-width" :--input-border-weight-extra-light
                        ">.kushi-checkbox-input:bw"         :--input-border-weight-extra-light}
    :light             {:fw                               :--text-light
                        ">.kushi-radio-input:outline-width" :--input-border-weight-light
                        ">.kushi-checkbox-input:bw"         :--input-border-weight-light}
    :normal            {:fw                               :--text-normal
                        ">.kushi-radio-input:outline-width" :--input-border-weight-normal
                        ">.kushi-checkbox-input:bw"         :--input-border-weight-normal}
    :wee-bold          {:fw                               :--text-wee-bold
                        ">.kushi-radio-input:outline-width" :--input-border-weight-wee-bold
                        ">.kushi-checkbox-input:bw"         :--input-border-weight-wee-bold}
    :semi-bold         {:fw                               :--text-semi-bold
                        ">.kushi-radio-input:outline-width" :--input-border-weight-semi-bold
                        ">.kushi-checkbox-input:bw"         :--input-border-weight-semi-bold}
    :bold              {:fw                               :--text-bold
                        ">.kushi-radio-input:outline-width" :--input-border-weight-bold
                        ">.kushi-checkbox-input:bw"         :--input-border-weight-bold}
    :extra-bold        {:fw                               :--text-extra-bold
                        ">.kushi-radio-input:outline-width" :--input-border-weight-extra-bold
                        ">.kushi-checkbox-input:bw"         :--input-border-weight-extra-bold}
    :heavy             {:fw                               :--text-heavy
                        ">.kushi-radio-input:outline-width" :--input-border-weight-heavy
                        ">.kushi-checkbox-input:bw"         :--input-border-weight-heavy}


    ;; Animations
    ;; ------------------------------------------------------
    :instant           {:transition-duration :--duration-instant}
    :fast              {:transition-duration :--duration-fast}
    :slow              {:transition-duration :--duration-slow}
    :extra-slow        {:transition-duration :--duration-extra-slow}
    :super-slow        {:transition-duration :--duration-super-slow}
    :ultra-slow        {:transition-duration :--duration-ultra-slow}


    ;; Surfaces, buttons, containers
    ;; ------------------------------------------------------
    :rounded           {:border-radius :--rounded}
    :sharp             {:border-radius 0}
    :elevated          {:box-shadow :--elevated}


    ;; Buttons, tags, & labels
    ;; ------------------------------------------------------
    :primary           {:c         :--primary-b
                        :bgc       :--primary
                        :hover:bgc :--gray400}
    :secondary         {:bgc       :--gray100
                        :hover:bgc :--gray200
                        :color     :--primary}
    :tertiary          {:bgc       :transparent
                        :hover:bgc :--gray100}
    :ghosted           {:bw        :1px
                        :bs        :solid
                        :bc        :--black
                        :bgc       :transparent
                        :hover:bgc :transparent
                        :hover:o   0.6}
    :positive          {:c   :--positive
                        :bgc :--positive50}
    :warning           {:c   :--warning600
                        :bgc :--warning100}
    :negative          {:c   :--negative
                        :bgc :--negative50}
    :positive-inverted {:c   :--primary-b
                        :bgc :--positive}
    :warning-inverted  {:c   :--primary-b
                        :bgc :--warning}
    :negative-inverted {:c   :--primary-b
                        :bgc :--negative}
    :minimal           {:bgc :transparent
                        :p   0}
    :pill              {:border-radius "9999px"}


    ;; Buttons
    ;; ------------------------------------------------------
    :link              {:>span:p   0
                        :td        :u
                        :tup       :u
                        :bgc       :transparent
                        :hover:bgc :transparent
                        :hover:o   0.7}

    ;; Controls
    ;; ------------------------------------------------------
    :disabled          {:o      :40%
                        :cursor :not-allowed}}))

(def utility-classes
  (merge combo-flex-utility-classes base-classes override-classes))
