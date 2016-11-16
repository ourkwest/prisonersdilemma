(ns prisoners.dilemma
  (:require [prisoners.strategies :as strategies]
            [prisoners.world :as world]))


(def payoffs {:co-op {:co-op [3 3]
                      :betray [0 5]}
              :betray {:co-op  [5 0]
                       :betray [1 1]}})

(defn add-history [history m1 m2]
  (vec (take 10 (cons [m1 m2] history))))

(defn pick-team [nodes index]
  (let [[x y] (world/find-xy index)
        teams (map (comp :team
                         #(get nodes %)
                         world/find-index)
                   (world/all-neighbours x y))
        options (concat teams teams teams teams teams (keys strategies/strategies-by-label))]
    (rand-nth options)))

(defn find-limits [{:keys [nodes] :as world}]
  (let [scores (map :score nodes)
        new-min (apply min scores)
        new-max (apply max scores)
        swap (.indexOf scores new-min)]
    (-> world
        (assoc :min-score new-min :max-score new-max :swap swap)
        (assoc-in [:nodes swap :score] (Math/round (+ new-min 100)))

        (update-in [:nodes swap] #(world/add-team % (pick-team nodes swap)))

        ;(assoc-in [:nodes swap :team] (pick-team nodes swap))

        )))

;TODO: put strategy factories on nodes, but strategies themselves on the interactions/relationships
;TODO: create a new strategy from the factory when a new relationship is forged.

(defn score-teams [{:keys [nodes] :as world}]
  (assoc world :scores
               (reduce #(update %1 (:team %2) (fnil + 0) (:score %2)) {} nodes)))

(defn add-noise [move]
  (let [r (rand)]
    (cond
      (> r 0.95) :betray
      (< r 0.05) :co-op
      :else move)))

(defn pose-dilemmas [{:keys [nodes inter] :as world}]
  (let [t-nodes (transient nodes)
        t-inter (transient inter)]
    (doseq [i (range (count t-inter))]
      (let [[i1 i2 pl1 pl2 history] (nth t-inter i)
            node1 (nth t-nodes i1)
            node2 (nth t-nodes i2)
            t1 (:team node1)
            t2 (:team node2)
            s1 (:score node1)
            s2 (:score node2)
            fn1 (get-in node1 [:players pl1])
            fn2 (get-in node2 [:players pl2])
            h1 (map first history)
            h2 (map second history)
            move1 (add-noise (fn1 t1 h1 t2 h2 i))
            move2 (add-noise (fn2 t2 h2 t1 h1 i))
            [ds1 ds2] (get-in payoffs [move1 move2])]
        (assoc! t-nodes i1 (assoc node1 :score (+ s1 ds1)))
        (assoc! t-nodes i2 (assoc node2 :score (+ s2 ds2)))
        (assoc! t-inter i [i1 i2 pl1 pl2 (add-history history move1 move2)])))
    (-> world
        (assoc :nodes (persistent! t-nodes))
        (assoc :inter (persistent! t-inter)))))

(defn play [state]
  (-> state
      (update :world pose-dilemmas)
      (update :world find-limits)
      (update :world score-teams)))
