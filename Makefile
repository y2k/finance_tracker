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
		.github/build.gen.sh
	@ mkdir -p .github/android/app/src/main/java/y2k \
		&& cp $(shell dirname $(ANDROID_PRELUDE_PATH))/RT.java .github/android/app/src/main/java/y2k/RT.java
	@ cp $(OUT_DIR)/res/res/manifest.repl .github/android/app/src/main/AndroidManifest.xml
	@ cp $(OUT_DIR)/res/res/html.repl .github/android/app/src/main/assets/index.html

.PHONE: gen_build
gen_build:
	@ export OCAMLRUNPARAM=b && clj2js make_build_script \
		-lang java \
		-path app \
		-lib vendor/packages/interpreter/java/0.1.0 \
		-target .github/android/app/src/main/java \
		> .github/build.gen.sh
	@ export OCAMLRUNPARAM=b && clj2js make_build_script \
		-lang repl \
		-path res \
		-lib vendor/packages/xml/0.2.0 \
		-target .github/bin/res \
		>> .github/build.gen.sh
	@ export OCAMLRUNPARAM=b && clj2js make_build_script \
		-lang js \
		-path web \
		-target .github/android/app/src/main/assets \
		>> .github/build.gen.sh
	@ export OCAMLRUNPARAM=b && clj2js make_build_script \
		-lang js \
		-path web \
		-path test \
		-target .github/bin \
		>> .github/build.gen.sh
	@ export OCAMLRUNPARAM=b && clj2js make_build_script \
		-lang bytecode \
		-path shared \
		-target .github/android/app/src/main/assets \
		>> .github/build.gen.sh
	@ chmod +x .github/build.gen.sh

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

# .PHONY: test
# test: build build_java
# 	@ node $(OUT_DIR)/test/test.js

# .PHONY: build
# build:
# # @ PRELUDE_JS=$(PRELUDE_PATH) .github/build.gen.sh
# # @ set -e; find $(SRC_DIRS) -name '*.clj' | while read clj_file; do \
# # 	out_file=$(OUT_DIR)/$$(echo $$clj_file | sed 's|\.clj$$|.js|'); \
# # 	mkdir -p $$(dirname $$out_file); \
# # 	clj2js js $$clj_file $(PRELUDE_PATH) > $$out_file; \
# #   done
# # @ cp .github/bin/src/domain.js .github/android/app/src/main/assets/
# # @ node .github/bin/src/build/build.js manifest

# .PHONY: clean
# clean:
# 	@ rm -rf $(OUT_DIR)

# .PHONY: build_java
# build_java: build
# 	@ PRELUDE_JAVA=$(ANDROID_PRELUDE_PATH) \
# 		PRELUDE_JS=$(PRELUDE_PATH) \
# 		.github/build.gen.sh
# 	@ mkdir -p ${OUT_DIR}/android/y2k && cp $(shell dirname $(ANDROID_PRELUDE_PATH))/RT.java ${OUT_DIR}/android/y2k/RT.java
# 	@ rm -rf .github/android/app/src/main/java
# 	@ cp -r ${OUT_DIR}/android .github/android/app/src/main/java

# .PHONY: install_apk
# install_apk: pre_install_apk reload

# .PHONY: pre_install_apk
# pre_install_apk: build_java
# 	@ echo "" >> .github/temp/build_duration.txt
# 	@ date +%s >> .github/temp/build_duration.txt
# 	@ docker run --rm -v ${PWD}/.github/temp/android:/root/.android -v ${PWD}/.github/temp/gradle:/root/.gradle -v ${PWD}/.github/android:/target y2khub/cljdroid build
# 	@ date +%s >> .github/temp/build_duration.txt
# 	@ adb install -r .github/android/app/build/outputs/apk/debug/app-debug.apk

# .PHONY: docker_build_init
# docker_build_init:
# 	@ rm -rf .github/android && docker run --rm -v ${PWD}/.github/android:/target y2khub/cljdroid copy

# .PHONY: reload
# reload: build
# 	@ adb root
# 	@ node .github/bin/src/build/build.js reload

# .PHONE: gen_build
# gen_build:
# 	@ export OCAMLRUNPARAM=b && clj2js make_build_script \
# 		-lang java \
# 		-path app \
# 		-lib vendor/packages/interpreter/java/0.1.0 \
# 		-target .github/bin/android \
# 		> .github/build.gen.sh
# 	@ export OCAMLRUNPARAM=b && clj2js make_build_script \
# 		-lang js \
# 		-path web \
# 		-target .github/bin \
# 		>> .github/build.gen.sh
# 	@ export OCAMLRUNPARAM=b && clj2js make_build_script \
# 		-lang js \
# 		-path test \
# 		-target .github/bin \
# 		>> .github/build.gen.sh
# 	@ chmod +x .github/build.gen.sh
