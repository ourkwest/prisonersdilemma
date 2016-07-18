(ns prisoners.strategies
  (:require [prisoners.strategies.random :as random]))



;(def moves [:co-op :betray])

(def payoffs {:co-op {:co-op [2 2]
                      :betray [0 3]}
              :betray {:co-op  [3 0]
                       :betray [1 1]}})

(def strategies
  {:random random/play})