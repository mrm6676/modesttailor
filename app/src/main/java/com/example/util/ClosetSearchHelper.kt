package com.example.util

import com.example.data.model.SavedOutfit

object ClosetSearchHelper {

    val QUICK_STYLE_TAGS: List<String> = listOf(
        "Summer",
        "Formal",
        "Work",
        "Casual",
        "Festive",
        "Spring",
        "Autumn",
        "Winter",
        "Hijab-Friendly"
    )

    private val HASHTAG_REGEX = Regex("#([A-Za-z0-9_-]{2,32})")

    private val TAG_KEYWORD_RULES: List<Pair<String, List<String>>> = listOf(
        "Summer" to listOf("summer", "linen", "breezy", "sunlit", "warm", "cotton", "chiffon", "resort"),
        "Formal" to listOf("formal", "evening", "gala", "festive", "celebration", "abaya", "kaftan", "satin", "silk", "eid", "wedding", "occasion"),
        "Work" to listOf("work", "office", "studio", "tailoring", "tailored", "blazer", "trench", "structured", "professional", "meeting"),
        "Casual" to listOf("casual", "everyday", "weekend", "denim", "campus", "school", "brunch", "relaxed", "cottagecore"),
        "Festive" to listOf("festive", "celebration", "eid", "party", "champagne", "embellished", "gold"),
        "Spring" to listOf("spring", "floral", "pastel", "garden", "blossom"),
        "Autumn" to listOf("autumn", "fall", "crisp", "layering", "camel", "espresso"),
        "Winter" to listOf("winter", "wool", "knit", "cozy", "velvet", "sweater"),
        "Hijab-Friendly" to listOf("hijab", "headscarf", "shawl", "scarf", "wrap")
    )

    fun SavedOutfit.extractCustomHashtags(): List<String> {
        val raw = "$title $stylingNotes"
        return HASHTAG_REGEX.findAll(raw)
            .map { it.groupValues[1].trim() }
            .filter { it.isNotEmpty() }
            .distinctBy { it.lowercase() }
            .toList()
    }

    fun SavedOutfit.deriveStyleTags(): List<String> {
        val tags = mutableListOf<String>()
        val seenLower = mutableSetOf<String>()

        fun addUnique(tag: String) {
            val clean = tag.trim()
            if (clean.isNotEmpty() && seenLower.add(clean.lowercase())) {
                tags.add(clean)
            }
        }

        // 1. Explicit #hashtags from title & personal styling notes
        extractCustomHashtags().forEach { addUnique(it) }

        // 2. Semantic keyword tags across occasion, trend, notes, coverage, and matched pieces
        val searchableText = buildString {
            append(title).append(' ')
            append(occasion).append(' ')
            append(trendHighlight).append(' ')
            append(coverageVerification).append(' ')
            append(stylingNotes).append(' ')
            append(matchedPieces.joinToString(" ")).append(' ')
            append(audience)
        }.lowercase()

        for ((displayTag, keywords) in TAG_KEYWORD_RULES) {
            if (keywords.any { keyword -> containsWordOrPhrase(searchableText, keyword) }) {
                addUnique(displayTag)
            }
        }

        return tags
    }

    fun collectAvailableTags(outfits: List<SavedOutfit>): List<String> {
        val result = mutableListOf<String>()
        val seenLower = mutableSetOf<String>()

        for (tag in QUICK_STYLE_TAGS) {
            if (seenLower.add(tag.lowercase())) {
                result.add(tag)
            }
        }
        for (outfit in outfits) {
            for (tag in outfit.deriveStyleTags()) {
                if (seenLower.add(tag.lowercase())) {
                    result.add(tag)
                }
            }
        }
        return result
    }

    fun parseQueryTokens(searchQuery: String): List<String> {
        return searchQuery
            .split(Regex("[\\s,]+"))
            .map { it.trim().removePrefix("#").lowercase() }
            .filter { it.isNotEmpty() }
            .distinct()
    }

    fun toggleTagInQuery(currentQuery: String, tag: String): String {
        val normalizedTag = tag.trim().removePrefix("#").lowercase()
        val existingTokens = parseQueryTokens(currentQuery)
        val updated = if (normalizedTag in existingTokens) {
            existingTokens.filterNot { it == normalizedTag }
        } else {
            existingTokens + normalizedTag
        }
        return updated.joinToString(" ")
    }

    fun SavedOutfit.matchesClosetFilter(
        selectedAudienceFilter: String,
        searchQuery: String
    ): Boolean {
        val matchesChip = when (selectedAudienceFilter) {
            "Favorites" -> favorite
            "Ladies" -> audience.contains("Ladies", ignoreCase = true)
            "Girls" -> audience.contains("Girls", ignoreCase = true)
            "Teens" -> audience.contains("Teens", ignoreCase = true)
            else -> true
        }
        if (!matchesChip) return false

        val tokens = parseQueryTokens(searchQuery)
        if (tokens.isEmpty()) return true

        val derivedLowerTags = deriveStyleTags().map { it.lowercase() }
        val fullCorpus = buildString {
            append(title).append(' ')
            append(audience).append(' ')
            append(occasion).append(' ')
            append(trendHighlight).append(' ')
            append(coverageVerification).append(' ')
            append(stylingNotes).append(' ')
            append(matchedPieces.joinToString(" ")).append(' ')
            append(boutiqueSources.joinToString(" ")).append(' ')
            append(derivedLowerTags.joinToString(" "))
        }.lowercase()

        return tokens.all { token ->
            derivedLowerTags.any { tag -> tag.contains(token) } || fullCorpus.contains(token)
        }
    }

    private fun containsWordOrPhrase(text: String, keyword: String): Boolean {
        val pattern = Regex("\\b${Regex.escape(keyword)}\\b", RegexOption.IGNORE_CASE)
        return pattern.containsMatchIn(text)
    }
}
