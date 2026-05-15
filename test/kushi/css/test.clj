(ns kushi.css.test
  (:require [clojure.test :refer :all]
            [clojure.edn :as edn]
            [clojure.data :as data]
            [clojure.walk :refer [postwalk]]
            [kushi.ui.variants]
            [kushi.css.sandbox]
            [fireworks.core :refer [? !? ?> !?> pprint]]
            [fireworks.sample :as sample]
            [bling.core :as bling :refer [print-bling bling callout point-of-interest]]
            [bling.explain :refer [explain-malli]]
            [bling.util :as util]
            [bling.sample]
            [bling.banner :refer [banner]]
            [bling.fonts.isometric-1 :refer [isometric-1]]
            [kushi.core :refer [css-block-data
                                css-block
                                css-rule
                                css-rule*
                                css
                                ?css
                                sx
                                sx2
                                ?sx
                                defcss
                                ?defcss
                                ?defcolorway
                                css-vars
                                css-vars-map
                                lightning-opts
                                lightning]]
            [clojure.string :as string]
            [kushi.css.build.surfaces :as surfaces]
            [kushi.css.specs :as specs]
            [kushi.css.schemas :as schemas]
            [kushi.cssprops :as cssprops]
            [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.walk :as walk]
            [kushi.css.defs :as defs]
            [edamame.core :as e :refer [parse-string parse-string-all]]
            [kushi.css.specs :as kushi-specs]
            [kushi.css.build.utility-classes :as utility-classes]
            [kushi.util :refer [maybe
                                keyed
                                nameable?
                                as-str
                                kw->cssvar2
                                when->
                                when->>
                                cssval->ks
                                printcss]]
            [kushi.colors2 :refer [oklch-colors]]
            [taoensso.tufte :as tufte :refer [p profile]]
            [me.flowthing.pp :as pp]
            [kushi.css.shorthand :as shorthand]
            [malli.core :as malli]))


(fireworks.core/config! {:scalar-max-length           33
                         :scalar-depth-1-max-length   33
                         :scalar-mapkey-max-length    33
                         :single-line-coll-max-length 20})

;; TODO
;; outlaw stack shorthand such as `:1px:solid:$border-color` on value side?
;; outlaw stack on prop side? Yes. Maybe use :media/sm for @media.
(def myccc
  (css-rule ".bang"
            {
            ;;  :w                :$foo
            ;;  :h                '(calc (+ 2 3))
            ;;  ;; :border           [[:1px :solid :$border-color]]
            ;;  :border           :1px:solid:$border-color
            ;;  :box-shadow       [[0 0 :10px '(max :8px (calc (* 2 :3px))) :blue]
            ;;                     [0 0 :10px :12px :blue]]
            ;;  :font-family      ["Arial" "Helvetica" :$fallback-font-stack]
            ;;  :background-color :blue
            ;;  "p"                 {:color :orange}

             ;; TODO figure out how to correct this in hydrate 
            ;;  :p:c                 '(light-dark :orange
            ;;                                    :pink)

            :p>li:hover             {:color :gold}

            ;; "@media min-width(680px)" {:color :orange}
             }))

(printcss myccc)

#_(? (s/valid? ::specs/css-prop-stack :hover))



;; (? (calc (+ :$my-val (- 2 (/ 10 3)))))


;; (? (s/valid? ::specs/vector-of-cssfns
;;              [(css-linear-gradient "to bottom" "#0000 50%" :$transparent-black-09)
;;               (linear-gradient "to bottom" "#0000 50%" [:$classic-trim-color :80%])]))


;; (? (s/valid? ::specs/cssfn (quote (calc ($ :2px :3px)))))

