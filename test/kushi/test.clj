(ns kushi.test
  (:require
   [clojure.string :as string]
   [clojure.test :as test :refer [is testing deftest]]
   [garden.core :as garden]
   [clojure.pprint :refer [pprint]]
   [kushi.core :refer (sx-dispatch sx cssfn defclass defclass-dispatch keyframe add-font-face* add-system-font-stack system-at-font-face-rules)]
   [kushi.state :as state]
   [kushi.config :as config]))


(defn user-config-args-sx-defclass-stub! [m]
  ;; Explicitly disable caching
  (let [stub-config (config/->user-config (merge m {:enable-caching? false}))]
    (reset! state/user-config-args-sx-defclass-stub
            (config/->user-config-args-sx-defclass stub-config))
    state/user-config-args-sx-defclass-stub))


;; This must be called like so to make sure that caching is not used for testing
(user-config-args-sx-defclass-stub! {})



(deftest basics
  ;; Basics
  ;; -------------------------------------------------------------------------
  (is (= (sx-dispatch {:args (list {:style {:color :red}})})
         {:element-style-inj  ["._-309199624{color:red}"],
          :distinct-classes   (),
          :data-cljs          "::",
          :inj-type           :sx,
          :shared-styles-inj  {},
          :invalid-style-args nil,
          :garden-vecs        [["._-309199624" {"color" "red"}]],
          :attrs-base         {:data-cljs "::"},
          :prefixed-classlist ["_-309199624"],
          :kushi-attr         {},
          :css-vars           {}}))

  (is (= (sx-dispatch {:args (list :c--blue)})
         {:element-style-inj  ["._-987759238{color:blue}"],
          :distinct-classes   (),
          :data-cljs          "::",
          :inj-type           :sx,
          :shared-styles-inj  {},
          :invalid-style-args nil,
          :garden-vecs        [["._-987759238" {"color" "blue"}]],
          :attrs-base         {:data-cljs "::"},
          :prefixed-classlist ["_-987759238"],
          :kushi-attr         {},
          :css-vars           {}}))

  (is (= (sx-dispatch {:args (list :c--red)})
         {:element-style-inj ["._-2002783545{color:red}"],
          :distinct-classes (),
          :data-cljs "::",
          :inj-type :sx,
          :shared-styles-inj {},
          :invalid-style-args nil,
          :garden-vecs [["._-2002783545" {"color" "red"}]],
          :attrs-base {:data-cljs "::"},
          :prefixed-classlist ["_-2002783545"],
          :kushi-attr {},
          :css-vars {}}))

  (is (= (sx-dispatch {:args (list :color--red)})
         {:element-style-inj ["._-1265122990{color:red}"],
          :distinct-classes (),
          :data-cljs "::",
          :inj-type :sx,
          :shared-styles-inj {},
          :invalid-style-args nil,
          :garden-vecs [["._-1265122990" {"color" "red"}]],
          :attrs-base {:data-cljs "::"},
          :prefixed-classlist ["_-1265122990"],
          :kushi-attr {},
          :css-vars {}})))


(deftest shorthand
   ;; Basic shorthand
   ;; -------------------------------------------------------------------------
  (is (= (sx-dispatch {:args (list :ta--center)})
         {:element-style-inj  ["._-1999494570{text-align:center}"],
          :distinct-classes   (),
          :data-cljs          "::",
          :inj-type           :sx,
          :shared-styles-inj  {},
          :invalid-style-args nil,
          :garden-vecs        [["._-1999494570" {"text-align" "center"}]],
          :attrs-base         {:data-cljs "::"},
          :prefixed-classlist ["_-1999494570"],
          :kushi-attr         {},
          :css-vars           {}}))
  (is (= (sx-dispatch {:args (list :ta--c)})
         {:element-style-inj  ["._668461336{text-align:center}"],
          :distinct-classes   (),
          :data-cljs          "::",
          :inj-type           :sx,
          :shared-styles-inj  {},
          :invalid-style-args nil,
          :garden-vecs        [["._668461336" {"text-align" :center}]],
          :attrs-base         {:data-cljs "::"},
          :prefixed-classlist ["_668461336"],
          :kushi-attr         {},
          :css-vars           {}})))


