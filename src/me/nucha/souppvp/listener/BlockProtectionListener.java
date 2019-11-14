package me.nucha.souppvp.listener;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class BlockProtectionListener implements Listener {

	@EventHandler
	public void onHanging(HangingBreakByEntityEvent event) {
		if (event.getRemover() instanceof Player) {
			Player p = (Player) event.getRemover();
			if (!p.getGameMode().equals(GameMode.CREATIVE)) {
				event.setCancelled(true);
			}
		} else {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onHanging2(PlayerInteractEntityEvent event) {
		if (event.getRightClicked().getType() == EntityType.ITEM_FRAME
				|| event.getRightClicked().getType() == EntityType.PAINTING) {
			Player p = event.getPlayer();
			if (!p.getGameMode().equals(GameMode.CREATIVE)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onHanging3(EntityDamageByEntityEvent event) {
		if (event.getEntity().getType() == EntityType.ITEM_FRAME
				|| event.getEntity().getType() == EntityType.PAINTING) {
			if (event.getDamager() instanceof Player) {
				Player p = (Player) event.getDamager();
				if (!p.getGameMode().equals(GameMode.CREATIVE)) {
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(true);
			}
		}
	}

	/*@EventHandler
	public void onPaintBreak(PaintingBreakByEntityEvent event) {
		if (event.getRemover() instanceof Player) {
			Player p = (Player) event.getRemover();
			if (!p.getGameMode().equals(GameMode.CREATIVE)) {
				event.setCancelled(true);
			}
		} else {
			event.setCancelled(true);
		}
	}*/

	@EventHandler
	public void onArmorStand(EntityDamageByEntityEvent event) {
		if (event.getEntity().getType().equals(EntityType.ARMOR_STAND)) {
			if (!(event.getDamager() instanceof Player)) {
				event.setCancelled(true);
				return;
			}
			Player p = (Player) event.getDamager();
			if (!p.getGameMode().equals(GameMode.CREATIVE)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onArmorStand2(PlayerInteractAtEntityEvent event) {
		if (event.getRightClicked().getType().equals(EntityType.ARMOR_STAND)) {
			Player p = event.getPlayer();
			if (!p.getGameMode().equals(GameMode.CREATIVE)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onWeather(WeatherChangeEvent event) {
		World w = event.getWorld();
		if (!w.isThundering() || !w.hasStorm()) {
			event.setCancelled(true);
		}
	}

}
