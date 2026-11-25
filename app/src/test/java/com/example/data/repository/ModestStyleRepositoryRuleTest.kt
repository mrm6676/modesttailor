package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.InventoryItem
import com.example.data.model.SavedOutfit
import com.example.data.model.StyleProfile
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class ModestStyleRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun saveAndGetStyleProfile_authenticatedOwner_succeeds() = runBlocking {
        val uid = signInTestUser("amira_${UUID.randomUUID()}@test.com")
        val repository = ModestStyleRepository(firestore, auth)

        val saveResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveStyleProfile(
                StyleProfile(
                    userId = uid,
                    displayName = "Amira",
                    audience = "Ladies & Girls",
                    necklineRule = "High Crew / Mock Neck",
                    sleeveRule = "Full Wrist-Length",
                    hemlineRule = "Maxi / Ankle-Length",
                    fitRule = "Flowy & Relaxed Drape",
                    hijabFriendly = true,
                    cityOrArea = "Oakbrook"
                )
            )
        }
        assertTrue("Expected profile save to succeed: ${saveResult.exceptionOrNull()}", saveResult.isSuccess)

        val fetched = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.getStyleProfile(uid).getOrThrow()
        }
        assertNotNull(fetched)
        assertEquals("Amira", fetched?.displayName)
        assertEquals("Ladies & Girls", fetched?.audience)
    }

    @Test
    fun addAndObserveInventory_authenticatedOwner_emitsRealtimeItemAndTogglesStock() = runBlocking {
        val uid = signInTestUser("layla_${UUID.randomUUID()}@test.com")
        val repository = ModestStyleRepository(firestore, auth)

        val addResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.addInventoryItem(
                InventoryItem(
                    userId = uid,
                    name = "Girls Chambray Tiered Dress",
                    category = "Dress & Abaya",
                    audience = "Girls",
                    boutiqueName = "Little Cedar Modest Kids",
                    neighborhood = "Promenade",
                    priceUsd = 54.0,
                    color = "Chambray Blue",
                    sizeRange = "Girls 6-12",
                    coverageSummary = "Full sleeves, ankle-length cotton tiers, high Peter-Pan collar",
                    trendTag = "Tiered Cottagecore",
                    inStock = true
                )
            )
        }
        assertTrue("Expected addInventoryItem to succeed: ${addResult.exceptionOrNull()}", addResult.isSuccess)
        val itemId = addResult.getOrThrow()

        val items = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeInventory(uid).first { list -> list.any { it.id == itemId } }
        }
        assertTrue(items.any { it.id == itemId && it.inStock })

        val toggleResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.toggleInventoryStock(itemId, false)
        }
        assertTrue("Expected toggleInventoryStock to succeed: ${toggleResult.exceptionOrNull()}", toggleResult.isSuccess)
    }

    @Test
    fun saveOutfit_authenticatedOwner_persistsAndObservesLookbook() = runBlocking {
        val uid = signInTestUser("noor_${UUID.randomUUID()}@test.com")
        val repository = ModestStyleRepository(firestore, auth)

        val outfitId = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveOutfit(
                SavedOutfit(
                    userId = uid,
                    title = "Rosewood & Linen Gallery Look",
                    audience = "Ladies",
                    occasion = "Weekend Garden Brunch",
                    trendHighlight = "Quiet luxury linen paired with fluid satin pleats.",
                    coverageVerification = "Full wrist sleeves, high collar, ankle-length skirt.",
                    stylingNotes = "Add woven leather flats and draped modal shawl.",
                    matchedPieces = listOf("Rosewood Pleated Skirt", "Silk Ivory Blouse"),
                    boutiqueSources = listOf("Atelier Noor"),
                    favorite = true
                )
            ).getOrThrow()
        }

        val outfits = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeSavedOutfits(uid).first { list -> list.any { it.id == outfitId } }
        }
        assertTrue(outfits.any { it.id == outfitId && it.favorite })
    }

    @Test
    fun getInventoryItemById_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser("alice_${UUID.randomUUID()}@test.com")
        val aliceRepo = ModestStyleRepository(firestore, auth)
        val itemId = withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.addInventoryItem(
                InventoryItem(
                    userId = aliceUid,
                    name = "Alice Private Abaya",
                    category = "Dress & Abaya",
                    audience = "Ladies",
                    boutiqueName = "Dar Al-Harir",
                    neighborhood = "Downtown",
                    priceUsd = 140.0,
                    color = "Espresso",
                    sizeRange = "54",
                    coverageSummary = "Floor length opaque crepe",
                    trendTag = "Occasion Abaya & Kaftan",
                    inStock = true
                )
            ).getOrThrow()
        }

        signInTestUser("bob_${UUID.randomUUID()}@test.com")
        val bobRepo = ModestStyleRepository(firestore, auth)
        val crossResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.getInventoryItemById(aliceUid, itemId)
        }
        assertTrue("Expected cross-user access to fail", crossResult.isFailure)
        val fsEx = crossResult.exceptionOrNull()?.unwrapFirestoreException()
        assertNotNull("Expected FirebaseFirestoreException", fsEx)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsEx?.code)
    }

    @Test
    fun observeInventory_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        val targetUid = "unauth_target_uid"
        auth.signOut()
        val repository = ModestStyleRepository(firestore, auth)

        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeInventory(targetUid).first()
            }
            fail("Expected PERMISSION_DENIED when observing inventory while signed out")
        } catch (e: Exception) {
            val firestoreException = checkNotNull(e.unwrapFirestoreException()) {
                "Expected nested FirebaseFirestoreException, got $e"
            }
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
        }
    }

    private companion object {
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 4000L
    }
}
