# Style made easy.

- Native ClojureScript styling solution.
- Co-locate styling at the element level.
- Compile-time macros generate static css.
- Standards-based shorthand syntax.
- Supports media-queries, psuedos, and combo selectors.
- Leverages css variables for run-time dynamics.
- Composable, user-defined shared styles.
- Ships with a small handful of useful utility classes.
- Default industry-standard breakpoint scale.
- Auto-generated prefixing to avoid pontential collisions.
- Robust and flexible prefixing config options.
- Optionally use metadata to enhance debugging.
- Detailed warnings for the terminal and browser.

## Setup and Usage
For a well commented, feature-complete minimal project template, please see [kushi-quickstart](https://github.com/paintparty/kushi-quickstart).

## Syntax
```Clojure
(defn my-component []
 [:div
  (sx :c--red
      :ta--c
      :fs--18px)])

;; The above call to the sx macro will return the following attribute map:
;; {:class "_j7338"}

;; When your build finishes, the following css will be written to disk:
;; ._j7338 {color: red; text-align: center; font-size: 18px;}
```
`kushi.core/sx` is a macro that returns an attribute map.


This map contains:
  - A class property containing the correct auto-generated, prefixed classnames.
  - If necessary, a style property containing the correct auto-generated css variable names.
  - All the other attributes you specify in your attributes map (supplied as an optional last arg to sx).
  - An optional data-ns attribute to help with browser-based debugging (see docs: "Using metadata").
Using a build hook for the :compile-finish stage (or similar), your css is written to a static file.

Most values supplied to `sx` are keywords.<br>
Keywords containing `--` represent a css prop and value pair (split on `--`).
```Clojure
:c--red   ; => :color--#191970
:ai--c    ; => :align-items--center
:ai--e    ; => :align-items--end
:ta--r    ; => :text-align--right
:fs--18px ; => :font-size--18px
:d--b     ; => :display--block
:d--f     ; => :display--flex
:bgs--50% ; => :background-size--50%
```
This shorthand syntax is supported for the most commonly used css props:
```Clojure
:ai  ; => :align-items
:b   ; => :border
:bb  ; => :border-bottom
:bc  ; => :border-color
:bg  ; => :background
:bgc ; => :background-color
:bgi ; => :background-image
:bgp ; => :background-position
:bgr ; => :background-repeat
:bgs ; => :background-size
:bl  ; => :border-left
:br  ; => :border-right
:bs  ; => :border-style
:bt  ; => :border-top
:bw  ; => :border-width
:c   ; => :color
:d   ; => :display
:ff  ; => :font-family
:fs  ; => :font-size
:fv  ; => :font-variant
:fw  ; => :font-weight
:h   ; => :height
:jc  ; => :justify-content
:ji  ; => :justify-items
:lh  ; => :line-height
:m   ; => :margin
:mb  ; => :margin-bottom
:ml  ; => :margin-left
:mr  ; => :margin-right
:mt  ; => :margin-top
:o   ; => :opacity
:p   ; => :padding
:pb  ; => :padding-bottom
:pl  ; => :padding-left
:pr  ; => :padding-right
:pt  ; => :padding-top
:ta  ; => :text-align
:td  ; => :text-decoration
:tdc ; => :text-decoration-color
:tdl ; => :text-decoration-line
:tds ; => :text-decoration-style
:tdt ; => :text-decoration-thickness
:tt  ; => :text-transform
:va  ; => :vertical-align
:w   ; => :width
:ws  ; => :white-space
:z   ; => :z-index
```

## Shared styles
You will typically want to defined all your shared styles in a dedicated namespace.
```Clojure
(ns myapp.shared-styles
  (:require
   [kushi.core :refer (defclass)]))

(defclass headline
  :.absolute
  :top--0
  :left--0
  :b--1px:solid:black
  :fs--200px
  :tt--u
  :font-style--italic
  :mix-blend-mode--darken)

;; The above example will generate a data-representation of the css rule-set.
;; This data is added to a register (an atom that exists in the build state).
;; This css class is only written to disk (once) if a component actually uses it.
```
Then, in another namespace
```Clojure
(ns ^:dev-always myapp.core
  (:require
   [kushi.core :refer (sx)]
   [myapp.shared-styles]
   [reagent.dom :as rdom]))

  (defn my-headline [text]
    [:h1 (sx :.headline) text])

;; The above call to the sx macro will return the following attribute map:
;; {:class "_s77920__headline"}

;; When your build finishes, the following css would be written to disk:
;; ._s77920__headline {
;;     position: absolute;
;;     top: 0px;
;;     left: 0px;
;;     border: 1px solid black;
;;     font-size: 200px;
;;     font-style: italic;
;;     text-transform: uppercase;
;;     mix-blend-mode: darken;
;; }
```
As arguments to `sx` classes are distinguished from other prop-styles by using a keyword beginning with a `.`, e.g. `:.headline`, as in the example above. You may have also noticed that the `defclass headline` code example above uses `:.absolute`, and then composes additional styles on top of that. `declasses` can mixin other classes. In this case, `headline` is mixing-in `:.absolute`, which is one of a small handful of very useful, pre-defined `defclasses` that ships with Kushi. The full list is as follows:

```
:absolute
:absolute-centered
:absolute-fill
:bgi-contain
:bgi-cover
:bordered
:code
:content-blank
:debug-grid
:debug-grid-16
:debug-grid-16-solid
:debug-grid-8-solid
:fixed
:fixed-fill
:flex-col-c
:flex-col-fe
:flex-col-fs
:flex-col-sa
:flex-col-sb
:flex-col-se
:flex-row-c
:flex-row-fe
:flex-row-fs
:flex-row-sa
:flex-row-sb
:flex-row-se
:outlined
:relative
:sans-serif
:serif
```


## Dynamic values
...docs coming very soon. See quickstart for working example.

## Media Queries
...docs coming very soon. See quickstart for working example.

## Pseudos
...docs coming very soon. See quickstart for working example.

## Combo Selectors
...docs coming very soon. See quickstart for working example.

## Injecting stylesheets
...docs coming very soon. See quickstart for working example.

## Adding font resources
...docs coming very soon. See quickstart for working example.

## Using metadata
...docs coming very soon. See quickstart for working example.

## Style injection during development
...docs coming very soon. See quickstart for working example.

## Build tools
...docs coming very soon.

## Roadmap
...docs coming very soon.

## Development
...docs coming very soon.

## License

Copyright © 2021 Jeremiah Coyle

Distributed under the EPL License. See LICENSE.
