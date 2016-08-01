(ns prisoners.pre-amble
  (:require [reagent.core :refer [atom]]))


(def state (atom [nil nil]))

(defn dim [a b]
  (reset! state [a b]))

(defn pre-amble []

  (let [[x0 x1 x2 x3 x4] [0 10 20 40 60]
        [y0 y1 y2 y3 y4] [0 10 20 40 60]
        color-a "rgb(255,255,0)"
        color-b "rgb(255,150,255)"
        color-c "rgb(100,250,100)"
        color-d "rgb(250,100,100)"
        lines "black"

        color-a-dim "rgb(75,75,0)"
        color-b-dim "rgb(75,30,75)"
        color-c-dim "rgb(20,75,20)"
        color-d-dim "rgb(75,20,20)"

        [a b] @state
        a-co-op (not= a "betray")
        a-betray (not= a "co-op")
        b-co-op (not= b "betray")
        b-betray (not= b "co-op")]

    [:div
     [:h1 "The Prisoner's Dilemma"]
     [:h2 "The Setup"]
     [:p
      "You and another person are arrested and given prison sentences. "
      "You are held in separate cells and cannot communicate with each other. "
      "The guard gives you both an ultimatum: "
      [:ul
       [:li [:span {:style {:color color-d}} "Betray"] " the other prisoner by giving evidence against them and you walk free."]
       [:li [:span {:style {:color color-c}} "Co-operate"] " with the other prisoner by keeping quiet and serving your current sentence."]]
      "But:"
      [:ul
       [:li "If you both betray each other then you both get a slightly worse sentence."]
       [:li "If you co-operate and the other prisoner betrays you then you will get the worst possible sentence."]]]

     [:h2 "The Payoffs"]

     [:svg {:style    {:width  "50%"
                       :height "50%"}
            :view-box "-1 -1 61.5 61.5"}

      [:g {:on-mouse-leave #(dim nil nil)}

       [:rect {:x     x2 :y y0 :width (- x4 x2) :height (- y1 y0)
               :style {:stroke lines
                       :fill color-a}}]
       [:rect {:x     x2 :y y1 :width (- x3 x2) :height (- y2 y1)
               :style {:stroke lines
                       :fill   (if a-co-op color-c color-c-dim)
                       }}]
       [:rect {:x     x3 :y y1 :width (- x4 x3) :height (- y2 y1)
               :style {:stroke lines
                       :fill (if a-betray color-d color-d-dim)}}]

       [:text {:x           (+ x2 5)
               :y           (- y1 2.5)
               :font-family "Verdana"
               :font-size   "7"
               :fill        "black"}
        "Player A"]
       [:text {:x           (+ x2 2.5)
               :y           (- y2 3.5)
               :font-family "Verdana"
               :font-size   "5"
               :fill        "black"}
        "Co-op"]
       [:text {:x           (+ x3 1.75)
               :y           (- y2 3.5)
               :font-family "Verdana"
               :font-size   "5"
               :fill        "black"}
        "Betray"]

       [:rect {:x     x0 :y y2 :width (- x1 x0) :height (- y4 y2)
               :style {:stroke lines
                       :fill color-b}}]
       [:rect {:x     x1 :y y2 :width (- x2 x1) :height (- y3 y2)
               :style {:stroke lines
                       :fill (if b-co-op color-c color-c-dim)}}]
       [:rect {:x     x1 :y y3 :width (- x2 x1) :height (- y4 y3)
               :style {:stroke lines
                       :fill (if b-betray color-d color-d-dim)}}]

       [:text {:x           (+ x1 5)
               :y           (- y4 2.5)
               :transform   (str "rotate(270 " x1 "," y4 ")")
               :font-family "Verdana"
               :font-size   "7"
               :fill        "black"}
        "Player B"]
       [:text {:x           (+ x2 2.5)
               :y           (- y3 3.5)
               :transform   (str "rotate(270 " x2 "," y3 ")")
               :font-family "Verdana"
               :font-size   "5"
               :fill        "black"}
        "Co-op"]
       [:text {:x           (+ x2 1.75)
               :y           (- y4 3.5)
               :transform   (str "rotate(270 " x2 "," y4 ")")
               :font-family "Verdana"
               :font-size   "5"
               :fill        "black"}
        "Betray"]

       (for [[xa xb ya yb as bs [a-bg b-bg] move-a move-b]
             [[x2 x3 y2 y3 3 3 (if (and a-co-op b-co-op) [color-a color-b] [color-a-dim color-b-dim]) "co-op" "co-op"]
              [x3 x4 y2 y3 5 0 (if (and a-betray b-co-op) [color-a color-b] [color-a-dim color-b-dim]) "betray" "co-op"]
              [x2 x3 y3 y4 0 5 (if (and a-co-op b-betray) [color-a color-b] [color-a-dim color-b-dim]) "co-op" "betray"]
              [x3 x4 y3 y4 1 1 (if (and a-betray b-betray) [color-a color-b] [color-a-dim color-b-dim]) "betray" "betray"]]]
         [:g {:key (str xa ya)}
          [:polygon {:points         (str xa \, ya \space
                                          xb \, yb \space
                                          xb \, ya)
                     :fill           a-bg
                     :stroke         lines
                     :on-mouse-enter #(dim move-a move-b)}]
          [:polygon {:points         (str xa \, ya \space
                                          xb \, yb \space
                                          xa \, yb)
                     :fill           b-bg
                     :stroke         lines
                     :on-mouse-enter #(dim move-a move-b)}]
          [:text {:x           (+ xa 12)
                  :y           (- yb 10)
                  :font-family "Verdana"
                  :font-size   "10"
                  :fill        "black"} as]
          [:text {:x           (+ xa 2)
                  :y           (- yb 2)
                  :font-family "Verdana"
                  :font-size   "10"
                  :fill        "black"} bs]])]]]))