(deftest dynamic-values
  ;; Basic vars
  ;; -------------------------------------------------------------------------
  (let [mycolor :red]
    (is (= (sx-dispatch {:args (list :c--$mycolor)})
           {:element-style-inj  ["._2008909213{color:var(--mycolor)}"],
            :distinct-classes   (),
            :data-cljs          "::",
            :inj-type           :sx,
            :shared-styles-inj  {},
            :invalid-style-args nil,
            :garden-vecs        [["._2008909213" {"color" "var(--mycolor)"}]],
            :attrs-base         {:data-cljs "::"},
            :prefixed-classlist ["_2008909213"],
            :kushi-attr         {},
            :css-vars           {"--mycolor" 'mycolor}})))
  (let [mycolor :blue]
    (is (= (sx-dispatch {:args (list {:style {:color mycolor}})})
           {:element-style-inj ["._-214725158{color:blue}"],
            :distinct-classes (),
            :inj-type :sx,
            :data-cljs "::",
            :shared-styles-inj {},
            :invalid-style-args nil,
            :garden-vecs [["._-214725158" {"color" "blue"}]],
            :attrs-base {:data-cljs "::"},
            :prefixed-classlist ["_-214725158"],
            :kushi-attr {},
            :css-vars {}}))))

(deftest css-shorthand
  ;; Basic css shorthand
  ;; -------------------------------------------------------------------------
  (is (= (sx-dispatch {:args (list {:style {:b :1px:solid:black}})})
         {:element-style-inj  ["._1575402217{border:1px solid black}"],
          :distinct-classes   (),
          :data-cljs          "::",
          :inj-type           :sx,
          :shared-styles-inj  {},
          :invalid-style-args nil,
          :garden-vecs        [["._1575402217" {"border" [["1px" "solid" "black"]]}]],
          :attrs-base         {:data-cljs "::"},
          :prefixed-classlist ["_1575402217"],
          :kushi-attr         {},
          :css-vars           {}}))
  (is (= (sx-dispatch {:args (list :b--1px:solid:black)})
         {:element-style-inj  ["._-52600747{border:1px solid black}"],
          :distinct-classes   (),
          :data-cljs          "::",
          :inj-type           :sx,
          :shared-styles-inj  {},
          :invalid-style-args nil,
          :garden-vecs        [["._-52600747" {"border" [["1px" "solid" "black"]]}]],
          :attrs-base         {:data-cljs "::"},
          :prefixed-classlist ["_-52600747"],
          :kushi-attr         {},
          :css-vars           {}}))

  ;; Value Lists
  ;; -------------------------------------------------------------------------
  (is (= (sx-dispatch {:args (list :ff--FiraCodeRegular|Consolas|monospace)})
         {:element-style-inj
          ["._-324716481{font-family:FiraCodeRegular,Consolas,monospace}"]
          :distinct-classes ()
          :data-cljs "::"
          :inj-type :sx
          :shared-styles-inj {}
          :invalid-style-args nil
          :garden-vecs
          [["._-324716481"
            {"font-family" ["FiraCodeRegular" "Consolas" "monospace"]}]],
          :attrs-base {:data-cljs "::"},
          :prefixed-classlist ["_-324716481"],
          :kushi-attr {},
          :css-vars {}}))
  (is (= (sx-dispatch {:args (list :text-shadow--5px:5px:10px:red|-5px:-5px:10px:blue)})
         {:element-style-inj
          ["._765496283{text-shadow:5px 5px 10px red,-5px -5px 10px blue}"],
          :distinct-classes (),
          :data-cljs "::",
          :inj-type :sx,
          :shared-styles-inj {},
          :invalid-style-args nil,
          :garden-vecs
          [["._765496283"
            {"text-shadow"
             [["5px" "5px" "10px" "red"] ["-5px" "-5px" "10px" "blue"]]}]],
          :attrs-base {:data-cljs "::"},
          :prefixed-classlist ["_765496283"],
          :kushi-attr {},
          :css-vars {}})))

(deftest css-custom-properties
  (is (= (sx-dispatch {:args (list :b--:--my-css-custom-prop-for-border)})
         {:element-style-inj  ["._370307581{border:var(--my-css-custom-prop-for-border)}"],
          :distinct-classes   (),
          :data-cljs          "::",
          :inj-type           :sx,
          :shared-styles-inj  {},
          :invalid-style-args nil,
          :garden-vecs        [["._370307581" {"border" "var(--my-css-custom-prop-for-border)"}]],
          :attrs-base         {:data-cljs "::"},
          :prefixed-classlist ["_370307581"],
          :kushi-attr         {},
          :css-vars           {}}))
  (is (= (sx-dispatch {:args (list {:style {:b :--my-css-custom-prop-for-border}})})
         {:element-style-inj  ["._1820818409{border:var(--my-css-custom-prop-for-border)}"],
          :distinct-classes   (),
          :data-cljs          "::",
          :inj-type           :sx,
          :shared-styles-inj  {},
          :invalid-style-args nil,
          :garden-vecs        [["._1820818409" {"border" "var(--my-css-custom-prop-for-border)"}]],
          :attrs-base         {:data-cljs "::"},
          :prefixed-classlist ["_1820818409"],
          :kushi-attr         {},
          :css-vars           {}})))

(deftest complex-values
  (is (= (sx-dispatch {:args (list {:style {:before:content "\"*\""
                                            :width          "calc((100vw / 3) + 12px)"}})})
         {:element-style-inj
          ["._897127374{width:calc((100vw / 3)+12px)}"
           "._897127374::before{content:\"*\"}"],
          :distinct-classes (),
          :data-cljs "::",
          :inj-type :sx,
          :shared-styles-inj {},
          :invalid-style-args nil,
          :garden-vecs
          [["._897127374" {"width" "calc((100vw / 3) + 12px)"}]
           ["._897127374::before" {"content" "\"*\""}]],
          :attrs-base {:data-cljs "::"},
          :prefixed-classlist ["_897127374"],
          :kushi-attr {},
          :css-vars {}})))

(deftest with-mqs
  (is (= (sx-dispatch {:args (list :c--black :md:c--:orange :lg:c--:blue :xl:c--:pink)})
         {:element-style-inj  ["._-1675102898{color:black}"
                               "@media(min-width:768px){._-1675102898{color:orange}}"
                               "@media(min-width:1024px){._-1675102898{color:blue}}"
                               "@media(min-width:1280px){._-1675102898{color:pink}}"],
          :distinct-classes   (),
          :data-cljs          "::",
          :inj-type           :sx,
          :shared-styles-inj  {},
          :invalid-style-args nil,
          :garden-vecs        [["._-1675102898" {"color" "black"}]
                               #garden.types.CSSAtRule{:identifier :media,
                                                       :value      {:media-queries {:min-width :768px},
                                                                    :rules         (["._-1675102898"
                                                                                     {"color" [["" "orange"]]}])}}
                               #garden.types.CSSAtRule{:identifier :media,
                                                       :value      {:media-queries {:min-width :1024px},
                                                                    :rules         (["._-1675102898"
                                                                                     {"color" [["" "blue"]]}])}}
                               #garden.types.CSSAtRule{:identifier :media,
                                                       :value      {:media-queries {:min-width :1280px},
                                                                    :rules         (["._-1675102898"
                                                                                     {"color" [["" "pink"]]}])}}],
          :attrs-base         {:data-cljs "::"},
          :prefixed-classlist ["_-1675102898"],
          :kushi-attr         {},
          :css-vars           {}}))
  (is (= (sx-dispatch {:args (list {:style {:c    :black
                                            :md:c :orange
                                            :lg:c :blue
                                            :xl:c :pink}})})
         {:element-style-inj  ["._-2039585620{color:black}"
                               "@media(min-width:768px){._-2039585620{color:orange}}"
                               "@media(min-width:1024px){._-2039585620{color:blue}}"
                               "@media(min-width:1280px){._-2039585620{color:pink}}"],
          :distinct-classes   (),
          :data-cljs          "::",
          :inj-type           :sx,
          :shared-styles-inj  {},
          :invalid-style-args nil,
          :garden-vecs        [["._-2039585620" {"color" "black"}]
                               #garden.types.CSSAtRule{:identifier :media,
                                                       :value      {:media-queries {:min-width :768px},
                                                                    :rules         (["._-2039585620" {"color" "orange"}])}}
                               #garden.types.CSSAtRule{:identifier :media,
                                                       :value      {:media-queries {:min-width :1024px},
                                                                    :rules         (["._-2039585620" {"color" "blue"}])}}
                               #garden.types.CSSAtRule{:identifier :media,
                                                       :value      {:media-queries {:min-width :1280px},
                                                                    :rules         (["._-2039585620" {"color" "pink"}])}}],
          :attrs-base         {:data-cljs "::"},
          :prefixed-classlist ["_-2039585620"],
          :kushi-attr         {},
          :css-vars           {}})))


