package me.nucha.souppvp.listener;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;
import com.mojang.authlib.properties.Property;

import me.nucha.core.hologram.Hologram;
import me.nucha.core.npc.NPC;
import me.nucha.souppvp.SoupPvPPlugin;
import me.nucha.souppvp.arenafight.duel.DuelManager;
import me.nucha.souppvp.arenafight.match.MatchListener;
import me.nucha.souppvp.language.Language;
import me.nucha.souppvp.language.LanguageManager;
import me.nucha.souppvp.leaderboard.LeaderboardManager;
import me.nucha.souppvp.nick.NickManager;
import me.nucha.souppvp.player.PlayerState;
import me.nucha.souppvp.player.PlayerUtil;
import me.nucha.souppvp.util.LocationUtil;
import me.nucha.souppvp.util.PlayerDataUtil;
import me.nucha.souppvp.util.ScoreboardUtils;

public class JoinListener implements Listener {

	public static List<NPC> npcs;
	public static List<Hologram> holograms;
	private static HashMap<String, Property> properties;

	public JoinListener() {
		npcs = Lists.newArrayList();
		holograms = Lists.newArrayList();
		properties = new HashMap<>();
		properties.put("smilenucha", new Property("textures",
				"eyJ0aW1lc3RhbXAiOjE1NTMxNzA2NzI3MzEsInByb2ZpbGVJZCI6ImE1OTQwMTgzOGZkOTQyNzJiOWVhMGQyOGM1NzgwZTY4IiwicHJvZmlsZU5hbWUiOiJrb2t1bWluIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yZWZmMTc0ODc0NWU3NDkyZmFhYjMyZGZlMjZhN2RhMDU1YzY0MzQxNTI4ZjFmNDdlNTAwZTJhMWY3YzViYmUyIn19fQ==",
				"QeHjtjkqjio4dmCypQzrP0cBoeLDYMzjwMWHPQE4PRNA9RrJHedIvLiMVkINqXyO33a0sDE/TEqYSQrWsZiGyktOS4PoTB2ISpuQHFkWB8ELVe9mXB1lFCjysPXmv53AG6fjD4orGQgSwZqXRIWtLRa89EzC8cccYvz1CuowZJBKo0X3KsNAsZp8yjeziPn01cw7yd2HbnL/hU3vFhoyitrsFiwn+q07nZXZz82tDCinKJNHX+z2OSL5FGyhmviqRf7my/iFOGx1zLBpppYnYdvZJO0zlTran5ZXDGOj+eHkzLWipsZetIE5I0XsAw84PsEaIbmya0VBnSrLbZ/GAJU87XNXbkqkRFDTR3b4f1MN9Jgm/nG4dkcE3yW53z6HD36+zLsL9z3iJNO0uPw3BnnaI1I8ZRGNQRiAEzF5uCEgSqxcwUwcWcCmK/HermANhxfwYvQPNDJ2LbP8WEoeyQg0w6Bh4C18PK2PpLAc22w4jPF8mUAEBzE6AI99RUtnGWDQmyXoBLLHPf3jBA+KvGE30N1Jg8W2vc9pCJYFFMhaa3l2qjGDNNA/5CaKpw83jdsNTaUJ+CWHymJB5+PWua/dhOLa6hAeW7+VYnQDRbNEozECMdphQk01PRQ/hswZADQjXPvJdUvCi8bmcRLVilPhA+yxHYHbv2yO4iHzt2s="));
		properties.put("lqfly", new Property("textures",
				"eyJ0aW1lc3RhbXAiOjE1NTMxNzA5MTk0OTIsInByb2ZpbGVJZCI6ImE1OTQwMTgzOGZkOTQyNzJiOWVhMGQyOGM1NzgwZTY4IiwicHJvZmlsZU5hbWUiOiJrb2t1bWluIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80NzY5Y2E0M2JjOWMyMzU3MDllN2E2NjYyYTEwODgxZjM1ZjU1ZTZjNDY0MWEyYmE2MTcxYWZkNWU1MDk2ZThhIn19fQ==",
				"dE0EwZ1kZdDa3CUk4TLG3IFpDnKES03XPXtxC+PYsrJbZ5UMuiKWy3BIGtFcZjDG9ssX3XhO73VtRVFVR5UDPZJNiJr/nWs5PMqo/RqgCY0KjYO6RkHPC7FeHybt961LMhq1hGDKWW1pIYjLeA8kRgxq1pNsYXuh95di0eXDqjDMGjwAHNhsBUhz+Mpkk6OsaRVhWbME4gb9Zm5b3WWU25TlNYjw9z/jkt1aM3Kk+p3nU1RWSdd8e8KKP42RANqsmCkvB4AGfqZNawkzOQ5DEsHLxtGJ5cEdNJFE4PPIkCySwanU58yTeqbUG6oYuKteXhL8ZJ3RXR9nDA7dPffLaXOaVR7LA6EdoSst2on7xwrYVvVFVyYarHznGwVj7LQfeZPN5rA2zvcRssXV2+C3U5eQYEVngfm272qua640Q+M9DnYt5o+PWkUihq22fy8Nqhfe5OKqpJXfrbewm5dhH2O7cCb3qNknrGc/VlgXfV5cFUBqiGh2LV3Tb4N2UAeXsP84zRPM3IlkanlAyT1p1I53aG6NxaBwnnEm95Zc3rmDE4ohqstcaUvRVTOEFmpisHcQ+nGFMlipSQ8yS/OVhAvAgDQTVEtUPURjaN+/4c+u78BxBGtHC0k44/cK0/URZtnTrYeLnr+neh2c95Q5IULasXAsZ41h5loGZucTPJg="));
		properties.put("tyuro", new Property("textures",
				"eyJ0aW1lc3RhbXAiOjE1NTMxNzA5OTI4MDEsInByb2ZpbGVJZCI6ImE1OTQwMTgzOGZkOTQyNzJiOWVhMGQyOGM1NzgwZTY4IiwicHJvZmlsZU5hbWUiOiJrb2t1bWluIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81MmVmMzU4ZjRjYWY0M2E3ZWM3NGIxMTcyNjJkODlmYTQzODc0NzNhOTY3Mzk0MTY2NWZmYWEyMjc2NmM5Mjg2In19fQ==",
				"xArKCRHBmkBviAd2MXo2w4iNz+5uTPuSXsbdXl8XnxmpGrND1XMxIjhCTlzYHKR/8QVG7ajcZ/pVrMRgFFuXg4LyI+SsJOxD1SGCu1RtUX90KEaebx+czawSYsF2OTCv8B43uSmxxCbsN2up3u90WfbUEQjhqD67SyJHZ9hna+2xy6PsI2V/D0JbnAloH5s4fiRRbnf+MA1gFujOkb74Tp8cPY9AeLEYYcmYIIKwaeZqnABlzVPAN3PUHAvNZoBIGZ8nm+25yNKRdrr+a8AZxoMBOWgep1450s0x+cMAwXFdxrkHQLZLqXf6lLK93fR0kX4Rs8wayB7sENnDU1zhTypCXeHX3G5ZaSE/eraq5SYRlphtxCNYZrlpWa7sMLt/irmVLODoi9SFAzjmsZH+TWgvoNtBCpYluhnvo3EpLW0EzfumKzt8Df3A3bIzmdjsGh/xFvGQN+mmc/0BbgTeirMEmWJxzqwzYtic9uA+x6UYgG3dN5DYONcaOSE7NOSoljOHVJaFblm7VqcKM75ZEi7pNtZq32hECiXfh0A5Qk5SBuNbFMaGALtcnPa4wsPaA+2YN0TKulG64bh0ZWypfSu3/Qk+Pk5v9ZsI/YH1bZioi3K6Y3vOnxKQ92hdymz1PtC4I6EkISvknL4+15eeF/lhrp8T9VSycbT3etW7izo="));
		properties.put("simplyrin", new Property("textures",
				"eyJ0aW1lc3RhbXAiOjE1NTMxNzEwNzg3NDIsInByb2ZpbGVJZCI6ImE1OTQwMTgzOGZkOTQyNzJiOWVhMGQyOGM1NzgwZTY4IiwicHJvZmlsZU5hbWUiOiJrb2t1bWluIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85ZjFiMjQ0MmM2ODVjZThkODEyOTVlMmViNjM3MWE3YzVlZDE1NDI1ODVjNzlkZTI1N2VkYWI0OGY1Y2Q5ZjZiIn19fQ==",
				"KoW5QAAi6MR3nYDsPqP91ZfeSqBDj7QQN8S4BNFroe1PT/QfqVLsjwlrFQ2NuPH6EOI3Yn5/QiockaOwDEQw1HYYoYj2+cfGu2jHtYiWBXlIJ3Tt4XV9kdk6tM5AUk+C9VOtjv6CZqIinhx6oaNsph70dMmPFI7deYgPdNj4vCw/egxpgR2uwZtnGe+dUveNFQj4cQnUpPDyEgJaSNwgSvC+LAirwpOftZa85dG9dwKomG6hO7pIBGiD274G9GyJQSMJwkIEN8MKQN9NHL6msdCY9NDdiRSVk9YPUHITyeK6eFWnEii13iT/S7ftblFLl45nE5DCigRuVyigIQSC/Dvkrasjv2PPszeQlMzuB3GHs7KVdmbKCyB2MwLgjG+J3W2Rlh2X3QXKDKvYosxGnLRbfklfKbC4ZKNg36CxXEL/hsrQeWSTW12qek1rfCbyuyQj5c2t8f7wuHteAtcWqGRdDYP9n2+mU0MfUxZT4xLp+jaWL8kDhbGtUpm1vVAbnkmtLGLmFg5cYpQaZeGsol5v7CeGmjTA73YDvMKuVcM82KqO/YdHdDEnp7QFAgxT/kN1a1ikK8/QiwHhgJbgFwwtxEkyy32nSvK7C6El+71aWjkLP1G6O3t8NaiCaUn7k3mpUAAFjA4msAlGvCRZvmVlX4U9f3JuXskhqMR79lY="));
		properties.put("tom_satumaimo", new Property("textures",
				"eyJ0aW1lc3RhbXAiOjE1NTMxNzExMjA3NzAsInByb2ZpbGVJZCI6ImE1OTQwMTgzOGZkOTQyNzJiOWVhMGQyOGM1NzgwZTY4IiwicHJvZmlsZU5hbWUiOiJrb2t1bWluIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mMzU1ODgzZWRhMmRlNzc5MjMwNzk4MWNjZGNjN2FkNmUxNzVmOWM0YTU4OWUzNzUwYTkyYmQzYzExY2EzYmUyIn19fQ==",
				"XiNsHu+Poa3QaiH1XE6BiqCuWwkx2dccZyr5Ko+MJLsvXFUma9gKlOf8Gqdv4cJwHqGrPrtFqDarPhtvsfQxzspz2sMsU0gzyWAxnnfFcrOSYjEkKMzupfUNV2AikSKg1h6gXE0bdI6NW35zZ5m9Kx83iIuDzCUWwQy2l1LtEgfDhvYyxA5G9QhEo1o8Z7EInFbk7PhlOenmaurgxFXvljl9krgzqcLFoHZVKgv6Ox6UAVaXkIwqmdVLRjzv4EwdwrOwtU5/ar0muwp70+ZfCBYG/N0zMN6u88RtaasCw3tfMPzI1LpNB/MJmIX1fFLXowX8ShFJce7hBMbfDd7HVRsdXkkptBfE1Orv2mLmOXg3TT4cAYxGLjh/VOCy8RtAVUGKYljRgrZmMeymCjucKftzF6Ydx/YodWrCb+oTWA1yXID3z4VPivROdFNOszeAbabg4IT2mRK/rHdoo2Z87fM2BWAjJmnB5ejGy1Ui4swaSZFGXFxd1qPQBFcpHFIPoiEQnofGzGx9kUPAhQXkVESHyl5UB4vnGowtlCg9vy6fqFGfGAVhQGafUx1fZHnqmtMOp58bp/wyj3ExCI3N8DO0Q9hapI4hYxQ6aQ68smh9T9IN5EWDYw1xDo3matw8bI3Z4ez1vdta2Gl/FSgAALdh5JAs8s5MZ6/oRuDuOr4="));
		properties.put("namem", new Property("textures",
				"eyJ0aW1lc3RhbXAiOjE1NTMxNzExNjg2NzIsInByb2ZpbGVJZCI6ImE1OTQwMTgzOGZkOTQyNzJiOWVhMGQyOGM1NzgwZTY4IiwicHJvZmlsZU5hbWUiOiJrb2t1bWluIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82YjA3YzZhZmIyOTZjOGM2ZjNlM2M4ODRhZTE1MTg0ZTg5NDM2YzM4N2FjMjZiMmZkZWE5MTAxYWU2MTdiMGY0In19fQ==",
				"q76nJ9m0xOIMvtnH0N29PP9ZDnz28T50/p4J5vaqmWakx7rqJUk2qvkWWu296igenpiu9J54dv31pMgeprvKM/EQiS4wFotnBq3jhK8t2N+D6+JaqfQ8jVCevqfHmHG1DnTqKjivClnbgb90yCFDqzq4f65Ct+yBNkvCVL3n9Tbq3/Z9Qa0NbMF7+9Tp9KF+7mL2RhUVlEQmyVMqxcdNsgzveGIZiwGQtnb3nXoWaBiXTayLrFHzDx2JdiRFJLsmMG9PBblXrMLeOZ9n2IxM0+1LPADuESF+8rV1eFTIyXaWMLk3E3ujVyiH3nnTkzRlun9oDyJuxEw2HsPobV5LHpte9vk4EtvkKnqDEJgBNIKu+hvBfoGWrRv00Y+K7KBxCsa4lqjH0DtVHqa2dtyCFpV6VPBVqkNRTxPy9UVS4ACirNJYHo3PS2wQvObBUVuqa3MSiIwzKcJDGVwgfdyTZc05cmFd1Gm/xqc7li40ZgbIrGHc9j7Dj0YMvS7P4AflT4DH3X+y5UuT4i+sejoVEeAKulUkNtrLZR5K5zUMRYhAvYwgh/fDlm2hH4EvDfqT5dHHQgqEJs65Vxz7rdRniKLTd/7UfUV4ZNz3QOnxPyhNrlAIuAKWuKLdDwlkTK5QsmMiYSqsmuYBk5Gbv/+kQ+tzN0EW+sKyFEbvvUCXfzc="));
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		if (LanguageManager.getLanguage(p) == null) {
			LanguageManager.setLanguage(p, Language.JAPANESE);
		}
		PlayerDataUtil.setupPlayer(p);
		PlayerUtil.clearInventory(p);
		PlayerUtil.removePotionEfects(p);
		PlayerUtil.giveLobbyItems(p);
		String message = event.getJoinMessage();
		event.setJoinMessage(null);
		if (!p.hasPlayedBefore()) {
			message += "§d(For first time)";
		}
		for (Player all : Bukkit.getOnlinePlayers()) {
			if (p.isOp()) {
				if (all.isOp()) {
					all.sendMessage("§7(Hidden) " + message);
				}
			} else {
				all.sendMessage(message);
			}
			ScoreboardUtils.updateOnlines(all);
		}
		Bukkit.getScheduler().runTask(SoupPvPPlugin.getInstance(), new BukkitRunnable() {
			@Override
			public void run() {
				if (LocationUtil.isSet("lobby")) {
					p.teleport(LocationUtil.get("lobby"));
				}
				ScoreboardUtils.displayBoard(p);
				PlayerState.setState(p, PlayerState.IN_LOBBY);
				Bukkit.getScheduler().runTaskAsynchronously(SoupPvPPlugin.getInstance(), new BukkitRunnable() {
					@Override
					public void run() {
						removeNPCsAndHolograms(p);
						displayNPCsAndHolograms(p);
						LeaderboardManager.showLeaderboards(p);
					}
				});
			}
		});
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		MatchListener.onQuit(event);
		ScoreboardUtils.undisplayBoard(p);
		Bukkit.getScheduler().runTask(SoupPvPPlugin.getInstance(), new BukkitRunnable() {
			@Override
			public void run() {
				for (Player all : Bukkit.getOnlinePlayers()) {
					ScoreboardUtils.updateOnlines(all);
				}
			}
		});
		PlayerState.unregisterState(p);
		removeNPCsAndHolograms(p);
		String message = event.getQuitMessage();
		if (p.isOp()) {
			for (Player all : Bukkit.getOnlinePlayers()) {
				if (all.isOp()) {
					all.sendMessage("§7(Hidden) " + message);
				}
			}
			event.setQuitMessage(null);
		}
		NickManager.unNick(p, false);
		DuelManager.removeDuel(p);
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player p = event.getPlayer();
		if (PlayerState.isState(p, PlayerState.IN_LOBBY) || PlayerState.isState(p, PlayerState.IN_FFA)) {
			PlayerUtil.giveLobbyItems(p);
		}
		if (PlayerState.isState(p, PlayerState.SPECTATING_MATCH)) {
			PlayerUtil.giveSpectatorItems(p);
		}
	}

