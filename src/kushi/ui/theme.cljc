(ns ^:dev/always kushi.ui.theme
  (:require
   [kushi.config :refer [user-config]]
   [kushi.utils :as util :refer [keyed]]
   [kushi.printing2 :as printing2]
   [kushi.specs2 :as specs2]
   [kushi.ui.basetheme :as basetheme]
   [kushi.ui.utility :refer [utility-class-ks]]
   [expound.alpha :as expound]
   [clojure.spec.alpha :as s]
   [clojure.string :as string]
   [io.aviso.ansi :as ansi]))

(defn resolve-user-theme
  ([x]
   (resolve-user-theme x :user))
  ([x kw]
   (when x
     (try (let [[ns-name themevar] (string/split (str x) #"/")]
            (require (symbol ns-name) :reload)
            (let [bar (find-ns (symbol ns-name))]
              (var-get (ns-resolve bar (symbol themevar)))))
          (catch Exception
                 e
            (println
             (str
              "\n[kushi.core/theme!][ERROR]\n"
              (if (= :user kw)
                "Possibly a bad theme ns specified in kushi.edn user config -> "
                "Can't resolve base kushi ux theme -> ")
              (str x)
              (when (= :user kw)
                (str
                 "\n"
                 "Or possibly a malformed def -> "
                 (last (string/split (str x) #"/"))))
              "\n" (.getMessage e) "\n")))))))



(defn inner3 [[css-prop css-val]]
  (let [css-val (if (number? css-val) css-val (name css-val))]
    [(name css-prop) css-val]))


(defn remove-global-selector
  "Simple-bad-global-selector-key warning."
  [m]
  (if (and (map? m) (:* m))
    (do (printing2/simple-bad-global-selector-key-warning {:form-meta    {:file "kushi.ui.theme/theme"}
                                                           :invalid-args {:ui (:* m)}})
        (dissoc m :*))
    m))


(defn by-component
  [acc [kui-key m]]
  (let [flat     (mapv inner3 m)
        stylemap (reduce (fn [acc [css-prop x]]
                           (if (vector? x)
                             (let [[css-var fallback] x]
                               (->> fallback
                                    util/maybe-wrap-css-var
                                    (util/s->cssvar css-var)
                                    (assoc acc css-prop)))
                             (assoc acc css-prop (util/maybe-wrap-css-var x))))
                         {}
                         flat)
        ret      (conj acc
                       [{:style                stylemap
                         :kushi-selector       (name kui-key)
                         :kushi-theme-selector (name kui-key)}])]
    #_(println :by-component-reduce (keyed flat stylemap ret))
    ret))


(defn varize-utility-classes* [m]
  (reduce (fn [acc [k v]]
            (assoc acc
                   k
                   (-> v
                       util/stringify
                       util/maybe-wrap-css-var)))
          {}
          m))

(defn varize-utility-classes [overrides]
  (reduce (fn [acc [k m]]
            (assoc acc k (varize-utility-classes* m)))
          {}
          overrides))


(def google-font-maps
  {"Fira Code" {:family "Fira Code"
                :styles {:normal [400 500]}}
   "Inter"     {:family "Inter"
                :styles {:normal [500 700]}}})

(defn font-loading-opts
  "Provides an argument map for kushi.core.ui/init-typography!"
  [m]
  (let [default-code      (when-not (false? (:use-default-code-font-family? m))
                            (get google-font-maps "Fira Code"))
        default-primary   (when-not (false? (:use-default-primary-font-family? m))
                            (get google-font-maps "Inter"))
        google-font-maps1 (when (or default-code default-primary)
                            [default-code default-primary])
        google-font-maps2 (map (fn [s] {:family s :styles {:normal :all :italic :all}}) (:google-fonts* m))
        google-font-maps  (->> m
                               :google-fonts
                               seq
                               (concat google-font-maps1 google-font-maps2)
                               (remove nil?)
                               (into []))
        ;; Always adds system font stack unless `:add-system-font-stack?` is set to `false` in theme
        add-system-font-stack? (-> m :add-system-font-stack? false? not)
        system-font-stack-weights* (-> m :system-font-stack-weights)
        system-font-stack-weights  (if (and (seq system-font-stack-weights*)
                                            (every? int? system-font-stack-weights*))
                                     system-font-stack-weights*
                                     [])]
    (keyed google-font-maps add-system-font-stack? system-font-stack-weights)))


(defn merged-theme-props [base-theme-map user-theme-map]
  (let [
        base-tokens    (:design-tokens base-theme-map)
        user-tokens    (:design-tokens user-theme-map)
        base-tokens-ks (util/kwargs-keys base-tokens)
        user-tokens-ks (util/kwargs-keys user-tokens)
        tokens-ks      (distinct (concat base-tokens-ks user-tokens-ks))
        ;; merge base and user tokens
        design-tokens  (apply merge
                              (map #(some->> %
                                             :design-tokens
                                             (partition 2)
                                             (mapv (fn [pair] (into [] pair)))
                                             (into {}))
                                   [base-theme-map user-theme-map]))
        ;; preserve authored token ordering
        design-tokens (mapv (fn [k] [k (k design-tokens)]) tokens-ks)
        font-loading   (merge (:font-loading base-theme-map) (:font-loading user-theme-map))]
    (keyed font-loading design-tokens)))

(defn utility-classes [m ks]
  (let [utility-classes (when (:add-kushi-defclass? user-config)
                          (some-> m :utility-classes varize-utility-classes))]
    (util/ordered-pairs ks utility-classes)))

(defn user-theme-map []
  (let [m (resolve-user-theme (:theme user-config) :user)
        ret (when m
              (if (s/valid? ::specs2/theme m)
                m
                (printing2/simple-warning2
                 {:commentary  (str  "kushi.ui.theme/theme\n"
                                     "Invalid value(s) in user theming config:\n"
                                     ansi/bold-font (:theme user-config) ansi/reset-font)
                  :expound-str (expound/expound-str ::specs2/theme m)})))]
    ret))


(defn merge-base-and-user-ui
  [base-theme-map user-theme-map]
  (let [base-ui*   (:ui base-theme-map)
        user-ui*   (:ui user-theme-map)
        base-ui    (->> base-ui* (apply hash-map) remove-global-selector)
        user-ui    (->> user-ui* (apply hash-map) remove-global-selector)
        user-ui-ks (util/kwargs-keys user-ui*)
        base-ui-ks (util/kwargs-keys base-ui*)
        merged     (merge base-ui user-ui)
        ks-ui      (distinct (concat base-ui-ks user-ui-ks))
        ordered-ui (util/ordered-pairs ks-ui merged)]
    (keyed merged ks-ui ordered-ui)))


(defn theme []
  (let [user-theme-map                       (user-theme-map)

        base-theme-map                       (basetheme/base-theme-map)

        {:keys [ordered-ui]}                 (merge-base-and-user-ui base-theme-map user-theme-map)

        {:keys [font-loading design-tokens]} (merged-theme-props base-theme-map user-theme-map)

        by-component                         (reduce by-component [] ordered-ui)

        styles                               (map first by-component)

        utility-classes                      (utility-classes base-theme-map utility-class-ks)

        css-reset                            (when (:add-css-reset? user-config)
                                               (or (:css-reset user-theme-map)
                                                   (:css-reset base-theme-map)))

        font-loading-opts                    (let [opts (font-loading-opts font-loading)]
                                               (if (false? (:add-system-font-stack? user-config))
                                                 (assoc opts :add-system-font-stack? false)
                                                 opts))]
    (merge
     (keyed
      css-reset
      utility-classes
      font-loading-opts
      styles
      design-tokens))))
