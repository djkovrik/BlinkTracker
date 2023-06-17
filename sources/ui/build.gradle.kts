@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
}

android {
    namespace = "com.sedsoftware.blinktracker.ui"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
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
