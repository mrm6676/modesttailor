package com.example.util

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.example.R
import com.example.data.model.AiCuratedOutfit
import com.example.data.model.SavedOutfit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object OutfitShareHelper {

    private const val CARD_WIDTH = 1080
    private const val CARD_HEIGHT = 1440
    private const val DEEP_LINK_SCHEME = "modesttailor"
    private const val DEEP_LINK_HOST = "outfit"

    fun buildOutfitDeepLink(outfit: AiCuratedOutfit): Uri {
        return Uri.Builder()
            .scheme(DEEP_LINK_SCHEME)
            .authority(DEEP_LINK_HOST)
            .appendQueryParameter("title", outfit.title.take(120))
            .appendQueryParameter("audience", outfit.audience.take(40))
            .appendQueryParameter("occasion", outfit.occasion.take(80))
            .appendQueryParameter("trend", outfit.trendHighlight.take(200))
            .appendQueryParameter("coverage", outfit.coverageVerification.take(300))
            .appendQueryParameter("notes", outfit.stylingNotes.take(400))
            .appendQueryParameter("pieces", outfit.matchedPieces.take(8).joinToString("||"))
            .appendQueryParameter("sources", outfit.boutiqueSources.take(6).joinToString("||"))
            .build()
    }

    fun parseOutfitDeepLink(uri: Uri?): AiCuratedOutfit? {
        if (uri == null) return null
        if (uri.scheme != DEEP_LINK_SCHEME || uri.host != DEEP_LINK_HOST) return null
        val title = uri.getQueryParameter("title")?.takeIf { it.isNotBlank() } ?: return null
        val piecesRaw = uri.getQueryParameter("pieces").orEmpty()
        val sourcesRaw = uri.getQueryParameter("sources").orEmpty()
        return AiCuratedOutfit(
            title = title,
            audience = uri.getQueryParameter("audience").orEmpty().ifBlank { "Ladies" },
            occasion = uri.getQueryParameter("occasion").orEmpty().ifBlank { "Everyday Chic" },
            trendHighlight = uri.getQueryParameter("trend").orEmpty(),
            coverageVerification = uri.getQueryParameter("coverage").orEmpty()
                .ifBlank { "Verified modest coverage ensemble" },
            stylingNotes = uri.getQueryParameter("notes").orEmpty(),
            matchedPieces = piecesRaw.split("||").map { it.trim() }.filter { it.isNotEmpty() },
            boutiqueSources = sourcesRaw.split("||").map { it.trim() }.filter { it.isNotEmpty() }
        )
    }

    fun SavedOutfit.toShareableOutfit(): AiCuratedOutfit {
        return AiCuratedOutfit(
            title = title,
            audience = audience,
            occasion = occasion,
            trendHighlight = trendHighlight,
            coverageVerification = coverageVerification,
            stylingNotes = stylingNotes,
            matchedPieces = matchedPieces,
            boutiqueSources = boutiqueSources
        )
    }

    suspend fun createShareIntent(
        context: Context,
        outfit: AiCuratedOutfit
    ): Intent = withContext(Dispatchers.IO) {
        val deepLink = buildOutfitDeepLink(outfit)
        val imageUri = renderAndCacheOutfitCard(context, outfit)

        val shareCaption = buildString {
            appendLine("${outfit.title} — Modest Tailor Edit")
            appendLine("${outfit.audience} • ${outfit.occasion}")
            appendLine()
            appendLine("Modest Coverage Verified: ${outfit.coverageVerification}")
            if (outfit.matchedPieces.isNotEmpty()) {
                appendLine()
                appendLine("Pieces: ${outfit.matchedPieces.joinToString(", ")}")
            }
            if (outfit.boutiqueSources.isNotEmpty()) {
                appendLine("Local Boutiques: ${outfit.boutiqueSources.joinToString(" • ")}")
            }
            if (outfit.stylingNotes.isNotBlank()) {
                appendLine()
                appendLine("Styling Note: ${outfit.stylingNotes}")
            }
            appendLine()
            append("Open in Modest Tailor: $deepLink")
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_SUBJECT, "${outfit.title} — Modest Tailor")
            putExtra(Intent.EXTRA_TEXT, shareCaption)
            putExtra(Intent.EXTRA_STREAM, imageUri)
            clipData = ClipData.newRawUri(outfit.title, imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        Intent.createChooser(
            sendIntent,
            context.getString(R.string.atelier_share_chooser_title)
        )
    }

    private fun renderAndCacheOutfitCard(
        context: Context,
        outfit: AiCuratedOutfit
    ): Uri {
        val bitmap = Bitmap.createBitmap(CARD_WIDTH, CARD_HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val serifTypeface = ResourcesCompat.getFont(context, R.font.playfair_display)
            ?: Typeface.SERIF
        val sansTypeface = ResourcesCompat.getFont(context, R.font.plus_jakarta_sans)
            ?: Typeface.SANS_SERIF

        // 1. Silk Ivory Canvas Background
        canvas.drawColor(Color.parseColor("#FAF7F2"))

        // 2. Top Rosewood Gradient Editorial Header
        val headerRect = RectF(0f, 0f, CARD_WIDTH.toFloat(), 360f)
        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, CARD_WIDTH.toFloat(), 360f,
                Color.parseColor("#6E3B49"),
                Color.parseColor("#9A5A6C"),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(headerRect, headerPaint)

        // Subtle Gold Accent Rule
        val brassPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#D8BC82")
            strokeWidth = 4f
        }
        canvas.drawLine(72f, 358f, CARD_WIDTH - 72f, 358f, brassPaint)

        // Brand Eyebrow
        val eyebrowPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FFDDB8")
            textSize = 30f
            typeface = Typeface.create(sansTypeface, Typeface.BOLD)
            letterSpacing = 0.08f
        }
        canvas.drawText("MODEST TAILOR • EDITORIAL LOOK", 72f, 98f, eyebrowPaint)

        // Audience & Occasion Pill
        val pillText = "${outfit.audience.uppercase()}  •  ${outfit.occasion.uppercase()}"
        val pillTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#280B14")
            textSize = 24f
            typeface = Typeface.create(sansTypeface, Typeface.BOLD)
        }
        val pillTextWidth = pillTextPaint.measureText(pillText)
        val pillBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FADAE2")
        }
        val pillRect = RectF(72f, 132f, (72f + pillTextWidth + 56f).coerceAtMost(CARD_WIDTH - 72f), 190f)
        canvas.drawRoundRect(pillRect, 29f, 29f, pillBgPaint)
        canvas.drawText(pillText, 100f, 169f, pillTextPaint)

        // Outfit Title
        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 54f
            typeface = Typeface.create(serifTypeface, Typeface.BOLD)
        }
        var currentY = drawWrappedText(
            canvas = canvas,
            text = outfit.title,
            paint = titlePaint,
            x = 72f,
            y = 222f,
            maxWidth = CARD_WIDTH - 144,
            maxLines = 2
        )

        // Move below header
        currentY = maxOf(currentY + 56f, 412f)

        // 3. Modest Coverage Verification Card
        val coverageCardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#E8F2EB")
        }
        val coverageTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#1F3528")
            textSize = 29f
            typeface = Typeface.create(sansTypeface, Typeface.NORMAL)
        }
        val coverageLayout = StaticLayout.Builder
            .obtain(outfit.coverageVerification, 0, outfit.coverageVerification.length, coverageTextPaint, CARD_WIDTH - 208)
            .setMaxLines(4)
            .build()
        val coverageCardHeight = 84f + coverageLayout.height
        canvas.drawRoundRect(
            RectF(72f, currentY, CARD_WIDTH - 72f, currentY + coverageCardHeight),
            28f,
            28f,
            coverageCardPaint
        )
        val sectionLabelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#4C6455")
            textSize = 26f
            typeface = Typeface.create(sansTypeface, Typeface.BOLD)
        }
        canvas.drawText("MODEST COVERAGE VERIFIED", 104f, currentY + 46f, sectionLabelPaint)
        canvas.save()
        canvas.translate(104f, currentY + 62f)
        coverageLayout.draw(canvas)
        canvas.restore()

        currentY += coverageCardHeight + 40f

        // 4. Matched Pieces & Boutiques
        if (outfit.matchedPieces.isNotEmpty()) {
            val headingPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#6E3B49")
                textSize = 28f
                typeface = Typeface.create(sansTypeface, Typeface.BOLD)
            }
            canvas.drawText("CURATED PIECES", 72f, currentY, headingPaint)
            currentY += 24f

            val itemPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#1F1A1B")
                textSize = 30f
                typeface = Typeface.create(sansTypeface, Typeface.NORMAL)
            }
            outfit.matchedPieces.take(5).forEach { piece ->
                currentY = drawWrappedText(
                    canvas = canvas,
                    text = "•  $piece",
                    paint = itemPaint,
                    x = 80f,
                    y = currentY + 12f,
                    maxWidth = CARD_WIDTH - 160,
                    maxLines = 2
                )
            }
            currentY += 28f
        }

        if (outfit.boutiqueSources.isNotEmpty()) {
            val boutiquePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#845926")
                textSize = 28f
                typeface = Typeface.create(sansTypeface, Typeface.BOLD)
            }
            currentY = drawWrappedText(
                canvas = canvas,
                text = "Local Boutiques: ${outfit.boutiqueSources.joinToString(" • ")}",
                paint = boutiquePaint,
                x = 72f,
                y = currentY + 8f,
                maxWidth = CARD_WIDTH - 144,
                maxLines = 2
            )
            currentY += 24f
        }

        // 5. Styling Notes
        if (outfit.stylingNotes.isNotBlank() && currentY < CARD_HEIGHT - 220f) {
            val notePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#514346")
                textSize = 28f
                typeface = Typeface.create(sansTypeface, Typeface.ITALIC)
            }
            drawWrappedText(
                canvas = canvas,
                text = "\"${outfit.stylingNotes}\"",
                paint = notePaint,
                x = 72f,
                y = currentY + 10f,
                maxWidth = CARD_WIDTH - 144,
                maxLines = 4
            )
        }

        // 6. Footer Banner
        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#F1EAE4")
        }
        canvas.drawRect(RectF(0f, CARD_HEIGHT - 110f, CARD_WIDTH.toFloat(), CARD_HEIGHT.toFloat()), footerPaint)
        val footerTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#6E3B49")
            textSize = 26f
            typeface = Typeface.create(sansTypeface, Typeface.BOLD)
        }
        canvas.drawText("Styled with Modest Tailor • Ladies & Girls Atelier", 72f, CARD_HEIGHT - 46f, footerTextPaint)

        // Write PNG to cacheDir/shared_outfits
        val shareDir = File(context.cacheDir, "shared_outfits").apply { mkdirs() }
        // Clean stale images older than 1 hour to keep cache tidy
        shareDir.listFiles()?.forEach { oldFile ->
            if (System.currentTimeMillis() - oldFile.lastModified() > 3_600_000L) {
                oldFile.delete()
            }
        }
        val outFile = File(shareDir, "modest_outfit_${System.currentTimeMillis()}.png")
        FileOutputStream(outFile).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, stream)
        }
        bitmap.recycle()

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            outFile
        )
    }

    private fun drawWrappedText(
        canvas: Canvas,
        text: String,
        paint: TextPaint,
        x: Float,
        y: Float,
        maxWidth: Int,
        maxLines: Int
    ): Float {
        val layout = StaticLayout.Builder
            .obtain(text, 0, text.length, paint, maxWidth)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setMaxLines(maxLines)
            .build()
        canvas.save()
        canvas.translate(x, y)
        layout.draw(canvas)
        canvas.restore()
        return y + layout.height
    }
}
