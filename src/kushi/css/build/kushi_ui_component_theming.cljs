(ns ^{:kushi/layer "kushi-ui-theming"} kushi.css.build.kushi-ui-component-theming
  (:require
   [kushi.css.build.macros :refer [defcss]]))

(defcss
  "body"
  {"font-family" "var(--sans-serif-font-stack)",
   "color" "var(--foreground-color)",
   "background-color" "var(--background-color)",
   "transition-property" "background-color, color",
   "transition-duration" "var(--fast)",
   "transition-timing-function" "var(--timing-linear-curve)",
   "overflow-y" "scroll"})

(defcss
  ".dark, body.dark"
  {"background-color" "var(--background-color-dark-mode)",
   "color" "var(--foreground-color-dark-mode)"})

(defcss
  "code, .code"
  {"width" "fit-content",
   "transition-duration" "var(--fast)",
   "transition-property" "all",
   "font-family" "var(--code-font-stack)",
   "height" "fit-content",
   "white-space" "nowrap",
   "padding-inline" "var(--code-padding-inline)",
   "transition-timing-function" "cubic-bezier(0, 0, 1, 1)",
   "font-size" "var(--code-font-size)",
   "background-color" "var(--code-background-color)",
   "border-radius" "var(--code-border-radius)",
   "padding-block" "var(--code-padding-block)",
   "color" "var(--code-color)"})

(defcss
  ".dark code, .dark .code"
  {"background-color" "var(--code-background-color-dark-mode)",
   "color" "var(--code-color-dark-mode)"})

(defcss
  ".styled-scrollbars"
  {"scrollbar-color" "var(--scrollbar-thumb-color)",
   "scrollbar-width" "thin"})

(defcss
  ".dark .styled-scrollbars"
  {"scrollbar-color" "var(--scrollbar-thumb-color-dark-mode)"})

(defcss
  ".styled-scrollbars::-webkit-scrollbar"
  {"width" "var(--scrollbar-width)", "height" "var(--scrollbar-width)"})

(defcss
  ".styled-scrollbars::-webkit-scrollbar-thumb"
  {"background" "var(--scrollbar-thumb-color)",
   "border-radius" "9999px",
   "border" "0px solid var(--scrollbar-background-color)"})

(defcss
  ".dark .styled-scrollbars::-webkit-scrollbar-thumb"
  {"background" "var(--scrollbar-thumb-color-dark-mode)",
   "border" "0px solid var(--scrollbar-background-color-dark-mode)"})

(defcss
  ".styled-scrollbars::-webkit-scrollbar-track"
  {"background" "var(--scrollbar-background-color)"})

(defcss
  ".dark .styled-scrollbars::-webkit-scrollbar-track"
  {"background" "var(--scrollbar-background-color-dark-mode)"})

(defcss
  "*:focus-visible"
  {"outline" "4px solid rgba(0, 125, 250, 0.6)",
   "outline-offset" "1px"})

(defcss
  "*:disabled"
  {"opacity" "45%!important", "cursor" "not-allowed!important"})

(defcss
  ".kushi-radio-input:focus-visible"
  {"box-shadow" "0 0 0 4px rgba(0, 125, 250, 0.6)"})

(defcss ".kushi-tag" {"font-family" "var(--primary-font-family)"})

(defcss ".dark .kushi-radio-input" {"background-color" "black"})

(defcss ".dark .kushi-checkbox-input" {"background-color" "black"})

(defcss
  ".dark .kushi-checkbox-input:before"
  {"box-shadow" "inset 1em 1em black"})

(defcss
  ".dark .kushi-slider-step-label.kushi-slider-step-label-selected"
  {"color" "white"})

(defcss ".dark .kushi-slider-step-label" {"color" "var(--gray-300)"})

(defcss ".invisible" {"opacity" "0"})

(defcss ".hidden" {"visibility" "hidden"})

(defcss ".visible" {"visibility" "visible"})

(defcss ".collapse" {"visibility" "collapse"})

(defcss
  ".offscreen"
  {"position" "absolute",
   "left" "-10000px",
   "top" "auto",
   "width" "1px",
   "height" "1px",
   "overflow" "hidden"})

(defcss ".pointer" {"cursor" "pointer"})

(defcss ".block" {"display" "block"})

(defcss ".inline" {"display" "inline"})

(defcss ".inline-block" {"display" "inline-block"})

(defcss ".flex" {"display" "flex"})

(defcss ".inline-flex" {"display" "inline-flex"})

(defcss ".grid" {"display" "grid"})

(defcss ".inline-grid" {"display" "inline-grid"})

(defcss ".flow-root" {"display" "flow-root"})

(defcss ".contents" {"display" "contents"})

(defcss
  ".rounded-absolute"
  {"border-radius" "var(--rounded-absolute-medium)"})

(defcss ".rounded" {"border-radius" "var(--rounded)"})

(defcss ".sharp" {"border-radius" "0"})

(defcss ".pill" {"border-radius" "9999px"})

