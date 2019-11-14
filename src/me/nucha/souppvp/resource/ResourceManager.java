package me.nucha.souppvp.resource;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import me.nucha.souppvp.SoupPvPPlugin;
import me.nucha.souppvp.language.LanguageManager;
import me.nucha.souppvp.util.ConfigUtil;
import me.nucha.souppvp.util.LocationUtil;

public class ResourceManager {

	private static File resourceFile;
	private static FileConfiguration resourceYml;
	private static Set<Location> resources;
	private static HashMap<Location, Material> resourcesRegenerating;
	private static Set<Player> seeing;

	public static void init(SoupPvPPlugin plugin) {
		resources = Sets.newHashSet();
		resourcesRegenerating = new HashMap<>();
		resourceFile = new File(plugin.getDataFolder() + "/resource.yml");
		if (!resourceFile.exists()) {
			try {
				resourceFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		resourceYml = YamlConfiguration.loadConfiguration(resourceFile);
		if (resourceYml.isSet("resources")) {
			List<String> resourceLocations = resourceYml.getStringList("resources");
			for (String resourceLocation : resourceLocations) {
				Location location = LocationUtil.locationFromString(resourceLocation, false);
				resources.add(location);
			}
		}
		seeing = Sets.newHashSet();
	}

	public static void shutdown() {
		for (Location location : resourcesRegenerating.keySet()) {
			location.getBlock().setType(resourcesRegenerating.get(location));
		}
		List<String> locationStrings = Lists.newArrayList();
		for (Location location : resources) {
			locationStrings.add(LocationUtil.locationToString(location, false));
		}
		resourceYml.set("resources", locationStrings);
		save();
	}

	public static void registerResource(Location resource) {
		if (!resources.contains(resource)) {
			resources.add(resource);
		}
	}

	public static void unregisterResource(Location resource) {
		if (resources.contains(resource)) {
			resources.remove(resource);
		}
	}

	public static void save() {
		try {
			resourceYml.save(resourceFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Set<Location> getResources() {
		return resources;
	}

	public static boolean isResource(Location location) {
		return resources.contains(location);
	}

	public static boolean isResource(Block b) {
		if (!isResource(b.getLocation())) {
			return false;
		}
		Material type = b.getType();
		return type.equals(Material.RED_MUSHROOM) || type.equals(Material.BROWN_MUSHROOM) || type.equals(Material.LOG)
				|| type.equals(Material.LOG_2);
	}

	public static void addResource(Block b) {
		resources.add(b.getLocation());
		Bukkit.getScheduler().runTask(SoupPvPPlugin.getInstance(), new BukkitRunnable() {
			@Override
			public void run() {
				for (Player p : seeing) {
					p.sendBlockChange(b.getLocation(), Material.DIAMOND_BLOCK, (byte) 0);
				}
			}
		});
	}

	public static void removeResource(Block b) {
		resources.remove(b.getLocation());
		Bukkit.getScheduler().runTask(SoupPvPPlugin.getInstance(), new BukkitRunnable() {
			@Override
			public void run() {
				for (Player p : seeing) {
					p.sendBlockChange(b.getLocation(), b.getType(), b.getData());
				}
			}
		});
	}

	public static String getResourceName(Material material) {
		if (material.equals(Material.BROWN_MUSHROOM)) {
			return "茶キノコ";
		}
		if (material.equals(Material.RED_MUSHROOM)) {
			return "赤キノコ";
		}
		if (material.equals(Material.LOG) || material.equals(Material.LOG_2)) {
			return "木材";
		}
		return "何か";
	}

	public static void gatherResource(Player p, Block b) {
		if (isResource(b)) {
			Location l = b.getLocation();
			if (resourcesRegenerating.keySet().contains(l)) {
				return;
			}
			Material resourceType = b.getType();
			resourcesRegenerating.put(l, resourceType);
			String resourceName = ResourceManager.getResourceName(resourceType);
			Random random = new Random();
			int amountMax = ConfigUtil.RESOURCE_AMOUNT_MAX;
			int amountMin = ConfigUtil.RESOURCE_AMOUNT_MIN;
			int amount = random.nextInt(amountMax - amountMin) + amountMin;
			p.getInventory().addItem(new ItemStack(resourceType, amount));
			p.sendMessage("§8[§aFFA§8] §r" + LanguageManager.get(p, "ffa.earn-resource")
					.replaceAll("%resource", resourceName).replaceAll("amount", String.valueOf(amount)));
			b.setType(Material.AIR);
			BukkitRunnable taskSound = new BukkitRunnable() {
				int i = amount;

				@Override
				public void run() {
					if (i > 0) {
						float pitch = 0.5f;
						if (Math.random() < 0.5) {
							pitch = 0.75f;
						}
						b.getWorld().playSound(l, Sound.ITEM_PICKUP, 1f, pitch);
					} else {
						cancel();
					}
					i--;
				}
			};
			taskSound.runTaskTimer(SoupPvPPlugin.getInstance(), 0L, 1L);
			int cooldownMax = ConfigUtil.RESOURCE_COOLDOWN_MAX;
			int cooldownMin = ConfigUtil.RESOURCE_COOLDOWN_MIN;
			int cooldown = random.nextInt(cooldownMax - cooldownMin) + cooldownMin;
			BukkitRunnable taskRegeneration = new BukkitRunnable() {
				@Override
				public void run() {
					for (Player all : Bukkit.getOnlinePlayers()) {
						if (all.getWorld().equals(l.getWorld()) && all.getLocation().distance(l) < 256) {
							all.playEffect(l, Effect.STEP_SOUND, resourceType.getId());
						}
					}
					b.setType(resourceType);
					resourcesRegenerating.remove(l);
				}
			};
			taskRegeneration.runTaskLater(SoupPvPPlugin.getInstance(), cooldown * 20L);
		}
	}

	public static boolean isSeeing(Player p) {
		return seeing.contains(p);
	}

	public static void displayResources(Player p) {
		seeing.add(p);
		for (Location resource : resources) {
			p.sendBlockChange(resource, Material.DIAMOND_BLOCK, (byte) 0);
		}
	}

	public static void undisplayResources(Player p) {
		seeing.remove(p);
		for (Location resource : resources) {
			p.sendBlockChange(resource, resource.getBlock().getType(), resource.getBlock().getData());
		}
	}

}
