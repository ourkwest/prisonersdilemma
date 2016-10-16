(ns prisoners.strategies)


(defn rgb [r g b]
  (str "rgb(" r "," g "," b ")"))

(def strategies
  [["Always Betray" (rgb 255 0 0) #(fn [& _] :betray)]
   ["Always Co-operate" (rgb 0 255 0) #(fn [& _] :co-op)]

   ["Random 50/50" (rgb 150 150 50) #(fn [& _]
                                  (if (<= 0.5 (rand))
                                    :co-op
                                    :betray))]
   ["Random Like You +" (rgb 255 250 0) #(fn [_ _ _ h2]
                                      (rand-nth (cons :co-op h2)))]
   ["Random Like You -" (rgb 255 200 50) #(fn [_ _ _ h2]
                                      (rand-nth (cons :betray h2)))]

   ["Tit For Tat" (rgb 200 150 250) #(fn [_ _ _ h2]
                                  (or (first h2) :co-op))]
   ["Two Tits For Tat" (rgb 150 100 255) #(fn [_ _ _ h2]
                                       (if (or (= (first h2) :betray)
                                               (= (second h2) :betray))
                                         :betray
                                         :co-op))]
   ["Tit For Two Tats" (rgb 204 51 255) #(fn [_ _ _ h2]
                                      (if (and (= (first h2) :betray)
                                               (= (second h2) :betray))
                                        :betray
                                        :co-op))]])

(def strategy-by-index
  (into {} (map-indexed vector strategies)))

(defn partisan [t1 _ t2 _ _]
  (if (= t1 t2)
    :co-op
    :betray))

(defn probabilistic [_ [m1] _ [m2] _]
  (let [p1 (/ 11 13)
        p2 (/ 1 2)
        p3 (/ 7 26)
        p4 0
        p (get-in {:co-op {:co-op p1
                           :betray p2}
                   :betray {:co-op p3
                            :betray p4}} [m1 m2])]
    (if (< p (rand))
      :co-op
      :betray)))

;(def payoffs {:co-op {:co-op [3 3]
;                      :betray [0 5]}
;              :betray {:co-op  [5 0]
;                       :betray [1 1]}})

;(def memory (atom {:relationship, }))
;(defn up [x] (if x (* 1.01 x) nil))
;
;(defn pavlov [_ [m1] _ [m2] i]
;
;  ;; need to create a new one closing over state each time
;  ;; should we keep the scores in the passed history as well?
;
;  ;; track score for each relationship, and weight options accordingly.
;
;  (condp = [m1 m2]
;    [:co-op :co-op] (swap! memory update-in [i :betray] up)
;    [:co-op :betray] (swap! memory update-in [i :betray] up)
;    [:betray :co-op] (swap! memory update-in [i :betray] up)
;    [:betray :betray] (swap! memory update-in [i :co-op] up)
;    ;;?????
;    )
;
;  (get-in payoffs [(first h1) (first h2)])
;
;  )

;(def strategies
;  {
;   :always-co-op          [always-co-op "rgb(0,250,0)" "Always Co-operate"]
;   ;:always-betray         [always-betray "rgb(250,0,0)" "Always Betray"]
;   ;:random50              [random50 "rgb(200,150,50)" "Random 0.5"]
;   :random-like-you       [random-like-you "rgb(250,250,0)" "Random Like You +"]
;   :random-like-you-nasty [random-like-you-nasty "rgb(250,128,0)" "Random Like You -"]
;   ;:tit-for-tat           [tit-for-tat "rgb(200,150,250)" "Tit For Tat"]
;   ;:tit-for-2-tats        [tit-for-2-tats "rgb(204,51,255)" "Tit For Two Tats"]
;   :two-tits-for-tats        [two-tits-for-tat "rgb(150,100,255)" "Two Tits For Tat"]
;   ;:partisan              [partisan "rgb(100,100,250)" "Partisan 1"]
;   ;:partisan2             [partisan "rgb(150,150,250)" "Partisan 2"]
;   ;:partisan3             [partisan "rgb(200,200,250)" "Partisan 3"]
;   ;:probabilistic            [probabilistic "rgb(250,150,150)" "Probabilistic"]
;   }
;  ;(into {} [random/entry
;  ;          always-co-op/entry
;  ;          always-betray/entry])
;  )

;(def teams (keys strategies))


;(defn make-always-betray []
;  ["Always Betray" [255 0 0] (fn [_ _ _ _] :betray)])
;
;
;(def always-betray ["Always Betray" [255 0 0] (fn [] (fn [& _] :betray))])
;
;(def template
;  "(fn []
;  [\"Example\" [0 255 150]
;   (fn [team-1 history-1 team-2 history-2]
;     (if (<= 0.5 (rand))
;       :co-op
;       :betray))]")
