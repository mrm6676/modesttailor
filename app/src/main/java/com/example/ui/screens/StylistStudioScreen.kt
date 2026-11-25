package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.AiCuratedOutfit
import com.example.data.model.FashionCatalogConstants
import com.example.data.model.FeedbackVote
import com.example.data.model.InventoryItem
import com.example.data.model.StyleProfile
import com.example.data.model.formattedSizeSummary
import com.example.ui.theme.EditorialScrimBottom
import com.example.ui.theme.EditorialScrimTop
import com.example.ui.theme.atelierSwatchColor
import com.example.ui.viewmodel.DailyEditUiState
import com.example.ui.viewmodel.DataUiState
import com.example.ui.viewmodel.StylistUiState
import com.example.util.OutfitDesignArtworkHelper
import com.example.util.SizeMatchingHelper
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StylistStudioScreen(
    profileState: DataUiState<StyleProfile>,
    inventoryState: DataUiState<List<InventoryItem>>,
    dailyEditState: DailyEditUiState = DailyEditUiState.Idle,
    stylistState: StylistUiState,
    savedLookTitles: Set<String>,
    savingOutfitTitles: Set<String> = emptySet(),
    sharingOutfitTitle: String? = null,
    dailyFeedbackVotes: Map<String, FeedbackVote> = emptyMap(),
    submittingFeedbackTitles: Set<String> = emptySet(),
    savedOutfitsCount: Int = savedLookTitles.size,
    isAuthenticated: Boolean = true,
    onSignInClick: () -> Unit = {},
    onRefreshDailyEdit: () -> Unit = {},
    onGenerateOutfits: (occasion: String, season: String, customNote: String) -> Unit,
    onSaveOutfit: (AiCuratedOutfit) -> Unit,
    onShareOutfit: (AiCuratedOutfit) -> Unit = {},
    onDailyOutfitFeedback: (AiCuratedOutfit, FeedbackVote, String) -> Unit = { _, _, _ -> },
    onSeedStarterInventory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToInventory: () -> Unit = {},
    onNavigateToCloset: () -> Unit = {}
) {
    var selectedOccasion by rememberSaveable {
        mutableStateOf(FashionCatalogConstants.OCCASION_OPTIONS.first())
    }
    var selectedSeason by rememberSaveable {
        mutableStateOf(FashionCatalogConstants.SEASON_OPTIONS.first())
    }
    var customNote by rememberSaveable { mutableStateOf("") }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val profile = (profileState as? DataUiState.Success)?.data
    val inventory = (inventoryState as? DataUiState.Success)?.data ?: emptyList()
    val inStockItems = inventory.filter { it.inStock }
    val inStockCount = inStockItems.size
    val sizeMatchedCount = if (profile != null) {
        SizeMatchingHelper.partitionInventoryBySizeAndCoverage(inStockItems, profile).matchingItems.size
    } else {
        inStockCount
    }

    val showEmptyInventoryBanner = inventoryState is DataUiState.Success && inventory.isEmpty()
    val dailyEditItemIndex = if (showEmptyInventoryBanner) 4 else 3
    val composerItemIndex = if (showEmptyInventoryBanner) 6 else 5

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        val isWide = maxWidth >= 720.dp
        val maxContentWidth = if (isWide) 840.dp else 680.dp

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = maxContentWidth),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Editorial Masthead & Live Atelier Pulse Strip
            item {
                AtelierMastheadSection(
                    profile = profile,
                    sizeMatchedCount = sizeMatchedCount,
                    inStockCount = inStockCount,
                    savedOutfitsCount = savedOutfitsCount,
                    isAuthenticated = isAuthenticated,
                    onSignInClick = onSignInClick,
                    onNavigateToInventory = onNavigateToInventory,
                    onNavigateToCloset = onNavigateToCloset
                )
            }

            // 2. Asymmetric Layered Hero + Overlapping Charter & Palette Ribbon
            item {
                LayeredAtelierHeroSection(
                    profile = profile,
                    inStockCount = inStockCount,
                    sizeMatchedCount = sizeMatchedCount,
                    onNavigateToProfile = onNavigateToProfile
                )
            }

            // 3. Quick Actions Row
            item {
                QuickActionsRowSection(
                    savedOutfitsCount = savedOutfitsCount,
                    onViewDailyRecommendation = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(dailyEditItemIndex)
                        }
                    },
                    onExploreCloset = onNavigateToCloset,
                    onUpdatePreferences = onNavigateToProfile
                )
            }

            // 4. Empty Local Inventory Quick-Load Callout
            if (showEmptyInventoryBanner) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f)),
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.tertiary,
                                    shape = CircleShape,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Storefront,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onTertiary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = stringResource(R.string.atelier_empty_inventory_banner),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            OutlinedButton(
                                onClick = onSeedStarterInventory,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("seed_starter_boutique_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Checkroom,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.atelier_seed_starter_cta))
                            }
                        }
                    }
                }
            }

            // 4. Today's Daily Personalized Edit (Swipeable Lookbook Pager)
            item {
                DailyPersonalizedEditSection(
                    dailyEditState = dailyEditState,
                    savedLookTitles = savedLookTitles,
                    savingOutfitTitles = savingOutfitTitles,
                    sharingOutfitTitle = sharingOutfitTitle,
                    dailyFeedbackVotes = dailyFeedbackVotes,
                    submittingFeedbackTitles = submittingFeedbackTitles,
                    onRefreshDailyEdit = onRefreshDailyEdit,
                    onSaveOutfit = onSaveOutfit,
                    onShareOutfit = onShareOutfit,
                    onDailyOutfitFeedback = onDailyOutfitFeedback,
                    onNavigateToCloset = onNavigateToCloset
                )
            }

            // 5. Visual Editorial Capsule Starters (Interactive Ladies & Girls Preset Cards)
            item {
                EditorialCapsuleStartersSection(
                    onApplyLadiesCapsule = {
                        selectedOccasion = "Work & Studio"
                        selectedSeason = "Crisp Autumn Layering"
                        customNote = "Refined Ladies silhouette pairing pleated drape with tailored outerwear"
                        coroutineScope.launch {
                            listState.animateScrollToItem(composerItemIndex)
                        }
                    },
                    onApplyGirlsCapsule = {
                        selectedOccasion = "Weekend Garden Brunch"
                        selectedSeason = "Warm Sunlit Spring"
                        customNote = "Playful, breathable Girls & Teens modest capsule with full-sleeve comfort"
                        coroutineScope.launch {
                            listState.animateScrollToItem(composerItemIndex)
                        }
                    }
                )
            }

            // 6. Bespoke Occasion & Season Styling Composer
            item {
                BespokeStylistComposerCard(
                    selectedOccasion = selectedOccasion,
                    onSelectOccasion = { selectedOccasion = it },
                    selectedSeason = selectedSeason,
                    onSelectSeason = { selectedSeason = it },
                    customNote = customNote,
                    onCustomNoteChange = { customNote = it },
                    isGenerating = stylistState is StylistUiState.Loading,
                    onGenerate = {
                        onGenerateOutfits(selectedOccasion, selectedSeason, customNote)
                    }
                )
            }

            // 7. Error State
            if (stylistState is StylistUiState.Error) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("stylist_error_card")
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
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    text = stylistState.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            OutlinedButton(
                                onClick = {
                                    onGenerateOutfits(selectedOccasion, selectedSeason, customNote)
                                },
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text(stringResource(R.string.action_retry))
                            }
                        }
                    }
                }
            }

            // 8. Generated Editorial Outfits
            if (stylistState is StylistUiState.Success) {
                item {
                    val accent = MaterialTheme.colorScheme.primary
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically { it / 4 }
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .drawBehind {
                                        drawLine(
                                            color = accent,
                                            start = Offset(16.dp.toPx(), 16.dp.toPx()),
                                            end = Offset(16.dp.toPx(), size.height - 16.dp.toPx()),
                                            strokeWidth = 3.dp.toPx()
                                        )
                                    }
                                    .padding(start = 28.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)
                            ) {
                                Text(
                                    text = "Stylist's Editorial Note",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stylistState.recommendation.editorialSummary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                itemsIndexed(stylistState.recommendation.outfits) { index, outfit ->
                    val normalizedTitle = outfit.title.trim().take(120)
                    val alreadySaved = normalizedTitle in savedLookTitles
                    val isSaving = normalizedTitle in savingOutfitTitles
                    CuratedOutfitCard(
                        index = index,
                        outfit = outfit,
                        alreadySaved = alreadySaved,
                        isSaving = isSaving,
                        isSharing = sharingOutfitTitle == outfit.title,
                        onSaveToggle = { onSaveOutfit(outfit) },
                        onShare = { onShareOutfit(outfit) },
                        onViewCloset = onNavigateToCloset
                    )
                }
            }
        }
    }
}

