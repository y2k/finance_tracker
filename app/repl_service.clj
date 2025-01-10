(ns _ (:import [android.content Intent]
               [android.app Activity]
               [android.util Base64]
               [java.util Arrays])
    (:require ["../interpreter/interpreter" :as i]))

(defn- update [^Intent intent env]
  (if (.hasExtra intent "code")
    (let [enc_code (.getString (.getExtras intent) "code")
          code (String. (Base64/decode enc_code 0))
          code_lines (Arrays/asList (.split code "\n"))]
      (-> env
          (i/eval code_lines)
          second))
    env))

(defn live_reload_code [env_atom ^Activity context ^Intent intent]
  (recover
   (fn []
     (let [env (update intent (deref env_atom))]
       (reset! env_atom env)
       nil))
   (fn [e]
     (.printStackTrace (as e Exception))
     (.show (android.widget.Toast/makeText context (.toString e) 1))
     nil)))
