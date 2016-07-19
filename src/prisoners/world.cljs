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


;; 2222222222222222

(defn new-node-2 [x y]
  {:x x :y y :score 0 :team (random-team)})

(defn new-nodes []
  (vec (for [x (range world-w) y (range world-h)]
         (new-node-2 x y))))

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

(defn new-world-2 []
  {:max 100
   :min 0
   :loser 0
   :nodes (new-nodes)
   :inter (new-inter)})
