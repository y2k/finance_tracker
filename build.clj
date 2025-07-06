(ns _ (:require [".github/vendor/make/main" :as b]))

(b/generate
 [{:target "java"
   :root "src"
   :namespace "app"
   :out-dir ".github/android/app/src/main/java/app"}
  {:target "java"
   :root "test"
   :namespace "app"
   :out-dir ".github/android/app/src/test/java/app"}
  {:target "eval"
   :src "res/manifest.clj"
   :out ".github/android/app/src/main/AndroidManifest.xml"}])
