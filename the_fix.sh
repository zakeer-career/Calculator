#!/bin/bash
FILE="app/src/main/java/com/zakeercareer/calculator/MainActivity.kt"
awk '!/HapticFeedbackType/ && !/LocalHapticFeedback/ && !/val haptic/ && !/haptic\.performHapticFeedback/' "$FILE" > temp_main.kt
mv temp_main.kt "$FILE"
