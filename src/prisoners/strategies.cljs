(ns prisoners.strategies)


(defn rgb [r g b]
  (str "rgb(" r "," g "," b ")"))

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
                                            :co-op))]])

(def strategies-by-label
  (into {} (for [[label color factory] strategy-list]
             [label [label color factory]])))

; TODO: pavlov - whatever is working best at the moment?
; or some strategy that can exploit an overly generous neighbour
;  tit for tat, unless neighbour is too nice?