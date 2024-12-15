(ns _ (:import [java.io BufferedOutputStream BufferedReader InputStreamReader OutputStream]
               [java.net ServerSocket Socket]
               [android.content Context]
               [java.lang Thread]))

(def code_atom (atom ""))

;; (defn get_code [] (.split (as (deref code_atom) "String") "\n"))
(defn get_code []
  (->
   (deref code_atom)
   (as String)
   (.split "\n")
   vec))

(defn- read_all [^BufferedReader reader]
  (let [line (checked! (.readLine reader))]
    (if (nil? line)
      ""
      (str line "\n" (read_all reader)))))

(defn load_init [^Context context]
  (let [input (-> (.getAssets context) (.open "shared/user.bytecode") checked!)
        result (-> input InputStreamReader. BufferedReader. read_all checked!)]
    (checked! (do (.close input) unit))
    (reset! code_atom result)
    unit))

(defn start []
  (let [stop_atom (atom false)
        thread (Thread. (runnable
                         (fn []
                           (checked!
                            (let [server_socket (ServerSocket. 8787)
                                  client_socket (.accept server_socket)
                                  reader (BufferedReader. (InputStreamReader. (.getInputStream client_socket)))
                                  client_output (BufferedOutputStream. (.getOutputStream client_socket))
                                  request (second (.split (as (read_all reader) String) "\n\n" 2))]
                              (.write client_output (.getBytes "HTTP/1.1 200 OK\n\n"))
                              (.flush client_output)
                              (.close client_socket)
                              (reset! code_atom request)
                              unit)))))]
    (.start thread)
    (runnable (fn []
                (reset! stop_atom true)
                unit))))
