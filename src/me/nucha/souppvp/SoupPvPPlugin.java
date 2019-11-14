package me.nucha.souppvp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.nucha.core.packet.PacketHandler;
import me.nucha.souppvp.arenafight.arena.ArenaManager;
import me.nucha.souppvp.arenafight.duel.DuelManager;
import me.nucha.souppvp.arenafight.match.MatchListener;
import me.nucha.souppvp.arenafight.match.MatchManager;
import me.nucha.souppvp.command.CommandArena;
import me.nucha.souppvp.command.CommandDuel;
import me.nucha.souppvp.command.CommandKit;
import me.nucha.souppvp.command.CommandLanguage;
import me.nucha.souppvp.command.CommandLastInventory;
import me.nucha.souppvp.command.CommandLavaChallenge;
import me.nucha.souppvp.command.CommandNick;
import me.nucha.souppvp.command.CommandResource;
import me.nucha.souppvp.command.CommandSoup;
import me.nucha.souppvp.command.CommandSoupPvP;
import me.nucha.souppvp.command.CommandSpawn;
import me.nucha.souppvp.command.CommandSpectate;
import me.nucha.souppvp.kit.KitManager;
import me.nucha.souppvp.language.LanguageManager;
import me.nucha.souppvp.leaderboard.LeaderboardManager;
import me.nucha.souppvp.listener.BlockProtectionListener;
import me.nucha.souppvp.listener.CoinListener;
import me.nucha.souppvp.listener.CombatListener;
import me.nucha.souppvp.listener.CustomNPCListener;
import me.nucha.souppvp.listener.FFAListener;
import me.nucha.souppvp.listener.JoinListener;
import me.nucha.souppvp.listener.JumpPadListener;
import me.nucha.souppvp.listener.SignListener;
import me.nucha.souppvp.listener.SoupListener;
import me.nucha.souppvp.listener.combattag.CombatTagListener;
import me.nucha.souppvp.listener.combattag.CombatTagManager;
import me.nucha.souppvp.listener.gui.GuiKits;
import me.nucha.souppvp.listener.gui.GuiPartyTeaming;
import me.nucha.souppvp.listener.gui.GuiShop;
import me.nucha.souppvp.listener.gui.GuiStats;
import me.nucha.souppvp.listener.gui.ItemClickListener;
import me.nucha.souppvp.listener.perk.KillAndRunListener;
import me.nucha.souppvp.listener.perk.LuckyGappleListener;
import me.nucha.souppvp.nick.NickManager;
import me.nucha.souppvp.perk.PerkManager;
import me.nucha.souppvp.player.PlayerState;
import me.nucha.souppvp.player.PlayerUtil;
import me.nucha.souppvp.resource.ResourceListener;
import me.nucha.souppvp.resource.ResourceManager;
import me.nucha.souppvp.util.ConfigUtil;
import me.nucha.souppvp.util.ItemUtil;
import me.nucha.souppvp.util.LocationUtil;
import me.nucha.souppvp.util.PlayerDataUtil;
import me.nucha.souppvp.util.ScoreboardUtils;
import me.nucha.souppvp.util.cooldown.CooldownUtil;

public class SoupPvPPlugin extends JavaPlugin {

	private static SoupPvPPlugin plugin;
	private static CustomNPCListener customNpcListener;
	private static CombatTagListener.ForceFieldListener forceFieldListener;

