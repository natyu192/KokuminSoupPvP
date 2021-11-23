package me.nucha.souppvp.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;

import me.nucha.souppvp.perk.Perk;
import me.nucha.souppvp.perk.PerkManager;

public class PlayerDataUtil {

	private static File playerDataFile;
	private static FileConfiguration playerData;
	public static FileConfiguration playerDataOld;
	public static HashMap<String, Object> defaults;

	public static void init(JavaPlugin plugin) {
		playerDataFile = new File(plugin.getDataFolder() + "/playerdata.yml");
		if (!playerDataFile.exists()) {
			try {
				playerDataFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		playerData = YamlConfiguration.loadConfiguration(playerDataFile);
		playerDataOld = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/playerdata-old.yml"));
		plugin.saveConfig();
		defaults = new HashMap<>();
		defaults.put("xp", 0);
		defaults.put("kills", 0);
		defaults.put("deaths", 0);
		defaults.put("left-clicks", 0);
		defaults.put("soups-used", 0);
		for (Perk perk : PerkManager.getPerks()) {
			defaults.put("perks." + perk.getId(), 0);
		}
		defaults.put("kits-purchased", new ArrayList<String>());
	}

	public static void setupPlayer(Player p) {
		for (String key : defaults.keySet()) {
			setDefault(p, key, defaults.get(key));
		}
		// save();
	}

	public static void setDefault(Player p, String category, Object value) {
		String path = p.getUniqueId().toString() + "." + category;
		if (!playerData.isSet(path)) {
			playerData.set(path, value);
		}
	}

	public static void add(Player p, String category) {
		add(p, category, 1);
	}

	public static void add(Player p, String category, int amount) {
		int a = 0;
		String path = p.getUniqueId().toString() + "." + category;
		if (playerData.isSet(path) && StringUtils.isNumeric(playerData.getString(path))) {
			a = playerData.getInt(path);
		}
		a += amount;
		playerData.set(path, a);
		// save();
	}

	public static void take(Player p, String category) {
		take(p, category, 1);
	}

	public static void take(Player p, String category, int amount) {
		int a = 0;
		String path = p.getUniqueId().toString() + "." + category;
		if (playerData.isSet(path) && StringUtils.isNumeric(playerData.getString(path))) {
			a = playerData.getInt(path);
		}
		a -= amount;
		if (a < 0) {
			a = 0;
		}
		playerData.set(path, a);
		// save();
	}

	public static int getInt(OfflinePlayer p, String category) {
		return playerData.getInt(p.getUniqueId().toString() + "." + category);
	}

	public static String getString(OfflinePlayer p, String category) {
		return playerData.getString(p.getUniqueId().toString() + "." + category);
	}

	public static boolean getBoolean(OfflinePlayer p, String category) {
		return playerData.getBoolean(p.getUniqueId().toString() + "." + category);
	}

	public static List<String> getStringList(OfflinePlayer p, String category) {
		return playerData.getStringList(p.getUniqueId().toString() + "." + category);
	}

	public static Object get(OfflinePlayer p, String category) {
		return playerData.get(p.getUniqueId().toString() + "." + category);
	}

	public static void set(OfflinePlayer p, String category, Object value) {
		playerData.set(p.getUniqueId().toString() + "." + category, value);
		// save();
	}

	public static void addToList(OfflinePlayer p, String category, String value) {
		List<String> a = Lists.newArrayList();
		String path = p.getUniqueId().toString() + "." + category;
		if (playerData.isSet(path) && playerData.get(path) instanceof List) {
			a = playerData.getStringList(path);
		}
		a.add(value);
		playerData.set(path, a);
	}

	public static void removeFromList(OfflinePlayer p, String category, String value) {
		List<String> a = Lists.newArrayList();
		String path = p.getUniqueId().toString() + "." + category;
		if (playerData.isSet(path) && playerData.get(path) instanceof List) {
			a = playerData.getStringList(path);
		}
		a.remove(value);
		playerData.set(path, a);
	}

	public static void save() {
		try {
			playerData.save(playerDataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
