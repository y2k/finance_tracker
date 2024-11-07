(ns _ (:require [js.fs.promises :as fs]
                [js.child_process :as cp]
                ["../../vendor/packages/xml/0.1.0/main" :as tools]
                ["../view" :as app]))

(defn- manifest []
  [:manifest {:xmlns:android "http://schemas.android.com/apk/res/android"}
   [:uses-permission {:android:name "android.permission.READ_EXTERNAL_STORAGE"}]
   [:uses-permission {:android:name "android.permission.ACCESS_NOTIFICATION_POLICY"}]
   [:uses-permission {:android:name "android.permission.POST_NOTIFICATIONS"}]
   [:application {:android:icon "@drawable/ic_launcher"
                  :android:label "Finance Tracker"
                  :android:roundIcon "@drawable/ic_launcher"
                  :android:theme "@style/Theme.ChargeTimer"}
    [:activity {:android:name "y2k.finance_tracker.android.Main$MainActivity"
                :android:configChanges "orientation|screenSize"
                :android:exported "true"
                :android:theme "@style/Theme.ChargeTimer"}
     [:intent-filter
      [:action {:android:name "android.intent.action.MAIN"}]
      [:category {:android:name "android.intent.category.LAUNCHER"}]]]]])

(defn- html []
  [:html {:lang "ru" :data-theme "dark"}
   [:head
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:link {:rel "stylesheet" :href "file:///android_asset/css/pico.classless.css"}]]
   [:body {:style "user-select: none; height: 100%; display: flex; flex-direction: column-reverse;"}
    (app/html)
    [:script {:type :module} "import { main } from './domain.js'; main()"]
    [:script {:type :module} "window.update_ui=(query,text)=>{document.querySelector(query).innerHTML=text}"]]])

(def HTML_PATH ".github/android/app/src/main/assets/index.html")
(def PATH_JS ".github/bin/src/domain.js")

(defn- write_html []
  (fs/writeFile HTML_PATH (tools/to_string (html)) "utf-8"))

(defn exec_async [cmd]
  (Promise. (fn [resolve reject]
              (cp/exec cmd (fn [e] (if (some? e) (reject e) (resolve)))))))

(case (get process.argv 2)
  :reload
  (-> (write_html)
      (.then (fn [] (exec_async (str "adb push " HTML_PATH " /data/data/y2k.finance_tracker/files/index.html"))))
      (.then (fn [] (exec_async (str "adb push " PATH_JS " /data/data/y2k.finance_tracker/files/domain.js"))))
      (.then (fn [] (exec_async "adb shell am start -S -n 'y2k.finance_tracker/.android.Main\\$MainActivity'"))))

  :manifest
  (Promise.all
   [(write_html)
    (fs/writeFile ".github/android/app/src/main/AndroidManifest.xml"
                  (tools/to_string (manifest))
                  "utf-8")])
  null)
