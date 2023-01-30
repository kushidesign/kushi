(ns site.theme)


(def my-theme
  {:design-tokens [:--sans-serif-font-stack "Inter, sys, sans-serif"
                   :--serif-font-stack      "Times, serif"
                   :--primary-font-family   :--sans-serif-font-stack]
   :font-loading  {
                      ;;  :google-fonts  [
                      ;;                  "Roboto"
                      ;;                  {:family "Public Sans"
                      ;;                   :styles {:normal [100]
                      ;;                            :italic [300]}}]
                      ;;

                        :google-material-symbols ["Material Symbols Outlined"
                                                  "Material Symbols Rounded"
                                                  "Material Symbols Sharp"]

                        ;; Because all three requests above (the ones for Material Symbols font-families)
                        ;; do not contain an entry for :axes, they will load fonts with all the variable axes included.
                        ;; An example, the first map in the :google-material-symbols is equivalent to this:
                        ;;
                        ;; {:family "Material Symbols Outlined"
                        ;;  :axes {:opsz :20..48
                        ;;         :wght :100..700
                        ;;         :grad :-50..200
                        ;;         :fill :0..1}}

                        ;; When you've made a design decision about the look and feel of icons
                        ;; in your project, you can load the symbols font of your choice as a
                        ;; static icon font instead of a variable one:

                        ;; {:family "Material Symbols Outlined"
                        ;;  :axes {:opsz 24
                        ;;         :wght 400
                        ;;         :grad 0
                        ;;         :fill 0}}
                   }

   :ui            ["body"
                   ;; this should be :--primary-font-family ?
                   {:font-family "Inter, sys, sans-serif"
                    :color       :--gray900} ]})
