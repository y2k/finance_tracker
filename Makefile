OUT_DIR := .github/bin

.PHONY: test
test: build

.PHONY: build
build:
	@ export OCAMLRUNPARAM=b && \
		clj2js compile -target repl -src build.clj > .github/Makefile
	@ $(MAKE) -f .github/Makefile
	@ mkdir -p .github/android/app/src/main/java/y2k && \
		clj2js gen -target java > .github/android/app/src/main/java/y2k/RT.java
	@ docker run --rm \
		-v ${PWD}/.github/temp/android:/root/.android \
		-v ${PWD}/.github/temp/gradle:/root/.gradle \
		-v ${PWD}/.github/android:/target \
		y2khub/cljdroid build

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

.PHONY: run
run: install hard_reload

.PHONY: hard_reload
hard_reload:
	@ mkdir -p ${OUT_DIR}
	@ clj2js compile -target bytecode -src domain/user.clj > ${OUT_DIR}/domain.bytecode
	@ export CODE=$$(base64 -i ${OUT_DIR}/domain.bytecode) && \
		adb shell am start -S -n y2k.finance_tracker/app.main\\\$$MainActivity --es "code" $$CODE

.PHONY: clean
clean:
	@ rm -rf $(OUT_DIR)
	@ rm -rf .github/android/app/src/main/assets/web
	@ rm -rf .github/android/app/src/main/assets/index.html
	@ rm -rf .github/android/app/src/main/java
	@ rm -rf .github/android/app/src/main/AndroidManifest.xml

.PHONY: install
install: build
	@ adb install -r .github/android/app/build/outputs/apk/debug/app-debug.apk

.PHONY: log
log:
	@ adb logcat -c && clear && adb logcat -v brief -s \*:I | grep FIXME
