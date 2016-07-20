(ns prisoners.world
  (:require [prisoners.strategies :as strategies]))


(def world-h 20)
(def world-w 30)

(defn random-team []
  (rand-nth (keys strategies/strategies)))

(defn new-node [x y]
  {:x x :y y :score 0 :team (random-team)})

(defn new-nodes []
  (vec (for [x (range world-w) y (range world-h)]
         (new-node x y))))

(defn neighbours [x y]
  (for [dx (range -1 2) dy (range -1 2) :when (not= dx dy 0)]
    [(mod (+ x dx) world-w) (mod (+ y dy) world-h)]))

(defn find-index [x y]
  (+ (* x world-h) y))

(defn new-inter []
  (vec (for [x (range world-w)
             y (range world-h)
             [nx ny] (neighbours x y)]
         (let [index (find-index x y)
               nindex (find-index nx ny)]
           [index nindex []]))))

(defn new-world []
  {:max 100
   :min 0
   :nodes (new-nodes)
   :inter (new-inter)})
