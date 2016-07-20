(ns prisoners.dilemma
  (:require [prisoners.strategies :as strategies]
            [prisoners.world :as world]))


(defn add-history [history m1 m2]
  (take 10 (conj history m1 m2)))


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
        [ds1 ds2] (get-in strategies/payoffs [move1 move2])]

    ;(when (= i 0)
    ;  (println "Sample:" t1 "vs" t2 " -> " move1 "vs" move2 " -> " ds1 " vs " ds2)
    ;
    ;  )

    (-> world
        (update-in [:nodes i1 :score] + ds1)
        (update-in [:nodes i2 :score] + ds2)
        (update-in [:inter i 2] add-history move1 move2))))

(defn find-limits [{:keys [nodes] :as world}]
  (let [scores (map :score nodes)
        new-min (apply min scores)
        new-max (apply max scores)
        swap (.indexOf scores new-min)]
    (-> world
        (assoc :min new-min :max new-max :swap swap)
        (assoc-in [:nodes swap :team] (world/random-team))
        (assoc-in [:nodes swap :score] (/ (+ new-min new-max) 2)))))

(defn play [{{:keys [inter]} :world :as state}]
  (let [value (-> state
                  (update :world #(reduce rplay % (map-indexed cons inter)))
                  (update :world find-limits))]
    ;(println "Ticking... _2_" (system-time))
    value))
