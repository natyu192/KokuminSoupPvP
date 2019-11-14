package me.nucha.souppvp.language;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.nucha.souppvp.SoupPvPPlugin;

public class LanguageManager {

	public static HashMap<String, Language> languages;
	public static HashMap<Language, FileConfiguration> ymls;
	private static File languageFile;
	private static FileConfiguration languageData;

	public static void init() {
		ymls = new HashMap<>();
		SoupPvPPlugin plugin = SoupPvPPlugin.getInstance();
		for (Language language : Language.values()) {
			String fileName = language.name().toLowerCase() + ".yml";
			FileConfiguration yml = YamlConfiguration.loadConfiguration(plugin.getResource(fileName));
			Player nucha = Bukkit.getPlayer("Nucha");
			if (nucha != null) {
				nucha.sendMessage("put " + fileName);
			}
			ymls.put(language, yml);
		}
		languageFile = new File(plugin.getDataFolder() + "/language.yml");
		if (!languageFile.exists()) {
			try {
				languageFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		languages = new HashMap<>();
		languageData = YamlConfiguration.loadConfiguration(languageFile);
		if (languageData.contains("languages")) {
			for (String uuid : languageData.getConfigurationSection("languages").getKeys(false)) {
				Language lang = Language.parse(languageData.getString(uuid));
				languages.put(uuid, lang);
				Player nucha = Bukkit.getPlayer("Nucha");
				if (nucha != null) {
					nucha.sendMessage("put lang set " + uuid + "," + lang.name());
				}
			}
		}
	}

	public static void shutdown() {
		for (String uuid : languages.keySet()) {
			languageData.set(uuid, languages.get(uuid).name());
		}
		save();
	}

	public static String get(Player p, String path) {
		return get(getLanguage(p), path);
	}

	public static String get(Language l, String path) {
		if (!ymls.get(l).contains(path)) {
			return path;
		}
		return ChatColor.translateAlternateColorCodes('&', ymls.get(l).getString(path));
	}

	public static Language getLanguage(Player p) {
		String uuid = p.getUniqueId().toString();
		if (!languages.containsKey(uuid)) {
			languages.put(uuid, Language.JAPANESE);
		}
		return languages.get(uuid);
	}

	public static void setLanguage(Player p, Language language) {
		languages.put(p.getUniqueId().toString(), language);
	}

	public static void save() {
		try {
			languageData.save(languageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
