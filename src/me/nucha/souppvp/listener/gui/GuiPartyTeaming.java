package me.nucha.souppvp.listener.gui;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.common.collect.Sets;

import me.nucha.kokumin.utils.CustomItem;
import me.nucha.parties.Party;
import me.nucha.parties.events.PartyLeaveEvent;
import me.nucha.souppvp.arenafight.match.MatchManager;
import me.nucha.souppvp.arenafight.match.TeamInfo;
import me.nucha.souppvp.language.LanguageManager;
import me.nucha.souppvp.player.PlayerState;
import me.nucha.souppvp.player.PlayerUtil;

public class GuiPartyTeaming implements Listener {

	public static HashMap<Player, TeamInfo> infos;
	public static int[] team1slots;
	public static int[] team2slots;

	public GuiPartyTeaming() {
		infos = new HashMap<>();
		team1slots = new int[] { 0, 1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21, 27, 28, 29, 30, 36, 37, 38, 39, 45, 46, 47, 48 };
		team2slots = new int[] { 5, 6, 7, 8, 14, 15, 16, 17, 23, 24, 25, 26, 32, 33, 34, 35, 41, 42, 43, 44, 50, 51, 52, 53 };
	}

	public static void open(Player p, TeamInfo info) {
		infos.put(p, info);
		Inventory gui = Bukkit.createInventory(null, 54, "§dParty Fight - Teaming");
		ItemStack space = new CustomItem(Material.STAINED_GLASS_PANE, 1, "§7左右にプレイヤーの", 5, "§7頭を移動させることで", "§7チームメンバーを編成", "§7することができます");
		ItemStack start = new CustomItem(Material.NETHER_STAR, 1, "§aゲーム開始", "§7クリックしてこのチーム編成で", "§7Party Fightを始めます");
		ItemStack random = new CustomItem(Material.BONE, 1, "§aランダムチーム分け", "§7クリックしてランダムに", "§7チーム分けを行います");
		gui.setItem(4, space);
		gui.setItem(13, space);
		gui.setItem(22, space);
		gui.setItem(31, space);
		gui.setItem(40, random);
		gui.setItem(49, start);
		for (int i = 0; i < info.getTeam1().size(); i++) {
			Player player = info.getTeam1().get(i);
			ItemStack skull = new CustomItem(Material.SKULL_ITEM, 1, "§e" + player.getName(), 3);
			SkullMeta meta = (SkullMeta) skull.getItemMeta();
			meta.setOwner(player.getName());
			skull.setItemMeta(meta);
			gui.setItem(team1slots[i], skull);
		}
		for (int i = 0; i < info.getTeam2().size(); i++) {
			Player player = info.getTeam2().get(i);
			ItemStack skull = new CustomItem(Material.SKULL_ITEM, 1, "§e" + player.getName(), 3);
			SkullMeta meta = (SkullMeta) skull.getItemMeta();
			meta.setOwner(player.getName());
			skull.setItemMeta(meta);
			gui.setItem(team2slots[i], skull);
		}
		p.openInventory(gui);
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		if (event.getClickedInventory() != null && p.getOpenInventory().getTopInventory() != null) {
			if (p.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("§dParty Fight - Teaming")) {
				if (event.getClickedInventory().equals(p.getOpenInventory().getBottomInventory())) {
					event.setCancelled(true);
				}
				if (event.getAction().equals(InventoryAction.HOTBAR_SWAP)
						|| event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)
						|| event.getAction().name().contains("DROP")) {
					event.setCancelled(true);
					return;
				}
				if (event.getClickedInventory().equals(p.getOpenInventory().getTopInventory())) {
					int slot = event.getSlot();
					Inventory inventory = p.getOpenInventory().getTopInventory();
					if (slot == 4 || slot == 13 || slot == 22 || slot == 31 || slot == 40 || slot == 49) {
						event.setCancelled(true);
					}
					if (slot == 40) {
						TeamInfo info = infos.get(p);
						info.randomize();
						p.sendMessage(LanguageManager.get(p, "party-gui.random-team"));
						p.closeInventory();
						open(p, info);
					}
					if (slot == 49) {
						Set<Player> team1 = getTeam(inventory, 1);
						Set<Player> team2 = getTeam(inventory, 2);
						if (team1.size() <= 0 || team2.size() <= 0) {
							p.sendMessage(LanguageManager.get(p, "party-gui.member-size-error"));
							return;
						}
						if (!infos.containsKey(p)) {
							p.closeInventory();
							p.sendMessage(LanguageManager.get(p, "party-gui.error-occured").replaceAll("%code", String.valueOf(1)));
							return;
						}
						if (infos.get(p).getParty() == null) {
							p.closeInventory();
							p.sendMessage(LanguageManager.get(p, "party-gui.error-occured").replaceAll("%code", String.valueOf(2)));
							return;
						}
						Party party = infos.get(p).getParty();
						for (OfflinePlayer member : party.getMembers()) {
							if (!member.isOnline()) {
								for (OfflinePlayer member2 : party.getMembers()) {
									if (member2.isOnline()) {
										Player member2Player = member2.getPlayer();
										member2Player.sendMessage(
												LanguageManager.get(member2Player, "party-gui.error-offline").replaceAll("%player", member.getName()));
									}
								}
								return;
							}
							if (!PlayerState.isState(member.getPlayer(), PlayerState.IN_LOBBY)) {
								for (OfflinePlayer member2 : party.getMembers()) {
									if (member2.isOnline()) {
										Player member2Player = member2.getPlayer();
										member2Player
												.sendMessage(LanguageManager.get(member2Player, "party-gui.error-goToTheLobby").replaceAll("%player",
														member.getName()));
										member2Player.sendMessage(PlayerState.getState(member.getPlayer()).name());
									}
								}
								return;
							}
							boolean success = false;
							for (Player p1 : team1) {
								if (p1.getUniqueId().equals(member.getPlayer().getUniqueId())) {
									success = true;
								}
							}
							for (Player p2 : team2) {
								if (p2.getUniqueId().equals(member.getPlayer().getUniqueId())) {
									success = true;
								}
							}
							if (!success) {
								p.closeInventory();
								p.sendMessage(LanguageManager.get(p, "party-gui.invalid-team"));
								return;
							}
							/*if (!team1.contains(member.getPlayer()) && !team2.contains(member.getPlayer())) {
								p.closeInventory();
								p.sendMessage(LanguageManager.get(p, "party-gui.invalid-team"));
								return;
							}*/
						}
						p.closeInventory();
						MatchManager.createMatch(team1, team2);
					}
				}
			}
		}
	}

	@EventHandler
	public void onLeaveParty(PartyLeaveEvent event) {
		OfflinePlayer p = event.getPlayer();
		if (infos.containsKey(p)) {
			infos.remove(p);
			if (p.isOnline()) {
				p.getPlayer().sendMessage("§6パーティを抜けたためチーム編成は破棄されました");
			}
		}
		for (Player infop : Sets.newHashSet(infos.keySet())) {
			TeamInfo info = infos.get(infop);
			if (info.getTeam1().contains(p)) {
				info.getTeam1().remove(p);
				open(infop, info);
			}
			if (info.getTeam2().contains(p)) {
				info.getTeam2().remove(p);
				open(infop, info);
			}
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		Player p = (Player) event.getPlayer();
		if (event.getInventory() != null && p.getOpenInventory().getTopInventory() != null) {
			if (p.getOpenInventory().getTopInventory().getName().equalsIgnoreCase("§dParty Fight - Teaming")) {
				infos.remove(p);
				PlayerUtil.giveLobbyItems(p);
			}
		}
	}

	private Set<Player> getTeam(Inventory inventory, int teamNumber) {
		Set<Player> list = Sets.newHashSet();
		int[] slots = (teamNumber == 1)
				? team1slots
				: team2slots;
		for (int i : slots) {
			ItemStack item = inventory.getItem(i);
			if (item != null && item.getType().equals(Material.SKULL_ITEM)) {
				SkullMeta meta = (SkullMeta) item.getItemMeta();
				String owner = meta.getOwner();
				Player p = Bukkit.getPlayer(owner);
				if (p != null) {
					list.add(p);
				}
			}
		}
		return list;
	}

}
