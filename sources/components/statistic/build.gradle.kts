@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
}

android {
    namespace = "com.sedsoftware.blinktracker.components.statistic"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":sources:database"))

    implementation(libs.org.jetbrains.coroutines)
    implementation(libs.org.jetbrains.datetime)
    implementation(libs.ark.mvikotlin.core)
    implementation(libs.ark.mvikotlin.main)
    implementation(libs.ark.mvikotlin.extensions.coroutines)
    implementation(libs.ark.decompose.core)
    implementation(libs.ark.decompose.extensions)
    implementation(libs.ark.essenty)
}
