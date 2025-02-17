(ns _
  (:import [org.junit Test])
  (:require ["../app/domain" :as d]
            ["../app/client" :as c]))

(gen-class :name Tests :extends Object :constructors {[] []} :prefix "test_" :methods
           [[^Test main [] void]
            [^Test nrepl [] void]])

(defn- test_main [_]
  (let [expected ["<div class='group'><button onclick=\\\"Android.dispatch('qr_clicked', '')\\\">QR</button><button onclick=\\\"Android.dispatch('test_sqlite_clicked', '')\\\">Test sqlite</button></div>"]
        effect (d/home)
        actual_atom (atom [])]
    (effect {:println (fn [x] (swap! actual_atom (fn [xs] (conj xs x))) nil)
             :decode_qr (fn [x] (swap! actual_atom (fn [xs] (conj xs x))) nil)})
    (if (not= expected (deref actual_atom))
      (FIXME "Test failed\nExpected: " expected "\nActual: " (deref actual_atom)))))

(defn- test_nrepl [_]
  (let [w {:register (fn [x] (FIXME "LOG1:" x))}]
    ((c/main) w)))
