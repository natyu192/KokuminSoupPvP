package me.nucha.souppvp.util;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.google.common.collect.Lists;

public class CustomItem extends ItemStack {

	public CustomItem(Material type, int amount, String name, List<String> lore) {
		super(type, amount);
		ItemMeta im = getItemMeta();
		if (name != null) {
			im.setDisplayName(name);
		}
		if (lore != null) {
			im.setLore(lore);
		}
		setItemMeta(im);
	}

	public CustomItem(Material type, int amount, String name, String... lore) {
		super(type, amount);
		ItemMeta im = getItemMeta();
		if (name != null) {
			im.setDisplayName(name);
		}
		if (lore != null) {
			im.setLore(Arrays.asList(lore));
		}
		setItemMeta(im);
		this.setType(type);
		this.setAmount(amount);
		this.setItemMeta(im);
	}

	public CustomItem(Material type, int amount, String name, int data, String... lore) {
		super(type, amount, (byte) data);
		ItemMeta im = getItemMeta();
		if (name != null) {
			im.setDisplayName(name);
		}
		if (lore != null) {
			im.setLore(Arrays.asList(lore));
		}
		setItemMeta(im);
		this.setType(type);
		this.setAmount(amount);
		this.setItemMeta(im);
		this.setDurability((byte) data);
	}

	public CustomItem(Material type, int amount, String name) {
		super(type, amount);
		ItemMeta im = getItemMeta();
		if (name != null) {
			im.setDisplayName(name);
		}
		setItemMeta(im);
		this.setType(type);
		this.setAmount(amount);
		this.setItemMeta(im);
	}

	public CustomItem(Material type, int amount) {
		super(type, amount);
		this.setType(type);
		this.setAmount(amount);
	}

	public void setDisplayName(String name) {
		ItemMeta im = this.getItemMeta();
		im.setDisplayName(name);
		this.setItemMeta(im);
	}

	public void addLore(String text) {
		ItemMeta im = this.getItemMeta();
		List<String> lore = Lists.newArrayList();
		if (im.hasLore()) {
			lore = im.getLore();
		}
		lore.add(text);
		im.setLore(lore);
		this.setItemMeta(im);
	}

	public void setLore(String... lore) {
		ItemMeta im = this.getItemMeta();
		im.setLore(Arrays.asList(lore));
		this.setItemMeta(im);
	}

	public void setData(int data) {
		MaterialData mdata = new MaterialData((byte) data);
		this.setData(mdata);
	}

}
