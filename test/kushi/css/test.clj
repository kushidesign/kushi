(ns kushi.css.test
  (:require [clojure.test :refer :all]
            [kushi.css.sandbox]
            [fireworks.core :refer [? !? ?> !?>]]
            [bling.core :refer [bling callout]]
            [kushi.css.defs]
            [kushi.css.core :refer [ansi-colorized-css-block
                                    css-block-data
                                    css-block
                                    nested-css-block
                                    css-rule
                                    css-rule*
                                    css
                                    ?css
                                    sx
                                    ?sx
                                    defcss
                                    ?defcss
                                    css-vars
                                    css-vars-map
                                    lightning-opts
                                    lightning
                                    ]]
            [clojure.string :as string]
            [kushi.css.specs :as specs]
            [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.walk :as walk]
            ;; [taoensso.tufte :as tufte :refer [p profile]]
            [kushi.css.defs :as defs]
            [edamame.core :as e :refer [parse-string parse-string-all]]
            ))



#_(let [myclass :.gall]
  (?css :.wtf
        myclass
        {:ta                                          :c
         :w                                           0
         :h                                           0
         :.kushi-slider-step-label-selected:transform "scale(1)"
         :.kushi-slider-step-label-selected:o         1
         :.kushi-slider-step-label-selected:c         :currentColor
         :.kushi-slider-step-label-selected>span:v    :visible
         :transform                                   :$label-scale-factor
         :before:content                              :$step-marker-content}))


#_(? (re-find #"^([^\s:]+):" (? (-> [:sm:c :red] first name))))

;; (? (css-block
;;     ["first-child#foos:display" :block]))

;; (? (css-block
;;     :first-child#foos:display--block))

;; (? (css-block
;;     :first-child>div:display--block))

#_(? (css-block
    :sm:w--34px
    :sm:h--34px
    :xsm:w--29px
    :xsm:h--29px
    :w--26px
    :h--26px))

;; (? (css-block "text-shadow--5px:5px:10px:red|-5px:-5px:10px:blue"))


;; (def v1 (css-block :box-shadow--0:0:0:12px:$shadow-1||$shadow-1b|0:0:0:5px:$shadow-2||$shadow-2b))
;; (def v2 (css-block [:box-shadow "0 0 0 12px $shadow-1||$shadow-1b, 0 0 0 5px $shadow-2||$shadow-2b"]))
;; (? (= v1 (? v2)))

;; (? (css-block [:box-shadow "0 0 0 12px $my-gold||$my-silver"]))

