package com.example.data.ai

import android.content.Context
import com.example.data.model.AiCuratedOutfit
import com.example.data.model.AiDailyEditRecommendation
import org.json.JSONArray
import org.json.JSONObject

class DailyOutfitCache(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun loadIfValid(
        userId: String,
        dateKey: String,
        profileFingerprint: String
    ): AiDailyEditRecommendation? {
        val raw = prefs.getString(cacheKey(userId), null) ?: return null
        return try {
            val root = JSONObject(raw)
            if (root.optString("dateKey") != dateKey) return null
            if (root.optString("profileFingerprint") != profileFingerprint) return null

            val outfitsArray = root.optJSONArray("outfits") ?: return null
            val outfits = buildList {
                for (i in 0 until outfitsArray.length()) {
                    val obj = outfitsArray.getJSONObject(i)
                    add(
                        AiCuratedOutfit(
                            title = obj.optString("title", "Daily Modest Edit"),
                            audience = obj.optString("audience", "Ladies"),
                            occasion = obj.optString("occasion", "Daily Wear"),
                            trendHighlight = obj.optString("trendHighlight", ""),
                            coverageVerification = obj.optString("coverageVerification", ""),
                            stylingNotes = obj.optString("stylingNotes", ""),
                            matchedPieces = obj.optJSONArray("matchedPieces").toStringList(),
                            boutiqueSources = obj.optJSONArray("boutiqueSources").toStringList()
                        )
                    )
                }
            }
            if (outfits.isEmpty()) return null

            AiDailyEditRecommendation(
                dateKey = dateKey,
                dateDisplay = root.optString("dateDisplay", dateKey),
                dayTheme = root.optString("dayTheme", "Today's Modest Curation"),
                editorialSummary = root.optString("editorialSummary", ""),
                outfits = outfits
            )
        } catch (_: Exception) {
            null
        }
    }

    fun save(
        userId: String,
        profileFingerprint: String,
        recommendation: AiDailyEditRecommendation
    ) {
        val outfitsJson = JSONArray().apply {
            recommendation.outfits.forEach { outfit ->
                put(
                    JSONObject().apply {
                        put("title", outfit.title)
                        put("audience", outfit.audience)
                        put("occasion", outfit.occasion)
                        put("trendHighlight", outfit.trendHighlight)
                        put("coverageVerification", outfit.coverageVerification)
                        put("stylingNotes", outfit.stylingNotes)
                        put("matchedPieces", JSONArray(outfit.matchedPieces))
                        put("boutiqueSources", JSONArray(outfit.boutiqueSources))
                    }
                )
            }
        }
        val payload = JSONObject().apply {
            put("dateKey", recommendation.dateKey)
            put("dateDisplay", recommendation.dateDisplay)
            put("profileFingerprint", profileFingerprint)
            put("dayTheme", recommendation.dayTheme)
            put("editorialSummary", recommendation.editorialSummary)
            put("outfits", outfitsJson)
        }
        prefs.edit().putString(cacheKey(userId), payload.toString()).apply()
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        return buildList {
            for (i in 0 until length()) {
                add(optString(i))
            }
        }
    }

    private fun cacheKey(userId: String) = "daily_edit_$userId"

    private companion object {
        const val PREFS_NAME = "modest_tailor_daily_edit_cache"
    }
}
