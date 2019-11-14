package me.nucha.souppvp.kit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import me.nucha.souppvp.SoupPvPPlugin;

public class KitKnight extends Kit {

	private int chanceToReduceDamage;

	public KitKnight() {
		super("Knight", "knight", 4, 5000);
		chanceToReduceDamage = 30;
		ItemStack sword = new ItemStack(Material.IRON_SWORD);
		sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
		ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
		ItemStack chest = new ItemStack(Material.IRON_CHESTPLATE);
		chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
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
		description.add("§7とても打たれ強いキット");
		description.add("");
		description.add("§6特殊効果:");
		description.add("  §e" + chanceToReduceDamage + "%§7の確率で受けたダメージを半減する");
		Bukkit.getServer().getPluginManager().registerEvents(new KitListener(), SoupPvPPlugin.getInstance());
	}

	public class KitListener implements Listener {

		@EventHandler(priority = EventPriority.LOW)
		public void onDamage(EntityDamageByEntityEvent event) {
			if (event.isCancelled()) {
				return;
			}
			if (event.getEntity() instanceof Player &&
					event.getDamager() instanceof Player) {
				Player p = (Player) event.getEntity();
				if (KitManager.isKitSelected(p, id)) {
					int chance = (int) (Math.random() * 100);
					if (chance < chanceToReduceDamage) {
						event.setDamage(event.getDamage() / 2);
						p.getWorld().playSound(p.getLocation(), Sound.BLAZE_HIT, 2f, 2f);
					}
				}
			}
		}

	}

}
