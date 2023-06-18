package com.sedsoftware.blinktracker.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedsoftware.blinktracker.components.statistic.BlinkStatistic
import com.sedsoftware.blinktracker.ui.PreviewStubs
import com.sedsoftware.blinktracker.ui.R
import com.sedsoftware.blinktracker.ui.model.toChartEntries
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme

@Composable
fun StatsPanel(
    model: BlinkStatistic.Model,
    modifier: Modifier = Modifier,
) {

    when {
        !model.checked -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(id = R.string.loading),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(all = 32.dp),
                )
            }
        }

        model.checked && model.showPlaceholder -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(id = R.string.stats_placeholder),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(all = 32.dp),
                )
            }
        }

        else -> {
            StatsPanelDetails(
                min = model.min,
                max = model.max,
                average = model.average,
                entries = model.records.toChartEntries(),
                modifier = modifier,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewStatsLoadingLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.primaryContainer) {
            StatsPanel(
                model = PreviewStubs.statsEmptyNotChecked,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewStatsLoadedLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.primaryContainer) {
            StatsPanel(
                model = PreviewStubs.statsEmptyChecked,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewStatsLoadingDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.primaryContainer) {
            StatsPanel(
                model = PreviewStubs.statsEmptyNotChecked,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewStatsLoadedDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.primaryContainer) {
            StatsPanel(
                model = PreviewStubs.statsEmptyChecked,
            )
        }
    }
}
