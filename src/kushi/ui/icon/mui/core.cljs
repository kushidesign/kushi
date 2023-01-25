(ns kushi.ui.icon.mui.core
  (:require
   [clojure.string :as string]
   [kushi.core :refer (merge-attrs sx inject-stylesheet defclass)]
   [kushi.ui.core :refer (defcom)]))

(inject-stylesheet {:rel "preconnet"
                    :href "https://fonts.gstatic.com"
                    :cross-origin "anonymous"})
(inject-stylesheet {:rel "preconnet"
                    :href "https://fonts.googleapis.com"})
(inject-stylesheet {:rel "stylesheet"
                    :href "https://fonts.googleapis.com/css2?family=Material+Icons&family=Material+Icons+Outlined&family=Material+Icons+Sharp&family=Material+Icons+Round&family=Material+Icons+Two+Tone"})

(defn kw->string [coll]
  (mapv #(cond
           (= % :<>)
           :<>
           (keyword? %)
           (-> % name (string/replace #"-" "_"))
           :else %)
        coll))

(defclass kushi-mui-icon
  :.relative
  :d--inline-flex
  :flex-direction--row
  :jc--c
  :ta--center
  :ai--c
  [:>span:fs "var(--mui-icon-relative-font-size, inherit)"])

(def mui-icon-svgs
  {
  ;;  "add"       [:svg {:xmlns   "http://www.w3.org/2000/svg"
  ;;                     :viewBox "0 0 20 20"}
  ;;               [:path {:d "M9.25 15v-4.25H5v-1.5h4.25V5h1.5v4.25H15v1.5h-4.25V15Z"}]]
   "add"       [:svg {:xmlns  "http://www.w3.org/2000/svg"
                      :viewBox "0 0 40 40"}
                [:path {:d "M18.625 31.667V21.375H8.333v-2.75h10.292V8.333h2.75v10.292h10.292v2.75H21.375v10.292Z"}]]
   "dark_mode" [:svg {:xmlns   "http://www.w3.org/2000/svg"
                      :viewBox "0 0 20 20"}
                [:path {:d "M10 17.5q-3.125 0-5.312-2.188Q2.5 13.125 2.5 10q0-3.125 2.188-5.312Q6.875 2.5 10 2.5q.292 0 .573.021.281.021.552.062-.854.605-1.365 1.573-.51.969-.51 2.094 0 1.875 1.312 3.188 1.313 1.312 3.188 1.312 1.146 0 2.104-.51.958-.511 1.563-1.365.041.271.062.552.021.281.021.573 0 3.125-2.188 5.312Q13.125 17.5 10 17.5Zm0-1.75q1.792 0 3.198-.938 1.406-.937 2.073-2.5-.375.105-.75.146-.375.042-.771.042-2.604 0-4.427-1.823Q7.5 8.854 7.5 6.25q0-.396.042-.771.041-.375.146-.75-1.563.667-2.5 2.073Q4.25 8.208 4.25 10q0 2.396 1.677 4.073Q7.604 15.75 10 15.75Zm-.229-5.521Z"}]] })


(defcom mui-icon-outlined
  [:div
   (merge-attrs
    (sx
     'kushi-mui-icon
     :&_svg>path:fill--currentColor
     {:data-kushi-ui :mui-icon-outlined})
    &attrs)
   (let [icon-name (kw->string &children)]
     (or
       (:icon-svg &opts)
       [:span.material-icons-outlined icon-name]))])

(defcom mui-icon-round
  [:div
   (merge-attrs
    (sx
     'kushi-mui-icon
     {:data-kushi-ui :mui-icon-round})
    &attrs)
   [:span.material-icons-round (kw->string &children)]])

(defcom mui-icon-sharp
  [:div
   (merge-attrs
    (sx
     'kushi-mui-icon
     {:data-kushi-ui :mui-icon-sharp})
    &attrs)
   [:span.material-icons-sharp (kw->string &children)]])

(defcom mui-icon-two-tone
  [:div
   (merge-attrs
    (sx
     'kushi-mui-icon
     {:data-kushi-ui :mui-icon-two-tone})
    &attrs)
   [:span.material-icons-two-tone (kw->string &children)]])

(defcom mui-icon
  [:div
   (merge-attrs
    (sx
     'kushi-mui-icon
     {:data-kushi-ui :mui-icon})
    &attrs)
   [:span.material-icons (kw->string &children)]])
