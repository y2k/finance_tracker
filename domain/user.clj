(ns user (:require ["./ui" :as ui]))

(defn main [{event :event payload :payload}]
  (case event
    :qr_clicked (ui/input {:onclick :file_selected})
    :file_selected (str
                    (ui/label {:text "Пожалуйста, подождите..."})
                    (str "WebView.decode_qr({id:'" payload "', next:'" :qr_recognized "'});"))
    :qr_recognized (str
                    (ui/label {:text (str "Результат: " (:params (ext/decode_url payload)))})
                    (ui/button {:title "Home"
                                :onclick :home}))
    (ui/button {:title "QR"
                :onclick :qr_clicked})))
