(ns kushi.test
  (:require
   [clojure.pprint :refer [pprint]]
   [clojure.test :as test :refer [is deftest]]
   [kushi.core :refer (sx-dispatch defclass-dispatch defkeyframes)]
   [kushi.state2 :as state2]
   [kushi.typography :refer (add-font-face*)]))


;; ;; keyframes ---------------------------------------------------------

;; (deftest keyframes
;;   (is (= (do (defkeyframes y-axis-spinner
;;                [:0% {:transform "rotateY(0deg)"}]
;;                [:100% {:transform "rotateY(360deg)"}])
;;              (select-keys  @state2/user-defined-keyframes [:y-axis-spinner]))
;;          {:y-axis-spinner [["0%" {"transform" "rotateY(0deg)"}]
;;                            ["100%" {"transform" "rotateY(360deg)"}]]})))


;; ;; add-font-face ---------------------------------------------------------

;; (deftest font-face
;;   (is (= (add-font-face* {:font-family "FiraCodeBold"
;;                           :font-weight "Bold"
;;                           :font-style  "Normal"
;;                           :src         ["local (\"Fira Code Bold \")"]})
;;          {:css-rule                    "@font-face {\n  font-family: FiraCodeBold;\n  font-weight: Bold;\n  font-style: Normal;\n  src: local (\"Fira Code Bold \");\n}",
;;           :expound-str                 nil,
;;           :entries/bad                 nil,
;;           :entries/weird               nil,
;;           :entries/missing             nil,
;;           :entries/fatal               nil,
;;           :clojure.spec.alpha/problems nil,
;;           :cache-map                   {:caching?  false
;;                                         :cache-key 497721303
;;                                         :cached    nil}})))


;; ;; defclass --------------------------------------------------------------

;; (deftest
;;   defclass-basics
;;   (is
;;    (=
;;     (defclass-dispatch
;;       {:sym       'gold
;;        :args      '(:c--gold)

;;        :form-meta {:file   "filename.cljs"
;;                    :line   11
;;                    :column 11}})
;;     '{:args                  (gold :c--gold),
;;       :kushi/process         :kushi.core/defclass,
;;       :form-meta             {:file   "filename.cljs"
;;                               :line   11
;;                               :column 11},
;;       :defclass-style-tuples [["c" "gold"]],
;;       :entries/weird         nil,
;;       :selector              {:selector*     "gold"
;;                               :selector      ".gold"
;;                               :prefixed-name "gold"},
;;       :element-style-inj     [".gold{color:gold}"],
;;       :expound-str           "Success!\n",
;;       :garden-vecs           ([".gold" {"color" "gold"}]),
;;       :kushi/chunk           :kushi.core/defclass,
;;       :args/bad              nil,
;;       :attrs                 {:class   ["gold"]
;;                               :style   {}},
;;       :classlist             ["gold"],
;;       :data-sx               "filename.cljs:11:11",
;;       :css-vars              {}})))


;; (deftest
;;   defclass-merging
;;   ;; We need to create 'absolute defclass first to populate state2/shared-classes
;;   ;; with style tuples for 'absolute, so we can compose with it in 'abs-blue.
;;   (defclass-dispatch
;;     {:sym       'absolute
;;      :args      [{:position :absolute}]
;;      :form-meta {:file   "filename.cljs"
;;                  :line   11
;;                  :column 11}})
;;   (is
;;    (=
;;     (defclass-dispatch
;;       {:sym       'abs-blue
;;        :args      [:.absolute :c--blue]
;;        :form-meta {:file   "filename.cljs"
;;                    :line   11
;;                    :column 11}})
;;     '{:args                  (abs-blue :.absolute :c--blue),
;;       :kushi/process         :kushi.core/defclass,
;;       :form-meta             {:file   "filename.cljs"
;;                               :line   11
;;                               :column 11},
;;       :defclass-style-tuples [[:position "absolute"] ["c" "blue"]],
;;       :entries/weird         nil,
;;       :selector              {:selector*     "abs-blue",
;;                               :selector      ".abs-blue",
;;                               :prefixed-name "abs-blue"},
;;       :element-style-inj     [".abs-blue{position:absolute;color:blue}"],
;;       :expound-str           "Success!\n",
;;       :garden-vecs           ([".abs-blue" {"position" "absolute"
;;                                             "color"    "blue"}]),
;;       :kushi/chunk           :kushi.core/defclass,
;;       :args/bad              nil,
;;       :attrs                 {:class ["abs-blue"]
;;                               :style {}},
;;       :classlist             ["abs-blue"],
;;       :data-sx               "filename.cljs:11:11",
;;       :css-vars              {}})))



