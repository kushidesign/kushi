(ns kushi.ui.collapse.core
  (:require
   [kushi.css.core :refer [css sx css-vars-map merge-attrs]]
   [clojure.string :as string]
   [kushi.ui.collapse.header :refer (collapse-header-contents)]
   [kushi.ui.core :refer (defcom opts+children)]
   [domo.core :as domo]))

;; TODO - How to tie children to id of collapse?
;TODO refactor this out

(defn collapse-body [& args]
  (let [[_ attrs & children] (opts+children args)]
    [:section
     (merge-attrs (sx :.kushi-collapse-body-wrapper :overflow--hidden) attrs)
     [:div (sx :.kushi-collapse-body
               :bbe--1px:solid:transparent
               :padding-block--0.25em:0.5em)
      children]]))

#_(defcom collapse-body
  [:section
   (merge-attrs (sx ".kushi-collapse-body-wrapper" :overflow--hidden) &attrs)
   [:div (sx ".kushi-collapse-body"
             :bbe--1px:solid:transparent
             :padding-block--0.25em:0.5em)
    &children]])

(defn toggle-class-on-ancestor [node root-class class]
  (let [root (.closest node (str "." (name root-class)))]
    (when root (.toggle (.-classList root) (name class)))))

(defn toggle-boolean-attribute [node attr]
  (let [oldv (.getAttribute node (name attr))
        newv (if (or (= oldv "false") (= oldv false))
               "true"
               "false")]
    (.setAttribute node (name attr) newv)))

(defn bod-height [el]
  (let [styles (js/window.getComputedStyle el)
        margin-top (js/parseFloat (.-marginTop styles))
        margin-bottom (js/parseFloat (.-marginBottom styles))
        ret  (+ margin-top margin-bottom (js/Math.ceil (.-offsetHeight el)))]
    ret))

;; todo figure out accordion
(defn currently-open-accordion-node
  [currently-open-header]
  (let [accordion-root* (domo/grandparent currently-open-header)
        accordion-root  (when (domo/has-class accordion-root* "kushi-accordion") accordion-root*)]
    (when accordion-root
      (when-let [open-node (.querySelector
                            accordion-root
                            "section>div[aria-expanded='true'][role='button']")]
        (when-not (= open-node currently-open-header)
          open-node)))))

(defn collapse-header
  [& args]
  (let [[opts attrs & children]       (opts+children args)
        {:keys [speed expanded?]
         :or   {speed 250}} opts]
    (let [on-click
          #(let [header   (.closest (-> % .-target) "[aria-expanded][role='button']")
                 collapse (.-parentNode header)]

             ;; First, we make sure the collapse is not already in the process of opening or closing.
             (when-not (domo/has-class collapse "kushi-collapse-transit")

               ;; Add an 'in-transit' class to the collapse
               (domo/add-class! collapse "kushi-collapse-transit")

               (let [bod                           (-> header .-nextSibling)
                     collapsed?                    (= "none" (.-display (.-style bod)))
                     _                             (when collapsed? (set! bod.style.display "block"))
                     expanded?                     (domo/attribute-true? header :aria-expanded)
                     bod-height-px                 (some-> bod .-firstChild bod-height (str "px"))
                     next-bod-height-px            (if expanded? "0px" bod-height-px)
                     expanded-and-not-yet-clicked? (and expanded? (string/blank? bod.style.height))

                     ;; If the collapse happens to be inside an accordion,
                     ;; and the user has clicked on a "collapsed" collapse & there is currently an expanded sibling collapse,
                     ;; get the currently open sibling collapse that needs to be auto-closed as this one opens.
                     currently-open-accordion-child-head (currently-open-accordion-node header)]

                 ;; If accordion with open sibling, simulate click on it
                 (some-> currently-open-accordion-child-head .click)

                 ;; Toggle kushi-collapse-expanded classes
                 ((if expanded? domo/remove-class! domo/add-class!) collapse :kushi-collapse-expanded)

                 (when expanded-and-not-yet-clicked?
                   ;; Set the bod height to something, so we can animate it to the actual value we need.
                   #_(js/console.log "expanded-and-not-yet-clicked, setting bod height to: " bod-height-px)
                   (set! bod.style.height bod-height-px))

                 ;; Set the bod height to animate it open or closed.
                 (set! bod.style.height bod-height-px)

                 (js/window.requestAnimationFrame
                  (fn []
                    ;; Now set the current collapse height
                    (set! bod.style.height next-bod-height-px)

                    ;; Toggle the aria-expanded attribute
                    ;; If false:
                    ;;    - This will set the collapse's body-wrapper to 0 via css selector rule
                    ;;    - This will set the collapse's body-wrapper opacity duration to 10ms, and the opacity to 0 (fade out effect)
                    ;; If true:
                    ;;    - This will set the collapse's body-wrapper to 0 via css selector rule
                    ;;    - This will set the collapse's body-wrapper opacity duration to 200ms, and the opacity to 1 (fade-in effect)
                    (toggle-boolean-attribute header :aria-expanded)
                    (.setAttribute header "aria-expanded" (if expanded? "false" "true"))
                    (if-not collapsed?
                      (js/setTimeout (fn []
                                       ;; body is open, closing
                                       (set! bod.style.display "none")
                                       (domo/remove-class! collapse "kushi-collapse-transit"))
                                     speed)
                      (js/setTimeout (fn []
                                       ;; body is closed, opening
                                       (set! bod.style.height "auto")
                                       (domo/remove-class! collapse "kushi-collapse-transit"))
                                     (+ speed 10))))))))]
      (into [:div
             (merge-attrs
              {:style         {:--speed  (str speed "ms") }
               :class         (css
                               ".kushi-collapse-header"
                               :.flex-row-fs
                               :cursor--pointer
                               {:ai                                          :center
                                :padding-block                               :0.75em
                                :+section:transition-property                :height
                                :+section:transition-timing-function         "cubic-bezier(0.23, 1, 0.32, 1)"
                                :+section:transition-duration                :$speed
                                "[aria-expanded='false']+section:height"       :0px
                                "[aria-expanded='false']+section>*:transition" :opacity:$speed:linear:10ms
                                "[aria-expanded='true']+section>*:transition"  :opacity:$speed:linear:200ms
                                "[aria-expanded='false']+section>*:opacity"    0
                                "[aria-expanded='true']+section>*:opacity"     1
                                "[aria-expanded='true']>.kushi-collapse-header-label-collapsed:display" :none
                                "[aria-expanded='true']>.kushi-collapse-header-label-expanded:display" :flex})
               :tabIndex      0
               :role          :button
               :aria-expanded expanded?
               :on-click      on-click
               :onKeyDown     #(when (or (= "Enter" (.-key %))
                                         (= 13 (.-which %))
                                         (= 13  (.-keyCode %)))
                                (-> % .-target .click))}
              attrs)]
            children))))