	public static void displayNPCsAndHolograms(Player p) {
		if (LocationUtil.isSet("npc-shop")) {
			NPC npc = new NPC(p, "Perk Shop(CLICK)", UUID.randomUUID(), p.getWorld());
			npc.setSkin(properties.get("lqfly"));
			npc.spawn(LocationUtil.get("npc-shop"), false);
			npc.setBlocking(true);
			npc.equipHand(new ItemStack(Material.IRON_SWORD));
			npc.addTag("Shop");
			npc.setNameTagVisibility(false);
			npcs.add(npc);
			Hologram hologram = new Hologram(p, LocationUtil.get("npc-shop").clone().add(0, 1.5, 0), "§aPerk Shop", "§aクリックして確認!");
			hologram.display();
			hologram.addTag("Kokumin");
			holograms.add(hologram);
		}
		if (LocationUtil.isSet("npc-stats")) {
			NPC npc = new NPC(p, "Stats(CLICK)", UUID.randomUUID(), p.getWorld());
			npc.setSkin(properties.get("namem"));
			npc.spawn(LocationUtil.get("npc-stats"), false);
			npc.setBlocking(true);
			npc.equipHand(new ItemStack(Material.DIAMOND_SWORD));
			npc.addTag("Stats");
			npc.setNameTagVisibility(false);
			npcs.add(npc);
			Hologram hologram = new Hologram(p, LocationUtil.get("npc-stats").clone().add(0, 1.5, 0), "§aSTATS", "§aクリックして確認!");
			hologram.display();
			hologram.addTag("Kokumin");
			holograms.add(hologram);
		}

		if (LocationUtil.isSet("holo-welcome")) {
			Hologram hologram = new Hologram(p, LocationUtil.get("holo-welcome"), "§aWelcome §e" + p.getName(),
					"§ato the Kokumin §6PvP");
			hologram.display();
			hologram.addTag("Kokumin");
			holograms.add(hologram);
		}
	}

	public static void removeNPCsAndHolograms(Player p) {
		List<NPC> npcsToRemove = Lists.newArrayList();
		for (NPC npc : npcs) {
			if (npc.getPlayer().getUniqueId().equals(p.getUniqueId())) {
				npc.destroy();
				npcsToRemove.add(npc);
			}
		}
		npcs.removeAll(npcsToRemove);
		List<Hologram> hologramsToRemove = Lists.newArrayList();
		for (Hologram hologram : holograms) {
			if (hologram.getPlayer().getUniqueId().equals(p.getUniqueId())) {
				hologram.remove();
				hologramsToRemove.add(hologram);
			}
		}
		holograms.removeAll(hologramsToRemove);
	}

	public static HashMap<String, Property> getProperties() {
		return properties;
	}
}