(defcss ".xxxtight" {"letter-spacing" "var(--xxxtight)"})

(defcss ".xxtight" {"letter-spacing" "var(--xxtight)"})

(defcss ".xtight" {"letter-spacing" "var(--xtight)"})

(defcss ".tight" {"letter-spacing" "var(--tight)"})

(defcss ".default-tracking" {"letter-spacing" "0"})

(defcss ".loose" {"letter-spacing" "var(--loose)"})

(defcss ".xloose" {"letter-spacing" "var(--xloose)"})

(defcss ".xxloose" {"letter-spacing" "var(--xxloose)"})

(defcss ".xxxloose" {"letter-spacing" "var(--xxxloose)"})

(defcss ".instant" {"transition-duration" "var(--instant)"})

(defcss ".xxxfast" {"transition-duration" "var(--xxxfast)"})

(defcss ".xxfast" {"transition-duration" "var(--xxfast)"})

(defcss ".xfast" {"transition-duration" "var(--xfast)"})

(defcss ".fast" {"transition-duration" "var(--fast)"})

(defcss ".moderate" {"transition-duration" "var(--moderate)"})

(defcss ".slow" {"transition-duration" "var(--slow)"})

(defcss ".xslow" {"transition-duration" "var(--xslow)"})

(defcss ".xxslow" {"transition-duration" "var(--xxslow)"})

(defcss ".xxxslow" {"transition-duration" "var(--xxxslow)"})

(defcss
  ".rounded-absolute-xxsmall"
  {"border-radius" "var(--rounded-absolute-xxsmall)"})

(defcss
  ".rounded-absolute-xsmall"
  {"border-radius" "var(--rounded-absolute-xsmall)"})

(defcss
  ".rounded-absolute-small"
  {"border-radius" "var(--rounded-absolute-small)"})

(defcss
  ".rounded-absolute-medium"
  {"border-radius" "var(--rounded-absolute-medium)"})

(defcss
  ".rounded-absolute-large"
  {"border-radius" "var(--rounded-absolute-large)"})

(defcss
  ".rounded-absolute-xlarge"
  {"border-radius" "var(--rounded-absolute-xlarge)"})

(defcss
  ".rounded-absolute-xxlarge"
  {"border-radius" "var(--rounded-absolute-xxlarge)"})

(defcss
  ".rounded-absolute-xxxlarge"
  {"border-radius" "var(--rounded-absolute-xxxlarge)"})

(defcss
  ".rounded-xxxsmall"
  {"border-radius" "var(--rounded-xxxsmall)"})

(defcss ".rounded-xxsmall" {"border-radius" "var(--rounded-xxsmall)"})

(defcss ".rounded-xsmall" {"border-radius" "var(--rounded-xsmall)"})

(defcss ".rounded-small" {"border-radius" "var(--rounded-small)"})

(defcss ".rounded-medium" {"border-radius" "var(--rounded-medium)"})

(defcss ".rounded-large" {"border-radius" "var(--rounded-large)"})

(defcss ".rounded-xlarge" {"border-radius" "var(--rounded-xlarge)"})

(defcss ".rounded-xxlarge" {"border-radius" "var(--rounded-xxlarge)"})

(defcss
  ".rounded-xxxlarge"
  {"border-radius" "var(--rounded-xxxlarge)"})

(defcss ".xxxxsmall" {"font-size" "var(--xxxxsmall)"})

(defcss ".xxxsmall" {"font-size" "var(--xxxsmall)"})

(defcss ".xxsmall" {"font-size" "var(--xxsmall)"})

(defcss ".xsmall" {"font-size" "var(--xsmall)"})

(defcss ".small" {"font-size" "var(--small)"})

(defcss ".medium" {"font-size" "var(--medium)"})

(defcss ".large" {"font-size" "var(--large)"})

(defcss ".xlarge" {"font-size" "var(--xlarge)"})

(defcss ".xxlarge" {"font-size" "var(--xxlarge)"})

(defcss ".xxxlarge" {"font-size" "var(--xxxlarge)"})

(defcss ".xxxxlarge" {"font-size" "var(--xxxxlarge)"})

(defcss ".thin" {"font-weight" "var(--thin)"})

(defcss ".thin .kushi-icon" {"font-variation-settings" "'wght' 100"})

(defcss ".thin.kushi-icon" {"font-variation-settings" "'wght' 100"})

(defcss
  ".thin>.kushi-radio-input"
  {"outline-width" "var(--input-border-weight-thin)"})

(defcss
  ".thin>.kushi-checkbox-input"
  {"border-width" "var(--input-border-weight-thin)"})

(defcss ".extra-light" {"font-weight" "var(--extra-light)"})

(defcss
  ".extra-light .kushi-icon"
  {"font-variation-settings" "'wght' 200"})

(defcss
  ".extra-light.kushi-icon"
  {"font-variation-settings" "'wght' 200"})

(defcss
  ".extra-light>.kushi-radio-input"
  {"outline-width" "var(--input-border-weight-extra-light)"})

