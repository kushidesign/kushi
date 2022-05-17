(ns ^:dev/always kushi.stylesheet
  (:require
   [clojure.string :as string]
   [clojure.set :as set]
   [clojure.java.io :as io]
   [clojure.edn :as edn] ;Take this out?
   [clojure.data :as data]
   [garden.stylesheet]
   [garden.core :as garden]
   [kushi.config :refer [user-config user-css-file-path kushi-cache-dir kushi-cache-path version]]
   [kushi.state :as state]
   [kushi.utils :as util :refer [keyed]]
   [par.core :refer [? !? ?+ !?+]]
   [kushi.atomic :as atomic]
   [kushi.reporting :as reporting]
   [medley.core :as medley]))


(defn garden-vecs-injection
  [garden-vecs]
  (->> garden-vecs
       (map (fn [v] {:garden-vec v :rule-css (garden.core/css {:pretty-print? false} v)}))
       (remove (fn [{x :garden-vec}] (and (vector? x) (nil? (second x)))))
       (map :rule-css)
       (remove string/blank?)
       (into [])))

(defn append-css-chunk!
  [{:keys [css-text
           comment
           content]}]
  (let [cmnt (when comment (str "\n\n/* " comment " */\n\n"))
        print? (and content (not (string/blank? content)))
        content (str cmnt content)]
    (when print?
     (reset! css-text (str @css-text "\n" content))) ))

(defn has-mqs? [coll]
  (and (map? coll)
        (some-> coll :value :media-queries)
        coll))

(defn bunch-mqs [garden-vecs]
  (reduce (fn [acc m]
            (let [mq (-> m :value :media-queries)]
              (let [existing-rules (get acc mq)
                    rules          (some-> m :value :rules)]
                (assoc acc mq (concat existing-rules rules)))))
          {}
          (filter has-mqs? garden-vecs)))

(defn atomic-classes-mq
  [garden-vecs*]
  (let [medias (-> user-config :media vals)
        mq-idx (fn [x]
                 (let [mq  (-> x :value :media-queries)
                       idx (first (keep-indexed (fn [idx v] (when (= mq v) idx)) medias))]
                   idx))
        ret*   (mapv #(let [[mq args] %]
                        (apply (partial garden.stylesheet/at-media mq) args))
                     (bunch-mqs garden-vecs*))
        ret    (sort-by mq-idx < ret*)]
    ret))

(defn print-status [n kind]
  (println (str "    " n " unique " kind)))

(def license-comment-header
  (str "/*! kushi v" version " | EPL License | https://github.com/paintparty/kushi */"))

(defn design-tokens-css
  [{:keys [toks pretty-print?]}]
  (let [gvecs (->> toks
                   (mapv (fn [[prop val]] {prop (util/maybe-wrap-css-var val)}))
                   (cons ":root")
                   (into [])
                   vector)]
    (garden/css {:pretty-print? pretty-print?} gvecs)))

(defn append-tokens!
  [{:keys [token-type] :as m}]
  (let [toks  (case token-type
                :global @state/global-tokens
                :alias @state/alias-tokens
                :used @state/used-tokens
                [])
        count (if (seq toks) (count toks) 0)
        k     (-> token-type name (str  "-tokens-count") keyword)]
    (!?+ (swap! (:to-be-printed m) assoc k count))
    (when (pos-int? count)
      (append-css-chunk!
       {:css-text (:css-text m)
        :comment  (str (name token-type) " design tokens")
        :content  (design-tokens-css (assoc m :toks toks))}))))

(defn append-at-font-face!
  [{:keys [css-text to-be-printed]}]
  (let [font-face-count (count @state/user-defined-font-faces)]
    (swap! to-be-printed assoc :font-face font-face-count)
    (when (pos-int? font-face-count)
      (append-css-chunk!
       {:css-text css-text
        :comment  "Font faces"
        :content  (string/join "\n" @state/user-defined-font-faces)}))))

  (defn defkeyframes->css [[nm frames]]
    (str "@keyframes "
         (name nm)
         " {\n"
         (garden.core/css frames)
         "\n}\n"))

(defn all-defkeyframes-content []
  (string/join
   "\n"
   (map defkeyframes->css @state/user-defined-keyframes)))

