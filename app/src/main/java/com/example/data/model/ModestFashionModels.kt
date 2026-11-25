package com.example.data.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class StyleProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "Guest Stylist",
    val audience: String = "Ladies & Girls",
    val necklineRule: String = "High Crew / Mock Neck",
    val sleeveRule: String = "Full Wrist-Length",
    val hemlineRule: String = "Maxi / Ankle-Length",
    val fitRule: String = "Flowy & Relaxed Drape",
    val hijabFriendly: Boolean = true,
    val cityOrArea: String = "Local Boutique District",
    val trendTags: List<String> = listOf(
        "Quiet Luxury Linen",
        "Pleated Satin Movement",
        "Tiered Cottagecore"
    ),
    val favoriteColors: List<String> = listOf("Rosewood", "Silk Ivory", "Sage Olive"),
    val topSize: String = "M",
    val bottomSize: String = "M",
    val dressAbayaLength: String = "54",
    val girlsSize: String = "Girls 10-12",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class InventoryItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val category: String = "Dress & Abaya",
    val audience: String = "Ladies",
    val boutiqueName: String = "",
    val neighborhood: String = "",
    val priceUsd: Double = 0.0,
    val color: String = "",
    val sizeRange: String = "XS - XL",
    val coverageSummary: String = "",
    val trendTag: String = "",
    val inStock: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class SavedOutfit(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val audience: String = "Ladies",
    val occasion: String = "",
    val trendHighlight: String = "",
    val coverageVerification: String = "",
    val stylingNotes: String = "",
    val matchedPieces: List<String> = emptyList(),
    val boutiqueSources: List<String> = emptyList(),
    val favorite: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

enum class FeedbackVote {
    UP,
    DOWN;

    companion object {
        fun fromRaw(raw: String?): FeedbackVote? {
            return entries.firstOrNull { it.name.equals(raw, ignoreCase = true) }
        }
    }
}

@Keep
@IgnoreExtraProperties
data class OutfitFeedback(
    @DocumentId val id: String = "",
    val userId: String = "",
    val outfitTitle: String = "",
    val vote: String = FeedbackVote.UP.name,
    val dateKey: String = "",
    val audience: String = "Ladies",
    val occasion: String = "",
    val trendHighlight: String = "",
    val matchedPieces: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

fun outfitFeedbackDocId(dateKey: String, outfitTitle: String): String {
    val cleanDate = dateKey.replace(Regex("[^a-zA-Z0-9_-]"), "_").ifBlank { "daily" }.take(20)
    val cleanSlug = outfitTitle.lowercase()
        .replace(Regex("[^a-z0-9]+"), "_")
        .trim('_')
        .ifBlank { "look" }
        .take(72)
    val hashSuffix = (outfitTitle.hashCode().toUInt().toString(16)).take(8)
    return "${cleanDate}_${cleanSlug}_$hashSuffix".take(120)
}

data class AiCuratedOutfit(
    val title: String,
    val audience: String,
    val occasion: String,
    val trendHighlight: String,
    val coverageVerification: String,
    val stylingNotes: String,
    val matchedPieces: List<String>,
    val boutiqueSources: List<String>
)

data class AiStylingRecommendation(
    val editorialSummary: String,
    val outfits: List<AiCuratedOutfit>
)

data class AiDailyEditRecommendation(
    val dateKey: String,
    val dateDisplay: String,
    val dayTheme: String,
    val editorialSummary: String,
    val outfits: List<AiCuratedOutfit>
)

fun StyleProfile.preferenceFingerprint(): String {
    return listOf(
        audience,
        necklineRule,
        sleeveRule,
        hemlineRule,
        fitRule,
        hijabFriendly.toString(),
        cityOrArea,
        topSize,
        bottomSize,
        dressAbayaLength,
        girlsSize,
        trendTags.sorted().joinToString(","),
        favoriteColors.sorted().joinToString(",")
    ).joinToString("|")
}

fun StyleProfile.formattedSizeSummary(): String {
    val parts = buildList {
        add("Top $topSize")
        add("Bottom $bottomSize")
        add("Length ${dressAbayaLength}\"")
        if (girlsSize.isNotBlank() && !girlsSize.equals("Not Needed", ignoreCase = true)) {
            add("Youth: $girlsSize")
        }
    }
    return parts.joinToString(" • ")
}

data class AiGarmentScanResult(
    val suggestedName: String,
    val category: String,
    val audience: String,
    val color: String,
    val coverageSummary: String,
    val trendTag: String,
    val stylingTip: String
)

object FashionCatalogConstants {
    val AUDIENCE_PROFILE_OPTIONS = listOf("Ladies", "Girls", "Teens", "Ladies & Girls")
    val ITEM_AUDIENCE_OPTIONS = listOf("Ladies", "Girls", "Teens")
    val CATEGORY_OPTIONS = listOf(
        "Top",
        "Bottom",
        "Dress & Abaya",
        "Outerwear",
        "Headwear & Accessory",
        "Set & Co-ord"
    )
    val NECKLINE_OPTIONS = listOf(
        "High Crew / Mock Neck",
        "Boat / Collar Neckline",
        "Hijab-Friendly High Neck"
    )
    val SLEEVE_OPTIONS = listOf(
        "Full Wrist-Length",
        "Three-Quarter (3/4) Sleeve",
        "Layered Long Sleeve"
    )
    val HEMLINE_OPTIONS = listOf(
        "Maxi / Floor-Length",
        "Ankle-Length",
        "Midi Below-Knee"
    )
    val FIT_OPTIONS = listOf(
        "Flowy & Relaxed Drape",
        "Structured Loose Tailoring",
        "A-Line Tiered Silhouette"
    )
    val TREND_TAGS = listOf(
        "Quiet Luxury Linen",
        "Pleated Satin Movement",
        "Tiered Cottagecore",
        "Utility Longline Trench",
        "Monochrome Minimal",
        "Soft Pastel Co-ords",
        "Occasion Abaya & Kaftan",
        "Modest Campus Denim"
    )
    val COLOR_PALETTE = listOf(
        "Rosewood",
        "Silk Ivory",
        "Sage Olive",
        "Warm Camel",
        "Dusty Mauve",
        "Chambray Blue",
        "Espresso",
        "Champagne Gold"
    )
    val OCCASION_OPTIONS = listOf(
        "Everyday Chic",
        "Work & Studio",
        "School & Campus",
        "Festive & Celebration",
        "Weekend Garden Brunch"
    )
    val SEASON_OPTIONS = listOf(
        "Crisp Autumn Layering",
        "Warm Sunlit Spring",
        "Breezy Summer Linen",
        "Cozy Winter Wool & Knit"
    )
    val LADIES_SIZE_OPTIONS = listOf(
        "XS",
        "S",
        "M",
        "L",
        "XL",
        "XXL",
        "Plus 1X-3X"
    )
    val ABAYA_LENGTH_OPTIONS = listOf(
        "50",
        "52",
        "54",
        "56",
        "58",
        "60"
    )
    val GIRLS_TEENS_SIZE_OPTIONS = listOf(
        "Not Needed",
        "Girls 4-6",
        "Girls 7-8",
        "Girls 10-12",
        "Girls 14",
        "Teens XS",
        "Teens S",
        "Teens M",
        "Teens L"
    )

    fun starterBoutiqueItems(userId: String, district: String): List<InventoryItem> {
        val area = district.ifBlank { "Oakbrook & Promenade" }
        return listOf(
            InventoryItem(
                userId = userId,
                name = "Rosewood Sunburst Pleated Satin Maxi Skirt",
                category = "Bottom",
                audience = "Ladies",
                boutiqueName = "Atelier Noor",
                neighborhood = area,
                priceUsd = 88.0,
                color = "Rosewood",
                sizeRange = "XS - XXL",
                coverageSummary = "Full ankle-length opaque satin lining, non-cling fluid pleats, high elastic back waist",
                trendTag = "Pleated Satin Movement",
                inStock = true
            ),
            InventoryItem(
                userId = userId,
                name = "Silk Ivory Bishop-Sleeve Mock-Neck Blouse",
                category = "Top",
                audience = "Ladies",
                boutiqueName = "Atelier Noor",
                neighborhood = area,
                priceUsd = 74.0,
                color = "Silk Ivory",
                sizeRange = "XS - XL",
                coverageSummary = "High mock collar, full wrist-buttoned bishop sleeves, double-lined bodice",
                trendTag = "Quiet Luxury Linen",
                inStock = true
            ),
            InventoryItem(
                userId = userId,
                name = "Sage Belted Longline Linen Trench Coat",
                category = "Outerwear",
                audience = "Ladies",
                boutiqueName = "Olive & Loom Boutique",
                neighborhood = area,
                priceUsd = 135.0,
                color = "Sage Olive",
                sizeRange = "S - XXL",
                coverageSummary = "Calf-skimming relaxed duster cut, full storm cuffs, breathable heavyweight flax linen",
                trendTag = "Utility Longline Trench",
                inStock = true
            ),
            InventoryItem(
                userId = userId,
                name = "Champagne Modal Crinkle Hijab & Shawl Set",
                category = "Headwear & Accessory",
                audience = "Ladies",
                boutiqueName = "Atelier Noor",
                neighborhood = area,
                priceUsd = 28.0,
                color = "Champagne Gold",
                sizeRange = "One Size (190x75cm)",
                coverageSummary = "Generous chest and shoulder drape, non-slip breathable modal weave",
                trendTag = "Monochrome Minimal",
                inStock = true
            ),
            InventoryItem(
                userId = userId,
                name = "Girls' Chambray Peter-Pan Collar Tiered Maxi Dress",
                category = "Dress & Abaya",
                audience = "Girls",
                boutiqueName = "Little Cedar Modest Kids",
                neighborhood = area,
                priceUsd = 54.0,
                color = "Chambray Blue",
                sizeRange = "Girls 6 - 12",
                coverageSummary = "High rounded Peter-Pan collar, elasticated full wrist puff sleeves, ankle-length cotton tiers",
                trendTag = "Tiered Cottagecore",
                inStock = true
            ),
            InventoryItem(
                userId = userId,
                name = "Girls' Dusty Rose Pointelle Long-Sleeve Cardigan",
                category = "Outerwear",
                audience = "Girls",
                boutiqueName = "Little Cedar Modest Kids",
                neighborhood = area,
                priceUsd = 38.0,
                color = "Dusty Mauve",
                sizeRange = "Girls 6 - 14",
                coverageSummary = "Soft combed cotton layer, hip-covering length, full rib-knit cuffs, pearl buttons",
                trendTag = "Soft Pastel Co-ords",
                inStock = true
            ),
            InventoryItem(
                userId = userId,
                name = "Teens' Relaxed Denim Long Tunic & Wide-Leg Co-ord",
                category = "Set & Co-ord",
                audience = "Teens",
                boutiqueName = "Olive & Loom Boutique",
                neighborhood = area,
                priceUsd = 92.0,
                color = "Chambray Blue",
                sizeRange = "Teens XS - L",
                coverageSummary = "Mid-thigh tunic shirt over loose full-length wide-leg trousers, high crew collar",
                trendTag = "Modest Campus Denim",
                inStock = true
            ),
            InventoryItem(
                userId = userId,
                name = "Espresso Satin-Trimmed Open Abaya & Slip Dress",
                category = "Dress & Abaya",
                audience = "Ladies",
                boutiqueName = "Dar Al-Harir",
                neighborhood = area,
                priceUsd = 148.0,
                color = "Espresso",
                sizeRange = "52 - 58 Length",
                coverageSummary = "Floor-skimming opaque crepe inner dress paired with flowing kimono-sleeve outer abaya",
                trendTag = "Occasion Abaya & Kaftan",
                inStock = true
            )
        )
    }
}
