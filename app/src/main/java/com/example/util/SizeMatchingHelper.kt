package com.example.util

import com.example.data.model.InventoryItem
import com.example.data.model.StyleProfile

data class SizeFilteredInventory(
    val matchingItems: List<InventoryItem>,
    val excludedItems: List<InventoryItem>
)

object SizeMatchingHelper {

    private val LETTER_SIZE_RANK = mapOf(
        "XXS" to 0,
        "XS" to 1,
        "S" to 2,
        "SM" to 2,
        "M" to 3,
        "MD" to 3,
        "L" to 4,
        "LG" to 4,
        "XL" to 5,
        "XXL" to 6,
        "2XL" to 6,
        "XXXL" to 7,
        "3XL" to 7,
        "1X" to 5,
        "2X" to 6,
        "3X" to 7
    )

    private val RANGE_REGEX = Regex(
        """([A-Za-z0-9]+)\s*(?:-|to|–|—)\s*([A-Za-z0-9]+)"""
    )
    private val NUMBER_REGEX = Regex("""\d+""")

    fun partitionInventoryBySize(
        items: List<InventoryItem>,
        profile: StyleProfile
    ): SizeFilteredInventory {
        val matching = mutableListOf<InventoryItem>()
        val excluded = mutableListOf<InventoryItem>()
        for (item in items) {
            if (item.matchesSavedSizes(profile)) {
                matching.add(item)
            } else {
                excluded.add(item)
            }
        }
        return SizeFilteredInventory(
            matchingItems = matching,
            excludedItems = excluded
        )
    }

    fun partitionInventoryBySizeAndCoverage(
        items: List<InventoryItem>,
        profile: StyleProfile
    ): SizeFilteredInventory {
        val matching = mutableListOf<InventoryItem>()
        val excluded = mutableListOf<InventoryItem>()
        for (item in items) {
            if (item.matchesSavedSizes(profile) && item.matchesCoverageRequirements(profile)) {
                matching.add(item)
            } else {
                excluded.add(item)
            }
        }
        return SizeFilteredInventory(
            matchingItems = matching,
            excludedItems = excluded
        )
    }

    fun InventoryItem.matchesCoverageRequirements(profile: StyleProfile): Boolean {
        val cat = category.trim()
        if (cat.equals("Headwear & Accessory", ignoreCase = true)) {
            return profile.hijabFriendly
        }

        val summary = coverageSummary.trim().lowercase()
        if (summary.isBlank()) return true

        // Neckline check
        val neckPref = profile.necklineRule.lowercase()
        if (neckPref.contains("high") || neckPref.contains("mock") || neckPref.contains("crew")) {
            val hasLowNeck = summary.contains("low neck") ||
                summary.contains("v-neck") ||
                summary.contains("plunge") ||
                summary.contains("strapless")
            if (hasLowNeck) return false
        }

        // Sleeve check
        val sleevePref = profile.sleeveRule.lowercase()
        if (sleevePref.contains("full") || sleevePref.contains("wrist") || sleevePref.contains("long")) {
            val hasShortSleeve = summary.contains("sleeveless") ||
                summary.contains("short sleeve") ||
                summary.contains("cap sleeve")
            if (hasShortSleeve) return false
        }

        // Hemline check for bottoms, dresses, co-ords, and outerwear
        if (cat in setOf("Bottom", "Dress & Abaya", "Set & Co-ord", "Outerwear")) {
            val hemPref = profile.hemlineRule.lowercase()
            val isFloorOnlyPref = hemPref.contains("floor") && !hemPref.contains("ankle")
            if (isFloorOnlyPref) {
                val isMidiOrKnee = summary.contains("midi") || summary.contains("knee")
                if (isMidiOrKnee) return false
                // Floor-length strict filter prefers floor/maxi/full-length silhouettes
                val meetsFloorOrMaxi = summary.contains("floor") ||
                    summary.contains("maxi") ||
                    summary.contains("full-length")
                if (!meetsFloorOrMaxi && (summary.contains("calf") || summary.contains("mid-thigh"))) {
                    return false
                }
            } else if (hemPref.contains("ankle") || hemPref.contains("maxi")) {
                val isShortOrMidi = summary.contains("above knee") ||
                    summary.contains("mini ") ||
                    (summary.contains("midi") && !summary.contains("maxi") && !summary.contains("ankle"))
                if (isShortOrMidi) return false
            }
        }

        return true
    }

