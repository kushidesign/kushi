(ns kushi.ui.modal
  (:require [kushi.ui.icon :refer (icon)]
            [fireworks.core :refer [? !? ?> !?>]]
            [kushi.ui.button :refer [button]]
            [clojure.string :as string]
            [domo.core :as domo]
            [goog.dom :as gdom]
            [kushi.ui.core :refer (extract)]
            [kushi.core :refer [sx
                                css
                                merge-attrs
                                css-vars-map
                                register-design-tokens
                                register-design-tokens-by-category]]))

(register-design-tokens-by-category
 "elevation"
 "pane"
 "modal")

(register-design-tokens
 :--transparent-dark-gray-90)

(declare close-on-backdrop-click)

;; TODO - Change name to close-modal!
(defn close-kushi-modal [e]
  (.stopPropagation e)
  (let [el     (domo/et e)
        dialog (if (= "DIALOG" (.-nodeName el))
                 el
                 (domo/nearest-ancestor el ".kushi-modal"))]
    (when (gdom/isElement dialog)
      (let [duration* (.-transitionDuration (js/window.getComputedStyle dialog))
            duration  (js/Math.round (* 1000 (js/parseFloat (string/replace duration* #"s$" ""))))]
        (.removeEventListener dialog "click" close-on-backdrop-click)
        (domo/remove-class! dialog "kushi-modal-open")
        (js/setTimeout #(.close dialog) duration)))))

(defn close-on-backdrop-click  [f e]
  (when (= "DIALOG" (.-nodeName (domo/et e)))
    (close-kushi-modal e))
  (when f (f)))

;; TODO - Change name to open-modal!
(defn open-kushi-modal
  "Takes an id of the modal, and an optional callback, which fires on
   light-dismiss."
  ([id]
   (open-kushi-modal id nil))
  ([id f]
   (if-let [dialog (domo/el-by-id id)]
     (do (.addEventListener dialog
                            "click"
                            (partial close-on-backdrop-click f)
                            ;; #js {"once" true}
                            )
         (.showModal dialog)
         (domo/add-class! dialog "kushi-modal-open"))
     (js/console.warn
      (str "kushi.ui.modal/open-kushi-modal\nNo dialog found with an id of:
            " id)))))

(defn modal-close-button
  {:desc ["The `modal-close-button` is meant to be a cta for closing a modal
           that is independent of other button groups that may be in the modal."

          "It is typically a single × icon positioned in the upper right or left
           corner of the dialog."]
  ;;  :opts '[{:name    icon
  ;;           :schema  keyword?
  ;;           :default :close
  ;;           :desc    ["Optional. A name of a Google Material Symbols icon.
  ;;                      Defaults to a close (×) icon."]}
  ;;          {:name    icon-svg
  ;;           :schema  vector?
  ;;           :default nil
  ;;           :desc    ["Optional. A Hiccup representation of an svg icon. Supply
  ;;                      this as an alternative to using the Google Material
  ;;                      Symbols icon font"]}]
   }
  [& args]
  (let [[opts attrs & _] (extract args)
        {:keys     [icon-svg]
         icon-name :icon}     opts
        icon-name             (when-not icon-svg
                                (if (and icon-name
                                         (or (string? icon-name)
                                             (keyword? icon-name)))
                                  icon-name
                                  :close))]
    [button
     (merge-attrs
      {:contour        :pill
       :surface      :minimal
       :class         (css
                       ".kushi-modal-close-button"
                       {:fs                 :$large
                        :pb                 :0.4rem
                        :pis                :0.4rem
                        :pie                :0.399rem
                        :position           :absolute
                        :inset-block-start  :1rem
                        :inset-block-end    :unset
                        :inset-inline-end   :1rem
                        :inset-inline-start :unset})
       :on-mouse-down close-kushi-modal}
      attrs)
     (if icon-svg
       [icon {:icon-svg icon-svg}]
       [icon icon-name])]))

(defn modal
  {:summary "Modal dialogs create a new floating layer over the current view to
             get user feedback or display information."
   :desc "Elements and behaviors of modals can be custom styled and controlled
          via the following tokens in your theme: 
        
          `--modal-box-shadow`<br>
          `--modal-box-shadow-dark-mode`<br>
          `--modal-border-radius`<br>
          `--modal-border-width`<br>
          `--modal-border-style`<br>
          `--modal-border-color`<br>
          `--modal-border-color-dark-mode`<br>
          `--modal-padding-block`<br>
          `--modal-padding-inline`<br>
          `--modal-backdrop-color`<br>
          `--modal-margin`<br>
          `--modal-min-width`<br>
          `--modal-transition-duration`
          
          Note that the value supplied to `--modal-box-shadow` should be one of 
          the stock elevation tokens, level 1~5, expressed like so:<br>
          `:$elevation-3`, or `var(--elevation-3)`"
   :opts '[{:name    modal-title
            :schema    string?
            :default nil
            :desc    "Optional. If supplied, this will be rendered as an h2
                      element within the modal."}
           {:name    description
            :schema    string?
            :default nil
            :desc    "Optional. If supplied, this will be rendered as an p
                      element within the modal."}

           ;; TODO -- add on-dismiss callback option (for calling function with on backdrop click)
           ;; TODO -- add option for disabling auto close-on-background click
           ;; TODO -- add option for naive BSL
           ;; TODO -- add x
           ;; TODO -- add y
           ]}
  [& args]
  (let [{:keys [opts attrs children]} (extract args)
        {:keys [modal-title
                description
                expanded?]}      opts
        {:keys [id]}              attrs
        desc-id                   (str id "-description")
        title-id                  (str id "-title")
        light-box-shadow (str
                          "var(--modal-box-shadow), "
                          "0 0 0 100vmax var(--modal-backdrop-color)")
        dark-box-shadow (str
                         "var(--modal-box-shadow-dark-mode), "
                         "0 0 0 100vmax var(--transparent-dark-gray-90)")]

    ;; TODO document the how and why of this
    (when expanded? (js/setTimeout #(open-kushi-modal id) 100))
    (into
     [:dialog
      (merge-attrs
       {:style            (css-vars-map light-box-shadow dark-box-shadow)
        :class            (css
                           ".kushi-modal"
                           :.styled-scrollbars
                           :.fixed-centered
                           :.transition
                           [:transition-duration
                            "var(--modal-transition-duration, var(--fast))"]
                           [:max-width
                            "calc(100vw - (2 * var(--modal-margin, 1rem)))"]
                           [".kushi-modal-open>.kushi-modal-inner:opacity" 1]
                           ["kushi-modal-open>.kushi-modal-inner:transition-delay"  
                            "calc(var(--modal-transition-duration, var(--fast)))"]
                           [:box-shadow :$light-box-shadow]
                           [:dark:box-shadow :$dark-box-shadow]
                           :backdrop:bgc--transparent
                           :overflow--visible
                           :bgc--$background-color
                           :dark:bgc--$background-color-dark-mode
                           :border-radius--$modal-border-radius
                           :b--$modal-border
                           :min-width--$modal-min-width||200px
                           :height--fit-content
                           [:max-height "calc(100vh - 40px)"]
                           :opacity--0
                           :.kushi-modal-open:opacity--1
                           :.kushi-modal-open:display--flex
                           :flex-direction--column
                           )
        :id               id
        :aria-labelledby  title-id
        :aria-describedby desc-id}
       attrs)
      (into [:div 
             (sx
              ".kushi-modal-inner"
              :.flex-col-fs
              :.transition
              :opacity--0
              :gap--2em
              :pi--$modal-padding-inline||$modal-padding
              :pb--$modal-padding-block||$modal-padding
              :w--100%
              :h--100%
              :overflow--auto)
             (when (or modal-title description)
               [:div 
                {:class (css
                         ".kushi-modal-title-and-description"
                         :.flex-col-fs
                         :.large
                         :gap--1em)
                 :id    title-id}
                (when modal-title 
                  [:h2 {:class (css ".kushi-modal-title"
                                    :.semi-bold)
                        :id    title-id}
                   modal-title])
                (when description
                  [:p {:class (css
                               ".kushi-modal-description"
                               :.small)
                       :id    desc-id}
                   description])])]
            children)])))
