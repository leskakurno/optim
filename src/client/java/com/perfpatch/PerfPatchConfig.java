package com.perfpatch;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

/**
 * Tiny dependency-free config. File: config/perfpatch.properties
 */
public final class PerfPatchConfig {
	public enum GlowMode { VANILLA, NEAR, OFF }
	public enum GlintMode { VANILLA, OFF }

	private static final PerfPatchConfig INSTANCE = new PerfPatchConfig();

	/** VANILLA = unchanged, NEAR = outline only within glowMaxDistance blocks, OFF = never. */
	public volatile GlowMode glowMode = GlowMode.NEAR;
	public volatile double glowMaxDistanceSq = 32.0 * 32.0;
	/** VANILLA = unchanged, OFF = no enchantment glint anywhere. */
	public volatile GlintMode glintMode = GlintMode.VANILLA;

	private PerfPatchConfig() {}

	public static PerfPatchConfig get() {
		return INSTANCE;
	}

	public static void load() {
		Path file = FabricLoader.getInstance().getConfigDir().resolve("perfpatch.properties");
		Properties p = new Properties();
		if (Files.exists(file)) {
			try (InputStream in = Files.newInputStream(file)) {
				p.load(in);
			} catch (IOException e) {
				PerfPatchClient.LOGGER.warn("Could not read {}, using defaults", file, e);
			}
		}

		INSTANCE.glowMode = parseEnum(p.getProperty("glow_outline_mode"), GlowMode.NEAR);
		double dist = parseDouble(p.getProperty("glow_outline_max_distance"), 32.0);
		INSTANCE.glowMaxDistanceSq = dist * dist;
		INSTANCE.glintMode = parseEnum(p.getProperty("glint_mode"), GlintMode.VANILLA);

		// Always (re)write so the user can see all options.
		Properties out = new Properties();
		out.setProperty("glow_outline_mode", INSTANCE.glowMode.name());
		out.setProperty("glow_outline_max_distance", String.valueOf(dist));
		out.setProperty("glint_mode", INSTANCE.glintMode.name());
		try (OutputStream os = Files.newOutputStream(file)) {
			out.store(os, "PerfPatch\n"
				+ "glow_outline_mode: VANILLA | NEAR | OFF  (outline of glowing entities)\n"
				+ "glow_outline_max_distance: blocks, used by NEAR\n"
				+ "glint_mode: VANILLA | OFF  (enchantment glint)");
		} catch (IOException e) {
			PerfPatchClient.LOGGER.warn("Could not write {}", file, e);
		}
	}

	private static <E extends Enum<E>> E parseEnum(String s, E def) {
		if (s == null) return def;
		try {
			return Enum.valueOf(def.getDeclaringClass(), s.trim().toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException e) {
			return def;
		}
	}

	private static double parseDouble(String s, double def) {
		if (s == null) return def;
		try {
			double v = Double.parseDouble(s.trim());
			return v > 0 ? v : def;
		} catch (NumberFormatException e) {
			return def;
		}
	}
}
