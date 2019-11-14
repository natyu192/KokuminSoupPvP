package me.nucha.souppvp.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nucha.parties.PartyManager;
import me.nucha.souppvp.arenafight.duel.DuelManager;
import me.nucha.souppvp.language.LanguageManager;
import me.nucha.souppvp.nick.NickManager;
import me.nucha.souppvp.player.PlayerState;

public class CommandDuel implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("duel")) {
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
				if (p.equals(target)) {
					sender.sendMessage(LanguageManager.get(p, "command.duel.cannot-duel-yourself"));
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
				if (!PlayerState.isState(target, PlayerState.IN_LOBBY)) {
					sender.sendMessage(
							LanguageManager.get(p, "command.error.target-must-be-in-lobby").replaceAll("%target", NickManager.getName(p)));
					return true;
				}
				if (PartyManager.isInParty(target)) {
					sender.sendMessage(LanguageManager.get(p, "command.error.target-must-not-be-in-party").replaceAll("%target",
							NickManager.getName(p)));
					return true;
				}
				if (DuelManager.isPendingDuelTo(p, target)) {
					sender.sendMessage(
							LanguageManager.get(p, "command.duel.already-pending-duel").replaceAll("%target", NickManager.getName(p)));
					return true;
				}
				DuelManager.sendDuel(p, target);
				return true;
			}
			sender.sendMessage(LanguageManager.get(p, "command.duel.usage"));
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("accept")) {
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
				if (p.equals(target)) {
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
				if (!DuelManager.isPendingDuelTo(target, p)) {
					sender.sendMessage(
							LanguageManager.get(p, "command.accept.no-duel-request").replaceAll("%target", NickManager.getName(p)));
					return true;
				}
				if (!PlayerState.isState(target, PlayerState.IN_LOBBY)) {
					sender.sendMessage(
							LanguageManager.get(p, "command.error.target-must-be-in-lobby").replaceAll("%target", NickManager.getName(p)));
					return true;
				}
				if (PartyManager.isInParty(target)) {
					sender.sendMessage(LanguageManager.get(p, "command.error.target-must-not-be-in-party").replaceAll("%target",
							NickManager.getName(p)));
				}
				DuelManager.acceptDuel(p, target);
				return true;
			}
			sender.sendMessage(LanguageManager.get(p, "command.accept.usage"));
			return true;
		}
		return true;
	}

}
