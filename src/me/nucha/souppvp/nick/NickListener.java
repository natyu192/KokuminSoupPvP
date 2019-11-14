package me.nucha.souppvp.nick;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.nucha.core.packet.PacketInfo;
import me.nucha.core.packet.PacketListener;
import me.nucha.core.reflect.Reflection;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.PlayerInfoData;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.WorldServer;

public class NickListener implements PacketListener, Listener {

	private Property kokuminSkin;

	@SuppressWarnings("unused")
	private Property namemSkin;

	public NickListener() {
		kokuminSkin = new Property("textures",
				"eyJ0aW1lc3RhbXAiOjE1NTM0MDk0NzYwNzAsInByb2ZpbGVJZCI6ImE1OTQwMTgzOGZkOTQyNzJiOWVhMGQyOGM1NzgwZTY4IiwicHJvZmlsZU5hbWUiOiJrb2t1bWluIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lNmE5ZTFkYTRiZWExMGQ0N2Q5YWE3ZjNlOTZhYWM5MjFjMTY0NTJiYjIxODExYmVkMmJiMGEzZDI1M2FkOTY0In19fQ==",
				"RynEL0DuMKojQiobq1aDEoIaIq6zS2u4yYfizJTgenwQ/d9lMxbTTwZ9KQVkUhaVjClznWvAnb9WDTKPUWcb0Cby4sRiZgJX2kbICDdFJTo13fGoLEHRlMoaRyUtUCbSbbtNgW1Hzy45XJvxmCexObJ+tYhjNVeUrxkDWbaTxcKcJyvmx0OlhJ7ZUUITocRG1ENtOoYOPunbHu4N+BgfcvVzj6eTcuWQGBXQAzfsV0CEiacUwic8LmNk1la3VL7Jy+77KHLhUMoSaHR95jKEzWNDUyHubcbYFfaDS0okz83X2l5vg2J0kLHVwUnuv7MGqvFlhJFPaC/caQx9qrY7S/Kia3ttqYX3StJUTG7TA1nAA16xGj6cCcCflIFBWUlkGkLCJZpD1pT7CpClFIirxlii7LOO1/KbUOfiE2zaBlumX6DK3xemR3A1ZQ7eq3SLyay3GCm42jNA+oyTXXnijXYxIvH7XALQ8Zt6ZlrZdqQ+Zix5juI+salUhAEn5/Lc7IFQqR/ZnWMljjK/lSBE2+Glxp/+8WQHASzwU4Bj5TeRLHuh52xVYkSVCxhr9NYU525wnLdAcOiCnayrDdVKxB5E3mRCTT4DLYG9n/erq1V95EU4vRs5lluK2riUW0yyaIAOA0xIZQhVb/DW3NmLcfu8XSMO1ybdrSm2TSGWi8w=");
		namemSkin = new Property("textures",
				"eyJ0aW1lc3RhbXAiOjE1NTM0MTE0MzM1NDUsInByb2ZpbGVJZCI6Ijc2Yzc0NDY5MWYyNDRjMDA4M2RjY2MwNjRmODJlOWE5IiwicHJvZmlsZU5hbWUiOiJuYW1lbSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjcwY2ExMjMyOGM0ZmMzNTgxZDRiYjhiNDJiNzI4OWQxYzcwNjgwNTQwNDc1YjA4NWM5OGQwMmUzMWI1ZmRlMiIsIm1ldGFkYXRhIjp7Im1vZGVsIjoic2xpbSJ9fX19",
				"ePIPcH8eSr0cE/VJ/9nQlPDcQtFpCL7TAUO8cphFWe2wItmS4PpyxFql4daWgPtBPXE7n8sVjxHvH1nToCSNlsu8EW4YAqyqRJbRt9cZDmPvlsyETDcunG8SlwjBFDn7qFzlDCtNpUIxnhTeXZPzs1SDk7NcojSSiHi6mKUGqO1vGzY2PqHKTrDCzgnHqrojrJ9kM0v4N4wkBNwwaJj2f7C6ixdgMdvqDT6RhVGuPuAlPz+GtgfvyyM0IAX3L+AmeUvtsefbGhSCBHjxzW6DMe1enSHIPbmmNRz44plBmteOfHBDfhuSl5weig4QgVpPNhER4nNQIa3Q2yH8+DmNdOhcYA/SNZgR52ztPAYJN53T/K+20bDk60yWn1yiBVmhqxJ8RyJBjxbybESHkI09aD2hmJINkoj8XQY/TLXGUTT5XIW/Qb45+kKngu1OuA4FrqEFmUFZ5WQx6T/SZjrzgJVwsJt8fohnKhYdZiFh7ijswXx0tgTW52nD+dQn+VMrPSmTAM3kdSZ3y2HYqWV6T8voKUMO+VpptotcBkM/C2c/15rmXa4nPbYx42oq7xKQvgwV1pD2MfNynNpn2ZsU6bXI/oP4o+4Fhb8O7C4XdSR1adspLVeQW8KZdeiDzrMcuvy7nuXyEwgf+eXCX5AClB3RPPQAvouKAb6QCrMMMLc=");
	}

