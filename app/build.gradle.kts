@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.google.play.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.perf)
}

android {
    namespace = "com.sedsoftware.blinktracker"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.sedsoftware.blinktracker"
        minSdk = 26
        targetSdk = 33
        versionCode = 100000
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    androidResources {
        noCompress += "tflite"
    }
}

dependencies {
    implementation(project(":sources:root"))
    implementation(project(":sources:settings"))
    implementation(project(":sources:database"))
    implementation(project(":sources:ui"))
    implementation(project(":sources:components:camera"))
    implementation(project(":sources:components:preferences"))
    implementation(project(":sources:components:statistic"))
    implementation(project(":sources:components:tracker"))

    implementation(platform(libs.compose.bom))
    implementation(platform(libs.firebase.bom))

    implementation(libs.org.jetbrains.coroutines)
    implementation(libs.org.jetbrains.datetime)
    implementation(libs.ark.mvikotlin.core)
    implementation(libs.ark.mvikotlin.main)
    implementation(libs.ark.decompose.core)

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(libs.face.detection)
    implementation(libs.timber)

    implementation("com.google.firebase:firebase-perf-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
}