(defcss
  ".extra-light>.kushi-checkbox-input"
  {"border-width" "var(--input-border-weight-extra-light)"})

(defcss ".light" {"font-weight" "var(--light)"})

(defcss ".light .kushi-icon" {"font-variation-settings" "'wght' 300"})

(defcss ".light.kushi-icon" {"font-variation-settings" "'wght' 300"})

(defcss
  ".light>.kushi-radio-input"
  {"outline-width" "var(--input-border-weight-light)"})

(defcss
  ".light>.kushi-checkbox-input"
  {"border-width" "var(--input-border-weight-light)"})

(defcss ".normal" {"font-weight" "var(--normal)"})

(defcss ".normal .kushi-icon" {"font-variation-settings" "'wght' 400"})

(defcss ".normal.kushi-icon" {"font-variation-settings" "'wght' 400"})

(defcss
  ".normal>.kushi-radio-input"
  {"outline-width" "var(--input-border-weight-normal)"})

(defcss
  ".normal>.kushi-checkbox-input"
  {"border-width" "var(--input-border-weight-normal)"})

(defcss ".wee-bold" {"font-weight" "var(--wee-bold)"})

(defcss
  ".wee-bold .kushi-icon"
  {"font-variation-settings" "'wght' 500"})

(defcss
  ".wee-bold.kushi-icon"
  {"font-variation-settings" "'wght' 500"})

(defcss
  ".wee-bold>.kushi-radio-input"
  {"outline-width" "var(--input-border-weight-wee-bold)"})

(defcss
  ".wee-bold>.kushi-checkbox-input"
  {"border-width" "var(--input-border-weight-wee-bold)"})

(defcss ".semi-bold" {"font-weight" "var(--semi-bold)"})

(defcss
  ".semi-bold .kushi-icon"
  {"font-variation-settings" "'wght' 600"})

(defcss
  ".semi-bold.kushi-icon"
  {"font-variation-settings" "'wght' 600"})

(defcss
  ".semi-bold>.kushi-radio-input"
  {"outline-width" "var(--input-border-weight-semi-bold)"})

(defcss
  ".semi-bold>.kushi-checkbox-input"
  {"border-width" "var(--input-border-weight-semi-bold)"})

(defcss ".bold" {"font-weight" "var(--bold)"})

(defcss ".bold .kushi-icon" {"font-variation-settings" "'wght' 700"})

(defcss ".bold.kushi-icon" {"font-variation-settings" "'wght' 700"})

(defcss
  ".bold>.kushi-radio-input"
  {"outline-width" "var(--input-border-weight-bold)"})

(defcss
  ".bold>.kushi-checkbox-input"
  {"border-width" "var(--input-border-weight-bold)"})

(defcss ".extra-bold" {"font-weight" "var(--extra-bold)"})

(defcss
  ".extra-bold .kushi-icon"
  {"font-variation-settings" "'wght' 800"})

(defcss
  ".extra-bold.kushi-icon"
  {"font-variation-settings" "'wght' 800"})

(defcss
  ".extra-bold>.kushi-radio-input"
  {"outline-width" "var(--input-border-weight-extra-bold)"})

(defcss
  ".extra-bold>.kushi-checkbox-input"
  {"border-width" "var(--input-border-weight-extra-bold)"})

(defcss ".heavy" {"font-weight" "var(--heavy)"})

(defcss ".heavy .kushi-icon" {"font-variation-settings" "'wght' 900"})

(defcss ".heavy.kushi-icon" {"font-variation-settings" "'wght' 900"})

(defcss
  ".heavy>.kushi-radio-input"
  {"outline-width" "var(--input-border-weight-heavy)"})

(defcss
  ".heavy>.kushi-checkbox-input"
  {"border-width" "var(--input-border-weight-heavy)"})

(defcss
  ".absolute-centered"
  {"position" "absolute",
   "inset-inline-start" "50%",
   "inset-block-start" "50%",
   "translate" "-50% -50%"})

(defcss
  ".absolute-fill"
  {"position" "absolute",
   "top" "0",
   "right" "0",
   "bottom" "0",
   "left" "0"})

(defcss
  ".after-absolute-fill::after"
  {"content" "\"\"",
   "position" "absolute",
   "top" "0",
   "right" "0",
   "bottom" "0",
   "left" "0"})

(defcss
  ".before-absolute-fill::before"
  {"content" "\"\"",
   "position" "absolute",
   "top" "0",
   "right" "0",
   "bottom" "0",
   "left" "0"})

(defcss
  ".absolute-inline-start-inside"
  {"position" "absolute",
   "inset-inline-start" "0%",
   "inset-inline-end" "unset",
   "inset-block-start" "50%",
   "translate" "0px -50%"})

(defcss
  ".absolute-inline-end-inside"
  {"position" "absolute",
   "inset-inline-start" "unset",
   "inset-inline-end" "0%",
   "inset-block-start" "50%",
   "translate" "0px -50%"})

(defcss
  ".absolute-block-start-inside"
  {"position" "absolute",
   "inset-block-start" "0%",
   "inset-block-end" "unset",
   "inset-inline-start" "50%",
   "translate" "-50% 0px"})