;; (? (-> "$shadow-1||$shadow-2|$shadow-3||$shadow-4"
;;        (string/replace #"\|\|" "____*DOUBLE-BAR*____")
;;        (string/split #"\|")
;;        (->> (map #(string/replace % #"____\*DOUBLE-BAR\*____" "||")))
;;        ))

;; (? (css-rule* ".wtf"
;;               ;; [:fs--$large||$small||$xsmall||$no]
;;               ;; [[:fs "$large||$small||$xsmall||$no"]]
;;               ;; [[:ff "$large||regular||$gold|$bang"]]
;;               [[:box-shadow--0:0:0:10px:red|0:0:0:10px:black]]
;;               nil nil))

;; (? (css-rule* ".wtf" [[">*:not([data-kushi-playground-sidenav]):pi" :1.25rem]] nil nil))
;; (? (css-rule* ".wtf" [[">*:last-child:pi" :1.25rem]] nil nil))
;; (? (css-rule* ".wtf" [:>*:last-child:pi--1.25rem] nil nil))
;; (? (css-rule* ".wtf" [{:.foo:last-child:c :red}] nil nil))
;; (? (css-rule* ".wtf" [["input:checked+.kushi-label:c" :red]] nil nil))
;; (? (css-rule* ".wtf" [["input:checked:c" :red]] nil nil))


;; (? (s/valid? ::specs/css-value "\"2||3\""))
;; (? (s/valid? ::specs/css-value "2||3"))

(deftest sample (is (= 1 1)))


#_(? (s/valid? ::specs/style-vec
          [:--_arrow-stop "calc(50% + max(1px, (var(--border-width) * 0.72)))"]))

;; TODO - fix or catch this
;; (?css :--_x--2)

;; TODO - Fix spec to issue warning
;; (?css [:$f 2])

;; TODO - Fix spec to issue warning
;; (?css :$f--2)

;; TODO - Fix spec to issue warning, or fix overflow error
;; (?css :$_f--2)
;; (? {["wtf" "OH yeah"] '(1 2 3)})
;; (? (array-map ["wtf" "OH yeah"] '(1 2 3)))

#_(?defcss
  ".kushi-pane-arrow"
  ;; :c--red
  ;; :.absolute
  ;; :bw--inherit
  ;; :bs--inherit
  ;; :bc--inherit
  ;; [:--sz "calc(sqrt(2)* var(--arrow-depth))"]
  ;; :w--$sz
  ;; :h--$sz
  ;; [:--arrow-inline-inset :-50%]
  ;; [:--arrow-block-inset :-50%]
  ;; :bgc--inherit
  ;; :h--$sz
  [:--_arrow-stop "calc(50% + max(1px, (var(--border-width) * 0.72)))"]
  ;; [:mask-image "linear-gradient(var(--_arrow-gradient-direction), black var(--_arrow-stop), transparent var(--_arrow-stop))"]
  ;; [:transform "translate(var(--arrow-tx), var(--arrow-ty)) rotate(45deg)"]

  ;; [".kushi-pane-tl &" {:border-top-width :0!important
  ;;                                :border-left-width :0!important}]
  ;; [".kushi-pane-t &" {:border-top-width :0!important
  ;;                               :border-left-width :0!important}]
  ;; [".kushi-pane-tr &" {:border-top-width :0!important
  ;;                                :border-left-width :0!important}]

  ;; [".kushi-pane-rt &" {:border-top-width :0!important
  ;;                                :border-right-width :0!important}]
  ;; [".kushi-pane-r &" {:border-top-width :0!important
  ;;                               :border-right-width :0!important}]
  ;; [".kushi-pane-rb &" {:border-top-width :0!important
  ;;                                :border-right-width :0!important}]

  ;; [".kushi-pane-br &" {:border-bottom-width :0!important
  ;;                                :border-right-width :0!important}]
  ;; [".kushi-pane-b &" {:border-bottom-width :0!important
  ;;                               :border-right-width :0!important}]
  ;; [".kushi-pane-bl &" {:border-bottom-width :0!important
  ;;                                :border-right-width :0!important}]

  ;; [".kushi-pane-l &" {:border-bottom-width :0!important
  ;;                               :border-left-width :0!important}]
  ;; [".kushi-pane-lt &" {:border-bottom-width :0!important
  ;;                                :border-left-width :0!important}]
  ;; [".kushi-pane-lb &" {:border-bottom-width :0!important
  ;;                                :border-left-width :0!important}]
  )

;; (!? (-> (css-rule ".bar"
;;                   :c--red
;;                   :_.bar:c--green)
;;         lightning))

;; (!? (update-in {} ["new vector"] conj {:a 1}))

;; (!? (css-rule* "@layer bang .foo_bar__L12_C11" [:c--red]
;;               (with-meta (list 'css :w--100%)
;;                 {:file "wtf.cljs" :line 20 :column 12})
;;               nil))

;; (!? (css-rule* ".foo_bar__L12_C11" [:c--red]
;;               (with-meta (list 'css :w--100%)
;;                 {:file "wtf.cljs" :line 20 :column 12})
;;               nil))

;;  (def css-data (volatile! {:defcss [] :css []}))

;;  #_(? (vswap! css-data update-in [:defcss] conj 1))

;;  (def form '(let [a 1]
;;               '(css :c--red)
;;               :foo
;;               '(defcss ".foo" :c--blue))) 

;; (walk/postwalk (fn [x] 
;;                  (when-let [sym (when (list? x) (first x))]
;;                    (case sym
;;                      defcss
;;                      (vswap! css-data
;;                              update-in
;;                              [:defcss]
;;                              conj
;;                              {:kushi/macro 'defcss
;;                               :args (rest x)})
;;                      css
;;                      (vswap! css-data
;;                              update-in
;;                              [:css]
;;                              conj
;;                              {:kushi/macro 'css
;;                               :args (rest x)})
;;                      nil))
;;                  x)
;;                form)
;;  (? css-data)

#_(? (css-rule*
    ".kushi-link"
    [
     :cursor--pointer
     :td--underline
     :tup--under
     [:tdc "color-mix(in oklch, currentColor 40%, transparent)"]
     [:hover:tdc :currentColor]
     ]
    nil
    nil))
#_(? (css-rule*
    ".kushi-switch"
    [
    ;;  [:hover:bgc :transparent!important]
     :c--red!important
     "bgc--blue!important"
     [:hover:bgc "transparent!important"]
     { :hover:bgc "transparent!important" }
     ]
    nil
    nil))
#_(? (css-rule*
    ".kushi-switch"
    [[" .kushi-radio-input:checked+.kushi-label>.emoji"
      {:filter    :none
       :transform "scale(1.5)"
       :animation :jiggle2:0.5s}]]
    nil
    nil))

;; (? (distinct [1 2 1 5 3 1 4 1]))


;; (!? (string/split
;;     "@layer mylayer
;;      .foobar"
;;     #"[\t\n\r\s]+"))

;; (? (css ".foos" :w--100% [:h :30px]))

;; (? (css-rule* ".foos"
;;               [:w--100% [:h :30px]]
;;               (with-meta (list 'css :w--100%)
;;                 {:file "wtf.cljs" :line 20 :column 12})
;;               nil))

;; #_(? (css-block {" .foo:color" :red}))

;; #_(? (css-rule ".foo" {:c "blue"} :c--red))

;; #_(? (css-rule "@keyframes yspinner"
;;      [:0% {:transform "rotateY(0deg)"}]
;;      [:100% {:transform "rotateY(360deg)"}]))

#_(? (css-rule "p" {:c :red :bgc :blue}))
;; (? (css-rule* "p" (list {:c :red :bgc :blue}) nil nil))

#_(do (def release? true #_false)
    (def all-tokens [:--foreground-color         :$neutral-950
                     :--foreground-color-inverse :$neutral-50
                     :--background-color         :white
                     :--background-color-inverse :$neutral-1000
                     ])
    (def bs {:all-design-tokens all-tokens})

    (defn design-tokens-css [req {:keys [all-design-tokens]}]
      (let [tokens (if release?
                     (->> all-design-tokens
                          (partition 2)
                          (filter (fn [[k _]] (contains? req k)))
                          (apply concat)
                          (apply array-map))
                     (apply array-map all-design-tokens))]
        (css-rule* ":root" (list tokens) nil nil)))

    (? (design-tokens-css #{:--foreground-color :--background-color} bs)))

#_(? (css-rule* ":root"
              (list (array-map
                     :--foreground-color         :$neutral-950
                     :--foreground-color-inverse :$neutral-50

                     :--background-color         :white
                     :--background-color-inverse :$neutral-1000
                     ))
              nil nil))

#_(def block
  (css-block {:border-block-end           :$divisor
              :dark:border-block-end      :$divisor-inverse
              ;; :_.foo:c                    :red
              ;; :dark:color                 :blue
              :transition-property        :none
              :transition-timing-function :$transition-timing-function
              :transition-duration        :$transition-duration}))

#_(println (ansi-colorized-css-block {:block block :sel ".wtf"}))

#_(println (ansi-colorized-css-block
          {:block (nested-css-block 
                   (list (apply array-map
                                :$foreground-color :$neutral-950
                                :$foreground-color-inverse :$neutral-50

                                :$background-color :white
                                :$background-color-inverse :$neutral-1000)
                         nil
                         nil
                         'myfun
                         nil))
           :sel   ".wtf"}))


