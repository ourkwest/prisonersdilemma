(ns prisoners.pre-amble
  (:require [reagent.core :as reagent :refer [atom]]))


(def state (atom [nil nil]))

(defn dim [class-1 class-2]
  ;(js/alert classes)
  (doseq [el (array-seq (.getElementsByClassName js/document "dimmable"))]
    (if (or (.contains (.-classList el) class-1)
            (.contains (.-classList el) class-2))
      (.setAttribute el "style" "fill:rgba(0,0,0,0.75)")
      (.setAttribute el "style" "fill:rgba(0,0,0,0.1)"))))


(defn pre-amble []
  [:div [:h1 "The Prisoner's Dilemma"]

   [:svg {:style    {:width  "50%"
                     :height "50%"}
          :view-box "-1 -1 61.5 61.5"}

    (let [[x0 x1 x2 x3 x4] [0 10 20 40 60]
          [y0 y1 y2 y3 y4] [0 10 20 40 60]
          color-a "yellow"
          color-b "rgb(255,150,255)"
          color-c "rgb(100, 250, 100)"
          color-d "rgb(250, 100, 100)"

          coop-a-fg "green"
          coop-a-bg "yellow"
          betray-a-fg "red"
          betray-a-bg "yellow"
          coop-b-fg "green"
          coop-b-bg "rgb(255,150,255)"
          betray-b-fg "red"
          betray-b-bg "rgb(255,150,255)"

          coop-a-fg "black"
          coop-a-bg "yellow"
          betray-a-fg "black"
          betray-a-bg "yellow"
          coop-b-fg "black"
          coop-b-bg "rgb(255,150,255)"
          betray-b-fg "black"
          betray-b-bg "rgb(255,150,255)"

          ]

      [:g

       [:rect {:x     x2 :y y0 :width (- x4 x2) :height (- y1 y0)
               :style {:stroke "white"
                       :fill color-a}}]
       [:rect {:x     x2 :y y1 :width (- x3 x2) :height (- y2 y1)
               :style {:stroke "white"
                       :fill color-c}}]
       [:rect {:x     x3 :y y1 :width (- x4 x3) :height (- y2 y1)
               :style {:stroke "white"
                       :fill color-d}}]

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
               :style {:stroke "white" 
                       :fill color-b}}]
       [:rect {:x     x1 :y y2 :width (- x2 x1) :height (- y3 y2)
               :style {:stroke "white" 
                       :fill color-c}}]
       [:rect {:x     x1 :y y3 :width (- x2 x1) :height (- y4 y3)
               :style {:stroke "white" 
                       :fill color-d}}]

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

       (for [[xa xb ya yb as bs a-bg a-fg b-bg b-fg] 
             [[x2 x3 y2 y3 3 3 coop-a-bg coop-a-fg coop-b-bg coop-b-fg] [x3 x4 y2 y3 5 0 betray-a-bg betray-a-fg coop-b-bg coop-b-fg]
              [x2 x3 y3 y4 0 5 coop-a-bg coop-a-fg betray-b-bg betray-b-fg] [x3 x4 y3 y4 1 1 betray-a-bg betray-a-fg betray-b-bg betray-b-fg]]]
         [:g {:key (str xa ya)}
          [:polygon {:points (str xa \, ya \space
                                  xb \, yb \space
                                  xb \, ya)
                     :fill   a-bg
                     :stroke :white}]
          [:polygon {:points (str xa \, ya \space
                                  xb \, yb \space
                                  xa \, yb)
                     :fill   b-bg
                     :stroke :white}]
          [:text {:x           (+ xa 12)
                  :y           (- yb 10)
                  :font-family "Verdana"
                  :font-size   "10"
                  :fill a-fg} as]
          [:text {:x           (+ xa 2)
                  :y           (- yb 2)
                  :font-family "Verdana"
                  :font-size   "10"
                  :fill b-fg} bs]])

       [:rect {:class "dimmable a-coop"
               :x     x2
               :y y1
               :width (- x3 x2)
               :height (- y2 y1)
               :style {:fill "rgba(0,0,0,0.1)"}}]
       [:rect {:class "dimmable a-betray"
               :x     x3
               :y y1
               :width (- x4 x3)
               :height (- y2 y1)
               :style {:fill "rgba(0,0,0,0.1)"}}]

       [:rect {:class "dimmable b-coop"
               :x     x1
               :y y2
               :width (- x2 x1)
               :height (- y3 y2)
               :style {:fill color-c}}]
       [:rect {:class "dimmable b-betray"
               :x     x1
               :y y3
               :width (- x2 x1)
               :height (- y4 y3)
               :style {:fill color-d}}]


       [:rect {:class  "dimmable a-coop b-coop"
               :x x2 :y y2 :width (- x3 x2) :height (- y3 y2)
               :style {:fill "rgba(0,0,0,0.1)"}
               :on-mouse-over #(dim "a-betray" "b-betray")}]
       [:rect {:class "dimmable a-betray b-coop"
               :x x3 :y y2 :width (- x4 x3) :height (- y3 y2)
               :style {:fill "rgba(0,0,0,0.1)"}
               :on-mouse-over #(dim "a-coop" "b-betray")}]
       [:rect {:class  "dimmable a-coop b-betray"
               :x x2 :y y3 :width (- x3 x2) :height (- y4 y3)
               :style {:fill "rgba(0,0,0,0.1)"}
               :on-mouse-over #(dim "a-betray" "b-coop")}]
       [:rect {:class  "dimmable a-betray b-betray"
               :x x3 :y y3 :width (- x4 x3) :height (- y4 y3)
               :style {:fill "rgba(0,0,0,0.1)"}
               :on-mouse-over #(dim "a-coop" "b-coop")}]

       ])]])