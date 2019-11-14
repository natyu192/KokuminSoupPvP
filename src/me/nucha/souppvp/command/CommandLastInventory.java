package me.nucha.souppvp.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.nucha.souppvp.arenafight.match.MatchManager;

public class CommandLastInventory implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("§cプレイヤーのみ実行可能です");
				return true;
			}
			Player p = (Player) sender;
			Inventory inventory = MatchManager.getLastInventory(args[0]);
			if (inventory == null) {
				return true;
			}
			p.openInventory(inventory);
			return true;
		}
		sender.sendMessage("§cUsage: /lastinventory <player>");
		return true;
	}

}
