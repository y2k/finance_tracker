(ns _ (:import [android.net Uri]))

(defn decode_url [url]
  (let [uri (Uri/parse (as url String))]
    {:params (reduce
              (fn [acc k]
                (assoc acc k (.getQueryParameter uri (as k String))))
              {}
              (.getQueryParameterNames uri))}))
