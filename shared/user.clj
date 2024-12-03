(defonce count_state (atom 1))

(defn main []
  (reset! user/count_state (+ 1 (deref user/count_state)))
  (str "window.update_ui('#log', `<button onclick=\"Android.dispatch('notification', null)\">"
       (deref user/count_state)
       "</button>`)"))
