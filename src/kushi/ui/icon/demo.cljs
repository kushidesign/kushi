(ns ^{:kushi/layer "user-styles"} kushi.ui.icon.demo
  (:require
   [kushi.ui.icon.core :refer [icon]]
   [kushi.ui.core :refer [ui-demo]]
   [kushi.ui.tooltip.core :refer [tooltip-attrs]]
   [kushi.core :refer (sx merge-attrs)]
   [kushi.playground.component-examples :as component-examples]
   [kushi.playground.util :refer-macros [sx-call]]))


(def icons-without-filled-variants
  ["search"
   "playlist-add"
   "expand"
   "compress"
   "arrow-back"
   "arrow-forward"
   "sort"
   "clear"
   "keyboard-return"
   "check"
   "find-replace"
   "open-in-new"
   "fingerprint"
   "refresh"
   "download"
   "menu"])

(def icons-with-filled-variants
  ["auto-awesome"
   "help"
   "info"
   "favorite"
   "settings"
   "filter-alt"
   "cloud-upload"
   "download"
   "delete"
   "cancel"
   "auto-awesome-motion"
   "archive"
   "sell"
   "visibility"
   "visibility-off"
   "report-problem"
   "check-circle"
   "error"
   "edit"
   "folder"
   "smartphone"
   "star"
   "add-circle"
   "expand-circle-down"])

(def demos
  (ui-demo
   
   [{:label   "Semantic colorways"
     :desc    "Examples of semantic coloring of icons"
     :require [[kushi.ui.icon.core :refer [icon]]]
     :samples ["accent"
               [icon {:-colorway :accent
                      :-size     :xxxlarge} :star]

               "negative"
               [icon {:-colorway :negative
                      :-size     :xxxlarge} :cancel]

               "positive"
               [icon {:-colorway :positive
                      :-size     :xxxlarge} :check-circle]

               "warning"
               [icon {:-colorway :warning
                      :-size     :xxxlarge} :warning]]}
    
    {:label     "Semantic colorways"
     :desc      "Examples of semantic coloring of icons"
     :require   [[kushi.ui.icon.core :refer [icon]]]
     :samples   [[icon {:-colorway :accent
                        :-size     :xxxlarge} :star]

                 [icon {:-colorway :negative
                        :-size     :xxxlarge} :cancel]

                 [icon {:-colorway :positive
                        :-size     :xxxlarge} :check-circle]

                 [icon {:-colorway :warning
                        :-size     :xxxlarge} :warning]]}
    
    {:label         [:span "Various icons" [:span " (hover to view icon name)"]]
     :label/modal   "Various icons"
     :desc          "Examples of semantic coloring of icons"
     :require       '[[kushi.ui.icon.core :refer [icon]]]
     :row-style     {:flex-wrap :wrap
                     :gap       :2em}
     :attrs/display {:-size   :xxxlarge
                     :-weight :light}
     :render-as     :icons-with-tooltips
     :samples       [:auto-awesome
                     :help
                     :info
                     :favorite
                     :settings
                     :filter-alt
                     :cloud-upload
                     :download
                     :delete
                     :cancel
                     :auto-awesome-motion
                     :archive
                     :sell
                     :visibility
                     :visibility-off
                     :warning
                     :check-circle
                     :error 
                     :edit
                     :folder
                     :smartphone
                     :add-circle
                     :expand-circle-down
                     :search
                     :playlist-add
                     :expand
                     :compress
                     :arrow-back
                     :arrow-forward 
                     :sort
                     :keyboard-return
                     :check
                     :find-replace
                     :open-in-new
                     :fingerprint
                     :refresh
                     :download
                     :menu]}]))
