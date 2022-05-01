(ns ^:dev/always kushi.ui.basetheme
 (:require
   [kushi.utils :as util :refer [keyed]]
   [kushi.ui.tokens :refer [global-tokens alias-tokens]]
   [par.core    :refer [? !? ?+ !?+]]
   [kushi.ui.util :refer [compound-override]]))


;; Minimal theming for test cases

(def global-tokens-min
  {:--white       :#fff000
   :--text-light  300
   :--text-bold   700
   :--text-mini   :0.75rem
   :--text-medium :1rem
   :--text-xlarge     :1.5rem
   })

(def alias-tokens-min
  {:--primary-b :blue
   :--primary-font-family "Inter, system, sans"})

(def overrides-min
  (merge
   {
    ;; Type sizing
    :mini   {:fs :--text-mini}
    :medium {:fs :--text-medium}
    :xlarge {:fs :--text-xlarge}

    ;; Type weight
    :light  {:fw :--text-light}
    :bold   {:fw :--text-bold}}))

(def font-loading-min
  {:google-fonts* ["Inter"]})

;; Minimal theming for test cases end

(def overrides
  (merge
   #_(compound-override
    [
    ;;  [:&_.kushi-label-text+.kushi-icon:mis
    ;;   :&_.kushi-icon+.kushi-label-text:mis]
     :fs
     #_[:&.kushi-icon&_*:fs
      :&_.kushi-icon&_*:fs]]
    {:mini   [#_:--mui-icon-mini-margin-inline :--text-mini #_:--mui-icon-mini-font-size]
     :small  [#_:--mui-icon-margin-inline :--text-small #_:--mui-icon-small-font-size]
     :medium [#_:--mui-icon-margin-inline :--text-medium #_:--mui-icon-medium-font-size]
     :large  [#_:--mui-icon-margin-inline :--text-large #_:--mui-icon-large-font-size]
     :xlarge  [#_:--mui-icon-margin-inline :--text-xlarge #_:--mui-icon-large-font-size]
     :huge   [#_:--mui-icon-margin-inline :--text-huge #_:--mui-icon-huge-font-size]})

   {
    ;; Type sizing
    :mini   {:fs :--text-mini}
    :small  {:fs :--text-small}
    :medium {:fs :--text-medium}
    :large  {:fs :--text-large}
    :xlarge {:fs :--text-xlarge}
    :huge   {:fs :--text-huge}

    ;; Type weight
    :thin        {:fw :--text-thin}
    :extra-light {:fw :--text-extra-light}
    :light       {:fw :--text-light}
    :normal      {:fw :--text-normal}
    :wee-bold    {:fw :--text-wee-bold}
    :semi-bold   {:fw :--text-semi-bold}
    :bold        {:fw :--text-bold}
    :extra-bold  {:fw :--text-extra-bold}
    :heavy       {:fw :--text-heavy}

    ;; Animations
    :instant {:transition-duration :--duration-instant }
    :fast {:transition-duration :--duration-fast }
    :slow {:transition-duration :--duration-slow }
    :extra-slow {:transition-duration :--duration-extra-slow }
    :super-slow {:transition-duration :--duration-super-slow }
    :ultra-slow {:transition-duration :--duration-ultra-slow }

    ;; Surfaces, buttons, containers
    :rounded {:border-radius :--rounded}
    :sharp {:border-radius 0}
    :elevated {:box-shadow :--elevated}

    ;; Buttons, tags, & labels
    :primary           {:c         :--primary-b
                        :bgc       :--primary
                        :hover:bgc :--gray400}
    :secondary         {:bgc        :--gray100
                        :hover:bgc  :--gray200
                        :color      :--primary}
    :tertiary          {:bgc       :transparent
                        :hover:bgc :--gray100}
    :ghosted           {:bw        :1px
                        :bgc       :transparent
                        :hover:bgc :transparent
                        :hover:o   0.6}
    :positive-inverted {:c   :--primary-b
                        :bgc :--positive}
    :warning-inverted  {:c   :--primary-b
                        :bgc :--warning}
    :negative-inverted {:c   :--primary-b
                        :bgc :--negative}
    :minimal           {:bgc :transparent
                        :p   0}

    ;; Buttons
    :link      {:>span:p   0
                :td        :u
                :tup       :u
                :bgc       :transparent
                :hover:bgc :transparent
                :hover:o   0.7

                ;; THEME
                ;; :tdc                 :red
                ;; :transition-duration :--duration-0

                }

    ;; controls
    :disabled          {:o      :40%
                        :cursor :not-allowed}}))


(def font-loading
  {
  ;;  :use-system-font-stack?  false
  ;;  :use-default-code-font-family? false
  ;;  :use-default-primary-font-family? false
  ;;  :google-fonts [{:family "Public Sans"
  ;;                  :styles {:normal [100] :italic [300]}}]
   :google-fonts* ["Fira Code" "Inter"]})


(def base-theme-map
  (merge
   (keyed overrides global-tokens alias-tokens font-loading)
  ;;  {:overrides overrides-min :global-tokens global-tokens-min :alias-tokens alias-tokens-min :font-loading font-loading-min }
   {:kushi {:button {:default (merge (:secondary overrides)
                                     {:fw :--text-wee-bold
                                      :ff :--primary-font-family})
            ;; :disabled  {:color :turquoise}
            ;; :primary   (:primary theme*)
            ;; :link      {:td        :underline
            ;;             :bgc       :transparent
            ;;             :hover:bgc :transparent}
            ;; :secondary {:bgc        :--gray100
            ;;             :hover:bgc  :--gray200
            ;;             :color      :--primary}
            ;; :tertiary  {:bgc       :transparent
            ;;             :hover:bgc :--gray100}
            ;; :minimal   (:minimal theme*)
            ;; :ghosted   (:ghosted theme*)
                     }

            :tag    {;; :default  {:c :--primary}
            ;; :primary  (:primary theme*)
            ;; :positive (:positive-inverted theme*)
            ;; :negative (:negative-inverted theme*)
            ;; :warning  (:warning-inverted theme*)
                     }

            ;; stuff like this needs to be in sync with the var name it is creating
            }}))
