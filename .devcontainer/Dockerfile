FROM ubuntu:24.04

# --- バージョンは1か所で管理 ---
ARG NODE_VERSION=24
ARG USERNAME=vscode
# cmdline-tools のリビジョン。https://developer.android.com/studio#command-line-tools-only で最新を確認
ARG CMDLINE_TOOLS_VERSION=13114758
ARG ANDROID_HOME=/opt/android-sdk

ENV NODE_VERSION=${NODE_VERSION}
ENV DEBIAN_FRONTEND=noninteractive
ENV LANG=C.UTF-8
# Android / JDK
ENV ANDROID_HOME=${ANDROID_HOME}
ENV ANDROID_SDK_ROOT=${ANDROID_HOME}
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=${JAVA_HOME}/bin:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools:/usr/local/cargo/bin:$PATH

# 基本パッケージ + Claude Code 実行に有用なツール + Android ビルド用 JDK17。
# Tauri 用の GTK/WebKit 系ライブラリ・Rust・Playwright は Android では不要なため含めない。
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        # --- 基本ツール（Claude Code / git 連携に有用） ---
        curl \
        ca-certificates \
        gnupg \
        git \
        jq \
        tmux \
        vim-tiny \
        file \
        wget \
        sudo \
        zip \
        unzip \
        netcat-openbsd \
        # --- Android ビルド用 JDK（Ubuntu 公式リポジトリの OpenJDK 17） ---
        openjdk-17-jdk-headless \
    && useradd -m -s /bin/bash $USERNAME \
    # Node.js（Claude Code は Node ランタイムを必要とする）
    && curl -fsSL https://deb.nodesource.com/setup_${NODE_VERSION}.x | bash - \
    && apt-get install -y nodejs \
    && echo "$USERNAME ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/$USERNAME \
    && chmod 0440 /etc/sudoers.d/$USERNAME \
    && rm -rf /var/lib/apt/lists/*

# Android cmdline-tools のインストール（SDK 本体 platforms/build-tools は volume に初回取得）
RUN mkdir -p ${ANDROID_HOME}/cmdline-tools \
    && curl -fsSL -o /tmp/cmdline-tools.zip \
        "https://dl.google.com/android/repository/commandlinetools-linux-${CMDLINE_TOOLS_VERSION}_latest.zip" \
    && unzip -q /tmp/cmdline-tools.zip -d /tmp/cmdline-tools \
    && mv /tmp/cmdline-tools/cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest \
    && rm -rf /tmp/cmdline-tools.zip /tmp/cmdline-tools \
    && chown -R $USERNAME:$USERNAME ${ANDROID_HOME}

USER $USERNAME

SHELL ["/bin/bash", "-l", "-c"]

# Claude Code / AI ツール・gh・pnpm safe-chain のセットアップ。
# NOTE: gist scripts are fetched from the repo owner's gists.
# TODO: Pin to specific gist revisions or vendor scripts locally for supply chain safety.
RUN curl -fsSL https://gist.githubusercontent.com/DIO0550/f5999a0512a369a232b7d4f824f0cc08/raw/install-gh-local.sh | bash
RUN curl -fsSL https://gist.githubusercontent.com/DIO0550/dcc09dc9d2dcefceb9b0cda183e7ef8e/raw/pnpm-safe-chain-setup.sh | sh
RUN curl -fsSL https://gist.githubusercontent.com/DIO0550/148b1392cf435ba3562da0d398bee442/raw/install-ai-tools.sh | bash
RUN curl -fsSL https://gist.githubusercontent.com/DIO0550/356434ec2b182f71d99dc0c467555ba5/raw/tmux-setup.sh | bash

WORKDIR /workspace
