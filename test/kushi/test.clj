(ns kushi.test
  (:require
   [clojure.test :as test :refer [is deftest]]
   [clojure.pprint :refer [pprint]]
   [kushi.core :refer (sx-dispatch defclass-dispatch defkeyframes)]
   [kushi.state2 :as state2]
   [kushi.typography :refer (sysfont add-font-face*)]))


;; ;; keyframes ---------------------------------------------------------
(deftest keyframes
  (is (= (do (defkeyframes y-axis-spinner
               [:0% {:transform "rotateY(0deg)"}]
               [:100% {:transform "rotateY(360deg)"}])
             (select-keys  @state2/user-defined-keyframes [:y-axis-spinner]))
         {:y-axis-spinner [["0%" {"transform" "rotateY(0deg)"}]
                           ["100%" {"transform" "rotateY(360deg)"}]]})))


;; ;; add-font-face ---------------------------------------------------------

(deftest font-face
  (is (= (add-font-face* {:font-family "FiraCodeBold"
                          :font-weight "Bold"
                          :font-style  "Normal"
                          :src         ["local (\"Fira Code Bold \")"]})
         {:css-rule                    "@font-face {\n  font-family: FiraCodeBold;\n  font-weight: Bold;\n  font-style: Normal;\n  src: local (\"Fira Code Bold \");\n}",
          :expound-str                 nil,
          :entries/bad                 nil,
          :entries/weird               nil,
          :entries/missing             nil,
          :entries/fatal               nil,
          :clojure.spec.alpha/problems nil,
          :cache-map                   {:caching?  false
                                        :cache-key -971616958
                                        :cached    nil}})))



;; ;; add-font-face ---------------------------------------------------------

(deftest add-system-font-stack
  (is (= (sysfont [300 500])
         '{:m         {:args                        [300 500],
                       :kushi/process               :kushi.core/add-system-font-stack,
                       :form-meta                   nil,
                       :cached                      nil,
                       :cache-key                   1123987584,
                       :expound-str                 nil,
                       :caching?                    false,
                       :css-rule                    ["@font-face {\n  font-family: sys;\n  font-style: normal;\n  font-weight: 300;\n  src: local(\".SFNS-Light\"), local(\".SFNSText-Light\"), local(\".HelveticaNeueDeskInterface-Light\"), local(\".LucidaGrandeUI\"), local(\"Segoe UI Light\"), local(\"Ubuntu Light\"), local(\"Roboto-Light\"), local(\"DroidSans\"), local(\"Tahoma\");\n}"
                                                     "@font-face {\n  font-family: sys;\n  font-style: italic;\n  font-weight: 300;\n  src: local(\".SFNS-LightItalic\"), local(\".SFNSText-LightItalic\"), local(\".HelveticaNeueDeskInterface-Italic\"), local(\".LucidaGrandeUI\"), local(\"Segoe UI Light Italic\"), local(\"Ubuntu Light Italic\"), local(\"Roboto-LightItalic\"), local(\"DroidSans\"), local(\"Tahoma\");\n}"
                                                     "@font-face {\n  font-family: sys;\n  font-style: normal;\n  font-weight: 500;\n  src: local(\".SFNS-Medium\"), local(\".SFNSText-Medium\"), local(\".HelveticaNeueDeskInterface-MediumP4\"), local(\".LucidaGrandeUI\"), local(\"Segoe UI Semibold\"), local(\"Ubuntu Medium\"), local(\"Roboto-Medium\"), local(\"DroidSans-Bold\"), local(\"Tahoma Bold\");\n}"
                                                     "@font-face {\n  font-family: sys;\n  font-style: italic;\n  font-weight: 500;\n  src: local(\".SFNS-MediumItalic\"), local(\".SFNSText-MediumItalic\"), local(\".HelveticaNeueDeskInterface-MediumItalicP4\"), local(\".LucidaGrandeUI\"), local(\"Segoe UI Semibold Italic\"), local(\"Ubuntu Medium Italic\"), local(\"Roboto-MediumItalic\"), local(\"DroidSans-Bold\"), local(\"Tahoma Bold\");\n}"],
                       :clojure.spec.alpha/problems nil},
           :cache-map {:caching?  false
                       :cache-key 1123987584
                       :cached    nil}})))



