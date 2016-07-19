(ns prisoners.dilemma
  (:require [prisoners.strategies :as strategies]
            [prisoners.world :as world]))


(defn rplay [world [i i1 i2 history]]
  (let [nodes (:nodes world)
        node1 (nth nodes i1)
        node2 (nth nodes i2)
        t1 (:team node1)
        t2 (:team node2)
        fn1 (first (strategies/strategies t1))
        fn2 (first (strategies/strategies t2))
        h1 (map first history)
        h2 (map second history)
        move1 (fn1 h1 h2)
        move2 (fn2 h2 h1)
        [ds1 ds2] (get-in strategies/payoffs [move1 move2])
        s1 (+ (:score node1) ds1)
        s2 (+ (:score node2) ds2)
        loser (:loser world)
        new-loser (cond
                    (= s1 (:min world)) i1
                    (= s2 (:min world)) i2
                    :else loser)]
    (-> world
        (update-in [:nodes i1 :score] + ds1)
        (update-in [:nodes i2 :score] + ds2)
        (update-in [:inter i 2] conj [move1 move2])
        (update :max max s1 s2)
        (update :min min s1 s2)
        (assoc :loser new-loser)
        )))

(defn replace-loser [{:keys [loser min max] :as world}]
  (-> world
      (assoc-in [:nodes loser :team] (world/random-team))
      (assoc-in [:nodes loser :score] (/ (+ min max) 2))))

(defn play [{{:keys [min max inter] :as world} :world :as state}]
  (-> state
      (assoc :world
             (reduce rplay (assoc world :min max :max min) (map-indexed cons inter)))
      (update :world replace-loser)))