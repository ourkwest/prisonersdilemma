(ns prisoners.core
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as string]
            [prisoners.world :as world]
            [prisoners.dilemma :as dilemma]
            [prisoners.strategies :as strategies]
            ))

(enable-console-print!)

(println "Hello Console!")


(defonce app-state (atom {}))
;
(defn new-state [] {:counter 0
                    :world   (world/new-world)})
;
(defn hello-world []
  [:div
   [:h1 (str ">>> " (:counter @app-state) " - " (:loser (:world @app-state)))]

   [:svg {:style    {:border "1px solid black"
                     :width  "70%"
                     :height "50%"}
          :view-box (string/join " " [0 0 world/world-w world/world-h])}

    (let [{:keys [min max nodes]} (:world @app-state)
          score-range (- max min)]
      (into [:g] (for [{:keys [x y score team]} nodes]
                   (let [color (second (team strategies/strategies))
                         size (/ (- score min) score-range)]
                     [:rect {:x      x
                             :y      y
                             :width  size
                             :height size
                             :style  {"fill" color}}]))))]])

;(reagent/render-component [hello-world] (. js/document (getElementById "app")))


;(defn on-js-reload []
  ; optionally touch your app-state to force rerendering depending on
  ; your application
  ; (swap! app-state update-in [:__figwheel_counter] inc)
;)



(defn tick! []
  (swap! app-state update :counter inc)
  ;(println "Ticking... 1__" (system-time))
  (swap! app-state dilemma/play)
  ;(println "Ticking... __3" (system-time))
  ;(println (update-in @app-state [:world :inter] (fn [x] (map #(take 2 %) x))))
  )
;
;;(js/setInterval tick! 1500)
;
;
(defn on-js-reload []
  (println "Reloaded...")
  (reset! app-state (new-state))
  (reagent/render-component [hello-world] (. js/document (getElementById "app"))))

(defn animate []
  (tick!)
  (.requestAnimationFrame js/window animate))

(defn init []
  (on-js-reload)
  ;(.addEventListener js/document "keydown" handle-keydown)
  ;(js/setInterval tick! 500)
  (animate)
  )

(defonce start
         (init))