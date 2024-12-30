(ns _ (:require ["../vendor/packages/build/0.1.0/main" :as b]))

(str
 (b/default)
 (b/build-files
  {:target "repl"
   :rules [{:src "res/html.clj"
            :target ".github/android/app/src/main/assets/index.html"}
           {:src "res/manifest.clj"
            :target ".github/android/app/src/main/AndroidManifest.xml"}]})
 (b/build-files
  {:target "js"
   :rules [{:src "web/domain.clj"
            :target ".github/android/app/src/main/assets/domain.js"}]})
 (b/build-java-package
  {:root-ns "app"
   :target-dir ".github/android/app/src/main/java"
   :items ["main"
           "notifications"
           "signal"
           "repl_server"]}))
