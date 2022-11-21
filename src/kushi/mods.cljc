(ns kushi.mods
 (:require [kushi.utils :as util :refer [keyed]]
           [kushi.specs2 :as specs2]
           [clojure.string :as string]
           [kushi.state2 :as state2]))

(defn parent-or-ancestor [x s]
 (when-not (or (string/blank? x) (nil? x)) (str x s)) )

(defn leading-pseudo-classes [s]
  (string/replace s
                  (re-pattern (str "^" specs2/css-pseudo-class-re))
                  #(str ":" %)))

(defn pseudo-elements [s]
  (-> s
      (string/replace (re-pattern specs2/css-pseudo-element-re)
                      #(str "::" %))
      (string/replace #":::" "::")))

(defn mods [x-wo-mq prop-re]
  (let [;; parents & ancestors
        mods1*            (string/replace x-wo-mq prop-re "")
        dark-re          #"^dark:(.+)"
        [_ mods-dark]    (re-find dark-re mods1*)
        dark?            (or mods-dark (= mods1* "dark"))
        mods1            (or mods-dark (if (= "dark"mods1*) "" mods1*))
        parent-re        #"\:?has-parent\((.+)\)"
        ancestor-re      #"\:?has-ancestor\((.+)\)"
        [_ parent*]      (re-find parent-re mods1)
        [_ ancestor*]    (re-find ancestor-re mods1)
        ancestor*        (str (when dark? ".dark")
                              (when ancestor* (str " " ancestor*)))
        parent           (parent-or-ancestor parent* ">")
        ancestor         (parent-or-ancestor ancestor* " ")
        mods2*           (string/replace mods1 parent-re "")
        mods2            (string/replace mods2* ancestor-re "")

        ;; class, id, nested, adjacent, and sibling selectors
        mods3            (string/replace mods2 #"\&([\[\>\+\*\~\.\#])" #(second %))
        mods             (let [with-spaces            (string/replace mods3 #"\&\_" " ")
                               leading-pseudo-classes (leading-pseudo-classes with-spaces)
                               pseudo-elements        (pseudo-elements leading-pseudo-classes)
                               ret                    pseudo-elements]
                           ret)]

    #_(when (state2/trace? )
      (println (keyed
          mods1*
          mods-dark
          mods1
          mods3
          mods
          parent*
          parent
          ancestor*
          ancestor
          mods2*
          mods2
          mods3
          mods)))
    (keyed parent ancestor mods)))
