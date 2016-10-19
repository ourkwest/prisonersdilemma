(ns prisoners.iterated
  (:require [reagent.core :refer [atom]]
            [prisoners.strategies :as strategies]
            [prisoners.dilemma :as dilemma]
            [cljs.tools.reader :refer [read-string]]
            [cljs.js :refer [empty-state eval js-eval]]))



;; TODO:
;;   tick boxes to add new pre-defined strategies?
;;   small editor to view source for them / edit them?
;;   strategy should be a closure over a function to allow per instance state
;;   all selected strategies should be played against each other (100 rounds?) in a grid showing the final scores
;;   user can select the numebr of rounds.

(def nbsp "\u00A0")

(defn space [n-before text n-after]
  (apply str (concat (repeat n-before nbsp) [text] (repeat n-after nbsp))))

(def state (atom {:touch           0
                  :iteration-count 100
                  :strategies      (select-keys strategies/strategies-by-label
                                                ["Always Betray" "Always Co-operate"])}))

(defn remove-strategy [team-label]
  (swap! state update :strategies dissoc team-label))


(defn eval-str [s]
  (:value
    (eval (empty-state)
          (read-string s)
          {:eval       js-eval
           :source-map true
           :context    :expr}
          identity)))

(defn add-strategy []
  (let [code (.-value (. js/document (getElementById "incoming-code")))]
    (println code)
    ;(println (read-string code))
    (let [my-function (eval-str code)]
      (println my-function)
      (println (my-function 5))
      )))

(defn add-selected-stategy []
  (let [label (.-value (. js/document (getElementById "select-strategy")))]
    (println label)
    (let [strategy (strategies/strategies-by-label label)]
      (swap! state update :strategies assoc label strategy))

    ;(println (read-string code))
    ;(let [my-function (eval-str code)]
    ;  (println my-function)
    ;  (println (my-function 5))
    ;  )
    ))

(defn re-run []
  (swap! state update :touch inc))

(defn play-iterations [team-1 team-2 function-1 function-2 n]
  (loop [history-1 []
         history-2 []
         score-1 0
         iterations n]
    (if (= 0 iterations)
      score-1
      (let [move-1 (function-1 team-1 history-1 team-2 history-2)
            move-2 (function-2 team-2 history-2 team-1 history-1)
            new-history-1 (cons move-1 history-1)
            new-history-2 (cons move-2 history-2)
            this-score-1 (get-in dilemma/payoffs [move-1 move-2 0])
            new-score-1 (+ score-1 this-score-1)]
        (recur new-history-1 new-history-2 new-score-1 (dec iterations))))))

(defn play-games [strategies iteration-count]
  (into {}
        (for [[_ [label-1 _ make-strategy-1]] strategies]
          [label-1 (into {}
                         (for [[_ [label-2 _ make-strategy-2]] strategies]
                           [label-2 (play-iterations label-1 label-2
                                                     (make-strategy-1) (make-strategy-2)
                                                     iteration-count)]))])))

(defn matrix-view []

  (let [strategies (:strategies @state)
        iteration-count (:iteration-count @state)
        games (play-games strategies iteration-count)
        totals (into {} (for [[team-1 m] games]
                          [team-1 (reduce + (vals m))]))
        max-score-per-game (* 5 iteration-count)
        exageration-factor 0.95
        smallest-total (* (apply min (vals totals)) exageration-factor)
        largest-total (apply max (vals totals))
        total-range (- largest-total smallest-total)
        ]

    [:div

     [:h1 "The Iterated Prisoner's Dilemma"]

     [:div

      [:table {:style {:border-collapse :collapse}}

       [:thead

        [:tr
         (concat [[:td {:key "empty-header-1"}
                   [:input {:type :button
                            :value "Re-run"
                            :on-click re-run}]]
                  [:td {:key "empty-header-2"}]]
                 (for [[_ [label-2 color-2]] strategies]
                   [:td {:key   (str "header-" label-2)
                         :style {:background-color color-2
                                 :color            :black
                                 :border-right     (str "1px solid " color-2)
                                 ;:border-radius "50px 50px 0px 0px"
                                 :height           "1em"}} ""])
                 [[:td {:key   "total-header"
                        :style {:background-color :black
                                :color            :white
                                :padding          5}} (space 5 "Total Score" 5)]])]]

       [:tbody
        (for [[_ [label-1 color-1]] strategies]
          [:tr {:key   (str "tr-" label-1)
                :style {:background-color color-1 :color :black}}
           (concat [[:td {:key (str "td-button-" label-1)
                          :style {:border-bottom (str "1px solid " color-1)}}
                     [:input {:type     "button"
                              :value    "Remove"
                              :on-click #(remove-strategy label-1)
                              :style    {:margin 10}}]]
                    [:td {:key   (str "td-" label-1)
                          :style {:border-bottom (str "1px solid " color-1)}} label-1]]
                   (for [[_ [label-2 color-2]] strategies]
                     (let [score-1 (get-in games [label-1 label-2])
                           relative-score (/ score-1 max-score-per-game)
                           scaled-score (Math/round (* 512 relative-score))
                           color (str "rgb("
                                      (min 255 (- 512 scaled-score)) \,
                                      (min 255 scaled-score) \,
                                      0 \))]
                       [:td {:key   (str "td-" label-2 label-1)
                             :style {:background-color :black
                                     :border-bottom    (str "1px solid " color-1)
                                     :border-right     (str "1px solid " color-2)}}
                        [:div {:style {:background-color color
                                       :padding          5
                                       :margin           5
                                       :text-align       :right
                                       :border-radius    15 ;
                                       }}
                         score-1]]))

                   (let [total (get totals label-1)
                         relative-total (Math/round (/ (- total smallest-total) total-range 0.01))]
                     [[:td {:key   (str "total-" label-1)
                            :style {:border-bottom    (str "1px solid " color-1)
                                    :background-color :black}}
                       [:div {:style {:position :relative
                                      :width    "100%"
                                      :height   "2em"}}
                        [:div {:style {:position         :absolute
                                       :width            (str relative-total "%")
                                       :height           "100%"
                                       :background-color color-1}}
                         " "]]]])
                   )])]]]

     [:span "TODO: finish this bit!"]


     ;; drop down menu with list of strategies?

     ;; drop down to add directly! (and also pre-fill boxes?)

     ;; select strategy
     ;;   fills boxes



     [:div

      "Add: "
      [:select {:id "select-strategy" :on-change add-selected-stategy}
       (for [[label] strategies/strategies-by-label]
         [:option {:key label :value label} label])]


      [:br] [:br]

      [:textarea {:id "incoming-name"
                  :value "Random Pick"}]

      [:input {:id "incoming-color" :type :color}]

      [:textarea {:id "incoming-colour"
                  :value "200,250,200"}]
      [:textarea {:id "incoming-code"
                  :value "(fn [x] (inc x))"}]

      [:input {:type     "button"
               :value    "Add"
               :on-click add-strategy
               :style    {:margin 10}}]
      ;[:textarea {}
       ;"(defn foo [x] (println x))"
       ;]
      ]

     ]))


