(ns kushi.ui.tooltip.core
  (:require
   [clojure.string :as string]
   [kushi.core :refer (sx defclass defclass-with-override token->ms merge-attrs)]
   [kushi.ui.dom :as dom]))

(defclass
 ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-pseudo-tooltip
  {:before:z-index                           9999
   :before:display                           :none
   :hover:before:display                     :block
   :before:width                             :auto
   :before:height                            :fit-content
   :before:position                          :absolute
   :before:text-transform                    :$tooltip-text-transform
   :before:color                             :$tooltip-color
   :dark:before:color                        :$tooltip-color-inverse
   :before:bgc                               :$tooltip-background-color
   :dark:before:bgc                          :$tooltip-background-color-inverse
   :before:padding-block                     :$tooltip-padding-block
   :before:padding-inline                    :$tooltip-padding-inline
   :before:border-radius                     :$tooltip-border-radius
   :before:font-size                         :$tooltip-font-size
   :before:font-weight                       :$tooltip-font-weight
   :before:white-space                       :pre

   :hover:before:transition-property         :opacity
   :hover:before:transition-duration         :$tooltip-transition-duration
   :hover:before:transition-timing-function  :linear
   :hover:before:transition-delay            :$tooltip-transition-delay

   :after:display                          :none
   :hover:after:display                    :block
   :after:position                         :absolute
   :after:z-index                          :9998
   :after:w                                0
   :after:h                                0
   :hover:after:transition-property        :opacity
   :hover:after:transition-duration        :$tooltip-transition-duration
   :hover:after:transition-timing-function :linear
   :hover:after:transition-delay           :$tooltip-transition-delay

   :focus-visible:before:opacity             1
   :focus-visible:after:opacity            1
   :transition                              :color:200ms:linear})

(defclass-with-override
  ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-pseudo-tooltip-hidden
  {:before:display       :none
   :hover:before:display :none
   :after:display        :none
   :hover:after:display  :none})

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-pseudo-tooltip-revealed
  {:before:display       :block
   :hover:before:display :block
   :after:display        :block
   :hover:after:display  :block})

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-pseudo-tooltip-right-pointing-arrow
  {:after:border-left      :$_tooltip-arrow-border
   :dark:after:border-left :$_tooltip-arrow-border-inverse
   :after:border-top       :$_tooltip-arrow-border-trans
   :after:border-bottom    :$_tooltip-arrow-border-trans
   :after:content          "\" \""})

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-pseudo-tooltip-left-pointing-arrow
  {:after:border-right      :$_tooltip-arrow-border
   :dark:after:border-right :$_tooltip-arrow-border-inverse
   :after:border-top        :$_tooltip-arrow-border-trans
   :after:border-bottom     :$_tooltip-arrow-border-trans
   :after:content           "\" \""})

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-pseudo-tooltip-down-pointing-arrow
  {:after:border-top      :$_tooltip-arrow-border
   :dark:after:border-top :$_tooltip-arrow-border-inverse
   :after:border-left     :$_tooltip-arrow-border-trans
   :after:border-right    :$_tooltip-arrow-border-trans
   :after:content         "\" \""})

(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-pseudo-tooltip-up-pointing-arrow
  {:after:border-bottom      :$_tooltip-arrow-border
   :dark:after:border-bottom :$_tooltip-arrow-border-inverse
   :after:border-left        :$_tooltip-arrow-border-trans
   :after:border-right       :$_tooltip-arrow-border-trans
   :after:content            "\" \""})


(defclass ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-pseudo-tooltip-tlc
  {:before:beer      0
   :before:top       :0%
   :before:left      :0%
   :before:transform "translate(calc(-100% - calc(var(--tooltip-offset) * 0.71)), calc(-100% - calc(var(--tooltip-offset) * 0.71)))"
   :after:content    "\"\""})

(defclass
  ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-pseudo-tooltip-tl
  {:before:top       :0%
   :before:left      :0%
   :before:transform "translate(0%, calc(-100% - var(--tooltip-offset) - var(--tooltip-arrow-depth)))"
   :after:top        :0%
   :after:left       :0%
   :after:transform  "translate(calc(0% + 0.333px + var(--tooltip-border-radius) + var(--tooltip-arrow-x-offset) ), calc(-100% - var(--tooltip-offset)))"})

