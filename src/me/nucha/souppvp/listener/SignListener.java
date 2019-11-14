package me.nucha.souppvp.listener;

import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import me.nucha.souppvp.leaderboard.LeaderboardData;
import me.nucha.souppvp.leaderboard.LeaderboardManager;
import me.nucha.souppvp.player.PlayerUtil;
import me.nucha.souppvp.util.LocationUtil;
import me.nucha.souppvp.util.PlayerDataUtil;
import me.nucha.souppvp.util.cooldown.CooldownUtil;

public class SignListener implements Listener {

	@EventHandler
	public void onSignSet(SignChangeEvent event) {
		Player p = event.getPlayer();
		if (!p.hasPermission("souppvp.sign")) {
			return;
		}
		if (event.getLine(0).equalsIgnoreCase("[soup]") || event.getLine(0).equalsIgnoreCase("[soup]") ||
				event.getLine(0).equalsIgnoreCase("[soup]")) {
			event.setLine(0, "§6§lSoupPvP");
			String line2 = event.getLine(1);
			if (line2.equalsIgnoreCase("soup")) {
				event.setLine(1, "§bFree Soup");
				return;
			} else if (line2.equalsIgnoreCase("refill")) {
				event.setLine(1, "§4§lSoup Refill");
				return;
			} else if (line2.equalsIgnoreCase("spawn")) {
				event.setLine(1, "§bスポーンに戻る");
				return;
			} else if (line2.equalsIgnoreCase("leaveffa")) {
				event.setLine(1, "§bFFAから抜ける");
				return;
			} else if (line2.equalsIgnoreCase("joinffa")) {
				event.setLine(1, "§bFFAに入る");
				return;
			} else if (line2.startsWith("lb-")) {
				String category = line2.substring(3);
				if (PlayerDataUtil.defaults.containsKey(category)) {
					event.setLine(1, "§a§lLeaderboard");
					event.setLine(2, "§b" + LeaderboardManager.stringFormat(category));
				} else {
					event.setLine(1, "§4ERROR");
					event.setLine(2, "§4ERROR");
					event.setLine(3, "§4ERROR");
					p.sendMessage("§c" + category + " というstatsの項目がありません");
				}
				return;
			} else {
				event.setLine(1, "§4ERROR");
				event.setLine(2, "§4ERROR");
				event.setLine(3, "§4ERROR");
			}
			p.sendMessage("§a---------- 看板の設定 ----------");
			p.sendMessage("§e(2行目), [3行目], [4行目] §6--- §a(説明)");
			p.sendMessage("§esoup §6--- §aFree Soup");
			p.sendMessage("§erefill §6--- §aSoup Refill");
			p.sendMessage("§espawn §6--- §aスポーンに戻る");
			p.sendMessage("§ejoinffa §6--- §aFFAに入る");
			p.sendMessage("§eleaveffa §6--- §aFFAから抜ける");
		}
	}

	@EventHandler
	public void onClickSign(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block b = event.getClickedBlock();
			if (b.getState() instanceof Sign) {
				Sign s = (Sign) b.getState();
				if (s.getLine(0).equalsIgnoreCase("§6§lSoupPvP")) {
					Player p = event.getPlayer();
					if (s.getLine(1).equalsIgnoreCase("§bFree Soup")) {
						if (CooldownUtil.hasCooldown(p, "free soup")) {
							p.sendMessage("§eFree Soup: §c" + CooldownUtil.getCooldownExpireSeconds(p, 1, "free soup") + "秒");
							return;
						}
						Inventory soups = Bukkit.createInventory(null, 54);
						ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);
						Random random = new Random();
						for (int i = 0; i < 9; i++) {
							soups.setItem(random.nextInt(54), soup);
						}
						p.openInventory(soups);
						p.playSound(p.getLocation(), Sound.CHEST_OPEN, 1f, 0.5f);
						CooldownUtil.setCooldown(p, 30, "free soup");
					}
					if (s.getLine(1).equalsIgnoreCase("§4§lSoup Refill")) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "soup " + p.getName());
					}
					if (s.getLine(1).equalsIgnoreCase("§bスポーンに戻る")) {
						if (LocationUtil.isSet("lobby")) {
							p.teleport(LocationUtil.get("lobby"));
						}
					}
					if (s.getLine(1).equalsIgnoreCase("§bFFAから抜ける")) {
						PlayerUtil.leaveFFA(p);
					}
					if (s.getLine(1).equalsIgnoreCase("§bFFAに入る")) {
						PlayerUtil.leaveFFA(p);
					}
					if (s.getLine(1).equalsIgnoreCase("§a§lLeaderboard")) {
						for (String key : PlayerDataUtil.defaults.keySet()) {
							if (s.getLine(2).contains(LeaderboardManager.stringFormat(key))) {
								LeaderboardData data = LeaderboardManager.getLeaderboardData(key);
								List<String> texts = Lists.newArrayList();
								texts.add("§a§lLeaderboard");
								texts.add("§b§l" + LeaderboardManager.stringFormat(data.getCategory()));
								int i = 1;
								for (Entry<String, Integer> entry : data.getLeaderboard()) {
									if (i == 11) {
										break;
									}
									int score = entry.getValue();
									if (score == 0) {
										continue;
									}
									String color = "§a";
									String th = "th";
									if (i == 1) {
										color = "§d";
										th = "st";
									}
									if (i == 2) {
										color = "§e";
										th = "nd";
									}
									if (i == 3) {
										color = "§6";
										th = "rd";
									}
									if (p.getName().equalsIgnoreCase(entry.getKey())) {
										texts.add(color + "§l" + i + th + " §2§l- " + color + "§l" + entry.getKey() +
												" §l§2- §b§l" + score + " " + LeaderboardManager.stringFormat(data.getCategory()));
									} else {
										texts.add(color + "" + i + th + " §2- " + color + entry.getKey() +
												" §2- §b" + score + " " + LeaderboardManager.stringFormat(data.getCategory()));
									}
									i++;
								}
								if (data.getScore(p) >= 1) {
									int place = data.getPlace(p);
									String th = "th";
									if (place == 1)
										th = "st";
									if (place == 2)
										th = "nd";
									if (place == 3)
										th = "rd";
									texts.add("§dYou're on §e" + data.getPlace(p) + th + " Place§d!");
								} else {
									texts.add("§7You're not on the leaderboard.");
								}
								texts.add("§7§oLast update: " + data.getLastUpdatedDate());
								for (String text : texts) {
									p.sendMessage(text);
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		CooldownUtil.removeCooldown(event.getEntity(), "free soup");
	}

}