(defn append-defkeyframes!
  [{:keys [css-text to-be-printed]}]
  (let [keyframes-count (count @state/user-defined-keyframes)]
    (swap! to-be-printed assoc :keyframes keyframes-count)
    (when (pos-int? keyframes-count)
      (append-css-chunk!
       {:css-text css-text
        :comment  "Animation Keyframes"
        :content  (all-defkeyframes-content)})
      #_(reset! state/user-defined-keyframes {}))))

(defn count-mqs-rules [mqs]
  (count (apply concat (map #(some-> % :value :rules) mqs))))

(defn no-declarations? [coll]
  (and (vector? coll)
       (= (count coll) 2)
       (string? (first coll))
       (nil? (second coll))))

(defn append-defclasses!
  [{:keys [pretty-print? css-text to-be-printed comment-base classtype] :as m*}]
  (let [gv* (if @state/KUSHIDEBUG
              (->> @state/utility-classes-by-classtype classtype vals)
              (->> @state/utility-classes-used-by-classtype classtype))
        gv  (map :garden-vecs gv*)]
   (when (seq gv)
     (let [garden-vecs*                   (apply concat (concat gv))
           garden-vecs                    (->> garden-vecs*
                                               (remove has-mqs?)
                                               (remove no-declarations?))
           atomic-classes-mq              (atomic-classes-mq garden-vecs*)
           defclass-mq-count              (count atomic-classes-mq)
           defclass-style-rules-under-mqs (count-mqs-rules atomic-classes-mq)]
       (swap! to-be-printed
              assoc
              :defclass-style-rules-under-mqs
              defclass-style-rules-under-mqs
              :defclass-style-rules
              (count garden-vecs)
              :defclass-mq-count
              defclass-mq-count)
       (append-css-chunk!
        {:css-text css-text
         :content  (garden/css {:pretty-print? pretty-print?} garden-vecs)
         :comment  comment-base})

       (when (pos-int? defclass-mq-count)
         (append-css-chunk!
          {:css-text css-text
           :content  (garden/css {:pretty-print? pretty-print?} atomic-classes-mq)
           :comment  (str comment-base ", media queries")}))))))

(defn resolve-rules [m st reset?]
  (let [rules** (:rules @st)
        rules*  (if reset? (reverse rules**) rules**)
        rules   (keep (fn [[k v]] (when v [k v])) rules*)]
    rules))


(defn append-rules!
  [{:keys [css-text pretty-print? to-be-printed]
    :as   m}
   gv-state-coll
   comment]
  (let [reset?                       (= :reset (:kushi/sheet m))
        rules                        (resolve-rules m gv-state-coll reset?)
        mqs                          (keep (fn [[k v]]
                                             (when-let [as-seq (seq v)]
                                               (apply garden.stylesheet/at-media
                                                      (cons k as-seq))))
                                           (dissoc @gv-state-coll :rules))
        garden-vecs                  (remove nil? (concat rules mqs))
        css-reset-style-rules        (if reset? (count rules) (:css-reset-style-rules to-be-printed))
        normal-style-rules-under-mqs (count-mqs-rules mqs)
        normal-style-rules           (count rules)
        normal-mq-count              (count mqs)]

    (!?+
     (keyed
      rules
      ;; mqs
      ;; garden-vecs
      ;; normal-style-rules-under-mqs
      ;; normal-style-rules
      ;; normal-mq-count
      ))

    (swap! to-be-printed
           merge
           (keyed normal-style-rules-under-mqs
                  normal-style-rules
                  normal-mq-count
                  css-reset-style-rules))
    (append-css-chunk!
     {:css-text css-text
      :content  (garden/css {:pretty-print? pretty-print?} garden-vecs)
      :comment  comment})))

(defn append-reset-rules! [m]
  (append-rules! (assoc m :kushi/sheet :reset)
                 state/css-reset
                 "CSS Reset rules via:\nThe new CSS reset - version 1.6.0 (last updated 29.4.2022)\nGitHub page: https://github.com/elad2412/the-new-css-reset"))

(defn append-reusable-component-rules! [m]
  (append-rules! m state/garden-vecs-state-components "Reusable component styles for kushi-ui components"))

(defn append-component-rules! [m]
  (append-rules! m state/garden-vecs-state "Component styles"))

(defn append-theme-rules! [m]
  (append-rules! m state/garden-vecs-state-theme "Kushi UI theming rules"))

(defn write-cache! [cache-is-equal?]
  (when-not cache-is-equal?
    (do
      (let [created-cache-dir? (io/make-parents kushi-cache-path)
            {fname :name
             ns*   :ns}  (meta #'write-cache!)
            nsfn         (str (ns-name ns*) "/" fname)]
        (when created-cache-dir?
          (reporting/report! nsfn (str " Created cache dir -> " kushi-cache-dir))))
      (spit kushi-cache-path @state/styles-cache-updated :append false)))
  (reset! state/styles-cache-current @state/styles-cache-updated))

(defn cache-is-equal? []
  (let [[only-in-a only-in-b _] (data/diff @state/styles-cache-current @state/styles-cache-updated)
        cache-is-equal? (and (nil? only-in-a) (nil? only-in-b))]
    cache-is-equal?))

(defn create-css-text []
  (let [pretty-print? true
        printables    (atom [])
        to-be-printed (atom {})
        css-text      (atom license-comment-header)
        m             {:css-text      css-text
                       :pretty-print? pretty-print?
                       :printables    printables
                       :to-be-printed to-be-printed}
        caching?      (true? (:enable-caching? user-config))]

    ;; (?+ @state/utility-classes-by-classtype)
    ;; (?+ @state/utility-classes-used)
    ;; (?+ @state/utility-classes-used-by-classtype)
    ;; (?+ :kushi-utility-classes (-> @state/utility-classes-by-classtype :user-utility keys))

    (when-let [select-ns-msg (reporting/select-ns-msg)]
      (swap! to-be-printed assoc :select-ns-msg select-ns-msg))

    (append-reset-rules! m)
    (append-at-font-face! m)
    (append-tokens! (assoc m :token-type :global))
    (append-tokens! (assoc m :token-type :alias))
    (append-tokens! (assoc m :token-type :used))
    (append-defkeyframes! m)
    (append-theme-rules! m)
    (append-defclasses! (merge m {:classtype    :kushi-utility
                                  :comment-base "Kushi base utility classes"}))
    (append-defclasses! (merge m {:classtype    :user-utility
                                  :comment-base "User-defined base utility classes"}))
    (append-reusable-component-rules! m)
    (append-component-rules! m)
    (append-defclasses! (merge m {:classtype    :kushi-utility-override
                                  :comment-base "Kushi base utility classes, override versions"}))
    (append-defclasses! (merge m {:classtype    :user-utility-override
                                  :comment-base "User-defined base utility classes, override versions"}))

    (reset! state/kushi-css-sync @css-text)
    (reset! state/kushi-css-sync-to-be-printed @to-be-printed)

      ;; Do caching here???
    #_(let [cache-will-update? (when caching?
                                 (let [cache-is-equal? (cache-is-equal?)]
                                   (write-cache! cache-is-equal?)
                                   (not cache-is-equal?)))]
        #_(? "kushi.stylesheet/create-css-file: local bindings"
             (assoc
              (keyed write-styles?
                     cache-will-update?
                     post-build-report?)
              :to-be-printed @to-be-printed))

        (when (and post-build-report? something-to-write?)
          (reporting/print-report! to-be-printed cache-will-update?)))))

(defn create-css-file
  {:shadow.build/stage :compile-finish}
  [build-state]
  (when (nil? @state/kushi-css-sync) (create-css-text))
  (let [to-be-printed       state/kushi-css-sync-to-be-printed
        zero-total-rules?   (nil? (some #(not (zero? %)) (vals @to-be-printed)))
        something-to-write? (not zero-total-rules?)]

    (when (and (:write-stylesheet? user-config) something-to-write?)
      (use 'clojure.java.io)
      (spit user-css-file-path @state/kushi-css-sync :append false))

    (when (and (:post-build-report? user-config) something-to-write?)
      (reporting/print-report! to-be-printed)))

  ;; Must return the build state
  build-state)

(defn garden-mq-rule? [v]
  (and (map? v) (= :media (:identifier v))))
