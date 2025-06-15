(ns kushi.css.build.colorways)

(defn colorway-selector [s]
  (str "[data-kushi-colorway=\"" s "\"]"))


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

   [{"[data-kushi-surface= \"transparent\"]"                   base

     "[data-kushi-surface= \"transparent\"][data-kushi-inert]" base-inert

     "[data-kushi-surface= \"minimal\"]"                       (merge base
                                                                      {:hover:bgc       (keyword (str "$background-color-" s "-soft"))
                                                                       :active:bgc      (keyword (str "$background-color-" s "-soft-2"))
                                                                       :dark:hover:bgc  (keyword (str "$background-color-" s "-soft-dark-mode"))
                                                                       :dark:active:bgc (keyword (str "$background-color-" s "-soft-2-dark-mode"))})

     "[data-kushi-surface= \"minimal\"][data-kushi-inert]"     base-inert

     "[data-kushi-surface= \"faint\"]"                         {:bgc             (keyword (str "$background-color-" s "-soft"))
                                                                :dark:bgc        (keyword (str "$background-color-" s "-soft-dark-mode"))
                                                                :hover:bgc       (keyword (str "$background-color-" s "-soft-2"))
                                                                :dark:hover:bgc  (keyword (str "$background-color-" s "-soft-2-dark-mode"))
                                                                :active:bgc      (keyword (str "$background-color-" s "-soft-3"))
                                                                :dark:active:bgc (keyword (str "$background-color-" s "-soft-3-dark-mode"))}

     "[data-kushi-surface= \"faint\"][data-kushi-inert]"       {:bgc             (keyword (str "$background-color-" s "-soft"))
                                                                :hover:bgc       (keyword (str "$background-color-" s "-soft"))
                                                                :active:bgc      (keyword (str "$background-color-" s "-soft"))
                                                                :dark:bgc        (keyword (str "$background-color-" s "-soft-dark-mode"))
                                                                :dark:hover:bgc  (keyword (str "$background-color-" s "-soft-dark-mode"))
                                                                :dark:active:bgc (keyword (str "$background-color-" s "-soft-dark-mode"))}

     "[data-kushi-surface= \"soft\"]"                          soft

     "[data-kushi-surface= \"soft\"][data-kushi-inert]"        soft-inert

     "[data-kushi-surface= \"soft-classic\"]"                  (merge soft
                                                                      {:--classic-trim-color      (keyword (str "$" s "-150"))
                                                                       :--classic-trim-color-dark (keyword (str "$" s "-800"))})

     "[data-kushi-surface= \"solid\"]"                         {:bgc        (keyword (str "$background-color-" s "-hard"))
                                                                :hover:bgc  (keyword (str "$background-color-" s "-hard-2"))
                                                                :active:bgc (keyword (str "$background-color-" s "-hard-3"))}

     "[data-kushi-surface= \"solid\"][data-kushi-inert]"       {:bgc        (keyword (str "$background-color-" s "-hard"))
                                                                :hover:bgc  (keyword (str "$background-color-" s "-hard"))
                                                                :active:bgc (keyword (str "$background-color-" s "-hard"))}

     "[data-kushi-surface= \"solid-classic\"]"                 {:--classic-trim-color (keyword (str "$" s "-550"))
                                                                :bgc                  (keyword (str "$background-color-" s "-hard"))
                                                                :hover:bgc            (keyword (str "$background-color-" s "-hard-2"))
                                                                :active:bgc           (keyword (str "$background-color-" s "-hard-3"))}

     "[data-kushi-surface= \"solid-classic\"][inert]"          {:bgc                  (keyword (str "$background-color-" s "-hard"))
                                                                :hover:bgc            (keyword (str "$background-color-" s "-hard"))
                                                                :active:bgc           (keyword (str "$background-color-" s "-hard"))}

     "dark:[data-kushi-surface= \"solid-classic\"]"            {:bgc        (keyword (str "$background-color-" s "-hard-dark-mode"))
                                                                :hover:bgc  (keyword (str "$background-color-" s "-hard-2-dark-mode"))
                                                                :active:bgc (keyword (str "$background-color-" s "-hard-3-dark-mode"))}

     "dark:[data-kushi-surface= \"solid-classic\"][inert]"     {:bgc        (keyword (str "$background-color-" s "-hard-dark-mode"))
                                                                :hover:bgc  (keyword (str "$background-color-" s "-hard-dark-mode"))
                                                                :active:bgc (keyword (str "$background-color-" s "-hard-dark-mode"))}

     "dark:[data-kushi-surface= \"solid\"]"                    {:bgc        (keyword (str "$background-color-" s "-hard-dark-mode"))
                                                                :hover:bgc  (keyword (str "$background-color-" s "-hard-2-dark-mode"))
                                                                :active:bgc (keyword (str "$background-color-" s "-hard-3-dark-mode"))}
     
     "dark:[data-kushi-surface= \"solid\"][inert]"             {:bgc        (keyword (str "$background-color-" s "-hard-dark-mode"))
                                                                :hover:bgc  (keyword (str "$background-color-" s "-hard-dark-mode"))
                                                                :active:bgc (keyword (str "$background-color-" s "-hard-dark-mode"))}
     }]))
