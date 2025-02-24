#_{:name         "divisor-dark-mode",
 :value        :$divisor-5-dark-mode,
 :desc         {:en "Fix me"},
 :category   ["Surface" "Borders" "Color"],
 :tags         ["divisor"],
 :family       nil,
 :added        "1.0",
 :alias-token? true,
 :provenance   {:namespace 'kushi.ui.tokens
                :added     "1.0"}}
;; OTHER NOTES
 

;; TODO
;; Use a vector of maps like this map:
;; {:name         "neutral-secondary-fg"
;;  :desc         {:en "Foreground text that is slightly de-emphasized (such as text input field helper text)."}
;;  :value        :$gray-700
;;  :added        "1.0"
;;  :category   ["Color" "Typography"]
;;  :tags         ["label" "de-emphasis"]
;;  :family       :font-weight  ;; :code :font-weight :font-size :letter-spacing etc (could be nil)
;;  :alias-token? <reactive based on :value>
;;  :provenance   {:namespace 'kushi.ui.tokens :added "1.0"}

;;  TODO need some kind of curve fn which creates a scale of values,
;;  e.g. more precise at beginning with larger jumps towards end
;;  -- we would only use this :suggested for like padding or similar
;;  -- or infer it in general based on css-prop?
;;  :suggested    {"em" [{:min 0 :max 4 :step 0.05}
;;                       {:min 0 :max 12 :step 0.5}]
;;                 "px" {:min 0 :max 30}}}



;; TODO what about a children concept
;; {:name         “fune-background-color”
;;  :desc         {:en “Background color for fune floating layer primitive”}
;;  :value        :transparent
;;  :added        "1.0"
;;  :category   ["Color" “Layers” “Contextual”]
;;  :tags         [“color”]
;;  :family       :fune  ;; :code :font-weight :font-size :letter-spacing etc (could be nil)
;;  :name-sub     “fune”
;;  :children     [“tooltip” “popover” “context-menu” “toast” “hover-board”]
;; REACTIVE additions
;;  :desc         <{:en "Foreground text for fune floating layer abstraction”}>
;;  :inherits     <reactive based on if :value points to another token list of inherits>
;;  :provenance   <{:namespace 'kushi.ui.tokens
;;                  :added “1.0”}>
(ns kushi.css.build.tokens
  (:require [clojure.string :as string]
            [kushi.css.build.tokens-shared :as shared]
            [kushi.css.build.design-tokens :refer [design-tokens* ]]
            [fireworks.core :refer [? !? ?> !?>]]
            [kushi.colors2 :as colors2]))


;; All tokens  -----------------------------------------------------------------
(def color-tokens
  [{:family   "Transparent neutrals colors"
      :category ["transparent-colors"]
      :tags     ["colors" "oklch" "neutrals" "transparent"]}
   colors2/transparent-neutrals-oklch

   {:family   "Colors"
    :category ["colors"]
    :tags     ["colors" "oklch"]}
   colors2/oklch-colors-flattened2

   {:family   "Foreground colors"
    :category ["foreground-colors"]
    :tags     ["colors" "oklch" "foreground"]}
   colors2/theming-colors-oklch-flattened

   ;; TODO - make sure these always get written
   {:family   "Global colors"
    :category ["global-colors"]
    :tags     ["colors" "oklch" "global"]
    }
   [
    :--foreground-color                     :$neutral-950
    :--background-color                     :white
    :--foreground-color-dark-mode           :$neutral-50
    :--background-color-dark-mode           :$neutral-1000
    :--foreground-color-secondary           :$neutral-700
    :--foreground-color-secondary-dark-mode :$neutral-350
    ]
   ])


(def design-tokens
  (apply
   array-map
   (concat
    color-tokens
    design-tokens*)))

(!? :pp kushi.css.build.design-tokens/design-tokens-by-component-usage)

(def enriched-tokens-ordered (shared/enriched-tokens-ordered* design-tokens))

(def enriched-tokens-array-map (shared/enriched-tokens-array-map* enriched-tokens-ordered))

;; This version has css-var-keywords cast to strs e.g. :$bold -> "var(--bold)"
(def design-tokens-by-token-array-map
  (shared/tokens-by-token-array-map* enriched-tokens-array-map))

(def design-tokens-by-token
  (shared/tokens-by-token* enriched-tokens-array-map) )

(def design-tokens-by-category 
  (shared/tokens-by-category* enriched-tokens-ordered 
                              ["pane"
                               "elevation"
                               "modal"
                               "popover"
                               "tooltip"
                               "toast"]))
