package me.nucha.souppvp.perk;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.nucha.souppvp.util.PlayerDataUtil;
import me.nucha.souppvp.util.SymbolUtils;

public class Perk {

	private String id;
	private String name;
	private List<String> description;
	private ItemStack icon;
	private int[] upgradeCosts;
	private int guiSlot;

	public Perk(String id, String name, List<String> description, ItemStack icon, int[] upgradeCosts, int guiSlot) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.icon = icon;
		this.upgradeCosts = upgradeCosts;
		this.guiSlot = guiSlot;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<String> getDescription() {
		return description;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public int[] getUpgradeCosts() {
		return upgradeCosts;
	}

	public int getGuiSlot() {
		return guiSlot;
	}

	public String[] getMoreInfo(Player p) {
		List<String> moreInfo = new ArrayList<String>();
		int level = getLevel(p);
		int cost = getNextCost(p);
		if (level > 0) {
			String text = "§7Perk Level: §b" + SymbolUtils.convertRomaNumbers(level);
			if (cost == -1) {
				text += " §a(Maxed)";
			}
			moreInfo.add(text);
		}
		if (cost > 0) {
			moreInfo.add("§6Upgrade Cost: §e" + cost + " coins");
		}
		return moreInfo.toArray(new String[] {});
	}

	public int getLevel(Player p) {
		return PlayerDataUtil.getInt(p, "perks." + getId());
	}

	public boolean hasPurchasedBy(Player p) {
		return getLevel(p) > 0;
	}

	public int getNextCost(Player p) {
		int level = getLevel(p);
		if (level == upgradeCosts.length) {
			return -1;
		} else {
			return upgradeCosts[level];
		}
	}

}
