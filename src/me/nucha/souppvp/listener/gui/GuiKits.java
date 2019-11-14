package me.nucha.souppvp.listener.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.nucha.kokumin.coin.Coin;
import me.nucha.souppvp.SoupPvPPlugin;
import me.nucha.souppvp.animation.KitUpgradeAnimation;
import me.nucha.souppvp.kit.Kit;
import me.nucha.souppvp.kit.KitManager;
import me.nucha.souppvp.language.LanguageManager;
import me.nucha.souppvp.listener.combattag.CombatTagListener;
import me.nucha.souppvp.player.PlayerState;
import me.nucha.souppvp.util.PlayerDataUtil;
import me.nucha.souppvp.util.cooldown.CooldownUtil;

public class GuiKits implements Listener {

	public static void open(Player p) {
		open(p, false);
	}

	public static void open(Player p, boolean force) {
		if (!force && CooldownUtil.hasCooldown(p, "gui-kits")) {
			p.sendMessage("§6" + CooldownUtil.getCooldownExpireSeconds(p, 1, "gui-kits") +
					"§c秒後にお試しください");
			return;
		}
		Inventory gui = Bukkit.createInventory(null, 9, "§aKits");
		for (Kit kit : KitManager.getKits()) {
			ItemStack icon = kit.getIcon();
			ItemMeta meta = icon.getItemMeta();
			List<String> lore = new ArrayList<String>();
			lore.add(" ");
			lore.addAll(kit.getDescription());
			lore.add(" ");
			String displayName = kit.getName();
			if (!kit.isEnabled()) {
				displayName = "§c" + displayName;
				lore.add("§c§nCurrently disabled");
			} else {
				if (kit.isForceUnlocked()) {
					displayName = "§d" + displayName;
					lore.add("§aUNLOCKED (全員が使えます！)");
				} else {
					if (kit.hasPurchasedBy(p)) {
						displayName = "§a" + displayName;
						lore.add("§aUNLOCKED");
					} else {
						displayName = "§c" + displayName;
						lore.add("§cLOCKED");
						lore.add("§6クリックすると §e" + kit.getCost() + "コイン §6で購入！");
					}
				}
			}
			meta.setDisplayName(displayName);
			meta.setLore(lore);
			icon.setItemMeta(meta);
			gui.setItem(kit.getGuiSlot(), icon);
		}
		p.openInventory(gui);
		CooldownUtil.setCooldown(p, 1, "gui-kits");
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		if (event.getClickedInventory() != null && p.getOpenInventory().getTopInventory() != null) {
			if (p.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("§aKits")) {
				event.setCancelled(true);
				if (event.getClickedInventory().equals(p.getOpenInventory().getTopInventory())) {
					int slot = event.getSlot();
					for (Kit kit : KitManager.getKits()) {
						if (slot == kit.getGuiSlot()) {
							if (!kit.isEnabled()) {
								p.sendMessage(LanguageManager.get(p, "kit.gui.unable-kit").replaceAll("%kit", kit.getName()));
								return;
							}
							if (kit.hasPurchasedBy(p) || kit.isForceUnlocked()) {
								if (!PlayerState.isState(p, PlayerState.IN_FFA)) {
									p.sendMessage(LanguageManager.get(p, "kit.gui.go-ffa-first"));
									p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
									return;
								}
								boolean success = KitManager.selectKit(p, kit.getId());
								if (success) {
									p.sendMessage(LanguageManager.get(p, "kit.gui.kit-selected").replaceAll("%kit", kit.getName()));
									p.closeInventory();
									CombatTagListener.sendRealBlocks(p);
								} else {
									p.sendMessage(LanguageManager.get(p, "kit.gui.kit-could-not-select").replaceAll("%kit", kit.getName()));
								}
								return;
							}
							int cost = KitManager.getCost(kit.getId());
							if (Coin.hasCoin(p, cost)) {
								p.playSound(p.getLocation(), Sound.LEVEL_UP, 1f, 1f);
								Coin.takeCoin(p, cost, LanguageManager.get(p, "kit.gui.purchasing-kit"));
								PlayerDataUtil.addToList(p, "kits-purchased", kit.getId());
								new KitUpgradeAnimation(p.getLocation().clone().add(0, 1, 0))
										.runTaskTimer(SoupPvPPlugin.getInstance(), 0L, 5L);
								p.closeInventory();
								Bukkit.broadcastMessage("§8[§aFFA§8] §r" + LanguageManager.get(p, "kit.gui.kit-unlock-broadcast")
										.replaceAll("%player", p.getDisplayName()).replaceAll("%kit", kit.getName()));
							} else {
								int needed = cost - Coin.getCoin(p);
								p.sendMessage("§6" + needed + " §cコイン足りません！");
								p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
							}
						}
					}
				}
			}
		}
	}

}
