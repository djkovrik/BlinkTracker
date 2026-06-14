package io.github.sergio.sastre.composable.preview.scanner.paparazzi.plugin

import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.api.tasks.testing.Test

fun setupGenerateComposablePreviewPaparazziTestsTask(
    project: Project,
    extension: ComposablePreviewPaparazziExtension
) {
    if (extension.packages.get().isEmpty()) {
        throw IllegalArgumentException(
            "Please set 'packages' in the composablePreviewPaparazzi extension."
        )
    }

    val generatedSourceDir = project.layout.buildDirectory.dir("generated/source/composablePreviewPaparazziTests")
    val generateTestsTask = project.tasks.register(
        "generateComposablePreviewPaparazziTests",
        GenerateComposablePreviewPaparazziTestsTask::class.java
    ) { task ->
        task.group = "verification"
        task.description = "Generates Paparazzi test files for screenshot testing Composable Previews"
        task.outputDir.set(generatedSourceDir)
        task.scanPackageTrees.set(extension.packages)
        task.includePrivatePreviews.set(extension.includePrivatePreviews)
        task.testClassName.set(extension.testClassName)
        task.testPackageName.set(extension.testPackageName)
    }

    project.tasks.withType(AbstractCompile::class.java).configureEach { compileTask ->
        if (compileTask.name.contains("Test", ignoreCase = true)) {
            compileTask.dependsOn(generateTestsTask)
        }
    }

    project.tasks.withType(Test::class.java).configureEach { testTask ->
        testTask.dependsOn(generateTestsTask)
    }

    project.tasks.configureEach { task ->
        if (task.name.contains("compileTestKotlin") || task.name.contains("compileDebugUnitTestKotlin")) {
            task.dependsOn(generateTestsTask)
        }
    }
}
