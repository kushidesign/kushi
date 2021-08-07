(ns ^:dev/always kushi.selector
  #?(:clj (:require [garden.def]))
  (:require
   [clojure.string :as string]
   [kushi.config :refer [user-config]]
   [kushi.utils :refer [auto-generated-hash]]))


(defn nsqkw->selector-friendly [s]
  (-> (str s)
      (string/replace #"\." "-")
      (string/replace #"/" "_")
      (string/replace #":" "")))

(defn selector-name
  [{:keys [ident element f prefix parent classname defclass-hash]}]
  (let [hash (auto-generated-hash)
        {global-parent :parent
         global-prefix :prefix
         defclass-prefix :defclass-prefix
         add-empty-classes? :add-empty-classes?
         prefix-parent-to-defclass? :prefix-parent-to-defclass?} user-config
        prefix (or prefix global-prefix)
        parent (or parent global-parent)
        prefixed-names-for-selectors? (and prefix ident (not add-empty-classes?))
        prefixed-name (when (and prefix ident) (str (name prefix) "__" (name ident)))
        selector* (if classname
                    (str (or defclass-prefix defclass-hash) "__" (nsqkw->selector-friendly classname))
                    (if prefixed-names-for-selectors?
                      prefixed-name
                      hash))
        use-parent-prefix? (if defclass-hash
                             prefix-parent-to-defclass?
                             (not (nil? parent)))
        selector (str (when use-parent-prefix? (str (name parent) " "))
                      (when element (name element))
                      "."
                      selector*)]
    {:selector* selector*
     :selector selector
     :prefixed-name prefixed-name}))
