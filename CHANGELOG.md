# Change Log
All notable changes to this project will be documented in this file.<br>
This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## 1.0.0
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
