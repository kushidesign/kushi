(ns kushi.ui.toast.core
  (:require [domo.core :as domo]
            [fireworks.core :refer [? !? ?> !?>]]
            [kushi.css.core :refer [utilize register-design-tokens-by-category]]
            [kushi.ui.core :refer (keyed)]
            [kushi.ui.dom.pane.core :as pane]
            [kushi.ui.dom.pane.placement :refer [user-placement]]
            [kushi.ui.dom.pane.styles]
            [kushi.ui.dom.pane.toast :refer [toast-slot-cleanup!
                                             update-toast-slot-dimensions!]]))


(register-design-tokens-by-category
 "elevation"
 "pane"
 "toast")

(utilize 
 {:lt  "top-left-corner-inside"
  :tlc "top-left-corner-inside"
  :tl  "top-left-corner-inside"
  :t   "top-inside"
  :tr  "top-right-corner-inside"
  :trc "top-right-corner-inside"
  :rt  "top-right-corner-inside"
  :r   "right-inside"
  :rb  "bottom-right-corner-inside"
  :brc "bottom-right-corner-inside"
  :br  "bottom-right-corner-inside"
  :b   "bottom-inside"
  :bl  "bottom-left-corner-inside"
  :blc "bottom-left-corner-inside"
  :lb  "bottom-left-corner-inside"
  :l   "left-inside"})


