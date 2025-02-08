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
  {"background-color" "var(--background-color-inverse)",
   "color" "var(--foreground-color-inverse)"})

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
  {"background-color" "var(--code-background-color-inverse)",
   "color" "var(--code-color-inverse)"})

(defcss
  ".styled-scrollbars"
  {"scrollbar-color" "var(--scrollbar-thumb-color)",
   "scrollbar-width" "thin"})

(defcss
  ".dark .styled-scrollbars"
  {"scrollbar-color" "var(--scrollbar-thumb-color-inverse)"})

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
  {"background" "var(--scrollbar-thumb-color-inverse)",
   "border" "0px solid var(--scrollbar-background-color-inverse)"})

(defcss
  ".styled-scrollbars::-webkit-scrollbar-track"
  {"background" "var(--scrollbar-background-color)"})

(defcss
  ".dark .styled-scrollbars::-webkit-scrollbar-track"
  {"background" "var(--scrollbar-background-color-inverse)"})

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

(defcss
  ".neutral-secondary-foreground"
  {"color" "var(--neutral-secondary-foreground)"})

(defcss
  ".dark .neutral-secondary-foreground"
  {"color" "var(--neutral-secondary-foreground-inverse)"})

(defcss ".neutral-foreground" {"color" "var(--neutral-foreground)"})

(defcss
  ".dark .neutral-foreground"
  {"color" "var(--neutral-foreground-inverse)"})

(defcss ".accent-foreground" {"color" "var(--accent-foreground)"})

(defcss
  ".dark .accent-foreground"
  {"color" "var(--accent-foreground-inverse)"})

(defcss ".positive-foreground" {"color" "var(--positive-foreground)"})

(defcss
  ".dark .positive-foreground"
  {"color" "var(--positive-foreground-inverse)"})

(defcss ".negative-foreground" {"color" "var(--negative-foreground)"})

(defcss
  ".dark .negative-foreground"
  {"color" "var(--negative-foreground-inverse)"})

(defcss ".warning-foreground" {"color" "var(--warning-foreground)"})

(defcss
  ".dark .warning-foreground"
  {"color" "var(--warning-foreground-inverse)"})

(defcss
  ".neutral-bg"
  {"background-color" "var(--neutral-background-color)"})

(defcss
  ".accent-bg"
  {"background-color" "var(--accent-background-color)"})

(defcss
  ".positive-bg"
  {"background-color" "var(--positive-background-color)"})

(defcss
  ".negative-bg"
  {"background-color" "var(--negative-background-color)"})

(defcss
  ".warning-bg"
  {"background-color" "var(--warning-background-color)"})

(defcss
  ".neutral"
  {"color" "var(--neutral-color)",
   "background-color" "var(--neutral-background-color)"})

(defcss
  ".neutral:hover"
  {"color" "var(--neutral-color-hover)",
   "background-color" "var(--neutral-background-color-hover)"})

(defcss
  ".neutral:active"
  {"color" "var(--neutral-color-active)",
   "background-color" "var(--neutral-background-color-active)"})

(defcss
  ".neutral.info"
  {"color" "var(--neutral-info-color)",
   "background-color" "var(--neutral-info-background-color)"})

(defcss
  ".neutral.info:hover"
  {"color" "var(--neutral-info-color-hover)",
   "background-color" "var(--neutral-info-background-color-hover)"})

(defcss
  ".neutral.info:active"
  {"color" "var(--neutral-info-color-active)",
   "background-color" "var(--neutral-info-background-color-active)"})

(defcss
  ".neutral.minimal"
  {"color" "var(--neutral-minimal-color)",
   "background-color" "var(--neutral-minimal-background-color)"})

(defcss
  ".neutral.minimal:hover"
  {"color" "var(--neutral-minimal-color-hover)",
   "background-color" "var(--neutral-minimal-background-color-hover)"})

(defcss
  ".neutral.minimal:active"
  {"color" "var(--neutral-minimal-color-active)",
   "background-color" "var(--neutral-minimal-background-color-active)"})

(defcss
  ".neutral.bordered:active"
  {"background-color" "var(--neutral-bordered-background-color-active)",
   "border-color" "var(--neutral-bordered-border-color-active)",
   "color" "var(--neutral-bordered-color-active)"})

(defcss
  ".neutral.bordered"
  {"border-color" "var(--neutral-bordered-border-color)",
   "background-color" "var(--neutral-bordered-background-color)",
   "color" "var(--neutral-bordered-color)"})

(defcss
  ".neutral.bordered:hover"
  {"color" "var(--neutral-bordered-color-hover)",
   "border-color" "var(--neutral-bordered-border-color-hover)",
   "background-color" "var(--neutral-bordered-background-color-hover)"})

(defcss
  ".neutral.bordered.info"
  {"background-color" "var(--neutral-bordered-info-background-color)",
   "border-color" "var(--neutral-bordered-info-border-color)"})

(defcss
  ".neutral.bordered.info:hover"
  {"background-color"
   "var(--neutral-bordered-info-background-color-hover)",
   "border-color" "var(--neutral-bordered-info-border-color-hover)"})

(defcss
  ".neutral.bordered.info:active"
  {"background-color"
   "var(--neutral-bordered-info-background-color-active)",
   "border-color" "var(--neutral-bordered-info-border-color-active)"})

(defcss
  ".neutral.filled"
  {"color" "var(--neutral-filled-color)",
   "background-color" "var(--neutral-filled-background-color)"})

(defcss
  ".neutral.filled:hover"
  {"color" "var(--neutral-filled-color-hover)",
   "background-color" "var(--neutral-filled-background-color-hover)"})

(defcss
  ".neutral.filled:active"
  {"color" "var(--neutral-filled-color-active)",
   "background-color" "var(--neutral-filled-background-color-active)"})

(defcss
  ".neutral.filled.info"
  {"background-color" "var(--neutral-filled-info-background-color)"})

(defcss
  ".neutral.filled.info:hover"
  {"background-color"
   "var(--neutral-filled-info-background-color-hover)"})

(defcss
  ".neutral-filled.info:active"
  {"background-color" "var(--info-background-color-active)"})

