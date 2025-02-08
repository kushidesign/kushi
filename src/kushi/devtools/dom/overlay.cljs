(ns kushi.devtools.dom.overlay
  (:require
   [kushi.core :refer [sx css defcss css-vars-map]]))

;; TODO - USE before and/or after pseudos with svg bg images to do the coloring of the margins and padding


(defcss ".padding-guide"
  [:bgc "hsl(116.92deg 33.53% 66.02% / 53%)"])

(defcss ".margin-guide"
  [:bgc "hsl(29deg 84.76% 69.97% / 68%)"])

(defn mb-corners [misw miew]
  [:<>
   [:div (sx :.mbis-guide-corner
             :.margin-guide
             :.left-outside
             :h--100%
             [:w misw])]
   [:div (sx :.mbie-guide-corner
             :.margin-guide
             :.right-outside
             :h--100%
             [:w miew])]])

(defn overlay [el]
  (let [mbs (str 20 "px")
        mbe (str 40 "px")
        mis (str 60 "px")
        mie (str 30 "px")
        pbs (str 8 "px")
        pie (str 16 "px")
        pbe (str 32 "px")
        pis (str 44 "px")]
    [:div {:style (css-vars-map mbs
                                mbe
                                mis
                                mie
                                pbs
                                pie
                                pbe
                                pis)
           :class (css :.absolute-fill
                       :.flex-col-fs
                       :d--none
                       :w--100%
                       :h--100%
                       {:--main-color "hsl(211deg 54.5% 65.08% / 74%)"
                        :--mbs :$mbs
                        :--mbe :$mbe
                        :--mis :$mis
                        :--mie :$mie
                        :--pbs :$pbs
                        :--pbe :$pbe
                        :--pis :$pis
                        :--pie :$pie})}

     [:div {:class (css :.mbs-guide
                        :.margin-guide
                        :.top-outside
                        :w--100%
                        [:h :$mbs])}
      [mb-corners (str mis "px") (str mie "px")]]

     [:div (sx :.mie-guide
               :.margin-guide
               :.right-outside
               :h--100%
               [:w :$mie])]

     [:div (sx :.mbe-guide
               :.margin-guide
               :.bottom-outside
               :w--100%
               [:h :$mbe])
      [mb-corners (str mis "px") (str mie "px")]]

     [:div (sx :.mis-guide
               :.margin-guide
               :.left-outside
               :h--100%
               [:w :$mis])]

     [:div (sx :.pbs-guide
               :.padding-guide
               :w--100%
               [:h :$pbs])]

     [:div (sx :.main-el-guid
               :w--100%
               :.flex-row-fs
               :ai--stretch
               :.no-shrink
               :.grow
               :height--auto)
      [:div (sx :.pis-guide-block
                :.padding-guide
                :h--100%
                [:width :$pis])]
      [:div (sx :.grow
                :.no-shrink
                :outline--1px:dashed:$purple-700
                :outline-offset---1px
                [:bgc :$main-color])]
      [:div (sx :.pie-guide-block
                :.padding-guide
                :h--100%
                [:width :$pie])]]

     [:div (sx :.pbe-guide-block
               :.padding-guide
               :w--100%
               [:h :$pbe])]]))
