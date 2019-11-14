package me.nucha.souppvp.listener.combattag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;
import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

import me.nucha.core.packet.PacketInfo;
import me.nucha.core.packet.PacketListener;
import me.nucha.core.reflect.Reflection;
import me.nucha.souppvp.SoupPvPPlugin;
import me.nucha.souppvp.kit.KitManager;
import me.nucha.souppvp.language.LanguageManager;
import me.nucha.souppvp.player.PlayerState;
import me.nucha.souppvp.util.LocationUtil;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig.EnumPlayerDigType;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;

public class CombatTagListener implements Listener {

	private static List<BlockFace> ALL_DIRECTIONS;

	private static HashMap<UUID, Set<Location>> previousUpdates;

	public CombatTagListener() {
		ALL_DIRECTIONS = Lists.newArrayList();
		ALL_DIRECTIONS.add(BlockFace.NORTH);
		ALL_DIRECTIONS.add(BlockFace.SOUTH);
		ALL_DIRECTIONS.add(BlockFace.EAST);
		ALL_DIRECTIONS.add(BlockFace.WEST);
		previousUpdates = new HashMap<>();
	}

	@EventHandler
	public void onAttack(EntityDamageByEntityEvent event) {
		if (event.isCancelled())
			return;
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (PlayerState.isState(p, PlayerState.IN_MATCH) || PlayerState.isState(p, PlayerState.SPECTATING_MATCH)) {
				return;
			}
			if (p.getGameMode().equals(GameMode.CREATIVE)) {
				return;
			}
			Player d = null;
			if (event.getDamager() instanceof Projectile) {
				Projectile projectile = (Projectile) event.getDamager();
				if (projectile.getShooter() != null && projectile.getShooter() instanceof Player) {
					d = (Player) projectile.getShooter();
				}
			}
			if (event.getDamager() instanceof Player) {
				d = (Player) event.getDamager();
			}
			if (d != null) {
				if (d.getGameMode().equals(GameMode.CREATIVE)) {
					return;
				}
				if (PlayerState.isState(d, PlayerState.IN_MATCH) || PlayerState.isState(d, PlayerState.SPECTATING_MATCH)) {
					return;
				}
				CombatTagManager.setTag(d, 30);
				if (CombatTagManager.getTagExpireSeconds(p, 0) < 15) {
					CombatTagManager.setTag(p, 15);
				}
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		CombatTagManager.removeTag(p);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		CombatTagManager.removeTag(p);
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player p = event.getPlayer();
		CombatTagManager.removeTag(p);
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getClickedBlock() != null) {
			Block b = event.getClickedBlock();
			if (previousUpdates.containsKey(p.getUniqueId()) && previousUpdates.get(p.getUniqueId()).contains(b.getLocation())) {
				p.sendBlockChange(b.getLocation(), Material.STAINED_GLASS, (byte) 2);
			}
		}
	}

