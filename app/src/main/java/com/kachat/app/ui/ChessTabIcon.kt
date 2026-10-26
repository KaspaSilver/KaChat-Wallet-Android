package com.kachat.app.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asAndroidColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource

/**
 * The Chess section's icon: a rook and a knight, side by side (iOS 6fd5248). The Material set has
 * no chess pieces, so the two glyphs are drawn once into a bitmap - black on transparent, so an
 * Icon's tint colours it like any other symbol - and scaled to whatever size each place asks for.
 */
private val chessIconBitmap by lazy {
    val side = 96
    val width = (side * 1.55f).toInt()
    val bitmap = Bitmap.createBitmap(width, side, Bitmap.Config.ARGB_8888)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.BLACK
        textSize = side * 1.08f
        typeface = Typeface.DEFAULT
        textAlign = Paint.Align.CENTER
    }
    // U+FE0E asks for the text glyphs, never an emoji rendering.
    val text = "♜︎♞︎"
    val metrics = paint.fontMetrics
    val baseline = side / 2f - (metrics.ascent + metrics.descent) / 2f
    Canvas(bitmap).drawText(text, width / 2f, baseline, paint)
    bitmap.asImageBitmap()
}

/**
 * The ".kachat" wordmark as a tintable painter (iOS `KachatTabIcon`) - the Kaspa Hub tile, the dock
 * item, Customize Dock, the .kachat hero and the setup guide all draw it. The word is drawn at
 * whatever size it is given (crisp from the 24 dp dock item to the 56 dp hero, where a bitmap
 * would have to be scaled), [WORDMARK_SIDE] units tall with its natural width, black, so an
 * Icon's tint colours it like any other symbol.
 *
 * iOS draws it in SF Pro Rounded Heavy; Apple's fonts are licensed for Apple platforms only, so it
 * cannot ship here, and the heaviest system face (sans-serif-black) stands in.
 */
private const val WORDMARK_SIDE = 96f

private fun wordmarkPaint(side: Float) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = android.graphics.Color.BLACK
    textSize = side * 0.62f
    // iOS draws it heavy (UIFont .heavy, rounded design); black is the heaviest system weight.
    typeface = Typeface.create("sans-serif-black", Typeface.NORMAL)
    textAlign = Paint.Align.CENTER
}

private const val WORDMARK_TEXT = ".kachat"

/** The wordmark's width at [WORDMARK_SIDE] tall: the word's, a little air, never under square. */
private val wordmarkWidth by lazy {
    maxOf(wordmarkPaint(WORDMARK_SIDE).measureText(WORDMARK_TEXT) + 8f, WORDMARK_SIDE)
}

private class KachatWordmarkPainter : Painter() {
    private var filter: androidx.compose.ui.graphics.ColorFilter? = null
    private var alpha = 1f

    override val intrinsicSize: Size get() = Size(wordmarkWidth, WORDMARK_SIDE)

    override fun applyColorFilter(colorFilter: androidx.compose.ui.graphics.ColorFilter?): Boolean {
        filter = colorFilter
        return true
    }

    override fun applyAlpha(alpha: Float): Boolean {
        this.alpha = alpha
        return true
    }

    override fun DrawScope.onDraw() {
        // drawn into the box the painter was given, keeping the word's proportions
        val scale = minOf(size.width / wordmarkWidth, size.height / WORDMARK_SIDE)
        val side = WORDMARK_SIDE * scale
        val paint = wordmarkPaint(side).apply {
            colorFilter = filter?.asAndroidColorFilter()
            alpha = (this@KachatWordmarkPainter.alpha * 255).toInt().coerceIn(0, 255)
        }
        val metrics = paint.fontMetrics
        val baseline = size.height / 2f - (metrics.ascent + metrics.descent) / 2f
        drawIntoCanvas { it.nativeCanvas.drawText(WORDMARK_TEXT, size.width / 2f, baseline, paint) }
    }
}

/**
 * The .kachat wordmark [height] tall in [tint] (iOS `KachatTabIcon.view(side:)`): the .kachat
 * hero and the setup guide's first step draw it at 56.
 */
@Composable
fun KachatWordmark(height: androidx.compose.ui.unit.Dp, modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier, tint: androidx.compose.ui.graphics.Color = com.kachat.app.ui.theme.KaspaTeal) {
    androidx.compose.material3.Icon(
        painter = remember { KachatWordmarkPainter() },
        contentDescription = ".kachat",
        tint = tint,
        modifier = modifier.then(Screen.KachatNames.tabIconModifier(height)),
    )
}

