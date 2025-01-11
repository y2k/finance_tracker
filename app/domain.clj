(ns _)

;; UI

(defn- button [{title :title onclick :onclick}]
  (str "<button onclick=\\\"Android.dispatch('" onclick "', '')\\\">" title "</button>"))

(defn- label [{text :text}]
  (str "<pre><code>" text "</code></pre>"))

(defn- input [{onclick :onclick}]
  (let [id (gensym)]
    (str "<input id='" id "' onchange=\\\"Android.dispatch('" onclick "', '" id "')\\\" type='file' accept='image/*' class='input'>")))

;; Domain

(comment

  (defn foo [a b] (+ a b))

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

(defn- make_event_handler [register dispatch]
  (fn [event handler]
    (register event
              (fn [payload]
                (run!
                 (fn [[e_name e_payload]]
                   (dispatch e_name e_payload))
                 (handler payload))))))

(defn home []
  [[:println (button {:title "QR"
                      :onclick :qr_clicked})]
   [:println (button {:title "Test sqlite"
                      :onclick :test_sqlite_clicked})]])

(defn main [register dispatch]
  (let [reg_event (make_event_handler register dispatch)]
    (reg_event :home (fn [args] (home)))

    (reg_event :qr_clicked
               (fn [args]
                 [[:println (input {:onclick :file_selected})]]))

    (reg_event :file_selected
               (fn [payload]
                 [[:println (label {:text "Пожалуйста, подождите..."})]
                  [:decode_qr {:id payload :next :qr_recognized}]]))

    (reg_event :qr_recognized
               (fn [[payload]]
                 [[:println (label {:text (str "Результат: " (:rawValue payload))})]
                  [:println (button {:title "Home"
                                     :onclick :home})]]))))
