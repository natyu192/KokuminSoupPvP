package me.nucha.souppvp.arenafight.match;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.google.common.collect.Lists;

import me.nucha.souppvp.listener.CombatListener;
import me.nucha.souppvp.player.PlayerState;

public class MatchListener implements Listener {

	public static void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		MatchManager.removeQueue(p);
		if (PlayerState.isState(p, PlayerState.IN_MATCH) || PlayerState.isState(p, PlayerState.SPECTATING_MATCH)) {
			Match match = MatchManager.getMatch(p);
			if (match != null) {
				if (match.isFighting(p)) {
					match.death(p, false);
				}
				if (match.isSpectator(p)) {
					match.unregisterPlayer(p);
				}
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		if (PlayerState.isState(p, PlayerState.IN_MATCH) || PlayerState.isState(p, PlayerState.SPECTATING_MATCH)) {
			Match match = MatchManager.getMatch(p);
			if (match != null) {
				if (match.isFighting(p)) {
					match.death(p, true);
				}
			}
		}
		CombatListener.onDeath(event);
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player p = (Player) event.getDamager();
			if (PlayerState.isState(p, PlayerState.IN_LOBBY) || PlayerState.isState(p, PlayerState.LAVA_CHALLENGE)) {
				event.setCancelled(true);
			}
			if (PlayerState.isState(p, PlayerState.IN_MATCH) || PlayerState.isState(p, PlayerState.SPECTATING_MATCH)) {
				Match match = MatchManager.getMatch(p);
				if (match != null) {
					if (match.isFighting(p) && !match.getMatchstate().equals(MatchState.IN_GAME)) {
						event.setCancelled(true);
					}
					if (match.isSpectator(p)) {
						event.setCancelled(true);
					}
				}
			}
		}
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (PlayerState.isState(p, PlayerState.IN_MATCH) || PlayerState.isState(p, PlayerState.SPECTATING_MATCH)) {
				Match match = MatchManager.getMatch(p);
				if (match != null) {
					if (match.isFighting(p) && !match.getMatchstate().equals(MatchState.IN_GAME)) {
						event.setCancelled(true);
					}
					if (match.isSpectator(p)) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		for (Match match : MatchManager.getMatches()) {
			List<Player> players = Lists.newArrayList();
			players.addAll(match.getTeam1Living());
			players.addAll(match.getTeam2Living());
			players.addAll(match.getSpectators());
			for (Player player : players) {
				player.hidePlayer(p);
			}
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) { // for lastinventory
		Player p = (Player) event.getWhoClicked();
		if (event.getClickedInventory() != null && p.getOpenInventory().getTopInventory() != null) {
			if (p.getOpenInventory().getTopInventory().getName().startsWith("§aInventory - ")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player p = event.getPlayer();
		if (PlayerState.isState(p, PlayerState.IN_MATCH) || PlayerState.isState(p, PlayerState.SPECTATING_MATCH)) {
			Match match = MatchManager.getMatch(p);
			if (match != null) {
				if (match.isSpectator(p)) {
					event.setRespawnLocation(match.getArena().getSpawn1());
				}
			}
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		if (PlayerState.isState(p, PlayerState.SPECTATING_MATCH)) {
			event.setCancelled(true);
		}
		if (PlayerState.isState(p, PlayerState.IN_MATCH) || PlayerState.isState(p, PlayerState.IN_LOBBY)) {
			event.getItemDrop().remove();
		}
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		Player p = event.getPlayer();
		if (PlayerState.isState(p, PlayerState.IN_MATCH) || PlayerState.isState(p, PlayerState.SPECTATING_MATCH)) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		if (PlayerState.isState(p, PlayerState.IN_MATCH) || PlayerState.isState(p, PlayerState.SPECTATING_MATCH)) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		if (PlayerState.isState(p, PlayerState.IN_MATCH) || PlayerState.isState(p, PlayerState.SPECTATING_MATCH)) {
			event.setCancelled(true);
			return;
		}
	}

}
