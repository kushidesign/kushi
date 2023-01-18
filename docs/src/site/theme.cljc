(ns site.theme)


(def my-theme
  {:design-tokens [:--sans-serif-font-stack "Inter, sys, sans-serif"
                   :--serif-font-stack      "Times, serif"
                   :--primary-font-family   :--sans-serif-font-stack]
   :ui            ["body"
                   {:font-family "Inter, sys, sans-serif"
                    :color       :--gray900} ]})
