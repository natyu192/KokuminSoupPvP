package me.nucha.souppvp.util;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class LocationUtil {

	private static JavaPlugin plugin;
	private static HashMap<String, Location> locationMap;

	public static void init(JavaPlugin plugin) {
		LocationUtil.plugin = plugin;
		locationMap = new HashMap<>();
		ConfigurationSection section = plugin.getConfig().getConfigurationSection("location");
		if (section != null) {
			for (String name : section.getKeys(false)) {
				double x = plugin.getConfig().getDouble("location." + name + ".x");
				double y = plugin.getConfig().getDouble("location." + name + ".y");
				double z = plugin.getConfig().getDouble("location." + name + ".z");
				float yaw = (float) plugin.getConfig().getDouble("location." + name + ".yaw");
				float pitch = (float) plugin.getConfig().getDouble("location." + name + ".pitch");
				String world = plugin.getConfig().getString("location." + name + ".world");
				Location location = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
				locationMap.put(name, location);
			}
		}
	}

	public static boolean isSet(String name) {
		return locationMap.containsKey(name);
	}

	public static Location get(String name) {
		return locationMap.get(name);
	}

	public static void set(String name, Location location) {
		locationMap.put(name, location);
		plugin.getConfig().set("location." + name + ".x", location.getBlockX() + 0.5d);
		plugin.getConfig().set("location." + name + ".y", location.getBlockY());
		plugin.getConfig().set("location." + name + ".z", location.getBlockZ() + 0.5d);
		plugin.getConfig().set("location." + name + ".yaw", location.getYaw());
		plugin.getConfig().set("location." + name + ".pitch", location.getPitch());
		plugin.getConfig().set("location." + name + ".world", location.getWorld().getName());
		plugin.saveConfig();
	}

	public static Location get(FileConfiguration config, String path) {
		double x = config.getDouble(path + ".x");
		double y = config.getDouble(path + ".y");
		double z = config.getDouble(path + ".z");
		float yaw = (float) config.getDouble(path + ".yaw");
		float pitch = (float) config.getDouble(path + ".pitch");
		String world = config.getString(path + ".world");
		return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
	}

	public static void set(FileConfiguration config, String path, Location location) {
		config.set(path + ".x", location.getBlockX() + 0.5d);
		config.set(path + ".y", location.getBlockY());
		config.set(path + ".z", location.getBlockZ() + 0.5d);
		config.set(path + ".yaw", location.getYaw());
		config.set(path + ".pitch", location.getPitch());
		config.set(path + ".world", location.getWorld().getName());
	}

	public static Location locationFromString(String locationString, boolean includingAngles) {
		String[] splitted = locationString.split(",");
		double x = Double.valueOf(splitted[0]);
		double y = Double.valueOf(splitted[1]);
		double z = Double.valueOf(splitted[2]);
		if (includingAngles) {
			float yaw = Float.valueOf(splitted[3]);
			float pitch = Float.valueOf(splitted[4]);
			String world = splitted[5];
			return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
		} else {
			String world = splitted[3];
			return new Location(Bukkit.getWorld(world), x, y, z);
		}
	}

	public static String locationToString(Location location, boolean includingAngles) {
		String world = location.getWorld().getName();
		if (includingAngles) {
			double x = location.getX();
			double y = location.getY();
			double z = location.getZ();
			float yaw = location.getYaw();
			float pitch = location.getPitch();
			return x + "," + y + "," + z + "," + yaw + "," + pitch + "," + world;
		} else {
			int x = location.getBlockX();
			int y = location.getBlockY();
			int z = location.getBlockZ();
			return x + "," + y + "," + z + "," + world;
		}
	}
}
