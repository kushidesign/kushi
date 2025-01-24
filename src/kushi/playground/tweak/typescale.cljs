(ns kushi.playground.tweak.typescale
  (:require [clojure.string :as string]
            [clojure.data :as data]
            [kushi.css.core :refer [sx css merge-attrs css-vars-map]]
            [domo.core :as domo]
            [kushi.ui.label.core :refer [label]]
            [kushi.ui.button.core :refer [button]]
            [kushi.ui.icon.core :refer [icon]]
            [kushi.ui.typescale :refer [create-typescale scale-a scale-b]]
            [kushi.playground.component-examples :refer [copy-to-clipboard-button]]
            [reagent.core :as r]))

(defn scale-syntax
  [{:keys [coll postfix]}]
  (keep-indexed (fn [idx x]
                  (when (even? idx)
                    [(-> x
                         name
                         (string/replace #"^\$" "")
                         (str postfix)
                         keyword)
                     (js/parseFloat (name (nth coll (inc idx))))] ))
                coll))

(defn code-block [x]
  [:div.relative
   [:textarea
    (merge-attrs (sx :fs--$small
                     :.code
                     :bgc--white
                     :b--1px:solid:$neutral-200
                     :p--1rem)
                 {:defaultValue x
                  :rows         22
                  :readOnly     true})]
   [copy-to-clipboard-button
    (merge-attrs (sx :.top-right-corner-inside!)
                 {:-text-to-copy x})]
   ])

(defn scale-row [s state ks]
  [:div (sx :.flex-col-fs :gap--0.25rem)
   [label (str "Scale-" s)]
   [code-block (string/join "\n" (keep-indexed (fn [idx x] (when ((if (= s "a") even? odd?) idx) (js/parseFloat (x @state)))) ks))]])

(defn type-tweaker []
  (let [scale-a*   (create-typescale {:size-limit "xxxx"
                                      :shift      0})
        scale-b*   (create-typescale {:size-limit "xxxx"
                                      :shift      1})
        scale-a    (scale-syntax {:coll scale-a*})
        scale-b    (scale-syntax {:coll    scale-b*
                                  :postfix "-b"})
        scale      (interleave scale-a scale-b)
        scale-ks   (map first scale)
        init-scale (into {} scale)
        state      (r/atom init-scale)
        copy-view? (r/atom false)]
    (fn []
      (into [:div (sx :.fixed-inline-end
                      :w--800px
                      :.wireframe
                      :.elevated
                      :zi--1000
                      :bgc--white
                      [:--wrapper-padding-inline :2rem]
                      [:--wrapper-padding-block :3rem]
                      :pb--$wrapper-padding-block
                      :pi--$wrapper-padding-inline)]
            (into []
                  (concat (for [size-kw scale-ks]
                            [:div (sx :.flex-row-fs
                                      :.relative
                                      :pb--0.333rem
                                      :c--$neutral-600
                                      ["nth-child(odd):color" :$neutral-900]
                                      ["nth-child(odd):fw" :$semi-bold]
                                      ["nth-child(even)_.type-tweaker-type-size-class-name:visibility" :hidden])
                             [:span (sx :.flex-row-fs #_:min-width--200px)
                              [:span (sx :.type-tweaker-type-size-class-name
                                         :.xxsmall
                                         :ff--$code-font-stack
                                         :.inline-block
                                         :min-width--125px)
                               (name size-kw)]
                              [:span (sx :.inline-block
                                         :min-width--50px)
                               (size-kw @state)]]
                             [:input {:id        size-kw
                                      :type      :range
                                      :min       0.5
                                      :max       4
                                      :value     (size-kw @state)
                                      ;; :value     size-kw
                                      :step      0.005
                                      :on-change #(let [node (domo/et %)
                                                        val  (.-value node)]
                                                    (swap! state assoc (keyword size-kw) val)
                                                    (domo/set-css-var! js/document.body
                                                                      (str "--" (name size-kw))
                                                                      (str val "rem")))}]])
                          [[:div
                            (sx :.flex-row-c :gap--1.5rem :mbs--2rem)
                            [button
                             (sx :fs--$small
                                 {:on-click #(do (let [[_ to-reset _] (data/diff init-scale @state)]
                                                   (doseq [[size-kw _] to-reset]
                                                     (domo/set-css-var! js/document.body
                                                                       (str "--" (name size-kw))
                                                                       (str (size-kw init-scale) "rem"))))
                                                 (reset! state init-scale))})
                             "Reset"]
                            [button
                             (sx :fs--$small
                                 {:on-click #(reset! copy-view? (not @copy-view?))})
                             "Copy data..."]]
                           [:div
                            (let [display (if @copy-view? "flex" "none")]
                              {:style (css-vars-map display)
                               :class (css :.type-tweaker-copy-data-view
                                           :.flex-col-sb
                                           :.absolute-fill
                                           :bgc--white
                                           :pb--$wrapper-padding-block
                                           :pi--$wrapper-padding-inline
                                           [:d display])})
                            [:div
                             (sx :.flex-row-sa
                                 :ai--fs
                                 :gap--1.5rem)
                             [scale-row "a" state scale-ks]
                             [scale-row "b" state scale-ks]
                             [:div (sx :.flex-col-fs
                                       :gap--0.25rem)
                              [label "K/V sequence"]
                              [code-block (string/join
                                           "\n"
                                           (apply concat
                                                  (keep-indexed
                                                   (fn [idx x]
                                                     (when (even? idx)
                                                       [(keyword (str "$" (name x)))
                                                        (keyword (str (x @state) "rem"))]))
                                                   scale-ks))) ]]]
                            [:div
                             (sx :.flex-row-c)
                             [button
                              (merge-attrs
                               (sx :fs--$small)
                               {:on-click #(reset! copy-view? (not @copy-view?))})
                              [icon :west]
                              "Back to controls"]]]]))))))

