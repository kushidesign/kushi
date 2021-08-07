(ns kushi.scales )

(defonce tailwind-widths
  {"0" :0px
   "0.5" :0.125rem
   "1" :0.25rem
   "1.5" :0.375rem
   "2" :0.5rem
   "2.5" :0.625rem
   "3" :0.75rem
   "3.5" :0.875rem
   "4" :1rem
   "5" :1.25rem
   "6" :1.5rem
   "7" :1.75rem
   "8" :2rem
   "9" :2.25rem
   "10" :2.5rem
   "11" :2.75rem
   "12" :3rem
   "14" :3.5rem
   "16" :4rem
   "20" :5rem
   "24" :6rem
   "28" :7rem
   "32" :8rem
   "36" :9rem
   "40" :10rem
   "44" :11rem
   "48" :12rem
   "52" :13rem
   "56" :14rem
   "60" :15rem
   "64" :16rem
   "72" :18rem
   "80" :20rem
   "96" :24rem })

(defonce scales
  {:tachyons
   {:width {"0" :0 "1" :1rem "2" :2rem "3" :4rem "4" :8rem "5" :16rem}
    :border-width {"0" :0 "1" :.125rem "2" :.25rem "3" :.5rem  "4" :1rem "5" :2rem}
    :font-size {"1" :3rem "2" :2.25rem "3" :1.5rem "4" :1.25rem "5" :1rem "6" :.875rem "7" :.75rem }
    :spacing {"0" :0
              "xs" :.25rem
              "sm" :.5rem
              "md" :1rem
              "lg" :2rem
              "xl" :4rem
              "xxl" :8rem
              "xxxl" :16rem }}
   :tailwind
   {:width tailwind-widths
    :border-width {"0" :0 "2" :2px "4" :4px "8" :8px }
    :font-size {"xs" :0.75rem "sm" :0.875rem "base" :1rem "lg" :1.125rem "xl" :1.25rem "2xl" :1.5rem "3xl" :1.875rem "4xl" :2.25rem "5xl" :3rem "6xl" :3.75rem "7xl" :4.5rem "8xl" :6rem "9xl" :8rem }
    :spacing tailwind-widths}})

(defonce scaling-map
  {:width :width
   :border :border-width
   :font-size :font-size
   :padding :spacing
   :margin :spacing})
