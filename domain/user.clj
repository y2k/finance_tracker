(ns user (:require ["./ui" :as ui]))

(comment

  (ext/dispatch :println (gensym))

  (ext/dispatch :println
                (ui/button {:title (gensym)
                            :onclick :qr_clicked}))

  (ext/dispatch :home {})

  (ext/reg-event :home
                 (fn []
                   [[:println (ui/button {:title "FIXME"
                                          :onclick :home})]]))

  comment)

(ext/reg-event :home
               (fn []
                 [[:println (ui/button {:title "QR"
                                        :onclick :qr_clicked})]
                  [:println (ui/button {:title "Test sqlite"
                                        :onclick :test_sqlite_clicked})]]))

(ext/reg-event :qr_clicked
               (fn []
                 [[:println (ui/input {:onclick :file_selected})]]))

(ext/reg-event :file_selected
               (fn [payload]
                 [[:println (ui/label {:text "Пожалуйста, подождите..."})]
                  [:decode_qr {:id payload :next :qr_recognized}]]))

(ext/reg-event :qr_recognized
               (fn [[payload]]
                 [[:println (ui/label {:text (str "Результат: " (:rawValue payload))})]
                  [:println (ui/button {:title "Home"
                                        :onclick :home})]]))

;; :test_sqlite_clicked [[:database {:sql "INSERT INTO main (content) VALUES (?)"
;;                                   :args [(ext/to-json {:title "Hello" :description "World"})]
;;                                   :next :sqlite_executed}]]