    fun InventoryItem.matchesSavedSizes(profile: StyleProfile): Boolean {
        val rawRange = sizeRange.trim()
        if (rawRange.isBlank()) return true

        val normalizedRange = rawRange.lowercase()
        if (
            normalizedRange.contains("one size") ||
            normalizedRange.contains("free size") ||
            normalizedRange.contains("all sizes") ||
            normalizedRange == "os"
        ) {
            return true
        }

        val itemAudience = audience.trim()
        val profileAudience = profile.audience.trim()
        val girlsPref = profile.girlsSize.trim()
        val hasGirlsOrTeensPref = girlsPref.isNotBlank() &&
            !girlsPref.equals("Not Needed", ignoreCase = true)

        if (itemAudience.equals("Girls", ignoreCase = true)) {
            if (!hasGirlsOrTeensPref) {
                return false
            }
            if (girlsPref.startsWith("Teens", ignoreCase = true) &&
                normalizedRange.contains("girls")
            ) {
                return false
            }
            return matchesYouthSize(rawRange, girlsPref)
        }

        if (itemAudience.equals("Teens", ignoreCase = true)) {
            if (girlsPref.startsWith("Teens", ignoreCase = true)) {
                return matchesTeensSize(rawRange, girlsPref)
            }
            if (profileAudience.equals("Teens", ignoreCase = true)) {
                return matchesLadiesSize(rawRange, profile.topSize) ||
                    matchesLadiesSize(rawRange, profile.bottomSize)
            }
            if (!hasGirlsOrTeensPref && profileAudience.equals("Ladies", ignoreCase = true)) {
                return false
            }
            return matchesYouthSize(rawRange, girlsPref) ||
                matchesLadiesSize(rawRange, profile.topSize)
        }

        // Ladies items
        if (profileAudience.equals("Girls", ignoreCase = true)) {
            return false
        }

        // Check abaya/dress length ranges like "52 - 58 Length" or "54"
        if (isAbayaLengthRange(rawRange)) {
            return matchesAbayaLength(rawRange, profile.dressAbayaLength)
        }

        return when (category.trim()) {
            "Bottom" -> matchesLadiesSize(rawRange, profile.bottomSize)
            "Top", "Outerwear" -> matchesLadiesSize(rawRange, profile.topSize)
            "Set & Co-ord", "Dress & Abaya" -> {
                matchesLadiesSize(rawRange, profile.topSize) ||
                    matchesLadiesSize(rawRange, profile.bottomSize)
            }
            else -> true
        }
    }

    private fun isAbayaLengthRange(rawRange: String): Boolean {
        val lower = rawRange.lowercase()
        if (lower.contains("length") || lower.contains("\"")) return true
        val nums = NUMBER_REGEX.findAll(rawRange).mapNotNull { it.value.toIntOrNull() }.toList()
        return nums.isNotEmpty() && nums.all { it in 46..66 }
    }

    private fun matchesAbayaLength(rawRange: String, savedLengthRaw: String): Boolean {
        val targetLength = NUMBER_REGEX.find(savedLengthRaw)?.value?.toIntOrNull() ?: return true
        val rangeMatch = RANGE_REGEX.find(rawRange)
        if (rangeMatch != null) {
            val start = rangeMatch.groupValues[1].toIntOrNull()
            val end = rangeMatch.groupValues[2].toIntOrNull()
            if (start != null && end != null) {
                val low = minOf(start, end)
                val high = maxOf(start, end)
                return targetLength in low..high
            }
        }
        val nums = NUMBER_REGEX.findAll(rawRange).mapNotNull { it.value.toIntOrNull() }.toList()
        return nums.isEmpty() || targetLength in nums
    }

