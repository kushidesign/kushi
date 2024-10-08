(ns ^:dev/always kushi.config
  (:require
   [kushi.utils :as util]
   [clojure.string :as string]
   [clojure.set :as set]
   [clojure.edn :as edn]))

(def default-font-families-from-google-fonts
  {:code "Fira Code"
   :sans "Inter"
   :serif "Cormorant"})


(def default-kushi-responsive
  [:xsm {:min-width :480px}
   :sm {:min-width :640px}
   :md {:min-width :768px}
   :lg {:min-width :1024px}
   :xl {:min-width :1280px}
   :xxl {:min-width :1536px}])

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
                (printf
                 (str "Warning in kushi.config/load-edn: "
                      "Could not open '%s': "
                      " %s.\nIgnore the above warning if you are running"
                      " tests from the source repo kushi/test/kushi/test.clj.\n")
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
   :css-dir                             nil


   ;; OPTIONAL

   ;; Misc ---------------------------------------------------------------------
   ;; User theme - this should be a fully qualified symbol to a theme config
   ;; map e.g. 'my-project.theme/my-theme
   :theme                               nil
   :caching?                            false

   ;; Usually would be the id of the "app" container, e.g "#app".
   :selector-prepend                    nil

   ;; A typical override for this (for narrowing) would be the id of
   ;; the "app" container, e.g "#app". Defaults to ":root"
   ;; TODO - example comment to show both above in resulting css file.
   :design-tokens-root                  nil
   :data-attr-name                      :sx

   ;; Responsive breakpoint scale, which must be a vector of kwargs, not a map.

   ;; The default scale that takes effect if you don't provide one:
   ;; :media                            [:xsm {:min-width :480px}
   ;;                                    :sm {:min-width :640px}
   ;;                                    :md {:min-width :768px}
   ;;                                    :lg {:min-width :1024px}
   ;;                                    :xl {:min-width :1280px}
   ;;                                    :xxl {:min-width :1536px}]
    
   ;; Example scale that would be "desktop-first", because the size
   ;; goes from largest to smallest.
   ;; :media                            [:2xl {:max-width :1536px}
   ;;                                    :xl {:max-width :1280px}
   ;;                                    :lg {:max-width :1024px}
   ;;                                    :md {:max-width :768px}
   ;;                                    :sm {:max-width :640px}]

   ;; Runtime injection -------------------------------------------------------
   :inject-at-runtime-prod?             false
   :inject-at-runtime-dev?              true


   ;; For leaving things out of css -------------------------------------------
   :add-stylesheet-prod?                true
   :add-stylesheet-dev?                 true

   :add-css-reset?                      true
   :add-design-tokens?                  true


   ;; If :add-kushi-ui-theming? is set to false, it will not include theming
   ;; classes for for kushi.ui components such as buttons, tags, etc.
   :add-kushi-ui-theming?               true

   ;; If :add-kushi-ui-theming-defclass? is set to false, it will not include
   ;; defclasses for kushi.ui components (these are defined internally from
   ;; namespaces within kushi.ui). You probably do not want to disable this
   ;; unless you are developing on kushi itself.
   :add-kushi-ui-theming-defclass?      true

   ;; Set this to false to leave out dark theme variants for kushi.ui related
   ;; classes.
   :add-kushi-ui-dark-theming?          true

   ;; Set this to false to leave out light theme variants for kushi.ui related
   ;; classes.
   :add-kushi-ui-light-theming?         true

   ;; Set this to false to leave out user theme-related classes
   ;; TODO - provide examples of what is leaves out
   :add-ui-theming?                     true

   ;; Set this to false to leave out all of kushi's built-in utility classes
   ;; TODO - provide examples of what is leaves out
   :add-kushi-defclass?                 true

   ;; Set this to false to leave out all of kushi's built-in utility classes,
   ;; override versions.
   :add-kushi-defclass-overrides?       true

   ;; Set this to false to leave out any shared classes created by the user via
   ;; the defclass macro You probably do not want to disable this unless you
   ;; are developing on kushi itself.
   :add-user-defclass?                  true

   ;; Set this to false to leave out any shared classes (override versions)
   ;; created by the user via the defclass macro.
   ;; You probably do not want to disable this unless you are developing on
   ;; kushi itself.
   :add-user-defclass-overrides?        true

   ;; Set this to false to leave out any styling classes created by the user
   ;; via the sx macro. You probably do not want to disable this unless you are
   ;; developing on kushi itself.
   :add-user-sx?                        true

   ;; You can explicitly elide support for `kind` and `semantic` variants of
   ;; certain kushi.ui components. By default, support for all these variants
   ;; is included in the css, so narrowing it will reduce the amount of default
   ;; theme-related styles that gets included in the css.
    
   ;; The components that use `kind` and `semantic` variants are:
   ;; kushi.ui.button.core/button
   ;; kushi.ui.tag.core/tag

   ;; elide-ui-variants-semantic can include the following keywords:
   ;; :accent :negative :warning :neutral :positive
   :elide-ui-variants-semantic          #{} 
   ;; elide-ui-variants-style can include the following keywords:
   ;; can include :bordered :minimal :filled
   :elide-ui-variants-style             #{} 
    
   ;; Setting :elide-unused-kushi-utility-classes? to true will only include
   ;; kushi utility classes that are explicitly used within the sx macro.
   ;; This will reduce the amount of css in the output.
   ;; This means that only the following syntax examples will result in the
   ;; utility class being written to your css file:
   ;; [:div (sx :.absolute) "hi"]
   ;; [:div (sx (when x :.absolute)) "hi"]
   ;; [:div (sx {:class [:absolute]}) "hi"]
   ;; Note that with this option on, the following examples cannot be
   ;; guaranteed to work:
   ;; [:div.absolute "hi"]
   ;; [:div {:class [:absolute]} "hi"]
    
   :elide-unused-kushi-utility-classes? false

   ;; If you have :elide-unused-kushi-utility-classes? set to true, you can add
   ;; specific kushi utility classes to `:kushi-utility-classes-to-always-add`
   ;; in order to ensure they are always written. The value must a set. Any
   ;; values that are not strings or keywords will be discarded. Remaining
   ;; values will be stringified, bookended with "^" and "$" and passed to
   ;; clojure.core/re-pattern. The "flex-.+" example below demonstrates how to
   ;; included "families" of kushi utility classes.
   ;; #{:xxxtight "flex-.+" "heavy"} => {#"^xxxtight$" #"^flex-.+$" #"^heavy$"}
    
   :kushi-utility-classes-to-always-add #{}

   ;; Build process logging ----------------------------------------------------
   :log-build-report?                   true
   :log-build-report-style              :simple ;; :simple | :detailed
   :log-kushi-version?                  true
   :log-updates-to-cache?               false ;; not yet documented
   :log-cache-call-sites?               false ;; not yet documented
   :log-relevant-specs?                 false ;; not yet documented
    
   ;; log-warning-banner is a vector of strings that makes an asci-art banner
   ;; above warnings. You can use this if you want to draw more attention
   ;; to the terminal when there is a problem/warning. This is optional, but
   ;; perhaps useful in helping your catch malformed style arguments to `sx`.
   :log-warning-banner                  nil


   ;; Experimental - add later -------------------------------------------------
   ;; :scaling-system          nil


   ;; Chopping block -----------------------------------------------------------
   ;; :warn-duplicates?        true
   ;; :ui                      []
   ;; :select-ns               nil
   })

