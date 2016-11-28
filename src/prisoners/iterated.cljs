(ns prisoners.iterated
  (:require [reagent.core :refer [atom]]
            [prisoners.strategies :as strategies]
            [prisoners.dilemma :as dilemma]
            [cljs.tools.reader :refer [read-string]]
            [cljs.js :refer [empty-state eval js-eval]]))


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
  (let [label (.-value (. js/document (getElementById "incoming-label")))
        color (.-value (. js/document (getElementById "incoming-color")))
        code (.-value (. js/document (getElementById "incoming-code")))
        factory (eval-str code)]
    (swap! state update :strategies assoc label [label color factory])))

(defn add-selected-stategy []
  (let [label (.-value (. js/document (getElementById "select-strategy")))]
    (println label)
    (let [strategy (strategies/strategies-by-label label)]
      (swap! state update :strategies assoc label strategy))))

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
                           scaled-score (Math/round (* 200 relative-score))
                           color (str "rgb("

                                      (+ 55 scaled-score) \,
                                      (+ 55 scaled-score) \,
                                      (+ 55 scaled-score) \)

                                      ;(min 255 (- 512 scaled-score)) \,
                                      ;(min 255 scaled-score) \,
                                      ;0 \)
                                      )]
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

     [:div

      "Add: "
      [:select {:id "select-strategy" :on-change add-selected-stategy}
       (for [[label] strategies/strategies-by-label]
         [:option {:key label :value label} label])]


      [:br]

      [:h2 "The Result"]
      [:ul
       [:li "The best strategy depends on your opponent."]
       [:li "Strategies that reciprocate do well. This is analagous to the '" [:span {:style {:color "rgb(255,200,0)"}} "Golden Rule"] "'."]]

      ;TODO: preset selections of strategies for talk
      ; demonstrate the effectiveness of a cooperating strategy
      ; demenstrate the lack of a universally best strategy

      ;[:div {:style {:border        "1px solid white"
      ;               :border-radius "10px"
      ;               :margin "5px"
      ;               :padding "5px"}}
      ;
      ; " Label: " [:input {:id    "incoming-label"
      ;                   :type  "text"
      ;                   :default-value "My First Strategy"}]
      ; " Color: " [:input {:id "incoming-color"
      ;                     :type :color
      ;                     :default-value (str "#" (.toString (rand-int 16rFFFFFF) 16))}]
      ; [:br]
      ;
      ; [:textarea {:id    "incoming-code"
      ;             :cols 80
      ;             :rows 20
      ;             :default-value "#(fn [team-1 history-1 team-2 history-2]\n  (rand-nth (cons :co-op history-2)))"}]
      ; [:br]
      ;
      ; [:input {:type     "button"
      ;          :value    "Add"
      ;          :on-click add-strategy
      ;          :style    {:margin 10}}]]
      ]]))


