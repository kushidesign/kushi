(ns kushi.ui.switch.demo
  (:require
   [fireworks.core :refer [? !? ?- !?- ?-- !?-- ?> !?> ?i !?i ?l !?l ?log !?log ?log- !?log- ?pp !?pp ?pp- !?pp-]]
   [clojure.walk :as walk]
   [kushi.playground.util :as util]
   [kushi.core :refer (sx)]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.label.core :refer [label]]
   [kushi.ui.switch.core :refer [switch]]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.playground.component-examples :as component-examples :refer [section-label]]
   [markdown-to-hiccup.core :as md->hc]
   [kushi.playground.component-docs :as docs]))

;; (defn switch-demo [{dark? :-dark? :or {dark? false}}]
;;   (into [:div
;;          (sx :.flex-col-fs
;;              :gap--0.5em
;;              :padding--1rem
;;              :.xxxlarge
;;              {:class [(when dark? :dark)]})]
;;         (for [semantic [:foo
;;                         :neutral :accent
;;                         :positive :warning :negative]]
;;           [:div (sx :.flex-row-fs :gap--0.5em)
;;            [switch (sx {:-disabled? false
;;                         :class      [semantic]})]
;;            [switch (sx {:-disabled? false
;;                         :-on?       true
;;                         :class      [semantic]})]])))

(def sizes
  [:small
   :medium
   :large
   :xlarge
   :xxlarge
   :xxxlarge])

(def examples
  (let [row-attrs       (sx 'kushi-playground-switch-example-row
                            :xsm:ai--fe
                            :xsm:flex-direction--row )
        container-attrs (sx 'playground-switch-rows-container
                            :gtc--max-content:max-content
                            :xsm:gtc--max-content
                            )]
    [{:desc            "Semantic variants"
      :sx-attrs        (sx-call (sx :.xxlarge))
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :examples        (for [s (take 5 component-examples/colors)]
                         {:label (name s)
                          :attrs {:class [s]}})}

     ;; Leave this off until you figure out dark theme styling w/new color paradigm
     #_{:desc            "Semantic variants, outline styling"
        :sx-attrs        (sx-call (sx
                                   :.xxlarge
                                   :$switch-border-color--$gray-400
                                   :$switch-border-width--1.5px
                                   :$switch-off-background-color--white
                                   ["bgc" :transparent!important]
                                   ["hover:bgc" :transparent!important]
                                   ["[aria-checked='true']:bc" :currentColor]
                                   ["[aria-checked='true']:hover:bc" :currentColor]
                                   {:-thumb-attrs (sx
                                                   :.elevated-0!
                                                   [:border
                                                    "calc(var(--switch-border-width) * (1 / var(--switch-thumb-scale-factor))) solid transparent"]
                                                   [:bgc
                                                    :$switch-border-color]
                                                   ["has-ancestor(.kushi-switch[aria-checked='true']):bgc"
                                                    :currentColor])}))

        :row-attrs       row-attrs
        :container-attrs container-attrs
        :examples        (for [s component-examples/colors]
                           {:label (name s)
                            :attrs {:class [s]}})}

     {:desc            "Showing sizes from small to xxxlarge"
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :examples        (for [sz sizes]
                         {:label (name sz)
                          :attrs {:class sz}})}

     {:desc            "With convex-styled thumb control"
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :sx-attrs        (sx-call (sx {:-thumb-attrs (sx :bgi--$convex
                                                       :dark:bgi--$convex-3)}))
      :examples        (for [sz sizes]
                         {:label (name sz)
                          :attrs {:class [sz]}})}

     {:desc            "With oversized thumb control"
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :sx-attrs        (sx-call (sx
                                 :$switch-border-width--0px
                                 :$switch-thumb-scale-factor--1.25
                                 {:-thumb-attrs (sx :border--1px:solid:currentColor)}))
      :examples        (for [sz sizes]
                         {:label (name sz)
                          :attrs {:class [sz]}})}

     {:desc            "With labeled track"
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :sx-attrs        (sx-call (sx
                                 :$switch-width-ratio--2.25
                                 {:-track-content-on  "ON"
                                  :-track-content-off "OFF"}))
      :examples        (for [sz sizes]
                         {:label (name sz)
                          :attrs {:class [sz]}})}

     {:desc            "With labeled thumb"
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :sx-attrs        (sx-call (sx {:-thumb-content-on  [:span (sx :.semi-bold :fs--0.3em) "ON"]
                                     :-thumb-content-off [:span (sx :.semi-bold :fs--0.3em) "OFF"]}))
      :examples        (for [sz (drop 2 sizes)]
                         {:label (name sz)
                          :attrs {:class [sz]}})}
     
     {:desc            "With icon track"
      :reqs            '[[kushi.ui.icon.core :refer [icon]]]
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :sx-attrs        (sx-call (sx {:-track-content-on  [icon (sx :fs--0.55em
                                                                   {:-icon-filled? true})
                                                          :visibility]
                                     :-track-content-off [icon (sx :fs--0.55em
                                                                   {:-icon-filled? true})
                                                          :visibility-off]}))
      :examples        (for [sz sizes]
                         {:label (name sz)
                          :attrs {:class [sz]}})}

     {:desc            "With icon thumb"
      :reqs            '[[kushi.ui.icon.core :refer [icon]]]
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :sx-attrs        (sx-call (sx {:-thumb-content-on  [icon (sx :fs--0.55em
                                                                   {:-icon-filled? true})
                                                          :visibility]
                                     :-thumb-content-off [icon (sx :fs--0.55em
                                                                   {:-icon-filled? true})
                                                          :visibility-off]}))
      :examples        (for [sz sizes]
                         {:label (name sz)
                          :attrs {:class [sz]}})}

     {:desc            "Disabled states"
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :examples        (for [sz sizes]
                         {:label (name sz)
                          :attrs {:disabled true
                                  :class    [sz]}})}]))



#_(defn section-label
  "Renders a vertical label"
  [s]
  [:p (sx :.xxsmall
          :c--$neutral-secondary-foreground
          :min-width--55px
          {:style {:writing-mode :vertical-lr
                   :text-orientation :upright
                   :text-transform :uppercase
                   :font-weight :800
                   :color :#7d7d7d
                   :font-family "JetBrains Mono"
                   :text-align :center
                   :background-image "linear-gradient(90deg, #e3e3e3, #e3e3d3 1px, transparent 1px)"
                   :background-position-x :1ch}})
   [:span (sx :bgc--white :pi--0.5em) s]])

