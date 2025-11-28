(ns kushi.css.build.surfaces
  (:require [clojure.string :as string]))

(defn data-ks-surface-str
  [{:keys
    [
     surface
     color
     background-color
     color-dark-mode
     background-color-dark-mode

     lightness-fgc  
     lightness-bgc
     chroma-fgc
     chroma-bgc

     chroma-fgc-muted
     chroma-bgc-muted

     lightness-fgc-dark-mode
     lightness-bgc-dark-mode
     chroma-fgc-dark-mode
     chroma-bgc-dark-mode]
    :or {color           "var(--fgc)"
         background-color "var(--bgc)" }}] 

(str
 "
/* " (string/upper-case surface) " " (string/join (repeat (- 80 (+ (count surface) 7)) "-")) " */
  
[data-ks-surface2=" surface "] {
  --lightness-fgc: " lightness-fgc ";
  --lightness-bgc: " lightness-bgc ";
  --chroma-fgc:    " chroma-fgc ";
  --chroma-bgc:    " chroma-bgc ";

  color:            " color ";
  background-color: " background-color ";

  &[data-ks-colorway2=sand], &[data-ks-colorway2=slate] {
    --chroma-fgc: " chroma-fgc-muted ";
    --chroma-bgc: " chroma-bgc-muted ";
  }

  .dark & {
    --lightness-fgc: " lightness-fgc-dark-mode ";
    --chroma-fgc:    " chroma-fgc-dark-mode ";
    --lightness-bgc: " lightness-bgc-dark-mode ";
    --chroma-bgc:    " chroma-bgc-dark-mode ";
    " (some->> color-dark-mode (str "color: ")) (when color-dark-mode ";") "
    " (some->> background-color-dark-mode (str "background-color: ")) (when background-color-dark-mode ";") "
    &[data-ks-colorway2=sand], &[data-ks-colorway2=slate] {
      --chroma-fgc: " chroma-fgc ";
      --chroma-bgc: " chroma-bgc ";
    }
  }

  &[data-ks-interactive] {
    --chroma-shift:        6.25%;
    --lightness-shift-bgc: 4%;
    --lightness-shift-fgc: 12%;

    .dark & {
      --chroma-shift:        6.25%;
      --lightness-shift-bgc: 4%;
      --lightness-shift-fgc: 12%;
    }

    &:hover {
      color: var(--hover-fgc);
      background-color: var(--hover-bgc);
    }

    &:active {
      color: var(--active-fgc);
      background-color: var(--active-bgc);
    }
  }
}"))



;; Write to filesystem from here while running docs in shadow,
;; should show realtime changes
#_(println (data-ks-surface-str
          {:surface                 "soft"
           :lightness-fgc           "30%"
           :lightness-bgc           "94.25%"
           :chroma-fgc              "32.5%"
           :chroma-bgc              "15.75%"
           :chroma-fgc-muted        "12.5%"
           :chroma-bgc-muted        "2%"
           :lightness-bgc-dark-mode "39%"
           :chroma-bgc-dark-mode    "35.6%"
           :lightness-fgc-dark-mode "95.7%"
           :chroma-fgc-dark-mode    "8.8%"}))
