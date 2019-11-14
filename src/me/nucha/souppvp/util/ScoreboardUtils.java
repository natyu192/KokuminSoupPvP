package me.nucha.souppvp.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.google.common.collect.Lists;

import me.nucha.kokumin.coin.Coin;
import me.nucha.souppvp.SoupPvPPlugin;
import me.nucha.souppvp.arenafight.match.Match;
import me.nucha.souppvp.arenafight.match.MatchManager;
import me.nucha.souppvp.nick.NickManager;
import me.nucha.souppvp.player.PlayerState;

public class ScoreboardUtils {

	private static SoupPvPPlugin plugin;

	public static void plugin(SoupPvPPlugin plugin) {
		ScoreboardUtils.plugin = plugin;
		BukkitRunnable pingUpdateTask = new BukkitRunnable() {

			@Override
			public void run() {
				for (Player all : Bukkit.getOnlinePlayers()) {
					updatePing(all);
				}
			}
		};
		pingUpdateTask.runTaskTimer(plugin, 0L, 60L);
		for (Player all : Bukkit.getOnlinePlayers()) {
			displayBoard(all);
		}
	}

	public static void undisplayBoard(Player p) {
		p.setScoreboard(plugin.getServer().getScoreboardManager().getMainScoreboard());
	}

	public static void displayBoard(Player p) {
		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective o = sb.registerNewObjective(p.getName(), "dummy");
		o.setDisplayName("§a§lKokumin PvP");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		p.setScoreboard(sb);
		updateBoard(p);
	}

	public static void updateBoard(Player p) {
		Scoreboard sb = p.getScoreboard();
		Objective o = sb.getObjective(DisplaySlot.SIDEBAR);
		if (o == null) {
			o = sb.registerNewObjective(p.getName(), "dummy");
		}
		o.setDisplayName("§a§lKokumin PvP");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		p.setScoreboard(sb);
		removeAllScores(p);
		String date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
		if (PlayerState.isState(p, PlayerState.LAVA_CHALLENGE)) {
			replaceScore(p, 9, "", "§7", date);
			replaceScore(p, 8, "        ");
			replaceScore(p, 7, "§cThe Lava", " Challenge!", "");
			replaceScore(p, 6, "      ");
			replaceScore(p, 5, "", "§cがんばれがんばれ", "");
			replaceScore(p, 4, "", "§cできるできる", "できるぞ！");
			replaceScore(p, 3, "", "§c君ならやれる", "からがんばれ");
			replaceScore(p, 2, "", "§cうおおお", "おおおお");
			replaceScore(p, 1, " ");
			replaceScore(p, 0, "§akokumin.work");
		}
		if (PlayerState.isState(p, PlayerState.IN_LOBBY) || PlayerState.isState(p, PlayerState.IN_FFA)) {
			replaceScore(p, 9, "", "§7", date);
			replaceScore(p, 8, "        ");
			replaceScore(p, 7, "", "§3XP: ", "§b" + PlayerDataUtil.getInt(p, "xp"));
			replaceScore(p, 6, "", "§6Coins: ", "§e" + String.valueOf(Coin.getCoin(p)));
			replaceScore(p, 5, "", "§7Kills: ", "§a" + PlayerDataUtil.getInt(p, "kills"));
			replaceScore(p, 4, "    ");
			replaceScore(p, 3, "", "§7Ping: ", "§6Loading...");
			replaceScore(p, 2, "", "§7Onlines: ", "§a" + Bukkit.getOnlinePlayers().size());
			replaceScore(p, 1, " ");
			replaceScore(p, 0, "§akokumin.work");
		}
		if (PlayerState.isState(p, PlayerState.IN_MATCH) || PlayerState.isState(p, PlayerState.SPECTATING_MATCH)) {
			Match match = MatchManager.getMatch(p);
			List<String> texts = Lists.newArrayList();
			if (match != null) {
				texts.add("    ");
				texts.add("§bTeam 1:");
				for (Player player : match.getTeam1()) {
					texts.add(NickManager.getName(player));
				}
				texts.add("   ");
				texts.add("§bTeam 2:");
				for (Player player : match.getTeam2()) {
					texts.add(NickManager.getName(player));
				}
				if (texts.size() >= 16) {
					texts = texts.subList(0, 14);
				}
			}
			texts.add(" ");
			texts.add("§akokumin.work");
			int score = texts.size() + 1;
			replaceScore(p, score--, "", "§7", date);
			for (int i = 0; i < texts.size(); i++) {
				replaceScore(p, score--, texts.get(i));
			}
		}
	}

	public static void updateKills(Player p) {
		getOrCreateTeam(p, "§7Kills: ").setSuffix("§a" + PlayerDataUtil.getInt(p, "kills"));
	}

	public static void updatePing(Player p) {
		int ping = ((CraftPlayer) p).getHandle().ping;
		String suffix = pingColor(ping).toString() + ping + "ms";
		if (suffix.length() > 16) {
			suffix = "§4Too high!";
		}
		getOrCreateTeam(p, "§7Ping: ").setSuffix(suffix);
	}