(defn collapse
  {:summary ["A collapse is a section of content which can be collapsed and expanded."]
   :opts '[{:name    label
            :pred    string?
            :default nil
            :desc    "The text to display in the collapse header."}
           {:name    label-expanded
            :pred    string?
            :default nil
            :desc    "The text to display in the collapse header when expanded. Optional."}
           {:name    icon
            :pred    vector?
            :default '[kushi.ui.icon.core/icon :add]
            :desc    ["An instance of a kushi.ui.icon/icon component"
                      "Optional."]}
           {:name    icon-expanded
            :pred    vector?
            :default '[kushi.ui.icon.core/icon :remove]
            :desc    ["An instance of a kushi.ui.icon/icon component. Optional."]}
           {:name    icon-position
            :pred    #{:start :end}
            :default :start
            :desc    ["A value of `:start` will place the at the inline start of the header, preceding the label. "
                      "A value of `:end` will place the icon at the inline end of the header, opposite the label. "
                      "Optional."]}
           {:name    header-attrs
            :pred    map?
            :default nil
            :desc    ["Attribute map for header element."]}
           {:name    body-attrs
            :pred    map?
            :default nil
            :desc    ["Attribute map for body element."]}
           {:name    expanded?
            :pred    boolean?
            :default false
            :desc    ["When a value of `true` is passed, the collapse is initially rendered in an expanded state. "
                      "Optional."]}
           {:name    speed
            :pred    pos-int?
            :default 250
            :desc    ["The speed of the transition."]}
           ]}
  [& args]
  (let [[opts attr & children] (opts+children args)
        {:keys [header-attrs
                body-attrs
                expanded?
                on-click
                icon-position
                speed]
         :or   {speed 250}}    opts
        expanded-class (when expanded? :.kushi-collapse-expanded)]
    [:section
     (merge-attrs
      {:style         (let [speed (str speed "ms")]
                        (css-vars-map speed))
       :class         (css ".kushi-collapse"
                           expanded-class
                           :.flex-col-fs
                           :w--100%)
       :data-kushi-ui :collapse}
      attr)
     [collapse-header
      (merge-attrs header-attrs
                   {:on-click       on-click
                    :aria-expanded  (if expanded? "true" "false")
                    :-icon-position icon-position
                    :-speed         speed})
      [collapse-header-contents opts]]

     ;; collapse body
     [:section
      (merge-attrs (sx :.kushi-collapse-body-wrapper :overflow--hidden)
                   body-attrs
                   {:style {:display             (if expanded? :block :none)
                            :transition-duration (str speed "ms")}})
      (into [:div (sx :.kushi-collapse-body
                      :bbe--1px:solid:transparent
                      :pb--0.25em:0.5em)]
            children)]]))


(defn accordion
  {:desc ["A wrapper for multiple instances of the `collapse` component."
          :br
          "When `collapse` components are children of the accordion component, they can only be expanded one at a time."]}
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys []}              opts]
    (into
     [:div
      (merge-attrs
       {:class [:kushi-accordion]}
       attrs)]
     children)))

