(ns _ (:import [java.util.function Function]))

(defn make [] {})

(defn register [event_store event ^Function listener]
  (assoc
   event_store
   event
   (conj (get3 event_store event []) listener)))

(defn dispatch [event_store event data]
  (run!
   (fn [listener] (listener data))
   (get3 event_store event [])))
