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

        move1 ((strategies/strategies t1) h1 h2)
        move2 ((strategies/strategies t2) h2 h1)
        [ds1 ds2] (get-in strategies/payoffs [move1 move2])]

    ;(println [x1 y1] h1 t1 s1 move1 ds1)
    ;(println [x2 y2] h2 t2 s2 move2 ds2)

    (-> board
        (assoc-in [x1 y1] {:history (take 10 (cons move1 h1))
                           :team    t1
                           :score   (+ s1 ds1)})
        (assoc-in [x2 y2] {:history (take 10 (cons move2 h2))
                           :team    t2
                           :score   (+ s2 ds2)}))
    )

  ;(let [{h1 :history t1 :team s1 :score} (get-in board [x1 y1])
  ;      {h2 :history t2 :team s2 :score} (get-in board [x2 y2])
  ;      move1 (t1 h1 h2)
  ;      move2 (t2 h2 h1)
  ;      [ds1 ds2] (get-in strategies/payoffs [move1 move2])]
  ;  (-> board
  ;      (assoc-in [x1 y1] {:history (take 10 (cons move1 h1))
  ;                         :team    t1
  ;                         :score   (+ s1 ds1)})
  ;      (assoc-in [x2 y2] {:history (take 10 (cons move2 h2))
  ;                         :team    t2
  ;                         :score   (+ s2 ds2)})))

  ;board
  )

(defn play-one-square [board [x y]]
  (reduce play-one board (neighbours x y))
  ;board
  )

(defn find-max [[biggest board] [x y]]
  [(max biggest (get-in board [x y :score])) board])

(defn play [{:keys [board] :as world}]
  ;(println "playing...")
  (let [new-board (reduce play-one-square board (for [x (range world/world-w) y (range world/world-h)] [x y]))
        new-max (first (reduce find-max [0 new-board] (for [x (range world/world-w) y (range world/world-h)] [x y])))]
    (assoc world :board new-board :max new-max)))
