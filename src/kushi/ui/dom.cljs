(ns kushi.ui.dom
  (:require
   [applied-science.js-interop :as j]
   [camel-snake-kebab.core :as csk]))


;; TODO organized this by Global and Element
#_(sort-by
first
    (map (fn [x] (let [kebab     (csk/->kebab-case x)
                       kw        (keyword x)
                       kushi*    (string/split (name kebab) #"-")
                       kushi     (->> kushi* (map first) string/join keyword)
                       attribute x
                       attribute-kw (keyword x)
                       ]
                   #_(keyed kebab kw kushi attribute)
                   [kushi attribute-kw]))
         '[ariaAtomic
           ariaAutoComplete
           ariaBusy
           ariaChecked
           ariaColCount
           ariaColIndex
           ariaColIndexText
           ariaColSpan
           ariaCurrent
           ariaDescription
           ariaDisabled
           ariaExpanded
           ariaHasPopup
           ariaHidden
           ariaKeyShortcuts
           ariaLabel
           ariaLevel
           ariaLive
           ariaModal
           ariaMultiLine
           ariaMultiSelectable
           ariaOrientation
           ariaPlaceholder
           ariaPosInSet
           ariaPressed
           ariaReadOnly
           ariaRelevant
           ariaRequired
           ariaRoleDescription
           ariaRowCount
           ariaRowIndex
           ariaRowIndexText
           ariaRowSpan
           ariaSelected
           ariaSetSize
           ariaSort
           ariaValueMax
           ariaValueMin
           ariaValueNow
           ariaValueText
           assignedSlot
           attributes
           childElementCount
           children
           classList
           className
           clientHeight
           clientLeft
           clientTop
           clientWidth
           firstElementChild
           id
           innerHTML
           lastElementChild
           localName
           namespaceURI
           nextElementSibling
           openOrClosedShadowRoot
           outerHTML
           part
           prefix
           previousElementSibling
           scrollHeight
           scrollLeft
           scrollLeftMax
           scrollTop
           scrollTopMax
           scrollWidth
           shadowRoot
           slot
           tagName
          ;Node interface  https://developer.mozilla.org/en-US/docs/Web/API/Node
           baseURI
           childNodes
           firstChild
           isConnected
           lastChild
           nextSibling
           nodeName
           nodeType
           nodeValue
           ownerDocument
           parentElement
           parentNode
           previousSibling
           textContent]))

;; https://gist.github.com/rotaliator/73daca2dc93c586122a0da57189ece13
(defn copy-to-clipboard [val]
  (let [el (js/document.createElement "textarea")]
    (set! (.-value el) val)
    (.appendChild js/document.body el)
    (.select el)
    (js/document.execCommand "copy")
    (.removeChild js/document.body el)))

(defn screen-quadrant
  "Pass a dom node and get a tuple back reprenting the quadrant in which the center of the node lives.
   (screen-quadrant (js/document.getElementById \"my-id\")) => [:top :left]"
  [node]
  (let [rect (.getBoundingClientRect node)
        x* (* 0.5 (- (.-right rect) (.-left rect)))
        x  (+ (.-right rect) x*)
        y* (* 0.5 (- (.-bottom rect) (.-top rect)))
        y  (+ (.-top rect) y*)
        top? (> 0.5 (/ y js/window.innerHeight))
        left? (> 0.5 (/ x js/window.innerWidth))]
    [(if top? :top :bottom) (if left? :left :right)]))

(defn set-overlay-position!
  [node parent]
  (let [[tb lr] (screen-quadrant parent)
        position-block (.getAttribute node "data-kushi-tooltip-position-block")
        position-inline (.getAttribute node "data-kushi-tooltip-position-inline")
        right? (cond (= "end" position-inline)
                   true
                   (= "start" position-inline)
                   false
                   :else
                   (= lr :right))
        top? (cond (= "end" position-block)
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

(defn et [e] (some-> e .-target))
(defn et+ [e] (some-> e .-target el+))
(defn etv [e] (some-> e .-target .-value))
(defn etv->int [e] (some-> e .-target .-value js/parseInt))
(defn etv->float [e] (some-> e .-target .-value js/parseFloat))

(defn el-by-id [s] (js/document.getElementById s))

(defn get-first-onscreen-child-from-top [parent]
  (js/console.log (.hasOwnProperty #js {} "childNodes"))
  (js/console.log (.hasOwnProperty parent "childNodes"))
  (first (filter (fn [node] (pos? (.-top (.getBoundingClientRect node)))) (js->clj parent.childNodes))))

(defn nearest-ancestor [node selector]
  (.closest node selector))

(defn toggle-class [el & xs] (doseq [x xs] (.toggle (.-classList el) x)))

(defn remove-class [el & xs] (doseq [x xs] (.remove (.-classList el) x)))

(defn add-class [el & xs] (doseq [x xs] (.add (.-classList el) x)))

(defn set-css-var! [el prop val] (when el (.setProperty el.style prop val)))

(defn set-client-wh-css-vars! [el]
  (when el
    (set-css-var! el "--client-width" (str el.clientWidth "px"))
    (set-css-var! el "--client-height" (str el.clientHeight "px"))))

(defn set-neg-client-wh-css-vars! [el]
  (set-css-var! el "--client-width" (str "-" el.clientWidth "px"))
  (set-css-var! el "--client-height" (str "-" el.clientHeight "px")))

(defn set-style!* [el prop s]
  (when el (.setProperty el.style prop s)))

(defn set-style! [el prop s]
  (if (coll? el)
    (doseq [x el] (set-style!* x prop s))
    (set-style!* el prop s)))

(defn set-attribute! [el attr v]
  (when el (.setAttribute el (name attr) v)))

(defn has-class? [el s]
  (when el (.contains (.-classList el) s)))
