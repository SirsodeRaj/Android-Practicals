#!/bin/sh
# Creates a keystore (upload key) in this folder. keytool will ask you for passwords and details.
# Run:  sh generate_keystore.sh
keytool -genkeypair -v -keystore my-release-key.jks -alias upload \
        -keyalg RSA -keysize 2048 -validity 10000
