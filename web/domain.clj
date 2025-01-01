(defn- decode_qr [{id :id next :next}]
  (let [target (.querySelector document (str "#" id))
        file (first (.-files target))
        barcodeDetector (BarcodeDetector. {:formats [:qr_code]})]
    (->
     (createImageBitmap file)
     (.then (fn [bitmap] (.detect barcodeDetector bitmap)))
     (.then (fn [barcodes]
              (.dispatch window.Android next (:rawValue (get barcodes 0))))))))

(defn- update_ui [query text]
  (.insertAdjacentHTML (.querySelector document query) "beforeend" text))

(defn- register_event [store name handler]
  (assoc store name handler))

(defn main []
  (set! (.-WebView window)
        (-> {}
            (register_event :decode_qr decode_qr)
            (register_event :update_ui update_ui)))
  (.dispatch window.Android :home ""))
