(ns ^:dev/always kushi.playground.showcase.snippets
  (:require
   [clojure.repl]
   [fireworks.core :refer [!? ?]]
   [kushi.core :refer [css defcss ?defcss merge-attrs sx]]
   [kushi.playground.md2hiccup :refer [desc->hiccup]]
   [kushi.ui.tooltip.core :refer [tooltip-attrs]]
   [kushi.ui.icon.mui.svg :as mui.svg]
   [kushi.ui.button.core :refer [button icon-button]]
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.util :refer [as-str maybe keyed]]
   [clojure.string :as string]
   [kushi.ui.core :refer (opts+children)]
   [kushi.css.media]
   [me.flowthing.pp :refer [pprint]]
   [domo.core :as d]))

;; not merging
;; (defcss "@layer user-shared-styles .kushi-bordered-panel"
;;   {
;;    :border-width      :$code-border-width||1px
;;    :border-color      :$code-border-color||$neutral-150
;;    :dark:border-color :$code-border-color-dark-mode||$neutral-800
;;    :border-style      :$code-border-style||solid
;;    })

(defcss "@layer user-shared-styles .kushi-code-block"
  ;; :.kushi-bordered-panel
  {:border-width         :$code-border-width||1px
   :border-color         :$code-border-color||$neutral-150
   :dark:border-color    :$code-border-color-dark-mode||$neutral-800
   :border-style         :$code-border-style||solid
   :background-color     :$code-background-color
  ;;  :--code-bracket-color :$neutral-600   
  ;;  :_.parent-open:c      :$code-bracket-color
  ;;  :_.bracket-open:c     :$code-bracket-color
  ;;  :_.brace-open:c       :$code-bracket-color
  ;;  :_.parent-close:c     :$code-bracket-color
  ;;  :_.bracket-close:c    :$code-bracket-color
  ;;  :_.brace-close:c      :$code-bracket-color
  ;;  :_.language-clojure   {:.symbol:c  :#4d6dba
  ;;                         :.keyword:c :#7A3E9D
  ;;                         :.string:c  :#4488C27}
   })

(defcss "@layer user-shared-styles .dark .kushi-code-block"
  {:--code-bracket-color-dark-mode :$neutral-500   
   :background-color               :$code-background-color-dark-mode
  ;;  :_.parent-open:c                :$code-bracket-color-dark-mode
  ;;  :_.bracket-open:c               :$code-bracket-color-dark-mode
  ;;  :_.brace-open:c                 :$code-bracket-color-dark-mode
  ;;  :_.parent-close:c               :$code-bracket-color-dark-mode
  ;;  :_.bracket-close:c              :$code-bracket-color-dark-mode
  ;;  :_.brace-close:c                :$code-bracket-color-dark-mode
  ;;  :_.language-clojure             {:.symbol:c  "#71ADE7"
  ;;                                   :.keyword:c "#b696b5"
  ;;                                   :.string:c  "#8cbd7a"}
   })


(defcss "@layer user-shared-styles .kushi-playground-code-snippet-preview"
   #_:.bordered-panel
  {:border-width               :$code-border-width||1px
   :border-color               :$code-border-color||$neutral-150
   :dark:border-color          :$code-border-color-dark-mode||$neutral-800
   :border-style               :$code-border-style||solid
   :beer                       0
   :besr                       0
   :bser                       :$rounded-absolute-medium,
   :bssr                       :$rounded-absolute-medium,
   :bbew                       0
   ">*:nth-child(2):line-height" "revert"
   :p                          :1rem})

(defn- formatted-code [s]
  [:pre
   [:code {:class :language-clojure
           :style {:white-space :pre
                   :line-height 1.5}}
    s]])

(defn copy-to-clipboard-button [& args]
  (let [[opts attrs] (opts+children args)]
    [button
     (merge-attrs
      {:-colorway :accent
       :-surface :minimal
       :class    (css :.kushi-playground-copy-to-clipboard-button
                      :p--7px)
       :on-click #(d/copy-to-clipboard!
                   (or (some->> opts 
                                :clipboard-parent-sel
                                (d/nearest-ancestor (d/et %)))
                       js/document.body) 
                   (:text-to-copy opts))}
      (tooltip-attrs
       {:-text                        
        "Click to copy"

        :-text-on-click               
        "Copied!"

        :-text-on-click-tooltip-class 
        (css :.kushi-playground-copy-to-clipboard-button-tooltip-class
              [:--tooltip-background-color :$background-color-accent-hard])

        :-placement                  
        [:block-start :inline-end]})
      attrs)
     [icon (sx :.kushi-playground-copy-to-clipboard-button-icon
               :fs--medium) mui.svg/content-copy]]))


