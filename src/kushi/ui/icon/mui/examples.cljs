(ns kushi.ui.icon.mui.examples
  (:require
   [kushi.core :refer (sx)]
   [kushi.ui.icon.core :refer (icon)]
   [kushi.playground.util :refer-macros (feature example2)]))

#_(def icons
  [
   "auto_awesome"
   "auto_awesome*"
   "settings"
   "settings*"
   "search"
   "filter_alt"
   "filter_alt*"
   "cloud_upload"
   "cloud_upload*"
   "download"
   "download*"
   "playlist_add"
   "expand"
   "compress"
   "arrow_back"
   "arrow_forward"
   "sort"
   "clear"
   "delete"
   "delete*"
   "cancel"
   "cancel*"
   "auto_awesome_motion"
   "auto_awesome_motion*"
   "keyboard_return"
   "archive"
   "archive*"
   "star"
   "star_border"
   "check"
   "sell"
   "sell*"
   "visibility"
   "visibility*"
   "visibility_off"
   "visibility_off*"
   "find_replace"
   "report_problem"
   "report_problem*"
   "error"
   "error_outline"
   "check_circle"
   "check_circle*"
   "open_in_new"
   "edit"
   "edit*"
   "favorite"
   "favorite_border"
   "fingerprint"
   "help"
   "help_outline"
   "info"
   "info_outline"
   "smartphone"
   "folder"
   "folder*"])


