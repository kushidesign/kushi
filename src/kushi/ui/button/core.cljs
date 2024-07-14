(ns kushi.ui.button.core
  (:require-macros
   [kushi.core :refer (sx)])
  (:require [kushi.core :refer (merge-attrs)]
            [kushi.ui.core :refer (opts+children)]
            [kushi.ui.icon.core]
            [kushi.ui.util :refer [as-str maybe nameable?]]
            [clojure.string :as string]))


(def variants
  {:shape    #{"rounded" "sharp" "pill"}
   :surface  #{"minimal" "outline" "solid" "soft"}
  ;;  :semantic #{"neutral" "accent" "positive" "negative" "warning"}
   :colorway #{"accent" "positive" "negative" "warning"}
   })

(def variant-defaults
  {:colorway nil
   :surface  "soft"
   :shape    "rounded"})

(defn data-kui- [x k]
  (some-> x
          as-str
          #_(maybe (get variants k nil))
          (->> (hash-map (keyword (str "data-kui-" (name k)))))))

;; (def color-mix-support? (? (.supports js/window.CSS "(color: color-mix(in oklch, red, transparent)")))
;; (def oklch-support? (? (.supports js/window.CSS "(color: oklch(40.1% 0.123 21.57))")))

(defn- get-variants [opts]
  (reduce-kv (fn [acc k v]
               (assoc acc
                      k
                      (or (some-> k
                                  opts
                                  (maybe nameable?)
                                  as-str
                                  (maybe (k variants)))
                          v)))
             {}
             variant-defaults))


(defn valid-hue? [x]
  (and (number? x) (<= 0 x 360)))

(defn- hue-style-map [x]
  (when-let [v (or (when-let [s (some-> x (maybe nameable?) as-str)]
                     (or (when (string/starts-with? s "$")
                           (str "var(--" (subs s 1) ")"))
                         (maybe s #(re-find #"^var\(--.+\)$" %))))
                   (valid-hue? x)
                   (some-> x (maybe nameable?) js/parseInt valid-hue?))]
    {:style {"--_hue" v}}))


(defn button
  {:summary ["Buttons provide cues for actions and events."]
   :desc    ["Buttons are fundamental components allow users to process actions or navigate an experience."
             :br
             :br
             "They can be custom styled via a variety of tokens in your theme."
             :br
             :br "`:$button-padding-inline-ems`"
             :br "The default value is `:1.2em`"
             :br
             :br "`:$icon-button-padding-inline-ems`"
             :br "The default value is `:0.69em`"
             :br
             :br "`:$button-padding-block-ems`"
             :br "The default value is `:0.67em`"
             :br
             :br "`:$button-with-icon-padding-inline-offset`"
             :br "The default value is `:0.9em`"
             :br
             :br "`:$button-border-width`"
             :br "The default value is `:1px`"
             :br]
   :opts    '[{:name    loading?
               :pred    boolean?
               :default false
               :desc    "When `true`, this will set the appropriate values for `aria-busy` and `aria-label`"}]}
  [& args]
  (let [[opts attrs & children]
        (opts+children args)

        {:keys [loading?
                start-enhancer
                end-enhancer
                colorway
                stroke-align
                packing
                icon]}
        opts

        {:keys             [shape surface]
         semantic-colorway :colorway}
        (get-variants opts)

        hue-style-map                 
        (when-not semantic-colorway 
          (some-> colorway
                  hue-style-map))]
    (into [:button
           (merge-attrs
            (sx 'kui-button
                :d--flex
                :flex-direction--row
                :jc--c
                :ai--c
                :w--fit-content
                :gap--$icon-enhanceable-gap
                :cursor--pointer
                :transition-property--all
                :transition-timing-function--$transition-timing-function
                :transition-duration--$transition-duration
                :pi--$_padding-inline
                :pb--$_padding-block
                {:aria-busy        loading?
                 :aria-label       (when loading? "loading")
                 :data-kui-ia      ""
                 :data-kui-surface surface})
            (when loading? {:data-kushi-ui-spinner true})
            {:class ["kui-surface" (str "kui-" shape)]}
            (when (and (not icon) end-enhancer) (data-kui- "" :end-enhancer))
            (when (and (not icon) start-enhancer) (data-kui- "" :start-enhancer))
            (when icon (data-kui- "" :icon-button))
            (some-> stroke-align 
                    (maybe #{:outside "outside"})
                    (data-kui- :stroke-align))
            (some-> (or semantic-colorway
                        (when hue-style-map ""))
                    (data-kui- :colorway))
            (some-> packing
                    (maybe nameable?)
                    as-str
                    (maybe #{"compact" "roomy"})
                    (data-kui- :packing))
            hue-style-map
            (some-> surface (data-kui- :surface))
            attrs)]
          (cond icon           [[kushi.ui.icon.core/icon :star]]
                start-enhancer (cons start-enhancer children)
                end-enhancer   (concat children [end-enhancer])
                :else          children))))
