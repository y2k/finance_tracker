(ns _ (:import [io.requery.android.database.sqlite SQLiteDatabase]))

(defn main [register dispatch]
  (let [^SQLiteDatabase db (SQLiteDatabase/openOrCreateDatabase ":memory:" nil)]
    (.execSQL db "CREATE TABLE IF NOT EXISTS main (id INTEGER PRIMARY KEY AUTOINCREMENT, content BLOB)")

    (register :database
              (fn [payload]
                (let [c (.rawQuery db
                                   (:sql payload)
                                   (into-array2 (class String) (:args payload)))
                      result (if (.moveToFirst c) (.getString c 0) nil)]
                  (dispatch (:next payload) result))))))
