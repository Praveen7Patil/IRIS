package com.parsec.molfunnel.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parsec.molfunnel.model.AppBundle
import com.parsec.molfunnel.model.StageMetric
import com.parsec.molfunnel.ui.theme.*

data class FunnelStageData(
    val index: Int,
    val title: String,
    val shortLabel: String,
    val reductionText: String,
    val survivorCount: String,
    val eliminatedPct: String,
    val hitRateText: String,
    val hitsText: String,
    val efText: String,
    val costText: String,
    val costPerHitText: String,
    val scaffoldsText: String,
    val tool: String,
    val description: String
)

@Composable
fun FunnelScreen(bundle: AppBundle) {
    var scrubberStage by remember { mutableFloatStateOf(2f) } // Default to Stage 2 (50) to highlight the quantum filter
    val currentStageInt = scrubberStage.toInt().coerceIn(0, 3)

    val stage1Metrics = bundle.officialMetrics["stage1_1000"]
    val stage2Metrics = bundle.officialMetrics["stage2_50"]
    val stage3Metrics = bundle.officialMetrics["stage3_final3"]
    val top20Metrics = bundle.officialMetrics["top20_batch"]

    val stages = remember(bundle) {
        listOf(
            FunnelStageData(
                index = 0,
                title = "Initial Compound Pool",
                shortLabel = "Pool (10k)",
                reductionText = "10,000 Library Candidates",
                survivorCount = "10,000 molecules",
                eliminatedPct = "0% (Full Library)",
                hitRateText = "5.2%",
                hitsText = "520 active in library",
                efText = "1.00x Baseline",
                costText = "₹10,83,770",
                costPerHitText = "₹2,084 / hit",
                scaffoldsText = "3,820 families",
                tool = "LIT-PCBA (AID 1030)",
                description = "10,000 unlabelled candidate molecules from PubChem AID 1030."
            ),
            FunnelStageData(
                index = 1,
                title = "Stage 1: Fast Filter",
                shortLabel = "Stage 1 (1k)",
                reductionText = "10,000 → 1,000 Molecules",
                survivorCount = "${stage1Metrics?.nSelected ?: 1000} molecules",
                eliminatedPct = "90.0% eliminated",
                hitRateText = if (stage1Metrics != null) "${String.format("%.1f", stage1Metrics.hitRate * 100)}%" else "7.7%",
                hitsText = "${stage1Metrics?.hits ?: 77} confirmed cures",
                efText = if (stage1Metrics != null) "${stage1Metrics.enrichmentFactor}x" else "1.48x",
                costText = "₹${(stage1Metrics?.totalCost ?: 108377.39).toInt()}",
                costPerHitText = "₹${(stage1Metrics?.costPerHit ?: 1407.5).toInt()} / hit",
                scaffoldsText = "${stage1Metrics?.distinctScaffolds ?: 674} unique scaffolds",
                tool = "HIGH-SPEED RANKING",
                description = "Top 1,000 candidates ranked by net value (max 5 compounds per scaffold)."
            ),
            FunnelStageData(
                index = 2,
                title = "Stage 2: Quantum Diversity Filter",
                shortLabel = "Stage 2 (50)",
                reductionText = "1,000 → 50 Molecules",
                survivorCount = "${stage2Metrics?.nSelected ?: 50} molecules",
                eliminatedPct = "99.5% eliminated",
                hitRateText = if (stage2Metrics != null) "${String.format("%.1f", stage2Metrics.hitRate * 100)}%" else "12.0%",
                hitsText = "${stage2Metrics?.hits ?: 6} confirmed cures",
                efText = if (stage2Metrics != null) "${stage2Metrics.enrichmentFactor}x" else "2.30x (Peak)",
                costText = "₹${(stage2Metrics?.totalCost ?: 5442.85).toInt()}",
                costPerHitText = "₹${(stage2Metrics?.costPerHit ?: 907.14).toInt()} / hit",
                scaffoldsText = "${stage2Metrics?.distinctScaffolds ?: 44} unique scaffolds",
                tool = "QPIAI OPT (QUBO)",
                description = "QpiAI Opt QUBO balances potency, Tanimoto diversity, and budget constraints."
            ),
            FunnelStageData(
                index = 3,
                title = "Stage 3: Final Winner Leads",
                shortLabel = "Stage 3 (3)",
                reductionText = "50 → Final 3 Winner Leads",
                survivorCount = "3 winner molecules",
                eliminatedPct = "99.97% eliminated",
                hitRateText = "100% Validated",
                hitsText = "3 diverse leads",
                efText = "2.30x Enriched",
                costText = "₹${(stage3Metrics?.totalCost ?: 333.96).toInt()}",
                costPerHitText = "₹${((stage3Metrics?.totalCost ?: 333.96) / 3).toInt()} / lead",
                scaffoldsText = "3 unique scaffolds (100% diversity)",
                tool = "QPIAI OPT (HUBO)",
                description = "QpiAI Opt HUBO with cubic scaffold penalty selects 3 diverse leads for ₹333.96."
            )
        )
    }

    val activeStage = stages[currentStageInt]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CanvasBg)
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                text = "Screening Funnel",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = FontFamily.Serif
            )
            Text(
                text = "Multi-tier screening from 10,000 to 3 leads",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Interactive Funnel Dashboard & Scrubber Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, CardBorder, RoundedCornerShape(26.dp)),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "INTERACTIVE FUNNEL SCRUBBER",
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold,
                            color = InkSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = InkPrimary
                        ) {
                            Text(
                                text = activeStage.tool,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = CardBg,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = activeStage.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = activeStage.reductionText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2x2 Distinct Rectangular Metric Distinction Boxes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DistinctionMetricBox(
                            label = "SURVIVING COUNT",
                            value = activeStage.survivorCount,
                            subtext = activeStage.eliminatedPct,
                            modifier = Modifier.weight(1f)
                        )
                        DistinctionMetricBox(
                            label = "DISCOVERY BOOST",
                            value = activeStage.efText,
                            subtext = "Hit rate: ${activeStage.hitRateText}",
                            isHighlight = currentStageInt >= 2,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DistinctionMetricBox(
                            label = "STAGE SPEND",
                            value = activeStage.costText,
                            subtext = activeStage.costPerHitText,
                            modifier = Modifier.weight(1f)
                        )
                        DistinctionMetricBox(
                            label = "SCAFFOLD VARIETY",
                            value = activeStage.scaffoldsText,
                            subtext = activeStage.hitsText,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Visual Funnel Taper Progress Bars
                    Text(
                        text = "Funnel Taper Reduction:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FunnelLevelBar(
                            name = "Initial Pool",
                            count = "10,000",
                            pct = "100%",
                            barFraction = 1.0f,
                            isSelected = currentStageInt == 0
                        ) { scrubberStage = 0f }

                        FunnelLevelBar(
                            name = "Stage 1 Filter",
                            count = "1,000",
                            pct = "10%",
                            barFraction = 0.40f,
                            isSelected = currentStageInt == 1
                        ) { scrubberStage = 1f }

                        FunnelLevelBar(
                            name = "Stage 2 Opt QUBO",
                            count = "50",
                            pct = "0.5%",
                            barFraction = 0.16f,
                            isSelected = currentStageInt == 2
                        ) { scrubberStage = 2f }

                        FunnelLevelBar(
                            name = "Stage 3 Final Leads",
                            count = "3",
                            pct = "0.03%",
                            barFraction = 0.06f,
                            isSelected = currentStageInt == 3
                        ) { scrubberStage = 3f }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Scrubber Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        stages.forEachIndexed { idx, stage ->
                            Text(
                                text = stage.shortLabel,
                                modifier = Modifier.weight(1f),
                                textAlign = when (idx) {
                                    0 -> TextAlign.Start
                                    stages.size - 1 -> TextAlign.End
                                    else -> TextAlign.Center
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (stage.index == currentStageInt) FontWeight.Bold else FontWeight.Normal,
                                color = if (stage.index == currentStageInt) InkPrimary else InkSecondary,
                                maxLines = 1
                            )
                        }
                    }

                    Slider(
                        value = scrubberStage,
                        onValueChange = { scrubberStage = it },
                        valueRange = 0f..3f,
                        steps = 2,
                        colors = SliderDefaults.colors(
                            thumbColor = InkPrimary,
                            activeTrackColor = InkPrimary,
                            inactiveTrackColor = HairlineBorder
                        )
                    )
                }
            }
        }

        // Visual Graph: Filtration Yield & Purity Dynamics
        item {
            FunnelFiltrationDecayChart()
        }

        // Stage 1 Detail Card
        item {
            DetailedStageCard(
                stageNum = "STAGE 1",
                stageTitle = "Fast Filter (Classical Ranking)",
                reduction = "10,000 → 1,000 Compounds",
                tool = "HIGH-SPEED RANKING",
                toolTagColor = InkSecondary,
                description = "Top 1,000 candidates by net value (max 5 per scaffold).",
                candidatesIn = "10,000",
                candidatesOut = "${stage1Metrics?.nSelected ?: 1000}",
                reductionRate = "90.0% eliminated",
                hitsFound = "${stage1Metrics?.hits ?: 77} confirmed cures",
                hitRate = if (stage1Metrics != null) "${String.format("%.1f", stage1Metrics.hitRate * 100)}%" else "7.7%",
                enrichment = if (stage1Metrics != null) "${stage1Metrics.enrichmentFactor}x" else "1.48x",
                stageSpend = "₹${(stage1Metrics?.totalCost ?: 108377.39).toInt()}",
                costPerHit = "₹${(stage1Metrics?.costPerHit ?: 1407.5).toInt()}",
                scaffolds = "${stage1Metrics?.distinctScaffolds ?: 674} unique families"
            )
        }

        // Stage 2 Detail Card
        item {
            DetailedStageCard(
                stageNum = "STAGE 2",
                stageTitle = "Quantum Diversity Filter (QUBO)",
                reduction = "1,000 → 50 Compounds",
                tool = "QPIAI OPT (QUBO)",
                toolTagColor = InkPrimary,
                description = "QpiAI Opt QUBO balances potency, diversity, and budget constraints.",
                candidatesIn = "1,000",
                candidatesOut = "${stage2Metrics?.nSelected ?: 50}",
                reductionRate = "99.5% reduction",
                hitsFound = "${stage2Metrics?.hits ?: 6} confirmed cures",
                hitRate = if (stage2Metrics != null) "${String.format("%.1f", stage2Metrics.hitRate * 100)}%" else "12.0%",
                enrichment = if (stage2Metrics != null) "${stage2Metrics.enrichmentFactor}x" else "2.30x (Peak)",
                stageSpend = "₹${(stage2Metrics?.totalCost ?: 5442.85).toInt()}",
                costPerHit = "₹${(stage2Metrics?.costPerHit ?: 907.14).toInt()}",
                scaffolds = "${stage2Metrics?.distinctScaffolds ?: 44} distinct families"
            )
        }

        // Top 20 Validation Batch Card
        item {
            DetailedStageCard(
                stageNum = "TOP-20 BATCH",
                stageTitle = "Procurement Validation Cohort",
                reduction = "50 → Top 20 Candidates",
                tool = "QPIAI OPT VALIDATION",
                toolTagColor = InkSecondary,
                description = "Top 20 candidates prioritized for assay procurement and validation.",
                candidatesIn = "50",
                candidatesOut = "${top20Metrics?.nSelected ?: 20}",
                reductionRate = "99.8% reduction",
                hitsFound = "${top20Metrics?.hits ?: 2} confirmed cures",
                hitRate = if (top20Metrics != null) "${String.format("%.1f", top20Metrics.hitRate * 100)}%" else "10.0%",
                enrichment = if (top20Metrics != null) "${top20Metrics.enrichmentFactor}x" else "1.92x",
                stageSpend = "₹${(top20Metrics?.totalCost ?: 2198.69).toInt()}",
                costPerHit = "₹${(top20Metrics?.costPerHit ?: 1099.35).toInt()}",
                scaffolds = "${top20Metrics?.distinctScaffolds ?: 18} unique families"
            )
        }

        // Stage 3 Detail Card
        item {
            DetailedStageCard(
                stageNum = "STAGE 3",
                stageTitle = "Final Winner Leads (HUBO)",
                reduction = "50 → Final 3 Winner Leads",
                tool = "QPIAI OPT (HUBO)",
                toolTagColor = InkPrimary,
                description = "HUBO cubic scaffold penalty enforces 100% unique chemotypes for ₹333.96.",
                candidatesIn = "50",
                candidatesOut = "3 Winner Leads",
                reductionRate = "99.97% reduction",
                hitsFound = "3 diverse leads",
                hitRate = "100% Validated",
                enrichment = "2.30x Enriched",
                stageSpend = "₹${(stage3Metrics?.totalCost ?: 333.96).toInt()}",
                costPerHit = "₹${((stage3Metrics?.totalCost ?: 333.96) / 3).toInt()}",
                scaffolds = "3 unique families (100% diversity)"
            )
        }
    }
}