(defcss
  ".dark .neutral"
  {"color" "var(--neutral-color-inverse)",
   "background-color" "var(--neutral-background-color-inverse)"})

(defcss
  ".dark .neutral:hover"
  {"color" "var(--neutral-color-hover-inverse)",
   "background-color" "var(--neutral-background-color-hover-inverse)"})

(defcss
  ".dark .neutral:active"
  {"color" "var(--neutral-color-active-inverse)",
   "background-color" "var(--neutral-background-color-active-inverse)"})

(defcss
  ".dark .neutral.info"
  {"color" "var(--neutral-info-color-inverse)",
   "background-color" "var(--neutral-info-background-color-inverse)"})

(defcss
  ".dark .neutral.info:hover"
  {"color" "var(--neutral-info-color-hover-inverse)",
   "background-color"
   "var(--neutral-info-background-color-hover-inverse)"})

(defcss
  ".dark .neutral.info:active"
  {"color" "var(--neutral-info-color-active-inverse)",
   "background-color"
   "var(--neutral-info-background-color-active-inverse)"})

(defcss
  ".dark .neutral.minimal"
  {"color" "var(--neutral-minimal-color-inverse)",
   "background-color"
   "var(--neutral-minimal-background-color-inverse)"})

(defcss
  ".dark .neutral.minimal:hover"
  {"color" "var(--neutral-minimal-color-hover-inverse)",
   "background-color"
   "var(--neutral-minimal-background-color-hover-inverse)"})

(defcss
  ".dark .neutral.minimal:active"
  {"color" "var(--neutral-minimal-color-active-inverse)",
   "background-color"
   "var(--neutral-minimal-background-color-active-inverse)"})

(defcss
  ".dark .neutral.bordered:active"
  {"background-color"
   "var(--neutral-bordered-background-color-active-inverse)",
   "border-color" "var(--neutral-bordered-border-color-active-inverse)",
   "color" "var(--neutral-bordered-color-active-inverse)"})

(defcss
  ".dark .neutral.bordered"
  {"border-color" "var(--neutral-bordered-border-color-inverse)",
   "background-color"
   "var(--neutral-bordered-background-color-inverse)",
   "color" "var(--neutral-bordered-color-inverse)"})

(defcss
  ".dark .neutral.bordered:hover"
  {"color" "var(--neutral-bordered-color-hover-inverse)",
   "border-color" "var(--neutral-bordered-border-color-hover-inverse)",
   "background-color"
   "var(--neutral-bordered-background-color-hover-inverse)"})

(defcss
  ".dark .neutral.bordered.info:active"
  {"background-color"
   "var(--neutral-bordered-info-background-color-active-inverse)",
   "border-color"
   "var(--neutral-bordered-info-border-color-active-inverse)",
   "color" "var(--neutral-bordered-info-color-active-inverse)"})

(defcss
  ".dark .neutral.bordered.info"
  {"border-color" "var(--neutral-bordered-info-border-color-inverse)",
   "background-color"
   "var(--neutral-bordered-info-background-color-inverse)",
   "color" "var(--neutral-bordered-info-color-inverse)"})

(defcss
  ".dark .neutral.bordered.info:hover"
  {"color" "var(--neutral-bordered-info-color-hover-inverse)",
   "border-color"
   "var(--neutral-bordered-info-border-color-hover-inverse)",
   "background-color"
   "var(--neutral-bordered-info-background-color-hover-inverse)"})

(defcss
  ".dark .neutral.filled"
  {"color" "var(--neutral-filled-color-inverse)",
   "background-color" "var(--neutral-filled-background-color-inverse)"})

(defcss
  ".dark .neutral.filled:hover"
  {"color" "var(--neutral-filled-color-hover-inverse)",
   "background-color"
   "var(--neutral-filled-background-color-hover-inverse)"})

(defcss
  ".dark .neutral.filled:active"
  {"color" "var(--neutral-filled-color-active-inverse)",
   "background-color"
   "var(--neutral-filled-background-color-active-inverse)"})

(defcss
  ".dark .neutral.filled.info"
  {"background-color"
   "var(--neutral-filled-info-background-color-inverse)"})

(defcss
  ".dark .neutral.filled.info:hover"
  {"background-color"
   "var(--neutral-filled-info-background-color-hover-inverse)"})

(defcss
  ".dark .neutral-filled.info:active"
  {"background-color" "var(--info-background-color-active-inverse)"})

(defcss
  ".accent"
  {"color" "var(--accent-color)",
   "background-color" "var(--accent-background-color)"})

(defcss
  ".accent:hover"
  {"color" "var(--accent-color-hover)",
   "background-color" "var(--accent-background-color-hover)"})

(defcss
  ".accent:active"
  {"color" "var(--accent-color-active)",
   "background-color" "var(--accent-background-color-active)"})

(defcss
  ".accent.info"
  {"color" "var(--accent-info-color)",
   "background-color" "var(--accent-info-background-color)"})

(defcss
  ".accent.info:hover"
  {"color" "var(--accent-info-color-hover)",
   "background-color" "var(--accent-info-background-color-hover)"})

(defcss
  ".accent.info:active"
  {"color" "var(--accent-info-color-active)",
   "background-color" "var(--accent-info-background-color-active)"})

(defcss
  ".accent.minimal"
  {"color" "var(--accent-minimal-color)",
   "background-color" "var(--accent-minimal-background-color)"})

(defcss
  ".accent.minimal:hover"
  {"color" "var(--accent-minimal-color-hover)",
   "background-color" "var(--accent-minimal-background-color-hover)"})

(defcss
  ".accent.minimal:active"
  {"color" "var(--accent-minimal-color-active)",
   "background-color" "var(--accent-minimal-background-color-active)"})

(defcss
  ".accent.bordered:active"
  {"background-color" "var(--accent-bordered-background-color-active)",
   "border-color" "var(--accent-bordered-border-color-active)",
   "color" "var(--accent-bordered-color-active)"})

(defcss
  ".accent.bordered"
  {"border-color" "var(--accent-bordered-border-color)",
   "background-color" "var(--accent-bordered-background-color)",
   "color" "var(--accent-bordered-color)"})

