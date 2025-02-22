;; A namespace for testing out random things
;; Used with `lein test-refresh`

(ns kushi.css.sandbox
  (:require [clojure.test :refer :all]
            [fireworks.core :refer [? !? ?> !?>]]
            [bling.core :refer [bling callout]]
            [kushi.css.defs]
            [kushi.core :refer [css-block-data
                                    css-block
                                    css-rule
                                    css
                                    ?css
                                    sx
                                    ?sx
                                    defcss
                                    ?defcss
                                    css-vars
                                    css-vars-map
                                    lightning-opts
                                    lightning]]
            [kushi.css.specs :as specs]
            [clojure.spec.alpha :as s]
            [clojure.walk :as walk]
            ;; [taoensso.tufte :as tufte :refer [p profile]]
            [kushi.css.defs :as defs]))



#_(?defcss "@font-face"
  {:font-family "FiraCodeRegular"
   :font-weight "400"
   :font-style "normal"
  ;;  :src "url(../fonts/FiraCode-Regular.woff)"
   :src "local('Trickster'),
         url('trickster-COLRv1.otf') format('opentype') tech(color-COLRv1),
         url('trickster-outline.otf') format('opentype'),
         url('trickster-outline.woff') format('woff')"})


;; -----------------------------------------------------------------------------
;; Profiling setup
;; -----------------------------------------------------------------------------

;; (tufte/add-basic-println-handler! {})

;; (profile ; Profile any `p` forms called during body execution
;;   {} ; Profiling options; we'll use the defaults for now
;;   (dotimes [_ 100000]
;;     (p :a (+ 1 1))
;;     (p :b (reduce + 0 [1 1]))))

;; -----------------------------------------------------------------------------

;; (println (s/conform ::specs/sx-args 
;;                     '(:c--red
;;                       222
;;                       :bgc--blue
;;                       :.gold )
;;                     ))

;; (? specs/css-selector-re #_(s/explain ::specs/css-selector "p"))
;; (? (s/explain ::specs/css-selector "p"))

#_(def my-runtime-class-binding "foo")

#_(?   (css-block {:c  :blue
                 :>p {:c   :red
                      :bgc :blue}}))



#_(s/explain ::specs/style-vec [:>p {:c   :red
                                   :bgc :blue}])


#_(s/explain ::specs/pseudo-element-and-string [:before:content "\"⌫\""])




;; (? (css-block {"[data-foo-bar-sidenav][aria-expanded=\"true\"] &"
;;                {:>.sidenav-menu-icon:d  :none
;;                 :>.sidenav-close-icon:d :inline-flex
;;                 :>ul:h                  "calc((100vh - (var(--navbar-height) * 2)) * 1)"
;;                 :h                      :fit-content
;;                 :o                      1} }))


;; (? (css-rule "@font-face" :c--blue))

;; (at-rule "supports not (color: oklch(50% .37 200))"
;;            (css-rule ".element" {:color :#0288D7}))

;; (css-rule 9 {:color :#0288D1})

#_(? (at-rule "@font-face"
            {:font-family "Trickster"
             :src         "local(Trickster), url(\"trickster-COLRv1.otf\") format(\"opentype\") tech(color-COLRv1)"}))


;; (!? (css-block 
;;     {:font-family "Trickster"
;;      :src         "local(Trickster), url(\"trickster-COLRv1.otf\") format(\"opentype\") tech(color-COLRv1),"}))


#_(? (css-block
    ;; :hover:c--blue
    ;; :>a:hover:c--red
    ;; :_a:hover:c--gold ; The "_" gets converted to " "
    ;; :.bar:hover:c--pink
    ;; :before:fw--bold
    ;; :after:mie--5px
    ;; {"~a:hover:c" :blue} ; Vector is used as "~" is not valid in a keyword
    ;; {"nth-child(2):c" :red} ; Vector is used as "(" and ")" are not valid in keywords
    [:before:content "\"⌫\""]

    #_{"nav[data-foo-bar-sidenav][aria-expanded=\"true\"] &"
       {:>.sidenav-menu-icon:d  :none
        :>.sidenav-close-icon:d :inline-flex
        :>ul:h                  "calc((100vh - (var(--navbar-height) * 2)) * 1)"
        :o                      1}}

    #_{:>p {:hover {:c   :blue
                    :td  :underline
                    :bgc :yellow
                    :_a  {:c   :purple
                          :td  :none
                          :bgc :pink}
                    }}}

    ;; :hover:c--red
    ;; :active:c--red
    ;; :lg:dark:hover:c--orange
    ;; :lg:dark:hover:>p:hover:c--black
    ;;  :lg:dark:c--black
    ;;  :hover:c--red
    ;;  :dark:c--white
    ;;  :dark:hover:c--hotpink
    ;;  :lg:dark:hover:c--yellow
    ;;  :lg:dark:hover:>div.foo:c--silver
    ;; [:hover {:bgc :blue
    ;;          :>p  {:c   :teal
    ;;                :bgc :gray}}]
    ;; [:hover:c :yellow]
    ;; {:>p :5px}
    ;; {:>p :10px}
    ;; [:hover {:c :red
    ;;          :bgc :blue}]
    

    ;; ["@media (min-width: 30em) and (max-width: 50em)" 
    ;;  {:c :red}]
    

    ;; ["@media screen and print" 
    ;;  {:c :red}]
    
    ;; {:>a {:c   :green
    ;;       :bgc :orange}
    ;;  :>b {:c   :silver
    ;;       :bgc :gold}}
    ;; {:hover:>a {:c   :blue
    ;;             :bgc :orange}
    ;;  :hover:>b {:c   :black
    ;;             :bgc :gold}}
