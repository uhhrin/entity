# Entity

Herobrine-style **haunting** plugin for **Paper 1.21.x**. Random (or forced) scare sequences with custom skin, sounds, particles, and titles.

## Features

- Auto-start haunting when players join (configurable)
- Random haunt intervals between min/max delays
- Forced haunt via admin command
- Custom player skin (texture value + signature)
- Title / subtitle messages, sounds, and particles
- Bed-enter interaction, death cleanup, per-player tracking
- Bypass permission for immune players

## Requirements

| Component | Version |
|-----------|---------|
| Server | Paper (or compatible) 1.21.x |
| Java | 21+ |

## Install

1. Build or download the shaded jar from [Releases](https://github.com/uhhrin/entity/releases).
2. Drop the jar into your server `plugins/` folder.
3. Restart. Default `config.yml` is created on first run.

## Build

```bash
./gradlew build
```

Shaded plugin jar lands in `build/libs/` (`build` depends on `shadowJar`).

Optional local test server:

```bash
./gradlew runServer
```

## Commands

Aliases: `/entity`, `/ent`, `/herobrine`

| Command | Permission | Description |
|---------|------------|-------------|
| `/entity force [player]` | `entity.force` | Trigger a haunt immediately |
| `/entity stop [player]` | `entity.admin` | Stop haunting for a player |
| `/entity start [player]` | `entity.admin` | Resume haunting |
| `/entity status [player]` | `entity.admin` | Show haunt status |
| `/entity reload` | `entity.admin` | Reload config |
| `/entity toggle [player]` | `entity.admin` | Toggle haunting on/off |

## Permissions

| Node | Default | Description |
|------|---------|-------------|
| `entity.force` | op | Force haunt |
| `entity.admin` | op | Admin commands |
| `entity.bypass` | false | Immune to haunting |
| `entity.*` | op | All of the above |

## Configuration

Key options in `plugins/entity/config.yml`:

| Key | Default | Meaning |
|-----|---------|---------|
| `haunt.enabled` | `true` | Master switch |
| `haunt.auto-start-on-join` | `true` | Start schedule on join |
| `haunt.first-haunt-delay` | `120` | Seconds before first haunt |
| `haunt.min-interval` / `max-interval` | `60` / `600` | Random delay range (seconds) |
| `entity.damage` | `2.0` | Damage dealt during haunt |
| `entity.flash-duration` | `15` | Flash length (ticks) |
| `entity.spawn-distance` | `4.0` | Spawn distance from player |
| `entity.skin.value` / `signature` | (set) | Herobrine skin texture |

Sounds, particles, and messages are also configurable — see [`src/main/resources/config.yml`](src/main/resources/config.yml).

## Releases

Push a tag matching `v*` (e.g. `v1.0.0`) to publish a GitHub Release with the plugin jar.

## License

No license file is bundled yet — all rights reserved unless you add one.
