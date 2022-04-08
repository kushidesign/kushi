(ns ^:dev/always kushi.clean
  (:require [clojure.string :as string]))

;; This a toggle for debugging while developing kushi itself
(def log-clean? false)

(defn log-cleaning-summary!
  [rules rules-len sheet-id]
  (let [log-msg (if (pos? rules-len)
                  (str "Deleting " rules-len " rule(s) in stylesheet #" sheet-id " ...")
                  (str "No rules to delete in stylesheet #" sheet-id))]
    (js/console.log
     (str "[kushi.core/clean!][#" sheet-id "]\n")
     rules
     (str "\n   " log-msg))))

(defn log-cleaning-rule-at-idx!
  [rules idx]
  (js/console.log "      "
                  (-> (.-cssText (aget rules idx))
                      (string/replace  #"\{.*\}" "{ ... }")
                      (string/replace  #"\n" ""))))
(defn clean!
  "Removes all existing styles that were injected into
   #_kushi-rules-shared_ or #_kushi-rules_ stylesheets at dev time.
   Is called automatically in dev builds on every save/reload,
   for the purpose of keeping the kushi style sheet lengths under control."
  [ids]
  (let [log?  log-clean?]
    (when ^boolean js/goog.DEBUG
      (doseq [sheet-id ids]
        (when-let [stylesheet-el (js/document.getElementById sheet-id)]
          (let [sheet     (.-sheet stylesheet-el)
                rules     (.-rules sheet)
                rules-len (.-length rules)]
            (when log? (log-cleaning-summary! rules rules-len sheet-id))
            (doseq [idx (reverse (range rules-len))]
              (when log? (log-cleaning-rule-at-idx! rules idx))
              (.deleteRule sheet idx))))))))