;; ;; sx tests --------------------------------------------------------------

;; (deftest
;;    dynamic-values
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args      [[:c 'my-color1]],
;;        :form-meta {:file   "filename.cljs"
;;                    :line   11
;;                    :column 11}})
;;      '{:args              [[:c my-color1]],
;;        :kushi/process     :kushi.core/sx,
;;        :form-meta         {:file   "filename.cljs"
;;                            :line   11
;;                            :column 11},
;;        :entries/weird     nil,
;;        :selector          {:selector*     "_1267012933",
;;                            :selector      "._1267012933",
;;                            :prefixed-name nil},
;;        :element-style-inj ["._1267012933{color:var(--my-color1)}"],
;;        :expound-str       "Success!\n",
;;        :garden-vecs       (["._1267012933" {"color" "var(--my-color1)"}]),
;;        :kushi/chunk       :kushi.core/sx,
;;        :args/bad          nil,
;;        :attrs             {:class   ["_1267012933"],
;;                            :style   {"--my-color1" my-color1},
;;                            :data-sx "filename.cljs:11:11"},
;;        :classlist         ["_1267012933"],
;;        :data-sx           "filename.cljs:11:11",
;;        :css-vars          {"--my-color1" my-color1}})))

;; (deftest
;;    css-fn
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args      [{:style {:color '(rgba 0 0 10 0.5)}}],
;;        :form-meta {:file   "filename.cljs"
;;                    :line   11
;;                    :column 11}})
;;      '{:args              [{:style {:color (rgba 0 0 10 0.5)}}],
;;        :kushi/process     :kushi.core/sx,
;;        :form-meta         {:file   "filename.cljs"
;;                            :line   11
;;                            :column 11},
;;        :entries/weird     nil,
;;        :selector          {:selector*     "_-2036144280",
;;                            :selector      "._-2036144280",
;;                            :prefixed-name nil},
;;        :element-style-inj ["._-2036144280{color:rgba(0,0,10,0.5)}"],
;;        :expound-str       "Success!\n",
;;        :garden-vecs       (["._-2036144280" {"color" "rgba(0, 0, 10, 0.5)"}]),
;;        :kushi/chunk       :kushi.core/sx,
;;        :args/bad          nil,
;;        :attrs             {:class   ["_-2036144280"]
;;                            :style   {}
;;                            :data-sx "filename.cljs:11:11"},
;;        :classlist         ["_-2036144280"],
;;        :data-sx           "filename.cljs:11:11",
;;        :css-vars          {}})))

;; (deftest
;;    assigned-class
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args      ['myclassname :c--black],
;;        :form-meta {:file   "filename.cljs"
;;                    :line   11
;;                    :column 11}})
;;      {:args              '[myclassname :c--black],
;;       :kushi/process     :kushi.core/sx,
;;       :form-meta         {:file   "filename.cljs"
;;                           :line   11
;;                           :column 11},
;;       :entries/weird     nil,
;;       :selector          {:selector*     "myclassname",
;;                           :selector      ".myclassname",
;;                           :prefixed-name "myclassname"},
;;       :element-style-inj [".myclassname{color:black}"],
;;       :expound-str       "Success!\n",
;;       :garden-vecs       '([".myclassname" {"color" "black"}]),
;;       :kushi/chunk       :kushi.core/sx,
;;       :args/bad          nil,
;;       :attrs             {:class   ["myclassname"]
;;                           :style   {}
;;                           :data-sx "filename.cljs:11:11"},
;;       :classlist         ["myclassname"],
;;       :data-sx           "filename.cljs:11:11",
;;       :css-vars          {}})))



