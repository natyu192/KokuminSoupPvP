package me.nucha.souppvp.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;

import me.nucha.kokumin.coin.Coin;
import me.nucha.souppvp.SoupPvPPlugin;
import me.nucha.souppvp.kit.KitManager;
import me.nucha.souppvp.language.LanguageManager;
import me.nucha.souppvp.listener.combattag.CombatTagManager;
import me.nucha.souppvp.nick.NickManager;
import me.nucha.souppvp.player.PlayerState;
import me.nucha.souppvp.util.ItemUtil;
import me.nucha.souppvp.util.LocationUtil;
import me.nucha.souppvp.util.PlayerDataUtil;
import me.nucha.souppvp.util.ScoreboardUtils;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;

public class CombatListener implements Listener {

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getAction().equals(Action.LEFT_CLICK_AIR) ||
				event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			PlayerDataUtil.add(p, "left-clicks");
		}
	}

	public static void onDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		event.setDeathMessage(null);
		if (PlayerState.isState(p, PlayerState.LAVA_CHALLENGE)) {
			event.getDrops().clear();
			return;
		}
		if (PlayerState.isState(p, PlayerState.IN_FFA)) {
			p.getWorld().strikeLightningEffect(p.getLocation());
		}
		PlayerDataUtil.add(p, "deaths");
		if (KitManager.hasKitSelected(p)) {
			KitManager.unselectKit(p);
		}
		if (p.getKiller() != null) {
			PlayerDataUtil.add(p.getKiller(), "kills");
			ScoreboardUtils.updateKills(p.getKiller());
			PlayerDataUtil.add(p.getKiller(), "xp", 150);
			ScoreboardUtils.updateXp(p.getKiller());
			Coin.addCoin(p.getKiller(), 30, "Kill");
			for (Player all : Bukkit.getOnlinePlayers()) {
				String deathLog = LanguageManager.get(p, "combat.be-killed")
						.replaceAll("%victim", NickManager.getName(p)).replaceAll("%killer", NickManager.getName(p.getKiller()));
				if (all.getName().equalsIgnoreCase(p.getName()) || all.getName().equalsIgnoreCase(p.getKiller().getName())) {
					deathLog.replaceAll("§", "l§");
				}
				all.sendMessage(deathLog);
			}
			Bukkit.getConsoleSender().sendMessage("[FFA] " + p.getName() + " was killed by " + p.getKiller().getName() + ".");
		} else {
			for (Player all : Bukkit.getOnlinePlayers()) {
				String deathLog = LanguageManager.get(p, "combat.died")
						.replaceAll("%victim", NickManager.getName(p));
				if (all.getName().equalsIgnoreCase(p.getName())) {
					deathLog.replaceAll("§", "l§");
				}
				all.sendMessage(deathLog);
			}
			Bukkit.getConsoleSender().sendMessage("[FFA] " + p.getName() + " died.");
		}
		if (PlayerState.isState(p, PlayerState.IN_FFA)) {
			List<ItemStack> drops = new ArrayList<ItemStack>(event.getDrops());
			Location location = p.getLocation();
			World world = p.getWorld();
			List<Item> itemsDropped = Lists.newArrayList();
			for (ItemStack item : drops) {
				if (item != null && item.getType() != Material.AIR && !ItemUtil.isUndroppable(item)) {
					itemsDropped.add(world.dropItemNaturally(location, item));
				}
			}
			event.getDrops().clear();
			BukkitRunnable removeDropsTask = new BukkitRunnable() {
				@Override
				public void run() {
					for (Item item : itemsDropped) {
						if (item != null) {
							item.remove();
						}
					}
				}
			};
			removeDropsTask.runTaskLater(SoupPvPPlugin.getInstance(), 100L);
		} else {
			event.getDrops().clear();
		}
		BukkitRunnable respawnTask = new BukkitRunnable() {
			@Override
			public void run() {
				if (p != null && p.isOnline()) {
					((CraftPlayer) p).getHandle().playerConnection
							.a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
				}
			}
		};
		respawnTask.runTaskLater(SoupPvPPlugin.getInstance(), 60L);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if (p.getGameMode() != GameMode.CREATIVE && CombatTagManager.hasTag(p) && p.getKiller() != null) {
			p.getWorld().strikeLightningEffect(p.getLocation());
			PlayerDataUtil.add(p, "deaths");
			PlayerDataUtil.add(p.getKiller(), "kills");
			ScoreboardUtils.updateKills(p.getKiller());
			PlayerDataUtil.add(p.getKiller(), "xp", 150);
			ScoreboardUtils.updateXp(p.getKiller());
			Coin.takeCoin(p, 100, "Logging out while fighting");
			Coin.addCoin(p.getKiller(), 30, "Kill");
			for (Player all : Bukkit.getOnlinePlayers()) {
				String deathLog = LanguageManager.get(p, "combat.be-killed")
						.replaceAll("%victim", NickManager.getName(p)).replaceAll("%killer", NickManager.getName(p.getKiller()));
				if (all.getName().equalsIgnoreCase(p.getName()) || all.getName().equalsIgnoreCase(p.getKiller().getName())) {
					deathLog.replaceAll("§", "l§");
				}
				deathLog += " (Logged out)";
				all.sendMessage(deathLog);
			}
			Bukkit.getConsoleSender().sendMessage("[FFA] " + p.getName() + " was killed by " + p.getKiller().getName() + ". (Logged out)");
			if (PlayerState.isState(p, PlayerState.IN_FFA)) {
				List<ItemStack> drops = Lists.newArrayList();
				drops.addAll(Arrays.asList(p.getInventory().getContents()));
				drops.addAll(Arrays.asList(p.getInventory().getArmorContents()));
				Location location = p.getLocation();
				World world = p.getWorld();
				List<Item> itemsDropped = Lists.newArrayList();
				for (ItemStack item : drops) {
					if (item != null && item.getType() != Material.AIR && !ItemUtil.isUndroppable(item)) {
						itemsDropped.add(world.dropItemNaturally(location, item));
					}
				}
				BukkitRunnable removeDropsTask = new BukkitRunnable() {
					@Override
					public void run() {
						for (Item item : itemsDropped) {
							if (item != null) {
								item.remove();
							}
						}
					}
				};
				removeDropsTask.runTaskLater(SoupPvPPlugin.getInstance(), 100L);
			}
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player p = event.getPlayer();
		if (PlayerState.isState(p, PlayerState.LAVA_CHALLENGE) && LocationUtil.isSet("lava-challenge")) {
			event.setRespawnLocation(LocationUtil.get("lava-challenge"));
		}
		if (PlayerState.isState(p, PlayerState.IN_FFA) && LocationUtil.isSet("ffa-spawn")) {
			event.setRespawnLocation(LocationUtil.get("ffa-spawn"));
		}
		if (PlayerState.isState(p, PlayerState.IN_LOBBY) && LocationUtil.isSet("lobby")) {
			event.setRespawnLocation(LocationUtil.get("lobby"));
		}
	}

	@EventHandler
	public void onVoid(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player p = (Player) event.getEntity();
		if (PlayerState.isState(p, PlayerState.IN_LOBBY)) {
			event.setCancelled(true);
		}
		if (event.getCause().equals(DamageCause.THORNS)) {
			event.setCancelled(true);
		}
		if (event.getCause().equals(DamageCause.VOID)) {
			if (PlayerState.isState(p, PlayerState.IN_FFA)) {
				event.setDamage(p.getMaxHealth());
			} else if (PlayerState.isState(p, PlayerState.IN_LOBBY)) {
				event.setCancelled(true);
				p.teleport(LocationUtil.get("lobby"));
			}
		}
	}
}