	public static void updateOnlines(Player p) {
		getOrCreateTeam(p, "§7Onlines: ").setSuffix("§a" + Bukkit.getOnlinePlayers().size());
	}

	public static void updateDate(Player p) {
		String date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
		getOrCreateTeam(p, "§7").setSuffix(date);
	}

	public static void updateCoins(Player p) {
		getOrCreateTeam(p, "§6Coins: ").setSuffix("§e" + String.valueOf(Coin.getCoin(p)));
	}

	public static void updateXp(Player p) {
		getOrCreateTeam(p, "§3XP: ").setSuffix("§b" + PlayerDataUtil.getInt(p, "xp"));
	}

	public static void replaceScore(Player p, int score, String str) {
		Scoreboard sb = p.getScoreboard();
		Objective o = sb.getObjective(DisplaySlot.SIDEBAR);
		for (String entry : sb.getEntries()) {
			if (o.getScore(entry).getScore() == score) {
				sb.resetScores(entry);
			}
		}
		if (str.length() > 16) {
			String prefix = str.substring(0, 16);
			String name = "";
			String suffix = "";
			if (str.length() > 32) {
				name = str.substring(16, 32);
			} else {
				name = str.substring(16);
			}
			if (str.length() > 48) {
				suffix = str.substring(32, 48);
			} else {
				if (str.length() > 32) {
					suffix = str.substring(16, 32);
				} else {
					suffix = "";
				}
			}
			org.bukkit.scoreboard.Team team = getOrCreateTeam(p, name);
			team.setPrefix(prefix);
			team.setSuffix(suffix);
			team.addEntry(name);
			str = name;
		}
		o.getScore(str).setScore(score);
	}

	public static void replaceScore(Player p, int score, String str1, String str2, String str3) {
		org.bukkit.scoreboard.Team team = getOrCreateTeam(p, str2);
		team.setPrefix(str1);
		team.setSuffix(str3);
		team.addEntry(str2);
		replaceScore(p, score, str2);
	}

	public static void removeScore(Player p, String score) {
		try {
			Scoreboard sb = p.getScoreboard();
			sb.resetScores(score);
		} catch (Exception e) {

		}
	}

	public static void removeScore(Player p, int score) {
		try {
			Scoreboard sb = p.getScoreboard();
			Objective o = sb.getObjective(DisplaySlot.SIDEBAR);
			for (String entry : sb.getEntries()) {
				if (o.getScore(entry).getScore() == score) {
					sb.resetScores(entry);
				}
			}
		} catch (Exception e) {

		}
	}

	public static void removeAllScores(Player p) {
		try {
			Scoreboard sb = p.getScoreboard();
			for (String entry : sb.getEntries()) {
				sb.resetScores(entry);
			}
		} catch (Exception e) {

		}
	}

	public static org.bukkit.scoreboard.Team getOrCreateTeam(Player p, String name) {
		org.bukkit.scoreboard.Team t = p.getScoreboard().getTeam(name);
		if (t == null) {
			if (name.length() > 16) {
				name = name.substring(0, 16);
			}
			t = p.getScoreboard().registerNewTeam(name);
		}
		return t;
	}

	public static void startTimerOnScoreboard(Player p, String text, double timercount, int line) {
		if (text.length() > 16) {
			System.out.println("The text cannot be more than 16");
			return;
		}
		org.bukkit.scoreboard.Team team = getOrCreateTeam(p, text);
		team.setPrefix("§e");
		team.setSuffix(timercount + "秒");
		team.addEntry(text);
		replaceScore(p, line, text);
		Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			double ndt = timercount;
			boolean cont = true;

			@Override
			public void run() {
				if (cont) {
					BigDecimal bd = new BigDecimal(ndt);
					double count = bd.setScale(1, BigDecimal.ROUND_DOWN).doubleValue();
					if (count <= 0.0) {
						p.getScoreboard().resetScores(text);
						cont = false;
						return;
					}
					team.setSuffix(count + "秒");
					ndt -= 0.1;
				}
			}
		}, 0L, 2L);
	}

	public static String toMinAndSec(int seconds) {
		int huni = seconds / 60;
		int byoui = seconds % 60;
		String hun = String.valueOf(huni);
		String byou = String.valueOf(byoui);
		if (hun.length() == 1) {
			hun = "0" + hun;
		}
		if (byou.length() == 1) {
			byou = "0" + byou;
		}
		return hun + ":" + byou;
	}

	private static ChatColor pingColor(int ping) {
		if (ping < 150)
			return ChatColor.GREEN;
		if (ping < 300)
			return ChatColor.YELLOW;
		if (ping < 450)
			return ChatColor.GOLD;
		if (ping < 600)
			return ChatColor.RED;
		return ChatColor.DARK_RED;
	}

}
