(ns _ (:require ["vendor/make/0.1.0/main" :as b]))

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
    :root-ns "interpreter"
    :src-dir "vendor/interpreter/java/0.1.0"
    :target-dir ".github/android/app/src/main/java/interpreter"
    :items ["interpreter"]})
  (b/module
   {:lang "java"
    :root-ns "app"
    :src-dir "app"
    :target-dir ".github/android/app/src/main/java"
    :items ["main" "database" "notifications" "repl_service"]})])
