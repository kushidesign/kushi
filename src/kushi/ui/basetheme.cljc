(ns ^:dev/always kushi.ui.basetheme
  (:require
   [clojure.string :as string]
   [clojure.set :as set]
   [kushi.specs2 :as specs]
   [kushi.config :refer [user-config]]
   [kushi.ui.tokens :refer [design-tokens]]
   [kushi.ui.utility :refer [utility-classes disabled]]
   [kushi.colors :refer [colors transparent-neutrals]]
   [kushi.color :refer [colors->tokens colors->alias-tokens semantic-aliases]]))

;; TODO - explore more efficient way to transform authored theme code

;; Right now we are using vectors instead of maps to preserve
;; authored "insertion" order

;; Then using the maybe-remove-lights-and-darks fn to remove them
;; after the fact (based on user-config options to elide light or dark styles)

;; Would it be possible to use maps like this and just remove stuff
;; structurally? (like all lights or all darks, or all bordereds, etc),
;; before the base-theme-map is built up?

;; This^^^ approach would necessitate a seperately defined order to
;; eventually re-sort them in the following way the following in nested way:
(comment
  [:neutral :accent :positive :negative :warning]
  [:default :minimal :bordered :filled]
  [:color
   :hover:color
   :active:color

   :background-color
   :background-color:hover:color
   :background-color:hover:color

   :.info:color
   :.info:hover:color
   :.info:neutral:active:color])

(comment
  {:neutral {:light {:default  {:color        1000
                                :hover:color  1000
                                :active:color 1000
                                :...          :...}
                     :minimal  {:... :...}
                     :bordered {:... :...}}
             :dark  {:default  {:... :...}
                     :minimal  {:... :...}
                     :bordered {:... :...}}}

   :accent  {:light {:default  {:color        1000
                                :hover:color  1000
                                :active:color 1000
                                :...          :...}
                     :minimal  {:... :...}
                     :bordered {:... :...}}
             :dark  {:default  {:... :...}
                     :minimal  {:... :...}
                     :bordered {:... :...}}}})


