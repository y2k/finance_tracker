(ns domain (:require ["./effect" :as e]))

;; UI

(defn- button [{title :title onclick :onclick}]
  (str "<button onclick=\\\"Android.dispatch('" onclick "', '')\\\">" title "</button>"))

(defn- label [{text :text}]
  (str "<pre><code>" text "</code></pre>"))

(defn- input [{onclick :onclick}]
  (let [id (gensym)]
    (str "<input id='" id "' onchange=\\\"Android.dispatch('" onclick "', '" id "')\\\" type='file' accept='image/*' class='input'>")))

(defn- group [children]
  (str "<div class='group'>"
       (reduce (fn [acc x] (str acc x)) "" children)
       "</div>"))

;; Effects

(defn- printlnfx [p]
  (fn [w] (let [fx (:println w)] (fx p))))

(defn- decode_qrfx [p]
  (fn [w] (let [fx (:decode_qr w)] (fx p))))

;; Domain

(defn home []
  (printlnfx
   (group [(button {:title "QR"
                    :onclick :qr_clicked})
           (button {:title "Test sqlite"
                    :onclick :test_sqlite_clicked})])))

(defn qr_clicked [args]
  (printlnfx (input {:onclick :file_selected})))

(defn file_selected [payload]
  (e/batch
   [(printlnfx (label {:text "Пожалуйста, подождите..."}))
    (decode_qrfx {:id payload :next :qr_recognized})]))

(defn qr_recognized [[payload]]
  (e/batch
   [(printlnfx (label {:text (str "Результат: " (:rawValue payload))}))
    (printlnfx (button {:title "Home"
                        :onclick :home}))]))

;; Infrastructure

(defn main [register dispatch]
  ;; (let [w {:println (fn [x] (dispatch :println x))
  ;;          :decode_qr (fn [x] (dispatch :decode_qr x))}
  ;;       reg_event (fn [event handler]
  ;;                   (register event
  ;;                             (fn [payload]
  ;;                               (let [r (handler payload)]
  ;;                                 (r w)))))]
  ;;   (reg_event :home (fn [_] (home)))
  ;;   (reg_event :qr_clicked (fn [x] (qr_clicked x)))
  ;;   (reg_event :file_selected (fn [x] (file_selected x)))
  ;;   (reg_event :qr_recognized (fn [x] (qr_recognized x))))
  nil)
