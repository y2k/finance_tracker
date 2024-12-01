(defn html []
  [:main {:id "log" :style "flex-direction: column; display: flex; padding-bottom: 0px;"}

   [:template {:id "label_template"}
    [:div
     [:code {:class "text" :style "width: 100%"}]
     [:div {:style "height: 16px"}]]]

   [:template {:id "button_template"}
    [:button {:class "title" :style "width: auto;"}]]

   [:template {:id "group_template"}
    [:div {:class "root" :style "display: flex; flex-direction: row-reverse; gap: 16px; row-gap: 0px; flex-wrap: wrap"}]]

   [:template {:id "input_template"}
    [:input {:type "file" :accept "image/*" :class "input"}]]

   [:style {}
    ".short_button {width: auto;}"]

   [:span {:id :output}]

  ;;  [:button {:class "title" :style "width: auto;"} "Test"]
   ])
