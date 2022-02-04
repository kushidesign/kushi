(ns ^:dev/always kushi.selector
  #?(:clj (:require [garden.def]))
  (:require
   [clojure.string :as string]
   [kushi.config :refer [user-config]]
   [kushi.utils :as util :refer [auto-generated-hash keyed ?]]))

(defn nsqkw->selector-friendly [s]
  (-> (str s)
      (string/replace #"\." "-")
      (string/replace #"/" "_")
      (string/replace #":" "")))

(defn selector-name
  [{:keys [ident
           element
           prefix
           ancestor
           defclass-name
           atomic-class?] :as m}]
  (let [hash                          (auto-generated-hash)
        {global-ancestor :ancestor
         global-prefix :prefix}       user-config
        prefix                        (or prefix global-prefix)
        ancestor                      (or ancestor global-ancestor)
        prefixed-names-for-selectors? (and prefix ident (not (:add-empty-classes? user-config)))
        prefixed-name-for-el          (when (and prefix ident) (str (name prefix) (name ident)))
        shared-class-prefix           (if atomic-class?
                                        (:atomic-class-prefix user-config)
                                        (:defclass-prefix user-config))
        ;; TODO take out defclass-hash
        selector*                     (if defclass-name
                                        (nsqkw->selector-friendly (str shared-class-prefix defclass-name))
                                        (if prefixed-names-for-selectors?
                                          prefixed-name-for-el
                                          hash))
        use-ancestor-prefix?          (if defclass-name
                                        (:prefix-ancestor-to-defclass? user-config)
                                        (not (nil? ancestor)))
        selector                      (str (when use-ancestor-prefix? (str (name ancestor) " "))
                                           (when element (name element))
                                           "."
                                           selector*)
        ret                           {:selector* selector*
                                       :selector selector
                                       :prefixed-name prefixed-name-for-el}]

    #_(? 'kushi.selector/selector-name (keyed m ret))
    #_(pprint
       {:ident ident
        :prefix prefix
        :prefixed-name prefixed-name
        :defclass-name defclass-name
        :defclass-prefix defclass-prefix
        :defclass-hash defclass-hash
        :prefixed-names-for-selectors? prefixed-names-for-selectors?
        :hash hash
        :selector selector
        :selector* selector*})
    ret))
