(ns ^{:kushi/layer "css-reset"} kushi.css.build.css-reset
  (:require
   [kushi.css.build.macros :refer [defcss]]))

(defcss
  "*:where(:not(html, iframe, canvas, img, svg, video):not(svg *, symbol *))"
  {"all" "unset" "display" "revert"})

(defcss
  "*, *::before, *::after"
  {"box-sizing" "border-box"})

(defcss
  "a, button"
  {"cursor" "revert"})

(defcss
  "ol, ul, menu"
  {"list-style" "none"})

(defcss "img" {"max-width" "100%"})

(defcss
  "table"
  {"border-collapse" "collapse"})

(defcss
  "textarea"
  {"white-space" "revert"})

(defcss
  "meter"
  {"-webkit-appearance" "revert"
   "appearance"         "revert"})

(defcss
  "::placeholder"
  {"color" "unset"})

(defcss
  ":where([hidden])"
  {"display" "none"})

(defcss
  ":where([contenteditable])"
  {"-moz-user-modify"    "read-write"
   "-webkit-user-modify" "read-write"
   "overflow-wrap"       "break-word"
   "-webkit-line-break"  "after-white-space"})

(defcss
  ":where([draggable='true'])"
  {"-webkit-user-drag" "element"})
