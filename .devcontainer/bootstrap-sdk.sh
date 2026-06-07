#!/usr/bin/env bash
# SDK 本体（platforms / build-tools / platform-tools）を volume に取得する。
# 初回のみ実行。バージョンはここで一元管理し、ホストの Android Studio と揃える。
set -euo pipefail

# --- ホストの Android Studio と必ず揃える ---
PLATFORM_VERSION="android-30"     # compileSdk / targetSdk に合わせる
BUILD_TOOLS_VERSION="30.0.0"

SDKMANAGER="${ANDROID_HOME}/cmdline-tools/latest/bin/sdkmanager"

# 既に取得済みならスキップ
if [ -d "${ANDROID_HOME}/platforms/${PLATFORM_VERSION}" ]; then
  echo "SDK already bootstrapped (${PLATFORM_VERSION}). Skipping."
  exit 0
fi

echo "Accepting licenses..."
yes | "${SDKMANAGER}" --licenses >/dev/null

echo "Installing SDK components..."
"${SDKMANAGER}" \
  "platform-tools" \
  "platforms;${PLATFORM_VERSION}" \
  "build-tools;${BUILD_TOOLS_VERSION}"

echo "Done. Installed:"
"${SDKMANAGER}" --list_installed
