(ns ^:dev/always kushi.selector
  #?(:clj (:require [garden.def]))
  (:require
   [clojure.string :as string]
   [kushi.config :refer [user-config]]
   [kushi.utils :as util :refer [auto-generated-hash]]))

(defn nsqkw->selector-friendly [s]
  (-> (str s)
      (string/replace #"\." "-")
      (string/replace #"/" "_")
      (string/replace #":" "")))

(defn selector-name
  [{:keys [ident
           element
           prefix
           parent
           defclass-name
           defclass-hash]}]
  (let [hash                          (auto-generated-hash)
        {global-parent :parent
         global-prefix :prefix}       user-config
        prefix                        (or prefix global-prefix)
        parent                        (or parent global-parent)
        prefixed-names-for-selectors? (and prefix ident (not (:add-empty-classes? user-config)))
        prefixed-name                 (when (and prefix ident) (str (name prefix) (name ident)))
        selector*                     (if defclass-name
                                        (str (or (:defclass-prefix user-config) defclass-hash)
                                             "__"
                                             (nsqkw->selector-friendly defclass-name))
                                        (if prefixed-names-for-selectors?
                                          prefixed-name
                                          hash))
        use-parent-prefix?            (if defclass-hash
                                        (:prefix-parent-to-defclass? user-config)
                                        (not (nil? parent)))
        selector                      (str (when use-parent-prefix? (str (name parent) " "))
                                           (when element (name element))
                                           "."
                                           selector*)]

    #_(util/pprint+
       "selector-name"
       {:defclass-name defclass-name
        :defclass-prefix defclass-prefix
        :defclass-hash defclass-hash
        :prefixed-names-for-selectors? prefixed-names-for-selectors?
        :hash hash
        :selector selector
        :selector* selector*})

    {:selector* selector*
     :selector selector
     :prefixed-name prefixed-name}))
