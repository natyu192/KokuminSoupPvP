package me.nucha.souppvp.listener.combattag;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.nucha.core.utils.TimeUtil;
import me.nucha.souppvp.SoupPvPPlugin;
import me.nucha.souppvp.util.ScoreboardUtils;

public class CombatTagManager {

	private static HashMap<Player, Date> tag;
	private static HashMap<Player, Integer> scoreboardTasks;
	private static String combatTagDisplay;

	public static void init() {
		tag = new HashMap<Player, Date>();
		scoreboardTasks = new HashMap<>();
		combatTagDisplay = "§7Combat Tag";
	}

	public static Date getTagExpire(Player p) {
		if (hasTag(p)) {
			return tag.get(p);
		}
		return null;
	}

	private static void runScoreboardTask(Player p) {
		if (scoreboardTasks.containsKey(p)) {
			return;
		}
		ScoreboardUtils.replaceScore(p, 10, "", "§7", "");
		ScoreboardUtils.updateDate(p);
		ScoreboardUtils.replaceScore(p, 9, "", combatTagDisplay, ": §a" + CombatTagManager.getTagExpireSeconds(p, 1) + "s");
		BukkitRunnable runnable = new BukkitRunnable() {
			public void run() {
				if (p == null || !p.isOnline()) {
					cancel();
					return;
				}
				double expire = CombatTagManager.getTagExpireSeconds(p, 1);
				ScoreboardUtils.getOrCreateTeam(p, combatTagDisplay).setSuffix(": §c" + expire + "s");
				if (expire <= 0) {
					ScoreboardUtils.removeScore(p, combatTagDisplay);
					ScoreboardUtils.removeScore(p, "§7");
					ScoreboardUtils.replaceScore(p, 9, "", "§7", "");
					ScoreboardUtils.updateDate(p);
					CombatTagListener.sendRealBlocks(p);
					cancel();
					scoreboardTasks.remove(p);
				}
			};
		};
		runnable.runTaskTimer(SoupPvPPlugin.getInstance(), 1L, 1L);
		scoreboardTasks.put(p, runnable.getTaskId());
	}

	public static double getTagExpireSeconds(Player p, int doubleScale) {
		if (hasTag(p)) {
			Long diff = TimeUtil.diffTime(tag.get(p));
			double tag = diff.doubleValue() / 1000;
			BigDecimal bi = new BigDecimal(tag);
			return bi.setScale(doubleScale, BigDecimal.ROUND_DOWN).doubleValue();
		}
		return 0.0d;
	}

	public static void setTag(Player p, int seconds) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.SECOND, seconds);
		setTag(p, cal.getTime());
	}

	public static void setTag(Player p, Date date) {
		if (tag.containsKey(p)) {
			tag.remove(p);
		}
		tag.put(p, date);
		CombatTagListener.sendForceFieldBlocks(p);
		runScoreboardTask(p);
	}

	public static void removeTag(Player p) {
		if (tag.containsKey(p)) {
			tag.remove(p);
		}
	}

	public static boolean hasTag(Player p) {
		if (!tag.containsKey(p)) {
			return false;
		}
		return TimeUtil.diffTime(tag.get(p)) > 0;
	}

	public static HashMap<Player, Date> getTagMap() {
		return tag;
	}

}
