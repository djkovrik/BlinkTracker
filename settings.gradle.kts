@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BlinkTracker"
include(":app")
include(":sources:root")
include(":sources:settings")
include(":sources:database")
include(":sources:ui")
include(":sources:components:home")
include(":sources:components:camera")
include(":sources:components:preferences")
include(":sources:components:statistic")
include(":sources:components:tracker")
