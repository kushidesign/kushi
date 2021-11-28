# Change Log
All notable changes to this project will be documented in this file.<br>
This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## 1.0.0
2021-11-28

### Added
- [`merge-with-style` function for more flexible composibility](https://github.com/paintparty/kushi/issues/1)

- [`map-mode`, which enables opting-in to project-wide style-map syntax instead of default style short-hand](https://github.com/paintparty/kushi/issues/5)

- [!important helper function for wrapping dynamic css values](https://github.com/paintparty/kushi/issues/3)

- Add `visibility` prop to available shorthand list.

- Add `zi` (z-index) prop to available shorthand list.

- kushi-specific `:fn` key in attributes map to refer to var-quoted component function.

### Changed
- User-defined media queries (breakpoints) scale in kushi.edn is a vector of kwargs (instead of a map)

- `defkeyframes` takes a symbol as name, instead of a keyword

### Fixed

- [`cssfn` wraps 2nd arg in escaped quotes for :url](https://github.com/paintparty/kushi/issues/2)

- Bad arg console warnings for `defclass`

- Ensure correct printing order of media query rules

- [`deref`'ed atoms use the same css var naming convention as normal bindings](https://github.com/paintparty/kushi/issues/5)

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
