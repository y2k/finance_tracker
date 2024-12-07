(defn add [a b] (+ a b))

(defn- decode_qr [{id :id next :next}]
  (let [target (.querySelector document (str "#" id))
        file (first (.-files target))
        barcodeDetector (BarcodeDetector. {:formats [:qr_code]})]
    (->
     (createImageBitmap file)
     (.then (fn [bitmap] (.detect barcodeDetector bitmap)))
     (.then (fn [barcodes]
              (.dispatch Android next (JSON.stringify barcodes null 2)))))))

(defn main []
  (set! (.-WebView window) {:decode_qr decode_qr})
  (.dispatch Android :init ""))
