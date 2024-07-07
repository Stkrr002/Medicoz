// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("com.android.application") version "8.1.2" apply false
    id("com.android.library") version ("8.0.2") apply false
    id("org.jetbrains.kotlin.android") version ("1.9.0") apply false
    id("org.jetbrains.kotlin.plugin.serialization") version ("1.9.0") apply true
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    id("com.google.devtools.ksp") version "1.9.0-1.0.12" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
}