(def variant-values
  [
   ;; Neutral --------------------------------------------------
   ;; default
   :.neutral:color                                  900
   :.neutral:hover:color                            900
   :.neutral:active:color                           900

   :.neutral:background-color                       100
   :.neutral:hover:background-color                 200
   :.neutral:active:background-color                300

   :.neutral.info:color                             900
   :.neutral.info:hover:color                       900
   :.neutral.info:active:color                      900

   :.neutral.info:background-color                  100
   :.neutral.info:hover:background-color            100
   :.neutral.info:active:background-color           100


   ;; minimal
   :.neutral.minimal:color                          900
   :.neutral.minimal:hover:color                    900
   :.neutral.minimal:active:color                   900

   :.neutral.minimal:background-color               :transparent
   :.neutral.minimal:hover:background-color         100
   :.neutral.minimal:active:background-color        200


   ;; bordered
   :.neutral.bordered:color                         900
   :.neutral.bordered:hover:color                   900
   :.neutral.bordered:active:color                  900

   :.neutral.bordered:background-color              :transparent
   :.neutral.bordered:hover:background-color        :transparent
   :.neutral.bordered:active:background-color       :transparent

   :.neutral.bordered:border-color                  900
   :.neutral.bordered:hover:border-color            900
   :.neutral.bordered:active:border-color           900

   :.neutral.bordered.info:background-color         :transparent
   :.neutral.bordered.info:hover:background-color   :transparent
   :.neutral.bordered.info:active:background-color  :transparent

   :.neutral.bordered.info:border-color             900
   :.neutral.bordered.info:hover:border-color       900
   :.neutral.bordered.info:active:border-color      900


   ;; filled
   :.neutral.filled:color                           :white
   :.neutral.filled:hover:color                     :white
   :.neutral.filled:active:color                    :white

   :.neutral.filled:background-color                700
   :.neutral.filled:hover:background-color          800
   :.neutral.filled:active:background-color         9000

   :.neutral.filled.info:background-color           700
   :.neutral.filled.info:hover:background-color     700
   :.neutral-filled.info:active:background-color    700


   ;; NEUTRAL INVERSE
   ;; default
   ".dark .neutral:color"                                  50
   ".dark .neutral:hover:color"                            0
   ".dark .neutral:active:color"                           0

   ".dark .neutral:background-color"                       850
   ".dark .neutral:hover:background-color"                 800
   ".dark .neutral:active:background-color"                800

   ".dark .neutral.info:color"                             50
   ".dark .neutral.info:hover:color"                       50
   ".dark .neutral.info:active:color"                      50

   ".dark .neutral.info:background-color"                  750
   ".dark .neutral.info:hover:background-color"            750
   ".dark .neutral.info:active:background-color"           750

   ;; minimal
   ".dark .neutral.minimal:color"                          150
   ".dark .neutral.minimal:hover:color"                    50
   ".dark .neutral.minimal:active:color"                   0

   ".dark .neutral.minimal:background-color"               :transparent
   ".dark .neutral.minimal:hover:background-color"         850
   ".dark .neutral.minimal:active:background-color"        800

   ;; bordered
   ".dark .neutral.bordered:color"                         150
   ".dark .neutral.bordered:hover:color"                   100
   ".dark .neutral.bordered:active:color"                  50

   ".dark .neutral.bordered:background-color"              :transparent
   ".dark .neutral.bordered:hover:background-color"        :transparent
   ".dark .neutral.bordered:active:background-color"       :transparent

   ".dark .neutral.bordered:border-color"                  150
   ".dark .neutral.bordered:hover:border-color"            100
   ".dark .neutral.bordered:active:border-color"           50

   ".dark .neutral.bordered.info:color"                    100
   ".dark .neutral.bordered.info:hover:color"              100
   ".dark .neutral.bordered.info:active:color"             100

   ".dark .neutral.bordered.info:background-color"         :transparent
   ".dark .neutral.bordered.info:hover:background-color"   :transparent
   ".dark .neutral.bordered.info:active:background-color"  :transparent

   ".dark .neutral.bordered.info:border-color"             50
   ".dark .neutral.bordered.info:hover:border-color"       50
   ".dark .neutral.bordered.info:active:border-color"      50

   ;; filled
   ".dark .neutral.filled:color"                           :black
   ".dark .neutral.filled:hover:color"                     :black
   ".dark .neutral.filled:active:color"                    :black

   ".dark .neutral.filled:background-color"                250
   ".dark .neutral.filled:hover:background-color"          100
   ".dark .neutral.filled:active:background-color"         50

   ".dark .neutral.filled.info:background-color"           250
   ".dark .neutral.filled.info:hover:background-color"     250
   ".dark .neutral-filled.info:active:background-color"    250

   ;; Accent
   :.accent:color                                  700
   :.accent:hover:color                            800
   :.accent:active:color                           900

   :.accent:background-color                       100
   :.accent:hover:background-color                 200
   :.accent:active:background-color                300

   :.accent.info:color                             800
   :.accent.info:hover:color                       800
   :.accent.info:active:color                      800

   :.accent.info:background-color                  100
   :.accent.info:hover:background-color            100
   :.accent.info:active:background-color           100

   :.accent.minimal:color                          650
   :.accent.minimal:hover:color                    800
   :.accent.minimal:active:color                   900

   :.accent.minimal:background-color               :transparent
   :.accent.minimal:hover:background-color         100
   :.accent.minimal:active:background-color        150

   :.accent.bordered:color                         600
   :.accent.bordered:hover:color                   700
   :.accent.bordered:active:color                  800

   :.accent.bordered:background-color              :transparent
   :.accent.bordered:hover:background-color        :transparent
   :.accent.bordered:active:background-color       :transparent

   :.accent.bordered:border-color                  600
   :.accent.bordered:hover:border-color            700
   :.accent.bordered:active:border-color           800

   :.accent.bordered.info:color                    600
   :.accent.bordered.info:hover:color              600
   :.accent.bordered.info:active:color             600

   :.accent.bordered.info:background-color         :transparent
   :.accent.bordered.info:hover:background-color   :transparent
   :.accent.bordered.info:active:background-color  :transparent

   :.accent.bordered.info:border-color             600
   :.accent.bordered.info:hover:border-color       600
   :.accent.bordered.info:active:border-color      600

   :.accent.filled:color                           :white
   :.accent.filled:hover:color                     :white
   :.accent.filled:active:color                    :white

   :.accent.filled:background-color                600
   :.accent.filled:hover:background-color          750
   :.accent.filled:active:background-color         850

   :.accent.filled.info:background-color           600
   :.accent.filled.info:hover:background-color     600
   :.accent-filled.info:active:background-color    600

   ;; ACCENT INVERSE
   ;; default
   ".dark .accent:color"                                  100
   ".dark .accent:hover:color"                            50
   ".dark .accent:active:color"                           50

   ".dark .accent:background-color"                       750
   ".dark .accent:hover:background-color"                 600
   ".dark .accent:active:background-color"                500

   ".dark .accent.info:color"                             100
   ".dark .accent.info:hover:color"                       100
   ".dark .accent.info:active:color"                      100

   ".dark .accent.info:background-color"                  750
   ".dark .accent.info:hover:background-color"            750
   ".dark .accent.info:active:background-color"           750

   ;; minimal
   ".dark .accent.minimal:color"                          300
   ".dark .accent.minimal:hover:color"                    200
   ".dark .accent.minimal:active:color"                   100

   ".dark .accent.minimal:background-color"               :transparent
   ".dark .accent.minimal:hover:background-color"         750
   ".dark .accent.minimal:active:background-color"        850

   ;; bordered
   ".dark .accent.bordered:color"                         300
   ".dark .accent.bordered:hover:color"                   200
   ".dark .accent.bordered:active:color"                  100

   ".dark .accent.bordered:background-color"              :transparent
   ".dark .accent.bordered:hover:background-color"        :transparent
   ".dark .accent.bordered:active:background-color"       :transparent

   ".dark .accent.bordered:border-color"                  300
   ".dark .accent.bordered:hover:border-color"            200
   ".dark .accent.bordered:active:border-color"           100

   ".dark .accent.bordered.info:color"                    300
   ".dark .accent.bordered.info:hover:color"              300
   ".dark .accent.bordered.info:active:color"             300

   ".dark .accent.bordered.info:background-color"         :transparent
   ".dark .accent.bordered.info:hover:background-color"   :transparent
   ".dark .accent.bordered.info:active:background-color"  :transparent

   ".dark .accent.bordered.info:border-color"             300
   ".dark .accent.bordered.info:hover:border-color"       300
   ".dark .accent.bordered.info:active:border-color"      300

   ;; filled
   ".dark .accent.filled:color"                           :black
   ".dark .accent.filled:hover:color"                     :black
   ".dark .accent.filled:active:color"                    :black

   ".dark .accent.filled:background-color"                400
   ".dark .accent.filled:hover:background-color"          250
   ".dark .accent.filled:active:background-color"         100

   ".dark .accent.filled.info:background-color"           400
   ".dark .accent.filled.info:hover:background-color"     400
   ".dark .accent-filled.info:active:background-color"    400

   ;; Pos
   :.positive:color                                  800
   :.positive:hover:color                            900
   :.positive:active:color                           1000

   :.positive:background-color                       100
   :.positive:hover:background-color                 150
   :.positive:active:background-color                250

   :.positive.info:color                             800
   :.positive.info:hover:color                       800
   :.positive.info:active:color                      800

   :.positive.info:background-color                  100
   :.positive.info:hover:background-color            100
   :.positive.info:active:background-color           100


   :.positive.minimal:color                          700
   :.positive.minimal:hover:color                    800
   :.positive.minimal:active:color                   900

   :.positive.minimal:background-color               :transparent
   :.positive.minimal:hover:background-color         100
   :.positive.minimal:active:background-color        150

   :.positive.bordered:color                         700
   :.positive.bordered:hover:color                   800
   :.positive.bordered:active:color                  900

   :.positive.bordered:background-color              :transparent
   :.positive.bordered:hover:background-color        :transparent
   :.positive.bordered:active:background-color       :transparent

   :.positive.bordered:border-color                  650
   :.positive.bordered:hover:border-color            750
   :.positive.bordered:active:border-color           900

   :.positive.bordered.info:color                    700
   :.positive.bordered.info:hover:color              700
   :.positive.bordered.info:active:color             700

   :.positive.bordered.info:background-color         :transparent
   :.positive.bordered.info:hover:background-color   :transparent
   :.positive.bordered.info:active:background-color  :transparent

   :.positive.bordered.info:border-color             650
   :.positive.bordered.info:hover:border-color       650
   :.positive.bordered.info:active:border-color      650

   :.positive.filled:color                           :white
   :.positive.filled:hover:color                     :white
   :.positive.filled:active:color                    :white

   :.positive.filled:background-color                650
   :.positive.filled:hover:background-color          750
   :.positive.filled:active:background-color         850

   :.positive.filled.info:background-color           650
   :.positive.filled.info:hover:background-color     650
   :.positive-filled.info:active:background-color    650

   ;; POSITIVE INVERSE
   ;; default
   ".dark .positive:color"                                  200
   ".dark .positive:hover:color"                            100
   ".dark .positive:active:color"                           50

   ".dark .positive:background-color"                       800
   ".dark .positive:hover:background-color"                 700
   ".dark .positive:active:background-color"                600

   ".dark .positive.info:color"                             200
   ".dark .positive.info:hover:color"                       200
   ".dark .positive.info:active:color"                      200

   ".dark .positive.info:background-color"                  800
   ".dark .positive.info:hover:background-color"            800
   ".dark .positive.info:active:background-color"           800

   ;; minimal
   ".dark .positive.minimal:color"                          350
   ".dark .positive.minimal:hover:color"                    150
   ".dark .positive.minimal:active:color"                   50

   ".dark .positive.minimal:background-color"               :transparent
   ".dark .positive.minimal:hover:background-color"         800
   ".dark .positive.minimal:active:background-color"        700

   ;; bordered
   ".dark .positive.bordered:color"                         350
   ".dark .positive.bordered:hover:color"                   150
   ".dark .positive.bordered:active:color"                  50

   ".dark .positive.bordered:background-color"              :transparent
   ".dark .positive.bordered:hover:background-color"        :transparent
   ".dark .positive.bordered:active:background-color"       :transparent

   ".dark .positive.bordered:border-color"                  350
   ".dark .positive.bordered:hover:border-color"            150
   ".dark .positive.bordered:active:border-color"           50

   ".dark .positive.bordered.info:color"                    350
   ".dark .positive.bordered.info:hover:color"              350
   ".dark .positive.bordered.info:active:color"             350

   ".dark .positive.bordered.info:background-color"         :transparent
   ".dark .positive.bordered.info:hover:background-color"   :transparent
   ".dark .positive.bordered.info:active:background-color"  :transparent

   ".dark .positive.bordered.info:border-color"             350
   ".dark .positive.bordered.info:hover:border-color"       350
   ".dark .positive.bordered.info:active:border-color"      350

   ;; filled
   ".dark .positive.filled:color"                           :black
   ".dark .positive.filled:hover:color"                     :black
   ".dark .positive.filled:active:color"                    :black

   ".dark .positive.filled:background-color"                500
   ".dark .positive.filled:hover:background-color"          350
   ".dark .positive.filled:active:background-color"         250

   ".dark .positive.filled.info:background-color"           500
   ".dark .positive.filled.info:hover:background-color"     500
   ".dark .positive-filled.info:active:background-color"    500

   ;; Negative
   :.negative:color                                  600
   :.negative:hover:color                            800
   :.negative:active:color                           900

   :.negative:background-color                       100
   :.negative:hover:background-color                 200
   :.negative:active:background-color                300

   :.negative.info:color                             600
   :.negative.info:hover:color                       600
   :.negative.info:active:color                      600

   :.negative.info:background-color                  100
   :.negative.info:hover:background-color            100
   :.negative.info:active:background-color           100

   :.negative.minimal:color                          650
   :.negative.minimal:hover:color                    800
   :.negative.minimal:active:color                   900

   :.negative.minimal:background-color               :transparent
   :.negative.minimal:hover:background-color         100
   :.negative.minimal:active:background-color        200

   :.negative.bordered:color                         600
   :.negative.bordered:hover:color                   700
   :.negative.bordered:active:color                  800

   :.negative.bordered:background-color              :transparent
   :.negative.bordered:hover:background-color        :transparent
   :.negative.bordered:active:background-color       :transparent

   :.negative.bordered:border-color                  600
   :.negative.bordered:hover:border-color            700
   :.negative.bordered:active:border-color           800

   :.negative.bordered.info:color                    600
   :.negative.bordered.info:hover:color              600
   :.negative.bordered.info:active:color             600

   :.negative.bordered.info:background-color         :transparent
   :.negative.bordered.info:hover:background-color   :transparent
   :.negative.bordered.info:active:background-color  :transparent

   :.negative.bordered.info:border-color             600
   :.negative.bordered.info:hover:border-color       600
   :.negative.bordered.info:active:border-color      600

   :.negative.filled:color                           :white
   :.negative.filled:hover:color                     :white
   :.negative.filled:active:color                    :white

   :.negative.filled:background-color                500
   :.negative.filled:hover:background-color          700
   :.negative.filled:active:background-color         800

   :.negative.filled.info:background-color           600
   :.negative.filled.info:hover:background-color     600
   :.negative-filled.info:active:background-color    600


   ;; NEGATIVE INVERSE
   ;; default
   ".dark .negative:color"                                  100
   ".dark .negative:hover:color"                            50
   ".dark .negative:active:color"                           50

   ".dark .negative:background-color"                       800
   ".dark .negative:hover:background-color"                 700
   ".dark .negative:active:background-color"                600

   ".dark .negative.info:color"                             100
   ".dark .negative.info:hover:color"                       100
   ".dark .negative.info:active:color"                      100

   ".dark .negative.info:background-color"                  800
   ".dark .negative.info:hover:background-color"            800
   ".dark .negative.info:active:background-color"           800

   ;; minimal
   ".dark .negative.minimal:color"                          300
   ".dark .negative.minimal:hover:color"                    200
   ".dark .negative.minimal:active:color"                   100

   ".dark .negative.minimal:background-color"               :transparent
   ".dark .negative.minimal:hover:background-color"         850
   ".dark .negative.minimal:active:background-color"        750

   ;; bordered
   ".dark .negative.bordered:color"                         350
   ".dark .negative.bordered:hover:color"                   250
   ".dark .negative.bordered:active:color"                  150

   ".dark .negative.bordered:background-color"              :transparent
   ".dark .negative.bordered:hover:background-color"        :transparent
   ".dark .negative.bordered:active:background-color"       :transparent

   ".dark .negative.bordered:border-color"                  350
   ".dark .negative.bordered:hover:border-color"            250
   ".dark .negative.bordered:active:border-color"           150

   ".dark .negative.bordered.info:color"                    350
   ".dark .negative.bordered.info:hover:color"              350
   ".dark .negative.bordered.info:active:color"             350

   ".dark .negative.bordered.info:background-color"         :transparent
   ".dark .negative.bordered.info:hover:background-color"   :transparent
   ".dark .negative.bordered.info:active:background-color"  :transparent

   ".dark .negative.bordered.info:border-color"             350
   ".dark .negative.bordered.info:hover:border-color"       350
   ".dark .negative.bordered.info:active:border-color"      350

   ;; filled
   ".dark .negative.filled:color"                           :black
   ".dark .negative.filled:hover:color"                     :black
   ".dark .negative.filled:active:color"                    :black

   ".dark .negative.filled:background-color"                450
   ".dark .negative.filled:hover:background-color"          350
   ".dark .negative.filled:active:background-color"         250

   ".dark .negative.filled.info:background-color"           450
   ".dark .negative.filled.info:hover:background-color"     450
   ".dark .negative-filled.info:active:background-color"    450

   ;; Warning
   :.warning:color                                  800
   :.warning:hover:color                            850
   :.warning:active:color                           900

   :.warning:background-color                       150
   :.warning:hover:background-color                 200
   :.warning:active:background-color                300

   :.warning.info:color                             800
   :.warning.info:hover:color                       800
   :.warning.info:active:color                      800

   :.warning.info:background-color                  150
   :.warning.info:hover:background-color            150
   :.warning.info:active:background-color           150

   :.warning.minimal:color                          750
   :.warning.minimal:hover:color                    850
   :.warning.minimal:active:color                   900

   :.warning.minimal:background-color               :transparent
   :.warning.minimal:hover:background-color         100
   :.warning.minimal:active:background-color        150

   :.warning.bordered:color                         700
   :.warning.bordered:hover:color                   800
   :.warning.bordered:active:color                  900

   :.warning.bordered:background-color              :transparent
   :.warning.bordered:hover:background-color        :transparent
   :.warning.bordered:active:background-color       :transparent

   :.warning.bordered:border-color                  650
   :.warning.bordered:hover:border-color            750
   :.warning.bordered:active:border-color           850

   :.warning.bordered.info:color                    650
   :.warning.bordered.info:hover:color              650
   :.warning.bordered.info:active:color             650

   :.warning.bordered.info:background-color         :transparent
   :.warning.bordered.info:hover:background-color   :transparent
   :.warning.bordered.info:active:background-color  :transparent

   :.warning.bordered.info:border-color             650
   :.warning.bordered.info:hover:border-color       650
   :.warning.bordered.info:active:border-color      650

   :.warning.filled:color                           :white
   :.warning.filled:hover:color                     :white
   :.warning.filled:active:color                    :white

   :.warning.filled:background-color                650
   :.warning.filled:hover:background-color          700
   :.warning.filled:active:background-color         750

   :.warning.filled.info:background-color           650
   :.warning.filled.info:hover:background-color     650
   :.warning-filled.info:active:background-color    650

   ;; WARNING INVERSE
   ;; default
   ".dark .warning:color"                                  200
   ".dark .warning:hover:color"                            100
   ".dark .warning:active:color"                           50

   ".dark .warning:background-color"                       850
   ".dark .warning:hover:background-color"                 750
   ".dark .warning:active:background-color"                650

   ".dark .warning.info:color"                             200
   ".dark .warning.info:hover:color"                       200
   ".dark .warning.info:active:color"                      200

   ".dark .warning.info:background-color"                  850
   ".dark .warning.info:hover:background-color"            850
   ".dark .warning.info:active:background-color"           850

     ;; minimal
   ".dark .warning.minimal:color"                          500
   ".dark .warning.minimal:hover:color"                    350
   ".dark .warning.minimal:active:color"                   200

   ".dark .warning.minimal:background-color"               :transparent
   ".dark .warning.minimal:hover:background-color"         850
   ".dark .warning.minimal:active:background-color"        750

     ;; bordered
   ".dark .warning.bordered:color"                         500
   ".dark .warning.bordered:hover:color"                   350
   ".dark .warning.bordered:active:color"                  200

   ".dark .warning.bordered:background-color"              :transparent
   ".dark .warning.bordered:hover:background-color"        :transparent
   ".dark .warning.bordered:active:background-color"       :transparent

   ".dark .warning.bordered:border-color"                  450
   ".dark .warning.bordered:hover:border-color"            300
   ".dark .warning.bordered:active:border-color"           200

   ".dark .warning.bordered.info:color"                    400
   ".dark .warning.bordered.info:hover:color"              400
   ".dark .warning.bordered.info:active:color"             400

   ".dark .warning.bordered.info:background-color"         :transparent
   ".dark .warning.bordered.info:hover:background-color"   :transparent
   ".dark .warning.bordered.info:active:background-color"  :transparent

   ".dark .warning.bordered.info:border-color"             450
   ".dark .warning.bordered.info:hover:border-color"       450
   ".dark .warning.bordered.info:active:border-color"      450

     ;; filled
   ".dark .warning.filled:color"                           :black
   ".dark .warning.filled:hover:color"                     :black
   ".dark .warning.filled:active:color"                    :black

   ".dark .warning.filled:background-color"                500
   ".dark .warning.filled:hover:background-color"          350
   ".dark .warning.filled:active:background-color"         250

   ".dark .warning.filled.info:background-color"           500
   ".dark .warning.filled.info:hover:background-color"     500
   ".dark .warning-filled.info:active:background-color"    500])

