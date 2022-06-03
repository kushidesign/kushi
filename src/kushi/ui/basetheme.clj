(ns ^:dev/always kushi.ui.basetheme
 (:require
   [kushi.utils :as util :refer [keyed]]
   [kushi.ui.tokens :refer [global-tokens alias-tokens]]
   [par.core    :refer [? !? ?+ !?+]]
   [kushi.ui.util :refer [compound-override]]))

(def css-reset
  [["*:where(:not(html, iframe, canvas, img, svg, video):not(svg *, symbol *))"]
   {:all     :unset
    :display :revert}

   ["*" "*::before" "*::after"]
   {:box-sizing :border-box}

   ["a" "button"]
   {:cursor :revert}

   ["ol" "ul" "menu"]
   {:list-style :none }

   ["img"]
   {:max-width :100% }

   ["table"]
   {:border-collapse :collapse}

   ["textarea"]
   {:white-space :revert}

   ["meter"]
   {:-webkit-appearance :revert
    :appearance         :revert}

   ["::placeholder"]
   {:color :unset}

   [":where([hidden])"]
   {:display :none}

   [":where([contenteditable])"]
   {:-moz-user-modify    :read-write
    :-webkit-user-modify :read-write
    :overflow-wrap       :break-word
    :-webkit-line-break  :after-white-space}

   [":where([draggable='true'])"]
   {:-webkit-user-drag :element}

   ;; reverting this back to normal, for now
   ["input" "textarea" "select" "p"]
   {:all :revert}])


(defn flex-atomic [fd]
  (reduce
   (fn [acc [sh expanded]]
     (assoc acc
            (keyword (str "flex-"
                          (let [fdn* (-> fd name)
                                fdn (if (= fdn* "column") "col" "row")]
                            fdn)
                          "-"
                          (name sh)))
            {:flex-direction fd
             :justify-content expanded
             :display :flex}))
   {}
   {:c :center
    :fs :flex-start
    :fe :flex-end
    :sb :space-between
    :sa :space-around
    :se :space-evenly}))

(def flex-utility-classes
  (merge
   (flex-atomic :row)
   (flex-atomic :column)))

(def base-classes
  (merge

   ;; Combinatorial flexbox utilities
   ;; ------------------------------------------------------
   flex-utility-classes
   {
    ;; Visual debugging utilities
    ;; ------------------------------------------------------
    :debug-grid          {:background "transparent url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAgAAAAICAYAAADED76LAAAAFElEQVR4AWPAC97/9x0eCsAEPgwAVLshdpENIxcAAAAASUVORK5CYII=) repeat top left"}
    :debug-grid-16       {:background "transparent url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAMklEQVR4AWOgCLz/b0epAa6UGuBOqQHOQHLUgFEDnAbcBZ4UGwDOkiCnkIhdgNgNxAYAiYlD+8sEuo8AAAAASUVORK5CYII=) repeat top left "}
    :debug-grid-8-solid  {:background "white url(data:image/gif;base64,R0lGODdhCAAIAPEAAADw/wDx/////wAAACwAAAAACAAIAAACDZQvgaeb/lxbAIKA8y0AOw==) repeat top left"}
    :debug-grid-16-solid {:background "white url(data:image/gif;base64,R0lGODdhEAAQAPEAAADw/wDx/xXy/////ywAAAAAEAAQAAACIZyPKckYDQFsb6ZqD85jZ2+BkwiRFKehhqQCQgDHcgwEBQA7) repeat top left"}
    :bordered          {:border-color :silver
                        :border-style :solid
                        :border-width "1px"}
    :outlined          {:outline-color :silver
                        :outline-style :solid
                        :outline-width "1px"}

    ;; Combinatorial absolute positioning utilities
    ;; ------------------------------------------------------
    :fixed-fill        {:position :fixed
                        :top      0
                        :right    0
                        :bottom   0
                        :left     0}

    :absolute-fill     {:position :absolute
                        :top      0
                        :right    0
                        :bottom   0
                        :left     0}

    :absolute-centered {:position  :absolute
                        :top       "50%"
                        :left      "50%"
                        :transform "translate(-50%, -50%)"}


    ;; Combinatorial flexbox utilities
    ;; ------------------------------------------------------
    :flex-row-centered   {:display         :flex
                          :justify-content :center
                          :align-content   :center
                          :justify-items   :center
                          :align-items     :center}

    :flex-col-centered   {:display         :flex
                          :flex-direction  :col
                          :justify-content :center
                          :align-content   :center
                          :justify-items   :center
                          :align-items     :center}


    ;; Surfaces, buttons, containers
    ;; ------------------------------------------------------
    :bgi-cover         {:background-position "center center"
                        :background-repeat   :no-repeat
                        :width               "100%"}

    :bgi-contain       {:background-position "center center"
                        :background-size     :contain
                        :background-repeat   :no-repeat
                        :width               "100%"
                        :height              "100%"}


    ;; Combinatorial transition utility
    ;; ------------------------------------------------------
    :transition        {:transition-property        :all
                        :transition-timing-function "cubic-bezier(0, 0, 1, 1)"
                        :transition-duration        :200ms}}))

