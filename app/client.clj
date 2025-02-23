(ns _ (:import [java.net ServerSocket Socket])
    (:require ["../interpreter/interpreter" :as i]
              ["../effects/effects" :as e]
              ["../socket/socket" :as st]))

(defn reset_fx [a value]
  (e/thunk :reset_fx nil (fn [] (reset! a value) nil)))

(defn main_loop [env_atom server]
  (e/then
   (st/read server)
   (fn [[^"byte[]" input_bytes sockt]]
     (let [lexems (vec (.split (String. input_bytes) "\\n"))
           [result env] (i/eval (deref env_atom) lexems)]
       (e/batch
        [(reset_fx env_atom env)
         (st/write sockt (.getBytes (str result)) {:close true})
         (main_loop env_atom server)])))))

(defn main [env env_atom]
  (let [server_socket (atom nil)]
    (.start
     (Thread.
      (fn []
        (reset! server_socket (ServerSocket. 8090))
        ((main_loop env_atom (deref server_socket)) env))))
    (fn []
      (.close (as (deref server_socket) ServerSocket))
      nil)))
