(ns user (:require ["./ui" :as ui]))

(defn main [{event :event payload :payload}]
  (case event
    :home [[:println (ui/button {:title "QR"
                                 :onclick :qr_clicked})]
           [:println (ui/button {:title "Test sqlite"
                                 :onclick :test_sqlite_clicked})]]

    ;; :test_sqlite_clicked [[:database {:sql "INSERT INTO main (content) VALUES ('{\"title\": \"Test\"}')"
    ;;                                   :next :sqlite_executed}]]
    ;; :test_sqlite_clicked [[:database {:sql "SELECT COUNT(*) FROM main"
    ;;                                   :next :sqlite_executed}]]
    :test_sqlite_clicked [[:database {:sql "SELECT content->>'title' FROM main"
                                      :next :sqlite_executed}]]

    :sqlite_executed [[:println (ui/label {:text (str "Результат: " payload)})]]

    :qr_clicked [[:println (ui/input {:onclick :file_selected})]]

    :file_selected [[:println (ui/label {:text "Пожалуйста, подождите..."})]
                    [:decode_qr {:id payload :next :qr_recognized}]]

    :qr_recognized [[:println (ui/label {:text (str "Результат: " (:rawValue (get payload 0)))})]
                    [:println (ui/button {:title "Home"
                                          :onclick :home})]]

    []))
