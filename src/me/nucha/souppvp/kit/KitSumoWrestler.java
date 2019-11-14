package me.nucha.souppvp.kit;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;

import me.nucha.souppvp.SoupPvPPlugin;
import me.nucha.souppvp.language.LanguageManager;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityVelocity;

public class KitSumoWrestler extends Kit {

	private double fistDamage;
	private double fistDamageCritMultiplier;
	private PotionEffect speedEffect;
	private List<String> noDamageable;

	public KitSumoWrestler() {
		super("Sumo Wrestler", "sumo-wrestler", 6, 8000);
		fistDamage = 8;
		fistDamageCritMultiplier = 25;
		speedEffect = new PotionEffect(PotionEffectType.SPEED, 20 * 3, 0);
		noDamageable = Lists.newArrayList();
		ItemStack sword = new ItemStack(Material.AIR);
		ItemStack helmet = new ItemStack(Material.AIR);
		ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta chestMeta = (LeatherArmorMeta) chest.getItemMeta();
		chestMeta.setColor(Color.fromRGB(221, 143, 104));
		chest.setItemMeta(chestMeta);
		chest.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
		ItemStack legs = new ItemStack(Material.IRON_LEGGINGS);
		legs.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
		ItemStack boots = new ItemStack(Material.AIR);
		items.put(0, sword);
		this.helmet = helmet;
		this.chest = chest;
		this.legs = legs;
		this.boots = boots;
		setUnbreakable(true);
		setUndroppable(true);
		setIcon(new ItemStack(Material.LEASH));
		description.add("§7はっけよい、ﾉｺｯﾀﾉｺｯﾀ！");
		description.add("");
		description.add("§6特殊効果:");
		description.add("  §7体重が増え、ノックバックが小さくなる");
		description.add("  §6素手§7でしか攻撃できなくなる");
		description.add("  §6素手§7の§c攻撃力アップ");
		description.add("  §7攻撃すると§bスピードI(3秒)§7が付く");
		description.add("");
		description.add("§6素手の攻撃力: §c" + fistDamage + " (+" + fistDamageCritMultiplier + "% on critical)");
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
				Player p = (Player) event.getDamager();
				Player victim = (Player) event.getEntity();
				if (KitManager.isKitSelected(p, id)) {
					if (p.getItemInHand() != null) {
						String typeName = p.getItemInHand().getType().name();
						if (!typeName.equalsIgnoreCase("AIR")) {
							if (typeName.endsWith("SWORD") || typeName.endsWith("AXE") || typeName.endsWith("HOE")) {
								p.sendMessage(LanguageManager.get(p, "kits.sumo-wrestler.only-fists"));
								event.setDamage(0);
								return;
							}
						}
					}
					String playerName = p.getName();
					if (noDamageable.contains(playerName)) {
						return;
					}
					noDamageable.add(playerName);
					Bukkit.getScheduler().runTaskLater(SoupPvPPlugin.getInstance(), new BukkitRunnable() {
						@Override
						public void run() {
							noDamageable.remove(playerName);
						}
					}, 10L);
					event.setDamage(fistDamage);
					Location victimLocation = victim.getLocation();
					Location location = p.getLocation();
					if (!p.isOnGround() && victimLocation.getY() < location.getY()) {
						event.setDamage(fistDamage + (fistDamage * (fistDamageCritMultiplier / 100))); // +50%
					}
					p.removePotionEffect(PotionEffectType.SPEED);
					p.addPotionEffect(speedEffect);
				}
			}
		}

		@EventHandler
		public void onVelocity(PlayerVelocityEvent event) {
			if (event.isCancelled()) {
				return;
			}
			Player p = event.getPlayer();
			if (KitManager.isKitSelected(p, id)) {
				event.setCancelled(true);
				Vector velocity = event.getVelocity();
				velocity = velocity.multiply(0.85d);
				EntityPlayer entityPlayer = ((CraftPlayer) p).getHandle();
				PacketPlayOutEntityVelocity velocityPacket = new PacketPlayOutEntityVelocity(entityPlayer.getId(), velocity.getX(),
						velocity.getY(), velocity.getZ());
				entityPlayer.playerConnection.sendPacket(velocityPacket);
			}
		}

	}

}