/**
 * The box a tab's icon is drawn in, [size] tall. The chess pieces and the .kachat wordmark are
 * wider than they are tall, and iOS draws them at the full height with their natural width
 * (ChessTabIcon / KachatTabIcon `image(side:)`); a [size] square shrank them to fit its width,
 * which left the wordmark a sliver.
 */
fun Screen.tabIconModifier(size: androidx.compose.ui.unit.Dp): androidx.compose.ui.Modifier = when (this) {
    Screen.Chess -> androidx.compose.ui.Modifier.size(width = size * (chessIconBitmap.width.toFloat() / chessIconBitmap.height), height = size)
    Screen.KachatNames -> androidx.compose.ui.Modifier.size(width = size * (wordmarkWidth / WORDMARK_SIDE), height = size)
    else -> androidx.compose.ui.Modifier.size(size)
}

/** The painter a tab's icon is drawn with: the Kaspa mark, the chess pieces, the .kachat
 *  wordmark, or its vector. */
@Composable
fun Screen.tabIconPainter(): Painter = when {
    usesKaspaLogo -> painterResource(com.kachat.app.R.drawable.ic_kaspa_logo)
    this == Screen.Chess -> remember { BitmapPainter(chessIconBitmap) }
    this == Screen.KachatNames -> remember { KachatWordmarkPainter() }
    else -> rememberVectorPainter(icon)
}

/**
 * KaChat Wallet's dock icon for .kachat (iOS wallet `KachatTabIcon.domainsImage`): a name tag -
 * a rounded body on the right, a point on the left - with the hole it hangs by and an "@" cut out
 * of it. Black on transparent like the chess pieces, so an Icon's tint colours it, laid out on
 * iOS's 24-unit grid and drawn once at [DOMAINS_SIDE] px.
 */
private const val DOMAINS_SIDE = 144

private val domainsIconBitmap by lazy {
    val u = DOMAINS_SIDE / 24f
    val mid = 12 * u
    val bitmap = Bitmap.createBitmap(DOMAINS_SIDE, DOMAINS_SIDE, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val top = 3.5f * u
    val bottom = 20.5f * u
    val left = 0.5f * u
    val shoulder = 7f * u
    val right = 23.5f * u
    val r = 3.5f * u
    val tag = android.graphics.Path().apply {
        moveTo(left, mid)
        lineTo(shoulder, top)
        lineTo(right - r, top)
        arcTo(right - 2 * r, top, right, top + 2 * r, -90f, 90f, false)
        lineTo(right, bottom - r)
        arcTo(right - 2 * r, bottom - 2 * r, right, bottom, 0f, 90f, false)
        lineTo(shoulder, bottom)
        close()
    }
    val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.BLACK }
    canvas.drawPath(tag, fill)
    // The hole and the "@" are cleared out of the body, not painted over it.
    val clear = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR)
    }
    canvas.drawCircle(5.9f * u, mid, 1.5f * u, clear)
    clear.apply {
        textSize = 14 * u
        // iOS draws it heavy and rounded; black is the heaviest system weight (see wordmarkPaint).
        typeface = Typeface.create("sans-serif-black", Typeface.NORMAL)
        textAlign = Paint.Align.CENTER
    }
    val metrics = clear.fontMetrics
    canvas.drawText("@", 15.5f * u, mid - (metrics.ascent + metrics.descent) / 2f, clear)
    bitmap.asImageBitmap()
}

/** The painter a tab is drawn with in the dock: .kachat's name tag, else its usual icon. */
@Composable
fun Screen.dockIconPainter(): Painter =
    if (this == Screen.KachatNames) remember { BitmapPainter(domainsIconBitmap) } else tabIconPainter()

/**
 * The box a tab's dock icon is drawn in. The name tag fills its square edge to edge where a
 * Material symbol leaves a margin, so it gets a little more room to read as the same size.
 */
fun Screen.dockIconModifier(size: androidx.compose.ui.unit.Dp): androidx.compose.ui.Modifier =
    if (this == Screen.KachatNames) androidx.compose.ui.Modifier.size(size * 7 / 6) else tabIconModifier(size)
