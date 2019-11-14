package me.nucha.souppvp.arenafight.arena;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.collect.Lists;

import me.nucha.souppvp.SoupPvPPlugin;
import me.nucha.souppvp.kit.Kit;
import me.nucha.souppvp.kit.KitManager;
import me.nucha.souppvp.util.LocationUtil;

public class ArenaManager {

	private static List<Arena> arenas;
	private static File arenaFile;
	private static FileConfiguration arenaYml;

	public static void init(SoupPvPPlugin plugin) {
		arenas = Lists.newArrayList();
		arenaFile = new File(plugin.getDataFolder() + "/arena.yml");
		if (!arenaFile.exists()) {
			try {
				arenaFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		arenaYml = YamlConfiguration.loadConfiguration(arenaFile);
		if (arenaYml.isSet("arenas")) {
			for (String arenaId : arenaYml.getConfigurationSection("arenas").getKeys(false)) {
				String arenaName = arenaYml.isSet("arenas." + arenaId + ".name") ? arenaYml.getString("arenas." + arenaId + ".name")
						: arenaId;
				Location spawn1 = LocationUtil.get(arenaYml, "arenas." + arenaId + ".spawn-1");
				Location spawn2 = LocationUtil.get(arenaYml, "arenas." + arenaId + ".spawn-2");
				Kit kit = KitManager.getKitById(arenaYml.getString("arenas." + arenaId + ".kit"));
				boolean enabled = arenaYml.getBoolean("arenas." + arenaId + ".enabled");
				Arena arena = new Arena(arenaId, arenaName, spawn1, spawn2, kit, enabled);
				arenas.add(arena);
			}
		}
	}

	public static void shutdown() {
		for (Arena arena : arenas) {
			arenaYml.set("arenas." + arena.getId() + ".name", arena.getName());
			LocationUtil.set(arenaYml, "arenas." + arena.getId() + ".spawn-1", arena.getSpawn1());
			LocationUtil.set(arenaYml, "arenas." + arena.getId() + ".spawn-2", arena.getSpawn2());
			arenaYml.set("arenas." + arena.getId() + ".kit", arena.getKit().getId());
			arenaYml.set("arenas." + arena.getId() + ".enabled", arena.isEnabled());
		}
		save();
	}

	public static void registerArena(Arena arena) {
		if (!arenas.contains(arena)) {
			arenas.add(arena);
		}
	}

	public static void unregisterArena(Arena arena) {
		if (arenas.contains(arena)) {
			arenas.remove(arena);
		}
	}

	public static void save() {
		try {
			arenaYml.save(arenaFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<Arena> getArenas() {
		return arenas;
	}

	public static Arena getRandomArena() {
		Random random = new Random();
		List<Arena> availableArenas = Lists.newArrayList();
		for (Arena arena : Lists.newArrayList(arenas)) {
			if (arena.isAvailable()) {
				availableArenas.add(arena);
			}
		}
		if (availableArenas.size() == 0) {
			return null;
		} else if (availableArenas.size() == 1) {
			return availableArenas.get(0);
		} else {
			return availableArenas.get(random.nextInt(availableArenas.size()));
		}
	}

	public static Arena getArenaById(String id) {
		for (Arena arena : arenas) {
			if (arena.getId().equalsIgnoreCase(id)) {
				return arena;
			}
		}
		return null;
	}

}
