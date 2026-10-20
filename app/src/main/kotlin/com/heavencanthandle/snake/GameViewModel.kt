package com.heavencanthandle.snake

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.heavencanthandle.snake.game.Direction
import com.heavencanthandle.snake.game.Snake
import kotlin.random.Random
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class Phase { Ready, Playing, Paused, Over }

/** Runs the clock. One [Snake.step] per [TICK_MS]; the screen changes on that beat only. */
class GameViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs = app.getSharedPreferences("snake", Context.MODE_PRIVATE)
    private val random = Random.Default
    private var clock: Job? = null

    var game by mutableStateOf(Snake.start(COLUMNS, ROWS, random))
        private set
    var phase by mutableStateOf(Phase.Ready)
        private set
    var best by mutableIntStateOf(prefs.getInt(KEY_BEST, 0))
        private set

    /** A direction press or swipe. Before the first move it also starts the game. */
    fun turn(direction: Direction) {
        when (phase) {
            Phase.Playing -> game = game.turn(direction)
            Phase.Ready, Phase.Paused -> {
                game = game.turn(direction)
                play()
            }
            Phase.Over -> Unit
        }
    }

    /** The middle button: start, pause, resume, or a new game after a crash. */
    fun primary() {
        when (phase) {
            Phase.Ready, Phase.Paused -> play()
            Phase.Playing -> pause()
            Phase.Over -> {
                game = Snake.start(COLUMNS, ROWS, random)
                phase = Phase.Ready
            }
        }
    }

    fun pause() {
        if (phase != Phase.Playing) return
        clock?.cancel()
        phase = Phase.Paused
    }

    private fun play() {
        phase = Phase.Playing
        clock?.cancel()
        clock = viewModelScope.launch {
            while (true) {
                delay(TICK_MS)
                game = game.step(random)
                if (game.over) {
                    phase = Phase.Over
                    if (game.score > best) {
                        best = game.score
                        prefs.edit().putInt(KEY_BEST, best).apply()
                    }
                    break
                }
            }
        }
    }

    companion object {
        const val COLUMNS = 16
        const val ROWS = 15

        /** About three moves a second: slow enough for the E Ink panel to keep up cleanly. */
        const val TICK_MS = 330L

        private const val KEY_BEST = "best"
    }
}
