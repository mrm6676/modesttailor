package com.example.ui.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.DailyOutfitCache
import com.example.data.ai.TailorAiService
import com.example.data.model.AiCuratedOutfit
import com.example.data.model.AiDailyEditRecommendation
import com.example.data.model.AiGarmentScanResult
import com.example.data.model.AiStylingRecommendation
import com.example.data.model.FeedbackVote
import com.example.data.model.InventoryItem
import com.example.data.model.OutfitFeedback
import com.example.data.model.SavedOutfit
import com.example.data.model.StyleProfile
import com.example.data.model.outfitFeedbackDocId
import com.example.data.model.preferenceFingerprint
import com.example.data.repository.ModestStyleRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface DataUiState<out T> {
    data object Loading : DataUiState<Nothing>
    data class Success<T>(val data: T) : DataUiState<T>
    data class Error(val message: String) : DataUiState<Nothing>
}

sealed interface StylistUiState {
    data object Idle : StylistUiState
    data object Loading : StylistUiState
    data class Success(val recommendation: AiStylingRecommendation) : StylistUiState
    data class Error(val message: String) : StylistUiState
}

sealed interface DailyEditUiState {
    data object Idle : DailyEditUiState
    data object Loading : DailyEditUiState
    data class Success(
        val recommendation: AiDailyEditRecommendation,
        val isFromCache: Boolean
    ) : DailyEditUiState
    data class Error(val message: String) : DailyEditUiState
}

sealed interface GarmentScanUiState {
    data object Idle : GarmentScanUiState
    data object Scanning : GarmentScanUiState
    data class Success(val result: AiGarmentScanResult) : GarmentScanUiState
    data class Error(val message: String) : GarmentScanUiState
}

