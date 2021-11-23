package me.nucha.souppvp.player;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import me.nucha.souppvp.arenafight.match.MatchManager;
import me.nucha.souppvp.kit.KitManager;
import me.nucha.souppvp.util.CustomItem;
import me.nucha.souppvp.util.ItemUtil;
import me.nucha.souppvp.util.LocationUtil;
import net.minecraft.server.v1_8_R3.Packet;

public class PlayerUtil {

	public static ItemStack item_perkshop;
	public static ItemStack item_kits;
	public static ItemStack item_stats;
	public static ItemStack item_kabu;
	public static ItemStack item_arenafight;
	public static ItemStack item_arenafight_queued;
	public static ItemStack item_partyfight;
	public static ItemStack item_lava_challenge;
	public static ItemStack item_leavespec;

	public static void init() {
		item_perkshop = new CustomItem(Material.EMERALD, 1, "§aPerk Shop", "§7右クリックしてPerk Shopを開きます");
		item_kits = new CustomItem(Material.BOOK, 1, "§aKits", "§7右クリックしてKitメニューを開きます");
		item_stats = new CustomItem(Material.PAPER, 1, "§aStats", "§7右クリックしてStatsメニューを開きます");
		item_kabu = new CustomItem(Material.GOLD_NUGGET, 1, "§e株 (仮)", "§7右クリックして株メニューを開きます");
		item_arenafight = new CustomItem(Material.IRON_SWORD, 1, "§c1vs1に参加", "§7右クリックして対戦相手を待機します");
		item_arenafight_queued = item_arenafight.clone();
		item_arenafight_queued.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		ItemMeta meta = item_arenafight_queued.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item_arenafight_queued.setItemMeta(meta);
		item_partyfight = new CustomItem(Material.DIAMOND_SWORD, 1, "§bParty Fightを始める", "§7右クリックしてチーム編成を行います");
		item_lava_challenge = new CustomItem(Material.BLAZE_POWDER, 1, "§cLava Challenge", "§7右クリックしてLava Challengeにテレポートします");
		item_leavespec = new CustomItem(Material.REDSTONE, 1, "§3観戦をやめる", "§7右クリックしてロビーに戻ります");
		ItemUtil.setUndroppable(item_perkshop, true);
		ItemUtil.setUndroppable(item_kits, true);
		ItemUtil.setUndroppable(item_stats, true);
		ItemUtil.setUndroppable(item_kabu, true);
		ItemUtil.setUndroppable(item_arenafight, true);
		ItemUtil.setUndroppable(item_partyfight, true);
		ItemUtil.setUndroppable(item_leavespec, true);
	}

	public static void joinFFA(Player p) {
		MatchManager.removeQueue(p);
		if (PlayerState.isState(p, PlayerState.IN_LOBBY)) {
			PlayerState.setState(p, PlayerState.IN_FFA);
			if (LocationUtil.isSet("ffa-spawn")) {
				p.teleport(LocationUtil.get("ffa-spawn"));
			}
			removePotionEfects(p);
			giveLobbyItems(p);
		} else {
			if (LocationUtil.isSet("lobby")) {
				p.teleport(LocationUtil.get("lobby"));
			}
		}
	}

	public static void leaveFFA(Player p) {
		PlayerState.setState(p, PlayerState.IN_LOBBY);
		if (LocationUtil.isSet("lobby")) {
			p.teleport(LocationUtil.get("lobby"));
		}
		removePotionEfects(p);
		KitManager.unselectKit(p);
		giveLobbyItems(p);
	}

	public static void joinLavaChallenge(Player p) {
		MatchManager.removeQueue(p);
		if (PlayerState.isState(p, PlayerState.IN_LOBBY)) {
			PlayerState.setState(p, PlayerState.LAVA_CHALLENGE);
			if (LocationUtil.isSet("lava-challenge")) {
				p.teleport(LocationUtil.get("lava-challenge"));
			}
			removePotionEfects(p);
			PlayerUtil.clearInventory(p);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "soup " + p.getName());
		} else {
			if (LocationUtil.isSet("lobby")) {
				p.teleport(LocationUtil.get("lobby"));
			}
		}
	}

	public static void leaveLavaChallenge(Player p) {
		PlayerState.setState(p, PlayerState.IN_LOBBY);
		if (LocationUtil.isSet("lobby")) {
			p.teleport(LocationUtil.get("lobby"));
		}
		removePotionEfects(p);
		treat(p);
		giveLobbyItems(p);
	}

	public static void clearInventory(Player p) {
		p.getInventory().clear();
		ItemStack air = new ItemStack(Material.AIR);
		p.getInventory().setArmorContents(new ItemStack[] { air, air, air, air });
	}

	public static void removePotionEfects(Player p) {
		for (PotionEffect effect : p.getActivePotionEffects()) {
			p.removePotionEffect(effect.getType());
		}
	}

	public static void giveLobbyItems(Player p) {
		clearInventory(p);
		p.getInventory().setItem(0, item_kits.clone());
		p.getInventory().setItem(1, item_perkshop.clone());
		p.getInventory().setItem(2, item_stats.clone());
		p.getInventory().setItem(3, item_kabu.clone());
		p.getInventory().setItem(5, MatchManager.isQueued(p) ? item_arenafight_queued.clone() : item_arenafight.clone());
		p.getInventory().setItem(6, item_partyfight.clone());
		if (LocationUtil.isSet("lava-challenge")) {
			p.getInventory().setItem(7, item_lava_challenge.clone());
		}
	}

	public static void giveSpectatorItems(Player p) {
		clearInventory(p);
		p.getInventory().setItem(0, item_leavespec.clone());
	}

	public static void treat(Player p) {
		p.setHealth(p.getMaxHealth());
		p.setFoodLevel(20);
		p.setSaturation(3);
		p.setFireTicks(0);
	}

	public static void sendPacket(Player p, Packet<?> packet) {
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}

}
