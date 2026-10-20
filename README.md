# snake

Snake game! old dumb phone style

The flip-phone Snake, for the **Mudita Kompakt** (E Ink, Android 12). A grid snake, food, growing,
wall and self collision, score and best score, game over and again. Chunky black-on-white pixels in
the old Nokia style. The look and feel follows "Snake 97: Retro Phone Classic"; no code comes from it.

## Playing

- **Arrows** under the board, or **swipe** anywhere on the board. The first arrow or swipe starts
  the game.
- The **middle button** (or a tap on the board) starts, pauses, resumes, and starts again after a
  crash.
- Leaving the app pauses it.

## Made for E Ink

- About three moves a second (`GameViewModel.TICK_MS`), and nothing moves between ticks.
- Black on white only. Every "dot" is a whole number of screen pixels, so there is no grey edge.
- A move changes only the head, the tail and sometimes the food. The score line recomposes only
  when the score changes, and the board canvas is the only thing drawn on every tick.

## Build

Kotlin + Jetpack Compose + Mudita MMD, one `:app` module. AGP 8.13.1, Kotlin 2.2.21, Gradle 8.14.3,
JDK 17, minSdk/targetSdk 31.

```
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug
adb -d install -r app\build\outputs\apk\debug\app-debug.apk
```

The phone has the **debug** build, signed with this PC's debug key. There is no release keystore yet.

## Code

- `game/Snake.kt`: the whole game as one immutable value. No Android, no time. Unit-tested in
  `app/src/test/kotlin/.../game/SnakeTest.kt`.
- `GameViewModel.kt`: the clock (one step per tick), phase (ready, playing, paused, over), best
  score in SharedPreferences.
- `ui/Board.kt`: draws the board in dots. `ui/GameScreen.kt`: score, board, swipes, buttons.

## License

CC0 1.0 (see `LICENSE`).