(defcss
  ".accent.bordered:hover"
  {"color" "var(--accent-bordered-color-hover)",
   "border-color" "var(--accent-bordered-border-color-hover)",
   "background-color" "var(--accent-bordered-background-color-hover)"})

(defcss
  ".accent.bordered.info:active"
  {"background-color"
   "var(--accent-bordered-info-background-color-active)",
   "border-color" "var(--accent-bordered-info-border-color-active)",
   "color" "var(--accent-bordered-info-color-active)"})

(defcss
  ".accent.bordered.info"
  {"border-color" "var(--accent-bordered-info-border-color)",
   "background-color" "var(--accent-bordered-info-background-color)",
   "color" "var(--accent-bordered-info-color)"})

(defcss
  ".accent.bordered.info:hover"
  {"color" "var(--accent-bordered-info-color-hover)",
   "border-color" "var(--accent-bordered-info-border-color-hover)",
   "background-color"
   "var(--accent-bordered-info-background-color-hover)"})

(defcss
  ".accent.filled"
  {"color" "var(--accent-filled-color)",
   "background-color" "var(--accent-filled-background-color)"})

(defcss
  ".accent.filled:hover"
  {"color" "var(--accent-filled-color-hover)",
   "background-color" "var(--accent-filled-background-color-hover)"})

(defcss
  ".accent.filled:active"
  {"color" "var(--accent-filled-color-active)",
   "background-color" "var(--accent-filled-background-color-active)"})

(defcss
  ".accent.filled.info"
  {"background-color" "var(--accent-filled-info-background-color)"})

(defcss
  ".accent.filled.info:hover"
  {"background-color"
   "var(--accent-filled-info-background-color-hover)"})

(defcss
  ".accent-filled.info:active"
  {"background-color" "var(--info-background-color-active)"})

(defcss
  ".dark .accent"
  {"color" "var(--accent-color-inverse)",
   "background-color" "var(--accent-background-color-inverse)"})

(defcss
  ".dark .accent:hover"
  {"color" "var(--accent-color-hover-inverse)",
   "background-color" "var(--accent-background-color-hover-inverse)"})

(defcss
  ".dark .accent:active"
  {"color" "var(--accent-color-active-inverse)",
   "background-color" "var(--accent-background-color-active-inverse)"})

(defcss
  ".dark .accent.info"
  {"color" "var(--accent-info-color-inverse)",
   "background-color" "var(--accent-info-background-color-inverse)"})

(defcss
  ".dark .accent.info:hover"
  {"color" "var(--accent-info-color-hover-inverse)",
   "background-color"
   "var(--accent-info-background-color-hover-inverse)"})

(defcss
  ".dark .accent.info:active"
  {"color" "var(--accent-info-color-active-inverse)",
   "background-color"
   "var(--accent-info-background-color-active-inverse)"})

(defcss
  ".dark .accent.minimal"
  {"color" "var(--accent-minimal-color-inverse)",
   "background-color" "var(--accent-minimal-background-color-inverse)"})

(defcss
  ".dark .accent.minimal:hover"
  {"color" "var(--accent-minimal-color-hover-inverse)",
   "background-color"
   "var(--accent-minimal-background-color-hover-inverse)"})

(defcss
  ".dark .accent.minimal:active"
  {"color" "var(--accent-minimal-color-active-inverse)",
   "background-color"
   "var(--accent-minimal-background-color-active-inverse)"})

(defcss
  ".dark .accent.bordered:active"
  {"background-color"
   "var(--accent-bordered-background-color-active-inverse)",
   "border-color" "var(--accent-bordered-border-color-active-inverse)",
   "color" "var(--accent-bordered-color-active-inverse)"})

(defcss
  ".dark .accent.bordered"
  {"border-color" "var(--accent-bordered-border-color-inverse)",
   "background-color" "var(--accent-bordered-background-color-inverse)",
   "color" "var(--accent-bordered-color-inverse)"})

(defcss
  ".dark .accent.bordered:hover"
  {"color" "var(--accent-bordered-color-hover-inverse)",
   "border-color" "var(--accent-bordered-border-color-hover-inverse)",
   "background-color"
   "var(--accent-bordered-background-color-hover-inverse)"})

(defcss
  ".dark .accent.bordered.info:active"
  {"background-color"
   "var(--accent-bordered-info-background-color-active-inverse)",
   "border-color"
   "var(--accent-bordered-info-border-color-active-inverse)",
   "color" "var(--accent-bordered-info-color-active-inverse)"})

(defcss
  ".dark .accent.bordered.info"
  {"border-color" "var(--accent-bordered-info-border-color-inverse)",
   "background-color"
   "var(--accent-bordered-info-background-color-inverse)",
   "color" "var(--accent-bordered-info-color-inverse)"})

(defcss
  ".dark .accent.bordered.info:hover"
  {"color" "var(--accent-bordered-info-color-hover-inverse)",
   "border-color"
   "var(--accent-bordered-info-border-color-hover-inverse)",
   "background-color"
   "var(--accent-bordered-info-background-color-hover-inverse)"})

(defcss
  ".dark .accent.filled"
  {"color" "var(--accent-filled-color-inverse)",
   "background-color" "var(--accent-filled-background-color-inverse)"})

(defcss
  ".dark .accent.filled:hover"
  {"color" "var(--accent-filled-color-hover-inverse)",
   "background-color"
   "var(--accent-filled-background-color-hover-inverse)"})

(defcss
  ".dark .accent.filled:active"
  {"color" "var(--accent-filled-color-active-inverse)",
   "background-color"
   "var(--accent-filled-background-color-active-inverse)"})

(defcss
  ".dark .accent.filled.info"
  {"background-color"
   "var(--accent-filled-info-background-color-inverse)"})

(defcss
  ".dark .accent.filled.info:hover"
  {"background-color"
   "var(--accent-filled-info-background-color-hover-inverse)"})

(defcss
  ".dark .accent-filled.info:active"
  {"background-color" "var(--info-background-color-active-inverse)"})

(defcss
  ".positive"
  {"color" "var(--positive-color)",
   "background-color" "var(--positive-background-color)"})

