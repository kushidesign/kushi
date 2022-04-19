(ns kushi.sheets)

(def sheet-ids-by-type
  {
   ;; Custom CSS properties
   ;; These are injected from theme (and by user, in future)
   :custom-properties       "_kushi-rules-custom-properties_"

   ;; Built-in utility classes
   :kushi-atomic            "_kushi-rules-utility_"

   ;; Built-in Kushi utility classes
   :defclass                "_kushi-rules-shared_"

   ;;
   :theme                   "_kushi-rules-theme_"

   :ui                      "_kushi-rules-ui_"

   :sx                      "_kushi-rules_"

   ;; Built-in Kushi utility classes designed to override component styles
   :defclass-kushi-override "_kushi-rules-overrides_"

   ;; User-defined utility classes designed to override component styles
   :defclass-user-override  "_kushi-rules-user-overrides_"
   })
