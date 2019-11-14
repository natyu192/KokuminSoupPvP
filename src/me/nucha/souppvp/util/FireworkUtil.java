package me.nucha.souppvp.util;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import net.minecraft.server.v1_8_R3.EntityFireworks;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_8_R3.WorldServer;

public class FireworkUtil {

	public static void explode(Location l, Color color, boolean flicker, boolean trail, Type type) {
		WorldServer nmsWorld = ((CraftWorld) l.getWorld()).getHandle();
		EntityFireworks fw = new EntityFireworks(nmsWorld);
		FireworkMeta fm = ((Firework) fw.getBukkitEntity()).getFireworkMeta();
		fm.addEffect(FireworkEffect.builder().flicker(flicker).trail(trail).with(type).withColor(color).build());
		((Firework) fw.getBukkitEntity()).setFireworkMeta(fm);
		fw.setPosition(l.getX(), l.getY(), l.getZ());
		nmsWorld.addEntity(fw);
		PacketPlayOutEntityStatus thePacket = new PacketPlayOutEntityStatus(fw, (byte) 17);
		for (Player all : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) all).getHandle().playerConnection.sendPacket(thePacket);
		}
		fw.die();
	}

}
