(ns kushi.typography)

(def system-font-stacks
  (array-map
   300 {:normal [".SFNS-Light" ".SFNSText-Light" ".HelveticaNeueDeskInterface-Light" ".LucidaGrandeUI" "Segoe UI Light" "Ubuntu Light" "Roboto-Light" "DroidSans" "Tahoma"]
        :italic [".SFNS-LightItalic" ".SFNSText-LightItalic" ".HelveticaNeueDeskInterface-Italic" ".LucidaGrandeUI" "Segoe UI Light Italic" "Ubuntu Light Italic" "Roboto-LightItalic" "DroidSans" "Tahoma"]}
   400 {:normal [".SFNS-Regular" ".SFNSText-Regular" ".HelveticaNeueDeskInterface-Regular" ".LucidaGrandeUI" "Segoe UI" "Ubuntu" "Roboto-Regular" "DroidSans" "Tahoma"]
        :italic [".SFNS-Italic" ".SFNSText-Italic" ".HelveticaNeueDeskInterface-Italic" ".LucidaGrandeUI" "Segoe UI Italic" "Ubuntu Italic" "Roboto-Italic" "DroidSans" "Tahoma"]}
   500 {:normal [".SFNS-Medium" ".SFNSText-Medium" ".HelveticaNeueDeskInterface-MediumP4" ".LucidaGrandeUI" "Segoe UI Semibold" "Ubuntu Medium" "Roboto-Medium" "DroidSans-Bold" "Tahoma Bold"]
        :italic [".SFNS-MediumItalic" ".SFNSText-MediumItalic" ".HelveticaNeueDeskInterface-MediumItalicP4" ".LucidaGrandeUI" "Segoe UI Semibold Italic" "Ubuntu Medium Italic" "Roboto-MediumItalic" "DroidSans-Bold" "Tahoma Bold"]}
   700 {:normal [".SFNS-Bold" ".SFNSText-Bold" ".HelveticaNeueDeskInterface-Bold" ".LucidaGrandeUI" "Segoe UI Bold" "Ubuntu Bold" "Roboto-Bold" "DroidSans-Bold" "Tahoma Bold"]
        :italic [".SFNS-BoldItalic" ".SFNSText-BoldItalic" ".HelveticaNeueDeskInterface-BoldItalic" ".LucidaGrandeUI" "Segoe UI Bold Italic" "Ubuntu Bold Italic" "Roboto-BoldItalic" "DroidSans-Bold" "Tahoma Bold"]}))
