(ns _ (:require ["../vendor/packages/xml/0.2.0/main" :as xml]))

(xml/to_string
 [:html {:lang "ru" :data-theme "dark"}
  [:head
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
   [:link {:rel "stylesheet" :href "file:///android_asset/css/pico.classless.css"}]
   [:style {}
    ".group_template {display: flex; flex-direction: row-reverse; gap: 16px; row-gap: 0px; flex-wrap: wrap} .short_button {width: auto;}"]]
  [:body {:style "user-select: none; height: 100%; display: flex; flex-direction: column-reverse;"}
   [:main {:id "log" :style "flex-direction: column; display: flex; padding-bottom: 0px; gap: 4px;"}]
   [:script {:type :module} "import { main } from './web/domain.js'; main()"]
   [:script {:type :module} "window.update_ui=(query,text)=>{document.querySelector(query).insertAdjacentHTML('beforeend', text);}"]]])
