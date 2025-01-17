(ns effect)

(defn pure [x] (fn [_] x))

(defn then [effect f]
  (fn [w]
    (let [r (effect w)
          effect2 (f r)]
      (effect2 w))))

(defn batch [effects]
  (if (empty? effects)
    (pure [])
    (then
     (first effects)
     (fn [hr]
       (then
        (batch (rest effects))
        (fn [tr]
          (pure (concat [hr] tr))))))))
