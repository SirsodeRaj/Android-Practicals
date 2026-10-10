@echo off
REM Creates a keystore (upload key) in this folder. keytool will ask you for passwords and details.
REM keytool is inside Android Studio's JBR: ...\Android Studio\jbr\bin  (add it to PATH or run from there)
keytool -genkeypair -v -keystore my-release-key.jks -alias upload -keyalg RSA -keysize 2048 -validity 10000
