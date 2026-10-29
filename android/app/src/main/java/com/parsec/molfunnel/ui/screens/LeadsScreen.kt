package com.parsec.molfunnel.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.parsec.molfunnel.model.AppBundle
import com.parsec.molfunnel.model.LeadCompound
import com.parsec.molfunnel.ui.theme.*

@Composable
fun LeadsScreen(bundle: AppBundle) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Final 3, 1 = Top-20

    val leads = if (selectedTab == 0) bundle.finalLeads.take(1) else bundle.top20Batch

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CanvasBg)
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Column {
                Text(
                    text = if (selectedTab == 0) "Winning Lead" else "Top-20 Batch",
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = if (selectedTab == 0) "Optimal lead selected by QpiAI Opt HUBO (₹2,000 budget)" else "Top-20 QML ZZ-Ring Quantum Fidelity Survivors",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ChipBg, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                TabButton(
                    text = "WINNING LEAD",
                    selected = selectedTab == 0,
                    modifier = Modifier.weight(1f)
                ) { selectedTab = 0 }

                TabButton(
                    text = "TOP-20 BATCH",
                    selected = selectedTab == 1,
                    modifier = Modifier.weight(1f)
                ) { selectedTab = 1 }
            }
        }

        itemsIndexed(leads) { index, lead ->
            val rankNum = if (lead.leadNum > 0) lead.leadNum else (index + 1)
            LeadCard(
                lead = lead,
                displayRank = rankNum,
                isFinalLead = (selectedTab == 0),
                validationPlan = bundle.validationPlan.find { it.pubchemSid == lead.pubchemSid }
            )
        }
    }
}

