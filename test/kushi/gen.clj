(ns kushi.gen
  (:require
   [clojure.test :as test :refer [is deftest]]
   [clojure.pprint :refer [pprint]]
   [kushi.core :refer (sx-dispatch defclass-dispatch)]))

;; Code below used to generated baseline tests

(def form-meta {:file "filename.cljs" :line 11 :column 11})

(defn gentest [f fsym args form-meta]
  (let [result (f {:args      args
                   :form-meta form-meta})
        result (if-let [gv (:garden-vecs result)]
                 (assoc result :garden-vecs (into [] gv))
                 result)]
    (list 'is
          (list '=
                (list fsym {:args      (into [] args)
                            :form-meta form-meta})
                result))))

(def sx-tests*

  [['basics
    nil
    [[:c--red]
     [:c--blue]
     [[:c :red]]
     [[:color :red]]
     [["color" "red"]]
     [[:color "red"]]
     [["color" :red]]
     [{:style {:color :red}}]
     [{:style {:c :red}}]]]

   ['shorthand
    nil
    [[:ta--c]
     [:ta--center]
     [[:ta :c]]
     [[:ta :center]]
     [{:style {:ta :c}}]
     [{:style {:ta :center}}]
     [{:style {"text-align" "center"}}]]]

   ['css-shorthand
    nil
    [[:b--1px:solid:red]
     [[:b :1px:solid:red]]
     [{:style {:b :1px:solid:red}}]]]

   ['css-alternation-lists
    nil
    [[:ff--FiraCodeRegular|Consolas|monospace]
     [:text-shadow--5px:5px:10px:red|-5px:-5px:10px:blue]
     [{:style {:b :1px:solid:red}}]]]

   ['css-custom-properties
    nil
    [[:b--:--my-css-custom-prop-for-border]
     [{:style {:b :--my-css-custom-prop-for-border}}]]]

   ['complex-values
    nil
    [[{:style {:before:content "\"*\""
               :width          "calc((100vw / 3) + 12px)"}}]]]

   ['media-queries
    nil
    [[:c--black :md:c--orange :lg:c--blue :xl:c--pink]
     [{:style {:c    :black
               :md:c :orange
               :lg:c :blue
               :xl:c :pink}}]
     [[:c    :black]
      [:md:c :orange]
      [:lg:c :blue]
      [:xl:c :pink]]]]

   ;; TODO test mq with different config

   ['with-classes
    nil
    [[:.absolute :c--black]
    ;;  [:.absolute :.foo :c--black]
     ]]

   ['with-pseudos
    nil
    [[:hover:c--blue
      :>a:hover:c--red
      :&_a:hover:c--gold
      :&.bar:hover:c--pink
      :before:fw--bold
      :before:mie--5px
      {:style {:before:content "\"⌫\""
               "~a:hover:c"      :blue
               "nth-child(2):c"  :red}}]]]
   ])


(def defclass-tests*
  [['basics
    nil
    [[{:sym  'gold
       :args '(:c--gold)}]]]
   ['defclass-merging
    nil
    [[{:sym  'bold
       :args '(:.absolute :c--blue)}]]]])


(defn *-tests-gentest-args [f fsym coll only-sym]
  (map (fn [[test-name bindings args-coll]]
         (concat ['deftest test-name]
                 (let [tests (map #(gentest f
                                            fsym
                                            %
                                            form-meta)
                                  args-coll)]
                   (if bindings
                     [(apply list (concat ['let] [bindings] tests))]
                     tests))))
       (if only-sym
         (filter #(= (first %) only-sym) coll)
         coll)))


(def sx-tests
  (*-tests-gentest-args
   sx-dispatch
   'sx-dispatch
   sx-tests*
   ;; with-classes
   nil
   ))


#_(def defclass-tests
  (*-tests-gentest-args
   defclass-dispatch
   'defclass-dispatch
   defclass-tests*
   ;; with-classes
   nil
   ))


#_(pprint (*-tests-gentest-args
         sx-dispatch
         'sx-dispatch
         sx-tests*
        ;;  'with-classes
         nil
         ))

;; (println (map #(apply gentest %) sx-tests-gentest-args))
;; (println sx-tests-gentest-args)
;; (println (gentest sx-dispatch 'sx-dispatch [:c--red] form-meta))


;; Uncomment these to run tests
;; (pprint sx-tests)
;; (deftest hey (is (= 1 1)))
