(ns _ (:import [android.app Activity]
               [android.content Intent]
               [android.net Uri]
               [android.os Bundle]
               [android.webkit WebView WebChromeClient ValueCallback JavascriptInterface]
               [java.util.function Function]
               [com.google.gson Gson])
    (:require ["../interpreter/interpreter" :as i]
              ["./repl_service" :as repl]
              ["./event_store" :as es]
              ["./database" :as db]))

(gen-class
 :name MainActivity
 :extends Activity
 :constructors {[] []}
 :prefix "activity_"
 :methods [[^Override onCreate [Bundle] void]
           [^Override onActivityResult [int int Intent] void]
           [^Override onNewIntent [Intent] void]])

(defn- make_default_state []
  (atom (i/make_env {:ext/to-json (fn [[x]] (.toJson (Gson.) x))})))

(def env_atom (make_default_state))

(defn- live_reload_code [^Intent intent]
  (repl/update intent)
  (let [env (-> (deref env_atom) (i/eval (repl/get_code)) second)]
    (reset! env_atom env)
    nil))

(declare handle_event)

(defn dispatch [^WebView wv effects]
  (run!
   (fn [[fx data]]
     (db/invoke (fn [fx2 data2] (handle_event wv fx2 data2)) fx data)
     (.evaluateJavascript wv (str "WebView.dispatch(`" fx "`, `" (.toJson (Gson.) data) "`)") nil)
     nil)
   effects))

(defn activity_onCreate [^MainActivity self ^Bundle bundle]
  (db/init)
  (live_reload_code (.getIntent self))

  (let [webview (WebView. self)
        webSettings (.getSettings webview)]
    (.setContentView self webview)
    (.setDomStorageEnabled webSettings true)
    (.setJavaScriptEnabled webSettings true)
    (.setAllowFileAccess webSettings true)
    (.setAllowFileAccessFromFileURLs webSettings true)
    (.setAllowUniversalAccessFromFileURLs webSettings true)
    (.addJavascriptInterface webview (WebViewJsListener. self webview) "Android")
    (.setWebChromeClient webview (WebChromeClientImpl.))
    (.loadUrl webview "file:///android_asset/index.html")
    (swap! env_atom
           (fn [env]
             (assoc env :scope
                    (assoc (:scope env) :ext/dispatch
                           (fn [[fx data]] (dispatch webview [[fx data]]))))))
    unit))

(defn ^void activity_onNewIntent [^MainActivity self ^Intent intent]
  (live_reload_code intent))

(def- filePathCallbackRef (atom nil))

(defn activity_onActivityResult [^MainActivity self requestCode ^int resultCode ^Intent data]
  (let [results (WebChromeClient.FileChooserParams/parseResult resultCode data)
        filePathCallback (as (deref filePathCallbackRef) "ValueCallback<Uri[]>")]
    (.onReceiveValue filePathCallback results)
    (reset! filePathCallbackRef nil)
    unit))

(gen-class
 :name WebChromeClientImpl
 :extends WebChromeClient
 :constructors {[] []}
 :prefix "wvc_"
 :methods [[onShowFileChooser [WebView "ValueCallback<Uri[]>" WebChromeClient.FileChooserParams] boolean]])

(defn- wvc_onShowFileChooser [self ^WebView wv filePathCallback ^WebChromeClient.FileChooserParams fileChooserParams]
  (let [intent (.createIntent fileChooserParams)
        activity (as (.getContext wv) Activity)]
    (.startActivityForResult activity (Intent/createChooser intent "Выберите изображение") 1)
    (reset! filePathCallbackRef filePathCallback)
    true))

(gen-class
 :name WebViewJsListener
 :extends Object
 :constructors {[Activity WebView] []}
 :prefix "wv_"
 :methods [[^JavascriptInterface dispatch [String String] void]])

(defn- handle_event [^WebView wv event payload]
  (let [f (-> (deref env_atom) :scope (get "user/main"))
        effects (f [{:event event :payload payload}])]
    (dispatch wv effects)))

(defn- wv_dispatch [^WebViewJsListener self event ^String payload]
  (let [[^Activity activity ^WebView wv] self.state]
    (.runOnUiThread
     activity
     (runnable (fn! [] (handle_event wv event (.fromJson (Gson.) payload (class Object))))))
    unit))
