import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "dev.hotwire.turbo.fitplanandroid"
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.hotwire.turbo.fitplanandroid"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    // Android
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.browser:browser:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("com.google.android.material:material:1.12.0")

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2025.02.00"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.activity:activity-compose:1.10.1")

    // Turbo Android
    implementation("dev.hotwire:turbo:7.0.0")

    // Strada
    implementation("dev.hotwire:strada:1.0.0-beta2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
}