class ModestTailorViewModel(
    private val repository: ModestStyleRepository,
    private val aiService: TailorAiService,
    private val currentUserId: String,
    private val defaultDisplayName: String,
    private val initialAudience: String = "Ladies & Girls",
    private val initialHijabFriendly: Boolean = true,
    private val dailyOutfitCache: DailyOutfitCache? = null,
    private val initialProfileDraft: StyleProfile? = null
) : ViewModel() {

    private var profileBootstrapped = false
    private var lastEvaluatedDailyKey: String? = null
    private var dailyEditJob: Job? = null

    private fun buildInitialProfile(): StyleProfile {
        val base = initialProfileDraft?.copy(
            userId = currentUserId,
            displayName = if (defaultDisplayName.isNotBlank() && defaultDisplayName != "Guest Stylist") {
                defaultDisplayName
            } else {
                initialProfileDraft.displayName
            },
            audience = initialAudience.ifBlank { initialProfileDraft.audience },
            hijabFriendly = initialHijabFriendly
        )
        return base ?: StyleProfile(
            userId = currentUserId,
            displayName = defaultDisplayName.ifBlank { "Modest Stylist" },
            audience = initialAudience,
            hijabFriendly = initialHijabFriendly
        )
    }

    val profileState: StateFlow<DataUiState<StyleProfile>> = repository
        .observeStyleProfile(currentUserId)
        .onEach { profile ->
            if (profile == null && !profileBootstrapped) {
                profileBootstrapped = true
                repository.saveStyleProfile(buildInitialProfile())
            }
        }
        .map<StyleProfile?, DataUiState<StyleProfile>> { profile ->
            DataUiState.Success(profile ?: buildInitialProfile())
        }
        .catch { error ->
            Log.w(TAG, "Error observing style profile", error)
            emit(DataUiState.Error(error.localizedMessage ?: "Failed to load style profile"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = DataUiState.Loading
        )

    val inventoryState: StateFlow<DataUiState<List<InventoryItem>>> = repository
        .observeInventory(currentUserId)
        .map<List<InventoryItem>, DataUiState<List<InventoryItem>>> { items ->
            DataUiState.Success(items)
        }
        .catch { error ->
            Log.w(TAG, "Error observing inventory", error)
            emit(DataUiState.Error(error.localizedMessage ?: "Failed to load local inventory"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = DataUiState.Loading
        )

    val savedOutfitsState: StateFlow<DataUiState<List<SavedOutfit>>> = repository
        .observeSavedOutfits(currentUserId)
        .map<List<SavedOutfit>, DataUiState<List<SavedOutfit>>> { outfits ->
            DataUiState.Success(outfits)
        }
        .catch { error ->
            Log.w(TAG, "Error observing saved outfits", error)
            emit(DataUiState.Error(error.localizedMessage ?: "Failed to load saved closet"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = DataUiState.Loading
        )

    private val _dailyEditState = MutableStateFlow<DailyEditUiState>(DailyEditUiState.Idle)
    val dailyEditState: StateFlow<DailyEditUiState> = _dailyEditState.asStateFlow()

    private val _stylistState = MutableStateFlow<StylistUiState>(StylistUiState.Idle)
    val stylistState: StateFlow<StylistUiState> = _stylistState.asStateFlow()

    private val _garmentScanState = MutableStateFlow<GarmentScanUiState>(GarmentScanUiState.Idle)
    val garmentScanState: StateFlow<GarmentScanUiState> = _garmentScanState.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _isSavingProfile = MutableStateFlow(false)
    val isSavingProfile: StateFlow<Boolean> = _isSavingProfile.asStateFlow()

    private val _savingOutfitTitles = MutableStateFlow<Set<String>>(emptySet())
    val savingOutfitTitles: StateFlow<Set<String>> = _savingOutfitTitles.asStateFlow()

    private val _submittingFeedbackTitles = MutableStateFlow<Set<String>>(emptySet())
    val submittingFeedbackTitles: StateFlow<Set<String>> = _submittingFeedbackTitles.asStateFlow()

    val outfitFeedbackState: StateFlow<DataUiState<List<OutfitFeedback>>> = repository
        .observeOutfitFeedback(currentUserId)
        .map<List<OutfitFeedback>, DataUiState<List<OutfitFeedback>>> { items ->
            DataUiState.Success(items)
        }
        .catch { error ->
            Log.w(TAG, "Error observing outfit feedback", error)
            emit(DataUiState.Error(error.localizedMessage ?: "Failed to load feedback preferences"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = DataUiState.Loading
        )

    val dailyFeedbackVotes: StateFlow<Map<String, FeedbackVote>> = outfitFeedbackState
        .map { state ->
            (state as? DataUiState.Success)
                ?.data
                ?.mapNotNull { fb ->
                    val vote = FeedbackVote.fromRaw(fb.vote) ?: return@mapNotNull null
                    "${fb.dateKey}|${fb.outfitTitle.trim().take(120)}" to vote
                }
                ?.toMap()
                ?: emptyMap()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = emptyMap()
        )

    val savedLookTitles: StateFlow<Set<String>> = savedOutfitsState
        .map { state ->
            (state as? DataUiState.Success)
                ?.data
                ?.map { it.title.trim().take(120) }
                ?.toSet()
                ?: emptySet()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = emptySet()
        )

    val savedOutfitsCount: StateFlow<Int> = savedOutfitsState
        .map { state -> (state as? DataUiState.Success)?.data?.size ?: 0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = 0
        )

    init {
        viewModelScope.launch {
            combine(profileState, inventoryState) { pState, iState -> pState to iState }
                .collect { (pState, iState) ->
                    if (pState is DataUiState.Success && iState is DataUiState.Success) {
                        ensureDailyEditForToday(
                            profile = pState.data,
                            inventory = iState.data,
                            forceRefresh = false
                        )
                    }
                }
        }
    }

    fun dismissStatusMessage() {
        _statusMessage.value = null
    }

    fun refreshDailyEdit() {
        val profile = currentProfileOrDefault()
        val inventory = currentInventoryOrEmpty()
        ensureDailyEditForToday(profile = profile, inventory = inventory, forceRefresh = true)
    }

    private fun ensureDailyEditForToday(
        profile: StyleProfile,
        inventory: List<InventoryItem>,
        forceRefresh: Boolean
    ) {
        val now = Date()
        val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(now)
        val dateDisplay = SimpleDateFormat("MMM d", Locale.getDefault()).format(now)
        val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(now)
        val fingerprint = profile.preferenceFingerprint()
        val combinedKey = "$dateKey|$fingerprint"

        if (!forceRefresh && lastEvaluatedDailyKey == combinedKey) {
            return
        }

        if (!forceRefresh) {
            val cached = dailyOutfitCache?.loadIfValid(currentUserId, dateKey, fingerprint)
            if (cached != null) {
                lastEvaluatedDailyKey = combinedKey
                _dailyEditState.value = DailyEditUiState.Success(cached, isFromCache = true)
                return
            }
        }

        lastEvaluatedDailyKey = combinedKey
        dailyEditJob?.cancel()
        dailyEditJob = viewModelScope.launch {
            _dailyEditState.value = DailyEditUiState.Loading
            try {
                val dailyRecommendation = aiService.generateDailyPersonalizedEdit(
                    profile = profile,
                    availableInventory = inventory,
                    dateKey = dateKey,
                    dateDisplay = "$dayOfWeek, $dateDisplay",
                    dayOfWeek = dayOfWeek,
                    recentFeedback = currentFeedbackOrEmpty()
                )
                if (dailyRecommendation.outfits.isNotEmpty()) {
                    dailyOutfitCache?.save(currentUserId, fingerprint, dailyRecommendation)
                    _dailyEditState.value = DailyEditUiState.Success(
                        recommendation = dailyRecommendation,
                        isFromCache = false
                    )
                } else {
                    _dailyEditState.value = DailyEditUiState.Error(
                        "Daily AI returned no outfits. Tap Retry to refresh."
                    )
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Daily personalized edit timed out", e)
                lastEvaluatedDailyKey = null
                _dailyEditState.value = DailyEditUiState.Error(
                    "Daily curation timed out after 60 seconds. Tap Retry to try again."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Daily personalized edit failed", e)
                lastEvaluatedDailyKey = null
                _dailyEditState.value = DailyEditUiState.Error(
                    e.localizedMessage ?: "Unable to generate today's personalized edit."
                )
            }
        }
    }

    private fun currentProfileOrDefault(): StyleProfile {
        return (profileState.value as? DataUiState.Success)?.data
            ?: StyleProfile(
                userId = currentUserId,
                displayName = defaultDisplayName.ifBlank { "Modest Stylist" }
            )
    }

    private fun currentInventoryOrEmpty(): List<InventoryItem> {
        return (inventoryState.value as? DataUiState.Success)?.data ?: emptyList()
    }

    private fun currentFeedbackOrEmpty(): List<OutfitFeedback> {
        return (outfitFeedbackState.value as? DataUiState.Success)?.data ?: emptyList()
    }

    fun generateOutfits(occasion: String, season: String, customRequest: String) {
        viewModelScope.launch {
            _stylistState.value = StylistUiState.Loading
            try {
                val recommendation = aiService.generateOutfitRecommendations(
                    profile = currentProfileOrDefault(),
                    availableInventory = currentInventoryOrEmpty(),
                    occasion = occasion,
                    season = season,
                    customRequest = customRequest,
                    recentFeedback = currentFeedbackOrEmpty()
                )
                if (recommendation.outfits.isNotEmpty()) {
                    _stylistState.value = StylistUiState.Success(recommendation)
                } else {
                    _stylistState.value = StylistUiState.Error(
                        "Tailor AI returned no outfits. Try adjusting your occasion or request."
                    )
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Outfit generation timed out", e)
                _stylistState.value = StylistUiState.Error(
                    "Styling timed out after 60 seconds. Please try again."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Outfit generation failed", e)
                _stylistState.value = StylistUiState.Error(
                    e.localizedMessage ?: "Unable to generate outfit recommendations."
                )
            }
        }
    }

    fun toggleClosetBookmark(outfit: AiCuratedOutfit) {
        val normalizedTitle = outfit.title.trim().take(120)
        if (normalizedTitle in _savingOutfitTitles.value) return

        viewModelScope.launch {
            _savingOutfitTitles.value = _savingOutfitTitles.value + normalizedTitle
            val existingMatch = (savedOutfitsState.value as? DataUiState.Success)
                ?.data
                ?.firstOrNull { it.title.trim().take(120) == normalizedTitle }

            if (existingMatch != null) {
                repository.deleteSavedOutfit(existingMatch.id)
                    .onSuccess {
                        _statusMessage.value = "Removed \"${outfit.title}\" from your Closet."
                    }
                    .onFailure { err ->
                        _statusMessage.value = err.localizedMessage ?: "Failed to remove outfit."
                    }
            } else {
                val saved = SavedOutfit(
                    userId = currentUserId,
                    title = outfit.title,
                    audience = outfit.audience,
                    occasion = outfit.occasion,
                    trendHighlight = outfit.trendHighlight,
                    coverageVerification = outfit.coverageVerification,
                    stylingNotes = outfit.stylingNotes,
                    matchedPieces = outfit.matchedPieces,
                    boutiqueSources = outfit.boutiqueSources,
                    favorite = false
                )
                repository.saveOutfit(saved)
                    .onSuccess {
                        _statusMessage.value = "Saved \"${outfit.title}\" to your Closet."
                    }
                    .onFailure { err ->
                        _statusMessage.value = err.localizedMessage ?: "Failed to save outfit to Closet."
                    }
            }
            _savingOutfitTitles.value = _savingOutfitTitles.value - normalizedTitle
        }
    }

    fun submitDailyOutfitFeedback(
        outfit: AiCuratedOutfit,
        vote: FeedbackVote,
        dateKey: String
    ) {
        val normalizedTitle = outfit.title.trim().take(120)
        if (normalizedTitle in _submittingFeedbackTitles.value) return
        val safeDateKey = dateKey.trim().ifBlank {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        }.take(20)
        val docId = outfitFeedbackDocId(safeDateKey, normalizedTitle)

        viewModelScope.launch {
            _submittingFeedbackTitles.value = _submittingFeedbackTitles.value + normalizedTitle
            val existingFeedback = currentFeedbackOrEmpty().firstOrNull {
                it.id == docId || (it.dateKey == safeDateKey && it.outfitTitle.trim().take(120) == normalizedTitle)
            }
            val existingVote = FeedbackVote.fromRaw(existingFeedback?.vote)

            if (existingFeedback != null && existingVote == vote) {
                repository.deleteOutfitFeedback(existingFeedback.id.ifBlank { docId })
                    .onSuccess {
                        _statusMessage.value = "Cleared rating for \"${outfit.title}\"."
                    }
                    .onFailure { err ->
                        _statusMessage.value = err.localizedMessage ?: "Failed to update rating."
                    }
            } else {
                val feedback = OutfitFeedback(
                    id = existingFeedback?.id?.ifBlank { docId } ?: docId,
                    userId = currentUserId,
                    outfitTitle = normalizedTitle,
                    vote = vote.name,
                    dateKey = safeDateKey,
                    audience = outfit.audience,
                    occasion = outfit.occasion,
                    trendHighlight = outfit.trendHighlight,
                    matchedPieces = outfit.matchedPieces
                )
                repository.upsertOutfitFeedback(feedback)
                    .onSuccess {
                        _statusMessage.value = if (vote == FeedbackVote.UP) {
                            "Saved Thumbs Up! Future AI outfits will lean toward styles like this."
                        } else {
                            "Saved Thumbs Down! Future AI outfits will steer in a different direction."
                        }
                    }
                    .onFailure { err ->
                        _statusMessage.value = err.localizedMessage ?: "Failed to save rating."
                    }
            }
            _submittingFeedbackTitles.value = _submittingFeedbackTitles.value - normalizedTitle
        }
    }

    fun toggleOutfitFavorite(outfitId: String, favorite: Boolean) {
        viewModelScope.launch {
            repository.toggleOutfitFavorite(outfitId, favorite)
                .onFailure { err ->
                    _statusMessage.value = err.localizedMessage ?: "Failed to update favorite."
                }
        }
    }

    fun updateSavedOutfitNotes(
        outfitId: String,
        title: String,
        stylingNotes: String,
        favorite: Boolean
    ) {
        viewModelScope.launch {
            repository.updateSavedOutfitNotes(outfitId, title, stylingNotes, favorite)
                .onSuccess {
                    _statusMessage.value = "Updated closet notes."
                }
                .onFailure { err ->
                    _statusMessage.value = err.localizedMessage ?: "Failed to update closet notes."
                }
        }
    }

    fun deleteSavedOutfit(outfitId: String) {
        viewModelScope.launch {
            repository.deleteSavedOutfit(outfitId)
                .onSuccess {
                    _statusMessage.value = "Removed ensemble from Saved Closet."
                }
                .onFailure { err ->
                    _statusMessage.value = err.localizedMessage ?: "Failed to delete outfit."
                }
        }
    }

    fun seedStarterBoutique() {
        viewModelScope.launch {
            val district = currentProfileOrDefault().cityOrArea
            repository.seedStarterBoutiqueInventory(district)
                .onSuccess { count ->
                    _statusMessage.value = "Loaded $count Ladies, Teens & Girls pieces into Local Inventory."
                }
                .onFailure { err ->
                    _statusMessage.value = err.localizedMessage ?: "Failed to load starter collection."
                }
        }
    }

    fun addInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            repository.addInventoryItem(item)
                .onSuccess {
                    _statusMessage.value = "Added \"${item.name}\" to Local Inventory."
                }
                .onFailure { err ->
                    _statusMessage.value = err.localizedMessage ?: "Failed to add garment."
                }
        }
    }

    fun toggleInventoryStock(itemId: String, inStock: Boolean) {
        viewModelScope.launch {
            repository.toggleInventoryStock(itemId, inStock)
                .onFailure { err ->
                    _statusMessage.value = err.localizedMessage ?: "Failed to update stock status."
                }
        }
    }

    fun deleteInventoryItem(itemId: String) {
        viewModelScope.launch {
            repository.deleteInventoryItem(itemId)
                .onSuccess {
                    _statusMessage.value = "Removed garment from Local Inventory."
                }
                .onFailure { err ->
                    _statusMessage.value = err.localizedMessage ?: "Failed to remove garment."
                }
        }
    }

    fun analyzeGarmentBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            _garmentScanState.value = GarmentScanUiState.Scanning
            try {
                val result = aiService.analyzeGarmentPhoto(bitmap, currentProfileOrDefault())
                _garmentScanState.value = GarmentScanUiState.Success(result)
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Garment photo scan timed out", e)
                _garmentScanState.value = GarmentScanUiState.Error(
                    "Photo analysis timed out after 60 seconds."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Garment photo scan failed", e)
                _garmentScanState.value = GarmentScanUiState.Error(
                    e.localizedMessage ?: "Unable to analyze garment photo."
                )
            }
        }
    }

    fun clearGarmentScan() {
        _garmentScanState.value = GarmentScanUiState.Idle
    }

    fun saveProfile(profile: StyleProfile, onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            _isSavingProfile.value = true
            repository.saveStyleProfile(profile)
                .onSuccess {
                    _statusMessage.value = "Saved your style, palette, and modesty charter to Firestore."
                    onSaved()
                }
                .onFailure { err ->
                    _statusMessage.value = err.localizedMessage ?: "Failed to save profile."
                }
            _isSavingProfile.value = false
        }
    }

    private companion object {
        const val TAG = "ModestTailorVM"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
