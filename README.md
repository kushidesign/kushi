<br>

# Kushi
<h5>Kushi is a foundation for building web UI with ClojureScript.</h5>
<!-- <h5><a href=""><strong>Explore Kushi UI Playground »</strong></a></h5> -->
<h5><a href="https://github.com/paintparty/kushi-quickstart"><strong>Kushi Quickstart »</strong></a></h5>



<br>

## Features
- **100% Clojure(Script)**

- **A minimal set of headless UI components**

- **Themeable design system foundation**

- **Shorthand styling syntax shadows CSS standard**

- **Co-location of styling at the element level**

- **Compile-time macros generate static css**

- **Supports media-queries, psuedos, and combo selectors**

- **Leverages CSS variables for runtime dynamics**

- **Composable, user-defined shared classes**

- **A collection of Useful css utility classes**

- **Default industry-standard breakpoint scale**

- **Auto-generated selectors to avoid pontential collisions**

- **Robust and flexible selector prefixing options**

- **Helpers for typography, keyframes, and more**

- **Enhanced debugging via metadata**

- **Detailed, human-readable warnings**

- **Framework & build-tool agnostic**





<br>

## Table of Contents
- [Setup and usage](#setup-and-usage)<br>
- [Syntax](#syntax)<br>
- [Shared styles](#shared-styles)<br>
- [Media queries](#media-queries)<br>
- [Pseudos and combo selectors](#pseudos-and-combo-selectors)<br>
- [Selector prefixing options](#selector-prefixing-options)<br>
- [Injecting stylesheets](#injecting-stylesheets)<br>
- [Adding font resources](#adding-font-resources)<br>
- [Element attributes](#element-attributes)<br>
- [Helpful metadata](#helpful-metadata)<br>
- [Configuration options](#configuration-options)<br>
- [Useful warnings](#useful-warnings)<br>
- [Usage with build tools](#usage-with-build-tools)<br>
- [Roadmap](#roadmap)<br>
- [Development](#development)<br>
- [License](#license)

<br>

## Introduction
Kushi provides a comprehensive solution for creating and evolving web-based UI projects in ClojureScript.

The following features work in concert, making it easy to roll your own design system:
- A set of professionally designed, themeable, headless UI components
- Solid foundation of hand-tuned global + alias design tokens
- Functional styling engine
- Configurable theming

Usage of Kushi's design system and component library is completly optional. You can just use the functional styling engine for a lightweight css-in-cljs solution.

<br>

## Setup and Usage
[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.paintparty/kushi.svg)](https://clojars.org/org.clojars.paintparty/kushi)

Usage with [Reagent](https://reagent-project.github.io/) + [Shadow-CLJS](https://github.com/thheller/shadow-cljs) is currently recommended.

Please check out [Kushi Quickstart](https://github.com/paintparty/kushi-quickstart) for a well commented, feature-complete minimal project template. This is probably the easiest way to get started with Kushi.

<!--[Checkout interactive playground of pre-built headless UI components from the `kushi.ui` namespace](https://paintparty.github.io/kushi/public/index.html).-->
<br>


<!--Intro section for ui lib vs user guide -->


<!--TODO reverse examples -->
## Kushi Styling Syntax
Styles are co-located at the element level.
The macro `kushi.core/sx` takes any number of styles followed by an (optional) attributes map :
```Clojure
(ns myns.core
  (:require
   [kushi.core :refer [sx]]))
 
(defn my-component []
 [:div
  (sx :c--red
      :ta--c
      :fs--18px
      {:id :my-id})])

;; html attribute map is (optional) last arg to sx
```

As you can see in the above example, Kushi promotes a simple tokenized-keyword-based shorthand grammer which shadows standard CSS. This approach is similar to Tachyons (and its follow-on called Tailwind), but much more helpful in learning actual CSS, and much more intuitive if you are an existing CSS expert.

The above could also be written (verbosely) like this:

```Clojure
(defn my-component []
 [:div
  (sx {:id    :my-id
       :style {:color      :red
               :text-align :center
               :font-size  :18px}})])
```
You could also use shorthand syntax with style maps, and mix in tokenized keywords.

```Clojure
(defn my-component []
 [:div
  (sx :c--red
      {:style {:ta :c
               :fs :18px}
       :id :my-id})])
```

In all three examples above, the `sx` macro would return the following attribute map with an auto-generated, hashed value for the `class` attribute:

```Clojure
{:class "_680769808"
 :id    :my-id}
```

When your build finishes, the following css will be written to disk:

```css
 ._680769808 { color: red; text-align: center; font-size: 18px; }
```

If you need or want to define your own classnames, you can supply your own classname by passing a quoted symbol as the first argument to sx:

```Clojure
(defn my-component []
 [:div
  (sx 'foobar
      :c--red
      :ta--c
      :fs--18px)])
```
The above call to `sx` would generate the following attribute map:
```Clojure
{:class "foobar"}
```


And the following css will be written to disk:
```css
 .foobar { color: red; text-align: center; font-size: 18px; }
```
<br>

In summary, the `kushi.core/sx` is a macro that returns an attribute map which contains the following:

  - A `class` property containing the correct auto-generated (or prefixed) classnames.
  - If necessary, a `style` property containing the correct auto-generated css variable names.
  - All the other attributes you specify in your attributes map (supplied as an optional last arg to `sx`).
  - A dev-build-only `data-cjs` attribute for browser debugging. See [Helpful metadata](#helpful-metadata).

All your css is written to a static file, via a build hook for the `:compile-finish` stage (or similar depending on build tool).
<!---You can optionally disable writing styles to disk and enable producton builds to [inject styles at runtime](#runtime-injection).
-->
<br>

### Styles as Keywords

Keywords containing `--` represent a css prop and value pair (split on `--`).


```Clojure
:color--red
```

More examples, using Kushi's optional shorthand grammer.
```Clojure
:c--red    ; :color--red
:ai--c     ; :align-items--center
:ai--e     ; :align-items--end
:ta--r     ; :text-align--right
:fs--18px  ; :font-size--18px
:ff--serif ; :font-family--serif
```
This shorthand grammer is available for the most commonly used props:
```Clojure
:ai   ; :align-items
:b    ; :border
:bc   ; :border-color
:bi   ; :border-inline
:bb   ; :border-block
:bs   ; :border-style
:bw   ; :border-width
:bg   ; :background
:c    ; :color
:d    ; :display
:ff   ; :font-family
:fs   ; :font-size
:fv   ; :font-variant
:fw   ; :font-weight
:h    ; :height
:jc   ; :justify-content
:ji   ; :justify-items
:lh   ; :line-height
:m    ; :margin
:mb   ; :margin-block
:mbs  ; :margin-block-start
:mbe  ; :margin-block-end
:mi   ; :margin-inline
:mis  ; :margin-inline-start
:mie  ; :margin-inline-end
:o    ; :opacity
:p    ; :padding
:pb   ; :padding-block
:pbs  ; :padding-block-start
:pbe  ; :padding-block-end
:pi   ; :padding-inline
:pis  ; :padding-inline-start
:pie  ; :padding-inline-end
:ta   ; :text-align
:td   ; :text-decoration
:tt   ; :text-transform
:w    ; :width
:ws   ; :white-space
:zi   ; :z-index
```

<!-- TODO maybe just link to source? -->
See the complete list of supported css properties [here](https://github.com/paintparty/kushi/blob/main/docs/kushi-shorthand-reference.md).

Shorthand grammer extends to cover enumerated values:
```Clojure
;; text-decoration
:td--u   ; text-decoration--uppercase
:td--o   ; text-decoration--overline
:td--lt  ; text-decoration--line-through

;; background-repeat
:bgr--nr ; background-repeat--no-repeat
:bgr--rx ; background-repeat--repeat-x
:bgr--ry ; background-repeat--repeat-y
:bgr--r  ; background-repeat--round
:bgr--s  ; background-repeat--space

;; align-items
:ai--c   ; align-items--center
:ai--fs  ; align-items--flex-start
:ai--fe  ; align-items--flex-end
:ai--n   ; align-items--normal
:ai--s   ; align-items--start
:ai--e   ; align-items--end
:ai--b   ; align-items--baseline
```

Note that the enumerated value `none`, as well as global properties such as `inherit`, `initial`, `revert`, `unset`, etc. are intentially not supported with shorthand syntax:

```Clojure
;; This will NOT work
:td--r

;; This will work
:td--revert ; => text-decoration: revert;
```
<!-- TODO maybe just link to source? -->
See the complete list of supported enum values [here](https://github.com/paintparty/kushi/blob/main/doc/kushi-shorthand-reference.md).

<br>

### Expressing dynamic values

Sometimes you need to use dynamic values based on application state.

```Clojure
;; Assuming there is a binding to `mycolor` with a value of `:red`

;; Expressed as a 2-element vector
(sx :fs--36px [:c mycolor])

;; You could also write this as:
(sx :fs--36px {:style {:color mycolor})
```

Both examples above would result in the following attribute map.
```Clojure
{:class "_617784030" :style "--mycolor:red"}
```
And the following css would be written to disk:
```css
._617784030 {color: var(--mycolor); font-size: 36px}
```
<br>

### Expressing complex values
Sometimes, css syntax is inherently convoluted. In these cases, you may want or need to express a style as a 2-element vector.

When a string is desired, or necessary:
```Clojure
(sx [:before:content "\"$\""]
    [:width "calc((100vw / 3) + 12px)"])
```
When constructing a value using css function syntax:
```Clojure
(sx [:transform '(translateY :-100px)]])
```
You can likewise locate these as entries in the `:style` entry of the attributes map:
```Clojure
(sx {:style {:transform '(translateY :-100px)}}])
```

<br>

### Using css function syntax
As seen in the example above, you can use a quoted list to convey css function values.
```Clojure
(sx [:color '(rgba 0 200 100 0.4)])

;; The above example is equivalent to:
"rgba(0, 200, 100, 0.4)"
```

<br>

### Using CSS custom properties

The following sugar is supported for css variables:

```Clojure
(sx :border-radius--:--mycssvarname)

(sx [:border-radius :--mycssvarname)

(sx {:style {:border-radius :--mycssvarname})

;; All of the above would be equivalent to:

(sx {:style {:color "--var(mycssvarname)"})
```

<!-- Add section for defining css custom props -->



<br>


### CSS Shorthand Properties
[CSS shorthand properties](https://developer.mozilla.org/en-US/docs/Web/CSS/Shorthand_properties) are a fundamental feature of CSS. They are properties that let you set the values of multiple other CSS properties simultaneously. With Kushi, you can write them like this:

```Clojure
;; with tokenized keyword
(sx :b--1px:solid:black)

;; with style tuple
(sx [:b :1px:solid:black])

;; With string
(sx [:b "1px solid black"])

;; with style map
(sx {:style {:b :1px:solid:black}})

```

All of the above examples will resolve to the following css declaration:
```css
border: 1px solid black;
```
<br>

### CSS Value Lists
In css, sometimes multiple values are seperated by commas to indicate they are ordered, or that there are ordered alternatives. With Kushi, you can write them like this:
```Clojure
(sx :ff--FiraCodeRegular|Consolas|monospace)
```
The above will resolve to the following css declaration:
```css
font-family: FiraCodeRegular, Consolas, monospace;
```
The example below uses a list of css shorthand values in order to render multiple text-shadows in different colors:
```Clojure
(sx :text-shadow--5px:5px:10px:red|-5px:-5px:10px:blue)
```
The above will resolve to the following css declaration:
```css
text-shadow: 5px 5px 10px red, -5px -5px 10px blue;
```

<br>

## Shared Styles
`kushi.core/defclass` is intended for the creation of shared styles.

These shared styles should be defined in a dedicated namespace, or set of dedicated namespaces, and required once in your core or main ns.

Unlike the `sx` macro, defclass does not support runtime bindings.

 <!-- This css class is only written to disk if a component references it. -->

```Clojure
(ns myapp.shared-styles
  (:require
   [kushi.core :refer [defclass]]))


;; using tokenized keywords
(defclass headline
  :ta--left
  :w--100%
  :ff--Inter|sys|sans-serif
  :fw--900
  :fs--24px
  :tt--u
  :mix-blend-mode--darken)


;; tokenized-keywords + usage of 2-element vectors for css-fn syntax
(defclass headline2
  :top--0
  :left--0
  :b--1px:solid:black
  :fs--200px
  :tt--u
  :mix-blend-mode--darken
  [:c '(rgba 155 155 155 0.8)])


;; Example using a single map.
;; Note that when using a map, unlike the sx macro it
;; does not need to be a nested `:style` entry.
(defclass headline3
  {:top               0
   :left              0
   :b                 :1px:solid:black
   :fs                :200px
   :tt                :u
   :mix-blend-mode    :darken
   :c                 '(rgba 155 155 155 0.8)})


;; Unlike the `sx` macro, defclass does not support runtime bindings.
;; The following will NOT work.
(def mycolor :red)

(defclass headline-alert
  {:c mycolor})
```
By authoring your shared styles in a dedicated ns (or namespaces), you only need to require once in your main or core ns, and all the styles from that ns will be available globally.
```Clojure
(ns myapp.core
  (:require
   [kushi.core :refer [sx]]
   [myapp.shared-styles]))

  (defn my-headline [text]
    [:h1 (sx :.headline :mt--5px) text])

;; The above call to the sx macro will return attribute map like this:
;; {:class "headline _887777949"}

;; The resulting css would be:

;; .headline {
;;     top: 0px;
;;     left: 0px;
;;     border: 1px solid black;
;;     font-size: 200px;
;;     text-transform: uppercase;
;;     mix-blend-mode: darken;
;; }

;; ._887777949 {
;;     margin-top: 5px;
;; }

;; The `.headline` selector is the shared class,
;; and the `._887777949` is the autogenerated selector for margin-top rule.
```
As arguments to `sx`, classes are distinguished from other prop-styles by using a keyword beginning with a `.`, e.g. `:.headline`, as in the example above.

With `defclass`, you can mix-in any other defined classes:<br>
```Clojure
(defclass headline
  :.flex-row-fs
  :top--0
  :left--0
  :b--1px:solid:black
  :fs--200px
  :tt--u
  :fs--italic
  :mix-blend-mode--darken)

;; The above example will mix-in the individual declarations
;; of the :.flex-row-fs class and result in the following css:

;; .headline {
;;   display: flex;
;;   flex-direction: row;
;;   justify-content: center;
;;   top: 0px;
;;   left: 0px;
;;   border: 1px solid black;
;;   font-size: 200px;
;;   text-transform: uppercase;
;;   mix-blend-mode: darken;
;; }

(defclass headline-colored
  :.headline
  :c--red
  :b--1px:solid:pink)

;; The above example will mix-in the individual declarations
;; of the :.headline class and result in the following css:

;; .headline {
;;   display: flex;
;;   flex-direction: row;
;;   justify-content: center;
;;   top: 0px;
;;   left: 0px;
;;   font-size: 200px;
;;   text-transform: uppercase;
;;   mix-blend-mode: darken;
;;   c--red
;;   border: 1px solid pink;
;; }
```
In the example above, the `:.headline` class is one of several predefined classes that ships with kushi.

The full list of predefined classes:

```Clojure
;; positioning
:.absolute
:.absolute-centered
:.absolute-fill
:.relative
:.fixed
:.fixed-fill

;; background-images
:.bgi-contain
:.bgi-cover
:.debug-grid
:.debug-grid-16
:.debug-grid-16-solid
:.debug-grid-8-solid

;; flex layouts
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

;; borders & outlines
:.bordered
:.outlined
:.pill

;; type styling
:.sans
:.italic
:.oblique
:.uppercase
:.lowercase
:.capitalize
:.full-width
:.full-width-kana

;; type weight
:.thin
:.extra-light
:.light
:.regular
:.medium
:.semi-bold
:.bold
:.extra-bold
:.heavy

;; cursor
:.pointer

;; transitions
:.transition

;; psuedo-element helper
:.content-blank

```
<!-- TODO add debug grid helpers to above list -->

Checkout [this source file](https://github.com/paintparty/kushi/blob/main/src/kushi/ui/utility.cljc) for a complete reference of all current pre-defined utility classes.
<br>

### Applying Classes Conditionally

You can apply classes conditionally within the `sx` macro using the following constructs: `if` `when` `cond` `if-let` `when-let` `if-not`, and `when-not`.<br>
```Clojure
;; In your ns for shared styles
(defclass active-link :color--red)

;; In some other ns
(defn link [opts]
 [:a
  (sx 'mylink
      (when (:active? opts) :.active-link)
      :bb--1px:solid:black))
  "Go"])

;; Somewhere else in your code, calling above component

[link {:active? true}]
; => [:a {:class ["active-link" "mylink"]}]

;; "active-link" is the selector for your custom defclass.
```
The class to be returned cannot be nested. For example, the following will not work:
```Clojure
;; This will NOT work.

(def foo true)

(defn link [opts]
 [:a
  (sx (when (:active? opts)
        (if foo :.active-link :.some-other-class))
      :bb--1px:solid:black))
  "Go"])

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
[:xsm {:min-width :480px}
 :sm {:min-width :640px}
 :md {:min-width :768px}
 :lg {:min-width :1024px}
 :xl {:min-width :1280px}
 :xxl {:min-width :1536px}]
```
Both the names and values can be customized via supplying a kwargs vector (not a map) as the `:media` entry in your `kushi.edn` config file. Becuase CSS Media Queries must be explicity ordered, this scale must be written as a vector of kwargs. See [Configuration Options](#configuration-options).

Below is an example of a scale that is desktop-first and uses different names.<br>
Note that in the case of desktop-first (`max-width`), the order is reversed (relative to mobile-first / `min-width`).
```Clojure
[:desktop {:max-width :1280px}
 :tablet {:max-width :1024px}
 :mobile {:max-width :768px}
 :small {:max-width :640px}]
```
Any media-query modifier that you use must correspond to a key in the breakpoint map.

When "stacking" other modifiers (such as psuedo-classes) in front of css props, the media queries must always come first.
<!-- TODO: Provide example of such stacking -->

<br>

## Pseudos and Combo Selectors
Pseudo-classes, pseudo-elements, and combo selectors are available via modifiers:
```Clojure
[:div (sx 'foo
          :hover:c--blue
          :>a:hover:c--red
          :&_a:hover:c--gold   ; The "_" gets converted to " "
          :&.bar:hover:c--pink
          :before:fw--bold
          :after:mie--5px
          ["~a:hover:c"     :blue] ; Because "~" is not valid in a keyword
          ["nth-child(2):c" :red] ; Because "(" and ")" are not valid in keywords
          [:before:content  "\"⌫\""])
 [:a "Erase"]]
```
CSS resulting from the above example:
```css
.foo:hover {
  color: blue;
}

.foo > a:hover {
  color: red;
}

.foo a:hover {
  color: gold;
}

.foo.bar:hover {
  color: pink;
}

.foo::before {
  font-weight: bold;
  margin-inline-end: 5px;
  content: "⌫";
}

.foo::after {
  margin-inline-end: 5px;
}

.foo ~ a:hover {
  color: blue;
}

.foo:nth-child(2) {
  color: red;
}
```

### Parents and ancestors
Kushi provides a special sugar token in the form of `has-parent()` and `has-ancestor()` to achieve further specificity with regards to parents and ancestors of the element that you are styling. This is useful when you want to use styles that might change when, for example, a class is toggled or changed further up in the DOM.


```Clojure
(defn my-button [text]
  [:button
   (sx 'foo
       ["has-ancestor(section.baz):color" :blue]
       ["has-parent(section.dark):color" :white]
       {:on-click #(prn "clicked!")})
     text])

```
The above would result in the following css:
```css
section.baz .foo {color: blue}
section.dark > .foo {color: white}
```

### Targeting dark mode
You can use the `dark` modifier to define styles that are scoped to the dark themes. This is sugar for `has-ancestor(.dark)`. It is assumed there will potentially be a class of `.dark` on an ancestor element in the DOM. This would typically be the `<body>` or the target element for the app.

```Clojure
(defn my-button [text]
  [:button
   (sx 'foo
       :dark:color--hotpink
       :dark:b--2px:solid:hotpink
       :dark:&_.some-other-class:c--white
       {:on-click #(prn "clicked!")})
     text])

```
The above would result in the following css:
```css
.dark .foo {color: hotpink; border: 2px solid hotpink}
.dark .foo .some-other-class {color: white}
```

You can use `kushi.ui.core/lightswitch!` to toggle a `.dark` class on the body, or a specific element of your choice.
```Clojure
(ns myns.core
  (:require [kushi.ui.core :refer [lightswitch!]]))

;; Toggle `.dark` class on body
(lightswitch!)

;; Toggle `.dark` class using querySelector
(lightswitch! "#my-id")

;; Any querySelector is valid and will work as long as it
;; corresponds to an existing element in the DOM.
(lightswitch! "div.some-class")

```
<br>

## Selector Prefixing Options
You can narrow the specicifity of you selectors by globally prepending a class or id (or any valid selector) of an ancestor element. Typically this would be something like the id of your "app" container.
```Clojure
;; In your kushi.edn map ...
{:selector-prepend "#my-app"}

;; In one of your component namespaces ...
[:div
 (sx '-my-el :c--red)]

;; The above example would write the following rule to the css file:
;; #my-app .my-el {
;;    color: red;
;;}
```


<br>

## Defining Animations

Use `kushi.core/defkeyframes` to define css keyframes.
```Clojure
;; This will twirl something on its y-axis
(defkeyframes yspinner
  [:0% {:transform (cssfn :rotateY :0deg)}]
  [:100% {:transform (cssfn :rotateY :360deg)}])


;; Somewhere in your component code...
[:div
 (sx :fs--32px
     :animation--yspinner:12s:linear:infinite)
 "Round & Round"]
```
<br>

<!--
## Using Scales
Kushi ships with two different predefined scaling systems, which provide a scale of values for `width`, `font-size`, `padding`, `margin`, and `border-widths`.

These two systems shadow the scales provided by [Tachyons](http://tachyons.io/docs/typography/scale/) and [Tailwindcss](https://tailwindcss.com/docs/font-size).

You must explicitly opt-in to use one of the scales in your `kushi.edn` config file:
```Clojure
{...
 :scaling-system :tachyons
 ...}

; or the tailwind flavor

{...
 :scaling-system :tailwind
 ...}
```

To use values from these scales, supply a value affixed with an `*` to one of the applicable css properties:
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
-->

## Injecting Stylesheets
You can also use `kushi.core/inject-stylesheet` to inject a stylesheet, or a third-party style library.
This is more of an edge case, as you would typically just do this with a `<link>` in your index.html.
However, if your project uses a clj file to generate the contents of your `<head>` at build time,
it may be handy to use this during development to inject new stylesheets without restarting your build.

```Clojure
(inject-stylesheet {:rel "stylesheet"
                    :href "css/my-global-styles.css"})
```
### Loading Google Fonts
A more common use case for injecting a stylesheet would the loading of webfonts via stylesheets, ala Google Fonts, or another similar webfonts service.

You can leverage `kushi.core/add-google-font!` to simplify the process of adding Google fonts to your project.

The example below is a typical use case which loads a stylesheet from Google Fonts.
```Clojure
(ns myapp.core
  (:require
   [kushi.core :refer [add-google-fonts!]]))

(add-google-fonts! {:family "Playfair Display"
                    :styles {:normal [400 700]
                             :italic [400 700]}})

;; The above call is equivalent to the following:

;; Note - the additional "preconnect" hints will improve Google Fonts performance.

;; (inject-stylesheet {:rel "preconnet"
;;                     :href "https://fonts.gstatic.com"
;;                     :cross-origin "anonymous"})

;; (inject-stylesheet {:rel "preconnet"
;;                     :href "https://fonts.googleapis.com"})

;; (inject-stylesheet {:rel "stylesheet"
;;                     :href "https://fonts.googleapis.com/css2?family=Playfair+Display:ital,wght@0,400;0,700;1,400;1,700&display=swap"})

```
`kushi.core/add-google-font!` accepts any number of args, each one a single map that represents a font-family and associated weights & styles. You can as many different families as you want in a single go (although be mindful of performance!):


```Clojure
(add-google-fonts! {:family "Playfair Display"
                    :styles {:normal [400 700] :italic [400 700]}}
                   {:family "Lato"
                    :styles {:normal [100 400]}}
                   {:family "Pacifico"
                    :styles {:normal [400]}})
```
<br>

## Adding Font Resources
You can use the `kushi.core/add-font-face` macro to load a local font from a file.

This will add an `@font-face` block to the css file generated by kushi.

The `:src` entry must be a path (string), or vector of paths if you want to specify multiple urls.

The path(s) must be relative to the location of the generated css file.

You could also use a remote url to load a hosted font file.
```Clojure
(add-font-face {:font-family "FiraCodeRegular"
                :font-weight "400"
                :font-style "normal"
                :src ["url(../fonts/FiraCode-Regular.woff)"]})
```

### System Font Stack
You can use the `kushi.core/add-system-font-stack` macro to use a system font stack.
This uses an efficient, [`@font-face`-based  approach](https://github.com/csstools/system-font-css) introduced by Jonathan Neal.
```Clojure
; In your core namespace
(add-system-font-stack)
```
The example above would add a total of 8 `@font-face` definitions to your kushi css file.
One `normal` and one `italic` for weights `300`("light"), `400`("regular"), `500`("semi-bold"), and `700`("bold"). Note that the name of the font-family kushi provides is **`sys`**, *not* `system-ui`. This is for [differentiation](https://developer.mozilla.org/en-US/docs/Web/CSS/font-family) and to [help avoid confusion](https://infinnie.github.io/blog/2017/systemui.html).

```CSS
@font-face {
  font-family: sys;
  font-style: normal;
  font-weight: 300;
  src: local(".SFNS-Light"), local(".SFNSText-Light"), local(".HelveticaNeueDeskInterface-Light"), local(".LucidaGrandeUI"), local("Segoe UI Light"), local("Ubuntu Light"), local("Roboto-Light"), local("DroidSans"), local("Tahoma");
}
@font-face {
  font-family: sys;
  font-style: italic;
  font-weight: 300;
  src: local(".SFNS-LightItalic"), local(".SFNSText-LightItalic"), local(".HelveticaNeueDeskInterface-Italic"), local(".LucidaGrandeUI"), local("Segoe UI Light Italic"), local("Ubuntu Light Italic"), local("Roboto-LightItalic"), local("DroidSans"), local("Tahoma");
}
/* + 6 more */
```
If you want to be more precise you can pass in only the weights you need. The example below would write a total of 4 `@font-face` rules to your kushi css file (`normal` and `italic` for both `300`("light") & `700`("bold")).
```Clojure
(add-system-font-stack 300 700)
```

Then you can use the system font stack like so:
```Clojure
[:div (sx :font-family--sys)]

; Using kushi shorthand:
[:div (sx :ff--sys)]

; An example using kushi syntax to specify multiple values for the css shorthand `font` property.
[:div (sx :font--italic:700:sys)] ;

```

<br>

## Helpful Metadata
Relative to using vanilla css or sass, Kushi will obviate the need to write your styles in a separate location and/or language. In turn, you will not need to worry about keeping selector names in css files synced with classnames in your markup code.

During development builds, the `sx` macro will automatically attach a `data-sx` attribute to the DOM element. The value of this is the file name, line number, and column number of the source.
```Clojure
(ns myapp.core
  (:require
   [kushi.core :refer [sx]]))

;; A component defined, for example, on line 170
(defn my-button [text]
  [:button
   (sx :c--white
       :bgi--none
       :bgc--blue
       :border-radius--5px
       :cursor--pointer)
     text])
```
You would see something like this in the browser console, when inspecting an element rendered from this function:
```html
<div data-sx="myapp.core.cljs:172:4" class="_617784030">
  Button Text
</div>
```
If you would like to change the name of this attribute to something else (for example, `data-foo`), simply supply a `:data-attr-name` entry with a value of `:foo` or `"foo"` in your `kushi.edn` config map.

<br>
<br>

## Configuration Options
Various options are configurable via a required `kushi.edn` file.

This file must live in your project's root directory.

The only required entry in this map is `:css-dir`.

Below is a full map of all the options available. The values shown in the map below correspond to the default values.
If you are looking for a well commented starting point for your own config, [the sample `kushi.edn` config from the Kushi Quickstart template](https://github.com/paintparty/kushi-quickstart/blob/main/kushi.edn) (similar to below) is recommended.
```Clojure
{
 ;; REQUIRED

 ;; Needs to be a path to a dir, e.g. "public/css"
 ;; The example above would write to "public/css/kushi.css"
 :css-dir                nil


 ;; OPTIONAL

 ;; You can specify your own filename, e.g. "mystyles.css"
 :css-filename           "kushi.css"

 ;; Fully qualified name of user theming map.
 ;; This needs to be defined in a clj namespace, and saved as a `.clj` file.
 ;; e.g. `myproject.theme/theme`
 :theme                   nil

 ;; Optionally defined your own breakpoint scale to override
 ;; kushi's default breakpoint scale.
 ;; This must be a vector of kwargs, not a map.
 :media [:2xl {:max-width :1536px}
         :xl {:max-width :1280px}
         :lg {:max-width :1024px}
         :md {:max-width :768px}
         :sm {:max-width :640px}]

 ;; Optionally disable build caching.
 :caching?                true

 ;; Prepend to generated selectors, useful for narrowing scope.
 ;; Usually would be the id of the "app" container, e.g "#app".
 :selector-prepend        nil

 ;; Optionally narrow the scope of generated design tokens.
 ;; Usually would be the id of the "app" container, e.g "#app".
 ;; If nil, defaults to using ":root"
 :design-tokens-root      nil
 :data-attr-name          :sx

 ;; Runtime injection
 :inject-at-runtime-prod? false
 :inject-at-runtime-dev?  true

 ;; Logging
 :log-build-report?       true
 :log-build-report-style  :simple ;; or :detailed
 :log-kushi-version?      true
 :log-updates-to-cache?   false
 :log-cache-call-sites?   false


 ;; ADVANCED

 ;; For leaving specific things out of css, set individual options below to `false`.
 ;; You probably don't want to override these unless you are only using
 ;; Kushi's styling engine (`sx`, `defclass`, etc.) and not using any prebuilt
 ;; kushi.ui lib components or kushi's design tokens, etc.
 :add-stylesheet-prod?    true
 :add-stylesheet-dev?     true
 :add-css-reset?          true
 :add-system-font-stack?  true
 :add-design-tokens?      true
 ;; If :add-kushi-ui-theming? is set to false, it will not include
 ;; theming classes for for kushi ui components such as buttons, tags, etc.
 :add-kushi-ui-theming?   true
 :add-ui-theming?         true
 :add-kushi-defclass?      true
 :add-user-defclass?      true
 :add-user-sx?            true}
```

<br>



## Useful Warnings
It is highly recommended to keep the terminal (that is running the `cljs-shadow` build process) visible so that you can catch warnings for malformed arguments to Kushi functions.

Given the following:
```Clojure
(sx :.relative
    :ta--center
    :.sans
    :p--10px
    12
    :fs--18px
    :c--#efefef)
```

You would receive warnings about invalid args in the terminal:

```
◢◤◢◤ WARNING ◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤

File: collage/compound_rules_editor/ui/nav.cljs:65:9

65 |  (sx
66 |   :.relative
67 |   :ta--center
68 |   "badstring"
69 |   :p--10px
70 |   :m-10px
71 |   12
72 |   :border-radius--18px)

Invalid args:
:m-10px
12

◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤◢◤
```
<br>
<br>

## Defining Components
Below is a contrived example of creating a reusable, stateless, and composable component using `kushi.ui.core/defcom`.


```Clojure
(ns myapp.core
  (:require
   [kushi.core :refer [sx merge-attrs]]
   [kushi.ui.core :refer [defcom]]))

(defcom my-section
  (let [{:keys [label label-attrs body-attrs]} &opts]
    [:section
     (merge-attrs (sx :c--black)
                  &attrs
     (when label [:div label-attrs label])
     [:div body-attrs &children]]))
```

`defcom` is a macro that returns a component rendering function which accepts an optional attributes map, plus any number of children. The signature at the call site mirrros hiccup itself.

Under the hood, `defcom` pulls out any keys in attr map that start with `:-` and puts them in a separate `opts` map. This allows passing in various custom options within the attributes map that will not clash with existing html attributes. This opts map can be referenced in the `defcom` body with the `&opts` binding. `&attrs` and `&children` are also available. This ampersand-leading naming convention takes its cue from the special `&form` and `&env` bindings used by Clojure's own `defmacro`.

The example above also uses `kushi.core/merge-attrs` to carefully merge attribute maps that are created with `kushi.core/sx`.

Assuming your are using something like Reagent, you can use the resulting `my-section` component (from the above example) in your application code like so:

```Clojure
;; Basic, no label
[my-section [:p "Child one"] [:p "Child two"]]

;; With optional label
[my-section (sx {:-label "My Label"}) [:p "Child one"] [:p "Child two"]]

;; With all the options and additional styling
[my-section
 (sx
  'my-section-wrapper    ; Provides custom classname (instead of auto-generated).
  :.xsmall               ; Font-size utility class.
  :p--1rem               ; Padding inside component.
  :b--1px:solid-black    ; Border around component.
  {:-label "My Label"
   :-label-attrs (sx :.huge :c--red)
   :-body-attrs (sx :bgc--#efefef)})
 [:p "Child one"]
 [:p "Child two"]]

```
<br>

### Manually defining complex components

 If, for some reason, you don't want use the `defcom` to define your complex components, you can use the same underlying pattern that `defcom` abstracts. This component definition pattern relies on using the `kushi.ui.core/opts+children` helper fn. It optionally makes use of `kushi.core/merge-attrs` to enable decoration, and also uses the `(into [:div ] ...)` for the parent node of the `children`.

The `my-section` function below would result in the exact same component as the previous example (that used `defmacro`).

```Clojure
(ns myapp.core
  (:require
   [kushi.core :refer [sx]]
   [kushi.ui.core :refer [opts+children]]))

(defn my-section
  [& args]
  (let [[opts attrs & children]  (opts+children args)
        {:keys [label label-attrs body-attrs]} opts]
    [:section
     attrs
     (when label
       [:div label-attrs label])
     (into [:div body-attrs] children)]))
```

The example above assumes the following:

- The args list in the function definition is variadic
- The optional attributes map may contain the custom attributes `:-label`, `:-label-attrs`, `:-body-attrs`.
- The values of `:-label-attrs` and `:-body-attrs` are html attribute maps.

The helper function `kushi.ui.core/opts+children` will pull any keys prefixed with `:-` out of the attributes map and into a user `opts` map. `opts+children` always returns a vector in the form of `[user-opts attr child & more-children]`.

<!-- ### Theming
You can theme -->
<!-- <br> -->

<br>

## Usage with Build Tools
Although Kushi is designed to be build-tool and framework agnostic, thus far it has only been used in production with [Reagent](https://reagent-project.github.io/) + [Shadow-CLJS](https://github.com/thheller/shadow-cljs).

### shadow-cljs
See the [kushi-quickstart](https://github.com/paintparty/kushi-quickstart) template for a detailed example of using Kushi in a shadow-cljs project.

<br>

<!--
## Roadmap
...more info coming soon.

<br>

## Development
...more info coming soon.

<br>
-->
## License

Copyright © 2021-2022 Jeremiah Coyle

Distributed under the EPL License. See LICENSE.
