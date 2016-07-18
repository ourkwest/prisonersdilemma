(ns prisoners.core
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as string]
            [prisoners.world :as world]
            [prisoners.dilemma :as dilemma]
            ))

(enable-console-print!)

(println "Hello Console!")


(defonce app-state (atom {}))
;
(defn new-state [] {:text "Hello Bananas!" :counter 1 :max 1
                    :board (world/new-world)})
;
(defn hello-world []
  [:div
   [:h1 (:text @app-state) (str ">>>" (:counter @app-state) " - " (get-in @app-state [:board 1 1]))]

   [:svg {:style    {:border "1px solid black"
                     :width  "90%"
                     :height "90%"}
          :view-box (string/join " " [0 0 world/world-w world/world-h])}

    (into [:g] (for [x (range world/world-w) y (range world/world-h)]
               (let [score (:score (get-in @app-state [:board x y]))
                     size (/ score (:max @app-state))]
                 [:rect {:x x :y y :width size :height size
                         ;:key (str "node-" x "-" y)
                         }])

               ))]]

  )

;(reagent/render-component [hello-world] (. js/document (getElementById "app")))


;(defn on-js-reload []
  ; optionally touch your app-state to force rerendering depending on
  ; your application
  ; (swap! app-state update-in [:__figwheel_counter] inc)
;)



(defn tick! []
  (println "Ticking...")
  (swap! app-state update :counter inc)
  (swap! app-state dilemma/play)
  )
;
;;(js/setInterval tick! 1500)
;
;
(defn on-js-reload []
  (println "Reloaded...")
  (reset! app-state (new-state))
  (reagent/render-component [hello-world] (. js/document (getElementById "app"))))

(defn init []
  (on-js-reload)
  ;(.addEventListener js/document "keydown" handle-keydown)
  (js/setInterval tick! 100)
  )

(defonce start
         (init))