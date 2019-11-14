package me.nucha.souppvp.arenafight.match;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import me.nucha.souppvp.SoupPvPPlugin;
import me.nucha.souppvp.arenafight.arena.Arena;
import me.nucha.souppvp.kit.KitManager;
import me.nucha.souppvp.language.LanguageManager;
import me.nucha.souppvp.nick.NickManager;
import me.nucha.souppvp.player.PlayerState;
import me.nucha.souppvp.player.PlayerUtil;
import me.nucha.souppvp.util.LocationUtil;
import me.nucha.souppvp.util.ScoreboardUtils;
import mkremins.fanciful.FancyMessage;
import net.minecraft.server.v1_8_R3.EntityLightning;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityWeather;

public class Match {

	private Set<Player> team1;
	private Set<Player> team2;
	private Set<Player> team1Living;
	private Set<Player> team2Living;
	private Set<Player> died;
	private Arena arena;
	private MatchState matchstate;
	private Set<Player> spectators;

	private BukkitRunnable taskCountdown;

	private Match instance;

	public Match(Set<Player> team1, Set<Player> team2, Arena arena) {
		this.team1 = team1;
		this.team2 = team2;
		this.team1Living = Sets.newHashSet(team1);
		this.team2Living = Sets.newHashSet(team2);
		this.died = Sets.newHashSet();
		this.arena = arena;
		this.matchstate = MatchState.IDLING;
		this.spectators = Sets.newHashSet();
		this.instance = this;
	}

	public void startCountingdown() {
		if (matchstate != MatchState.IDLING) {
			return;
		}
		matchstate = MatchState.COUNTDOWN;
		MatchManager.registerMatch(instance);
		Set<Player> players = Sets.newHashSet();
		players.addAll(team1);
		players.addAll(team2);
		for (Player p : players) {
			PlayerState.setState(p, PlayerState.IN_MATCH);
			updateSbTeam(p);
		}
		Bukkit.getConsoleSender().sendMessage("§a[1vs1] A match is starting... players: " + players.toString());
		clearInventoryToPlayers();
		healToPlayers();
		teleportSpawnToPlayers();
		updateTrackers();
		taskCountdown = new BukkitRunnable() {
			int i = 5; // 10

			@Override
			public void run() {
				if (i >= 0) {
					if (i == 0) {
						sendMessageToPlayers("arena-fight.match.started", true);
						start();
						playSoundToPlayers(Sound.NOTE_PLING, 2f, true);
						cancel();
					}
					if (i == 10 || (i <= 5 && i >= 1)) {
						playSoundToPlayers(Sound.NOTE_STICKS, 1f, true);
						sendMessageToPlayers("arena-fight.match.starting-in", true, "count:" + i);
					}
				}
				i--;
			}
		};
		taskCountdown.runTaskTimer(SoupPvPPlugin.getInstance(), 0L, 20L);
	}

	public void cancelCountingdown() {
		if (taskCountdown != null) {
			taskCountdown.cancel();
		}
	}

	public void start() {
		if (matchstate != MatchState.COUNTDOWN) {
			return;
		}
		matchstate = MatchState.IN_GAME;
		giveKitToPlayers();
		healToPlayers();
		teleportSpawnToPlayers();
	}

