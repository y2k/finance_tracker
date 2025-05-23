OUT_DIR := .github/bin

.PHONY: test
test: clean build_clj
	@ cd .github/android && ./gradlew test

# .PHONY: test
# test: clean build
# 	@ docker run --rm \
# 		-v ${PWD}/.github/temp/android:/root/.android \
# 		-v ${PWD}/.github/temp/gradle:/root/.gradle \
# 		-v ${PWD}/.github/android:/target \
# 		y2khub/cljdroid test || open ".github/android/app/build/reports/tests/testDebugUnitTest/index.html"

.PHONY: nrepl
nrepl:
	@ echo 8080 > .nrepl-port
	@ adb forward tcp:18090 tcp:8090
	@ clj2js nrepl -host 127.0.0.1

.PHONY: build
build: build_clj
	@ docker run --rm \
		-v ${PWD}/.github/temp/android:/root/.android \
		-v ${PWD}/.github/temp/gradle:/root/.gradle \
		-v ${PWD}/.github/android:/target \
		y2khub/cljdroid build

.PHONY: build_clj
build_clj:
	@ export OCAMLRUNPARAM=b && \
		clj2js compile -target repl -src build.clj > .github/Makefile
	@ $(MAKE) -f .github/Makefile
	@ mkdir -p .github/android/app/src/main/java/y2k && \
		clj2js gen -target java > .github/android/app/src/main/java/y2k/RT.java

.PHONY: repl
repl:
	@ temp_file=$$(mktemp) && \
		pbpaste > $$temp_file && \
		clj2js compile -src $$temp_file -target bytecode > ${OUT_DIR}/domain.bytecode && \
		export CODE=$$(base64 -i ${OUT_DIR}/domain.bytecode) && \
		adb shell am start -n y2k.finance_tracker/app.main\\\$$MainActivity -f 0x20000000 --es "code" $$CODE

.PHONY: reload
reload:
	@ mkdir -p ${OUT_DIR}
	@ clj2js compile -target bytecode -src domain/user.clj > ${OUT_DIR}/domain.bytecode
	@ export CODE=$$(base64 -i ${OUT_DIR}/domain.bytecode) && \
		adb shell am start -n y2k.finance_tracker/app.main\\\$$MainActivity -f 0x20000000 --es "code" $$CODE

.PHONY: reload_file
reload_file:
	@ mkdir -p ${OUT_DIR}
	@ clj2js compile -target bytecode -src $(FILE) > ${OUT_DIR}/domain.bytecode
	@ export CODE=$$(base64 -i ${OUT_DIR}/domain.bytecode) && \
		adb shell am start -n y2k.finance_tracker/app.main\\\$$MainActivity -f 0x20000000 --es "code" $$CODE

.PHONY: run
run: install am_start

.PHONY: am_start
am_start:
	@ adb shell am start -S -n y2k.finance_tracker/app.main\\\$$MainActivity

.PHONY: hard_reload
hard_reload:
	@ mkdir -p ${OUT_DIR}
	@ clj2js compile -target bytecode -src domain/domain.clj > ${OUT_DIR}/domain.bytecode
	@ export CODE=$$(base64 -i ${OUT_DIR}/domain.bytecode) && \
		adb shell am start -S -n y2k.finance_tracker/app.main\\\$$MainActivity --es "code" $$CODE

.PHONY: clean
clean:
	@ rm -rf $(OUT_DIR)
	@ rm -rf .github/android/app/src/main/assets/web
	@ rm -rf .github/android/app/src/main/assets/index.html
	@ rm -rf .github/android/app/src/main/AndroidManifest.xml
	@ rm -rf .github/android/app/src/main/java
	@ rm -rf .github/android/app/src/test/java

.PHONY: install
install: build
	@ adb install -r .github/android/app/build/outputs/apk/debug/app-debug.apk

.PHONY: log
log:
	@ adb logcat -c && clear && adb logcat -v brief -s \*:I | grep FIXME
