package com.heavencanthandle.snake.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import com.heavencanthandle.snake.game.Cell
import com.heavencanthandle.snake.game.Direction
import com.heavencanthandle.snake.game.Snake

/**
 * Old-phone pixels: every board square is [DOTS] x [DOTS] "dots", and every dot is a whole
 * number of screen pixels, so edges stay crisp on E Ink (no grey anti-aliased fringes).
 * A segment fills the middle 3x3 dots and bridges to its neighbours, which gives the
 * continuous one-cell-wide tube of the Nokia snake.
 *
 * [game] is read inside the draw block only, so a move redraws this canvas and recomposes
 * nothing else. Squares that did not change keep exactly the same pixels, so the panel
 * only has to flip the head, the tail and the food.
 */
@Composable
fun Board(columns: Int, rows: Int, game: () -> Snake, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        // Two dots of frame (line + gap) on each side of the playing field.
        val dotsWide = columns * DOTS + 4
        val dotsHigh = rows * DOTS + 4
        val dotPx = minOf(
            constraints.maxWidth / dotsWide,
            constraints.maxHeight / dotsHigh,
        ).coerceAtLeast(1)
        val density = LocalDensity.current
        val size = with(density) {
            androidx.compose.ui.unit.DpSize((dotsWide * dotPx).toDp(), (dotsHigh * dotPx).toDp())
        }
        Canvas(Modifier.size(size)) {
            val d = dotPx.toFloat()
            frame(dotsWide, dotsHigh, d)
            val g = game()
            g.food?.let { food(it, d) }
            g.body.forEachIndexed { i, cell ->
                segment(cell, d)
                g.body.getOrNull(i + 1)?.let { bridge(cell, it, d) }
            }
            head(g.head, g.heading, g.dead, d)
        }
    }
}

private const val DOTS = 5
private val Ink = Color.Black
private val Paper = Color.White

private fun DrawScope.dot(x: Int, y: Int, d: Float, color: Color = Ink) {
    drawRect(color, Offset(x * d, y * d), Size(d, d))
}

private fun DrawScope.dots(x: Int, y: Int, w: Int, h: Int, d: Float, color: Color = Ink) {
    drawRect(color, Offset(x * d, y * d), Size(w * d, h * d))
}

private fun DrawScope.frame(wide: Int, high: Int, d: Float) {
    dots(0, 0, wide, 1, d)
    dots(0, high - 1, wide, 1, d)
    dots(0, 0, 1, high, d)
    dots(wide - 1, 0, 1, high, d)
}

/** Top-left dot of a board square, inside the frame. */
private fun origin(cell: Cell) = Pair(2 + cell.x * DOTS, 2 + cell.y * DOTS)

private fun DrawScope.segment(cell: Cell, d: Float) {
    val (x, y) = origin(cell)
    dots(x + 1, y + 1, 3, 3, d)
}

/** Fills the one-dot gap between two neighbouring segments. */
private fun DrawScope.bridge(a: Cell, b: Cell, d: Float) {
    val (ax, ay) = origin(a)
    when {
        b.x == a.x + 1 -> dots(ax + 4, ay + 1, 2, 3, d)
        b.x == a.x - 1 -> dots(ax - 1, ay + 1, 2, 3, d)
        b.y == a.y + 1 -> dots(ax + 1, ay + 4, 3, 2, d)
        b.y == a.y - 1 -> dots(ax + 1, ay - 1, 3, 2, d)
    }
}

/** The head gets an eye: one paper dot on its front-left corner. A crash gets an X. */
private fun DrawScope.head(cell: Cell, heading: Direction, dead: Boolean, d: Float) {
    val (x, y) = origin(cell)
    if (dead) {
        dots(x + 1, y + 1, 3, 3, d, Paper)
        for (i in 0..4) {
            dot(x + i, y + i, d)
            dot(x + 4 - i, y + i, d)
        }
        return
    }
    val (ex, ey) = when (heading) {
        Direction.Right -> 3 to 1
        Direction.Left -> 1 to 3
        Direction.Up -> 1 to 1
        Direction.Down -> 3 to 3
    }
    dot(x + ex, y + ey, d, Paper)
}

/** A small hollow diamond, like the Nokia's food. */
private fun DrawScope.food(cell: Cell, d: Float) {
    val (x, y) = origin(cell)
    dot(x + 2, y + 1, d)
    dot(x + 1, y + 2, d)
    dot(x + 3, y + 2, d)
    dot(x + 2, y + 3, d)
}
