<br>

# Style Made Easy.

<br>

[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.paintparty/kushi.svg)](https://clojars.org/org.clojars.paintparty/kushi)

<br>

**Native ClojureScript styling solution**

**Co-locate all styles at the element level**

**Compile-time macros generate static css**

**Standards-based dynamic shorthand syntax**

**Supports media-queries, psuedos, and combo selectors**

**Leverages CSS variables for runtime dynamics**

**Composable, user-defined shared classes**

**Ships with a small handful of useful utility classes**

**Includes a default industry-standard breakpoint scale**

**Predefined scales for typography, widths, and more**

**Auto-generated selectors to avoid pontential collisions**

**Robust and flexible selector prefixing options**

**Optionally use metadata to enhance debugging**

**Detailed warnings for the terminal and browser**

<br>

## Index
[Setup and usage](#setup-and-usage)<br>
[Syntax](#syntax)<br>
[Shared styles](#shared-styles)<br>
[Media queries](#media-queries)<br>
[Pseudos and combo selectors](#pseudos-and-combo-selectors)<br>
[Using scales](#using-scales)<br>
[Injecting stylesheets](#injecting-stylesheets)<br>
[Adding font resources](#adding-font-resources)<br>
[Element attributes](#element-attributes)<br>
[Using metadata](#using-metadata)<br>
[Configuration options](#configuration-options)<br>
[Prefixing options](#prefixing-options)<br>
[Style injection during development](#style-injection-during-development)<br>
[Useful warnings](#useful-warnings)<br>
[Usage with build tools](#usage-with-build-tools)<br>
[Roadmap](#roadmap)<br>
[Development](#development)<br>
[License](#license)

<br>
<br>

## Setup and Usage
For a well commented, feature-complete minimal project template, please see [kushi-quickstart](https://github.com/paintparty/kushi-quickstart).

<br>

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
  - All the other attributes you specify in your attributes map (supplied as an optional last arg to `sx`).
  - An optional data-ns attribute to help with browser-based debugging.  See [Using metadata](#using-metadata).

All your css is written to a static file, via a build hook for the `:compile-finish` stage (or similar depending on build tool).

<br>

### Styles as keywords
Most values supplied to `sx` are keywords.<br>
Keywords containing `--` represent a css prop and value pair (split on `--`).
```Clojure
:c--red   ; => :color--red
:ai--c    ; => :align-items--center
:ai--e    ; => :align-items--end
:ta--r    ; => :text-align--right
:fs--18px ; => :font-size--18px
:d--b     ; => :display--block
:d--f     ; => :display--flex
:bgs--50% ; => :background-size--50%
```
Kushi employs a simple shorthand grammer based on the existing CSS standard.<br>
It is available for the most commonly used css props:
```Clojure
:ai  ; :align-items
:b   ; :border
:bb  ; :border-bottom
:bc  ; :border-color
:bg  ; :background
:bgc ; :background-color
:bgi ; :background-image
:bgp ; :background-position
:bgr ; :background-repeat
:bgs ; :background-size
:bl  ; :border-left
:br  ; :border-right
:bs  ; :border-style
:bt  ; :border-top
:bw  ; :border-width
:c   ; :color
:d   ; :display
:ff  ; :font-family
:fs  ; :font-size
:fv  ; :font-variant
:fw  ; :font-weight
:h   ; :height
:jc  ; :justify-content
:ji  ; :justify-items
:lh  ; :line-height
:m   ; :margin
:mb  ; :margin-bottom
:ml  ; :margin-left
:mr  ; :margin-right
:mt  ; :margin-top
:o   ; :opacity
:p   ; :padding
:pb  ; :padding-bottom
:pl  ; :padding-left
:pr  ; :padding-right
:pt  ; :padding-top
:ta  ; :text-align
:td  ; :text-decoration
:tdc ; :text-decoration-color
:tdl ; :text-decoration-line
:tds ; :text-decoration-style
:tdt ; :text-decoration-thickness
:tt  ; :text-transform
:va  ; :vertical-align
:w   ; :width
:ws  ; :white-space
:z   ; :z-index
```
<br>

### Styles as tuples

Any css prop-value declaration can also be written as tuple (2-element vector).<br>
All syntax listed above is valid (in the first position as css property name).<br>
By convention, such a tuple should only be used in the following cases:

Most commonly, when using dynamic values:
```Clojure
(sx [:color my-color])
```

When a string is desired, or necessary:
```Clojure
(sx [:before:content "\"*\""]
    [:width "calc((100vw / 3) + 12px)"])
```
When using `kushi.core/cssfn` to construct a value:
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

### CSS Shorthand properties
[Shorthand properties](https://developer.mozilla.org/en-US/docs/Web/CSS/Shorthand_properties) are CSS properties that let you set the values of multiple other CSS properties simultaneously. With kushi, you can write them like this:
```Clojure
;; Some element in your component
[:div (sx :b--1px:solid:black)]
```
The above example arg to `sx` will resolve to the following css declaration:
```css
border: 1px solid black;
```
You can also write shorthand properties in the kushi tuple syntax with the value expressed using a double vector (ala [Garden](https://github.com/noprompt/garden) syntax), or just a string:
```Clojure
;; Garden-style
[:div (sx [:b [[:1px :solid :black]]])]

;; With string
[:div (sx [:b "1px solid black"])]
```
Future support is planned for dynamic values nested in garden-style double vectors and css values construction method such as `kushi.core/cssfn` or `cljs.core/str`.<br>
In the meantime, if you would like to incorporate a dynamic value into a css shorthand property, you can do it like this:
```Clojure
;; Write the shorthand with a "default" value that subsequently
;; gets overridden with a dynamic value on the specific property.
[:div
 (sx (sx :b--1px:solid:black
         [:bc mycolor])]

;; Alternatively, this would work:
[:div
 (let [myborder (str "1px solid " mycolor)]
   (sx [:b myborder])]

```
<br>

### CSS value lists
Sometimes multiple css values are seperated by commas to indicate they are ordered, or that there are ordered alternatives. With kushi, you can write them like this:
```Clojure
;; Some element in your component
[:div (sx :ff--FiraCodeRegular|Consolas|monospace)]
```
The above will resolve to the following css declaration:
```css
font-family: FiraCodeRegular, Consolas, monospace;
```
The example below uses a list of css shorthand values in order to render multiple text-shadows in different colors:
```Clojure
[:div
 (sx :text-shadow--5px:5px:10px:red|-5px:-5px:10px:blue)
 "Shadows"]
```
The above will resolve to the following css declaration:
```css
text-shadow: 5px 5px 10px red, -5px -5px 10px blue;
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

You may have noticed that the `defclass` example above mixes-in `:.absolute`.<br>
With `declass`, you can mix-in any other defined classes.<br>
The `:.absolute` class is one of several predefined classes that ships with kushi.

The full list of predefined classes:

```Clojure
:.absolute
:.absolute-centered
:.absolute-fill
:.bgi-contain
:.bgi-cover
:.bordered
:.code
:.content-blank
:.debug-grid
:.debug-grid-16
:.debug-grid-16-solid
:.debug-grid-8-solid
:.fixed
:.fixed-fill
:.flex-col-c
:.flex-col-fe
:.flex-col-fs
:.flex-col-sa
:.flex-col-sb
:.flex-col-se
:.flex-row-c
:.flex-row-fe
:.flex-row-fs
:.flex-row-sa
:.flex-row-sb
:.flex-row-se
:.outlined
:.relative
:.sans-serif
:.serif
```
Detailed documentation on the above classes can be found [here](https://github.com/paintparty/kushi/blob/main/doc/kushi-predefined-classes.css).

If you pass a class to `sx` that is neither a predefined kushi class nor one of your own classes defined with `defclass`, then it will simply be attached to the element's classlist as an unscoped class, exactly as you wrote it. You might want to do this to apply classes from some other stylesheet.

<br>

### Applying classes conditionally
You can apply classes conditionally:
```Clojure
[:a
 (sx :bb--1px:solid:black
     (when my-condition :.active-link))]
```
Works with `if` `when` `cond` `if-let` `when-let` `if-not` `when-not`, and `case`.<br>
The class to be returned cannot be nested. For example, this following will not work:
```Clojure
;; This will NOT work.
[:a
 (sx :bb--1px:solid:black
     (when my-condition
       (when some-other-condition :.active-link)))]
```
<br>

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
Both the names and values can be customized via supplying a map in the `:media` entry in your `kushi.edn` config file. See [Configuration Options](#configuration-options).

Below is an example of a scale that is desktop-first and uses different names.<br>
Note that in the case of desktop-first(`max-width`), the order is reversed (relative to mobile-first / `min-width`).
```Clojure
{:desktop {:max-width :1280px}
 :tablet {:max-width :1024px}
 :mobile {:max-width :768px}
 :small {:max-width :640px}}
```
Any media-query modifier that you use must correspond to a key in the breakpoint map.

When "stacking" other modifiers (such as psuedo-classes) in front of props, the media queries must always come first.

<br>

## Pseudos and combo selectors
Pseudo-classes, pseudo-elements, and combo selectors are available via modifiers:
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

;; The last arg to sx above is an edge case that requires
;; the tuple syntax with prop being expressed as a string.
```
<br>

## Defining Animations

`kushi.core/defkeyframes` macro makes it easy to define css keyframes.
```Clojure
;; This will twirl something on its y-axis
(defkeyframes :y-axis-spinner
  [:0% {:transform (cssfn :rotateY :0deg)}]
  [:100% {:transform (cssfn :rotateY :360deg)}])


;; Somewhere in your component code...
[:div
 (sx :fs--32px
     :animation--y-axis-spinner:12s:linear:infinite)
 "Round & Round"]
```
<br>

## Using scales
Kushi ships with two different predefined scaling systems.<br>
These systems provide a scale of values for `width`, `font-size`, `padding`, `margin`, and `border-widths`.

These two systems shadow the scales provided by [Tachyons](http://tachyons.io/docs/typography/scale/) and [Tailwindcss](https://tailwindcss.com/docs/font-size).

The Tacyons scale is available by default.<br>
You can opt to instead use the Tailwind scale in your `kushi.edn` config file:
```Clojure
{...
 :scaling-system :tailwind
 ...}
```
Supply a value affixed with an `*` to of the applicable css properties:
```Clojure
(sx :w--1*
    :bw--2*
    :fs--3*
    :p--sm*
    :m--md*)

;; The above is equivalent to the following

(sx :width--1rem
    :border-width--.25rem
    :font-size--1.5rem
    :padding--.5rem
    :margin--1rem)
```
View all the scale values [here](https://github.com/paintparty/kushi/blob/main/src/kushi/scales.cljc).

<br>

## Injecting stylesheets
You can use `kushi.core/inject-stylesheet` to load css stylesheets.<br>
The example below (typical use case), loads a stylesheet from Google Fonts.
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


You can also use `kushi.core/inject-stylesheet` to inject a css reset stylesheet, or a third-party style library.
This is more of an edge case, as you would typically just do this with a `<link>` in your index.html.<br>
However, if your project uses a clj file to generate the contents of your `<head>` at build time,
it may be handy to use this during development to inject new stylesheets without restarting your build.

```Clojure
(inject-stylesheet {:rel "stylesheet"
                    :href "css/my-global-styles.css"})
```
<br>

## Adding font resources
You can use `kushi.core/add-font-face` macro to load a local font from a file.<br>
This will add an `@font-face` block to the css file generated by kushi.<br>
The location of the font file must be a path, relative the location of the generated css file.<br>
You could also use a remote url to load a hosted font file.

```Clojure
(add-font-face {:font-family "FiraCodeRegular"
                :font-weight "400"
                :font-style "normal"
                :src ["url(../fonts/FiraCode-Regular.woff)"]})
```
<br>

## Element attributes
Element attributes, when needed, can be supplied via an optional map.<br>This map must be the must be the last argument to `sx`.
```Clojure
(defn my-button [text]
  [:button
   (sx :c--white
       :bgi--none
       :bgc--blue
       :border-radius--5px
       :cursor--pointer
       {:on-click #(prn "clicked!")
        :class [:my-other-class :some-other-class]
        :name :my-button})
     text])
```

<br>

## Using metadata
Co-locating your style inside components obviates the need to use class names and css selectors. The html generated in the DOM will have many auto-generated class names. As a result, it can become difficult to quickly comprehend the source location when looking at elements in a browser inspector (such as Chrome DevTool Elements panel).

With `sx`, you can add metadata via a special `:f` entry in the element attribute map, which will then be transformed into a unique value and attached to the element as a custom data attribute called `data-ns`. The value of this `:f` entry is the var-quoted function name. If you are using the `kushi`-specific `:ident` entry in your attributes map, this will also be incorporated into the value of the generated `data-ns` attribute.
```Clojure
(ns myapp.core
  (:require
   [kushi.core :refer (sx)]
   [reagent.dom :as rdom]))

;; A component defined, for example, on line 170
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
<div data-ns="myapp.core/my-button::wrapper:170" class="_h30702"">
  Button Text
</div>
```
<br>

## Configuration options
Various options are configurable via a required `kushi.edn` file.<br>
This file must live in your project's root directory.

The only required entry in this map is `:static-css-dir`

```Clojure
{
 ;; REQUIRED.
 ;; Must be relative to proj root e.g "public/css" or "resources/public/css".
 :static-css-dir "public/css"

 ;; Optional. Name of generated css file. Defaults to kushi.css.
 ;; :css-filename "my-kushi-styles.css"

 ;; If this is defined it will prepend to selectors as parent element.
 ;; Useful for scoping.
 ;; :parent :#my-target-div

 ;; If this is defined it will use this plus call-site supplied :ident
 ;; for classnames, instead of auto-generated hash.
 ;; :prefix :my-prefix

 ;; If this is defined it will override the default :data-ns key used for
 ;; attaching ns info to element attribute (dev only).
 ;; :ns-attr-key :data-foo

 ;; Optionally defined your own breakpoint scale to override the kushi
 ;; default breakpoint scale.
 ;; :media {:2xl {:max-width :1536px}
 ;;         :xl {:max-width :1280px}
 ;;         :lg {:max-width :1024px}
 ;;         :md {:max-width :768px}
 ;;         :sm {:max-width :640px}}
}
```
<br>

## Prefixing options
If you would like to prefix your generated classes with something other than an auto-generated string, you can make use of several kushi-specific properties in the attribute map that you pass to `sx`. These keys and values are only used by the macro at compile time and are removed in the attribute map that is returned by `sx`.

The most common use case for this would be setting a global `:prefix` value, and then providing an `:ident` value (in the attr map) to some or all of your calls to `sx`.
If you do this on a project-wide basis, you will need to make sure that your all your `:ident` values (or combos of `:parent` and `:ident`) are global unique.

```Clojure
;; In your kushi.edn map ...
{:prefix :_mypfx}

;; In one of your component namespaces ...
[:div
 (sx :c--red
     {:ident :my-el})]

;; The above example will return the following attribute map:
;; {:class "_mypfx__my-el"}

;; And will write the following rule to the css file:
;; ._mypfx__my-el {
;;    color: red;
;;}

;; Note that using an :ident property will only affect the selector name
;; if you also have a value set for :prefix in your kushi.edn config.
;; Otherwise, the :ident property will be ignored and you will just get
;; an auto-generated selector. If you want to use a custom prefix
;; a la carte, you can supply both :prefix and :ident locally to the attr
;; map that you are passing to `sx`.


;; You can also use a :parent and/or :element prop for further selector
;; specicifity. The value of :parent has to match the id or class of one
;; of the element's ancestors. The value of :element needs to be the same
;; as the element that it is within.

[:div
 (sx :c--red
     {:ident :my-el
      :parent :#myapp
      :element :div})]

;; The above would instead result the following css:
;; #myapp div._mypfx__my-el {color: red;}
```
<br>

## Style injection during development
For instantaneous previews when developing, all styling from `sx` calls are injected dynamically in the the following tag that is required to be in your `index.html` :
```html
<style type="text/css" id="_kushi-dev_"></style>
```
You will want to call `kushi.core/clean!` once in your project's main or core namespace.
```Clojure
(ns myapp.core
  (:require
   [kushi.core :refer (clean!)]))

(clean!)
```

See the [kushi-quickstart](https://github.com/paintparty/kushi-quickstart) template for an example of this setup.

<br>

## Useful warnings
Given the following:
```Clojure
(sx :.relative
    :ta--center
    "badstring"
    :.sans
    :p--10px
    12
    :fs--18px
    :c--#efefef)
```



You will receive warnings about invalid args in the terminal:

<img width=500 src="doc/terminal-warning.png"/><br>

<br>

And also in your browser's dev console:

<img width=500 src="doc/cdt-warning.png"/>

<br>
The browser console warning will provide you with file and line info.

<br>
<br>

## Usage with build tools
### Shadow-cljs
See the [kushi-quickstart](https://github.com/paintparty/kushi-quickstart) template for a detailed example of using Kushi in a shadow-cljs project.

### Figwheel
Figwheel quickstart template coming soon.

<br>

## Roadmap
...more info coming very soon.

<br>

## Development
...more info coming very soon.

<br>

## License

Copyright © 2021 Jeremiah Coyle

Distributed under the EPL License. See LICENSE.
