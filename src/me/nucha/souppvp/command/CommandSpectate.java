package me.nucha.souppvp.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nucha.parties.PartyManager;
import me.nucha.souppvp.arenafight.match.Match;
import me.nucha.souppvp.arenafight.match.MatchManager;
import me.nucha.souppvp.language.LanguageManager;
import me.nucha.souppvp.player.PlayerState;

public class CommandSpectate implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cプレイヤーのみ実行可能です");
			return true;
		}
		Player p = (Player) sender;
		if (args.length == 1) {
			Player target = Bukkit.getPlayer(args[0]);
			if (target == null) {
				sender.sendMessage(LanguageManager.get(p, "command.error.player-not-found").replaceAll("%player", args[0]));
				return true;
			}
			if (PartyManager.isInParty(p)) {
				p.sendMessage(LanguageManager.get(p, "command.error.cannot-do-while-in-party"));
				return true;
			}
			if (!PlayerState.isState(p, PlayerState.IN_LOBBY)) {
				sender.sendMessage(LanguageManager.get(p, "command.error.can-do-in-lobby"));
				return true;
			}
			if (PlayerState.isState(target, PlayerState.IN_MATCH)) {
				Match match = MatchManager.getMatch(target);
				if (match.isFighting(p)) {
					sender.sendMessage(LanguageManager.get(p, "command.spectate.cannot-spec-yourself"));
					return true;
				}
				match.addSpectator(p);
			} else {
				sender.sendMessage(LanguageManager.get(p, "command.error.target-not-in-match").replaceAll("%target", target.getName()));
			}
			return true;
		}
		sender.sendMessage(LanguageManager.get(p, "command.spectate.usage"));
		return true;
	}

}
