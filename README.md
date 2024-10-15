<p><sub>Many thanks to <a href="https://www.clojuriststogether.org/">Clojurists Together</a> for generously supporting this project!</sub></p>

<br>

# Kushi
<h5>Kushi is a foundation for building web UI with ClojureScript.</h5>
<h5><a href="https://kushi.design" target="_blank"><strong>Explore Kushi UI Playground »</strong></a></h5>
<h5><a href="https://github.com/kushidesign/kushi-quickstart" target="_blank"><strong>Kushi Quickstart »</strong></a></h5>

<br>

## Features
- **100% Clojure(Script)**

- **Compile-time macros generate static CSS**

- **Co-location of styling at the element level**

- **Shorthand styling syntax shadows CSS standard**

- **Suite of accessible,  headless UI components**

- **Themeable design system foundation**

- **Supports media-queries, psuedos, and combo selectors**

- **Leverages CSS variables for runtime dynamics**

- **Composable, user-defined shared classes**

- **Many useful CSS utility classes**

- **Default industry-standard breakpoint scale**

- **Auto-generated selectors to avoid pontential collisions**

- **Flexible selector prefixing options**

- **Helpers for typography, keyframes, and more**

- **Enhanced debugging via metadata**

- **Detailed, human-readable warnings**

- **Framework & build-tool agnostic**

- **Generates interactive UI documentation**





<br>

