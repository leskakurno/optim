# PerfPatch (Fabric 1.21.11)

Client-only optimizations that sit next to Sodium:

- **Glowing entity outline**: limit by distance or disable (`glow_outline_mode`).
- **Enchantment glint**: optional full disable (`glint_mode`).

Config: `config/perfpatch.properties` (created on first launch).

All mixins use `require = 0`, so a changed internal name only disables that one feature
(check `latest.log` for "PerfPatch" / "@Inject ... not found").

Build: `./gradlew build` (JDK 21+), jar in `build/libs/`.
