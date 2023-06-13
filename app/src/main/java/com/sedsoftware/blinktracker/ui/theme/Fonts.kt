package com.sedsoftware.blinktracker.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
fun font(name: String, res: String, weight: FontWeight, style: FontStyle): Font {
    val context = LocalContext.current
    val id = context.resources.getIdentifier(res, "font", context.packageName)
    name
    return Font(id, weight, style)
}

object Fonts {
    @Composable
    fun exo2() = FontFamily(
        font("Exo 2", "exo2_regular", FontWeight.Normal, FontStyle.Normal),
        font("Exo 2", "exo2_regular_italic", FontWeight.Normal, FontStyle.Italic),
        font("Exo 2 SemiBold", "exo2_bold", FontWeight.Bold, FontStyle.Normal),
        font("Exo 2 SemiBold", "exo2_bold_italic", FontWeight.Bold, FontStyle.Italic),
        font("Exo 2 Medium", "exo2_medium", FontWeight.Medium, FontStyle.Normal),
        font("Exo 2 Medium", "exo2_medium_italic", FontWeight.Medium, FontStyle.Italic),
    )
}
