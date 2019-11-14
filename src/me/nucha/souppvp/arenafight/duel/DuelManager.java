package me.nucha.souppvp.arenafight.duel;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.nucha.souppvp.SoupPvPPlugin;
import me.nucha.souppvp.arenafight.match.MatchManager;
import me.nucha.souppvp.language.LanguageManager;
import me.nucha.souppvp.nick.NickManager;
import mkremins.fanciful.FancyMessage;

public class DuelManager {

	private static HashMap<Player, Player> duels;

	public static void init() {
		duels = new HashMap<>();
	}

	public static boolean isPendingDuel(Player p) {
		return duels.containsKey(p);
	}

	public static boolean isPendingDuelTo(Player p, Player target) {
		return duels.containsKey(p) && duels.get(p).equals(target);
	}

	public static void removeDuel(Player p) {
		duels.remove(p);
	}

	public static Player getDuelSentTo(Player p) {
		return duels.get(p);
	}

	public static void sendDuel(Player p, Player target) {
		if (isPendingDuelTo(p, target)) {
			return;
		}
		if (p.equals(target)) {
			return;
		}
		duels.put(p, target);
		p.sendMessage("§8[§6Match§8] §r" + LanguageManager.get(p, "arena-fight.duel.send").replaceAll("%target", NickManager.getName(target)));
		target.sendMessage(
				"§8[§6Match§8] §r" + LanguageManager.get(target, "arena-fight.duel.receive").replaceAll("%sender", NickManager.getName(p)));
		FancyMessage fm = new FancyMessage("§8[§6Match§8] §r")
				.then(LanguageManager.get(target, "arena-fight.duel.click-here") + " ").tooltip("/accept " + NickManager.getName(p))
				.command("/accept " + p.getName())
				.then(LanguageManager.get(p, "arena-fight.duel.to-accept"));
		fm.send(target);
		Bukkit.getScheduler().runTaskLater(SoupPvPPlugin.getInstance(), new BukkitRunnable() {
			@Override
			public void run() {
				if (p != null && p.isOnline() && isPendingDuelTo(p, target)) {
					duels.remove(p);
				}
			}
		}, 40 * 20L);
	}

	public static void acceptDuel(Player accepter, Player sender) {
		if (accepter.equals(sender)) {
			return;
		}
		if (isPendingDuelTo(sender, accepter)) {
			removeDuel(sender);
			MatchManager.removeQueue(sender);
			MatchManager.removeQueue(accepter);
			accepter.sendMessage("§8[§6Match§8] §r" +
					LanguageManager.get(accepter, "arena-fight.duel.accepted").replaceAll("%sender", NickManager.getName(sender)));
			sender.sendMessage("§8[§6Match§8] §r" +
					LanguageManager.get(sender, "arena-fight.duel.was-accepted").replaceAll("%accepter", NickManager.getName(accepter)));
			MatchManager.createMatch(sender, accepter);
		}
	}

	public static HashMap<Player, Player> getDuels() {
		return duels;
	}

}
