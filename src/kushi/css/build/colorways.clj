(ns kushi.css.build.colorways)

(defn colorway-selector [s]
  (str "[data-ks-colorway=\"" s "\"]"))


(defn colorway-args [s]
  (let [base
        {:color             (keyword (str "$foreground-color-" s ))
         :hover:color       (keyword (str "$foreground-color-" s "-2"))
         :active:color      (keyword (str "$foreground-color-" s "-3"))
         :dark:color        (keyword (str "$foreground-color-" s "-dark-mode"))
         :dark:hover:color  (keyword (str "$foreground-color-" s "-2-dark-mode"))
         :dark:active:color (keyword (str "$foreground-color-" s "-3-dark-mode"))}

        base-inert
        {:color             (keyword (str "$foreground-color-" s ))
         :hover:color       (keyword (str "$foreground-color-" s ))
         :active:color      (keyword (str "$foreground-color-" s ))
         :dark:color        (keyword (str "$foreground-color-" s "-dark-mode"))
         :dark:hover:color  (keyword (str "$foreground-color-" s "-dark-mode"))
         :dark:active:color (keyword (str "$foreground-color-" s "-dark-mode"))}

        soft
        {:bgc               (keyword (str "$background-color-" s "-soft-3"))
         :dark:bgc          (keyword (str "$background-color-" s "-soft-3-dark-mode"))
         :hover:bgc         (keyword (str "$background-color-" s "-soft-2"))
         :dark:hover:bgc    (keyword (str "$background-color-" s "-soft-2-dark-mode"))
         :active:bgc        (keyword (str "$background-color-" s "-soft"))
         :dark:active:bgc   (keyword (str "$background-color-" s "-soft-dark-mode"))
         :color             (keyword (str "$foreground-color-" s "-3"))
         :hover:color       (keyword (str "$foreground-color-" s "-3"))
         :active:color      (keyword (str "$foreground-color-" s "-3"))
         :dark:color        (keyword (str "$foreground-color-" s "-3-dark-mode"))
         :dark:hover:color  (keyword (str "$foreground-color-" s "-3-dark-mode"))
         :dark:active:color (keyword (str "$foreground-color-" s "-3-dark-mode"))}
        
        soft-inert
        {
         :bgc               (keyword (str "$background-color-" s "-soft-3"))
         :hover:bgc         (keyword (str "$background-color-" s "-soft-3"))
         :active:bgc        (keyword (str "$background-color-" s "-soft-3"))

         :dark:bgc          (keyword (str "$background-color-" s "-soft-3-dark-mode"))
         :dark:hover:bgc    (keyword (str "$background-color-" s "-soft-3-dark-mode"))
         :dark:active:bgc   (keyword (str "$background-color-" s "-soft-3-dark-mode"))}
        ]

   [{"[data-ks-surface= \"transparent\"]"                  
     base


     "[data-ks-surface= \"transparent\"][data-ks-inert]"   
     base-inert


     "[data-ks-surface= \"minimal\"]" 
     (merge base
            {:hover:bgc       (keyword (str "$background-color-" s "-soft"))
             :active:bgc      (keyword (str "$background-color-" s "-soft-2"))
             :dark:hover:bgc  (keyword (str "$background-color-" s "-soft-dark-mode"))
             :dark:active:bgc (keyword (str "$background-color-" s "-soft-2-dark-mode"))})


     "[data-ks-surface= \"outline\"]" 
     (merge base
            {:hover:bgc       (keyword (str "$background-color-" s "-soft"))
             :active:bgc      (keyword (str "$background-color-" s "-soft-2"))
             :dark:hover:bgc  (keyword (str "$background-color-" s "-soft-dark-mode"))
             :dark:active:bgc (keyword (str "$background-color-" s "-soft-2-dark-mode"))})


     "[data-ks-surface= \"minimal\"][data-ks-inert]"
     (merge base-inert
            {:hover:bgc       "transparent"
             :active:bgc      "transparent"
             :dark:hover:bgc  "transparent"
             :dark:active:bgc "transparent"})


     "[data-ks-surface= \"outline\"][data-ks-inert]"
     (merge base-inert
            {:hover:bgc       "transparent"
             :active:bgc      "transparent"
             :dark:hover:bgc  "transparent"
             :dark:active:bgc "transparent"})


     "[data-ks-surface= \"faint\"]"                        
     (merge base
            {:bgc             (keyword (str "$background-color-" s "-soft"))
             :dark:bgc        (keyword (str "$background-color-" s "-soft-dark-mode"))
             :hover:bgc       (keyword (str "$background-color-" s "-soft-2"))
             :dark:hover:bgc  (keyword (str "$background-color-" s "-soft-2-dark-mode"))
             :active:bgc      (keyword (str "$background-color-" s "-soft-3"))
             :dark:active:bgc (keyword (str "$background-color-" s "-soft-3-dark-mode"))})


     "[data-ks-surface= \"faint\"][data-ks-inert]"         
     (merge base-inert
            {:bgc             (keyword (str "$background-color-" s "-soft"))
             :hover:bgc       (keyword (str "$background-color-" s "-soft"))
             :active:bgc      (keyword (str "$background-color-" s "-soft"))
             :dark:bgc        (keyword (str "$background-color-" s "-soft-dark-mode"))
             :dark:hover:bgc  (keyword (str "$background-color-" s "-soft-dark-mode"))
             :dark:active:bgc (keyword (str "$background-color-" s "-soft-dark-mode"))})


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


     "[data-ks-surface= \"solid-classic\"]"                    {:--classic-trim-color (keyword (str "$" s "-550"))
                                                                :bgc                  (keyword (str "$background-color-" s "-hard"))
                                                                :hover:bgc            (keyword (str "$background-color-" s "-hard-2"))
                                                                :active:bgc           (keyword (str "$background-color-" s "-hard-3"))}

     "[data-ks-surface= \"solid-classic\"][data-ks-inert]"  {:bgc        (keyword (str "$background-color-" s "-hard"))
                                                             :hover:bgc  (keyword (str "$background-color-" s "-hard"))
                                                             :active:bgc (keyword (str "$background-color-" s "-hard"))}

     "dark:[data-ks-surface= \"solid-classic\"]"               {:bgc        (keyword (str "$background-color-" s "-hard-dark-mode"))
                                                                :hover:bgc  (keyword (str "$background-color-" s "-hard-2-dark-mode"))
                                                                :active:bgc (keyword (str "$background-color-" s "-hard-3-dark-mode"))}

     "dark:[data-ks-surface= \"solid-classic\"][data-ks-inert]" {:bgc        (keyword (str "$background-color-" s "-hard-dark-mode"))
                                                                 :hover:bgc  (keyword (str "$background-color-" s "-hard-dark-mode"))
                                                                 :active:bgc (keyword (str "$background-color-" s "-hard-dark-mode"))}

     "dark:[data-ks-surface= \"solid\"]"                       {:bgc        (keyword (str "$background-color-" s "-hard-dark-mode"))
                                                                :hover:bgc  (keyword (str "$background-color-" s "-hard-2-dark-mode"))
                                                                :active:bgc (keyword (str "$background-color-" s "-hard-3-dark-mode"))}
     
     "dark:[data-ks-surface= \"solid\"][data-ks-inert]"        {:bgc        (keyword (str "$background-color-" s "-hard-dark-mode"))
                                                                :hover:bgc  (keyword (str "$background-color-" s "-hard-dark-mode"))
                                                                :active:bgc (keyword (str "$background-color-" s "-hard-dark-mode"))}
     }]))
