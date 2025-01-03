(ns _ (:import [android.app Activity]
               [android.content Intent]
               [android.net Uri]
               [android.os Bundle]
               [android.webkit WebView WebChromeClient ValueCallback JavascriptInterface]
               [java.util.function Function])
    (:require ["../interpreter/interpreter" :as i]
              ["./repl_service" :as repl]
              ["./event_store" :as es]
              ["./utils" :as u]))

(gen-class
 :name MainActivity
 :extends Activity
 :constructors {[] []}
 :prefix "activity_"
 :methods [[^Override onCreate [Bundle] void]
           [^Override onActivityResult [int int Intent] void]
           [^Override onNewIntent [Intent] void]])

(defn ^void activity_onNewIntent [^MainActivity self ^Intent intent]
  (repl/update intent))

(defn activity_onCreate [^MainActivity self ^Bundle bundle]
  (repl/update (.getIntent self))

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
    unit))

(def- filePathCallbackRef (atom null))

(defn activity_onActivityResult [^MainActivity self requestCode ^int resultCode ^Intent data]
  (let [results (WebChromeClient.FileChooserParams/parseResult resultCode data)
        filePathCallback (as (deref filePathCallbackRef) "ValueCallback<Uri[]>")]
    (.onReceiveValue filePathCallback results)
    (reset! filePathCallbackRef null)
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

(defn- make_default_state []
  (atom (i/make_env {:ext/decode_url (fn [[url]] (u/decode_url url))})))

(def env_atom (make_default_state))

(defn- handle_event [^WebView wv event payload]
  (let [env (->
             (deref env_atom)
             (i/eval (repl/get_code))
             second)]
    (reset! env_atom env)
    (.evaluateJavascript
     wv
     (->
      env :scope (get "user/main")
      (as Function)
      (.apply [{:event event :payload payload}])
      str)
     nil)
    nil))

(defn- wv_dispatch [^WebViewJsListener self event payload]
  (let [[^Activity activity ^WebView wv] self.state]
    (.runOnUiThread
     activity
     (runnable (fn! [] (handle_event wv event payload))))
    unit))
