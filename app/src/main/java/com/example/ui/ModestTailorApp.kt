package com.example.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.R
import com.example.data.ai.DailyOutfitCache
import com.example.data.ai.TailorAiService
import com.example.data.model.StyleProfile
import com.example.data.repository.ModestStyleRepository
import com.example.ui.auth.AuthOnboardingDraft
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.authStateFlow
import com.example.ui.auth.signOutUser
import com.example.ui.viewmodel.ModestTailorViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AppNavigation(
    auth: FirebaseAuth = Firebase.auth,
    incomingDeepLinkUri: Uri? = null,
    onConsumeDeepLink: () -> Unit = {}
) {
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    var showAuthScreen by rememberSaveable { mutableStateOf(false) }
    var onboardingDraft by remember { mutableStateOf<AuthOnboardingDraft?>(null) }
    var guestProfileDraft by remember { mutableStateOf<StyleProfile?>(null) }
    val user = currentUser

    if (user == null) {
        if (showAuthScreen) {
            AuthScreen(
                onAuthSuccess = { draft ->
                    onboardingDraft = draft
                    showAuthScreen = false
                },
                onDismiss = {
                    showAuthScreen = false
                },
                attemptSilentAutoSignIn = false
            )
        } else {
            GuestAtelierShell(
                initialDraftProfile = guestProfileDraft,
                onGuestProfileUpdated = { updated -> guestProfileDraft = updated },
                incomingDeepLinkUri = incomingDeepLinkUri,
                onConsumeDeepLink = onConsumeDeepLink,
                onRequestSignIn = { showAuthScreen = true }
            )
        }
    } else {
        key(user.uid) {
            AuthenticatedAtelierShell(
                currentUserId = user.uid,
                userDisplayName = user.displayName ?: "Modest Stylist",
                userEmail = user.email,
                onboardingDraft = onboardingDraft,
                guestProfileDraft = guestProfileDraft,
                incomingDeepLinkUri = incomingDeepLinkUri,
                onConsumeDeepLink = onConsumeDeepLink,
                onClearOnboardingDraft = {
                    onboardingDraft = null
                    showAuthScreen = false
                }
            )
        }
    }
}

@Composable
private fun AuthenticatedAtelierShell(
    currentUserId: String,
    userDisplayName: String,
    userEmail: String?,
    onboardingDraft: AuthOnboardingDraft?,
    guestProfileDraft: StyleProfile?,
    incomingDeepLinkUri: Uri?,
    onConsumeDeepLink: () -> Unit,
    onClearOnboardingDraft: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val viewModel: ModestTailorViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                ModestTailorViewModel(
                    repository = ModestStyleRepository(db),
                    aiService = TailorAiService(),
                    currentUserId = currentUserId,
                    defaultDisplayName = userDisplayName,
                    initialAudience = onboardingDraft?.audience
                        ?: guestProfileDraft?.audience
                        ?: "Ladies & Girls",
                    initialHijabFriendly = onboardingDraft?.hijabFriendly
                        ?: guestProfileDraft?.hijabFriendly
                        ?: true,
                    dailyOutfitCache = DailyOutfitCache(app.applicationContext),
                    initialProfileDraft = guestProfileDraft
                )
            }
        }
    )

    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val inventoryState by viewModel.inventoryState.collectAsStateWithLifecycle()
    val savedOutfitsState by viewModel.savedOutfitsState.collectAsStateWithLifecycle()
    val dailyEditState by viewModel.dailyEditState.collectAsStateWithLifecycle()
    val stylistState by viewModel.stylistState.collectAsStateWithLifecycle()
    val garmentScanState by viewModel.garmentScanState.collectAsStateWithLifecycle()
    val savedLookTitles by viewModel.savedLookTitles.collectAsStateWithLifecycle()
    val savingOutfitTitles by viewModel.savingOutfitTitles.collectAsStateWithLifecycle()
    val dailyFeedbackVotes by viewModel.dailyFeedbackVotes.collectAsStateWithLifecycle()
    val submittingFeedbackTitles by viewModel.submittingFeedbackTitles.collectAsStateWithLifecycle()
    val savedOutfitsCount by viewModel.savedOutfitsCount.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()
    val isSavingProfile by viewModel.isSavingProfile.collectAsStateWithLifecycle()

    AtelierShellContent(
        isAuthenticated = true,
        userEmail = userEmail,
        profileState = profileState,
        inventoryState = inventoryState,
        savedOutfitsState = savedOutfitsState,
        dailyEditState = dailyEditState,
        stylistState = stylistState,
        garmentScanState = garmentScanState,
        savedLookTitles = savedLookTitles,
        savingOutfitTitles = savingOutfitTitles,
        dailyFeedbackVotes = dailyFeedbackVotes,
        submittingFeedbackTitles = submittingFeedbackTitles,
        savedOutfitsCount = savedOutfitsCount,
        statusMessage = statusMessage,
        isSavingProfile = isSavingProfile,
        incomingDeepLinkUri = incomingDeepLinkUri,
        onConsumeDeepLink = onConsumeDeepLink,
        onDismissStatusMessage = viewModel::dismissStatusMessage,
        onRefreshDailyEdit = viewModel::refreshDailyEdit,
        onGenerateOutfits = viewModel::generateOutfits,
        onSaveOutfit = viewModel::toggleClosetBookmark,
        onDailyOutfitFeedback = viewModel::submitDailyOutfitFeedback,
        onSeedStarterInventory = viewModel::seedStarterBoutique,
        onAddInventoryItem = viewModel::addInventoryItem,
        onToggleInventoryStock = viewModel::toggleInventoryStock,
        onDeleteInventoryItem = viewModel::deleteInventoryItem,
        onAnalyzeGarmentBitmap = viewModel::analyzeGarmentBitmap,
        onClearGarmentScan = viewModel::clearGarmentScan,
        onToggleOutfitFavorite = viewModel::toggleOutfitFavorite,
        onUpdateSavedOutfitNotes = viewModel::updateSavedOutfitNotes,
        onDeleteSavedOutfit = viewModel::deleteSavedOutfit,
        onSaveProfile = viewModel::saveProfile,
        onSignInClick = {},
        onSignOutClick = {
            signOutUser(
                credentialManager = credentialManager,
                onSignOutComplete = onClearOnboardingDraft,
                scope = scope
            )
        }
    )
}
