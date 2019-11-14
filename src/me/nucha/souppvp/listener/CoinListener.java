package me.nucha.souppvp.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.nucha.kokumin.events.CoinChangeEvent;
import me.nucha.souppvp.util.ScoreboardUtils;

public class CoinListener implements Listener {

	@EventHandler
	public void onCoin(CoinChangeEvent event) {
		Player p = (Player) event.getPlayer();
		ScoreboardUtils.updateCoins(p);
	}

}