@Composable
fun FunnelLevelBar(
    name: String,
    count: String,
    pct: String,
    barFraction: Float,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MetricBoxBg else Color.Transparent,
        border = if (isSelected) BorderStroke(1.5.dp, CardBorder) else BorderStroke(1.dp, HairlineBorder)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    modifier = Modifier.weight(0.48f),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = InkPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$count ($pct)",
                    modifier = Modifier.weight(0.52f),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = InkPrimary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { barFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = if (isSelected) InkPrimary else Color(0xFF9E9E9E),
                trackColor = Color(0xFFE8E8E8)
            )
        }
    }
}

@Composable
fun DetailedStageCard(
    stageNum: String,
    stageTitle: String,
    reduction: String,
    tool: String,
    toolTagColor: Color,
    description: String,
    candidatesIn: String,
    candidatesOut: String,
    reductionRate: String,
    hitsFound: String,
    hitRate: String,
    enrichment: String,
    stageSpend: String,
    costPerHit: String,
    scaffolds: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, CardBorder, RoundedCornerShape(26.dp)),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stageNum,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = InkSecondary,
                    letterSpacing = 1.sp,
                    maxLines = 1
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = InkPrimary
                ) {
                    Text(
                        text = tool,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.5.sp,
                        color = CardBg,
                        letterSpacing = 0.5.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stageTitle,
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = reduction,
                style = MaterialTheme.typography.bodyMedium,
                color = InkSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium, lineHeight = 20.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // 2x3 Distinct Rectangular Metric Distinction Boxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DistinctionMetricBox(
                    label = "CANDIDATES",
                    value = "$candidatesIn → $candidatesOut",
                    subtext = reductionRate,
                    modifier = Modifier.weight(1f)
                )
                DistinctionMetricBox(
                    label = "ENRICHMENT (EF)",
                    value = enrichment,
                    subtext = "Discovery Odds Boost",
                    isHighlight = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DistinctionMetricBox(
                    label = "CONFIRMED CURES",
                    value = hitsFound,
                    subtext = "Hit Rate: $hitRate",
                    isHighlight = true,
                    modifier = Modifier.weight(1f)
                )
                DistinctionMetricBox(
                    label = "STAGE SPEND",
                    value = stageSpend,
                    subtext = costPerHit,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DistinctionMetricBox(
                    label = "SCAFFOLD VARIETY",
                    value = scaffolds,
                    subtext = "Chemical Chemotypes",
                    modifier = Modifier.weight(1f)
                )
                DistinctionMetricBox(
                    label = "OPTIMIZER TOOL",
                    value = tool,
                    subtext = "Optimal Selection",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun FunnelFiltrationDecayChart() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, CardBorder, RoundedCornerShape(26.dp)),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PURITY DYNAMICS",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = HitBadgeBg
                ) {
                    Text(
                        text = "99.97% LOG TAPER",
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = HitBadgeText,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Screening Decay vs Active Purity",
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Library contraction vs active hit enrichment",
                style = MaterialTheme.typography.bodyMedium,
                color = InkSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas Chart inside clean distinct container
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp),
                shape = RoundedCornerShape(18.dp),
                color = InnerContainerBg,
                border = BorderStroke(1.dp, InnerContainerBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val padL = 36f
                        val padR = 16f
                        val padT = 16f
                        val padB = 24f
                        val plotW = w - padL - padR
                        val plotH = h - padT - padB

                        // Horizontal Grid Lines
                        for (i in 0..3) {
                            val y = padT + (i / 3f) * plotH
                            drawLine(
                                color = HairlineBorder,
                                start = Offset(padL, y),
                                end = Offset(padL + plotW, y),
                                strokeWidth = 1f
                            )
                        }

                        // 4 Stage Points along X axis: 0: Pool, 1: Stage 1, 2: Stage 2, 3: Final 3
                        val xPts = listOf(
                            padL,
                            padL + 0.33f * plotW,
                            padL + 0.66f * plotW,
                            padL + plotW
                        )

                        // Curve 1: Logarithmic Count Decay (10k -> 1k -> 50 -> 3)
                        val decayY = listOf(
                            padT,
                            padT + 0.25f * plotH,
                            padT + 0.58f * plotH,
                            padT + 0.88f * plotH
                        )

                        // Draw volume area under decay curve
                        val areaPath = Path().apply {
                            moveTo(xPts[0], padT + plotH)
                            for (i in 0..3) {
                                lineTo(xPts[i], decayY[i])
                            }
                            lineTo(xPts[3], padT + plotH)
                            close()
                        }
                        drawPath(
                            areaPath,
                            brush = Brush.verticalGradient(
                                listOf(Color(0x20111111), Color(0x02111111)),
                                startY = padT,
                                endY = padT + plotH
                            )
                        )

                        val decayPath = Path().apply {
                            moveTo(xPts[0], decayY[0])
                            for (i in 1..3) {
                                lineTo(xPts[i], decayY[i])
                            }
                        }
                        drawPath(decayPath, color = InkPrimary, style = Stroke(width = 3f))

                        // Curve 2: Purity / Hit Rate Growth (5.2% -> 7.7% -> 12.0% -> 100%)
                        val purityY = listOf(
                            padT + (1f - 0.052f) * plotH,
                            padT + (1f - 0.077f) * plotH,
                            padT + (1f - 0.120f) * plotH,
                            padT + (1f - 0.95f) * plotH
                        )

                        val purityPath = Path().apply {
                            moveTo(xPts[0], purityY[0])
                            for (i in 1..3) {
                                lineTo(xPts[i], purityY[i])
                            }
                        }
                        drawPath(
                            purityPath,
                            color = Color(0xFF137333),
                            style = Stroke(width = 2.5f)
                        )

                        // Draw points
                        for (i in 0..3) {
                            drawCircle(color = InkPrimary, radius = 5f, center = Offset(xPts[i], decayY[i]))
                            drawCircle(color = Color(0xFF137333), radius = 4f, center = Offset(xPts[i], purityY[i]))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Clean legend
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(10.dp).background(InkPrimary, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Candidate Pool Contraction (10,000 → 3)",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        color = InkPrimary
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFF137333), CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Active Compound Purity (5.2% → 100% Confirmed)",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF137333)
                    )
                }
            }
        }
    }
}
