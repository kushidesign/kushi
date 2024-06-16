(ns kushi.ui.modal.core
  (:require [kushi.ui.icon.core :refer (icon)]
            [kushi.ui.button.core :refer [button]]
            [clojure.string :as string]
            [domo.core :as domo]
            [goog.dom :as gdom]
            [kushi.ui.core :refer (opts+children)]
            [kushi.core :refer (merge-attrs) :refer-macros (sx)]))

(declare close-on-backdrop-click)

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

(defn open-kushi-modal
  "Takes an id of the modal, and an optional callback, which fires on light-dismiss."
  ([id]
   (open-kushi-modal id nil))
  ([id f]
   (if-let [dialog (domo/el-by-id id)]
     (do (.addEventListener dialog
                            "click"
                            (partial close-on-backdrop-click f)
                            #js {"once" true})
         (.showModal dialog)
         (domo/add-class! dialog "kushi-modal-open"))
     (js/console.warn (str "kushi.ui.modal.core/open-kushi-modal\nNo dialog found with an id of: " id)))
   ))

(defn modal-close-button
  {:desc ["The `modal-close-button` is meant to be a cta for closing a modal that is independant of other button groups that may be in the modal."
          "It is typically a single × icon positioned in the upper right or left corner of the dialog."]
   :opts '[{:name    icon
            :pred    keyword?
            :default :close
            :desc    ["Optional. A name of a Google Material Symbols icon. Defaults to a close (×) icon."]}
           {:name    icon-svg
            :pred  vector?
            :default nil
            :desc    ["Optional. A Hiccup representation of an svg icon. Supply this as an alternative to using the Google Material Symbols icon font"]}]}
  [& args]
  (let [[opts attrs & _] (opts+children args)
        {:keys     [icon-svg]
         icon-name :icon}      opts
        icon-name              (when-not icon-svg
                                 (if (and icon-name
                                          (or (string? icon-name)
                                              (keyword? icon-name)))
                                   icon-name
                                   :close))]
    [button
     (merge-attrs
      (sx 'kushi-modal-close-button
          :.minimal
          :.pill
          :.large
          :padding--0.5rem
          {:on-mouse-down close-kushi-modal
           :style         {:position           :absolute
                           :inset-block-start  :0.5rem
                           :inset-block-end    :unset
                           :inset-inline-end   :0.5rem
                           :inset-inline-start :unset}})
      attrs)
     (if icon-svg
       [icon {:icon-svg icon-svg}]
       [icon icon-name])]))

(defn modal
  {:desc ["Modal dialogs create a new floating layer over the current view "
          "to get user feedback or display information."
          :br
          :br
          "Elements and behaviors of modals can be custom styled and "
          "controlled via the following tokens in your theme:"
          :br
          :br "`:$modal-border-radius`"      
          :br "`:$modal-border`"             
          :br "`:$modal-padding-block`"      
          :br "`:$modal-padding-inline`"     
          :br "`:$modal-backdrop-color`"     
          :br "`:$modal-margin`"             
          :br "`:$modal-min-width`"          
          :br "`:$modal-transition-duration`"]
   :opts '[{:name    modal-title
            :pred    string?
            :default nil
            :desc    "Optional. If supplied, this will be rendered as an h2 element within the modal."}
           {:name    description
            :pred    string?
            :default nil
            :desc    "Optional. If supplied, this will be rendered as an p element within the modal."}
           {:name    elevation
            :pred    #(< -1 % 6)
            :default nil
            :desc    "Optional. The kushi utility class in the elevation family that will be used to create a drop-shadow for the modal panel"}

           ;; TODO -- add on-dismiss callback option (for calling function with on backdrop click)

           ;; TODO -- add option for disabling auto close-on-background click
           ;; TODO -- add option for naive BSL
           ;; TODO -- add x
           ;; TODO -- add y
           ]}
  [& args]
  (let [[opts attrs & children] (opts+children args)
        {:keys [modal-title
                description
                elevation
                expanded?]}      opts
        {:keys [id]}              attrs
        desc-id                   (str id "-description")
        title-id                  (str id "-title")
        valid-elevation?          (and (int? elevation) (< -1 elevation 6))
        elevation-token           (when-not (zero? elevation)
                                    (if valid-elevation?
                                      (str "var(--elevated-" elevation "), ")
                                      (str "var(--elevated), ")))
        elevation-token-inverse   (when-not (zero? elevation)
                                    (if valid-elevation?
                                      (str "var(--elevated-" elevation "-inverse), ")
                                      (str "var(--elevated-inverse), ")))]

    ;; TODO document the how and why of this
    (when expanded? (js/setTimeout #(open-kushi-modal id) 100))

    (into
     [:dialog (merge-attrs
               (sx 'kushi-modal
                   :.fixed-centered
                   :.transition
                   :backdrop:bgc--transparent
                   :overflow--visible
                   [:transition-duration "var(--modal-transition-duration, var(--fast))"]
                   :bgc--$background-color
                   :dark:bgc--$background-color-inverse
                   :border-radius--$modal-border-radius
                   :b--$modal-border
                   :min-width--$modal-min-width||450px
                   [:max-width "calc(100vw - (2 * var(--modal-margin, 1rem)))"]
                   [:max-height "calc(100vh - (2 * var(--modal-margin, 1rem)))"]
                   :height--$modal-min-height
                   [:box-shadow (str elevation-token "0 0 0 100vmax var(--modal-backdrop-color)")]
                   [:dark:box-shadow (str elevation-token-inverse "0 0 0 100vmax var(--dark-gray-transparent-90)")]
                   :opacity--0
                   :&.kushi-modal-open:opacity--1
                   {:id               id
                    :aria-labelledby  title-id
                    :aria-describedby desc-id})
               attrs)
      (into [:div (sx 'kushi-modal-inner
                      :.flex-col-fs
                      :.transition
                      :opacity--0
                      ["has-parent(.kushi-modal.kushi-modal-open):opacity" 1]
                      ["has-parent(.kushi-modal.kushi-modal-open):transition-delay" "calc(var(--modal-transition-duration, var(--fast)))"]
                      :gap--2em
                      :pi--$modal-padding-inline||$modal-padding
                      :pb--$modal-padding-block||$modal-padding
                      :w--100%
                      :h--100%
                      :overflow--auto)
             (when (or modal-title description)
               [:div (sx 'kushi-modal-title-and-description
                         :.flex-col-fs
                         :gap--1em
                         :.large
                         {:id title-id})
                (when modal-title 
                  [:h2 (sx 'kushi-modal-title
                           :.semi-bold
                           {:id title-id})
                   modal-title])
                (when description
                  [:p (sx 'kushi-modal-description
                          :.small
                          {:id desc-id})
                   description])])]
            children)])))
