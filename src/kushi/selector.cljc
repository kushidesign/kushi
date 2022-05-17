(ns ^:dev/always kushi.selector
  #?(:clj (:require [garden.def]))
  (:require
   [clojure.string :as string]
   [kushi.config :refer [user-config]]
   [kushi.utils :as util :refer [auto-generated-selector keyed]]))

(defn nsqkw->selector-friendly [s]
  (-> (str s)
      (string/replace #"\." "-")
      (string/replace #"/" "_")
      (string/replace #":" "")))

(defn selector-name
  [{:keys [
          ;;  kushi-class-prefix
           kushi-class
           kushi-selector
           kushi-prepend
           defclass-name
           cache-key
           atomic-class?] :as m}]

#_(?+
 :selector-name
 (keyed
  kushi-class
  kushi-selector
  kushi-prepend
  defclass-name
  cache-key
  atomic-class?))

  (let [;;autogen                       (auto-generated-selector)
        autogen                       (str "_" cache-key)
        ;; kushi-class-prefix            (or kushi-class-prefix (:kushi-class-prefix user-config))

        prepend                       kushi-prepend
        prefixed-name-for-el          (some-> kushi-class name)

        ;; Currently using same shared class as sx
        shared-class-prefix           (if atomic-class?
                                        (:atomic-class-prefix user-config)
                                        (:kushi-class-prefix user-config))
        selector*                     (or
                                       kushi-selector
                                       (when defclass-name (nsqkw->selector-friendly (str shared-class-prefix defclass-name)))
                                       prefixed-name-for-el
                                       autogen)
        selector                      (if kushi-selector
                                        selector*
                                        ;; TODO replace this stuff with :kushi/prepend
                                        (str (when-not (nil? prepend) (name prepend))
                                             "."
                                             selector*))
        ret                           {:selector*     selector*
                                       :selector      selector
                                       :prefixed-name prefixed-name-for-el}]

    #_(when true #_(state/debug?)
      (println
       {:kushi-class kushi-class
        :defclass-name defclass-name
        :autogen autogen
        :selector selector
        :selector* selector*}))
    ret))