(defn- snippet-section
  [{:keys [header
           preformatted
           quoted-source-code
           copyable
           bottom-half?]}]
  [:section (sx :.kushi-playground-snippet-section
                :.snippet-section
                :.flex-col-fs
                ;; :gap--0.5em
                ;; :first-of-type:mbe--2.5em
                ) 
   header
   [:section 
    (merge-attrs 
     (sx :.kushi-playground-code-snippet
         :.kushi-code-block
         :.code
         :.xsmall
         :xsm:p--1.5em
         :position--relative
         :p--1.0em
         :pie--3.5em
         :xsm:pie--2.25em
         :w--100%
         :lh--1.2
         :fs--$xsmall-b
         :p--1rem
         {">*:nth-child(2):line-height" "revert"})
    (when bottom-half? (sx :bser--0 :bssr--0)))
    (when-let [attrs (some->> copyable
                              (hash-map :-text-to-copy)
                              (merge-attrs 
                               ;; TODO - can this be done without :.top-right-corner-inside!
                               ;; TODO - can this be done without :.top-right-corner-inside!
                               (sx :.top-right-corner-inside
                                   :position--absolute)
                               {:-clipboard-parent-sel ".kushi-modal"}))]
      [copy-to-clipboard-button attrs])
    preformatted
    ]])

(defn reqs-coll
  "Returns something like this:
   '[[kushi.ui.button.core  :refer  [button]]
     [kushi.ui.icon.core  :refer  [icon]]]"
  [reqs-by-refers]
  (some->> reqs-by-refers
           keys
           distinct
           (reduce (fn [acc v]
                     (let [_ns    (get reqs-by-refers v)
                           refers (or (get acc _ns) [])]
                       (assoc acc _ns (conj refers v))))
                   {})
           (reduce-kv (fn [acc k v]
                        (conj acc [k :refer v]))
                      [])))

(defn reqs-by-refers
  "This creates a map of syms / syms representing :requires by :refers
   Used to populate popover snipped
   Ex '{button kushi.ui.button.core
   icon   kushi.ui.icon.core}"
  [all-reqs]
  (some->> (mapv (fn [vc]
                   (let [_ns (first vc)
                         m   (apply hash-map (rest vc))
                         ret (into {} (map (fn [v] [v _ns]) (:refer m)))]
                     ret))
                 all-reqs)
           seq
           (apply merge)))


(defn component-snippets
  []
  (fn [{:keys [reqs-coll
               reqs-for-uic
               snippets-header
               snippets
               hiccup-for-examples]}]

    [:div
     (sx :.relative
         :.flex-row-fs
         :_code:ws--n
         :_.code:ws--n
         :_pre_code:p--0
         :_pre_.code:p--0
         :min-height--120px
         :lh--1.7
         :ai--fs
         :min-width--200px
         :min-height--120px)
     [:div
      (sx :.flex-col-fs
          :w--100%
          :gap--1em
          :_.kushi-text-input-label:min-width--7em
          :_.kushi-input-inline:gtc--36%:64%)
      (let [max-width      (or (when-let [[p v] (some-> kushi.css.media/media
                                                        :sm
                                                        first)]
                                 (when-not (d/matches-media? p (as-str v))
                                   27))
                               50)
            formatted*     #(-> % (pprint {:max-width max-width}) with-out-str)]
        (into [:div (sx ".kushi-playground-snippets-modal-requires"
                        :.flex-col-fs
                        :gap--2.25rem
                        :mbs--1.5em
                        #_:pbe--2rem)
               [snippet-section
                {:header             (into [:div (sx :.small :.wee-bold :mbe--1em)]
                                           (desc->hiccup
                                            "Paste into the `:require` section of your `:ns` form:"))
                 :preformatted       (-> reqs-for-uic
                                         formatted*
                                         (subs 1)
                                         (drop-last)
                                         string/join
                                         (string/replace #"\n \[kushi." "\n[kushi.")
                                         formatted-code)
                 :quoted-source-code reqs-for-uic
                 :copyable           (string/join "\n" reqs-coll)}]]

              ;; This produces a snippet section for each of the examples 
              (for [[i call] (map-indexed (fn [i call] [i call]) snippets)
                    :let     [
                          ;; header (when (zero? i) 
                          ;;          (when (string? snippets-header)
                          ;;            (some->> snippets-header
                          ;;                     desc->hiccup 
                          ;;                     (into [:div])
                          ;;                     #_(docs/add-links scroll-to-elsewhere-on-page))))
                              header [:div 
                                      (sx :.transition
                                          :.kushi-playground-code-snippet-preview
                                          )
                                      #_header
                                      (nth hiccup-for-examples i)]]]
                [snippet-section
                 {:header       header

                  :bottom-half? true

                  :preformatted (-> call
                                    formatted*
                                    string/join
                                    formatted-code)
                  :copyable     (let [[ob cb] (if (list? call) ["(" ")"] ["[" "]"])]
                                  (str ob
                                       (string/join "\n" 
                                                    (map #(if (string? %)
                                                            (str "\"" % "\"")
                                                            %)
                                                         call))
                                       cb))}])))]]))