;; ;; defclass --------------------------------------------------------------

(deftest
  defclass-basics
  (is
   (=
    (defclass-dispatch
      {:sym       'gold
       :args      '(:c--gold)

       :form-meta {:file   "filename.cljs"
                   :line   11
                   :column 11}})
    '{:args                  (gold :c--gold),
      :kushi/process         :kushi.core/defclass,
      :form-meta             {:file   "filename.cljs"
                              :line   11
                              :column 11},
      :defclass-style-tuples [["c" "gold"]],
      :entries/weird         nil,
      :selector              {:selector*     "gold"
                              :selector      ".gold"
                              :prefixed-name "gold"},
      :element-style-inj     [".gold{color:gold}"],
      :expound-str           "Success!\n",
      :garden-vecs           ([".gold" {"color" "gold"}]),
      :kushi/chunk           :kushi.core/defclass,
      :args/bad              nil,
      :attrs                 {:class   ["gold"]
                              :style   {}
                              :data-sx "filename.cljs:11:11"},
      :classlist             ["gold"],
      :data-sx               "filename.cljs:11:11",
      :css-vars              {}})))


(deftest
  defclass-merging
  ;; We need to create 'absolute defclass first to populate state2/shared-classes
  ;; with style tuples for 'absolute, so we can compose with it in 'abs-blue.
  (defclass-dispatch
    {:sym       'absolute
     :args      [{:position :absolute}]
     :form-meta {:file   "filename.cljs"
                 :line   11
                 :column 11}})
  (is
   (=
    (defclass-dispatch
      {:sym       'abs-blue
       :args      [:.absolute :c--blue]
       :form-meta {:file   "filename.cljs"
                   :line   11
                   :column 11}})
    '{:args                  (abs-blue :.absolute :c--blue),
      :kushi/process         :kushi.core/defclass,
      :form-meta             {:file   "filename.cljs"
                              :line   11
                              :column 11},
      :defclass-style-tuples [["c" "blue"] [:position "absolute"]],
      :entries/weird         nil,
      :selector              {:selector*     "abs-blue",
                              :selector      ".abs-blue",
                              :prefixed-name "abs-blue"},
      :element-style-inj     [".abs-blue{color:blue;position:absolute}"],
      :expound-str           "Success!\n",
      :garden-vecs           ([".abs-blue" {"color"    "blue"
                                            "position" "absolute"}]),
      :kushi/chunk           :kushi.core/defclass,
      :args/bad              nil,
      :attrs                 {:class   ["abs-blue"]
                              :style   {}
                              :data-sx "filename.cljs:11:11"},
      :classlist             ["abs-blue"],
      :data-sx               "filename.cljs:11:11",
      :css-vars              {}})))



;; sx tests --------------------------------------------------------------

