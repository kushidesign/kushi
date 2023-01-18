(ns kushi.playground.util
  (:require
   [markdown-to-hiccup.core :as md->hc]
   [clojure.pprint :refer [pprint]]
   [clojure.string :as string]))

(def kushi-github-url  "https://github.com/kushidesign/kushi")
(def kushi-clojars-url "https://clojars.org/design.kushi/kushi")

(defn pprint-snippet [coll]
  (string/replace (with-out-str (pprint coll)) #"\n$" ""))

(defn meta->fname [m*]
  (-> m* meta :name str))

(defn require-snippet-text
  [m* refers]
  #_(js/console.log  {:m* m* :refers refers})
  (let [nm (meta->fname m*)
        vc (-> m*
               meta
               :ns
               #_(str "/" nm)
               )]
    #_(js/console.log {:nm nm :vc vc})
    (-> vc
        symbol
        vector
        (conj :refer (into [] (concat [(symbol nm)] refers)))
        str)))

(defn code-snippet [nm [_ x]]
  (let [{:keys [args code other-fn]} x
        [a1]          args
        [_sx sx-attr] (when (list? a1) a1)
        break?        (or (map? a1) (and (= _sx 'sx) (map? sx-attr)))]
    (-> code first str)
    #_(-> (str (into []
                   (cons (if break? :__BR__ :__NOBR__) args*)
                   #_(cons (symbol nm)
                           (cons (if break? :__BR__ :__NOBR__)
                                 args*))))
        (string/replace #" :__BR__" "\n")
        (string/replace #" :__NOBR__" "")) ))

(defn capitalize-words
  "Capitalize every word in a string"
  [s]
  (->> (string/split (str s) #"\b")
       (map string/capitalize)
       string/join))

(defn formatted-code [s]
  [:pre
   [:code {:class :language-clojure
           :style {:white-space :pre
                   :line-height 1.5}}
    s]])

(defn kushi-component-desc->md [coll]
  (string/replace (string/join " "
                               (map (fn [x] (if (= :br x) "____br____" x)) coll))
                  #"____br____"
                  "<br>" ))

(defn md->component [v]
  (-> v
      md->hc/md->hiccup
      md->hc/component))

(defn desc->hiccup [coll]
 (some->> coll
          kushi-component-desc->md
          md->component))
