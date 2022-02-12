(ns ^:dev/always kushi.theme
  #_(:require))

(def presets
  {:button {:pill :9999px}}  )

;; TODO try pumping the map through a kushi parser?
(def light
  {:border-radius :0px
   :panel {:border-radius 0
           :border-color :pink
           :border-width :0px
           :border-style :solid}
   :button {:border-radius (-> presets :button :pill)}
   :button-group {:>button {:margin "0 0.5rem"}}})

(def theme (atom light))

(defn get-style [ks default]
  #_(js/console.log (get-in theme [(last ks)]))
  ;; TODO warn user if reverse order
  (or (get-in @theme ks)
      (get-in @theme [(last ks)])
      default))
