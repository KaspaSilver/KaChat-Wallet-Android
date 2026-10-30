package com.kachat.app.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorGroup
import androidx.compose.ui.graphics.vector.VectorPath
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * SF Symbols that Material has no single icon for, put together from Material's own paths so
 * they tint and size like any other icon.
 */
object IosSymbols {
    /** "lock.shield": a shield outline with a solid lock inside. */
    val LockShield: ImageVector by lazy {
        val builder = ImageVector.Builder(
            name = "LockShield",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        )
        fun addPaths(group: VectorGroup) {
            for (node in group) {
                when (node) {
                    is VectorPath -> builder.addPath(
                        pathData = node.pathData,
                        pathFillType = node.pathFillType,
                        fill = SolidColor(Color.Black),
                    )
                    is VectorGroup -> addPaths(node)
                }
            }
        }
        addPaths(Icons.Outlined.Shield.root)
        builder.addGroup(scaleX = 0.4f, scaleY = 0.4f, pivotX = 12f, pivotY = 11.5f)
        addPaths(Icons.Filled.Lock.root)
        builder.clearGroup()
        builder.build()
    }

    /**
     * "lock.shield.fill" (the dock's Storage, as the iPhone's tab bar draws "lock.shield"): a solid
     * shield (Material Shield) with the lock cut out of it - one even-odd path, so the lock's body
     * and its shackle are holes.
     */
    val LockShieldFill: ImageVector by lazy {
        ImageVector.Builder(
            name = "LockShieldFill",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).addPath(
            pathData = addPathNodes(
                "M12,1L3,5v6c0,5.55 3.84,10.74 9,12 5.16,-1.26 9,-6.45 9,-12V5l-9,-4z" +
                    // the lock's body
                    "M8.5,11.2h7v5.6h-7z" +
                    // its shackle: an upside-down U, the outer edge then the inner edge back
                    "M9.6,11.2V9.1a2.4,2.4 0 0 1 4.8,0v2.1h-1.3V9.1a1.1,1.1 0 0 0 -2.2,0v2.1z"
            ),
            pathFillType = PathFillType.EvenOdd,
            fill = SolidColor(Color.Black),
        ).build()
    }

    /**
     * "quote.bubble": the speech bubble as an outline (Material ChatBubbleOutline) with the
     * same two quote marks as [QuoteBubbleFill], solid inside it - one even-odd path, so the
     * ring and the marks fill and the space between them stays clear.
     */
    val QuoteBubble: ImageVector by lazy {
        ImageVector.Builder(
            name = "QuoteBubble",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).addPath(
            pathData = addPathNodes(
                "M20,2H4c-1.1,0 -2,0.9 -2,2v18l4,-4h14c1.1,0 2,-0.9 2,-2V4c0,-1.1 -0.9,-2 -2,-2z" +
                    "M20,16H5.17L4,17.17V4h16v12z" +
                    "M8.4,13h1.8l1.2,-2.4V7H7.8v3.6h1.8z" +
                    "M13.2,13H15l1.2,-2.4V7h-3.6v3.6h1.8z"
            ),
            pathFillType = PathFillType.EvenOdd,
            fill = SolidColor(Color.Black),
        ).build()
    }

    /**
     * "quote.bubble.fill": a solid speech bubble (Material ChatBubble) with two quote marks
     * (Material FormatQuote at 0.6x about the bubble's centre) cut out of it - one even-odd
     * path, so the marks are holes that show what is behind.
     */
    val QuoteBubbleFill: ImageVector by lazy {
        ImageVector.Builder(
            name = "QuoteBubbleFill",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).addPath(
            pathData = addPathNodes(
                "M20,2H4c-1.1,0 -2,0.9 -2,2v18l4,-4h14c1.1,0 2,-0.9 2,-2V4c0,-1.1 -0.9,-2 -2,-2z" +
                    "M8.4,13h1.8l1.2,-2.4V7H7.8v3.6h1.8z" +
                    "M13.2,13H15l1.2,-2.4V7h-3.6v3.6h1.8z"
            ),
            pathFillType = PathFillType.EvenOdd,
            fill = SolidColor(Color.Black),
        ).build()
    }
}
