(ns kushi.ui.button.core
  (:require-macros
   [kushi.core :refer (sx)])
  (:require [kushi.core :refer (merge-attrs)]
            [fireworks.core :refer [? !? ?- !?- ?-- !?-- ?> !?> ?i !?i ?l !?l ?log !?log ?log- !?log- ?pp !?pp ?pp- !?pp-]]
            [kushi.ui.core :refer (opts+children)]
            [kushi.ui.util :refer [as-str maybe nameable?]]
            [clojure.string :as string]))


(def variants
  {:shape    #{"rounded" "sharp" "pill"}
   :surface  #{"minimal" "outline" "solid" "soft"}
   :semantic #{"neutral" "accent" "positive" "negative" "warning"}})

(def variant-defaults
  {:semantic "neutral"
   :surface  "soft"
   :shape    "rounded"})

(defn data-kushi- [x k]
  (some-> x
          as-str
          (maybe (get variants k nil))
          (->> (hash-map (keyword (str "data-kushi-" (name k)))))))

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

(defn- hue-style-map [x]
  {:style {"--_semantic-hue" (or (let [s (some-> x (maybe nameable?) as-str)]
                                   (when (string/starts-with? s "$")
                                     (str "var(--" (subs s 1) ")")))
                                 x)}})

(defn button
  {:summary ["Buttons provide cues for actions and events."]
   :desc ["Buttons are fundamental components allow users to process actions or navigate an experience."
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
   :opts '[{:name    loading?
            :pred    boolean?
            :default false
            :desc    "When `true`, this will set the appropriate values for `aria-busy` and `aria-label`"}]}
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys [loading?
                start-enhancer
                end-enhancer
                hue]}           opts
        {:keys [shape
                surface
                semantic]}      (get-variants opts)]
    (into [:button
           (merge-attrs
            (sx '_ks_button
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
                ["&._ks_with-start-enhancer:pis" :$button-with-icon-padding-inline-offset]
                ["&._ks_with-end-enhancer:pie" :$button-with-icon-padding-inline-offset]
                :pi--$button-padding-inline-ems
                :pb--$button-padding-block-ems
                {:aria-busy     loading?
                 :aria-label    (when loading? "loading")})
            (when loading? {:data-kushi-ui-spinner true})
            {:class ["_ks_ia"
                     ;; TODO - maybe change this cos if user passes :-hue, it still needs this,
                     ;; but then its not necessarily a semantic thing
                     "_ks_semantic"
                     (when start-enhancer "_ks_with-start-enhancer")
                     (when end-enhancer "_ks_with-end-enhancer")
                     (str "_ks_" shape)
                     (str "_ks_" surface)
                     (when-not hue (str "_ks_" semantic))]}
            (some-> hue hue-style-map)
            attrs)]
          (cond start-enhancer 
                (cons start-enhancer children)
                end-enhancer
                (concat children [end-enhancer])
                :else
                children))))