(defclass
  ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-pseudo-tooltip-t
  {:before:top       :0%
   :before:left      :50%
   :before:transform "translate(-50%, calc(-100% - var(--tooltip-offset) - var(--tooltip-arrow-depth)))"
   :after:top        :0%
   :after:left       :50%
   :after:transform  "translate(-50%, calc(-100% - 0.333px - var(--tooltip-offset)))"})

(defclass
  ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-pseudo-tooltip-tr
  {:before:top       :0%
   :before:left      :100%
   :before:transform "translate(-100%, calc(-100% - var(--tooltip-offset) - var(--tooltip-arrow-depth)))"
   :after:top        :0%
   :after:left       :100%
   :after:transform  "translate(calc(-100% - 0.333px - var(--tooltip-border-radius) - var(--tooltip-arrow-x-offset) ), calc(-100% - var(--tooltip-offset)))"})

(defclass
  ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-pseudo-tooltip-trc
  {:before:besr      0
   :before:top       :0%
   :before:left      :100%
   :before:transform "translate(calc(0% + calc(var(--tooltip-offset) * 0.71)), calc(-100% - calc(var(--tooltip-offset) * 0.71)))"
   :after:content    "\"\""})

(defclass
 ^{:kushi/chunk :kushi/kushi-ui-defclass}
 kushi-pseudo-tooltip-rt
  {:before:top            :0%
   :before:left           :100%
   :before:transform      "translate(calc(0% + var(--tooltip-offset) + var(--tooltip-arrow-depth)), 0%)"
   :after:top           :0%
   :after:left          :100%
   :after:transform     "translate(calc(0% + 0.333px + var(--tooltip-offset)), calc(0% + var(--tooltip-border-radius) + var(--tooltip-arrow-y-offset)))"})

(defclass kushi-pseudo-tooltip-r
 ^{:kushi/chunk :kushi/kushi-ui-defclass}
  {:before:top        :50%
   :before:left       :100%
   :before:transform  "translate(calc(0% + var(--tooltip-offset) + var(--tooltip-arrow-depth)), -50%)"
   :after:top       :50%
   :after:left      :100%
   :after:transform "translate(calc(0% + 0.333px + var(--tooltip-offset)), -50%)"})

(defclass
  ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-pseudo-tooltip-rb
  {:before:top       :100%
   :before:left      :100%
   :before:transform "translate(calc(0% + var(--tooltip-offset) + var(--tooltip-arrow-depth)), -100%)"
   :after:top        :100%
   :after:left       :100%
   :after:transform  "translate(calc(0% + 0.333px + var(--tooltip-offset)), calc(-100% - var(--tooltip-border-radius) - var(--tooltip-arrow-y-offset)))"})

(defclass
 ^{:kushi/chunk :kushi/kushi-ui-defclass}
 kushi-pseudo-tooltip-brc
  {:before:bssr      0
   :before:top       :100%
   :before:left      :100%
   :before:transform "translate(calc(0% + calc(var(--tooltip-offset) * 0.71)), calc(0% + calc(var(--tooltip-offset) * 0.71)))"
   :after:content  "\"\""})

(defclass
  ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-pseudo-tooltip-br
  {:before:top       :100%
   :before:left      :100%
   :before:transform "translate(-100%, calc(0% + var(--tooltip-offset) + var(--tooltip-arrow-depth)))"

   :after:top        :100%
   :after:left       :100%
   :after:transform  "translate(calc(-100% - 0.333px - var(--tooltip-border-radius) - var(--tooltip-arrow-x-offset) ), calc(0% + var(--tooltip-offset)))"})

(defclass
  ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-pseudo-tooltip-b
  {:before:top       :100%
   :before:left      :50%
   :before:transform "translate(-50%, calc(0% + var(--tooltip-offset) + var(--tooltip-arrow-depth)))"
   :after:top        :100%
   :after:left       :50%
   :after:transform  "translate(-50%, calc(0% + 0.333px + var(--tooltip-offset)))"})

(defclass
  ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-pseudo-tooltip-bl
  {:before:top       :100%
   :before:left      :0%
   :before:transform "translate(0%, calc(0% + var(--tooltip-offset) + var(--tooltip-arrow-depth)))"

   :after:top        :100%
   :after:left       :0%
   :after:transform  "translate(calc(0% + 0.333px + var(--tooltip-border-radius) + var(--tooltip-arrow-x-offset) ), calc(0% + var(--tooltip-offset)))"})

(defclass
  ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-pseudo-tooltip-blc
  {:before:bser      0
   :before:top       :100%
   :before:left      :0%
   :before:transform "translate(calc(-100% - calc(var(--tooltip-offset) * 0.71)), calc(0% + calc(var(--tooltip-offset) * 0.71)))"
   :after:content    "\"\""})

