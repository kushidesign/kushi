(ns kushi.ui.switch.demo
  (:require
   [kushi.playground.util :as util]
   [kushi.core :refer (sx)]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.switch.core :refer [switch]]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.playground.component-examples :as component-examples]))


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
      :snippets-header ["Use the utility classes `:.neutral`, `:.accent`, `:.positive`, `:.warning`, and `:.negative` to control the semantic color variant."
                        :br
                        :br
                        "The default is `:.neutral`."]                        
      :snippets        '[[:div
                          [switch]
                          [switch (sx :.neutral)]
                          [switch (sx :.accent)]
                          [switch (sx :.positive)]
                          [switch (sx :.warning)]
                          [switch (sx :.negative)]]]
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

     (merge 
      (component-examples/sizes-snippet-scale 'switch)
      {:desc            "Different sizes from small to xxxlarge"
       :row-attrs       row-attrs
       :container-attrs container-attrs
       :examples        (for [sz sizes]
                          {:label (name sz)
                           :attrs {:class sz}})})

     {:desc            "With convex-styled thumb control"
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :sx-attrs        (sx-call (sx {:-thumb-attrs (sx :bgi--$convex
                                                       :dark:bgi--$convex-3)}))
      :snippets        '[[switch {:-thumb-attrs (sx :bgi--$convex
                                                    :dark:bgi--$convex-3)}]]
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
      :snippets        '[[switch (sx
                                 :$switch-border-width--0px
                                 :$switch-thumb-scale-factor--1.25
                                 {:-thumb-attrs (sx :border--1px:solid:currentColor)})]]
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
      :snippets        '[[switch (sx
                                  :$switch-width-ratio--2.25
                                  {:-track-content-on  "ON"
                                   :-track-content-off "OFF"})]]
      :examples        (for [sz sizes]
                         {:label (name sz)
                          :attrs {:class [sz]}})}

     {:desc            "With labeled thumb"
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :sx-attrs        (sx-call (sx {:-thumb-content-on  [:span (sx :.semi-bold :fs--0.3em) "ON"]
                                     :-thumb-content-off [:span (sx :.semi-bold :fs--0.3em) "OFF"]}))

      :snippets        '[[switch (sx
                                  {:-thumb-content-on  [:span (sx :.semi-bold :fs--0.3em) "ON"]
                                   :-thumb-content-off [:span (sx :.semi-bold :fs--0.3em) "OFF"]})]]
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

      :snippets        '[[switch (sx {:-track-content-on  [icon (sx :fs--0.55em
                                                                    {:-icon-filled? true})
                                                           :visibility]
                                      :-track-content-off [icon (sx :fs--0.55em
                                                                    {:-icon-filled? true})
                                                           :visibility-off]})]]
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

      :snippets        '[[switch (sx {:-thumb-content-on  [icon (sx :fs--0.55em
                                                                    {:-icon-filled? true})
                                                           :visibility]
                                      :-thumb-content-off [icon (sx :fs--0.55em
                                                                    {:-icon-filled? true})
                                                           :visibility-off]})]]
      :examples        (for [sz sizes]
                         {:label (name sz)
                          :attrs {:class [sz]}})}

     {:desc            "Disabled state"
      :row-attrs       row-attrs
      :container-attrs container-attrs
      :snippets        '[[switch {:disabled true}]]
      :examples        (for [sz sizes]

                         {:label (name sz)
                          :attrs {:disabled true
                                  :class    [sz]}})}]))
