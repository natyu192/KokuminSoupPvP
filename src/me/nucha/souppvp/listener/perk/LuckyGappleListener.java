package me.nucha.souppvp.listener.perk;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import me.nucha.souppvp.perk.PerkLuckyGapple;
import me.nucha.souppvp.perk.PerkManager;
import me.nucha.souppvp.player.PlayerState;

public class LuckyGappleListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onKill(PlayerDeathEvent event) {
		Player died = event.getEntity();
		if (!PlayerState.isState(died, PlayerState.IN_FFA)) {
			return;
		}
		if (died.getKiller() != null) {
			Player killer = died.getKiller();
			PerkLuckyGapple perk = (PerkLuckyGapple) PerkManager.getPerkById("lucky-gapple");
			if (perk.hasPurchasedBy(killer)) {
				int chance = perk.getChance(killer);
				if (Math.random() * 100 < chance) {
					ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE);
					Location location = died.getLocation();
					World world = location.getWorld();
					world.dropItemNaturally(location, gapple);
				}
			}
		}

	}

}
