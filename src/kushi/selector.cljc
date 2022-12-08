(ns ^:dev/always kushi.selector
  #?(:clj (:require [garden.def]))
  (:require
   [clojure.string :as string]
   [kushi.config :refer [user-config]]))

(defn nsqkw->selector-friendly [s]
  (-> (str s)
      (string/replace #"\." "-")
      (string/replace #"/" "_")
      (string/replace #":" "")))

(defn selector-name
  [{:keys [assigned-class
           kushi-selector
           cache-key
           :kushi/process]}]
  (let [autogen              (str "_" cache-key)
        prefixed-name-for-el (some-> assigned-class name)
        selector*            (or
                               kushi-selector
                               prefixed-name-for-el
                               autogen)
        selector             (if kushi-selector
                               selector*
                               (str (when-not (string? assigned-class) ".") selector*))
        ppend*               (:prepend-selector* user-config)
        ppend                (when (and (not= process :kushi/css-reset)
                                        ppend*
                                        (or (string? ppend*)
                                            (keyword? ppend*)))
                               (name ppend*))
        ret                  {:selector*     selector*
                              :selector      (str ppend selector)
                              :prefixed-name prefixed-name-for-el}]

    #_(when true
        (pprint
         {:assigned-class assigned-class
          :kushi-selector kushi-selector
          :cache-key      cache-key
          :autogen        autogen
          :selector       selector
          :selector*      selector*}))
    ret))
