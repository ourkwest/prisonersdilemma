(ns prisoners.strategies.random)


(defn play [thistory thatory]
  (if (<= 0.5 (rand))
    :co-op
    :betray))

(def entry [:random [play "rgb(200,150,50)" "Random 0.5"]])
