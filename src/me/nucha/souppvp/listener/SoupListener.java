package me.nucha.souppvp.listener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.nucha.souppvp.SoupPvPPlugin;
import me.nucha.souppvp.language.LanguageManager;
import me.nucha.souppvp.util.ConfigUtil;
import me.nucha.souppvp.util.PlayerDataUtil;
import me.nucha.souppvp.util.ScoreboardUtils;

public class SoupListener implements Listener {

	private SoupPvPPlugin plugin;
	private String date;
	public static HashMap<String, Integer> dailySoupXp = new HashMap<>();;

	public SoupListener(SoupPvPPlugin plugin) {
		this.plugin = plugin;
		dailySoupXp.clear();
		for (Player all : Bukkit.getOnlinePlayers()) {
			dailySoupXp.put(all.getUniqueId().toString(), 0);
		}
		if (plugin.getConfig().isSet("cache.daily-soup-xp")) { // load cache
			for (String uuidString : plugin.getConfig().getConfigurationSection("cache.daily-soup-xp").getKeys(false)) {
				dailySoupXp.put(uuidString, plugin.getConfig().getInt("cache.daily-soup-xp." + uuidString));
			}
		}
		BukkitRunnable task = new BukkitRunnable() {
			@Override
			public void run() {
				String now = new SimpleDateFormat("MM/dd").format(new Date());
				if (date == null) {
					date = now;
					return;
				}
				if (now.equalsIgnoreCase(date)) {
					return;
				}
				date = now;
				dailySoupXp.clear();
				Bukkit.broadcastMessage("§b--------------------");
				Bukkit.broadcastMessage("§b§l日付が変わり、デイリースープXPがリセットされました！戦いましょう！");
				Bukkit.broadcastMessage("§b--------------------");
				for (Player all : Bukkit.getOnlinePlayers()) {
					dailySoupXp.put(all.getUniqueId().toString(), 0);
				}
			}
		};
		task.runTaskTimer(plugin, 0L, 20L);
	}

	public static void shutdown() {
		FileConfiguration config = SoupPvPPlugin.getInstance().getConfig();
		config.set("cache.daily-soup-xp", null); // reset
		for (String uuidString : dailySoupXp.keySet()) {
			config.set("cache.daily-soup-xp." + uuidString, dailySoupXp.get(uuidString));
		}
	}

	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent event) {
		event.setFoodLevel(20);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		String uuid = event.getPlayer().getUniqueId().toString();
		if (!dailySoupXp.containsKey(uuid)) {
			dailySoupXp.put(uuid, 0);
		}
	}

	@EventHandler
	public void onEatSoup(PlayerInteractEvent event) {
		if ((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
				&& event.getItem() != null && event.getItem().getType().equals(Material.MUSHROOM_SOUP)) {
			event.setCancelled(true);
			Player p = event.getPlayer();
			if (p.getHealth() == p.getMaxHealth()) {
				return;
			}
			// p.playSound(p.getLocation(), Sound.BURP, 1f, 1f);
			double healedHealth = p.getHealth() + ConfigUtil.AMOUNT_OF_SOUP_HEALING;
			if (healedHealth >= p.getMaxHealth()) {
				healedHealth = p.getMaxHealth();
			}
			p.setHealth(healedHealth);
			p.setItemInHand(new ItemStack(Material.BOWL));
			PlayerDataUtil.add(p, "soups-used");
			int dailySoup = 0;
			String uuid = p.getUniqueId().toString();
			if (dailySoupXp.containsKey(uuid)) {
				dailySoup = dailySoupXp.get(uuid);
			}
			if (dailySoup < 1000) {
				dailySoup++;
				dailySoupXp.put(uuid, dailySoup);
				PlayerDataUtil.add(p, "xp");
				ScoreboardUtils.updateXp(p);
				if (dailySoup == 1000) {
					p.sendMessage(LanguageManager.get(p, "ffa.daily-soup-completed"));
				}
			}
		}
	}

	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		Item itemDropped = event.getItemDrop();
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				if (itemDropped != null) {
					itemDropped.remove();
				}
			}
		};
		runnable.runTaskLater(plugin, 60L);
	}

}
