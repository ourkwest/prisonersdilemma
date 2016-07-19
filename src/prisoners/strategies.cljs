(ns prisoners.strategies
  (:require [prisoners.strategies.random :as random]
            [prisoners.strategies.always-co-op :as always-co-op]
            [prisoners.strategies.always-betray :as always-betray]))



;(def moves [:co-op :betray])

(def payoffs {:co-op {:co-op [2 2]
                      :betray [0 3]}
              :betray {:co-op  [3 0]
                       :betray [1 1]}})

(def strategies
  (into {} [random/entry
            always-co-op/entry
            always-betray/entry])
  ;{:random [random/play "rgb(200,150,50)"]}
  )

(println strategies)