;; Fix tests
#_(do 

;; *****************************************************************************
;; *****************************************************************************
;; *****************************************************************************
  
  (deftest tokenized-keywords
    (testing "tokenized keywords ->"
      (testing "single -> "
        (testing "1"
          (is (= (css-block :c--red)
                 "{\n  color: red;\n}")))

        (testing "with css var"
          (is (= (css-block :c--$red-100)
                 "{\n  color: var(--red-100);\n}")))

        (testing "with with multiple properties syntax"
          (is (= (css-block :b--1px:solid:red)
                 "{\n  border: 1px solid red;\n}")))

        (testing "with with multiple properties syntax and css-var"
          (is (= (css-block :b--1px:solid:$red-100)
                 "{\n  border: 1px solid var(--red-100);\n}")))

        (testing "with alternation syntax"
          (is (= (css-block :ff--sans-serif|fantasy)
                 "{\n  font-family: sans-serif, fantasy;\n}")))

        (testing "with alternation syntax and multiple properties syntax"
          (is (= (css-block :text-shadow--5px:5px:10px:red|-5px:-5px:10px:blue)
                 "{\n  text-shadow: 5px 5px 10px red, -5px -5px 10px blue;\n}"))))
      

      (testing "multiple -> " 
        (testing "2"
          (is (= (css-block :c--red :bgc--blue)
                 "{\n  color: red;\n  background-color: blue;\n}")))
        (testing "with classname"
          (is (= (css-block :.foo :c--red :bgc--blue)
                 "{\n  color: red;\n  background-color: blue;\n}")))
        (testing "with pseudoclasses"
          (is (= (css-block :active:c--magenta
                            :visited:c--orange
                            :hover:c--red)
                 "{
  &:visited {
    color: orange;
  }
  &:hover {
    color: red;
  }
  &:active {
    color: magenta;
  }
}")))
        (testing "with pseudoclasses and nesting"
          (is (= (css-block :active:c--magenta
                            :visited:c--orange
                            :hover:c--red
                            :focus:c--pink
                            :focus:bgc--blue)
                 "{
  &:visited {
    color: orange;
  }
  &:focus {
    color: pink;
    background-color: blue;
  }
  &:hover {
    color: red;
  }
  &:active {
    color: magenta;
  }
}"))))))

;; *****************************************************************************
;; *****************************************************************************
;; *****************************************************************************
  
  (deftest tokenized-strings
    (testing "tokenized strings -> " 
      (testing "strings -> " 
        (testing "2"
          (is (= (css-block "c--red" "bgc--blue")
                 "{\n  color: red;\n  background-color: blue;\n}")))

        (testing "with classname"
          (is (= (css-block :.foo "c--red" "bgc--blue")
                 "{\n  color: red;\n  background-color: blue;\n}"))))

      (testing "string -> "
        (testing "1"
          (is (= (css-block "c--red")
                 "{\n  color: red;\n}")))

        (testing "with css var"
          (is (= (css-block "c--$red-100")
                 "{\n  color: var(--red-100);\n}")))

        (testing "with with multiple properties syntax"
          (is (= (css-block "b--1px:solid:red")
                 "{\n  border: 1px solid red;\n}")))

        (testing "with with multiple properties syntax and css-var"
          (is (= (css-block "b--1px:solid:$red-100")
                 "{\n  border: 1px solid var(--red-100);\n}")))

        (testing "with alternation syntax"
          (is (= (css-block "ff--sans-serif|fantasy")
                 "{\n  font-family: sans-serif, fantasy;\n}")))

        (testing "with alternation syntax and multiple properties syntax"
          (is (= (css-block "text-shadow--5px:5px:10px:red|-5px:-5px:10px:blue")
                 "{\n  text-shadow: 5px 5px 10px red, -5px -5px 10px blue;\n}"))))))


;; *****************************************************************************
;; *****************************************************************************
;; *****************************************************************************
  

  (deftest map-args
    (testing "map args ->"
      (testing "1 entry"
        (is (= (css-block {:c :red})
               "{\n  color: red;\n}")))

      (testing "2 entries"
        (is (= (css-block {:c   :red
                           :mie :1rem})
               "{\n  color: red;\n  margin-inline-end: 1rem;\n}")))

      
      (testing "1 entry, with css calc"
        (is (= (css-block {:w "calc((100vh - (var(--navbar-height) * (2 + (6 / 2)))) * 1)"})
               "{
  width: calc((100vh - (var(--navbar-height) * (2 + (6 / 2)))) * 1);
}")))

      (testing "with psdeudoclass ->"
        (testing "1 entry"
          (is (= (css-block {:last-child:c :red})
                 "{\n  &:last-child {\n    color: red;\n  }\n}")))
        (testing "2 entries"
          (is (= (css-block {:last-child:c  :red
                             :first-child:c :blue})
                 "{\n  &:last-child {\n    color: red;\n  }\n  &:first-child {\n    color: blue;\n  }\n}")))
        (testing "1 entry, nested"
          (is (= (css-block {:last-child {:c   :red
                                          :bgc :blue}})
                 "{\n  &:last-child {\n    color: red;\n    background-color: blue;\n  }\n}")))
        (testing "1 entries, grouped"
          (is (= (css-block {:>p:c :red}
                            {:>p:bgc :blue})
                 "{
  &>p {
    color: red;
    background-color: blue;
  }
}"))))

      (testing "with psdeudoelement ->"
        (testing "1 entry"
          (is (= (css-block {:before:content "\"⌫\"" })
                 "{
  &::before {
    content: \"⌫\";
  }
}")))
        (testing "2 entries"
          (is (= (css-block {:before:content "\"⌫\""
                             :after:content  "\"⌫\""})
                 "{
  &::before {
    content: \"⌫\";
  }
  &::after {
    content: \"⌫\";
  }
}")))
        (testing "1 entry, nested"
          (is (= (css-block {:last-child {:c   :red
                                          :bgc :blue}})
                 "{\n  &:last-child {\n    color: red;\n    background-color: blue;\n  }\n}")))
        (testing "1 entries, grouped"
          (is (= (css-block {:>p:c :red}
                            {:>p:bgc :blue})
                 "{
  &>p {
    color: red;
    background-color: blue;
  }
}"))))
      (testing "with compound data attribute selectors ->"
        (testing "1 entry, with nesting"
          (is (= (css-block {"[data-foo-bar-sidenav][aria-expanded=\"true\"]"
                             {:>.sidenav-menu-icon:d  :none
                              :>.sidenav-close-icon:d :inline-flex
                              :>ul:h                  "calc((100vh - (var(--navbar-height) * 2)) * 1)"
                              :h                      :fit-content
                              :o                      1} })
                 "{
  &[data-foo-bar-sidenav][aria-expanded=\"true\"] {
    height: fit-content;
    opacity: 1;
    &>.sidenav-menu-icon {
      display: none;
    }
    &>.sidenav-close-icon {
      display: inline-flex;
    }
    &>ul {
      height: calc((100vh - (var(--navbar-height) * 2)) * 1);
    }
  }
}")))
        (testing "1 entry, with nesting, ancestor selector"
          (is (= (css-block {"[data-foo-bar-sidenav][aria-expanded=\"true\"] &"
                             {:>.sidenav-menu-icon:d  :none
                              :>.sidenav-close-icon:d :inline-flex
                              :>ul:h                  "calc((100vh - (var(--navbar-height) * 2)) * 1)"
                              :h                      :fit-content
                              :o                      1} })
                 "{
  [data-foo-bar-sidenav][aria-expanded=\"true\"] & {
    height: fit-content;
    opacity: 1;
    &>.sidenav-menu-icon {
      display: none;
    }
    &>.sidenav-close-icon {
      display: inline-flex;
    }
    &>ul {
      height: calc((100vh - (var(--navbar-height) * 2)) * 1);
    }
  }
}"))))))

;; *****************************************************************************
;; *****************************************************************************
;; *****************************************************************************
  
  (deftest vector-args
    (testing "vector ->"
      (testing "1 entry"
        (is (= (css-block [:c :red])
               "{\n  color: red;\n}")))


      (testing "2 entries"
        (is (= (css-block [:c   :red] [:mie :1rem])
               "{\n  color: red;\n  margin-inline-end: 1rem;\n}")))

      
      (testing "1 entry, with css calc"
        (is (= (css-block [:w "calc((100vh - (var(--navbar-height) * (2 + (6 / 2)))) * 1)"])
               "{
  width: calc((100vh - (var(--navbar-height) * (2 + (6 / 2)))) * 1);
}")))


      (testing "with psdeudoclass ->"

        (testing "1 entry"
          (is (= (css-block [:last-child:c :red])
                 "{\n  &:last-child {\n    color: red;\n  }\n}")))

        (testing "2 entry"
          (is (= (css-block [:last-child:c  :red]
                            [:first-child:c :blue])
                 "{
  &:last-child {
    color: red;
  }
  &:first-child {
    color: blue;
  }
}")))

        (testing "1 entry, nested"
          (is (= (css-block [:last-child {:c   :red
                                          :bgc :blue}])
                 "{\n  &:last-child {\n    color: red;\n    background-color: blue;\n  }\n}")))
        
        (testing "1 entry, double nesting"
          (is (= (css-block [:hover {:bgc :blue
                                     :>p  {:c   :teal
                                           :bgc :gray}}])
                 "{
  &:hover {
    background-color: blue;
    &>p {
      color: teal;
      background-color: gray;
    }
  }
}")))
        

        (testing "2 entries, double nesting and grouping"
          (is (= (css-block [:hover {:bgc :blue
                                     :>p  {:c   :teal
                                           :bgc :gray}}]
                            [:hover:c :yellow])
                 "{
  &:hover {
    background-color: blue;
    color: yellow;
    &>p {
      color: teal;
      background-color: gray;
    }
  }
}")))) ))


;; *****************************************************************************
;; *****************************************************************************
;; *****************************************************************************
  

  (deftest css-rule-macro

    (testing "tokenized keyword"
      (is (= (css-rule "p" :c--red)
             "p {\n  color: red;\n}")))

    (testing "tokenized keywords"
      (is (= (css-rule "p" :c--red :bgc--blue)
             "p {\n  color: red;\n  background-color: blue;\n}")))

    (testing "tokenized keyword with classname" 
      (is (= (css-rule "p" :.foo :c--red :bgc--blue)
             "p {\n  color: red;\n  background-color: blue;\n}"))) )



;; *****************************************************************************
;; *****************************************************************************
;; *****************************************************************************
  

;;  (deftest at-rules-bad-names
;;     (testing "at-rules-bad-names  ->"

;;       ;; This test should print a warning to terminal
;;       (testing "bad at-name"
;;         (is (= (css-rule "font-face"
;;                          {:font-family "Trickster"
;;                           :src         "local(Trickster), url(\"trickster-COLRv1.otf\") format(\"opentype\") tech(color-COLRv1)"})
;;                nil)))
      
;;       ;; This test should print a warning to terminal
;;       (testing "bad at-keyframes anme"
;;         (is (= (css-rule "@keyframes "
;;                          [:from {:color :blue}]
;;                          [:to {:color :red}])
;;                nil)))

;;       ;; This test should print a warning to terminal
;;       (testing "bad at-keyframe arg"
;;         (is (= (css-rule "@keyframes blue-to-red"
;;                          [:froms {:color :blue}]
;;                          [:to {:color :red}])
;;                nil))))
;; (callout {:type :info
;;             :label "[kushi.css.core-test]"
;;             :padding-top 1}
;;            (bling [:bold "NOTE:"] "\n"
;;                   "The above tests should print several warning blocks.\n"
;;                   "This is to be expected, as several tests are being\n"
;;                   "run with a malformed calls to functions/macros, which\n"
;;                   "return nil, but issue a user-facing warning about\n"
;;                   "what went wrong."))
  
;;   ) 

  (deftest at-rules
    (testing "at-rules  ->"

      (testing "@font-face"
        (is (= (css-rule "@font-face"
                         {:font-family "Trickster"
                          :src         "local(Trickster), url(\"trickster-COLRv1.otf\") format(\"opentype\") tech(color-COLRv1)"})
"@font-face {
  font-family: Trickster;
  src: local(Trickster), url(\"trickster-COLRv1.otf\") format(\"opentype\") tech(color-COLRv1);
}")))


      (testing "@keyframes with from to"
        (is (= (css-rule "@keyframes blue-to-red"
                         [:from {:color :blue}]
                         [:to {:color :red}])
               "@keyframes blue-to-red {
  from {
    color: blue;
  }
  to {
    color: red;
  }
}")))
      

      (testing "@keyframes with percentages"
        (is (= (css-rule "@keyframes yspinner"
                         [:0% {:transform "rotateY(0deg)"}]
                         [:100% {:transform "rotateY(360deg)"}])
               "@keyframes yspinner {
  0% {
    transform: rotateY(0deg);
  }
  100% {
    transform: rotateY(360deg);
  }
}")))


      (testing "@supports with single nested css ruleset"
        (is (= (css-rule "@supports not (color: oklch(50% .37 200))"
                         (css-rule ".element" {:color :#0288D7}))
               "@supports not (color: oklch(50% .37 200)) {
  .element {
    color: #0288D7;
  }
}")))
      

      (testing "@supports with two nested css rulesets"
        (is (= (css-rule "@supports not (color: oklch(50% .37 200))"
                         (css-rule ".element" {:color :#0288D7})
                         (css-rule ".element2" {:color :#0288D0}))
               "@supports not (color: oklch(50% .37 200)) {
  .element {
    color: #0288D7;
  }
  .element2 {
    color: #0288D0;
  }
}")))))


;; *****************************************************************************
;; *****************************************************************************
;; *****************************************************************************
  

  (deftest css-custom-properties
    (testing "CSS custom properties ->"

      ;; Subject to change
      (testing "css-vars"
        (is (= 
             (let [my-var1 "blue"
                   my-var2 "yellow"]
               (css-vars my-var1 my-var2))
             "--my-var1: blue;--my-var2: yellow;")))

      ;; Subject to change
      (testing "css-vars-map"
        (is (= 
             (let [my-var1 "blue"
                   my-var2 "yellow"]
               (css-vars-map my-var1 my-var2))
             {"--my-var1" "blue" "--my-var2" "yellow"})))

      ;; Subject to change
      (testing "local bindings in css-block"
        (is (= (css-block [:c "`my-var1`"] [:bgc "`my-var2`"])
               "{
  color: var(--_my-var1);
  background-color: var(--_my-var2);
}")))

      (testing "css-vars in css-block"
        (is (= (css-block :c--$my-var1 :bgc--$my-var2)
               "{
  color: var(--my-var1);
  background-color: var(--my-var2);
}")))
      
      
      ))

  

  ) ;; end of `(do ...)`

