(ns _ (:import [org.junit Test]
               [java.net Socket InetSocketAddress])
    (:require ["../app/domain" :as d]
              ["../app/client" :as c]
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
   (let [close_server (c/main 8090 (atom (i/make_env {})))
         socket (Socket.)]
     (.connect socket (InetSocketAddress. "localhost" 8090) 1000)
     (let [out (.getOutputStream socket)
           in (.getInputStream socket)]
       (.write out (.getBytes "(\n+\n2\n2\n)"))
       (.shutdownOutput socket)
       (let [actual (String. (.readAllBytes in))]
         (close_server)
         (if (not= "4" actual)
           (FIXME actual)))))))
