(ns kushi.sheets)

(def sheet-ids-by-type
  {;; Built-in kushi utility base classes
   ;; TODO change to kushi-defclass-utility
   :kushi-atomic            "_kushi-defclass-utility_"

   ;; Injection to sync with css file output
   :kushi-css-sync "_kushi-css-sync_"

   ;; Built-in kushi utility classes designed to override (decorate)
   ;; TODO change to :kushi-defclass-overrides
   :defclass-kushi-override "_kushi-defclass-overrides_"

   ;; User-defined utility classes designed to override (decorate)
   ;; TODO change to :kushi-defclass-user-overrides
   :defclass-user-override  "_kushi-defclass-user-overrides_"})

;; keep these synced with above map
(def sheet-types-ordered
  [:kushi-atomic
   :kushi-css-sync
   :defclass-kushi-override
   :defclass-user-override])
