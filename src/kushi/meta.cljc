(ns ^:dev/always kushi.meta
  (:require
   [clojure.string :as string]
   [kushi.config :refer [user-config]]))


(defn ns+
  "Creates a string that represents a fully namespaced-qualified
   identifier for an element within a component rendering function.
   String includes line number of parent function. This string is
   used as the value of a custom-data attribute, in order to help
   quickly identify the associated namespace of the specific element
   when inspecting output in an environment such as Chrome DevTools.

   Intended to be called with 1 or 2 arguments:
   1) A var-quoted name of the enclosing component rendering function.
   2) (Optional) A user-defined keyword, which is a semantic name associated with the html element.


   Example:

    (defn my-button [label]
      [:div
      (s+ {:cursor :pointer
           :text-align :center
           :border [[1 :solid :blue]}
          {:role :button
           :on-click #()
           :data-ns (ns+ #'my-button :outer)})
        [:span
         (s+ {:background :yellow}
             {:data-ns (ns+ #'my-button :inner})
         label]])"

  ([x]
   (when #?(:cljs (= cljs.core/Var (type x))
            :clj (clojure.core/var? x))
     (ns+ x nil)))
  ([var-quoted-fn el-ident]
   (let [{ns* :ns name* :name line* :line} (meta var-quoted-fn)
         namespace* (when ns* (str ns* "/"))
         fn-name (when name* (str name*))
         el-ident-str (when el-ident (str (when fn-name "::") (name el-ident)))
         line-number (when line* (str ":" line*))]
     (str namespace* fn-name el-ident-str line-number))))
