(ns user (:require ["./ui" :as ui]))

(comment

  (ext/dispatch :println (gensym))

  (ext/dispatch :println
                (ui/button {:title (gensym)
                            :onclick :qr_clicked}))

  (ext/dispatch :home {})

  comment)

(defn main [{event :event payload :payload}]
  (case event
    :home [[:println (ui/button {:title "QR"
                                 :onclick :qr_clicked})]
           [:println (ui/button {:title "Test sqlite"
                                 :onclick :test_sqlite_clicked})]]

    ;; :test_sqlite_clicked [[:database {:sql "INSERT INTO main (content) VALUES (?)"
    ;;                                   :args [(ext/to-json {:title "Hello" :description "World"})]
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
