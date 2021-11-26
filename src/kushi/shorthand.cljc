(ns kushi.shorthand)

(def css-sh
  {:ai {:name :align-items
        :vals {:c :center
               :fs :flex-start
               :fe :flex-end
               :n :normal
               :s :start
               :e :end
               :b :baseline}}

   :b {:name :border :example-val [[:1px :solid :orange]]}
   :br {:name :border-right :example-val :2px}
   :bl {:name :border-left :example-val [[:1px :solid :black]]}
   :bt {:name :border-top :example-val [[:5% :dotted :#efefef]]}
   :bb {:name :border-bottom :example-val [[:5px :solid :white]]}
   :bs {:name :border-style
        :vals {:n :none
               :h :hidden
               :d :dotted
               :s :solid
               :g :groove
               :r :ridge
               :i :inset
               :o :outset}}
   :bw {:name :border-width :example-val :3px}
   :bc {:name :border-color :example-val :blue}

   :bgi {:name :background-image :example-val "linear-gradient(red, blue)"}
   :bg {:name :background :example-val [[:no-repeat "url('../bg-img.png')"]]}
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
   :mr {:name :margin-right :example-val :5%}
   :ml {:name :margin-left :example-val :10px}
   :mt {:name :margin-top :example-val :5px}
   :mb {:name :margin-bottom :example-val :8px}

   :p {:name :padding  :example-val [[:10px 0]]}
   :pr {:name :padding-right :example-val :5px}
   :pl {:name :padding-left :example-val :3px}
   :pt {:name :padding-top :example-val :1px}
   :pb {:name :padding-bottom :example-val :2px}

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
   :z  {:name :z-index :example-val 10000}
   })


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
