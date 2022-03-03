(ns ^:dev/always kushi.theme
  (:require
   [kushi.config :refer [user-config]]
   [kushi.arguments :as arguments]
   [kushi.utils :as util :refer [keyed]]
   [kushi.specs :as specs]
   [kushi.state :as state]
   [kushi.stylesheet :as stylesheet]
   [medley.core :refer [filter-keys map-kv-keys]]
   [clojure.spec.alpha :as s]
   [clojure.string :as string]
  ;;  [par.core    :refer [? !? ?+ !?+]]
   ))

(defn resolve-user-theme
  ([x]
   (resolve-user-theme x :user))
  ([x kw]
   (when x
     (try (let [[ns-name themevar] (string/split (str x) #"/")]
            (require (symbol ns-name) :reload)
            (let [bar (find-ns (symbol ns-name))]
              ;; #_(?+ bar)
              ;; #_(?+ (symbol themevar))
              (var-get (ns-resolve bar (symbol themevar)))))
          (catch Exception
                 e
            (println
             (str
              "\n[kushi.core/theme!][ERROR]\n"
              (if (= :user kw)
                "Possibly a bad theme ns specified in kushi.edn user config -> "
                "Can't resolve base kushi ux theme -> ") (str x)
              "\n" (.getMessage e) "\n")))))))

(defn garden-vecs-by-component [m]
  (reduce
   (fn [acc [k style]]
     (let [ancestor* (util/stacked-kw-head k)
           ancestor  (when (= "dark" ancestor*) (str "." ancestor*))
           ident     (if ancestor
                       (-> k util/stacked-kw-tail keyword)
                       k)]
      (conj acc
            (-> [(assoc (keyed style ident ancestor) :prefix :kui-)]
                (arguments/new-args nil)
                :garden-vecs))))
   []
   m))


(defn extract-darks [m]
  (reduce
   (fn [acc [ui m]]
     (let [dark-key?  #(-> % name (string/starts-with? "dark"))
           darks*     (filter-keys dark-key? m)
           dark-ks    (keys darks*)
           darks      (when (seq darks*)
                        (map-kv-keys (fn [k _]
                                       (-> k util/stacked-kw-tail keyword))
                                     darks*))
           darks-map  (when darks {(->> ui name (str "dark:") keyword) darks})
           lights     (apply dissoc m dark-ks)
           lights-map (when (seq lights) {ui lights})]
       (merge acc lights-map darks-map)))
   {}
   m))

(def base-theme-sym* 'kushi.basetheme/base-theme)

(defmacro theme! []
  (when-let [base-theme (resolve-user-theme base-theme-sym*)]
    (let [merged      (some->>
                       user-config
                       :theme
                       resolve-user-theme
                       (util/deep-merge base-theme))]

      (when merged
        (if (s/valid? ::specs/kushi-theme-map merged)
          (let [+darks (extract-darks merged)
                gvecs  (->> +darks
                            garden-vecs-by-component
                            (apply concat))
                inj    (stylesheet/garden-vecs-injection gvecs)]
            ;; (!?+ +darks)
            ;; (!?+ inj)
            (state/add-theme-styles! gvecs)
            `(when (clojure.core/seq ~inj)
               (kushi.core/inject-css* ~inj "_kushi-rules-shared_")))
          (do
        ;; TODO format!
            (println "\n[kushi.core/theme!][WARNING] - Kushi theme map does not conform to spec:")
            (s/explain ::specs/kushi-theme-map merged)
            (println "\n")))))))

;; (def presets
;;   {:button {:pill :9999px}
;;    :colors {:gray100 :eeeeee
;;             :gray200 :#e2e2e2
;;             :gray900 "rgba(20, 20, 20)"}})

;; (defn deep-merge
;;   "Recursively merges maps."
;;   [& maps]
;;   (letfn [(m [& xs]
;;             (if (some #(and (map? %) (not (record? %))) xs)
;;               (apply merge-with m xs)
;;               (last xs)))]
;;     (reduce m maps)))

;; (def base
;;   {:border-radius    :0px
;;    :panel            {:border-radius 0
;;                       :border-color  :pink
;;                       :border-width  :0px
;;                       :border-style  :solid}
;;    :button           {:border-radius (-> presets :button :pill) }
;;    :button:hover     {:opacity :0.7}
;;    :button-group     {:>button {:margin "0 0.5rem"}}})

;; (def light
;;   (deep-merge
;;    base
;;    {:background-color       :white
;;     :color                  :black
;;     :button                 {:background-color :white}
;;     :button:hover           {:background-color (-> presets :colors :gray100)}
;;     :primary-button         {:color            :white
;;                              :background-color :black}
;;     :secondary-button       {:color            :black
;;                              :background-color (-> presets :colors :gray100)}
;;     :secondary-button:hover {:background-color (-> presets :colors :gray200)}}))


;; (def dark
;;   (deep-merge
;;    base
;;    {:background-color "rgba(20, 20, 20)"
;;     :color            :#e2e2e2
;;     :primary-button   {:color :black
;;                        :background-color :white
;;                        :hover:background-color (-> presets :colors :gray100)}}))

;; (def theme (atom light))

