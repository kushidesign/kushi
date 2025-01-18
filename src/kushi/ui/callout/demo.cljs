(ns kushi.ui.callout.demo
  (:require
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.link.core :refer [link]]
   [kushi.css.core :refer (sx merge-attrs)]
   [kushi.playground.util :refer-macros [sx-call]]
   [kushi.ui.button.core :refer [button]]
   [kushi.ui.callout.core :refer [callout]]
   [clojure.string :as string]))

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
            :_.instance-code:flex-direction--column
            :md:_.instance-code:flex-direction--column)

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
                           [link (sx :ws--n {:href "#"})
                            "Take action."]]]
               ["negative" [:span "Something went wrong. "
                            [link (sx :ws--nw {:href "#"})
                             "Learn more."]]]]]
          {:desc            (str (string/capitalize s) " variant")
           :row-attrs       row-attrs
           :container-attrs container-attrs
           :examples        [{:attrs {:-header-text msg
                                      :-icon        [icon :info]
                                      :class        [s]}}]} )]

    (into [{:desc            "Showing sizes from xxsmall to large, in accent variant"

            :reqs            '[[kushi.ui.icon.core :refer [icon]]
                               [kushi.ui.link.core :refer [link]]]
            :row-attrs       (merge-attrs row-attrs
                                          (sx :flex-direction--column
                                              :md:flex-direction--column))
            :container-attrs container-attrs
            :variants-       [:filled :bordered]
            :snippets        '[[callout (merge-attrs
                                         (sx :.large :.accent)
                                         {:-header-text [:span "Please check out the " [link (sx :ws--n {:href "#"}) "new features"]]
                                          :-icon        [icon :info]})]]
            :examples        [{:code (sx-call (for [sz sizes]
                                                [callout {:-header-text [:span "Please check out the " [link (sx :ws--n {:href "#"}) "new features"]]
                                                          :-icon        [icon :info]
                                                          :class        [sz "accent"]}]))}]}

           {:desc            "With icon and dismiss button, in positive variant"
            :row-attrs       row-attrs
            :container-attrs container-attrs
            :variants-       [:filled :bordered]
            :examples        [{:code (sx-call [callout
                                               (merge (sx :.positive)
                                                      {:-icon         [icon :check-circle]
                                                       :-header-text  "Your transaction was successful."
                                                       :-close-button [button
                                                                       (merge-attrs 
                                                                        (sx :.pill :.positive :p--0.25em)
                                                                        {:on-click (fn [] (js/alert "Example close-icon click event."))})
                                                                       [icon :clear]]})])}]}]
          semantic-variants)))
