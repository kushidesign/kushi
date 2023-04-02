(ns kushi.playground.demobox.defs)

(def variants-by-category
  {
   :size     [:xxxsmall :xxsmall :xsmall :small :medium :large :xlarge #_:xxlarge #_:xxxlarge]
   :size-expanded    [:xxxsmall :xxsmall :xsmall :small :medium :large :xlarge :xxlarge :xxxlarge :xxxxlarge]
   :kind     [:default :minimal :bordered :filled]
   :kind2    [:default :bordered :filled]
  ;;  :status   [:disabled]
   :shape    [:pill :rounded :sharp]
   :weight   [:thin
              :extra-light
              :light
              :normal
              :wee-bold
              :semi-bold
              :bold
              :extra-bold]
   :semantic [:neutral
              :accent
              :positive
              :negative
              :warning]})
