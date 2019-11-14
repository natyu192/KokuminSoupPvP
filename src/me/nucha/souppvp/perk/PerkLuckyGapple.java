package me.nucha.souppvp.perk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PerkLuckyGapple extends Perk {

	public PerkLuckyGapple() {
		super("lucky-gapple", "Lucky Gapple",
				Arrays.asList(new String[] { "§cプレイヤーをキル§7すると", "§7ある確率で§e金リンゴ§7がドロップします！" }), new ItemStack(Material.GOLDEN_APPLE),
				new int[] { 500, 1000, 2000, 3500, 5000 }, 12);
	}

	@Override
	public String[] getMoreInfo(Player p) {
		List<String> moreInfo = new ArrayList<String>();
		int level = getLevel(p);
		if (level == 0) {
			level = 1;
			moreInfo.add("§6Perk Level I での効果:");
		} else {
			moreInfo.add("§6現在のレベルでの効果:");
		}
		moreInfo.add("  §7金リンゴがドロップする確率: §e" + getChance(level) + "%");
		moreInfo.add(" ");
		moreInfo.addAll(Arrays.asList(super.getMoreInfo(p)));
		return moreInfo.toArray(new String[] {});
	}

	public int getChance(Player p) {
		return getChance(getLevel(p));
	}

	public int getChance(int level) {
		if (level >= 5) {
			return 100;
		} else {
			return level * 20;
		}
	}

}
