(ns prisoners.world
  (:require [prisoners.strategies :as strategies]))


(def world-h 25)
(def world-w 35)

(defn random-team []
  (rand-nth (keys strategies/strategies)))

(defn new-node [x y]
  {:x x :y y :score 0 :team (random-team)})

(defn new-nodes []
  (vec (for [x (range world-w) y (range world-h)]
         (new-node x y))))

(defn half-neighbours [x y]
  [[x (mod (inc y) world-h)]
   [(mod (inc x) world-w) y]])

(defn all-neighbours [x y]
  (for [dx (range -1 2) dy (range -1 2) :when (not= dx dy 0)]
    [(mod (+ x dx) world-w) (mod (+ y dy) world-h)]))

(defn find-index [[x y]]
  (+ (* x world-h) y))

(defn find-xy [index]
  [(Math/round (/ index world-h)) (mod index world-h)])

(defn new-inter []
  (vec (for [x (range world-w)
             y (range world-h)
             [nx ny] (half-neighbours x y)]
         (let [index (find-index [x y])
               nindex (find-index [nx ny])]
           [index nindex []]))))

(defn new-world []
  {:max 100
   :min 0
   :nodes (new-nodes)
   :inter (new-inter)})
