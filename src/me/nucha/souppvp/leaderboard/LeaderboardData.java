package me.nucha.souppvp.leaderboard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.google.common.collect.Lists;

import me.nucha.souppvp.util.PlayerDataUtil;

public class LeaderboardData {

	private String category;
	private List<Entry<String, Integer>> leaderboard;
	private String date;

	public LeaderboardData(String category) {
		this.category = category;
		this.leaderboard = Lists.newArrayList();
		updateLeaderboard();
	}

	public String getCategory() {
		return category;
	}

	public void updateLeaderboard() {
		if (PlayerDataUtil.defaults.containsKey(category)) {
			java.util.Map<String, Integer> hashMap = new HashMap<String, Integer>();
			for (OfflinePlayer all : Bukkit.getOfflinePlayers()) {
				hashMap.put(all.getName(), PlayerDataUtil.getInt(all, category));
			}

			List<Entry<String, Integer>> entries = new ArrayList<Entry<String, Integer>>(hashMap.entrySet());
			Collections.sort(entries, new Comparator<Entry<String, Integer>>() {
				@Override
				public int compare(Entry<String, Integer> entry1, Entry<String, Integer> entry2) {
					return ((Integer) entry2.getValue()).compareTo((Integer) entry1.getValue());
				}
			});
			leaderboard = entries;
			date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
		}
	}

	public List<Entry<String, Integer>> getLeaderboard() {
		return leaderboard;
	}

	public int getPlace(OfflinePlayer p) {
		int place = 1;
		for (Entry<String, Integer> entry : leaderboard) {
			if (entry.getKey().equalsIgnoreCase(p.getName())) {
				return place;
			}
			place++;
		}
		return place;
	}

	public int getScore(OfflinePlayer p) {
		for (Entry<String, Integer> entry : leaderboard) {
			if (entry.getKey().equalsIgnoreCase(p.getName())) {
				return entry.getValue();
			}
		}
		return 0;
	}

	public String getLastUpdatedDate() {
		return date;
	}

}
