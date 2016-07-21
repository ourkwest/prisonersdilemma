(ns prisoners.dilemma
  (:require [prisoners.strategies :as strategies]
            [prisoners.world :as world]))


(def payoffs {:co-op {:co-op [2 2]
                      :betray [0 3]}
              :betray {:co-op  [3 0]
                       :betray [1 1]}})

(defn add-history [history m1 m2]
  (take 10 (conj history [m1 m2])))

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
        [ds1 ds2] (get-in payoffs [move1 move2])]

    ;(when (= i 0)
    ;  (println "Sample:" t1 "vs" t2 " -> " move1 "vs" move2 " -> " ds1 " vs " ds2)
    ;
    ;  )

    (-> world
        (update-in [:nodes i1 :score] + ds1)
        (update-in [:nodes i2 :score] + ds2)
        (update-in [:inter i 2] add-history move1 move2))))

(defn pick-team [nodes index]
  (let [[x y] (world/find-xy index)
        teams (map (comp :team
                         #(get nodes %)
                         world/find-index)
                   (world/all-neighbours x y))
        options (into teams strategies/teams)
        option (rand-nth options)]

    ;(println "N1:" (world/all-neighbours x y))
    ;(println "N2:" (map world/find-index (world/all-neighbours x y)))
    ;(println "N3:" (map (comp #(get nodes %) world/find-index) (world/all-neighbours x y)))
    ;(println "N4:" (map (comp :team #(get nodes %) world/find-index) (world/all-neighbours x y)))
    ;(println "Picked" option "from" options)

    option))

(defn find-limits [{:keys [nodes] :as world}]
  (let [scores (map :score nodes)
        new-min (apply min scores)
        new-max (apply max scores)
        swap (.indexOf scores new-min)]
    ;(println new-min " --> " new-max)
    (-> world
        (assoc :min new-min :max new-max :swap swap)
        (assoc-in [:nodes swap :team] (pick-team nodes swap))
        (assoc-in [:nodes swap :score] (/ (+ new-min new-max) 2)))))

(defn score-teams [{:keys [nodes] :as world}]
  (assoc world :scores
               (reduce #(update %1 (:team %2) (fnil + 0) (:score %2)) {} nodes)))

(defn play [{{:keys [inter]} :world :as state}]
  (let [value (-> state
                  (update :world #(reduce rplay % (map-indexed cons inter)))
                  (update :world find-limits)
                  (update :world score-teams))]
    ;(println "Ticking... _2_" (system-time))
    value))
