package me.nucha.souppvp.player;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.nucha.souppvp.SoupPvPPlugin;
import me.nucha.souppvp.util.LocationUtil;
import me.nucha.souppvp.util.ScoreboardUtils;

public enum PlayerState {
	IN_LOBBY,
	IN_FFA,
	IN_MATCH,
	SPECTATING_MATCH,
	LAVA_CHALLENGE;

	private static HashMap<UUID, PlayerState> states;

	public static void init() {
		states = new HashMap<>();
		ScoreboardUtils.plugin(SoupPvPPlugin.getInstance());
		for (Player all : Bukkit.getOnlinePlayers()) {
			if (LocationUtil.isSet("ffa-spawn") && LocationUtil.get("ffa-spawn").getWorld().equals(all.getWorld())) {
				states.put(all.getUniqueId(), IN_FFA);
			} else {
				states.put(all.getUniqueId(), IN_LOBBY);
				if (LocationUtil.isSet("lobby")) {
					all.teleport(LocationUtil.get("lobby"));
					PlayerUtil.treat(all);
				}
			}
		}
	}

	public static PlayerState getState(Player p) {
		return states.get(p.getUniqueId());
	}

	public static void setState(Player p, PlayerState state) {
		states.put(p.getUniqueId(), state);
		ScoreboardUtils.updateBoard(p);
	}

	public static boolean isState(Player p, PlayerState state) {
		return states.containsKey(p.getUniqueId()) && states.get(p.getUniqueId()).equals(state);
	}

	public static void unregisterState(Player p) {
		states.remove(p.getUniqueId());
	}

}
