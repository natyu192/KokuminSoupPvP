package me.nucha.souppvp.kit;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class KitStandard extends Kit {

	public KitStandard() {
		super("Standard", "standard", 2, 0);
		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
		sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
		ItemStack helmet = new ItemStack(Material.IRON_HELMET);
		ItemStack chest = new ItemStack(Material.IRON_CHESTPLATE);
		ItemStack legs = new ItemStack(Material.IRON_LEGGINGS);
		ItemStack boots = new ItemStack(Material.IRON_BOOTS);
		items.put(0, sword);
		this.helmet = helmet;
		this.chest = chest;
		this.legs = legs;
		this.boots = boots;
		setUnbreakable(true);
		setUndroppable(true);
		setIcon(sword);
		description.add("§7攻撃力と防御力のバランスの取れたキット");
		description.add("");
		description.add("§6特殊効果:");
		description.add("  §7なし");
	}

}
