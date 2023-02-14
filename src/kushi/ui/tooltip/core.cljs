(ns kushi.ui.tooltip.core
  (:require
   [clojure.string :as string]
   [kushi.core :refer (sx defclass)]
   [kushi.ui.dom :as dom]))

(defclass kushi-pseudo-tooltip
  ^{:kushi/chunk :kushi/kushi-ui-defclass}
  {:hover:after:z-index        9999
   :hover:after:opacity        1
   :hover:after:width          :auto
   :hover:after:height         :fit-content
   :hover:after:position       :absolute
   :hover:after:text-transform :$tooltip-text-transform
   :hover:after:color          :$tooltip-color
   :dark:hover:after:color     :$tooltip-color-inverse
   :hover:after:bgc            :$tooltip-background-color
   :dark:hover:after:bgc       :$tooltip-background-color-inverse
   :hover:after:padding-block  :$tooltip-padding-block
   :hover:after:padding-inline :$tooltip-padding-inline
   :hover:after:border-radius  :$tooltip-border-radius
   :hover:after:font-size      :$tooltip-font-size
   :hover:after:top            :$_kushi-ui-pseudo-tooltip-top
   :hover:after:left           :$_kushi-ui-pseudo-tooltip-left
   :hover:after:transform      :$_kushi-ui-pseudo-tooltip-transform
   :hover:after:white-space    :pre

   :focus-visible:after:z-index        9999
   :focus-visible:after:opacity        1
   :focus-visible:after:width          :auto
   :focus-visible:after:height         :fit-content
   :focus-visible:after:position       :absolute
   :focus-visible:after:text-transform :$tooltip-text-transform
   :focus-visible:after:color          :$tooltip-color
   :dark:focus-visible:after:color     :$tooltip-color-inverse
   :focus-visible:after:bgc            :$tooltip-background-color
   :dark:focus-visible:after:bgc       :$tooltip-background-color-inverse
   :focus-visible:after:padding-block  :$tooltip-padding-block
   :focus-visible:after:padding-inline :$tooltip-padding-inline
   :focus-visible:after:border-radius  :$tooltip-border-radius
   :focus-visible:after:font-size      :$tooltip-font-size
   :focus-visible:after:top            :$_kushi-ui-pseudo-tooltip-top
   :focus-visible:after:left           :$_kushi-ui-pseudo-tooltip-left
   :focus-visible:after:transform      :$_kushi-ui-pseudo-tooltip-transform
   :focus-visible:after:white-space    :pre
   })

(defclass kushi-pseudo-tooltip-hidden
  ^{:kushi/chunk :kushi/kushi-ui-defclass}
  {:after:display        :none})

(defclass kushi-pseudo-tooltip-revealed
  ^{:kushi/chunk :kushi/kushi-ui-defclass}
  {:after:display        :block
   :after:z-index        9999
   :after:opacity        1
   :after:width          :auto
   :after:height         :fit-content
   :after:position       :absolute
   :after:text-transform :$tooltip-text-transform
   :after:color          :$tooltip-color
   :dark:after:color     :$tooltip-color-inverse
   :after:bgc            :$tooltip-background-color
   :dark:after:bgc       :$tooltip-background-color-inverse
   :after:padding-block  :$tooltip-padding-block
   :after:padding-inline :$tooltip-padding-inline
   :after:border-radius  :$tooltip-border-radius
   :after:font-size      :$tooltip-font-size
   :after:top            :$_kushi-ui-pseudo-tooltip-top
   :after:left           :$_kushi-ui-pseudo-tooltip-left
   :after:transform      :$_kushi-ui-pseudo-tooltip-transform
   :after:white-space    :pre})


(def non-logicals
  #{"bottom" "right" "top" "left" "corner"})


(def placement-values
  {:inline #{"inline-end" "inline-start" "inline-auto"}
   :block #{"block-end" "block-start" "block-auto"}})


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
      ["block-auto"])))

(defn- translate-with-calc [coord offset corner?]
  (let [offset  (name offset)
        pct     (str coord "%")
        ret     (if (= offset "0")
                  pct
                  (let [tt* "var(--tooltip-offset)"
                        tt (if corner? (str "calc(" tt* " * 0.71)") tt*)]
                    (str "calc(" pct " " offset " " tt ")")))]
    ret))

