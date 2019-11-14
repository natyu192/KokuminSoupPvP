package me.nucha.souppvp.arenafight.match;

import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import me.nucha.parties.Party;

public class TeamInfo {

	private Party party;
	private List<Player> team1;
	private List<Player> team2;

	public TeamInfo(Party party, List<Player> team1, List<Player> team2) {
		this.party = party;
		this.team1 = team1;
		this.team2 = team2;
	}

	public Party getParty() {
		return party;
	}

	public List<Player> getTeam1() {
		return team1;
	}

	public List<Player> getTeam2() {
		return team2;
	}

	public void randomize() {
		List<Player> members = Lists.newArrayList(party.getMembers());
		Collections.shuffle(members);
		List<Player> team1 = Lists.newArrayList();
		List<Player> team2 = Lists.newArrayList();
		for (int i = 0; i < members.size(); i++) {
			if (i < (members.size() / 2)) {
				team1.add(members.get(i));
			} else {
				team2.add(members.get(i));
			}
		}
		this.team1 = team1;
		this.team2 = team2;
	}

}
