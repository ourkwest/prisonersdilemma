(ns prisoners.dilemma
  (:require [prisoners.strategies :as strategies]
            [prisoners.world :as world]))



(defn neighbours [x y]
  (for [dx (range -1 2) dy (range -1 2) :when (not= dx dy 0)]
    [[x y] [(mod (+ x dx) world/world-w) (mod (+ y dy) world/world-h)]]))

(defn play-one [board [[x1 y1] [x2 y2]]]

  ;(when (and (= x1 0)
  ;           (= y1 0)
  ;           (= x2 0)
  ;           (= y2 1))
  ;  (println "PLAYING..."))

  (let [{h1 :history t1 :team s1 :score} (get-in board [x1 y1])
        {h2 :history t2 :team s2 :score} (get-in board [x2 y2])

        ;_ (println t1 (strategies/strategies t1))
        ;_ (println t2 (strategies/strategies t2))

        fn1 (first (strategies/strategies t1))
        fn2 (first (strategies/strategies t2))

        move1 (fn1 h1 h2)
        move2 (fn2 h2 h1)
        [ds1 ds2] (get-in strategies/payoffs [move1 move2])]

    ;(println [x1 y1] h1 t1 s1 move1 ds1)
    ;(println [x2 y2] h2 t2 s2 move2 ds2)

    (-> board
        (assoc-in [x1 y1] {:history (take 10 (cons move1 h1))
                           :team    t1
                           :score   (+ s1 ds1 -1)})
        (assoc-in [x2 y2] {:history (take 10 (cons move2 h2))
                           :team    t2
                           :score   (+ s2 ds2 -1)}))
    )
  )

(defn play-one-square [board [x y]]
  (reduce play-one board (neighbours x y))
  ;board
  )

(defn find-limits [[lo hi loser board] [x y]]
  (let [score (get-in board [x y :score])
        new-lo (min lo score)
        new-hi (max hi score)
        new-loser (if (= new-lo score) [x y] loser)]
    [new-lo new-hi new-loser board]))

(defn play [{:keys [board max min] :as world}]
  ;(println "playing...")
  (let [new-board (reduce play-one-square board (for [x (range world/world-w) y (range world/world-h)] [x y]))
        [lo hi loser _] (reduce find-limits [max min [0 0] new-board] (for [x (range world/world-w) y (range world/world-h)] [x y]))
        new-board (assoc-in new-board loser (world/new-node))]
    (println "replaced " loser)
    (assoc world :board new-board :max hi :min lo)))