(defcss
  ".absolute-block-end-inside"
  {"position" "absolute",
   "inset-block-start" "unset",
   "inset-block-end" "0%",
   "inset-inline-start" "50%",
   "translate" "-50% 0px"})

(defcss
  ".fixed-fill"
  {"position" "fixed",
   "top" "0",
   "right" "0",
   "bottom" "0",
   "left" "0"})

(defcss
  ".fixed-centered"
  {"position" "fixed",
   "inset-inline-start" "50%",
   "inset-block-start" "50%",
   "translate" "-50% -50%"})

(defcss
  ".fixed-inline-start-inside"
  {"position" "fixed",
   "inset-inline-start" "0%",
   "inset-inline-end" "unset",
   "inset-block-start" "50%",
   "translate" "0px -50%"})

(defcss
  ".fixed-inline-end-inside"
  {"position" "fixed",
   "inset-inline-end" "0%",
   "inset-inline-start" "unset",
   "inset-block-start" "50%",
   "translate" "0px -50%"})

(defcss
  ".fixed-block-start-inside"
  {"position" "fixed",
   "inset-block-start" "0%",
   "inset-block-end" "unset",
   "inset-inline-start" "50%",
   "translate" "-50%"})

(defcss
  ".fixed-block-end-inside"
  {"position" "fixed",
   "inset-block-end" "0%",
   "inset-block-start" "unset",
   "inset-inline-start" "50%",
   "translate" "-50%"})

(defcss
  ".bgi-cover"
  {"background-position" "center center",
   "background-repeat" "no-repeat",
   "width" "100%"})

(defcss
  ".bgi-contain"
  {"background-position" "center center",
   "background-size" "contain",
   "background-repeat" "no-repeat",
   "width" "100%",
   "height" "100%"})

(defcss
  ".transition"
  {"transition-property" "all",
   "transition-timing-function" "var(--transition-timing-function)",
   "transition-duration" "var(--transition-duration)"})

(defcss
  ".top-left-corner-outside"
  {"position" "absolute",
   "top" "0%",
   "bottom" "unset",
   "left" "0%",
   "right" "unset",
   "translate" "-100% -100%"})

(defcss
  ".top-left-corner"
  {"position" "absolute",
   "top" "0%",
   "bottom" "unset",
   "left" "0%",
   "right" "unset",
   "translate" "-50% -50%"})

(defcss
  ".top-left-corner-inside"
  {"position" "absolute",
   "top" "0%",
   "bottom" "unset",
   "left" "0%",
   "right" "unset",
   "translate" "0% 0%"})

(defcss
  ".top-left-corner-inside-fixed"
  {"position" "fixed",
   "top" "0%",
   "bottom" "unset",
   "left" "0%",
   "right" "unset",
   "translate" "0% 0%"})

(defcss
  ".top-left-outside"
  {"position" "absolute",
   "top" "0%",
   "bottom" "unset",
   "left" "0%",
   "right" "unset",
   "translate" "0% -100%"})

(defcss
  ".top-left"
  {"position" "absolute",
   "top" "0%",
   "bottom" "unset",
   "left" "0%",
   "right" "unset",
   "translate" "0% -50%"})

(defcss
  ".left-top-outside"
  {"position" "absolute",
   "top" "0%",
   "bottom" "unset",
   "left" "0%",
   "right" "unset",
   "translate" "-100% 0%"})

(defcss
  ".left-top"
  {"position" "absolute",
   "top" "0%",
   "bottom" "unset",
   "left" "0%",
   "right" "unset",
   "translate" "-50% 0%"})

(defcss
  ".top-right-outside"
  {"position" "absolute",
   "top" "0%",
   "bottom" "unset",
   "left" "unset",
   "right" "0%",
   "translate" "0% -100%"})

(defcss
  ".top-right"
  {"position" "absolute",
   "top" "0%",
   "bottom" "unset",
   "left" "unset",
   "right" "0%",
   "translate" "0% -50%"})

(defcss
  ".top-right-corner-outside"
  {"position" "absolute",
   "top" "0%",
   "bottom" "unset",
   "left" "unset",
   "right" "0%",
   "translate" "100% -100%"})

(defcss
  ".top-right-corner"
  {"position" "absolute",
   "top" "0%",
   "bottom" "unset",
   "left" "unset",
   "right" "0%",
   "translate" "50% -50%"})

(defcss
  ".top-right-corner-inside"
  {"position" "absolute",
   "top" "0%",
   "bottom" "unset",
   "left" "unset",
   "right" "0%",
   "translate" "0% 0%"})

(defcss
  ".top-right-corner-inside-fixed"
  {"position" "fixed",
   "top" "0%",
   "bottom" "unset",
   "left" "unset",
   "right" "0%",
   "translate" "0% 0%"})

(defcss
  ".right-top-outside"
  {"position" "absolute",
   "top" "0%",
   "bottom" "unset",
   "left" "unset",
   "right" "0%",
   "translate" "100% 0%"})

