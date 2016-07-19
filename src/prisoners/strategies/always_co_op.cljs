(ns prisoners.strategies.always-co-op)

(defn play [thistory thatory] :co-op)

(def entry [:always-co-op [play "rgb(0,250,0)" "Always Co-operate"]])
