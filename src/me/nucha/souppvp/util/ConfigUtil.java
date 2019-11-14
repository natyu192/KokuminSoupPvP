package me.nucha.souppvp.util;

import me.nucha.souppvp.SoupPvPPlugin;

public class ConfigUtil {

	public static int AMOUNT_OF_SOUP_HEALING;
	public static boolean FFA_BUILD;
	public static int FFA_BUILD_HEIGHT;
	public static int RESOURCE_COOLDOWN_MIN;
	public static int RESOURCE_COOLDOWN_MAX;
	public static int RESOURCE_AMOUNT_MIN;
	public static int RESOURCE_AMOUNT_MAX;

	public static void init(SoupPvPPlugin plugin) {
		plugin.getConfig().addDefault("amount-of-soup-healing", 7);
		plugin.getConfig().addDefault("ffa-build", false);
		plugin.getConfig().addDefault("ffa-build-height", 30);
		plugin.getConfig().addDefault("resource.cooldown.min", 15);
		plugin.getConfig().addDefault("resource.cooldown.max", 25);
		plugin.getConfig().addDefault("resource.amount.min", 7);
		plugin.getConfig().addDefault("resource.amount.max", 15);
		plugin.getConfig().options().copyDefaults(true);
		load(plugin);
	}

	public static void load(SoupPvPPlugin plugin) {
		AMOUNT_OF_SOUP_HEALING = plugin.getConfig().getInt("amount-of-soup-healing");
		if (AMOUNT_OF_SOUP_HEALING <= 0) {
			AMOUNT_OF_SOUP_HEALING = 7;
			plugin.getConfig().set("amount-of-soup-healing", 7);
		}
		FFA_BUILD = plugin.getConfig().getBoolean("ffa-build");
		FFA_BUILD_HEIGHT = plugin.getConfig().getInt("ffa-build-height");
		if (FFA_BUILD_HEIGHT < 0) {
			FFA_BUILD_HEIGHT = 0;
			plugin.getConfig().set("ffa-build-height", 7);
		}
		RESOURCE_COOLDOWN_MIN = plugin.getConfig().getInt("resource.cooldown.min");
		if (RESOURCE_COOLDOWN_MIN < 0) {
			RESOURCE_COOLDOWN_MIN = 0;
			plugin.getConfig().set("resource.cooldown.min", 7);
		}
		RESOURCE_COOLDOWN_MAX = plugin.getConfig().getInt("resource.cooldown.max");
		if (RESOURCE_COOLDOWN_MAX < 0) {
			RESOURCE_COOLDOWN_MAX = 0;
			plugin.getConfig().set("resource.cooldown.max", 7);
		}
		RESOURCE_AMOUNT_MIN = plugin.getConfig().getInt("resource.amount.min");
		if (RESOURCE_AMOUNT_MIN < 0) {
			RESOURCE_AMOUNT_MIN = 0;
			plugin.getConfig().set("resource.amount.min", 7);
		}
		RESOURCE_AMOUNT_MAX = plugin.getConfig().getInt("resource.amount.max");
		if (RESOURCE_AMOUNT_MAX < 0) {
			RESOURCE_AMOUNT_MAX = 0;
			plugin.getConfig().set("resource.amount.max", 7);
		}
		plugin.saveConfig();
	}

}
