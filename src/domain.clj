(defn add [a b] (+ a b))

(defn- add_nodes [& nodes]
  (let [root (.querySelector document "#log")]
    (.forEach (.querySelectorAll root "button") (fn [n] (set! (.-disabled n) true)))
    (.forEach (.querySelectorAll root "input") (fn [n] (set! (.-disabled n) true)))
    (.forEach nodes (fn [node] (.appendChild root node)))))

(defn- view [text]
  (let [node (.cloneNode (.-content (.querySelector document "#label_template")) true)]
    (set! (.-textContent (.querySelector node ".text")) text)
    node))

(defn- button [title event]
  (let [node (.cloneNode (.-content (.querySelector document "#button_template")) true)]
    (set! (.-textContent (.querySelector node ".title")) title)
    (set! (.-onclick (.querySelector node ".title")) event)
    node))

(defn- group [& items]
  (let [node (.cloneNode (.-content (.querySelector document "#group_template")) true)]
    (.forEach items
              (fn [[t e]]
                (.appendChild (.querySelector node ".root") (button t e))))
    node))

(def hello_ref (atom null))

(defn back_home []
  (button "В меню" (fn [] ((deref hello_ref)))))

(defn- decode_qr [target]
  (let [file (first (.-files target))
        barcodeDetector (BarcodeDetector. {:formats [:qr_code]})]
    (add_nodes (view "Пожалуйста, подождите..."))
    (->
     (createImageBitmap file)
     (.then (fn [bitmap] (.detect barcodeDetector bitmap)))
     (.then (fn [barcodes]
              (add_nodes
               (view (JSON.stringify barcodes null 2))
               (back_home))))
     (.catch (fn [e] (view e))))))

(defn- input []
  (let [node (.cloneNode (.-content (.querySelector document "#input_template")) true)]
    (.addEventListener (.querySelector node ".input") "change" (fn [e] (decode_qr (.-target e))))
    node))

;;
;;
;;

(defn- hello []
  (add_nodes
   (view "Команды")
   (group ["QR" (fn [] (add_nodes (input)))] ["Настроить нотификации" (fn [] (hello))])))

(defn main []
  (reset! hello_ref hello)
  (hello))
