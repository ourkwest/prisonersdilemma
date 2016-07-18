(ns prisoners.world)



(def world-h 50)
(def world-w 50)
;
;
(defn new-world []

  (into [] (for [x (range world-w)]
             (into [] (for [y (range world-h)]
                        {:team :random
                         :score (rand)
                         :history []}))))

  ;(into [] (repeat 100 (into [] (repeat 100 {:team :random :score (rand)}))))

  )


