(ns main
  (:require ["../chat_ui/chat_ui" :as ui]
            ["../effects/effects" :as e])
  (:import [android.app Activity]
           [android.content Intent]
           [android.net Uri]
           [android.os Bundle]
           [android.webkit WebView WebChromeClient ValueCallback JavascriptInterface]
           [java.util.function Function]
           [com.google.gson Gson]
           [java.io File]))

(defn- main_ui []
  [:row {}
   [:button {:title "QR"
             :onclick (ui/update_ui [:button {:title "TEST 1"
                                              :onclick nil}])}]
   [:button {:title "Settings"
             :onclick (ui/update_ui [:button {:title "TEST 2"
                                              :onclick nil}])}]])

(defn main []
  (ui/update_ui (main_ui)))

(defn- activity_onCreate [^MainActivity self ^Bundle bundle]
  (let [root (ui/root_ self)]
    (.setContentView self root)
    (let [w_atom (ui/add_effect_handlers self root (atom {}))]
      ((main) (deref w_atom)))))

(gen-class
 :name MainActivity
 :extends Activity
 :constructors {[] []}
 :prefix "activity_"
 :methods [[^Override onCreate [Bundle] void]])
