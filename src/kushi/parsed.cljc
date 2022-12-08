(ns kushi.parsed
  (:require [kushi.utils :as util :refer [keyed]]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [kushi.shorthand :as shorthand]
            [kushi.mods :as mods]
            [kushi.config :refer [user-config]]
            [kushi.specs2 :as specs2]))

(defn parsed [coll {:keys [selector]}]
  (doall
   (map (fn [[x v]]
          (let [;; media-query
                mq-keys                        (->> user-config :media keys (map name) (into #{}))
                mq-re                          (re-pattern (str "^(" (string/join "|" mq-keys) ")\\:.+"))
                [_ mq]                         (re-find mq-re (name x))
                x-wo-mq                        (string/replace (name x) (re-pattern (str "^" mq ":")) "")

                ;; css-property
                prop-re                        #"\:-?[a-z-]+$"
                mods?                          (not (nil? (re-find #":" x-wo-mq)))
                prop-kw                        (if mods?
                                                 (let [ret* (re-find prop-re x-wo-mq)
                                                       ret  (-> ret* (subs 1) keyword)]
                                                   ret)
                                                 (keyword x-wo-mq))
                css-prop                       (-> prop-kw shorthand/key-sh name)

                ;; hydrated shorthand value
                css-value                      (if (s/valid? ::specs2/s|kw v)
                                                 (if (= css-prop "content")
                                                   v
                                                   (name (or (shorthand/enum-prop-shorty (keyword css-prop)
                                                                                         (keyword v))
                                                           ;; hydrate css shorthand and alternations
                                                             (util/hydrate-css-shorthand+alternations v))))
                                                 v)
                {:keys [mods parent ancestor]} (when mods? (mods/mods x-wo-mq prop-re))

                ;; compound selector
                compound-selector              (str (or parent ancestor) selector mods)]

            (keyed
             selector
             compound-selector
             mq
             parent
             ancestor
             mods
             css-prop
             css-value)
            ))
        coll)))
