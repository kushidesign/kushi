(ns kushi.css.build.colorways
  (:require [fireworks.core :refer [? !? ?> !?>]]
            [kushi.util :refer [kw->cssvar as-str color-mix linear-gradient css-varize]]
            [clojure.string :as string]))

(defn colorway-selector [s]
  (str "[data-ks-colorway=\"" s "\"], .colorway-" s))

(defn color-var-kw 
  ([s]
   (color-var-kw s nil))
  ([s level]
   (keyword (str "$foreground-color-" s level))))

(defn colorway-args [s]
  (let [color-var-kw
        (partial color-var-kw s)

        bg-color-var-kw
        (fn [level] (keyword (str "$background-color-" s level )))

        convex-light-mode-grad 
        #(linear-gradient :180deg :transparent [:transparent :15%] %)

        convex-dark-mode-grad 
        #(linear-gradient :360deg :transparent [:transparent :15%] %)

        convex-light-mode-shadow-color 
        #(color-mix "in oklch"
                    :transparent 
                    [(css-varize "background-color-" s "-hard" %)
                     :$convex-shadow-strength||10%])


        base-light-mode
        {:color        (color-var-kw)
         :hover:color  (color-var-kw "-2")
         :active:color (color-var-kw "-3")}

        base
        (merge base-light-mode
               {:dark:color        (color-var-kw "-dark-mode")
                :dark:hover:color  (color-var-kw "-2-dark-mode")
                :dark:active:color (color-var-kw "-3-dark-mode")})

        base-inert-light-mode
        {:color        (color-var-kw)
         :hover:color  (color-var-kw)
         :active:color (color-var-kw)}

        base-inert
        (merge base-inert-light-mode
               {:dark:color        (color-var-kw "-dark-mode")
                :dark:hover:color  (color-var-kw "-dark-mode")
                :dark:active:color (color-var-kw "-dark-mode")})

        ;; TODO - maybe lighten by a step?
        soft
        {:bgc               (bg-color-var-kw "-soft-3")
         :hover:bgc         (bg-color-var-kw "-soft-4")
         :active:bgc        (bg-color-var-kw "-soft-5")
         :dark:bgc          (bg-color-var-kw "-soft-3-dark-mode")
         :dark:hover:bgc    (bg-color-var-kw "-soft-4-dark-mode")
         :dark:active:bgc   (bg-color-var-kw "-soft-5-dark-mode")
         :color             (color-var-kw "-3")
         :hover:color       (color-var-kw "-3")
         :active:color      (color-var-kw "-3")
         :dark:color        (color-var-kw "-3-dark-mode")
         :dark:hover:color  (color-var-kw "-3-dark-mode")
         :dark:active:color (color-var-kw "-3-dark-mode")}
        
        soft-inert
        (let [soft-light (bg-color-var-kw "-soft-3")
              soft-dark (bg-color-var-kw "-soft-3-dark-mod")]
          {
           :bgc             soft-light
           :hover:bgc       soft-light
           :active:bgc      soft-light

           :dark:bgc        soft-dark
           :dark:hover:bgc  soft-dark
           :dark:active:bgc soft-dark})
        
        faint-light-mode
        {:bgc        (bg-color-var-kw "-soft")
         :hover:bgc  (bg-color-var-kw "-soft-2")
         :active:bgc (bg-color-var-kw "-soft-3")}

        faint
        (merge faint-light-mode
               {:dark:bgc        (bg-color-var-kw "-soft-dark-mode")
                :dark:hover:bgc  (bg-color-var-kw "-soft-2-dark-mode")
                :dark:active:bgc (bg-color-var-kw "-soft-3-dark-mode")})

        faint-inert-light-mode
        (let [bgc (bg-color-var-kw "-soft")]
          {:bgc        bgc
           :hover:bgc  bgc
           :active:bgc bgc})

        faint-inert
        (merge faint-inert-light-mode
               (let [bgc (bg-color-var-kw "-soft-dark-mode")]
                 {:dark:bgc        bgc
                  :dark:hover:bgc  bgc
                  :dark:active:bgc bgc}))
        
        convex-light-mode
        {:bgi                                            (convex-light-mode-grad (bg-color-var-kw "-soft-3"))
         :hover:bgi                                      (convex-light-mode-grad (bg-color-var-kw "-soft-4"))
         :active:bgi                                     (convex-light-mode-grad (bg-color-var-kw "-soft-5"))
         :color                                          (color-var-kw "-2")
         :hover:color                                    (color-var-kw "-3")
         :active:color                                   (color-var-kw "-3")
         :hover:bgc                                      (bg-color-var-kw "-soft")
         :active:bgc                                     (bg-color-var-kw "-soft-2")

         "@supports(color: color-mix(in oklch, red, red))" 
         {:bgi        (convex-light-mode-grad (convex-light-mode-shadow-color ""))
          :hover:bgi  (convex-light-mode-grad (convex-light-mode-shadow-color "-2"))
          :active:bgi (convex-light-mode-grad (convex-light-mode-shadow-color "-3"))}}

        convex
        (merge 
         convex-light-mode
         {
          :dark:bgi          (convex-dark-mode-grad (bg-color-var-kw "-soft-3-dark-mode"))
          :dark:hover:bgi    (convex-dark-mode-grad (bg-color-var-kw "-soft-4-dark-mode"))
          :dark:active:bgi   (convex-dark-mode-grad (bg-color-var-kw "-soft-5-dark-mode"))
          :dark:color        (color-var-kw "-2-dark-mode")
          :dark:hover:color  (color-var-kw "-3-dark-mode")
          :dark:active:color (color-var-kw "-3-dark-mode")
          :dark:hover:bgc    (color-var-kw "-soft-dark-mode")
          :dark:active:bgc   (color-var-kw "-soft-2-dark-mode")
          })

        convex-inert-light-mode
        (let [grad      (convex-light-mode-grad (bg-color-var-kw "-soft-3"))]
          {:bgi        grad
           :hover:bgi  grad
           :active:bgi grad})

        convex-inert
        (let [grad (convex-dark-mode-grad (bg-color-var-kw "-soft-3-dark-mode"))]
          (merge 
           convex-inert-light-mode
           {:dark:bgi        grad
            :dark:hover:bgi  grad
            :dark:active:bgi grad}
           ))]


   ;; do <surface>-light and <surface-dark> versions of all these to lock in light and dark versions
   ;; minimal, faint, convex, soft, soft-classic, solid, solid-classic
   ;; rectify with the "dark:" prefixed at bottom


   [
    ;; {"[data-ks-surface=\"transparent\"], .surface-transparent"
    ;;  base

    ;;  "[data-ks-surface=\"transparent\"].inert, .surface-transparent.inert"
    ;;  base-inert

    ;;  "[data-ks-surface=\"minimal\"], .surface-minimal" 
    ;;  (merge base
    ;;         {:bgc             :$background-color ; TODO - should this be accesible via token?
    ;;          :hover:bgc       (bg-color-var-kw "-soft")
    ;;          :active:bgc      (bg-color-var-kw "-soft-2")
    ;;          :dark:bgc        :$background-color-dark-mode ; TODO - should this be accesible via token?
    ;;          :dark:hover:bgc  (bg-color-var-kw "-soft-dark-mode")
    ;;          :dark:active:bgc (bg-color-var-kw "-soft-2-dark-mode")})

    ;;  "[data-ks-surface=\"minimal\"].inert, .surface-minimal.inert"
    ;;  (merge base-inert
    ;;         {:hover:bgc       :$background-color
    ;;          :active:bgc      :$background-color
    ;;          :dark:hover:bgc  :$background-color-dark-mode
    ;;          :dark:active:bgc :$background-color-dark-mode})

    ;;  "[data-ks-surface=\"minimal-light-mode\"], .surface-minimal-light-mode" 
    ;;  (merge base-light-mode
    ;;         {:bgc        :$background-color
    ;;          :hover:bgc  (bg-color-var-kw "-soft")
    ;;          :active:bgc (bg-color-var-kw "-soft-2")})

    ;;  "[data-ks-surface=\"minimal-light-mode\"].inert"
    ;;  (merge base-inert-light-mode
    ;;         {:hover:bgc       :$background-color
    ;;          :active:bgc      :$background-color})

    ;;  "[data-ks-surface=\"faint\"]"                        
    ;;  (merge base
    ;;         faint)

    ;;  "[data-ks-surface=\"faint\"].inert"         
    ;;  (merge base-inert
    ;;         faint-inert)

    ;;  ;; todo - use minimal
    ;;  "[data-ks-surface=\"convex\"]"                        
    ;;  (merge faint ; <- for :bgc
    ;;         convex)

    ;;  ;; todo - use minimal
    ;;  "[data-ks-surface=\"convex\"].inert"         
    ;;  (merge faint-inert ; <- for :bgc
    ;;         convex-inert)

    ;;  ;; todo - use minimal
    ;;  "[data-ks-surface=\"convex-light-mode\"]"                        
    ;;  (merge faint-light-mode ; <- for :bgc
    ;;         convex-light-mode)

    ;;  ;; todo - use minimal
    ;;  "[data-ks-surface=\"convex-light-mode\"].inert"         
    ;;  (merge faint-inert-light-mode ; <- for :bgc
    ;;         convex-inert-light-mode)


    ;;  "[data-ks-surface=\"soft\"]"                          
    ;;  soft


    ;;  "[data-ks-surface=\"soft\"].inert"           
    ;;  soft-inert


    ;;  "[data-ks-surface=\"soft-classic\"]"                  
    ;;  (merge soft
    ;;         {:--classic-trim-color      (keyword (str "$" s "-150"))
    ;;          :--classic-trim-color-dark (keyword (str "$" s "-800"))})


    ;;  "[data-ks-surface=\"soft-classic\"].inert"   
    ;;  (merge soft
    ;;         soft-inert
    ;;         {:--classic-trim-color      (keyword (str "$" s "-150"))
    ;;          :--classic-trim-color-dark (keyword (str "$" s "-800"))})


    ;;  "[data-ks-surface=\"solid\"], .surface-solid"                         
    ;;  {:bgc        (bg-color-var-kw "-hard")
    ;;   :hover:bgc  (bg-color-var-kw "-hard-2")
    ;;   :active:bgc (bg-color-var-kw "-hard-3")}


    ;;  "[data-ks-surface=\"solid\"].inert, .surface-solid.inert"          
    ;;  {:bgc         (bg-color-var-kw "-hard")
    ;;   :hover:bgc   (bg-color-var-kw "-hard")
    ;;   :active:bgc  (bg-color-var-kw "-hard")}


    ;;  "[data-ks-surface=\"solid-classic\"]"                    
    ;;  {:--classic-trim-color (keyword (str "$" s "-550"))
    ;;   :bgc                  (bg-color-var-kw "-hard")
    ;;   :hover:bgc            (bg-color-var-kw "-hard-2")
    ;;   :active:bgc           (bg-color-var-kw "-hard-3")}

    ;;  "[data-ks-surface=\"solid-classic\"].inert"  
    ;;  {:bgc        (bg-color-var-kw "-hard")
    ;;   :hover:bgc  (bg-color-var-kw "-hard")
    ;;   :active:bgc (bg-color-var-kw "-hard")}

    ;;  "dark:[data-ks-surface=\"solid-classic\"]"               
    ;;  {:bgc        (bg-color-var-kw "-hard-dark-mode")
    ;;   :hover:bgc  (bg-color-var-kw "-hard-2-dark-mode")
    ;;   :active:bgc (bg-color-var-kw "-hard-3-dark-mode")}

    ;;  "dark:[data-ks-surface=\"solid-classic\"].inert" 
    ;;  {:bgc        (bg-color-var-kw "-hard-dark-mode")
    ;;   :hover:bgc  (bg-color-var-kw "-hard-dark-mode")
    ;;   :active:bgc (bg-color-var-kw "-hard-dark-mode")}

    ;;  "dark:[data-ks-surface=\"solid\"], :dark:.surface-solid"                       
    ;;  {:bgc        (bg-color-var-kw "-hard-dark-mode")
    ;;   :hover:bgc  (bg-color-var-kw "-hard-2-dark-mode")
    ;;   :active:bgc (bg-color-var-kw "-hard-3-dark-mode")}
     
    ;;  ;; TODO - abstract above^ into:
    ;;  #_(bgc-map "-hard-dark-mode"
    ;;           "-hard-2-dark-mode"
    ;;           "-hard-3-dark-mode")
     
    ;;  "dark:[data-ks-surface=\"solid\"].inert, :dark:.surface-solid.inert"        
    ;;  {:bgc        (bg-color-var-kw "-hard-dark-mode")
    ;;   :hover:bgc  (bg-color-var-kw "-hard-dark-mode")
    ;;   :active:bgc (bg-color-var-kw "-hard-dark-mode")}
    ;;  }
     ]))
