# Change Log
All notable changes to this project will be documented in this file.<br>
This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).


<br>

## 1.0.0-a.22
2024-8-26

### Changed
- Refactor of all reporting, warning, and error messaging
- Uses `io.github.paintparty/bling` for warning and error templating and printing

### Removed
- `io.aviso/pretty` dep removed.

<br>
<br>

## 1.0.0-a.21
2024-7-6

### Added
- New `kushi.ui.playground` design and implementation, which drives the `design.kushi` interactive site running on Github Pages.
- Enhanced docs on all UI components.
- New utility classes:
  - `:.offscreen`
  - `:.neutralize`
  - `:.convex`, `:.convex-0` ~ `convex-5`
  - `:.sans` and `:.serif`
- New `kushi.ui.divisor.core/divisor` component and related `$:divisor-*` tokens and `:.divisor-*` utility classes.

### Fixed
- Updated `kushi.ui.link` component
- Updated `kushi.ui.switch.core/switch` to use `:.no-shrink` class
- Positioning bug in popover pane placement logic #530b005

### Breaking changes
- The following components have been renamed:
  `kushi.ui.alert.core/alert`  -> `kushi.ui.callout.core/callout`
  `kushi.ui.progress.core/progress` -> `kushi.ui.progress.core/spinner`   
  `kushi.ui.progress.core/spinner` -> `kushi.ui.progress.core/donut`   
  `kushi.ui.input.text.core/input` -> `kushi.ui.text-field.core/text-field`

- All `<input>` related component namespaces have been simplified:
  `kushi.ui.input.checkbox.core`   -> `kushi.ui.checkbox.core`
  `kushi.ui.input.radio.core`      -> `kushi.ui.radio.core`
  `kushi.ui.input.slider.core`     -> `kushi.ui.slider.core`
  `kushi.ui.input.switch.core`     -> `kushi.ui.switch.core`
  `kushi.ui.input.text.core/input` -> `kushi.ui.text-field.core/text-field`

- All uses of `fg` in token and utility class names has been hydrated to `foreground`:<br>
  `$neutral-fg` -> `$neutral-foreground`<br>
  `:.neutral-fg` ->  `:.neutral-foreground`

- `:$progress-animation-duration` -> `:$spinner-animation-duration`

- Default value of `$pane-min-width` is now `70px`
- Default value of `$pane-min-height` is now `35px`



<br>
<br>

## 1.0.0-a.20
2024-3-21

### Added
- New floating layer abstraction & tooltip implementation. Closes #43. Closes #47.
- New popover component. Closes #49.
- New toast component. Closes #46.
- Dependency on `design.kushi/domo` in place of `kushi.ui.dom`

### Fixed
- Typos in animation utility classes.

### Breaking changes
- Removed `:-reveal-on-click?` `:-reveal-on-click-duration` from tooltip. These will resurface in upcoming toggle tip or popover component.
- Removed `kushi.dom.ui` namespace. Replaced by dependency `design.kushi/domo`



<br>
<br>

## 1.0.0-a.19
2023-5-17

### Added
- Textarea primitive support via `:-textarea?` option on `kushi.ui.input.text/input` component. Closes #43