;; (s/explain ::specs/quoted-cssfn-list (list 'quote (list 'calc ('$ :2px :3px))))

;; (? (css-rule*
;;     ".foo"
;;     {:width '(calc (+ :2px :3px))}
;;     nil
;;     nil))

;; (? (cssval->ks "linear-gradient(to bottom, #0000 50%, var(--transparent-black-09)), linear-gradient(to bottom, #0000 50%, var(--classic-trim-color-dark) 80%)"))

;; ;; with this:
;; `(cssval->ks "linear-gradient(to bottom, #0000 50%, var(--classic-trim-color-dark) 80%)")`

;; ;; i would expect this:
;; ```clojure
;; '(linear-gradient
;;  [:to :bottom]
;;  [:#0000 :50%]
;;  [:$classic-trim-color-dark :80%])
;; ```

;; ;; and with this (two layered gradients)
;; ("linear-gradient(to bottom, #0000 50%, var(--transparent-black-09)), linear-gradient(to bottom, #0000 50%, var(--classic-trim-color-dark) 80%)")

;; ;; i would expect this:
;; ```clojure
;; ['(linear-gradient
;;    [:to :bottom]
;;    [:#0000 :50%]
;;    :$transparent-black-09)
;;  '(linear-gradient
;;    [:to :bottom]
;;    [:#0000 :50%]
;;    [:$classic-trim-color-dark :80%])]
;; ```

{:hey      1
 :bnaasfsd "sadfasdf"}

(fireworks.core/config! {:single-line-coll-max-length 79
                         :quote-lists?                true})

(defn- x [coll]
  (->> coll
       kushi.util/css-str-prop-values->structured-syntax))

#_(? :+
     (kushi.util/legacy-sx-call->sx2
      '(merge-attrs
        (sx ".ks-callout"
            :position--relative
            :d--flex
            :flex-direction--row
            :jc--c
            :ai--c
            :w--100%
            :gap--$icon-enhanceable-gap
            [:--padding-block-start "calc(var(--callout-padding-block) * var(--callout-padding-block-start-reduction-ratio, 1))"]
            [:--padding-block-end   :$callout-padding-block]
            [:--padding-inline      :$callout-padding-inline]
            :pi--$_padding-inline
            :pbs--$_padding-block-start
            :pbe--$_padding-block-end)

        {:aria-busy  loading
         :aria-label (when loading "loading")}

        (when stroke-width
          {:style {"--_stroke-width" (name stroke-width)}})

        (when-not (false? inert) {:data-ks-inert ""})
        (when loading {:data-ks-ui-spinner ""})

        &attrs)))



;; (? :+ (x ["width"                      "fit-content",
;;           "transition-duration"        "var(--transition-fast)",
;;           "transition-property"        "all",
;;           "font-family"                "var(--code-font-stack)",
;;           "font-weight"                "var(--code-font-weight)",
;;           "height"                     "fit-content",
;;           "white-space"                "nowrap",
;;           "padding-inline"             "var(--code-padding-inline)",
;;           "transition-timing-function" "cubic-bezier(0, 0, 1, 1)",
;;           "font-size"                  "var(--code-font-size)",
;;           "background-color"           "var(--code-background-color)",
;;           "padding-block"              "var(--code-padding-block)",
;;           "border-radius"              "var(--code-border-radius, var(--rounded-sm-absolute))",
;;           ;;  "border-width"               "var(--code-border-width, 1px)",
;;           ;;  "border-color"               "var(--code-border-color, var(--neutral-200))",
;;           ;;  "border-style"               "var(--code-border-style, solid)",
;;           "color"                      "var(--code-color)"]))

;; (defcss "body"
;;   {:font-family                :$sans-serif-font-stack
;;    :font-weight                :$body-font-weight
;;    :color                      :$foreground-color
;;    :background-color           :$background-color
;;    :transition-property        [:background-color :color]
;;    :transition-duration        :$transition-fast
;;    :transition-timing-function :$timing-linear-curve
;;    :overflow-y                 :scroll})

;; (defcss ".dark, body.dark, .dark body"
;;   {:background-color :$background-color-dark-mode
;;    :color            :$foreground-color-dark-mode})

;; (defcss "code"
;;   {:width                      :fit-content
;;    :transition-duration        :$transition-fast
;;    :transition-property        :all
;;    :font-family                :$code-font-stack
;;    :font-weight                :$code-font-weight
;;    :height                     :fit-content
;;    :white-space                :nowrap
;;    :padding-inline             :$code-padding-inline
;;    :transition-timing-function '(cubic-bezier 0 0 1 1)
;;    :font-size                  :$code-font-size
;;    :background-color           :$code-background-color
;;    :padding-block              :$code-padding-block
;;    :border-radius              :$code-border-radius||$rounded-sm-absolute
;;    :color                      :$code-color})




#_(println
   (css-rule
    ".foo"
    {;; :filter      '(drop-shadow :2px :4px (oklch yellow 200 / 0.3))
     ;;  :line-height      '(abs (+ 2 :$my-num))


     ;; Fix
     :.gold {:width :30px}

     :background-image '(linear-gradient "180deg"
                                         :transparent
                                         [:transparent :15% :$bang]
                                         [(oklch :$convex-shadow-lightness
                                                 :$convex-shadow-chroma
                                                 :$colorway-hue)])

     :font-family      ["Arial" "Helvetica" "fantasy"]
     :box-shadow       [[:2px '(calc (+ :2px :3px)) 0 '(oklch :30% 0.3 44 / 0.8)]
                        [:2px :4px 0 :blue]
                        [:2px :6px 0 :$yellow-500]]}))


