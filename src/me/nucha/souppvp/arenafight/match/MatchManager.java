package me.nucha.souppvp.arenafight.match;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import me.nucha.souppvp.SoupPvPPlugin;
import me.nucha.souppvp.arenafight.arena.Arena;
import me.nucha.souppvp.arenafight.arena.ArenaManager;
import me.nucha.souppvp.language.LanguageManager;
import me.nucha.souppvp.nick.NickManager;
import me.nucha.souppvp.util.CustomItem;

public class MatchManager {

	private static List<Match> matches;
	private static Player queued;
	private static HashMap<String, Inventory> lastInventories;

	public static void init() {
		matches = Lists.newArrayList();
		lastInventories = new HashMap<>();
	}

	public static void shutdown() {
		for (Match match : Lists.newArrayList(matches)) {
			if (match.getMatchstate().equals(MatchState.COUNTDOWN)) {
				match.cancelCountingdown();
			}
			if (match.getMatchstate().equals(MatchState.IN_GAME)) {
				match.end();
			}
		}
		matches.clear();
	}

	public static List<Match> getMatches() {
		return matches;
	}

	public static Match getMatch(Player p) {
		for (Match match : matches) {
			if (match.isInMatch(p)) {
				return match;
			}
		}
		return null;
	}

	public static Match createMatch(Player p1, Player p2) {
		Set<Player> team1 = Sets.newHashSet();
		Set<Player> team2 = Sets.newHashSet();
		team1.add(p1);
		team2.add(p2);
		return createMatch(team1, team2);
	}

	public static Match createMatch(Set<Player> team1, Set<Player> team2) {
		Arena arena = ArenaManager.getRandomArena();
		if (arena == null) {
			List<Player> players = Lists.newArrayList();
			players.addAll(team1);
			players.addAll(team2);
			for (Player all : players) {
				all.sendMessage("§8[§6Match§8] §r" + LanguageManager.get(all, "arena-fight.match.no-arena"));
			}
			return null;
		}
		Match match = new Match(team1, team2, ArenaManager.getRandomArena());
		match.startCountingdown();
		return match;
	}

	public static void registerMatch(Match match) {
		if (!matches.contains(match)) {
			matches.add(match);
		}
	}

	public static void unregisterMatch(Match match) {
		if (matches.contains(match)) {
			matches.remove(match);
		}
	}

	public static Player getQueued() {
		return queued;
	}

	public static void addQueue(Player p) {
		if (queued == null) {
			queued = p;
		} else {
			if (queued == p) {
				return;
			}
			p.sendMessage("§8[§6Match§8] §r"
					+ LanguageManager.get(p, "arena-fight.match.opponent-found").replaceAll("%opponent", NickManager.getName(queued)));
			queued.sendMessage("§8[§6Match§8] §r"
					+ LanguageManager.get(p, "arena-fight.match.opponent-found").replaceAll("%opponent", NickManager.getName(p)));
			createMatch(p, queued);
			queued = null;
		}
	}

	public static void removeQueue(Player p) {
		if (isQueued(p)) {
			queued = null;
		}
	}

	public static boolean isQueued(Player p) {
		return (queued != null && queued == p);
	}

	public static void putLastInventory(Player p, boolean died) {
		Inventory inventory = Bukkit.createInventory(null, 9 * 6, "§aInventory - " + NickManager.getName(p));
		Inventory playerInventory = Bukkit.createInventory(null, 9 * 4);
		ItemStack air = new ItemStack(Material.AIR);
		ItemStack helmet = p.getInventory().getHelmet() != null ? p.getInventory().getHelmet() : air;
		ItemStack chest = p.getInventory().getChestplate() != null ? p.getInventory().getChestplate() : air;
		ItemStack legs = p.getInventory().getLeggings() != null ? p.getInventory().getLeggings() : air;
		ItemStack boots = p.getInventory().getBoots() != null ? p.getInventory().getBoots() : air;
		for (int i = 0; i < 36; i++) {
			ItemStack item = p.getInventory().getItem(i);
			if (item == null) {
				item = air;
			}
			if (i >= 0 && i <= 8) {
				playerInventory.setItem(i + 27, item);
			}
			if (i >= 9) {
				playerInventory.setItem(i - 9, item);
			}
		}
		Bukkit.getScheduler().runTaskAsynchronously(SoupPvPPlugin.getInstance(), new BukkitRunnable() {
			@Override
			public void run() {
				ItemStack none = new CustomItem(Material.STAINED_GLASS_PANE, 1, "", 5);
				int potsRemaining = 0;
				int soupsRemaining = 0;
				for (int i = 0; i < 36; i++) {
					ItemStack item = playerInventory.getItem(i);
					if (item == null) {
						item = air;
					}
					if (item.getType().equals(Material.MUSHROOM_SOUP)) {
						soupsRemaining++;
					}
					if (item.getType().equals(Material.POTION)) {
						potsRemaining++;
					}
					inventory.setItem(i, item);
				}
				inventory.setItem(36, none);
				inventory.setItem(37, none);
				inventory.setItem(38, none);
				inventory.setItem(39, none);
				inventory.setItem(40, none);
				inventory.setItem(41, none);
				inventory.setItem(42, none);
				inventory.setItem(43, none);
				inventory.setItem(44, none);
				inventory.setItem(45, helmet);
				inventory.setItem(46, chest);
				inventory.setItem(47, legs);
				inventory.setItem(48, boots);
				inventory.setItem(49, none);
				ItemStack isDied = new CustomItem(Material.INK_SACK, 1, "§cDied", 1);
				if (!died) {
					isDied = new CustomItem(Material.INK_SACK, 1, "§aSurvived!", 14);
				}
				inventory.setItem(50, isDied);
				ItemStack healthInfo = new CustomItem(Material.INK_SACK, 1, "§4Health: §c" + (int) p.getHealth(), 9);
				inventory.setItem(51, healthInfo);
				if (potsRemaining <= soupsRemaining) {
					CustomItem soups = new CustomItem(Material.MUSHROOM_SOUP, 1, "§6Soups: §e" + soupsRemaining);
					if (potsRemaining == soupsRemaining) {
						soups.addLore("§5Pots: §d" + potsRemaining);
					}
					inventory.setItem(52, soups);
				} else {
					ItemStack pots = new CustomItem(Material.POTION, 1, "§5Pots: §d" + potsRemaining);
					inventory.setItem(52, pots);
				}
				lastInventories.put(NickManager.getName(p), inventory);
			}
		});
	}

	public static Inventory getLastInventory(String playerName) {
		return lastInventories.get(playerName);
	}

}
