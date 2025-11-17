;; TODO - Delete this namespace when no longer needed.

;; This namespace exists solely for the purpose of including legacy css via 
;; includes in kushi/core.cljs. Those includes can't use kushi.core/defcss
;; because of circular reference.

(ns kushi.css.build.macros)

(defmacro ^:public defcss
  "Used to define shared css rulesets.
   `sel` must be a valid css selector in the form of a string.
   `args` must be valid style args, same as the `css` and `sx` macros.
   The function call will be picked up in the analyzation phase of a build, then fed to `css-rule` to produce a css rule that will be written to disk.
   Expands to nil."
  [sel & args]
  nil)

(defmacro ^:public defcolorway
  "Used internally to define colorway rulesets for kushi ui theming system.
   `s` must be a string that maps to a color-token base e.g. `\"red\"`, 
   `\"warning\"`, etc.
   The function call will be picked up in the analyzation phase of a build,
   then fed to `css-colorway` to produce a css rule that will be written to disk.
   Expands to nil."
  [s]
  nil)