(deftest with-mqs-with-different-media-config
  ;; Temporarily change the media config
  (user-config-args-sx-defclass-stub! {:media [:sm {:min-width :540px}
                                               :md {:min-width :668px}
                                               :lg {:min-width :1024px}
                                               :xl {:min-width :1280px}
                                               :2xl {:min-width :1536px}]})

  (is (= (sx-dispatch {:args (list :c--black :md:c--:orange :lg:c--:blue :xl:c--:pink)})
         {:element-style-inj
          ["._-1477448333{color:black}"
           "@media(min-width:768px){._-1477448333{color:orange}}"
           "@media(min-width:1024px){._-1477448333{color:blue}}"
           "@media(min-width:1280px){._-1477448333{color:pink}}"],
          :distinct-classes (),
          :data-cljs "::",
          :inj-type :sx,
          :shared-styles-inj {},
          :invalid-style-args nil,
          :garden-vecs
          [["._-1477448333" {"color" "black"}]
           #garden.types.CSSAtRule{:identifier :media,
                                   :value
                                   {:media-queries {:min-width :768px},
                                    :rules
                                    (["._-1477448333"
                                      {"color" [["" "orange"]]}])}}
           #garden.types.CSSAtRule{:identifier :media,
                                   :value
                                   {:media-queries {:min-width :1024px},
                                    :rules
                                    (["._-1477448333"
                                      {"color" [["" "blue"]]}])}}
           #garden.types.CSSAtRule{:identifier :media,
                                   :value
                                   {:media-queries {:min-width :1280px},
                                    :rules
                                    (["._-1477448333"
                                      {"color" [["" "pink"]]}])}}],
          :attrs-base {:data-cljs "::"},
          :prefixed-classlist ["_-1477448333"],
          :kushi-attr {},
          :css-vars {}}))

  ;; Resent the media config
  (user-config-args-sx-defclass-stub! {}) )


