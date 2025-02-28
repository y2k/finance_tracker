(ns _ (:import [java.net ServerSocket Socket])
    (:require ["../interpreter/interpreter" :as i]))

(defn make_env []
  (atom (i/make_env {})))

(comment
  (defn foo [a b] (+ a b))
  (foo 1 2)

  (+ 2 2)

  comment)

(defn- main_loop [env_atom ^ServerSocket server]
  (checked!
   (let [^Socket socket (recover (fn [] (.accept server)) (fn [] nil))]
     (if (some? socket)
       (let [in (.getInputStream socket)
             input_bytes (.readAllBytes in)
             lexems (vec (.split (String. input_bytes) "\\n"))
             [result env] (i/eval (deref env_atom) lexems)
             out (.getOutputStream socket)]
         (reset! env_atom env)
         (.write out (.getBytes (str result)))
         (.flush out)
         (.shutdownOutput socket)
         (.close out)
         (.close socket)
         (main_loop env_atom server))))))

(defn main [^int port env_atom]
  (let [server_socket (atom nil)]
    (.start
     (Thread.
      (fn []
        (reset! server_socket (ServerSocket. port))
        (main_loop env_atom (as (deref server_socket) ServerSocket)))))
    (fn []
      (.close (as (deref server_socket) ServerSocket))
      nil)))
