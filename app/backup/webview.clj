(ns _ (:import [android.app Activity]
               [android.content Intent]
               [android.net Uri]
               [android.os Bundle]
               [android.webkit WebView WebChromeClient ValueCallback JavascriptInterface]
               [java.util.function Function]
               [com.google.gson Gson]))

(defn ^WebView make_webview [^Activity context dispatch register]
  (let [webview (WebView. context)
        webSettings (.getSettings webview)]
    (.setDomStorageEnabled webSettings true)
    (.setJavaScriptEnabled webSettings true)
    (.setAllowFileAccess webSettings true)
    (.setAllowFileAccessFromFileURLs webSettings true)
    (.setAllowUniversalAccessFromFileURLs webSettings true)
    (.addJavascriptInterface webview (WebViewJsListener. context webview dispatch register) :Android)
    (.setWebChromeClient webview (WebChromeClientImpl.))
    (.loadUrl webview "file:///android_asset/index.html")
    webview))

;; WebChromeClientImpl

(gen-class
 :name WebChromeClientImpl
 :extends WebChromeClient
 :constructors {[] []}
 :prefix "wvc_"
 :methods [[onShowFileChooser [WebView "ValueCallback<Uri[]>" WebChromeClient.FileChooserParams] boolean]])

(def- filePathCallbackRef (atom nil))

(defn on_receive_value [^int resultCode ^Intent data]
  (let [results (WebChromeClient.FileChooserParams/parseResult resultCode data)
        filePathCallback (as (deref filePathCallbackRef) "ValueCallback<Uri[]>")]
    (.onReceiveValue filePathCallback results)
    (reset! filePathCallbackRef nil)
    unit))

(defn- wvc_onShowFileChooser [self ^WebView wv filePathCallback ^WebChromeClient.FileChooserParams fileChooserParams]
  (let [intent (.createIntent fileChooserParams)
        activity (as (.getContext wv) Activity)]
    (.startActivityForResult activity (Intent/createChooser intent "Select image") 1)
    (reset! filePathCallbackRef filePathCallback)
    true))

;; WebViewJsListener

(gen-class
 :name WebViewJsListener
 :extends Object
 :constructors {[Activity WebView Object Object] []}
 :prefix "wv_"
 :methods [[^JavascriptInterface register [String] void]
           [^JavascriptInterface dispatch [String String] void]])

(defn- wv_register [^WebViewJsListener self ^String name]
  (let [[_ ^WebView wv _ register] self.state]
    (register name (fn [data]
                     (.evaluateJavascript
                      wv (str "WebView.dispatch(`" name "`, `" (.toJson (Gson.) data) "`)") nil)
                     nil))))

(defn- wv_dispatch [^WebViewJsListener self event ^String payload]
  (let [[^Activity activity ^WebView wv dispatch] self.state
        payload_obj (.fromJson (Gson.) payload (class Object))]
    (.runOnUiThread activity (fn [] (dispatch event payload_obj)))
    unit))