	public void updateSbTeam(Player p) {
		unregisterTeam(p);
		if (team1.contains(p)) {
			Team sbTeam = ScoreboardUtils.getOrCreateTeam(p, "team");
			Team sbEnemy = ScoreboardUtils.getOrCreateTeam(p, "enemy");
			Team sbDied = ScoreboardUtils.getOrCreateTeam(p, "died");
			sbTeam.setPrefix("§a");
			sbEnemy.setPrefix("§c");
			sbDied.setPrefix("§7");
			sbTeam.setAllowFriendlyFire(false);
			sbEnemy.setAllowFriendlyFire(false);
			sbDied.setAllowFriendlyFire(false);
			for (Player teammate : team1) {
				sbTeam.addPlayer(teammate);
				sbTeam.addEntry(NickManager.getName(teammate));
			}
			for (Player enemy : team2) {
				sbEnemy.addPlayer(enemy);
				sbEnemy.addEntry(NickManager.getName(enemy));
			}
			if (died.contains(p)) {
				Team sbSpectator = ScoreboardUtils.getOrCreateTeam(p, "spectator");
				sbSpectator.setPrefix("§a[S]§2");
				sbSpectator.setAllowFriendlyFire(false);
				for (Player spectator : spectators) {
					sbSpectator.addPlayer(spectator);
					sbSpectator.addEntry(NickManager.getName(spectator));
				}
			}
			for (Player death : died) {
				sbDied.addPlayer(death);
				sbDied.addEntry(NickManager.getName(death));
			}
		} else if (team2.contains(p)) {
			Team sbTeam = ScoreboardUtils.getOrCreateTeam(p, "team");
			Team sbEnemy = ScoreboardUtils.getOrCreateTeam(p, "enemy");
			Team sbDied = ScoreboardUtils.getOrCreateTeam(p, "died");
			sbTeam.setPrefix("§a");
			sbEnemy.setPrefix("§c");
			sbDied.setPrefix("§7");
			sbTeam.setAllowFriendlyFire(false);
			sbEnemy.setAllowFriendlyFire(false);
			sbDied.setAllowFriendlyFire(false);
			for (Player teammate : team2) {
				sbTeam.addPlayer(teammate);
				sbTeam.addEntry(NickManager.getName(teammate));
			}
			for (Player enemy : team1) {
				sbEnemy.addPlayer(enemy);
				sbEnemy.addEntry(NickManager.getName(enemy));
			}
			if (died.contains(p)) {
				Team sbSpectator = ScoreboardUtils.getOrCreateTeam(p, "spectator");
				sbSpectator.setPrefix("§a[S]§2");
				sbSpectator.setAllowFriendlyFire(false);
				for (Player spectator : spectators) {
					sbSpectator.addPlayer(spectator);
					sbSpectator.addEntry(NickManager.getName(spectator));
				}
			}
			for (Player death : died) {
				sbDied.addPlayer(death);
				sbDied.addEntry(NickManager.getName(death));
			}
		} else if (spectators.contains(p)) {
			Team sbTeam1 = ScoreboardUtils.getOrCreateTeam(p, "team1");
			Team sbTeam2 = ScoreboardUtils.getOrCreateTeam(p, "team2");
			Team sbDied = ScoreboardUtils.getOrCreateTeam(p, "died");
			Team sbSpectator = ScoreboardUtils.getOrCreateTeam(p, "spectator");
			sbTeam1.setPrefix("§e");
			sbTeam2.setPrefix("§d");
			sbDied.setPrefix("§7");
			sbSpectator.setPrefix("§a[S]§2");
			sbTeam1.setAllowFriendlyFire(false);
			sbTeam2.setAllowFriendlyFire(false);
			sbDied.setAllowFriendlyFire(false);
			sbSpectator.setAllowFriendlyFire(false);
			for (Player team1player : team1) {
				sbTeam1.addPlayer(team1player);
				sbTeam1.addEntry(NickManager.getName(team1player));
			}
			for (Player team2player : team2) {
				sbTeam2.addPlayer(team2player);
				sbTeam2.addEntry(NickManager.getName(team2player));
			}
			for (Player spectator : spectators) {
				sbSpectator.addPlayer(spectator);
				sbSpectator.addEntry(NickManager.getName(spectator));
			}
			for (Player death : died) {
				sbDied.addPlayer(death);
				sbDied.addEntry(NickManager.getName(death));
			}
		}
	}

