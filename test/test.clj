(ns _ (:import [org.junit Test]
               [org.junit.runner RunWith]
               [org.robolectric RobolectricTestRunner])
    (:require ["../app/main" :as app]))

(gen-class :name Tests
           :annotations ["RunWith(RobolectricTestRunner.class)"]
           :methods [[^Test test [] void]])

(defn- assert_app [expected]
  (let [actual_atom (atom [])
        world {:chat_ui:row (fn []
                              [[:row {} (atom [])] nil])
               :chat_ui:button (fn [props]
                                 [[:button props] nil])
               :chat_ui:add (fn [target child]
                              (let [[_ _ children] target]
                                (swap! children (fn [xs] (conj xs child)))
                                [nil nil]))
               :chat_ui:update (fn [v] (swap! actual_atom (fn [xs] (conj xs [:update v])))
                                 [nil nil])}]
    ((app/main) world)
    (if (not= (str expected) (str (deref actual_atom)))
      (FIXME "Test failed\nExpected: " expected "\nActual: " (deref actual_atom)))))

(defn- _test [_]
  (assert_app [[:update [:row {}
                         [[:button {:onclick :lambda :title :QR}]
                          [:button {:onclick :lambda :title :Settings}]]]]]))
