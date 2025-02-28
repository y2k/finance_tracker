(ns _ (:require ["./repl_service" :as repl]
                ["./database" :as db]
                ["./message_broker" :as mb]
                ["./webview" :as wv]
                ["./domain" :as d]
                ["./client" :as nrepl])
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
           [^Override onNewIntent [Intent] void]
           [^Override onResume [] void]
           [^Override onPause [] void]])

(def- state_atom (atom {:handlers []}))

(defn activity_onResume [^MainActivity self]
  (let [env_atom (nrepl/make_env)]
    (nrepl/main 8090 env_atom)))

(defn- activity_onPause [^MainActivity self]
  (run!
   (fn [[event handler]] (if (= event :dispose) ((handler nil) {})))
   (:handlers (deref state_atom))))

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

    nil))

(defn activity_onNewIntent [^MainActivity self ^Intent intent]
  (repl/live_reload_code self intent))

(defn activity_onActivityResult [^MainActivity self requestCode ^int resultCode ^Intent data]
  (wv/on_receive_value resultCode data))
