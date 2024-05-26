(ns kushi.playground.snippet
  (:require [clojure.string :as string]
            [clojure.walk :as walk]
            [fireworks.core :refer [!? ? pprint]]
            [kushi.core :refer (sx keyed)]
            [kushi.playground.demobox.core :refer (copy-to-clipboard-button)]
            [kushi.ui.util]))

(defn- component->sym [f]
  (-> f
      pprint
      with-out-str
      (string/split #"\$")
      last
      string/trim
      drop-last
      string/join
      symbol))


(defn- formatted-code [s]
  [:pre
   [:code {:class :language-clojure
           :style {:white-space :pre
                   :line-height 1.5}}
    s]])


(defn- snippet-section-header [header]
  [:h3 (sx 'snippet-section-header
           :.small
           :&_code:fs--$xsmall
           :&_.code:fs--$xsmall
           :&_.code:fw--$normal
           :mbe--0.75em)
   header])


(defn- code-snippet-args
  "This normalizes args for display, turning component references into symbols"
  [coll]
  (walk/postwalk #(if (fn? %)
                   (let [ret (component->sym %)]
                     ret)
                   %)
                 coll))


(defn select-keys-re
  [m re]
  (->> m
       (keep (fn [[k v]]
               (when (re-find re (name k))
                 [k v])))
       (into {})))


(defn- sx-args [merged-attrs*]
  (->> merged-attrs*
       :class
       (keep #(some->> % 
                       name
                       (re-find #"^[^_].*$")
                       (str ".")
                       keyword))
       seq))


(defn- sx-call
  [ma qa]
  (if (:instance-sx-attrs? ma)
    (if (not= 'sx (first qa))
      qa
      (concat '(sx) [qa]))
    (let [sx-args (sx-args ma)]
      (concat '(sx)
              sx-args
              (some-> qa rest)))))


(defn- code-snippet 
  "Returns something like this:
   {:code-snippet
    [:pre
      [:code
      {:class :language-clojure,
        :style {:white-space :pre, :line-height 1.5}}
      \"[button (sx :.rounded :.small) [icon :play-arrow] \\\"Play\\\"]\n\"]]]}"
  [component
   {:keys [args] :as merged-attrs*}
   quoted-attrs]
  (let [sx-call      (sx-call merged-attrs* quoted-attrs)
        component    (component->sym component)
        args         (code-snippet-args args)
        code-snippet (formatted-code
                      (with-out-str
                        (pprint (into [component sx-call] args)
                                {:max-width 50})))]
    code-snippet))


(defn- reqs-coll
  "Returns something like this:
   '[[kushi.ui.button.core  :refer  [button]]
     [kushi.ui.icon.core  :refer  [icon]]]"
  [merged-attrs*]
  (some->> merged-attrs*
           :reqs-by-refers
           keys
           distinct
           (reduce (fn [acc v]
                     (let [_ns    (get (:reqs-by-refers merged-attrs*) v)
                           refers (or (get acc _ns) [])]
                       (assoc acc _ns (conj refers v))))
                   {})
           (reduce-kv (fn [acc k v]
                        (conj acc [k :refer v]))
                      [])))

(defn- snippets
  [component merged-attrs* quoted-attrs]
  ;; (!? 'snipptes (keyed component merged-attrs* quoted-attrs))
  (let [code-snippet   (code-snippet component merged-attrs* quoted-attrs)
        reqs-coll      (reqs-coll merged-attrs*)
        reqs-snippet*  (string/join "\n" reqs-coll)
        reqs-snippet   (map #(formatted-code (with-out-str (pprint %)))
                            reqs-coll)]
    (keyed code-snippet reqs-snippet* reqs-snippet)))


(defn- snippet-section [header-content coll]
  [:section 
   [snippet-section-header header-content]
   (into [:section 
          (sx 'component-details-popover-snippet-box
              :.relative
              :.code
              :&_code:fs--$xsmall
              :&_.code:fs--$xsmall
              :p--1.5em
              :pie--3.5em
              :w--100%)
          [copy-to-clipboard-button
           (sx :.top-right-corner-inside!
               {:-text-to-copy (:reqs-snippet* snippets)})]]
         coll)])


(defn component-details-popover
  []
  (fn [component
       merged-attrs*
       quoted-attrs]
    [:div
     (sx 'component-details-popover
         :.relative
         :.flex-row-fs
         :.small
         :ai--fs
         :pi--2.5em
         :pb--2.25em:2.75em
         :min-width--200px
         :min-height--120px)

     [:div
      (sx 'my-form
          :.flex-col-fs
          :gap--1em
          :&_.kushi-text-input-label:min-width--7em
          :&_.kushi-input-inline:gtc--36%:64%)
      (let [snippets              (snippets component
                                            merged-attrs*
                                            quoted-attrs)]
        [:div (sx :.flex-col-fs :gap--1rem)
         [snippet-section
          (kushi.ui.util/backtics->hiccup "Paste into the `:require` section of your `:ns` form:")
          (:reqs-snippet snippets)]

         [snippet-section
          "Component snippet:"
          (list (:code-snippet snippets))]])]]))
