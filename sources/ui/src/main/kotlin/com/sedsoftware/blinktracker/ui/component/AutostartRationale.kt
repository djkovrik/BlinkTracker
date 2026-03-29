package com.sedsoftware.blinktracker.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedsoftware.blinktracker.ui.R
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme

@Composable
fun AutostartRationale(
    modifier: Modifier = Modifier,
    onAgree: () -> Unit = {},
    onDisagree: () -> Unit = {},
) {
    Card(
        shape = RoundedCornerShape(size = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
        ),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(all = 16.dp)) {
            Text(
                text = stringResource(id = R.string.overlay_permission_rationale),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .padding(bottom = 16.dp)
            )

            Text(
                text = stringResource(id = R.string.overlay_permission_rationale_ok),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .clickable(onClick = onAgree)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = stringResource(id = R.string.overlay_permission_rationale_cancel),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .clickable(onClick = onDisagree)
                    .padding(bottom = 8.dp)
            )
        }
    }
}

@Composable
@Preview
private fun AutostartRationalePreviewLight() {
    BlinkTrackerTheme(darkTheme = false) {
        AutostartRationale()
    }
}

@Composable
@Preview
private fun AutostartRationalePreviewDark() {
    BlinkTrackerTheme(darkTheme = true) {
        AutostartRationale()
    }
}
