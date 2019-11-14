package me.nucha.souppvp.listener.gui;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.common.collect.Lists;

import me.nucha.kokumin.coin.Coin;
import me.nucha.souppvp.language.LanguageManager;
import me.nucha.souppvp.listener.SoupListener;
import me.nucha.souppvp.util.PlayerDataUtil;
import me.nucha.souppvp.util.cooldown.CooldownUtil;

public class GuiStats implements Listener {

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		if (p.getOpenInventory().getTopInventory() != null) {
			if (p.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("§aYour stats")) {
				event.setCancelled(true);
			}
		}
	}

	public static void open(Player p) {
		if (CooldownUtil.hasCooldown(p, "gui-stats")) {
			double cooldown = CooldownUtil.getCooldownExpireSeconds(p, 1, "gui-stats");
			p.sendMessage(LanguageManager.get(p, "cooldown").replaceAll("%cooldown", String.valueOf(cooldown)));
			return;
		}
		Inventory gui = Bukkit.createInventory(null, 27, "§aYour stats");
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setOwner(p.getName());
		skullMeta.setDisplayName("§e" + p.getName() + "§a's Stats");
		List<String> skullLore = Lists.newArrayList();
		skullLore.add("");
		skullLore.add("§e戦闘系");
		skullLore.add("  §7キル数: §a" + PlayerDataUtil.getInt(p, "kills"));
		skullLore.add("  §7死亡数: §a" + PlayerDataUtil.getInt(p, "deaths"));
		skullLore.add("");
		skullLore.add("§eポイント系");
		skullLore.add("  §6所持コイン数: §e" + Coin.getCoin(p));
		skullLore.add("  §3経験値: §b" + PlayerDataUtil.getInt(p, "xp"));
		skullLore.add("  §6デイリースープ§3経験値ボーナス: §b" + SoupListener.dailySoupXp.get(p.getUniqueId().toString()) + "/1000");
		skullLore.add("");
		skullLore.add("§eその他");
		skullLore.add("  §7左クリック数: §a" + PlayerDataUtil.getInt(p, "left-clicks"));
		skullLore.add("  §7スープ使用数: §a" + PlayerDataUtil.getInt(p, "soups-used"));
		skullMeta.setLore(skullLore);
		skull.setItemMeta(skullMeta);
		gui.setItem(12, skull);
		ItemStack paper = new ItemStack(Material.PAPER);
		ItemMeta paperMeta = paper.getItemMeta();
		paperMeta.setDisplayName("§e説明");
		List<String> paperLore = Lists.newArrayList();
		paperLore.add("");
		paperLore.add("§eコイン§aについて:");
		paperLore.add("  §eコイン§7は§cプレイヤーをキルする§7ことで");
		paperLore.add("  §7集めることができます。");
		paperLore.add("  §e現在は使い道がありません§7が、今後実装していく予定です。");
		paperLore.add("");
		paperLore.add("§b経験値§aについて:");
		paperLore.add("  §b経験値§7は§cプレイヤーをキル§7したり");
		paperLore.add("  §6スープを飲む§7ことで集めることができます。");
		paperLore.add("  §7消費することはできず、このサーバーで§eどのくらい");
		paperLore.add("  §e遊んでいるか§7の目印になります。");
		paperMeta.setLore(paperLore);
		paper.setItemMeta(paperMeta);
		gui.setItem(14, paper);
		p.openInventory(gui);
	}

}
