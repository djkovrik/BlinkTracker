package io.github.sergio.sastre.composable.preview.scanner.paparazzi.plugin

import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class GenerateComposablePreviewPaparazziTestsTask : DefaultTask() {
    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val scanPackageTrees: ListProperty<String>

    @get:Input
    abstract val includePrivatePreviews: Property<Boolean>

    @get:Input
    abstract val testClassName: Property<String>

    @get:Input
    abstract val testPackageName: Property<String>

    @TaskAction
    fun generateTests() {
        val packageName = testPackageName.get()
        val className = testClassName.get()
        val directory = File(outputDir.get().asFile, packageName.replace(".", "/"))
        directory.mkdirs()

        File(directory, "$className.kt").writeText(
            generateTestFileContent(
                packageName = packageName,
                className = className,
                packagesExpression = scanPackageTrees.get().joinToString(", ") { "\"$it\"" },
                includePrivatePreviewsExpression = includePrivatePreviews.get()
            )
        )
    }

    private fun generateTestFileContent(
        packageName: String,
        className: String,
        packagesExpression: String,
        includePrivatePreviewsExpression: Boolean
    ): String {
        val includePrivatePreviewsCall = if (includePrivatePreviewsExpression) ".includePrivatePreviews()" else ""
        return """
            package $packageName

            import android.content.res.Configuration.UI_MODE_NIGHT_MASK
            import android.content.res.Configuration.UI_MODE_NIGHT_YES
            import androidx.compose.foundation.background
            import androidx.compose.foundation.layout.Box
            import androidx.compose.foundation.layout.size
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.graphics.Color
            import androidx.compose.ui.unit.dp
            import app.cash.paparazzi.DeviceConfig
            import app.cash.paparazzi.HtmlReportWriter
            import app.cash.paparazzi.Paparazzi
            import app.cash.paparazzi.Snapshot
            import app.cash.paparazzi.SnapshotHandler
            import app.cash.paparazzi.SnapshotVerifier
            import app.cash.paparazzi.TestName
            import app.cash.paparazzi.detectEnvironment
            import com.android.ide.common.rendering.api.SessionParams
            import com.android.resources.Density
            import com.android.resources.NightMode
            import com.android.resources.ScreenOrientation
            import com.android.resources.ScreenRatio
            import com.android.resources.ScreenRound
            import com.android.resources.ScreenSize
            import kotlin.math.ceil
            import org.junit.Rule
            import org.junit.Test
            import org.junit.runner.RunWith
            import org.junit.runners.Parameterized
            import sergio.sastre.composable.preview.scanner.android.AndroidComposablePreviewScanner
            import sergio.sastre.composable.preview.scanner.android.AndroidPreviewInfo
            import sergio.sastre.composable.preview.scanner.android.device.DevicePreviewInfoParser
            import sergio.sastre.composable.preview.scanner.android.device.domain.Device
            import sergio.sastre.composable.preview.scanner.android.device.types.DEFAULT
            import sergio.sastre.composable.preview.scanner.android.screenshotid.AndroidPreviewScreenshotIdBuilder
            import sergio.sastre.composable.preview.scanner.core.preview.ComposablePreview

            private data class Dimensions(
                val screenWidthInPx: Int,
                val screenHeightInPx: Int
            )

            private object ScreenDimensions {
                fun dimensions(
                    parsedDevice: Device,
                    widthDp: Int,
                    heightDp: Int
                ): Dimensions {
                    val conversionFactor = parsedDevice.densityDpi / DENSITY_BASE
                    val previewWidthInPx = ceil(widthDp * conversionFactor).toInt()
                    val previewHeightInPx = ceil(heightDp * conversionFactor).toInt()
                    return Dimensions(
                        screenHeightInPx = if (heightDp > 0) previewHeightInPx else parsedDevice.dimensions.height.toInt(),
                        screenWidthInPx = if (widthDp > 0) previewWidthInPx else parsedDevice.dimensions.width.toInt()
                    )
                }
            }

            private object DeviceConfigBuilder {
                fun build(preview: AndroidPreviewInfo): DeviceConfig {
                    val parsedDevice = DevicePreviewInfoParser.parse(preview.device)?.inPx() ?: return DeviceConfig()
                    val dimensions = ScreenDimensions.dimensions(
                        parsedDevice = parsedDevice,
                        widthDp = preview.widthDp,
                        heightDp = preview.heightDp
                    )

                    return DeviceConfig(
                        screenHeight = dimensions.screenHeightInPx,
                        screenWidth = dimensions.screenWidthInPx,
                        density = Density(parsedDevice.densityDpi),
                        xdpi = parsedDevice.densityDpi,
                        ydpi = parsedDevice.densityDpi,
                        size = ScreenSize.valueOf(parsedDevice.screenSize.name),
                        ratio = ScreenRatio.valueOf(parsedDevice.screenRatio.name),
                        screenRound = ScreenRound.valueOf(parsedDevice.shape.name),
                        orientation = ScreenOrientation.valueOf(parsedDevice.orientation.name),
                        locale = preview.locale.ifBlank { DEFAULT_LOCALE },
                        fontScale = preview.fontScale,
                        nightMode = if (preview.uiMode and UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES) {
                            NightMode.NIGHT
                        } else {
                            NightMode.NOTNIGHT
                        }
                    )
                }
            }

            private val paparazziTestName = TestName(
                packageName = "Paparazzi",
                className = "Preview",
                methodName = "Test"
            )

            private class PreviewSnapshotVerifier(maxPercentDifference: Double) : SnapshotHandler {
                private val snapshotHandler = SnapshotVerifier(maxPercentDifference = maxPercentDifference)

                override fun newFrameHandler(
                    snapshot: Snapshot,
                    frameCount: Int,
                    fps: Int
                ): SnapshotHandler.FrameHandler =
                    snapshotHandler.newFrameHandler(
                        snapshot = snapshot.copy(testName = paparazziTestName),
                        frameCount = frameCount,
                        fps = fps
                    )

                override fun close() {
                    snapshotHandler.close()
                }
            }

            private class PreviewHtmlReportWriter(maxPercentDifference: Double) : SnapshotHandler {
                private val snapshotHandler = HtmlReportWriter(maxPercentDifference = maxPercentDifference)

                override fun newFrameHandler(
                    snapshot: Snapshot,
                    frameCount: Int,
                    fps: Int
                ): SnapshotHandler.FrameHandler =
                    snapshotHandler.newFrameHandler(
                        snapshot = snapshot.copy(testName = paparazziTestName),
                        frameCount = frameCount,
                        fps = fps
                    )

                override fun close() {
                    snapshotHandler.close()
                }
            }

            private object PaparazziPreviewRule {
                fun createFor(preview: ComposablePreview<AndroidPreviewInfo>): Paparazzi {
                    val previewInfo = preview.previewInfo
                    val previewApiLevel = if (previewInfo.apiLevel == UNDEFINED_API_LEVEL) {
                        MAX_API_LEVEL
                    } else {
                        previewInfo.apiLevel
                    }
                    val tolerance = DEFAULT_TOLERANCE
                    return Paparazzi(
                        environment = detectEnvironment().copy(compileSdkVersion = previewApiLevel),
                        deviceConfig = DeviceConfigBuilder.build(preview.previewInfo),
                        supportsRtl = true,
                        showSystemUi = previewInfo.showSystemUi,
                        renderingMode = when {
                            previewInfo.showSystemUi -> SessionParams.RenderingMode.NORMAL
                            previewInfo.widthDp > 0 && previewInfo.heightDp > 0 -> SessionParams.RenderingMode.FULL_EXPAND
                            else -> SessionParams.RenderingMode.NORMAL
                        },
                        snapshotHandler = if (System.getProperty("paparazzi.test.verify")?.toBoolean() == true) {
                            PreviewSnapshotVerifier(tolerance)
                        } else {
                            PreviewHtmlReportWriter(tolerance)
                        },
                        maxPercentDifference = tolerance
                    )
                }
            }

            @Composable
            private fun SystemUiSize(
                widthInDp: Int,
                heightInDp: Int,
                content: @Composable () -> Unit
            ) {
                Box(
                    Modifier
                        .size(width = widthInDp.dp, height = heightInDp.dp)
                        .background(Color.White)
                ) {
                    content()
                }
            }

            @Composable
            private fun PreviewSize(
                widthInDp: Int,
                heightInDp: Int,
                content: @Composable () -> Unit
            ) {
                Box(Modifier.size(width = widthInDp.dp, height = heightInDp.dp)) {
                    content()
                }
            }

            @Composable
            private fun PreviewBackground(
                showBackground: Boolean,
                backgroundColor: Long,
                content: @Composable () -> Unit
            ) {
                if (showBackground) {
                    val color = if (backgroundColor != DEFAULT_BACKGROUND_COLOR) Color(backgroundColor) else Color.White
                    Box(Modifier.background(color)) {
                        content()
                    }
                } else {
                    content()
                }
            }

            @RunWith(Parameterized::class)
            class $className(
                private val preview: ComposablePreview<AndroidPreviewInfo>,
            ) {
                @get:Rule
                val paparazzi: Paparazzi = PaparazziPreviewRule.createFor(preview)

                @Test
                fun snapshot() {
                    val screenshotId = AndroidPreviewScreenshotIdBuilder(preview)
                        .doNotIgnoreMethodParametersType()
                        .encodeUnsafeCharacters()
                        .build()

                    paparazzi.snapshot(name = screenshotId) {
                        val previewInfo = preview.previewInfo
                        if (previewInfo.showSystemUi) {
                            val parsedDevice = (DevicePreviewInfoParser.parse(previewInfo.device) ?: DEFAULT).inDp()
                            SystemUiSize(
                                widthInDp = parsedDevice.dimensions.width.toInt(),
                                heightInDp = parsedDevice.dimensions.height.toInt()
                            ) {
                                PreviewBackground(showBackground = true, backgroundColor = previewInfo.backgroundColor) {
                                    preview()
                                }
                            }
                        } else if (previewInfo.widthDp > 0 && previewInfo.heightDp > 0) {
                            PreviewSize(widthInDp = previewInfo.widthDp, heightInDp = previewInfo.heightDp) {
                                PreviewBackground(
                                    showBackground = previewInfo.showBackground,
                                    backgroundColor = previewInfo.backgroundColor
                                ) {
                                    preview()
                                }
                            }
                        } else {
                            PreviewBackground(
                                showBackground = previewInfo.showBackground,
                                backgroundColor = previewInfo.backgroundColor
                            ) {
                                preview()
                            }
                        }
                    }
                }

                companion object {
                    private val cachedPreviews: List<ComposablePreview<AndroidPreviewInfo>> by lazy {
                        AndroidComposablePreviewScanner()
                            .scanPackageTrees($packagesExpression)
                            $includePrivatePreviewsCall
                            .getPreviews()
                    }

                    @JvmStatic
                    @Parameterized.Parameters(name = "{index}: {0}")
                    fun values(): List<ComposablePreview<AndroidPreviewInfo>> = cachedPreviews
                }
            }

            private const val DENSITY_BASE = 160f
            private const val DEFAULT_BACKGROUND_COLOR = 0L
            private const val DEFAULT_LOCALE = "en"
            private const val DEFAULT_TOLERANCE = 0.0
            private const val UNDEFINED_API_LEVEL = -1
            private const val MAX_API_LEVEL = 36
            """.trimIndent()
    }
}
