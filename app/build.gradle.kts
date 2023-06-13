@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
}

android {
    namespace = "com.sedsoftware.blinktracker"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.sedsoftware.blinktracker"
        minSdk = 24
        targetSdk = 33
        versionCode = 1000
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
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
    implementation(project(":sources:components:camera"))
    implementation(project(":sources:components:preferences"))
    implementation(project(":sources:components:tracker"))

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.icons)
    
    implementation(libs.org.jetbrains.coroutines)
    implementation(libs.ark.mvikotlin.core)
    implementation(libs.ark.mvikotlin.main)
    implementation(libs.ark.mvikotlin.extensions.coroutines)
    implementation(libs.ark.decompose.core)
    implementation(libs.ark.decompose.extensions)
    implementation(libs.ark.essenty)

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.datastore)
    implementation(libs.activity.compose)
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.face.detection)
    implementation(libs.timber)

    debugImplementation(libs.ui.tooling)
}
