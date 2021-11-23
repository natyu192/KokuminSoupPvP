package me.nucha.souppvp.listener.gui;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.nucha.kokumin.kabu.GuiKabu;
import me.nucha.parties.Party;
import me.nucha.parties.PartyManager;
import me.nucha.parties.events.PartyCreateEvent;
import me.nucha.parties.events.PartyJoinEvent;
import me.nucha.souppvp.arenafight.match.Match;
import me.nucha.souppvp.arenafight.match.MatchManager;
import me.nucha.souppvp.arenafight.match.TeamInfo;
import me.nucha.souppvp.language.LanguageManager;
import me.nucha.souppvp.player.PlayerState;
import me.nucha.souppvp.player.PlayerUtil;

public class ItemClickListener implements Listener {

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
			if (p.getItemInHand() != null) {
				ItemStack item = p.getItemInHand();
				if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
					return;
				}
				String displayName = item.getItemMeta().getDisplayName();
				if (displayName.startsWith("§aPerk Shop")) {
					event.setCancelled(true);
					GuiShop.open(p);
				}
				if (displayName.startsWith("§aStats")) {
					event.setCancelled(true);
					GuiStats.open(p);
				}
				if (displayName.startsWith("§aKits")) {
					event.setCancelled(true);
					GuiKits.open(p);
				}
				if (displayName.startsWith("§e株 (仮)")) {
					event.setCancelled(true);
					GuiKabu.open(p);
				}
				if (displayName.startsWith("§cLava Challenge")) {
					event.setCancelled(true);
					PlayerUtil.joinLavaChallenge(p);
				}
				if (displayName.startsWith("§c1vs1に参加")) {
					event.setCancelled(true);
					if (!PartyManager.isInParty(p)) {
						if (PlayerState.isState(p, PlayerState.IN_LOBBY)) {
							if (MatchManager.isQueued(p)) {
								p.sendMessage("§8[§aMatch§8] §r" + LanguageManager.get(p, "queue.remove"));
								p.setItemInHand(PlayerUtil.item_arenafight);
								MatchManager.removeQueue(p);
							} else {
								p.sendMessage("§8[§aMatch§8] §r" + LanguageManager.get(p, "queue.join"));
								p.setItemInHand(PlayerUtil.item_arenafight_queued);
								MatchManager.addQueue(p);
							}
						} else {
							p.sendMessage("§8[§aMatch§8] §r" + LanguageManager.get(p, "command.error.can-do-in-lobby"));
						}
					} else {
						p.sendMessage("§8[§aMatch§8] §r" + LanguageManager.get(p, "command.error.cannot-do-while-in-party"));
					}
				}
				if (displayName.startsWith("§bParty Fightを始める")) {
					if (!PlayerState.isState(p, PlayerState.IN_LOBBY)) {
						p.sendMessage("§8[§aMatch§8] §r" + LanguageManager.get(p, "command.error.can-do-in-lobby"));
						return;
					}
					if (!PartyManager.isInParty(p)) {
						p.sendMessage("§8[§aMatch§8] §r" + LanguageManager.get(p, "command.error.must-be-in-party"));
						return;
					}
					Party party = PartyManager.getParty(p);
					if (!party.isLeader(p)) {
						p.sendMessage("§8[§aMatch§8] §r" + LanguageManager.get(p, "party-gui.only-leader-can-do"));
						return;
					}
					/*List<Player> members = Lists.newArrayList(party.getMembers());
					Collections.shuffle(members);
					List<Player> team1 = Lists.newArrayList();
					List<Player> team2 = Lists.newArrayList();
					for (int i = 0; i < members.size(); i++) {
						if (i < (members.size() / 2)) {
							team1.add(members.get(i));
						} else {
							team2.add(members.get(i));
						}
					}*/
					TeamInfo teamInfo = new TeamInfo(party, new ArrayList<>(), new ArrayList<>());
					boolean success = teamInfo.randomize();
					if (success) {
						GuiPartyTeaming.open(p, teamInfo);
					} else {
						p.sendMessage(LanguageManager.get(p, "party-gui.error-must-be-online"));
					}
				}
				if (displayName.startsWith("§3観戦をやめる")) {
					if (PlayerState.isState(p, PlayerState.SPECTATING_MATCH)) {
						Match match = MatchManager.getMatch(p);
						if (match.isSpectator(p)) {
							if (match.hasFought(p)) {
								p.sendMessage("§8[§aMatch§8] §r" + LanguageManager.get(p, "arena-fight.match.watch-your-own-game"));
							} else {
								match.removeSectator(p);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPartyJoin(PartyJoinEvent event) {
		Player p = event.getPlayer();
		if (MatchManager.isQueued(p)) {
			MatchManager.removeQueue(p);
			p.sendMessage("§8[§aMatch§8] §r" + LanguageManager.get(p, "queue.remove-for-being-in-party"));
		}
	}

	@EventHandler
	public void onPartyCreate(PartyCreateEvent event) {
		Player p = event.getPlayer();
		if (MatchManager.isQueued(p)) {
			MatchManager.removeQueue(p);
			p.sendMessage("§8[§aMatch§8] §r" + LanguageManager.get(p, "queue.remove-for-being-in-party"));
		}
	}

}
