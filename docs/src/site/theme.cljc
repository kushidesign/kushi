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
                   }

   :ui            ["body"
                   ;; this should be :--primary-font-family ?
                   {:font-family "Inter, sys, sans-serif"
                    :color       :--gray900} ]})
