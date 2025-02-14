(ns _ (:import [java.net ServerSocket URL HttpURLConnection])
    (:require ["../interpreter/interpreter" :as i]
              ["./effect" :as e]))

(def env_atom (atom (i/make_env {})))

(defn- loop [condition body]
  (if (= true (condition))
    (do
      (body)
      (loop condition body))))

(defn dispatch [event payload]
  (fn [w] ((:dispatch w) {:event event :payload payload})))

(defn register [event handler]
  (fn [w] ((:register w) {:event event :handler handler})))

;; execute : String -> String
(defn- start [server execute]
  (recover
   (fn [] (loop (fn [] (not (Thread/interrupted)))
            (fn []
              (let [socket (.accept server)
                    in (.getInputStream socket)
                    code (String. (.readAllBytes in))
                    result (recover
                            (fn [] (execute code))
                            (fn [] "error"))
                    out (.getOutputStream socket)]
                (.write out (.getBytes (as result String)))
                (.close socket)
                nil))))
   (fn [] nil)))

(defn main []
  (let [server (ServerSocket. 8080)
        t (Thread. (fn [] (start server (FIXME))))]
    (e/batch
     [(fn [_] (.start t))
      (register :dispose (fn [_] (fn [_] (.close server) (.interrupt t) (.join t))))])))
