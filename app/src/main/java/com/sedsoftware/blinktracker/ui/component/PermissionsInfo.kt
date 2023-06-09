package com.sedsoftware.blinktracker.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sedsoftware.blinktracker.R

@Composable
fun PermissionsInfo(
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize(),
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_selfie),
            contentDescription = "Camera icon",
            modifier = modifier
                .size(size = 176.dp)
                .padding(all = 16.dp)
        )

        Text(
            text = message,
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = modifier.padding(horizontal = 32.dp),
        )
    }
}
