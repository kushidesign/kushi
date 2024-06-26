(ns kushi.demo.utility-classes.placement
  (:require
   [kushi.core :refer [sx]]
   [kushi.utils :as kushi.utils]))

(defn absolute-placement []
  (let [spot (fn [k]
               [:div.spot.relative.xxxsmall.semi-bold.code #_.uppercase
                {:class [k :wireframe]}
                [:span.absolute-centered (kushi.utils/kebab->shorthand k)]])]
    [:div (sx :.debug-blue
              :.flex-col-fs
              :gap--6rem
              :mbs--100px
              :p--4rem
              :$sz--300px
              :>div:w--$sz
              :>div:h--$sz
              :$spot-sz--50px
              :&_.spot:w--$spot-sz
              :&_.spot:h--$spot-sz
              :&_.spot:bgc--transparent!important)

      ;; Just for trying stuff out - paste here
     (into [:div (sx :.debug-yellow :.relative )
            [:div.absolute-centered.capitalize "boba"]]
           (for [k [
                    :top-right-outside
                    :top-right-corner-outside
                    :right-top-outside
                    :top-left-corner-outside
                    :top-left-outside
                    :left-top-outside

                    :bottom-right-outside
                    :bottom-right-corner-outside
                    :right-bottom-outside
                    :bottom-left-corner-outside
                    :bottom-left-outside
                    :left-bottom-outside

                    :left-outside
                    :right-outside
                    :top-outside
                    :bottom-outside
                    ]]
             [spot k]))

     (into [:div (sx :.debug-lime :.relative )
            [:div.absolute-centered.capitalize "line"]]
           (for [k [:top-right
                    :top-right-corner
                    :right-top

                    :top-left-corner
                    :top-left
                    :left-top

                    :bottom-right
                    :bottom-right-corner
                    :right-bottom

                    :bottom-left-corner
                    :bottom-left
                    :left-bottom
                    
                    :left
                    :right
                    :top
                    :bottom
                    ]]
             [spot k]))
     (into [:div (sx :.debug-orange :.relative )
            [:div.absolute-centered.capitalize "inside"]]
           (for [k [

                    :top-right-corner-inside
                    :top-left-corner-inside
                    :bottom-right-corner-inside
                    :bottom-left-corner-inside
                    
                    :left-inside
                    :right-inside
                    :top-inside
                    :bottom-inside
                    ]]
             [spot k]))
     ]))