(defcss
  ".right-top"
  {"position" "absolute",
   "top" "0%",
   "bottom" "unset",
   "left" "unset",
   "right" "0%",
   "translate" "50% 0%"})

(defcss
  ".bottom-left-outside"
  {"position" "absolute",
   "top" "unset",
   "bottom" "0%",
   "left" "0%",
   "right" "unset",
   "translate" "0% 100%"})

(defcss
  ".bottom-left"
  {"position" "absolute",
   "top" "unset",
   "bottom" "0%",
   "left" "0%",
   "right" "unset",
   "translate" "0% 50%"})

(defcss
  ".bottom-left-corner-outside"
  {"position" "absolute",
   "top" "unset",
   "bottom" "0%",
   "left" "0%",
   "right" "unset",
   "translate" "-100% 100%"})

(defcss
  ".bottom-left-corner"
  {"position" "absolute",
   "top" "unset",
   "bottom" "0%",
   "left" "0%",
   "right" "unset",
   "translate" "-50% 50%"})

(defcss
  ".bottom-left-corner-inside"
  {"position" "absolute",
   "top" "unset",
   "bottom" "0%",
   "left" "0%",
   "right" "unset",
   "translate" "0% 0%"})

(defcss
  ".bottom-left-corner-inside-fixed"
  {"position" "fixed",
   "top" "unset",
   "bottom" "0%",
   "left" "0%",
   "right" "unset",
   "translate" "0% 0%"})

(defcss
  ".left-bottom-outside"
  {"position" "absolute",
   "top" "unset",
   "bottom" "0%",
   "left" "0%",
   "right" "unset",
   "translate" "-100% 0%"})

(defcss
  ".left-bottom"
  {"position" "absolute",
   "top" "unset",
   "bottom" "0%",
   "left" "0%",
   "right" "unset",
   "translate" "-50% 0%"})

(defcss
  ".right-bottom-outside"
  {"position" "absolute",
   "top" "unset",
   "bottom" "0%",
   "left" "unset",
   "right" "0%",
   "translate" "100% 0%"})

(defcss
  ".right-bottom"
  {"position" "absolute",
   "top" "unset",
   "bottom" "0%",
   "left" "unset",
   "right" "0%",
   "translate" "50% 0%"})

(defcss
  ".bottom-right-corner-outside"
  {"position" "absolute",
   "top" "unset",
   "bottom" "0%",
   "left" "unset",
   "right" "0%",
   "translate" "100% 100%"})

(defcss
  ".bottom-right-corner"
  {"position" "absolute",
   "top" "unset",
   "bottom" "0%",
   "left" "unset",
   "right" "0%",
   "translate" "50% 50%"})

(defcss
  ".bottom-right-corner-inside"
  {"position" "absolute",
   "top" "unset",
   "bottom" "0%",
   "left" "unset",
   "right" "0%",
   "translate" "0% 0%"})

(defcss
  ".bottom-right-corner-inside-fixed"
  {"position" "fixed",
   "top" "unset",
   "bottom" "0%",
   "left" "unset",
   "right" "0%",
   "translate" "0% 0%"})

(defcss
  ".bottom-right-outside"
  {"position" "absolute",
   "top" "unset",
   "bottom" "0%",
   "left" "unset",
   "right" "0%",
   "translate" "0% 100%"})

(defcss
  ".bottom-right"
  {"position" "absolute",
   "top" "unset",
   "bottom" "0%",
   "left" "unset",
   "right" "0%",
   "translate" "0% 50%"})

(defcss
  ".left-inside"
  {"position" "absolute",
   "top" "50%",
   "bottom" "unset",
   "right" "unset",
   "left" "0%",
   "translate" "0% -50%"})

(defcss
  ".left-inside-fixed"
  {"position" "fixed",
   "top" "50%",
   "bottom" "unset",
   "right" "unset",
   "left" "0%",
   "translate" "0% -50%"})

(defcss
  ".left"
  {"position" "absolute",
   "top" "50%",
   "bottom" "unset",
   "right" "unset",
   "left" "0%",
   "translate" "-50% -50%"})

(defcss
  ".left-outside"
  {"position" "absolute",
   "top" "50%",
   "bottom" "unset",
   "right" "unset",
   "left" "0%",
   "translate" "-100% -50%"})

(defcss
  ".right-inside"
  {"position" "absolute",
   "top" "50%",
   "bottom" "unset",
   "left" "unset",
   "right" "0%",
   "translate" "0% -50%"})

(defcss
  ".right-inside-fixed"
  {"position" "fixed",
   "top" "50%",
   "bottom" "unset",
   "left" "unset",
   "right" "0%",
   "translate" "0% -50%"})

(defcss
  ".right"
  {"position" "absolute",
   "top" "50%",
   "bottom" "unset",
   "left" "unset",
   "right" "0%",
   "translate" "50% -50%"})

(defcss
  ".right-outside"
  {"position" "absolute",
   "top" "50%",
   "bottom" "unset",
   "left" "unset",
   "right" "0%",
   "translate" "100% -50%"})

