(ns test (:require ["../../app/domain" :as d]))

(defn- test_main [_]
  (let [expected ["<div class='group'><button onclick=\\\"Android.dispatch('qr_clicked', '')\\\">QR</button><button onclick=\\\"Android.dispatch('test_sqlite_clicked', '')\\\">Test sqlite</button></div>"]
        effect (d/home)
        actual_atom (atom [])]
    (effect {:println (fn [x] (swap! actual_atom (fn [xs] (conj xs x))) nil)
             :decode_qr (fn [x] (swap! actual_atom (fn [xs] (conj xs x))) nil)})
    (if (not= expected (deref actual_atom))
      (FIXME "Test failed\nExpected: " expected "\nActual: " (deref actual_atom)))))

(test_main nil)
