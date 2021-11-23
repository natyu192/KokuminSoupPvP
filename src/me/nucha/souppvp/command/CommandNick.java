package me.nucha.souppvp.command;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nucha.souppvp.language.LanguageManager;
import me.nucha.souppvp.nick.NickGenerator;
import me.nucha.souppvp.nick.NickManager;
import me.nucha.souppvp.player.PlayerState;
import me.nucha.souppvp.util.CooldownUtil;

public class CommandNick implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("nick")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("§cプレイヤーのみ実行可能です");
				return true;
			}
			Player p = (Player) sender;
			if (!PlayerState.isState(p, PlayerState.IN_LOBBY) && !PlayerState.isState(p, PlayerState.IN_FFA)) {
				p.sendMessage(LanguageManager.get(p, "command.error.can-do-in-lobby-or-ffa"));
				return true;
			}
			if (CooldownUtil.hasCooldown(p, "nick")) {
				double cooldown = CooldownUtil.getCooldownExpireSeconds(p, 1, "nick");
				p.sendMessage("§8[§aNick§8] §r" + LanguageManager.get(p, "cooldown").replaceAll("%cooldown", String.valueOf(cooldown)));
				return true;
			}
			String nick = NickGenerator.generate();
			if (args.length == 1 && sender.hasPermission("souppvp.nick.set")) {
				nick = args[0];
				if (nick.length() > 16 || nick.length() < 4) {
					p.sendMessage("§8[§aNick§8] §r" + LanguageManager.get(p, "command.nick.length-error"));
					return true;
				}
				if (NickManager.isNicknameUsed(nick)) {
					p.sendMessage("§8[§aNick§8] §r" + LanguageManager.get(p, "command.nick.name-is-already-used"));
					return true;
				}
			}
			if (NickManager.isNicked(p)) {
				String prevNick = NickManager.getNickname(p);
				for (Player all : Bukkit.getOnlinePlayers()) {
					if (all.hasPermission("souppvp.nick.random")) {
						all.sendMessage("§8[§aNick§8] §e" + sender.getName() + " has unnicked!");
					}
					all.sendMessage("§e" + prevNick + " left the game.");
					NickManager.unNick(p, false);
				}
			}
			NickManager.setNickname(p, nick);
			for (Player all : Bukkit.getOnlinePlayers()) {
				if (all.hasPermission("souppvp.nick.random")) {
					all.sendMessage("§8[§aNick§8] §e" + sender.getName() + " has nicked as " + nick + "!");
				}
				all.sendMessage("§e" + nick + " joined the game.");
			}
			CooldownUtil.setCooldown(p, 10, "nick");
		}
		if (cmd.getName().equalsIgnoreCase("unnick")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("§cプレイヤーのみ実行可能です");
				return true;
			}
			Player p = (Player) sender;
			if (!PlayerState.isState(p, PlayerState.IN_LOBBY) && !PlayerState.isState(p, PlayerState.IN_FFA)) {
				p.sendMessage("§cロビー、FFAでのみ使用可能です");
				return true;
			}
			if (CooldownUtil.hasCooldown(p, "nick")) {
				double cooldown = CooldownUtil.getCooldownExpireSeconds(p, 1, "nick");
				p.sendMessage("§8[§aNick§8] §r" + LanguageManager.get(p, "cooldown").replaceAll("%cooldown", String.valueOf(cooldown)));
				return true;
			}
			if (!NickManager.isNicked(p)) {
				sender.sendMessage(LanguageManager.get(p, "command.nick.not-nicked"));
				return true;
			}
			String nick = NickManager.getNickname(p);
			NickManager.unNick(p, true);
			for (Player all : Bukkit.getOnlinePlayers()) {
				if (all.hasPermission("souppvp.nick.random")) {
					all.sendMessage("§8[§aNick§8] §e" + sender.getName() + " has unnicked!");
				}
				all.sendMessage("§e" + nick + " left the game.");
			}
			CooldownUtil.setCooldown(p, 10, "nick");
		}
		if (cmd.getName().equalsIgnoreCase("nicklist")) {
			sender.sendMessage("§a---------- Nicknames used by someone ----------");
			HashMap<Player, String> nicknamesUsed = NickManager.getNicknamesUsed();
			HashMap<Player, String> displayNames = NickManager.getDisplayNames();
			for (Player p : nicknamesUsed.keySet()) {
				String nick = nicknamesUsed.get(p);
				String displayName = displayNames.get(p);
				sender.sendMessage("§b" + displayName + " §3--- §b" + nick);
			}
		}
		return true;
	}

}
