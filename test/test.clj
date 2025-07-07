(ns _ (:import [org.junit Test]
               [org.junit.runner RunWith]
               [org.robolectric RobolectricTestRunner])
    (:require ["./domain" :as app]))

(gen-class :name Tests
           :annotations ["RunWith(RobolectricTestRunner.class)"]
           :methods [[^Test test_home [] void]
                     [^Test test_gallery [] void]])

(defn- assert_fx [expected fx]
  (let [actual_atom (atom [])
        world {:chat_ui:row (fn []
                              [[:row {} (atom [])] nil])
               :chat_ui:button (fn [props]
                                 [[:button props] nil])
               :chat_ui:add (fn [{target :parent child :child}]
                              (let [[_ _ children] target]
                                (swap! children (fn [xs] (conj xs child)))
                                [nil nil]))
               :chat_ui:update (fn [v]
                                 (swap! actual_atom (fn [xs] (conj xs [:update v])))
                                 [nil nil])
               :android_qr:recognize (fn [props]
                                       (swap! actual_atom (fn [xs] (conj xs [:recognize props])))
                                       [nil nil])}]

    (fx world)
    (if (not= (str expected) (str (deref actual_atom)))
      (FIXME "Test failed\nExpected: " expected "\nActual: " (deref actual_atom)))))

(defn- _test_home [_]
  (assert_fx
   [[:update
     [:row {}
      [[:button {:onclick :lambda1 :title :QR}]
       [:button {:onclick :lambda1 :title :Settings}]]]]]
   (app/main {:name :home})))

(defn- _test_gallery [_]
  (assert_fx
   [[:recognize {:url "some_url" :props {:callback :lambda1}}]]
   (app/main {:name :gallery :props {:url "some_url"}})))
