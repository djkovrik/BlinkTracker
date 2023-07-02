package com.sedsoftware.blinktracker.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.edges.rememberFadingEdges
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisPosition.Horizontal
import com.patrykandpatrick.vico.core.axis.AxisPosition.Vertical
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis.HorizontalLabelPosition
import com.patrykandpatrick.vico.core.chart.line.LineChart.PointPosition
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.scroll.AutoScrollCondition
import com.patrykandpatrick.vico.core.scroll.InitialScroll
import com.sedsoftware.blinktracker.components.statistic.BlinkStatistic
import com.sedsoftware.blinktracker.components.statistic.model.CustomChartEntry
import com.sedsoftware.blinktracker.components.statistic.model.DisplayedPeriod
import com.sedsoftware.blinktracker.ui.component.PeriodChips
import com.sedsoftware.blinktracker.ui.component.rememberChartStyle
import com.sedsoftware.blinktracker.ui.preview.PreviewStubs
import com.sedsoftware.blinktracker.ui.theme.BlinkTrackerTheme

@Composable
fun BlinkStatisticContent(
    state: BlinkStatistic.Model,
    modifier: Modifier = Modifier,
    onChipClick: (DisplayedPeriod) -> Unit = {},
) {

    StatsPanelCard(
        model = state,
        modifier = modifier,
        onChipClick = onChipClick,
    )
}

@Composable
private fun StatsPanelCard(
    model: BlinkStatistic.Model,
    modifier: Modifier = Modifier,
    onChipClick: (DisplayedPeriod) -> Unit = {},
) {
    Card(
        shape = RoundedCornerShape(size = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
        ),
        modifier = modifier
            .fillMaxWidth()
    ) {
        StatsPanelDetails(
            min = model.min,
            max = model.max,
            average = model.average,
            period = model.period,
            entries = model.records,
            isLoading = model.isLoading,
            isEmpty = model.isEmpty,
            onChipSelect = onChipClick,
        )
    }
}

@Composable
private fun StatsPanelDetails(
    min: Float,
    max: Float,
    average: Float,
    period: DisplayedPeriod,
    entries: List<ChartEntry>,
    isLoading: Boolean,
    isEmpty: Boolean,
    onChipSelect: (DisplayedPeriod) -> Unit = {},
) {
    val color1: Color = MaterialTheme.colorScheme.primary
    val color2: Color = MaterialTheme.colorScheme.secondary
    val chartColors: List<Color> = listOf(color1, color2)
    val chartEntryModelProducer = remember { ChartEntryModelProducer() }

    LaunchedEffect(entries) {
        chartEntryModelProducer.setEntries(entries)
    }

    Column {
        Text(
            text = stringResource(id = R.string.your_stats),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp)
        )

        AnimatedVisibility(visible = !isLoading && !isEmpty) {
            Text(
                text = "${stringResource(id = R.string.min)}: $min | " +
                    "${stringResource(id = R.string.max)}: $max | " +
                    "${stringResource(id = R.string.average)}: $average",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            when {
                isLoading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
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

                isEmpty -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
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
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        ProvideChartStyle(rememberChartStyle(chartColors)) {
                            Chart(
                                chart = lineChart(
                                    pointPosition = PointPosition.Start,
                                ),
                                chartModelProducer = chartEntryModelProducer,
                                startAxis = startAxis(
                                    guideline = null,
                                    horizontalLabelPosition = HorizontalLabelPosition.Outside,
                                    valueFormatter = startAxisFormatter,
                                ),
                                bottomAxis = bottomAxis(
                                    valueFormatter = bottomAxisFormatter,
                                    labelRotationDegrees = -90f,
                                ),
                                fadingEdges = rememberFadingEdges(),
                                chartScrollSpec = rememberChartScrollSpec(
                                    isScrollEnabled = true,
                                    initialScroll = InitialScroll.End,
                                    autoScrollCondition = AutoScrollCondition.OnModelSizeIncreased,
                                ),
                                modifier = Modifier
                                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp)
                                    .fillMaxSize(),
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(visible = !isLoading) {
            PeriodChips(
                period = period,
                modifier = Modifier.padding(horizontal = 8.dp),
                onSelect = onChipSelect,
            )
        }
    }
}

private val startAxisFormatter = AxisValueFormatter<Vertical.Start> { value, _ ->
    "${value.toInt()}"
}

private val bottomAxisFormatter = AxisValueFormatter<Horizontal.Bottom> { value, chartValues ->
    (chartValues.chartEntryModel.entries.firstOrNull()?.getOrNull(value.toInt()) as? CustomChartEntry)
        ?.label
        .orEmpty()
}

@Composable
@Preview(showBackground = true, widthDp = 400, heightDp = 300)
private fun PreviewStatsLoadingLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.primaryContainer) {
            StatsPanelCard(
                model = PreviewStubs.statsEmptyNotChecked,
            )
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 400, heightDp = 300)
private fun PreviewStatsLoadedLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.primaryContainer) {
            StatsPanelCard(
                model = PreviewStubs.statsEmptyChecked,
            )
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 400, heightDp = 300)
private fun PreviewStatsLoadingDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.primaryContainer) {
            StatsPanelCard(
                model = PreviewStubs.statsEmptyNotChecked,
            )
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 400, heightDp = 300)
private fun PreviewStatsLoadedDark() {
    BlinkTrackerTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.primaryContainer) {
            StatsPanelCard(
                model = PreviewStubs.statsEmptyChecked,
            )
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 400, heightDp = 300)
private fun PreviewStatsContentLight() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.primaryContainer) {
            StatsPanelCard(
                model = PreviewStubs.statsFull,
            )
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 400, heightDp = 300)
private fun PreviewStatsContentDark() {
    BlinkTrackerTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.primaryContainer) {
            StatsPanelCard(
                model = PreviewStubs.statsFull,
            )
        }
    }
}
