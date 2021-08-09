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

##  Usage Details
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
Using a build hook for the `:compile-finish` stage (or similar), your css is written to a static file.

<br>

### Styles as keywords
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
<br>

### Styles as tuples

Any css prop-value declaration can also be written as tuple (2-element vector).<br>
All the shorthand syntax listed above is valid in the first (property) position.<br>
By convention, this form should only be used in the following cases:

Most commonly, when using a dynamic, or variable, value:
```Clojure
(sx [:color my-color])
```

When a string is desired, or necessary:
```Clojure
(sx [:before:content "\"*\""]
    [:width "calc((100vw / 3) + 12px)"])
```
When using kushi.core/cssfn to construct a value:
```Clojure
(sx [:transform (cssfn :translateY :-100px)])
```
<br>

### Using the `cssfn` helper
As seen in the example above, you can use `kushi.core/cssfn` to contruct values.
```Clojure
(sx [:transform (cssfn :translate :-30px :5%)]
    [:c (cssfn :rgba 0 200 100 0.4)]
    [:bgi (cssfn :linear-gradient "to bottom right" :red :blue)])

; The above example would be equivalent to:
(sx [:transform "translate(-30px, 5%)"]
    [:color "rgba(0, 200, 100, 0.4)"]
    [:background-image "linear-gradient(to bottom right, red, blue)"])
```

<br>

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
As arguments to `sx`, classes are distinguished from other prop-styles by using a keyword beginning with a `.`, e.g. `:.headline`, as in the example above.

You may have also noticed that the `defclass headline` code example above uses `:.absolute`, and then composes additional styles on top of that. `declasses` can mixin other classes. The class `:.absolute` is one of a small handful of very useful, pre-defined classes that ships with Kushi.

The full list:

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
Detailed documentation on the above classes can be found [here](https://github.com/paintparty/kushi/blob/main/doc/intro.md).

If you pass a class to `sx` that is neither a pre-defined kushi class or one of your own classes defined with `defclass`, then it will simpley be attached to the elements classlist as an unscoped class, exactly as you wrote it. You might want to do this to pull in classes from some other stylesheet.


## Media Queries
```Clojure
;; Specify the font-size of an <h1> element across breakpoints
[:h1
 (sx :fs--1.25rem
     :md:fs--1.5rem
     :lg:fs--1.75rem
     :xl:fs--2rem)]
```
As in the example above, you can use preceding modifiers to set different values for a property at different breakpoints.

Kushi ships with the following, industry-standard, mobile-first breakpoint scale:
```Clojure
{:sm {:min-width :640px}
 :md {:min-width :768px}
 :lg {:min-width :1024px}
 :xl {:min-width :1280px}
 :2xl {:min-width :1536px}}
```
Both the names and values can be customized via supplying a map in the `:media` entry in your `kushi.edn` config file. See [Configuration Options](##configuration-options).

Below is an example of a scale that is desktop-first and uses different names.<br>
Note that in the case of desktop-first(`max-width`), the order is reversed(relative to mobile-first / `min-width`).
```Clojure
{:desktop {:max-width :1280px}
 :tablet {:max-width :1024px}
 :mobile {:max-width :768px}
 :small {:max-width :640px}}
```
Any media-query modifier that you use needs to correspond to a key in the breakpoint map.

<br>

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
