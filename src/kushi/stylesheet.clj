(ns ^:dev/always kushi.stylesheet
  (:require
   [clojure.string :as string]
   [garden.stylesheet]
   [garden.core :as garden]
   [kushi.config :refer [user-config]]
   [kushi.state :as state]
   [kushi.utils :as util]))

(defn garden-vecs-injection
  [garden-vecs]
  (into []
        (map
         :rule-css
         (remove
          (fn [{x :garden-vec}] (and (vector? x) (nil? (second x))))
          (map (fn [v]
                 {:garden-vec v
                  :rule-css (garden.core/css v)})
               garden-vecs)))))

(def user-css-file-path
  (str (or (:css-dir user-config) (:static-css-dir user-config))
       "/"
       (or (:css-filename user-config) "kushi.css")))

(defn spit-css
  [{:keys [header comment garden-vecs content defclass? append pretty-print?]
    :or {append true}}]
  (use 'clojure.java.io)
  (let [cmnt (if header
               (str "/*" header "*/\n\n")
               (str (when comment (str "\n\n/*" comment "*/\n\n"))))
        path user-css-file-path
        content (str cmnt (or content (garden/css {:pretty-print? pretty-print?} garden-vecs)))]
    (if
     defclass?
      (let [file-contents (slurp path)]
        (spit path (str content "\n" file-contents)))
      (spit path content :append append))))

(defn has-mqs? [coll]
  (and (map? coll)
        (some-> coll :value :media-queries)
        coll))

(defn bunch-mqs [garden-vecs]
  (reduce (fn [acc m]
            (let [mq (-> m :value :media-queries)]
              (let [existing-rules (get acc mq)
                    rules (some-> m :value :rules)]
                (assoc acc mq (concat existing-rules rules)))))
          {}
          (filter has-mqs? garden-vecs)))

(defn print-status [n kind]
  (println (str "    " n " unique " kind)))

(def version* "0.1.3")
(def _local_? false) ; Only (optionally) set to true when developing kushi from local filesystem.
(def version (str "v" version* (when _local_? ":LOCAL")))

(defn create-css-file
  {:shadow.build/stage :compile-finish}
  [build-state]
  (let [mode (:shadow.build/mode build-state)
        pretty-print? (if (= :dev mode) true false)]
    (use 'clojure.java.io)
    (spit-css {:header (str "! kushi v" version " | EPL License | https://github.com/paintparty/kushi ") :append false})
    (println (str "\nkushi " version "\nkushi.stylsheet/create-css-file\nWriting the following to " user-css-file-path ":"))

    ;; write @font-face declarations
    (when-not (empty? @state/user-defined-font-faces)
      (do
        (print-status (count @state/user-defined-font-faces) "@font-face rule(s)")
        (spit-css {:pretty-print? pretty-print?
                   :comment "Font faces"
                   :content (string/join "\n" @state/user-defined-font-faces)}))
      (reset! state/user-defined-font-faces []))

    ;; write defkeyframes
    (when-not (empty? @state/user-defined-keyframes)
      (do
        (print-status (count @state/user-defined-keyframes) "@keyframes rule(s)")
        (spit-css {:comment "Animation Keyframes"
                   :content (let [content (string/join
                                   "\n"
                                   (map (fn [[nm frames]]
                                          (str "@keyframes "
                                               (name nm)
                                               " {\n"
                                               (garden.core/css frames)
                                               "\n}\n"))
                                        @state/user-defined-keyframes))]
                              content)}))
      (reset! state/user-defined-keyframes {}))

    ;; write defclasses
    (when-not (empty? @state/atomic-declarative-classes-used)
      (let [gv (map #(let [normalized-class-kw (util/normalized-class-kw %)]
                       (some-> @state/kushi-atomic-user-classes normalized-class-kw :garden-vecs))
                    @state/atomic-declarative-classes-used)
            garden-vecs* (apply concat (concat gv))
            garden-vecs (remove has-mqs? garden-vecs*)
            atomic-classes-mq (mapv #(let [[mq args] %]
                                       (apply (partial garden.stylesheet/at-media mq) args))
                                    (bunch-mqs garden-vecs*))]
        (print-status (count garden-vecs) "defclasses")
        (spit-css {:pretty-print? pretty-print?
                   :garden-vecs garden-vecs
                   :comment "Atomic classes"})

        (when-not (empty? atomic-classes-mq)
          (do
            (print-status (count atomic-classes-mq) "defclasses under media-queries")
            (spit-css {:pretty-print? pretty-print?
                       :garden-vecs atomic-classes-mq
                       :comment "Atomic classes, media queries"}))))
      (reset! state/atomic-declarative-classes-used #{}))


       ;; write rules
    (let [rules (map (fn [[k v]] (when v [k v]))
                     (:rules @state/garden-vecs-state))
          mqs (map (fn [[k v]]
                     (when-let [as-seq (seq v)]
                       (apply garden.stylesheet/at-media
                              (cons k as-seq))))
                   (dissoc @state/garden-vecs-state :rules))
          garden-vecs (remove nil? (concat rules mqs))]
      (print-status (count garden-vecs) "classes")
      (spit-css {:pretty-print? pretty-print?
                 :garden-vecs garden-vecs
                 :comment "Component styles"}))

    (println "")
    (reset! state/garden-vecs-state state/garden-vecs-state-init))
  build-state)

(defn garden-mq-rule? [v]
  (and (map? v) (= :media (:identifier v))))
