(ns ^:dev/always kushi.config
  (:require
   [clojure.edn :as edn]))


(def default-kushi-responsive
  [:sm {:min-width :640px}
   :md {:min-width :768px}
   :lg {:min-width :1024px}
   :xl {:min-width :1280px}
   :2xl {:min-width :1536px}])

(defn valid-responsive? [m]
  (and (map? m)
       (not-empty m)))

(defn load-edn
  "Load edn from an io/reader source (filename or io/resource)."
  [source]
  #?(:clj (do
            (use 'clojure.java.io)
            (try
              (with-open [r (clojure.java.io/reader source)]
                (edn/read (java.io.PushbackReader. r)))

              ;; TODO use warning or error panel
              ;; TODO consolidate these somewhere?
              (catch java.io.IOException e
                (printf (str "Warning in kushi.config/load-edn: "
                             "Could not open '%s': "
                             " %s.\nIgnore the above warning if you are running tests from the source repo kushi/test/kushi/test.clj.\n")
                        source
                        (.getMessage e)))

              (catch RuntimeException e
                (printf "Error parsing edn file '%s':\n %s\n"
                        source
                        (.getMessage e)))))))

(def user-config-defaults
  {
   ;; REQUIRED

   ;; Needs to be a path to a dir
   :css-dir                        nil


   ;; OPTIONAL

   ;; Misc --------------------------------------------------------------
   ;; User theme - this should be a fully qualified symbol to a theme config map
   ;; e.g. 'my-project.theme/my-theme
   :theme                          nil
   :caching?                       false

   ;; Usually would be the id of the "app" container, e.g "#app".
   :selector-prepend               nil

   ;; A typical override for this (for narrowing) would be the id of
   ;; the "app" container, e.g "#app". Defaults to ":root"
   :design-tokens-root             nil
   :data-attr-name                 :sx


   ;; Runtime injection --------------------------------------------------
   :inject-at-runtime-prod?        false
   :inject-at-runtime-dev?         true


   ;; For leaving things out of css --------------------------------------
   :add-stylesheet-prod?           true
   :add-stylesheet-dev?            true
   :add-system-font-stack?         true
   :add-css-reset?                 true
   :add-design-tokens?             true

   ;; If :add-kushi-ui-theming? is set to false, it will not include theming classes
   ;; for for kushi.ui components such as buttons, tags, etc.
   :add-kushi-ui-theming?          true

   ;; If :add-kushi-ui-theming-defclass? is set to false, it will not include defclasses
   ;; for kushi.ui components (these are defined internally from namespaces within kushi.ui).
   ;; You probably do not want to disable this unless you are developing on kushi itself.
   :add-kushi-ui-theming-defclass? true

   ;; Set this to false to leave out dark theme variants for kushi.ui related classes
   :add-kushi-ui-dark-theming?     true

   ;; Set this to false to leave out light theme variants for kushi.ui related classes
   :add-kushi-ui-light-theming?    true

   ;; Set this to false to leave out user theme-related classes
   :add-ui-theming?                true

   ;; Set this to false to leave out all of kushi's built-in utility classes
   :add-kushi-defclass?            true

   ;; Set this to false to leave out any shared classes created by the user via the defclass macro
   :add-user-defclass?             true

   ;; Set this to false to leave out any styling classes created by the user via the sx macro
   :add-user-sx?                   true



   ;; NOTE - probably just remove this option or rethink interface?

   ;; You can explicitly opt-in to include support for `kind` and `semantic` variants of certain kushi.ui components.
   ;; By default, support for all these variants is included in the css, so narrowing it will reduce
   ;; the amount of default theme-related css that gets included in the css.

   ;; The components that use `kind` and `semantic` variants are:
   ;; kushi.ui.button.core/button
   ;; kushi.ui.tag.core/tag

   ;; Using this example value below would limit the support to just a subset of these variants:
   ;; {:button {:semantic #{:positive :negative}
   ;;           :kind     #{:primary :minimal}}
   ;;  :tag    {:semantic #{:positive :negative}}}

   :kushi-ui-variants              :all





   ;; Build process logging ----------------------------------------------
   :log-build-report?              true
   :log-build-report-style         :simple
   :log-kushi-version?             true
   :log-updates-to-cache?          false
   :log-cache-call-sites?          false


   ;; Experimental - add later ------------------------------------------
   ;; :scaling-system          nil

   ;; Chopping block
   ;; :warn-duplicates?        true
   ;; :ui                      []
   ;; :select-ns               nil
   })

(defn ->user-config [m]
  (let [config*         m
        user-responsive (apply array-map (:media config*))
        responsive      (if (valid-responsive? user-responsive)
                          user-responsive
                          (apply array-map default-kushi-responsive))
        ret*            (assoc config* :media responsive)
        ret             (merge user-config-defaults ret*)]
    ret))

(def user-config
  (let [config* (let [m (load-edn "kushi.edn")]
                  (if (map? m) m {}))]
    (->user-config config*)))

;; TODO used?
(defn ->user-config-args-sx-defclass
  "Takes a merged config and selects only the keys needed for hashing (cache key) for sx and defclass."
  [m]
  (select-keys m
               [:data-attr-name
                :selector-prepend
                :media]))

(def user-config-args-sx-defclass
  (->user-config-args-sx-defclass user-config))

(def user-css-file-path
  (str (or (:css-dir user-config) (:static-css-dir user-config))
       "/"
       (or (:css-filename user-config) "kushi.css")))

;; ! Update kushi version here for console printing and cache file path generation
(def version* "1.0.0-a.14")

;; Optionally unsilence the ":LOCAL" bit when developing kushi from local filesystem (for visual feedback sanity check).
(def version (str version* #_":LOCAL"))


