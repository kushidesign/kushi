(ns kushi.css.shorthand
  (:require 
   [clojure.string :as string]))


;; Make sure to re-generated (from the repl) the dependant
;; `shorthand-syntax` def in this namespace, when/if any values are
;; added/changed to `shorthand-syntax*`.

;; The members of this list must be kept in a particular order within their
;; respective 'family` groups e.g.:

;; "border"
;; "border-block"
;; "border-block-color"
;; "border-block-end"
;; "border-block-end-color"

(def shorthand-syntax*
  {"align-items"               {:tier 1,
                                :vals {"c"  "center",
                                       "fs" "flex-start",
                                       "fe" "flex-end",
                                       "n"  "normal",
                                       "s"  "start",
                                       "e"  "end",
                                       "b"  "baseline"}}
   "background"                {:tier      2
                                :shorthand "bg"}
   "background-color"          {:tier      1
                                :shorthand "bgc"}
   "background-image"          {:tier      2
                                :shorthand "bgi"}
   "background-position"       {:tier      2,
                                :shorthand "bgp"
                                :vals      {"t" "top"
                                            "b" "bottom"
                                            "l" "left"
                                            "r" "right"
                                            "c" "center"}}
   "background-repeat"         {:tier      2,
                                :shorthand "bgr"
                                :vals      {"nr" "no-repeat",
                                            "rx" "repeat-x",
                                            "ry" "repeat-y",
                                            "r"  "round",
                                            "s"  "space"}}
   "background-size"           {:shorthand "bgs"
                                :tier      2}
   "border"                    {:tier 1}
   "border-block"              {:tier 1}
   "border-block-color"        {:tier 2}
   "border-block-end"          {:tier 1}
   "border-block-end-color"    {:tier 2}
   "border-block-end-style"    {:tier 2,
                                :vals {"h" "hidden",
                                       "d" "dotted",
                                       "s" "solid",
                                       "g" "groove",
                                       "r" "ridge",
                                       "i" "inset",
                                       "o" "outset"}}
   "border-block-end-width"    {:tier 2}
   "border-block-start"        {:tier 1}
   "border-block-start-color"  {:tier 2}
   "border-block-start-style"  {:tier 2,
                                :vals {"h" "hidden",
                                       "d" "dotted",
                                       "s" "solid",
                                       "g" "groove",
                                       "r" "ridge",
                                       "i" "inset",
                                       "o" "outset"}}
   "border-block-start-width"  {:tier 2}
   "border-block-width"        {:tier 2}
   "border-color"              {:tier 1}
   "border-end-end-radius"     {:tier 3}
   "border-end-start-radius"   {:tier 3}
   "border-inline"             {:tier 1}
   "border-inline-color"       {:tier 3}
   "border-inline-end"         {:tier 1}
   "border-inline-end-color"   {:tier 3}
   "border-inline-end-style"   {:tier 3,
                                :vals {"h" "hidden",
                                       "d" "dotted",
                                       "s" "solid",
                                       "g" "groove",
                                       "r" "ridge",
                                       "i" "inset",
                                       "o" "outset"}}
   "border-inline-end-width"   {:tier 3}
   "border-inline-start"       {:tier 1}
   "border-inline-start-color" {:tier 3}
   "border-inline-start-style" {:tier 3,
                                :vals {"h" "hidden",
                                       "d" "dotted",
                                       "s" "solid",
                                       "g" "groove",
                                       "r" "ridge",
                                       "i" "inset",
                                       "o" "outset"}}
   "border-inline-start-width" {:tier 3}
   "border-inline-width"       {:tier 3}
   "border-radius"             {:tier 1}
   "border-start-end-radius"   {:tier 3}
   "border-start-start-radius" {:tier 3}
   "border-style"              {:tier 2,
                                :vals {"h" "hidden",
                                       "d" "dotted",
                                       "s" "solid",
                                       "g" "groove",
                                       "r" "ridge",
                                       "i" "inset",
                                       "o" "outset"}}
   "border-width"              {:tier 1}
   "color"                     {:tier 1}
   "display"                   {:tier 1,
                                :vals {"ib"  "inline-block",
                                       "f"   "flex",
                                       "tr"  "table-row",
                                       "it"  "inline-table",
                                       "trg" "table-row-group",
                                       "ig"  "inline-grid",
                                       "tfg" "table-footer-group",
                                       "if"  "inline-flex",
                                       "tcg" "table-column-group",
                                       "t"   "table",
                                       "i"   "inline",
                                       "b"   "block",
                                       "g"   "grid",
                                       "tc"  "table-cell",
                                       "thg" "table-header-group",
                                       "c"   "contents",
                                       "li"  "list-item"}}
   "font-family"               {:tier 1}
   "font-size"                 {:tier 1}
   "font-variant"              {:tier 1}
   "font-weight"               {:tier 1}
   "grid"                      {:tier 2}
   "grid-area"                 {:tier 3}
   "grid-auto-columns"         {:tier 3}
   "grid-auto-flow"            {:tier 3}
   "grid-auto-rows"            {:tier 3}
   "grid-column"               {:tier 3}
   "grid-column-end"           {:tier 3}
   "grid-column-gap"           {:tier 3}
   "grid-column-start"         {:tier 3}
   "grid-row"                  {:tier 3}
   "grid-row-end"              {:tier 3}
   "grid-row-gap"              {:tier 3}
   "grid-row-start"            {:tier 3}
   "grid-template"             {:tier 3}
   "grid-template-areas"       {:tier 1}
   "grid-template-columns"     {:tier 1}
   "grid-template-rows"        {:tier 1}
   "height"                    {:tier 1}
   "inset"                     {:tier 2}
   "inset-block"               {:tier 2}
   "inset-block-end"           {:tier 2}
   "inset-block-start"         {:tier 2}
   "inset-inline"              {:tier 2}
   "inset-inline-end"          {:tier 2}
   "inset-inline-start"        {:tier 2}
   "justify-content"           {:tier 1,
                                :vals {"n"  "normal",
                                       "s"  "start",
                                       "e"  "end",
                                       "fe" "flex-end",
                                       "sa" "space-around",
                                       "sb" "space-between",
                                       "se" "space-evenly",
                                       "r"  "right",
                                       "fs" "flex-start",
                                       "l"  "left",
                                       "c"  "center"}}
   "justify-items"             {:tier 2,
                                :vals {"n"  "normal",
                                       "s"  "start",
                                       "e"  "end",
                                       "ss" "self-start",
                                       "fe" "flex-end",
                                       "a"  "auto",
                                       "se" "self-end",
                                       "r"  "right",
                                       "fs" "flex-start",
                                       "l"  "left",
                                       "c"  "center"}}
   "line-height"               {:tier 2}
   "margin"                    {:tier 1}
   "margin-block"              {:tier 1}
   "margin-block-end"          {:tier 1}
   "margin-block-start"        {:tier 1}
   "margin-inline"             {:tier 1}
   "margin-inline-end"         {:tier 1}
   "margin-inline-start"       {:tier 1}
   "opacity"                   {:tier 1}
   "padding"                   {:tier 1}
   "padding-block"             {:tier 1}
   "padding-block-end"         {:tier 1}
   "padding-block-start"       {:tier 1}
   "padding-inline"            {:tier 1}
   "padding-inline-end"        {:tier 1}
   "padding-inline-start"      {:tier 1}
   "text-align"                {:tier 1,
                                :vals {"c"  "center",
                                       "r"  "right",
                                       "l"  "left",
                                       "j"  "justify",
                                       "ja" "justify-all",
                                       "s"  "start",
                                       "e"  "end",
                                       "mp" "match-parent"}}
   "text-decoration"           {:tier 2
                                :vals {"u"  "underline"
                                       "o"  "overline"
                                       "lt" "line-through"}}
   "text-decoration-color"     {:tier 2}
   "text-decoration-line"      {:tier 2
                                :vals {"u"  "underline"
                                       "o"  "overline"
                                       "lt" "line-through"}}
   "text-decoration-style"     {:tier 2
                                :vals {"s" "solid"
                                       "w" "wavy"}}
   "text-decoration-thickness" {:tier 2
                                :vals {"ff" "from-font"}}
   "text-shadow"               {:tier 1}
   "text-transform"            {:tier 1,
                                :vals {"u"  "uppercase",
                                       "l"  "lowercase",
                                       "c"  "captitalize",
                                       "fw" "full-width"}}
   "text-underline-offset"     {:tier 2}
   "text-underline-position"   {:tier 2,
                                :vals {"ff" "from-font"
                                       "l"  "left"
                                       "r"  "right"
                                       "u"  "under"}}
   "vertical-align"            {:tier 2,
                                :vals {"b"  "baseline",
                                       "s"  "sub",
                                       "t"  "top",
                                       "tt" "text-top",
                                       "tb" "text-bottom",
                                       "m"  "middle"}}
   "visibility"                {:tier 2
                                :vals {"h" "hidden"
                                       "v" "visibile"
                                       "c" "collapse"}}
   "white-space"               {:tier 1,
                                :vals {"n"  "nowrap"
                                       "p"  "pre"
                                       "pw" "pre-wrap"
                                       "pl" "pre-line"}}
   "width"                     {:tier 1}
   "z-index"                   {:tier 1}})

