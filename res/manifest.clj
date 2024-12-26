(ns _ (:require ["../vendor/packages/xml/0.2.0/main" :as xml]))

(xml/to_string
 [:manifest {:xmlns:android "http://schemas.android.com/apk/res/android"}
  [:uses-permission {:android:name "android.permission.INTERNET"}]
  [:uses-permission {:android:name "android.permission.READ_EXTERNAL_STORAGE"}]
  [:uses-permission {:android:name "android.permission.ACCESS_NOTIFICATION_POLICY"}]
  [:uses-permission {:android:name "android.permission.POST_NOTIFICATIONS"}]
  [:application {:android:icon "@drawable/ic_launcher"
                 :android:label "Finance Tracker"
                 :android:roundIcon "@drawable/ic_launcher"
                 :android:theme "@style/Theme.ChargeTimer"}
   [:activity {:android:name "app.main$MainActivity"
               :android:configChanges "orientation|screenSize"
               :android:exported "true"
               :android:theme "@style/Theme.ChargeTimer"}
    [:intent-filter
     [:action {:android:name "android.intent.action.MAIN"}]
     [:category {:android:name "android.intent.category.LAUNCHER"}]]]
   [:service
    {:android:name "y2k.finance_tracker.android.Notifications$NotificationListenerServiceImpl"
     :android:exported "true"
     :android:permission "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE"}
    [:intent-filter
     [:action {:android:name "android.service.notification.NotificationListenerService"}]]]]])
