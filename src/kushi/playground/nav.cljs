(ns kushi.playground.nav
  (:require
   [fireworks.core :refer [? !? ?- !?- ?-- !?-- ?> !?> ?i !?i ?l !?l ?log !?log ?log- !?log- ?pp !?pp ?pp- !?pp-]]
   [clojure.string :as string]
   [domo.core :as domo]
   [kushi.playground.state :as state]
   [kushi.playground.ui :refer [light-dark-mode-switch]]
   [kushi.core :refer [sx merge-attrs defclass]]
   [kushi.ui.core :refer [defcom]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.button.core :refer [button]]))

(defn path-transitioning! [app]
    (domo/add-class! app "path-transitioning")
    (js/setTimeout #(domo/remove-class! (domo/el-by-id "app")
                                        "path-transitioning")
                   5000))

(defn route! [menu-id guide? e]
  (let [e (or e js/window.event)
        app (domo/el-by-id "app")]
    (domo/remove-class! (domo/el-by-id menu-id) "has-hover")
    #_(path-transitioning! app)
    (when-not guide?
      (.preventDefault e)
      (let [el         (domo/cet e)
            href       (.-href el)
            path-label (-> (new js/URL href)
                           .-pathname
                           (string/replace #"^/" "")
                           (string/split #"/")
                           last)]
        (.setAttribute app
                       "data-kushi-playground-active-path"
                       path-label)
        (js/requestAnimationFrame domo/scroll-to-top!)
        (.pushState (.-history js/window) 
                    #js{}
                    ""
                    href)
        ;; This is key!
        (js/setTimeout #(state/set-focused-path! (into [] path-label))
                       250)))))


(defcom header-nav-button
  [button 
   (let [focused? (:focused? &opts)]
     (merge-attrs 
      (sx :.xlarge
          :.minimal
          :.pill
          :.capitalize
          :pi--0.7em
          :pb--0.3em
          ;; Remove these when theming gets revamped
          :&.neutral.minimal:c--$neutral-secondary-foreground
          :&.neutral.minimal:hover:c--$neutral-950
          :&.neutral.minimal:active:c--$neutral-1000
          :&.neutral.minimal:hover:bgc--$neutral-100
          :&.neutral.minimal:active:bgc--$neutral-0

          :dark:&.neutral.minimal:c--$neutral-secondary-foreground-inverse
          :dark:&.neutral.minimal:hover:c--$neutral-50
          :dark:&.neutral.minimal:active:c--$neutral-0
          :dark:&.neutral.minimal:hover:bgc--$neutral-850
          :dark:&.neutral.minimal:active:bgc--$neutral-900

          ["&.neutral.minimal[aria-selected='true']:c" :black]
          ["dark:&.neutral.minimal[aria-selected='true']:c" :white])
      &attrs))
   &children])


(defn header-menu
  [menu-id]
  (into [:nav (sx :.flex-col-c
                  :.semi-bold
                  :.transition
                  :.header-menu-transition-group
                  :ai--stretch
                  :gap--1.5rem
                  :mbs--2rem)]
         (for [label ["intro" "components" "colors" "typography" "guide"]
               :let [guide? (= label "guide")
                     href   (if guide?
                              "https://github.com/kushidesign/kushi"
                              (str "/" label))
                     target (if guide? :_blank :_self)]]
           [:a
            (merge-attrs 
             (sx :.flex-row-c
                 :d--none
                 :hover>button.neutral.minimal:c--$neutral-950
                 :active>button.neutral.minimal:c--$neutral-1000
                 :hover>button.neutral.minimal:bgc--$neutral-100
                 :active>button.neutral.minimal:bgc--$neutral-0

                 :dark:hover>button.neutral.minimal:c--$neutral-50
                 :dark:active>button.neutral.minimal:c--$neutral-0
                 :dark:hover>button.neutral.minimal:bgc--$neutral-850
                 :dark:active>button.neutral.minimal:bgc--$neutral-900
                 {:href   href
                  :target target})
             {:on-click (partial route! menu-id guide?)}
            ;;  (when (domo/media-supports-hover?)
            ;;    {:on-click (partial route! menu-id guide?)})
            ;;  (when (domo/media-supports-touch?)
            ;;    {:on-touch-start (partial route! menu-id guide?)})
             )
            [header-nav-button
             (sx [:translate (when guide? "-0.33ch")]
                 {:aria-selected false})
             (when guide?
               [:img (merge-attrs
                      (sx 'grayscale-icon-image
                          :max-height--100%
                          :max-width--100%
                          :object-fit--contain
                          :o--0.65
                          :w--0.75em
                          :w--0.75em
                          
                          :h--0.75em)
                      {:src "graphics/github.svg"})]
               #_[icon (sx :fs--0.75em)
                  :open-in-new])
             label]])))

(defn header-touchstart-handler [menu-id e]
  (let [menu-el       (domo/el-by-id menu-id)
        et            (domo/et e)
        menu-trigger? (domo/matches-or-nearest-ancestor?
                       et
                       "button.kushi-explore" )]
    (if menu-trigger?
      (!? 'menu-trigger:toggling-class
          (domo/toggle-class! menu-el "has-hover"))
      (if (domo/matches-or-nearest-ancestor?
           et
           (str "#" menu-id " a"))
        (!?-- "do nothing")
        (!? 'menu-dead-zone:removing-class
            (when (domo/has-class? menu-el "has-hover") 
              (domo/remove-class! menu-el "has-hover")))))))

(defn header []
 (let [menu-id "kushi-playground-menu"]
  [:div#header-navbar
   (merge-attrs 
    (sx [:$overlay-width "calc(100vw + 40px)"]
        :$menu-height--415px
        :.fixed
        :.flex-row-sb
        :.neutralize
        :.divisor-block-end
        ;; :o--0
        :top--0
        :left--0
        :right--0
        :ai--c
        :zi--5
        :w--100%
        :p--1rem
        :height--$navbar-height
        :pi--1.25rem
        :md:pi--4rem
        
       #_["has(~&_div&_nav[aria-expanded=\"true\"][data-kushi-playground-sidenav])>*:opacity"
        0]
        
        
        )
    (when (domo/media-supports-touch?)
      {:on-touch-start (partial header-touchstart-handler menu-id)}))

   [:span (sx #_:.transition :.semi-bold :fs--$xlarge :o--0.5)
    "Kushi"]
   [:div
    (merge-attrs
     (sx :.relative
         #_:.transition
         :&.has-hover&_a:d--flex
         :&.has-hover>div.explore-menu-container:h--$menu-height
         :&.has-hover&_nav:mbs--4rem
         :&.has-hover>div.explore-menu-container:o--1
         ["&.has-hover+div.bg-scrim-gradient:height" :100vh]
         ["&.has-hover+div.bg-scrim-gradient:o" 1]
         :zi--1
         :translate---30px
         {:id menu-id})
     (when (domo/media-supports-hover?)
       (domo/hover-class-attrs "has-hover")))
    [button 
     (sx 'kushi-explore
         :.pill
         :.minimal
         :.small
         :pi--0.8em
         :pb--0.4em
        ;;  :&.neutral.minimal:c--$neutral-secondary-foreground
        ;;  :dark:&.neutral.minimal:c--$neutral-secondary-foreground-inverse
         )
     [icon :keyboard-arrow-down]
     "Explore"]
    [:div (sx 'explore-menu-container
              :.header-menu-transition-group
              :.bottom-outside
              :.flex-col-fs
              :.transition
              :.neutralize
              :bgc--$background-color
              :w--$overlay-width
              :o--0
              :h--0
              :overflow--hidden
              [:box-shadow "0  calc(var(--menu-height) / 2) calc(100vh - var(--menu-height)) var(--background-color)"]
              [:dark:box-shadow "0  calc(var(--menu-height) / 2) calc(100vh - var(--menu-height)) var(--background-color-inverse)"]
              [:transform "translateX(7px)"])
     [header-menu menu-id]]]

   [:div (sx :.bg-scrim-gradient
             :.bottom-outside
             :.transition
             :.header-menu-transition-group
             :o--0
             :w--$overlay-width
             :h--0)]

   [light-dark-mode-switch (sx :.light :.transition)]]))
         

