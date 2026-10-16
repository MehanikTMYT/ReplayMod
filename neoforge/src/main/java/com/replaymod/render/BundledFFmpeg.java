package com.replaymod.render;

import com.replaymod.core.versions.MCVer;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Fleet port (NeoForge 26.1.2): optional bundled FFmpeg.
 *
 * <p>The companion mod {@code replaymod_ffmpeg} (built by {@code :ffmpegJar}) ships FFmpeg 8.1.2 GPL executables and
 * their shared libraries (from org.bytedeco:ffmpeg, linux-x86_64-gpl / windows-x86_64-gpl) under
 * {@code replaymod-ffmpeg/<platform>/} with an {@code index.txt}. When no system FFmpeg is found, they are extracted
 * once into {@code .replay_cache/ffmpeg/<platform>-<version>/} and used. The Linux binary has RUNPATH $ORIGIN and
 * Windows looks up DLLs next to the exe, so no further setup is needed.
 */
public final class BundledFFmpeg {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String ROOT = "replaymod-ffmpeg/";

    private BundledFFmpeg() {}

    static boolean isOnPath(String exe) {
        String path = System.getenv("PATH");
        if (path == null) return false;
        for (String dir : path.split(File.pathSeparator)) {
            if (!dir.isEmpty() && new File(dir, exe).canExecute()) return true;
        }
        return false;
    }

    /** @return the absolute path of the bundled ffmpeg, or {@code null} if not available for this platform */
    public static String find() {
        String platform = switch (Util.getPlatform()) {
            case LINUX -> "linux-x86_64";
            case WINDOWS -> "windows-x86_64";
            default -> null;
        };
        String arch = System.getProperty("os.arch", "");
        if (platform == null || !(arch.equals("amd64") || arch.equals("x86_64"))) return null;

        ClassLoader loader = BundledFFmpeg.class.getClassLoader();
        URL index = loader.getResource(ROOT + platform + "/index.txt");
        if (index == null) return null;
        try {
            List<String> lines;
            try (InputStream in = index.openStream()) {
                lines = new String(in.readAllBytes(), StandardCharsets.UTF_8).lines().filter(l -> !l.isBlank()).toList();
            }
            // first line: version, then one file name per line; the executable is "ffmpeg" / "ffmpeg.exe"
            String version = lines.get(0).trim();
            Path dir = MCVer.getMinecraft().gameDirectory.toPath().resolve(".replay_cache/ffmpeg/" + platform + "-" + version);
            String exeName = platform.startsWith("windows") ? "ffmpeg.exe" : "ffmpeg";
            Path exe = dir.resolve(exeName);
            Path done = dir.resolve(".complete");
            if (!Files.exists(done)) {
                Files.createDirectories(dir);
                for (String name : lines.subList(1, lines.size())) {
                    name = name.trim();
                    try (InputStream in = loader.getResourceAsStream(ROOT + platform + "/" + name)) {
                        if (in == null) throw new IOException("missing bundled file " + name);
                        Files.copy(in, dir.resolve(name), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
                exe.toFile().setExecutable(true);
                Files.writeString(done, version);
                LOGGER.info("Extracted bundled FFmpeg {} to {}", version, dir);
            }
            return exe.toAbsolutePath().toString();
        } catch (Exception e) {
            LOGGER.error("Failed to extract bundled FFmpeg:", e);
            return null;
        }
    }
}
