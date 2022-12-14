(ns kushi.ui.icon.mui.examples
  (:require
   [kushi.core :refer (sx)]
   [kushi.ui.icon.mui.core :refer (mui-icon mui-icon-outlined)]
   [playground.util :refer-macros (example2)]))

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



#_(def icon-examples*
  (into (mapv (fn [x]
                (let [outlined? (re-find #"\*$" x)
                      x         (string/replace x #"\*$" "")]
                  (if outlined?
                    {:label   [:mui-icon-outlined [:sx :.xlarge {:title x}] x]
                     :example [:example2 [:mui-icon-outlined x]]}
                    {:label   [:mui-icon [:sx :.xlarge {:title x}] x]
                     :example [:example2 [:mui-icon x]]})))
              icons)
        '[
         {:label   [mui-icon (sx :.xlarge :c--$red500 {:title "auto_awesome"}) "auto_awesome"]
          :example (example2 [mui-icon (sx :c--$red500) "auto_awesome"])}
         {:label   [mui-icon (sx :.xlarge :c--$orange500 {:title "auto_awesome"}) "auto_awesome"]
          :example (example2 [mui-icon (sx :c--$orange500) "auto_awesome"])}
         {:label   [mui-icon (sx :.xlarge :c--$yellow500 {:title "auto_awesome"}) "auto_awesome"]
          :example (example2 [mui-icon (sx :c--$yellow500) "auto_awesome"])}
         {:label   [mui-icon (sx :.xlarge :c--$green500 {:title "auto_awesome"}) "auto_awesome"]
          :example (example2 [mui-icon (sx :c--$green500) "auto_awesome"])}
         {:label   [mui-icon (sx :.xlarge :c--$blue500 {:title "auto_awesome"}) "auto_awesome"]
          :example (example2 [mui-icon (sx :c--$blue500) "auto_awesome"])}
         {:label   [mui-icon (sx :.xlarge :c--$purple500 {:title "auto_awesome"}) "auto_awesome"]
          :example (example2 [mui-icon (sx :c--$purple500) "auto_awesome"])}
         {:label   [mui-icon (sx :.xlarge :c--$magenta500 {:title "auto_awesome"}) "auto_awesome"]
          :example (example2 [mui-icon (sx :c--$magenta500) "auto_awesome"])}]))


#_(pprint icon-examples*)


(def icon-examples
  [{:label   [mui-icon (sx :.xlarge {:title "auto_awesome"}) "auto_awesome"],
    :example (example2 [mui-icon "auto_awesome"])}
   {:label   [mui-icon-outlined
              (sx :.xlarge {:title "auto_awesome"})
              "auto_awesome"],
    :example (example2 [mui-icon-outlined "auto_awesome"])}
   {:label   [mui-icon (sx :.xlarge {:title "settings"}) "settings"],
    :example (example2 [mui-icon "settings"])}
   {:label   [mui-icon-outlined (sx :.xlarge {:title "settings"}) "settings"],
    :example (example2 [mui-icon-outlined "settings"])}
   {:label   [mui-icon (sx :.xlarge {:title "search"}) "search"],
    :example (example2 [mui-icon "search"])}
   {:label   [mui-icon (sx :.xlarge {:title "filter_alt"}) "filter_alt"],
    :example (example2 [mui-icon "filter_alt"])}
   {:label   [mui-icon-outlined
              (sx :.xlarge {:title "filter_alt"})
              "filter_alt"],
    :example (example2 [mui-icon-outlined "filter_alt"])}
   {:label   [mui-icon (sx :.xlarge {:title "cloud_upload"}) "cloud_upload"],
    :example (example2 [mui-icon "cloud_upload"])}
   {:label   [mui-icon-outlined
              (sx :.xlarge {:title "cloud_upload"})
              "cloud_upload"],
    :example (example2 [mui-icon-outlined "cloud_upload"])}
   {:label   [mui-icon (sx :.xlarge {:title "download"}) "download"],
    :example (example2 [mui-icon "download"])}
   {:label   [mui-icon-outlined (sx :.xlarge {:title "download"}) "download"],
    :example (example2 [mui-icon-outlined "download"])}
   {:label   [mui-icon (sx :.xlarge {:title "playlist_add"}) "playlist_add"],
    :example (example2 [mui-icon "playlist_add"])}
   {:label   [mui-icon (sx :.xlarge {:title "expand"}) "expand"],
    :example (example2 [mui-icon "expand"])}
   {:label   [mui-icon (sx :.xlarge {:title "compress"}) "compress"],
    :example (example2 [mui-icon "compress"])}
   {:label   [mui-icon (sx :.xlarge {:title "arrow_back"}) "arrow_back"],
    :example (example2 [mui-icon "arrow_back"])}
   {:label   [mui-icon (sx :.xlarge {:title "arrow_forward"}) "arrow_forward"],
    :example (example2 [mui-icon "arrow_forward"])}
   {:label   [mui-icon (sx :.xlarge {:title "sort"}) "sort"],
    :example (example2 [mui-icon "sort"])}
   {:label   [mui-icon (sx :.xlarge {:title "clear"}) "clear"],
    :example (example2 [mui-icon "clear"])}
   {:label   [mui-icon (sx :.xlarge {:title "delete"}) "delete"],
    :example (example2 [mui-icon "delete"])}
   {:label   [mui-icon-outlined (sx :.xlarge {:title "delete"}) "delete"],
    :example (example2 [mui-icon-outlined "delete"])}
   {:label   [mui-icon (sx :.xlarge {:title "cancel"}) "cancel"],
    :example (example2 [mui-icon "cancel"])}
   {:label   [mui-icon-outlined (sx :.xlarge {:title "cancel"}) "cancel"],
    :example (example2 [mui-icon-outlined "cancel"])}
   {:label   [mui-icon
              (sx :.xlarge {:title "auto_awesome_motion"})
              "auto_awesome_motion"],
    :example (example2 [mui-icon "auto_awesome_motion"])}
   {:label   [mui-icon-outlined
              (sx :.xlarge {:title "auto_awesome_motion"})
              "auto_awesome_motion"],
    :example (example2 [mui-icon-outlined "auto_awesome_motion"])}
   {:label   [mui-icon
              (sx :.xlarge {:title "keyboard_return"})
              "keyboard_return"],
    :example (example2 [mui-icon "keyboard_return"])}
   {:label   [mui-icon (sx :.xlarge {:title "archive"}) "archive"],
    :example (example2 [mui-icon "archive"])}
   {:label   [mui-icon-outlined (sx :.xlarge {:title "archive"}) "archive"],
    :example (example2 [mui-icon-outlined "archive"])}
   {:label   [mui-icon (sx :.xlarge {:title "star"}) "star"],
    :example (example2 [mui-icon "star"])}
   {:label   [mui-icon (sx :.xlarge {:title "star_border"}) "star_border"],
    :example (example2 [mui-icon "star_border"])}
   {:label   [mui-icon (sx :.xlarge {:title "check"}) "check"],
    :example (example2 [mui-icon "check"])}
   {:label   [mui-icon (sx :.xlarge {:title "sell"}) "sell"],
    :example (example2 [mui-icon "sell"])}
   {:label   [mui-icon-outlined (sx :.xlarge {:title "sell"}) "sell"],
    :example (example2 [mui-icon-outlined "sell"])}
   {:label   [mui-icon (sx :.xlarge {:title "visibility"}) "visibility"],
    :example (example2 [mui-icon "visibility"])}
   {:label   [mui-icon-outlined
              (sx :.xlarge {:title "visibility"})
              "visibility"],
    :example (example2 [mui-icon-outlined "visibility"])}
   {:label   [mui-icon
              (sx :.xlarge {:title "visibility_off"})
              "visibility_off"],
    :example (example2 [mui-icon "visibility_off"])}
   {:label   [mui-icon-outlined
              (sx :.xlarge {:title "visibility_off"})
              "visibility_off"],
    :example (example2 [mui-icon-outlined "visibility_off"])}
   {:label   [mui-icon (sx :.xlarge {:title "find_replace"}) "find_replace"],
    :example (example2 [mui-icon "find_replace"])}
   {:label   [mui-icon
              (sx :.xlarge {:title "report_problem"})
              "report_problem"],
    :example (example2 [mui-icon "report_problem"])}
   {:label   [mui-icon-outlined
              (sx :.xlarge {:title "report_problem"})
              "report_problem"],
    :example (example2 [mui-icon-outlined "report_problem"])}
   {:label   [mui-icon (sx :.xlarge {:title "error"}) "error"],
    :example (example2 [mui-icon "error"])}
   {:label   [mui-icon (sx :.xlarge {:title "error_outline"}) "error_outline"],
    :example (example2 [mui-icon "error_outline"])}
   {:label   [mui-icon (sx :.xlarge {:title "check_circle"}) "check_circle"],
    :example (example2 [mui-icon "check_circle"])}
   {:label   [mui-icon-outlined
              (sx :.xlarge {:title "check_circle"})
              "check_circle"],
    :example (example2 [mui-icon-outlined "check_circle"])}
   {:label   [mui-icon (sx :.xlarge {:title "open_in_new"}) "open_in_new"],
    :example (example2 [mui-icon "open_in_new"])}
   {:label   [mui-icon (sx :.xlarge {:title "edit"}) "edit"],
    :example (example2 [mui-icon "edit"])}
   {:label   [mui-icon-outlined (sx :.xlarge {:title "edit"}) "edit"],
    :example (example2 [mui-icon-outlined "edit"])}
   {:label   [mui-icon (sx :.xlarge {:title "favorite"}) "favorite"],
    :example (example2 [mui-icon "favorite"])}
   {:label   [mui-icon
              (sx :.xlarge {:title "favorite_border"})
              "favorite_border"],
    :example (example2 [mui-icon "favorite_border"])}
   {:label   [mui-icon (sx :.xlarge {:title "fingerprint"}) "fingerprint"],
    :example (example2 [mui-icon "fingerprint"])}
   {:label   [mui-icon (sx :.xlarge {:title "help"}) "help"],
    :example (example2 [mui-icon "help"])}
   {:label   [mui-icon (sx :.xlarge {:title "help_outline"}) "help_outline"],
    :example (example2 [mui-icon "help_outline"])}
   {:label   [mui-icon (sx :.xlarge {:title "info"}) "info"],
    :example (example2 [mui-icon "info"])}
   {:label   [mui-icon (sx :.xlarge {:title "info_outline"}) "info_outline"],
    :example (example2 [mui-icon "info_outline"])}
   {:label   [mui-icon (sx :.xlarge {:title "smartphone"}) "smartphone"],
    :example (example2 [mui-icon "smartphone"])}
   {:label   [mui-icon (sx :.xlarge {:title "folder"}) "folder"],
    :example (example2 [mui-icon "folder"])}
   {:label   [mui-icon-outlined (sx :.xlarge {:title "folder"}) "folder"],
    :example (example2 [mui-icon-outlined "folder"])}
   {:label   [mui-icon
              (sx :.xlarge :c--$red500 {:title "auto_awesome"})
              "auto_awesome"],
    :key     "auto_awesome-red"
    :example (example2 [mui-icon (sx :c--$red500) "auto_awesome"])}
   {:label   [mui-icon
              (sx :.xlarge :c--$orange500 {:title "auto_awesome"})
              "auto_awesome"],
    :key     "auto_awesome-orange"
    :example (example2 [mui-icon (sx :c--$orange500) "auto_awesome"])}
   {:label   [mui-icon
              (sx :.xlarge :c--$yellow500 {:title "auto_awesome"})
              "auto_awesome"],
    :key     "auto_awesome-yellow"
    :example (example2 [mui-icon (sx :c--$yellow500) "auto_awesome"])}
   {:label   [mui-icon
              (sx :.xlarge :c--$green500 {:title "auto_awesome"})
              "auto_awesome"],
    :key     "auto_awesome-green"
    :example (example2 [mui-icon (sx :c--$green500) "auto_awesome"])}
   {:label   [mui-icon
              (sx :.xlarge :c--$blue500 {:title "auto_awesome"})
              "auto_awesome"],
    :key     "auto_awesome-blue"
    :example (example2 [mui-icon (sx :c--$blue500) "auto_awesome"])}
   {:label   [mui-icon
              (sx :.xlarge :c--$purple500 {:title "auto_awesome"})
              "auto_awesome"],
    :key     "auto_awesome-purple"
    :example (example2 [mui-icon (sx :c--$purple500) "auto_awesome"])}
   {:label   [mui-icon
              (sx :.xlarge :c--$magenta500 {:title "auto_awesome"})
              "auto_awesome"],
    :key     "auto_awesome-magenta"
    :example (example2 [mui-icon (sx :c--$magenta500) "auto_awesome"])}])
