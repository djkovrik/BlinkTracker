@file:OptIn(ExperimentalMaterial3Api::class)

package com.sedsoftware.blinktracker.ui.component

import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sedsoftware.blinktracker.components.statistic.model.DisplayedPeriod
import com.sedsoftware.blinktracker.ui.extension.asString
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme
import kotlinx.coroutines.launch

@Composable
fun PeriodChips(
    period: DisplayedPeriod,
    modifier: Modifier = Modifier,
    onSelect: (DisplayedPeriod) -> Unit = {},
) {

    val listState = rememberLazyListState()

    LazyRow(
        horizontalArrangement = Arrangement.Center,
        state = listState,
        modifier = modifier.fillMaxWidth()
    ) {
        items(DisplayedPeriod.values()) { item ->
            val isSelected = item == period
            FilterChip(
                selected = isSelected,
                onClick = { onSelect.invoke(item) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                ),
                label = {
                    Text(
                        text = item.asString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        },
                        modifier = Modifier.padding(all = 0.dp)
                    )
                },
                border = if (!isSelected) {
                    FilterChipDefaults.filterChipBorder(
                        borderColor = MaterialTheme.colorScheme.primary,
                        borderWidth = 1.dp,
                    )
                } else {
                    null
                },
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.padding(all = 4.dp),
            )
        }
    }

    val itemSize = 120.dp
    val density = LocalDensity.current
    val itemSizePx = with(density) { itemSize.toPx() }

    LaunchedEffect(key1 = period) {
        val selectedIndex = DisplayedPeriod.values().indexOf(period)

        if (selectedIndex < LIST_SCROLL_THRESHOLD) {
            launch {
                listState.animateScrollBy(
                    value = -itemSizePx,
                    animationSpec = tween(durationMillis = LIST_SCROLL_DURATION)
                )
            }
        }

        if (selectedIndex == DisplayedPeriod.values().size - LIST_SCROLL_THRESHOLD) {
            launch {
                listState.animateScrollBy(
                    value = itemSizePx,
                    animationSpec = tween(durationMillis = LIST_SCROLL_DURATION)
                )
            }
        }
    }
}

private const val LIST_SCROLL_THRESHOLD = 2
private const val LIST_SCROLL_DURATION = 500

@Composable
@Preview(showBackground = true)
fun ChipsPreviewLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.primaryContainer) {
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
        Surface(color = MaterialTheme.colorScheme.primaryContainer) {
            PeriodChips(
                period = DisplayedPeriod.HOUR,
            )
        }
    }
}
