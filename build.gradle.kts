// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}
buildscript {
    dependencies {
        classpath ("com.android.tools.build:gradle:8.3.0") // 예시로 최신 버전을 기입합니다.
    }
}