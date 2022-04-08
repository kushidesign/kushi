(ns ^:dev/always kushi.basetheme)


(def base-theme
  {:button {:default {:bgc        :#eee
                      :hover:bgc  :heliotrope
                      :color      :black
                      :dark:color :red}
            :secondary {:bgc        :#eee
                        :hover:bgc  :heliotrope
                        :color      :black
                        :dark:color :red}
            :primary  {:bgc        :#eee
                       :hover:bgc  :heliotrope
                       :color      :black
                       :dark:color :red}}})


.kushi-button.kushi-secondary {background-color: var(--kushi-button-secondary_background-color), #eee;
                               color:            var(--kushi-button-secondary_color), #black;}
.kushi-button.kushi-secondary:hover {background-color: heliotrope}


(def base-theme
  {:button           {:bgc        :#eee
                      :hover:bgc  :heliotrope
                      :color      :black
                      :dark:color :red
                      :_.goo:p    :10px}
   :secondary-button {:bgc       :#eee
                      :hover:bgc :orange
                      :color     :blue}
   :primary-button   {:color :white
                      :dark:color :black
                      :bgc :black
                      :dark:bgc :#eee
                      }
   })
