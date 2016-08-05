(ns prisoners.iterated
  (:require [reagent.core :refer [atom]]
            [prisoners.strategies :as strategies]))



;; TODO:
;;   tick boxes to add new pre-defined strategies?
;;   small editor to view source for them / edit them?
;;   strategy should be a closure over a function to allow per instance state
;;   all selected strategies should be played against each other (100 rounds?) in a grid showing the final scores
;;   user can select the numebr of rounds.

(def strategies (atom []))

(defn play-games []
  )

(def alphabetic-strategies (sort-by (comp last last) strategies/strategies))

(defn matrix-view []
  [:div

   [:h1 "The Iterated Prisoner's Dilemma"]

   [:div

    [:table

     [:tr
      (cons [:td {:key "empty-header"}]
            (for [[_ [_ color-2 label-2]] alphabetic-strategies]
              [:td {:key    (str "header-" label-2)
                    :style {:background-color color-2
                            :color            :black
                            :height           "1em"}} ""]
              ))]

     (for [[_ [_ color-1 label-1]] alphabetic-strategies]
       [:tr {:key (str "tr-" label-1)
             :style {:background-color color-1 :color :black}}
        (cons [:td {:key (str "td-" label-1)} label-1]
              (for [[_ [_ color-2 label-2]] alphabetic-strategies]
                (condp #(%1 0 %2) (compare label-1 label-2)
                  < [:td {:key (str "td-" label-2 label-1) :style {:background-color color-1}} "score here"]
                  = [:td {:key (str "td-" label-2 label-1) :style {:background-color :black}}]
                  > [:td {:key (str "td-" label-2 label-1) :style {:background-color color-2}} "score here"]
                  )
                ))])]]

   [:span "TODO: finish this bit!"]

   ])


