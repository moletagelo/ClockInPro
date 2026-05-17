#!/bin/bash

export JAVA_HOME=/root/.local/share/mise/installs/java/17.0.2
export PATH=$JAVA_HOME/bin:/root/.local/share/mise/installs/gradle/8.14.4/gradle-8.14.4/bin:$PATH
export ANDROID_HOME=/opt/android-sdk
export ANDROID_SDK_ROOT=$ANDROID_HOME

cd "$(dirname "$0")"

exec gradle "$@"
