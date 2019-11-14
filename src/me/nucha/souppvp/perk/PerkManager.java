package me.nucha.souppvp.perk;

import java.util.List;

import com.google.common.collect.Lists;

public class PerkManager {

	private static List<Perk> perks;

	public static void init() {
		perks = Lists.newArrayList();
		registerPerk(new PerkKillAndRun());
		registerPerk(new PerkLuckyGapple());
	}

	public static void registerPerk(Perk perk) {
		if (!isRegistered(perk)) {
			perks.add(perk);
		}
	}

	public static void unregisterPerk(Perk perk) {
		if (isRegistered(perk)) {
			perks.remove(perk);
		}
	}

	public static boolean isRegistered(Perk perk) {
		return perks.contains(perk);
	}

	public static List<Perk> getPerks() {
		return perks;
	}

	public static Perk getPerkById(String id) {
		for (Perk perk : perks) {
			if (perk.getId().equalsIgnoreCase(id)) {
				return perk;
			}
		}
		return null;
	}

}
