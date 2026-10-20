package com.heavencanthandle.snake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.heavencanthandle.snake.ui.GameScreen
import com.heavencanthandle.snake.ui.monochrome
import com.mudita.mmd.ThemeMMD

class MainActivity : ComponentActivity() {
    private val vm: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThemeMMD(colorScheme = monochrome) {
                GameScreen(vm)
            }
        }
    }

    /** Leaving the app (or the screen going off) pauses, so the snake never dies unseen. */
    override fun onPause() {
        super.onPause()
        vm.pause()
    }
}
