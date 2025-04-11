(ns domain)

(defn main []
  [:row {}
   [:button {:title "QR"
             :onclick [:android_gallery:get_image]}]
   [:button {:title "Settings"
             :onclick [:chat_ui:update [:button {:title "TEST 2"
                                                 :onclick nil}]]}]])

(defn main_event [uri]
  [:android_qr:recognize
   {:uri uri
    :callback (fn [xs]
                [:chat_ui:update [:label {:text (str x)}]])}])