	@Override
	public void onEnable() {
		plugin = this;
		saveDefaultConfig();
		ConfigUtil.init(plugin);
		LanguageManager.init();
		ItemUtil.init(plugin);
		PlayerUtil.init();
		// ScoreboardUtils.plugin(plugin);
		PerkManager.init();
		PlayerDataUtil.init(plugin);
		CombatTagManager.init();
		CooldownUtil.init(plugin);
		LocationUtil.init(plugin);
		PlayerState.init();
		LeaderboardManager.init();
		NickManager.init();
		KitManager.init(plugin);
		ArenaManager.init(plugin);
		MatchManager.init();
		DuelManager.init();
		ResourceManager.init(plugin);
		PacketHandler.registerPacketListener(customNpcListener = new CustomNPCListener());
		PacketHandler.registerPacketListener(forceFieldListener = new CombatTagListener.ForceFieldListener());
		getServer().getPluginManager().registerEvents(new BlockProtectionListener(), this);
		getServer().getPluginManager().registerEvents(new SoupListener(plugin), this);
		getServer().getPluginManager().registerEvents(new MatchListener(), this);
		getServer().getPluginManager().registerEvents(new CombatListener(), this);
		getServer().getPluginManager().registerEvents(new CombatTagListener(), this);
		getServer().getPluginManager().registerEvents(new FFAListener(), this);
		getServer().getPluginManager().registerEvents(new JumpPadListener(plugin), this);
		getServer().getPluginManager().registerEvents(new SignListener(), this);
		getServer().getPluginManager().registerEvents(new CoinListener(), this);
		getServer().getPluginManager().registerEvents(new ItemClickListener(), this);
		getServer().getPluginManager().registerEvents(new JoinListener(), this);
		getServer().getPluginManager().registerEvents(new ResourceListener(), this);

		getServer().getPluginManager().registerEvents(new GuiStats(), this);
		getServer().getPluginManager().registerEvents(new GuiShop(), this);
		getServer().getPluginManager().registerEvents(new GuiKits(), this);
		getServer().getPluginManager().registerEvents(new GuiPartyTeaming(), this);

		getServer().getPluginManager().registerEvents(new KillAndRunListener(), this);
		getServer().getPluginManager().registerEvents(new LuckyGappleListener(), this);
		for (Player all : Bukkit.getOnlinePlayers()) {
			ScoreboardUtils.displayBoard(all);
			JoinListener.displayNPCsAndHolograms(all);
			LeaderboardManager.showLeaderboards(all);
		}
		getCommand("soup").setExecutor(new CommandSoup());
		getCommand("kits").setExecutor(new CommandKit());
		getCommand("kit").setExecutor(new CommandKit());
		getCommand("souppvp").setExecutor(new CommandSoupPvP());
		getCommand("spawn").setExecutor(new CommandSpawn());
		getCommand("nick").setExecutor(new CommandNick());
		getCommand("unnick").setExecutor(new CommandNick());
		getCommand("nicklist").setExecutor(new CommandNick());
		getCommand("arena").setExecutor(new CommandArena());
		getCommand("arenas").setExecutor(new CommandArena());
		getCommand("spectate").setExecutor(new CommandSpectate());
		getCommand("duel").setExecutor(new CommandDuel());
		getCommand("accept").setExecutor(new CommandDuel());
		getCommand("resource").setExecutor(new CommandResource());
		getCommand("lastinventory").setExecutor(new CommandLastInventory());
		getCommand("language").setExecutor(new CommandLanguage());
		getCommand("lavachallengesucceed").setExecutor(new CommandLavaChallenge());
		for (Player all : Bukkit.getOnlinePlayers()) {
			for (Player all2 : Bukkit.getOnlinePlayers()) {
				if (all.canSee(all2)) {
					all.hidePlayer(all2);
					all.showPlayer(all2);
				}
			}
		}
	}

	@Override
	public void onDisable() {
		PlayerDataUtil.save();
		PacketHandler.unregisterPacketListener(customNpcListener);
		PacketHandler.unregisterPacketListener(forceFieldListener);
		FFAListener.removeBlocks();
		LeaderboardManager.removeHolograms();
		for (Player all : Bukkit.getOnlinePlayers()) {
			JoinListener.removeNPCsAndHolograms(all);
			CombatTagListener.sendRealBlocks(all);
		}
		NickManager.shutdown();
		SoupListener.shutdown();
		ArenaManager.shutdown();
		MatchManager.shutdown();
		KitManager.shutdown();
		ResourceManager.shutdown();
		LanguageManager.shutdown();
		saveConfig();
	}

	public static SoupPvPPlugin getInstance() {
		return plugin;
	}

}
