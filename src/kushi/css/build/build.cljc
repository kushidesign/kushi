;; TODO -- Annotate from generate function

(ns kushi.css.build.build
  (:require
   [kushi.css.core :as kushi.css]
   [kushi.css.specs :as kushi-specs]
   [fireworks.core :refer [? !?] :rename {? ff}]
   [bling.core :refer [callout bling]]
   [kushi.css.build.utility-classes :as utility-classes]
   [kushi.css.build.tokens :as tokens]
   [kushi.css.build.specs :as s]
   [kushi.css.build.analyzer :as ana]
   [kushi.utils :refer [partition-by-spec]]
   [clojure.spec.alpha]
   [clojure.set :as set]
   [clojure.string :as str]
   #?@(:clj
       [[clojure.edn :as edn]
        [clojure.java.io :as io]
        [fireworks.macros :refer [keyed]]]
       :cljs
       [[cljs.reader :as edn]
        [fireworks.macros :refer-macros [keyed]]]))
  #?(:clj
     (:import [java.io File StringWriter Writer])
     :cljs
     (:import [goog.string StringBuffer])))

#?(:cljs
   (!? :result 'kushi.css.build.build.cljc:cljs)
   :clj
   (!? :result 'kushi.css.build.build.cljc:clj))

(def kdbg? true)

(defn co-top [f msg]
  #_(when kdbg? 
    (callout {:type          :magenta
              :border-weight :medium
              :label         f}
             msg)))

(defn co [f msg]
  #_(when kdbg?
    (callout {:margin-left 4
              :margin-top  2
              :type        :gray
              :label       f}
             msg)))

(defn co2 [f msg]
  #_(when kdbg? 
    (callout {:margin-left 8
              :margin-top  2
              :type        :gray
              :label       f}
             msg)))

(defn co->> [f msg x]
  #_(when kdbg? 
    (callout {:margin-left 4
              :margin-top  2
              :type        :gray
              :label       f}
             msg))
  x)

(defn ? [& args]
  (last args))

