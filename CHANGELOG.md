# Change Log
All notable changes to this project will be documented in this file.<br>
This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## 1.0.0-a.18
2023-5-17

### Added

#### Functionality
- `fireworks.core/breakpoints`
- `fireworks.core/token->ms`

#### Styling Engine / sx macro
- Support mixing of shorthand and map notation in argument to sx macro 
- Support `has-parent()` and `has-ancestor()`
- Support all pseudo and combo selectors
- 

#### Font loading
- `fireworks.core/add-google-font!`
- `fireworks.core/add-google-material-symbols-font!`

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
  - Accordian
  - Grid

- `kushi.ui.core/lightswitch!` for toggling between light and dark modes.

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
- Color pallet of `gray`, `purple`, `blue`, `green`, `lime`, `yellow`, `gold`, `orange`, `red`, `magena`, and `brown`
- Scaled transparent grayscale design tokens e.g. `$white-transparent-50`, `$dark-gray-transparent-15`, etc.
- `:.bordered-red`, `:.bordered-blue`, etc. for all colors in stock kushi pallette.
- Support syncing of type weight with radio and checkbox outline/border-width  


#### Interactive documentation
- Added a `kushi.playground.*` set of namespaces for generating interactive documentation
site for projects.

#### Build system
- Ability to elide sections of css output by category.
- Added various `:log-*` options to config to control what gets logged on each build.

#### DOM utilility namespace
- Added `kushi.ui.dom` utility namespace for helper fns like `copy-to-clipboard!`, `scroll-into-view!`, `screen-quadrant`, and many more.




### Changed
- `kushi.core/merge-with-style` renamed to `kushi.core/merge-attrs` 

#### Icons
- Uses Material Symbols instead of Material Icons




### Fixed
- `kushi.core/merge-with-style` renamed to `kushi.core/merge-attrs` 



### Removed
- `fireworks.core/add-system-font-stack`
- Explicit `map-mode`.


<br>

## 1.0.0-alpha
2022-1-14

### Added
- [`merge-with-style` function for more flexible composibility](https://github.com/paintparty/kushi/issues/1)

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

- `:select-ns` config option for narrrowing compilation to select namespaces

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

- Preformatting of js warnings for bad args fixes edge case dev build exceptions.
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
