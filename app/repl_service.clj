(ns _ (:import [android.content Intent]
               [android.util Base64]
               [java.util Arrays]))

(def code_atom (atom ["(" "def*" "user/main" "(" "fn*" "(" ")" "(" "vector" ")" ")" ")"]))

(defn get_code []
  (deref code_atom))

(defn update [^Intent intent]
  (if (.hasExtra intent "code")
    (let [enc_code (.getString (.getExtras intent) "code")
          code (String. (Base64/decode enc_code 0))]
      (reset! code_atom (Arrays/asList (.split code "\n")))
      nil)))
