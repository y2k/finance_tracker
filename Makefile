PRELUDE_PATH := vendor/prelude/js/src/prelude.clj
OUT_DIR := .github/bin
SRC_DIRS := src test vendor/packages/xml
ANDROID_PRELUDE_PATH := vendor/prelude/java/src/prelude.clj
ANDROID_SRC_DIRS := android
ANDROID_OUT_DIR := .github/android/app/src/main/java/y2k/finance_tracker

.PHONY: test
test: build
	@ node $(OUT_DIR)/test/test.js

.PHONY: build
build:
	@ set -e; find $(SRC_DIRS) -name '*.clj' | while read clj_file; do \
		out_file=$(OUT_DIR)/$$(echo $$clj_file | sed 's|\.clj$$|.js|'); \
		mkdir -p $$(dirname $$out_file); \
		clj2js js $$clj_file $(PRELUDE_PATH) > $$out_file; \
	  done
	@ cp .github/bin/src/domain.js .github/android/app/src/main/assets/
	@ node .github/bin/src/build/build.js manifest

.PHONY: clean
clean:
	@ rm -rf $(OUT_DIR)

.PHONY: build_java
build_java: build
	@ .github/build.gen.sh
	@ .github/build-interpreter.gen.sh
	@ cp vendor/prelude/java/src/RT.java ${OUT_DIR}/android/y2k/RT.java
	@ rm -rf .github/android/app/src/main/java
	@ cp -r ${OUT_DIR}/android .github/android/app/src/main/java

.PHONY: install_apk
install_apk: build_java
	@ echo "" >> .github/temp/build_duration.txt
	@ date +%s >> .github/temp/build_duration.txt
	@ docker run --rm -v ${PWD}/.github/temp/android:/root/.android -v ${PWD}/.github/temp/gradle:/root/.gradle -v ${PWD}/.github/android:/target y2khub/cljdroid build
	@ date +%s >> .github/temp/build_duration.txt
	@ adb install -r .github/android/app/build/outputs/apk/debug/app-debug.apk
	@ adb shell am start -S -n 'y2k.finance_tracker/.android.Main\$$MainActivity'

.PHONY: docker_build_init
docker_build_init:
	@ rm -rf .github/android && docker run --rm -v ${PWD}/.github/android:/target y2khub/cljdroid copy

.PHONY: reload
reload: build
	@ adb root
	@ node .github/bin/src/build/build.js reload

.PHONE: gen_build
gen_build:
	@ clj2js make_build_script $$PWD/vendor/packages/interpreter/java/0.1.0 $$PWD/.github/bin/android/interpreter > .github/build-interpreter.gen.sh
	@ chmod +x .github/build-interpreter.gen.sh
	@ clj2js make_build_script $$PWD/android $$PWD/.github/bin/android/y2k/finance_tracker/android > .github/build.gen.sh
	@ chmod +x .github/build.gen.sh
