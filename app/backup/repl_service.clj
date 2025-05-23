(ns _ (:import [android.content Intent]
               [android.app Activity]
               [android.util Base64]
               [java.util Arrays]
               [com.google.gson Gson])
    (:require ["../interpreter/interpreter" :as i]))

(defn- update [^Intent intent env]
  (if (.hasExtra intent "code")
    (let [enc_code (.getString (.getExtras intent) "code")
          code (String. (Base64/decode enc_code 0))
          code_lines (Arrays/asList (.split code "\n"))]
      (->
       (i/eval {} env code_lines)
       second))
    env))

(def env_atom (atom (i/make_env {})))

(defn live_reload_code [^Activity context ^Intent intent]
  (recover
   (fn []
     (let [env (update intent (deref env_atom))]
       (reset! env_atom env)
       (let [f (deref (get (:ns env) "domain/home"))
             w (:world env)
             fx (f [])]
         (fx [w]))
       nil))
   (fn [e]
     (.printStackTrace (as e Exception))
     (.show (android.widget.Toast/makeText context (.toString e) 1))
     nil)))

(defn main [register dispatch]
  (let [w {:println (fn [[x]] (dispatch :println x))
           :decode_qr (fn [[x]] (dispatch :decode_qr x))}]

    (reset! env_atom
            (assoc
             (i/make_env {:ext/to-json (fn [[x]] (.toJson (Gson.) x))
                          :ext/dispatch (fn [[event payload]] (dispatch event payload))})
             :world w))

    (let [reg_event (fn [event]
                      (register event
                                (fn [payload]
                                  (let [env (deref env_atom)
                                        fname (str "domain/" event)
                                        fatom (get (:ns env) fname)]
                                    (if (some? fatom)
                                      (let [f (deref fatom)
                                            fx (f [payload])]
                                        (fx [w])))))))]

      (reg_event :home)
      (reg_event :qr_clicked)
      (reg_event :file_selected)
      (reg_event :qr_recognized))))