    private fun matchesYouthSize(rawRange: String, savedYouthSize: String): Boolean {
        val savedNums = NUMBER_REGEX.findAll(savedYouthSize).mapNotNull { it.value.toIntOrNull() }.toList()
        val itemRangeMatch = RANGE_REGEX.find(rawRange)
        if (savedNums.isNotEmpty() && itemRangeMatch != null) {
            val start = itemRangeMatch.groupValues[1].toIntOrNull()
            val end = itemRangeMatch.groupValues[2].toIntOrNull()
            if (start != null && end != null) {
                val itemMin = minOf(start, end)
                val itemMax = maxOf(start, end)
                val savedMin = savedNums.min()
                val savedMax = savedNums.max()
                return savedMax >= itemMin && savedMin <= itemMax
            }
        }
        val itemNums = NUMBER_REGEX.findAll(rawRange).mapNotNull { it.value.toIntOrNull() }.toList()
        if (savedNums.isNotEmpty() && itemNums.isNotEmpty()) {
            return savedNums.any { it in itemNums }
        }
        return false
    }

    private fun matchesTeensSize(rawRange: String, savedTeensSize: String): Boolean {
        val cleanSavedLetter = savedTeensSize.uppercase()
            .removePrefix("TEENS")
            .trim()
        return matchesLadiesSize(rawRange, cleanSavedLetter)
    }

    internal fun matchesLadiesSize(rawRange: String, savedSizeRaw: String): Boolean {
        val targetRanks = resolveTargetRanks(savedSizeRaw)
        if (targetRanks.isEmpty()) return true

        val cleanedRange = rawRange.uppercase()
            .replace("TEENS", "")
            .replace("LADIES", "")
            .trim()

        val rangeMatch = RANGE_REGEX.find(cleanedRange)
        if (rangeMatch != null) {
            val startToken = rangeMatch.groupValues[1].trim()
            val endToken = rangeMatch.groupValues[2].trim()
            val startRank = LETTER_SIZE_RANK[startToken]
            val endRank = LETTER_SIZE_RANK[endToken]
            if (startRank != null && endRank != null) {
                val low = minOf(startRank, endRank)
                val high = maxOf(startRank, endRank)
                return targetRanks.any { it in low..high }
            }
        }

        // Check discrete size tokens (e.g., "S, M, L" or "M" or "Plus 1X-3X")
        val tokens = Regex("""[A-Z0-9]+""")
            .findAll(cleanedRange)
            .map { it.value }
            .toList()
        val itemRanks = tokens.mapNotNull { LETTER_SIZE_RANK[it] }.toSet()
        if (itemRanks.isNotEmpty()) {
            return targetRanks.any { it in itemRanks }
        }

        return false
    }

    private fun resolveTargetRanks(savedSizeRaw: String): Set<Int> {
        val upper = savedSizeRaw.uppercase().trim()
        if (upper.contains("PLUS") || upper.contains("1X") || upper.contains("2X") || upper.contains("3X")) {
            return setOf(6, 7)
        }
        val single = LETTER_SIZE_RANK[upper]
        if (single != null) return setOf(single)

        val rangeMatch = RANGE_REGEX.find(upper)
        if (rangeMatch != null) {
            val s = LETTER_SIZE_RANK[rangeMatch.groupValues[1]]
            val e = LETTER_SIZE_RANK[rangeMatch.groupValues[2]]
            if (s != null && e != null) {
                return (minOf(s, e)..maxOf(s, e)).toSet()
            }
        }
        return emptySet()
    }
}
