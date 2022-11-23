(ns kushi.typography
  (:require [kushi.utils :as util :refer [keyed]]
            [kushi.specs2 :as specs2]
            [kushi.state2 :as state2]
            [kushi.printing2 :as printing2]
            [expound.alpha :as expound]
            [garden.core :as garden]
            [garden.stylesheet :refer [at-font-face]]
            [clojure.spec.alpha :as s]))


;; SYSTEM UI FONT STACK --------------------------------------------------

(def stacks
  (array-map
   300 {:normal [".SFNS-Light" ".SFNSText-Light" ".HelveticaNeueDeskInterface-Light" ".LucidaGrandeUI" "Segoe UI Light" "Ubuntu Light" "Roboto-Light" "DroidSans" "Tahoma"]
        :italic [".SFNS-LightItalic" ".SFNSText-LightItalic" ".HelveticaNeueDeskInterface-Italic" ".LucidaGrandeUI" "Segoe UI Light Italic" "Ubuntu Light Italic" "Roboto-LightItalic" "DroidSans" "Tahoma"]}
   400 {:normal [".SFNS-Regular" ".SFNSText-Regular" ".HelveticaNeueDeskInterface-Regular" ".LucidaGrandeUI" "Segoe UI" "Ubuntu" "Roboto-Regular" "DroidSans" "Tahoma"]
        :italic [".SFNS-Italic" ".SFNSText-Italic" ".HelveticaNeueDeskInterface-Italic" ".LucidaGrandeUI" "Segoe UI Italic" "Ubuntu Italic" "Roboto-Italic" "DroidSans" "Tahoma"]}
   500 {:normal [".SFNS-Medium" ".SFNSText-Medium" ".HelveticaNeueDeskInterface-MediumP4" ".LucidaGrandeUI" "Segoe UI Semibold" "Ubuntu Medium" "Roboto-Medium" "DroidSans-Bold" "Tahoma Bold"]
        :italic [".SFNS-MediumItalic" ".SFNSText-MediumItalic" ".HelveticaNeueDeskInterface-MediumItalicP4" ".LucidaGrandeUI" "Segoe UI Semibold Italic" "Ubuntu Medium Italic" "Roboto-MediumItalic" "DroidSans-Bold" "Tahoma Bold"]}
   700 {:normal [".SFNS-Bold" ".SFNSText-Bold" ".HelveticaNeueDeskInterface-Bold" ".LucidaGrandeUI" "Segoe UI Bold" "Ubuntu Bold" "Roboto-Bold" "DroidSans-Bold" "Tahoma Bold"]
        :italic [".SFNS-BoldItalic" ".SFNSText-BoldItalic" ".HelveticaNeueDeskInterface-BoldItalic" ".LucidaGrandeUI" "Segoe UI Bold Italic" "Ubuntu Bold Italic" "Roboto-BoldItalic" "DroidSans-Bold" "Tahoma Bold"]}))

(defn- clean-weights [weights* problems]
  (if-let [to-remove (some->> problems
                              (mapv :in)
                              distinct
                              (apply concat)
                              (into []))]
    (doseq [idx to-remove]
      (util/remove-nth idx weights*))
    weights*))

(defn- reduced-weights [weights*]
  (if (empty? weights*)
    stacks
    (reduce (fn [acc v]
              (if (contains? stacks v)
                (assoc acc v (get stacks v))
                acc))
            {}
            weights*)))

(defn- stack-rules [weights]
  (apply concat
         (for [[weight fonts-by-style] weights]
           (for [[style fonts] fonts-by-style]
             (garden/css
              (at-font-face
               {:font-family "sys"
                :font-style  (name style)
                :font-weight weight
                :src         (mapv #(str "local(\"" % "\")") fonts)}))))))

(defn sysfont
  ([weights*]
   (sysfont weights* nil nil))
  ([weights* form-meta fn-meta]
   (let [{:keys [caching? cache-key cached]
          :as   cache-map}
         (state2/cached {:process :system-font-stack :args weights*})

         spec
         ::specs2/add-system-font-stack-args

         problems
         (some->> weights*
                  (s/explain-data spec)
                  :clojure.spec.alpha/problems)

         css-rules
         (into []
               (or cached
                   (-> weights*
                       (clean-weights problems)
                       reduced-weights
                       stack-rules)))

         expound-str
         (when problems (expound/expound-str spec weights*))

         m
         (merge cache-map
                {:expound-str                 expound-str
                 :kushi/process               :kushi.core/add-system-font-stack
                 :args                        weights*
                 :form-meta                   form-meta
                 :css-rule                    css-rules
                 :clojure.spec.alpha/problems problems}
                (when expound-str
                  {:doc (:doc fn-meta)}))]

     (swap! state2/user-defined-font-faces conj m)

     (when expound-str
       (printing2/simple-warning2 m))


     (keyed m cache-map))))


;; @FONT-FACE ------------------------------------------------------------

(defn- missing
  [problems]
  (let [missing*
        (into []
              (keep #(when (= (:in %) [0])
                       (let [k (some-> % :pred last last)]
                         {:path [0 k]
                          :key  k}))
                    problems))]
    (when (seq missing*) missing*)))

(defn- bad
  [problems]
  (some->> problems
           (keep (fn [{:keys [in val]}]
                   (when (< 1 (count in))
                     {:path  in
                      :entry [(last in) val]})))
           distinct
           (into [])))

(defn- fatal
  [bad missing]
  (some->> bad
           (filter #(contains? #{:font-family :src} %))
           (concat missing)
           (into [])))

(defn- weird [m]
  (let [valid-ks  specs2/valid-font-face-map-ks
        clean-map (select-keys m valid-ks)
        ret*      (filter (fn [[k _]] (not (get clean-map k))) m)
        ret       (when (seq ret*)
                    (mapv (fn [[k v]]
                            {:path  [0 k]
                             :entry [k v]})
                          ret*))]
    ret))

(defn add-font-face* [m]

  (let [spec        ::specs2/add-font-face-args
        problems    (some->> [m]
                             (s/explain-data spec)
                             :clojure.spec.alpha/problems)
        missing     (missing problems)
        bad         (bad problems)
        fatal       (fatal bad missing)
        weird       (weird m)
        cache-map   (state2/cached {:process :add-font-face
                                    :args [m]})
        css-rule    (when-not fatal
                      (or (:cached cache-map)
                          (garden/css (at-font-face m))))
        expound-str (when problems (expound/expound-str spec [m]))]
    (merge (keyed css-rule)
           {:expound-str                 expound-str
            :entries/bad                 bad
            :entries/weird               weird
            :entries/missing             missing
            :entries/fatal               fatal
            :clojure.spec.alpha/problems problems
            :cache-map                   cache-map})))
