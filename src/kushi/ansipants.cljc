(ns kushi.ansipants
  #?(:clj (:require [io.aviso.ansi :as ansi]))
  (:require
   [clojure.pprint]
   [clojure.string :as string]))

(defmacro keyed [& ks]
  #?(:clj
     `(let [keys# (quote ~ks)
            keys# (map keyword keys#)
            vals# (list ~@ks)]
        (zipmap keys# vals#))))

(defn pprint+
  ([v]
   (pprint+ nil v))
  ([title v]
   #?(:cljs (do (if title
                  (println "\n" title " =>")
                  (println "\n\n"))
                (cljs.pprint/pprint v)
                (println "\n"))
      :clj (do (if title
                 (println "\n" title " =>")
                 (println "\n\n"))
               (clojure.pprint/pprint v)
               (println "\n")))))

(def ansi-color-map
  {:red          ansi/red
   :magenta      ansi/magenta
   :blue         ansi/blue
   :cyan         ansi/cyan
   :green        ansi/green
   :yellow       ansi/yellow
   :bold-red     ansi/bold-red
   :bold-magenta ansi/bold-magenta
   :bold-blue    ansi/bold-blue
   :bold-cyan    ansi/bold-cyan
   :bold-green   ansi/bold-green
   :bold-yellow  ansi/bold-yellow})

(defn shift-cycle [vc* i]
  (let [vc (into [] vc*)]
    (into [] (concat (subvec vc i) (subvec vc 0 i)))))

(def rainbow
  #?(:clj
     [ansi/red
      ansi/magenta
      ansi/blue
      ansi/cyan
      ansi/green
      ansi/yellow]))

(def rainbow-bright
  #?(:clj
     [ansi/bold-red
      ansi/bold-magenta
      ansi/bold-blue
      ansi/bold-cyan
      ansi/bold-green
      ansi/bold-yellow]))

(def warning-stripes-bright
  #?(:clj
     [ansi/bold-yellow
      ansi/bold-black]))

(def warning-stripes
  #?(:clj
     [ansi/yellow
      ansi/black]))

(def error-stripes
  #?(:clj
     [ansi/red-font
      ansi/white-font]))

(def error-stripes-bright
  #?(:clj
     [ansi/bold-red-font
      ansi/bold-white-font]))


(defn border-seq->styled-string
  [{:keys [border-seq border-width cyc top? bottom?] :as m}]
  #?(:clj (string/join
           (let [adjusted-border-width (Math/round (float (/ border-width (count border-seq))))]
             (if cyc
               (map #(% (apply str border-seq)) (take adjusted-border-width cyc))
               (apply str (repeat adjusted-border-width (apply str border-seq))))))))

(defn k->ansi [k]
  (when (keyword? k)
    (k ansi-color-map)))

(defn nl->sp [x] (if (or (= x :br) (= x "\n")) " " x))

(defn reduce-report-lines [lines*]
  (reduce
   (fn [acc v]
     (concat acc (if (coll? v) (map nl->sp v) [(nl->sp v)])))
   []
   (remove nil? lines*)))

(defn closest-number
  ([n m]
   (closest-number n m nil))
  ([n m k]
   #?(:clj (let [prev  (- n (rem n m))
                 next  (+ n (- m (rem n m)))
                 prevd (Math/abs (- n prev))
                 nextd (Math/abs (- n next))
                 ret   (cond
                         (= k :up)
                         next
                         (= k :down)
                         prev
                         :else (if (<= prevd nextd) prev next))]
             ret))))

(defn panel-border-top
  [{:keys [border-width
           header
           header-color
           header-width
           header-weight
           post-header-width
           theme
           border-seq
           border-tl-string
           indent]
    :as   m}]
  #?(:clj
     (apply str
            (concat
             ["\n\n"
              ((if theme (nth theme 0) str) border-tl-string)]
             (let [chars (take (dec indent) (cycle border-seq))]
               (if theme
                 (map-indexed (fn [idx char] ((nth theme idx) char)) chars)
                 (apply str chars)))
             (when header [" " ((if (= :bold header-weight) ansi/bold str) ((or header-color str) header)) " "])
             [(border-seq->styled-string
               (assoc m
                      :top? true
                      :border-width post-header-width
                      :cyc (when theme (take border-width (cycle theme)))))]))))

(defn panel-border-bottom
  [{:keys [color-cycle border-width border-bl-string theme header]
    :as   m}]
  #?(:clj
     (str
      "\n"
      ((last color-cycle) border-bl-string)
      (border-seq->styled-string
       (assoc m
              :bottom? true
              :border-width ((if header dec inc) border-width)
              :cyc (when theme (take border-width (cycle (reverse (shift-cycle color-cycle 1)))))))
      "\n\n")))

(defn panel
  [{:keys [header
           header-weight
           indent
           theme
           border-color
           header-color
           border-seq
           border-tl-string
           border-bl-string
           border-width
           border-weight
           border-v-char]
    :or   {indent       3
           border-width 50}}
   & lines*]
  (let [bold-border?       (= :bold border-weight)
        border-seq         (or border-seq (if bold-border? ["━" "━"] ["─" "─"]))
        border-tl-string   (or border-tl-string (if bold-border? "┏" "┌"))
        border-bl-string   (or border-bl-string (if bold-border? "┗" "└"))
        border-v-char      (or border-v-char (if bold-border? "┃" "│"))
        indent             (if (or (not (number? indent))
                                   (not (pos? indent)))
                             1
                             indent)
        header-width       (if header (+ (count header) 2 indent) 0)
        og-border-width    border-width
        post-header-width* (if header (- border-width header-width) border-width)
        post-header-width  (closest-number post-header-width* (count border-seq))
        post-hd-diff       (- post-header-width post-header-width*)
        border-width       (+ border-width post-hd-diff)
        border-color       (k->ansi border-color)
        header-color       (k->ansi header-color)
        header             (when header ((or header-color str) header))
        _                  nil #_(pprint+
                                  "wtf"
                                  (keyed og-border-width
                                         header-width
                                         header-width
                                         post-header-width*
                                         post-header-width
                                         post-hd-diff
                                         border-width))
        theme              (or theme
                               (when border-color (into [] (repeat 6 border-color))))
        brdr-opts          (keyed border-width
                                  header-width
                                  header-weight
                                  post-header-width
                                  header
                                  theme
                                  border-tl-string
                                  border-bl-string
                                  border-seq
                                  indent)
        report-lines       (concat
                            [(panel-border-top brdr-opts) " "]
                            (reduce-report-lines lines*))
        color-cycle        (take (* 2 (count report-lines))
                                 (cycle (if theme (shift-cycle theme 1) [str])))
        lines              (interleave
                            report-lines
                            (map #(% (str "\n" border-v-char (apply str (repeat indent " ")))) color-cycle))
        ;; _                  (? "lines" {:lines lines :report-lines-count (count lines) :report-lines-last (last lines)})
        bb-opts            (assoc brdr-opts :color-cycle color-cycle)]
    (string/join
     (concat lines
             [(panel-border-bottom bb-opts)]))))

(defn error-panel
  [& args*]
   (let [[arg1 & args]  args*
         [opts* lines*] (if (map? arg1) [arg1 args] [nil args*])
         opts           (merge {:header-weight :normal
                                :header        "ERROR"
                                :theme         error-stripes-bright
                                :border-width  48
                                :border-weight :normal
                                :indent        3}
                               opts*)
         panel-args     (into [opts] lines*)]
     (apply panel panel-args)))

(defn warning-panel
  [& args*]
   (let [[arg1 & args]  args*
         [opts* lines*] (if (map? arg1) [arg1 args] [nil args*])
         opts           (merge {:header-weight :normal
                                :header        "WARNING"
                                :theme         warning-stripes-bright
                                :border-width  48
                                :border-weight :normal
                                :indent        3}
                               opts*)
         panel-args     (into [opts] lines*)]
     (apply panel panel-args)))
