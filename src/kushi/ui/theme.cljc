(ns ^:dev/always kushi.ui.theme
  (:require
   [kushi.config :as config :refer [user-config]]
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

(defn- default-font [k1 k2]
  (when (k1 user-config)
    (k2 config/default-font-families-from-google-fonts)))

(defn- google-font-maps
  "Expects a map of font-loading opts that may contain a :google-fonts entry.
   The :google-fonts entry is a user-exposed option that can be added to
   the :font-loading entry of their theme map. It is a vector of font-family names
   which correspond to hosted Google fonts. All font-families listed in this vector
   will be loaded, with all available weights and italics variants included."
  [m]
  (let [primary      (default-font :add-default-primary-font-family? :sans)
        code         (default-font :add-default-code-font-family? :code)
        families     (->> m
                          :google-fonts
                          (util/partition-by-pred string?))
        full-fams    (-> families
                         first
                         (conj primary code))
        font-maps    (let [gf (->> (conj full-fams primary code)
                                   (remove nil?)
                                   distinct
                                   (into []))]
                       (mapv (fn [s] {:family s
                                      :styles {:normal :all
                                               :italic :all}})
                             gf))

        partial-fams (second families)
        ret          (->> partial-fams
                          seq
                          (concat font-maps)
                          (remove nil?)
                          (into []))]
    ret))

(defn- system-font-opts
  "Always creates @font-face declarations for all the weights and italics for
   system ui fonts, unless the user sets the entry `:add-system-font-stack?` to `false`
   in their kushi.edn config map.

   If the user passes a vector of valid font weights for
   `:system-font-stack-weights` entry, only those weights will be added."
  []
  (let [add?     (not (false? (:add-system-font-stack? user-config)))
        weights* (:add-system-font-stack-weights user-config)
        weights  (if (and (seq weights*)
                          (every? int? weights*))
                   weights*
                   [])]
    {:add-system-font-stack?    add?
     :system-font-stack-weights weights}))

(defn- font-loading-opts
  "Provides an argument map for kushi.core.ui/init-typography!"
  [m]
  (let [google-font-maps (google-font-maps m)
        sys              (system-font-opts)]
    (merge (keyed google-font-maps)
           sys)))


(defn- design-tokens [base user]
  (let [f       #(-> % :design-tokens util/kwargs-keys)
        toks-ks (distinct (concat (f base)
                                  (f user)))

        ;; merge base and user tokens
        toks    (apply merge
                       (map #(some->> %
                                      :design-tokens
                                      (partition 2)
                                      (mapv (fn [pair] (into [] pair)))
                                      (into {}))
                            [base user]))

        ;; preserve authored token ordering
        toks    (mapv (fn [k] [k (k toks)]) toks-ks)]
    toks))


(defn merged-theme-props [base user]
  (let [design-tokens (design-tokens base user)
        font-loading  (font-loading-opts (merge (:font-loading base)
                                                (:font-loading user)))]
    (keyed font-loading
           design-tokens)))


(defn utility-classes [m ks]
  (let [utility-classes (when (:add-kushi-defclass? user-config)
                          (some-> m
                                  :utility-classes
                                  varize-utility-classes))]
    (util/ordered-pairs ks
                        utility-classes)))


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
  [base user]
  (let [base-ui*   (:ui base)
        user-ui*   (:ui user)
        f          #(->> % :ui (apply hash-map) remove-global-selector)
        base-ui    (f base)
        user-ui    (f user)
        user-ui-ks (util/kwargs-keys user-ui*)
        base-ui-ks (util/kwargs-keys base-ui*)
        merged     (merge base-ui user-ui)
        ks-ui      (distinct (concat base-ui-ks user-ui-ks))
        ordered-ui (util/ordered-pairs ks-ui merged)]
    (keyed merged ks-ui ordered-ui)))


(defn theme []
  (let [user                 (user-theme-map)
        base                 (basetheme/base-theme-map)
        {:keys [ordered-ui]} (merge-base-and-user-ui base user)
        merged-theme-props   (merged-theme-props base user)
        by-component         (reduce by-component [] ordered-ui)
        styles               (map first by-component)
        utility-classes      (utility-classes base utility-class-ks)
        css-reset            (when (:add-css-reset? user-config)
                               (or (:css-reset user)
                                   (:css-reset base)))]
    (merge
     merged-theme-props ;; <- map with the entries of [:font-loading :design tokens]
     (keyed
      css-reset
      utility-classes
      styles))))
