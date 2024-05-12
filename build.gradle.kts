import org.jetbrains.kotlin.fir.declarations.builder.buildScript

// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
//    id("org.jetbrains.kotlin.android") version "1.7.0" apply false
    id("com.vanniktech.maven.publish") version "0.25.2" apply false
}