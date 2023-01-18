(ns kushi.ui.icon.mui.examples
  (:require
   [kushi.core :refer (sx)]
   [kushi.ui.icon.mui.core :refer (mui-icon mui-icon-outlined)]
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
  [{:label   [mui-icon (sx :.xlarge {:title :auto-awesome}) :auto-awesome],
    :example (example2 [mui-icon :auto-awesome])}
   {:label   [mui-icon-outlined
              (sx :.xlarge {:title :auto-awesome})
              :auto-awesome],
    :example (example2 [mui-icon-outlined :auto-awesome])}
   {:label   [mui-icon (sx :.xlarge {:title :settings}) :settings],
    :example (example2 [mui-icon :settings])}
   {:label   [mui-icon-outlined (sx :.xlarge {:title :settings}) :settings],
    :example (example2 [mui-icon-outlined :settings])}
   {:label   [mui-icon (sx :.xlarge {:title :search}) :search],
    :example (example2 [mui-icon :search])}
   {:label   [mui-icon (sx :.xlarge {:title :filter-alt}) :filter-alt],
    :example (example2 [mui-icon :filter-alt])}
   {:label   [mui-icon-outlined
              (sx :.xlarge {:title :filter-alt})
              :filter-alt],
    :example (example2 [mui-icon-outlined :filter-alt])}
   {:label   [mui-icon (sx :.xlarge {:title :cloud-upload}) :cloud-upload],
    :example (example2 [mui-icon :cloud-upload])}
   {:label   [mui-icon-outlined
              (sx :.xlarge {:title :cloud-upload})
              :cloud-upload],
    :example (example2 [mui-icon-outlined :cloud-upload])}
   {:label   [mui-icon (sx :.xlarge {:title :download}) :download],
    :example (example2 [mui-icon :download])}
   {:label   [mui-icon-outlined (sx :.xlarge {:title :download}) :download],
    :example (example2 [mui-icon-outlined :download])}
   {:label   [mui-icon (sx :.xlarge {:title :playlist-add}) :playlist-add],
    :example (example2 [mui-icon :playlist-add])}
   {:label   [mui-icon (sx :.xlarge {:title :expand}) :expand],
    :example (example2 [mui-icon :expand])}
   {:label   [mui-icon (sx :.xlarge {:title :compress}) :compress],
    :example (example2 [mui-icon :compress])}
   {:label   [mui-icon (sx :.xlarge {:title :arrow-back}) :arrow-back],
    :example (example2 [mui-icon :arrow-back])}
   {:label   [mui-icon (sx :.xlarge {:title :arrow-forward}) :arrow-forward],
    :example (example2 [mui-icon :arrow-forward])}
   {:label   [mui-icon (sx :.xlarge {:title :sort}) :sort],
    :example (example2 [mui-icon :sort])}
   {:label   [mui-icon (sx :.xlarge {:title :clear}) :clear],
    :example (example2 [mui-icon :clear])}
   {:label   [mui-icon (sx :.xlarge {:title :delete}) :delete],
    :example (example2 [mui-icon :delete])}
   {:label   [mui-icon-outlined (sx :.xlarge {:title :delete}) :delete],
    :example (example2 [mui-icon-outlined :delete])}
   {:label   [mui-icon (sx :.xlarge {:title :cancel}) :cancel],
    :example (example2 [mui-icon :cancel])}
   {:label   [mui-icon-outlined (sx :.xlarge {:title :cancel}) :cancel],
    :example (example2 [mui-icon-outlined :cancel])}
   {:label   [mui-icon
              (sx :.xlarge {:title :auto-awesome-motion})
              :auto-awesome-motion],
    :example (example2 [mui-icon :auto-awesome-motion])}
   {:label   [mui-icon-outlined
              (sx :.xlarge {:title :auto-awesome-motion})
              :auto-awesome-motion],
    :example (example2 [mui-icon-outlined :auto-awesome-motion])}
   {:label   [mui-icon
              (sx :.xlarge {:title :keyboard-return})
              :keyboard-return],
    :example (example2 [mui-icon :keyboard-return])}
   {:label   [mui-icon (sx :.xlarge {:title :archive}) :archive],
    :example (example2 [mui-icon :archive])}
   {:label   [mui-icon-outlined (sx :.xlarge {:title :archive}) :archive],
    :example (example2 [mui-icon-outlined :archive])}
   {:label   [mui-icon (sx :.xlarge {:title :star}) :star],
    :example (example2 [mui-icon :star])}
   {:label   [mui-icon (sx :.xlarge {:title :star-border}) :star-border],
    :example (example2 [mui-icon :star-border])}
   {:label   [mui-icon (sx :.xlarge {:title :check}) :check],
    :example (example2 [mui-icon :check])}
   {:label   [mui-icon (sx :.xlarge {:title :sell}) :sell],
    :example (example2 [mui-icon :sell])}
   {:label   [mui-icon-outlined (sx :.xlarge {:title :sell}) :sell],
    :example (example2 [mui-icon-outlined :sell])}
   {:label   [mui-icon (sx :.xlarge {:title :visibility}) :visibility],
    :example (example2 [mui-icon :visibility])}
   {:label   [mui-icon-outlined
              (sx :.xlarge {:title :visibility})
              :visibility],
    :example (example2 [mui-icon-outlined :visibility])}
   {:label   [mui-icon
              (sx :.xlarge {:title :visibility-off})
              :visibility-off],
    :example (example2 [mui-icon :visibility-off])}
   {:label   [mui-icon-outlined
              (sx :.xlarge {:title :visibility-off})
              :visibility-off],
    :example (example2 [mui-icon-outlined :visibility-off])}
   {:label   [mui-icon (sx :.xlarge {:title :find-replace}) :find-replace],
    :example (example2 [mui-icon :find-replace])}
   {:label   [mui-icon
              (sx :.xlarge {:title :report-problem})
              :report-problem],
    :example (example2 [mui-icon :report-problem])}
   {:label   [mui-icon-outlined
              (sx :.xlarge {:title :report-problem})
              :report-problem],
    :example (example2 [mui-icon-outlined :report-problem])}
   {:label   [mui-icon (sx :.xlarge {:title :error}) :error],
    :example (example2 [mui-icon :error])}
   {:label   [mui-icon (sx :.xlarge {:title :error-outline}) :error-outline],
    :example (example2 [mui-icon :error-outline])}
   {:label   [mui-icon (sx :.xlarge {:title :check-circle}) :check-circle],
    :example (example2 [mui-icon :check-circle])}
   {:label   [mui-icon-outlined
              (sx :.xlarge {:title :check-circle})
              :check-circle],
    :example (example2 [mui-icon-outlined :check-circle])}
   {:label   [mui-icon (sx :.xlarge {:title :open-in-new}) :open-in-new],
    :example (example2 [mui-icon :open-in-new])}
   {:label   [mui-icon (sx :.xlarge {:title :edit}) :edit],
    :example (example2 [mui-icon :edit])}
   {:label   [mui-icon-outlined (sx :.xlarge {:title :edit}) :edit],
    :example (example2 [mui-icon-outlined :edit])}
   {:label   [mui-icon (sx :.xlarge {:title :favorite}) :favorite],
    :example (example2 [mui-icon :favorite])}
   {:label   [mui-icon
              (sx :.xlarge {:title :favorite-border})
              :favorite-border],
    :example (example2 [mui-icon :favorite-border])}
   {:label   [mui-icon (sx :.xlarge {:title :fingerprint}) :fingerprint],
    :example (example2 [mui-icon :fingerprint])}
   {:label   [mui-icon (sx :.xlarge {:title :help}) :help],
    :example (example2 [mui-icon :help])}
   {:label   [mui-icon (sx :.xlarge {:title :help-outline}) :help-outline],
    :example (example2 [mui-icon :help-outline])}
   {:label   [mui-icon (sx :.xlarge {:title :info}) :info],
    :example (example2 [mui-icon :info])}
   {:label   [mui-icon (sx :.xlarge {:title :info-outline}) :info-outline],
    :example (example2 [mui-icon :info-outline])}
   {:label   [mui-icon (sx :.xlarge {:title :smartphone}) :smartphone],
    :example (example2 [mui-icon :smartphone])}
   {:label   [mui-icon (sx :.xlarge {:title :folder}) :folder],
    :example (example2 [mui-icon :folder])}
   {:label   [mui-icon-outlined (sx :.xlarge {:title :folder}) :folder],
    :example (example2 [mui-icon-outlined :folder])}
   {:label   [mui-icon
              (sx :.xlarge :c--$red500 {:title :auto-awesome})
              :auto-awesome],
    :key     :auto-awesome-red
    :example (example2 [mui-icon (sx :c--$red500) :auto-awesome])}
   {:label   [mui-icon
              (sx :.xlarge :c--$orange500 {:title :auto-awesome})
              :auto-awesome],
    :key     :auto-awesome-orange
    :example (example2 [mui-icon (sx :c--$orange500) :auto-awesome])}
   {:label   [mui-icon
              (sx :.xlarge :c--$yellow500 {:title :auto-awesome})
              :auto-awesome],
    :key     :auto-awesome-yellow
    :example (example2 [mui-icon (sx :c--$yellow500) :auto-awesome])}
   {:label   [mui-icon
              (sx :.xlarge :c--$green500 {:title :auto-awesome})
              :auto-awesome],
    :key     :auto-awesome-green
    :example (example2 [mui-icon (sx :c--$green500) :auto-awesome])}
   {:label   [mui-icon
              (sx :.xlarge :c--$blue500 {:title :auto-awesome})
              :auto-awesome],
    :key     :auto-awesome-blue
    :example (example2 [mui-icon (sx :c--$blue500) :auto-awesome])}
   {:label   [mui-icon
              (sx :.xlarge :c--$purple500 {:title :auto-awesome})
              :auto-awesome],
    :key     :auto-awesome-purple
    :example (example2 [mui-icon (sx :c--$purple500) :auto-awesome])}
   {:label   [mui-icon
              (sx :.xlarge :c--$magenta500 {:title :auto-awesome})
              :auto-awesome],
    :key     :auto-awesome-magenta
    :example (example2 [mui-icon (sx :c--$magenta500) :auto-awesome])}])
