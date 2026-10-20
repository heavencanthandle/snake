package com.heavencanthandle.snake.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.heavencanthandle.snake.GameViewModel
import com.heavencanthandle.snake.Phase
import com.heavencanthandle.snake.game.Direction
import com.mudita.mmd.components.buttons.OutlinedButtonMMD
import com.mudita.mmd.components.text.TextMMD
import kotlin.math.abs

@Composable
fun GameScreen(vm: GameViewModel) {
    Column(
        Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ScoreLine(vm)
        Spacer(Modifier.height(6.dp))
        Board(
            columns = GameViewModel.COLUMNS,
            rows = GameViewModel.ROWS,
            game = { vm.game },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .swipes(vm::turn, vm::primary),
        )
        Spacer(Modifier.height(6.dp))
        StatusLine(vm)
        Spacer(Modifier.height(6.dp))
        Controls(vm)
    }
}

/** Reads only the score, so it recomposes when food is eaten and not on every move. */
@Composable
private fun ScoreLine(vm: GameViewModel) {
    val digits = MaterialTheme.typography.titleLarge.copy(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
    )
    val score by remember(vm) { derivedStateOf { vm.game.score } }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        TextMMD(text = score.toString().padStart(4, '0'), style = digits)
        TextMMD(text = "BEST " + vm.best.toString().padStart(4, '0'), style = digits)
    }
}

@Composable
private fun StatusLine(vm: GameViewModel) {
    val text = when (vm.phase) {
        Phase.Ready -> "Press an arrow or swipe to start"
        Phase.Playing -> ""
        Phase.Paused -> "Paused"
        Phase.Over -> if (vm.game.dead) "Game over" else "You filled the board!"
    }
    TextMMD(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.height(28.dp),
    )
}

/**
 * A swipe turns as soon as it has travelled past touch slop, without waiting for the
 * finger to lift. A tap is the middle button.
 */
private fun Modifier.swipes(onSwipe: (Direction) -> Unit, onTap: () -> Unit) =
    pointerInput(Unit) {
        val slop = viewConfiguration.touchSlop * 2
        var travel = Offset.Zero
        var fired = false
        detectDragGestures(
            onDragStart = {
                travel = Offset.Zero
                fired = false
            },
        ) { change, amount ->
            change.consume()
            travel += amount
            if (!fired && travel.getDistance() > slop) {
                fired = true
                onSwipe(
                    if (abs(travel.x) > abs(travel.y)) {
                        if (travel.x > 0) Direction.Right else Direction.Left
                    } else {
                        if (travel.y > 0) Direction.Down else Direction.Up
                    },
                )
            }
        }
    }.pointerInput(Unit) {
        detectTapGestures(onTap = { onTap() })
    }

@Composable
private fun Controls(vm: GameViewModel) {
    val gap = 8.dp
    Column(verticalArrangement = Arrangement.spacedBy(gap), horizontalAlignment = Alignment.CenterHorizontally) {
        Arrow(Direction.Up, vm::turn)
        Row(horizontalArrangement = Arrangement.spacedBy(gap), verticalAlignment = Alignment.CenterVertically) {
            Arrow(Direction.Left, vm::turn)
            OutlinedButtonMMD(onClick = vm::primary, modifier = Modifier.width(ButtonWidth).height(ButtonHeight)) {
                TextMMD(
                    text = when (vm.phase) {
                        Phase.Ready -> "Start"
                        Phase.Playing -> "Pause"
                        Phase.Paused -> "Go"
                        Phase.Over -> "Again"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
            }
            Arrow(Direction.Right, vm::turn)
        }
        Arrow(Direction.Down, vm::turn)
    }
}

private val ButtonWidth = 96.dp
private val ButtonHeight = 52.dp

/** Arrows are drawn triangles, not Unicode, so they look the same whatever the font has. */
@Composable
private fun Arrow(direction: Direction, onPress: (Direction) -> Unit) {
    OutlinedButtonMMD(
        onClick = { onPress(direction) },
        modifier = Modifier
            .width(ButtonWidth)
            .height(ButtonHeight)
            .semantics { contentDescription = direction.name },
    ) {
        Canvas(Modifier.size(22.dp)) {
            val w = size.width
            val h = size.height
            val path = Path().apply {
                when (direction) {
                    Direction.Up -> { moveTo(w / 2, 0f); lineTo(w, h); lineTo(0f, h) }
                    Direction.Down -> { moveTo(0f, 0f); lineTo(w, 0f); lineTo(w / 2, h) }
                    Direction.Left -> { moveTo(w, 0f); lineTo(w, h); lineTo(0f, h / 2) }
                    Direction.Right -> { moveTo(0f, 0f); lineTo(w, h / 2); lineTo(0f, h) }
                }
                close()
            }
            drawPath(path, Color.Black)
        }
    }
}
