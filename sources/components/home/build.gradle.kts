plugins {
    alias(libs.plugins.com.android.library)
    kotlin("android")
}

android {
    namespace = "com.sedsoftware.blinktracker.components.home"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {
    implementation(project(":sources:database"))
    implementation(project(":sources:settings"))
    implementation(project(":sources:components:camera"))
    implementation(project(":sources:components:statistic"))
    implementation(project(":sources:components:tracker"))

    implementation(libs.org.jetbrains.coroutines)
    implementation(libs.ark.mvikotlin.core)
    implementation(libs.ark.mvikotlin.main)
    implementation(libs.ark.decompose.core)
    implementation(libs.ark.mvikotlin.extensions.coroutines)
    implementation(libs.ark.decompose.core)
    implementation(libs.ark.decompose.extensions)
    implementation(libs.ark.essenty)
}