(defclass
  ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-pseudo-tooltip-lb
  {:before:top       :100%
   :before:left      :0%
   :before:transform "translate(calc(-100% - var(--tooltip-offset) - var(--tooltip-arrow-depth)), -100%)"
   :after:top        :100%
   :after:left       :0%
   :after:transform  "translate(calc(-100% - 0.333px - var(--tooltip-offset)), calc(-100% - var(--tooltip-border-radius) - var(--tooltip-arrow-y-offset)))"})

(defclass
  ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-pseudo-tooltip-l
  {:before:top       :50%
   :before:left      :0%
   :before:transform "translate(calc(-100% - var(--tooltip-offset) - var(--tooltip-arrow-depth)), -50%)"
   :after:top        :50%
   :after:left       :0%
   :after:transform  "translate(calc(-100% - 0.333px - var(--tooltip-offset)), -50%)"})

(defclass
  ^{:kushi/chunk :kushi/kushi-ui-defclass}
  kushi-pseudo-tooltip-lt
  {:before:top       :0%
   :before:left      :0%
   :before:transform "translate(calc(-100% - var(--tooltip-offset) - var(--tooltip-arrow-depth)), 0%)"
   :after:top        :0%
   :after:left       :0%
   :after:transform  "translate(calc(-100% - 0.333px - var(--tooltip-offset)), calc(0% + var(--tooltip-border-radius) + var(--tooltip-arrow-y-offset)))"})


