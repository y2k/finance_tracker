(ns _ (:require [js.fs.promises :as fs]
                [js.child_process :as cp]
                ["../xml/xml" :as tools]
                ["./manifest" :as m]
                ["./html" :as h]))

;; (def PATH_JS ".github/bin/src/domain.js")
(def HTML_PATH ".github/android/app/src/main/assets/index.html")
(def MANIFEST_PATH ".github/android/app/src/main/AndroidManifest.xml")

;; (defn- write_html []
;;   (fs/writeFile HTML_PATH (tools/to_string (h/html)) "utf-8"))

;; (defn exec_async [cmd]
;;   (Promise. (fn [resolve reject]
;;               (cp/exec cmd (fn [e] (if (some? e) (reject e) (resolve)))))))

;; (defn- reload []
;;   (-> (write_html)
;;       (.then (fn [] (exec_async (str "clj2js bytecode shared/user.clj /Users/igor/Projects/finance_tracker/vendor/prelude/bytecode/prelude.clj > .github/temp/user.txt"))))
;;       (.then (fn [] (exec_async (str "adb push .github/temp/user.txt /data/data/y2k.finance_tracker/files/user.txt.tmp"))))
;;       (.then (fn [] (exec_async (str "adb shell cp /data/data/y2k.finance_tracker/files/user.txt.tmp /data/data/y2k.finance_tracker/files/user.txt"))))
;;       ;;
;;       (.then (fn [] (exec_async (str "adb push " HTML_PATH " /data/data/y2k.finance_tracker/files/index.html"))))
;;       (.then (fn [] (exec_async (str "adb push " PATH_JS " /data/data/y2k.finance_tracker/files/domain.js"))))
;;       (.then (fn [] (exec_async "adb shell am start -n 'y2k.finance_tracker/.android.Main\\$MainActivity'")))
;;       ;; (.then (fn [] (exec_async "adb shell am start -S -n 'y2k.finance_tracker/.android.Main\\$MainActivity'")))
;;       ))

(case (get process.argv 2)
  ;; :reload (reload)
  :resources (Promise.all
              [(fs/writeFile HTML_PATH
                             (tools/to_string (h/html))
                             "utf-8")
               (fs/writeFile MANIFEST_PATH
                             (tools/to_string (m/manifest))
                             "utf-8")])
  null)
