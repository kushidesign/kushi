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
(ns myapp.core
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

When "stacking" other modifiers (such as psuedo-classes) in front of props, the media queries must always come first.

<br>

## Pseudos and combo selectors
Pseudo-classes, pseudo-elements, and combo selectors can be achieved through the use of the modifiers syntax.
```Clojure
(sx :hover:c--blue
    :>a:hover:c--red
    :~a:hover:c--blue
    :_a:hover:c--gold
    :before:position--absolute
    :before:top--0
    :before:right--0
    :before:fw--bold
    [:before:content "\"*\""]
    ["nth:child(2):c" :red])

;; The last arg to sx above is an edge case requires the tuple syntax with prop being expressed as a string.
```
<br>

## Injecting stylesheets
You can use `kushi.core/inject-stylesheet` to load resources such as a font from Google Fonts.
```Clojure
;;The additional "preconnect" hints will improve Google Fonts performance.

(inject-stylesheet {:rel "preconnet"
                    :href "https://fonts.gstatic.com"
                    :cross-origin "anonymous"})

(inject-stylesheet {:rel "preconnet"
                    :href "https://fonts.googleapis.com"})

(inject-stylesheet {:rel "stylesheet"
                    :href "https://fonts.googleapis.com/css2?family=Inter:wght@900&display=swap"})
```

You can also use `kushi.core/inject-stylesheet` to inject a static css file.
This stylesheet might be a css reset file, or a third-party style library.
This is more of an edge case, as you would typically just do this with a <link> in your index.html.
However, if your project uses a clj file to generate the contents of your index's <head> at build time,
it may be handy to use this during development to inject new stylesheets without restarting your build.

```Clojure
(inject-stylesheet {:rel "stylesheet"
                    :href "css/my-global-styles.css"})
```
<br>

## Adding font resources
You can use `kushi.core/add-font-face` macro to load a local font from a file.
This will add an @font-face block to the css file generated by kushi.
The location of the font file must be a path, relative the location of the generated css file.
You could also use a remote url to load a hosted font file.

```Clojure
(add-font-face {:font-family "FiraCodeRegular"
                :font-weight "400"
                :font-style "normal"
                :src ["url(../fonts/FiraCode-Regular.woff)"]})
```
<br>

## Element attributes
Element attributes, if needed, can be supplied via a map. They must be the last argument to `sx`.
```Clojure
(defn my-button [text]
  [:button
   (sx :c--white
       :bgi--none
       :bgc--blue
       :border-radius--5px
       :cursor--pointer
       {:data-foo "my data here"
        :on-click #(prn "clicked!")
        :class [:my-other-class :some-other-class]
        :name :my-button})
     text])
```

## Prefixing options
... docs coming very soon.

## Using metadata
Co-locating your style inside components obviates the need to use class names and css selectors. The html generated in the DOM will have many auto-generated class names. As a result, it can become difficult to quickly comprehend the source location when looking at elements in a browser inspector (such as Chrome DevTool Elements panel).

With `sx`, you can add metadata via a special `:f` entry in the element attribute map, which will then be transformed into a unique value and attached to the element as a custom data attribute called `data-ns`. The value of this `:f` entry is the var-quoted function name. If you are using the `kushi`-specific `:ident` entry in your attributes map, this will also be incorporated into the value of the generated `data-ns` attr as well.
```Clojure
(ns myapp.core
  (:require
   [kushi.core :refer (sx)]
   [reagent.dom :as rdom]))

(defn my-button [text]
  [:button
   (sx :c--white
       :bgi--none
       :bgc--blue
       :border-radius--5px
       :cursor--pointer
       {:f #'headline-layer
        :ident :wrapper
        :on-click #(prn "clicked!")})
     text])
```
Then you would see this when inspecting the element in browser console:
```html
<div data-ns="myapp.core/my-button::wrapper:170" class="_h30702"">Button Text</div>
```
<br>

## Style injection during development
...docs coming very soon. See quickstart for working example.

<br>

## Build tools
...more info coming very soon.

<br>

## Roadmap
...more info coming very soon.

<br>

## Development
...more info coming very soon.

## License

Copyright © 2021 Jeremiah Coyle

Distributed under the EPL License. See LICENSE.
