# Snake

Read [README.md](README.md) for what the app is, how it is built, and where the code is.

## Do not

- Do not speed the tick up past a few moves a second, add animation between moves, or add grey or colour. The panel is E Ink: every extra redraw ghosts.
- Do not make the score line or the buttons read `game` directly. Only the board canvas should redraw on every move; the score reads it through `derivedStateOf`.
- Do not put time, Android, or randomness from a global into `game/Snake.kt`. It takes a `Random` so tests can pass a seeded one.
- Do not draw arrows or snake parts with Unicode characters. They are drawn shapes, so they look the same whatever the font has.
- Phone updates replace the installed app: bump `versionCode`, then `adb -d install -r`, same certificate. The phone has the debug build. Do not uninstall, clear data, or create a keystore unless asked.
