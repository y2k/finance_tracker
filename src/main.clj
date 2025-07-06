(ns _ (:require
       ["./vendor/chat_ui/chat_ui" :as ui]
       ["./vendor/effects/effects" :as e]
       ["./vendor/android_gallery/android_gallery" :as ag]
       ["./vendor/android_qr/android_qr" :as qr]
       ["./vendor/android_db/android_db" :as db]
       ["./domain" :as d])
    (:import [android.app Activity]
             [android.content Intent]
             [android.view View]
             [android.net Uri]
             [android.os Bundle]
             [java.io File]))

(defn main []
  (ui/update_ui (d/main)))

(def- w_atom (atom {}))
(defn- execute_fx [fx] (fx (deref w_atom)))

(defn- activity_onCreate [^MainActivity self ^Bundle bundle]
  (let [root (cast View (ui/root_ self))]
    (.setContentView self root)
    (ui/add_effect_handlers self root w_atom)
    (qr/attach_effect_handler self w_atom)
    (swap! w_atom (fn [w] (ag/attach_effect_handler self w)))
    ;; (swap! w_atom (fn [w] (db/attach_effect_handler {:db ":memory:"} w)))
    (execute_fx (main))))

(defn- activity_onActivityResult [^MainActivity self ^int requestCode ^int resultCode ^Intent data]
  (let [uri (ag/on_activity_result self requestCode resultCode data)]
    (execute_fx (d/get_image_callback uri))))

;;

(gen-class
 :name MainActivity
 :extends Activity
 :prefix "activity_"
 :methods [[^Override onCreate [Bundle] void]
           [^Override onActivityResult [int int Intent] void]])