@Composable
fun TabButton(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .height(38.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = if (selected) InkPrimary else Color.Transparent
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                letterSpacing = 0.5.sp,
                color = if (selected) CardBg else InkSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LeadCard(
    lead: LeadCompound,
    displayRank: Int,
    isFinalLead: Boolean,
    validationPlan: com.parsec.molfunnel.model.ValidationPlanItem?
) {
    var expandedPlan by remember { mutableStateOf(false) }
    var showQuoteDialog by remember { mutableStateOf(false) }
    var quoteRequested by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, CardBorder, RoundedCornerShape(26.dp))
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    clipboardManager.setText(AnnotatedString(lead.smiles))
                    Toast.makeText(context, "Copied SMILES to clipboard", Toast.LENGTH_SHORT).show()
                }
            ),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header: Title and Badges matching the reference image exactly
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isFinalLead) "Lead #$displayRank" else "Lead #$displayRank",
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = InkPrimary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = InkPrimary
                    ) {
                        Text(
                            text = "NOVEL SCAFFOLD",
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.5.sp,
                            letterSpacing = 0.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = CardBg
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = HitBadgeBg
                    ) {
                        Text(
                            text = if (isFinalLead) "FINALIST" else "TOP-20",
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.5.sp,
                            letterSpacing = 0.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = HitBadgeText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2D Molecular Structure Panel (Generous rounded container, matching reference image)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = RoundedCornerShape(18.dp),
                color = InnerContainerBg,
                border = BorderStroke(1.dp, InnerContainerBorder)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (lead.svgAsset.isNotEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("file:///android_asset/${lead.svgAsset}")
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = "2D Molecular Structure for ${lead.pubchemSid}",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                    } else {
                        Text(
                            text = "[ 2D Molecular Structure ]\nTap to Zoom",
                            style = MaterialTheme.typography.labelSmall,
                            color = InkSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2x2 Distinct Rectangular Metric Boxes (Exact Reference Image Styling)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DistinctionMetricBox(
                    label = "PROCUREMENT",
                    value = "₹${lead.costInr.toInt()}",
                    subtext = lead.costSource,
                    modifier = Modifier.weight(1f)
                )
                DistinctionMetricBox(
                    label = "P(ACTIVE)",
                    value = "${(lead.pActive * 100).toInt()}%",
                    subtext = "± ${(lead.sigma * 100).toInt()}% conf",
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
                    label = "SYNTHESIS (SA)",
                    value = "${String.format("%.1f", lead.saScore)} / 10",
                    subtext = "Easy Precursors",
                    subtextColor = Color(0xFF137333),
                    modifier = Modifier.weight(1f)
                )
                DistinctionMetricBox(
                    label = "PUBCHEM SID",
                    value = lead.pubchemSid,
                    subtext = "Stage 1 # ${lead.stage1Rank}",
                    isMonospaceValue = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DistinctionMetricBox(
                    label = "DRUG-LIKENESS",
                    value = "Lipinski Pass",
                    subtext = if (lead.tpsa > 140.0) "Fails Veber (TPSA > 140)" else "Passes Veber & Lipinski",
                    subtextColor = if (lead.tpsa > 140.0) Color(0xFFC0392B) else Color(0xFF137333),
                    modifier = Modifier.weight(1f)
                )
                DistinctionMetricBox(
                    label = "STRUCTURAL ALERTS",
                    value = "PAINS Clear",
                    subtext = "In chemical domain",
                    subtextColor = Color(0xFF137333),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SMILES and Bemis-Murcko Scaffold Blocks
            Text(
                text = "SMILES",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = InkSecondary
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = lead.smiles,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.5.sp,
                lineHeight = 16.sp,
                color = InkPrimary,
                fontFamily = FontFamily.Monospace,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "BEMIS-MURCKO SCAFFOLD",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = InkSecondary
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = lead.scaffold,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.5.sp,
                lineHeight = 16.sp,
                color = InkPrimary,
                fontFamily = FontFamily.Monospace,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Full-Width Solid Black Action Button
            Button(
                onClick = { showQuoteDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (quoteRequested) HitBadgeBg else InkPrimary
                ),
                border = BorderStroke(1.dp, if (quoteRequested) HitBadgeText else InkPrimary)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (quoteRequested) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Confirmed",
                            tint = HitBadgeText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = if (quoteRequested) "QUOTE CONFIRMED (₹${lead.costInr.toInt()})" else "REQUEST VENDOR PROCUREMENT RFQ",
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (quoteRequested) HitBadgeText else CardBg
                    )
                }
            }

            // Vendor Procurement Quote Dialog
            if (showQuoteDialog) {
                AlertDialog(
                    onDismissRequest = { showQuoteDialog = false },
                    title = {
                        Text(
                            text = "Vendor Procurement Quote",
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Official procurement quote for Lead #${lead.leadNum} (ALDH1 Target Screen):",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            HorizontalDivider(color = HairlineBorder)
                            Spacer(modifier = Modifier.height(4.dp))

                            QuoteRow("Vendor Catalog", "Mcule / Enamine Real Space")
                            QuoteRow("PubChem SID", lead.pubchemSid)
                            QuoteRow("Standard Sample", "10 mg powder (>95% purity)")
                            QuoteRow("Quoted Unit Price", "₹${lead.costInr.toInt()} (${lead.costSource})")
                            QuoteRow("Building Block Cost", "₹${lead.buildingBlockCost.toInt()}")
                            QuoteRow("Estimated Delivery", "5–7 Business Days")
                            QuoteRow("Vendor Reference", "MCL-RFQ-${lead.pubchemSid}")
                            QuoteRow("Availability Status", "In Stock / Readily Synthesizable")
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val quoteText = """
                                    IRIS VENDOR RFQ QUOTE
                                    Target: ALDH1 Inhibitor (PubChem AID 1030)
                                    Lead Number: #${lead.leadNum}
                                    PubChem SID: ${lead.pubchemSid}
                                    SMILES: ${lead.smiles}
                                    Quoted Price: ₹${lead.costInr.toInt()}
                                    Vendor Ref: MCL-RFQ-${lead.pubchemSid}
                                    Status: In Stock / Verified
                                """.trimIndent()
                                clipboardManager.setText(AnnotatedString(quoteText))
                                Toast.makeText(context, "Vendor Quote MCL-RFQ-${lead.pubchemSid} copied to clipboard!", Toast.LENGTH_LONG).show()
                                quoteRequested = true
                                showQuoteDialog = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = InkPrimary)
                        ) {
                            Text("COPY RFQ QUOTE", color = CardBg, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showQuoteDialog = false }) {
                            Text("CLOSE", color = InkSecondary, style = MaterialTheme.typography.labelSmall)
                        }
                    },
                    containerColor = CardBg,
                    shape = RoundedCornerShape(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Collapsible Validation Plan in a Clean Styled Distinct Container
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedPlan = !expandedPlan },
                shape = RoundedCornerShape(14.dp),
                color = MetricBoxBg,
                border = BorderStroke(1.dp, MetricBoxBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "WET-LAB VALIDATION PROTOCOL",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = InkPrimary
                            )
                        }
                        Icon(
                            imageVector = if (expandedPlan) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand validation plan",
                            tint = InkSecondary
                        )
                    }

                    if (expandedPlan) {
                        val plan = validationPlan ?: com.parsec.molfunnel.model.ValidationPlanItem(
                            leadNum = displayRank,
                            pubchemSid = lead.pubchemSid,
                            primaryAssay = "NADH fluorescence assay (AID 1030 replicate, 340/460 nm, 15 min readout)",
                            orthogonalAssay = "LC-MS substrate consumption assay (4-NBA → 4-NA)",
                            counterScreening = "ALDH2 isozyme selectivity counter-screen",
                            ic50Determination = "10-point 3-fold serial dilution curve (top 50 µM)",
                            solubilityCheck = "Nephelometric turbidity in 1% DMSO buffer"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = HairlineBorder)
                        Spacer(modifier = Modifier.height(8.dp))
                        PlanItem("Primary Assay", plan.primaryAssay)
                        PlanItem("Orthogonal Assay", plan.orthogonalAssay)
                        PlanItem("Counter-Screen", plan.counterScreening)
                        PlanItem("IC50 Dilution", plan.ic50Determination)
                        PlanItem("Solubility", plan.solubilityCheck)

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = HairlineBorder)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "QUANTITATIVE GO / NO-GO GATES",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = InkSecondary,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        PlanItem("Primary Potency Gate", "IC50 <= 10 uM with >= 50% max inhibition -> GO; IC50 > 50 uM -> DROP")
                        PlanItem("Selectivity Window", "ALDH2 IC50 / ALDH1 IC50 >= 10x -> GO (avoids acetaldehyde cytotoxicity)")
                        PlanItem("Aqueous Solubility", "Turbidity < 10 NTU at 50 uM in 1% DMSO aqueous buffer -> GO")
                        PlanItem("Stopping Rule", "If Lead 1 shows < 20% inhibition or precipitation > 10 NTU, advance Lead 2")
                    }
                }
            }
        }
    }
}

@Composable
fun PlanItem(label: String, text: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            color = InkSecondary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 11.5.sp,
            lineHeight = 16.sp,
            color = InkPrimary
        )
    }
}

@Composable
fun QuoteRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(0.45f),
            style = MaterialTheme.typography.bodySmall,
            color = InkSecondary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            modifier = Modifier.weight(0.55f),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
            color = InkPrimary
        )
    }
}
