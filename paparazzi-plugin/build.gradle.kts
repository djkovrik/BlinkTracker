plugins {
    kotlin("jvm") version "2.2.0"
    id("java-gradle-plugin")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

gradlePlugin {
    plugins {
        create("composablePreviewPaparazziPlugin") {
            id = "io.github.sergio-sastre.composable-preview-scanner.paparazzi-plugin"
            implementationClass =
                "io.github.sergio.sastre.composable.preview.scanner.paparazzi.plugin.ComposablePreviewPaparazziPlugin"
            displayName = "Composable Preview Paparazzi Generator"
            description = "Generates Paparazzi tests for screenshot testing Composable Previews"
        }
    }
}

dependencies {
    implementation(gradleApi())
}

kotlin {
    jvmToolchain(17)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