(defcss
  ".positive:hover"
  {"color" "var(--positive-color-hover)",
   "background-color" "var(--positive-background-color-hover)"})

(defcss
  ".positive:active"
  {"color" "var(--positive-color-active)",
   "background-color" "var(--positive-background-color-active)"})

(defcss
  ".positive.info"
  {"color" "var(--positive-info-color)",
   "background-color" "var(--positive-info-background-color)"})

(defcss
  ".positive.info:hover"
  {"color" "var(--positive-info-color-hover)",
   "background-color" "var(--positive-info-background-color-hover)"})

(defcss
  ".positive.info:active"
  {"color" "var(--positive-info-color-active)",
   "background-color" "var(--positive-info-background-color-active)"})

(defcss
  ".positive.minimal"
  {"color" "var(--positive-minimal-color)",
   "background-color" "var(--positive-minimal-background-color)"})

(defcss
  ".positive.minimal:hover"
  {"color" "var(--positive-minimal-color-hover)",
   "background-color" "var(--positive-minimal-background-color-hover)"})

(defcss
  ".positive.minimal:active"
  {"color" "var(--positive-minimal-color-active)",
   "background-color"
   "var(--positive-minimal-background-color-active)"})

(defcss
  ".positive.bordered:active"
  {"background-color"
   "var(--positive-bordered-background-color-active)",
   "border-color" "var(--positive-bordered-border-color-active)",
   "color" "var(--positive-bordered-color-active)"})

(defcss
  ".positive.bordered"
  {"border-color" "var(--positive-bordered-border-color)",
   "background-color" "var(--positive-bordered-background-color)",
   "color" "var(--positive-bordered-color)"})

(defcss
  ".positive.bordered:hover"
  {"color" "var(--positive-bordered-color-hover)",
   "border-color" "var(--positive-bordered-border-color-hover)",
   "background-color"
   "var(--positive-bordered-background-color-hover)"})

(defcss
  ".positive.bordered.info:active"
  {"background-color"
   "var(--positive-bordered-info-background-color-active)",
   "border-color" "var(--positive-bordered-info-border-color-active)",
   "color" "var(--positive-bordered-info-color-active)"})

(defcss
  ".positive.bordered.info"
  {"border-color" "var(--positive-bordered-info-border-color)",
   "background-color" "var(--positive-bordered-info-background-color)",
   "color" "var(--positive-bordered-info-color)"})

(defcss
  ".positive.bordered.info:hover"
  {"color" "var(--positive-bordered-info-color-hover)",
   "border-color" "var(--positive-bordered-info-border-color-hover)",
   "background-color"
   "var(--positive-bordered-info-background-color-hover)"})

(defcss
  ".positive.filled"
  {"color" "var(--positive-filled-color)",
   "background-color" "var(--positive-filled-background-color)"})

(defcss
  ".positive.filled:hover"
  {"color" "var(--positive-filled-color-hover)",
   "background-color" "var(--positive-filled-background-color-hover)"})

(defcss
  ".positive.filled:active"
  {"color" "var(--positive-filled-color-active)",
   "background-color" "var(--positive-filled-background-color-active)"})

(defcss
  ".positive.filled.info"
  {"background-color" "var(--positive-filled-info-background-color)"})

(defcss
  ".positive.filled.info:hover"
  {"background-color"
   "var(--positive-filled-info-background-color-hover)"})

(defcss
  ".positive-filled.info:active"
  {"background-color" "var(--info-background-color-active)"})

(defcss
  ".dark .positive"
  {"color" "var(--positive-color-inverse)",
   "background-color" "var(--positive-background-color-inverse)"})

(defcss
  ".dark .positive:hover"
  {"color" "var(--positive-color-hover-inverse)",
   "background-color" "var(--positive-background-color-hover-inverse)"})

(defcss
  ".dark .positive:active"
  {"color" "var(--positive-color-active-inverse)",
   "background-color"
   "var(--positive-background-color-active-inverse)"})

(defcss
  ".dark .positive.info"
  {"color" "var(--positive-info-color-inverse)",
   "background-color" "var(--positive-info-background-color-inverse)"})

(defcss
  ".dark .positive.info:hover"
  {"color" "var(--positive-info-color-hover-inverse)",
   "background-color"
   "var(--positive-info-background-color-hover-inverse)"})

(defcss
  ".dark .positive.info:active"
  {"color" "var(--positive-info-color-active-inverse)",
   "background-color"
   "var(--positive-info-background-color-active-inverse)"})

(defcss
  ".dark .positive.minimal"
  {"color" "var(--positive-minimal-color-inverse)",
   "background-color"
   "var(--positive-minimal-background-color-inverse)"})

(defcss
  ".dark .positive.minimal:hover"
  {"color" "var(--positive-minimal-color-hover-inverse)",
   "background-color"
   "var(--positive-minimal-background-color-hover-inverse)"})

(defcss
  ".dark .positive.minimal:active"
  {"color" "var(--positive-minimal-color-active-inverse)",
   "background-color"
   "var(--positive-minimal-background-color-active-inverse)"})

(defcss
  ".dark .positive.bordered:active"
  {"background-color"
   "var(--positive-bordered-background-color-active-inverse)",
   "border-color"
   "var(--positive-bordered-border-color-active-inverse)",
   "color" "var(--positive-bordered-color-active-inverse)"})

(defcss
  ".dark .positive.bordered"
  {"border-color" "var(--positive-bordered-border-color-inverse)",
   "background-color"
   "var(--positive-bordered-background-color-inverse)",
   "color" "var(--positive-bordered-color-inverse)"})

(defcss
  ".dark .positive.bordered:hover"
  {"color" "var(--positive-bordered-color-hover-inverse)",
   "border-color" "var(--positive-bordered-border-color-hover-inverse)",
   "background-color"
   "var(--positive-bordered-background-color-hover-inverse)"})

(defcss
  ".dark .positive.bordered.info:active"
  {"background-color"
   "var(--positive-bordered-info-background-color-active-inverse)",
   "border-color"
   "var(--positive-bordered-info-border-color-active-inverse)",
   "color" "var(--positive-bordered-info-color-active-inverse)"})

