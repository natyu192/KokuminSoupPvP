package me.nucha.souppvp.listener.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.nucha.kokumin.coin.Coin;
import me.nucha.kokumin.utils.CustomItem;
import me.nucha.souppvp.SoupPvPPlugin;
import me.nucha.souppvp.animation.PerkUpgradeAnimation;
import me.nucha.souppvp.language.LanguageManager;
import me.nucha.souppvp.perk.Perk;
import me.nucha.souppvp.perk.PerkManager;
import me.nucha.souppvp.util.CooldownUtil;
import me.nucha.souppvp.util.PlayerDataUtil;

public class GuiShop implements Listener {

	public GuiShop() {
	}

	public static void open(Player p) {
		open(p, false);
	}

	public static void open(Player p, boolean force) {
		if (!force && CooldownUtil.hasCooldown(p, "gui-shop")) {
			p.sendMessage("§6" + CooldownUtil.getCooldownExpireSeconds(p, 1, "gui-shop") +
					"§c秒後にお試しください");
			return;
		}
		Inventory gui = Bukkit.createInventory(null, 27, "§aPerk Shop");
		for (Perk perk : PerkManager.getPerks()) {
			ItemStack icon = perk.getIcon();
			ItemMeta meta = icon.getItemMeta();
			meta.setDisplayName("§c" + perk.getName());
			List<String> lore = new ArrayList<String>();
			lore.addAll(perk.getDescription());
			if (perk.getLevel(p) >= 1) {
				meta.setDisplayName("§a" + perk.getName());
			}
			lore.add(" ");
			lore.addAll(Arrays.asList(perk.getMoreInfo(p)));
			meta.setLore(lore);
			icon.setItemMeta(meta);
			gui.setItem(perk.getGuiSlot(), icon);
		}
		CustomItem comingsoon = new CustomItem(Material.IRON_FENCE, 1, "§7Coming soon...");
		gui.setItem(14, comingsoon);
		gui.setItem(16, comingsoon);
		p.openInventory(gui);
		CooldownUtil.setCooldown(p, 1, "gui-shop");
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		if (event.getClickedInventory() != null && p.getOpenInventory().getTopInventory() != null) {
			if (p.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("§aPerk Shop")) {
				event.setCancelled(true);
				if (event.getClickedInventory().equals(p.getOpenInventory().getTopInventory())) {
					int slot = event.getSlot();
					for (Perk perk : PerkManager.getPerks()) {
						if (slot == perk.getGuiSlot()) {
							int level = perk.getLevel(p);
							if (level == perk.getUpgradeCosts().length) {
								p.sendMessage(LanguageManager.get(p, "perk.gui.is-maxed"));
								return;
							}
							int cost = perk.getUpgradeCosts()[level];
							if (Coin.hasCoin(p, cost)) {
								p.playSound(p.getLocation(), Sound.LEVEL_UP, 1f, 1f);
								Coin.takeCoin(p, cost,
										LanguageManager.get(p, "perk.gui.upgrading-perk").replaceAll("%perk", perk.getName()));
								PlayerDataUtil.add(p, "perks." + perk.getId());
								new PerkUpgradeAnimation(p.getLocation().clone().add(0, 1, 0))
										.runTaskTimer(SoupPvPPlugin.getInstance(), 0L, 5L);
								if (level == perk.getUpgradeCosts().length) {
									for (Player all : Bukkit.getOnlinePlayers()) {
										all.sendMessage("§8[§aFFA§8] §r" + LanguageManager.get(all, "perk.gui.max-unlocked")
												.replaceAll("%perk", perk.getName()).replaceAll("%player", p.getDisplayName()));
									}
									return;
								} else {
									open(p, true);
								}
							} else {
								int needed = cost - Coin.getCoin(p);
								p.sendMessage(LanguageManager.get(p, "not-enough-coin").replaceAll("%coin", String.valueOf(needed)));
								p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
							}
						}
					}
					if (slot == 14 || slot == 16) {
						p.playSound(p.getLocation(), Sound.VILLAGER_YES, 1f, 1f);
						p.sendMessage(LanguageManager.get(p, "coming-soon"));
					}
				}
			}
		}
	}

}
