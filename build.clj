(ns _ (:require ["vendor/make/0.2.0/main" :as b]))

(b/generate
 [(b/module-files
   {:target "repl"
    :rules [{:src "res/html.clj"     :target ".github/android/app/src/main/assets/index.html"}
            {:src "res/manifest.clj" :target ".github/android/app/src/main/AndroidManifest.xml"}]})
  (b/module
   {:lang "js"
    :src-dir "web"
    :target-dir ".github/android/app/src/main/assets"
    :items ["domain"]})
  (b/module
   {:lang "java"
    :root-ns "app"
    :src-dir "app"
    :target-dir ".github/android/app/src/main/java/app"
    :items ["main" "effect" "database" "notifications" "repl_service" "message_broker" "webview" "domain"]})
  (b/module
   {:lang "java"
    :root-ns "test"
    :src-dir "test"
    :target-dir ".github/android/app/src/test/java/test"
    :items ["test"]})
  (b/vendor
   {:lang "java"
    :target-dir ".github/android/app/src/main/java"
    :items [{:name "interpreter" :version "0.3.0"}
            {:name "nrepl"       :version "0.1.0"}
            {:name "effects"     :version "0.2.0"}
            {:name "socket"      :version "0.1.0"}]})])