(defcss
  ".dark .positive.bordered.info"
  {"border-color" "var(--positive-bordered-info-border-color-inverse)",
   "background-color"
   "var(--positive-bordered-info-background-color-inverse)",
   "color" "var(--positive-bordered-info-color-inverse)"})

(defcss
  ".dark .positive.bordered.info:hover"
  {"color" "var(--positive-bordered-info-color-hover-inverse)",
   "border-color"
   "var(--positive-bordered-info-border-color-hover-inverse)",
   "background-color"
   "var(--positive-bordered-info-background-color-hover-inverse)"})

(defcss
  ".dark .positive.filled"
  {"color" "var(--positive-filled-color-inverse)",
   "background-color"
   "var(--positive-filled-background-color-inverse)"})

(defcss
  ".dark .positive.filled:hover"
  {"color" "var(--positive-filled-color-hover-inverse)",
   "background-color"
   "var(--positive-filled-background-color-hover-inverse)"})

(defcss
  ".dark .positive.filled:active"
  {"color" "var(--positive-filled-color-active-inverse)",
   "background-color"
   "var(--positive-filled-background-color-active-inverse)"})

(defcss
  ".dark .positive.filled.info"
  {"background-color"
   "var(--positive-filled-info-background-color-inverse)"})

(defcss
  ".dark .positive.filled.info:hover"
  {"background-color"
   "var(--positive-filled-info-background-color-hover-inverse)"})

(defcss
  ".dark .positive-filled.info:active"
  {"background-color" "var(--info-background-color-active-inverse)"})

(defcss
  ".negative"
  {"color" "var(--negative-color)",
   "background-color" "var(--negative-background-color)"})

(defcss
  ".negative:hover"
  {"color" "var(--negative-color-hover)",
   "background-color" "var(--negative-background-color-hover)"})

(defcss
  ".negative:active"
  {"color" "var(--negative-color-active)",
   "background-color" "var(--negative-background-color-active)"})

(defcss
  ".negative.info"
  {"color" "var(--negative-info-color)",
   "background-color" "var(--negative-info-background-color)"})

(defcss
  ".negative.info:hover"
  {"color" "var(--negative-info-color-hover)",
   "background-color" "var(--negative-info-background-color-hover)"})

(defcss
  ".negative.info:active"
  {"color" "var(--negative-info-color-active)",
   "background-color" "var(--negative-info-background-color-active)"})

(defcss
  ".negative.minimal"
  {"color" "var(--negative-minimal-color)",
   "background-color" "var(--negative-minimal-background-color)"})

(defcss
  ".negative.minimal:hover"
  {"color" "var(--negative-minimal-color-hover)",
   "background-color" "var(--negative-minimal-background-color-hover)"})

(defcss
  ".negative.minimal:active"
  {"color" "var(--negative-minimal-color-active)",
   "background-color"
   "var(--negative-minimal-background-color-active)"})

(defcss
  ".negative.bordered:active"
  {"background-color"
   "var(--negative-bordered-background-color-active)",
   "border-color" "var(--negative-bordered-border-color-active)",
   "color" "var(--negative-bordered-color-active)"})

(defcss
  ".negative.bordered"
  {"border-color" "var(--negative-bordered-border-color)",
   "background-color" "var(--negative-bordered-background-color)",
   "color" "var(--negative-bordered-color)"})

(defcss
  ".negative.bordered:hover"
  {"color" "var(--negative-bordered-color-hover)",
   "border-color" "var(--negative-bordered-border-color-hover)",
   "background-color"
   "var(--negative-bordered-background-color-hover)"})

(defcss
  ".negative.bordered.info:active"
  {"background-color"
   "var(--negative-bordered-info-background-color-active)",
   "border-color" "var(--negative-bordered-info-border-color-active)",
   "color" "var(--negative-bordered-info-color-active)"})

(defcss
  ".negative.bordered.info"
  {"border-color" "var(--negative-bordered-info-border-color)",
   "background-color" "var(--negative-bordered-info-background-color)",
   "color" "var(--negative-bordered-info-color)"})

(defcss
  ".negative.bordered.info:hover"
  {"color" "var(--negative-bordered-info-color-hover)",
   "border-color" "var(--negative-bordered-info-border-color-hover)",
   "background-color"
   "var(--negative-bordered-info-background-color-hover)"})

(defcss
  ".negative.filled"
  {"color" "var(--negative-filled-color)",
   "background-color" "var(--negative-filled-background-color)"})

(defcss
  ".negative.filled:hover"
  {"color" "var(--negative-filled-color-hover)",
   "background-color" "var(--negative-filled-background-color-hover)"})

(defcss
  ".negative.filled:active"
  {"color" "var(--negative-filled-color-active)",
   "background-color" "var(--negative-filled-background-color-active)"})

(defcss
  ".negative.filled.info"
  {"background-color" "var(--negative-filled-info-background-color)"})

(defcss
  ".negative.filled.info:hover"
  {"background-color"
   "var(--negative-filled-info-background-color-hover)"})

(defcss
  ".negative-filled.info:active"
  {"background-color" "var(--info-background-color-active)"})

(defcss
  ".dark .negative"
  {"color" "var(--negative-color-inverse)",
   "background-color" "var(--negative-background-color-inverse)"})

(defcss
  ".dark .negative:hover"
  {"color" "var(--negative-color-hover-inverse)",
   "background-color" "var(--negative-background-color-hover-inverse)"})

(defcss
  ".dark .negative:active"
  {"color" "var(--negative-color-active-inverse)",
   "background-color"
   "var(--negative-background-color-active-inverse)"})

(defcss
  ".dark .negative.info"
  {"color" "var(--negative-info-color-inverse)",
   "background-color" "var(--negative-info-background-color-inverse)"})

(defcss
  ".dark .negative.info:hover"
  {"color" "var(--negative-info-color-hover-inverse)",
   "background-color"
   "var(--negative-info-background-color-hover-inverse)"})

(defcss
  ".dark .negative.info:active"
  {"color" "var(--negative-info-color-active-inverse)",
   "background-color"
   "var(--negative-info-background-color-active-inverse)"})