(def non-logicals
  #{"bottom" "right" "top" "left" "corner"})

(def placement-values-auto
  {:inline #{"inline-end" "inline-start" "inline-auto"}
   :block #{"block-end" "block-start" "block-auto"}})

(def placement-values
  {:inline #{"inline-end" "inline-start"}
   :block #{"block-end" "block-start"}})

(defn- p2? [s kw]
  (or (= "center" s)
      (contains? (kw placement-values) s)))

(defn- p1? [s kw]
  (contains? (kw placement-values) s))


(defn valid-placement [[p1 p2 corner]]
  (let [p1-inline?         (p1? p1 :inline)
        p1-block?          (p1? p1 :block)
        p2-inline?         (p2? p2 :inline)
        p2-block?          (p2? p2 :block)
        inline-then-block? (and p1-inline? p2-block?)
        block-then-inline? (and p1-block? p2-inline?)
        p1+p2              (or inline-then-block? block-then-inline?)]
    (if (or (and (= corner "corner") p1+p2)
            p1+p2
            (and (nil? corner)
                 (nil? p2)
                 (or p1-inline? p1-block?)))
      [p1 p2 corner]
      ["block-start"])))


(def placement-by-kw
  {:tlc [0 0 -100 -100 :- :-]
   :tl  [0 0 0 -100 :0 :-]
   :t   [0 50 -50 -100 :0 :-]
   :tr  [0 100 -100 -100 :0 :-]
   :trc [0 100 0 -100 :+ :-]

   :rtc [0 100 0 -100 :+ :-]
   :rt  [0 100 0 0 :+ :0]
   :r   [50 100 0 -50 :+ :0]
   :rb  [100 100 0 -100 :+ :0]
   :rbc [100 100 0 0 :+ :+]

   :brc [100 100 0 0 :+ :+]
   :br  [100 100 -100 0 :0 :+]
   :b   [100 50 -50 0 :0 :+]
   :bl  [100 0 0 0 :0 :+]
   :blc [100 0 -100 0 :- :+]

   :lbc [100 0 -100 0 :- :+]
   :lb  [100 0 -100 -100 :- :0]
   :l   [50 0 -100 -50 :- :0]
   :lt  [0 0 -100 0 :- :0]
   :ltc [0 0 -100 -100 :- :-]})

(def by-logic
  (let [m {"block-start"  "t"
           "inline-end"   "r"
           "block-end"    "b"
           "inline-start" "l"
           "center"       nil
           true         "c"
           false        nil}]
    {:ltr m
     :rtl (assoc m "inline-end" "l" "inline-start" "r")}))

(defn logical-placement
  [{:keys [ltr? placement]}]
  (let [placement      (some-> placement
                               (string/trim)
                               (string/split #" "))
        [p1 p2 corner] (valid-placement placement)
        corner?        (= "corner" corner)
        p1             (or p1 "block-start")
        p2             (or p2 "center")
        placement      [p1 p2 corner?]
        ret            (->> placement
                            (map #(get ((if ltr? :ltr :rtl) by-logic) %))
                            string/join
                            keyword)]
   ret))

(def corner-placements
  {:rtc                 :trc
   :right-top-corner    :trc
   :rbc                 :brc
   :right-bottom-corner :brc
   :lbc                 :blc
   :left-bottom-corner  :blc
   :ltc                 :ltc
   :left-top-corner     :ltc})

(defn- user-placement
  "Expects a string"
  [s]
  (let [kw* (some-> s name keyword)
        kw (or (kw* corner-placements) kw*)]
    (or
     (when (contains? placement-by-kw kw) kw)
     (let [parts (string/split s #"-")]
       (when (every? #(contains? non-logicals %) parts)
         (let [kw (some->> parts
                           (map first)
                           string/join
                           keyword)]
           (when (contains? placement-by-kw kw)
             kw))))
     (logical-placement {:ltr?      (= (dom/writing-direction) "ltr")
                         :placement s}))))

(defn- text-type [x]
  (cond (and (string? x) (not (string/blank? x)))
        :string
        (and (keyword? x) (not (string/blank? (name x))))
        :keyword
        (number? x)
        :number
        (seq x)
        :multi-line-string))

(defn formatted-text [x]
  (when-let [text-type (text-type x)]
    (let [text* (case text-type
                  :multi-line-string
                  (string/join "\\a" (map str x))
                  x)
          text  (str "\"" text* "\"")]
      text)))

(defn tooltip-attrs
  {:desc ["Tooltips provide additional context when hovering or clicking on an element. They are intended to be ephemeral, containing only non-interactive content."
          :br
          :br
          "Specifying placement in various ways can be done with the `:-placement` option."
          :br
          :br
          "These tooltips are implemented with a `::after` and `::before` pseudo-elements and therefore differ from most of the other primitive component Kushi offers."
          "The element being tipped must receive an attributes map that is a result of passing a map of options to `kushi.ui.tooltip.core/tooltip-attrs`."
          "You can compose this map to an existing elements attributes map using the pattern:"
          :br
          :br "`(merge-attrs (sx ...) (tooltip-attrs {...}))`"
          :br
          :br
          "If the element that you are tipping is already using either of the `::before` or `::after` pseudo-elements, you will need to wrap it in a container (perhaps a `<span>`) and apply the tooltip attrs to that wrapper."
          :br
          :br
          "The element being tipped must also have a css `position` value such as `relative` set, so that the absolutely-positioned tooltip pseudo-element will end up with the desired placement."
          "Tooltips can be custom styled via the following tokens in your theme:"
          :br
          :br
          ;; TODO add documentation for each token
          :br "`:$tooltip-arrow-depth`"
          :br "`:$tooltip-arrow-x-offset`"
          :br "`:$tooltip-arrow-y-offset`"
          :br "`:$tooltip-padding-inline`"
          :br "`:$tooltip-padding-block`"
          :br "`:$tooltip-border-radius`"
          :br "`:$tooltip-font-size`"
          :br "`:$tooltip-font-weight`"
          :br "`:$tooltip-color`"
          :br "`:$tooltip-background-color`"
          :br "`:$tooltip-color-inverse`"
          :br "`:$tooltip-background-color-inverse`"
          :br "`:$tooltip-text-transform`"
          :br "`:$tooltip-offset`"
          :br
          :br
          "If you want supply the value of any of the above tokens ala-carte, use the following pattern."
          :br
          :br
          "`(merge-attrs (sx :$tooltip-offset--5px ...) (tooltip-attrs {...}))`"]
   :opts '[{:name    text
            :pred    #(or (string? %) (keyword? %))
            :default nil
            :desc    "Required. The text to display in the tooltip"}
           {:name    placement
            :pred    keyword?
            :default :top
            :desc    [
                      "You can use single keywords to specify the exact placement of the tooltip:"
                      :br
                      "`:top-left-corner`"
                      :br
                      "`:top-left`"
                      :br
                      "`:top`"
                      :br
                      "`:top-right`"
                      :br
                      "`:top-right-corner`"
                      :br
                      "`:right-top-corner`"
                      :br
                      "`:right-top`"
                      :br
                      "`:right`"
                      :br
                      "`:right-bottom`"
                      :br
                      "`:right-bottom-corner`"
                      :br
                      :br
                      "You can also use shorthand versions of the single keywords:"
                      :br
                      "`:tlc`"
                      :br
                      "`:tl`"
                      :br
                      "`:t`"
                      :br
                      "`:tr`"
                      :br
                      "`:trc`"
                      :br
                      "`:rtc`"
                      :br
                      "`:rt`"
                      :br
                      "`:r`"
                      :br
                      "`:rb`"
                      :br
                      "`:rbc`"
                      :br
                      :br
                      "If you care about the tooltip placement respecting writing direction and/or document flow, you can use a string of up to 3 logical properties, separated by spaces:"
                      :br
                      "`\"inline-end block-start\"`"
                      :br
                      "`\"inline-end block-start corner\"`"
                      :br
                      "`\"inline-start center\"`"
                      :br
                      "`\"inline-end center\"`"
                      :br
                      "`\"block-start center\"`"
                      :br
                      "`\"block-end center\"`"
                      :br
                      "`\"block-end inline-start\"`"
                      :br]}
           {:name    arrow?
            :pred  boolean?
            :default true
            :desc    "Setting to false will not render a directional arrow with the tooltip."}
           {:name    reveal-on-click?
            :pred  boolean?
            :default false
            :desc    "Setting to true will reveal the tooltip on click. It will not display on hover."}
           {:name    reveal-on-click-duration
            :pred    #(or (integer? %) (% :infinite))
            :default 2000
            :desc    "When `:-reveal-on-click?` is true, this milliseconds value will control the duration of the tooltip's appearance."}
           {:name    text-on-click
            :pred    string?
            :default nil
            :desc    ["The tooltip text, after the tipped element has been clicked."
                      "This is not related, and has no effect if you are using the `:-reveal-on-click` option."]}
           {:name    text-on-click-duration
            :pred  int?
            :default 2000
            :desc    "When `:-text-on-click` is utilized, this milliseconds value will control the duration of the `:-text-on-click` value being used as the tooltip text."}]}
  [{text                     :-text
    text-on-click            :-text-on-click
    text-on-click-duration   :-text-on-click-duration
    placement                :-placement
    arrow?                   :-arrow?
    reveal-on-click?         :-reveal-on-click?
    reveal-on-click-duration :-reveal-on-click-duration
    :or                      {reveal-on-click?         false
                              placement                :top
                              arrow?                   true}}]

  (when-let [text (formatted-text text)]
    (let [text-on-click   (formatted-text text-on-click)
          placement       (if-not (or (string? placement)
                                      (keyword? placement))
                            :top
                            placement)
          placement-kw    (user-placement (name placement))
          placement-class (str  "kushi-pseudo-tooltip-" (name placement-kw))
          corner?         (string/ends-with? (name placement-kw) "c")
          dir-by-letter   {"l" "right"
                           "r" "left"
                           "b" "up"
                           "t" "down"}
          dir             (get dir-by-letter (-> placement-kw name first) nil)
          dir-class       (when (and arrow? (not corner?))
                            (str "kushi-pseudo-tooltip-" dir "-pointing-arrow"))]

      (if reveal-on-click?
        (sx :.kushi-pseudo-tooltip-hidden
            :.kushi-pseudo-tooltip
            [:before:content text]
            {:class    [placement-class dir-class]
             :role     :button
             :on-click (fn [e]
                         (let [node     (-> e .-currentTarget)
                               class    "kushi-pseudo-tooltip-revealed"
                               duration (if (pos-int? reveal-on-click-duration)
                                          reveal-on-click-duration
                                          (token->ms :$tooltip-reveal-on-click-duration))]
                           (dom/toggle-class node class)
                           (when-not (= reveal-on-click-duration :infinite)
                             (js/setTimeout #(dom/remove-class node class)
                                            duration))))})
        (merge-attrs
         (sx :.kushi-pseudo-tooltip
             {:class    [placement-class dir-class]
              :style    {:before:content                                      text
                         :&.kushi-pseudo-tooltip-text-on-click:before:content text-on-click
                         :hover:after:display                               (when (false? arrow?) :none)}})
         (when text-on-click
           {:on-click (fn [e]
                        (let [node     (-> e .-currentTarget)
                              class    "kushi-pseudo-tooltip-text-on-click"
                              duration (if (pos-int? text-on-click-duration)
                                         text-on-click-duration
                                         (token->ms :$tooltip-reveal-on-click-duration))]
                          (when-not (dom/has-class? node class)
                            (dom/add-class node class)
                            (js/setTimeout #(dom/remove-class node class) duration))))}))))))
