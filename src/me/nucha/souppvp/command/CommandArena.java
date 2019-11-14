package me.nucha.souppvp.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nucha.souppvp.arenafight.arena.Arena;
import me.nucha.souppvp.arenafight.arena.ArenaManager;
import me.nucha.souppvp.kit.Kit;
import me.nucha.souppvp.kit.KitManager;

public class CommandArena implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("arenas")) {
			List<Arena> arenas = ArenaManager.getArenas();
			if (arenas.size() > 0) {
				sender.sendMessage("§a----------- Arenas -----------");
				for (Arena arena : arenas) {
					String msg = "§6- §e" + arena.getName();
					if (sender.isOp()) {
						msg += " §7(" + arena.getId() + ")";
					}
					sender.sendMessage(msg);
				}
			} else {
				sender.sendMessage("§cアリーナが１つも登録されていません");
			}
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("arena")) {
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("create")) {
					if (ArenaManager.getArenaById(args[1]) != null) {
						sender.sendMessage("§6" + args[1] + " §cというIDのアリーナは既に存在します");
						return true;
					}
					Arena arena = new Arena(args[1], args[1]);
					ArenaManager.registerArena(arena);
					sender.sendMessage("§b" + args[1] + " §aというアリーナを作成しました");
					return true;
				}
				if (args[0].equalsIgnoreCase("remove")) {
					Arena arena = ArenaManager.getArenaById(args[1]);
					if (arena == null) {
						sender.sendMessage("§6" + args[1] + " §cというIDのアリーナは存在しません");
						return true;
					}
					ArenaManager.unregisterArena(arena);
					sender.sendMessage("§b" + args[1] + " §eというアリーナを削除しました");
					return true;
				}
				if (args[0].equalsIgnoreCase("setspawn1")) {
					if (!(sender instanceof Player)) {
						sender.sendMessage("§cプレイヤーのみ実行可能です");
						return true;
					}
					Player p = (Player) sender;
					Arena arena = ArenaManager.getArenaById(args[1]);
					if (arena == null) {
						sender.sendMessage("§6" + args[1] + " §cというIDのアリーナは存在しません");
						return true;
					}
					arena.setSpawn1(p.getLocation());
					sender.sendMessage("§b" + args[1] + " §aというアリーナのスポーン1を現在位置に設定しました");
					return true;
				}
				if (args[0].equalsIgnoreCase("setspawn2")) {
					if (!(sender instanceof Player)) {
						sender.sendMessage("§cプレイヤーのみ実行可能です");
						return true;
					}
					Player p = (Player) sender;
					Arena arena = ArenaManager.getArenaById(args[1]);
					if (arena == null) {
						sender.sendMessage("§6" + args[1] + " §cというIDのアリーナは存在しません");
						return true;
					}
					arena.setSpawn2(p.getLocation());
					sender.sendMessage("§b" + args[1] + " §aというアリーナのスポーン2を現在位置に設定しました");
					return true;
				}
				if (args[0].equalsIgnoreCase("toggle")) {
					String id = args[1];
					Arena arena = ArenaManager.getArenaById(id);
					if (arena == null) {
						sender.sendMessage("§6" + id + " §cというIDのアリーナは存在しません");
						return true;
					}
					arena.setEnabled(!arena.isEnabled());
					if (arena.isEnabled()) {
						sender.sendMessage("§e" + arena.getName() + " §bというアリーナを§a有効化§bしました");
					} else {
						sender.sendMessage("§e" + arena.getName() + " §bというアリーナを§c無効化§bしました");
					}
					return true;
				}
			}
			if (args.length == 3) {
				if (args[0].equalsIgnoreCase("setname")) {
					Arena arena = ArenaManager.getArenaById(args[1]);
					if (arena == null) {
						sender.sendMessage("§6" + args[1] + " §cというIDのアリーナは存在しません");
						return true;
					}
					String newName = args[2].replaceAll("-", " ");
					arena.setName(newName);
					sender.sendMessage("§b" + args[1] + " §aというアリーナの表示名を " + newName + " にしました");
					return true;
				}
				if (args[0].equalsIgnoreCase("setkit")) {
					Arena arena = ArenaManager.getArenaById(args[1]);
					if (arena == null) {
						sender.sendMessage("§6" + args[1] + " §cというIDのアリーナは存在しません");
						return true;
					}
					Kit kit = KitManager.getKitById(args[2]);
					if (kit == null) {
						sender.sendMessage("§6" + args[2] + " §cというIDのキットは存在しません");
						return true;
					}
					arena.setKit(kit);
					sender.sendMessage("§b" + args[1] + " §aというアリーナのキットを " + kit.getName() + " にしました");
					return true;
				}
			}
			sender.sendMessage("§a----------- アリーナ設定コマンド -----------");
			sender.sendMessage("§e/arena create <arenaId> §6--- §eアリーナを作成します");
			sender.sendMessage("§e/arena remove <arenaId> §6--- §eアリーナを削除します");
			sender.sendMessage("§e/arena setname <arenaId> <newName(-でスペース)> §6--- §eアリーナの表示名を変更します");
			sender.sendMessage("§e/arena setspawn1 <arenaId> §6--- §eアリーナのスポーン1を現在位置に設定します");
			sender.sendMessage("§e/arena setspawn2 <arenaId> §6--- §eアリーナのスポーン2を現在位置に設定します");
			sender.sendMessage("§e/arena setkit <arenaId> <kitId> §6--- §eアリーナのキットを設定します");
			sender.sendMessage("§e/arena toggle <arenaId> §6--- §eアリーナの有効/無効を切り替えます");
			return true;
		}
		return true;
	}

}
