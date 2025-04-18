# Kushi shorthand reference


Shorthand grammer is available for the following css props:
```Clojure
:ai   ; :align-items
:b    ; :border
:bb   ; :border-block
:bbc  ; :border-block-color
:bbe  ; :border-block-end
:bbec ; :border-block-end-color
:bbes ; :border-block-end-style
:bbew ; :border-block-end-width
:bbs  ; :border-block-style
:bbsc ; :border-block-start-color
:bbss ; :border-block-start-style
:bbsw ; :border-block-start-width
:bbw  ; :border-block-width
:bc   ; :border-color
:beer ; :border-end-end-radius
:besr ; :border-end-start-radius
:bg   ; :background
:bgc  ; :background-color
:bgi  ; :background-image
:bgp  ; :background-position
:bgr  ; :background-repeat
:bgs  ; :background-size
:bi   ; :border-inline
:bic  ; :border-inline-color
:bie  ; :border-inline-end
:biec ; :border-inline-end-color
:bies ; :border-inline-end-style
:biew ; :border-inline-end-width
:bis  ; :border-inline-style
:bisc ; :border-inline-start-color
:biss ; :border-inline-start-style
:bisw ; :border-inline-start-width
:biw  ; :border-inline-width
:br   ; :border-radius
:bs   ; :border-style
:bser ; :border-start-end-radius
:bssr ; :border-start-start-radius
:bw   ; :border-width
:c    ; :color
:d    ; :display
:ff   ; :font-family
:fs   ; :font-size
:fv   ; :font-variant
:fw   ; :font-weight
:g    ; :grid
:ga   ; :grid-area
:gac  ; :grid-auto-columns
:gaf  ; :grid-auto-flow
:gar  ; :grid-auto-rows
:gc   ; :grid-column
:gce  ; :grid-column-end
:gcs  ; :grid-column-start
:gr   ; :grid-row
:gre  ; :grid-row-end
:grs  ; :grid-row-start
:gt   ; :grid-template
:gta  ; :grid-template-areas
:gtc  ; :grid-template-columns
:gtr  ; :grid-template-rows
:h    ; :height
:i    ; :inset
:ib   ; :inset-block
:ibe  ; :inset-block-end
:ibs  ; :inset-block-start
:ii   ; :inset-inline
:iie  ; :inset-inline-end
:iis  ; :inset-inline-start
:jc   ; :justify-content
:ji   ; :justify-items
:lh   ; :line-height
:m    ; :margin
:mb   ; :margin-block
:mbe  ; :margin-block-end
:mbs  ; :margin-block-start
:mi   ; :margin-inline
:mie  ; :margin-inline-end
:mis  ; :margin-inline-start
:o    ; :opacity
:p    ; :padding
:pb   ; :padding-block
:pbe  ; :padding-block-end
:pbs  ; :padding-block-start
:pi   ; :padding-inline
:pie  ; :padding-inline-end
:pis  ; :padding-inline-start
:ta   ; :text-align
:td   ; :text-decoration
:tdc  ; :text-decoration-color
:tdl  ; :text-decoration-line
:tds  ; :text-decoration-style
:tdt  ; :text-decoration-thickness
:tt   ; :text-transform
:tuo  ; :text-underline-offset
:tup  ; :text-underline-position
:v    ; :visibility
:va   ; :vertical-align
:w    ; :width
:ws   ; :white-space
:z    ; :z-index
:zi   ; :z-index
```

<br>

Shorthand grammer is available for the following enumerated property values:
```Clojure
:ai--b    ; align-items: baseline
:ai--c    ; align-items: center
:ai--e    ; align-items: end
:ai--fe   ; align-items: flex-end
:ai--fs   ; align-items: flex-start
:ai--n    ; align-items: normal
:ai--s    ; align-items: start
:bgp--b   ; background-position: bottom
:bgp--c   ; background-position: center
:bgp--l   ; background-position: left
:bgp--r   ; background-position: right
:bgp--t   ; background-position: top
:bgr--nr  ; background-repeat: no-repeat
:bgr--r   ; background-repeat: round
:bgr--rx  ; background-repeat: repeat-x
:bgr--ry  ; background-repeat: repeat-y
:bgr--s   ; background-repeat: space
:d--b     ; display: block
:d--c     ; display: contents
:d--f     ; display: flex
:d--g     ; display: grid
:d--i     ; display: inline
:d--ib    ; display: inline-block
:d--if    ; display: inline-flex
:d--ig    ; display: inline-grid
:d--it    ; display: inline-table
:d--li    ; display: list-item
:d--t     ; display: table
:d--tc    ; display: table-cell
:d--tcg   ; display: table-column-group
:d--tfg   ; display: table-footer-group
:d--thg   ; display: table-header-group
:d--tr    ; display: table-row
:d--trg   ; display: table-row-group
:jc--c    ; justify-content: center
:jc--e    ; justify-content: end
:jc--fe   ; justify-content: flex-end
:jc--fs   ; justify-content: flex-start
:jc--l    ; justify-content: left
:jc--n    ; justify-content: normal
:jc--r    ; justify-content: right
:jc--s    ; justify-content: start
:jc--sa   ; justify-content: space-around
:jc--sb   ; justify-content: space-between
:jc--se   ; justify-content: space-evenly
:ji--a    ; justify-items: auto
:ji--c    ; justify-items: center
:ji--e    ; justify-items: end
:ji--fe   ; justify-items: flex-end
:ji--fs   ; justify-items: flex-start
:ji--l    ; justify-items: left
:ji--n    ; justify-items: normal
:ji--r    ; justify-items: right
:ji--s    ; justify-items: start
:ji--se   ; justify-items: self-end
:ji--ss   ; justify-items: self-start
:ta--c    ; text-align: center
:ta--e    ; text-align: end
:ta--j    ; text-align: justify
:ta--ja   ; text-align: justify-all
:ta--l    ; text-align: left
:ta--mp   ; text-align: match-parent
:ta--r    ; text-align: right
:ta--s    ; text-align: start
:td--lt   ; text-decoration: line-through
:td--o    ; text-decoration: overline
:td--u    ; text-decoration: underline
:tdl--lt  ; text-decoration-line: line-through
:tdl--o   ; text-decoration-line: overline
:tdl--u   ; text-decoration-line: underline
:tds--s   ; text-decoration-style: solid
:tds--w   ; text-decoration-style: wavy
:tdt--ff  ; text-decoration-thickness: from-font
:tt--c    ; text-transform: captitalize
:tt--fw   ; text-transform: full-width
:tt--l    ; text-transform: lowercase
:tt--u    ; text-transform: uppercase
:tup--ff  ; text-underline-position: from-font
:tup--l   ; text-underline-position: left
:tup--r   ; text-underline-position: right
:tup--u   ; text-underline-position: under
:v--c     ; visibility: collapse
:v--h     ; visibility: hidden
:v--v     ; visibility: visibile
:va--b    ; vertical-align: baseline
:va--m    ; vertical-align: middle
:va--s    ; vertical-align: sub
:va--t    ; vertical-align: top
:va--tb   ; vertical-align: text-bottom
:va--tt   ; vertical-align: text-top
:ws--n    ; white-space: nowrap
:ws--p    ; white-space: pre
:ws--pl   ; white-space: pre-line
:ws--pw   ; white-space: pre-wrap
```
