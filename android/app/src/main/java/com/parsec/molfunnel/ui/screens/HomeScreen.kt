package com.parsec.molfunnel.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parsec.molfunnel.model.AppBundle
import com.parsec.molfunnel.ui.theme.*

@Composable
fun HomeScreen(
    bundle: AppBundle,
    onNavigateToTab: (Int) -> Unit
) {
    var inWalkthrough by remember { mutableStateOf(false) }
    var currentStep by remember { mutableIntStateOf(1) }

    val steps = listOf(
        Pair("1. Target Disease & Problem Scope", "ALDH1 inhibition in cancer stem cells under strict ₹2,000 assay budget for translational oncology discovery.") to 0,
        Pair("2. Assay Data Cleanse", "145,133 records curated with RDKit: salt stripping, canonicalization, Murcko scaffolds.") to 0,
        Pair("3. Zero-Overlap Split", "3,000 training vs 10,000 screening candidates with strictly 0% scaffold leakage.") to 0,
        Pair("4. 8-Qubit Quantum Kernel", "ZZ-ring fidelity kernel mapping molecular phase in Hilbert space via QpiAI Quantum SDK.") to 3,
        Pair("5. Stage 1 Pre-Filter", "10,000 → 1,000 candidates (77 true hits, 1.48x enrichment, max 5/scaffold).") to 0,
        Pair("6. Stage 2 Opt QUBO", "1,000 → 50 survivors (6 true hits, 2.30x enrichment, 100% unique scaffolds).") to 0,
        Pair("7. Stage 3 Native HUBO", "Narrows down to top lead chemotypes using QpiAI Opt HUBO with cubic scaffold penalty.") to 1,
        Pair("8. Blinded Integrity", "Zero label leakage during screening: prospectively verified with test answers locked.") to 3,
        Pair("9. Statistical Rigor", "Friedman test & Wilcoxon testing reported honestly across 5 seeds.") to 3,
        Pair("10. Chemical Novelty", "100% of hit scaffolds in selected batch are structurally distinct from training folds.") to 1,
        Pair("11. Procurement Cost", "Itemized catalog costing: single lead cost ₹112.82 under strict ₹2,000 budget.") to 1,
        Pair("12. Wet-Lab Protocol", "NADH fluorescence primary assay, LC-MS, and ALDH2 counter-screen.") to 1
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CanvasBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!inWalkthrough) {
            // Main Hero Editorial Card (Original Clean Style)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(1.5.dp, CardBorder, RoundedCornerShape(26.dp)),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "IRIS",
                        style = MaterialTheme.typography.labelMedium,
                        letterSpacing = 3.sp,
                        color = InkSecondary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Decorative Dotted Blob Canvas (Original Editorial Style)
                    Canvas(
                        modifier = Modifier
                            .size(130.dp)
                            .padding(8.dp)
                    ) {
                        drawCircle(color = BlobDeep, radius = size.minDimension / 2.2f)
                        val step = 13f
                        for (x in 20..size.width.toInt() - 20 step step.toInt()) {
                            for (y in 20..size.height.toInt() - 20 step step.toInt()) {
                                val dx = x - size.width / 2
                                val dy = y - size.height / 2
                                if (dx * dx + dy * dy < (size.minDimension / 2.4f) * (size.minDimension / 2.4f)) {
                                    drawCircle(color = CardBg, radius = 2f, center = Offset(x.toFloat(), y.toFloat()))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "From 10,000\nmolecules to 1",
                        style = MaterialTheme.typography.headlineLarge,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = InkPrimary,
                        lineHeight = 36.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = { inWalkthrough = true },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = InkPrimary),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(50.dp)
                    ) {
                        Text(
                            text = "START WALKTHROUGH",
                            style = MaterialTheme.typography.labelMedium,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = CardBg
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = { onNavigateToTab(1) },
                        shape = CircleShape,
                        border = BorderStroke(1.dp, CardBorder),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(48.dp)
                    ) {
                        Text(
                            text = "VIEW WINNING LEAD",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = InkPrimary
                        )
                    }
                }
            }
        } else {
            // Walkthrough Card
            val currentPair = steps[currentStep - 1]
            val stepTitle = currentPair.first.first
            val stepDesc = currentPair.first.second
            val targetTab = currentPair.second

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(1.5.dp, CardBorder, RoundedCornerShape(26.dp)),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = InkPrimary
                        ) {
                            Text(
                                text = "STEP $currentStep/12",
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = CardBg,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        TextButton(onClick = { inWalkthrough = false }) {
                            Text("CLOSE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = InkSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stepTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = InkPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MetricBoxBg,
                        border = BorderStroke(1.dp, MetricBoxBorder)
                    ) {
                        Text(
                            text = stepDesc,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 22.sp,
                            color = InkPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                if (currentStep > 1) currentStep--
                            },
                            enabled = currentStep > 1
                        ) {
                            Text(
                                "PREVIOUS",
                                color = if (currentStep > 1) InkPrimary else Color.Transparent,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                if (currentStep < 12) {
                                    currentStep++
                                } else {
                                    inWalkthrough = false
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = InkPrimary)
                        ) {
                            Text(
                                text = if (currentStep < 12) "NEXT STEP" else "FINISH",
                                color = CardBg,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = {
                            inWalkthrough = false
                            onNavigateToTab(targetTab)
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Open section in app →", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = InkPrimary)
                    }
                }
            }
        }
    }
}
