(ns _ (:import [org.junit Test]
               [java.net Socket InetSocketAddress]
               [java.nio ByteBuffer])
    (:require ["../app/domain" :as d]
              ["../nrepl/nrepl" :as nrepl]
              ["../interpreter/interpreter" :as i]))

(gen-class :name Tests :extends Object :constructors {[] []} :prefix "_" :methods
           [[^Test test [] void]
            [^Test eval_by_socket_executed [] void]])

(defn- _test [_]
  (let [expected ["<div class='group'><button onclick=\\\"Android.dispatch('qr_clicked', '')\\\">QR</button><button onclick=\\\"Android.dispatch('test_sqlite_clicked', '')\\\">Test sqlite</button></div>"]
        effect (d/home)
        actual_atom (atom [])]
    (effect {:println (fn [x] (swap! actual_atom (fn [xs] (conj xs x))) nil)
             :decode_qr (fn [x] (swap! actual_atom (fn [xs] (conj xs x))) nil)})
    (if (not= expected (deref actual_atom))
      (FIXME "Test failed\nExpected: " expected "\nActual: " (deref actual_atom)))))

(defn- _eval_by_socket_executed [_]
  (checked!
   (let [close_server (nrepl/main (fn [e l] (i/eval {:interpreter:save (fn [_ _] nil)} e l))
                                  (atom (i/make_env {}))
                                  {:port 8090})
         socket (Socket.)]
     (.connect socket (InetSocketAddress. "localhost" 8090) 1000)
     (let [out (.getOutputStream socket)
           in (.getInputStream socket)
           data (.getBytes "(\n+\n2\n2\n)")
           len_buf (ByteBuffer/allocate 4)]
       (.putInt len_buf (.-length data))
       (.write out (.array len_buf))
       (.write out data)
       (let [actual (String. (.readAllBytes in))]
         (close_server)
         (if (not= "4" actual)
           (FIXME actual)))))))