### Fixed
- [Support normalization of keywords for equality checks within conditional class application in sx macro](https://github.com/kushidesign/kushi/commit/ea745a3d4d46f9d3aa3369c4a6dfc4a6b8e570da)
- sans/sans-serif utility class typo, Fixes #39
- `kushi.ui.modal` documentation instructions, Fixes #42
- Accordion typos
- css var tuple support

### Breaking changes
- kushi.ui.collapse.core/accordian -> kushi.ui.collapse.core/accordion




<br>

## 1.0.0-a.18

2023-5-17

### Added

#### Functionality
- `kushi.core/breakpoints`
- `kushi.core/token->ms`

#### Styling Engine / sx macro
- Support mixing of shorthand and map notation in argument to sx macro 
- Support `has-parent()` and `has-ancestor()`
- Support all pseudo and combo selectors

#### Css Reset
- `:add-css-reset?` option adds css reset to stylesheet 

#### Font loading
- `kushi.core/add-google-font!`
- `kushi.core/add-google-material-symbols-font!`

#### Typography
- Added type scale system
- Ability to create custom type scale system with half-step shifts.

#### Design tokens
- Introduced use of `$` syntax for css variables `"var(--foo)"` -> `:$foo`
- Non-combo flex utility classes such as `:.shrink` `:.grow`
- Combinatorial absolute and fixed positioning utilities such as `:.after-absolute-centered` and `:.northwest`
- Augmented display utility classes with `:.inline-block`, `:.inline-flex`, etc.
- `:.enhanceable`` utility class for icons
- `:.xxxtight` ~ `:.xxxloose` utility class scale for tracking
- `:.xxxfast` ~ `:.xxxloose` utility class scale for animations
- `:.rounded-xxxsmall` ~ `:.rounded-xxxlarge` utility class scale for rounded-corners
- `:.convex`, `:.debossed`, `:.elevated-0` ~ `:.elevated-5`


#### UI Component Library
- Added a `kushi.ui.*` set of namespaces for library components which includes

  - Button
  - Radio
  - Checkbox
  - Switch
  - Input
  - Slider
  - Tooltip
  - Icons
  - Tag
  - Label
  - Card
  - Alert
  - Modal
  - Collapse
  - Accordion
  - Grid

- `kushi.ui.core/lightswitch!` for toggling between light and dark modes.
- `kushi.ui.progress.core` ns with basics components for animated loading/progress indicators.

#### Design system
- Added complete design system with light/dark support across variants:
  - Semantic variants:
    - `neutral`
    - `warning`
    - `positive`
    - `negative`
    - `accent`
  - Kind variants:
    - `default`
    - `minimal`
    - `bordered`
    - `filled`
  - Shape variants:
    - `pill`
    - `rounded`
    - `sharp`
- Ability to theme with user `.edn` theme file.
- Color pallet of `gray`, `purple`, `blue`, `green`, `lime`, `yellow`, `gold`, `orange`, `red`, `magenta`, and `brown`
- Scaled transparent grayscale design tokens e.g. `$white-transparent-50`, `$dark-gray-transparent-15`, etc.
- `:.bordered-red`, `:.bordered-blue`, etc. for all colors in stock kushi pallette.
- Support syncing of type weight with radio and checkbox outline/border-width  


#### Interactive documentation
- Added a `kushi.playground.*` set of namespaces for generating interactive documentation
site for projects.

#### Build system
- Ability to elide sections of css output by category.
- Added various `:log-*` options to config to control what gets logged on each build.

#### DOM utility namespace
- Added `kushi.ui.dom` utility namespace for helper fns like `copy-to-clipboard!`, `scroll-into-view!`, `screen-quadrant`, and many more.




### Changed
- `kushi.core/merge-with-style` renamed to `kushi.core/merge-attrs` 

#### Icons
- Uses Material Symbols instead of Material Icons




### Fixed
- `kushi.core/merge-with-style` renamed to `kushi.core/merge-attrs` 



### Removed
- `kushi.core/add-system-font-stack`
- Explicit `map-mode`.


<br>

## 1.0.0-alpha
2022-1-14

### Added
- [`merge-with-style` function for more flexible composability](https://github.com/paintparty/kushi/issues/1)

- [`map-mode`, which enables opting-in to project-wide style-map syntax instead of default atomic syntax](https://github.com/paintparty/kushi/issues/5)

- [`!important` helper function for wrapping dynamic css values](https://github.com/paintparty/kushi/issues/3)

- [`add-system-font-stack` for adding system font stack](https://github.com/paintparty/kushi/issues/6)

- `thin`,`extra-light`,`light`,`regular`,`medium`,`semi-bold`,`bold`,`extra-bold`,`heavy` props to built-in atomic classes. These correspond to `font-weight` values `100` - `900`.

- `italic` and `oblique` (`font-style`) to built-in atomic classes

- `visibility` prop to available shorthand list.

- `zi` (z-index) prop to available shorthand list.

- `data-cljs` attribute (ns and LOC info)
is automatically added to styled elements for dev builds

- Example reageant project.

- `:select-ns` config option for narrowing compilation to select namespaces

- Support for error reporting and assertion errors in the case of duplicate `keyframe` names, duplicate `defclass` names, and duplicate `:prefix` + `:ident` combos.

- Support for bad modifier warnings.

### Changed
- User-defined media queries (breakpoints) scale in kushi.edn now expects a vector of kwargs (instead of a map)

- `defkeyframes` now takes a symbol as name, instead of a keyword

- Use `:ancestor` instead of `:parent` for ancestor prefixing options

- Removed `:sans-serif`, `:serif`, and `:code` from built-in atomic classes.

- Removed kushi-specific `:fn` key in attributes map to refer to var-quoted component function.

- Refactored `stylesheet` and `reporting` namespaces.

### Fixed

- [`cssfn` wraps 2nd arg in escaped quotes for :url](https://github.com/paintparty/kushi/issues/2)

- Bad arg console warnings for `defclass`

- Ensure correct printing order of media query rules

- [`deref`'ed atoms use the same css var naming convention as normal bindings](https://github.com/paintparty/kushi/issues/5)

- Pre-formatting of js warnings for bad args fixes edge case dev build exceptions.
## 0.1.2
2021-08-16
### Fixed
- Conditional application of classes
- Browser console warnings for invalid number values
- Diagnostic printing config options

## 0.1.1
2021-08-10
### Fixed
- Console warnings for `defclass` and `sx`
- Diagnostic printing

## 0.1.0
2021-08-06

Initial commit.
