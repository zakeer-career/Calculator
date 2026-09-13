#!/bin/bash
OLD_PKG="com.example"
NEW_PKG="com.zakeercareer.calculator"
OLD_DIR="app/src/main/java/com/example"
NEW_DIR="app/src/main/java/com/zakeercareer/calculator"

mkdir -p "$NEW_DIR"
cp -r "$OLD_DIR"/* "$NEW_DIR"/
rm -rf "$OLD_DIR"

find app/src/ -type f -name "*.kt" -exec sed -i "s/package $OLD_PKG/package $NEW_PKG/g" {} +
find app/src/ -type f -name "*.kt" -exec sed -i "s/import $OLD_PKG/import $NEW_PKG/g" {} +
sed -i "s/namespace = \"$OLD_PKG\"/namespace = \"$NEW_PKG\"/g" app/build.gradle.kts
sed -i "s/applicationId = \"$OLD_PKG\"/applicationId = \"$NEW_PKG\"/g" app/build.gradle.kts