(defcss
  ".dark .negative.minimal"
  {"color" "var(--negative-minimal-color-inverse)",
   "background-color"
   "var(--negative-minimal-background-color-inverse)"})

(defcss
  ".dark .negative.minimal:hover"
  {"color" "var(--negative-minimal-color-hover-inverse)",
   "background-color"
   "var(--negative-minimal-background-color-hover-inverse)"})

(defcss
  ".dark .negative.minimal:active"
  {"color" "var(--negative-minimal-color-active-inverse)",
   "background-color"
   "var(--negative-minimal-background-color-active-inverse)"})

(defcss
  ".dark .negative.bordered:active"
  {"background-color"
   "var(--negative-bordered-background-color-active-inverse)",
   "border-color"
   "var(--negative-bordered-border-color-active-inverse)",
   "color" "var(--negative-bordered-color-active-inverse)"})

(defcss
  ".dark .negative.bordered"
  {"border-color" "var(--negative-bordered-border-color-inverse)",
   "background-color"
   "var(--negative-bordered-background-color-inverse)",
   "color" "var(--negative-bordered-color-inverse)"})

(defcss
  ".dark .negative.bordered:hover"
  {"color" "var(--negative-bordered-color-hover-inverse)",
   "border-color" "var(--negative-bordered-border-color-hover-inverse)",
   "background-color"
   "var(--negative-bordered-background-color-hover-inverse)"})

(defcss
  ".dark .negative.bordered.info:active"
  {"background-color"
   "var(--negative-bordered-info-background-color-active-inverse)",
   "border-color"
   "var(--negative-bordered-info-border-color-active-inverse)",
   "color" "var(--negative-bordered-info-color-active-inverse)"})

(defcss
  ".dark .negative.bordered.info"
  {"border-color" "var(--negative-bordered-info-border-color-inverse)",
   "background-color"
   "var(--negative-bordered-info-background-color-inverse)",
   "color" "var(--negative-bordered-info-color-inverse)"})

(defcss
  ".dark .negative.bordered.info:hover"
  {"color" "var(--negative-bordered-info-color-hover-inverse)",
   "border-color"
   "var(--negative-bordered-info-border-color-hover-inverse)",
   "background-color"
   "var(--negative-bordered-info-background-color-hover-inverse)"})

(defcss
  ".dark .negative.filled"
  {"color" "var(--negative-filled-color-inverse)",
   "background-color"
   "var(--negative-filled-background-color-inverse)"})

(defcss
  ".dark .negative.filled:hover"
  {"color" "var(--negative-filled-color-hover-inverse)",
   "background-color"
   "var(--negative-filled-background-color-hover-inverse)"})

(defcss
  ".dark .negative.filled:active"
  {"color" "var(--negative-filled-color-active-inverse)",
   "background-color"
   "var(--negative-filled-background-color-active-inverse)"})

(defcss
  ".dark .negative.filled.info"
  {"background-color"
   "var(--negative-filled-info-background-color-inverse)"})

(defcss
  ".dark .negative.filled.info:hover"
  {"background-color"
   "var(--negative-filled-info-background-color-hover-inverse)"})

(defcss
  ".dark .negative-filled.info:active"
  {"background-color" "var(--info-background-color-active-inverse)"})

(defcss
  ".warning"
  {"color" "var(--warning-color)",
   "background-color" "var(--warning-background-color)"})

(defcss
  ".warning:hover"
  {"color" "var(--warning-color-hover)",
   "background-color" "var(--warning-background-color-hover)"})

(defcss
  ".warning:active"
  {"color" "var(--warning-color-active)",
   "background-color" "var(--warning-background-color-active)"})

(defcss
  ".warning.info"
  {"color" "var(--warning-info-color)",
   "background-color" "var(--warning-info-background-color)"})

(defcss
  ".warning.info:hover"
  {"color" "var(--warning-info-color-hover)",
   "background-color" "var(--warning-info-background-color-hover)"})

(defcss
  ".warning.info:active"
  {"color" "var(--warning-info-color-active)",
   "background-color" "var(--warning-info-background-color-active)"})

(defcss
  ".warning.minimal"
  {"color" "var(--warning-minimal-color)",
   "background-color" "var(--warning-minimal-background-color)"})

(defcss
  ".warning.minimal:hover"
  {"color" "var(--warning-minimal-color-hover)",
   "background-color" "var(--warning-minimal-background-color-hover)"})

(defcss
  ".warning.minimal:active"
  {"color" "var(--warning-minimal-color-active)",
   "background-color" "var(--warning-minimal-background-color-active)"})

(defcss
  ".warning.bordered:active"
  {"background-color" "var(--warning-bordered-background-color-active)",
   "border-color" "var(--warning-bordered-border-color-active)",
   "color" "var(--warning-bordered-color-active)"})

(defcss
  ".warning.bordered"
  {"border-color" "var(--warning-bordered-border-color)",
   "background-color" "var(--warning-bordered-background-color)",
   "color" "var(--warning-bordered-color)"})

(defcss
  ".warning.bordered:hover"
  {"color" "var(--warning-bordered-color-hover)",
   "border-color" "var(--warning-bordered-border-color-hover)",
   "background-color" "var(--warning-bordered-background-color-hover)"})

(defcss
  ".warning.bordered.info:active"
  {"background-color"
   "var(--warning-bordered-info-background-color-active)",
   "border-color" "var(--warning-bordered-info-border-color-active)",
   "color" "var(--warning-bordered-info-color-active)"})

(defcss
  ".warning.bordered.info"
  {"border-color" "var(--warning-bordered-info-border-color)",
   "background-color" "var(--warning-bordered-info-background-color)",
   "color" "var(--warning-bordered-info-color)"})

(defcss
  ".warning.bordered.info:hover"
  {"color" "var(--warning-bordered-info-color-hover)",
   "border-color" "var(--warning-bordered-info-border-color-hover)",
   "background-color"
   "var(--warning-bordered-info-background-color-hover)"})

(defcss
  ".warning.filled"
  {"color" "var(--warning-filled-color)",
   "background-color" "var(--warning-filled-background-color)"})

(defcss
  ".warning.filled:hover"
  {"color" "var(--warning-filled-color-hover)",
   "background-color" "var(--warning-filled-background-color-hover)"})

