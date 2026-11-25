package com.example.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.AiCuratedOutfit
import com.example.data.model.FeedbackVote
import com.example.data.model.InventoryItem
import com.example.data.model.SavedOutfit
import com.example.data.model.StyleProfile
import com.example.ui.screens.BoutiqueInventoryScreen
import com.example.ui.screens.ModestyProfileScreen
import com.example.ui.screens.MyClosetScreen
import com.example.ui.screens.StylistStudioScreen
import com.example.ui.viewmodel.DailyEditUiState
import com.example.ui.viewmodel.DataUiState
import com.example.ui.viewmodel.GarmentScanUiState
import com.example.ui.viewmodel.StylistUiState
import com.example.util.OutfitShareHelper
import kotlinx.coroutines.launch

internal enum class AtelierDestination(
    val routeId: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    STYLIST(
        routeId = "stylist_studio",
        labelRes = R.string.nav_atelier,
        selectedIcon = Icons.Filled.AutoAwesome,
        unselectedIcon = Icons.Outlined.AutoAwesome
    ),
    INVENTORY(
        routeId = "boutique_inventory",
        labelRes = R.string.nav_boutique,
        selectedIcon = Icons.Filled.Checkroom,
        unselectedIcon = Icons.Outlined.Checkroom
    ),
    LOOKBOOK(
        routeId = "saved_lookbook",
        labelRes = R.string.nav_lookbook,
        selectedIcon = Icons.Filled.Bookmarks,
        unselectedIcon = Icons.Outlined.Bookmarks
    ),
    PROFILE(
        routeId = "modesty_profile",
        labelRes = R.string.nav_profile,
        selectedIcon = Icons.Filled.VerifiedUser,
        unselectedIcon = Icons.Outlined.VerifiedUser
    )
}

