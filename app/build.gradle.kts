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
        minSdk = 28
        targetSdk = 35
        versionCode = 2
        versionName = "1.8.1"
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"http://localhost:3000\"")
        }

        release {
            buildConfigField("String", "BASE_URL", "\"https://fitplan.vip\"")

            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // The app is handed out as a file, not through the Play Store, so it
            // carries the SDK's debug certificate. That only decides which APK may
            // replace which on a device; the build itself is a release, with
            // debugging off and R8 on.
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    packaging {
        resources.excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
    }
    buildFeatures {
        buildConfig = true
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

    // Hotwire Native
    implementation("dev.hotwire:core:1.3.1")
    implementation("dev.hotwire:navigation-fragments:1.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
}
