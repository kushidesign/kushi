(ns kushi.playground.snippet
  (:require
   [fireworks.core :refer [? !? ?pp pprint]]
   [clojure.string :as string]
   [clojure.walk :as walk]
   [kushi.core :refer (sx keyed)]
   [kushi.playground.demobox.core :refer (copy-to-clipboard-button)]))

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

(defn- args-inner [syms v]
  (if (fn? v)
    (let [ret (component->sym v)]
      (vswap! syms conj ret)
      ret)
    v))

(defn select-keys-re
  [m re]
  (->> m
       (keep (fn [[k v]]
               (when (re-find re (name k))
                 [k v])))
       (into {})))

(defn- sx-args [m]
  (->> m
       :class
       (keep #(some->> % 
                       name
                       (re-find #"^[^_].*$")
                       (str ".")
                       keyword))
       seq))

(defn- code-snippet 
  "Returns something like this:
   {:code-snippet*
    \"[button (sx :.rounded :.small) [icon :play-arrow] \\\"Play\\\"]\",
    :code-snippet
    [:pre
      [:code
      {:class :language-clojure,
        :style {:white-space :pre, :line-height 1.5}}
      \"[button (sx :.rounded :.small) [icon :play-arrow] \\\"Play\\\"]\n\"]],
    :syms #object[cljs.core.Volatile {:val [button icon]}]}"
  [{:keys [args component] :as m}
   quoted-attrs]
  (let [sx-args              (sx-args m)
        sx-args-quoted-attrs (some-> quoted-attrs rest)
        sx-call              (concat '(sx) sx-args sx-args-quoted-attrs)
        component            (component->sym component)
        syms                 (volatile! [component])
        args                 (walk/postwalk (partial args-inner syms) args)
        ;; what is this code-snippet* for?
        ;; TODO - Eliminate
        code-snippet*        (str (into [component sx-call] args))
        code-snippet         (formatted-code
                              (with-out-str
                                (pprint (into [component sx-call] args)
                                        {:max-width 50})))]
    (keyed code-snippet* code-snippet syms)))


(defn- reqs-coll
  "Returns something like this:
   '[[kushi.ui.button.core  :refer  [button]]
     [kushi.ui.icon.core  :refer  [icon]]]"
  [code-snippet m]
  (some->> (seq (distinct @(:syms code-snippet)))
           (reduce (fn [acc v]
                     (let [_ns    (get (:reqs-by-refers m) v)
                           refers (or (get acc _ns) [])]
                       (assoc acc _ns (conj refers v))))
                   {})
           (reduce-kv (fn [acc k v]
                        (conj acc [k :refer v]))
                      [])))

(defn- snippets
  [m quoted-attrs]
  (let [code-snippet   (code-snippet m quoted-attrs)
        reqs-coll      (reqs-coll code-snippet m)
        reqs-snippet*  (string/join "\n" reqs-coll)
        reqs-snippet   (map #(formatted-code (with-out-str (pprint %)))
                            reqs-coll)]
    (merge code-snippet (keyed reqs-snippet* reqs-snippet))))


(defn component-details-popover [m quoted-attrs]
  (fn [m]
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
      (let [snippets              (snippets m quoted-attrs)
            snippet-section-attrs (sx 'component-details-popover-snippet-box
                                      :.relative
                                      :.code
                                      :&_code:fs--$xsmall
                                      :&_.code:fs--$xsmall
                                      :p--1.5em
                                      :pie--3.5em
                                      :w--100%)]
        [:div (sx :.flex-col-fs :gap--1rem)
         [:section 
          [snippet-section-header 
           [:span
            (str "Paste into the ")
            [:span.code ":require"]
            " section of your "
            [:span.code :ns]
            " form:"]]
          (into [:section snippet-section-attrs
                 [copy-to-clipboard-button
                  (sx :.top-right-corner-inside!
                      {:-text-to-copy (:reqs-snippet* snippets)})]]
                (:reqs-snippet snippets))]

         [:section 
          [snippet-section-header
           "Component snippet:"]
          [:section snippet-section-attrs
           [copy-to-clipboard-button
            (sx :.top-right-corner-inside!
                {:-text-to-copy (:code-snippet* snippets)})]
           (:code-snippet snippets)]]])]]))
