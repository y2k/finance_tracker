(ns y2k.finance_tracker.android
  (:import [android.service.notification NotificationListenerService StatusBarNotification]
           [android.content Context]))

;; NotificationListenerService

(gen-class
 :name NotificationListenerServiceImpl
 :extends NotificationListenerService
 :constructors {[] []}
 :prefix "_"
 :methods [[^Override onNotificationPosted [StatusBarNotification] void]])

(defn- _onNotificationPosted [^NotificationListenerService self ^StatusBarNotification sbn]
  (println "FIXME: onNotificationPosted | "
           (.getPackageName sbn) " | "
           (.-tickerText (.getNotification sbn)) " | "
           (.getString (.-extras (.getNotification sbn)) "android.title") " | "
           (.getString (.-extras (.getNotification sbn)) "android.text")))
