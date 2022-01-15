(ns kushi.atomic
  (:require
   [kushi.selector :as selector]
   [kushi.utils :refer [auto-generated-hash]]))

(defonce defclass-hash (auto-generated-hash))

(defn flex-atomic [fd]
  (reduce
   (fn [acc [sh expanded]]
     (assoc acc
            (keyword (str "flex-"
                          (let [fdn* (-> fd name)
                                fdn (if (= fdn* "column") "col" "row")]
                            fdn)
                          "-"
                          (name sh)))
            {:flex-direction fd
             :justify-content expanded
             :display :flex}))
   {}
   {:c :center
    :fs :flex-start
    :fe :flex-end
    :sb :space-between
    :sa :space-around
    :se :space-evenly}))

(defonce declarative-classes*
   {;debug grids
    :debug-grid {:background "transparent url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAgAAAAICAYAAADED76LAAAAFElEQVR4AWPAC97/9x0eCsAEPgwAVLshdpENIxcAAAAASUVORK5CYII=) repeat top left"}
    :debug-grid-16 {:background "background:transparent url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAMklEQVR4AWOgCLz/b0epAa6UGuBOqQHOQHLUgFEDnAbcBZ4UGwDOkiCnkIhdgNgNxAYAiYlD+8sEuo8AAAAASUVORK5CYII=) repeat top left "}
    :debug-grid-8-solid {:background "background:white url(data:image/gif;base64,R0lGODdhCAAIAPEAAADw/wDx/////wAAACwAAAAACAAIAAACDZQvgaeb/lxbAIKA8y0AOw==) repeat top left"}
    :debug-grid-16-solid {:background "background:white url(data:image/gif;base64,R0lGODdhEAAQAPEAAADw/wDx/xXy/////ywAAAAAEAAQAAACIZyPKckYDQFsb6ZqD85jZ2+BkwiRFKehhqQCQgDHcgwEBQA7) repeat top left"}

    ;Typography
    :italic {:font-style :italic}
    :oblique {:font-style :oblique}
    :thin {:font-weight 100}
    :extra-light {:font-weight 200}
    :light {:font-weight 300}
    :regular {:font-weight 400}
    :medium {:font-weight 500}
    :semi-bold {:font-weight 600}
    :bold {:font-weight 700}
    :extra-bold {:font-weight 800}
    :heavy {:font-weight 900}

    ;Position
    :absolute {:position :absolute}
    :fixed {:position :fixed}
    :relative {:position :relative}

   ;Content
    :content-blank {:content "\"\""}

   ;Combonatorial background utilities
    :bgi-cover
    {:background-position "center center"
     :background-repeat :no-repeat
     :width "100%"}

    :bgi-contain
    {:background-position "center center"
     :background-size :contain
     :background-repeat :no-repeat
     :width "100%"
     :height "100%"}

   ;Combonatorial styling utilities
    :bordered
    {:border-color :silver, :border-style :solid, :border-width "1px"}

    :outlined
    {:outline-color :silver, :outline-style :solid, :outline-width "1px"}


   ;Combonatorial absolute positioning utilities
    :fixed-fill {:position :fixed, :top 0, :right 0, :bottom 0, :left 0}

    :absolute-fill
    {:position :absolute, :top 0, :right 0, :bottom 0, :left 0}

    :absolute-centered
    {:position :absolute
     :top "50%"
     :left "50%"
     :transform "translate(-50%, -50%)"}})

(defonce declarative-classes
  (merge
   declarative-classes*
   ;Combonatorial flexbox utilities
   (flex-atomic :row)
   (flex-atomic :column)))

(defonce declarative-classes-kushi-syntax
  (reduce
   (fn [acc [k v]] (assoc acc k (into [] v)))
   {}
   declarative-classes))


(def kushi-atomic-combo-classes
  (reduce
   (fn [acc [k v]]
     (let [garden-vecs (let [style-map (get declarative-classes k)
                             selector-name (selector/selector-name
                                            {:defclass-name k
                                             :defclass-hash defclass-hash})]
                         [[(:selector selector-name) style-map]])]
       (assoc acc k {:n k :args v :garden-vecs garden-vecs})))
   {}
   declarative-classes-kushi-syntax))
