(ns kushi.devtools.dom.overlay
  (:require
   [kushi.core :refer [sx inject! defclass]]))

;; TODO - USE before and/or after pseudos with svg bg images to do the coloring of the margins and padding


(defclass padding-guide
  [:bgc "hsl(116.92deg 33.53% 66.02% / 53%)"])

(defclass margin-guide
  [:bgc "hsl(29deg 84.76% 69.97% / 68%)"])

(defn mb-corners [misw miew]
  [:<>
   [:div (sx 'mbis-guide-corner
             :.margin-guide
             :.west-outside
             :h--100%
             [:w misw])]
   [:div (sx 'mbie-guide-corner
             :.margin-guide
             :.east-outside
             :h--100%
             [:w miew])]])

(defn overlay [el]
  (let [mbs 20
        mbe 40
        mis 60
        mie 30
        pbs 8
        pie 16
        pbe 32
        pis 44]
    [:div (sx :.absolute-fill
              :.flex-col-fs
              :d--none
              :w--100%
              :h--100%
              [:$main-color "hsl(211deg 54.5% 65.08% / 74%)"]
              [:$mbs (str mbs "px")]
              [:$mbe (str mbe "px")]
              [:$mis (str mis "px")]
              [:$mie (str mie "px")]
              [:$pbs (str pbs "px")]
              [:$pbe (str pbe "px")]
              [:$pis (str pis "px")]
              [:$pie (str pie "px")])

     [:div (sx 'mbs-guide
               :.margin-guide
               :.north-outside
               :w--100%
               [:h :$mbs])
      [mb-corners (str mis "px") (str mie "px")]]

     [:div (sx 'mie-guide
               :.margin-guide
               :.east-outside
               :h--100%
               [:w :$mie])]

     [:div (sx 'mbe-guide
               :.margin-guide
               :.south-outside
               :w--100%
               [:h :$mbe])
      [mb-corners (str mis "px") (str mie "px")]]

     [:div (sx 'mis-guide
               :.margin-guide
               :.west-outside
               :h--100%
               [:w :$mis])]

     [:div (sx 'pbs-guide
               :.padding-guide
               :w--100%
               [:h :$pbs])]

     [:div (sx 'main-el-guid
               :w--100%
               :.flex-row-fs
               :ai--stretch
               :.no-shrink
               :.grow
               :height--auto)
      [:div (sx 'pis-guide-block
                :.padding-guide
                :h--100%
                [:width :$pis])]
      [:div (sx :.grow
                :.no-shrink
                :outline--1px:dashed:$purple-700
                :outline-offset---1px
                [:bgc :$main-color])]
      [:div (sx 'pie-guide-block
                :.padding-guide
                :h--100%
                [:width :$pie])]]

     [:div (sx 'pbe-guide-block
               :.padding-guide
               :w--100%
               [:h :$pbe])]]))