## Table of Contents
- [Introduction](#introduction)<br>
- [Project Status](#project-status)<br>
- [Setup and usage](#setup-and-usage)<br>
- [Kushi styling syntax](#kushi-styling-syntax)<br>
- [Shared styles](#shared-styles)<br>
- [Media queries](#media-queries)<br>
- [Pseudos and combo selectors](#pseudos-and-combo-selectors)<br>
- [Selector prefixing options](#selector-prefixing-options)<br>
- [Injecting stylesheets](#injecting-stylesheets)<br>
- [Adding font resources](#adding-font-resources)<br>
- [Helpful metadata](#helpful-metadata)<br>
- [Configuration options](#configuration-options)<br>
- [Actionable warnings](#actionable-warnings)<br>
- [Defining components](#defining-components)<br>
- [Kushi Playground](#kushi-playground)
- [Usage with build tools](#usage-with-build-tools)<br>
- [Contributing](#contributing)<br>
- [License](#license)

<br>

## Introduction
Kushi provides a comprehensive solution for creating and evolving web-based UI projects in ClojureScript.

The following features work in concert, making it easy to roll your own design system:
- A set of professionally designed, themeable, headless UI components
- Solid foundation of hand-tuned global + alias design tokens
- Functional styling engine
- Configurable theming

Usage of Kushi's design system and component library is completly optional. You can just use the functional styling engine for a lightweight compile-time css-in-cljs solution.

<br>

## Project status
Current version is pre-release intended for early adopters and anyone who would like to provide feedback. New 1.0 alphas will be released frequently, while I continue to make improvements/changes/additions. Working towards a stable 1.0 release by end of 2024 or Q1 of 2025.

Please report anything unexpected on GitHub Issues.

<br>

## Setup and Usage
[![Clojars Project](https://img.shields.io/clojars/v/design.kushi/kushi.svg)](https://clojars.org/design.kushi/kushi)

Usage with [Reagent](https://reagent-project.github.io/) + [Shadow-CLJS](https://github.com/thheller/shadow-cljs) is currently recommended.

Please check out [Kushi Quickstart](https://github.com/kushidesign/kushi-quickstart) for a well commented, feature-complete minimal project template. This is probably the easiest way to get started with Kushi.

Checkout the
<a href="https://kushidesign.github.io/kushi/public/index.html" target="_blank">
interactive playground</a> of pre-built headless UI components.

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
  - A dev-build-only `data-sx` attribute for browser debugging. See [Helpful metadata](#helpful-metadata).

All your css is written to a static file, via a build hook for the `:compile-finish` stage (assuming shadow-cljs).
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
See the complete list of supported css properties <a href="https://github.com/kushidesign/kushi/blob/main/docs/kushi-shorthand-reference.md" target="_blank">here</a>.

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
See the complete list of supported enum values [here](https://github.com/kushidesign/kushi/blob/main/doc/kushi-shorthand-reference.md).

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

### Nested syntax
You can also you the 2-element vector form to "nest" styles, which is really just a way to dry up code and avoid repetition of the left half of the style:
```Clojure
(sx ["has-ancestor(nav[data-foo-bar-sidenav][aria-expanded=\"true\"])"
     {:>.sidenav-menu-icon:d  :none
      :>.sidenav-close-icon:d :inline-flex
      :>ul:h                  "calc((100vh - (var(--navbar-height) * 2)) * 1)"
      :h                      :fit-content
      :o                      1}])
```
The above would result in the following css:
```css
 nav[data-foo-bar-sidenav][aria-expanded="true"] ._1209178574>.sidenav-menu-icon {
  display: none;
}

 nav[data-foo-bar-sidenav][aria-expanded="true"] ._1209178574>.sidenav-close-icon {
  display: inline-flex;
}

 nav[data-foo-bar-sidenav][aria-expanded="true"] ._1209178574>ul {
  height: calc((100vh - (var(--navbar-height) * 2)) * 1);
}

 nav[data-foo-bar-sidenav][aria-expanded="true"] ._1209178574 {
  height: fit-content;
  opacity: 1;
}
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

The following sugar is supported for css variables values:

```Clojure
(sx :border-radius--$mycssvarname)

(sx [:border-radius :$mycssvarname)

(sx {:style {:border-radius :$mycssvarname})
```
All of the above would generate the following css value:

```css
border-radius: var(--mycssvarname);
```

Up to 2 additional fallback values can be supplied using the `||` syntax:

```Clojure
(sx :border-radius--$mycssvarname||$myfallback||10px)
```
The above would generate the following css value:

```css
border-radius: var(--mycssvarname, var(--myfallback, 10px));
```

css custom properties can be defined (on the element level) like this:

```Clojure
(sx $mycustomprop--10px)

(sx [$mycustomprop :10px])

(sx {:style {$mycustomprop :10px})
```
All of the above would generate the an attribute map looking something like this:

```css
{:class ["_897766778"]
 :style {"--mycustomprop" "10px"}}
```


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
  :ff--Inter|system-ui|sans-serif
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
;;   align-items: center;
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
;;   align-items: center;
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

Other predefined classes:

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


```
<!-- TODO add debug grid helpers to above list -->

Checkout <a href="https://github.com/kushidesign/kushi/blob/main/src/kushi/ui/utility.cljc" target="_blank">this source file</a>
 for a complete reference of all current pre-defined utility classes.
<br>

### Applying Classes Conditionally

You can apply classes conditionally within the `sx` macro using the following constructs: `if`, `when`, `cond`, `if-let`, `when-let`, `if-not`, and `when-not`.<br>
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
          :&_a:hover:c--gold ; The "_" gets converted to " "
          :&.bar:hover:c--pink
          :before:fw--bold
          :after:mie--5px
          ["~a:hover:c" :blue] ; Vector is used as "~" is not valid in a keyword
          ["nth-child(2):c" :red] ; Vector is used as "(" and ")" are not valid in keywords
          [:before:content "\"⌫\""])
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
Kushi provides 2 fake css pseudo-classes in the form of `has-parent()` and `has-ancestor()`. With these, you to achieve further specificity with regards to parents and ancestors of the element that you are styling. This is useful when you want to use styles that might change when a class is toggled or changed further up in the DOM.


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

## Transparent Colors
Kushi offers a special syntax for adding transparency to colors. This will work with any named css colors, hex colors, or any color that is part of Kushi's built-in color scale (click on the "Color" section in sidemenu of the [interactive docs page](kushi.design) to view color scale).

```Clojure
;; With a css named color ...
(sx :bgc--aliceblue/alpha-50)

;; With a hex color ...
(sx :bgc--00ff00/alpha-33)

;; With a color from Kushi's design tokens color scale ...
(sx :bgc--$purple-500/alpha-79)
```

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
View all the scale values [here](https://github.com/kushidesign/kushi/blob/main/src/kushi/scales.cljc).
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

<br>
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

For a well commented starting point to build your own config, [the sample `kushi.edn` config from the Kushi Quickstart template](https://github.com/kushidesign/kushi-quickstart/blob/main/kushi.edn) (similar to below) is recommended.

<br>
<br>


## Actionable Warnings
It is highly recommended to keep the terminal (that is running the `cljs-shadow` build process) visible so that you can catch warnings for malformed arguments to Kushi functions.

Given the following:
```Clojure
(sx :.flex-col-c
    :.absolute-fill
    :h--100%
    "badstring"
    :m-10px
    12
    :ai--c
    :bgc--black)
```

You would receive warnings about invalid args in the terminal:

<div align="center"><img src="docs/public/graphics/kushi-sx-bad-args-warnings.png" width="654px"/></div>

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

<br>
<br>

<!-- ## Theming
Detailed docs on theming coming soon...
<br>

<br>

## Kushi Playground
The `kushi.playground` namespace exists to enable the generation of a clean, interactive documentation site for all the UI in your project. You can customize this with all your own custom components, branding, typography, colors and more. Playground can be thought of as a lighter weight, ClojureScript-specific alternative to something like Storybook. Kushi's own UI documentation site at <a href="https://kushi.design">kushi.design</a> is built using Playground.

Detailed documentation for this feature is coming soon. In the meantime, you can peruse the `docs` dir in this repo which is the setup for the Kushi UI documentation site linked above.

<br> -->

## Usage with Build Tools
Although Kushi is designed to be build-tool and framework agnostic, thus far it has only been used in production with [Reagent](https://reagent-project.github.io/) + [Shadow-CLJS](https://github.com/thheller/shadow-cljs).

### shadow-cljs
See the [kushi-quickstart](https://github.com/kushidesign/kushi-quickstart) template for a detailed example of using Kushi in a shadow-cljs project.

<br>

## Contributing
Feel free to file issues or initiate discussion in <a href="https://github.com/kushidesign/kushi/issues" target="_blank">Issues</a>.


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

Copyright © 2021-2024 Jeremiah Coyle

Distributed under the EPL License. See LICENSE.
