(defonce id_state (atom 1))

(defn next-id []
  (reset! user/id_state (+ 1 (deref user/id_state)))
  (str "id_" (deref user/id_state)))

(defn update-ui [html]
  (str "update_ui('#log', `" html "`);"))

(defn ui-button [{title :title onclick :onclick}]
  (update-ui
   (str "<button onclick=\"Android.dispatch('" onclick "', '')\">" title "</button>")))

(defn ui-label [text]
  (update-ui
   (str "<pre><code>" text "</code></pre>")))

(defn ui-input [onclick]
  (let [id (next-id)]
    (update-ui
     (str "<input id='" id "' onchange=\"Android.dispatch('" onclick "', '" id "')\" type='file' accept='image/*' class='input'>"))))

(defn main [{event :event payload :payload}]
  (case event
    :qr_clicked (ui-input :file_selected)
    :file_selected (str
                    (ui-label "Пожалуйста, подождите...")
                    (str "WebView.decode_qr({id:'" payload "', next:'" :qr_recognized "'});"))
    :qr_recognized (str
                    (ui-label (str "Результат: " payload))
                    (ui-button {:title "Home"
                                :onclick :home}))
    (ui-button {:title "QR"
                :onclick :qr_clicked})))
