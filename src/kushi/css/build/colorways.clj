(ns kushi.css.build.colorways
  (:require [fireworks.core :refer [? !? ?> !?>]]))

(defn colorway-selector [s]
  (str "[data-ks-colorway=\"" s "\"]"))


(defn colorway-args [s]
  (let [base-light-mode
        {:color             (keyword (str "$foreground-color-" s ))
         :hover:color       (keyword (str "$foreground-color-" s "-2"))
         :active:color      (keyword (str "$foreground-color-" s "-3"))}

        base
        (merge base-light-mode
               {:dark:color        (keyword (str "$foreground-color-" s "-dark-mode"))
                :dark:hover:color  (keyword (str "$foreground-color-" s "-2-dark-mode"))
                :dark:active:color (keyword (str "$foreground-color-" s "-3-dark-mode"))})

        base-inert-light-mode
        {:color             (keyword (str "$foreground-color-" s ))
         :hover:color       (keyword (str "$foreground-color-" s ))
         :active:color      (keyword (str "$foreground-color-" s ))}

        base-inert
        (merge base-inert-light-mode
               {:dark:color        (keyword (str "$foreground-color-" s "-dark-mode"))
                :dark:hover:color  (keyword (str "$foreground-color-" s "-dark-mode"))
                :dark:active:color (keyword (str "$foreground-color-" s "-dark-mode"))})

        ;; TODO - maybe lighten by a step?
        soft
        {:bgc               (keyword (str "$background-color-" s "-soft-3"))
         :dark:bgc          (keyword (str "$background-color-" s "-soft-3-dark-mode"))
         :hover:bgc         (keyword (str "$background-color-" s "-soft-4"))
         :dark:hover:bgc    (keyword (str "$background-color-" s "-soft-4-dark-mode"))
         :active:bgc        (keyword (str "$background-color-" s "-soft-5"))
         :dark:active:bgc   (keyword (str "$background-color-" s "-soft-5-dark-mode"))
         :color             (keyword (str "$foreground-color-" s "-3"))
         :hover:color       (keyword (str "$foreground-color-" s "-3"))
         :active:color      (keyword (str "$foreground-color-" s "-3"))
         :dark:color        (keyword (str "$foreground-color-" s "-3-dark-mode"))
         :dark:hover:color  (keyword (str "$foreground-color-" s "-3-dark-mode"))
         :dark:active:color (keyword (str "$foreground-color-" s "-3-dark-mode"))
         }
        
        soft-inert
        {
         :bgc             (keyword (str "$background-color-" s "-soft-3"))
         :hover:bgc       (keyword (str "$background-color-" s "-soft-3"))
         :active:bgc      (keyword (str "$background-color-" s "-soft-3"))

         :dark:bgc        (keyword (str "$background-color-" s "-soft-3-dark-mode"))
         :dark:hover:bgc  (keyword (str "$background-color-" s "-soft-3-dark-mode"))
         :dark:active:bgc (keyword (str "$background-color-" s "-soft-3-dark-mode"))}
        
        faint-light-mode
        {:bgc             (keyword (str "$background-color-" s "-soft"))
         :hover:bgc       (keyword (str "$background-color-" s "-soft-2"))
         :active:bgc      (keyword (str "$background-color-" s "-soft-3"))}

        faint
        (merge faint-light-mode
               {:dark:bgc        (keyword (str "$background-color-" s "-soft-dark-mode"))
                :dark:hover:bgc  (keyword (str "$background-color-" s "-soft-2-dark-mode"))
                :dark:active:bgc (keyword (str "$background-color-" s "-soft-3-dark-mode"))})

        faint-inert-light-mode
        {:bgc             (keyword (str "$background-color-" s "-soft"))
         :hover:bgc       (keyword (str "$background-color-" s "-soft"))
         :active:bgc      (keyword (str "$background-color-" s "-soft"))}

        faint-inert
        (merge faint-inert-light-mode
               {:dark:bgc        (keyword (str "$background-color-" s "-soft-dark-mode"))
                :dark:hover:bgc  (keyword (str "$background-color-" s "-soft-dark-mode"))
                :dark:active:bgc (keyword (str "$background-color-" s "-soft-dark-mode"))})
        
        convex-light-mode
        (let [lg      "linear-gradient(180deg, transparent, transparent 15%, "]
          {:bgi               (str lg "var(--background-color-" s "-soft-3))")
           :hover:bgi         (str lg "var(--background-color-" s "-soft-4))")
           :hover:bgc         (keyword (str "$background-color-" s "-soft"))
           :active:bgi        (str lg "var(--background-color-" s "-soft-5))")
           :active:bgc        (keyword (str "$background-color-" s "-soft-2"))
           :color             (keyword (str "$foreground-color-" s "-2"))
           :hover:color       (keyword (str "$foreground-color-" s "-3"))
           :active:color      (keyword (str "$foreground-color-" s "-3"))
           })

        convex
        (let [lg-dark "linear-gradient(360deg, transparent, transparent 15%, "]
          (merge 
           convex-light-mode
           {:dark:bgi          (str lg-dark "var(--background-color-" s "-soft-3-dark-mode))")
            :dark:hover:bgi    (str lg-dark "var(--background-color-" s "-soft-4-dark-mode))")
            :dark:hover:bgc    (keyword (str "$background-color-" s "-soft-dark-mode"))
            :dark:active:bgi   (str lg-dark "var(--background-color-" s "-soft-5-dark-mode))")
            :dark:active:bgc   (keyword (str "$background-color-" s "-soft-2-dark-mode"))
            :dark:color        (keyword (str "$foreground-color-" s "-2-dark-mode"))
            :dark:hover:color  (keyword (str "$foreground-color-" s "-3-dark-mode"))
            :dark:active:color (keyword (str "$foreground-color-" s "-3-dark-mode"))
            }))

        convex-inert-light-mode
        (let [lg      "linear-gradient(180deg, transparent, transparent 15%, "]
          {:bgi             (str lg "var(--background-color-" s "-soft-3))")
           :hover:bgi       (str lg "var(--background-color-" s "-soft-3))")
           :active:bgi      (str lg "var(--background-color-" s "-soft-3))")})

        convex-inert
        (let [lg-dark "linear-gradient(360deg, transparent, transparent 15%, "]
          (merge 
           convex-inert-light-mode
           {:dark:bgi        (str lg-dark "var(--background-color-" s "-soft-3-dark-mode))")
            :dark:hover:bgi  (str lg-dark "var(--background-color-" s "-soft-3-dark-mode))")
            :dark:active:bgi (str lg-dark "var(--background-color-" s "-soft-3-dark-mode))")}))
        ]


   ;; do <surface>-light and <surface-dark> versions of all these to lock in light and dark versions
   ;; minimal, faint, convex, soft, soft-classic, solid, solid-classic
   ;; rectify with the "dark:" prefixed at bottom


   [{"[data-ks-surface= \"transparent\"]"                  
     base

     "[data-ks-surface= \"transparent\"][data-ks-inert]"   
     base-inert

     "[data-ks-surface= \"minimal\"]" 
     (merge base
            {:bgc             :white ; TODO - should this be accesible via token?
             :hover:bgc       (keyword (str "$background-color-" s "-soft"))
             :active:bgc      (keyword (str "$background-color-" s "-soft-2"))
             :dark:bgc        :black ; TODO - should this be accesible via token?
             :dark:hover:bgc  (keyword (str "$background-color-" s "-soft-dark-mode"))
             :dark:active:bgc (keyword (str "$background-color-" s "-soft-2-dark-mode"))})

     "[data-ks-surface= \"minimal\"][data-ks-inert]"
     (merge base-inert
            {:hover:bgc  :white
             :active:bgc :white
             :dark:hover:bgc  :black 
             :dark:active:bgc :black})

     "[data-ks-surface= \"minimal-light-mode\"]" 
     (merge base-light-mode
            {:bgc        :white
             :hover:bgc  (keyword (str "$background-color-" s "-soft"))
             :active:bgc (keyword (str "$background-color-" s "-soft-2"))})

     "[data-ks-surface= \"minimal-light-mode\"][data-ks-inert]"
     (merge base-inert-light-mode
            {:hover:bgc       :white
             :active:bgc      :white})

     "[data-ks-surface= \"faint\"]"                        
     (merge base
            faint)

     "[data-ks-surface= \"faint\"][data-ks-inert]"         
     (merge base-inert
            faint-inert)

     ;; todo - use minimal
     "[data-ks-surface= \"convex\"]"                        
     (merge faint ; <- for :bgc
            convex)

     ;; todo - use minimal
     "[data-ks-surface= \"convex\"][data-ks-inert]"         
     (merge faint-inert ; <- for :bgc
            convex-inert)

     ;; todo - use minimal
     "[data-ks-surface= \"convex-light-mode\"]"                        
     (merge faint-light-mode ; <- for :bgc
            convex-light-mode)

     ;; todo - use minimal
     "[data-ks-surface= \"convex-light-mode\"][data-ks-inert]"         
     (merge faint-inert-light-mode ; <- for :bgc
            convex-inert-light-mode)


     "[data-ks-surface= \"soft\"]"                          
     soft


     "[data-ks-surface= \"soft\"][data-ks-inert]"           
     soft-inert


     "[data-ks-surface= \"soft-classic\"]"                  
     (merge soft
            {:--classic-trim-color      (keyword (str "$" s "-150"))
             :--classic-trim-color-dark (keyword (str "$" s "-800"))})


     "[data-ks-surface= \"soft-classic\"][data-ks-inert]"   
     (merge soft
            soft-inert
            {:--classic-trim-color      (keyword (str "$" s "-150"))
             :--classic-trim-color-dark (keyword (str "$" s "-800"))})


     "[data-ks-surface= \"solid\"]"                         
     {:bgc        (keyword (str "$background-color-" s "-hard"))
      :hover:bgc  (keyword (str "$background-color-" s "-hard-2"))
      :active:bgc (keyword (str "$background-color-" s "-hard-3"))}


     "[data-ks-surface= \"solid\"][data-ks-inert]"          
     {:bgc        (keyword (str "$background-color-" s "-hard"))
      :hover:bgc  (keyword (str "$background-color-" s "-hard"))
      :active:bgc (keyword (str "$background-color-" s "-hard"))}


     "[data-ks-surface= \"solid-classic\"]"                    
     {:--classic-trim-color (keyword (str "$" s "-550"))
      :bgc                  (keyword (str "$background-color-" s "-hard"))
      :hover:bgc            (keyword (str "$background-color-" s "-hard-2"))
      :active:bgc           (keyword (str "$background-color-" s "-hard-3"))}

     "[data-ks-surface= \"solid-classic\"][data-ks-inert]"  
     {:bgc        (keyword (str "$background-color-" s "-hard"))
      :hover:bgc  (keyword (str "$background-color-" s "-hard"))
      :active:bgc (keyword (str "$background-color-" s "-hard"))}

     "dark:[data-ks-surface= \"solid-classic\"]"               
     {:bgc        (keyword (str "$background-color-" s "-hard-dark-mode"))
      :hover:bgc  (keyword (str "$background-color-" s "-hard-2-dark-mode"))
      :active:bgc (keyword (str "$background-color-" s "-hard-3-dark-mode"))}

     "dark:[data-ks-surface= \"solid-classic\"][data-ks-inert]" 
     {:bgc        (keyword (str "$background-color-" s "-hard-dark-mode"))
      :hover:bgc  (keyword (str "$background-color-" s "-hard-dark-mode"))
      :active:bgc (keyword (str "$background-color-" s "-hard-dark-mode"))}

     "dark:[data-ks-surface= \"solid\"]"                       
     {:bgc        (keyword (str "$background-color-" s "-hard-dark-mode"))
      :hover:bgc  (keyword (str "$background-color-" s "-hard-2-dark-mode"))
      :active:bgc (keyword (str "$background-color-" s "-hard-3-dark-mode"))}
     
     "dark:[data-ks-surface= \"solid\"][data-ks-inert]"        
     {:bgc        (keyword (str "$background-color-" s "-hard-dark-mode"))
      :hover:bgc  (keyword (str "$background-color-" s "-hard-dark-mode"))
      :active:bgc (keyword (str "$background-color-" s "-hard-dark-mode"))}}]))
