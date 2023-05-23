(ns kushi.playground.links
  (:require
   [kushi.core :refer (sx defclass merge-attrs)]
   [kushi.playground.util :refer [kushi-github-url kushi-clojars-url]]
   [kushi.ui.icon.core :refer [icon icon-svgs]]
   [kushi.ui.icon.svg :refer [icon-svgs]]
   [kushi.ui.core :refer (opts+children)]))

(defclass grayscale
  {:filter "grayscale(1) contrast(1) brightness(1)"})

(defclass small-badge
  :w--20px
  :h--20px
  :o--1
  :hover:o--0.5)

(defn contained-image
  [& args]
  (let [[_ attrs & _] (opts+children args)]
    [:img
     (merge-attrs
      (sx 'grayscale-icon-image
          {:style {:max-height :100%
                   :max-width  :100%
                   :object-fit :contain}})
      attrs)]))

(defn badge
  [& args]
  (let [[_ attrs & children] (opts+children args)]
    (into
     [:a
      (merge-attrs
       (sx :.pointer )
       attrs)]
     children)))

(defn badge2
  [& args]
  (let [[_ attrs & children] (opts+children args)]
    (into
     [:span
      (merge-attrs
       (sx :.pointer ["has-ancestor(.dark):filter" '(invert 1)])
       attrs)]
     children)))

(def link-data [{:href     kushi-github-url
                 :attrs    (sx :&.kushi-icon>svg:w--22px
                               :&.kushi-icon>svg:h--20px
                               :&.kushi-icon>svg>path:fill--black
                               {:-icon-svg (get icon-svgs "github")})}
                {:href     kushi-clojars-url
                 :attrs    (sx :&.kushi-icon>svg:w--22px
                               :&.kushi-icon>svg:h--20px
                               :&.kushi-icon>svg>path:fill--black
                               :dark:&.kushi-icon>svg>path:fill--white
                               {:-icon-svg (get icon-svgs "clojars")})}
                #_{:href "https://twitter.svg"
                   :src "graphics/twitter.svg"}])
(defn links []
  (into
   [:div
    (sx 'project-links
        :.flex-row-sa
        :>a:display--inline-flex
        :>a:mis--0.75rem)]
   (for [{:keys [href attrs]} link-data]
     [badge
      {:href href :target :_blank}
      [icon attrs]])))
