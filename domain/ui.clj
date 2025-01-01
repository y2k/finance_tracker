(ns ui)

(defonce id_state (atom 1))

(defn next-id []
  (reset! ui/id_state (+ 1 (deref ui/id_state)))
  (str "id_" (deref ui/id_state)))

(defn update-ui [html]
  (str "WebView.update_ui('#log', `" html "`);"))

(defn button [{title :title onclick :onclick}]
  (update-ui
   (str "<button onclick=\"Android.dispatch('" onclick "', '')\">" title "</button>")))

(defn label [{text :text}]
  (update-ui
   (str "<pre><code>" text "</code></pre>")))

(defn input [{onclick :onclick}]
  (let [id (next-id)]
    (update-ui
     (str "<input id='" id "' onchange=\"Android.dispatch('" onclick "', '" id "')\" type='file' accept='image/*' class='input'>"))))
