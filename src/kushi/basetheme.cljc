(ns ^:dev/always kushi.basetheme)

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
