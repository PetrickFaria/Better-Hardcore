# Better Hardcore

Minecraft Java 1.19.2 mod for Forge 43.5.0, built with Java 17 and Gradle.

## Modes

- **Hardcore:** the world remains vanilla Hardcore and players have a 7-heart (14 HP) maximum. Death remains permanent through vanilla Hardcore.
- **Softcore:** select **Softcore: ON** on the Create World screen. It uses Survival internally, but records Softcore as world data. Players start at 3 hearts (6 HP), gain 1 HP per 36,000 alive server ticks, and are capped at 15 hearts (30 HP). A non-final death removes 2 HP from maximum health. Death at 1 heart, or any penalty that would reduce maximum health below 1 heart, moves that player to spectator permanently.
- Survival, Creative, and Adventure are not changed when Softcore is inactive.

Softcore world mode and per-player progress are stored in overworld `SavedData`. Server operators can use `/betterhardcore mode softcore`, `/betterhardcore mode hardcore`, or `/betterhardcore mode vanilla` for dedicated-server worlds or administration.

## Build

Use Java 17 and Gradle 7.5.1, then run:

```sh
gradle --no-daemon clean test build
```

The mod JAR is written to `build/libs/`. GitHub Actions runs tests and the build on pushes and pull requests, and stores the JAR as a workflow artifact.

## Scope notes

The Create World control is a Softcore toggle alongside Minecraft's existing mode controls; Minecraft 1.19.2 does not permit adding a GameType enum value safely. The generated world remains Survival internally and Softcore identity is persisted separately. Manual checks in a Forge 43.5.0 client and a dedicated server are still needed for the world-creation UI, heart art, reconnect, dimension changes, and multiplayer behavior.
