package me.nucha.souppvp.util.cooldown;

import java.util.Date;

import org.bukkit.entity.Player;

public class Cooldown {

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
