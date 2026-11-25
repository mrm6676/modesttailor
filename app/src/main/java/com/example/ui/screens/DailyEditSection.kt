package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.AiCuratedOutfit
import com.example.data.model.FeedbackVote
import com.example.ui.theme.EditorialScrimBottom
import com.example.ui.theme.EditorialScrimTop
import com.example.ui.viewmodel.DailyEditUiState
import com.example.util.OutfitDesignArtworkHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DailyPersonalizedEditSection(
    dailyEditState: DailyEditUiState,
    savedLookTitles: Set<String>,
    savingOutfitTitles: Set<String>,
    sharingOutfitTitle: String? = null,
    dailyFeedbackVotes: Map<String, FeedbackVote> = emptyMap(),
    submittingFeedbackTitles: Set<String> = emptySet(),
    onRefreshDailyEdit: () -> Unit,
    onSaveOutfit: (AiCuratedOutfit) -> Unit,
    onShareOutfit: (AiCuratedOutfit) -> Unit = {},
    onDailyOutfitFeedback: (AiCuratedOutfit, FeedbackVote, String) -> Unit = { _, _, _ -> },
    onNavigateToCloset: () -> Unit
) {
    val primaryAccent = MaterialTheme.colorScheme.primary

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("daily_personalized_edit_section")
    ) {
        Column(
            modifier = Modifier.padding(vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.78f),
                    shape = CircleShape
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = stringResource(R.string.atelier_daily_badge),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                FilledTonalIconButton(
                    onClick = onRefreshDailyEdit,
                    enabled = dailyEditState !is DailyEditUiState.Loading,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("refresh_daily_edit_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.atelier_daily_refresh_cd)
                    )
                }
            }

            when (dailyEditState) {
                is DailyEditUiState.Idle,
                is DailyEditUiState.Loading -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = stringResource(R.string.atelier_daily_loading),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                is DailyEditUiState.Error -> {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                            .testTag("daily_edit_error_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                    text = dailyEditState.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            OutlinedButton(
                                onClick = onRefreshDailyEdit,
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Text(stringResource(R.string.action_retry))
                            }
                        }
                    }
                }

                is DailyEditUiState.Success -> {
                    val rec = dailyEditState.recommendation
                    val outfits = rec.outfits
                    val pagerState = rememberPagerState(pageCount = { outfits.size })

                    Column(
                        modifier = Modifier.padding(horizontal = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${rec.dateDisplay} · ${rec.dayTheme}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (rec.editorialSummary.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .drawBehind {
                                        drawLine(
                                            color = primaryAccent,
                                            start = Offset(0f, 4f),
                                            end = Offset(0f, size.height - 4f),
                                            strokeWidth = 3.dp.toPx()
                                        )
                                    }
                                    .padding(start = 12.dp)
                            ) {
                                Text(
                                    text = rec.editorialSummary,
                                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (outfits.isNotEmpty()) {
                        HorizontalPager(
                            state = pagerState,
                            contentPadding = PaddingValues(
                                start = 18.dp,
                                end = if (outfits.size > 1) 30.dp else 18.dp
                            ),
                            pageSpacing = 12.dp,
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) { index ->
                            val outfit = outfits[index]
                            val normalizedTitle = outfit.title.trim().take(120)
                            val alreadySaved = normalizedTitle in savedLookTitles
                            val isSaving = normalizedTitle in savingOutfitTitles
                            val isSharing = sharingOutfitTitle == outfit.title
                            val currentVote = dailyFeedbackVotes["${rec.dateKey}|$normalizedTitle"]
                            val isSubmittingFeedback = normalizedTitle in submittingFeedbackTitles

                            DailyLookbookPagerCard(
                                index = index,
                                totalCount = outfits.size,
                                outfit = outfit,
                                alreadySaved = alreadySaved,
                                isSaving = isSaving,
                                isSharing = isSharing,
                                currentVote = currentVote,
                                isSubmittingFeedback = isSubmittingFeedback,
                                onSaveOutfit = { onSaveOutfit(outfit) },
                                onShareOutfit = { onShareOutfit(outfit) },
                                onFeedback = { vote ->
                                    onDailyOutfitFeedback(outfit, vote, rec.dateKey)
                                },
                                onNavigateToCloset = onNavigateToCloset
                            )
                        }

                        if (outfits.size > 1) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(outfits.size) { page ->
                                    val selected = pagerState.currentPage == page
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .height(7.dp)
                                            .width(if (selected) 22.dp else 7.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (selected) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.outlineVariant
                                                }
                                            )
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DailyLookbookPagerCard(
    index: Int,
    totalCount: Int,
    outfit: AiCuratedOutfit,
    alreadySaved: Boolean,
    isSaving: Boolean,
    isSharing: Boolean,
    currentVote: FeedbackVote?,
    isSubmittingFeedback: Boolean,
    onSaveOutfit: () -> Unit,
    onShareOutfit: () -> Unit,
    onFeedback: (FeedbackVote) -> Unit,
    onNavigateToCloset: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.52f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("daily_outfit_card_$index")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val designImageRes = OutfitDesignArtworkHelper.resolveOutfitImage(outfit)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(MaterialTheme.shapes.medium)
            ) {
                Image(
                    painter = painterResource(id = designImageRes),
                    contentDescription = "${outfit.title} — ${stringResource(R.string.cd_outfit_design_preview)}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(190.dp)
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(EditorialScrimTop, EditorialScrimBottom.copy(alpha = 0.45f))
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.94f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = outfit.audience,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.94f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "Look 0${index + 1} / 0$totalCount",
                            style = MaterialTheme.typography.labelSmall,
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
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = outfit.coverageVerification,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (outfit.matchedPieces.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    outfit.matchedPieces.forEach { piece ->
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            shape = CircleShape
                        ) {
                            Text(
                                text = piece,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            if (outfit.boutiqueSources.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = outfit.boutiqueSources.joinToString(" • "),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
            )

            Text(
                text = outfit.stylingNotes,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (currentVote) {
                        FeedbackVote.UP -> stringResource(R.string.atelier_feedback_liked_hint)
                        FeedbackVote.DOWN -> stringResource(R.string.atelier_feedback_disliked_hint)
                        null -> stringResource(R.string.atelier_feedback_prompt)
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = when (currentVote) {
                        FeedbackVote.UP -> MaterialTheme.colorScheme.secondary
                        FeedbackVote.DOWN -> MaterialTheme.colorScheme.error
                        null -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.weight(1f)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalIconButton(
                        onClick = { onFeedback(FeedbackVote.UP) },
                        enabled = !isSubmittingFeedback,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = if (currentVote == FeedbackVote.UP) {
                                MaterialTheme.colorScheme.secondaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                            contentColor = if (currentVote == FeedbackVote.UP) {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        ),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("thumbs_up_daily_outfit_$index")
                    ) {
                        Icon(
                            imageVector = if (currentVote == FeedbackVote.UP) {
                                Icons.Filled.ThumbUp
                            } else {
                                Icons.Outlined.ThumbUp
                            },
                            contentDescription = stringResource(R.string.cd_thumbs_up_outfit),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    FilledTonalIconButton(
                        onClick = { onFeedback(FeedbackVote.DOWN) },
                        enabled = !isSubmittingFeedback,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = if (currentVote == FeedbackVote.DOWN) {
                                MaterialTheme.colorScheme.errorContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                            contentColor = if (currentVote == FeedbackVote.DOWN) {
                                MaterialTheme.colorScheme.onErrorContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        ),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("thumbs_down_daily_outfit_$index")
                    ) {
                        Icon(
                            imageVector = if (currentVote == FeedbackVote.DOWN) {
                                Icons.Filled.ThumbDown
                            } else {
                                Icons.Outlined.ThumbDown
                            },
                            contentDescription = stringResource(R.string.cd_thumbs_down_outfit),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (alreadySaved && !isSaving) {
                    FilledTonalButton(
                        onClick = onSaveOutfit,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("save_daily_outfit_button_$index")
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkAdded,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.atelier_saved_badge))
                    }
                } else {
                    OutlinedButton(
                        onClick = onSaveOutfit,
                        enabled = !isSaving,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("save_daily_outfit_button_$index")
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.atelier_saving_look))
                        } else {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.atelier_save_look))
                        }
                    }
                }

                OutlinedButton(
                    onClick = onShareOutfit,
                    enabled = !isSharing,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("share_daily_outfit_button_$index")
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
                            modifier = Modifier.size(16.dp)
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
                        onClick = onNavigateToCloset,
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Text(stringResource(R.string.atelier_view_closet))
                    }
                }
            }
        }
    }
}
