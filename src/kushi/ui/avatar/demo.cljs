(ns ^{:kushi/layer "user-styles"} kushi.ui.avatar.demo
  (:require [kushi.core :refer (sx css)]
            [kushi.playground.component-examples :as component-examples]
            [kushi.playground.util :refer-macros [sx-call]]
            [kushi.ui.avatar :refer [avatar]]
            [kushi.ui.icon :refer [icon]]
            [kushi.playground.assets.graphics.avatars :refer [avatar-1]]))

(def sizes
  [:24px :36px :48px :60px :72px :96px #_:128px])

(def sample-url
   "https://placecats.com/bella/200/200?fit=fill&position=right"
   #_"https://cdn.jsdelivr.net/gh/faker-js/assets-person-portrait/female/512/85.jpg"
  )

(def examples
  [
   {:desc            "Content examples"
    ;; :sx-attrs        (sx-call (sx :fs--$small))
    :container-attrs (sx :>*:d--none :>*:first-child:d--flex)
    :variants+       [:minimal]
    :examples        [
                      {:label "With picture"
                       :attrs {:src avatar-1}
                       :args  []}
                      {:label "f"
                       :args  ["M"]
                       :attrs {:surface :solid}}
                      {:label "g"
                       :args  ["MT"]
                       :attrs {:surface :solid}}
                      {:label "people"
                       :args  [[icon :diversity-3]]
                       :attrs {:surface :solid
                               :class (css :fs--$medium)}}
                      {:label "building"
                       :args  [[icon :account-balance]]
                       :attrs {:surface :solid
                               :class (css :fs--$medium)}}

                      {:label "f"
                       :args  ["M"]
                       :attrs {:surface :faint}}
                      {:label "g"
                       :args  ["MT"]
                       :attrs {:surface :faint}}
                      {:label "people"
                       :args  [[icon :diversity-3]]
                       :attrs {:surface :faint
                               :class (css :fs--$medium)}}
                      {:label "building"
                       :args  [[icon :account-balance]]
                       :attrs {:surface :faint
                               :class (css :fs--$medium)}}
                      ]}

   {:desc            "Colorway variants"
    :sx-attrs        (sx-call (sx :fs--$small))
    :container-attrs {}
    :variants+       [:minimal]
    :examples        (for [colorway component-examples/all-colors]
                       {:label (name colorway)
                        :args  ["M"]
                        :attrs {:colorway colorway}})}
   
   {:desc      "Sample sizes from 24px to 72px, circle"
    :row-attrs (sx 
                :ai--c
                :jc--fs)
    :container-attrs (sx :>*:d--none :>*:first-child:d--flex)
    :snippets-header component-examples/sizes-snippet-header*
    :snippets ['[avatar {:sizing "48px" :src "https://placecats.com/bella/200/200?fit=fill&position=right"}]]
    :variants+ [:minimal]
    :examples  (for [sz sizes]
                 {:label (name sz)
                  :attrs {:sizing sz
                          :src   avatar-1}
                  :args  []})}
   
   {:desc      "Sample sizes from 24px to 72px, rounded"
    :row-attrs (sx 
                :ai--c
                :jc--fs)
    :container-attrs (sx :>*:d--none :>*:first-child:d--flex)
    :snippets-header component-examples/sizes-snippet-header*
    :snippets ['[avatar {:sizing "48px" :src "https://placecats.com/bella/200/200?fit=fill&position=right"}]]
    :variants+ [:minimal]
    :examples  (for [sz sizes]
                 {:label (name sz)
                  :attrs {:sizing  sz
                          :contour :rounded
                          :src    avatar-1}
                  :args  []})}

   {:desc      "Sample sizes from 24px to 72px, with fallback text"
    :row-attrs (sx 
                :ai--c
                :jc--fs)
    :container-attrs (sx :>*:d--none :>*:first-child:d--flex)
    :snippets-header component-examples/sizes-snippet-header*
    :snippets ['[avatar]]
    :examples  (for [sz sizes]
                 {:label (name sz)
                  :attrs {:sizing sz}
                  :args  ["M"]})}

   {:desc      "Sample sizes from 24px to 72px, solid variant."
    :row-attrs (sx 
                :ai--c
                :jc--fs)
    :container-attrs (sx :>*:d--none :>*:first-child:d--flex)
    :snippets-header component-examples/sizes-snippet-header*
    :snippets ['[avatar]]
    :examples  (for [sz sizes]
                 {:label (name sz)
                  :attrs {:sizing    sz
                          :surface :solid}
                  :args  ["M"]})}

   {:desc      "Sample sizes from 24px to 72px, rounded shape"
    :row-attrs (sx 
                :ai--c
                :jc--fs)
    :container-attrs (sx :>*:d--none :>*:first-child:d--flex)
    :snippets-header component-examples/sizes-snippet-header*
    :snippets ['[avatar]]
    :examples  (for [sz sizes]
                 {:label (name sz)
                  :attrs {:sizing sz
                          :contour :rounded}
                  :args  ["M"]})}

   {:desc      "Sample sizes from 24px to 72px, rounded shape, solid variant"
    :row-attrs (sx 
                :ai--c
                :jc--fs)
    :container-attrs (sx :>*:d--none :>*:first-child:d--flex)
    :snippets-header component-examples/sizes-snippet-header*
    :snippets ['[avatar]]
    :examples  (for [sz sizes]
                 {:label (name sz)
                  :attrs {:sizing    sz
                          :contour   :rounded
                          :surface :solid}
                  :args  ["M"]})}

   

   #_{:desc      "Elevated levels from 0 to 5"
      :row-attrs (sx :.transition
                     :ai--fs
                     :flex-direction--column
                     :gap--2rem
                     :_.kushi-card:w--fit-content
                     :_.kushi-card:b--1px:solid:$neutral-200
                     :dark:_.kushi-card:b--1px:solid:$neutral-700)
      :examples  (for [sz   (range 5)
                       :let [class (str "elevated-" sz)]]
                   {:label class
                    :attrs {:class [class]}
                    :args  [[:div (sx :.flex-row-fs
                                      :ai--stretch
                                      :gap--0.8em)
                             [:div (sx :.rounded
                                       :position--relative
                                       :.transition
                                       :overflow--hidden
                                       :dark:bgc--$neutral-850
                                       :bgc--$neutral-200
                                       :w--3.5em
                                       :h--3.5em)
                              [:span (sx :.absolute-centered
                                         [:transform "translate(0, 0.045em)"]
                                         :display--block
                                         :scale--2.55)
                               "🐻‍❄"]]
                             [:section (sx :.flex-col-sa) 
                              [:p (sx :fs--1.25em :fw--$wee-bold) "Polar Bear"] 
                              [:p (sx :.foreground-color-secondary!) "polar.bear@example.com"]]]]})}
   


   ])
