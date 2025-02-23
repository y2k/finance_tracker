(ns _
  (:import [org.junit Test])
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
  (let [log (atom [])
        actual (atom nil)
        w {:thunk (fn [[x payload] _]
                    (reset! log (conj (deref log) {:fx x :data payload}))
                    (case x
                      :read_from_server_socket [[(.getBytes "(\n+\n2\n2\n)") nil] nil]
                      :reset_fx [nil nil]
                      :write (do
                               (reset! actual (String. (as (:data payload) "byte[]")))
                               [nil 1])
                      (FIXME x ", " payload)))}
        env_atom (atom (i/make_env {}))]
    ((c/main_loop env_atom nil) w))
  (if (not= "4" (deref actual))
    (FIXME actual)))
