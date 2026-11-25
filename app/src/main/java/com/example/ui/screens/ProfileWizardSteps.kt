package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.FashionCatalogConstants

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun WizardIdentityStep(
    displayName: String,
    onDisplayNameChange: (String) -> Unit,
    showNameError: Boolean,
    cityOrArea: String,
    onCityOrAreaChange: (String) -> Unit,
    audience: String,
    onAudienceChange: (String) -> Unit,
    topSize: String,
    onTopSizeChange: (String) -> Unit,
    bottomSize: String,
    onBottomSizeChange: (String) -> Unit,
    dressAbayaLength: String,
    onDressAbayaLengthChange: (String) -> Unit,
    girlsSize: String,
    onGirlsSizeChange: (String) -> Unit,
    matchingInventoryCount: Int,
    totalInStockCount: Int,
    isSavingProfile: Boolean,
    onQuickSaveSizes: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = MaterialTheme.shapes.extraLarge,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("wizard_step_identity")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PersonOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "Step 1 · Shopper Profile & District",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Personalize boutique proximity and wardrobe focus",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedTextField(
                    value = displayName,
                    onValueChange = onDisplayNameChange,
                    label = { Text("Stylist Client Name") },
                    placeholder = { Text("e.g. Amira & Layla") },
                    isError = showNameError,
                    supportingText = if (showNameError) {
                        { Text("Please enter a name for your style charter.") }
                    } else null,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_name_input")
                )

                OutlinedTextField(
                    value = cityOrArea,
                    onValueChange = onCityOrAreaChange,
                    label = { Text("Local Shopping District / City") },
                    placeholder = { Text("e.g. Oakbrook & Promenade") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_city_input")
                )

                Text(
                    text = "Who Are You Styling For?",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FashionCatalogConstants.AUDIENCE_PROFILE_OPTIONS.forEach { option ->
                        val selected = audience == option
                        val subtitle = when (option) {
                            "Ladies" -> "Tailored modest workwear, abayas, and refined occasionwear"
                            "Girls" -> "Breathable cotton tiers, full-sleeve play dresses, easy layers"
                            "Teens" -> "Campus-ready modest denim, co-ords, and crisp layering"
                            else -> "Coordinated ladies, teen, and girls modest wardrobe edits"
                        }
                        Surface(
                            color = if (selected) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            },
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.large)
                                .border(
                                    width = if (selected) 1.5.dp else 1.dp,
                                    color = if (selected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outlineVariant
                                    },
                                    shape = MaterialTheme.shapes.large
                                )
                                .clickable { onAudienceChange(option) }
                                .minimumInteractiveComponentSize()
                                .testTag("audience_option_${option.lowercase().replace(" ", "_").replace("&", "and")}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (selected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        ClothingSizesSectionCard(
            topSize = topSize,
            onTopSizeChange = onTopSizeChange,
            bottomSize = bottomSize,
            onBottomSizeChange = onBottomSizeChange,
            dressAbayaLength = dressAbayaLength,
            onDressAbayaLengthChange = onDressAbayaLengthChange,
            girlsSize = girlsSize,
            onGirlsSizeChange = onGirlsSizeChange,
            matchingInventoryCount = matchingInventoryCount,
            totalInStockCount = totalInStockCount,
            isSavingProfile = isSavingProfile,
            onQuickSaveSizes = onQuickSaveSizes
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ClothingSizesSectionCard(
    topSize: String,
    onTopSizeChange: (String) -> Unit,
    bottomSize: String,
    onBottomSizeChange: (String) -> Unit,
    dressAbayaLength: String,
    onDressAbayaLengthChange: (String) -> Unit,
    girlsSize: String,
    onGirlsSizeChange: (String) -> Unit,
    matchingInventoryCount: Int,
    totalInStockCount: Int,
    isSavingProfile: Boolean,
    onQuickSaveSizes: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("clothing_sizes_section")
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Straighten,
                            contentDescription = stringResource(R.string.cd_clothing_sizes),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = stringResource(R.string.profile_sizes_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.profile_sizes_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (totalInStockCount > 0) {
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.65f),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("size_match_preview_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Checkroom,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "$matchingInventoryCount of $totalInStockCount in-stock local pieces match these sizes",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            SizeChipSelector(
                title = stringResource(R.string.profile_sizes_top_label),
                options = FashionCatalogConstants.LADIES_SIZE_OPTIONS,
                selected = topSize,
                tagPrefix = "size_top_chip",
                onSelect = onTopSizeChange
            )

            SizeChipSelector(
                title = stringResource(R.string.profile_sizes_bottom_label),
                options = FashionCatalogConstants.LADIES_SIZE_OPTIONS,
                selected = bottomSize,
                tagPrefix = "size_bottom_chip",
                onSelect = onBottomSizeChange
            )

            SizeChipSelector(
                title = stringResource(R.string.profile_sizes_length_label),
                options = FashionCatalogConstants.ABAYA_LENGTH_OPTIONS,
                selected = dressAbayaLength,
                tagPrefix = "size_abaya_chip",
                labelTransform = { "${it}\"" },
                onSelect = onDressAbayaLengthChange
            )

            SizeChipSelector(
                title = stringResource(R.string.profile_sizes_girls_label),
                options = FashionCatalogConstants.GIRLS_TEENS_SIZE_OPTIONS,
                selected = girlsSize,
                tagPrefix = "size_girls_chip",
                onSelect = onGirlsSizeChange
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            FilledTonalButton(
                onClick = onQuickSaveSizes,
                enabled = !isSavingProfile,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_sizes_button")
            ) {
                if (isSavingProfile) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.profile_saving_cta))
                } else {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.profile_sizes_save_cta))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SizeChipSelector(
    title: String,
    options: List<String>,
    selected: String,
    tagPrefix: String,
    labelTransform: (String) -> String = { it },
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            options.forEach { option ->
                val slug = option.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_')
                FilterChip(
                    selected = selected == option,
                    onClick = { onSelect(option) },
                    label = { Text(labelTransform(option)) },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("${tagPrefix}_$slug")
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun WizardCoverageStep(
    necklineRule: String,
    onNecklineChange: (String) -> Unit,
    sleeveRule: String,
    onSleeveChange: (String) -> Unit,
    hemlineRule: String,
    onHemlineChange: (String) -> Unit,
    fitRule: String,
    onFitChange: (String) -> Unit,
    hijabFriendly: Boolean,
    onHijabFriendlyChange: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("wizard_step_coverage")
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "Step 2 · Modesty Coverage Charter",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Strict silhouette boundaries Tailor AI enforces on every outfit",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            WizardChipGroup(
                title = "Neckline Coverage",
                options = FashionCatalogConstants.NECKLINE_OPTIONS,
                selected = necklineRule,
                onSelect = onNecklineChange
            )

            WizardChipGroup(
                title = "Sleeve Coverage",
                options = FashionCatalogConstants.SLEEVE_OPTIONS,
                selected = sleeveRule,
                onSelect = onSleeveChange
            )

            WizardChipGroup(
                title = "Skirt & Dress Hemline",
                options = FashionCatalogConstants.HEMLINE_OPTIONS,
                selected = hemlineRule,
                onSelect = onHemlineChange
            )

            WizardChipGroup(
                title = "Silhouette & Drape Fit",
                options = FashionCatalogConstants.FIT_OPTIONS,
                selected = fitRule,
                onSelect = onFitChange
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.profile_hijab_label),
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = stringResource(R.string.profile_hijab_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Switch(
                        checked = hijabFriendly,
                        onCheckedChange = onHijabFriendlyChange,
                        modifier = Modifier.testTag("hijab_friendly_switch")
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun WizardStyleAndPaletteStep(
    selectedTrends: Set<String>,
    onToggleTrend: (String) -> Unit,
    selectedColors: Set<String>,
    onToggleColor: (String) -> Unit,
    validationHint: String?
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("wizard_step_palette")
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "Step 3 · Style Trends & Color Palettes",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Choose aesthetic directions and seasonal color stories",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (validationHint != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = validationHint,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Current Modest Trends",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "${selectedTrends.size} / 10 selected",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FashionCatalogConstants.TREND_TAGS.forEach { tag ->
                    val isSelected = tag in selectedTrends
                    FilterChip(
                        selected = isSelected,
                        onClick = { onToggleTrend(tag) },
                        label = { Text(tag) },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null,
                        modifier = Modifier.minimumInteractiveComponentSize()
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Preferred Color Palette",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "${selectedColors.size} / 10 selected",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FashionCatalogConstants.COLOR_PALETTE.forEach { colorName ->
                    val isSelected = colorName in selectedColors
                    FilterChip(
                        selected = isSelected,
                        onClick = { onToggleColor(colorName) },
                        label = { Text(colorName) },
                        leadingIcon = {
                            ColorSwatchDot(
                                colorName = colorName,
                                selected = isSelected
                            )
                        },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun WizardReviewStep(
    displayName: String,
    cityOrArea: String,
    audience: String,
    topSize: String,
    bottomSize: String,
    dressAbayaLength: String,
    girlsSize: String,
    necklineRule: String,
    sleeveRule: String,
    hemlineRule: String,
    fitRule: String,
    hijabFriendly: Boolean,
    selectedTrends: Set<String>,
    selectedColors: Set<String>,
    savedConfirmationVisible: Boolean,
    onJumpToStep: (Int) -> Unit,
    onNavigateToStylist: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("wizard_step_review")
    ) {
        if (savedConfirmationVisible) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_saved_banner")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Style & Modesty Charter Synced to Firestore",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    OutlinedButton(
                        onClick = onNavigateToStylist,
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .testTag("open_stylist_after_save_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.profile_go_stylist_cta))
                    }
                }
            }
        }

        ReviewSectionCard(
            title = "1. Shopper Profile & District",
            onEdit = { onJumpToStep(0) }
        ) {
            ReviewKeyValueRow("Client Name", displayName.ifBlank { "Modest Stylist" })
            ReviewKeyValueRow("Local District", cityOrArea.ifBlank { "Local Boutique District" })
            ReviewKeyValueRow("Audience Focus", audience)
        }

        ReviewSectionCard(
            title = "Clothing Sizes & Boutique Fit",
            onEdit = { onJumpToStep(0) }
        ) {
            ReviewKeyValueRow("Tops & Outerwear", topSize)
            ReviewKeyValueRow("Bottoms & Skirts", bottomSize)
            ReviewKeyValueRow("Abaya / Dress Length", "${dressAbayaLength}\"")
            ReviewKeyValueRow("Girls / Teens Size", girlsSize)
        }

        ReviewSectionCard(
            title = "2. Modesty Coverage Charter",
            onEdit = { onJumpToStep(1) }
        ) {
            ReviewKeyValueRow("Neckline", necklineRule)
            ReviewKeyValueRow("Sleeves", sleeveRule)
            ReviewKeyValueRow("Hemline", hemlineRule)
            ReviewKeyValueRow("Fit & Drape", fitRule)
            ReviewKeyValueRow("Hijab Styling", if (hijabFriendly) "Coordinated Hijab Included" else "Optional / Standard")
        }

        ReviewSectionCard(
            title = "3. Trends & Color Palette",
            onEdit = { onJumpToStep(2) }
        ) {
            Text(
                text = "Selected Trends (${selectedTrends.size})",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                selectedTrends.forEach { trend ->
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = trend,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Color Palette (${selectedColors.size})",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                selectedColors.forEach { colorName ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ColorSwatchDot(colorName = colorName, selected = false)
                            Text(
                                text = colorName,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewSectionCard(
    title: String,
    onEdit: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                TextButton(
                    onClick = onEdit,
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit $title",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }
            }
            content()
        }
    }
}

@Composable
private fun ReviewKeyValueRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WizardChipGroup(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = selected == option,
                    onClick = { onSelect(option) },
                    label = { Text(option) },
                    modifier = Modifier.minimumInteractiveComponentSize()
                )
            }
        }
    }
}

@Composable
private fun ColorSwatchDot(
    colorName: String,
    selected: Boolean
) {
    val swatchColor = when (colorName) {
        "Rosewood" -> Color(0xFF7D4050)
        "Silk Ivory" -> Color(0xFFF6F1E7)
        "Sage Olive" -> Color(0xFF6B8572)
        "Warm Camel" -> Color(0xFFC29B6F)
        "Dusty Mauve" -> Color(0xFFA87E8B)
        "Chambray Blue" -> Color(0xFF698CB2)
        "Espresso" -> Color(0xFF42312C)
        "Champagne Gold" -> Color(0xFFD8BC82)
        else -> MaterialTheme.colorScheme.primary
    }
    Box(
        modifier = Modifier
            .size(14.dp)
            .clip(CircleShape)
            .background(swatchColor)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                shape = CircleShape
            )
    )
}
