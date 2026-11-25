package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.AiCuratedOutfit
import com.example.data.model.SavedOutfit
import com.example.ui.theme.EditorialScrimBottom
import com.example.ui.theme.EditorialScrimTop
import com.example.ui.viewmodel.DataUiState
import com.example.util.ClosetSearchHelper
import com.example.util.ClosetSearchHelper.deriveStyleTags
import com.example.util.ClosetSearchHelper.matchesClosetFilter
import com.example.util.OutfitDesignArtworkHelper
import com.example.util.OutfitShareHelper.toShareableOutfit

private val MY_CLOSET_FILTERS = listOf("All", "Favorites", "Ladies", "Girls", "Teens")

@Composable
fun MyClosetScreen(
    savedOutfitsState: DataUiState<List<SavedOutfit>>,
    isAuthenticated: Boolean = true,
    onSignInClick: () -> Unit = {},
    onToggleFavorite: (outfitId: String, favorite: Boolean) -> Unit,
    onUpdateOutfitNotes: (outfitId: String, title: String, stylingNotes: String, favorite: Boolean) -> Unit,
    onDeleteOutfit: (outfitId: String) -> Unit,
    onShareOutfit: (AiCuratedOutfit) -> Unit = {},
    onNavigateToStylist: () -> Unit = {}
) {
    var selectedFilter by rememberSaveable { mutableStateOf("All") }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedOutfitForDetail by remember { mutableStateOf<SavedOutfit?>(null) }
    var editingOutfit by remember { mutableStateOf<SavedOutfit?>(null) }

    val activeTokens = remember(searchQuery) {
        ClosetSearchHelper.parseQueryTokens(searchQuery)
    }
    val successOutfits = (savedOutfitsState as? DataUiState.Success)?.data.orEmpty()
    val availableTags = remember(successOutfits) {
        ClosetSearchHelper.collectAvailableTags(successOutfits)
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        val gridColumns = when {
            maxWidth >= 840.dp -> 3
            maxWidth >= 320.dp -> 2
            else -> 1
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(gridColumns),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 920.dp)
                .testTag("my_closet_grid"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.lookbook_title),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (savedOutfitsState is DataUiState.Success && savedOutfitsState.data.isNotEmpty()) {
                            val total = savedOutfitsState.data.size
                            val favs = savedOutfitsState.data.count { it.favorite }
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "$total Saved • $favs Pinned",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = stringResource(
                            if (isAuthenticated) R.string.lookbook_subtitle else R.string.lookbook_subtitle_guest
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!isAuthenticated) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        ),
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = stringResource(R.string.cd_sign_in_account),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(22.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.closet_guest_sync_title),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = stringResource(R.string.closet_guest_sync_body),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            FilledTonalButton(
                                onClick = onSignInClick,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("closet_sign_in_button")
                            ) {
                                Text(stringResource(R.string.atelier_sign_in_chip))
                            }
                        }
                    }
                }
            }

            when (savedOutfitsState) {
                is DataUiState.Loading -> {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is DataUiState.Error -> {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = savedOutfitsState.message,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                is DataUiState.Success -> {
                    val outfits = savedOutfitsState.data

                    // Search Bar & Quick Style Tags Filter (always accessible on My Closet)
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text(stringResource(R.string.closet_search_placeholder))
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(
                                            onClick = { searchQuery = "" },
                                            modifier = Modifier.testTag("closet_search_clear_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Clear search"
                                            )
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = CircleShape,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("closet_search_input")
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                items(availableTags, key = { it.lowercase() }) { tag ->
                                    val isTagActive = tag.lowercase() in activeTokens
                                    FilterChip(
                                        selected = isTagActive,
                                        onClick = {
                                            searchQuery = ClosetSearchHelper.toggleTagInQuery(searchQuery, tag)
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.LocalOffer,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        },
                                        label = { Text(tag) },
                                        modifier = Modifier
                                            .minimumInteractiveComponentSize()
                                            .testTag("closet_tag_chip_${tag.lowercase()}")
                                    )
                                }
                            }

                            if (outfits.isNotEmpty()) {
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(MY_CLOSET_FILTERS) { filter ->
                                        FilterChip(
                                            selected = selectedFilter == filter,
                                            onClick = { selectedFilter = filter },
                                            label = { Text(filter) },
                                            modifier = Modifier
                                                .minimumInteractiveComponentSize()
                                                .testTag("closet_filter_chip_${filter.lowercase()}")
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (outfits.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                shape = MaterialTheme.shapes.extraLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp)
                                    .testTag("empty_lookbook_state")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(28.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = CircleShape,
                                        modifier = Modifier.size(64.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Bookmarks,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.size(30.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = stringResource(R.string.lookbook_empty_title),
                                        style = MaterialTheme.typography.titleLarge,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = stringResource(R.string.lookbook_empty_body),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(18.dp))
                                    Button(
                                        onClick = onNavigateToStylist,
                                        modifier = Modifier
                                            .height(48.dp)
                                            .testTag("my_closet_explore_stylist_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(stringResource(R.string.my_closet_explore_stylist))
                                    }
                                }
                            }
                        }
                    } else {
                        val filtered = outfits.filter { outfit ->
                            outfit.matchesClosetFilter(selectedFilter, searchQuery)
                        }
                        val hasActiveSearchOrFilter = selectedFilter != "All" || activeTokens.isNotEmpty()

                        if (hasActiveSearchOrFilter && filtered.isNotEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Showing ${filtered.size} of ${outfits.size} saved looks",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    TextButton(
                                        onClick = {
                                            selectedFilter = "All"
                                            searchQuery = ""
                                        },
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Text(stringResource(R.string.closet_clear_filters))
                                    }
                                }
                            }
                        }

                        if (filtered.isEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = MaterialTheme.shapes.large,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("closet_no_matches_card")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            text = "No bookmarked outfits match \"${searchQuery.trim().ifBlank { selectedFilter }}\".",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                        OutlinedButton(
                                            onClick = {
                                                selectedFilter = "All"
                                                searchQuery = ""
                                            },
                                            modifier = Modifier
                                                .minimumInteractiveComponentSize()
                                                .testTag("closet_clear_filters_button")
                                        ) {
                                            Text(stringResource(R.string.closet_clear_filters))
                                        }
                                    }
                                }
                            }
                        } else {
                            items(filtered, key = { it.id }) { outfit ->
                                ClosetOutfitGridCard(
                                    outfit = outfit,
                                    onClick = { selectedOutfitForDetail = outfit },
                                    onToggleFavorite = {
                                        onToggleFavorite(outfit.id, !outfit.favorite)
                                    },
                                    onEditNotes = { editingOutfit = outfit },
                                    onDelete = { onDeleteOutfit(outfit.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    selectedOutfitForDetail?.let { detailOutfit ->
        // Keep detail dialog reactive if favorite/notes change in Firestore
        val liveOutfit = (savedOutfitsState as? DataUiState.Success)
            ?.data
            ?.firstOrNull { it.id == detailOutfit.id }
            ?: detailOutfit
        ClosetOutfitDetailDialog(
            outfit = liveOutfit,
            onDismiss = { selectedOutfitForDetail = null },
            onToggleFavorite = {
                onToggleFavorite(liveOutfit.id, !liveOutfit.favorite)
            },
            onShare = {
                onShareOutfit(liveOutfit.toShareableOutfit())
            },
            onEditNotes = {
                editingOutfit = liveOutfit
                selectedOutfitForDetail = null
            },
            onDelete = {
                onDeleteOutfit(liveOutfit.id)
                selectedOutfitForDetail = null
            }
        )
    }

    editingOutfit?.let { outfit ->
        EditClosetOutfitDialog(
            outfit = outfit,
            onDismiss = { editingOutfit = null },
            onSave = { newTitle, newNotes ->
                onUpdateOutfitNotes(outfit.id, newTitle, newNotes, outfit.favorite)
                editingOutfit = null
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ClosetOutfitGridCard(
    outfit: SavedOutfit,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onEditNotes: () -> Unit,
    onDelete: () -> Unit
) {
    val derivedTags = remember(outfit) { outfit.deriveStyleTags().take(2) }
    val designImageRes = remember(outfit) { OutfitDesignArtworkHelper.resolveOutfitImage(outfit) }

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("saved_outfit_card_${outfit.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Editorial Design Artwork Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(134.dp)
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
                                colors = listOf(EditorialScrimTop, EditorialScrimBottom)
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.42f),
                        shape = CircleShape,
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = outfit.audience,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(36.dp)
                        .minimumInteractiveComponentSize()
                        .testTag("favorite_outfit_${outfit.id}")
                ) {
                    Icon(
                        imageVector = if (outfit.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(R.string.cd_favorite_toggle),
                        tint = if (outfit.favorite) Color(0xFFFFD6DF) else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Row(
                    modifier = Modifier.align(Alignment.BottomStart),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.92f),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = outfit.occasion,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.94f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                }
            }

            // Card Body
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = outfit.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    minLines = 2
                )

                if (derivedTags.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        derivedTags.forEach { tag ->
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "#${tag.lowercase()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Checkroom,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${outfit.matchedPieces.size} pieces",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val primaryBoutique = outfit.boutiqueSources.firstOrNull() ?: "Local Atelier"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = primaryBoutique,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Quick Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onEditNotes,
                        modifier = Modifier.testTag("edit_outfit_${outfit.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit styling note",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("delete_outfit_${outfit.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = stringResource(R.string.cd_delete_item),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
