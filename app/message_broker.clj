(ns _)

(defn make [] {:handlers {}})

(defn register [broker key handler]
  (assoc broker :handlers
         (assoc (:handlers broker) key handler)))

(defn dispatch [broker key data]
  (println "FIXME:dispatch:" key data)
  (let [h (get (:handlers broker) key)]
    (if (some? h)
      (h data))))
