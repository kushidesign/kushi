(ns kushi.playground.nav
  (:require
   [clojure.string :as string]
   [domo.core :as domo]
   [kushi.playground.state :as state]
   [kushi.playground.ui :refer [light-dark-mode-switch]]
   [kushi.core :refer [sx css merge-attrs css-vars-map]]
   [kushi.ui.core :refer [defcom]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.button.core :refer [button]]))

(defn path-transitioning! [app]
    (domo/add-class! app "path-transitioning")
    (js/setTimeout #(domo/remove-class! (domo/el-by-id "app")
                                        "path-transitioning")
                   5000))

(defn route! [menu-id href e]
  (let [e          (or e js/window.event)
        app        (domo/el-by-id "app")
        path-label (when (string/starts-with? href "/")
                     (subs href 1))]
    (domo/remove-class! (domo/el-by-id menu-id) "has-hover")
    (when path-label
        (.preventDefault e)
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
                       250))))

(def header-nav-button-attrs
  {:-surface :minimal
   :-shape :pill
   :class (css :.foreground-color-secondary!
               :tt--capitalize
               :fs--$xlarge
               :pi--0.7em
               :pb--0.3em
               :hover:c--$neutral-950
               :active:c--$neutral-1000
               :hover:bgc--$neutral-100
               :active:bgc--$neutral-0
               :dark:hover:c--$neutral-50
               :dark:active:c--$neutral-0
               :dark:hover:bgc--$neutral-850
               :dark:active:bgc--$neutral-900
               ["[aria-selected='true']:c" :black]
               ;; TODO test this one
               ["dark:[aria-selected='true']:c" :white])})

(defcom header-nav-button
  [button 
   (let [focused? (:focused? &opts)]
     (merge-attrs header-nav-button-attrs &attrs))
   &children])


(def octocat-svg
  [:svg {:width   "36"
         :height  "34"
         :viewBox "0 0 36 34"
         :fill    "none"
         :xmlns   "http://www.w3.org/2000/svg"}
   [:path {:d    "M18.0001 0C8.0602 0 0 7.80373 0 17.4304C0 25.1316 5.15756 31.6654 12.3096 33.9701C13.2092 34.1315 13.5395 33.592 13.5395 33.1316C13.5395 32.716 13.5226 31.3429 13.515 29.8865C8.50739 30.9408 7.45073 27.8299 7.45073 27.8299C6.63188 25.8152 5.45203 25.2794 5.45203 25.2794C3.81895 24.1976 5.57522 24.2199 5.57522 24.2199C7.38267 24.3428 8.33442 26.0161 8.33442 26.0161C9.93994 28.6807 12.5454 27.9104 13.5726 27.465C13.7341 26.3385 14.2006 25.5694 14.7154 25.1342C10.7173 24.6935 6.51445 23.1989 6.51445 16.5201C6.51445 14.6171 7.21758 13.0621 8.36902 11.8414C8.18213 11.4024 7.56605 9.62969 8.54339 7.22867C8.54339 7.22867 10.055 6.76023 13.4948 9.01541C14.9306 8.62922 16.4704 8.43558 18.0001 8.42891C19.5299 8.43558 21.071 8.62908 22.5094 9.01541C25.945 6.76037 27.4545 7.2288 27.4545 7.2288C28.4342 9.62955 27.8179 11.4025 27.631 11.8414C28.7851 13.0621 29.4834 14.6171 29.4834 16.5201C29.4834 23.2148 25.2726 24.6889 21.2643 25.1205C21.9099 25.6613 22.4852 26.7221 22.4852 28.3483C22.4852 30.6804 22.4644 32.5575 22.4644 33.1316C22.4644 33.5955 22.7884 34.139 23.7008 33.9677C30.8489 31.6605 36 25.1292 36 17.4306C36 7.80373 27.9409 0 18.0001 0ZM6.7417 24.83C6.70205 24.9166 6.56128 24.9426 6.43317 24.8831C6.30253 24.8263 6.22927 24.7083 6.27145 24.6214C6.31027 24.5322 6.45131 24.5073 6.58153 24.567C6.71231 24.624 6.78684 24.7431 6.7417 24.83ZM7.62708 25.595C7.5413 25.6721 7.37339 25.6363 7.25948 25.5145C7.14178 25.3931 7.1197 25.2305 7.20675 25.1523C7.29534 25.0752 7.45805 25.1113 7.57603 25.2328C7.69388 25.3558 7.71666 25.5171 7.62708 25.595ZM8.23458 26.5738C8.12419 26.6481 7.94391 26.5785 7.83239 26.4235C7.72214 26.2685 7.72214 26.0827 7.83478 26.0082C7.94658 25.9337 8.12419 26.0007 8.23711 26.1546C8.34708 26.3121 8.34708 26.498 8.23444 26.574L8.23458 26.5738ZM9.2617 27.7075C9.16313 27.8129 8.95289 27.7846 8.79905 27.6408C8.64169 27.5002 8.59795 27.3009 8.69695 27.1955C8.7968 27.0898 9.00816 27.1195 9.16313 27.2622C9.31922 27.4025 9.36703 27.6033 9.2617 27.7075ZM10.5895 28.0901C10.5459 28.2267 10.3434 28.2888 10.1396 28.2308C9.936 28.171 9.80283 28.0112 9.84389 27.8732C9.88622 27.7357 10.0896 27.6711 10.2949 27.7332C10.4982 27.7926 10.6318 27.9514 10.5895 28.0903V28.0901ZM12.1004 28.2526C12.1054 28.3963 11.9326 28.5154 11.7186 28.5181C11.5034 28.5228 11.3292 28.4065 11.3268 28.2648C11.3268 28.1198 11.4958 28.0018 11.7111 27.9982C11.9251 27.9941 12.1004 28.1096 12.1004 28.2526ZM13.5847 28.1974C13.6102 28.3377 13.4616 28.4818 13.249 28.5202C13.0402 28.5571 12.8467 28.4705 12.8201 28.3314C12.7942 28.1876 12.9457 28.0436 13.1542 28.0064C13.3671 27.9706 13.5575 28.0549 13.5847 28.1974Z"
           :fill "#161614"}]])

