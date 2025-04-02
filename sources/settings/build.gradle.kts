plugins {
    alias(libs.plugins.com.android.library)
    kotlin("android")
}

android {
    namespace = "com.sedsoftware.blinktracker.settings"
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
    implementation(libs.datastore)
}
