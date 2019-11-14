package me.nucha.souppvp.listener;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.nucha.souppvp.SoupPvPPlugin;
import me.nucha.souppvp.language.LanguageManager;
import me.nucha.souppvp.player.PlayerState;
import me.nucha.souppvp.util.ConfigUtil;

public class FFAListener implements Listener {

	private static Set<Location> blocksPlaced;

	public FFAListener() {
		blocksPlaced = new HashSet<>();
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Player p = event.getPlayer();
		if (p.getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		if (PlayerState.isState(p, PlayerState.IN_FFA)) {
			if (!ConfigUtil.FFA_BUILD) {
				event.setCancelled(true);
				return;
			}
			Block b = event.getBlock();
			Location loc = b.getLocation();
			if (!blocksPlaced.contains(loc)) {
				event.setCancelled(true);
				return;
			}
			if (loc.getBlockY() > ConfigUtil.FFA_BUILD_HEIGHT) {
				event.setCancelled(true);
				p.sendMessage(LanguageManager.get(p, "ffa.build-limit").replaceAll("%limit", String.valueOf(ConfigUtil.FFA_BUILD_HEIGHT)));
				return;
			}
			blocksPlaced.remove(loc);
		} else {
			if (!p.getGameMode().equals(GameMode.CREATIVE)) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Player p = event.getPlayer();
		if (p.getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		if (PlayerState.isState(p, PlayerState.IN_FFA)) {
			if (!ConfigUtil.FFA_BUILD) {
				event.setCancelled(true);
				return;
			}
			Block b = event.getBlock();
			if (b.getLocation().getBlockY() > ConfigUtil.FFA_BUILD_HEIGHT) {
				event.setCancelled(true);
				p.sendMessage(LanguageManager.get(p, "ffa.build-limit").replaceAll("%limit", String.valueOf(ConfigUtil.FFA_BUILD_HEIGHT)));
				return;
			}
			Location loc = b.getLocation();
			if (!blocksPlaced.contains(loc)) {
				blocksPlaced.add(loc);
			}
			BukkitRunnable breakTask = new BukkitRunnable() {
				@Override
				public void run() {
					if (!loc.getBlock().getType().equals(Material.AIR)) {
						if (blocksPlaced.contains(loc)) {
							if (!b.isLiquid()) {
								for (Player all : Bukkit.getOnlinePlayers()) {
									all.playEffect(loc, Effect.STEP_SOUND, b.getType().getId());
								}
								loc.getBlock().setType(Material.AIR);
							} else {
								loc.getBlock().setType(Material.SPONGE);
								Bukkit.getScheduler().runTask(SoupPvPPlugin.getInstance(), new Runnable() {
									@Override
									public void run() {
										loc.getBlock().setType(Material.AIR);
									}
								});
							}
							blocksPlaced.remove(loc);
						}
					}
				}
			};
			breakTask.runTaskLater(SoupPvPPlugin.getInstance(), 200L);
		} else {
			if (!p.getGameMode().equals(GameMode.CREATIVE)) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onBucket(PlayerBucketEmptyEvent event) {
		Player p = event.getPlayer();
		if (event.isCancelled()) {
			return;
		}
		if (PlayerState.isState(p, PlayerState.IN_FFA) && ConfigUtil.FFA_BUILD) {
			if (p.getGameMode().equals(GameMode.CREATIVE)) {
				return;
			}
			Location placed = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();
			if (placed.getBlockY() > ConfigUtil.FFA_BUILD_HEIGHT) {
				event.setCancelled(true);
				p.sendMessage(LanguageManager.get(p, "ffa.build-limit").replaceAll("%limit", String.valueOf(ConfigUtil.FFA_BUILD_HEIGHT)));
				return;
			}
			if (placed.getBlock().getType() != Material.STATIONARY_WATER) {
				blocksPlaced.add(placed);
				Bukkit.getScheduler().runTaskLater(SoupPvPPlugin.getInstance(), new Runnable() {
					@Override
					public void run() {
						if (blocksPlaced.contains(placed)) {
							placed.getBlock().setType(Material.SPONGE);
							Bukkit.getScheduler().runTask(SoupPvPPlugin.getInstance(), new Runnable() {
								@Override
								public void run() {
									placed.getBlock().setType(Material.AIR);
								}
							});
							blocksPlaced.remove(placed);
						}
					}
				}, 200L);
			}
		} else {
			if (!p.getGameMode().equals(GameMode.CREATIVE)) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if (p.getItemInHand() != null) {
					ItemStack item = p.getItemInHand();
					if (item.getType() == Material.WOOD_HOE
							|| item.getType() == Material.STONE_HOE
							|| item.getType() == Material.IRON_HOE
							|| item.getType() == Material.DIAMOND_HOE
							|| item.getType() == Material.GOLD_HOE) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onEatGapple(PlayerItemConsumeEvent event) {
		if (event.getItem().getType().equals(Material.GOLDEN_APPLE)) {
			Player p = event.getPlayer();
			if (PlayerState.isState(p, PlayerState.IN_FFA) && ConfigUtil.FFA_BUILD) {
				p.removePotionEffect(PotionEffectType.REGENERATION);
				p.removePotionEffect(PotionEffectType.ABSORPTION);
				PotionEffect regen = new PotionEffect(PotionEffectType.REGENERATION, 100, 2);
				PotionEffect abs = new PotionEffect(PotionEffectType.ABSORPTION, 120 * 20, 0);
				p.addPotionEffect(regen);
				p.addPotionEffect(abs);
			}
		}
	}

	@EventHandler
	public void onGamemode(PlayerGameModeChangeEvent event) {
		Player p = event.getPlayer();
		if (PlayerState.isState(p, PlayerState.IN_FFA)) {
			if (event.getNewGameMode().equals(GameMode.CREATIVE)) {
				p.sendMessage("§8[§aFFA§8] §a" + event.getNewGameMode().name() + "になったため、建築ができるようになりました");
			} else {
				p.sendMessage("§8[§aFFA§8] §e" + event.getNewGameMode().name() + "になったため、建築ができなくなりました");
			}
		}
	}

	public static void removeBlocks() {
		for (Location location : blocksPlaced) {
			location.getBlock().setType(Material.AIR);
		}
	}

}