(deftest
   dynamic-values
   (is
    (=
     (sx-dispatch
      {:args      [[:c 'my-color1]],
       :form-meta {:file   "filename.cljs"
                   :line   11
                   :column 11}})
     '{:args              [[:c my-color1]],
       :kushi/process     :kushi.core/sx,
       :form-meta         {:file   "filename.cljs"
                           :line   11
                           :column 11},
       :entries/weird     nil,
       :selector          {:selector*     "_-509241084",
                           :selector      "._-509241084",
                           :prefixed-name nil},
       :element-style-inj ["._-509241084{color:var(--my-color1)}"],
       :expound-str       "Success!\n",
       :garden-vecs       (["._-509241084" {"color" "var(--my-color1)"}]),
       :kushi/chunk       :kushi.core/sx,
       :args/bad          nil,
       :attrs             {:class   ["_-509241084"],
                           :style   {"--my-color1" my-color1},
                           :data-sx "filename.cljs:11:11"},
       :classlist         ["_-509241084"],
       :data-sx           "filename.cljs:11:11",
       :css-vars          {"--my-color1" my-color1}})))

(deftest
   css-fn
   (is
    (=
     (sx-dispatch
      {:args      [{:style {:color '(rgba 0 0 10 0.5)}}],
       :form-meta {:file   "filename.cljs"
                   :line   11
                   :column 11}})
     '{:args              [{:style {:color (rgba 0 0 10 0.5)}}],
       :kushi/process     :kushi.core/sx,
       :form-meta         {:file   "filename.cljs"
                           :line   11
                           :column 11},
       :entries/weird     nil,
       :selector          {:selector*     "_2117356411",
                           :selector      "._2117356411",
                           :prefixed-name nil},
       :element-style-inj ["._2117356411{color:rgba(0,0,10,0.5)}"],
       :expound-str       "Success!\n",
       :garden-vecs       (["._2117356411" {"color" "rgba(0, 0, 10, 0.5)"}]),
       :kushi/chunk       :kushi.core/sx,
       :args/bad          nil,
       :attrs             {:class   ["_2117356411"]
                           :style   {}
                           :data-sx "filename.cljs:11:11"},
       :classlist         ["_2117356411"],
       :data-sx           "filename.cljs:11:11",
       :css-vars          {}})))

(deftest
   assigned-class
   (is
    (=
     (sx-dispatch
      {:args      ['myclassname :c--black],
       :form-meta {:file   "filename.cljs"
                   :line   11
                   :column 11}})
     {:args              '[myclassname :c--black],
      :kushi/process     :kushi.core/sx,
      :form-meta         {:file   "filename.cljs"
                          :line   11
                          :column 11},
      :entries/weird     nil,
      :selector          {:selector*     "myclassname",
                          :selector      ".myclassname",
                          :prefixed-name "myclassname"},
      :element-style-inj [".myclassname{color:black}"],
      :expound-str       "Success!\n",
      :garden-vecs       '([".myclassname" {"color" "black"}]),
      :kushi/chunk       :kushi.core/sx,
      :args/bad          nil,
      :attrs             {:class   ["myclassname"]
                          :style   {}
                          :data-sx "filename.cljs:11:11"},
      :classlist         ["myclassname"],
      :data-sx           "filename.cljs:11:11",
      :css-vars          {}})))



;; sx tests below generated from output of kushi.gen/*-tests-gentest-args

(deftest
   basics
   (is
    (=
     (sx-dispatch
      {:args [:c--red],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [:c--red],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_864890704",
       :selector "._864890704",
       :prefixed-name nil},
      :element-style-inj ["._864890704{color:red}"],
      :expound-str "Success!\n",
      :garden-vecs [["._864890704" {"color" "red"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_864890704"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_864890704"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}}))
   (is
    (=
     (sx-dispatch
      {:args [:c--blue],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [:c--blue],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_1371769684",
       :selector "._1371769684",
       :prefixed-name nil},
      :element-style-inj ["._1371769684{color:blue}"],
      :expound-str "Success!\n",
      :garden-vecs [["._1371769684" {"color" "blue"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_1371769684"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_1371769684"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}}))
   (is
    (=
     (sx-dispatch
      {:args [[:c :red]],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [[:c :red]],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_1844657625",
       :selector "._1844657625",
       :prefixed-name nil},
      :element-style-inj ["._1844657625{color:red}"],
      :expound-str "Success!\n",
      :garden-vecs [["._1844657625" {"color" "red"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_1844657625"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_1844657625"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}}))
   (is
    (=
     (sx-dispatch
      {:args [[:color :red]],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [[:color :red]],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_-2048263565",
       :selector "._-2048263565",
       :prefixed-name nil},
      :element-style-inj ["._-2048263565{color:red}"],
      :expound-str "Success!\n",
      :garden-vecs [["._-2048263565" {"color" "red"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_-2048263565"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_-2048263565"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}}))
   (is
    (=
     (sx-dispatch
      {:args [["color" "red"]],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [["color" "red"]],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_-357155165",
       :selector "._-357155165",
       :prefixed-name nil},
      :element-style-inj ["._-357155165{color:red}"],
      :expound-str "Success!\n",
      :garden-vecs [["._-357155165" {"color" "red"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_-357155165"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_-357155165"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}}))
   (is
    (=
     (sx-dispatch
      {:args [[:color "red"]],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [[:color "red"]],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_190698281",
       :selector "._190698281",
       :prefixed-name nil},
      :element-style-inj ["._190698281{color:red}"],
      :expound-str "Success!\n",
      :garden-vecs [["._190698281" {"color" "red"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_190698281"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_190698281"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}}))
   (is
    (=
     (sx-dispatch
      {:args [["color" :red]],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [["color" :red]],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_886311108",
       :selector "._886311108",
       :prefixed-name nil},
      :element-style-inj ["._886311108{color:red}"],
      :expound-str "Success!\n",
      :garden-vecs [["._886311108" {"color" "red"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_886311108"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_886311108"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}}))
   (is
    (=
     (sx-dispatch
      {:args [{:style {:color :red}}],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [{:style {:color :red}}],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_2025187698",
       :selector "._2025187698",
       :prefixed-name nil},
      :element-style-inj ["._2025187698{color:red}"],
      :expound-str "Success!\n",
      :garden-vecs [["._2025187698" {"color" "red"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_2025187698"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_2025187698"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}}))
   (is
    (=
     (sx-dispatch
      {:args [{:style {:c :red}}],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [{:style {:c :red}}],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_-1957917367",
       :selector "._-1957917367",
       :prefixed-name nil},
      :element-style-inj ["._-1957917367{color:red}"],
      :expound-str "Success!\n",
      :garden-vecs [["._-1957917367" {"color" "red"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_-1957917367"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_-1957917367"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}})))
 (deftest
   shorthand
   (is
    (=
     (sx-dispatch
      {:args [:ta--c],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [:ta--c],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_-1806302294",
       :selector "._-1806302294",
       :prefixed-name nil},
      :element-style-inj ["._-1806302294{text-align:center}"],
      :expound-str "Success!\n",
      :garden-vecs [["._-1806302294" {"text-align" "center"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_-1806302294"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_-1806302294"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}}))
   (is
    (=
     (sx-dispatch
      {:args [:ta--center],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [:ta--center],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_-1608110993",
       :selector "._-1608110993",
       :prefixed-name nil},
      :element-style-inj ["._-1608110993{text-align:center}"],
      :expound-str "Success!\n",
      :garden-vecs [["._-1608110993" {"text-align" "center"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_-1608110993"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_-1608110993"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}}))
   (is
    (=
     (sx-dispatch
      {:args [[:ta :c]],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [[:ta :c]],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_-831099492",
       :selector "._-831099492",
       :prefixed-name nil},
      :element-style-inj ["._-831099492{text-align:center}"],
      :expound-str "Success!\n",
      :garden-vecs [["._-831099492" {"text-align" "center"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_-831099492"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_-831099492"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}}))
   (is
    (=
     (sx-dispatch
      {:args [[:ta :center]],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [[:ta :center]],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_-1408446678",
       :selector "._-1408446678",
       :prefixed-name nil},
      :element-style-inj ["._-1408446678{text-align:center}"],
      :expound-str "Success!\n",
      :garden-vecs [["._-1408446678" {"text-align" "center"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_-1408446678"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_-1408446678"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}}))
   (is
    (=
     (sx-dispatch
      {:args [{:style {:ta :c}}],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [{:style {:ta :c}}],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_-554254618",
       :selector "._-554254618",
       :prefixed-name nil},
      :element-style-inj ["._-554254618{text-align:center}"],
      :expound-str "Success!\n",
      :garden-vecs [["._-554254618" {"text-align" "center"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_-554254618"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_-554254618"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}}))
   (is
    (=
     (sx-dispatch
      {:args [{:style {:ta :center}}],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [{:style {:ta :center}}],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_-584898287",
       :selector "._-584898287",
       :prefixed-name nil},
      :element-style-inj ["._-584898287{text-align:center}"],
      :expound-str "Success!\n",
      :garden-vecs [["._-584898287" {"text-align" "center"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_-584898287"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_-584898287"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}}))
   (is
    (=
     (sx-dispatch
      {:args [{:style {"text-align" "center"}}],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [{:style {"text-align" "center"}}],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_-1663907178",
       :selector "._-1663907178",
       :prefixed-name nil},
      :element-style-inj ["._-1663907178{text-align:center}"],
      :expound-str "Success!\n",
      :garden-vecs [["._-1663907178" {"text-align" "center"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_-1663907178"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_-1663907178"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}})))
 (deftest
   css-shorthand
   (is
    (=
     (sx-dispatch
      {:args [:b--1px:solid:red],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [:b--1px:solid:red],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_-1668580909",
       :selector "._-1668580909",
       :prefixed-name nil},
      :element-style-inj ["._-1668580909{border:1px solid red}"],
      :expound-str "Success!\n",
      :garden-vecs [["._-1668580909" {"border" "1px solid red"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_-1668580909"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_-1668580909"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}}))
   (is
    (=
     (sx-dispatch
      {:args [[:b :1px:solid:red]],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [[:b :1px:solid:red]],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_-1398594190",
       :selector "._-1398594190",
       :prefixed-name nil},
      :element-style-inj ["._-1398594190{border:1px solid red}"],
      :expound-str "Success!\n",
      :garden-vecs [["._-1398594190" {"border" "1px solid red"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_-1398594190"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_-1398594190"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}}))
   (is
    (=
     (sx-dispatch
      {:args [{:style {:b :1px:solid:red}}],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [{:style {:b :1px:solid:red}}],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_658842594",
       :selector "._658842594",
       :prefixed-name nil},
      :element-style-inj ["._658842594{border:1px solid red}"],
      :expound-str "Success!\n",
      :garden-vecs [["._658842594" {"border" "1px solid red"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_658842594"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_658842594"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}})))
 (deftest
   css-alternation-lists
   (is
    (=
     (sx-dispatch
      {:args [:ff--FiraCodeRegular|Consolas|monospace],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [:ff--FiraCodeRegular|Consolas|monospace],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_665154283",
       :selector "._665154283",
       :prefixed-name nil},
      :element-style-inj
      ["._665154283{font-family:FiraCodeRegular,Consolas,monospace}"],
      :expound-str "Success!\n",
      :garden-vecs
      [["._665154283"
        {"font-family" "FiraCodeRegular, Consolas, monospace"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_665154283"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_665154283"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}}))
   (is
    (=
     (sx-dispatch
      {:args [:text-shadow--5px:5px:10px:red|-5px:-5px:10px:blue],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [:text-shadow--5px:5px:10px:red|-5px:-5px:10px:blue],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_612292916",
       :selector "._612292916",
       :prefixed-name nil},
      :element-style-inj
      ["._612292916{text-shadow:5px 5px 10px red,-5px -5px 10px blue}"],
      :expound-str "Success!\n",
      :garden-vecs
      [["._612292916"
        {"text-shadow" "5px 5px 10px red, -5px -5px 10px blue"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_612292916"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_612292916"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}}))
   (is
    (=
     (sx-dispatch
      {:args [{:style {:b :1px:solid:red}}],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [{:style {:b :1px:solid:red}}],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_658842594",
       :selector "._658842594",
       :prefixed-name nil},
      :element-style-inj ["._658842594{border:1px solid red}"],
      :expound-str "Success!\n",
      :garden-vecs [["._658842594" {"border" "1px solid red"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_658842594"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_658842594"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}})))
 (deftest
   css-custom-properties
   (is
    (=
     (sx-dispatch
      {:args [:b--:--my-css-custom-prop-for-border],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [:b--:--my-css-custom-prop-for-border],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_1753337593",
       :selector "._1753337593",
       :prefixed-name nil},
      :element-style-inj
      ["._1753337593{border:var(--my-css-custom-prop-for-border)}"],
      :expound-str "Success!\n",
      :garden-vecs
      [["._1753337593"
        {"border" "var(--my-css-custom-prop-for-border)"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_1753337593"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_1753337593"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}}))
   (is
    (=
     (sx-dispatch
      {:args [{:style {:b :--my-css-custom-prop-for-border}}],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [{:style {:b :--my-css-custom-prop-for-border}}],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_-73270017",
       :selector "._-73270017",
       :prefixed-name nil},
      :element-style-inj
      ["._-73270017{border:var(--my-css-custom-prop-for-border)}"],
      :expound-str "Success!\n",
      :garden-vecs
      [["._-73270017"
        {"border" "var(--my-css-custom-prop-for-border)"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_-73270017"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_-73270017"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}})))
 (deftest
   complex-values
   (is
    (=
     (sx-dispatch
      {:args
       [{:style
         {:before:content "\"*\"", :width "calc((100vw / 3) + 12px)"}}],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args
      [{:style
        {:before:content "\"*\"", :width "calc((100vw / 3) + 12px)"}}],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_-1275956296",
       :selector "._-1275956296",
       :prefixed-name nil},
      :element-style-inj
      ["._-1275956296::before{content:\"*\"}"
       "._-1275956296{width:calc((100vw / 3)+12px)}"],
      :expound-str "Success!\n",
      :garden-vecs
      [["._-1275956296::before" {"content" "\"*\""}]
       ["._-1275956296" {"width" "calc((100vw / 3) + 12px)"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_-1275956296"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_-1275956296"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}})))
 (deftest
   media-queries
   (is
    (=
     (sx-dispatch
      {:args [:c--black :md:c--orange :lg:c--blue :xl:c--pink],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [:c--black :md:c--orange :lg:c--blue :xl:c--pink],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_-1170053972",
       :selector "._-1170053972",
       :prefixed-name nil},
      :element-style-inj
      ["._-1170053972{color:black}"
       "@media(min-width:768px){._-1170053972{color:orange}}"
       "@media(min-width:1024px){._-1170053972{color:blue}}"
       "@media(min-width:1280px){._-1170053972{color:pink}}"],
      :expound-str "Success!\n",
      :garden-vecs
      [["._-1170053972" {"color" "black"}]
       #garden.types.CSSAtRule{:identifier :media,
                               :value
                               {:media-queries {:min-width :768px},
                                :rules
                                (["._-1170053972" {"color" "orange"}])}}
       #garden.types.CSSAtRule{:identifier :media,
                               :value
                               {:media-queries {:min-width :1024px},
                                :rules
                                (["._-1170053972" {"color" "blue"}])}}
       #garden.types.CSSAtRule{:identifier :media,
                               :value
                               {:media-queries {:min-width :1280px},
                                :rules
                                (["._-1170053972" {"color" "pink"}])}}],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_-1170053972"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_-1170053972"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}}))
   (is
    (=
     (sx-dispatch
      {:args
       [{:style {:c :black, :md:c :orange, :lg:c :blue, :xl:c :pink}}],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args
      [{:style {:c :black, :md:c :orange, :lg:c :blue, :xl:c :pink}}],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_622588759",
       :selector "._622588759",
       :prefixed-name nil},
      :element-style-inj
      ["._622588759{color:black}"
       "@media(min-width:768px){._622588759{color:orange}}"
       "@media(min-width:1024px){._622588759{color:blue}}"
       "@media(min-width:1280px){._622588759{color:pink}}"],
      :expound-str "Success!\n",
      :garden-vecs
      [["._622588759" {"color" "black"}]
       #garden.types.CSSAtRule{:identifier :media,
                               :value
                               {:media-queries {:min-width :768px},
                                :rules
                                (["._622588759" {"color" "orange"}])}}
       #garden.types.CSSAtRule{:identifier :media,
                               :value
                               {:media-queries {:min-width :1024px},
                                :rules
                                (["._622588759" {"color" "blue"}])}}
       #garden.types.CSSAtRule{:identifier :media,
                               :value
                               {:media-queries {:min-width :1280px},
                                :rules
                                (["._622588759" {"color" "pink"}])}}],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_622588759"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_622588759"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}}))
   (is
    (=
     (sx-dispatch
      {:args [[:c :black] [:md:c :orange] [:lg:c :blue] [:xl:c :pink]],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [[:c :black] [:md:c :orange] [:lg:c :blue] [:xl:c :pink]],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_-880421676",
       :selector "._-880421676",
       :prefixed-name nil},
      :element-style-inj
      ["._-880421676{color:black}"
       "@media(min-width:768px){._-880421676{color:orange}}"
       "@media(min-width:1024px){._-880421676{color:blue}}"
       "@media(min-width:1280px){._-880421676{color:pink}}"],
      :expound-str "Success!\n",
      :garden-vecs
      [["._-880421676" {"color" "black"}]
       #garden.types.CSSAtRule{:identifier :media,
                               :value
                               {:media-queries {:min-width :768px},
                                :rules
                                (["._-880421676" {"color" "orange"}])}}
       #garden.types.CSSAtRule{:identifier :media,
                               :value
                               {:media-queries {:min-width :1024px},
                                :rules
                                (["._-880421676" {"color" "blue"}])}}
       #garden.types.CSSAtRule{:identifier :media,
                               :value
                               {:media-queries {:min-width :1280px},
                                :rules
                                (["._-880421676" {"color" "pink"}])}}],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_-880421676"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_-880421676"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}})))
 (deftest
   with-classes
   (is
    (=
     (sx-dispatch
      {:args [:.absolute :c--black],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args [:.absolute :c--black],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_730259050",
       :selector "._730259050",
       :prefixed-name nil},
      :element-style-inj ["._730259050{color:black}"],
      :expound-str "Success!\n",
      :garden-vecs [["._730259050" {"color" "black"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["absolute" "_730259050"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["absolute" "_730259050"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}})))
 (deftest
   with-pseudos
   (is
    (=
     (sx-dispatch
      {:args
       [:hover:c--blue
        :>a:hover:c--red
        :&_a:hover:c--gold
        :&.bar:hover:c--pink
        :before:fw--bold
        :before:mie--5px
        {:style
         {:before:content "\"⌫\"",
          "~a:hover:c" :blue,
          "nth-child(2):c" :red}}],
       :form-meta {:file "filename.cljs", :line 11, :column 11}})
     {:args
      [:hover:c--blue
       :>a:hover:c--red
       :&_a:hover:c--gold
       :&.bar:hover:c--pink
       :before:fw--bold
       :before:mie--5px
       {:style
        {:before:content "\"⌫\"",
         "~a:hover:c" :blue,
         "nth-child(2):c" :red}}],
      :kushi/process :kushi.core/sx,
      :form-meta {:file "filename.cljs", :line 11, :column 11},
      :entries/weird nil,
      :selector
      {:selector* "_-1962865755",
       :selector "._-1962865755",
       :prefixed-name nil},
      :element-style-inj
      ["._-1962865755:hover{color:blue}"
       "._-1962865755>a:hover{color:red}"
       "._-1962865755 a:hover{color:gold}"
       "._-1962865755.bar:hover{color:pink}"
       "._-1962865755::before{font-weight:bold;margin-inline-end:5px;content:\"⌫\"}"
       "._-1962865755~a:hover{color:blue}"
       "._-1962865755:nth-child(2){color:red}"],
      :expound-str "Success!\n",
      :garden-vecs
      [["._-1962865755:hover" {"color" "blue"}]
       ["._-1962865755>a:hover" {"color" "red"}]
       ["._-1962865755 a:hover" {"color" "gold"}]
       ["._-1962865755.bar:hover" {"color" "pink"}]
       ["._-1962865755::before"
        {"font-weight" "bold",
         "margin-inline-end" "5px",
         "content" "\"⌫\""}]
       ["._-1962865755~a:hover" {"color" "blue"}]
       ["._-1962865755:nth-child(2)" {"color" "red"}]],
      :kushi/chunk :kushi.core/sx,
      :args/bad nil,
      :attrs
      {:class ["_-1962865755"],
       :style {},
       :data-sx "filename.cljs:11:11"},
      :classlist ["_-1962865755"],
      :data-sx "filename.cljs:11:11",
      :css-vars {}})))


