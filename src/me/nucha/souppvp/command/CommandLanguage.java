package me.nucha.souppvp.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nucha.souppvp.language.Language;
import me.nucha.souppvp.language.LanguageManager;

public class CommandLanguage implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("プレイヤーのみ実行可能です");
			return true;
		}
		Player p = (Player) sender;
		if (args.length == 1) {
			LanguageManager.setLanguage(p, Language.parse(args[0]));
			sender.sendMessage(
					LanguageManager.get(p, "command.language.set").replaceAll("%language", LanguageManager.getLanguage(p).name()));
			return true;
		}
		sender.sendMessage(LanguageManager.get(p, "command.language.usage"));
		return true;
	}

}
