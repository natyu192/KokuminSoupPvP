package me.nucha.souppvp.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.nucha.core.utils.TimeUtil;
import me.nucha.souppvp.SoupPvPPlugin;

public class CooldownUtil {

	private static HashMap<Player, List<Cooldown>> cooldown;

	public static void init(SoupPvPPlugin plugin) {
		cooldown = new HashMap<Player, List<Cooldown>>();
		BukkitRunnable boosterTask = new BukkitRunnable() {
			@Override
			public void run() {
				for (List<Cooldown> cl : new ArrayList<>(cooldown.values())) {
					for (Cooldown c : new ArrayList<>(cl)) {
						c.tick();
						if (!c.isActive()) {
							cooldown.get(c.getPlayer()).remove(c);
						}
					}
				}
			}
		};
		boosterTask.runTaskTimer(plugin, 1L, 1L);
	}

	public static double getCooldownExpireSeconds(Player p, int doubleScale, String name) {
		if (hasCooldown(p, name)) {
			Long diff = TimeUtil.diffTime(getCooldown(p, name).getExpire());
			double cooldown = diff.doubleValue() / 1000;
			BigDecimal bi = new BigDecimal(cooldown);
			return bi.setScale(doubleScale, BigDecimal.ROUND_DOWN).doubleValue();
		}
		return 0.0d;
	}

	public static void setCooldown(Player p, int seconds, String name) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.SECOND, seconds);
		setCooldown(p, cal.getTime(), name);
	}

	public static void setCooldown(Player p, Date date, String name) {
		removeCooldown(p, name);
		Cooldown newCd = new Cooldown(p, date, name);
		List<Cooldown> cds = null;
		if (cooldown.containsKey(p)) {
			cds = cooldown.get(p);
		} else {
			cds = new ArrayList<Cooldown>();
		}
		cds.add(newCd);
		cooldown.put(p, cds);
	}

	public static void removeCooldown(Player p, String name) {
		Cooldown cd = getCooldown(p, name);
		if (cd != null) {
			cooldown.get(p).remove(cd);
		}
	}

	public static Cooldown getCooldown(Player p, String name) {
		if (cooldown.containsKey(p)) {
			for (Cooldown cooldown : CooldownUtil.cooldown.get(p)) {
				if (cooldown.getName().equalsIgnoreCase(name)) {
					return cooldown;
				}
			}
		}
		return null;
	}

	public static boolean hasCooldown(Player p, String name) {
		Cooldown cd = getCooldown(p, name);
		if (cd != null) {
			return TimeUtil.diffTime(cd.getExpire()) > 0;
		}
		return false;
	}

	public static HashMap<Player, List<Cooldown>> getCooldownMap() {
		return cooldown;
	}

	static class Cooldown {

		private Player player;
		private Date expire;
		private String name;
		private boolean active;

		public Cooldown(Player player, Date expire, String name) {
			this.player = player;
			this.expire = expire;
			this.name = name;
			this.active = true;
		}

		public Date getExpire() {
			return expire;
		}

		public String getName() {
			return name;
		}

		public Player getPlayer() {
			return player;
		}

		public boolean isActive() {
			return active;
		}

		public void tick() {
			if (!active) {
				return;
			}
			long now = System.currentTimeMillis();
			if (now >= expire.getTime()) {
				active = false;
			}
		}

	}

}
