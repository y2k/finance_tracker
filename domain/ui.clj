(ns ui)

(defonce id_state (atom 1))

(defn next-id []
  (reset! ui/id_state (+ 1 (deref ui/id_state)))
  (str "id_" (deref ui/id_state)))

(defn button [{title :title onclick :onclick}]
  (str "<button onclick=\\\"Android.dispatch('" onclick "', '')\\\">" title "</button>"))

(defn label [{text :text}]
  (str "<pre><code>" text "</code></pre>"))

(defn input [{onclick :onclick}]
  (let [id (next-id)]
    (str "<input id='" id "' onchange=\\\"Android.dispatch('" onclick "', '" id "')\\\" type='file' accept='image/*' class='input'>")))