;; uses fireworks.pp/?pp
#_(?pp
   (reduce-kv
    (fn [m k {:keys [tier vals shorthand] :as v}]
      (let [sh     (string/join (map first (string/split k #"-")))
            sh-len (count sh)
            ret    (assoc-in m
                             [tier
                              (or shorthand sh)]
                             k)
            ret    (if (> sh-len (:max-shorthand-len m))
                     (assoc ret :max-shorthand-len sh-len)
                     ret)]
        (if vals
          (assoc-in ret [:enums k] vals)
          ret)))
    {1 {}
     2 {}
     3 {}
     :enums {}
     :max-shorthand-len 4}
    shorthand-syntax*))


;; This map is for resolving kushi shorthand syntax.
;; Regenerate this from reduce-kv routine above if you add or change anything
;; to the `shorthand-syntax*` source-of-truth map.
(def shorthand-syntax
  {1                  {"d"   "display",
                       "w"   "width",
                       "p"   "padding",
                       "gtr" "grid-template-rows",
                       "bw"  "border-width",
                       "br"  "border-radius",
                       "fv"  "font-variant",
                       "bgc" "background-color",
                       "jc"  "justify-content",
                       "bie" "border-inline-end",
                       "tt"  "text-transform",
                       "mb"  "margin-block",
                       "pis" "padding-inline-start",
                       "mi"  "margin-inline",
                       "gtc" "grid-template-columns",
                       "pbe" "padding-block-end",
                       "bi"  "border-inline",
                       "pi"  "padding-inline",
                       "mbs" "margin-block-start",
                       "bbs" "border-block-start",
                       "pie" "padding-inline-end",
                       "ai"  "align-items",
                       "mie" "margin-inline-end",
                       "gta" "grid-template-areas",
                       "bbe" "border-block-end",
                       "bis" "border-inline-start",
                       "b"   "border",
                       "fs"  "font-size",
                       "bc"  "border-color",
                       "ff"  "font-family",
                       "bb"  "border-block",
                       "fw"  "font-weight",
                       "h"   "height",
                       "m"   "margin",
                       "ws"  "white-space",
                       "ta"  "text-align",
                       "pb"  "padding-block",
                       "pbs" "padding-block-start",
                       "o"   "opacity",
                       "c"   "color",
                       "mis" "margin-inline-start",
                       "zi"  "z-index",
                       "mbe" "margin-block-end"},
   2                  {"bbw"  "border-block-width",
                       "ib"   "inset-block",
                       "td"   "text-decoration",
                       "tdt"  "text-decoration-thickness",
                       "ii"   "inset-inline",
                       "bgr"  "background-repeat",
                       "tuo"  "text-underline-offset",
                       "ji"   "justify-items",
                       "bs"   "border-style",
                       "bbew" "border-block-end-width",
                       "iis"  "inset-inline-start",
                       "v"    "visibility",
                       "bbc"  "border-block-color",
                       "iie"  "inset-inline-end",
                       "bbsc" "border-block-start-color",
                       "lh"   "line-height",
                       "bbec" "border-block-end-color",
                       "bgs"  "background-size",
                       "bgi"  "background-image",
                       "bbss" "border-block-start-style",
                       "i"    "inset",
                       "bgp"  "background-position",
                       "tdl"  "text-decoration-line",
                       "g"    "grid",
                       "bbes" "border-block-end-style",
                       "ibs"  "inset-block-start",
                       "tds"  "text-decoration-style",
                       "tup"  "text-underline-position",
                       "bg"   "background",
                       "tdc"  "text-decoration-color",
                       "bbsw" "border-block-start-width",
                       "va"   "vertical-align",
                       "ibe"  "inset-block-end"},
   3                  {"gce"  "grid-column-end",
                       "biss" "border-inline-start-style",
                       "bisw" "border-inline-start-width",
                       "gcg"  "grid-column-gap",
                       "bic"  "border-inline-color",
                       "gt"   "grid-template",
                       "besr" "border-end-start-radius",
                       "biw"  "border-inline-width",
                       "biec" "border-inline-end-color",
                       "gac"  "grid-auto-columns",
                       "gar"  "grid-auto-rows",
                       "gcs"  "grid-column-start",
                       "bies" "border-inline-end-style",
                       "bssr" "border-start-start-radius",
                       "grg"  "grid-row-gap",
                       "ga"   "grid-area",
                       "gaf"  "grid-auto-flow",
                       "gre"  "grid-row-end",
                       "gr"   "grid-row",
                       "beer" "border-end-end-radius",
                       "bisc" "border-inline-start-color",
                       "bser" "border-start-end-radius",
                       "gc"   "grid-column",
                       "grs"  "grid-row-start",
                       "biew" "border-inline-end-width"},
   :enums             {"background-position"       {"t" "top"
                                                    "b" "bottom"
                                                    "l" "left"
                                                    "r" "right"
                                                    "c" "center"},
                       "background-repeat"         {"nr" "no-repeat",
                                                    "rx" "repeat-x",
                                                    "ry" "repeat-y",
                                                    "r"  "round",
                                                    "s"  "space"},
                       "justify-content"           {"n"  "normal",
                                                    "s"  "start",
                                                    "e"  "end",
                                                    "fe" "flex-end",
                                                    "sa" "space-around",
                                                    "sb" "space-between",
                                                    "se" "space-evenly",
                                                    "r"  "right",
                                                    "fs" "flex-start",
                                                    "l"  "left",
                                                    "c"  "center"},
                       "text-decoration-thickness" {"ff" "from-font"},
                       "text-transform"            {"u"  "uppercase",
                                                    "l"  "lowercase",
                                                    "c"  "captitalize",
                                                    "fw" "full-width"},
                       "vertical-align"            {"b"  "baseline",
                                                    "s"  "sub",
                                                    "t"  "top",
                                                    "tt" "text-top",
                                                    "tb" "text-bottom",
                                                    "m"  "middle"},
                       "align-items"               {"c"  "center",
                                                    "fs" "flex-start",
                                                    "fe" "flex-end",
                                                    "n"  "normal",
                                                    "s"  "start",
                                                    "e"  "end",
                                                    "b"  "baseline"},
                       "text-align"                {"c"  "center",
                                                    "r"  "right",
                                                    "l"  "left",
                                                    "j"  "justify",
                                                    "ja" "justify-all",
                                                    "s"  "start",
                                                    "e"  "end",
                                                    "mp" "match-parent"},
                       "white-space"               {"n"  "nowrap"
                                                    "p"  "pre"
                                                    "pw" "pre-wrap"
                                                    "pl" "pre-line"},
                       "border-style"              {"h" "hidden",
                                                    "d" "dotted",
                                                    "s" "solid",
                                                    "g" "groove",
                                                    "r" "ridge",
                                                    "i" "inset",
                                                    "o" "outset"},
                       "visibility"                {"h" "hidden"
                                                    "v" "visibile"
                                                    "c" "collapse"},
                       "text-decoration"           {"u"  "underline"
                                                    "o"  "overline"
                                                    "lt" "line-through"},
                       "text-decoration-line"      {"u"  "underline"
                                                    "o"  "overline"
                                                    "lt" "line-through"},
                       "justify-items"             {"n"  "normal",
                                                    "s"  "start",
                                                    "e"  "end",
                                                    "ss" "self-start",
                                                    "fe" "flex-end",
                                                    "a"  "auto",
                                                    "se" "self-end",
                                                    "r"  "right",
                                                    "fs" "flex-start",
                                                    "l"  "left",
                                                    "c"  "center"},
                       "display"                   {"ib"  "inline-block",
                                                    "f"   "flex",
                                                    "tr"  "table-row",
                                                    "it"  "inline-table",
                                                    "trg" "table-row-group",
                                                    "ig"  "inline-grid",
                                                    "tfg" "table-footer-group",
                                                    "if"  "inline-flex",
                                                    "tcg" "table-column-group",
                                                    "t"   "table",
                                                    "i"   "inline",
                                                    "b"   "block",
                                                    "g"   "grid",
                                                    "tc"  "table-cell",
                                                    "thg" "table-header-group",
                                                    "c"   "contents",
                                                    "li"  "list-item"},
                       "border-inline-end-style"   {"h" "hidden",
                                                    "d" "dotted",
                                                    "s" "solid",
                                                    "g" "groove",
                                                    "r" "ridge",
                                                    "i" "inset",
                                                    "o" "outset"},
                       "text-decoration-style"     {"s" "solid"
                                                    "w" "wavy"},
                       "border-block-end-style"    {"h" "hidden",
                                                    "d" "dotted",
                                                    "s" "solid",
                                                    "g" "groove",
                                                    "r" "ridge",
                                                    "i" "inset",
                                                    "o" "outset"},
                       "text-underline-position"   {"ff" "from-font"
                                                    "l"  "left"
                                                    "r"  "right"
                                                    "u"  "under"},
                       "border-block-start-style"  {"h" "hidden",
                                                    "d" "dotted",
                                                    "s" "solid",
                                                    "g" "groove",
                                                    "r" "ridge",
                                                    "i" "inset",
                                                    "o" "outset"},
                       "border-inline-start-style" {"h" "hidden",
                                                    "d" "dotted",
                                                    "s" "solid",
                                                    "g" "groove",
                                                    "r" "ridge",
                                                    "i" "inset",
                                                    "o" "outset"}},
   :max-shorthand-len 4})
