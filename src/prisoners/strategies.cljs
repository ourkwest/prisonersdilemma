(ns prisoners.strategies)


(defn always-betray [_ _ _ _] :betray)

(defn always-co-op [_ _ _ _] :co-op)

(defn random50 [_ _ _ _]
  (if (<= 0.5 (rand))
    :co-op
    :betray))

(defn tit-for-tat [_ _ _ h2]
  (or (first h2) :co-op))

(defn random-like-you [_ _ _ h2]
  (rand-nth (cons :co-op h2)))

(defn random-like-you-nasty [_ _ _ h2]
  (rand-nth (cons :betray h2)))

(defn xenophobic [t1 _ t2 _]
  (if (= t1 t2)
    :co-op
    :betray))

(defn probabilistic [_ [m1] _ [m2]]
  (let [p1 (/ 11 13)
        p2 (/ 1 2)
        p3 (/ 7 26)
        p4 0
        p (get-in {:co-op {:co-op p1
                           :betray p2}
                   :betray {:co-op p3
                            :betray p4}} [m1 m2])]
    (if (> p (rand))
      :co-op
      :betray)))

(def strategies
  {:always-co-op          [always-co-op "rgb(0,250,0)" "Always Co-operate"]
   :always-betray         [always-betray "rgb(250,0,0)" "Always Betray"]
   :random50              [random50 "rgb(200,150,50)" "Random 0.5"]
   :random-like-you       [random-like-you "rgb(200,0,250)" "Random Like You +"]
   :random-like-you-nasty [random-like-you-nasty "rgb(250,0,200)" "Random Like You -"]
   :tit-for-tat           [tit-for-tat "rgb(200,150,250)" "Tit For Tat"]
   ;:xenophobic            [xenophobic "rgb(50,50,250)" "Xenophobic 1"]
   ;:xenophobic2            [xenophobic "rgb(0,0,150)" "Xenophobic 2"]
   ;:xenophobic3            [xenophobic "rgb(150,150,250)" "Xenophobic 3"]
   :probabilistic            [probabilistic "rgb(250,150,150)" "Probabilistic"]
   }
  ;(into {} [random/entry
  ;          always-co-op/entry
  ;          always-betray/entry])
  )

(def teams (keys strategies))
