(ns client (:import [java.net ServerSocket URL HttpURLConnection])
    (:require ["../interpreter/interpreter" :as i]))

(def env_atom (atom (i/make_env {})))

(defn- loop [condition body]
  (if (= true (condition))
    (do
      (body)
      (loop condition body))))

;; execute : String -> String
(defn start [execute]
  (recover
   (fn [] (let [server (ServerSocket. 8080)]
            (loop (fn [] (not (Thread/interrupted)))
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
                  nil)))))
   (fn [] nil)))
