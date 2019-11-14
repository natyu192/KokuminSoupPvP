package me.nucha.souppvp.listener.perk;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.nucha.souppvp.perk.PerkKillAndRun;
import me.nucha.souppvp.perk.PerkManager;
import me.nucha.souppvp.player.PlayerState;

public class KillAndRunListener implements Listener {

	@EventHandler
	public void onKill(PlayerDeathEvent event) {
		Player died = event.getEntity();
		if (!PlayerState.isState(died, PlayerState.IN_FFA)) {
			return;
		}
		if (died.getKiller() != null) {
			Player killer = died.getKiller();
			PerkKillAndRun perk = (PerkKillAndRun) PerkManager.getPerkById("kill-and-run");
			if (perk.hasPurchasedBy(killer)) {
				killer.removePotionEffect(PotionEffectType.SPEED);
				PotionEffect speed = new PotionEffect(PotionEffectType.SPEED,
						perk.getEffectDuration(killer) * 20, perk.getEffectAmplifier(killer) - 1);
				killer.addPotionEffect(speed);
			}
		}
	}

}