(def override-classes
  (merge
   {
    ;; General
    ;; ------------------------------------------------------
    :hidden            {:visibility :hidden}
    :visible           {:visibility :visible}
    :collapse          {:visibility :collapse}


    ;; Content
    ;; ------------------------------------------------------
    :content-blank     {:content "\"\""}


    ; Cursor
    ;; ------------------------------------------------------
    :pointer           {:cursor :pointer}


    ; Position
    ;; ------------------------------------------------------
    :absolute          {:position :absolute}
    :fixed             {:position :fixed}
    :relative          {:position :relative}


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
    :thin              {:fw :--text-thin}
    :extra-light       {:fw :--text-extra-light}
    :light             {:fw :--text-light}
    :normal            {:fw :--text-normal}
    :wee-bold          {:fw :--text-wee-bold}
    :semi-bold         {:fw :--text-semi-bold}
    :bold              {:fw :--text-bold}
    :extra-bold        {:fw :--text-extra-bold}
    :heavy             {:fw :--text-heavy}


    ;; Animations
    ;; ------------------------------------------------------
    :instant           {:transition-duration :--duration-instant }
    :fast              {:transition-duration :--duration-fast }
    :slow              {:transition-duration :--duration-slow }
    :extra-slow        {:transition-duration :--duration-extra-slow }
    :super-slow        {:transition-duration :--duration-super-slow }
    :ultra-slow        {:transition-duration :--duration-ultra-slow }


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
  (merge base-classes override-classes))

(def component-tokens
 {:--kushi-collapse-transition-duration :--duration-slow})

(def tokens
  {:global global-tokens
   :alias (merge alias-tokens component-tokens)})

(def font-loading
  {
  ;;  :use-system-font-stack?  false
  ;;  :use-default-code-font-family? false
  ;;  :use-default-primary-font-family? false
  ;;  :google-fonts [{:family "Public Sans"
  ;;                  :styles {:normal [100] :italic [300]}}]
   :google-fonts* ["Fira Code" "Inter"]})

(def ui
  {:kushi {:button      {:default (merge (:secondary override-classes)
                                         {:fw :--text-wee-bold
                                          :ff :--primary-font-family})
            ;; :disabled  {:color :turquoise}
            ;; :primary   (:primary override-classes)
            ;; :link      {:td        :underline
            ;;             :bgc       :transparent
            ;;             :hover:bgc :transparent}
            ;; :secondary {:bgc        :--gray100
            ;;             :hover:bgc  :--gray200
            ;;             :color      :--primary}
            ;; :tertiary  {:bgc       :transparent
            ;;             :hover:bgc :--gray100}
            ;; :minimal   (:minimal override-classes)
            ;; :ghosted   (:ghosted override-classes)
                         }
           :tag         {:default  {:c :--primary}
                         :primary  (:primary override-classes)
                         :positive (:positive override-classes)
                         :negative (:negative override-classes)
                         :warning  (:warning override-classes)}
            ;; stuff like this needs to be in sync with the var name it is creating
           }})

(def global {:font-family      :--sans-serif-stack
             :background-color :blanched-almond
             :color            :--primary})

;; Minimal theming for test cases
(def min-config
  {:css-reset       css-reset
   :tokens          {:global {:--black           :#3d3d3d
                              :--white           :#fff
                              :--gray100         :#EEEEEE
                              :--gray200         :#E2E2E2
                              :--text-wee-bold   500
                              :--text-extra-bold 500
                              :--text-mini       :0.75rem}

                     :alias  {:--primary   :--black
                              :--primary-b :--white}}

  ;; :base-classes              {} #_(merge flex-utility-classes)
  ;;  :utility-classes-base     {:base }
  ;;  :utility-classes-override {:absolute-fill {:position :absolute
  ;;                                             :inset    0}}
   
   :utility-classes {:extra-bold {:fw :--text-extra-bold}
                     :mini       {:fw :--text-mini}
                     :pill       {:border-radius "9999px"}}

   :font-loading    {:google-fonts*          ["Inter"]
                     :use-system-font-stack? false}

;;  :theme            {:global {}
;;                     :components {:kushi {}}}
;;  :global           {:font-family      :--sans-serif-stack
;;                     :background-color :blanched-almond
;;                     :color            :--primary}
;;  :global-dark      {:color            :--primary-b}
   
   :ui              {:kushi {:button {:default (merge (:secondary override-classes)
                                                      {:fw :--text-wee-bold
                                                       :ff :--primary-font-family})}
                             :tag    {:default {:c :--primary}}}}

;;  :ui-dark          {:kushi {}}
   })


;; Minimal theming for test cases end

(def base-theme-map
  (keyed css-reset utility-classes tokens font-loading global ui)
  #_min-config)