(defn x2 [k v]
  (let [
        color                    (into #{} (map name (vals semantic-aliases)))
        style                    (into #{} (map name [:bordered :minimal :filled]))
        context                  #{"info"}
        k                        (name k)
        [ _ dark semantics props] (re-find #"^(\.dark )?(\.[^:]+)(.+)$" k)
        inverse?                 (= dark ".dark ")
        f                        (fn [s re] (into [] (remove #(or (nil? %) (string/blank? %)) (string/split s re))))
        semantics                (f semantics #"\.")
        props                    (f props #":")
        css-prop                 (last props)
        css-mods                 (into []
                                       (if (> (count props) 1)
                                         (drop-last props)
                                         []))
        f2                       (fn [x] (first (filter #(contains? x %) semantics)))
        color                    (f2 color)
        style                    (f2 style)
        context                  (f2 context)
        token-name*              (string/join "-"
                                              (remove nil?
                                                      [color
                                                       style
                                                       context
                                                       css-prop
                                                       (when (seq css-mods) (string/join "-" css-mods))
                                                       (when inverse? "inverse")]))
        token-key                (keyword (str "--" (name token-name*)))
        token-value             (if (int? v)
                                  (keyword (str "--" color "-" v))
                                  (name v))
        [selector prop]         (string/split (name k) #":" 2)
        value                   [prop token-key]]
    {
    ;;  :inverse?    inverse?
    ;;  :props       props
    ;;  :css-prop    css-prop
    ;;  :css-mods    css-mods
    ;;  :color       color
    ;;  :style       style
    ;;  :context     context
    ;;  :token-name  token-key
    ;;  :token-key   token-key
    ;;  :token-value token-value
     :token-pair  [token-key token-value]
     :selector    selector
     :value       value
     }))

(defn tokenizer [coll]
  (let [all                   (map (fn [[k v]]
                                     (x2 k v))
                                   (partition 2 coll))
        token-pairs           (into [] (apply concat (map :token-pair all)))
        by-selector           (group-by :selector all)
        by-selector-stylemaps (reduce (fn [acc [selector coll]] (assoc acc selector (into {} (map :value coll)))) {} by-selector)
        selectors             (distinct (map :selector all))
        ret                   (mapv (fn [k] [k (get by-selector-stylemaps k)]) selectors)]
    {:styles      (into [] (apply concat ret))
     :token-pairs token-pairs}))


(def css-reset
  [["*:where(:not(html, iframe, canvas, img, svg, video):not(svg *, symbol *))"]
   {:all     :unset
    :display :revert}

   ["*" "*::before" "*::after"]
   {:box-sizing :border-box}

   ["a" "button"]
   {:cursor :revert}

   ["ol" "ul" "menu"]
   {:list-style :none }

   ["img"]
   {:max-width :100% }

   ["table"]
   {:border-collapse :collapse}

   ["textarea"]
   {:white-space :revert}

   ["meter"]
   {:-webkit-appearance :revert
    :appearance         :revert}

   ["::placeholder"]
   {:color :unset}

   [":where([hidden])"]
   {:display :none}

   [":where([contenteditable])"]
   {:-moz-user-modify    :read-write
    :-webkit-user-modify :read-write
    :overflow-wrap       :break-word
    :-webkit-line-break  :after-white-space}

   [":where([draggable='true'])"]
   {:-webkit-user-drag :element}])

(def ui
  [
   ;; uncomment TEMP
   "body"
   {:font-family                :$sans-serif-font-stack
    :color                      :$foreground-color
    :background-color           :$background-color
    :transition-property        :background-color|color
    :transition-duration        :$fast
    :transition-timing-function :$timing-linear-curve}

   ".dark, body.dark"
   {:bgc                        :$background-color-inverse
    :color                      :$foreground-color-inverse}

   "code, .code"
   {:font-family                :$code-font-stack
    :font-size                  :$code-font-size
    :pi                         :$code-padding-inline
    :pb                         :$code-padding-block
    :border-radius              :$code-border-radius
    :c                          :$code-color
    :bgc                        :$code-background-color
    :h                          :fit-content
    :w                          :fit-content
    :ws                         :nowrap
    :transition-property        :all
    :transition-duration        :$fast
    :transition-timing-function "cubic-bezier(0, 0, 1, 1)"}

   ".dark code, .dark .code"
   {:bgc                        :$code-background-color-inverse
    :c                          :$code-color-inverse}



   ;; Foreground, Background
   ".styled-scrollbars"
   {:scrollbar-color :$scrollbar-thumb-color
    :dark:scrollbar-color :$scrollbar-thumb-color-inverse
    :scrollbar-width :thin}

   ".styled-scrollbars::-webkit-scrollbar"
   {;; Mostly for vertical scrollbars
    :width  :$scrollbar-width
    ;; Mostly for horizontal scrollbars
    :height :$scrollbar-width}

   ;; scrollbar foreground
   ".styled-scrollbars::-webkit-scrollbar-thumb"
   {:background    :$scrollbar-thumb-color
    :dark:background    :$scrollbar-thumb-color-inverse
    :border-radius :9999px
    :border        "0px solid var(--scrollbar-background-color)"
    :dark:border   "0px solid var(--scrollbar-background-color-inverse)"}

   ;; scrollbar background
   ".styled-scrollbars::-webkit-scrollbar-track"
   {:background  :$scrollbar-background-color
    :dark:background  :$scrollbar-background-color-inverse}


   "*:focus-visible"
   {:outline        "4px solid rgba(0, 125, 250, 0.6)"
    :outline-offset :1px}

   "*:disabled"
   disabled

   ".kushi-radio-input:focus-visible"
   {:box-shadow "0 0 0 4px rgba(0, 125, 250, 0.6)"}

   ".kushi-radio-input:focus-visible"
   {:box-shadow "0 0 0 4px rgba(0, 125, 250, 0.6)"}

   ;; Focusing the button with a mouse, touch, or stylus will show a subtle drop shadow.
  ;; LEAVE OUT FOR NOS
  ;;  "*:focus:not(:focus-visible)"
  ;;  {:outline    :none
  ;;   :box-shadow "1px 1px 5px rgba(1, 1, 0, .7)"}


   ;; TODO - Move these to utility? or should then just be here?
   ;; Foreground text that is slightly de-emphasized (such as text input field helper text)
   :.neutral-secondary-foreground {:color      :$neutral-secondary-foreground
                           :dark:color :$neutral-secondary-foreground-inverse}

   ;; Semantic fg
   ;; TODO -- dark versions of each with *--inverse tokens to match
   :.neutral-foreground {:color :$neutral-foreground
                 :dark:color :$neutral-foreground-inverse}
   :.accent-foreground {:color :$accent-foreground
                :dark:color :$accent-foreground-inverse}
   :.positive-foreground {:color :$positive-foreground
                  :dark:color :$positive-foreground-inverse}
   :.negative-foreground {:color :$negative-foreground
                  :dark:color :$negative-foreground-inverse}
   :.warning-foreground {:color :$warning-foreground
                 :dark:color :$warning-foreground-inverse}

   ;; Semantic bg
   :.neutral-bg {:background-color :$neutral-background-color}
   :.accent-bg {:background-color :$accent-background-color}
   :.positive-bg {:background-color :$positive-background-color}
   :.negative-bg {:background-color :$negative-background-color}
   :.warning-bg {:background-color :$warning-background-color}
   ])


;; User would probably want to change from wee-bold for button and tag
;; Add to docs
(def ui*
  [".kushi-button"                                                   {"font-family" :$primary-font-family}
   ".kushi-button.bordered"                                          {"border-width" :$button-border-width}
   ".kushi-tag.bordered"                                             {"border-width" :$tag-border-width}
   ".kushi-tag"                                                      {"font-family" :$primary-font-family}
   ".dark .kushi-radio-input"                                        {:bgc :black}
   ".dark .kushi-checkbox-input"                                     {:bgc :black}
   ".dark .kushi-checkbox-input:before"                              {:box-shadow :inset:1em:1em:black}
   ".dark .kushi-slider-step-label.kushi-slider-step-label-selected" {:c :white}
   ".dark .kushi-slider-step-label"                                  {:c :$gray-300}

  ;;  ".dark .kushi-slider-step-label"                               {:c :$gray-300}
   ])

(defn- styles-transform-report
  ([coll ret]
   (styles-transform-report coll ret nil nil))
  ([coll ret k v]
   (let [in  (count coll)
         out (count ret)]
     (merge {:in      in
             :out     out
             :removed (- in out)}
            (when (and k v)
              {k v})))))

(defn- remove-darks? [] (not (:add-kushi-ui-dark-theming? user-config)))

(defn- remove-lights? [] (not (:add-kushi-ui-light-theming? user-config)))

(defn- starts-with-dark? [x]
  (re-find (re-pattern specs/starts-with-dark-re) (name x)))

(defn- filter-flatmap [coll f]
  (->> coll
       (partition 2)
       (filter #(f (first %)))
       flatten))

(defn- remove-lights-or-darks [coll k]
  (let [dark?  (= k :dark)
        ret    (filter-flatmap coll
                               (if dark?
                                 #(not (starts-with-dark? %))
                                 #(starts-with-dark? %)))
        report (styles-transform-report coll ret)]
    ret))

(defn- maybe-remove-lights-or-darks [coll]
  (cond (remove-darks?)
        (remove-lights-or-darks coll :dark)
        (remove-lights?)
        (remove-lights-or-darks coll :light)
        :else
        variant-values))

(defn- variant-elision-re [key-set leading-str]
  (->> key-set
       (map #(str leading-str (name %)))
       (string/join "|")
       re-pattern))

(defn- maybe-remove-semantic-or-style-variants
  [leading-str coll]
  (let [elided (set/union (:elide-ui-variants-semantic user-config)
                          (:elide-ui-variants-style    user-config))]
    (if (and (set? elided) (seq elided))
      (let [re  (variant-elision-re elided leading-str)
            ret (filter-flatmap coll #(not (re-find re (name %))))]
        #_(println "Removing styles from unused variants"
                   (styles-transform-report coll
                                            ret
                                            :variants-to-elide
                                            elided))

        ret)
      coll)))


(defn- maybe-remove-some-variants [coll leading-str]
  (->> coll
      ;;  maybe-remove-lights-or-darks
       (maybe-remove-semantic-or-style-variants leading-str)))

(defn base-theme-map
  []
  (let [{ui2            :styles
         variant-tokens :token-pairs}
        (tokenizer variant-values)

        ui                            
        (into [] (concat ui ui2 ui*))

        ui
        (maybe-remove-some-variants ui "\\.")

        design-tokens
        (let [color-tokens       (colors->tokens
                                  colors
                                  {:format    :css
                                   :expanded? true})
              alias-color-tokens (colors->alias-tokens
                                  semantic-aliases 
                                  {:expanded? true})
              alias-color-tokens (maybe-remove-some-variants alias-color-tokens "^\\$")
              variant-tokens     (maybe-remove-some-variants variant-tokens "^--")
              ]

          (into []
                (concat color-tokens
                        transparent-neutrals
                        alias-color-tokens
                        variant-tokens
                        design-tokens)))]

    {:css-reset       css-reset
     :utility-classes utility-classes
     :design-tokens   design-tokens
     :ui              ui
     :font-loading    {:add-default-sans-font-family?  true
                       :add-default-code-font-family?  true
                       :add-default-serif-font-family? true
                       :google-material-symbols        ["Material Symbols Outlined"
                                                        "Material Symbols Rounded"
                                                        "Material Symbols Sharp"]
                       ;;  :google-fonts  [{:family "Public Sans"
                       ;;                   :styles {:normal [100]
                       ;;                            :italic [300]}}]
                       }}))
