(ns _ (:import [io.requery.android.database.sqlite SQLiteDatabase]))

(def ^SQLiteDatabase db (SQLiteDatabase/openOrCreateDatabase ":memory:" nil))

(defn init []
  (.execSQL db "CREATE TABLE IF NOT EXISTS main (id INTEGER PRIMARY KEY AUTOINCREMENT, content BLOB)")
  nil)

;;    (println "FIXME")

(defn invoke [dispatch name payload]
  (println "FIXME3:" name payload)
  (if (= name :database)
    (let [c (.rawQuery db (:sql payload) nil)]
      (if (.moveToFirst c)
        (dispatch
         (:next payload)
         (.getString c 0))
        nil))
    nil))
