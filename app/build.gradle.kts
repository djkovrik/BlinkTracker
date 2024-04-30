
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.google.play.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.perf)
}

val useReleaseKeystore = rootProject.file("app/release.jks").exists()

android {
    namespace = "com.sedsoftware.blinktracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sedsoftware.blinktracker"
        minSdk = 26
        targetSdk = 34
        versionCode = 100301
        versionName = "1.3.1"
        setProperty("archivesBaseName", applicationId)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    signingConfigs {
        create("release") {
            if (useReleaseKeystore) {
                storeFile = rootProject.file("app/release.jks")
                keyAlias = "blinkz"
                keyPassword = gradleLocalProperties(rootDir).getProperty("releaseKeyPwd")
                storePassword = gradleLocalProperties(rootDir).getProperty("releaseKeystorePwd")
            }
        }
    }
    buildTypes {
        debug {
            signingConfig = signingConfigs["debug"]
            versionNameSuffix = "-debug"
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        release {
            if (useReleaseKeystore) {
                signingConfig = signingConfigs["release"]
            }
            isShrinkResources = true
            isMinifyEnabled = true
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
    implementation(project(":sources:components:home"))
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

fun Project.getLocalProperty(key: String, file: String = "local.properties"): String {
    val properties = Properties()
    val localProperties = File(file)
    if (localProperties.isFile) {
        InputStreamReader(FileInputStream(localProperties), Charsets.UTF_8).use { reader ->
            properties.load(reader)
        }
    } else error("File from not found")

    return properties.getProperty(key).toString()
}
