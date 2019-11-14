package me.nucha.souppvp.command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nucha.souppvp.kit.Kit;
import me.nucha.souppvp.kit.KitManager;

public class CommandKit implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("kits")) {
			List<Kit> kits = KitManager.getKits();
			if (kits.size() > 0) {
				sender.sendMessage("§a----------- Kits -----------");
				for (Kit kit : kits) {
					String msg = "§6- §e" + kit.getName();
					if (sender.isOp()) {
						msg += " §7(" + kit.getId() + ")";
					}
					sender.sendMessage(msg);
				}
			} else {
				sender.sendMessage("§cキットが１つも登録されていません");
			}
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("kit")) {
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("load")) {
					if (!(sender instanceof Player)) {
						sender.sendMessage("§cプレイヤーのみ実行可能です");
						return true;
					}
					Player p = (Player) sender;
					String id = args[1];
					Kit theKit = KitManager.getKitById(id);
					if (theKit == null) {
						sender.sendMessage("§6" + id + " §cというIDのキットは存在しません");
						return true;
					}
					theKit.give(p);
					sender.sendMessage("§e" + theKit.getName() + " §bというキットを読み込みました");
					return true;
				}
				if (args[0].equalsIgnoreCase("toggle")) {
					String id = args[1];
					Kit theKit = KitManager.getKitById(id);
					if (theKit == null) {
						sender.sendMessage("§6" + id + " §cというIDのキットは存在しません");
						return true;
					}
					KitManager.setEnabled(id, !KitManager.isEnabled(id));
					if (KitManager.isEnabled(id)) {
						sender.sendMessage("§e" + theKit.getName() + " §bというキットを§a有効化§bしました");
					} else {
						sender.sendMessage("§e" + theKit.getName() + " §bというキットを§c無効化§bしました");
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("forceunlock")) {
					String id = args[1];
					Kit theKit = KitManager.getKitById(id);
					if (theKit == null) {
						sender.sendMessage("§6" + id + " §cというIDのキットは存在しません");
						return true;
					}
					KitManager.setForceUnlocked(id, !KitManager.isForceUnlocked(id));
					if (KitManager.isForceUnlocked(id)) {
						sender.sendMessage("§e" + theKit.getName() + " §bというキットを§a全員使用可§bにしました");
					} else {
						sender.sendMessage("§e" + theKit.getName() + " §bというキットの§c全員使用可を解除§bしました");
					}
					return true;
				}
			}
			if (args.length == 3) {
				if (args[0].equalsIgnoreCase("load")) {
					Player target = Bukkit.getPlayer(args[2]);
					if (target == null) {
						sender.sendMessage("§6" + args[2] + " §cはオフラインです");
						return true;
					}
					String id = args[1];
					Kit theKit = KitManager.getKitById(id);
					if (theKit == null) {
						sender.sendMessage("§6" + id + " §cというIDのキットは存在しません");
						return true;
					}
					theKit.give(target);
					target.sendMessage("§e" + theKit.getName() + " §bというキットを読み込みました");
					return true;
				}
			}
			sender.sendMessage("§a----------- キット設定コマンド -----------");
			sender.sendMessage("§e/kit load <id> [player] §6--- §eキットを読み込みます");
			sender.sendMessage("§e/kit toggle <id> §6--- §eキットの有効/無効を切り替えます");
			sender.sendMessage("§e/kit forceunlock <id> §6--- §eキットの全員使用可能の有効/無効を切り替えます");
			return true;
		}
		return true;
	}

}