@Composable
internal fun AtelierShellContent(
    isAuthenticated: Boolean,
    userEmail: String?,
    profileState: DataUiState<StyleProfile>,
    inventoryState: DataUiState<List<InventoryItem>>,
    savedOutfitsState: DataUiState<List<SavedOutfit>>,
    dailyEditState: DailyEditUiState,
    stylistState: StylistUiState,
    garmentScanState: GarmentScanUiState,
    savedLookTitles: Set<String>,
    savingOutfitTitles: Set<String>,
    dailyFeedbackVotes: Map<String, FeedbackVote>,
    submittingFeedbackTitles: Set<String>,
    savedOutfitsCount: Int,
    statusMessage: String?,
    isSavingProfile: Boolean,
    incomingDeepLinkUri: Uri?,
    onConsumeDeepLink: () -> Unit,
    onDismissStatusMessage: () -> Unit,
    onRefreshDailyEdit: () -> Unit,
    onGenerateOutfits: (occasion: String, season: String, customNote: String) -> Unit,
    onSaveOutfit: (AiCuratedOutfit) -> Unit,
    onDailyOutfitFeedback: (AiCuratedOutfit, FeedbackVote, String) -> Unit,
    onSeedStarterInventory: () -> Unit,
    onAddInventoryItem: (InventoryItem) -> Unit,
    onToggleInventoryStock: (itemId: String, inStock: Boolean) -> Unit,
    onDeleteInventoryItem: (itemId: String) -> Unit,
    onAnalyzeGarmentBitmap: (Bitmap) -> Unit,
    onClearGarmentScan: () -> Unit,
    onToggleOutfitFavorite: (outfitId: String, favorite: Boolean) -> Unit,
    onUpdateSavedOutfitNotes: (outfitId: String, title: String, stylingNotes: String, favorite: Boolean) -> Unit,
    onDeleteSavedOutfit: (outfitId: String) -> Unit,
    onSaveProfile: (StyleProfile, onSaved: () -> Unit) -> Unit,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentTab by rememberSaveable { mutableStateOf(AtelierDestination.STYLIST) }
    var sharingOutfitTitle by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    val sharedDeepLinkOutfit = remember(incomingDeepLinkUri) {
        OutfitShareHelper.parseOutfitDeepLink(incomingDeepLinkUri)
    }

    val shareOutfit: (AiCuratedOutfit) -> Unit = { outfit ->
        if (sharingOutfitTitle == null) {
            scope.launch {
                sharingOutfitTitle = outfit.title
                try {
                    val chooserIntent = OutfitShareHelper.createShareIntent(context, outfit)
                    if (context !is Activity) {
                        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(chooserIntent)
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar(
                        "Unable to open share sheet: ${e.message ?: "unknown error"}"
                    )
                } finally {
                    sharingOutfitTitle = null
                }
            }
        }
    }

    // Mandatory BackHandler for secondary tabs when custom state switching is used
    BackHandler(enabled = currentTab != AtelierDestination.STYLIST) {
        currentTab = AtelierDestination.STYLIST
    }

    LaunchedEffect(statusMessage) {
        val msg = statusMessage
        if (!msg.isNullOrBlank()) {
            val isClosetSave = msg.contains("to your Closet")
            val result = snackbarHostState.showSnackbar(
                message = msg,
                actionLabel = if (isClosetSave) "My Closet" else null,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed && isClosetSave) {
                currentTab = AtelierDestination.LOOKBOOK
            }
            onDismissStatusMessage()
        }
    }

    val currentDistrict = (profileState as? DataUiState.Success)?.data?.cityOrArea ?: "Oakbrook"

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 600.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (!useNavigationRail) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        AtelierDestination.entries.forEach { dest ->
                            val selected = currentTab == dest
                            NavigationBarItem(
                                selected = selected,
                                onClick = { currentTab = dest },
                                icon = {
                                    ClosetBadgeIcon(
                                        dest = dest,
                                        selected = selected,
                                        savedOutfitsCount = savedOutfitsCount
                                    )
                                },
                                label = { Text(stringResource(dest.labelRes)) },
                                modifier = Modifier.testTag("nav_${dest.routeId}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavigationRail) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        AtelierDestination.entries.forEach { dest ->
                            val selected = currentTab == dest
                            NavigationRailItem(
                                selected = selected,
                                onClick = { currentTab = dest },
                                icon = {
                                    ClosetBadgeIcon(
                                        dest = dest,
                                        selected = selected,
                                        savedOutfitsCount = savedOutfitsCount
                                    )
                                },
                                label = { Text(stringResource(dest.labelRes)) },
                                modifier = Modifier.testTag("rail_${dest.routeId}")
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    when (currentTab) {
                        AtelierDestination.STYLIST -> StylistStudioScreen(
                            profileState = profileState,
                            inventoryState = inventoryState,
                            dailyEditState = dailyEditState,
                            stylistState = stylistState,
                            savedLookTitles = savedLookTitles,
                            savingOutfitTitles = savingOutfitTitles,
                            sharingOutfitTitle = sharingOutfitTitle,
                            dailyFeedbackVotes = dailyFeedbackVotes,
                            submittingFeedbackTitles = submittingFeedbackTitles,
                            savedOutfitsCount = savedOutfitsCount,
                            isAuthenticated = isAuthenticated,
                            onSignInClick = onSignInClick,
                            onRefreshDailyEdit = onRefreshDailyEdit,
                            onGenerateOutfits = onGenerateOutfits,
                            onSaveOutfit = onSaveOutfit,
                            onShareOutfit = shareOutfit,
                            onDailyOutfitFeedback = onDailyOutfitFeedback,
                            onSeedStarterInventory = onSeedStarterInventory,
                            onNavigateToProfile = { currentTab = AtelierDestination.PROFILE },
                            onNavigateToInventory = { currentTab = AtelierDestination.INVENTORY },
                            onNavigateToCloset = { currentTab = AtelierDestination.LOOKBOOK }
                        )

                        AtelierDestination.INVENTORY -> BoutiqueInventoryScreen(
                            inventoryState = inventoryState,
                            garmentScanState = garmentScanState,
                            defaultDistrict = currentDistrict,
                            onAddItem = onAddInventoryItem,
                            onToggleStock = onToggleInventoryStock,
                            onDeleteItem = onDeleteInventoryItem,
                            onSeedStarterInventory = onSeedStarterInventory,
                            onAnalyzeGarmentBitmap = onAnalyzeGarmentBitmap,
                            onClearGarmentScan = onClearGarmentScan
                        )

                        AtelierDestination.LOOKBOOK -> MyClosetScreen(
                            savedOutfitsState = savedOutfitsState,
                            isAuthenticated = isAuthenticated,
                            onSignInClick = onSignInClick,
                            onToggleFavorite = onToggleOutfitFavorite,
                            onUpdateOutfitNotes = onUpdateSavedOutfitNotes,
                            onDeleteOutfit = onDeleteSavedOutfit,
                            onShareOutfit = shareOutfit,
                            onNavigateToStylist = { currentTab = AtelierDestination.STYLIST }
                        )

                        AtelierDestination.PROFILE -> ModestyProfileScreen(
                            profileState = profileState,
                            userEmail = userEmail,
                            inStockInventory = (inventoryState as? DataUiState.Success)
                                ?.data
                                ?.filter { it.inStock }
                                .orEmpty(),
                            isSavingProfile = isSavingProfile,
                            isAuthenticated = isAuthenticated,
                            onSignInClick = onSignInClick,
                            onSaveProfile = onSaveProfile,
                            onNavigateToStylist = { currentTab = AtelierDestination.STYLIST },
                            onSignOut = onSignOutClick
                        )
                    }
                }
            }
        }

        if (sharedDeepLinkOutfit != null) {
            SharedModestLookDialog(
                outfit = sharedDeepLinkOutfit,
                alreadySaved = sharedDeepLinkOutfit.title.trim().take(120) in savedLookTitles,
                onSaveToCloset = {
                    if (sharedDeepLinkOutfit.title.trim().take(120) !in savedLookTitles) {
                        onSaveOutfit(sharedDeepLinkOutfit)
                    }
                    currentTab = AtelierDestination.LOOKBOOK
                    onConsumeDeepLink()
                },
                onDismiss = onConsumeDeepLink
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SharedModestLookDialog(
    outfit: AiCuratedOutfit,
    alreadySaved: Boolean,
    onSaveToCloset: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ) {
                    Text(
                        text = "${outfit.audience} • ${outfit.occasion}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = outfit.title,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
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
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = outfit.coverageVerification,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
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
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            ) {
                                Text(
                                    text = piece,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
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
                            text = outfit.boutiqueSources.joinToString(" • "),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (outfit.stylingNotes.isNotBlank()) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Text(
                        text = outfit.stylingNotes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSaveToCloset,
                modifier = Modifier.testTag("shared_look_save_button")
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (alreadySaved) "Open in My Closet" else "Save to Closet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun ClosetBadgeIcon(
    dest: AtelierDestination,
    selected: Boolean,
    savedOutfitsCount: Int
) {
    val iconVector = if (selected) dest.selectedIcon else dest.unselectedIcon
    if (dest == AtelierDestination.LOOKBOOK && savedOutfitsCount > 0) {
        BadgedBox(
            badge = {
                Badge {
                    Text(if (savedOutfitsCount > 99) "99+" else savedOutfitsCount.toString())
                }
            }
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = stringResource(dest.labelRes)
            )
        }
    } else {
        Icon(
            imageVector = iconVector,
            contentDescription = stringResource(dest.labelRes)
        )
    }
}