(defn- translate-with-offset
  [x y offset-x offset-y corner?]
  (let [x (translate-with-calc x offset-x corner?)
        y (translate-with-calc y offset-y corner?)]
    (str "translate(" x ", " y ")")))

(def placement-by-kw
  {:tlc [0 0 -100 -100 :- :-]
   :tl  [0 0 0 -100 :0 :-]
   :t   [0 50 -50 -100 :0 :-]
   :tr  [0 100 -100 -100 :0 :-]
   :trc [0 100 0 -100 :+ :-]

   :rtc [0 100 0 -100 :+ :+]
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


(defn logic-placement
  [{:keys [ltr? tb lr user-placement]}]
  (let [placement      (some-> user-placement
                               (string/trim)
                               (string/split #" "))

        [p1 p2 corner] (valid-placement placement)

        corner?        (= "corner" corner)

        p1*            (or p1 "block-auto")

        p1             (case p1*
                         "block-auto"
                         (if (= tb :top) "block-end" "block-start")
                         "inline-auto"
                         (if (= lr :left)
                           (if ltr? "inline-end" "inline-start")
                           p1*)
                         p1*)

        p2             (or p2
                           (cond
                             (p1? p1 :block)
                             (if (= lr :left)
                               (if ltr? "inline-start" "inline-end")
                               (if ltr? "inline-end" "inline-start"))
                             (p1? p1 :inline)
                             (if (= tb :top) "block-start" "block-end")
                             ))

        placement      [p1 p2 corner?]

        wtf            (->> placement
                            (map #(get ((if ltr? :ltr :rtl) by-logic) %))
                            string/join
                            keyword)]
    wtf))


(defn user-placement [s]
  (let [kw (some-> s name keyword)]
    (when (string? s)
      (or
       (when (contains? placement-by-kw kw)
         kw)
       (let [parts (string/split s #"-")]
         (when (every? #(contains? non-logicals %) parts)
           (let [kw (some->> parts
                             (map first)
                             string/join
                             keyword)]
             (when (contains? placement-by-kw kw)
               kw))))))))


(defn set-tooltip-position! [%]
  (let [node          (dom/et %)
        ltr?          (= (.-direction (js/window.getComputedStyle node)) "ltr")
        [tb lr]       (dom/screen-quadrant node)
        user-supplied (.getAttribute node "data-kushi-ui-pseudo-tooltip-placement")
        wtf           (or (user-placement user-supplied)
                          (logic-placement {:ltr?           ltr?
                                            :tb             tb
                                            :lr             lr
                                            :user-placement user-supplied}))
        corner?       (string/ends-with? (name wtf) "c")
        [tt-top
         tt-left
         tt-x
         tt-y
         offset-x
         offset-y]    (wtf placement-by-kw)]
    (dom/set-style! node
                    "--_kushi-ui-pseudo-tooltip-top" (str tt-top "%"))
    (dom/set-style! node
                    "--_kushi-ui-pseudo-tooltip-left" (str tt-left "%"))
    (dom/set-style! node
                    "--_kushi-ui-pseudo-tooltip-transform"
                    (translate-with-offset tt-x tt-y offset-x offset-y corner?))
    ))


(defn tooltip-attrs
  {:desc ["Tooltips provide additional context when hovering or clicking on an element."
          :br
          :br
          "Tooltips in Kushi have no arrow indicator and are placed automatically depending on the parent element's relative postition in the viewport."
          :br
          "Specifying placement in various ways can be done with the `:-placement` option."
          :br
          :br
          "Tooltips can be styled via the following tokens in your theme:"
          :br
          "`:--tooltip-padding-inline`"
          :br
          "`:--tooltip-padding-block`"
          :br
          "`:--tooltip-border-radius`"
          :br
          "`:--tooltip-font-size`"
          :br
          "`:--tooltip-color`"
          :br
          "`:--tooltip-background-color`"
          :br
          "`:--tooltip-color-inverse`"
          :br
          "`:--tooltip-background-color-inverse`"
          :br
          "`:--tooltip-text-transform`"
          :br
          "`:--tooltip-offset`"
          :br
          :br
          "These tooltips are implemented with an `::after` pseudo-element and therefore differ from most of the other primitive component Kushi offers."
          "The element being tipped must receive an attributes map that is a result of passing a map of options to `kushi.ui.tooltip.core/tooltip-attrs`."
          "You can compose this map to an existing elements attributes map using the pattern `(merge-attrs (sx ...) (tooltip-attrs ...))`."
          :br
          :br
          "The element being tipped must also have a css `postition` value such as `relative` set, so that the absolutely-positioned tooltip pseudo-element will end up with the desired placement."
          :br
          :br
          ]
   :opts '[{:name    text
            :type    :string
            :default nil
            :desc    "Required. The text to display in the tooltip"}
           {:name    placement
            :type    #{string keyword}
            :default "block-auto"
            :desc    ["Accepts a string of up to 3 logical properties, separated by spaces:"
                      :br
                      "`\"block-auto\"`"
                      :br
                      "`\"block-auto inline-end\"`"
                      :br
                      "`\"block-auto inline-end corner\"`"
                      :br
                      "`\"inline-end\"`"
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
                      :br
                      "If you don't care about the tooltip placement respecting writing direction and/or document flow, you can also use single keywords specifying exact placement:"
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
                      ]}
           {:name    reveal-on-click?
            :type    :boolean
            :default nil
            :desc    "Setting to true will reveal the tooltip on click."}
           {:name    reveal-on-click-duration
            :type    :integer
            :default 2000
            :desc    "When `:-reveal-on-click?` is true, this milliseconds value will control the duration of the tooltip's appearance."}]}
  [{text                     :-text
    placement                :-placement
    reveal-on-click?         :-reveal-on-click?
    reveal-on-click-duration :-reveal-on-click-duration
    :or                      {reveal-on-click-duration 2000
                              placement "block-end center"}
    :as m}]
  (when-let [tooltip-text-type (cond (and (string? text)
                                          (not (string/blank? text)))
                                     :string
                                     (and (seq text)
                                          (every? string? text))
                                     :multi-line-string)]
    (let [text*     (case tooltip-text-type
                      :multi-line-string
                      (string/join "\\a" text)
                      text)
          text      (str "\"" text* "\"")

          wtf       (user-placement (name placement))
          corner?   (string/ends-with? (name wtf) "c")
          [tt-top
           tt-left
           tt-x
           tt-y
           offset-x
           offset-y] (wtf placement-by-kw)
          translate-with-offset (translate-with-offset tt-x tt-y offset-x offset-y corner?)]

      (js/console.log [corner?
                       tt-top
                       tt-left
                       tt-x
                       tt-y
                       offset-x
                       offset-y])
      (if reveal-on-click?
        (sx :.kushi-pseudo-tooltip-hidden
            {
            ;;  :on-mouse-enter                         set-tooltip-position!
            ;;  :on-focus                               set-tooltip-position!
             :on-click                               (fn [e]
                                                       (let [node  (-> e .-currentTarget)
                                                             class "kushi-pseudo-tooltip-revealed"]
                                                         (dom/add-class node class)
                                                         (js/setTimeout
                                                          #(dom/remove-class node class)
                                                          reveal-on-click-duration)))
             :data-kushi-ui-pseudo-tooltip-text      true
             :data-kushi-ui-pseudo-tooltip-placement placement
             :style                                  {:after:content text}})
        (sx :.kushi-pseudo-tooltip
            {
            ;;  :on-mouse-enter                         set-tooltip-position!
            ;;  :on-focus                               set-tooltip-position!
             :data-kushi-ui-pseudo-tooltip-text      true
             :data-kushi-ui-pseudo-tooltip-placement placement
             :style                                  {:hover:after:content         text
                                                      :focus-visible:after:content text
                                                      :hover:after:top             (str tt-top "%")
                                                      :hover:after:left            (str tt-left "%")
                                                      :hover:after:transform        translate-with-offset}})))))
