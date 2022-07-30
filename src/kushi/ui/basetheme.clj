(ns ^:dev/always kushi.ui.basetheme
 (:require
   [kushi.utils :as util :refer [keyed]]
   [kushi.ui.tokens :refer [global-tokens alias-tokens]]
   [kushi.ui.utility :refer [utility-classes override-classes]]
   [kushi.parstub    :refer [? !? ?+ !?+]]))

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

                         :primary {:hover:bgc :--gray400}
                         :ghosted {:hover:o   0.6}
                         :secondary {:hover:bgc  :--gray200}
                         :tertiary  {:bgc       :transparent
                                     :hover:bgc :--gray100}

                         ;; :link      {:td        :underline
                         ;;             :bgc       :transparent
                         ;;             :hover:bgc :transparent}
                         ;; :minimal   (:minimal override-classes)
                         ;; :ghosted   (:ghosted override-classes)

                         }
           :tag         {:default  {:c :--primary}
                         :primary  (:primary override-classes)
                         :positive (:positive override-classes)
                         :negative (:negative override-classes)
                         :warning  (:warning override-classes)} }})

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
