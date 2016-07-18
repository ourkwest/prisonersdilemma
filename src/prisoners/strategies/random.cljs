(ns prisoners.strategies.random)


(defn play [thistory thatory]
  (if (<= 0.5 (rand))
    :co-op
    :betray))
