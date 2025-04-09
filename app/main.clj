(ns main
  (:require ["../chat_ui/chat_ui" :as ui]
            ["../effects/effects" :as e]
            ["../android_gallery/android_gallery" :as ag]
            ["../android_qr/android_qr" :as qr])
  (:import [android.app Activity]
           [android.content Intent]
           [android.net Uri]
           [android.os Bundle]
           [java.io File]))

(defn- main_ui []
  [:row {}
   [:button {:title "QR"
             :onclick (ag/get_image)}]
   [:button {:title "Settings"
             :onclick (ui/update_ui [:button {:title "TEST 2"
                                              :onclick nil}])}]])

(defn main []
  (ui/update_ui (main_ui)))

(def- w_atom (atom {}))

(defn- execute_fx [fx] (fx (deref w_atom)))

(defn main_event [uri]
  (qr/decode_qr
   uri
   {:callback (fn [[x]]
                (execute_fx (ui/update_ui [:label {:text (str x)}])))}))

(defn- activity_onCreate [^MainActivity self ^Bundle bundle]
  (let [root (ui/root_ self)]
    (.setContentView self root)
    (ui/add_effect_handlers self root w_atom)
    (swap! w_atom (fn [w] (ag/attach_effect_handler self w)))
    (swap! w_atom (fn [w] (qr/attach_effect_handler self w)))
    (execute_fx (main))))

(defn- activity_onActivityResult [^MainActivity self ^int requestCode ^int resultCode ^Intent data]
  (let [uri (ag/on_activity_result self requestCode resultCode data)]
    (execute_fx (main_event uri))))

(gen-class
 :name MainActivity
 :extends Activity
 :constructors {[] []}
 :prefix "activity_"
 :methods [[^Override onCreate [Bundle] void]
           [^Override onActivityResult [int int Intent] void]])
