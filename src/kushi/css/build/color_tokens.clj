(ns kushi.css.build.color-tokens
  (:require [clojure.string :as string]
            [kushi.css.build.tokens-shared :as shared]
            [kushi.colors2 :as colors2]))

(def color-tokens
  (array-map 

   ;; Colors
   ;; ------------------------------------------------------

   {:family   "Transparent neutrals colors"
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
    ]))


(def enriched-tokens-ordered (shared/enriched-tokens-ordered* color-tokens))

(def enriched-tokens-array-map (shared/enriched-tokens-array-map* enriched-tokens-ordered))

;; This version has css-var-keywords cast to strs e.g. :$bold -> "var(--bold)"
(def color-tokens-by-token-array-map
  (shared/tokens-by-token-array-map* enriched-tokens-array-map))

(def color-tokens-by-token
  (shared/tokens-by-token* enriched-tokens-array-map) )

(def color-tokens-by-category 
  (shared/tokens-by-category* enriched-tokens-ordered ["colors" "global-colors"]))

