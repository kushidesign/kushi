(ns kushi.ui.collapse
  (:require
   [kushi.ui.core :refer (defui)]
   [kushi.core :refer [css sx css-vars-map merge-attrs]]
   [clojure.string :as string]
   [kushi.ui.core :refer (extract)]
   [kushi.ui.icon]
   [kushi.ui.label]
   [domo.core :as domo]))

(defn header-title
  [{:keys [label-collapsed-or-expanded
           icon-opposite?
           collapsed-or-expanded-icon]
    :as   opts}]
  (if (string? label-collapsed-or-expanded)
    (let [w (when icon-opposite? "100%")
          jc (when icon-opposite? "space-between")
          attrs
          {:style (css-vars-map w jc)
           :class (css ".ks-collapse-header-title-contents"
                       :w--$w
                       :jc--$jc)}]
      (if icon-opposite?
        [kushi.ui.label/label
         attrs
         label-collapsed-or-expanded 
         [kushi.ui.icon/icon collapsed-or-expanded-icon]]
        [kushi.ui.label/label
         attrs
         [kushi.ui.icon/icon collapsed-or-expanded-icon]
         label-collapsed-or-expanded]))
    label-collapsed-or-expanded))


(defn collapse-header-contents
  [{:keys [label-collapsed
           label-expanded
           icon-collapsed
           icon-expanded
           icon-position]
    :or {icon-collapsed :add
         icon-expanded  :remove}}]
  (let [label-expanded    (or label-expanded label-collapsed)
        icon-opposite?    (= :end icon-position)
        opts              {:label-collapsed-or-expanded label-collapsed
                           :collapsed-or-expanded-icon  icon-collapsed
                           :icon-opposite?              icon-opposite?}]
    [:<>
     [:span
      (sx ".ks-collapse-header-label-collapsed"
          :display--flex
          :ai--c
          :w--100% )
      (if (string? label-collapsed)
        [header-title opts]
        label-collapsed)]
     [:span
      (sx ".ks-collapse-header-label-expanded"
          :display--flex
          :ai--c
          :w--100%
          :d--none)
      (if (string? label-expanded)
        [header-title (assoc opts
                             :label-collapsed-or-expanded 
                             label-expanded
                             :collapsed-or-expanded-icon 
                             icon-expanded)]
        label-expanded)]]))

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
        accordion-root  (when (domo/has-class accordion-root* "ks-accordion") accordion-root*)]
    (when accordion-root
      (when-let [open-node (.querySelector
                            accordion-root
                            "section>div[aria-expanded='true'][role='button']")]
        (when-not (= open-node currently-open-header)
          open-node)))))

(defn collapse-header
  [& args]
  (let [{:keys [opts attrs children]}
        (extract args)

        {:keys [speed expanded?]
         :or   {speed 250}}
        opts]
    (let [on-click
          #(let [header   (.closest (-> % .-target) "[aria-expanded][role='button']")
                 collapse (.-parentNode header)]

             ;; First, we make sure the collapse is not already in the process of opening or closing.
             (when-not (domo/has-class collapse "ks-collapse-transit")

               ;; Add an 'in-transit' class to the collapse
               (domo/add-class! collapse "ks-collapse-transit")

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
                 ((if expanded? domo/remove-class! domo/add-class!) collapse :ks-collapse-expanded)

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
                                       (domo/remove-class! collapse "ks-collapse-transit"))
                                     speed)
                      (js/setTimeout (fn []
                                       ;; body is closed, opening
                                       (set! bod.style.height "auto")
                                       (domo/remove-class! collapse "ks-collapse-transit"))
                                     (+ speed 10))))))))]
      (into [:div
             (merge-attrs
              {:style         {:--speed  (str speed "ms") }
               :class         (css
                               ".ks-collapse-header"
                               :.flex-row-fs
                               :display--flex
                               :cursor--pointer
                               {:ai                                          :center
                                :padding-block                               :0.35em
                                :+section:transition-property                :height
                                :+section:transition-timing-function         "cubic-bezier(0.23, 1, 0.32, 1)"
                                :+section:transition-duration                :$speed
                                "[aria-expanded='false']+section:height"       :0px
                                "[aria-expanded='false']+section>*:transition" :opacity:$speed:linear:10ms
                                "[aria-expanded='true']+section>*:transition"  :opacity:$speed:linear:200ms
                                "[aria-expanded='false']+section>*:opacity"    0
                                "[aria-expanded='true']+section>*:opacity"     1
                                "[aria-expanded='true']>.ks-collapse-header-label-collapsed:display" :none
                                "[aria-expanded='true']>.ks-collapse-header-label-expanded:display" :flex})
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


(defui collapse
  {:props/shared [:colorway :size]
   :props        {:label-collapsed {:schema  :string
                                    :default nil
                                    :desc    "The text to display in the collapse header."}
                  :label-expanded  {:schema  :string
                                    :default nil
                                    :desc    "The text to display in the collapse header, when expanded."}
                  :icon-collapsed  {:schema  :keyword
                                    :default nil
                                    :desc    "The icon to display in the collapse header, when collapsed."}
                  :icon-expanded   {:schema  :keyword
                                    :default nil
                                    :desc    "The icon to display in the collapse header, when expanded."}
                  :icon-position   {:schema  :map
                                    :default nil
                                    :desc    "A value of `:start` will place the at the inline start of
                                       the header, preceding the label. A value of `:end` will
                                       place the icon at the inline end of the header, opposite
                                       the label. Optional."}
                  :header-attrs    {:schema  :map
                                    :default nil
                                    :desc    "A value of `:start` will place the at the inline start of
                                              the header, preceding the label. A value of `:end` will
                                              place the icon at the inline end of the header, opposite
                                              the label. Optional."}
                  :body-attrs      {:schema  :map
                                    :default nil
                                    :desc    "A value of `:start` will place the at the inline start of
                                              the header, preceding the label. A value of `:end` will
                                              place the icon at the inline end of the header, opposite
                                              the label. Optional."}
                  :expanded?       {:schema  :boolean
                                    :default false
                                    :desc    "When a value of `true` is passed, the collapse is initially rendered in an expanded state. Optional."}
                  :speed           {:schema  :int
                                    :default 250
                                    :desc    "The speed of the transition. A positive integer representing milliseconds"}}}

  [& args]

  (let [{:keys [header-attrs
                body-attrs
                expanded?
                on-click
                icon-position
                speed]
         :or   {speed 250}}
        &props

        expanded-class                                                                            
        (when expanded? :.ks-collapse-expanded)]

    [:section
     (merge-attrs
      {:style      (let [speed (str speed "ms")]
                     (css-vars-map speed))
       :class      (css ".ks-collapse"
                        expanded-class
                        :display--flex
                        :flex-direction--column
                        :w--100%)
       :data-ks-ui :collapse}
      &attrs)
     [collapse-header
      (merge-attrs header-attrs
                   {:on-click      on-click
                    :aria-expanded (if expanded? "true" "false")
                    :icon-position icon-position
                    :speed         speed})
      [collapse-header-contents &props]]

     ;; collapse body
     [:section
      (merge-attrs (sx ".ks-collapse-body-wrapper" :overflow--hidden)
                   body-attrs
                   {:style {:display             (if expanded? :block :none)
                            :transition-duration (str speed "ms")}})
      (into [:div (merge-attrs (sx ".ks-collapse-body"
                                   :bbe--1px:solid:transparent
                                   :pb--0.25em:0.5em))]
            &children)]]))
