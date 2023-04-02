(ns kushi.playground.demobox.handlers
  (:require
   [kushi.ui.dom :as dom]
   [kushi.playground.state :refer [*state]]))

(defn variant-change
  [{:keys [variant-category
           utility-class
           *demostate]
    :as m}]
  (let [{:keys [component-id nm]} @*demostate]
   (when-let [node (js/document.getElementById component-id)]
         (when-let [els (.querySelectorAll node (str ".kushi-" nm))]
           (doseq [el els]
             (let [cl                      (.-classList el)
                   active-controls-by-type (:active-controls-by-type @*demostate)]
               (doseq [[_ class] active-controls-by-type
                       :when     class]
                 (.remove cl class))

        ;; (println (get-in @*state [:controls-by-type component-id variant-category]))
        ;; (println (swap! *state assoc-in [:controls-by-type component-id variant-category] utility-class))

          ;; (println :demo-state-before-swap (get-in @*demostate [:active-controls-by-type variant-category]))
          ;; (println :app-state-before-swap (get-in @*state [:demo component-id :active-controls-by-type variant-category]))

               (swap! *demostate
                      assoc-in
                      [:active-controls-by-type variant-category]
                      utility-class)

          ;; (println :demo-state-after-swap (get-in @*demostate [:active-controls-by-type variant-category]))
          ;; (println :app-state-after-swap (get-in @*state [:demo component-id :active-controls-by-type variant-category]))

               (swap! *state
                      assoc-in
                      [:demo component-id :active-controls-by-type variant-category]
                      utility-class)

          ;; (println :app-state-after-swap2 (get-in @*state [:demo component-id :active-controls-by-type]))

               (doseq [[_ class] active-controls-by-type
                       :when     class]
                 (.add cl class))))))))

(defn get-example [examples k]
  (->> examples
       (filter #(= k (:label %)))
       first))

(defn slider-on-change
    [{:keys [variant-category
             *demostate
             flavors]}
     event]

    (let [val  (dom/etv->int event)
          step (name (nth flavors val))
          {:keys [component-id nm active-controls-by-type]} @*demostate]
      (when-let [els (.querySelectorAll (js/document.getElementById component-id)
                                        (str ".kushi-" nm))]

        #_(js/console.log
         {:component-id      nm
          :nm                nm
          :component-e       (js/document.getElementById component-id)
          :els               els
          :el-count          (.-length els)
          :flavors           flavors
          :variant-category  variant-category
          :*controls-by-type @*controls-by-type})

        (let [class-to-remove (get active-controls-by-type variant-category)
              class-to-add    step]
          (doseq [el els]
            #_(js/console.log "\n" (.-innerText el) (str " \"" (.-classList el) "\""))
            #_(when class-to-remove
              (js/console.log "need to remove class: " class-to-remove)
              (js/console.log "remove classes: " flavors))
            #_(when class-to-add
              (js/console.log "adding class: " class-to-add))
            (when (and class-to-add class-to-remove)
              (apply dom/remove-class (concat [el] flavors))
              (dom/add-class el class-to-add))

          (swap! *demostate
                 assoc-in
                 [:active-controls-by-type variant-category]
                 step)

          (swap! *state
                 assoc-in
                 [:demo component-id :active-controls-by-type variant-category]
                 step)

            )))))
