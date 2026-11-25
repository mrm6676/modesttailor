package com.example.data.ai

import android.graphics.Bitmap
import android.util.Log
import com.example.data.model.AiCuratedOutfit
import com.example.data.model.AiDailyEditRecommendation
import com.example.data.model.AiGarmentScanResult
import com.example.data.model.AiStylingRecommendation
import com.example.data.model.FashionCatalogConstants
import com.example.data.model.FeedbackVote
import com.example.data.model.InventoryItem
import com.example.data.model.OutfitFeedback
import com.example.data.model.StyleProfile
import com.example.data.model.formattedSizeSummary
import com.example.util.SizeMatchingHelper
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.ThinkingLevel
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.ai.type.thinkingConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject

class TailorAiService {

    private val dailyEditSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "dayTheme" to Schema.string(
                    description = "Short 3-5 word editorial theme for today's outfits (e.g. Crisp Monday Linen Layering)."
                ),
                "editorialSummary" to Schema.string(
                    description = "Concise 1-2 sentence daily stylist greeting tailored to the user's stored preferences and coverage charter."
                ),
                "outfits" to Schema.array(
                    description = "2 daily personalized modest ensembles curated specifically for today.",
                    items = outfitItemSchema
                )
            )
        )
    }

    private val outfitItemSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "title" to Schema.string(
                    description = "Refined lookbook title for the modest ensemble."
                ),
                "audience" to Schema.enumeration(
                    values = FashionCatalogConstants.AUDIENCE_PROFILE_OPTIONS,
                    description = "Intended wearer category: Ladies, Girls, Teens, or Ladies & Girls."
                ),
                "occasion" to Schema.string(
                    description = "Occasion and climate context."
                ),
                "trendHighlight" to Schema.string(
                    description = "How this outfit captures current modest fashion trends."
                ),
                "coverageVerification" to Schema.string(
                    description = "Explicit check confirming neckline, sleeve length, hemline, opacity, and loose drape match the user's guidelines."
                ),
                "stylingNotes" to Schema.string(
                    description = "Actionable tailoring, layering, footwear, and headscarf/accessory styling advice."
                ),
                "matchedPieces" to Schema.array(
                    items = Schema.string(),
                    description = "Exact names of local inventory items paired in this outfit, plus any essential base layer."
                ),
                "boutiqueSources" to Schema.array(
                    items = Schema.string(),
                    description = "Names of the local boutiques or wardrobe sources carrying the matched pieces."
                )
            )
        )
    }

    private val outfitSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "editorialSummary" to Schema.string(
                    description = "Warm, concise editorial stylist introduction explaining how these looks unite modest coverage, current trends, and local inventory."
                ),
                "outfits" to Schema.array(
                    description = "2 to 3 complete head-to-toe modest fashion ensembles.",
                    items = outfitItemSchema
                )
            )
        )
    }

    private val garmentScanSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "suggestedName" to Schema.string(
                    description = "Concise descriptive garment name (e.g. Dusty Rose Pleated Midi Dress)."
                ),
                "category" to Schema.enumeration(
                    values = FashionCatalogConstants.CATEGORY_OPTIONS,
                    description = "Garment category."
                ),
                "audience" to Schema.enumeration(
                    values = FashionCatalogConstants.ITEM_AUDIENCE_OPTIONS,
                    description = "Best suited wearer category: Ladies, Girls, or Teens."
                ),
                "color" to Schema.string(
                    description = "Dominant color or tone."
                ),
                "coverageSummary" to Schema.string(
                    description = "Modest coverage evaluation: neckline height, sleeve length, hem length, fabric opacity, and silhouette looseness."
                ),
                "trendTag" to Schema.string(
                    description = "Closest contemporary modest fashion trend."
                ),
                "stylingTip" to Schema.string(
                    description = "Practical modest layering or pairing suggestion for this piece."
                )
            )
        )
    }

    // Lazily initialized per Firebase AI Logic invariants
    private val recommendationModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = outfitSchema
                thinkingConfig = thinkingConfig {
                    thinkingLevel = ThinkingLevel.LOW
                }
            }
        )
    }

    private val dailyEditModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = dailyEditSchema
                thinkingConfig = thinkingConfig {
                    thinkingLevel = ThinkingLevel.LOW
                }
            }
        )
    }

    private val garmentScanModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = garmentScanSchema
                thinkingConfig = thinkingConfig {
                    thinkingLevel = ThinkingLevel.LOW
                }
            }
        )
    }

    private suspend fun generateJson(
        model: GenerativeModel,
        promptContent: Content
    ): String = withContext(Dispatchers.IO) {
        val response = withTimeout(AI_TIMEOUT_MS) {
            model.generateContent(promptContent)
        }
        response.text?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("Tailor AI returned an empty response.")
    }

    suspend fun generateOutfitRecommendations(
        profile: StyleProfile,
        availableInventory: List<InventoryItem>,
        occasion: String,
        season: String,
        customRequest: String,
        recentFeedback: List<OutfitFeedback> = emptyList()
    ): AiStylingRecommendation {
        val inventoryPromptBlock = buildSizeFilteredInventoryBlock(profile, availableInventory)
        val feedbackBlock = buildFeedbackLearningBlock(recentFeedback)

        val prompt = buildString {
            appendLine("You are Modest Tailor AI, an expert modest fashion stylist for ladies, teens, and girls.")
            appendLine("Curate 3 personalized, trend-forward modest outfits strictly honoring every coverage guideline and saved clothing size below.")
            appendLine()
            appendLine("=== USER STYLE & MODESTY COVERAGE GUIDELINES ===")
            appendLine("- Stylist Client: ${profile.displayName}")
            appendLine("- Target Wearer Audience: ${profile.audience}")
            appendLine("- Neckline Requirement: ${profile.necklineRule}")
            appendLine("- Sleeve Requirement: ${profile.sleeveRule}")
            appendLine("- Hemline Requirement: ${profile.hemlineRule}")
            appendLine("- Silhouette / Fit Requirement: ${profile.fitRule}")
            appendLine("- Coordinate Hijab / Headscarf Styling: ${if (profile.hijabFriendly) "Yes (include opaque shawl/hijab coordination for Ladies/Teens; age-appropriate modest accessories for Girls)" else "Optional / Not required"}")
            appendLine("- Preferred Trend Vibes: ${profile.trendTags.joinToString(", ").ifBlank { "Contemporary Modest Elegance" }}")
            appendLine("- Favorite Color Palette: ${profile.favoriteColors.joinToString(", ").ifBlank { "Warm Neutrals & Earth Tones" }}")
            appendLine("- Local Area: ${profile.cityOrArea}")
            appendLine()
            appendLine("=== SAVED CLOTHING SIZES & FIT PROFILE ===")
            appendLine("- Tops & Outerwear Size: ${profile.topSize}")
            appendLine("- Bottoms & Skirts Size: ${profile.bottomSize}")
            appendLine("- Dress / Abaya Length: ${profile.dressAbayaLength}\"")
            appendLine("- Girls / Teens Size: ${profile.girlsSize}")
            appendLine()
            appendLine("=== OCCASION & CONTEXT ===")
            appendLine("- Occasion: $occasion")
            appendLine("- Season / Weather: $season")
            if (customRequest.isNotBlank()) {
                appendLine("- Special Request from Client: $customRequest")
            }
            if (feedbackBlock.isNotBlank()) {
                appendLine()
                appendLine(feedbackBlock)
            }
            appendLine()
            appendLine("=== SIZE-FILTERED LOCAL BOUTIQUE & CLOSET INVENTORY (IN STOCK) ===")
            appendLine(inventoryPromptBlock)
            appendLine()
            appendLine("INSTRUCTIONS:")
            appendLine("1. Prioritize pairing real in-stock pieces from the SIZE-MATCHED inventory list above, quoting their exact garment names, boutique sources, and confirming the piece fits the client's saved sizes (${profile.formattedSizeSummary()}). Do NOT recommend out-of-size local pieces.")
            appendLine("2. If any piece needs a modest layer (e.g. a high-neck slip, opaque undershirt, cardigan, or leggings for girls' comfort), explicitly include that layering guidance so the outfit 100% satisfies the user's Neckline, Sleeve, Hemline, and Fit guidelines.")
            appendLine("3. For 'Girls' or 'Ladies & Girls', ensure movement-friendly breathable fabrics, age-appropriate silhouettes, and contemporary playful polish.")
            if (feedbackBlock.isNotBlank()) {
                appendLine("4. Respect the User Feedback Learning Signals above: prioritize silhouettes, fabrics, and combinations similar to liked (Thumbs Up) looks and steer away from disliked (Thumbs Down) pairings.")
            }
        }

        try {
            val jsonString = generateJson(
                model = recommendationModel,
                promptContent = content { text(prompt) }
            )

            val root = JSONObject(jsonString)
            val summary = root.optString("editorialSummary", "Curated modest ensembles tailored to your guidelines.")
            val outfitsArray = root.getJSONArray("outfits")
            val parsedOutfits = parseOutfitArray(
                outfitsArray = outfitsArray,
                defaultAudience = profile.audience,
                defaultOccasion = occasion
            )
            return AiStylingRecommendation(
                editorialSummary = summary,
                outfits = parsedOutfits
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate outfit recommendations", e)
            throw e
        }
    }

    suspend fun generateDailyPersonalizedEdit(
        profile: StyleProfile,
        availableInventory: List<InventoryItem>,
        dateKey: String,
        dateDisplay: String,
        dayOfWeek: String,
        recentFeedback: List<OutfitFeedback> = emptyList()
    ): AiDailyEditRecommendation {
        val inventoryBlock = buildSizeFilteredInventoryBlock(profile, availableInventory)
        val feedbackBlock = buildFeedbackLearningBlock(recentFeedback)

        val prompt = buildString {
            appendLine("You are Modest Tailor AI curating the client's Daily Personalized Edit for $dayOfWeek ($dateDisplay).")
            appendLine("Generate 2 cohesive, wearable modest outfits tailored specifically to the user's stored preferences, clothing sizes, and strict coverage charter.")
            appendLine()
            appendLine("=== STORED PREFERENCES & COVERAGE REQUIREMENTS ===")
            appendLine("- Client Name: ${profile.displayName}")
            appendLine("- Audience: ${profile.audience}")
            appendLine("- Neckline Rule: ${profile.necklineRule}")
            appendLine("- Sleeve Rule: ${profile.sleeveRule}")
            appendLine("- Hemline Rule: ${profile.hemlineRule}")
            appendLine("- Fit & Drape Rule: ${profile.fitRule}")
            appendLine("- Hijab / Headscarf Styling: ${if (profile.hijabFriendly) "Required / Coordinated opaque wrap" else "Optional"}")
            appendLine("- Stored Trend Vibes: ${profile.trendTags.joinToString(", ").ifBlank { "Contemporary Modest Elegance" }}")
            appendLine("- Stored Color Palette: ${profile.favoriteColors.joinToString(", ").ifBlank { "Warm Neutrals" }}")
            appendLine("- Local District: ${profile.cityOrArea}")
            appendLine()
            appendLine("=== SAVED CLOTHING SIZES & FIT PROFILE ===")
            appendLine("- Tops & Outerwear Size: ${profile.topSize}")
            appendLine("- Bottoms & Skirts Size: ${profile.bottomSize}")
            appendLine("- Dress / Abaya Length: ${profile.dressAbayaLength}\"")
            appendLine("- Girls / Teens Size: ${profile.girlsSize}")
            if (feedbackBlock.isNotBlank()) {
                appendLine()
                appendLine(feedbackBlock)
            }
            appendLine()
            appendLine("=== SIZE-FILTERED IN-STOCK LOCAL INVENTORY ===")
            appendLine(inventoryBlock)
            appendLine()
            appendLine("INSTRUCTIONS:")
            appendLine("1. Tailor the mood to $dayOfWeek (balanced practical chic for weekdays, relaxed or celebratory polish for weekends/Fridays).")
            appendLine("2. Prioritize the client's stored color palette and trend tags while strictly verifying 100% coverage compliance.")
            appendLine("3. Pair ONLY size-matched in-stock local pieces (${profile.formattedSizeSummary()}) whenever present; exclude out-of-size local stock.")
            appendLine("4. Keep each JSON text field crisp and editorial (1 to 2 sentences max).")
            if (feedbackBlock.isNotBlank()) {
                appendLine("5. Use the User Feedback Learning Signals above: emphasize silhouettes/trends similar to liked looks and avoid disliked combinations.")
            }
        }

        try {
            val jsonString = generateJson(
                model = dailyEditModel,
                promptContent = content { text(prompt) }
            )
            val root = JSONObject(jsonString)
            val dayTheme = root.optString("dayTheme", "$dayOfWeek Modest Edit")
            val summary = root.optString(
                "editorialSummary",
                "Personalized looks curated for $dayOfWeek using your saved coverage rules."
            )
            val parsedOutfits = parseOutfitArray(
                outfitsArray = root.getJSONArray("outfits"),
                defaultAudience = profile.audience,
                defaultOccasion = "$dayOfWeek Daily Edit"
            )
            return AiDailyEditRecommendation(
                dateKey = dateKey,
                dateDisplay = dateDisplay,
                dayTheme = dayTheme,
                editorialSummary = summary,
                outfits = parsedOutfits
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate daily personalized edit", e)
            throw e
        }
    }

    private fun parseOutfitArray(
        outfitsArray: JSONArray,
        defaultAudience: String,
        defaultOccasion: String
    ): List<AiCuratedOutfit> {
        return buildList {
            for (i in 0 until outfitsArray.length()) {
                val obj = outfitsArray.getJSONObject(i)
                val piecesJson = obj.optJSONArray("matchedPieces")
                val pieces = buildList {
                    if (piecesJson != null) {
                        for (j in 0 until piecesJson.length()) {
                            add(piecesJson.getString(j))
                        }
                    }
                }
                val sourcesJson = obj.optJSONArray("boutiqueSources")
                val sources = buildList {
                    if (sourcesJson != null) {
                        for (j in 0 until sourcesJson.length()) {
                            add(sourcesJson.getString(j))
                        }
                    }
                }
                add(
                    AiCuratedOutfit(
                        title = obj.optString("title", "Curated Modest Look"),
                        audience = obj.optString("audience", defaultAudience),
                        occasion = obj.optString("occasion", defaultOccasion),
                        trendHighlight = obj.optString("trendHighlight", ""),
                        coverageVerification = obj.optString("coverageVerification", ""),
                        stylingNotes = obj.optString("stylingNotes", ""),
                        matchedPieces = pieces,
                        boutiqueSources = sources
                    )
                )
            }
        }
    }

    suspend fun analyzeGarmentPhoto(bitmap: Bitmap, profile: StyleProfile): AiGarmentScanResult {
        val prompt = """
            Analyze this garment photo for a modest fashion wardrobe and local boutique catalog (for ladies, teens, and girls).
            Evaluate its neckline coverage, sleeve length, hemline length, fabric opacity, and looseness against these user preferences:
            Neckline: ${profile.necklineRule}, Sleeves: ${profile.sleeveRule}, Hemline: ${profile.hemlineRule}, Fit: ${profile.fitRule}.
            Suggest a clean garment title, category, target audience (Ladies, Girls, or Teens), color, detailed modest coverage summary, current trend tag, and a practical modest layering tip if extra coverage is needed.
        """.trimIndent()

        try {
            val scaledBitmap = withContext(Dispatchers.IO) {
                scaleBitmapSafely(bitmap, maxEdge = 1024)
            }
            val jsonString = generateJson(
                model = garmentScanModel,
                promptContent = content {
                    image(scaledBitmap)
                    text(prompt)
                }
            )
            val json = JSONObject(jsonString)
            return AiGarmentScanResult(
                suggestedName = json.optString("suggestedName", "Modest Apparel Piece"),
                category = json.optString("category", "Dress & Abaya"),
                audience = json.optString("audience", "Ladies"),
                color = json.optString("color", "Neutral"),
                coverageSummary = json.optString("coverageSummary", "Modest relaxed coverage"),
                trendTag = json.optString("trendTag", "Quiet Luxury Linen"),
                stylingTip = json.optString("stylingTip", "Pair with opaque tonal layers.")
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to analyze garment photo", e)
            throw e
        }
    }

    private fun scaleBitmapSafely(source: Bitmap, maxEdge: Int): Bitmap {
        val width = source.width
        val height = source.height
        if (width <= maxEdge && height <= maxEdge) return source
        val ratio = width.toFloat() / height.toFloat()
        val (targetW, targetH) = if (width >= height) {
            maxEdge to (maxEdge / ratio).toInt().coerceAtLeast(1)
        } else {
            (maxEdge * ratio).toInt().coerceAtLeast(1) to maxEdge
        }
        return Bitmap.createScaledBitmap(source, targetW, targetH, true)
    }

    internal fun buildFeedbackLearningBlock(recentFeedback: List<OutfitFeedback>): String {
        if (recentFeedback.isEmpty()) return ""
        val liked = recentFeedback
            .filter { FeedbackVote.fromRaw(it.vote) == FeedbackVote.UP }
            .take(6)
        val disliked = recentFeedback
            .filter { FeedbackVote.fromRaw(it.vote) == FeedbackVote.DOWN }
            .take(6)
        if (liked.isEmpty() && disliked.isEmpty()) return ""

        return buildString {
            appendLine("=== USER FEEDBACK & STYLE LEARNING SIGNALS ===")
            if (liked.isNotEmpty()) {
                appendLine("Liked Looks (Thumbs Up — favor similar silhouettes, palettes & pairings):")
                liked.forEach { fb ->
                    val pieces = fb.matchedPieces.take(4).joinToString(", ").ifBlank { "N/A" }
                    appendLine("- \"${fb.outfitTitle}\" (${fb.audience}, Trend: ${fb.trendHighlight.ifBlank { fb.occasion }}, Pieces: $pieces)")
                }
            }
            if (disliked.isNotEmpty()) {
                appendLine("Disliked Looks (Thumbs Down — avoid repeating these styles/pairings):")
                disliked.forEach { fb ->
                    val pieces = fb.matchedPieces.take(4).joinToString(", ").ifBlank { "N/A" }
                    appendLine("- \"${fb.outfitTitle}\" (${fb.audience}, Trend: ${fb.trendHighlight.ifBlank { fb.occasion }}, Pieces: $pieces)")
                }
            }
        }.trimEnd()
    }

    internal fun buildSizeFilteredInventoryBlock(
        profile: StyleProfile,
        availableInventory: List<InventoryItem>
    ): String {
        val inStockItems = availableInventory.filter { it.inStock }
        if (inStockItems.isEmpty()) {
            return "No local boutique items are currently registered; recommend timeless modest wardrobe staples in sizes (${profile.formattedSizeSummary()}) and suggest local boutique categories to look for."
        }
        val filtered = SizeMatchingHelper.partitionInventoryBySize(inStockItems, profile)
        return buildString {
            if (filtered.matchingItems.isNotEmpty()) {
                appendLine("SIZE-MATCHED IN-STOCK PIECES (${filtered.matchingItems.size} of ${inStockItems.size} match ${profile.formattedSizeSummary()}):")
                filtered.matchingItems.forEach { item ->
                    appendLine("- [${item.audience}] ${item.name} (${item.category}, Sizes: ${item.sizeRange}, Color: ${item.color}, Trend: ${item.trendTag}, Price: $${item.priceUsd.toInt()}, Source: ${item.boutiqueName} in ${item.neighborhood.ifBlank { profile.cityOrArea }}, Coverage: ${item.coverageSummary})")
                }
            } else {
                appendLine("None of the ${inStockItems.size} in-stock local items match the saved sizes (${profile.formattedSizeSummary()}). Recommend wardrobe staples tailored to those exact sizes and note local boutique restock requests.")
            }
            if (filtered.excludedItems.isNotEmpty()) {
                appendLine("EXCLUDED (OUT OF SIZE RANGE — DO NOT RECOMMEND): ${filtered.excludedItems.size} piece(s) filtered out (${filtered.excludedItems.joinToString(", ") { "${it.name} [${it.sizeRange}]" }})")
            }
        }.trimEnd()
    }

    private companion object {
        const val TAG = "TailorAiService"
        const val MODEL_NAME = "gemini-3.8-flash"
        const val AI_TIMEOUT_MS = 60_000L
    }
}
