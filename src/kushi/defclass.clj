(ns kushi.defclass
  (:require
   [clojure.spec.alpha :as s]
   [kushi.parstub :refer [? !? ?+ !?+]]
   [kushi.arguments :as arguments]
   [kushi.parse :as parse]
   [kushi.selector :as selector]
   [kushi.specs :as specs]
   [kushi.cssvarspecs :as cssvarspecs]
   [kushi.state :as state]
   [kushi.utils :as util :refer [keyed]]
   #_[kushi.ui.theme :as theme]))

(defn defclass-noop? [sym args]
  ;; For skipping defclasses & overrides from theming
  (and (nil? sym) (= args '(nil))))

(defn sym->classtype [sym]
  (let [meta* (some-> sym meta)]
    (cond
      (:kushi-utility meta*)          :kushi-utility
      (:kushi-utility-override meta*) :kushi-utility-override
      (:override meta*)               :user-utility-override
      :else                           :user-utility)))

(defn style-map->vecs
  [m]
  (let [kw->dotkw #(some->> (when (keyword? %) %)
                            name
                            (str ".")
                            keyword)
        classes*  (some->> m :. util/into-coll)
        classes   (map #(cond (seq? %)
                              (map (fn [x] (if (keyword? x) (kw->dotkw x) x)) %)
                              (keyword? %)
                              (kw->dotkw %))
                       classes*)
        ret (into [] (concat classes (into [] (dissoc m :.))))]
    #_(? (keyed classes* classes ret))
    ret))


(defn- hydrate-literal-css-vars [coll]
  (mapv #(if (s/valid? ::cssvarspecs/tokenized-style-with-css-var %)
           (arguments/style-kw-with-cssvar->tuple %)
           %)
        coll))

(defn- hydrated-defclass
  [classname classtype coll*]
  (let [{:keys [selector
                selector*]} (selector/selector-name
                             {:defclass-name classname
                              :atomic-class? (contains? #{:kushi-utility :kushi-utility-override}
                                                        classtype)})
        hydrated-styles*    (parse/with-hydrated-classes coll*)
        hydrated-styles     (hydrate-literal-css-vars hydrated-styles*)
        tokenized-styles    (mapv (partial parse/kushi-style->token selector*) hydrated-styles)
        grouped-by-mqs      (parse/grouped-by-mqs tokenized-styles)
        garden-vecs         (parse/garden-vecs grouped-by-mqs selector)
        ret                 (keyed selector selector*
                                   hydrated-styles
                                   garden-vecs)]
    (!?+ (keyed hydrated-styles tokenized-styles grouped-by-mqs garden-vecs ret))
    ret))



(defn defclass* [{:keys [sym args classtype]}]
  (let [sym                      (if (keyword? sym) (symbol sym) sym)
        defclass-name            (keyword sym)
        last*                    (last args)
        style-map                (when (map? last*) last*)
        style-tokens             (if style-map (drop-last args) args)
        style-map-vecs           (some-> style-map style-map->vecs)
        coll                     (concat style-tokens style-map-vecs)
        {:keys
         [valid-styles-from-attrs
          valid-styles-from-tokens
          invalid-style-args]}     (arguments/validate-args args style-tokens {:style style-map})

        invalid-args             (or
                                  (when-not (s/valid? ::specs/defclass-name sym) ^:classname [sym])
                                  invalid-style-args)
        styles                   (into [] (concat valid-styles-from-tokens valid-styles-from-attrs))
        {:keys [selector
                selector*
                hydrated-styles
                garden-vecs]}    (hydrated-defclass defclass-name classtype styles)
        warnings                 (when invalid-args
                                   {:defclass-name defclass-name
                                    :args          (apply vector args)
                                    :invalid-args  invalid-args})
        ret                       {:n             defclass-name
                                  :args          hydrated-styles}]

    (!?+ (keyed last* style-map style-tokens style-map-vecs coll))
    ;; (?+ styles)

    ;; TODO remove or consolidate :n and :args entry
    ;; ...they are redundant with :defclass-name and :hydrated-styles
    (merge {:n    defclass-name
            :args hydrated-styles}
           (keyed defclass-name
                  coll
                  invalid-args
                  hydrated-styles
                  warnings
                  garden-vecs
                  classtype
                  selector
                  selector*))))


(defn defclass-dispatch [{:keys [sym] :as m*}]
  (reset! state/current-macro :defclass)
  (let [classtype  (sym->classtype sym)
        current-op (assoc m* :macro :defclass :classtype classtype)
        _          (reset! state/current-op current-op)
        cache-map  (state/cached current-op)
        result     (or (:cached cache-map) (defclass* current-op))]
    (state/add-utility-class! result)
    (state/update-cache! cache-map result)
    (?+ :xx classtype)))