(defn utility-classes-to-always-add
  [config*]
  (let [set1*    (:kushi-utility-classes-to-always-add user-config-defaults)
        set1     (if (set? set1*) set1* #{})
        set2*    (:kushi-utility-classes-to-always-add config*)
        set2     (if (set? set2*) set2* #{})
        combined (set/union set1 set2)
        ret      (keep #(cond #?(:clj (instance? java.util.regex.Pattern %))
                              %
                              (util/nameable? %)
                              (re-pattern (str "^" (string/replace (name %) #"^\." "") "$")))
                       combined)]
    (into #{} ret)))

(defn ->user-config [m]
  (let [config*         m
        user-responsive (apply array-map (:media config*))
        responsive      (if (valid-responsive? user-responsive)
                          user-responsive
                          (apply array-map default-kushi-responsive))
        always-add      (utility-classes-to-always-add config*)
        ret*            (assoc config* :media responsive :kushi-utility-classes-to-always-add always-add)
        ret             (merge user-config-defaults
                               ret*
                               {:warnings-and-errors {:print-specs? (:log-relevant-specs? m)}})]
    ret))

(def user-config
  (let [config* (let [m (load-edn "kushi.edn")]
                  (if (map? m) m {}))
        ret     (->user-config config*)]
    ret))

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
(def version* "1.0.0-a.22")

;; Optionally unsilence the ":LOCAL" bit when developing kushi from local filesystem (for visual feedback sanity check).
(def version (str version* #_":LOCAL"))


