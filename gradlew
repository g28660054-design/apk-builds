#!/usr/bin/env sh
CLASSPATH=$(pwd)/gradle/wrapper/gradle-wrapper.jar
exec java -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