;;  [:>p {:c   :red
;;        :bgc :blue}]
;;  [:>d {:c   :yellow
;;        :bgc :teal}]
    ))

;; (?defcss ".bang" :c--blue)

;; (? (at-rule* "@supports not (color: oklch(50% .37 200))"
;;             (css-rule ".element" {:color :#0288D7})
;;             (css-rule ".element2" {:color :#0288D0})))

;; (? (at-rule*2 "@supports not (color: oklch(50% .37 200))"
;;               3 #_[".element" {:color :#0288D7}]
;;               [".element2" {:color :#0288D0}]))

;; (? (at-rule*2 "@keyframes slider"
;;               [:froms {:transform "translateX(0%)"
;;                       :opacity   0}]
;;               [:to {:transform "translateX(100%)"
;;                     :opacity   1}]))

;; (? (css-rule "@keyframes slider"
;;   [:from {:transform "translateX(0%)"
;;           :opacity   0}]
;;   [:to {:transform "translateX(100%)"
;;         :opacity   1}]))

;; (? (css-rule "@bang"
;;              (css-rule ".b" :c--blue)
;;              (css-rule ".c" :c--red)
;;              ))


;; (?css :hover:c--blue
;;     :>a:hover:c--red
;;     :_a:hover:c--gold ; The "_" gets converted to " "
;;     :.bar:hover:c--pink
;;     :before:fw--bold
;;     :after:mie--5px
;;     ["~a:hover:c" :blue] ; Vector is used as "~" is not valid in a keyword
;;     ["nth-child(2):c" :red] ; Vector is used as "(" and ")" are not valid in keywords
;;     [:before:content "\"⌫\""])

;;  (? (s/explain ::specs/keyframe-percentage "50%"))
;;  (? (s/valid? ::specs/keyframe ["50%" {:color :blue}]))

#_(css-rule ".gold"
          {:>p {:c   :red
               :bgc :blue}
           :>div {:c :orange :b :1px:solid:black}}
          :c--blue)

;; (def my-var1 "blue")
;; (def my-var2 "yellow")

;; (? (css-vars my-var1 my-var2))
;; (? (css-vars-map my-var1 my-var2))

;; (? (sx :c--blue :.some-other-class))

;; (? (let [my-var1 "blue"       
;;          my-var2 "yellow"]
;;        (css-vars my-var1 my-var2)))

;; #_(? (let [my-var1 "blue"       
;;          my-var2 "yellow"]
;;   {
;;    :style (css-vars my-var1 my-var2)
;;   ;;  :class (css :c--$my-var1 :bgc--$my-var2)
;;    }))

;; (? :pp
;;    (css-rule
;;     "p"
;;     :c--red
;;     :bgc--blue
;;     #_(when false :.bold)))

;; (? #_:pp (css :.foo :p--10px :c--red))


;; (? (css :.foo--bang :c--red))
;; (? (re-find specs/classname-with-dot-re ".pc--gold"))



;; (def result 
;; ".foo {
;;   color: red;
;;   & .bar {
;;     color: green;
;;   }
;; }")

;; (? (into [] {:a "a" :b "b" :c "c"}))

;; (? (:out (shell {:in result :out :string} "npx" "lightningcss" "--minify" "--targets" ">= 0.25%")))

#_(css-block
;;  12
 :c--red
 :bgc--blue
 :p--10
 {:>p {:bgc :blue
       :b   :1px:solid:black
       :p   :10px}})

#_(? (-> (css-rule ".foo"
                 :c--red
                 :_.bar:c--green)
       (lightning {:minify false})))

