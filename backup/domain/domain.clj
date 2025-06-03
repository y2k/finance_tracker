(ns domain
  (:require ["../app/effect" :as e]))

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

(defn qr_clicked [_]
  (printlnfx (input {:onclick :file_selected})))

(defn file_selected [payload]
  (e/batch
  [(printlnfx (label {:text "Please wait..."}))
    (decode_qrfx {:id payload :next :qr_recognized})]))

(defn qr_recognized [[payload]]
  (e/batch
   [(printlnfx (label {:text (str "Результат: " (:rawValue payload))}))
    (printlnfx (button {:title "Home"
                        :onclick :home}))]))
