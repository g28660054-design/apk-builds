#!/bin/sh
# Gradle wrapper startup script for Unix
APP_HOME=`pwd`
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
exec java -cp "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
