(ns ^{:kushi/layer "user-styles"} kushi.ui.callout.demo
  (:require
   [clojure.string :as string]
   [kushi.core :refer (sx merge-attrs)]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.callout.core :refer [callout callout-close-button]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.link.core :refer [link]]))

(def sizes
  [:xxsmall
   :xsmall
   :small
   :medium
   :large])


(def examples
  (let [row-attrs         
        (sx :.fooosball
            :_.instance-code:w--100%
            :_.instance-code:w--100%
            :_.instance-code:flex-direction--column
            :md:_.instance-code:flex-direction--column
            :w--100%
            :w--100%
            :flex-direction--column
            :md:flex-direction--column)

        container-attrs   
        (sx :gtc--1fr)

        semantic-variants 
        (for [[s msg]
              [["neutral" [:span "Please check out the "
                           [link (merge-attrs (sx :ws--n) {:href "#"})
                            "new features"]]]
               ["accent" [:span "Please check out the "
                          [link (merge-attrs (sx :ws--n) {:href "#"})
                           "new features"]]]
               ["positive" "Your transaction was successful"]
               ["warning" [:span "Your subscription needs to be updated. "
                           [link (merge-attrs (sx :ws--n) {:href "#"})
                            "Take action."]]]
               ["negative" [:span "Something went wrong. "
                            [link (merge-attrs (sx :ws--nw) {:href "#"})
                             "Learn more."]]]]]
          {:desc            (str (string/capitalize s) " variant")
           :row-attrs       row-attrs
           :container-attrs container-attrs
           :examples        (for [surface #{:faint :solid :outline}]
                              {:attrs {:-header-text msg
                                       :-icon        [icon :info]
                                       :colorway    s
                                       :surface     surface}})})]


      (into [{:desc            "Sizes from xxsmall to xlarge"
              :row-attrs       row-attrs #_(sx :md:ai--fe)
              :container-attrs container-attrs
              :snippets        '[[callout
                                  (merge-attrs
                                   (sx :.large)
                                   {:-header-text [:span "Please check out the "
                                                   [link (merge-attrs (sx :ws--n)
                                                                      {:href "#"})
                                                    "new features"]]
                                    :colorway    :accent
                                    :-icon        [icon :info]})]]
              :examples          [{:code (sx-call
                                          (for [sz sizes]
                                            [callout {:-header-text [:span "Please check out the "
                                                                     [link (merge-attrs
                                                                            (sx :ws--n)
                                                                            {:href "#"})
                                                                      "new features"]]
                                                      :-icon        [icon :info]
                                                      :colorway    :accent
                                                      :class        [sz]}]))}]}

             {:desc            "With icon and dismiss button, in positive variant"
              :row-attrs       row-attrs
              :container-attrs container-attrs
              :variants-       [:filled :bordered]
              :examples        [{:code (sx-call 
                                        [callout
                                         {:-icon         [icon :check-circle]
                                          :colorway     :positive
                                          :-header-text  "Your transaction was successful."
                                          :-user-actions callout-close-button}])}]}

             ]
             semantic-variants)


    #_(into [{:desc            "Showing sizes from xxsmall to large, in accent variant"
            :reqs            '[[kushi.ui.icon.core :refer [icon]]
                               [kushi.ui.link.core :refer [link]]]
            :row-attrs       (merge-attrs row-attrs
                                          (sx :flex-direction--column
                                              :md:flex-direction--column))
            :container-attrs container-attrs
            :variants-       [:filled :bordered]
            :snippets        '[[callout
                                (merge-attrs
                                 (sx :.large)
                                 {:-header-text [:span "Please check out the "
                                                 [link (merge-attrs
                                                        (sx :ws--n)
                                                        {:href "#"})
                                                  "new features"]]
                                  :colorway    :accent
                                  :-icon        [icon :info]})]]
            :examples        [{:code 
                               (sx-call
                                (for [sz sizes]
                                  [callout {:-header-text [:span "Please check out the "
                                                           [link (merge-attrs
                                                                  (sx :ws--n)
                                                                  {:href "#"})
                                                            "new features"]]
                                            :-icon        [icon :info]
                                            :colorway    :accent
                                            :class        [sz]}]))}]}
           #_{:desc            "With icon and dismiss button, in positive variant"
            :row-attrs       row-attrs
            :container-attrs container-attrs
            :variants-       [:filled :bordered]
            :examples        [{:code
                               (sx-call 
                                [callout
                                 (merge {:-icon         [icon :check-circle]
                                         :colorway     :positive
                                         :-header-text  "Your transaction was successful."
                                         :-user-actions callout-close-button})])}]}]
          #_semantic-variants)))

