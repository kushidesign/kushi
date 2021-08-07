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
```
`kushi.core/sx` is a macro that returns an attribute map.


This map contains:
  - a class property containing the correct auto-generated, prefixed classnames.
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





## License

Copyright © 2021 Jeremiah Coyle

Distributed under the EPL License. See LICENSE.
