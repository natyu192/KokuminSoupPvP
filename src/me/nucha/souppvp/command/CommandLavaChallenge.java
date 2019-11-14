package me.nucha.souppvp.command;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nucha.kokumin.coin.Coin;
import me.nucha.souppvp.language.LanguageManager;
import me.nucha.souppvp.nick.NickManager;
import me.nucha.souppvp.player.PlayerState;
import me.nucha.souppvp.player.PlayerUtil;
import me.nucha.souppvp.util.PlayerDataUtil;

public class CommandLavaChallenge implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1) {
			Player t = Bukkit.getPlayer(args[0]);
			if (t == null) {
				return true;
			}
			if (PlayerState.isState(t, PlayerState.LAVA_CHALLENGE)) {
				PlayerUtil.leaveLavaChallenge(t);
				Coin.addCoin(t, 500, LanguageManager.get(t, "lava-challenge.addcoin-message"));
				PlayerDataUtil.add(t, "xp", 1000);
				t.sendMessage(LanguageManager.get(t, "lava-challenge.succeed"));
				t.playSound(t.getLocation(), Sound.LEVEL_UP, 1f, 0.5f);
				for (Player all : Bukkit.getOnlinePlayers()) {
					if (!all.getName().equalsIgnoreCase(t.getName())) {
						all.sendMessage(
								LanguageManager.get(all, "lava-challenge.succeed-broadcast").replaceAll("%player", NickManager.getName(t)));
					}
				}
			}
			return true;
		}
		sender.sendMessage("§c/lavachallengesucceed <player>");
		return true;
	}

}