(defn toast-attrs
  {:summary ["Toasts provide notifications to the user based on application "
              "state."]
   :desc ["Toasts can be interactive and are sometimes auto-dismissing."
          :br
          :br
          "Specifying placement in various ways can be done with the"
          "`:-placement` option. See the tooltip docs for details on "
          "`:-placement`. You will most likely want to use the `:right-top` "
          "or `:right-bottom` options, or the logic equivalent to these, "
          "which would be `[:inline-end :block-start]` and "
          "`[:inline-end :block-end]`, respectively. In both cases, the toast "
          "will slide in, horizontally, from outside the viewport. If you want "
          "the toast to slide in from the top or bottom, you would use "
          "`:top-right`, or `:bottom-right` instead."
          :br
          :br
          "You can trigger a toast via an element listener by using "
          "`kushi.ui.toast.core/toast-attrs`. You can compose this map to "
          "an existing element's attributes map with `kushi.core/merge-attrs` "
          "using the pattern:"
          ;; TODO make this like a code block with a couple versions of this
          ;; pattern e.g. `(merge-attrs {:id "foo" :class "bar"} (toast-attrs {...}))`
          :br
          :br "`(merge-attrs (sx ...) (toast-attrs {...}))`"
          :br
          :br
          "You are responsible for providing your own rendering function, "
          "which takes as a single argument the auto-generated dom node of the "
          "toast, into which you can render whatever you like."
          :br
          :br
          "You can use the `kushi.ui.toast.core/dismiss-toast!` function if "
          "you want to close the toast from an action within the toast."
          :br
          :br
          "Elements and behaviors of the toast containers can be custom "
          "styled and controlled via the following tokens in your theme:"
          :br
          :br
          "__Colors and images:__"
          "`--toast-background-color`" :br
          "`--toast-background-color-inverse`" :br 
          "`--toast-background-image`" :br                  
          "`--toast-box-shadow`" :br                  
          "`--toast-box-shadow-inverse`" :br                  
          "`--toast-border-width`" :br                  
          "`--toast-border-style`" :br                  
          "`--toast-border-color`" :br                  
          "`--toast-border-color-inverse`"
          :br                  
          :br
          "__Geometry:__"
          "`--toast-border-radius`" :br 
          "`--toast-slot-padding-inline`" :br 
          "`--toast-slot-padding-block`" :br 
          "`--toast-slot-gap`" :br 
          "`--toast-slot-z-index`"
          :br 
          :br
          "__Choreography:__"
          "`--toast-delay-duration`" :br             
          "`--toast-initial-scale`" :br              
          "`--toast-transition-duration`" :br        
          "`--toast-transition-timing-function`" :br 
          "`--toast-auto-dismiss-duration`"
          :br 
          :br
          "If you want supply the value of any of the above tokens ala-carte, "
          "use the following pattern."
          :br
          :br
          ;; TODO make this like a code block with a couple versions of this
          ;; pattern e.g.`
          "`(toast-attrs {:-toast-class (css [:--toast-background-color :beige])}))`"
          :br
          :br
          "If you would like to use a value of `0` (`px`, `ems`, `rem`, etc.) for "
          "any of the tokens, you will need to use an explicit unit e.g. `0px`."]
   :opts '[{:name    f
            :pred    fn?
            :default nil
            :desc    ["A component rendering function which takes a single "
                      "argument, (the toast container dom node), and renders "
                      "content into it."
                      :br
                      :br
                      "The example in this documentation framework (created with "
                      "`kushi.playground`) uses reagent, but you could do "
                      "something similar with another rendering library:"
                      :br
                      :br
                      "`(fn [el] (rdom/render [my-toast-content] el))`"]}
           {:name    placement
            :pred    keyword?
            :default :auto
            :desc    ["You can use single keywords to specify the exact placement "
                      "of the toast:"
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
                      "If you care about the toast placement respecting writing "
                      "direction and/or document flow, you can use a vector of of "
                      "up to 3 logical properties keywords, separated by spaces:"
                      :br
                      "`[:inline-end :block-start]`"
                      :br
                      "`[:inline-end :block-start :corner]`"
                      :br
                      "`[:inline-start :center]`"
                      :br
                      "`[:inline-end :center]`"
                      :br
                      "`[:block-start :enter]`"
                      :br
                      "`[:block-end :center]`"
                      :br
                      "`[:block-end :inline-start]`"
                      :br]}
           {:name    auto-dismiss?
            :pred    boolean?
            :default true
            :desc    ["Toasts are auto-dismissed by default. "
                      "The duration of display before dismissal is controlled "
                      "by the theme token `--toast-auto-dismiss-duration`"]}
           {:name    slide-in?
            :pred    boolean?
            :default true
            :desc    ["Toasts slide into the viewport by default. The timing "
                      "of this can be controlled by the theme token "
                      "`--toast-transition-duration`. For users prefering "
                      "reduced motion (an OS-level setting), toasts will never "
                      "slide in, nor will they scale up or down upon entry."]}
           {:name    toast-class
            :pred    string?
            :default nil
            :desc    ["A class name for a la carte application of classes on the "
                      "toast element."]}
           ]}

  ;; TODO -- add :class opts so you can ala-carte try things like --toast-slot-z-index
  [{placement         :-placement
    auto-dismiss?     :-auto-dismiss?
    slide-in?         :-slide-in?
    user-rendering-fn :-f
    toast-class       :-toast-class
    :or               {placement     :rb
                       auto-dismiss? true
                       slide-in?     true}}]

  (when user-rendering-fn 
    (let [placement       (if-not (or (string? placement)
                                      (keyword? placement)
                                      (vector? placement))
                            :rb
                            placement)
          placement-kw    (or (user-placement placement) :rb)
          pane-type       :toast
          reduced-motion? (domo/prefers-reduced-motion?)
          slide-in?       (if reduced-motion? false slide-in?)
          ;; TODO - require [kushi.ui.dom.pane.toast :refer [append-toast!]]
          ;;        in this ns, instead of in kushi.ui.dom.pane.core, then
          ;;        pass it to append-pane! as an opt in this map maybe with key
          ;;        of append-toast!
          opts            (keyed placement-kw
                                 auto-dismiss?
                                 pane-type
                                 user-rendering-fn
                                 slide-in?
                                 reduced-motion?
                                 toast-class)]
      (merge 
       ;; TODO should be :data-kushi-ui-pane-placement = se
       ;; and :data-kushi-ui-pane-type = toast

       {:on-click (partial pane/append-pane! opts)}))))


(defn dismiss-toast! [e]
  (let [et            (domo/et e)
        toast-el      (? (domo/nearest-ancestor et ".kushi-toast"))
        toast-slot-el (domo/nearest-ancestor et ".kushi-toast-slot")]
    (.remove toast-el)
    (update-toast-slot-dimensions! toast-slot-el)
    (toast-slot-cleanup! toast-slot-el)))
