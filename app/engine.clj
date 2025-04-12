(ns app.engine
  (:require ["../nrepl/nrepl" :as nrepl]
            ["../interpreter/interpreter" :as i]))

(defn main []
  (let [store_atom (atom {})
        external {:interpreter:save (fn [name code]
                                      (swap! store_atom (fn [x] (assoc x name (str code)))))
                  :interpreter:resolve (fn [name]
                                         (get (deref store_atom) name))}]
    (nrepl/main (fn [e l] (i/eval {} e l))
                env_atom
                {:port 8090})))
