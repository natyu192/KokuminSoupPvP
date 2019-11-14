package me.nucha.souppvp.kit;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import com.google.common.collect.Lists;

import me.nucha.souppvp.player.PlayerUtil;
import me.nucha.souppvp.util.ItemUtil;
import me.nucha.souppvp.util.PlayerDataUtil;

public abstract class Kit {

	protected String name;
	protected String id;
	protected HashMap<Integer, ItemStack> items;
	protected ItemStack helmet;
	protected ItemStack chest;
	protected ItemStack legs;
	protected ItemStack boots;
	protected List<PotionEffect> potionEffects;
	protected int guiSlot;
	protected List<String> description;
	private ItemStack icon;
	protected int cost;

	public Kit(String name, String id, int guiSlot, int cost) {
		this.name = name;
		this.id = id;
		this.items = new HashMap<>();
		ItemStack air = new ItemStack(Material.AIR);
		this.helmet = air;
		this.chest = air;
		this.legs = air;
		this.boots = air;
		this.potionEffects = Lists.newArrayList();
		this.guiSlot = guiSlot;
		this.description = Lists.newArrayList();
		this.icon = new ItemStack(Material.EMERALD);
		this.cost = cost;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public HashMap<Integer, ItemStack> getItems() {
		return items;
	}

	public int getGuiSlot() {
		return guiSlot;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public void setIcon(ItemStack icon) {
		icon = icon.clone();
		ItemMeta meta = icon.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		icon.setItemMeta(meta);
		this.icon = icon;
	}

	public List<String> getDescription() {
		return description;
	}

	public void give(Player p) {
		PlayerUtil.clearInventory(p);
		for (int slot : items.keySet()) {
			p.getInventory().setItem(slot, items.get(slot));
		}
		PlayerUtil.removePotionEfects(p);
		for (PotionEffect effect : potionEffects) {
			p.addPotionEffect(effect);
		}
		ItemStack mushroom1 = new ItemStack(Material.RED_MUSHROOM, 32);
		ItemStack mushroom2 = new ItemStack(Material.BROWN_MUSHROOM, 32);
		ItemStack bowl = new ItemStack(Material.BOWL, 32);
		p.getInventory().setItem(15, mushroom1);
		p.getInventory().setItem(16, mushroom2);
		p.getInventory().setItem(17, bowl);
		ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);
		for (int i = 0; i < 36; i++) {
			p.getInventory().addItem(soup);
		}
		p.getInventory().setHelmet(helmet);
		p.getInventory().setChestplate(chest);
		p.getInventory().setLeggings(legs);
		p.getInventory().setBoots(boots);
		p.updateInventory();
	}

	public boolean hasPurchasedBy(Player p) {
		List<String> kits = PlayerDataUtil.getStringList(p, "kits-purchased");
		return kits.contains(id);
	}

	protected void setUnbreakable(boolean flag) {
		for (int slot : items.keySet()) {
			ItemUtil.setUnbreakable(items.get(slot), flag);
		}
		if (helmet != null)
			ItemUtil.setUnbreakable(helmet, flag);
		if (chest != null)
			ItemUtil.setUnbreakable(chest, flag);
		if (legs != null)
			ItemUtil.setUnbreakable(legs, flag);
		if (boots != null)
			ItemUtil.setUnbreakable(boots, flag);
	}

	protected void setUndroppable(boolean flag) {
		for (int slot : items.keySet()) {
			ItemUtil.setUndroppable(items.get(slot), flag);
		}
		if (helmet != null)
			ItemUtil.setUndroppable(helmet, flag);
		if (chest != null)
			ItemUtil.setUndroppable(chest, flag);
		if (legs != null)
			ItemUtil.setUndroppable(legs, flag);
		if (boots != null)
			ItemUtil.setUndroppable(boots, flag);
	}

	public int getCost() {
		return cost;
	}

	public boolean isEnabled() {
		return KitManager.isEnabled(id);
	}

	public boolean isForceUnlocked() {
		return KitManager.isForceUnlocked(id);
	}

}
