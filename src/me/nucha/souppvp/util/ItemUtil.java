package me.nucha.souppvp.util;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;

import me.nucha.souppvp.SoupPvPPlugin;

public class ItemUtil {

	private static String undroppableTag;

	public static void init(SoupPvPPlugin plugin) {
		undroppableTag = "§7§o- This item can't be dropped.";
		plugin.getServer().getPluginManager().registerEvents(new UndroppableListener(), plugin);
	}

	public static ItemStack setUnbreakable(ItemStack item, boolean flag) {
		if (item == null || item.getType().equals(Material.AIR)) {
			return item;
		}
		ItemMeta meta = item.getItemMeta();
		meta.spigot().setUnbreakable(flag);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack setUndroppable(ItemStack item, boolean flag) {
		if (item == null || item.getType().equals(Material.AIR)) {
			return item;
		}
		ItemMeta meta = item.getItemMeta();
		if (flag && !isUndroppable(item)) {
			List<String> lore = Lists.newArrayList();
			if (meta.hasLore()) {
				lore = meta.getLore();
			}
			lore.add(undroppableTag);
			meta.setLore(lore);
		}
		if (!flag && isUndroppable(item)) {
			List<String> lore = Lists.newArrayList();
			if (meta.hasLore()) {
				lore = meta.getLore();
			}
			lore.remove(undroppableTag);
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		return item;
	}

	public static boolean isUndroppable(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		return (meta.hasLore() && meta.getLore().contains(undroppableTag));
	}

	public static String getDisplayName(ItemStack item) {
		if (item != null && !item.getType().equals(Material.AIR) && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
			return item.getItemMeta().getDisplayName();
		}
		return "";
	}

	public static class UndroppableListener implements Listener {

		@EventHandler
		public void onDrop(PlayerDropItemEvent event) {
			Player p = event.getPlayer();
			ItemStack item = event.getItemDrop().getItemStack();
			if (isUndroppable(item)) {
				event.setCancelled(true);
				p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1f, 1f);
			}
		}
	}
}
