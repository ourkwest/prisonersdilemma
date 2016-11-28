(ns prisoners.strategies)

(def payoffs {:co-op {:co-op [3 3]
                      :betray [0 5]}
              :betray {:co-op  [5 0]
                       :betray [1 1]}})

(defn rgb [r g b]
  (str "rgb(" r "," g "," b ")"))

(defn pavlova [last-score history memory]
  (loop [[move & moves] history
         betrays 2.25
         co-ops 2.25
         factor 1
         limit 10]
    (println ">" betrays ">" co-ops \> factor \> last-score \> (* last-score factor))
    (cond
      (= limit 0) [betrays co-ops]
      (= move :co-op) (recur moves betrays (+ co-ops (* (- last-score 2.25) factor)) (* factor memory) (dec limit))
      (= move :betray) (recur moves (+ betrays (* (- last-score 2.25) factor)) co-ops (* factor memory) (dec limit))
      :else [betrays co-ops])))

(defn fade [x y f]
  (* (+ x y) f))


(defn make-pavlov-factory [fade-rate memory]
  #(let [co-ops (volatile! 0)
        betrays (volatile! 0)]
    (fn [_ h1 _ h2]
      (when-let [[last-score _] (get-in payoffs [(first h1) (first h2)])]
        (let [[delta-b delta-c] (pavlova last-score h1 memory)]
          (vswap! co-ops fade delta-c fade-rate)
          (vswap! betrays fade delta-b fade-rate)))
      (println @co-ops @betrays)
      (if (>= @co-ops @betrays)
        :co-op
        :betray))))

(def strategy-list
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
                                            :co-op))]
   ;["Tit For Two Tats" (rgb 204 51 255) #(let [switch (volatile! true)]
   ;                                       (fn [_ _ _ _]
   ;                                         (if (vswap! switch not)
   ;                                           :betray
   ;                                           :co-op)))]

   ;["Pavlov 0.9" (rgb 0 50 255) (make-pavlov-factory 0.9 0.9)]
   ;["Pavlov 0.75" (rgb 0 50 200) (make-pavlov-factory 0.9 0.75)]
   ;["Pavlov 0.8" (rgb 255 105 180) (make-pavlov-factory 0.9 0.8)]
   ;["Pavlov 0.5" (rgb 0 50 100) (make-pavlov-factory 0.9 0.5)]

   ["Cheeky Monkey" (rgb 0 255 255) #(fn [_ _ _ h2]
                                       (if (apply = (cons :co-op h2))
                                         :betray
                                         (rand-nth (cons :co-op h2))))]

   ;["Alternator" (rgb 255 255 255) (fn []
   ;                                  (let [x (volatile! true)]
   ;                                    (fn [_ h1 _ h2]
   ;                                      (if (or (= h1 h2) (vswap! x not))
   ;                                        :co-op
   ;                                        :betray))))]


   ])

(def strategies-by-label
  (into {} (for [[label color factory] strategy-list]
             [label [label color factory]])))

; TODO: pavlov - whatever is working best at the moment?
; or some strategy that can exploit an overly generous neighbour
;  tit for tat, unless neighbour is too nice?