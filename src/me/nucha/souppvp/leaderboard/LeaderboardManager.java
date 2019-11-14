package me.nucha.souppvp.leaderboard;

import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;

import me.nucha.core.hologram.Hologram;
import me.nucha.souppvp.SoupPvPPlugin;
import me.nucha.souppvp.util.LocationUtil;
import me.nucha.souppvp.util.PlayerDataUtil;

public class LeaderboardManager {

	private static List<LeaderboardData> leaderboardDatas;
	private static List<Hologram> holograms;

	public static void init() {
		if (holograms != null) {
			removeHolograms();
		}
		leaderboardDatas = Lists.newArrayList();
		holograms = Lists.newArrayList();
		for (String key : PlayerDataUtil.defaults.keySet()) {
			LeaderboardData data = new LeaderboardData(key);
			leaderboardDatas.add(data);
		}
		Bukkit.getScheduler().runTaskTimerAsynchronously(SoupPvPPlugin.getInstance(), new BukkitRunnable() {
			@Override
			public void run() {
				updateLeaderboards();
				for (Player all : Bukkit.getOnlinePlayers()) {
					showLeaderboards(all);
				}

			}
		}, 20 * 60 * 30, 20 * 60 * 30); // 30 mins of interval
	}

	public static void updateLeaderboards() {
		for (LeaderboardData data : leaderboardDatas) {
			data.updateLeaderboard();
		}
	}

	public static void showLeaderboards(Player p) {
		removeHolograms(p);
		for (LeaderboardData data : leaderboardDatas) {
			if (LocationUtil.isSet("lb-" + data.getCategory())) {
				Location loc = LocationUtil.get("lb-" + data.getCategory());
				List<String> texts = Lists.newArrayList();
				texts.add("§a§lLeaderboard");
				texts.add("§b§l" + stringFormat(data.getCategory()));
				int i = 1;
				for (Entry<String, Integer> entry : data.getLeaderboard()) {
					if (i == 11) {
						break;
					}
					int score = entry.getValue();
					if (score == 0) {
						continue;
					}
					String color = "§a";
					String th = "th";
					if (i == 1) {
						color = "§d";
						th = "st";
					}
					if (i == 2) {
						color = "§e";
						th = "nd";
					}
					if (i == 3) {
						color = "§6";
						th = "rd";
					}
					if (p.getName().equalsIgnoreCase(entry.getKey())) {
						texts.add(color + "§l" + i + th + " §2§l- " + color + "§l" + entry.getKey() +
								" §l§2- §b§l" + score + " " + stringFormat(data.getCategory()));
					} else {
						texts.add(color + "" + i + th + " §2- " + color + entry.getKey() +
								" §2- §b" + score + " " + stringFormat(data.getCategory()));
					}
					i++;
				}
				if (data.getScore(p) >= 1) {
					int place = data.getPlace(p);
					String th = "th";
					if (place == 1)
						th = "st";
					if (place == 2)
						th = "nd";
					if (place == 3)
						th = "rd";
					texts.add("§dYou're on §e" + data.getPlace(p) + th + " Place§d!");
				} else {
					texts.add("§7You're not on the leaderboard.");
				}
				texts.add("§7§oLast update: " + data.getLastUpdatedDate());
				Hologram hologram = new Hologram(p, loc, texts.toArray(new String[] {}));
				hologram.display();
				holograms.add(hologram);
			}
		}
	}

	public static void removeHolograms() {
		for (Hologram hologram : holograms) {
			hologram.remove();
		}
		holograms.clear();
	}

	public static void removeHolograms(Player p) {
		List<Hologram> hologramsToRemove = Lists.newArrayList();
		for (Hologram hologram : holograms) {
			if (hologram.getPlayer().getUniqueId().equals(p.getUniqueId())) {
				hologram.remove();
				hologramsToRemove.add(hologram);
			}
		}
		holograms.removeAll(hologramsToRemove);
	}

	public static LeaderboardData getLeaderboardData(String id) {
		for (LeaderboardData data : leaderboardDatas) {
			if (data.getCategory().equalsIgnoreCase(id)) {
				return data;
			}
		}
		return null;
	}

	public static String stringFormat(String str) {
		str = str.replaceAll("_", " ");
		str = str.replaceAll("-", " ");
		String[] sprittype = str.split(" ");
		String typename = null;
		for (String typen : sprittype) {
			String t1 = typen.substring(0, 1);
			String t2 = typen.substring(1);
			t2 = t2.toLowerCase();
			String newtypen = t1 + t2;
			if (typename != null) {
				typename += " " + newtypen;
			} else {
				typename = newtypen;
			}
		}
		return typename;
	}

}
