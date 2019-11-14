package me.nucha.souppvp.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;

import me.nucha.core.npc.NPC;
import me.nucha.core.packet.PacketInfo;
import me.nucha.core.packet.PacketListener;
import me.nucha.souppvp.SoupPvPPlugin;
import me.nucha.souppvp.listener.gui.GuiShop;
import me.nucha.souppvp.listener.gui.GuiStats;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity.EnumEntityUseAction;

public class CustomNPCListener implements PacketListener {

	@Override
	public void playerSendPacket(PacketInfo packet) {
		if (packet.getPacket() instanceof PacketPlayInUseEntity) {
			Player p = packet.getPlayer();
			int id = (int) packet.getPacketValue("a");
			EnumEntityUseAction ee = (EnumEntityUseAction) packet.getPacketValue("action");
			if (ee == EnumEntityUseAction.INTERACT) {
				for (NPC n : Lists.newArrayList(JoinListener.npcs)) {
					if (n.getPlayer().getUniqueId().equals(p.getUniqueId()) && n.getId() == id) {
						if (n.hasTag("Stats")) {
							Bukkit.getScheduler().runTask(SoupPvPPlugin.getInstance(), new BukkitRunnable() {
								@Override
								public void run() {
									GuiStats.open(p);
								}
							});
							return;
						}
						if (n.hasTag("Shop")) {
							Bukkit.getScheduler().runTask(SoupPvPPlugin.getInstance(), new BukkitRunnable() {
								@Override
								public void run() {
									GuiShop.open(p);
								}
							});
							return;
						}
					}
				}
			}
		}
	}

	@Override
	public void playerReceivePacket(PacketInfo packet) {
	}

}
