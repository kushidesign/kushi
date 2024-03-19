(ns kushi.playground.demobox.decorate
  (:require
   [clojure.walk :as walk]
   [domo.core :as dom]
   [kushi.playground.demobox.defs :refer [variants-by-category]]
   [kushi.playground.util :as util :refer-macros (keyed)]))

(defn sx-attr+children [coll]
  (when (coll? coll)
    (let [[a & xs] coll
          attr     (cond (map? a) a
                         (and (list? a) (= (first a) 'sx)) a)]
      [attr (if attr xs coll)])))

(defn- current+preview-classes
  [{:keys [active-controls-by-type
           defaults
           hide-default-classes?]}]
  (let [current-classes  (vals active-controls-by-type)
        controls-by-type (reduce (fn [acc [k v]]
                                   (let [dv*              (k defaults)
                                         dv               (if (keyword? dv*) (name dv*) (str dv*))
                                         matches-default? (= v dv)]
                                     (cond
                                       (false? hide-default-classes?)
                                       (assoc acc k v)
                                       matches-default?
                                       acc
                                       :else
                                       (assoc acc k v))))
                                 {}
                                 active-controls-by-type)
        preview-classes  (filter #(not= % "none") (vals controls-by-type))]
    (keyed current-classes preview-classes)))


(defn utility-classes-into-dom
  [{:keys [component-id utility-class-target active-controls-by-type] :as m}]
  (when-let [dom-el (dom/el-by-id component-id)]
    (when-let [els (.querySelectorAll dom-el (str ".kushi-" utility-class-target ":not(.material-icons)"))]
    ;; (js/console.log "utility-class-target: " utility-class-target)
    ;; (js/console.log (js/document.getElementById component-id))
    ;; (js/console.log els)
      (doseq [el els]
        (let [cl (.-classList el)]
          ;; (js/console.log cl)
          ;; (println @*controls-by-type)
          (doseq [[category class] active-controls-by-type
                  :when            class
                  ]
            ;; (println (keyed category class))
            ;; (js/console.log (->> category keyword variants-by-category (map name)))
            (when-let [to-remove* (->> category keyword variants-by-category (map name))]
              (keyed component-id utility-class-target category class to-remove*)
              ; figure out controls by type
              (apply dom/remove-class el to-remove*)
              (.add cl (name class))))))
      (when (= utility-class-target "slider")
        (dom/set! (.querySelector dom-el "input") :value 0)))))


(defn utility-classes-into-snippet
  [coll
   {:keys [nm] :as m}]
  (let [{:keys [current-classes preview-classes] :as cm} (current+preview-classes m)
        ret (if (empty? current-classes)
              coll
              (walk/postwalk
               #(if (and (vector? %) (= (first %) (symbol nm)))
                  (let [[compo & more]               %
                        [attr children]              (sx-attr+children more)
                        utility-classes              (keep (fn [x] (keyword (str "." x))) preview-classes)
                        rest-of-attr                 (if (map? attr) [attr] (rest attr))
                        utility-classes+rest-of-attr (concat utility-classes rest-of-attr)
                        ret                          (into []
                                                           (remove nil?
                                                                   (concat [compo
                                                                            (when (seq utility-classes+rest-of-attr)
                                                                              (cons 'sx (concat utility-classes rest-of-attr)))]
                                                                           children)))]
                    #_(js/console.log
                       :postwalk
                       (keyed %
                              compo
                              more
                              attr
                              children
                              utility-classes
                              rest-of-attr utility-classes+rest-of-attr))
                    ret)
                  %)
               coll))]
    (keyed coll m cm ret)
    ret))
