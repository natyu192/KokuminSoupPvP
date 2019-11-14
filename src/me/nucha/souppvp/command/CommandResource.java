package me.nucha.souppvp.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.nucha.kokumin.utils.CustomItem;
import me.nucha.souppvp.resource.ResourceManager;
import me.nucha.souppvp.util.ItemUtil;

public class CommandResource implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("display")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("プレイヤーのみ実行可能です");
					return true;
				}
				Player p = (Player) sender;
				if (ResourceManager.isSeeing(p)) {
					ResourceManager.undisplayResources(p);
					p.sendMessage("§e資源の表示が元に戻りました");
				} else {
					ResourceManager.displayResources(p);
					p.sendMessage("§a資源がダイヤブロックとして表示されました");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("stick")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("プレイヤーのみ実行可能です");
					return true;
				}
				Player p = (Player) sender;
				ItemStack stick = new CustomItem(Material.STICK, 1, "§dResource Manager", "§7これを持ってブロックを右クリックすると", "§7資源として追加/削除できます");
				ItemUtil.setUndroppable(stick, true);
				p.getInventory().addItem(stick);
				p.sendMessage("§dResource Manager §aを手に入れました");
				return true;
			}
		}
		sender.sendMessage("§a/resource display §2- §a資源をダイヤブロックとして表示します");
		sender.sendMessage("§a/resource stick §2- §aResource Managerを手に入れます");
		return true;
	}

}
