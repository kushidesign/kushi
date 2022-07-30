(ns kushi.shorthand
 (:require [kushi.parstub :refer [? ?+]]
           [clojure.pprint :refer [pprint]]
           [clojure.string :as string]))

(def border-styles
  {:h :hidden
   :d :dotted
   :s :solid
   :g :groove
   :r :ridge
   :i :inset
   :o :outset})

(def logical-props
  '[border-block
    border-block-color
    border-block-end
    border-block-end-color
    border-block-end-style
    border-block-end-width
    border-block-start
    border-block-start-color
    border-block-start-style
    border-block-start-width
    border-block-width
    border-color
    border-inline
    border-inline-color
    border-inline-end
    border-inline-end-color
    border-inline-end-style
    border-inline-end-width
    border-inline-start
    border-inline-start-color
    border-inline-start-style
    border-inline-start-width
    border-inline-width
    border-start-start-radius
    border-start-end-radius
    border-end-start-radius
    border-end-end-radius
    border-style
    border-width
    margin-block
    margin-block-end
    margin-block-start
    margin-inline
    margin-inline-end
    margin-inline-start
    padding-block
    padding-block-end
    padding-block-start
    padding-inline
    padding-inline-end
    padding-inline-start])

(defn map-props [coll]
  (map (fn [sym]
         (let [parts  (-> sym name (string/split #"-"))
               style? (= (last parts) "style")
               sh     (->> parts (map first) string/join keyword)
               m      {:name (keyword sym)}]
           {sh (if style? (assoc m :vals border-styles) m)}))
       coll))

(def logicals
  (map-props logical-props))

(def unlogical-props
  '[border-top
    border-right
    border-left
    border-top-left-radius
    border-top-right-radius
    border-top-width
    border-right-width
    border-left-width
    border-top-color
    border-right-color
    border-left-color
    border-top-style
    border-right-style
    border-left-style
    padding-top
    padding-right
    padding-left
    margin-top
    margin-right
    margin-left])

(def unlogicals
  (map-props unlogical-props))

(def css-sh
  (merge
   (apply merge logicals)
   (apply merge unlogicals)
   {:ai {:name :align-items
         :vals {:c :center
                :fs :flex-start
                :fe :flex-end
                :n :normal
                :s :start
                :e :end
                :b :baseline}}

    :b {:name :border :example-val [[:1px :solid :orange]]}
    :bgi {:name :background-image :example-val "linear-gradient(red, blue)"}
    :bg  {:name :background :example-val [[:no-repeat "url('../bg-img.png')"]]}
    :bgs {:name :background-size :example-val :50%}
    :bgc {:name :background-color :example-val :red}
    :bgp {:name :background-position
          :vals {:t :top
                 :b :bottom
                 :l :left
                 :r :right
                 :c :center}}
    :bgr {:name :background-repeat
          :vals {:nr :no-repeat
                 :rx :repeat-x
                 :ry :repeat-y
                 :r :round
                 :s :space}}

    :c {:name :color :example-val :maroon}

    :d {:name :display
        :vals {:b :block
               :c :contents
               :f :flex
               :g :grid
               :i :inline
               :ib :inline-block
               :if :inline-flex
               :ig :inline-grid
               :it :inline-table
               :li :list-item
               :t :table
               :tc :table-cell
               :tcg :table-column-group
               :tfg :table-footer-group
               :thg :table-header-group
               :trg :table-row-group
               :tr :table-row}}

    :fs {:name :font-size :example-val :24px}
    :ff {:name :font-family :example-val :monospace}
    :fw {:name :font-weight :example-val 600}
    :fv {:name :font-variant :example-val [[:small-caps :slashed-zero]]}

    :g {:name :grid :example-val [["a" :100px "b" :1fr]]}
    :ga {:name :grid-area :example-val "a"}
    :gac {:name :grid-auto-columns :example-val "minmax(10px auto)"}
    :gaf {:name :grid-auto-flow :example-val [[:row :dense]]}
    :gar {:name :grid-auto-rows :example-val :min-content}
    :gc {:name :grid-column :example-val [[1 :/ :span 2]]}
    :gce {:name :grid-column-end :example-val 3}
    :gcs {:name :grid-column-start :example-val -1}
    :gr {:name :grid-row :example-val [[1 :/ 3]]}
    :gre {:name :grid-row-end :example-val 3}
    :grs {:name :grid-row-start :example-val :auto}
    :gt {:name :grid-template :example-val ["a b b" :20% "a c d" :auto]}
    :gta {:name :grid-template-areas :example-val ["a b b" "a c d"]}
    :gtr {:name :grid-template-rows :example-val [[:1fr :2fr :1fr]]}
    :gtc {:name :grid-template-columns :example-val [[:1fr :2fr :1fr]]}

    :h {:name :height :example-val "calc(100% - 20px)"}

    :i {:name :inset :example-val :8px:8px:10px:20px}
    :ib {:name :inset-block :example-val :8px}
    :ibs {:name :inset-block-start :example-val :8px}
    :ibe {:name :inset-block-end :example-val :8px}
    :ii {:name :inset-inline :example-val :8px}
    :iis {:name :inset-inline-start :example-val :8px}
    :iie {:name :inset-inline-end :example-val :8px}

    :jc {:name :justify-content
         :vals {:c :center
                :s :start
                :e :end
                :fs :flex-start
                :fe :flex-end
                :l :left
                :r :right
                :n :normal
                :sb :space-between
                :sa :space-around
                :se :space-evenly}}

    :ji {:name :justify-items
         :vals {:a :auto
                :n :normal
                :c :center
                :s :start
                :e :end
                :fs :flex-start
                :fe :flex-end
                :ss :self-start
                :se :self-end
                :l :left
                :r :right}}

    :lh {:name :line-height :example-val 1.45}

    :m {:name :margin :example-val [[0 :5px]]}
    :p {:name :padding  :example-val [[:10px 0]]}


    :o {:name :opacity :example-val 1}

    :ta {:name :text-align
         :vals {:c :center
                :r :right
                :l :left
                :j :justify
                :ja :justify-all
                :s :start
                :e :end
                :mp :match-parent}}
    :tt {:name :text-transform
         :vals {:u :uppercase
                :l :lowercase
                :c :captitalize
                :fw :full-width}}
    :td {:name :text-decoration
         :vals {:u :underline
                :o :overline
                :lt :line-through}}
    :tdl {:name :text-decoration-line
          :vals {:u :underline
                 :o :overline
                 :lt :line-through}}
    :tdc {:name :text-decoration-color :example-val :#333}
    :tds {:name :text-decoration-style
          :vals {:s :solid
                 :w :wavy}}
    :tdt {:name :text-decoration-thickness
          :vals {:ff :from-font}}
    :tuo {:name :text-underline-offset}
    :tup {:name :text-underline-position
          :vals {:ff :from-font
                 :l  :left
                 :r  :right
                 :u  :under}}

    :w {:name :width :example-val :80%}

    :ws {:name :white-space
         :vals {:n :nowrap
                :p :pre
                :pw :pre-wrap
                :pl :pre-line}}

    :v {:name :visibility
        :vals {:h :hidden
               :v :visibile
               :c :collapse}}

    :va {:name :vertical-align
         :vals {:b :baseline
                :s :sub
                :t :top
                :tt :text-top
                :tb :text-bottom
                :m :middle}}

    :zi {:name :z-index :example-val 10000}
    :z  {:name :z-index :example-val 10000}}))


(defonce css-sh-by-prop
  (reduce (fn [acc [k v]] (assoc acc (:name v) k)) {} css-sh))


(def css-sh-by-propname
  (reduce (fn [acc [_ v]] (if (not (nil? (:vals v)))
                            (assoc acc (:name v) (:vals v))
                            acc))
          {}
          css-sh))

(defn val-sh-atomic [v k repl]
  (swap! repl conj {:fn 'val-sh-atomic :v v :k k})
  (if (and (keyword? k) (keyword? v))
    (do
      (or (some-> css-sh k :vals v)
          (some-> css-sh-by-propname k v) v))
    v))

(defn enum-prop-shorty
  "Expects a hydrated css-prop keyword and keyword.
   Returns a hydrated css value or nil."
  [k v]
  (some-> css-sh-by-propname k v))

(defn val-sh [v k]
  (if (and (keyword? k) (keyword? v))
    (or
     (some-> css-sh k :vals v)
     (some-> css-sh-by-propname k v) v)
    v))


(defn key-sh [k]
  (if (keyword? k)
    (or (some-> css-sh k :name) k)
    k))

;; list of shorthands for docs
#_(doseq [x (sort (map (fn [[k v]]
                       (let [n (- 5 (-> k name count))]
                         (str k (string/join (repeat n " ")) "; " (:name v))))
                     css-sh))]
  (println x))

;; list of enum shorthands
#_(?+ (->> css-sh
         (map (fn [[k {prop-name :name prop-vals :vals}]]
                [(when prop-vals
                   (map (fn [[prop-val-sh prop-val]]
                          (let [tkw (str (name k) "--" (name prop-val-sh))
                                n (- 9 (count tkw))]
                            (str tkw (string/join (repeat n " ")) "; " (name prop-name) ": " (name prop-val))))
                        prop-vals))]))
         flatten
         sort
         (map keyword)
         (remove nil?)))

;; list of autocompletes
#_(?+ (->> css-sh
         (map (fn [[k {prop-name :name prop-vals :vals}]]
                [(if prop-vals
                   (map (fn [[_ prop-val]] (str (name prop-name) "--" (name prop-val))) prop-vals)
                   [(str (name prop-name) "--")])

                 (if prop-vals
                   (map (fn [[prop-val-sh _]] (str (name k) "--" (name prop-val-sh))) prop-vals)
                   [(str (name k) "--")])]))
         flatten
         sort
         (map keyword)
         ))