(defcss
  ".top-outside"
  {"position" "absolute",
   "left" "50%",
   "right" "unset",
   "bottom" "unset",
   "top" "0%",
   "translate" "-50% -100%"})

(defcss
  ".top"
  {"position" "absolute",
   "left" "50%",
   "right" "unset",
   "bottom" "unset",
   "top" "0%",
   "translate" "-50% -50%"})

(defcss
  ".top-inside"
  {"position" "absolute",
   "left" "50%",
   "right" "unset",
   "bottom" "unset",
   "top" "0%",
   "translate" "-50% 0%"})

(defcss
  ".top-inside-fixed"
  {"position" "fixed",
   "left" "50%",
   "right" "unset",
   "bottom" "unset",
   "top" "0%",
   "translate" "-50% 0%"})

(defcss
  ".bottom-inside"
  {"position" "absolute",
   "left" "50%",
   "right" "unset",
   "top" "unset",
   "bottom" "0%",
   "translate" "-50% 0%"})

(defcss
  ".bottom-inside-fixed"
  {"position" "fixed",
   "left" "50%",
   "right" "unset",
   "top" "unset",
   "bottom" "0%",
   "translate" "-50% 0%"})

(defcss
  ".bottom"
  {"position" "absolute",
   "left" "50%",
   "right" "unset",
   "top" "unset",
   "bottom" "0%",
   "translate" "-50% 50%"})

(defcss
  ".bottom-outside"
  {"position" "absolute",
   "left" "50%",
   "right" "unset",
   "top" "unset",
   "bottom" "0%",
   "translate" "-50% 100%"})


;; New theming

(defcss
  ":root"
  {
   "--debug-grid-size"                                  "107px",
   "--debug-grid-color"                                 "#eee",
   "--outlined-element-stroke-width"                    "1px"
   "--outlined-button-stroke-width"                     "var(--outlined-element-stroke-width)"
   "--outlined-tag-stroke-width"                        "var(--outlined-element-stroke-width)"
   "--outlined-callout-stroke-width"                    "var(--outlined-element-stroke-width)"
   "--button-padding-inline-compact"                    "0.8em",
   "--button-padding-inline"                            "1.2em",
   "--button-padding-inline-roomy"                      "1.5em",
   "--button-padding-block-compact"                     "0.42em",
   "--button-padding-block"                             "0.67em",
   "--button-padding-block-roomy"                       "0.8666em"
   "--icon-button-padding-inline"                        :$button-padding-block,
   "--icon-button-padding-block"                         :$button-padding-block,
   ;; Use or not use -ems ?
   "--tag-padding-block-start-reduction-ratio"          "0.9",
   "--tag-padding-block-start"                          "0.27em",
   "--tag-padding-block"                                "0.3em",
   "--tag-padding-inline"                               "0.6em",
   "--tag-padding-block-compact"                        "0.20em",
   "--tag-padding-inline-compact"                       "0.45em",
   "--tag-padding-block-roomy"                          "0.45em",
   "--tag-padding-inline-roomy"                         "0.9em"})

(defcss
  "[data-kushi-colorway=\"neutral\"]"
  {:color                           :$foreground-color-neutral
   :hover:color                     :$foreground-color-neutral-2
   :active:color                    :$foreground-color-neutral-3
   :dark:color                      :$foreground-color-neutral-dark-mode
   :dark:hover:color                :$foreground-color-neutral-2-dark-mode
   :dark:active:color               :$foreground-color-neutral-3-dark-mode
   :hover:bgc                       :$background-color-neutral-soft
   :active:bgc                      :$background-color-neutral-soft-2
   :dark:hover:bgc                  :$background-color-neutral-soft-dark-mode
   :dark:active:bgc                 :$background-color-neutral-soft-2-dark-mode
   "[data-kushi-surface= \"soft\"]"       {:bgc        :$background-color-neutral-soft
                                         :dark:bgc        :$background-color-neutral-soft-dark-mode
                                         :hover:bgc  :$background-color-neutral-soft-2
                                         :dark:hover:bgc  :$background-color-neutral-soft-2-dark-mode
                                         :active:bgc :$background-color-neutral-soft-3
                                         :dark:active:bgc :$background-color-neutral-soft-3-dark-mode}
   "[data-kushi-surface= \"solid\"]"      {:bgc          :$background-color-neutral-hard
                                         :hover:bgc    :$background-color-neutral-hard-2
                                         :active:bgc   :$background-color-neutral-hard-3}
   "dark:[data-kushi-surface= \"solid\"]" {:bgc        :$background-color-neutral-hard-dark-mode
                                         :hover:bgc  :$background-color-neutral-hard-2-dark-mode
                                         :active:bgc :$background-color-neutral-hard-3-dark-mode}})

