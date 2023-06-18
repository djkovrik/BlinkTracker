package com.sedsoftware.blinktracker.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.edges.rememberFadingEdges
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisPosition.Horizontal
import com.patrykandpatrick.vico.core.axis.AxisPosition.Vertical
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.sedsoftware.blinktracker.ui.R
import com.sedsoftware.blinktracker.ui.model.CustomChartEntry

@Composable
fun StatsPanelDetails(
    min: Int,
    max: Int,
    average: Float,
    entries: List<ChartEntry>,
    modifier: Modifier = Modifier,
) {
    val color1: Color = MaterialTheme.colorScheme.primary
    val onColor1: Color = MaterialTheme.colorScheme.onSecondary
    val color2: Color = MaterialTheme.colorScheme.secondary
    val chartColors: List<Color> = listOf(color1, color2)
    val chartEntryModelProducer = remember { ChartEntryModelProducer() }

    LaunchedEffect(entries) {
        chartEntryModelProducer.setEntries(entries)
    }

    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.today),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            ProvideChartStyle(rememberChartStyle(chartColors)) {
                Chart(
                    chart = lineChart(pointPosition = LineChart.PointPosition.Start),
                    chartModelProducer = chartEntryModelProducer,
                    startAxis = startAxis(
                        guideline = null,
                        horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Outside,
                        valueFormatter = startAxisFormatter,
                    ),
                    bottomAxis = bottomAxis(
                        valueFormatter = bottomAxisFormatter,
                        labelRotationDegrees = -90f,
                    ),
                    fadingEdges = rememberFadingEdges(),
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp)
                        .fillMaxSize(),
                )
            }
        }

        Text(
            text = "${stringResource(id = R.string.min)}: $min | " +
                "${stringResource(id = R.string.max)}: $max | " +
                "${stringResource(id = R.string.average)}: $average",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(all = 16.dp)
        )
    }
}

private val startAxisFormatter = AxisValueFormatter<Vertical.Start> { value, _ ->
    "${value.toInt()}"
}

private val bottomAxisFormatter = AxisValueFormatter<Horizontal.Bottom> { value, chartValues ->
    (chartValues.chartEntryModel.entries.first().getOrNull(value.toInt()) as? CustomChartEntry)
        ?.date
        .orEmpty()
}
