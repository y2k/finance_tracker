(ns _ (:import [java.net ServerSocket])
    (:require ["../interpreter/interpreter" :as i]
              ["./effect" :as e]))

(def env_atom (atom (i/make_env {})))

(defn- loop [condition body]
  (if (= true (condition))
    (do
      (body)
      (loop condition body))))

;; (defn dispatch [event payload]
;;   (fn [w] ((:dispatch w) {:event event :payload payload})))

(defn register [event handler]
  (fn [w] ((:register w) event handler)))

(defn thunk [description f]
  (fn [w] ((:thunk w) description f)))

;; execute : String -> String
(defn- start [^ServerSocket server execute]
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

;; (thunk :start_thread (fn [] (.start t) nil))

(defn th_start [t]
  (thunk :start_thread (fn [] (.start t) nil)))

(defn main []
  (let [^ServerSocket server (recover (fn [] (ServerSocket. 8090)) (fn [] (FIXME)))
        t (Thread. (fn [] (start server (FIXME))))]
    (e/batch
     [(th_start t)
      (register :dispose (fn [_]
                           (thunk :stop_server (fn [] (.close server) (.interrupt t) (.join t) nil))))])))
