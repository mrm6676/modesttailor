package com.example.util

import androidx.annotation.DrawableRes
import com.example.R
import com.example.data.model.AiCuratedOutfit
import com.example.data.model.SavedOutfit

object OutfitDesignArtworkHelper {

    @DrawableRes
    fun resolveOutfitImage(outfit: AiCuratedOutfit): Int {
        return resolveByAttributes(
            title = outfit.title,
            audience = outfit.audience,
            occasion = outfit.occasion,
            trendHighlight = outfit.trendHighlight,
            pieces = outfit.matchedPieces
        )
    }

    @DrawableRes
    fun resolveOutfitImage(outfit: SavedOutfit): Int {
        return resolveByAttributes(
            title = outfit.title,
            audience = outfit.audience,
            occasion = outfit.occasion,
            trendHighlight = outfit.trendHighlight,
            pieces = outfit.matchedPieces
        )
    }

    @DrawableRes
    fun resolveByAttributes(
        title: String,
        audience: String,
        occasion: String = "",
        trendHighlight: String = "",
        pieces: List<String> = emptyList()
    ): Int {
        val corpus = buildString {
            append(title).append(' ')
            append(audience).append(' ')
            append(occasion).append(' ')
            append(trendHighlight).append(' ')
            append(pieces.joinToString(" "))
        }.lowercase()

        return when {
            corpus.containsAny("abaya", "kaftan", "espresso", "evening", "gala", "eid", "festive", "celebration") ->
                R.drawable.img_design_espresso_abaya_1795595866796

            corpus.containsAny("trench", "sage", "olive", "linen", "utility", "duster", "khaki") ->
                R.drawable.img_design_sage_trench_1795595866152

            corpus.containsAny("girl", "peter-pan", "cottagecore", "pointelle", "cardigan", "garden", "tiered") ->
                R.drawable.img_design_girls_garden_1795595867765

            corpus.containsAny("teen", "denim", "campus", "wide-leg", "tunic", "school") ->
                R.drawable.img_design_teens_denim_1795595867918

            corpus.containsAny("rosewood", "satin", "pleated", "bishop", "silk", "mock-neck", "blouse") ->
                R.drawable.img_design_rosewood_satin_1795595866206

            audience.contains("girls", ignoreCase = true) ->
                R.drawable.img_design_girls_garden_1795595867765

            audience.contains("teens", ignoreCase = true) ->
                R.drawable.img_design_teens_denim_1795595867918

            else ->
                R.drawable.img_design_rosewood_satin_1795595866206
        }
    }

    private fun String.containsAny(vararg keywords: String): Boolean {
        return keywords.any { this.contains(it) }
    }
}
