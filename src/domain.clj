(ns _ (:require
       ["./vendor/android_gallery/android_gallery" :as ag]
       ["./vendor/android_qr/android_qr" :as qr]
       ["./vendor/chat_ui/chat_ui" :as ui]))

(defn- get_image_callback [uri]
  (qr/decode_qr
   uri
   {:callback (fn [[x]]
                (ui/update_ui [:label {:text (str x)}]))}))

(defn- main_ui []
  (ui/update_ui
   [:row {}
    [:button {:title "QR"
              :onclick (ag/get_image)}]
    [:button {:title "Settings"
              :onclick (ui/update_ui [:button {:title "TEST"
                                               :onclick "nil"}])}]]))

(defn main [event]
  (case (:name event)
    :home (main_ui)
    :gallery (get_image_callback (:url (:props event)))
    nil))
