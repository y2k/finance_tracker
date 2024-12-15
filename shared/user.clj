(defonce id_state (atom 1))

(defn next-id []
  (reset! user/id_state (+ 1 (deref user/id_state)))
  (str "id_" (deref user/id_state)))

(defn update-ui [html]
  (str "update_ui('#log', `" html "`);"))

(defn button [title onclick]
  (update-ui (str "<button onclick=\"Android.dispatch('" onclick "', '')\">" title "</button>")))

(defn label [text]
  (update-ui (str "<code>" text "</code>")))

(defn main [{event :event payload :payload}]
  (case event
    "init" (button "QR" "qr_clicked")
    "qr_clicked" (let [id (next-id)]
                   (update-ui (str "<input id='" id "' onchange=\"Android.dispatch('file_selected', '" id "')\" type='file' accept='image/*' class='input'>")))
    "file_selected" (str
                     (label "Пожалуйста, подождите...")
                     (str "WebView.decode_qr({id:'" payload "', next:'qr_recognized'});"))
    "qr_recognized" (label (str "Результат: " payload))
    null))
