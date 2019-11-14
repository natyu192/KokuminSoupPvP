package me.nucha.souppvp.perk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.nucha.souppvp.util.SymbolUtils;

public class PerkKillAndRun extends Perk {

	public PerkKillAndRun() {
		super("kill-and-run", "Kill And Run",
				Arrays.asList(new String[] { "§cプレイヤーをキル§7すると", "§bスピード§7が付きます！" }), new ItemStack(Material.SUGAR),
				new int[] { 1000, 2000, 3000, 4000, 7500 }, 10);
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
		moreInfo.add("  §7スピードの強さ: §e" + SymbolUtils.convertRomaNumbers(getEffectAmplifier(level)));
		moreInfo.add("  §7スピードの長さ: §e" + getEffectDuration(level) + "秒");
		moreInfo.add(" ");
		moreInfo.addAll(Arrays.asList(super.getMoreInfo(p)));
		return moreInfo.toArray(new String[] {});
	}

	public int getEffectDuration(Player p) {
		return getEffectDuration(getLevel(p));
	}

	public int getEffectDuration(int level) {
		if (level > 0) {
			if (level >= 4) {
				return 4 + 3;
			} else {
				return level + 3;
			}
		}
		return 0;
	}

	public int getEffectAmplifier(int level) {
		if (level >= 5) {
			return 3;
		}
		return 2;
	}

	public int getEffectAmplifier(Player p) {
		return getEffectAmplifier(getLevel(p));
	}

}
