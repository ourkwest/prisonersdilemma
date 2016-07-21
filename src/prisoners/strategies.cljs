(ns prisoners.strategies)


(defn always-betray [_ _] :betray)

(defn always-co-op [_ _] :co-op)

(defn random50 [_ _]
  (if (<= 0.5 (rand))
    :co-op
    :betray))

(defn tit-for-tat [_ h2]
  (or (first h2) :co-op))

(defn random-like-you [_ h2]
  (rand-nth (cons :co-op h2)))

(defn random-like-you-nasty [_ h2]
  (rand-nth (cons :betray h2)))

(def strategies
  {:always-co-op    [always-co-op "rgb(0,250,0)" "Always Co-operate"]
   :always-betray   [always-betray "rgb(250,0,0)" "Always Betray"]
   :random50        [random50 "rgb(200,150,50)" "Random 0.5"]
   :random-like-you [random-like-you "rgb(200,0,250)" "Random Like You +"]
   :random-like-you-nasty [random-like-you-nasty "rgb(250,0,200)" "Random Like You -"]
   :tit-for-tat     [tit-for-tat "rgb(200,150,250)" "Tit For Tat"]}
  ;(into {} [random/entry
  ;          always-co-op/entry
  ;          always-betray/entry])
  )

(def teams (keys strategies))
