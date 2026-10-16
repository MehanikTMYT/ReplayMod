# ReplayMod → NeoForge 26.1.2 (fleet port, 2026-10-10)

Upstream: ReplayMod/ReplayMod `stable` @ 4361ec95 (Release 2.6.27), GPL-3.0-or-later; jGui/ReplayStudio submodules.
Upstream builds Fabric/Forge only; 26.1 is Fabric. This directory is a standalone NeoForge (ModDevGradle) project.

## How the sources were produced
1. Upstream multi-version build, trimmed to the preprocessor chain 1.16.4 (main) → 26.1 (`settings.gradle.kts`,
   `root.gradle.kts`, `jGui/root.gradle.kts`; originals in `../.fleet-orig/`). Preprocessor forced to 221276c7d and the
   26.1 node has `null` mappings (unobfuscated) — the pinned 5bf41e6c3 fails `mergeSourceNamedAndMojangMappings`.
   Needs JDK 8 and 16 toolchains (ReplayStudio, 1.17.1) besides 17/21/25:
   `JDK=21 remote-build.sh run client-mods/ReplayMod ... -- -Porg.gradle.java.installations.paths=<jdk8>,<jdk16>
   :jGui:26.1:preprocessCode :26.1:preprocessCode :26.1:bundleJar`.
2. `src/main/java` = `versions/26.1/build/preprocessed/main/java` + `versions/26.1/src/main/java` (overlays) +
   `jGui/versions/26.1/build/preprocessed/main/java`; `com/replaymod/gradle` removed.
3. Resources = upstream processed resources; mixin configs taken from the 26.1 bundle jar (preprocessed), refmaps
   dropped, `mixins.jgui.json` un-relocated; Fabric metadata removed, `META-INF/neoforge.mods.toml` added.
4. `libs/replaymod-libs.jar` = third-party part of `:26.1:bundleJar` (relocated `com.replaymod.lib.*`, ReplayStudio,
   ViaVersion, netty → MC's io.netty). Source imports rewritten to the relocated packages
   (`versions/26.1/build/configureRelocation`, except jGui which is compiled in-tree). `lwjgl-utils` from JitPack.

## NeoForge glue (replaces Fabric API / loader)
- `core/ReplayModBackend`: `@Mod(dist = CLIENT)`. ReplayMod is created + modules initialised in
  `RegisterKeyMappingsEvent` (mods are constructed in parallel before `Minecraft` exists — the Fabric entrypoint ran
  inside the Minecraft constructor); config screen via `IConfigScreenFactory`; restrictions channel registered as an
  optional payload (configuration + play, handlers no-op as on Fabric).
- `core/KeyBindingRegistry`: category `new KeyMapping.Category`, mappings collected and registered in the event.
- `core/utils/ModInfoGetter`, `VideoRenderer`, `Pipelines`, `GuiHandler` (FancyMenu check), mixin plugin (Iris):
  `ModList` / `FMLLoader.getCurrent().getLoadingModList()`.
- `core/versions/LangResourcePack.modRootPath()`: mod root from our own resource URL (also used by the Iris ODS pack).
- `replay/handler/GuiHandler`: main-menu button via `Screen#addRenderableWidget` (Fabric `Screens.getWidgets`).
- Removed: MM/OptiFabric launchers, MixinExtras init (NeoForge ships MixinExtras), Mod Menu impl, frex entrypoint.
- `extras/youtube/YoutubeUploader`: `YouTubeScopes` constant inlined (class minimized out of the libs).

## Bundled FFmpeg (optional companion mod)
`:ffmpegJar` → `replaymod_ffmpeg-8.1.2-1.5.14.jar` (~64 MB): FFmpeg 8.1.2 GPL (x264/x265/libvpx/ProRes) executables
+ shared libraries for linux-x86_64 / windows-x86_64 from `org.bytedeco:ffmpeg` (JNI libs dropped). Without it the mod
behaves as upstream (system FFmpeg). `render/BundledFFmpeg`: used only when no `ffmpeg` is on the PATH (after the
upstream Windows/macOS lookups); extracted once to `.replay_cache/ffmpeg/<platform>-<version>/`.
Verified on Linux: extraction + H.264 encode with the extracted binary.

## Verified (vdsfi, Xvfb)
compile, `build`, client start without errors (all 102 mixins applied, `replaymod_lang` pack, 15 atlases), Replay
Viewer button on the title screen, bundled FFmpeg. Not yet: recording a world, replay viewer, rendering a video,
Iris/Sodium compat at runtime (compileOnly 1.11.4 / 0.9.2).
