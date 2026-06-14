plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.compose.compiler)
    alias(libs.plugins.paparazzi)
    kotlin("android")
    id("io.github.sergio-sastre.composable-preview-scanner.paparazzi-plugin")
}

android {
    namespace = "com.sedsoftware.blinktracker.ui"
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
    buildFeatures {
        compose = true
    }
    sourceSets {
        getByName("test") {
            java.srcDir(layout.buildDirectory.dir("generated/source/composablePreviewPaparazziTests"))
        }
    }
    testOptions {
        unitTests.all { test ->
            test.jvmArgs("-Xmx4g")
        }
    }
}

composablePreviewPaparazzi {
    enable.set(true)
    packages.set(listOf("com.sedsoftware.blinktracker.ui"))
    includePrivatePreviews.set(true)
    testClassName.set("BlinkTrackerComposablePreviewPaparazziTest")
    testPackageName.set("com.sedsoftware.blinktracker.ui.previewtest")
}

dependencies {
    implementation(project(":sources:root"))
    implementation(project(":sources:settings"))
    implementation(project(":sources:components:home"))
    implementation(project(":sources:components:camera"))
    implementation(project(":sources:components:preferences"))
    implementation(project(":sources:components:statistic"))
    implementation(project(":sources:components:tracker"))

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.icons)

    implementation(libs.org.jetbrains.coroutines)
    implementation(libs.org.jetbrains.datetime)

    implementation(libs.ark.decompose.core)
    implementation(libs.ark.decompose.extensions)
    implementation(libs.foundation)
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.face.detection)
    implementation(libs.timber)

    implementation(libs.vico.core)
    implementation(libs.vico.compose)

    debugImplementation(libs.ui.tooling)

    testImplementation(libs.junit)
    testImplementation(libs.composable.preview.scanner.android)
}
