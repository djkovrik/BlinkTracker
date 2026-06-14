package io.github.sergio.sastre.composable.preview.scanner.paparazzi.plugin

import javax.inject.Inject
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

open class ComposablePreviewPaparazziExtension @Inject constructor(objects: ObjectFactory) {
    val enable: Property<Boolean> = objects.property(Boolean::class.java)
    val packages: ListProperty<String> = objects.listProperty(String::class.java)
    val includePrivatePreviews: Property<Boolean> = objects.property(Boolean::class.java)
    val testClassName: Property<String> = objects.property(String::class.java)
    val testPackageName: Property<String> = objects.property(String::class.java)
}