(defcss
  "[data-kushi-colorway=\"accent\"]"
  {:color                           :$foreground-color-accent
   :hover:color                     :$foreground-color-accent-2
   :active:color                    :$foreground-color-accent-3
   :dark:color                      :$foreground-color-accent-dark-mode
   :dark:hover:color                :$foreground-color-accent-2-dark-mode
   :dark:active:color               :$foreground-color-accent-3-dark-mode
   :hover:bgc                       :$background-color-accent-soft
   :active:bgc                      :$background-color-accent-soft-2
   :dark:hover:bgc                  :$background-color-accent-soft-dark-mode
   :dark:active:bgc                 :$background-color-accent-soft-2-dark-mode
   "[data-kushi-surface= \"soft\"]"       {:bgc        :$background-color-accent-soft
                                         :dark:bgc        :$background-color-accent-soft-dark-mode
                                         :hover:bgc  :$background-color-accent-soft-2
                                         :dark:hover:bgc  :$background-color-accent-soft-2-dark-mode
                                         :active:bgc :$background-color-accent-soft-3
                                         :dark:active:bgc :$background-color-accent-soft-3-dark-mode}
   "[data-kushi-surface= \"solid\"]"      {:bgc          :$background-color-accent-hard
                                         :hover:bgc    :$background-color-accent-hard-2
                                         :active:bgc   :$background-color-accent-hard-3}
   "dark:[data-kushi-surface= \"solid\"]" {:bgc        :$background-color-accent-hard-dark-mode
                                         :hover:bgc  :$background-color-accent-hard-2-dark-mode
                                         :active:bgc :$background-color-accent-hard-3-dark-mode}})

(defcss
  "[data-kushi-colorway=\"positive\"]"
  {:color                           :$foreground-color-positive
   :hover:color                     :$foreground-color-positive-2
   :active:color                    :$foreground-color-positive-3
   :dark:color                      :$foreground-color-positive-dark-mode
   :dark:hover:color                :$foreground-color-positive-2-dark-mode
   :dark:active:color               :$foreground-color-positive-3-dark-mode
   :hover:bgc                       :$background-color-positive-soft
   :active:bgc                      :$background-color-positive-soft-2
   :dark:hover:bgc                  :$background-color-positive-soft-dark-mode
   :dark:active:bgc                 :$background-color-positive-soft-2-dark-mode
   "[data-kushi-surface= \"soft\"]"       {:bgc             :$background-color-positive-soft
                                         :dark:bgc        :$background-color-positive-soft-dark-mode
                                         :hover:bgc       :$background-color-positive-soft-2
                                         :dark:hover:bgc  :$background-color-positive-soft-2-dark-mode
                                         :active:bgc      :$background-color-positive-soft-3
                                         :dark:active:bgc :$background-color-positive-soft-3-dark-mode}
   "[data-kushi-surface= \"solid\"]"      {:bgc        :$background-color-positive-hard
                                         :hover:bgc  :$background-color-positive-hard-2
                                         :active:bgc :$background-color-positive-hard-3}
   "dark:[data-kushi-surface= \"solid\"]" {:bgc        :$background-color-positive-hard-dark-mode
                                         :hover:bgc  :$background-color-positive-hard-2-dark-mode
                                         :active:bgc :$background-color-positive-hard-3-dark-mode}})

(defcss
  "[data-kushi-colorway=\"negative\"]"
  {:color                           :$foreground-color-negative
   :hover:color                     :$foreground-color-negative-2
   :active:color                    :$foreground-color-negative-3
   :dark:color                      :$foreground-color-negative-dark-mode
   :dark:hover:color                :$foreground-color-negative-2-dark-mode
   :dark:active:color               :$foreground-color-negative-3-dark-mode
   :hover:bgc                       :$background-color-negative-soft
   :active:bgc                      :$background-color-negative-soft-2
   :dark:hover:bgc                  :$background-color-negative-soft-dark-mode
   :dark:active:bgc                 :$background-color-negative-soft-2-dark-mode
   "[data-kushi-surface= \"soft\"]"       {:bgc        :$background-color-negative-soft
                                         :dark:bgc        :$background-color-negative-soft-dark-mode
                                         :hover:bgc  :$background-color-negative-soft-2
                                         :dark:hover:bgc  :$background-color-negative-soft-2-dark-mode
                                         :active:bgc :$background-color-negative-soft-3
                                         :dark:active:bgc :$background-color-negative-soft-3-dark-mode}
   "[data-kushi-surface= \"solid\"]"      {:bgc          :$background-color-negative-hard
                                         :hover:bgc    :$background-color-negative-hard-2
                                         :active:bgc   :$background-color-negative-hard-3}
   "dark:[data-kushi-surface= \"solid\"]" {:bgc        :$background-color-negative-hard-dark-mode
                                         :hover:bgc  :$background-color-negative-hard-2-dark-mode
                                         :active:bgc :$background-color-negative-hard-3-dark-mode}})