(def icon-examples
  (feature
   icon
   {:title    "Icons"
    :stage    {:style {:min-height :135px}}
    :variants [:size-expanded]
    :defaults {:size-expanded :xxxlarge
               :examples      "auto-awesome"}
    :examples [
               {:radio-label [icon "auto-awesome"],
                :label       "auto-awesome"
                :example     [icon :auto-awesome]}

               {:radio-label [icon {:-icon-filled? true} :auto-awesome],
                :label       "auto-awesome-filled"
                :example     [icon (sx {:-icon-filled? true}) :auto-awesome]}

               {:radio-label [icon :settings],
                :label       "settings"
                :example     [icon :settings]}

               {:radio-label [icon {:-icon-filled? true} :settings]
                :label       "settings-filled"
                :example     [icon (sx {:-icon-filled? true}) :settings]}

               {:radio-label [icon {:-icon-filled? true} :search]
                :label       "search-filled"
                :example     [icon (sx {:-icon-filled? true}) :search]}

               {:radio-label [icon {:-icon-filled? true} :filter-alt]
                :label       "filter-alt-filled"
                :example     [icon (sx {:-icon-filled? true}) :filter-alt]}

               {:radio-label [icon :filter-alt]
                :label       "filter-alt"
                :example     [icon :filter-alt]}

               {:radio-label [icon {:-icon-filled? true} :cloud-upload]
                :label       "cloud-upload-filled"
                :example     [icon (sx {:-icon-filled? true}) :cloud-upload]}

               {:radio-label [icon :cloud-upload]
                :label       "cloud-upload"
                :example     [icon :cloud-upload]}

               {:radio-label [icon :download]
                :label       "download"
                :example     [icon :download]}

               {:radio-label [icon {:-icon-filled? true} :playlist-add]
                :label       "playlist-add-filled"
                :example     [icon (sx {:-icon-filled? true}) :playlist-add]}

               {:radio-label [icon {:-icon-filled? true} :expand]
                :label       "expanded-filled"
                :example     [icon (sx {:-icon-filled? true}) :expand]}

               {:radio-label [icon {:-icon-filled? true} :compress]
                :label       "compress-filled"
                :example     [icon (sx {:-icon-filled? true}) :compress]}

               {:radio-label [icon {:-icon-filled? true} :arrow-back],
                :label       "arrow-back-filled"
                :example     [icon (sx {:-icon-filled? true}) :arrow-back]}

               {:radio-label [icon {:-icon-filled? true} :arrow-forward],
                :label       "arrow-forward-filled"
                :example     [icon (sx {:-icon-filled? true}) :arrow-forward]}

               {:radio-label [icon {:-icon-filled? true} :sort]
                :label       "sort-filled"
                :example     [icon (sx {:-icon-filled? true}) :sort]}

               {:radio-label [icon {:-icon-filled? true} :clear],
                :label       "clear-filled"
                :example     [icon (sx {:-icon-filled? true}) :clear]}

               {:radio-label [icon {:-icon-filled? true} :delete],
                :label       "delete-filled"
                :example     [icon (sx {:-icon-filled? true}) :delete]}

               {:radio-label [icon {:title :delete} :delete],
                :label       "delete"
                :example     [icon :delete]}

               {:radio-label [icon {:-icon-filled? true} :cancel],
                :label       "cancel-filled"
                :example     [icon (sx {:-icon-filled? true}) :cancel]}

               {:radio-label [icon :cancel],
                :label       "cancel"
                :example     [icon :cancel]}

               {:radio-label [icon {:-icon-filled? true} :auto-awesome-motion],
                :label       "auto-awesome-motion-filled"
                :example     [icon (sx {:-icon-filled? true}) :auto-awesome-motion]}

               {:radio-label [icon {:title :auto-awesome-motion} :auto-awesome-motion],
                :label       "auto-awesome-motion"
                :example     [icon :auto-awesome-motion]}

               {:radio-label [icon {:-icon-filled? true} :keyboard-return],
                :label       "keyboard-return"
                :example     [icon (sx {:-icon-filled? true}) :keyboard-return]}

               {:radio-label [icon {:-icon-filled? true} :archive]
                :label       "archive-filled"
                :example     [icon (sx {:-icon-filled? true}) :archive]}

               {:radio-label [icon {:title :archive} :archive]
                :label       "archive"
                :example     [icon :archive]}

               {:radio-label [icon {:title :star} :star]
                :label       "star"
                :example     [icon :star]}

               {:radio-label [icon {:-icon-filled? true} :star-border]
                :label       "star-border-filled"
                :example     [icon (sx {:-icon-filled? true}) :star-border]}

               {:radio-label [icon {:-icon-filled? true} :check]
                :label       "check-filled"
                :example     [icon (sx {:-icon-filled? true}) :check]}

               {:radio-label [icon {:-icon-filled? true} :sell]
                :label       "sell-filled"
                :example     [icon (sx {:-icon-filled? true}) :sell]}

               {:radio-label [icon :sell]
                :label       "sell"
                :example     [icon :sell]}

               {:radio-label [icon {:-icon-filled? true} :visibility]
                :label       "visibility-filled"
                :example     [icon (sx {:-icon-filled? true}) :visibility]}

               {:radio-label [icon {:title :visibility} :visibility]
                :label       "visibility"
                :example     [icon :visibility]}

               {:radio-label [icon {:-icon-filled? true} :visibility-off]
                :label       "visibility-off-filled"
                :example     [icon (sx {:-icon-filled? true}) :visibility-off]}

               {:radio-label [icon :visibility-off]
                :label       "visibility-off"
                :example     [icon :visibility-off]}

               {:radio-label [icon {:-icon-filled? true} :find-replace]
                :label       "find-replace-filled"
                :example     [icon (sx {:-icon-filled? true}) :find-replace]}

               {:radio-label [icon {:-icon-filled? true} :report-problem]
                :label       "report-problem-filled"
                :example     [icon (sx {:-icon-filled? true}) :report-problem]}

               {:radio-label [icon :report-problem]
                :label       "report-problem"
                :example     [icon :report-problem]}

               {:radio-label [icon {:-icon-filled? true} :error]
                :label       "error-filled"
                :example     [icon (sx {:-icon-filled? true}) :error]}

               {:radio-label [icon :error]
                :label       "error"
                :example     [icon :error]}

               {:radio-label [icon {:-icon-filled? true} :check-circle]
                :label       "check-circle-filled"
                :example     [icon (sx {:-icon-filled? true}) :check-circle]}

               {:radio-label [icon :check-circle]
                :label       "check-circle"
                :example     [icon :check-circle]}

               {:radio-label [icon :open-in-new]
                :label       "open-in-new"
                :example     [icon  :open-in-new]}

               {:radio-label [icon {:-icon-filled? true} :edit]
                :label       "edit-filled"
                :example     [icon (sx {:-icon-filled? true}) :edit]}

               {:radio-label [icon :edit],
                :label       "edit"
                :example     [icon :edit]}

               {:radio-label [icon {:-icon-filled? true} :favorite],
                :label       "favorite"
                :example     [icon (sx {:-icon-filled? true}) :favorite]}

               {:radio-label [icon :favorite],
                :label       "favorite"
                :example     [icon :favorite]}

               {:radio-label [icon {:-icon-filled? true} :fingerprint],
                :label       "fingerprint"
                :example     [icon (sx {:-icon-filled? true}) :fingerprint]}

               {:radio-label [icon {:-icon-filled? true} :help],
                :label       "help-filled"
                :example     [icon (sx {:-icon-filled? true}) :help]}

               {:radio-label [icon :help],
                :label       "help"
                :example     [icon :help]}

               {:radio-label [icon {:-icon-filled? true} :info],
                :label       "info-filled"
                :example     [icon (sx {:-icon-filled? true}) :info]}

               {:radio-label [icon :info],
                :label       "info"
                :example     [icon :info]}

               {:radio-label [icon {:-icon-filled? true} :smartphone],
                :label       "smartphone"
                :example     [icon (sx {:-icon-filled? true}) :smartphone]}

               {:radio-label [icon {:-icon-filled? true} :folder],
                :label       "folder-filled"
                :example     [icon (sx {:-icon-filled? true}) :folder]}

               {:radio-label [icon :folder],
                :label       "folder"
                :example     [icon :folder]}

               {:radio-label [icon (sx :c--$red-500) :auto-awesome],
                :label       "auto-awesome-red"
                :example     [icon (sx :c--$red-500) :auto-awesome]}

               {:radio-label [icon
                              (sx :c--$orange-500)
                              :auto-awesome],
                :label       "auto-awesome-orange"
                :example     [icon (sx :c--$orange-500) :auto-awesome]}

               {:radio-label [icon
                              (sx :c--$yellow-500)
                              :auto-awesome],
                :label       "auto-awesome-yellow"
                :example     [icon (sx :c--$yellow-600) :auto-awesome]}

               {:radio-label [icon
                              (sx :c--$green-500)
                              :auto-awesome],
                :label       "auto-awesome-green"
                :example     [icon (sx :c--$green-500) :auto-awesome]}

               {:radio-label [icon
                              (sx :c--$blue-500)
                              :auto-awesome],
                :label       "auto-awesome-blue"
                :example     [icon (sx :c--$blue-500) :auto-awesome]}

               {:radio-label [icon
                              (sx :c--$purple-500)
                              :auto-awesome],
                :label       "auto-awesome-purple"
                :example     [icon (sx :c--$purple-500) :auto-awesome]}

               {:radio-label [icon
                              (sx :c--$magenta-500)
                              :auto-awesome],
                :label       "auto-awesome-magenta"
                :example     [icon (sx :c--$magenta-500) :auto-awesome]}

               ]}))

