PRELUDE_PATH := vendor/prelude/js/src/prelude.clj
OUT_DIR := .github/bin
SRC_DIRS := src/web test vendor/packages/xml
ANDROID_PRELUDE_PATH := vendor/prelude/java/src/prelude.clj
ANDROID_SRC_DIRS := android
ANDROID_OUT_DIR := .github/android/app/src/main/java/y2k/finance_tracker

.PHONY: build
build: gen_build
	@ PRELUDE_JAVA=$(ANDROID_PRELUDE_PATH) \
		PRELUDE_JS=$(PRELUDE_PATH) \
		PRELUDE_BYTECODE=vendor/prelude/bytecode/prelude.clj \
		PRELUDE_REPL=vendor/prelude/interpreter/prelude.clj \
		.github/build2.gen.sh
	@ mkdir -p .github/android/app/src/main/java/y2k \
		&& cp $(shell dirname $(ANDROID_PRELUDE_PATH))/RT.java .github/android/app/src/main/java/y2k/RT.java

.PHONE: gen_build
gen_build:
	@ export OCAMLRUNPARAM=b && \
		clj2js compile -target repl -src res/build.clj > .github/build2.gen.sh
	@ chmod +x .github/build2.gen.sh

.PHONE: clean
clean:
	@ rm -rf $(OUT_DIR)
	@ rm -rf .github/android/app/src/main/assets/web
	@ rm -rf .github/android/app/src/main/assets/index.html
	@ rm -rf .github/android/app/src/main/java
	@ rm -rf .github/android/app/src/main/AndroidManifest.xml

.PHONE: run
run: build
	@ docker run --rm \
		-v ${PWD}/.github/temp/android:/root/.android \
		-v ${PWD}/.github/temp/gradle:/root/.gradle \
		-v ${PWD}/.github/android:/target \
		y2khub/cljdroid build
	@ adb install -r .github/android/app/build/outputs/apk/debug/app-debug.apk
	@ adb shell am start -S -n 'y2k.finance_tracker/app.main\$$MainActivity'

.PHONE: test
test: build
	@ node $(OUT_DIR)/test/test.js
