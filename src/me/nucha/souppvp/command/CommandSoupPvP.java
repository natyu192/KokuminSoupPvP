package me.nucha.souppvp.command;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.nucha.souppvp.SoupPvPPlugin;
import me.nucha.souppvp.leaderboard.LeaderboardManager;
import me.nucha.souppvp.player.PlayerUtil;
import me.nucha.souppvp.util.ConfigUtil;
import me.nucha.souppvp.util.LocationUtil;
import me.nucha.souppvp.util.PlayerDataUtil;

public class CommandSoupPvP implements CommandExecutor {

	private HashMap<String, String> locationIdAndNames;
	private HashMap<String, OfflinePlayer> resetStatsConfirm;
	private boolean lbupdating;

	public CommandSoupPvP() {
		this.locationIdAndNames = new HashMap<>();
		locationIdAndNames.put("lobby", "ロビー");
		locationIdAndNames.put("ffa-spawn", "FFAのリスポーン地点");
		locationIdAndNames.put("lava-challenge", "Lava Challengeのリスポーン地点");
		locationIdAndNames.put("npc-stats", "Stats NPCのスポーン位置");
		locationIdAndNames.put("npc-shop", "Shop NPCのスポーン位置");
		locationIdAndNames.put("holo-welcome", "Welcome Hologramの位置");
		locationIdAndNames.put("lb-kills", "キル数ランキングの位置");
		locationIdAndNames.put("lb-deaths", "死亡数ランキングの位置");
		locationIdAndNames.put("lb-soups-used", "スープ使用数ランキングの位置");
		locationIdAndNames.put("lb-left-clicks", "左クリック数ランキングの位置");
		locationIdAndNames.put("lb-xp", "経験値ランキングの位置");
		this.resetStatsConfirm = new HashMap<>();
		this.lbupdating = false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("test")) {
				sender.sendMessage("nothing happens!");
				return true;
			}
			if (args[0].equalsIgnoreCase("soupheal")) {
				sender.sendMessage("§b現在のスープの回復量: " +
						ConfigUtil.AMOUNT_OF_SOUP_HEALING + " (" + (double) (ConfigUtil.AMOUNT_OF_SOUP_HEALING / 2) + "§c❤§b)");
				return true;
			}
			if (args[0].equalsIgnoreCase("loc") || args[0].equalsIgnoreCase("sloc")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("§c(プレイヤーのみ実行可能です)");
				}
				showLocationIds(sender);
				return true;
			}
			if (args[0].equalsIgnoreCase("ffabuild")) {
				ConfigUtil.FFA_BUILD = !ConfigUtil.FFA_BUILD;
				SoupPvPPlugin.getInstance().getConfig().set("ffa-build", ConfigUtil.FFA_BUILD);
				SoupPvPPlugin.getInstance().saveConfig();
				if (ConfigUtil.FFA_BUILD) {
					sender.sendMessage("§aFFAでのブロック使用をONにしました");
				} else {
					sender.sendMessage("§eFFAでのブロック使用をOFFにしました");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("ffabuildheight")) {
				sender.sendMessage("§b現在のFFAでのブロック設置最高高度: Y" +
						ConfigUtil.FFA_BUILD);
				return true;
			}
			if (args[0].equalsIgnoreCase("loc") || args[0].equalsIgnoreCase("sloc")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("§c(プレイヤーのみ実行可能です)");
				}
				showLocationIds(sender);
				return true;
			}
			if (args[0].equalsIgnoreCase("leaderboard")) {
				if (lbupdating) {
					sender.sendMessage("§cLeaderboardは更新中です！");
					return true;
				}
				String senderName = sender.getName();
				for (Player op : Bukkit.getOnlinePlayers()) {
					if (op.hasPermission("souppvp.souppvp")) {
						op.sendMessage("§8[§e" + senderName + "§8] §6Leaderboardを更新中...");
					}
				}
				lbupdating = true;
				Bukkit.getScheduler().runTaskAsynchronously(SoupPvPPlugin.getInstance(), new BukkitRunnable() {
					@Override
					public void run() {
						LeaderboardManager.updateLeaderboards();
						for (Player all : Bukkit.getOnlinePlayers()) {
							LeaderboardManager.showLeaderboards(all);
						}
						for (Player op : Bukkit.getOnlinePlayers()) {
							if (op.hasPermission("souppvp.souppvp")) {
								op.sendMessage("§8[§e" + senderName + "§8] §aLeaderboard更新完了！");
							}
						}
						lbupdating = false;
					}
				});
				return true;
			}
			if (args[0].equalsIgnoreCase("reload")) {
				ConfigUtil.load(SoupPvPPlugin.getInstance());
				sender.sendMessage("§aconfigを再読み込みしました");
				return true;
			}
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("soupheal")) {
				if (!StringUtils.isNumeric(args[1])) {
					sender.sendMessage("§c引数2は数字で指定してください");
					return true;
				}
				int set = Integer.valueOf(args[1]);
				if (set <= 0) {
					sender.sendMessage("§c引数2は1以上を指定してください");
					return true;
				}
				int before = ConfigUtil.AMOUNT_OF_SOUP_HEALING;
				ConfigUtil.AMOUNT_OF_SOUP_HEALING = set;
				SoupPvPPlugin.getInstance().getConfig().set("amount-of-soup-healing", set);
				SoupPvPPlugin.getInstance().saveConfig();
				sender.sendMessage("§aスープの回復量を設定しました §e(" + before + " -> " + set + ")");
				return true;
			}
			if (args[0].equalsIgnoreCase("ffabuildheight")) {
				if (!StringUtils.isNumeric(args[1])) {
					sender.sendMessage("§c引数2は数字で指定してください");
					return true;
				}
				int set = Integer.valueOf(args[1]);
				if (set <= 0) {
					sender.sendMessage("§c引数2は1以上を指定してください");
					return true;
				}
				int before = ConfigUtil.FFA_BUILD_HEIGHT;
				ConfigUtil.FFA_BUILD_HEIGHT = set;
				SoupPvPPlugin.getInstance().getConfig().set("ffa-build-height", set);
				SoupPvPPlugin.getInstance().saveConfig();
				sender.sendMessage("§aFFAでのブロック設置最高高度を設定しました §e(" + before + " -> " + set + ")");
				return true;
			}
			if (args[0].equalsIgnoreCase("loc")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("§cプレイヤーのみ実行可能です");
					return true;
				}
				Player p = (Player) sender;
				String location_id = args[1].toLowerCase();
				;
				if (!locationIdAndNames.containsKey(location_id)) {
					sender.sendMessage("利用できるIDではありません");
					showLocationIds(sender);
					return true;
				}
				if (!LocationUtil.isSet(location_id)) {
					sender.sendMessage("§6" + location_id + "§cというIDにlocationが設定されていません");
					return true;
				}
				p.teleport(LocationUtil.get(location_id));
				p.sendMessage("§d" + locationIdAndNames.get(location_id) + "§bにワープしました");
				return true;
			}
			if (args[0].equalsIgnoreCase("sloc")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("§cプレイヤーのみ実行可能です");
					return true;
				}
				Player p = (Player) sender;
				String location_id = args[1].toLowerCase();
				;
				if (!locationIdAndNames.containsKey(location_id)) {
					sender.sendMessage("利用できるIDではありません");
					showLocationIds(sender);
					return true;
				}
				Location l = p.getLocation();
				LocationUtil.set(location_id, l);
				p.sendMessage("§a現在位置を§d" + locationIdAndNames.get(location_id) + "§aに設定しました §e(" + l.getBlockX() + ", " +
						l.getBlockY() + ", " + l.getBlockZ() + ")");
				return true;
			}
			if (args[0].equalsIgnoreCase("joinffa")) {
				Player target = Bukkit.getPlayer(args[1]);
				if (target == null) {
					sender.sendMessage("§6" + args[1] + " §cはオフラインです");
					return true;
				}
				PlayerUtil.joinFFA(target);
				return true;
			}
			if (args[0].equalsIgnoreCase("leaveffa")) {
				Player target = Bukkit.getPlayer(args[1]);
				if (target == null) {
					sender.sendMessage("§6" + args[1] + " §cはオフラインです");
					return true;
				}
				PlayerUtil.leaveFFA(target);
				return true;
			}
			if (args[0].equalsIgnoreCase("resetstats")) {
				Bukkit.getScheduler().runTaskAsynchronously(SoupPvPPlugin.getInstance(), new BukkitRunnable() {
					@Override
					public void run() {
						if (resetStatsConfirm.containsKey(sender.getName())) {
							OfflinePlayer target = resetStatsConfirm.get(sender.getName());
							String targetName = target.getName();
							if (targetName.equalsIgnoreCase(args[1])) {
								for (String category : PlayerDataUtil.defaults.keySet()) {
									PlayerDataUtil.set(target, category, PlayerDataUtil.defaults.get(category));
								}
								sender.sendMessage("§c§l" + target.getName() + " §eのStatsをリセットしました！！");
							} else {
								sender.sendMessage("§6" + targetName + " のStatsのリセットをキャンセルしました！");
							}
							resetStatsConfirm.remove(sender.getName());
							return;
						}
						OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
						if (target == null || !target.hasPlayedBefore()) {
							sender.sendMessage("§6" + args[1] + " §cはこのサーバーにログインしたことがありません");
							return;
						}
						String targetName = target.getName();
						sender.sendMessage("§c§l本当に §4§l" + targetName + " §c§lのStatsをリセットしますか？");
						sender.sendMessage("§e§lリセットする場合、もう一度同じコマンドを入力してください！！");
						resetStatsConfirm.put(sender.getName(), target);
					}
				});
				return true;
			}
			if (args[0].equalsIgnoreCase("restorestats")) {
				Bukkit.getScheduler().runTaskAsynchronously(SoupPvPPlugin.getInstance(), new BukkitRunnable() {
					@Override
					public void run() {
						if (resetStatsConfirm.containsKey(sender.getName())) {
							OfflinePlayer target = resetStatsConfirm.get(sender.getName());
							String targetName = target.getName();
							if (targetName.equalsIgnoreCase(args[1])) {
								ConfigurationSection stats = PlayerDataUtil.playerDataOld.getConfigurationSection(target.getUniqueId().toString());
								for (String category : PlayerDataUtil.defaults.keySet()) {
									PlayerDataUtil.set(target, category, stats.get(category));
								}
								sender.sendMessage("§c§l" + target.getName() + " §eのStatsを復元しました！！");
							} else {
								sender.sendMessage("§6" + targetName + " のStatsの復元をキャンセルしました！");
							}
							resetStatsConfirm.remove(sender.getName());
							return;
						}
						OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
						if (target == null || !target.hasPlayedBefore()) {
							sender.sendMessage("§6" + args[1] + " §cはこのサーバーにログインしたことがありません");
							return;
						}
						String targetName = target.getName();
						sender.sendMessage("§6§l本当に §4§l" + targetName + " §c§lのStatsを復元しますか？");
						sender.sendMessage("§e§l復元する場合、もう一度同じコマンドを入力してください！！");
						resetStatsConfirm.put(sender.getName(), target);
					}
				});
				return true;
			}
		}
		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("loc")) {
				Player target = Bukkit.getPlayer(args[2]);
				if (target == null) {
					sender.sendMessage("§6" + args[2] + " §cはオフラインです");
					return true;
				}
				String location_id = args[1].toLowerCase();
				if (!locationIdAndNames.containsKey(location_id)) {
					sender.sendMessage("§c利用できるIDではありません");
					showLocationIds(sender);
					return true;
				}
				if (!LocationUtil.isSet(location_id)) {
					sender.sendMessage("§6" + location_id + "§cというIDにlocationが設定されていません");
					return true;
				}
				target.teleport(LocationUtil.get(location_id));
				sender.sendMessage("§e" + target.getName() + "§bを§d" + locationIdAndNames.get(location_id) + "§bにワープしました");
				return true;
			}
		}
		sender.sendMessage("§a----------- PvP設定コマンド -----------");
		sender.sendMessage("§e/souppvp soupheal <amount> §6--- §eスープの回復量を設定します");
		sender.sendMessage("§e/souppvp ffabuild §6--- §eFFAの建築のON/OFFを切り替えます");
		sender.sendMessage("§e/souppvp ffabuildheight <height> §6--- §eFFAの建築の最大高度を設定します");
		sender.sendMessage("§e/souppvp joinffa <player> §6--- §e<player>をFFAに参加させます");
		sender.sendMessage("§e/souppvp leaveffa <player> §6--- §e<player>をFFAから退出させます");
		sender.sendMessage("§e/souppvp loc <location_id> [player] §6--- §e<location_id>に設定された場所にワープします");
		sender.sendMessage("§e/souppvp sloc <location_id> §6--- §e現在位置を<location_id>に設定します");
		sender.sendMessage("§e/souppvp resetstats <player> §6--- §e<player>のStatsをリセットします");
		sender.sendMessage("§e/souppvp leaderboard §6--- §eLeaderboardを更新します(ラグが起きる可能性あり)");
		sender.sendMessage("§e/souppvp reload §6--- §econfigを再読み込みします");
		sender.sendMessage("§e/souppvp restorestats <player> §6--- §e<player>のStatsを復元します");
		return true;
	}

	private void showLocationIds(CommandSender sender) {
		StringBuilder builder = new StringBuilder();
		if (locationIdAndNames.size() == 0) {
			builder.append("§c設定できるIDがありません (エラー)");
		} else {
			boolean first = true;
			for (String id : locationIdAndNames.keySet()) {
				if (first) {
					builder.append("§a" + id + "§2(" + locationIdAndNames.get(id) + ")");
					first = false;
					continue;
				}
				builder.append("§7, §a" + id + "§2(" + locationIdAndNames.get(id) + ")");
			}
		}
		sender.sendMessage("§b利用できるlocation_id一覧: " + builder.toString());
	}

}
