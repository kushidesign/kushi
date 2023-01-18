(ns kushi.playground.links
  (:require
   [kushi.core :refer (sx defclass merge-attrs)]
   [kushi.playground.ui :refer [light-dark-mode-switch]]
   [kushi.playground.util :refer [kushi-github-url kushi-clojars-url]]
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

(def link-data [{:href kushi-github-url
                 :src  "public/graphics/github.svg"}
                {:href kushi-clojars-url
                 :on-error "this.onerror=null; this.src='graphics/clojars-logo-bw2.png'"
                 :src "public/graphics/clojars-logo-bw2.png"}
                #_{:href "https://twitter.svg"
                 :src "graphics/twitter.svg"}])
(defn links []
  (into
   [:div
    (sx 'project-links
        :.flex-row-sa
        :ai--center
        :>a:display--inline-flex
        :>a:mis--0.75rem
        )]
   (for [{:keys [href on-error src]} link-data]
     [badge
      {:href href :target :_blank}
      [contained-image (sx :.grayscale :.small-badge {:src src})]])))