	public void unregisterTeam(Player p) {
		Team sbTeam = ScoreboardUtils.getOrCreateTeam(p, "team");
		Team sbEnemy = ScoreboardUtils.getOrCreateTeam(p, "enemy");
		Team sbDied = ScoreboardUtils.getOrCreateTeam(p, "died");
		sbTeam.unregister();
		sbEnemy.unregister();
		sbDied.unregister();
		Team sbTeam1 = ScoreboardUtils.getOrCreateTeam(p, "team1");
		Team sbTeam2 = ScoreboardUtils.getOrCreateTeam(p, "team2");
		Team sbSpectator = ScoreboardUtils.getOrCreateTeam(p, "spectator");
		sbTeam1.unregister();
		sbTeam2.unregister();
		sbSpectator.unregister();
	}

	public void end() {
		end(null, null, true);
	}

	public void end(Set<Player> winners, Set<Player> losers) {
		end(winners, losers, false);
	}

	public void end(Set<Player> winners, Set<Player> losers, boolean shutdownMode) {
		cancelCountingdown();
		List<Player> playersLiving = Lists.newArrayList();
		playersLiving.addAll(team1Living);
		playersLiving.addAll(team2Living);
		for (Player playerLiving : playersLiving) {
			MatchManager.putLastInventory(playerLiving, false);
		}
		FancyMessage winnersMessage = new FancyMessage("§8[§6Match§8] §2Winner(s): §a");
		boolean first = true;
		if (winners != null) {
			for (Player winner : winners) {
				String command = "/lastinventory " + NickManager.getName(winner);
				if (first) {
					winnersMessage.then(NickManager.getName(winner))
							.tooltip(command).command(command);
					first = false;
				} else {
					winnersMessage.then("§2, ").then("§a" + NickManager.getName(winner)).tooltip(command).command(command);
				}
			}
		} else {
			winnersMessage.then("None");
		}
		FancyMessage losersMessage = new FancyMessage("§8[§6Match§8] §4Loser(s): §c");
		first = true;
		if (losers != null) {
			for (Player loser : losers) {
				String command = "/lastinventory " + NickManager.getName(loser);
				if (first) {
					losersMessage.then(NickManager.getName(loser))
							.tooltip(command).command(command);
					first = false;
				} else {
					losersMessage.then("§4, ").then("§a" + NickManager.getName(loser)).tooltip(command).command(command);
				}
			}
		} else {
			losersMessage.then("None");
		}
		sendMessageToPlayers("bar", true);
		sendMessageToPlayers("arena-fight.match.ended", true);
		sendMessageToPlayers("space", true);
		sendFancyMessageToPlayers(winnersMessage, true);
		sendFancyMessageToPlayers(losersMessage, true);
		sendMessageToPlayers("bar", true);
		BukkitRunnable task = new BukkitRunnable() {
			@Override
			public void run() {
				Set<Player> players = Sets.newHashSet();
				players.addAll(team1);
				players.addAll(team2);
				players.addAll(spectators);
				for (Player p : players) {
					if (p != null && p.isOnline()) {
						unregisterTeam(p);
						p.teleport(LocationUtil.get("lobby"));
						PlayerState.setState(p, PlayerState.IN_LOBBY);
						for (Player all : Bukkit.getOnlinePlayers()) {
							all.showPlayer(p);
							p.showPlayer(all);
						}
						KitManager.unselectKit(p);
						PlayerUtil.removePotionEfects(p);
						PlayerUtil.giveLobbyItems(p);
					}
				}
				healToPlayers();
				unregisterPlayerAll();
				MatchManager.unregisterMatch(instance);
				// updateTrackers();
			}
		};
		if (!shutdownMode) {
			Bukkit.getScheduler().runTaskLater(SoupPvPPlugin.getInstance(), task, 100L);
		} else {
			task.run();
		}
	}

	public void death(Player p, boolean beSpectator) {
		strikeLightningEffectToPlayers(p.getLocation());
		team1Living.remove(p);
		team2Living.remove(p);
		died.add(p);
		MatchManager.putLastInventory(p, true);
		if (beSpectator) {
			addSpectator(p);
		}
		Set<Player> observers = Sets.newHashSet();
		observers.addAll(team1);
		observers.addAll(team2);
		observers.addAll(spectators);
		for (Player player : observers) {
			if (player != null && player.isOnline()) {
				updateSbTeam(player);
			}
		}
		updateTrackersOn(p);
		checkEnd();
	}

