// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.0"
}
true // Needed to make the Suppress annotation work for the plugins block

detekt {
    buildUponDefaultConfig = true
    allRules = false
    source = files("$projectDir/app/")
    config = files("$projectDir/detekt/base-config.yml")
    baseline = file("$projectDir/detekt/baseline.xml")
    parallel = true
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true)
        html.outputLocation.set(file("$projectDir/detekt/reports/detekt.html"))
    }
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    jvmTarget = "1.8"
}

tasks.withType<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>().configureEach {
    jvmTarget = "1.8"
}

tasks.register<GradleBuild>("runOnGitHub") {
    tasks = listOf("detekt", "assembleDebug")
}
