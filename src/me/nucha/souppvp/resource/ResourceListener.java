package me.nucha.souppvp.resource;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import me.nucha.souppvp.player.PlayerState;
import me.nucha.souppvp.util.ItemUtil;
import me.nucha.souppvp.util.LocationUtil;

public class ResourceListener implements Listener {

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		Block b = event.getBlock();
		if (!PlayerState.isState(p, PlayerState.IN_FFA) || p.getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		ResourceManager.gatherResource(p, b);
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (!p.hasPermission("souppvp.resource")) {
			return;
		}
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Block b = event.getClickedBlock();
			ItemStack item = event.getItem();
			if (ItemUtil.getDisplayName(item).equalsIgnoreCase("§dResource Manager")) {
				if (ResourceManager.isResource(b.getLocation())) {
					ResourceManager.removeResource(b);
					p.sendMessage("§8[§dResource§8] §e資源§6(" + LocationUtil.locationToString(b.getLocation(), false) + ")§eを削除しました");
				} else {
					ResourceManager.addResource(b);
					p.sendMessage("§8[§dResource§8] §a資源§2(" + LocationUtil.locationToString(b.getLocation(), false) + ")§eを追加しました");
				}
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		ResourceManager.undisplayResources(event.getPlayer());
	}

}