	public boolean checkEnd() {
		if (team1Living.size() == 0) {
			end(team2, team1);
			return true;
		}
		if (team2Living.size() == 0) {
			end(team1, team2);
			return true;
		}
		return false;
	}

	public void addSpectator(Player p) {
		if (spectators.contains(p)) {
			return;
		}
		spectators.add(p);
		PlayerState.setState(p, PlayerState.SPECTATING_MATCH);
		p.teleport(arena.getSpawn1());
		// p.setAllowFlight(true);
		updateTrackersOn(p);
		updateSbTeam(p);
		PlayerUtil.giveSpectatorItems(p);
	}

	public void removeSectator(Player p) {
		if (!spectators.contains(p)) {
			return;
		}
		PlayerState.setState(p, PlayerState.IN_LOBBY);
		p.teleport(LocationUtil.get("lobby"));
		spectators.remove(p);
		// p.setAllowFlight(false);
		updateTrackersOn(p);
		updateSbTeam(p);
		PlayerUtil.giveLobbyItems(p);
	}

	public void sendMessageToPlayers(String path, boolean includingSpectators, String... replacements) {
		Set<Player> observers = Sets.newHashSet();
		observers.addAll(team1);
		observers.addAll(team2);
		if (includingSpectators) {
			observers.addAll(spectators);
		}
		for (Player all : observers) {
			if (all != null && all.isOnline()) {
				String m = LanguageManager.get(all, path);
				if (replacements != null) {
					for (String replacement : replacements) {
						String[] splitted = replacement.split(":");
						String key = splitted[0];
						String value = splitted[1];
						m = m.replaceAll("%" + key, value);
					}
				}
				all.sendMessage("§8[§6Match§8] §r" + m);
			}
		}
	}

	public void sendFancyMessageToPlayers(FancyMessage fancyMessage, boolean includingSpectators) {
		Set<Player> observers = Sets.newHashSet();
		observers.addAll(team1);
		observers.addAll(team2);
		if (includingSpectators) {
			observers.addAll(spectators);
		}
		fancyMessage.send(observers);
	}

	public void playSoundToPlayers(Sound sound, float pitch, boolean includingSpectators) {
		Set<Player> observers = Sets.newHashSet();
		observers.addAll(team1);
		observers.addAll(team2);
		if (includingSpectators) {
			observers.addAll(spectators);
		}
		for (Player all : observers) {
			if (all != null && all.isOnline()) {
				all.playSound(all.getLocation(), sound, 1f, pitch);
			}
		}
	}

	public void giveKitToPlayers() {
		Set<Player> observers = Sets.newHashSet();
		observers.addAll(team1Living);
		observers.addAll(team2Living);
		for (Player all : observers) {
			if (all != null && all.isOnline()) {
				// arena.getKit().give(all);
				KitManager.selectKit(all, arena.getKit().getId());
			}
		}
	}

	public void strikeLightningEffectToPlayers(Location l) {
		Set<Player> observers = Sets.newHashSet();
		observers.addAll(team1);
		observers.addAll(team2);
		EntityLightning lightning = new EntityLightning(((CraftWorld) l.getWorld()).getHandle(), l.getX(), l.getY(), l.getZ(), true);
		PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather(lightning);
		Random random = new Random();
		for (Player all : observers) {
			if (all != null && all.isOnline()) {
				PlayerUtil.sendPacket(all, packet);
				all.playSound(l, Sound.AMBIENCE_THUNDER, 10000.0F, 0.8F + random.nextFloat() * 0.2F);
				all.playSound(l, Sound.EXPLODE, 2.0F, 0.5F + random.nextFloat() * 0.2F);
			}
		}
	}

	public void clearInventoryToPlayers() {
		Set<Player> observers = Sets.newHashSet();
		observers.addAll(team1);
		observers.addAll(team2);
		for (Player all : observers) {
			if (all != null && all.isOnline()) {
				PlayerUtil.clearInventory(all);
			}
		}
	}

