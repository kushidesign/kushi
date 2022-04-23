(ns kushi.sheets)

(def sheet-ids-by-type
  {
   ;; Custom CSS properties / global tokens
   ;; These are injected from theme (and ala carte, by user, in future)
   :global-tokens           "_kushi-global-tokens"

   ;; Custom CSS properties / alias tokens
   ;; These are injected from theme (and ala carte, by user, in future)
   :alias-tokens            "_kushi-alias-tokens"

   ;; Built-in kushi utility base classes
   ;; Change to kushi-utility
   :kushi-atomic            "_kushi-defclass-utility_"

   ;; User defined shared classes
   :defclass                "_kushi-defclass-user"

   ;; Maybe lose this?
   :theme                   "_kushi-defclass-theme_"

   ;; Styles from reusable kushi-ui components or user-authored reusable components
   ;; These are override-able if a reusable component is instantiated with a decorator attr map
   :ui                      "_kushi-sx-ui_"

   ;; Styles (generally) from user calls to kushi.core/sx
   :sx                      "_kushi-sx_"

   ;; Built-in kushi utility classes designed to override (decorate)
   :defclass-kushi-override "_kushi-defclass-overrides_"

   ;; User-defined utility classes designed to override (decorate)
   :defclass-user-override  "_kushi-defclass-user-overrides_"
     })

;; keep these synced with above map
(def sheet-types-ordered
  [:global-tokens
   :alias-tokens
   :kushi-atomic
   :defclass
   :theme
   :ui
   :sx
   :defclass-kushi-override
   :defclass-user-override])
