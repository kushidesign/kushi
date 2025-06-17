(ns ^{:kushi/layer "user-styles"}
  kushi.ui.checkbox.demo
  (:require [kushi.core :refer (sx)]
            [kushi.ui.icon :refer [icon]]
            [kushi.playground.component-examples :as component-examples]
            [kushi.playground.util :refer-macros [sx-call]]
            [kushi.ui.checkbox :refer [checkbox]]
            [kushi.ui.label :refer [label]]))


;; (declare checkbox-examples)

;; (defn demo [component-opts]
;;   (into [:<>]
;;         (for [
;;               ;; example-opts (take 1 checkbox-examples)
;;               example-opts checkbox-examples
;;               ;; example-opts (keep-indexed (fn [idx m] (when (contains? #{9} idx) m)) checkbox-examples)
;;               ]
;;           [component-examples/examples-section
;;            component-opts
;;            example-opts])))

(def sizes
  [:xsmall
   :small
   :medium
   :large
   :xlarge
   :xxlarge
   :xxxlarge])

(def examples
  (let [row-attrs {:class ["playground-example-row-bounded"]}]
    [{:desc            "Different sizes"
      :row-attrs       (sx [:xsm:_.instance-code
                            {:ai             :fe
                             :flex-direction :row}]
                           [:_.instance-code
                            {:ai             :fs
                             :flex-direction :column
                             :w              :100%
                             :jc             :sb
                             :max-width      :400px}]
                           :xsm:_label:first-child:pbe--0.75em
                           :xsm:_label:pie--0.75em)
      :snippets-header component-examples/sizes-snippet-header*
      :snippets        '[[checkbox (merge-attrs 
                                    (sx :fs--$xxxlarge)
                                    {:input-attrs {:name           :xxxlarge-sample
                                                    :defaultChecked true}})]]
      :examples        [{:code (sx-call (into [:<>] 
                                              (for [sz sizes]
                                                [:div (sx :.flex-row-fs
                                                          :xsm:flex-direction--column) 
                                                 [checkbox {:label-attrs   {:class [sz]}
                                                            :defaultChecked true}]
                                                 [checkbox {:label-attrs {:class [sz]}}]])))}]}
     {:desc      "With label"
      :row-attrs row-attrs
      :snippets  '[[[label "Sign me up"]]]
      :examples  [{:code (sx-call [checkbox [label "Sign me up"]])}]}

     {:desc      "With label and trailing icon"
      :row-attrs row-attrs
      :examples  [{:code (sx-call [checkbox 
                                   [label "Make it shiny" [icon :auto-awesome]]])}]}
     
     (let [weights component-examples/type-weights]
       {:desc      (str "Weight variants " (name (first weights)) " to " (name (last weights)))
        :row-attrs (sx :.flex-col-fs
                       :.playground-example-row-bounded-parent
                       :md:flex-direction--column
                       :ai--fs
                       :gap--2rem
                      ;;  :w--fit-content
                      ;;  :border-radius--$rounded
                      ;;  :p--1em
                      ;;  :bgc--transparent
                      ;;  :b--1px:solid:$neutral-100
                      ;;  :dark:bgc--$neutral-950
                      ;;  :dark:b--1px:solid:$neutral-900
                       )
        :sx-attrs  (sx-call (sx :.large))
        :examples  (for [s weights]
                     {:label (name s)
                      :attrs {:label-attrs {:class [s]}}
                      :args  [[label "Make it shiny" [icon :auto-awesome]]]} )})
     ]))

#_{:desc      "Showing sizes from xsmall to xxxlarge"
 :row-attrs (sx [:md:_.instance-code
                 {:ai             :fe
                  :flex-direction :row}]
                [:_.instance-code
                 {:ai             :fs
                  :flex-direction :column}]
                :_label:pbe--0
                :_label:pie--0.35em)
 :examples  [{:code (sx-call (into [:<>] 
                                   (for [sz sizes]
                                     [:div (sx :.flex-row-fs
                                               :md:flex-direction--column) 
                                      [radio {:class        [sz]
                                              :input-attrs {:name           sz
                                                             :defaultChecked true}}]
                                      [radio {:class        [sz]
                                              :input-attrs {:name sz}}]])))}]}
