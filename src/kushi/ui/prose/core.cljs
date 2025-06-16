(ns kushi.ui.prose.core
  (:require
   [kushi.core :refer [merge-attrs css]]
   [kushi.ui.core :refer (extract)]
   [kushi.ui.icon.core]
   [kushi.ui.shared.theming :refer [get-variants]]
   [kushi.ui.util :refer [maybe nameable?]]))

(defn prose 
  {:summary "Avatars are graphical thumbnail representations of an individual
             or entity."
   :desc    "Avatars will display fallback text when no image is provided."
   :opts    '[colorway
              {:schema    #{:neutral :accent :positive :negative :warning}
               :default nil
               :desc    "Colorway of the avatar. Can also be a named color from
                         Kushi's design system, e.g `:red`, `:purple`, `:gold`,
                         etc. Only applies when fallback text is used."}
              size           
              {:schema    #{:xxxsmall
                          :xxsmall
                          :xsmall
                          :small
                          :medium
                          :large
                          :xlarge
                          :xxlarge
                          :xxxlarge}
               :default nil
               :desc    "Corresponds to the font-size based on Kushi's font-size
                         scale."}
              ]}
  [& args]
  (let [{:keys [opts attrs children]}       (extract args) 
        {:keys [size]}                opts
        size                          (or (some-> size
                                                  (maybe nameable?)
                                                  name)
                                          "36px")
        {semantic-colorway :colorway} (get-variants opts {:contour :circle})]
    (into [:p
           (merge-attrs
            {:class               (css ".kushi-prose"
                                       {:line-height  :1.75
                                        :font-weight :$prose-font-weight
                                        :font-family :$prose-font-family
                                        :color       :$prose-color})
             :data-ks-sizing     size
             :data-ks-colorway semantic-colorway}
            attrs)]
          children)))
