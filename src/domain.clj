(ns _ (:require
       ["./vendor/android_gallery/android_gallery" :as ag]
       ["./vendor/android_qr/android_qr" :as qr]
       ["./vendor/chat_ui/chat_ui" :as ui]))

(defn get_image_callback [uri]
  (qr/decode_qr
   uri
   {:callback (fn [[x]]
                (ui/update_ui [:label {:text (str x)}]))}))

(defn main []
  [:row {}
   [:button {:title "QR"
             :onclick (ag/get_image)}]
   [:button {:title "Settings"
             :onclick (ui/update_ui [:button {:title "TEST"
                                              :onclick "nil"}])}]])