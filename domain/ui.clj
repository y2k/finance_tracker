(ns ui)

(defn button [{title :title onclick :onclick}]
  (str "<button onclick=\\\"Android.dispatch('" onclick "', '')\\\">" title "</button>"))

(defn label [{text :text}]
  (str "<pre><code>" text "</code></pre>"))

(defn input [{onclick :onclick}]
  (let [id (gensym)]
    (str "<input id='" id "' onchange=\\\"Android.dispatch('" onclick "', '" id "')\\\" type='file' accept='image/*' class='input'>")))
