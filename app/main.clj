(ns _ (:require ["../interpreter/interpreter" :as i]
                ["./repl_service" :as repl]
                ["./event_store" :as es]
                ["./database" :as db]
                ["./message_broker" :as mb]
                ["./webview" :as wv]
                ["./domain" :as d])
    (:import [android.app Activity]
             [android.content Intent]
             [android.net Uri]
             [android.os Bundle]
             [android.webkit WebView WebChromeClient ValueCallback JavascriptInterface]
             [java.util.function Function]
             [com.google.gson Gson]))

(gen-class
 :name MainActivity
 :extends Activity
 :constructors {[] []}
 :prefix "activity_"
 :methods [[^Override onCreate [Bundle] void]
           [^Override onActivityResult [int int Intent] void]
           [^Override onNewIntent [Intent] void]])

(defn activity_onCreate [^MainActivity self ^Bundle bundle]
  (let [broker_atom (atom (mb/make))
        dispatch (fn [event payload]
                   (mb/dispatch (deref broker_atom) event payload))
        register (fn [key handler]
                   (swap! broker_atom (fn [broker] (mb/register broker key handler))))
        webview (wv/make_webview self dispatch register)]
    (.setContentView self webview)

    (db/main register dispatch)

    (d/main register dispatch)
    (repl/main register dispatch)

    ;; (repl/live_reload_code env_atom self (.getIntent self))
    nil))

(defn activity_onNewIntent [^MainActivity self ^Intent intent]
  (repl/live_reload_code self intent))

(defn activity_onActivityResult [^MainActivity self requestCode ^int resultCode ^Intent data]
  (wv/on_receive_value resultCode data))
