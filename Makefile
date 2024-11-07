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

.PHONY: clean
clean:
	@ rm -rf $(OUT_DIR)

.PHONY: build_java
build_java: build
	@ set -e; find $(ANDROID_SRC_DIRS) -name '*.clj' | while read clj_file; do \
		out_file=$(ANDROID_OUT_DIR)/$$(echo $$clj_file | sed 's|\.clj$$|.java|'); \
		mkdir -p $$(dirname $$out_file); \
		clj2js java $$clj_file $(ANDROID_PRELUDE_PATH) > $$out_file; \
	  done
	@ node .github/bin/src/build/build.js manifest
	@ cp vendor/prelude/java/src/RT.java .github/android/app/src/main/java/y2k

.PHONY: install_apk
install_apk: build_java
	@ docker run --rm -v ${PWD}/.github/temp/android:/root/.android -v ${PWD}/.github/temp/gradle:/root/.gradle -v ${PWD}/.github/android:/target y2khub/cljdroid build
	@ adb install -r .github/android/app/build/outputs/apk/debug/app-debug.apk

docker_extract:
	@ rm -rf .github/android && docker run --rm -v ${PWD}/.github/android:/target y2khub/cljdroid copy

.PHONY: reload
reload: build
	@ node .github/bin/src/build/build.js reload
