package com.heavencanthandle.snake.game

import kotlin.random.Random

/** One square of the board. (0, 0) is the top-left corner. */
data class Cell(val x: Int, val y: Int) {
    operator fun plus(direction: Direction) = Cell(x + direction.dx, y + direction.dy)
}

enum class Direction(val dx: Int, val dy: Int) {
    Up(0, -1),
    Down(0, 1),
    Left(-1, 0),
    Right(1, 0);

    val opposite: Direction
        get() = when (this) {
            Up -> Down
            Down -> Up
            Left -> Right
            Right -> Left
        }
}

/**
 * The whole game as one immutable value. [step] moves it one square; nothing here knows
 * about time, so the tick rate lives in the UI.
 *
 * [body] is head first. [queued] holds turns pressed between moves (at most [MAX_QUEUED]),
 * so two quick presses make a U-turn over two moves instead of one being lost.
 * [food] is null only when the snake fills the board.
 */
data class Snake(
    val width: Int,
    val height: Int,
    val body: List<Cell>,
    val heading: Direction,
    val food: Cell?,
    val queued: List<Direction> = emptyList(),
    val score: Int = 0,
    val dead: Boolean = false,
) {
    val head: Cell get() = body.first()

    /** True once nothing more can happen: crashed, or no free square left for food. */
    val over: Boolean get() = dead || food == null

    /** Queues a turn. Reversing into the neck, or repeating the last direction, is ignored. */
    fun turn(direction: Direction): Snake {
        if (over || queued.size >= MAX_QUEUED) return this
        val last = queued.lastOrNull() ?: heading
        if (direction == last || direction == last.opposite) return this
        return copy(queued = queued + direction)
    }

    fun step(random: Random): Snake {
        if (over) return this
        val direction = queued.firstOrNull() ?: heading
        val rest = queued.drop(1)
        val next = head + direction
        val eats = next == food
        // Without food the tail moves away this same step, so its square is free to enter.
        val kept = if (eats) body else body.dropLast(1)
        val offBoard = next.x !in 0 until width || next.y !in 0 until height
        if (offBoard || next in kept) {
            return copy(heading = direction, queued = rest, dead = true)
        }
        val moved = listOf(next) + kept
        return copy(
            body = moved,
            heading = direction,
            queued = rest,
            food = if (eats) placeFood(width, height, moved, random) else food,
            score = if (eats) score + POINTS else score,
        )
    }

    companion object {
        const val MAX_QUEUED = 2
        const val POINTS = 1
        const val START_LENGTH = 3

        /** A short snake in the middle of the left half, heading right. */
        fun start(width: Int, height: Int, random: Random): Snake {
            val y = height / 2
            val headX = width / 2 - 1
            val body = (0 until START_LENGTH).map { Cell(headX - it, y) }
            return Snake(
                width = width,
                height = height,
                body = body,
                heading = Direction.Right,
                food = placeFood(width, height, body, random),
            )
        }

        fun placeFood(width: Int, height: Int, body: List<Cell>, random: Random): Cell? {
            val taken = body.toHashSet()
            val free = ArrayList<Cell>(width * height - taken.size)
            for (y in 0 until height) for (x in 0 until width) {
                val cell = Cell(x, y)
                if (cell !in taken) free += cell
            }
            return if (free.isEmpty()) null else free[random.nextInt(free.size)]
        }
    }
}
