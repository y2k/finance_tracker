(ns y2k.finance_tracker.android
  (:import [android.app Activity]
           [android.content Intent]
           [android.net Uri]
           [android.os Bundle]
           [java.nio.file Files]
           [android.webkit WebView WebChromeClient ValueCallback JavascriptInterface]
           [interpreter Interpreter]))

(gen-class
 :name MainActivity
 :extends Activity
 :constructors {[] []}
 :prefix "activity_"
 :methods [[^Override onCreate [Bundle] void]
           [^Override onActivityResult [int int Intent] void]])

(defn activity_onCreate [^MainActivity self ^Bundle bundle]
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
    ;; (.loadUrl webview (str "file://" (.getFilesDir self) "/index.html"))
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

;; (defn update_ui [^WebView wv query text]
;;   (.evaluateJavascript wv (str "window.update_ui('" query "', `" text "`)") null)
;;   unit)

(defn- make_default_state []
  (atom (Interpreter/make_env
         {:vector (function (fn [xs] xs))
          :atom (function (fn [[x]] (atom x)))
          :deref (function (fn [[x]] (deref x)))
          :reset! (function (fn [[a x]] (reset! a x) x))
          :str (function (fn [xs] (str (into-array2 (.-class Object) xs))))})))

(def env_atom (make_default_state))

(defn- handle_event [^WebView wv event payload]
  (let [env (->
             (deref env_atom)
             (Interpreter/eval
              (checked!
               (Files/readAllLines
                (.toPath
                 (.getFileStreamPath (.getContext wv) "user.txt")))))
             second)]
    (reset! env_atom env)
    (.evaluateJavascript
     wv
     (-> env (Interpreter/eval ["(" "user/main" ")"]) first str)
     null)
    null))

(defn- wv_dispatch [^WebViewJsListener self event payload]
  (let [[^Activity activity ^WebView wv] self.state]
    (.runOnUiThread
     activity
     (runnable (fn! [] (handle_event wv event payload))))
    unit))
