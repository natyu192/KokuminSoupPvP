package me.nucha.souppvp.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.nucha.souppvp.SoupPvPPlugin;

public class JumpPadListener implements Listener {

	private List<String> nofall;
	private SoupPvPPlugin plugin;

	public JumpPadListener(SoupPvPPlugin plugin) {
		nofall = new ArrayList<String>();
		this.plugin = plugin;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getAction() == Action.PHYSICAL) {
			Block ironblock = event.getClickedBlock().getLocation().clone().add(0, -1, 0).getBlock();
			if (ironblock.getType() == Material.IRON_BLOCK) {
				Vector v = p.getLocation().getDirection();
				v = v.multiply(2.4D).setY(0.8D);
				p.setVelocity(v);
				p.getWorld().playSound(p.getLocation(), Sound.BLAZE_HIT, 1, 2);
				nofall.add(p.getName());
				BukkitRunnable runnable = new BukkitRunnable() {
					public void run() {
						nofall.remove(p.getName());
					};
				};
				runnable.runTaskLater(plugin, 20 * 5);
			}
			if (ironblock.getType() == Material.GOLD_BLOCK) {
				Vector v = p.getLocation().getDirection();
				v = v.multiply(3.5D).setY(1.5D);
				p.setVelocity(v);
				p.getWorld().playSound(p.getLocation(), Sound.BLAZE_HIT, 1, 2);
				nofall.add(p.getName());
				BukkitRunnable runnable = new BukkitRunnable() {
					public void run() {
						nofall.remove(p.getName());
					};
				};
				runnable.runTaskLater(plugin, 20 * 5);
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (event.getCause() == DamageCause.FALL && nofall.contains(p.getName())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		nofall.remove(event.getPlayer().getName());
	}

}