(defn header-menu
  [menu-id]
  (into [:nav (sx :.flex-col-c
                  :fw--$semi-bold
                  :.transition
                  :.header-menu-transition-group
                  :ai--stretch
                  :gap--1.5rem
                  :mbs--2rem)]
         (for [label ["intro" "components" "colors" "typography" "guide"]
               :let [guide?    (= label "guide")
                     href      (if guide?
                                 "https://github.com/kushidesign/kushi"
                                 (str "/" label))
                     target    (if guide? :_blank :_self)
                     translate (when guide? "-0.33ch")]]
           [:a
            {:class    (css :.flex-row-c
                            :d--none
                            :hover>button:c--$neutral-950
                            :active>button:c--$neutral-1000
                            :hover>button:bgc--$neutral-100
                            :active>button:bgc--$neutral-0
                            :dark:hover>button:c--$neutral-50
                            :dark:active>button:c--$neutral-0
                            :dark:hover>button:bgc--$neutral-850
                            :dark:active>button:bgc--$neutral-900)
             :href     href
             :target   target
             :on-click (partial route! menu-id href)}
            [header-nav-button
             {:style         (css-vars-map translate)
              :class         (css [:translate :$translate]
                                  :>svg:w--20px
                                  :>svg:h--20px
                                  :>svg:o--0.66
                                  :dark:>svg:o--0.86
                                  ["dark:>svg:filter" "invert(1)"])
              :aria-selected false}
             (when guide? octocat-svg)
             label]])))

(defn remove-hover! [menu-el]
  (when (domo/has-class? menu-el "has-hover") 
    (domo/remove-class! menu-el "has-hover")))

(defn header-touchstart-handler [menu-id e]
  (let [menu-el       (domo/el-by-id menu-id)
        et            (domo/et e)
        menu-trigger? (domo/matches-or-nearest-ancestor?
                       et
                       "button.kushi-explore" )]
    (if menu-trigger?
      (domo/toggle-class! menu-el "has-hover")
      (if (domo/matches-or-nearest-ancestor? et (str "#" menu-id " a"))
        (let [href (->> (str "#" menu-id " a")
                        (domo/nearest-ancestor et)
                        .-href)
              href (-> href 
                       (string/split href #"/")
                       last
                       (str "/"))]
          (route! menu-id href e)
          (remove-hover! menu-el))
        (when (domo/has-class? menu-el "has-hover") 
          (remove-hover! menu-el))))))


(defn header []
 (let [menu-id "kushi-playground-menu"]
  [:div#header-navbar
   (merge-attrs 
    (sx ["--overlay-width" "calc(100vw + 40px)"]
        ["--menu-height" :415px]
        :.flex-row-sb
        :.neutralize
        :.divisor-block-end
        :position--fixed
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
     {:class (css :.relative
                  :.has-hover_a:d--flex
                  :.has-hover>div.explore-menu-container:h--$menu-height
                  :.has-hover_nav:mbs--4rem
                  :.has-hover>div.explore-menu-container:o--1
                  [".has-hover+div.bg-scrim-gradient:height" :100vh]
                  [".has-hover+div.bg-scrim-gradient:o" 1]
                  :zi--1
                  :translate---30px)
      :id    menu-id}
     (when (domo/media-supports-hover?)
       (domo/hover-class-attrs "has-hover")))
    [button 
     {:-shape   :pill
      :-surface :minimal
      :class (css :.kushi-explore
                  :.foreground-color-secondary!
                  :fs--$small
                  :pi--0.8em
                  :pb--0.4em)}
     [icon :keyboard-arrow-down]
     "Explore"]
    [:div (sx :.explore-menu-container
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
              [:dark:box-shadow "0  calc(var(--menu-height) / 2) calc(100vh - var(--menu-height)) var(--background-color-dark-mode)"]
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
         

