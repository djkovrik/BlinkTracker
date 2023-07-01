@file:OptIn(ExperimentalMaterial3Api::class)

package com.sedsoftware.blinktracker.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedsoftware.blinktracker.components.statistic.model.DisplayedPeriod
import com.sedsoftware.blinktracker.ui.extension.asString
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme

@Composable
fun PeriodChips(
    period: DisplayedPeriod,
    modifier: Modifier = Modifier,
    onSelect: (DisplayedPeriod) -> Unit = {},
) {
    LazyRow(modifier = modifier.fillMaxWidth()) {
        items(DisplayedPeriod.values()) { item ->
            FilterChip(
                selected = item == period,
                onClick = { onSelect.invoke(item) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    labelColor = MaterialTheme.colorScheme.onTertiary,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                ),
                elevation = FilterChipDefaults.filterChipElevation(
                    elevation = 2.dp,
                    pressedElevation = 0.dp,
                ),
                label = {
                    Text(
                        text = item.asString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(all = 0.dp)
                    )
                },
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.padding(all = 4.dp),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ChipsPreviewLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface {
            PeriodChips(
                period = DisplayedPeriod.MINUTE,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ChipsPreviewDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface {
            PeriodChips(
                period = DisplayedPeriod.HOUR,
            )
        }
    }
}