(defn generate-spacing-aliases
  [{:keys [alias-groups
           spacing]
    :as   build-state}]
  (update build-state
          :aliases
          (fn [aliases]
            (let [ret (reduce-kv
                       (fn [aliases space-num space-val]
                         (reduce-kv
                          (fn [aliases prefix props]
                            (if (string? prefix)
                              (assoc aliases 
                                     (keyword (str prefix space-num))
                                     (reduce #(assoc %1
                                                     %2
                                                     (if (= \- (first prefix))
                                                       (str "-" space-val)
                                                       space-val)) {}
                                             props))
                              (let [[prefix sub-sel] prefix]
                                (assoc aliases
                                       (keyword (str prefix space-num))
                                       [[sub-sel (reduce #(assoc %1 %2 space-val) {} props)]]))))
                          aliases
                          (:spacing alias-groups)))
                       aliases
                       spacing)]

                 ;; KUSHI Printing ---------------------------------------
              (co
               'generate-spacing-aliases
               (bling
                (str "Generating spacing aliases for:\n" 
                     "(" (str/join " " (take 10 (keys spacing))) "...)"
                     "\n")
                (-> (? :data {:print-level 1
                              :coll-limit  10}
                       ret)
                    :formatted
                    :string)))
                 ;; KUSHI Printing ---------------------------------------
              
              ret))))



(defn generate-color-aliases*
  [aliases alias-groups colors]
  (reduce
    (fn [aliases [color-name suffix color]]
      (reduce-kv
        (fn [aliases alias-prefix props]
          (let [k (keyword (str alias-prefix color-name suffix))
                v (cond
                    (keyword? props)
                    {props color}

                    (fn? props)
                    (props color)

                    :else
                    nil)]

            ;; KUSHI Printing ----------------------------
            #_(when (and (= color-name "white")
                       (= alias-prefix "text-"))
              (co2
               'generate-color-aliases
               (bling
                "Example color alias generation for "
                "\n"
                [:blue (str alias-prefix color-name)]
                "\n"
                " : (assoc aliases " k " " v ")"
                "\n"
                (-> (? :data {:theme       "Neutral Light"       
                             :print-level 1
                             :coll-limit  3}
                            {:alias-prefix alias-prefix
                             :props        props
                             :suffix       suffix})
                    :formatted
                    :string))))
            ;; KUSHI Printing ----------------------------

            (assoc aliases k v)))
        aliases
        (:color alias-groups)))
    aliases
    (for [[name vals] colors
          [suffix color] vals]
      [name suffix color])))

(defn generate-color-aliases
  [{:keys [alias-groups colors] :as build-state}]
  (co 'generate-color-aliases
      (str 
       "generating color aliases for:\n"
       "(" (str/join " " (take 7 (keys colors)) ) "...)"
       "\n"
       (-> (? :data {:theme       "Neutral Light"       
                    :print-level 1
                    :coll-limit  3}
                   (keyed [alias-groups colors]))
           :formatted
           :string)))
  (update build-state :aliases generate-color-aliases* alias-groups colors))


;; -----------------------------------------------------------------------------
;; NEW -------------------------------------------------------------------------

(defn- css-section-comment [s]
  (str "\n/* "
       s
       " "
       (str/join (repeat (- 80 7 (count s)) "-"))
       " */\n"))

(defn- reduce-utility-classes-from-rules
  [rules]
  (reduce 
   (fn [acc m]
     (apply conj
            acc
            (filter #(and (clojure.spec.alpha/valid? ::kushi-specs/class-kw %)
                          (when (keyword? %)
                            (contains? utility-classes/utility-class-ks-set (name %))))
                    (:form m))))
   #{}
   rules))

;; stub for release or dev
(def release? false)

(defn- utility-classes-css
  [req {:keys [all-utility-classes]}]
  (reduce (fn [acc kw]
            (str acc
                 (let [sel (name kw)
                       m   (get all-utility-classes sel)
                       ;; TODO - provide values for &env &form (nil and nil) 
                       css (do (keyed [sel m])
                               (kushi.css/css-rule* sel (list m) nil nil))]
                   (some->> css (str "\n")))))
          (if (and (not release?) (seq req))
            (css-section-comment "Kushi utility classes")
            "")
          req))

(defn- tokens-css [req coll comment]
  (when-let [tokens
             (if (and release? (set? req))
               (when (seq req)
                 (->> coll
                      (partition 2)
                      (filter (fn [[k _]] (contains? req k)))
                      (apply concat)
                      (apply array-map)))
               (apply array-map coll))]
    (str (css-section-comment comment)
         (kushi.css/css-rule* ":root" (list tokens) nil nil))))

(defn- design-tokens-css
  "CSS for all the design tokens, or just a select few based on usage."
  [req {:keys [all-design-tokens]}] 
  (tokens-css req all-design-tokens "Kushi design tokens"))

(defn- user-design-tokens-css
  "CSS for user all design tokens, or just a select few based on usage."
  [req {{{:keys [design-tokens]} :theme} :user-config}] 
  (tokens-css req design-tokens "User design tokens"))

(defn- user-theming-classes-css
  "CSS for user theming"
  [req {{{:keys [ui]} :theme} :user-config}] 
  (when-let [classes
             (when (seq ui)
               (->> ui
                    (partition 2)
                    (map (fn [[sel m]]
                           (kushi.css/css-rule* sel (list m) nil nil)))))]
    (str (css-section-comment "User theming classes")
         "\n"
         (str/join "\n" classes))))

(defn emits
  #?(:clj
     ([^Writer w ^String s]
      (.write w s))
     :cljs
     ([w s]
      (.append w s)))
  ([w s & more]
   (emits w s)
   (doseq [s more]
     (emits w s))))

(defn emitln
  #?(:clj
     ([^Writer w]
      (.write w "\n"))
     :cljs
     ([sb]
      (.append sb "\n")))
  ([w & args]
   (doseq [s args]
     (emits w s))
   (emitln w)))

#_(defn emit-rule [w sel rules]
  (doseq [[group-sel group-rules] rules]
    (emitln w (str/replace group-sel #"&" sel) " {")
    (doseq [prop (sort (keys group-rules))]
      (emitln w "  " (name prop) ": " (get group-rules prop) ";"))
    (emitln w "}")))

;; TODO - use a sym for kind
;; TODO - figure out how to do recursively expand defclasses without going into infinite loop
;;      - maybe this should happen in build-css-for-chunks. Look at how this is currently done in kushi
(defn emit-def [w {:keys [sel form line column ns kind defcss-by-selector]}]
  (let [defcss?
        (= kind :defcss)

        sx?
        (some-> form meta :macro (= 'kushi.css/sx))

        kushi-macro-sym
        (cond 
          defcss?
          'defcss

          sx?
          'sx

          :else 'css)

        form+ 
        (if-not defcss?
          ;; When css or sx macro, check that args are NOT only classes (kushi
          ;; utility classes or user-defined shared classes). If args are only
          ;; classes, we can avoid writing an empty rule to css file.
          (let [[class-kws other]
                (partition-by-spec ::kushi-specs/class-kw form)
                skip? (boolean (and (seq class-kws)
                                    (empty? other)))]
            (when-not skip? form))
          (reduce 
            (fn [acc x]
              (let [class-kw?
                    (clojure.spec.alpha/valid? ::kushi-specs/class-kw x)

                    utility-class
                    (when class-kw?
                      (some->> x
                               name
                               (get utility-classes/utility-classes)))

                    user-defclass
                    (when (and (not utility-class) class-kw?)
                      (some->> x
                               name
                               (get defcss-by-selector)))]
                (if user-defclass 
                  (apply conj acc user-defclass)
                  (conj acc (or utility-class x)))))
            []
            form))]
    
    (when form+ 
      (emitln w
              (kushi.css/css-rule*
               sel
               form+
               (with-meta (cons kushi-macro-sym (into '() form))
                 {:line   line
                  :column column
                  :file   (str ns)})
               nil)))))

;; NEW -------------------------------------------------------------------------
;; -----------------------------------------------------------------------------

(defn build-css-for-chunk [build-state chunk-id]
  (!? build-css-for-chunk)
  (update-in build-state [:chunks chunk-id]
    (fn [{:keys [base
                 rules
                 defcss
                 defcss-by-selector
                 classpath-includes
                 utility-classes-css
                 design-tokens-css
                 user-design-tokens-css
                 user-theming-classes-css
                 ]
          :as chunk}]
      ;; (ff chunk)
      (assoc chunk
             :css
             (let [sw #?(:clj (StringWriter.) :cljs (StringBuffer.))]
               (when base
                 (when-let [pre (:preflight-src build-state)]
                   (emitln sw pre))

                 ;; kushi design tokens ----------------------------------------
                 (some->> design-tokens-css (emitln sw))

                 ;; user design tokens via user theme --------------------------
                 (some->> user-design-tokens-css (emitln sw))

                 ;; TODO - kushi animations (move from kushi utility classes) --

                 ;; TODO - user animations (move from user utility classes) ----

                 ;; kushi utility classes
                 (some->> utility-classes-css (emitln sw))

                 ;; TODO - kushi.ui component theming rules (what is this?) ----

                 ;; TODO - user theming rules via [:theme :ui] -----------------
                 (some->> user-theming-classes-css (emitln sw))

                 ;; TODO - kushi.ui component theming rules
                 ;;        (shared, via defclass or defclass-with-override) ---- 
                 
               
               #?@(:clj
                   [(doseq [inc classpath-includes]
                      (emitln sw (slurp (io/resource inc))))])

               ;;  user-defined shared classes (via defcss)
               (when (seq defcss)
                 (emitln sw (css-section-comment "User shared classes")))
               (doseq [def defcss]
                 (emit-def sw (assoc def
                                     :kind
                                     :defcss
                                     :defcss-by-selector
                                     defcss-by-selector)))

               ;; user styles generated from kushi.core/css or kushi.core/sx ---
               (when (seq rules)
                 (emitln sw (css-section-comment "User styling")))
               ;; NEW -----------------------------------
               (doseq [def rules]
                 (emit-def sw def))
               
               ;; TODO - Add kushi base utility classes, override versions -----

               (.toString sw)))))))


(defn collect-namespaces-for-chunk
  [{:keys [include entries] :as chunk} {:keys [namespaces] :as build-state}]
  (!? collect-namespaces-for-chunk)
  (let [namespace-matchers
        (->> include
             (map (fn [x]
                    (cond
                      (string? x)
                      (let [re (re-pattern x)]
                        #(re-find re (name %)))

                      (not (symbol? x))
                      (throw (ex-info "invalid include pattern" {:x x}))

                      :else
                      (let [s (str x)]
                        ;; FIXME: allow more patterns that can be expressed as string?
                        ;; foo.bar.*.views?

                        (if (str/ends-with? s "*")
                          ;; foo.bar.* - prefix match
                          (let [prefix (subs s 0 (-> s count dec))]
                            (fn [ns]
                              (str/starts-with? (str ns) prefix)))

                          ;; exact match
                          (fn [ns]
                            (= x ns))
                          )))))
             (into []))


        {entry-namespaces :namespaces}
        (reduce
          (fn step-fn [{:keys [visited] :as m} ns]
            (cond
              (contains? visited ns)
              m

              ;; npm support later
              (string? ns)
              m

              (str/includes? (str ns) "*")
              (throw (ex-info ":entries only takes full namespace names, not wildcards" {:ns ns}))

              :else
              (let [ns-info (get namespaces ns)]
                (-> m
                    (update :namespaces conj ns)
                    (update :visited conj ns)
                    (ana/reduce-> step-fn (:requires ns-info))))))

          {:visited #{}
           :namespaces #{}}
          entries)

        included-namespaces
        (->> (keys namespaces)
             (filter (fn [ns]
                       (or (contains? entry-namespaces ns)
                           (some (fn [matcher] (matcher ns)) namespace-matchers))))
             (into []))]

    (assoc chunk :namespaces included-namespaces)))

(defn build-css-for-chunks
  [{:keys [namespaces] :as build-state}]

  ;;------------------------;;
  ;; DEBUG THIS IN FW       ;;
  ;; (ff build-state)       ;;
  ;;------------------------;;

  #_(ff (keys build-state))

  ;; (ff :pp (:namespaces build-state))
  ;; (ff :pp (:colors build-state))
  ;; (ff :pp (:chunks build-state))
  (reduce-kv
    (fn [build-state chunk-id chunk]
      (let [all-rules
            (->> (for [ns (:chunk-namespaces chunk)
                       :let [{:keys [ns css] :as ns-info} (get namespaces ns)]
                       {:keys [line column] :as form-info} css
                       :let [css-id (s/generate-id ns line column)]]
                   (-> (ana/process-form build-state form-info)
                       (assoc
                        :ns ns
                        :css-id css-id
                        ;; FIXME: when adding optimization pass selector won't be based on css-id anymore
                        :sel (str "." css-id))))
                 (into []))

            ;; NEW --------------------------------------------
            all-defcss
            (->> (for [ns (:chunk-namespaces chunk)
                       :let [{:keys [ns defcss] :as ns-info} (get namespaces ns)]
                       {:keys [line column form] :as form-info} defcss
                       :let [css-id (s/generate-id ns line column)
                             [sel]  form]]
                   (-> (ana/process-form build-state form-info)
                       (assoc
                        :ns     ns
                        :css-id css-id
                        :form   (rest form) ; <- TODO - Use subvec here?
                        ;; FIXME: when adding optimization pass selector won't be based on css-id anymore
                        :sel    sel)))
                 (into []))

            defcss-by-selector
            (reduce (fn [acc {:keys [sel form]}]
                      (assoc acc sel form))
                    {} 
                    all-defcss)
            ;; NEW --------------------------------------------
            
            cp-includes
            (into #{} (for [ns (:chunk-namespaces chunk)
                            :let [{:keys [ns-meta]} (get namespaces ns)]
                            include (:kushi.css.build/include ns-meta)]
                        include))

            warnings
            (vec
             (for [{:keys [warnings ns line column]} all-rules
                   warning warnings]
               (assoc warning :ns ns :line line :column column)))


            ;; NEW --------------------------------------------
            required-utility-classes
            (reduce-utility-classes-from-rules all-rules)

            utility-classes-css
            (some-> required-utility-classes 
                    seq
                    (utility-classes-css build-state))

            required-design-tokens
            :all ;; <- creating harvesting fn here
            
            design-tokens-css
            (design-tokens-css required-design-tokens build-state)

            required-user-design-tokens
            :all ;; <- creating harvesting fn here

            user-design-tokens-css
            (user-design-tokens-css required-user-design-tokens build-state)

            user-theming-classes-css
            (user-theming-classes-css :all build-state)
            ;; NEW --------------------------------------------
            
            ]
        (co 'build-css-for-chunks
            (-> (? :data
                   {:display-metadata? false}
                   {:all-rules   all-rules
                    :cp-includes cp-includes
                    :warnings    warnings})
                :formatted
                :string))
        (-> build-state
            (update-in [:chunks chunk-id] assoc
              :warnings warnings
              :classpath-includes cp-includes
              :rules all-rules

              ;; NEW --------------------------------------------
              :defcss all-defcss
              :defcss-by-selector defcss-by-selector
              :required-utility-classes required-utility-classes
              :utility-classes-css utility-classes-css
              :required-design-tokens required-design-tokens
              :design-tokens-css design-tokens-css
              :user-design-tokens-css user-design-tokens-css
              :user-theming-classes-css user-theming-classes-css
              ;; NEW --------------------------------------------

                       )
            (build-css-for-chunk chunk-id))))

    build-state
    (:chunks build-state)))


(defn trim-chunks [build-state]
  (!? trim-chunks)
  (update build-state :chunks
    (fn [chunks]
      (reduce-kv
        (fn [chunks chunk-id {:keys [depends-on namespaces] :as chunk}]
          (co 'trim-chunks
              (-> (? :data
                     {:coll-limit 5}
                     {:chunks     chunks 
                      :chunk-id   chunk-id
                      :depends-on depends-on
                      :namespace  namespace})
                  :formatted
                  :string))
          (let [chunk-set
                (if-not (seq depends-on)
                  (set namespaces)
                  (let [provided-by-deps (reduce
                                          (fn step-fn [ns-set module-id]
                                            (let [{:keys [namespaces]
                                                   :as   other} (get chunks module-id)]
                                              (co2 'inside-provided-by-deps-reduce
                                                   (-> (? :data
                                                          {:coll-limit 5}
                                                          {:ns-set     ns-set
                                                           :chunk-id   module-id
                                                           :namespaces namespaces})
                                                       :formatted
                                                       :string))

                                              (co2 'inside-provided-by-deps-reduce
                                                   (-> (? :trace (-> ns-set
                                                                     (set/union (set namespaces))
                                                                     (ana/reduce-> step-fn (:depends-on other))))
                                                       :formatted
                                                       :string))

                                              (-> ns-set
                                                  (set/union (set namespaces))
                                                  (ana/reduce-> step-fn (:depends-on other)))))
                                          #{}
                                          depends-on)
                        set-diff         (!? (set/difference (set namespaces) provided-by-deps))]
                    set-diff))]
            (co2 'trim-chunks
                 (str "Associng\n"
                      [:main :chunk-namespaces]
                      "\n"
                      "with value of"
                      "\n"
                      (-> (? :data {:display-metadata? false} chunk-set)
                          :formatted
                          :string)
                      "\n"
                      "in `chunks`."))
            (assoc-in chunks [chunk-id :chunk-namespaces] chunk-set)))
        chunks
        chunks))))


(defn generate [build-state chunks]
  (co-top 'kushi.css.build.build/generate
          (str "This is were we generate all the chunks."
               "\n"
               (str chunks)))
  ;; FIXME: actually support chunks, similar to CLJS with :depends-on #{:other-chunk}
  ;; so chunks don't repeat everything, for that needs to analyze chunks first
  ;; then produce output
  (-> build-state
      (assoc :chunks {})
      (ana/reduce-kv->
       (fn [build-state chunk-id chunk]
         (let [chunk
               (-> chunk
                   (assoc :chunk-id chunk-id)
                   (cond->
                    (not (contains? chunk :depends-on))
                     (assoc :base true))
                   (collect-namespaces-for-chunk build-state))]
           (co 'ana/reduce-kv:reducer-fn
               (-> (? :data
                      {:coll-limit        14
                       :display-metadata? false}
                      chunk)
                   :formatted
                   :string))
           (assoc-in build-state [:chunks chunk-id] chunk)))
       chunks)
      (trim-chunks)
      (build-css-for-chunks) ))

;; simplistic regexp based css minifier
;; it'll destroy some stuff for sure
;; but for now it seems to be ok and doesn't require parsing css
(defn minify-chunk [chunk]
  (!? minify-chunk)
  #?(:cljs
     ;; FIXME: I don't know why the below regexp breaks in JS, look into it
     ;; currently only using JS variant in self-hosted grove examples app, which doesn't minify anyways
     chunk
     :clj
     (update chunk :css
       (fn [css]
         #_(!? :log css)
         (-> css
             ;; collapse multiple whitespace to one first
             (str/replace #"\s+" " ")
             ;; remove comments
             (str/replace #"\/\*(.*?)\*\/" "")
             ;; remove a few more whitespace
             (str/replace #"\s\{\s" "{")
             (str/replace #";\s+\}\s*" "}")
             (str/replace #";\s+" ";")
             (str/replace #":\s+" ":")
             (str/replace #"\s*,\s*" ",")
             )))))

(defn minify [build-state]
  (update build-state :chunks update-vals minify-chunk))

(defn index-source [build-state src]
  (let [{:keys [ns] :as contents}
        (ana/find-css-in-source src)]
    (if (not contents)
      build-state
      ;; index every namespace so we can follow requires properly
      ;; without anything else having to parse everything again
      ;; even though :css might be empty
      (assoc-in build-state [:namespaces ns] contents))))

(defn init []
  {:namespaces               {}
   :alias-groups             {}
   :aliases                  {}
   :spacing                  {}
   :all-utility-classes      utility-classes/utility-classes
   :all-utility-classes-keys utility-classes/utility-class-ks
   :all-design-tokens        tokens/design-tokens})

;; IO stuff not available in CLJS environments
#?(:clj
   (do (defn clj-file? [filename]
         ;; .clj .cljs .cljc .cljd
         (re-matches #".+\.clj[cs]?$" filename))

       (defn index-file [build-state ^File file]
         (let [src (slurp file)]
           (index-source build-state src)))

       (defn index-path
         [build-state ^File root config]
         #_(ff build-state)
         (let [files
               (->> root
                    (file-seq)
                    (filter #(clj-file? (.getName ^File %)))
                    (remove #(.isHidden ^File %)))]

           ;; FIXME: reducers/parallel?
           ;; takes ~80ms for entire shadow-cljs codebase which is fine
           ;; but also doesn't contain many sources with css, could be slow on bigger frontend projects
           ;; this can easily spread work in threads, just needs to merge namespaces after
           (reduce index-file build-state files)))


       (defn safe-pr-str
         "cider globally sets *print-length* for the nrepl-session which messes with pr-str when used to print cache or other files"
         [x]
         (binding [*print-length*         nil
                   *print-level*          nil
                   *print-namespace-maps* nil
                   *print-meta*           nil]
           (pr-str x)))

       (defn write-index-to [{:keys [namespaces]
                              :as   build-state} ^File output-to]
         (io/make-parents output-to)
         (spit output-to (safe-pr-str {:version    1
                                       :namespaces namespaces}))
         build-state)

       (defn write-outputs-to [build-state ^File output-dir]
         (reduce-kv
          (fn [_ chunk-id {:keys [css]
                           :as   chunk}]
            (let [output-file (io/file output-dir (str (name chunk-id) ".css"))]
              (io/make-parents output-file)
              (spit output-file css)))
          nil
          (:chunks build-state))

         build-state)

       (defn log-load-indexes [url contents]
         (co 'load-indexes-from-classpath
             (bling 
              "Num instances of \"(css...\" by namespace, within:"
              "\n\n"
              [:subtle url]
              "\n\n"
              (-> (? :data {:non-coll-mapkey-length-limit 50
                            :theme                        "Neutral Light"}
                     (reduce-kv (fn [acc k v]
                                  (if-let [css (-> v :css seq)]
                                    (assoc acc k (count css))
                                    acc ))
                                {}
                                (:namespaces contents)))
                  :formatted
                  :string))))
       
       (defn log-enum-seq [m]
         (co 'load-indexes-from-classpath
             (bling 
              "info\n"
              (-> (? :data {:non-coll-mapkey-length-limit 50
                            :theme                        "Neutral Light"}
                     m)
                  :formatted
                  :string))))

       
       (defn load-indexes-from-classpath [build-state]
         ;; More info here https://github.com/thheller/shadow-css/blob/main/doc/css.md#on-extensibility
         (reduce
          (fn [build-state url]
            (let [{:keys [version namespaces]
                   :as   contents}
                  (-> (slurp url)
                      (edn/read-string))]

               ;; KUSHI PRINTING
              (log-load-indexes url contents)

               ;; FIXME: validate version?
              (-> build-state
                  (assoc-in [:sources url] contents)
                  (update :namespaces merge namespaces))))
          build-state
          (let [current-thread       (Thread/currentThread)
                context-class-loader (.getContextClassLoader current-thread)
                resources            (.getResources context-class-loader "shadow-css-index.edn")
                enum-seq             (enumeration-seq resources)]
            (log-enum-seq {:current-thread       current-thread              
                           :context-class-loader context-class-loader  
                           :resources            resources                        
                           :enum-seq             enum-seq
                           })
            enum-seq)

          #_(-> (Thread/currentThread)
                (.getContextClassLoader)
                (.getResources "shadow-css-index.edn")
                (enumeration-seq))))

       (defn merge-left [left right]
         (merge right left))

       #_(defn load-default-aliases-from-classpath [build-state]
         (co "load-default-aliases-from-classpath"
             (bling "Adding "
                    [:blue ":aliases"]
                    ", aka \"utility-classes\" from "
                    [:subtle "\"kushi/css/build/aliases.edn\""]))
         (update build-state
                 :aliases
                 merge-left
                 (edn/read-string
                  (slurp (io/resource "kushi/css/build/aliases.edn")))))

       (defn load-preflight-from-classpath [build-state]
         (co "load-preflight-from-classpath"
             (bling "Adding "
                    [:blue ":preflight-src"]
                    ", from "
                    [:green " \"kushi/css/build/preflight.css\""]))
         
         (assoc build-state
                :preflight-src
                (slurp (io/resource "kushi/css/build/kushi-reset.css"))))

       (defn start
         ([]
          (start (init)))
         ([build-state]
          (-> build-state
              (load-preflight-from-classpath)
              (load-indexes-from-classpath) )))))
