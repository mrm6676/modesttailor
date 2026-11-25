package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.TailorAiService
import com.example.data.model.AiCuratedOutfit
import com.example.data.model.FashionCatalogConstants
import com.example.data.model.FeedbackVote
import com.example.data.model.OutfitFeedback
import com.example.data.model.SavedOutfit
import com.example.data.model.StyleProfile
import com.example.data.model.outfitFeedbackDocId
import com.example.data.model.preferenceFingerprint
import com.example.util.ClosetSearchHelper
import com.example.util.ClosetSearchHelper.deriveStyleTags
import com.example.util.ClosetSearchHelper.matchesClosetFilter
import com.example.util.OutfitShareHelper
import com.example.util.SizeMatchingHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Modest Tailor", appName)
  }

  @Test
  fun `outfit deep link round trip preserves all outfit details`() {
    val sample = AiCuratedOutfit(
      title = "Rosewood Silk Maxi & Linen Trench",
      audience = "Ladies",
      occasion = "Evening Gathering",
      trendHighlight = "Architectural longline drape",
      coverageVerification = "High collarbone, wrist-length cuff sleeves, ankle-length skirt",
      matchedPieces = listOf("Silk Maxi Dress", "Linen Trench Coat", "Chiffon Hijab"),
      boutiqueSources = listOf("Noor Atelier", "Safa Silk House"),
      stylingNotes = "Pin chiffon softly with brass brooch for warm evening polish."
    )

    val uri = OutfitShareHelper.buildOutfitDeepLink(sample)
    assertEquals("modesttailor", uri.scheme)
    assertEquals("outfit", uri.host)

    val parsed = OutfitShareHelper.parseOutfitDeepLink(uri)
    assertNotNull(parsed)
    assertEquals(sample.title, parsed!!.title)
    assertEquals(sample.audience, parsed.audience)
    assertEquals(sample.occasion, parsed.occasion)
    assertEquals(sample.coverageVerification, parsed.coverageVerification)
    assertEquals(sample.matchedPieces, parsed.matchedPieces)
    assertEquals(sample.boutiqueSources, parsed.boutiqueSources)
    assertEquals(sample.stylingNotes, parsed.stylingNotes)
    assertNull(OutfitShareHelper.parseOutfitDeepLink(Uri.parse("https://example.com/outfit")))
  }

  @Test
  fun `createShareIntent generates lookbook PNG and chooser intent`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val sample = AiCuratedOutfit(
      title = "Sage Pleated Tunic Edit",
      audience = "Girls",
      occasion = "Family Eid Brunch",
      trendHighlight = "Soft sage pleated layers",
      coverageVerification = "Full arm sleeves and midi-below-knee tunic",
      matchedPieces = listOf("Pleated Sage Tunic", "Ivory Wide-Leg Trousers"),
      boutiqueSources = listOf("Bloom Modest"),
      stylingNotes = "Pair with ivory flats and lightweight cotton scarf."
    )

    val chooser = OutfitShareHelper.createShareIntent(context, sample)
    assertEquals(Intent.ACTION_CHOOSER, chooser.action)

    @Suppress("DEPRECATION")
    val innerSend = chooser.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
    assertNotNull(innerSend)
    assertEquals(Intent.ACTION_SEND, innerSend!!.action)
    assertEquals("image/png", innerSend.type)
    assertTrue(
      innerSend.getStringExtra(Intent.EXTRA_TEXT).orEmpty().contains("modesttailor://outfit")
    )

    @Suppress("DEPRECATION")
    val streamUri = innerSend.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
    assertNotNull(streamUri)
    assertEquals("${context.packageName}.fileprovider", streamUri!!.authority)
  }

  @Test
  fun `closet tag search filters saved outfits by summer formal work and custom hashtags`() {
    val summerOutfit = SavedOutfit(
      id = "o1",
      title = "Breezy Linen Maxi Co-ord",
      audience = "Ladies",
      occasion = "Breezy Summer Linen",
      trendHighlight = "Quiet Luxury Linen",
      coverageVerification = "Full wrist sleeves and ankle drape",
      stylingNotes = "Lightweight chiffon wrap #brunch",
      matchedPieces = listOf("Ivory Linen Dress")
    )
    val formalOutfit = SavedOutfit(
      id = "o2",
      title = "Rosewood Silk Satin Kaftan",
      audience = "Ladies",
      occasion = "Evening Celebration",
      trendHighlight = "Pleated Satin Movement",
      coverageVerification = "Opaque double-lined floor hem",
      stylingNotes = "Champagne brooch styling",
      matchedPieces = listOf("Rosewood Satin Dress")
    )
    val workOutfit = SavedOutfit(
      id = "o3",
      title = "Sage Belted Trench & Mock Neck",
      audience = "Ladies",
      occasion = "Work & Studio",
      trendHighlight = "Structured Loose Tailoring",
      coverageVerification = "High collar and full cuffs",
      stylingNotes = "Ideal for office presentations",
      matchedPieces = listOf("Sage Linen Trench")
    )

    assertTrue("Summer" in summerOutfit.deriveStyleTags())
    assertTrue("brunch" in summerOutfit.deriveStyleTags())
    assertTrue("Formal" in formalOutfit.deriveStyleTags())
    assertTrue("Work" in workOutfit.deriveStyleTags())

    assertTrue(summerOutfit.matchesClosetFilter("All", "summer"))
    assertFalse(formalOutfit.matchesClosetFilter("All", "summer"))

    assertTrue(formalOutfit.matchesClosetFilter("All", "#formal"))
    assertFalse(workOutfit.matchesClosetFilter("All", "#formal"))

    assertTrue(workOutfit.matchesClosetFilter("All", "work"))
    assertTrue(workOutfit.matchesClosetFilter("All", "work summer"))
    assertFalse(summerOutfit.matchesClosetFilter("All", "work"))

    val toggledOn = ClosetSearchHelper.toggleTagInQuery("", "Summer")
    assertEquals("summer", toggledOn)
    val toggledOff = ClosetSearchHelper.toggleTagInQuery("summer formal", "Summer")
    assertEquals("formal", toggledOff)
  }

  @Test
  fun `daily outfit feedback generates rules-valid id and feeds AI preference learning block`() {
    val docId = outfitFeedbackDocId("2026-11-23", "Monday Sage Linen & Ivory Silk Edit!")
    assertTrue(docId.matches(Regex("^[a-zA-Z0-9_\\-]{1,128}$")))
    assertEquals(FeedbackVote.UP, FeedbackVote.fromRaw("up"))
    assertEquals(FeedbackVote.DOWN, FeedbackVote.fromRaw("DOWN"))
    assertNull(FeedbackVote.fromRaw("invalid"))

    val service = TailorAiService()
    val block = service.buildFeedbackLearningBlock(
      listOf(
        OutfitFeedback(
          outfitTitle = "Sage Linen Trench Co-ord",
          vote = FeedbackVote.UP.name,
          audience = "Ladies",
          trendHighlight = "Quiet Luxury Linen",
          matchedPieces = listOf("Sage Linen Trench")
        ),
        OutfitFeedback(
          outfitTitle = "Heavy Velvet Evening Abaya",
          vote = FeedbackVote.DOWN.name,
          audience = "Ladies",
          trendHighlight = "Velvet Drape",
          matchedPieces = listOf("Heavy Velvet Abaya")
        )
      )
    )
    assertTrue(block.contains("Liked Looks (Thumbs Up"))
    assertTrue(block.contains("Sage Linen Trench Co-ord"))
    assertTrue(block.contains("Disliked Looks (Thumbs Down"))
    assertTrue(block.contains("Heavy Velvet Evening Abaya"))
  }

  @Test
  fun `clothing size filter partitions boutique inventory and invalidates fingerprint`() {
    val starter = FashionCatalogConstants.starterBoutiqueItems("u1", "Oakbrook")

    val baseProfile = StyleProfile(
      userId = "u1",
      audience = "Ladies & Girls",
      topSize = "M",
      bottomSize = "M",
      dressAbayaLength = "54",
      girlsSize = "Girls 10-12"
    )
    val xxlTallLadiesProfile = baseProfile.copy(
      audience = "Ladies",
      topSize = "XXL",
      bottomSize = "XS",
      dressAbayaLength = "60",
      girlsSize = "Not Needed"
    )

    assertNotEquals(
      baseProfile.preferenceFingerprint(),
      xxlTallLadiesProfile.preferenceFingerprint()
    )

    val allMatchedForDefault = SizeMatchingHelper.partitionInventoryBySize(starter, baseProfile)
    assertTrue(allMatchedForDefault.matchingItems.any { it.name.contains("Silk Ivory Bishop-Sleeve") })
    assertTrue(allMatchedForDefault.matchingItems.any { it.name.contains("Espresso Satin-Trimmed Open Abaya") })
    assertTrue(allMatchedForDefault.matchingItems.any { it.name.contains("Girls' Chambray") })

    val filtered = SizeMatchingHelper.partitionInventoryBySize(starter, xxlTallLadiesProfile)
    val matchedNames = filtered.matchingItems.map { it.name }
    val excludedNames = filtered.excludedItems.map { it.name }

    // Trench ("S - XXL") & Skirt ("XS - XXL") & One Size Hijab match top=XXL, bottom=XS
    assertTrue(matchedNames.any { it.contains("Sage Belted Longline Linen Trench Coat") })
    assertTrue(matchedNames.any { it.contains("Rosewood Sunburst Pleated Satin Maxi Skirt") })
    assertTrue(matchedNames.any { it.contains("Champagne Modal Crinkle Hijab") })

    // Blouse ("XS - XL") excludes XXL top, Abaya ("52 - 58 Length") excludes 60", Girls/Teens excluded
    assertTrue(excludedNames.any { it.contains("Silk Ivory Bishop-Sleeve Mock-Neck Blouse") })
    assertTrue(excludedNames.any { it.contains("Espresso Satin-Trimmed Open Abaya") })
    assertTrue(excludedNames.any { it.contains("Girls' Chambray") })

    val aiPromptBlock = TailorAiService().buildSizeFilteredInventoryBlock(xxlTallLadiesProfile, starter)
    assertTrue(aiPromptBlock.contains("SIZE-MATCHED IN-STOCK PIECES"))
    assertTrue(aiPromptBlock.contains("EXCLUDED (OUT OF SIZE RANGE"))
    assertTrue(aiPromptBlock.contains("Silk Ivory Bishop-Sleeve Mock-Neck Blouse [XS - XL]"))

    val compactDailyBlock = TailorAiService().buildCompactDailyInventoryBlock(xxlTallLadiesProfile, starter)
    assertTrue(compactDailyBlock.contains("Sage Belted Longline Linen Trench Coat"))
    assertFalse(compactDailyBlock.contains("Silk Ivory Bishop-Sleeve Mock-Neck Blouse"))
  }

  @Test
  fun `youth size filter accurately matches girls and teens ranges`() {
    val starter = FashionCatalogConstants.starterBoutiqueItems("u1", "Oakbrook")
    val girls14Profile = StyleProfile(
      userId = "u1",
      audience = "Girls",
      girlsSize = "Girls 14"
    )
    val filtered = SizeMatchingHelper.partitionInventoryBySize(starter, girls14Profile)
    val matchedNames = filtered.matchingItems.map { it.name }

    // Cardigan is Girls 6 - 14 (matches 14), Dress is Girls 6 - 12 (excludes 14)
    assertTrue(matchedNames.any { it.contains("Dusty Rose Pointelle Long-Sleeve Cardigan") })
    assertFalse(matchedNames.any { it.contains("Chambray Peter-Pan Collar Tiered Maxi Dress") })
  }

  @Test
  fun `guest atelier session works immediately with starter inventory bookmarks and sizes`() = runTest {
    val vm = com.example.ui.GuestAtelierViewModel(
      aiService = TailorAiService(),
      dailyOutfitCache = null,
      autoGenerateDailyEdit = false
    )

    val initialProfile = (vm.profileState.first() as com.example.ui.viewmodel.DataUiState.Success).data
    assertEquals("Guest Stylist", initialProfile.displayName)
    assertEquals("guest", initialProfile.userId)

    val initialInventory = (vm.inventoryState.first() as com.example.ui.viewmodel.DataUiState.Success).data
    assertTrue(initialInventory.isNotEmpty())

    val sampleLook = AiCuratedOutfit(
      title = "Linen Trench & Pleated Skirt",
      audience = "Ladies",
      occasion = "Work & Studio",
      trendHighlight = "Quiet Luxury Linen",
      coverageVerification = "Full wrist cuffs and ankle drape",
      matchedPieces = listOf("Sage Linen Trench"),
      boutiqueSources = listOf("Noor Atelier"),
      stylingNotes = "Effortless tailored layering."
    )
    vm.toggleClosetBookmark(sampleLook)
    assertTrue("Linen Trench & Pleated Skirt" in vm.savedLookTitles.first())
    assertEquals(1, vm.savedOutfitsCount.first())

    vm.submitDailyOutfitFeedback(sampleLook, FeedbackVote.UP, "2026-11-23")
    assertEquals(
      FeedbackVote.UP,
      vm.dailyFeedbackVotes.first()["2026-11-23|Linen Trench & Pleated Skirt"]
    )

    var callbackFired = false
    var updatedDraft: StyleProfile? = null
    vm.saveProfile(
      profile = initialProfile.copy(topSize = "XL", cityOrArea = "Downtown"),
      onSaved = { callbackFired = true },
      onProfileChanged = { updatedDraft = it }
    )
    assertTrue(callbackFired)
    assertEquals("XL", updatedDraft?.topSize)
    assertEquals("Downtown", updatedDraft?.cityOrArea)
  }

  @Test
  fun `size and coverage partition accurately reflects hijab and hemline requirements`() {
    val starter = FashionCatalogConstants.starterBoutiqueItems("u1", "Oakbrook")
    val strictFloorNoHijab = StyleProfile(
      userId = "u1",
      audience = "Ladies",
      hemlineRule = "Maxi / Floor-Length",
      hijabFriendly = false,
      topSize = "M",
      bottomSize = "M",
      dressAbayaLength = "54"
    )
    val result = SizeMatchingHelper.partitionInventoryBySizeAndCoverage(starter, strictFloorNoHijab)
    val matchedNames = result.matchingItems.map { it.name }
    val excludedNames = result.excludedItems.map { it.name }

    // Floor-length Ladies skirt, blouse, and abaya match; hijab & calf-skimming duster & girls/teens excluded
    assertTrue(matchedNames.any { it.contains("Rosewood Sunburst Pleated Satin Maxi Skirt") })
    assertTrue(matchedNames.any { it.contains("Espresso Satin-Trimmed Open Abaya") })
    assertTrue(excludedNames.any { it.contains("Champagne Modal Crinkle Hijab") })
    assertTrue(excludedNames.any { it.contains("Sage Belted Longline Linen Trench") })
  }
}



