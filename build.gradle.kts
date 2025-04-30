plugins {
    // Déclaration des plugins globaux
    id("com.android.application") version "8.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")

    }
}
