(ns prisoners.core
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as string]
            [prisoners.world :as world]
            [prisoners.dilemma :as dilemma]
            [prisoners.strategies :as strategies]
            [prisoners.pre-amble :as pre-amble]
            [prisoners.iterated :as iterated]))

(enable-console-print!)

(defonce app-state (atom {}))

(defn new-state [] {:counter 0
                    :running false
                    :world   (world/new-world)})

(def debug (atom 0))

(declare animate)

(defn toggle-running []
  (swap! app-state update :running not)
  (animate))

(defn hello-world []
  [:div
   [:h1 "The Territorial Prisoner's Dilemma"]

   [:div
    [:span {:style {:margin 10}} (str "Iteration: " (:counter @app-state))]
    [:input {:type     "button"
             :value    (if (:running @app-state) "Pause" "Play")
             :on-click toggle-running
             :style    {:margin 10}}]]

   [:svg {:style    {:width  "40%"
                     :height "50%"}
          :view-box (string/join " " [0 0 world/world-w world/world-h])}
    (let [{:keys [min-score max-score nodes inter]} (:world @app-state)
          score-range (- max-score min-score)]
      (into [:g]
            (concat (for [[i1 i2] inter]
                      (let [n1 (get nodes i1)
                            n2 (get nodes i2)]
                        (if (= (:team n1) (:team n2))
                          [:line {:x1    (+ 0.5 (:x n1))
                                  :y1    (+ 0.5 (:y n1))
                                  :x2    (+ 0.5 (max (:x n1) (:x n2)))
                                  :y2    (+ 0.5 (max (:y n1) (:y n2)))
                                  :style {:stroke (:color n1)
                                          "strokeWidth" 0.5}}])))
                    [[:rect {:x 0 :y 0 :width 100 :height 100 :style {:fill "rgba(0,0,0,0.75)"}}]]
                    (for [{:keys [x y score color]} nodes]
                      (let [size (/ (- score min-score) score-range)
                            inset (/ (- 1 size) 2)]
                        [:rect {:x        (+ x inset)
                                :y        (+ y inset)
                                :width    size
                                :height   size
                                :style    {"fill" color}
                                :on-click #(reset! debug (world/find-index [x y]))}])))))
    [:div
     "Scores:"
     (let [max-score (apply max (vals (:scores (:world @app-state))))]
       (for [[team score] (sort (:scores (:world @app-state)))]

         (let [[label color] (strategies/strategies team)]
           [:div {:key   label
                  :style {:background-color "rgb(50,50,50)"
                          :width            "100%"}}
            [:div {:style {:background-color color
                           :color            "black"
                           :width            (str (/ score max-score 0.01) "%")}}
             label]])))]]

   ;[:div "Debug:" (-> @app-state :world :nodes (get @debug) str)
   ; (doall (for [[i1 i2 h] (-> @app-state :world :inter) :when (or (= i1 @debug)
   ;                                                                 (= i2 @debug))]
   ;           (let [nodes (-> @app-state :world :nodes)
   ;                 n1 (get nodes i1)
   ;                 n2 (get nodes i2)
   ;                 [label1] (strategies/strategies (:team n1))
   ;                 [label2] (strategies/strategies (:team n2))]
   ;           [:div {:key (str i1 "." i2)} label1 " vs. " label2 ": " (str h)])))]
   ])

(defn tick! [app-state]
  (-> app-state
      (update :counter inc)
      (dilemma/play)))

(defn animate []
  (when (:running @app-state)
    (swap! app-state tick!)
    (.requestAnimationFrame js/window animate)))

(defn on-js-reload []
  (println "Reloaded...")
  (reset! app-state (new-state))
  (reagent/render-component [pre-amble/pre-amble] (. js/document (getElementById "pre-amble")))
  (reagent/render-component [iterated/matrix-view] (. js/document (getElementById "matrix-view")))
  (reagent/render-component [hello-world] (. js/document (getElementById "app")))

  )

(defn init []
  (on-js-reload))

(defonce start (init))
