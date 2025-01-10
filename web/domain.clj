(defn- decode_qr [{id :id next :next}]
  (let [target (.querySelector document (str "#" id))
        file (first (.-files target))
        barcodeDetector (BarcodeDetector. {:formats [:qr_code]})]
    (->
     (createImageBitmap file)
     (.then (fn [bitmap] (.detect barcodeDetector bitmap)))
     (.then (fn [barcodes]
              (.dispatch window.Android next (JSON.stringify barcodes)))))))

(defn- update_ui [query text]
  (.insertAdjacentHTML (.querySelector document query) :beforeend text))

(defn main []
  (eprintln "FIXME:WEB:main")
  (set! (.-WebView window)
        {:dispatch (fn [name payload]
                     (eprintln "FIXME:WEB:dispatch:" name payload)
                     (case name
                       :println (update_ui "#log" (JSON.parse payload))
                       :decode_qr (decode_qr (JSON.parse payload))
                       nil))})
  (.dispatch window.Android :home ""))