#_(printcss
   (css-rule
    ".foo"
    {;; :hover:--convex-shadow-lightness-shift (calc (+ :$convex-shadow-lightness-shift-base :$lightness-shift))

     ;; calc* fn usage, expects a list
     ;;  :background-color (calc '(- :$convex-shadow-lightness-shift-base :$lightness-shift))


     ;;  :bgi "linear-gradient(to bottom,#0000 50%,var(--transparent-black-09)),linear-gradient(to bottom,#0000 50%, var(--classic-trim-color) 80%)"

     ;; :box-shadow 2px 2px 0 red, 4px 4px 0 orange, 6px 6px 0 gold

     ;; :box-shadow [[:2px '(calc ($ :2px :3px)) 0 '(oklch :30% 0.3 44 0.8)] [:2px :4px 0 :blue] :2px:6px:0:$yellow-500]

     :width       '(calc (+ :2px :3px))

     :line-height '(min (+ 1.5 2) :$line-height2)

     :box-shadow  [[:2px (calc '(+ :2px :3px)) 0 (oklch :30% 0.3 44 0.8)] [:2px :4px 0 :blue] :2px:6px:0:$yellow-500]

     :bgi         ['(linear-gradient "to bottom" "#0000 50%" :$transparent-black-09)
                   '(linear-gradient "to bottom" "#0000 50%" [:$classic-trim-color :80%])]
     ;; :mask-mode [:alpha :luminance]
     ;; :animation-iteration-count [:infinite 3 1]

     ;; :hover {:bgi [(linear-gradient "to bottom" "#fff 50%" :$transparent-black-09)
     ;;               (linear-gradient "to bottom" "#fff 50%" [:$classic-trim-color :80%])]}


     ;; :background-color '(oklch :$convex-shadow-lightness :$convex-shadow-chroma :$colorway-hue 0.4)

     ;;  ;;  :border :1px:solid:okm-purple-400

     ;; :background-image '(linear-gradient "180deg"
     ;;                                     [:transparent]
     ;;                                     [:transparent :15%]
     ;;                                     [(oklch :$convex-shadow-lightness
     ;;                                             :$convex-shadow-chroma 
     ;;                                             :$colorway-hue)])
     }))

#_(? :+ (=
         (!? :+ (css-rule* ".foo"
                           [{:hover:--convex-shadow-lightness-shift "calc(var(--convex-shadow-lightness-shift-base) + var(--lightness-shift))"
                             :background-image                      "linear-gradient(180deg, transparent, transparent 15%, oklch(var(--convex-shadow-lightness) var(--convex-shadow-chroma) var(--colorway-hue)))"}]
                           nil
                           nil))
         (!? :+ (css-rule* ".foo"
                           [{:hover:--convex-shadow-lightness-shift (calc :$convex-shadow-lightness-shift-base + :$lightness-shift)
                             :background-image                      (linear-gradient "180deg"
                                                                                     [:transparent]
                                                                                     [:transparent :15%]
                                                                                     [(oklch :$convex-shadow-lightness
                                                                                             :$convex-shadow-chroma
                                                                                             :$colorway-hue)])}]
                           nil
                           nil))))


;; (kushi.core/defui3
;;   boxer
;;   {}
;;   [& args]
;;   [:p (sx ".ks-boxer" {:stroke-width :1px}) "hi"])

;; (? (boxer))

#_(? (malli/validate [:or
                      [:float {:min 0.0
                               :max 1.0}]
                      [:enum 0 1]
                      [:and
                       [:or :string :keyword]
                       [:fn
                        kushi.ui.variants/percentage?]]
                      :symbol]
                     :80%))


;; Validation

;; 1) Issue warning if stroke prop other than `:stroke-width` is supplied (without stroke-width)
;; 2) Same for shadow ^^^