(defcss
  "[data-kushi-colorway=\"warning\"]"
  {:color                           :$foreground-color-warning
   :hover:color                     :$foreground-color-warning-2
   :active:color                    :$foreground-color-warning-3
   :dark:color                      :$foreground-color-warning-dark-mode
   :dark:hover:color                :$foreground-color-warning-2-dark-mode
   :dark:active:color               :$foreground-color-warning-3-dark-mode
   :hover:bgc                       :$background-color-warning-soft
   :active:bgc                      :$background-color-warning-soft-2
   :dark:hover:bgc                  :$background-color-warning-soft-dark-mode
   :dark:active:bgc                 :$background-color-warning-soft-2-dark-mode
   "[data-kushi-surface= \"soft\"]"       {:bgc             :$background-color-warning-soft
                                         :dark:bgc        :$background-color-warning-soft-dark-mode
                                         :hover:bgc       :$background-color-warning-soft-2
                                         :dark:hover:bgc  :$background-color-warning-soft-2-dark-mode
                                         :active:bgc      :$background-color-warning-soft-3
                                         :dark:active:bgc :$background-color-warning-soft-3-dark-mode}
   "[data-kushi-surface= \"solid\"]"      {:bgc        :$background-color-warning-hard
                                         :hover:bgc  :$background-color-warning-hard-2
                                         :active:bgc :$background-color-warning-hard-3}
   "dark:[data-kushi-surface= \"solid\"]" {:bgc        :$background-color-warning-hard-dark-mode
                                         :hover:bgc  :$background-color-warning-hard-2-dark-mode
                                         :active:bgc :$background-color-warning-hard-3-dark-mode}})

(defcss "[data-kushi-surface=\"solid\"]"
  {:color             :white
   :dark:color        :black
   :hover:color       :white
   :dark:hover:color  :black
   :active:color      :white
   :dark:active:color :black})

(defcss "[data-kushi-surface=\"outline\"]"
  {:--_stroke-width    :$outlined-element-stroke-width})

(defcss
  "[data-kushi-surface]"
  {"--_stroke"
   "inset 0  0  0  var(--_stroke-width, 0px)  var(--stroke-color, currentColor)"

   "[data-kushi-stroke-align=\"outside\"]"
    {"--_stroke" "0  0  0  var(--_stroke-width, 0px)  var(--stroke-color, currentColor)"}
   
   "box-shadow"
   "var(--_stroke)"})
                                     

(defcss "[data-kushi-shape=\"rounded\"]"
  {:border-radius :0.3em})


(defcss "[data-kushi-shape=\"pill\"]"
  {:border-radius :9999px})


;; Move to button ns?
(defcss ".kushi-button"
  {:--_padding-inline               "var(--button-padding-inline)"
   :--_padding-block                "var(--button-padding-block)"
   "[data-kushi-packing=\"compact\"]" {:--_padding-inline "var(--button-padding-inline-compact)"
                                     :--_padding-block  "var(--button-padding-block-compact)"}
   
   "[data-kushi-packing=\"roomy\"]"   {:--_padding-inline "var(--button-padding-inline-roomy)"
                                     :--_padding-block  "var(--button-padding-block-roomy)"}
   
   "[data-kushi-icon-button]"         {:--_padding-inline           "var(--_padding-block)"
                                     :_.material-symbols-outlined {:min-width   :1.2ch
                                                                   :line-height :normal}}})
(defcss ".kushi-icon-button"
  {:--_padding-block            :$icon-button-padding-block||$button-padding-block
   :--_padding-inline           :$icon-button-padding-inline||$button-padding-inline
   :_.material-symbols-outlined {:min-width   :1.2ch
                                 :line-height :normal}
   "[data-kushi-packing=\"compact\"]" {:--_padding-inline :$icon-button-padding-block-compact||$button-padding-block-compact
                                     :--_padding-block  :$icon-button-padding-block-compact||$button-padding-block-compact}
   "[data-kushi-packing=\"roomy\"]"   {:--_padding-inline :$icon-button-padding-block-roomy||$button-padding-block-roomy
                                     :--_padding-block  :$icon-button-padding-block-roomy||$button-padding-block-roomy}})


;; Keep here?
(defcss ".kushi-button, .kushi-tag"
  {"[data-kushi-start-enhancer]"      {:padding-inline-start "calc(var(--_padding-inline) * 0.7666)"}
   "[data-kushi-end-enhancer]"        {:padding-inline-end "calc(var(--_padding-inline) * 0.7666)"}})



;; Move to tag ns
(defcss ".kushi-tag"
  {"[data-kushi-packing=\"compact\"]"
   {:--_padding-inline      "var(--tag-padding-inline-compact)"
    :--_padding-block-start "calc(var(--tag-padding-block-compact) * var(--tag-padding-block-start-reduction-ratio, 1))"
    :--_padding-block-end   "calc(var(--tag-padding-block-compact))"
    }

   "[data-kushi-packing=\"roomy\"]"
   {:--_padding-inline "var(--tag-padding-inline-roomy)"
    :--_padding-block-start "calc(var(--tag-padding-block-roomy) * var(--tag-padding-block-start-reduction-ratio, 1))"
    :--_padding-block-end   "calc(var(--tag-padding-block-roomy))"
    }})
