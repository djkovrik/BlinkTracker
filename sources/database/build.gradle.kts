plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.kotlin.ksp)
    kotlin("android")
}

android {
    namespace = "com.sedsoftware.blinktracker.database"
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
    implementation(libs.org.jetbrains.coroutines)
    implementation(libs.org.jetbrains.datetime)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}