	@Override
	public void playerReceivePacket(PacketInfo packet) {
		if (packet.getPacket() instanceof PacketPlayOutPlayerInfo) {
			EnumPlayerInfoAction a = (EnumPlayerInfoAction) packet.getPacketValue("a");
			if (a.equals(EnumPlayerInfoAction.ADD_PLAYER)) {
				List<PlayerInfoData> b = (List<PlayerInfoData>) packet.getPacketValue("b");
				for (PlayerInfoData data : Lists.newArrayList(b)) {
					String name = data.a().getName();
					Player p = Bukkit.getPlayer(name);
					if (p != null) {
						name = NickManager.getName(p);
					}
					if (!data.a().getName().equalsIgnoreCase("Nucha") || (p != null && NickManager.isNicked(p))) {
						String date = new SimpleDateFormat("MM/dd").format(new Date());
						if (date.equalsIgnoreCase("03/25")) { // tanjoubi
							GameProfile profile = new GameProfile(data.a().getId(), name);
							MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
							WorldServer wServer = ((CraftWorld) packet.getPlayer().getWorld()).getHandle();
							profile.getProperties().put("textures", kokuminSkin);
							PlayerInteractManager piManager = new PlayerInteractManager(wServer);
							EntityPlayer entityPlayer = new EntityPlayer(server, wServer, profile, piManager);
							packet.setPacket(
									new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
							return;
						}
					}
					if (p != null && NickManager.isNicked(p)) {
						MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
						WorldServer wServer = ((CraftWorld) p.getWorld()).getHandle();
						GameProfile profile = new GameProfile(p.getUniqueId(), NickManager.getNickname(p));
						profile.getProperties().put("textures", NickManager.getProperties().get(p));
						PlayerInteractManager piManager = new PlayerInteractManager(wServer);
						EntityPlayer entityPlayer = new EntityPlayer(server, wServer, profile, piManager);
						packet.setPacket(
								new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
						return;
					}
				}
			}
		}
		/*if (packet.getPacket() instanceof PacketPlayOutNamedEntitySpawn) {
			UUID id = (UUID) packet.getPacketValue("b");
			Player p = Bukkit.getPlayer(id);
			if (p != null && NickManager.isNicked(p)) {
				Reflection.setValue(packet.getPacket(), "b", UUID.randomUUID());// NickManager.getNickUUID(p));
			}
		}*/
	}

	@Override
	public void playerSendPacket(PacketInfo packet) {
	}

	@EventHandler
	public void onTab(PlayerChatTabCompleteEvent event) {
		Collection<String> arrays = event.getTabCompletions();
		String token = event.getLastToken();
		for (Player all : Bukkit.getOnlinePlayers()) {
			if (NickManager.isNicked(all)) {
				arrays.remove(all.getName());
				String nickname = NickManager.getNickname(all);
				if (nickname.toLowerCase().startsWith(token.toLowerCase())) {
					arrays.add(nickname);
				}
			}
		}
		Reflection.setValue(event, "completions", arrays);
	}
}