	public void healToPlayers() {
		Set<Player> observers = Sets.newHashSet();
		observers.addAll(team1);
		observers.addAll(team2);
		for (Player all : observers) {
			if (all != null && all.isOnline()) {
				PlayerUtil.treat(all);
				if (!all.getGameMode().equals(GameMode.SURVIVAL)) {
					all.setGameMode(GameMode.SURVIVAL);
				}
			}
		}
	}

	public void teleportSpawnToPlayers() {
		Location spawn1 = arena.getSpawn1().clone().add(0, 1, 0);
		Location spawn2 = arena.getSpawn2().clone().add(0, 1, 0);
		for (Player all : team1) {
			all.teleport(spawn1);
		}
		for (Player all : team2) {
			all.teleport(spawn2);
		}

	}

	public void unregisterPlayer(Player p) {
		team1.remove(p);
		team2.remove(p);
		team1Living.remove(p);
		team2Living.remove(p);
		removeSectator(p);
	}

	public void unregisterPlayerAll() {
		team1.clear();
		team2.clear();
		team1Living.clear();
		team2Living.clear();
		for (Player spectator : Sets.newHashSet(spectators)) {
			if (spectator != null && spectator.isOnline()) {
				removeSectator(spectator);
			}
		}
	}

	public boolean isInMatch(Player p) {
		return isFighting(p) || isSpectator(p);
	}

	public boolean isSpectator(Player p) {
		return spectators.contains(p);
	}

	public boolean isFighting(Player p) {
		return team1Living.contains(p) || team2Living.contains(p);
	}

	public boolean hasFought(Player p) {
		return team1.contains(p) || team2.contains(p);
	}

	public Set<Player> getTeam1() {
		return team1;
	}

	public Set<Player> getTeam1Living() {
		return team1Living;
	}

	public Set<Player> getTeam2() {
		return team2;
	}

	public Set<Player> getTeam2Living() {
		return team2Living;
	}

	public Arena getArena() {
		return arena;
	}

	public MatchState getMatchstate() {
		return matchstate;
	}

	public Set<Player> getSpectators() {
		return spectators;
	}

	private void updateTrackers() {
		Set<Player> observers = Sets.newHashSet();
		observers.addAll(team1);
		observers.addAll(team2);
		observers.addAll(spectators);
		for (Player all : observers) {
			if (all != null && all.isOnline()) {
				updateTrackersOn(all);
			}
		}
	}

	private void updateTrackersOn(Player p) {
		HashMap<Player, Boolean> shouldBeShown = new HashMap<>();
		for (Player all : Bukkit.getOnlinePlayers()) {
			all.hidePlayer(p);
			p.hidePlayer(all);
			shouldBeShown.put(all, false);
		}
		if (isInMatch(p)) {
			Set<Player> players = Sets.newHashSet();
			players.addAll(team1);
			players.addAll(team2);
			for (Player player : players) { // 戦ってる人
				if (!isSpectator(p)) {// pが観戦じゃなかったら player -> p
					player.showPlayer(p);
				}
				p.showPlayer(player);
				shouldBeShown.put(player, true);
			}
			if (isSpectator(p)) { // pが観戦だったら
				for (Player player : spectators) { // スペクテイター
					player.showPlayer(p); // spectator -> p
					p.showPlayer(player); // p -> spectator
					shouldBeShown.put(player, true);
				}
			}
		} // else
		for (Player all : Bukkit.getOnlinePlayers()) {
			if (PlayerState.isState(all, PlayerState.IN_LOBBY) || PlayerState.isState(all, PlayerState.IN_FFA)) {
				all.showPlayer(p);
				if (!isInMatch(p)) {
					p.showPlayer(all);
					shouldBeShown.put(all, true);
				}
			}
		}
		// test
		for (Player all : Bukkit.getOnlinePlayers()) {
			if (shouldBeShown.get(all)) {
				p.showPlayer(all);
			} else {
				p.hidePlayer(all);
			}
		}
	}

}
