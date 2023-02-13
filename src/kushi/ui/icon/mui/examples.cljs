(ns kushi.ui.icon.mui.examples
  (:require
   [kushi.core :refer (sx)]
   [kushi.ui.icon.core :refer (icon)]
   [kushi.playground.util :refer-macros (example2)]))

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

;; (pprint icon-examples*)

(def icon-examples
  [{:label   [icon (sx :.xlarge {:title :auto-awesome}) "auto-awesome"],
    :example (example2 [icon "auto-awesome"])}
   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :auto-awesome}) :auto-awesome],
    :example (example2 [icon (sx {:-icon-filled? true}) :auto-awesome])}

   {:label   [icon (sx :.xlarge {:title :settings}) :settings],
    :example (example2 [icon :settings])}
   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :settings}) :settings],
    :example (example2 [icon (sx {:-icon-filled? true}) :settings])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :search}) :search],
    :example (example2 [icon (sx {:-icon-filled? true}) :search])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :filter-alt}) :filter-alt],
    :example (example2 [icon (sx {:-icon-filled? true}) :filter-alt])}
   {:label   [icon (sx :.xlarge {:title :filter-alt}) :filter-alt],
    :example (example2 [icon :filter-alt])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :cloud-upload}) :cloud-upload],
    :example (example2 [icon (sx {:-icon-filled? true}) :cloud-upload])}
   {:label   [icon (sx :.xlarge {:title :cloud-upload}) :cloud-upload],
    :example (example2 [icon :cloud-upload])}

   {:label   [icon (sx :.xlarge {:title :download}) :download],
    :example (example2 [icon :download])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :playlist-add}) :playlist-add],
    :example (example2 [icon (sx {:-icon-filled? true}) :playlist-add])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :expand}) :expand],
    :example (example2 [icon (sx {:-icon-filled? true}) :expand])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :compress}) :compress],
    :example (example2 [icon (sx {:-icon-filled? true}) :compress])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :arrow-back}) :arrow-back],
    :example (example2 [icon (sx {:-icon-filled? true}) :arrow-back])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :arrow-forward}) :arrow-forward],
    :example (example2 [icon (sx {:-icon-filled? true}) :arrow-forward])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :sort}) :sort],
    :example (example2 [icon (sx {:-icon-filled? true}) :sort])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :clear}) :clear],
    :example (example2 [icon (sx {:-icon-filled? true}) :clear])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :delete}) :delete],
    :example (example2 [icon (sx {:-icon-filled? true}) :delete])}
   {:label   [icon (sx :.xlarge {:title :delete}) :delete],
    :example (example2 [icon :delete])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :cancel}) :cancel],
    :example (example2 [icon (sx {:-icon-filled? true}) :cancel])}
   {:label   [icon (sx :.xlarge {:title :cancel}) :cancel],
    :example (example2 [icon :cancel])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :auto-awesome-motion}) :auto-awesome-motion],
    :example (example2 [icon (sx {:-icon-filled? true}) :auto-awesome-motion])}
   {:label   [icon (sx :.xlarge {:title :auto-awesome-motion}) :auto-awesome-motion],
    :example (example2 [icon :auto-awesome-motion])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :keyboard-return}) :keyboard-return],
    :example (example2 [icon (sx {:-icon-filled? true}) :keyboard-return])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :archive}) :archive],
    :example (example2 [icon (sx {:-icon-filled? true}) :archive])}
   {:label   [icon (sx :.xlarge {:title :archive}) :archive],
    :example (example2 [icon :archive])}

   {:label   [icon (sx :.xlarge {:title :star}) :star],
    :example (example2 [icon :star])}
   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :star-border}) :star-border],
    :example (example2 [icon (sx {:-icon-filled? true}) :star-border])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :check}) :check],
    :example (example2 [icon (sx {:-icon-filled? true}) :check])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :sell}) :sell],
    :example (example2 [icon (sx {:-icon-filled? true}) :sell])}
   {:label   [icon (sx :.xlarge {:title :sell}) :sell],
    :example (example2 [icon :sell])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :visibility}) :visibility],
    :example (example2 [icon (sx {:-icon-filled? true}) :visibility])}
   {:label   [icon (sx :.xlarge {:title :visibility}) :visibility],
    :example (example2 [icon :visibility])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :visibility-off}) :visibility-off],
    :example (example2 [icon (sx {:-icon-filled? true}) :visibility-off])}
   {:label   [icon (sx :.xlarge {:title :visibility-off}) :visibility-off],
    :example (example2 [icon :visibility-off])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :find-replace}) :find-replace],
    :example (example2 [icon (sx {:-icon-filled? true}) :find-replace])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :report-problem}) :report-problem],
    :example (example2 [icon (sx {:-icon-filled? true}) :report-problem])}
   {:label   [icon (sx :.xlarge {:title :report-problem}) :report-problem],
    :example (example2 [icon :report-problem])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :error}) :error],
    :example (example2 [icon (sx {:-icon-filled? true}) :error])}
   {:label   [icon (sx :.xlarge {:title :error}) :error],
    :example (example2 [icon :error])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :check-circle}) :check-circle],
    :example (example2 [icon (sx {:-icon-filled? true}) :check-circle])}
   {:label   [icon (sx :.xlarge {:title :check-circle}) :check-circle],
    :example (example2 [icon :check-circle])}

   {:label   [icon (sx :.xlarge {:title :open-in-new}) :open-in-new],
    :example (example2 [icon  :open-in-new])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :edit}) :edit],
    :example (example2 [icon (sx {:-icon-filled? true}) :edit])}

   {:label   [icon (sx :.xlarge {:title :edit}) :edit],
    :example (example2 [icon :edit])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :favorite}) :favorite],
    :example (example2 [icon (sx {:-icon-filled? true}) :favorite])}

   {:label   [icon (sx :.xlarge {:title :favorite}) :favorite],
    :example (example2 [icon :favorite])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :fingerprint}) :fingerprint],
    :example (example2 [icon (sx {:-icon-filled? true}) :fingerprint])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :help}) :help],
    :example (example2 [icon (sx {:-icon-filled? true}) :help])}
   {:label   [icon (sx :.xlarge {:title :help}) :help],
    :example (example2 [icon :help])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :info}) :info],
    :example (example2 [icon (sx {:-icon-filled? true}) :info])}
   {:label   [icon (sx :.xlarge {:title :info}) :info],
    :example (example2 [icon :info])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :smartphone}) :smartphone],
    :example (example2 [icon (sx {:-icon-filled? true}) :smartphone])}

   {:label   [icon (sx :.xlarge {:-icon-filled? true :title :folder}) :folder],
    :example (example2 [icon (sx {:-icon-filled? true}) :folder])}
   {:label   [icon (sx :.xlarge {:title :folder}) :folder],
    :example (example2 [icon :folder])}

   {:label   [icon
              (sx :.xlarge :c--$red500 {:title :auto-awesome})
              :auto-awesome],
    :key     :auto-awesome-red
    :example (example2 [icon (sx :c--$red500) :auto-awesome])}
   {:label   [icon
              (sx :.xlarge :c--$orange500 {:title :auto-awesome})
              :auto-awesome],
    :key     :auto-awesome-orange
    :example (example2 [icon (sx :c--$orange500) :auto-awesome])}
   {:label   [icon
              (sx :.xlarge :c--$yellow500 {:title :auto-awesome})
              :auto-awesome],
    :key     :auto-awesome-yellow
    :example (example2 [icon (sx :c--$yellow600) :auto-awesome])}
   {:label   [icon
              (sx :.xlarge :c--$green500 {:title :auto-awesome})
              :auto-awesome],
    :key     :auto-awesome-green
    :example (example2 [icon (sx :c--$green500) :auto-awesome])}
   {:label   [icon
              (sx :.xlarge :c--$blue500 {:title :auto-awesome})
              :auto-awesome],
    :key     :auto-awesome-blue
    :example (example2 [icon (sx :c--$blue500) :auto-awesome])}
   {:label   [icon
              (sx :.xlarge :c--$purple500 {:title :auto-awesome})
              :auto-awesome],
    :key     :auto-awesome-purple
    :example (example2 [icon (sx :c--$purple500) :auto-awesome])}
   {:label   [icon
              (sx :.xlarge :c--$magenta500 {:title :auto-awesome})
              :auto-awesome],
    :key     :auto-awesome-magenta
    :example (example2 [icon (sx :c--$magenta500) :auto-awesome])}
                                                                 ])