;; Still need to do jams at runtime? maybe not as you could mark thing
;; Or if yes you could wrap in a runtime-checking function?


#_(?sx ".he"
       {:color       :red
        :at-media/sm {:color :blue}})

#_(?sx ".he"
       {:color                                          :red
        "@supports(color: color-mix(in oklch, red, red))" {:color :blue}})

#_(let [s                               "neutral"
        convex-light-mode-grad          #(str "linear-gradient(180deg, transparent, transparent 15%, " % ")")
        convex-light-mode-shadow-color  #(str "color-mix(in oklch, transparent, var(--background-color-" s "-hard" % ") var(--convex-shadow-opacity, 10%))")
        css-fn                          (fn [fname & args] (str fname "(" (string/join ", " args) ")"))
        convex-light-mode-shadow-color+ #(color-mix "in oklch"
                                                    ["transparent"]
                                                    [(str "var(--background-color-" s "-hard" % ")")
                                                     "var(--convex-shadow-opacity, 10%)"])]

    ;; (? (convex-light-mode-shadow-color+ "-3"))
    (? (linear-gradient "45deg" [:$blue-250||$yellow-500||gold|| :50%] :red))

    #_(? (= (? (convex-light-mode-shadow-color "-3"))
            (? (convex-light-mode-shadow-color+ "-3")))))


;;  (println (css-rule* 
;;   "[data-ks-colorway= \"neutral\"]"
;;   [{"[data-ks-surface= \"convex\"]" 
;;     {"@supports(color: color-mix(in oklch, red, red))" {:bgi       "linear-gradient(180deg, transparent, transparent 15%, color-mix(in oklch, transparent, var(--background-color-neutral-hard-3) var(--convex-shadow-opacity, 10%)))",
;;                                                         :hover:bgi "linear-gradient(180deg, transparent, transparent 15%, color-mix(in oklch, transparent, var(--background-color-neutral-hard-4) var(--convex-shadow-opacity, 10%)))"},
;;      :bgi                                            "linear-gradient(180deg, transparent, transparent 15%, var(--background-color-neutral-soft-3))",
;;      :hover:bgi                                      "linear-gradient(180deg, transparent, transparent 15%, var(--background-color-neutral-soft-4))",
;;      }}]
;;    nil
;;    nil
;;   )),


;; '["["
;;   [:non-digit]
;;   [:zero-or-more [:or :a-z :A-Z :0-1 "_" "-"]]
;;   [:optional 
;;    "="
;;    [:alt
;;     :anything-in-double-quotes ;; ["\"" [:anything-but "\""] "\""]
;;     :anything-in-single-quotes ;; ["\'" [:anything-but "\'"] "\'"]
;;     ]]
;;   "]"]

;; (? (re-find #"^\[\D[a-zA-Z0-1_-]*(?:=(?:\"[^\"]+\"|\'[^\']+\'))?\]$"
;;             "[data-ks-ui='\"']"))


;; (def re #"^\[data-[a-zA-Z0-1_-]+(?:=\"?[a-z]+\")?\]$")
;; (? (re-find re ".ks-foo"))
;; (? (re-find re "[data-ks-ui]"))

;; (? (s/explain-data ::specs/supplied-selector ".ks-icon"))

#_(? (css
      ".ks-button"
      :.relative
      :m--10px
      nil nil))

#_(tufte/add-basic-println-handler! {})

#_(profile ; Profile any `p` forms called during body execution
   {}      ; Profiling options; we'll use the defaults for now
   (dotimes [_ 1000]
     (p :map-indexed (seqp bits styles))
     (p :map-indexed2 (seqp2 bits styles))))


;; (!? (css-rule*
;; ".foo"
;;  [{"@supports(color: color-mix(in oklch, currentColor, transparent 40%))"
;;    {:--bordercolor :blue}}]

;;  #_[{"@media(color: color-mix(in oklch, currentColo, yeah))"
;;    {:--bordercolor :blue}}]
;;  nil nil))



;; (!? (css-rule* ".foo" [:ai--$ai] nil nil))
;; (!? (css-rule* ".foo" [:aj--$ai] nil nil))

#_(def sample-css

    "
/* End of things from legacy, kushi.ui.basetheme/ui -------------------------*/


 .kushi-pane-r>.kushi-pane-arrow {

  border-top-width: 0!important;
  border-right-width: 0!important;
}

.kushi-pane-rt>.kushi-pane-arrow {
  border-top-width: 0!important;
  border-right-width: 0!important;
}

