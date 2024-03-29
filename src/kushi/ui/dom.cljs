(ns kushi.ui.dom
  (:require
   [clojure.string :as string]
   [applied-science.js-interop :as j]))


(defn css-style-string [m]
  (string/join ";"
               (map (fn [[k v]]
                      (str (name k)
                           ":"
                           (if (number? v) (str v) (name v))))
                    m)))

;; https://gist.github.com/rotaliator/73daca2dc93c586122a0da57189ece13
(defn copy-to-clipboard [val]
  (let [el (js/document.createElement "textarea")]
    (set! (.-value el) val)
    (.appendChild js/document.body el)
    (.select el)
    (js/document.execCommand "copy")
    (.removeChild js/document.body el)))

(defn screen-quadrant-from-point
  "Pass an x and y val and get a tuple back reprenting the quadrant in which the point lives.
   (screen-quadrant 10 20) => [:top :left]"
  [x y]
  (let [top? (> 0.5 (/ y js/window.innerHeight))
        left? (> 0.5 (/ x js/window.innerWidth))]
    [(if top? :top :bottom) (if left? :left :right)]))

(defn screen-quadrant
  "Pass a dom node and get a tuple back reprenting the quadrant in which the center of the node lives.
   (screen-quadrant (js/document.getElementById \"my-id\")) => [:top :left]"
  [node]
  (let [rect (.getBoundingClientRect node)
        x* (* 0.5 (- (.-right rect) (.-left rect)))
        x  (+ (.-left rect) x*)
        y* (* 0.5 (- (.-bottom rect) (.-top rect)))
        y  (+ (.-top rect) y*)
        top? (> 0.5 (/ y js/window.innerHeight))
        left? (> 0.5 (/ x js/window.innerWidth))]
    [(if top? :top :bottom) (if left? :left :right)]))

(defn set-overlay-position!
  [node parent]
  (let [[tb lr]         (screen-quadrant parent)
        position-block  (.getAttribute node "data-kushi-tooltip-position-block")
        position-inline (.getAttribute node "data-kushi-tooltip-position-inline")
        right?          (cond (= "end" position-inline)
                              true
                              (= "start" position-inline)
                              false
                              :else
                              (= lr :right))
        top?            (cond (= "end" position-block)
                              true
                              (= "start" position-block)
                              false
                              :else
                              (= tb :top))]

    ;; (js/console.log right? top?)
    ;; display on right or left
    ;; (set! node.style.left (if right? "unset" "100%"))
    ;; (set! node.style.right (if right? "100%" "unset"))
    ;; (set! node.style.top (if top? 0 "unset"))
    ;; (set! node.style.bottom (if top? "unset" 0))
    ;; display on top or bottom

    (set! node.style.left (if right? "unset" "0"))
    (set! node.style.right (if right? "0" "unset"))
    (set! node.style.top (if top? "100%" "unset"))
    (set! node.style.bottom (if top? "unset" "100%"))))

(defn conditional-display? [node]
  (= "true" (.getAttribute node "data-kushi-conditional-display")))

(defn toggle-boolean-attribute [node attr]
  (let [attr-val (.getAttribute node (name attr))
        newv (if (= attr-val "false") true false)]
    (.setAttribute node (name attr) newv)))

(defn parent [node] (some-> node .-parentNode))
(defn next-element-sibling [node] (some-> node .-nextElementSibling))
(defn previous-element-sibling [node] (some-> node .-previousElementSibling))
(defn grandparent [node] (some-> node .-parentNode .-parentNode))
(defn has-class [node classname] (some-> node .-classList (.contains (name classname))))
(defn attribute-true? [node attribute] (when node (= "true" (.getAttribute node (name attribute)))))

(def dom-el-attributes
  [[:cec :childElementCount]
   [:ch :clientHeight]
   [:cl :clientLeft]
   [:ct :clientTop]
   [:cw :clientWidth]
   [:fc :firstChild]
   [:fec :firstElementChild]
   [:ih :innerHTML]
   [:lc :lastChild]
   [:lec :lastElementChild]
   [:nes :nextElementSibling]
   [:ns :nextSibling]
   [:oh :outerHTML]
   [:pe :parentElement]
   [:pes :previousElementSibling]
   [:pn :parentNode]
   [:ps :previousSibling]
   [:sh :scrollHeight]
   [:sl :scrollLeft]
   [:slm :scrollLeftMax]
   [:st :scrollTop]
   [:sw :scrollWidth]
   [:tc :textContent]
   [:tn :tagName]])

(defn el+ [el]
  (doseq [[sh og] dom-el-attributes]
    (j/assoc! el (name sh) (j/get el og)))
  (let [parent (-> el .-parentNode)
        gpn (.-parentNode parent)
        ggpn (.-parentNode gpn)]
    (j/assoc! el "pnns" (j/get parent :nextSibling))
    (j/assoc! el "pnps" (j/get parent :previousSibling))
    (j/assoc! el "gpn" gpn)
    (j/assoc! el "gpnns" (j/get gpn :nextSibling))
    (j/assoc! el "gpnps" (j/get gpn :previousSibling))
    (j/assoc! el "ggpn" ggpn)
    el))

(defn event-target [e {:keys [attrs]}]
  ;; check node type
  (let [el (some-> e .-target)]
    (doseq [attr attrs]
      (j/assoc! el (name attr) (j/get el attr)))))

(defn cet [e] (some-> e .-currentTarget))
(defn cetv [e] (some-> e .-currentTarget .-value))
(defn et [e] (some-> e .-target))
(defn et+ [e] (some-> e .-target el+))
(defn etv [e] (some-> e .-target .-value))
(defn etv->int [e] (some-> e .-target .-value js/parseInt))
(defn etv->float [e] (some-> e .-target .-value js/parseFloat))

(defn el-by-id [s] (js/document.getElementById s))

(defn get-first-onscreen-child-from-top [parent]
  (first (filter (fn [node] (pos? (.-top (.getBoundingClientRect node)))) (js->clj parent.childNodes))))

(defn nearest-ancestor [node selector]
  (.closest node selector))

(defn toggle-class! [el & xs]
  (doseq [x xs] (.toggle (.-classList el) (name x))))

(defn remove-class [el & xs]
  (doseq [x xs] (.remove (.-classList el) (name x))))

(defn add-class [el & xs]
  (doseq [x xs] (.add (.-classList el) (name x))))

(defn set-css-var! [el prop val]
  (when el (.setProperty el.style prop val)))

(defn set-client-wh-css-vars! [el]
  (when el
    (set-css-var! el "--client-width" (str el.clientWidth "px"))
    (set-css-var! el "--client-height" (str el.clientHeight "px"))))

(defn set-neg-client-wh-css-vars! [el]
  (set-css-var! el "--client-width" (str "-" el.clientWidth "px"))
  (set-css-var! el "--client-height" (str "-" el.clientHeight "px")))

(defn set-style!* [el prop s]
  (when el (.setProperty el.style (name prop) s)))

(defn set-style! [el prop s]
  (if (coll? el)
    (doseq [x el] (set-style!* x prop s))
    (set-style!* el prop s)))

(defn set-attribute! [el attr v]
  (when el (.setAttribute el (name attr) v)))

(defn set-property! [el attr v]
  (when el (.setProperty el (name attr) v)))

(defn set! [el attr v]
  (when el (j/assoc! el (name attr) v)))

(defn has-class? [el s]
  (when el (.contains (.-classList el) (name s))))

;; TODO Rename?
(defn has-class-or-ancestor-with-class? [el]
  (boolean (or (has-class? el "dropdown-list-item")
               (nearest-ancestor el ".dropdown-list-item"))))

(defn el-idx
  "Get index of element, relative to its parent"
  [el]
  (when-let [parent (some-> el .-parentNode)]
    (let [children-array (.from js/Array (.-children parent))]
      (.indexOf children-array el))))

(defn observe-intersection
  [{:keys [element intersecting not-intersecting f threshold]
    :or {threshold 0.1}}]
  (when element
    (let [observer (js/IntersectionObserver.
                    (fn [^js entries]
                      (if (.-isIntersecting (aget entries 0))
                        (when intersecting (intersecting))
                        (when not-intersecting (not-intersecting)))
                      (when f (f)))
                    #js {:threshold threshold})]
      (when observer
        (.observe observer element)))))

(defn scroll-by
  [{:keys [x y behavior]
    :or   {x        0
           y        0
           behavior "auto"}}]
  (let [behavior (name behavior)]
    (j/call
     js/window
     :scrollBy
     #js
      {"top" y "left" x "behavior" behavior})))

(defn scroll-into-view
  ([el]
   (scroll-into-view el {}))
  ([el {:keys [inline block behavior]
        :or {block "start" inline "nearest" behavior "auto"}}]
   (let [opts {"block" (name block) "inline" (name inline) "behavior" (name behavior)}]
     (j/call el :scrollIntoView (clj->js opts)))))

(defn scroll-to-top [] (js/window.scrollTo 0 0))

(defn writing-direction []
  (.-direction (js/window.getComputedStyle js/document.documentElement)))

(defn dev-only [x]
  (when ^boolean js/goog.DEBUG x))


;; Events


;; Primitive Zipper navigation
(def zip-nav 
  {"^"     :parentNode
   "up"    :parentNode

   "V"     :firstElementChild
   "v"     :firstElementChild
   "down"  :firstElementChild

   ">"     :nextElementSibling
   "right" :nextElementSibling

   "<"     :previousElementSibling
   "left"  :previousElementSibling
   })

(defn zip-get [el steps]
  (reduce (fn [el x]
            (let [k (get zip-nav x x)]
              ;; TODO - Warning here in case x in not one of:
              ;; :parentNode ;; :firstElementChild ;; :nextElementSibling ;; :previousElementSibling
              (some-> el (j/get k nil))))
          el
          (if (string? steps)
            (string/split steps #" ")
            steps)))

;; querySelector
(defn data-selector= [data-attr v]
  (str "[data-" (name data-attr) "=\"" v "\"]"))

(defn value-selector= [v]
  (str "[value=\"" (str v) "\"]"))

(defn qs 
  ([s]
   (qs js/document s))
  ([el s]
   (.querySelector el s)))

(defn qs-data=
  ([data-attr v]
   (qs-data= js/document data-attr (str v)))
  ([el data-attr v]
   (.querySelector el (data-selector= data-attr (str v)))))

;; focus

;;macro?
(defn focus! [el] (some-> el .focus))

;;macro?
(defn click! [el] (some-> el .click))

;; data-* attribute
;;macro?
(defn data-attr [el nm]
  (.getAttribute el (str "data-" (name nm))))

;; node types
(defn node-is-of-type? [el s]
  (boolean (some-> el .-nodeName string/lower-case (= s))))

(defn el-type [el]
  (some-> el .-nodeName string/lower-case keyword))

;; keypresses
;;macro?
(defn arrow-keycode? [e]
  (< 36 e.keyCode 41))

;; text input
(defn set-caret! [el i]
  (some-> el (.setSelectionRange i i))
  i)

;;macro?
(defn prevent-default! [e] 
  (some-> e .preventDefault))

;;macro?
(defn click-xy [e]
  [e.clientX e.clientY])

;;macro?
(defn el-from-point [x y]
  (.elementFromPoint js/document x y))

;; geometry
(defn client-rect [el]
(j/let [^:js {:keys [left right top bottom x y width height]} (.getBoundingClientRect el)]
  {:left left
   :right right
   :top  top 
   :bottom bottom
   :x x
   :y y
   :width width
   :height height}))