;; ;; sx tests below generated from output of kushi.gen/*-tests-gentest-args

;; (deftest
;;   basics
;;   (is
;;    (=
;;     (sx-dispatch
;;      {:args      [:c--red],
;;       :form-meta {:file   "filename.cljs"
;;                   :line   11
;;                   :column 11}})
;;     {:args              [:c--red],
;;      :kushi/process     :kushi.core/sx,
;;      :form-meta         {:file   "filename.cljs"
;;                          :line   11
;;                          :column 11},
;;      :entries/weird     nil,
;;      :selector          {:selector*     "_1473062533",
;;                          :selector      "._1473062533",
;;                          :prefixed-name nil},
;;      :element-style-inj ["._1473062533{color:red}"],
;;      :expound-str       "Success!\n",
;;      :garden-vecs       [["._1473062533" {"color" "red"}]],
;;      :kushi/chunk       :kushi.core/sx,
;;      :args/bad          nil,
;;      :attrs             {:class   ["_1473062533"],
;;                          :style   {},
;;                          :data-sx "filename.cljs:11:11"},
;;      :classlist         ["_1473062533"],
;;      :data-sx           "filename.cljs:11:11",
;;      :css-vars          {}}))
;;   (is
;;    (=
;;     (sx-dispatch
;;      {:args      [:c--blue],
;;       :form-meta {:file   "filename.cljs"
;;                   :line   11
;;                   :column 11}})
;;     {:args              [:c--blue],
;;      :kushi/process     :kushi.core/sx,
;;      :form-meta         {:file   "filename.cljs"
;;                          :line   11
;;                          :column 11},
;;      :entries/weird     nil,
;;      :selector          {:selector*     "_83004637",
;;                          :selector      "._83004637",
;;                          :prefixed-name nil},
;;      :element-style-inj ["._83004637{color:blue}"],
;;      :expound-str       "Success!\n",
;;      :garden-vecs       [["._83004637" {"color" "blue"}]],
;;      :kushi/chunk       :kushi.core/sx,
;;      :args/bad          nil,
;;      :attrs             {:class   ["_83004637"]
;;                          :style   {}
;;                          :data-sx "filename.cljs:11:11"},
;;      :classlist         ["_83004637"],
;;      :data-sx           "filename.cljs:11:11",
;;      :css-vars          {}}))
;;   (is
;;    (=
;;     (sx-dispatch
;;      {:args      [[:c :red]],
;;       :form-meta {:file   "filename.cljs"
;;                   :line   11
;;                   :column 11}})
;;     {:args              [[:c :red]],
;;      :kushi/process     :kushi.core/sx,
;;      :form-meta         {:file   "filename.cljs"
;;                          :line   11
;;                          :column 11},
;;      :entries/weird     nil,
;;      :selector          {:selector*     "_1874471052",
;;                          :selector      "._1874471052",
;;                          :prefixed-name nil},
;;      :element-style-inj ["._1874471052{color:red}"],
;;      :expound-str       "Success!\n",
;;      :garden-vecs       [["._1874471052" {"color" "red"}]],
;;      :kushi/chunk       :kushi.core/sx,
;;      :args/bad          nil,
;;      :attrs             {:class   ["_1874471052"],
;;                          :style   {},
;;                          :data-sx "filename.cljs:11:11"},
;;      :classlist         ["_1874471052"],
;;      :data-sx           "filename.cljs:11:11",
;;      :css-vars          {}}))
;;   (is
;;    (=
;;     (sx-dispatch
;;      {:args      [[:color :red]],
;;       :form-meta {:file   "filename.cljs"
;;                   :line   11
;;                   :column 11}})
;;     {:args              [[:color :red]],
;;      :kushi/process     :kushi.core/sx,
;;      :form-meta         {:file   "filename.cljs"
;;                          :line   11
;;                          :column 11},
;;      :entries/weird     nil,
;;      :selector          {:selector*     "_767466751",
;;                          :selector      "._767466751",
;;                          :prefixed-name nil},
;;      :element-style-inj ["._767466751{color:red}"],
;;      :expound-str       "Success!\n",
;;      :garden-vecs       [["._767466751" {"color" "red"}]],
;;      :kushi/chunk       :kushi.core/sx,
;;      :args/bad          nil,
;;      :attrs             {:class   ["_767466751"],
;;                          :style   {},
;;                          :data-sx "filename.cljs:11:11"},
;;      :classlist         ["_767466751"],
;;      :data-sx           "filename.cljs:11:11",
;;      :css-vars          {}}))
;;   (is
;;    (=
;;     (sx-dispatch
;;      {:args      [["color" "red"]],
;;       :form-meta {:file   "filename.cljs"
;;                   :line   11
;;                   :column 11}})
;;     {:args              [["color" "red"]],
;;      :kushi/process     :kushi.core/sx,
;;      :form-meta         {:file   "filename.cljs"
;;                          :line   11
;;                          :column 11},
;;      :entries/weird     nil,
;;      :selector          {:selector*     "_2053943513",
;;                          :selector      "._2053943513",
;;                          :prefixed-name nil},
;;      :element-style-inj ["._2053943513{color:red}"],
;;      :expound-str       "Success!\n",
;;      :garden-vecs       [["._2053943513" {"color" "red"}]],
;;      :kushi/chunk       :kushi.core/sx,
;;      :args/bad          nil,
;;      :attrs             {:class   ["_2053943513"],
;;                          :style   {},
;;                          :data-sx "filename.cljs:11:11"},
;;      :classlist         ["_2053943513"],
;;      :data-sx           "filename.cljs:11:11",
;;      :css-vars          {}}))
;;   (is
;;    (=
;;     (sx-dispatch
;;      {:args      [[:color "red"]],
;;       :form-meta {:file   "filename.cljs"
;;                   :line   11
;;                   :column 11}})
;;     {:args              [[:color "red"]],
;;      :kushi/process     :kushi.core/sx,
;;      :form-meta         {:file   "filename.cljs"
;;                          :line   11
;;                          :column 11},
;;      :entries/weird     nil,
;;      :selector          {:selector*     "_-1939415975",
;;                          :selector      "._-1939415975",
;;                          :prefixed-name nil},
;;      :element-style-inj ["._-1939415975{color:red}"],
;;      :expound-str       "Success!\n",
;;      :garden-vecs       [["._-1939415975" {"color" "red"}]],
;;      :kushi/chunk       :kushi.core/sx,
;;      :args/bad          nil,
;;      :attrs             {:class   ["_-1939415975"],
;;                          :style   {},
;;                          :data-sx "filename.cljs:11:11"},
;;      :classlist         ["_-1939415975"],
;;      :data-sx           "filename.cljs:11:11",
;;      :css-vars          {}}))
;;   (is
;;    (=
;;     (sx-dispatch
;;      {:args      [["color" :red]],
;;       :form-meta {:file   "filename.cljs"
;;                   :line   11
;;                   :column 11}})
;;     {:args              [["color" :red]],
;;      :kushi/process     :kushi.core/sx,
;;      :form-meta         {:file   "filename.cljs"
;;                          :line   11
;;                          :column 11},
;;      :entries/weird     nil,
;;      :selector          {:selector*     "_-890706300",
;;                          :selector      "._-890706300",
;;                          :prefixed-name nil},
;;      :element-style-inj ["._-890706300{color:red}"],
;;      :expound-str       "Success!\n",
;;      :garden-vecs       [["._-890706300" {"color" "red"}]],
;;      :kushi/chunk       :kushi.core/sx,
;;      :args/bad          nil,
;;      :attrs             {:class   ["_-890706300"],
;;                          :style   {},
;;                          :data-sx "filename.cljs:11:11"},
;;      :classlist         ["_-890706300"],
;;      :data-sx           "filename.cljs:11:11",
;;      :css-vars          {}}))
;;   (is
;;    (=
;;     (sx-dispatch
;;      {:args      [{:style {:color :red}}],
;;       :form-meta {:file   "filename.cljs"
;;                   :line   11
;;                   :column 11}})
;;     {:args              [{:style {:color :red}}],
;;      :kushi/process     :kushi.core/sx,
;;      :form-meta         {:file   "filename.cljs"
;;                          :line   11
;;                          :column 11},
;;      :entries/weird     nil,
;;      :selector          {:selector*     "_654018753",
;;                          :selector      "._654018753",
;;                          :prefixed-name nil},
;;      :element-style-inj ["._654018753{color:red}"],
;;      :expound-str       "Success!\n",
;;      :garden-vecs       [["._654018753" {"color" "red"}]],
;;      :kushi/chunk       :kushi.core/sx,
;;      :args/bad          nil,
;;      :attrs             {:class   ["_654018753"],
;;                          :style   {},
;;                          :data-sx "filename.cljs:11:11"},
;;      :classlist         ["_654018753"],
;;      :data-sx           "filename.cljs:11:11",
;;      :css-vars          {}}))
;;   (is
;;    (=
;;     (sx-dispatch
;;      {:args      [{:style {:c :red}}],
;;       :form-meta {:file   "filename.cljs"
;;                   :line   11
;;                   :column 11}})
;;     {:args              [{:style {:c :red}}],
;;      :kushi/process     :kushi.core/sx,
;;      :form-meta         {:file   "filename.cljs"
;;                          :line   11
;;                          :column 11},
;;      :entries/weird     nil,
;;      :selector          {:selector*     "_-1130007916",
;;                          :selector      "._-1130007916",
;;                          :prefixed-name nil},
;;      :element-style-inj ["._-1130007916{color:red}"],
;;      :expound-str       "Success!\n",
;;      :garden-vecs       [["._-1130007916" {"color" "red"}]],
;;      :kushi/chunk       :kushi.core/sx,
;;      :args/bad          nil,
;;      :attrs             {:class   ["_-1130007916"],
;;                          :style   {},
;;                          :data-sx "filename.cljs:11:11"},
;;      :classlist         ["_-1130007916"],
;;      :data-sx           "filename.cljs:11:11",
;;      :css-vars          {}})))
;;  (deftest
;;    shorthand
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [:ta--c],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [:ta--c],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_892413198",
;;        :selector "._892413198",
;;        :prefixed-name nil},
;;       :element-style-inj ["._892413198{text-align:center}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs [["._892413198" {"text-align" "center"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_892413198"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_892413198"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}}))
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [:ta--center],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [:ta--center],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_127849206",
;;        :selector "._127849206",
;;        :prefixed-name nil},
;;       :element-style-inj ["._127849206{text-align:center}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs [["._127849206" {"text-align" "center"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_127849206"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_127849206"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}}))
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [[:ta :c]],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [[:ta :c]],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_901446160",
;;        :selector "._901446160",
;;        :prefixed-name nil},
;;       :element-style-inj ["._901446160{text-align:center}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs [["._901446160" {"text-align" "center"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_901446160"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_901446160"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}}))
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [[:ta :center]],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [[:ta :center]],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_-1166434267",
;;        :selector "._-1166434267",
;;        :prefixed-name nil},
;;       :element-style-inj ["._-1166434267{text-align:center}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs [["._-1166434267" {"text-align" "center"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_-1166434267"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_-1166434267"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}}))
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [{:style {:ta :c}}],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [{:style {:ta :c}}],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_-690598504",
;;        :selector "._-690598504",
;;        :prefixed-name nil},
;;       :element-style-inj ["._-690598504{text-align:center}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs [["._-690598504" {"text-align" "center"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_-690598504"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_-690598504"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}}))
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [{:style {:ta :center}}],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [{:style {:ta :center}}],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_-101515769",
;;        :selector "._-101515769",
;;        :prefixed-name nil},
;;       :element-style-inj ["._-101515769{text-align:center}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs [["._-101515769" {"text-align" "center"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_-101515769"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_-101515769"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}}))
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [{:style {"text-align" "center"}}],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [{:style {"text-align" "center"}}],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_726739487",
;;        :selector "._726739487",
;;        :prefixed-name nil},
;;       :element-style-inj ["._726739487{text-align:center}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs [["._726739487" {"text-align" "center"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_726739487"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_726739487"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}})))
;;  (deftest
;;    css-shorthand
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [:b--1px:solid:red],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [:b--1px:solid:red],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_-731860167",
;;        :selector "._-731860167",
;;        :prefixed-name nil},
;;       :element-style-inj ["._-731860167{border:1px solid red}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs [["._-731860167" {"border" "1px solid red"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_-731860167"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_-731860167"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}}))
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [[:b :1px:solid:red]],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [[:b :1px:solid:red]],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_624889559",
;;        :selector "._624889559",
;;        :prefixed-name nil},
;;       :element-style-inj ["._624889559{border:1px solid red}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs [["._624889559" {"border" "1px solid red"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_624889559"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_624889559"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}}))
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [{:style {:b :1px:solid:red}}],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [{:style {:b :1px:solid:red}}],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_-1324997852",
;;        :selector "._-1324997852",
;;        :prefixed-name nil},
;;       :element-style-inj ["._-1324997852{border:1px solid red}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs [["._-1324997852" {"border" "1px solid red"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_-1324997852"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_-1324997852"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}})))
;;  (deftest
;;    css-alternation-lists
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [:ff--FiraCodeRegular|Consolas|monospace],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [:ff--FiraCodeRegular|Consolas|monospace],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_-1699147308",
;;        :selector "._-1699147308",
;;        :prefixed-name nil},
;;       :element-style-inj
;;       ["._-1699147308{font-family:FiraCodeRegular,Consolas,monospace}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs
;;       [["._-1699147308"
;;         {"font-family" "FiraCodeRegular, Consolas, monospace"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_-1699147308"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_-1699147308"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}}))
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [:text-shadow--5px:5px:10px:red|-5px:-5px:10px:blue],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [:text-shadow--5px:5px:10px:red|-5px:-5px:10px:blue],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_-2128695325",
;;        :selector "._-2128695325",
;;        :prefixed-name nil},
;;       :element-style-inj
;;       ["._-2128695325{text-shadow:5px 5px 10px red,-5px -5px 10px blue}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs
;;       [["._-2128695325"
;;         {"text-shadow" "5px 5px 10px red, -5px -5px 10px blue"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_-2128695325"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_-2128695325"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}}))
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [{:style {:b :1px:solid:red}}],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [{:style {:b :1px:solid:red}}],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_-1324997852",
;;        :selector "._-1324997852",
;;        :prefixed-name nil},
;;       :element-style-inj ["._-1324997852{border:1px solid red}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs [["._-1324997852" {"border" "1px solid red"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_-1324997852"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_-1324997852"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}})))
;;  (deftest
;;    css-custom-properties
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [:b--$custom-prop],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [:b--$custom-prop],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_1460598836",
;;        :selector "._1460598836",
;;        :prefixed-name nil},
;;       :element-style-inj ["._1460598836{border:var(--custom-prop)}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs [["._1460598836" {"border" "var(--custom-prop)"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_1460598836"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_1460598836"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}}))
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [[:b :$custom-prop]],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [[:b :$custom-prop]],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_-1306391301",
;;        :selector "._-1306391301",
;;        :prefixed-name nil},
;;       :element-style-inj ["._-1306391301{border:var(--custom-prop)}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs [["._-1306391301" {"border" "var(--custom-prop)"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_-1306391301"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_-1306391301"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}}))
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [{:style {:b :$custom-prop}}],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [{:style {:b :$custom-prop}}],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_-1118811010",
;;        :selector "._-1118811010",
;;        :prefixed-name nil},
;;       :element-style-inj ["._-1118811010{border:var(--custom-prop)}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs [["._-1118811010" {"border" "var(--custom-prop)"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_-1118811010"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_-1118811010"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}}))
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [:$custom-prop-name--red],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [:$custom-prop-name--red],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_1343009138",
;;        :selector "._1343009138",
;;        :prefixed-name nil},
;;       :element-style-inj ["._1343009138{--custom-prop-name:red}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs [["._1343009138" {"--custom-prop-name" "red"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_1343009138"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_1343009138"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}}))
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [:$custom-prop-name--$custom-prop-val],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [:$custom-prop-name--$custom-prop-val],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_725942373",
;;        :selector "._725942373",
;;        :prefixed-name nil},
;;       :element-style-inj
;;       ["._725942373{--custom-prop-name:var(--custom-prop-val)}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs
;;       [["._725942373" {"--custom-prop-name" "var(--custom-prop-val)"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_725942373"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_725942373"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}}))
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [[:$custom-prop-name :$custom-prop-val]],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [[:$custom-prop-name :$custom-prop-val]],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_315080682",
;;        :selector "._315080682",
;;        :prefixed-name nil},
;;       :element-style-inj
;;       ["._315080682{--custom-prop-name:var(--custom-prop-val)}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs
;;       [["._315080682" {"--custom-prop-name" "var(--custom-prop-val)"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_315080682"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_315080682"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}}))
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [{:style {:$custom-prop-name :$custom-prop}}],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [{:style {:$custom-prop-name :$custom-prop}}],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_-1280988579",
;;        :selector "._-1280988579",
;;        :prefixed-name nil},
;;       :element-style-inj
;;       ["._-1280988579{--custom-prop-name:var(--custom-prop)}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs
;;       [["._-1280988579" {"--custom-prop-name" "var(--custom-prop)"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_-1280988579"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_-1280988579"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}})))
;;  (deftest
;;    complex-values
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args
;;        [{:style
;;          {:before:content "\"*\"", :width "calc((100vw / 3) + 12px)"}}],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args
;;       [{:style
;;         {:before:content "\"*\"", :width "calc((100vw / 3) + 12px)"}}],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_372855738",
;;        :selector "._372855738",
;;        :prefixed-name nil},
;;       :element-style-inj
;;       ["._372855738::before{content:\"*\"}"
;;        "._372855738{width:calc((100vw / 3)+12px)}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs
;;       [["._372855738::before" {"content" "\"*\""}]
;;        ["._372855738" {"width" "calc((100vw / 3) + 12px)"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_372855738"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_372855738"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}})))
;;  (deftest
;;    media-queries
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [:c--black :md:c--orange :lg:c--blue :xl:c--pink],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [:c--black :md:c--orange :lg:c--blue :xl:c--pink],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_-2111748893",
;;        :selector "._-2111748893",
;;        :prefixed-name nil},
;;       :element-style-inj
;;       ["._-2111748893{color:black}"
;;        "@media(min-width:768px){._-2111748893{color:orange}}"
;;        "@media(min-width:1024px){._-2111748893{color:blue}}"
;;        "@media(min-width:1280px){._-2111748893{color:pink}}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs
;;       [["._-2111748893" {"color" "black"}]
;;        #garden.types.CSSAtRule{:identifier :media,
;;                                :value
;;                                {:media-queries {:min-width :768px},
;;                                 :rules
;;                                 (["._-2111748893" {"color" "orange"}])}}
;;        #garden.types.CSSAtRule{:identifier :media,
;;                                :value
;;                                {:media-queries {:min-width :1024px},
;;                                 :rules
;;                                 (["._-2111748893" {"color" "blue"}])}}
;;        #garden.types.CSSAtRule{:identifier :media,
;;                                :value
;;                                {:media-queries {:min-width :1280px},
;;                                 :rules
;;                                 (["._-2111748893" {"color" "pink"}])}}],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_-2111748893"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_-2111748893"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}}))
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args
;;        [{:style {:c :black, :md:c :orange, :lg:c :blue, :xl:c :pink}}],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args
;;       [{:style {:c :black, :md:c :orange, :lg:c :blue, :xl:c :pink}}],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_-2070379525",
;;        :selector "._-2070379525",
;;        :prefixed-name nil},
;;       :element-style-inj
;;       ["._-2070379525{color:black}"
;;        "@media(min-width:768px){._-2070379525{color:orange}}"
;;        "@media(min-width:1024px){._-2070379525{color:blue}}"
;;        "@media(min-width:1280px){._-2070379525{color:pink}}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs
;;       [["._-2070379525" {"color" "black"}]
;;        #garden.types.CSSAtRule{:identifier :media,
;;                                :value
;;                                {:media-queries {:min-width :768px},
;;                                 :rules
;;                                 (["._-2070379525" {"color" "orange"}])}}
;;        #garden.types.CSSAtRule{:identifier :media,
;;                                :value
;;                                {:media-queries {:min-width :1024px},
;;                                 :rules
;;                                 (["._-2070379525" {"color" "blue"}])}}
;;        #garden.types.CSSAtRule{:identifier :media,
;;                                :value
;;                                {:media-queries {:min-width :1280px},
;;                                 :rules
;;                                 (["._-2070379525" {"color" "pink"}])}}],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_-2070379525"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_-2070379525"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}}))
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [[:c :black] [:md:c :orange] [:lg:c :blue] [:xl:c :pink]],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [[:c :black] [:md:c :orange] [:lg:c :blue] [:xl:c :pink]],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_665915155",
;;        :selector "._665915155",
;;        :prefixed-name nil},
;;       :element-style-inj
;;       ["._665915155{color:black}"
;;        "@media(min-width:768px){._665915155{color:orange}}"
;;        "@media(min-width:1024px){._665915155{color:blue}}"
;;        "@media(min-width:1280px){._665915155{color:pink}}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs
;;       [["._665915155" {"color" "black"}]
;;        #garden.types.CSSAtRule{:identifier :media,
;;                                :value
;;                                {:media-queries {:min-width :768px},
;;                                 :rules
;;                                 (["._665915155" {"color" "orange"}])}}
;;        #garden.types.CSSAtRule{:identifier :media,
;;                                :value
;;                                {:media-queries {:min-width :1024px},
;;                                 :rules
;;                                 (["._665915155" {"color" "blue"}])}}
;;        #garden.types.CSSAtRule{:identifier :media,
;;                                :value
;;                                {:media-queries {:min-width :1280px},
;;                                 :rules
;;                                 (["._665915155" {"color" "pink"}])}}],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_665915155"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_665915155"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}})))
;;  (deftest
;;    with-classes
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args [:.absolute :c--black],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args [:.absolute :c--black],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_768969572",
;;        :selector "._768969572",
;;        :prefixed-name nil},
;;       :element-style-inj ["._768969572{color:black}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs [["._768969572" {"color" "black"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["absolute" "_768969572"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["absolute" "_768969572"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}})))
;;  (deftest
;;    with-pseudos
;;    (is
;;     (=
;;      (sx-dispatch
;;       {:args
;;        [:hover:c--blue
;;         :>a:hover:c--red
;;         :&_a:hover:c--gold
;;         :&.bar:hover:c--pink
;;         :before:fw--bold
;;         :before:mie--5px
;;         {:style
;;          {:before:content "\"⌫\"",
;;           "~a:hover:c" :blue,
;;           "nth-child(2):c" :red}}],
;;        :form-meta {:file "filename.cljs", :line 11, :column 11}})
;;      {:args
;;       [:hover:c--blue
;;        :>a:hover:c--red
;;        :&_a:hover:c--gold
;;        :&.bar:hover:c--pink
;;        :before:fw--bold
;;        :before:mie--5px
;;        {:style
;;         {:before:content "\"⌫\"",
;;          "~a:hover:c" :blue,
;;          "nth-child(2):c" :red}}],
;;       :kushi/process :kushi.core/sx,
;;       :form-meta {:file "filename.cljs", :line 11, :column 11},
;;       :entries/weird nil,
;;       :selector
;;       {:selector* "_266396804",
;;        :selector "._266396804",
;;        :prefixed-name nil},
;;       :element-style-inj
;;       ["._266396804:hover{color:blue}"
;;        "._266396804>a:hover{color:red}"
;;        "._266396804 a:hover{color:gold}"
;;        "._266396804.bar:hover{color:pink}"
;;        "._266396804::before{font-weight:bold;margin-inline-end:5px;content:\"⌫\"}"
;;        "._266396804~a:hover{color:blue}"
;;        "._266396804:nth-child(2){color:red}"],
;;       :expound-str "Success!\n",
;;       :garden-vecs
;;       [["._266396804:hover" {"color" "blue"}]
;;        ["._266396804>a:hover" {"color" "red"}]
;;        ["._266396804 a:hover" {"color" "gold"}]
;;        ["._266396804.bar:hover" {"color" "pink"}]
;;        ["._266396804::before"
;;         {"font-weight" "bold",
;;          "margin-inline-end" "5px",
;;          "content" "\"⌫\""}]
;;        ["._266396804~a:hover" {"color" "blue"}]
;;        ["._266396804:nth-child(2)" {"color" "red"}]],
;;       :kushi/chunk :kushi.core/sx,
;;       :args/bad nil,
;;       :attrs
;;       {:class ["_266396804"],
;;        :style {},
;;        :data-sx "filename.cljs:11:11"},
;;       :classlist ["_266396804"],
;;       :data-sx "filename.cljs:11:11",
;;       :css-vars {}})))


