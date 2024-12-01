(defn foo [a b]
  (+ a b))

(def count_state (atom 1))

(defn main []
  (reset! user/count_state (+ 1 (deref user/count_state)))
  ;; (str "window.update_ui('#log', `<button onclick=\"Android.dispatch('notification', null)\">" (deref count_state) "</button>`)")
  (str "window.update_ui('#log', `<button onclick=\"Android.dispatch('notification', null)\">"
       (deref user/count_state)
       "</button>`)"))
