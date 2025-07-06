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
             [android.os Bundle]))

(gen-class
 :name MainActivity
 :extends Activity
 :prefix "activity_"
 :fields ["w_atom"]
 :init "init"
 :methods [[^Override onCreate [Bundle] void]
           [^Override onActivityResult [int int Intent] void]])

(defn- activity_init [^MainActivity self]
  (set! (.-w_atom self) (atom {})))

(defn- activity_onCreate [^MainActivity self ^Bundle bundle]
  (let [root (cast View (ui/root_ self))
        w_atom (.-w_atom self)]
    (.setContentView self root)
    (ui/add_effect_handlers self root w_atom)
    (qr/attach_effect_handler self w_atom)
    (swap! w_atom (fn [w] (ag/attach_effect_handler self w)))
    ((d/main) (deref w_atom))))

(defn- activity_onActivityResult [^MainActivity self ^int requestCode ^int resultCode ^Intent data]
  (let [uri (ag/on_activity_result self requestCode resultCode data)]
    ((d/get_image_callback uri) (deref (.-w_atom self)))))