(defcss
  ".warning.filled:active"
  {"color" "var(--warning-filled-color-active)",
   "background-color" "var(--warning-filled-background-color-active)"})

(defcss
  ".warning.filled.info"
  {"background-color" "var(--warning-filled-info-background-color)"})

(defcss
  ".warning.filled.info:hover"
  {"background-color"
   "var(--warning-filled-info-background-color-hover)"})

(defcss
  ".warning-filled.info:active"
  {"background-color" "var(--info-background-color-active)"})

(defcss
  ".dark .warning"
  {"color" "var(--warning-color-inverse)",
   "background-color" "var(--warning-background-color-inverse)"})

(defcss
  ".dark .warning:hover"
  {"color" "var(--warning-color-hover-inverse)",
   "background-color" "var(--warning-background-color-hover-inverse)"})

(defcss
  ".dark .warning:active"
  {"color" "var(--warning-color-active-inverse)",
   "background-color" "var(--warning-background-color-active-inverse)"})

(defcss
  ".dark .warning.info"
  {"color" "var(--warning-info-color-inverse)",
   "background-color" "var(--warning-info-background-color-inverse)"})

(defcss
  ".dark .warning.info:hover"
  {"color" "var(--warning-info-color-hover-inverse)",
   "background-color"
   "var(--warning-info-background-color-hover-inverse)"})

(defcss
  ".dark .warning.info:active"
  {"color" "var(--warning-info-color-active-inverse)",
   "background-color"
   "var(--warning-info-background-color-active-inverse)"})

(defcss
  ".dark .warning.minimal"
  {"color" "var(--warning-minimal-color-inverse)",
   "background-color"
   "var(--warning-minimal-background-color-inverse)"})

(defcss
  ".dark .warning.minimal:hover"
  {"color" "var(--warning-minimal-color-hover-inverse)",
   "background-color"
   "var(--warning-minimal-background-color-hover-inverse)"})

(defcss
  ".dark .warning.minimal:active"
  {"color" "var(--warning-minimal-color-active-inverse)",
   "background-color"
   "var(--warning-minimal-background-color-active-inverse)"})

(defcss
  ".dark .warning.bordered:active"
  {"background-color"
   "var(--warning-bordered-background-color-active-inverse)",
   "border-color" "var(--warning-bordered-border-color-active-inverse)",
   "color" "var(--warning-bordered-color-active-inverse)"})

(defcss
  ".dark .warning.bordered"
  {"border-color" "var(--warning-bordered-border-color-inverse)",
   "background-color"
   "var(--warning-bordered-background-color-inverse)",
   "color" "var(--warning-bordered-color-inverse)"})

(defcss
  ".dark .warning.bordered:hover"
  {"color" "var(--warning-bordered-color-hover-inverse)",
   "border-color" "var(--warning-bordered-border-color-hover-inverse)",
   "background-color"
   "var(--warning-bordered-background-color-hover-inverse)"})

(defcss
  ".dark .warning.bordered.info:active"
  {"background-color"
   "var(--warning-bordered-info-background-color-active-inverse)",
   "border-color"
   "var(--warning-bordered-info-border-color-active-inverse)",
   "color" "var(--warning-bordered-info-color-active-inverse)"})

(defcss
  ".dark .warning.bordered.info"
  {"border-color" "var(--warning-bordered-info-border-color-inverse)",
   "background-color"
   "var(--warning-bordered-info-background-color-inverse)",
   "color" "var(--warning-bordered-info-color-inverse)"})

(defcss
  ".dark .warning.bordered.info:hover"
  {"color" "var(--warning-bordered-info-color-hover-inverse)",
   "border-color"
   "var(--warning-bordered-info-border-color-hover-inverse)",
   "background-color"
   "var(--warning-bordered-info-background-color-hover-inverse)"})

(defcss
  ".dark .warning.filled"
  {"color" "var(--warning-filled-color-inverse)",
   "background-color" "var(--warning-filled-background-color-inverse)"})

(defcss
  ".dark .warning.filled:hover"
  {"color" "var(--warning-filled-color-hover-inverse)",
   "background-color"
   "var(--warning-filled-background-color-hover-inverse)"})

(defcss
  ".dark .warning.filled:active"
  {"color" "var(--warning-filled-color-active-inverse)",
   "background-color"
   "var(--warning-filled-background-color-active-inverse)"})

(defcss
  ".dark .warning.filled.info"
  {"background-color"
   "var(--warning-filled-info-background-color-inverse)"})

(defcss
  ".dark .warning.filled.info:hover"
  {"background-color"
   "var(--warning-filled-info-background-color-hover-inverse)"})

(defcss
  ".dark .warning-filled.info:active"
  {"background-color" "var(--info-background-color-active-inverse)"})

(defcss ".kushi-button" {"font-family" "var(--primary-font-family)"})

(defcss
  ".kushi-button.bordered"
  {"border-width" "var(--button-border-width)"})

(defcss
  ".kushi-tag.bordered"
  {"border-width" "var(--tag-border-width)"})

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
  {"--semantic-element-outer-box-shadow-blur-length"    "0px",
   "--semantic-element-box-outer-box-shadow-strength"   "30%",
   "--semantic-element-outer-box-shadow-2-blur-length"  "20px",
   "--semantic-element-box-outer-box-shadow-2-strength" "00%",
   "--debug-grid-size"                                  "107px",
   "--debug-grid-color"                                 "#eee",
   "--new-purple-hue"                                   "330",
   "--new-green-hue"                                    "145",
   "--new-red-hue"                                      "20",
   "--red-lightness-shift"                              "-7%",
   "--new-yellow-hue"                                   "94",
   "--yellow-lightness-shift"                           "5%",
   "--new-blue-hue"                                     "265",
   "--blue-c-shift"                                     "0.08",
   "--positive-hue"                                     "var(--new-green-hue)",
   "--negative-hue"                                     "var(--new-red-hue)",
   "--negative-lightness-shift"                         "var(--red-lightness-shift)",
   "--warning-hue"                                      "var(--new-yellow-hue)",
   "--warning-lightness-shift"                          "var(--yellow-lightness-shift)",
   "--accent-hue"                                       "var(--new-blue-hue)",
   "--neutral-hue"                                      "var(--new-blue-hue)",
   "--accent-c-shift"                                   "-0.08",
   "--button-padding-inline-compact"                    "0.8em",
   "--button-padding-inline"                            "1.2em",
   "--button-padding-inline-roomy"                      "1.5em",
   "--button-padding-block-compact"                     "0.42em",
   "--button-padding-block"                             "0.67em",
   "--button-padding-block-roomy"                       "0.8666em"})

