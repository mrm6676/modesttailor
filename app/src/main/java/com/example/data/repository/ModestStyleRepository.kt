package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.FashionCatalogConstants
import com.example.data.model.FeedbackVote
import com.example.data.model.InventoryItem
import com.example.data.model.OutfitFeedback
import com.example.data.model.SavedOutfit
import com.example.data.model.StyleProfile
import com.example.data.model.outfitFeedbackDocId
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ModestStyleRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        Firebase.auth
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    // --- 1. StyleProfile (/users/{userId}) ---

    fun observeStyleProfile(userId: String): Flow<StyleProfile?> {
        val docRef = db.collection("users").document(userId)
        return docRef.snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(
                        StyleProfile::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                } else {
                    null
                }
            }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.GET, docRef.path)
                }
                throw error
            }
    }

    suspend fun getStyleProfile(userId: String): Result<StyleProfile?> = runCatching {
        val docRef = db.collection("users").document(userId)
        try {
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                snapshot.toObject(
                    StyleProfile::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            } else {
                null
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            throw e
        }
    }

    suspend fun saveStyleProfile(profile: StyleProfile): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        try {
            val existingSnap = docRef.get().await()
            val sanitizedTrends = profile.trendTags
                .map { it.trim().take(160) }
                .filter { it.isNotEmpty() }
                .take(10)
            val sanitizedColors = profile.favoriteColors
                .map { it.trim().take(160) }
                .filter { it.isNotEmpty() }
                .take(10)

            val safeName = profile.displayName.trim().ifBlank { "Modest Stylist" }.take(80)
            val safeAudience = if (profile.audience in FashionCatalogConstants.AUDIENCE_PROFILE_OPTIONS) {
                profile.audience
            } else {
                "Ladies & Girls"
            }
            val safeTopSize = profile.topSize.trim().ifBlank { "M" }.take(24)
            val safeBottomSize = profile.bottomSize.trim().ifBlank { "M" }.take(24)
            val safeLength = profile.dressAbayaLength.trim().ifBlank { "54" }.take(24)
            val safeGirlsSize = profile.girlsSize.trim().ifBlank { "Not Needed" }.take(24)

            if (existingSnap.exists()) {
                val existingCreatedAt = existingSnap.getTimestamp("createdAt")
                val payload = mapOf(
                    "userId" to uid,
                    "displayName" to safeName,
                    "audience" to safeAudience,
                    "necklineRule" to profile.necklineRule.trim().ifBlank { "High Crew / Mock Neck" }.take(60),
                    "sleeveRule" to profile.sleeveRule.trim().ifBlank { "Full Wrist-Length" }.take(60),
                    "hemlineRule" to profile.hemlineRule.trim().ifBlank { "Maxi / Ankle-Length" }.take(60),
                    "fitRule" to profile.fitRule.trim().ifBlank { "Flowy & Relaxed Drape" }.take(60),
                    "hijabFriendly" to profile.hijabFriendly,
                    "cityOrArea" to profile.cityOrArea.trim().take(80),
                    "trendTags" to sanitizedTrends,
                    "favoriteColors" to sanitizedColors,
                    "topSize" to safeTopSize,
                    "bottomSize" to safeBottomSize,
                    "dressAbayaLength" to safeLength,
                    "girlsSize" to safeGirlsSize,
                    "createdAt" to (existingCreatedAt ?: FieldValue.serverTimestamp()),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            } else {
                val payload = mapOf(
                    "userId" to uid,
                    "displayName" to safeName,
                    "audience" to safeAudience,
                    "necklineRule" to profile.necklineRule.trim().ifBlank { "High Crew / Mock Neck" }.take(60),
                    "sleeveRule" to profile.sleeveRule.trim().ifBlank { "Full Wrist-Length" }.take(60),
                    "hemlineRule" to profile.hemlineRule.trim().ifBlank { "Maxi / Ankle-Length" }.take(60),
                    "fitRule" to profile.fitRule.trim().ifBlank { "Flowy & Relaxed Drape" }.take(60),
                    "hijabFriendly" to profile.hijabFriendly,
                    "cityOrArea" to profile.cityOrArea.trim().take(80),
                    "trendTags" to sanitizedTrends,
                    "favoriteColors" to sanitizedColors,
                    "topSize" to safeTopSize,
                    "bottomSize" to safeBottomSize,
                    "dressAbayaLength" to safeLength,
                    "girlsSize" to safeGirlsSize,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    // --- 2. InventoryItem (/users/{userId}/inventory/{itemId}) ---

    fun observeInventory(userId: String): Flow<List<InventoryItem>> {
        val collectionPath = "users/$userId/inventory"
        return db.collection("users")
            .document(userId)
            .collection("inventory")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    InventoryItem::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.LIST, collectionPath)
                }
                throw error
            }
    }

    suspend fun getInventoryItemById(targetUserId: String, itemId: String): Result<InventoryItem?> = runCatching {
        val docRef = db.collection("users")
            .document(targetUserId)
            .collection("inventory")
            .document(itemId)
        try {
            val snap = docRef.get().await()
            snap.toObject(
                InventoryItem::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            throw e
        }
    }

    suspend fun addInventoryItem(item: InventoryItem): Result<String> = runCatching {
        val uid = requireUserId()
        val collectionRef = db.collection("users").document(uid).collection("inventory")
        val docRef = collectionRef.document()
        try {
            val safeCategory = if (item.category in FashionCatalogConstants.CATEGORY_OPTIONS) {
                item.category
            } else {
                "Dress & Abaya"
            }
            val safeAudience = if (item.audience in FashionCatalogConstants.ITEM_AUDIENCE_OPTIONS) {
                item.audience
            } else {
                "Ladies"
            }
            val payload = mapOf(
                "userId" to uid,
                "name" to item.name.trim().ifBlank { "Modest Apparel Piece" }.take(100),
                "category" to safeCategory,
                "audience" to safeAudience,
                "boutiqueName" to item.boutiqueName.trim().ifBlank { "Personal Wardrobe" }.take(80),
                "neighborhood" to item.neighborhood.trim().take(80),
                "priceUsd" to item.priceUsd.coerceIn(0.0, 50000.0),
                "color" to item.color.trim().ifBlank { "Neutral" }.take(50),
                "sizeRange" to item.sizeRange.trim().ifBlank { "One Size" }.take(60),
                "coverageSummary" to item.coverageSummary.trim().ifBlank { "Modest opaque full coverage" }.take(240),
                "trendTag" to item.trendTag.trim().ifBlank { "Contemporary Modest" }.take(60),
                "inStock" to item.inStock,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun toggleInventoryStock(itemId: String, inStock: Boolean): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("inventory").document(itemId)
        try {
            docRef.update(
                mapOf(
                    "inStock" to inStock,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteInventoryItem(itemId: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("inventory").document(itemId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    suspend fun seedStarterBoutiqueInventory(district: String): Result<Int> = runCatching {
        val uid = requireUserId()
        val items = FashionCatalogConstants.starterBoutiqueItems(uid, district)
        val batch = db.batch()
        val collectionRef = db.collection("users").document(uid).collection("inventory")
        for (item in items) {
            val docRef = collectionRef.document()
            batch.set(
                docRef,
                mapOf(
                    "userId" to uid,
                    "name" to item.name.take(100),
                    "category" to item.category,
                    "audience" to item.audience,
                    "boutiqueName" to item.boutiqueName.take(80),
                    "neighborhood" to item.neighborhood.take(80),
                    "priceUsd" to item.priceUsd,
                    "color" to item.color.take(50),
                    "sizeRange" to item.sizeRange.take(60),
                    "coverageSummary" to item.coverageSummary.take(240),
                    "trendTag" to item.trendTag.take(60),
                    "inStock" to true,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
        }
        try {
            batch.commit().await()
            items.size
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, collectionRef.path)
            throw e
        }
    }

    // --- 3. SavedOutfit (/users/{userId}/saved_outfits/{outfitId}) ---

    fun observeSavedOutfits(userId: String): Flow<List<SavedOutfit>> {
        val collectionPath = "users/$userId/saved_outfits"
        return db.collection("users")
            .document(userId)
            .collection("saved_outfits")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    SavedOutfit::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedWith(
                    compareByDescending<SavedOutfit> { it.favorite }
                        .thenByDescending { it.createdAt ?: Timestamp(0, 0) }
                )
            }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.LIST, collectionPath)
                }
                throw error
            }
    }

    suspend fun saveOutfit(outfit: SavedOutfit): Result<String> = runCatching {
        val uid = requireUserId()
        val collectionRef = db.collection("users").document(uid).collection("saved_outfits")
        val docRef = collectionRef.document()
        try {
            val safeAudience = if (outfit.audience in FashionCatalogConstants.AUDIENCE_PROFILE_OPTIONS) {
                outfit.audience
            } else {
                "Ladies"
            }
            val payload = mapOf(
                "userId" to uid,
                "title" to outfit.title.trim().ifBlank { "Curated Modest Look" }.take(120),
                "audience" to safeAudience,
                "occasion" to outfit.occasion.trim().ifBlank { "Everyday Chic" }.take(80),
                "trendHighlight" to outfit.trendHighlight.trim().ifBlank { "Contemporary modest silhouette" }.take(240),
                "coverageVerification" to outfit.coverageVerification.trim().ifBlank { "Meets full modest coverage preferences" }.take(400),
                "stylingNotes" to outfit.stylingNotes.trim().ifBlank { "Layer gracefully for effortless movement." }.take(600),
                "matchedPieces" to outfit.matchedPieces.map { it.trim().take(160) }.filter { it.isNotEmpty() }.take(12),
                "boutiqueSources" to outfit.boutiqueSources.map { it.trim().take(160) }.filter { it.isNotEmpty() }.take(12),
                "favorite" to outfit.favorite,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            docRef.id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun toggleOutfitFavorite(outfitId: String, favorite: Boolean): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("saved_outfits").document(outfitId)
        try {
            docRef.update(
                mapOf(
                    "favorite" to favorite,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun updateSavedOutfitNotes(
        outfitId: String,
        title: String,
        stylingNotes: String,
        favorite: Boolean
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("saved_outfits").document(outfitId)
        try {
            docRef.update(
                mapOf(
                    "title" to title.trim().ifBlank { "Curated Modest Look" }.take(120),
                    "stylingNotes" to stylingNotes.trim().ifBlank { "Layer gracefully for effortless movement." }.take(600),
                    "favorite" to favorite,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    suspend fun deleteSavedOutfit(outfitId: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("saved_outfits").document(outfitId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }

    // --- 4. OutfitFeedback (/users/{userId}/outfit_feedback/{feedbackId}) ---

    fun observeOutfitFeedback(userId: String): Flow<List<OutfitFeedback>> {
        val collectionPath = "users/$userId/outfit_feedback"
        return db.collection("users")
            .document(userId)
            .collection("outfit_feedback")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    OutfitFeedback::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.updatedAt ?: it.createdAt ?: Timestamp(0, 0) }
            }
            .catch { error ->
                if (error is Exception) {
                    handleFirestoreError(error, OperationType.LIST, collectionPath)
                }
                throw error
            }
    }

    suspend fun upsertOutfitFeedback(feedback: OutfitFeedback): Result<Unit> = runCatching {
        val uid = requireUserId()
        val safeDateKey = feedback.dateKey.trim().ifBlank { "daily" }.take(20)
        val safeTitle = feedback.outfitTitle.trim().ifBlank { "Curated Modest Look" }.take(120)
        val docId = feedback.id.ifBlank { outfitFeedbackDocId(safeDateKey, safeTitle) }
        val docRef = db.collection("users")
            .document(uid)
            .collection("outfit_feedback")
            .document(docId)

        try {
            val safeVote = FeedbackVote.fromRaw(feedback.vote)?.name ?: FeedbackVote.UP.name
            val safeAudience = if (feedback.audience in FashionCatalogConstants.AUDIENCE_PROFILE_OPTIONS) {
                feedback.audience
            } else {
                "Ladies"
            }
            val safePieces = feedback.matchedPieces
                .map { it.trim().take(160) }
                .filter { it.isNotEmpty() }
                .take(12)
            val safeTrend = feedback.trendHighlight.trim().take(240)
            val existingSnap = docRef.get().await()

            if (existingSnap.exists()) {
                docRef.update(
                    mapOf(
                        "vote" to safeVote,
                        "trendHighlight" to safeTrend,
                        "matchedPieces" to safePieces,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                docRef.set(
                    mapOf(
                        "userId" to uid,
                        "outfitTitle" to safeTitle,
                        "vote" to safeVote,
                        "dateKey" to safeDateKey,
                        "audience" to safeAudience,
                        "occasion" to feedback.occasion.trim().take(80),
                        "trendHighlight" to safeTrend,
                        "matchedPieces" to safePieces,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    suspend fun deleteOutfitFeedback(feedbackId: String): Result<Unit> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users")
            .document(uid)
            .collection("outfit_feedback")
            .document(feedbackId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }
}