(deftest with-multiple-properties
  (let [wtf "10px"
        wtf2 "20px"]
    (is (= (sx-dispatch {:args (list :.absolute
                                     :c--black
                                     :ta--c
                                     :pis--$wtf
                                     :pie--:--my-pie
                                     {:class [:dull]
                                      :style {:margin-bottom 'wtf2}
                                      :id    :foo})})
           {:element-style-inj
            ["._72952793{color:black;text-align:center;padding-inline-start:var(--wtf);padding-inline-end:var(--my-pie);margin-bottom:var(--wtf2)}"],
            :distinct-classes '(:dull :absolute),
            :data-cljs "::",
            :inj-type :sx,
            :shared-styles-inj {},
            :invalid-style-args nil,
            :garden-vecs
            [["._72952793"
              {"color" "black",
               "text-align" :center,
               "padding-inline-start" "var(--wtf)",
               "padding-inline-end" "var(--my-pie)",
               "margin-bottom" "var(--wtf2)"}]],
            :attrs-base {:id :foo, :data-cljs "::"},
            :prefixed-classlist ["_72952793" "dull" "absolute"],
            :kushi-attr {},
           :css-vars {"--wtf" 'wtf, "--wtf2" 'wtf2}}))))

(deftest invalid-values
  (reset! state/silence-warnings? true)
  (is (= (sx-dispatch {:args (list :c--red "wtf" :b--1px:solid:black "wtf2")})
         {:element-style-inj ["._1397816810{color:red;border:1px solid black}"],
          :distinct-classes (),
          :data-cljs "::",
          :inj-type :sx,
          :shared-styles-inj {},
          :invalid-style-args {[1] "wtf", [3] "wtf2"},
          :garden-vecs
          [["._1397816810"
            {"color" "red", "border" [["1px" "solid" "black"]]}]],
          :attrs-base {:data-cljs "::"},
          :prefixed-classlist ["_1397816810"],
          :kushi-attr {},
          :css-vars {}}))
  (reset! state/silence-warnings? false))


(deftest with-classes
  (is (= (sx-dispatch {:args (list :.absolute :c--black)})
         {:element-style-inj  ["._1322700139{color:black}"],
          :distinct-classes   '(:absolute),
          :data-cljs          "::",
          :inj-type           :sx,
          :shared-styles-inj  {},
          :invalid-style-args nil,
          :garden-vecs        [["._1322700139" {"color" "black"}]],
          :attrs-base         {:data-cljs "::"},
          :prefixed-classlist ["_1322700139" "absolute"],
          :kushi-attr         {},
          :css-vars           {}}))
  (is (= (sx-dispatch {:args (list :.absolute :.foo :c--black)})
         {:element-style-inj  ["._1876721552{color:black}"],
          :distinct-classes   '(:absolute :foo),
          :data-cljs          "::",
          :inj-type           :sx,
          :shared-styles-inj  {},
          :invalid-style-args nil,
          :garden-vecs        [["._1876721552" {"color" "black"}]],
          :attrs-base         {:data-cljs "::"},
          :prefixed-classlist ["_1876721552" "absolute" "foo"],
          :kushi-attr         {},
          :css-vars           {}}))
  (is (= (sx-dispatch {:args (list :.absolute :.foo)})
         {:element-style-inj [],
          :distinct-classes '(:absolute :foo),
          :data-cljs "::",
          :inj-type :sx,
          :shared-styles-inj {},
          :invalid-style-args nil,
          :garden-vecs [["._-1620591236" nil]],
          :attrs-base {:data-cljs "::"},
          :prefixed-classlist ["absolute" "foo"],
          :kushi-attr {},
          :css-vars {}}))

;;  TODO add class tests with various permutations, conditionality etc.
)

(deftest with-psuedos
  (is (= (sx-dispatch {:args (list  :hover:c--blue
                                            :>a:hover:c--red
                                            :&_a:hover:c--gold
                                            :&.bar:hover:c--pink
                                            :before:fw--bold
                                            :before:mie--5px
                                            {:style {:before:content "\"⌫\""
                                                     "~a:hover:c"      :blue
                                                     "nth-child(2):c"  :red}})})
         {:element-style-inj
          ["._-1951396261 a:hover{color:gold}"
           "._-1951396261 ~ a:hover{color:blue}"
           "._-1951396261.bar:hover{color:pink}"
           "._-1951396261::before{font-weight:bold;margin-inline-end:5px;content:\"⌫\"}"
           "._-1951396261:hover{color:blue}"
           "._-1951396261>a:hover{color:red}"
           "._-1951396261:nth-child(2){color:red}"],
          :distinct-classes (),
          :data-cljs "::",
          :inj-type :sx,
          :shared-styles-inj {},
          :invalid-style-args nil,
          :garden-vecs
          [["._-1951396261" nil]
           ["._-1951396261 a:hover" {"color" "gold"}]
           ["._-1951396261 ~ a:hover" {"color" "blue"}]
           ["._-1951396261.bar:hover" {"color" "pink"}]
           ["._-1951396261::before"
            {"font-weight" "bold",
             "margin-inline-end" "5px",
             "content" "\"⌫\""}]
           ["._-1951396261:hover" {"color" "blue"}]
           ["._-1951396261 > a:hover" {"color" "red"}]
           ["._-1951396261:nth-child(2)" {"color" "red"}]],
          :attrs-base {:data-cljs "::"},
          :prefixed-classlist ["_-1951396261"],
          :kushi-attr {},
          :css-vars {}})))

#_(deftest with-combos)
#_(deftest with-hover)
#_(deftest with-nested-hover)
#_(deftest with-before-and-after)
#_(deftest with-descendant)
#_(deftest with-nth-child)

;; TODO - break up previous test into more specific chunks ala above deftests
;; :hover
;; :nested hover
;; :before & :after
;; :&_
;; one for each of these:
;; {:before:content "\"⌫\""
;;  "~a:hover:c"      :blue
;;  "nth-child(2):c"  :red}

(deftest with-ancestors
  (is (= (sx-dispatch {:args (list :section.baz&_.%:color--blue
                                           :section.dark>.%:color--white)})
         {:element-style-inj
          ["section.baz ._-895549182{color:blue}"
           "section.dark>._-895549182{color:white}"],
          :distinct-classes (),
          :data-cljs "::",
          :inj-type :sx,
          :shared-styles-inj {},
          :invalid-style-args nil,
          :garden-vecs
          [["._-895549182" nil]
           ["section.baz ._-895549182" {"color" "blue"}]
           ["section.dark > ._-895549182" {"color" "white"}]],
          :attrs-base {:data-cljs "::"},
          :prefixed-classlist ["_-895549182"],
          :kushi-attr {},
          :css-vars {}})))

(deftest  system-font-stack
  (is (= (system-at-font-face-rules '())
         '("@font-face {\n  font-family: sys;\n  font-style: normal;\n  font-weight: 300;\n  src: local(\".SFNS-Light\"), local(\".SFNSText-Light\"), local(\".HelveticaNeueDeskInterface-Light\"), local(\".LucidaGrandeUI\"), local(\"Segoe UI Light\"), local(\"Ubuntu Light\"), local(\"Roboto-Light\"), local(\"DroidSans\"), local(\"Tahoma\");\n}"
           "@font-face {\n  font-family: sys;\n  font-style: italic;\n  font-weight: 300;\n  src: local(\".SFNS-LightItalic\"), local(\".SFNSText-LightItalic\"), local(\".HelveticaNeueDeskInterface-Italic\"), local(\".LucidaGrandeUI\"), local(\"Segoe UI Light Italic\"), local(\"Ubuntu Light Italic\"), local(\"Roboto-LightItalic\"), local(\"DroidSans\"), local(\"Tahoma\");\n}"
           "@font-face {\n  font-family: sys;\n  font-style: normal;\n  font-weight: 400;\n  src: local(\".SFNS-Regular\"), local(\".SFNSText-Regular\"), local(\".HelveticaNeueDeskInterface-Regular\"), local(\".LucidaGrandeUI\"), local(\"Segoe UI\"), local(\"Ubuntu\"), local(\"Roboto-Regular\"), local(\"DroidSans\"), local(\"Tahoma\");\n}"
           "@font-face {\n  font-family: sys;\n  font-style: italic;\n  font-weight: 400;\n  src: local(\".SFNS-Italic\"), local(\".SFNSText-Italic\"), local(\".HelveticaNeueDeskInterface-Italic\"), local(\".LucidaGrandeUI\"), local(\"Segoe UI Italic\"), local(\"Ubuntu Italic\"), local(\"Roboto-Italic\"), local(\"DroidSans\"), local(\"Tahoma\");\n}"
           "@font-face {\n  font-family: sys;\n  font-style: normal;\n  font-weight: 500;\n  src: local(\".SFNS-Medium\"), local(\".SFNSText-Medium\"), local(\".HelveticaNeueDeskInterface-MediumP4\"), local(\".LucidaGrandeUI\"), local(\"Segoe UI Semibold\"), local(\"Ubuntu Medium\"), local(\"Roboto-Medium\"), local(\"DroidSans-Bold\"), local(\"Tahoma Bold\");\n}"
           "@font-face {\n  font-family: sys;\n  font-style: italic;\n  font-weight: 500;\n  src: local(\".SFNS-MediumItalic\"), local(\".SFNSText-MediumItalic\"), local(\".HelveticaNeueDeskInterface-MediumItalicP4\"), local(\".LucidaGrandeUI\"), local(\"Segoe UI Semibold Italic\"), local(\"Ubuntu Medium Italic\"), local(\"Roboto-MediumItalic\"), local(\"DroidSans-Bold\"), local(\"Tahoma Bold\");\n}"
           "@font-face {\n  font-family: sys;\n  font-style: normal;\n  font-weight: 700;\n  src: local(\".SFNS-Bold\"), local(\".SFNSText-Bold\"), local(\".HelveticaNeueDeskInterface-Bold\"), local(\".LucidaGrandeUI\"), local(\"Segoe UI Bold\"), local(\"Ubuntu Bold\"), local(\"Roboto-Bold\"), local(\"DroidSans-Bold\"), local(\"Tahoma Bold\");\n}"
           "@font-face {\n  font-family: sys;\n  font-style: italic;\n  font-weight: 700;\n  src: local(\".SFNS-BoldItalic\"), local(\".SFNSText-BoldItalic\"), local(\".HelveticaNeueDeskInterface-BoldItalic\"), local(\".LucidaGrandeUI\"), local(\"Segoe UI Bold Italic\"), local(\"Ubuntu Bold Italic\"), local(\"Roboto-BoldItalic\"), local(\"DroidSans-Bold\"), local(\"Tahoma Bold\");\n}")))
  (is (= (system-at-font-face-rules '(300 700))
         '("@font-face {\n  font-family: sys;\n  font-style: normal;\n  font-weight: 300;\n  src: local(\".SFNS-Light\"), local(\".SFNSText-Light\"), local(\".HelveticaNeueDeskInterface-Light\"), local(\".LucidaGrandeUI\"), local(\"Segoe UI Light\"), local(\"Ubuntu Light\"), local(\"Roboto-Light\"), local(\"DroidSans\"), local(\"Tahoma\");\n}"
           "@font-face {\n  font-family: sys;\n  font-style: italic;\n  font-weight: 300;\n  src: local(\".SFNS-LightItalic\"), local(\".SFNSText-LightItalic\"), local(\".HelveticaNeueDeskInterface-Italic\"), local(\".LucidaGrandeUI\"), local(\"Segoe UI Light Italic\"), local(\"Ubuntu Light Italic\"), local(\"Roboto-LightItalic\"), local(\"DroidSans\"), local(\"Tahoma\");\n}"
           "@font-face {\n  font-family: sys;\n  font-style: normal;\n  font-weight: 700;\n  src: local(\".SFNS-Bold\"), local(\".SFNSText-Bold\"), local(\".HelveticaNeueDeskInterface-Bold\"), local(\".LucidaGrandeUI\"), local(\"Segoe UI Bold\"), local(\"Ubuntu Bold\"), local(\"Roboto-Bold\"), local(\"DroidSans-Bold\"), local(\"Tahoma Bold\");\n}"
           "@font-face {\n  font-family: sys;\n  font-style: italic;\n  font-weight: 700;\n  src: local(\".SFNS-BoldItalic\"), local(\".SFNSText-BoldItalic\"), local(\".HelveticaNeueDeskInterface-BoldItalic\"), local(\".LucidaGrandeUI\"), local(\"Segoe UI Bold Italic\"), local(\"Ubuntu Bold Italic\"), local(\"Roboto-BoldItalic\"), local(\"DroidSans-Bold\"), local(\"Tahoma Bold\");\n}"))) )


(deftest font-face
  (is (= (:aff (add-font-face* {:font-family "FiraCodeRegular"
                                :font-weight "400"
                                :font-style  "normal"
                                :src         ["url(../fonts/FiraCode-Regular.woff)"]}))
         "@font-face {\n  font-family: FiraCodeRegular;\n  font-weight: 400;\n  font-style: normal;\n  src: url(../fonts/FiraCode-Regular.woff);\n}")) )


(deftest defclass-basics
  (is (= (defclass-dispatch {:sym 'gold :args '(:c--gold)})
         {:args [:c--gold],
          :defclass-name :gold,
          :selector ".gold",
          :classtype :user-utility,
          :hydrated-styles [:c--gold],
          :n :gold,
          :coll '(:c--gold),
          :warnings nil,
          :garden-vecs [[".gold" {"color" "gold"}]],
          :selector* "gold",
          :invalid-args nil,
          :style-map nil,
          :tokens [:c--gold],
          :style-map* nil})))


#_(deftest ^:test-refresh/focus defclass-merging
    (is (= (pprint (defclass-dispatch {:sym  'bold
                                       :args '(:.absolute :c--blue)}))
           nil #_{:args            [:c--gold],
                  :defclass-name   :gold,
                  :selector        ".gold",
                  :classtype       :user-utility,
                  :hydrated-styles [:c--gold],
                  :n               :gold,
                  :coll            '(:c--gold),
                  :warnings        nil,
                  :garden-vecs     [[".gold" {"color" "gold"}]],
                  :selector*       "gold",
                  :invalid-args    nil,
                  :style-map       nil,
                  :tokens          [:c--gold],
                  :style-map*      nil})))

#_(deftest cssfn-helper
    (is (= (sx-dispatch {:args (list {:style {:color  (cssfn :rgba 0 0 10 0.5)}})})
           {:element-style-inj  ["._1820818409{border:var(--my-css-custom-prop-for-border)}"],
            :distinct-classes   (),
            :data-cljs          "::",
            :inj-type           :sx,
            :shared-styles-inj  {},
            :invalid-style-args nil,
            :garden-vecs        [["._1820818409" {"border" "var(--my-css-custom-prop-for-border)"}]],
            :attrs-base         {:data-cljs "::"},
            :prefixed-classlist ["_1820818409"],
            :kushi-attr         {},
            :css-vars           {}})))
#_(deftest provided-classname
    (is (= (pprint (sx-dispatch {:args (list 'myname :c--black)}))
           {:element-style-inj  ["._1820818409{border:var(--my-css-custom-prop-for-border)}"],
            :distinct-classes   (),
            :data-cljs          "::",
            :inj-type           :sx,
            :shared-styles-inj  {},
            :invalid-style-args nil,
            :garden-vecs        [["._1820818409" {"border" "var(--my-css-custom-prop-for-border)"}]],
            :attrs-base         {:data-cljs "::"},
            :prefixed-classlist ["_1820818409"],
            :kushi-attr         {},
            :css-vars           {}})))

#_(deftest provided-classname-with-prefix)


#_(deftest ^:test-refresh/focus with-prepend
  (user-config-args-sx-defclass-stub! {:kushi-class-prefix ".wtf"})
  (is (= (pprint (sx-dispatch {:args (list :b--1px:solid:black)}))
         {:element-style-inj  ["._-52600747{border:1px solid black}"],
          :distinct-classes   (),
          :data-cljs          "::",
          :inj-type           :sx,
          :shared-styles-inj  {},
          :invalid-style-args nil,
          :garden-vecs        [["._-52600747" {"border" [["1px" "solid" "black"]]}]],
          :attrs-base         {:data-cljs "::"},
          :prefixed-classlist ["_-52600747"],
          :kushi-attr         {},
          :css-vars           {}}))
  (user-config-args-sx-defclass-stub! {})
  )

#_(deftest ^:test-refresh/focus with-scales
  (user-config-args-sx-defclass-stub! {:scaling-system :tachyons})
  (is (= (pprint (sx-dispatch {:args (list :w--1*
                                           :bw--2*
                                           :fs--3*
                                           :p--sm*
                                           :m--md*)}))
         nil))
  (user-config-args-sx-defclass-stub! {}))

;; TODO add a test for defkeyframes