(defcss
  "[data-kui-colorway=\"accent\"]"
  {"--_hue" "var(--accent-hue)"})

(defcss
  "[data-kui-colorway=\"positive\"]"
  {"--_hue" "var(--positive-hue)"})

(defcss
  "[data-kui-colorway=\"negative\"]"
  {"--_hue" "var(--negative-hue)"})

(defcss
  "[data-kui-colorway=\"warning\"]"
  {"--_hue" "var(--warning-hue)"})

(defcss
  ".kui-surface"
  {"--_bg-chroma"
   "0.0",

   "--_fg-chroma"
   "0.0",

   "--_color"
   "oklch(calc(var(--_fg-lightness) + var(--_fg-lightness-shift, 0%) + var(--_fg-lightness-shift-state, 0%))    calc(var(--_fg-chroma, 0) + var(--_fg-chroma-shift, 0) + var(--_fg-chroma-shift-state, 0))    var(--_hue, 0)  )",

   "color"
   "var(--_color)",

   "--_background-color"
   "oklch(calc(var(--_bg-lightness) + var(--_bg-lightness-shift, 0%) + var(--_bg-lightness-shift-state, 0%))    calc(var(--_bg-chroma, 0) + var(--_bg-chroma-shift, 0) + var(--_bg-chroma-shift-state, 0))    var(--_hue, 0) / var(--_bgc-alpha, 1)  )",

   "background-color"
   "var(--_background-color)"

   "--_stroke-args"
   "0  0  0  var(--stroke-width, 0px)  var(--stroke-color, var(--_color))",

   "--_stroke"
   "inset var(--_stroke-args)",

   "[data-kui-stroke-align=\"outside\"]"
    {"--_stroke" "var(--_stroke-args)"}
   
   "box-shadow"
   "var(--_stroke), 4px 4px var(--_box-shadow-blur-length, 0) 0px color-mix(in oklch, var(--_color) var(--_box-shadow-strength, 0%), transparent), 2px 2px var(--_box-shadow-2-blur-length, 0) 0px color-mix(in oklch, var(--_color) var(--_box-shadow-2-strength, 0%), transparent)"
   })
                                     
                                     
(defcss "[data-kui-ia]"
  {:hover  {:--_fg-lightness-shift-state :-13%
            :--_bg-lightness-shift-state :-3%}
   :active {:--_fg-lightness-shift-state :-23%
            :--_bg-lightness-shift-state :-8%}})

(defcss "[data-kui-colorway][data-kui-ia]"
  {:hover  {:--_fg-chroma-shift-state :0.06
            :--_bg-chroma-shift-state :0.03}
   :active {:--_fg-chroma-shift-state :0.13
            :--_bg-chroma-shift-state :0.07}})

(defcss "[data-kui-surface=\"soft\"]"
  {:--_bgc-alpha        :0.5
   :--_bg-lightness     :94%
   :--_fg-lightness     :44%
   "[data-kui-colorway]" {:--_bg-chroma :0.05
                          :--_fg-chroma :0.24}
   "[data-kui-ia]"       {:hover  {}
                          :active {}}})

(defcss "[data-kui-surface= \"solid\"]"
 {:color               :white
  :--_bg-lightness     :53%
  :--_fg-lightness     :44%
  "[data-kui-colorway]" {:--_bg-chroma :0.27
                         :--_fg-chroma :0.24}
  "[data-kui-ia]"       {:hover  {:--_bg-lightness-shift-state :-5%}
                         :active {:--_bg-lightness-shift-state :-10%}}})

(defcss "[data-kui-surface=\"outline\"]"
  {
  ;;  :--_bgc-alpha        :0.5
   :--_bg-lightness    :94%
   :--_fg-lightness    :44%
   :--stroke-width     :1px
   :--stroke-align     "inside"
   :--_bgc-alpha       0
   "[data-kui-colorway]" {:--_bg-chroma :0.05
                          :--_fg-chroma :0.24}
   "[data-kui-ia]"       {:hover  {:--_bgc-alpha :0.17}
                          :active {:--_bgc-alpha :0.23}}})


(defcss "[data-kui-surface=\"minimal\"]"
  {:--_bg-lightness     :94%
   :--_fg-lightness     :44%
   :--_bgc-alpha        :0.0
   "[data-kui-colorway]" {:--_fg-chroma :0.24
                          :--_bg-chroma :0.05}
   "[data-kui-ia]"       {:hover  {:--_bgc-alpha :0.25}
                          :active {:--_bgc-alpha :0.35}}})

(defcss ".kui-rounded" 
  {:border-radius :0.3em})

(defcss ".kui-pill" 
  {:border-radius :9999px})

(defcss ".kui-button"
  {:--_padding-inline            "var(--button-padding-inline)"
   :--_padding-block             "var(--button-padding-block)"
   "[data-kui-packing=\"compact\"]" {:--_padding-inline "var(--button-padding-inline-compact)"
                                     :--_padding-block  "var(--button-padding-block-compact)"}
   
   "[data-kui-packing=\"roomy\"]"   {:--_padding-inline "var(--button-padding-inline-roomy)"
                                     :--_padding-block  "var(--button-padding-block-roomy)"}
   
   "[data-kui-start-enhancer]"    {:padding-inline-start "calc(var(--_padding-inline) * 0.7666)"}

   "[data-kui-end-enhancer]"      {:padding-inline-end "calc(var(--_padding-inline) * 0.7666)"}
   
   "[data-kui-icon-button]"       {:--_padding-inline            "var(--_padding-block)"
                                   :_.material-symbols-outlined {:min-width   :1.2ch
                                                                 :line-height :normal}}})
