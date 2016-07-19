(ns prisoners.world
  (:require [prisoners.strategies :as strategies]))


(def world-h 30)
(def world-w 50)

(defn random-team []
  (rand-nth (keys strategies/strategies)))

(defn new-node []
  {:team (random-team)
   :score 0
   :history []})

(defn new-world []
  (into [] (for [_ (range world-w)]
             (into [] (for [_ (range world-h)]
                        (new-node))))))
