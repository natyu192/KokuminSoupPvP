package me.nucha.souppvp.kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.nucha.souppvp.SoupPvPPlugin;
import me.nucha.souppvp.player.PlayerUtil;

public class KitManager {

	private static List<Kit> kits;
	private static HashMap<Player, Kit> kitSelected;
	private static HashMap<String, Integer> kitid_cost;
	private static HashMap<String, Boolean> kitid_enabled;
	private static HashMap<String, Boolean> kitid_forceunlocked;

	public static void init(SoupPvPPlugin plugin) {
		kits = new ArrayList<>();
		registerKit(new KitStandard());
		registerKit(new KitKnight());
		registerKit(new KitSumoWrestler());
		kitSelected = new HashMap<>();
		kitid_cost = new HashMap<>();
		kitid_enabled = new HashMap<>();
		kitid_forceunlocked = new HashMap<>();
		if (plugin.getConfig().isSet("cache.kits-selected")) { // load cache
			for (String uuidString : plugin.getConfig().getConfigurationSection("cache.kits-selected").getKeys(false)) {
				Player player = Bukkit.getPlayer(UUID.fromString(uuidString));
				if (player != null) {
					String kitId = plugin.getConfig().getString("cache.kits-selected." + uuidString);
					Kit kit = getKitById(kitId);
					if (kit != null) {
						kitSelected.put(player, kit);
					}
				}
			}
		}
		for (Kit kit : kits) {
			String id = kit.getId();
			int cost = kit.getCost();
			if (plugin.getConfig().isSet("kits." + id + ".cost")) {
				cost = plugin.getConfig().getInt("kits." + id + ".cost");
			}
			kitid_cost.put(id, cost);
			boolean enabled = true;
			if (plugin.getConfig().isSet("kits." + id + ".enabled")) {
				enabled = plugin.getConfig().getBoolean("kits." + id + ".enabled");
			}
			kitid_enabled.put(id, enabled);
			boolean forceunlocked = false;
			if (plugin.getConfig().isSet("kits." + id + ".force-unlocked")) {
				forceunlocked = plugin.getConfig().getBoolean("kits." + id + ".force-unlocked");
			}
			kitid_forceunlocked.put(id, forceunlocked);
		}
	}

	public static void shutdown() {
		FileConfiguration config = SoupPvPPlugin.getInstance().getConfig();
		for (String id : kitid_cost.keySet()) {
			config.set("kits." + id + ".cost", kitid_cost.get(id));
		}
		for (String id : kitid_enabled.keySet()) {
			config.set("kits." + id + ".enabled", kitid_enabled.get(id));
		}
		for (String id : kitid_forceunlocked.keySet()) {
			config.set("kits." + id + ".force-unlocked", kitid_forceunlocked.get(id));
		}
		config.set("cache.kits-selected", null); // reset
		for (Player player : kitSelected.keySet()) {
			config.set("cache.kits-selected." + player.getUniqueId().toString(), kitSelected.get(player).getId());
		}
	}

	public static List<Kit> getKits() {
		return kits;
	}

	public static Kit getKitById(String id) {
		for (Kit kit : kits) {
			if (kit.getId().equalsIgnoreCase(id)) {
				return kit;
			}
		}
		return null;
	}

	public static void registerKit(Kit kit) {
		kits.add(kit);
	}

	public static void removeKit(String id) {
		if (getKitById(id) == null) {
			return;
		}
		kits.remove(getKitById(id));
	}

	public static boolean isExistKit(String id) {
		return getKitById(id) != null;
	}

	public static boolean selectKit(Player p, String id) {
		if (!isExistKit(id)) {
			return false;
		}
		Kit kit = getKitById(id);
		if (hasKitSelected(p)) {
			unselectKit(p);
		}
		kit.give(p);
		kitSelected.put(p, kit);
		return true;
	}

	public static boolean hasKitSelected(Player p) {
		return kitSelected.containsKey(p);
	}

	public static boolean isKitSelected(Player p, String id) {
		if (hasKitSelected(p)) {
			return getKitSelected(p).getId().equalsIgnoreCase(id);
		}
		return false;
	}

	public static Kit getKitSelected(Player p) {
		return kitSelected.get(p);
	}

	public static void unselectKit(Player p) {
		if (!hasKitSelected(p)) {
			return;
		}
		PlayerUtil.clearInventory(p);
		PlayerUtil.removePotionEfects(p);
		kitSelected.remove(p);
	}

	public static int getCost(String id) {
		Kit kit = getKitById(id);
		if (kit != null) {
			return kit.getCost();
		}
		return 0;
	}

	public static void setCost(String id, int cost) {
		if (kitid_cost.containsKey(id.toLowerCase())) {
			kitid_cost.put(id.toLowerCase(), cost);
		}
	}

	public static boolean isEnabled(String id) {
		if (kitid_enabled.containsKey(id.toLowerCase())) {
			return kitid_enabled.get(id.toLowerCase());
		}
		return false;
	}

	public static void setEnabled(String id, boolean flag) {
		if (kitid_enabled.containsKey(id.toLowerCase())) {
			kitid_enabled.put(id.toLowerCase(), flag);
		}
	}

	public static boolean isForceUnlocked(String id) {
		if (kitid_forceunlocked.containsKey(id.toLowerCase())) {
			return kitid_forceunlocked.get(id.toLowerCase());
		}
		return false;
	}

	public static void setForceUnlocked(String id, boolean flag) {
		if (kitid_forceunlocked.containsKey(id.toLowerCase())) {
			kitid_forceunlocked.put(id.toLowerCase(), flag);
		}
	}

}
