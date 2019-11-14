package me.nucha.souppvp.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandSoup implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("§cプレイヤーのみ実行可能です");
				return true;
			}
			Player p = (Player) sender;
			giveSoup(p);
			p.sendMessage("§aThere you go!");
		}
		if (args.length == 1) {
			Player target = Bukkit.getPlayer(args[0]);
			if (target == null) {
				sender.sendMessage("§6" + args[0] + " §cはオフラインです");
				return true;
			}
			sender.sendMessage("§e" + target.getName() + " §aにスープを与えました");
			giveSoup(target);
			target.sendMessage("§aThere you go!");
		}
		return true;
	}

	private void giveSoup(Player p) {
		ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);
		for (int i = 0; i < 36; i++) {
			p.getInventory().addItem(soup);
		}
	}

}
