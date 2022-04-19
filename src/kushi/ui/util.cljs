(ns kushi.ui.util
 (:require
  [clojure.string :as string]))

(defn capitalize-words
  "Capitalize every word in a string"
  [s]
  (->> (string/split (str s) #"\b")
       (map string/capitalize)
       string/join))

;; https://gist.github.com/rotaliator/73daca2dc93c586122a0da57189ece13
(defn copy-to-clipboard [val]
  (let [el (js/document.createElement "textarea")]
    (set! (.-value el) val)
    (.appendChild js/document.body el)
    (.select el)
    (js/document.execCommand "copy")
    (.removeChild js/document.body el)))

(defn screen-quadrent
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

(defn set-overlay-position! [node parent]
  (let [[tb lr] (screen-quadrent parent)
        right? (= lr :right)
        top? (= tb :top)]

    ;; display on right or left
    ;; (set! node.style.left (if right? "unset" "100%"))
    ;; (set! node.style.right (if right? "100%" "unset"))
    ;; (set! node.style.top (if top? 0 "unset"))
    ;; (set! node.style.bottom (if top? "unset" 0))

    ;; display on top or bottom
    (set! node.style.left (if right? "unset" "0"))
    (set! node.style.right (if right? "0" "unset"))
    (set! node.style.top (if top? "100%" "unset"))
    (set! node.style.bottom (if top? "unset" "100%"))
    ))

(defn conditional-display? [node]
  (= "true" (.getAttribute node "data-kushi-conditional-display")))

(defn toggle-boolean-attribute [node attr]
  (let [attr-val (.getAttribute node (name attr))
        newv (if (= attr-val "false") true false)]
    (.setAttribute node (name attr) newv)))

(defn grandparent [node] (some-> node .-parentNode .-parentNode))
(defn has-class [node classname] (some-> node .-classList (.contains (name classname))))
(defn attribute-true? [node attribute] (when node (= "true" (.getAttribute node (name attribute)))))