@Composable
private fun AtelierMastheadSection(
    profile: StyleProfile?,
    sizeMatchedCount: Int,
    inStockCount: Int,
    savedOutfitsCount: Int,
    isAuthenticated: Boolean,
    onSignInClick: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToCloset: () -> Unit
) {
    val firstName = profile?.displayName
        ?.trim()
        ?.split(" ")
        ?.firstOrNull()
        ?.takeIf { it.isNotBlank() && it != "Guest" }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.atelier_masthead_eyebrow),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = if (firstName != null) {
                        "Atelier for $firstName"
                    } else {
                        stringResource(R.string.atelier_header_title)
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (profile != null && profile.cityOrArea.isNotBlank()) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = profile.cityOrArea,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                if (!isAuthenticated) {
                    Surface(
                        onClick = onSignInClick,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("masthead_sign_in_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = stringResource(R.string.cd_sign_in_account),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = stringResource(R.string.atelier_sign_in_chip),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }

        val matchRatio = if (inStockCount > 0) {
            (sizeMatchedCount.toFloat() / inStockCount.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
        val matchPercent = if (inStockCount > 0) (matchRatio * 100f).roundToInt() else 0

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AtelierMetricPill(
                icon = Icons.Default.Checkroom,
                label = stringResource(R.string.atelier_nav_inventory_pill),
                value = if (inStockCount > 0) "$sizeMatchedCount/$inStockCount fit & coverage" else "Empty rack",
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                progress = if (inStockCount > 0) matchRatio else null,
                progressPercent = if (inStockCount > 0) matchPercent else null,
                onClick = onNavigateToInventory,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .testTag("masthead_inventory_shortcut")
            )
            AtelierMetricPill(
                icon = Icons.Default.Bookmarks,
                label = stringResource(R.string.atelier_nav_closet_pill),
                value = "$savedOutfitsCount curated",
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = onNavigateToCloset,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .testTag("masthead_closet_shortcut")
            )
        }
    }
}

@Composable
private fun AtelierMetricPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    containerColor: Color,
    contentColor: Color,
    progress: Float? = null,
    progressPercent: Int? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = containerColor,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.minimumInteractiveComponentSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = contentColor.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        if (progressPercent != null) {
                            Surface(
                                color = contentColor.copy(alpha = 0.14f),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "$progressPercent%",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = contentColor,
                                    modifier = Modifier
                                        .padding(horizontal = 7.dp, vertical = 2.dp)
                                        .testTag("local_boutique_stock_percent")
                                )
                            }
                        }
                    }
                    Text(
                        text = value,
                        style = MaterialTheme.typography.titleSmall,
                        color = contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (progress != null) {
                val animatedProgress by animateFloatAsState(
                    targetValue = progress.coerceIn(0f, 1f),
                    label = "stockMatchProgress"
                )
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = contentColor.copy(alpha = 0.16f),
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                        .testTag("local_boutique_stock_progress")
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LayeredAtelierHeroSection(
    profile: StyleProfile?,
    inStockCount: Int,
    sizeMatchedCount: Int,
    onNavigateToProfile: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            shape = MaterialTheme.shapes.extraLarge,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.height(236.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.img_editorial_hero_1793445199266),
                    contentDescription = stringResource(R.string.cd_hero_banner),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    EditorialScrimTop,
                                    EditorialScrimBottom
                                )
                            )
                        )
                )

                // Top Asymmetric Floating Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.94f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = profile?.audience ?: "Ladies & Girls",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.94f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = if (profile != null && inStockCount > 0) {
                                "$sizeMatchedCount/$inStockCount Size-Matched Pieces"
                            } else {
                                "$inStockCount Local Pieces Ready"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }

                // Editorial Title Positioned Above Bottom Card Overlap
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            bottom = if (profile != null) 44.dp else 20.dp
                        )
                ) {
                    Text(
                        text = stringResource(R.string.atelier_header_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.atelier_header_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Overlapping Active Coverage Charter & Clothing Sizes Ribbon
        if (profile != null) {
            Card(
                onClick = onNavigateToProfile,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 206.dp, start = 10.dp, end = 10.dp)
                    .testTag("active_coverage_summary_card")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = CircleShape,
                                modifier = Modifier.size(30.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = stringResource(R.string.atelier_charter_title),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = stringResource(R.string.atelier_charter_edit),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = "${profile.necklineRule} • ${profile.sleeveRule} • ${profile.hemlineRule}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Straighten,
                            contentDescription = stringResource(R.string.cd_clothing_sizes),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "Saved Sizes: ${profile.formattedSizeSummary()}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (profile.favoriteColors.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            profile.favoriteColors.take(5).forEach { colorName ->
                                val dotColor = atelierSwatchColor(
                                    colorName = colorName,
                                    fallback = MaterialTheme.colorScheme.primary
                                )
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                    shape = CircleShape
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(dotColor)
                                                .border(
                                                    width = 0.75.dp,
                                                    color = MaterialTheme.colorScheme.outline,
                                                    shape = CircleShape
                                                )
                                        )
                                        Text(
                                            text = colorName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionsRowSection(
    savedOutfitsCount: Int,
    onViewDailyRecommendation: () -> Unit,
    onExploreCloset: () -> Unit,
    onUpdatePreferences: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quick_actions_row_section")
    ) {
        Text(
            text = stringResource(R.string.atelier_quick_actions_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionCard(
                icon = Icons.Default.AutoAwesome,
                title = stringResource(R.string.quick_action_daily),
                subtitle = stringResource(R.string.quick_action_daily_sub),
                badgeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                badgeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = onViewDailyRecommendation,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .testTag("quick_action_daily_recommendation")
            )
            QuickActionCard(
                icon = Icons.Default.Bookmarks,
                title = stringResource(R.string.quick_action_closet),
                subtitle = "$savedOutfitsCount saved",
                badgeContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                badgeContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                onClick = onExploreCloset,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .testTag("quick_action_explore_closet")
            )
            QuickActionCard(
                icon = Icons.Default.Tune,
                title = stringResource(R.string.quick_action_preferences),
                subtitle = stringResource(R.string.quick_action_preferences_sub),
                badgeContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                badgeContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                onClick = onUpdatePreferences,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .testTag("quick_action_update_preferences")
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    badgeContainerColor: Color,
    badgeContentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = MaterialTheme.shapes.large,
        modifier = modifier.minimumInteractiveComponentSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = badgeContainerColor,
                shape = CircleShape,
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = badgeContentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun EditorialCapsuleStartersSection(
    onApplyLadiesCapsule: () -> Unit,
    onApplyGirlsCapsule: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = stringResource(R.string.atelier_quick_capsules_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CapsulePhotoCard(
                imageRes = R.drawable.img_ladies_collection_1793445197717,
                title = stringResource(R.string.atelier_capsule_ladies_title),
                subtitle = stringResource(R.string.atelier_capsule_ladies_sub),
                contentDescription = stringResource(R.string.cd_ladies_card),
                onClick = onApplyLadiesCapsule,
                modifier = Modifier
                    .weight(1f)
                    .testTag("capsule_starter_ladies")
            )
            CapsulePhotoCard(
                imageRes = R.drawable.img_girls_collection_1793445199505,
                title = stringResource(R.string.atelier_capsule_girls_title),
                subtitle = stringResource(R.string.atelier_capsule_girls_sub),
                contentDescription = stringResource(R.string.cd_girls_card),
                onClick = onApplyGirlsCapsule,
                modifier = Modifier
                    .weight(1f)
                    .testTag("capsule_starter_girls")
            )
        }
    }
}

@Composable
private fun CapsulePhotoCard(
    imageRes: Int,
    title: String,
    subtitle: String,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .height(152.dp)
            .minimumInteractiveComponentSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                EditorialScrimTop,
                                EditorialScrimBottom
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun BespokeStylistComposerCard(
    selectedOccasion: String,
    onSelectOccasion: (String) -> Unit,
    selectedSeason: String,
    onSelectSeason: (String) -> Unit,
    customNote: String,
    onCustomNoteChange: (String) -> Unit,
    isGenerating: Boolean,
    onGenerate: () -> Unit
) {
    val composerBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(composerBrush)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stringResource(R.string.atelier_composer_eyebrow),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = stringResource(R.string.atelier_custom_section_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "01 · ${stringResource(R.string.atelier_occasion_label)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(FashionCatalogConstants.OCCASION_OPTIONS) { occasion ->
                        FilterChip(
                            selected = selectedOccasion == occasion,
                            onClick = { onSelectOccasion(occasion) },
                            label = { Text(occasion) },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "02 · ${stringResource(R.string.atelier_season_label)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(FashionCatalogConstants.SEASON_OPTIONS) { season ->
                        FilterChip(
                            selected = selectedSeason == season,
                            onClick = { onSelectSeason(season) },
                            label = { Text(season) },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        )
                    }
                }
            }

            OutlinedTextField(
                value = customNote,
                onValueChange = onCustomNoteChange,
                label = { Text("03 · ${stringResource(R.string.atelier_prompt_label)}") },
                placeholder = { Text(stringResource(R.string.atelier_prompt_hint)) },
                minLines = 2,
                maxLines = 4,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("custom_styling_input")
            )

            Button(
                onClick = onGenerate,
                enabled = !isGenerating,
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("generate_outfits_button")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(stringResource(R.string.atelier_generating_status))
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.atelier_generate_cta),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CuratedOutfitCard(
    index: Int,
    outfit: AiCuratedOutfit,
    alreadySaved: Boolean,
    isSaving: Boolean,
    isSharing: Boolean,
    onSaveToggle: () -> Unit,
    onShare: () -> Unit,
    onViewCloset: () -> Unit
) {
    val stripeColor = MaterialTheme.colorScheme.tertiary
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("curated_outfit_card_$index")
    ) {
        Column(
            modifier = Modifier
                .drawBehind {
                    drawLine(
                        color = stripeColor,
                        start = Offset(0f, 24.dp.toPx()),
                        end = Offset(0f, size.height - 24.dp.toPx()),
                        strokeWidth = 4.dp.toPx()
                    )
                }
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val designImageRes = OutfitDesignArtworkHelper.resolveOutfitImage(outfit)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(196.dp)
                    .clip(MaterialTheme.shapes.large)
            ) {
                Image(
                    painter = painterResource(id = designImageRes),
                    contentDescription = "${outfit.title} — ${stringResource(R.string.cd_outfit_design_preview)}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(EditorialScrimTop, EditorialScrimBottom.copy(alpha = 0.45f))
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.94f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "${outfit.audience} • ${outfit.occasion}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.94f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "Look 0${index + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Text(
                text = outfit.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = "Modest Coverage Verified",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = outfit.coverageVerification,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Current Trend Highlight",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = outfit.trendHighlight,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (outfit.matchedPieces.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Matched Pieces & Layers",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        outfit.matchedPieces.forEach { piece ->
                            AssistChip(
                                onClick = {},
                                label = { Text(piece, style = MaterialTheme.typography.bodySmall) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Checkroom,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                }
            }

            if (outfit.boutiqueSources.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Local Sources: ${outfit.boutiqueSources.joinToString(" • ")}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Text(
                text = outfit.stylingNotes,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (alreadySaved && !isSaving) {
                    FilledTonalButton(
                        onClick = onSaveToggle,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("save_outfit_button_$index")
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkAdded,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.atelier_saved_badge))
                    }
                } else {
                    OutlinedButton(
                        onClick = onSaveToggle,
                        enabled = !isSaving,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("save_outfit_button_$index")
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.atelier_saving_look))
                        } else {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.atelier_save_look))
                        }
                    }
                }

                OutlinedButton(
                    onClick = onShare,
                    enabled = !isSharing,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("share_outfit_button_$index")
                ) {
                    if (isSharing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.cd_share_outfit),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.atelier_share_look))
                }
            }

            if (alreadySaved && !isSaving) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onViewCloset,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("view_closet_button_$index")
                    ) {
                        Text(stringResource(R.string.atelier_view_closet))
                    }
                }
            }
        }
    }
}