/* Beginning of legacy, hsl-based theming system ----------------------------*/

.kushi-pane-mounting {
  visibility: hidden;
}

/* End of whatever -------------------------*/
 ")
(def sample-css2
  ":root {
  --gray-hue: 0;
  --gray-50: hsl(var(--gray-hue), 0%, 98%);
  --gray-100: hsl(var(--gray-hue), 0%, 95%);
  --gray-150: hsl(var(--gray-hue), 0%, 93%);
  --gray-200: hsl(var(--gray-hue), 0%, 91%);
  --gray-250: hsl(var(--gray-hue), 0%, 88%);
  --gray-300: hsl(var(--gray-hue), 0%, 85%);
  --gray-350: hsl(var(--gray-hue), 0%, 81%);
  --gray-400: hsl(var(--gray-hue), 0%, 77%);
  --gray-450: hsl(var(--gray-hue), 0%, 72.5%);
 }")

(defn css->kushi
  "Converts non-nested css to a vector of kushi.core/defcss calls."
  [css-str]
  (let [;; Remove comments first
        css-str (string/replace css-str #"\/\*[^\*]+\*\/" "")
        sels    (-> css-str
                    (string/split #"\{[^\}]+\}")
                    (->> (map #(string/trim %))))
        rules   (->>  css-str
                      (re-seq #"\{[^\}]+\}")
                      (map #(-> %
                                (string/replace #"^\{|\}$" "")
                                (string/trim)
                                (string/split #"\:|\;")
                                (->> (map (fn [s] (string/trim s))))))
                      (map #(apply array-map %)))
        am      (apply array-map (interleave sels rules))]
    (reduce-kv (fn [acc k v] (conj acc (list 'defcss k v))) [] am)))

;; (? :pp (css->kushi sample-css2))
;; #_(? :pp {:scalar-mapkey-max-length 79}
;;  #_(css->kushi kushi.css.build.css-legacy/css-reset)
;;    #_(css->kushi kushi.css.build.css-legacy/design-tokens)
;;    (css->kushi kushi.css.build.css-legacy/kushi-ui-theming) 
;;  )

;;         [ 
;;              kushi.css.build.css-legacy/css-reset
;;              kushi.css.build.css-legacy/design-tokens
;;              kushi.css.build.css-legacy/kushi-ui-theming ]


#_(? (->  sample-css
          (->> (str "\n\""))
          (string/replace #"\{" "\"{")
          (string/replace #"\}" "}\"")
          (string/replace #"\"$" "")
          (string/replace #"  ([a-z_-]+): +([^\;]+)\;"
                          #(let [[_ prop val] %]
                             (str ":" prop " \"" val "\"")))

          (str "]")
          (->> (str "["))
          edn/read-string
          (->> (map-indexed (fn [i v] (if (even? i) (string/trim v) v))))
          (->> (apply array-map))))


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

;; (? (css-rule* ".wtf" [[">*:not([data-ks-playground-sidenav]):pi" :1.25rem]] nil nil))
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
      [:cursor--pointer
       :td--underline
       :tup--under
       [:tdc "color-mix(in oklch, currentColor 40%, transparent)"]
       [:hover:tdc :currentColor]]
      nil
      nil))
#_(? (css-rule*
      ".kushi-switch"
      [;;  [:hover:bgc :transparent!important]
       :c--red!important
       "bgc--blue!important"
       [:hover:bgc "transparent!important"]
       {:hover:bgc "transparent!important"}]
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
                       :--foreground-color-dark-mode :$neutral-50
                       :--background-color         :white
                       :--background-color-dark-mode :$neutral-1000])
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
                       :--foreground-color-dark-mode :$neutral-50

                       :--background-color         :white
                       :--background-color-dark-mode :$neutral-1000))
                nil nil))

#_(def block
    (css-block {:border-block-end           :$divisor
                :dark:border-block-end      :$divisor-dark-mode
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
                                  :$foreground-color-dark-mode :$neutral-50

                                  :$background-color :white
                                  :$background-color-dark-mode :$neutral-1000)
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
            (is (= (css-block {:before:content "\"⌫\""})
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
                                :o                      1}})
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
                                :o                      1}})
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
}"))))))


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
               "p {\n  color: red;\n  background-color: blue;\n}"))))



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
}")))))) ;; end of `(do ...)`

