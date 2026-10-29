package com.parsec.molfunnel.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Reusable rectangular metric distinction card matching the reference design:
 * - Upper category label (uppercase, tracked, subtle)
 * - Primary value (large, bold, high contrast)
 * - Lower descriptive qualifier/status
 * - Support for standard neutral state or positive highlight (soft green tint with green border)
 */
@Composable
fun DistinctionMetricBox(
    label: String,
    value: String,
    subtext: String,
    modifier: Modifier = Modifier,
    isHighlight: Boolean = false,
    valueColor: Color? = null,
    subtextColor: Color? = null,
    isMonospaceValue: Boolean = false
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = if (isHighlight) MetricBoxHighlightBg else MetricBoxBg,
        border = BorderStroke(1.dp, if (isHighlight) MetricBoxHighlightBorder else MetricBoxBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                letterSpacing = 0.6.sp,
                fontWeight = FontWeight.Bold,
                color = if (isHighlight) MetricBoxHighlightText else InkSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = if (isMonospaceValue) FontFamily.Monospace else FontFamily.Default,
                color = valueColor ?: if (isHighlight) MetricBoxHighlightText else InkPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = subtextColor ?: if (isHighlight) MetricBoxHighlightText.copy(alpha = 0.85f) else InkSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = InkSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = InkPrimary
        )
    }
}
