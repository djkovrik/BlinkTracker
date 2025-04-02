plugins {
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.org.jetbrains.kotlin.compose.compiler) apply false
    alias(libs.plugins.org.jetbrains.kotlin.serialization) apply false
    alias(libs.plugins.com.android.library) apply false
    alias(libs.plugins.google.play.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    parallel = true
    baseline = file("$projectDir/detekt/baseline.xml")
    source.setFrom(files("$projectDir/app/", "$projectDir/sources/"))
    config.setFrom(file("$projectDir/detekt/base-config.yml"))
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true)
        html.outputLocation.set(file("$projectDir/detekt/reports/detekt.html"))
    }
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    jvmTarget = "21"
}

tasks.withType<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>().configureEach {
    jvmTarget = "21"
}

tasks.register<GradleBuild>("runOnGitHub") {
    tasks = listOf("detekt", "assembleDebug")
}
