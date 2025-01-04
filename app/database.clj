(ns _ (:import [io.requery.android.database.sqlite SQLiteDatabase]))

(def ^SQLiteDatabase db (SQLiteDatabase/openOrCreateDatabase ":memory:" nil))

(defn init []
  (.execSQL db "CREATE TABLE IF NOT EXISTS main (id INTEGER PRIMARY KEY AUTOINCREMENT, content BLOB)")
  nil)

(defn invoke [dispatch name payload]
  (println "FIXME:DB:" name payload)
  (if (= name :database)
    (let [c (.rawQuery db (:sql payload) (into-array2 (class String) (:args payload)))
          result (if (.moveToFirst c) (.getString c 0) nil)]
      (dispatch (:next payload) result))
    nil))