	@EventHandler
	public void onEnterRegion(RegionEnterEvent event) {
		Player p = event.getPlayer();
		if (!CombatTagManager.hasTag(p)) {
			return;
		}
		if (p.getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		State state = event.getRegion().getFlag(DefaultFlag.PVP);
		if (state != null && state.equals(StateFlag.State.DENY)) {
			double tag = CombatTagManager.getTagExpireSeconds(p, 1);
			p.sendMessage(LanguageManager.get(p, "command.error.combat-tagged").replaceAll("%tag", String.valueOf(tag)));
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onExitRegion(RegionLeaveEvent event) {
		Player p = event.getPlayer();
		if (KitManager.hasKitSelected(p)) {
			return;
		}
		if (p.getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		if (PlayerState.isState(p, PlayerState.IN_FFA)) {
			State state = event.getRegion().getFlag(DefaultFlag.PVP);
			if (state != null && state.equals(StateFlag.State.DENY)) {
				p.sendMessage(LanguageManager.get(p, "ffa.select-kit-to-get-out"));
				p.teleport(LocationUtil.get("ffa-spawn"));
			}
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Location t = event.getTo();
		Location f = event.getFrom();
		if (t.getBlockX() == f.getBlockX() && t.getBlockY() == f.getBlockY() &&
				t.getBlockZ() == f.getBlockZ()) {
			return;
		}
		Player player = event.getPlayer();
		if (player.getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		sendForceFieldBlocks(player);
	}

	@EventHandler
	public void onGameMode(PlayerGameModeChangeEvent event) {
		Player p = event.getPlayer();
		if (event.getNewGameMode().equals(GameMode.CREATIVE)) {
			sendRealBlocks(p);
			CombatTagManager.removeTag(p);
		} else {
			sendForceFieldBlocks(p);
		}
	}

	public static void sendForceFieldBlocks(Player player) {
		// Asynchronously send block changes around player
		BukkitRunnable task = new BukkitRunnable() {
			@Override
			public void run() {
				// Stop processing if player has logged off
				UUID uuid = player.getUniqueId();
				if (player == null || !player.isOnline()) {
					previousUpdates.remove(uuid);
					return;
				}

				// Update the players force field perspective and find all blocks to stop spoofing
				Set<Location> changedBlocks = getChangedBlocks(player);
				Material forceFieldMaterial = Material.STAINED_GLASS;
				byte forceFieldMaterialDamage = 5;

				Set<Location> removeBlocks;
				if (previousUpdates.containsKey(uuid)) {
					removeBlocks = previousUpdates.get(uuid);
				} else {
					removeBlocks = new HashSet<>();
				}

				for (Location location : changedBlocks) {
					player.sendBlockChange(location, forceFieldMaterial, forceFieldMaterialDamage);
					removeBlocks.remove(location);
				}

				// Remove no longer used spoofed blocks
				for (Location location : removeBlocks) {
					Block block = location.getBlock();
					player.sendBlockChange(location, block.getType(), block.getData());
				}

				previousUpdates.put(uuid, changedBlocks);
			}
		};
		task.runTaskAsynchronously(SoupPvPPlugin.getInstance());
	}

	private static Set<Location> getChangedBlocks(Player player) {
		Set<Location> locations = new HashSet<>();

		// Do nothing if player is not tagged
		if (!CombatTagManager.hasTag(player) && PlayerState.isState(player, PlayerState.IN_FFA) && KitManager.hasKitSelected(player))
			return locations;

		// Find the radius around the player
		int r = 4;
		Location l = player.getLocation();
		Location loc1 = l.clone().add(r, 0, r);
		Location loc2 = l.clone().subtract(r, 0, r);
		int topBlockX = loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX();
		int bottomBlockX = loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX();
		int topBlockZ = loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ();
		int bottomBlockZ = loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ();

		// Iterate through all blocks surrounding the player
		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				// Location corresponding to current loop
				Location location = new Location(l.getWorld(), (double) x, l.getY(), (double) z);

				// PvP is enabled here, no need to do anything else
				if (isPvpEnabledAt(location))
					continue;

				// Check if PvP is enabled in a location surrounding this
				if (!isPvpSurrounding(location))
					continue;

				for (int i = -r; i < r; i++) {
					Location loc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());

					loc.setY(loc.getY() + i);

					// Do nothing if the block at the location is not air
					if (!loc.getBlock().getType().equals(Material.AIR))
						continue;

					// Add this location to locations
					locations.add(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
				}
			}
		}

		return locations;
	}

	private static boolean isPvpSurrounding(Location loc) {
		for (BlockFace direction : ALL_DIRECTIONS) {
			if (isPvpEnabledAt(loc.getBlock().getRelative(direction).getLocation())) {
				return true;
			}
		}

		return false;
	}

	private static boolean isPvpEnabledAt(Location loc) {
		StateFlag.State s = WGBukkit.getRegionManager(loc.getWorld()).getApplicableRegions(loc).getFlag(DefaultFlag.PVP);
		return s == null || s != StateFlag.State.DENY;
	}

	public static void sendRealBlocks(Player p) {
		if (previousUpdates.containsKey(p.getUniqueId())) {
			for (Location location : previousUpdates.get(p.getUniqueId())) {
				Block block = location.getBlock();
				p.sendBlockChange(location, block.getType(), block.getData());
			}
		}
	}

	public static class ForceFieldListener implements PacketListener {

		@Override
		public void playerSendPacket(PacketInfo packet) {
			if (packet.getPacket() instanceof PacketPlayInBlockPlace) {
				Player p = packet.getPlayer();
				if (!previousUpdates.containsKey(p.getUniqueId())) {
					return;
				}
				BlockPosition a = (BlockPosition) Reflection.getValue(packet.getPacket(), "b");
				int ax = a.getX();
				int ay = a.getY();
				int az = a.getZ();
				for (Location location : Lists.newArrayList(previousUpdates.get(p.getUniqueId()))) {
					location = location.getBlock().getLocation();
					int bx = location.getBlockX();
					int by = location.getBlockY();
					int bz = location.getBlockZ();
					if (ax == bx && ay == by && az == bz) {
						packet.setCancelled(true);
						return;
					}
				}
				return;
			}
			if (packet.getPacket() instanceof PacketPlayInBlockDig) {
				Player p = packet.getPlayer();
				if (!previousUpdates.containsKey(p.getUniqueId())) {
					return;
				}
				EnumPlayerDigType c = (EnumPlayerDigType) Reflection.getValue(packet.getPacket(), "c");
				if (c.equals(EnumPlayerDigType.STOP_DESTROY_BLOCK)) {
					BlockPosition a = (BlockPosition) Reflection.getValue(packet.getPacket(), "a");
					int ax = a.getX();
					int ay = a.getY();
					int az = a.getZ();
					for (Location location : Lists.newArrayList(previousUpdates.get(p.getUniqueId()))) {
						location = location.getBlock().getLocation();
						int bx = location.getBlockX();
						int by = location.getBlockY();
						int bz = location.getBlockZ();
						if (ax == bx && ay == by && az == bz) {
							p.sendBlockChange(location, Material.STAINED_GLASS, (byte) 4);
							packet.setCancelled(true);
							return;
						}
					}
				}
			}
		}

		@Override
		public void playerReceivePacket(PacketInfo packet) {

		}
	}
}
