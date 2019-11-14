package me.nucha.souppvp.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nucha.souppvp.arenafight.match.Match;
import me.nucha.souppvp.arenafight.match.MatchManager;
import me.nucha.souppvp.kit.KitManager;
import me.nucha.souppvp.language.LanguageManager;
import me.nucha.souppvp.listener.combattag.CombatTagManager;
import me.nucha.souppvp.player.PlayerState;
import me.nucha.souppvp.player.PlayerUtil;
import me.nucha.souppvp.util.LocationUtil;

public class CommandSpawn implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1) {
			if (sender.hasPermission("souppvp.spawn.other")) {
				if (args[0].startsWith("@a-ffa")) {
					if (!LocationUtil.isSet("ffa-spawn")) {
						sender.sendMessage("§cFFAのスポーン地点が設定されていません");
						return true;
					}
					Location spawn = LocationUtil.get("ffa-spawn");
					for (Player target : Bukkit.getOnlinePlayers()) {
						if (PlayerState.isState(target, PlayerState.IN_FFA)) {
							if (CombatTagManager.hasTag(target)) {
								sender.sendMessage("§e" + target.getName() + "§cにはCombatTagがついています §7(残り" +
										CombatTagManager.getTagExpireSeconds(target, 1) + "秒)");
								return true;
							}
							target.teleport(spawn);
							KitManager.unselectKit(target);
							PlayerUtil.giveLobbyItems(target);
							sender.sendMessage("§e" + target.getName() + "§bを§dFFAのスポーン地点§bにワープしました");
						}
					}
					return true;
				}
				if (args[0].startsWith("@a-lobby")) {
					if (!LocationUtil.isSet("lobby")) {
						sender.sendMessage("§cFFAのスポーン地点が設定されていません");
						return true;
					}
					Location spawn = LocationUtil.get("lobby");
					for (Player target : Bukkit.getOnlinePlayers()) {
						if (PlayerState.isState(target, PlayerState.IN_LOBBY)) {
							target.teleport(spawn);
							KitManager.unselectKit(target);
							PlayerUtil.giveLobbyItems(target);
							sender.sendMessage("§e" + target.getName() + "§bを§dロビー§bにワープしました");
						}
					}
					return true;
				}
				Player target = Bukkit.getPlayer(args[0]);
				if (target == null) {
					sender.sendMessage("§6" + args[0] + " §cはオフラインです");
					return true;
				}
				if (attemptToSpawn(target)) {
					sender.sendMessage("§e" + target.getName() + "§bをスポーンさせました");
				} else {
					sender.sendMessage("§6" + target.getName() + "§cのスポーンに失敗しました");
				}
				return true;
			}
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cプレイヤーのみ実行可能です");
			return true;
		}
		Player p = (Player) sender;
		attemptToSpawn(p);
		return true;
	}

	private boolean attemptToSpawn(Player p) {
		if (PlayerState.isState(p, PlayerState.LAVA_CHALLENGE)) {
			if (!LocationUtil.isSet("lobby")) {
				p.sendMessage("§cロビーが設定されていません");
				return false;
			}
			p.teleport(LocationUtil.get("lobby"));
			PlayerUtil.giveLobbyItems(p);
			PlayerUtil.treat(p);
			PlayerState.setState(p, PlayerState.IN_LOBBY);
			return true;
		}
		if (PlayerState.isState(p, PlayerState.IN_LOBBY)) {
			if (!LocationUtil.isSet("lobby")) {
				p.sendMessage("§cロビーが設定されていません");
				return false;
			}
			p.teleport(LocationUtil.get("lobby"));
			PlayerUtil.giveLobbyItems(p);
			return true;
		}
		if (PlayerState.isState(p, PlayerState.IN_FFA)) {
			if (CombatTagManager.hasTag(p)) {
				double tag = CombatTagManager.getTagExpireSeconds(p, 1);
				p.sendMessage(LanguageManager.get(p, "command.error.combat-tagged").replaceAll("%tag", String.valueOf(tag)));
				return false;
			}
			if (!LocationUtil.isSet("ffa-spawn")) {
				p.sendMessage("§cFFAのスポーン位置が設定されていません");
				return false;
			}
			if (KitManager.hasKitSelected(p)) {
				p.teleport(LocationUtil.get("ffa-spawn"));
				KitManager.unselectKit(p);
			} else {
				PlayerUtil.leaveFFA(p);
			}
			PlayerUtil.giveLobbyItems(p);
			return true;
		}
		if (PlayerState.isState(p, PlayerState.SPECTATING_MATCH)) {
			if (!LocationUtil.isSet("lobby")) {
				p.sendMessage("§cロビーが設定されていません");
				return false;
			}
			Match match = MatchManager.getMatch(p);
			if (match.isSpectator(p)) {
				match.removeSectator(p);
			}
			PlayerState.setState(p, PlayerState.IN_LOBBY);
			p.teleport(LocationUtil.get("lobby"));
			PlayerUtil.giveLobbyItems(p);
			return true;
		}
		return false;
	}

}
