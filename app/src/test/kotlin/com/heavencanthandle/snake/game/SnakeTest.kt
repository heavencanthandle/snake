package com.heavencanthandle.snake.game

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SnakeTest {
    private val random = Random(7)

    /** Heading right on a 10x10 board, head at (4, 5), food out of the way at (0, 0). */
    private fun snake(
        body: List<Cell> = listOf(Cell(4, 5), Cell(3, 5), Cell(2, 5)),
        heading: Direction = Direction.Right,
        food: Cell? = Cell(0, 0),
    ) = Snake(width = 10, height = 10, body = body, heading = heading, food = food)

    @Test
    fun movesOneSquareAndKeepsLength() {
        val moved = snake().step(random)
        assertEquals(listOf(Cell(5, 5), Cell(4, 5), Cell(3, 5)), moved.body)
        assertFalse(moved.dead)
    }

    @Test
    fun turnTakesEffectOnNextStep() {
        val moved = snake().turn(Direction.Up).step(random)
        assertEquals(Cell(4, 4), moved.head)
        assertEquals(Direction.Up, moved.heading)
    }

    @Test
    fun reversingIsIgnored() {
        val turned = snake().turn(Direction.Left)
        assertTrue(turned.queued.isEmpty())
        assertEquals(Cell(5, 5), turned.step(random).head)
    }

    @Test
    fun twoQuickTurnsMakeAUTurn() {
        val s = snake().turn(Direction.Down).turn(Direction.Left)
        val once = s.step(random)
        val twice = once.step(random)
        assertEquals(Cell(4, 6), once.head)
        assertEquals(Cell(3, 6), twice.head)
        assertFalse(twice.dead)
    }

    @Test
    fun eatingGrowsScoresAndPlacesNewFood() {
        val eaten = snake(food = Cell(5, 5)).step(random)
        assertEquals(listOf(Cell(5, 5), Cell(4, 5), Cell(3, 5), Cell(2, 5)), eaten.body)
        assertEquals(Snake.POINTS, eaten.score)
        assertNotNull(eaten.food)
        assertFalse(eaten.food in eaten.body)
    }

    @Test
    fun hittingTheWallEndsTheGame() {
        val atEdge = snake(body = listOf(Cell(9, 5), Cell(8, 5), Cell(7, 5)))
        val crashed = atEdge.step(random)
        assertTrue(crashed.dead)
        assertTrue(crashed.over)
        assertEquals(atEdge.body, crashed.body)
        assertEquals(crashed, crashed.step(random))
    }

    @Test
    fun hittingItselfEndsTheGame() {
        // A hook: head at (2, 2) heading down into its own body at (2, 3).
        val body = listOf(Cell(2, 2), Cell(3, 2), Cell(3, 3), Cell(2, 3), Cell(1, 3))
        val crashed = snake(body = body, heading = Direction.Left).turn(Direction.Down).step(random)
        assertTrue(crashed.dead)
    }

    @Test
    fun chasingTheTailIsAllowed() {
        // A 2x2 loop: the head moves into the square the tail leaves this same step.
        val body = listOf(Cell(2, 2), Cell(3, 2), Cell(3, 3), Cell(2, 3))
        val moved = snake(body = body, heading = Direction.Left).turn(Direction.Down).step(random)
        assertFalse(moved.dead)
        assertEquals(Cell(2, 3), moved.head)
    }

    @Test
    fun foodIsNeverOnTheSnakeAndNullWhenBoardIsFull() {
        val full = (0 until 2).flatMap { y -> (0 until 2).map { x -> Cell(x, y) } }
        assertNull(Snake.placeFood(2, 2, full, random))
        val almost = full.drop(1)
        assertEquals(Cell(0, 0), Snake.placeFood(2, 2, almost, random))
    }

    @Test
    fun startIsCentredAndAlive() {
        val s = Snake.start(16, 14, random)
        assertEquals(Snake.START_LENGTH, s.body.size)
        assertEquals(Direction.Right, s.heading)
        assertFalse(s.over)
        assertFalse(s.food!! in s.body)
    }
}
