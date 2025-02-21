#!/bin/bash

# Install OpenJDK 17
curl -fsSL https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_linux-x64_bin.tar.gz -o openjdk17.tar.gz
tar -xzf openjdk17.tar.gz
JAVA_PATH="$PWD/jdk-17.0.2"

# Export Java home
export JAVA_HOME=$JAVA_PATH
export PATH=$JAVA_HOME/bin:$PATH
echo "Using Java from: $JAVA_HOME"
java -version

# Make gradlew executable and run build
chmod +x ./gradlew
./gradlew clean teavm:build

# Create dist directory and copy build outputs
mkdir -p dist
cp -r /vercel/path0/teavm/build/dist/webapp/* dist/ 