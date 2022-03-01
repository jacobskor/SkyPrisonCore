package net.skyprison.skyprisoncore.commands.economy;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import net.skyprison.skyprisoncore.SkyPrisonCore;
import net.skyprison.skyprisoncore.utils.CooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Bounty implements CommandExecutor {
	private SkyPrisonCore plugin;
	private Connection conn;

	public Bounty(SkyPrisonCore plugin) {
		this.plugin = plugin;
		this.conn = plugin.conn;
	}

	public void openGUI(Player player, int page) {
		File f = new File(Bukkit.getServer().getPluginManager().getPlugin("SkyPrisonCore")
				.getDataFolder() + "/bounties.yml");
		FileConfiguration bounty = YamlConfiguration.loadConfiguration(f);
		Set<String> bountyList = bounty.getKeys(false);
		ArrayList<String> arr = new ArrayList<>();
		ArrayList<Integer> totalPages = new ArrayList<>();
		for(String bountyPlayer : bountyList) {
			if(bounty.getInt(bountyPlayer + ".page") == page) {
				arr.add(bountyPlayer);
			}
			totalPages.add(bounty.getInt(bountyPlayer + ".page"));
		}
		Inventory bounties = Bukkit.createInventory(null, 54, ChatColor.RED + "Bounties");
		int i = 0;
		for (String bountyPlayer : arr) {
			ArrayList<String> lore = new ArrayList<>();
			ItemStack head = new ItemStack(Material.PLAYER_HEAD);
			SkullMeta meta = (SkullMeta) head.getItemMeta();
			meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(bountyPlayer)));
			meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + Bukkit.getOfflinePlayer(UUID.fromString(bountyPlayer)).getName());
			lore.add(ChatColor.YELLOW + "Prize: $" + bounty.getDouble(bountyPlayer + ".bounty-prize"));
			meta.setLore(lore);
			head.setItemMeta(meta);
			bounties.setItem(i, head);
			i++;
		}
		ItemStack pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemStack pageChange = new ItemStack(Material.PAPER);
		ItemMeta itemMeta = pageChange.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "Next Page");
		pageChange.setItemMeta(itemMeta);
		for (int b = 45; b < 54; b++) {
			if (page == 0) {
				if(totalPages.size() < 1) {
					bounties.setItem(b, pane);
				} else {
					if (Collections.max(totalPages).equals(page)) {
						bounties.setItem(b, pane);
					} else {
						if(b != 52) {
							bounties.setItem(b, pane);
						} else {
							bounties.setItem(b, pageChange);
						}
					}
				}
			} else if (Collections.max(totalPages).equals(page)) {
				if(b != 46) {
					bounties.setItem(b, pane);
				} else {
					itemMeta.setDisplayName(ChatColor.GREEN + "Previous Page");
					pageChange.setItemMeta(itemMeta);
					bounties.setItem(b, pageChange);
				}
			} else {
				if(b != 46 && b != 52) {
					bounties.setItem(b, pane);
				} else if(b == 46) {
					itemMeta.setDisplayName(ChatColor.GREEN + "Previous Page");
					pageChange.setItemMeta(itemMeta);
					bounties.setItem(b, pageChange);
				} else {
					bounties.setItem(b, pageChange);
				}
			}
		}
		player.openInventory(bounties);
	}


/*	public void openGUI(Player player, int page) throws SQLException {
		String sql = "SELECT * FROM bounties";
		HashMap<UUID, Double> bountyPlayers = new HashMap<>();
		ResultSet bountyData = conn.prepareStatement(sql).executeQuery();
		while(bountyData.next()) {
			bountyPlayers.put(UUID.fromString(bountyData.getString(1)), bountyData.getDouble(2));
		}
		Map<UUID, Double> sortedMap = bountyPlayers.entrySet().stream()
				.sorted(Comparator.comparingDouble(e -> e.getValue()))
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(a, b) -> { throw new AssertionError(); },
						LinkedHashMap::new
				));

		Inventory bounties = Bukkit.createInventory(null, 54, ChatColor.RED + "Bounties");
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			int i = 0;
			for (UUID bountyPlayer : sortedMap.keySet()) {
				ArrayList<String> lore = new ArrayList<>();
				ItemStack head = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta meta = (SkullMeta) head.getItemMeta();
				meta.setOwningPlayer(Bukkit.getOfflinePlayer(bountyPlayer));
				meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + Bukkit.getOfflinePlayer(bountyPlayer).getName());
				lore.add(ChatColor.YELLOW + "Prize: $" + sortedMap.get(bountyPlayer));
				meta.setLore(lore);
				head.setItemMeta(meta);
				bounties.setItem(i, head);
				i++;
			}
		});
		ItemStack pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemStack pageChange = new ItemStack(Material.PAPER);
		ItemMeta itemMeta = pageChange.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "Next Page");
		pageChange.setItemMeta(itemMeta);
		for(int i = 0; i < 54; i++) {
			if(i < 45) {

			} else {

			}
		}
		player.openInventory(bounties);
	}*/

	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	private final CooldownManager cooldownManager = new CooldownManager();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			CMIUser user = CMI.getInstance().getPlayerManager().getUser(player);
			File f = new File(Bukkit.getServer().getPluginManager().getPlugin("SkyPrisonCore")
					.getDataFolder() + "/bounties.yml");
			if (!f.exists()) {
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			FileConfiguration bounty = YamlConfiguration.loadConfiguration(f);
			Set<String> bountyList = bounty.getKeys(false);
			//  /bounty set <player> <prize>
			String bountyHelp = ChatColor.WHITE + "----====" + ChatColor.RED + " Bounties " + ChatColor.WHITE + "====----" + ChatColor.YELLOW + "\n/bounty set <player> <amount> " + ChatColor.WHITE + "- Set a bounty on a player" + ChatColor.YELLOW + "\n/bounty help " + ChatColor.WHITE + "- Shows this" + ChatColor.YELLOW + "\n/bounty list " + ChatColor.WHITE + "- Shows all players with bounties" + ChatColor.YELLOW + "\n/bounty mute " + ChatColor.WHITE + "- Mutes/Unmutes bounty messages except for bounties towards yourself";
			if(args.length < 1) {
				player.sendMessage(bountyHelp);
			}else if(args[0].equalsIgnoreCase("set")) {
				long timeLeft = System.currentTimeMillis() - cooldownManager.getCooldown(player.getUniqueId());
				if(TimeUnit.MILLISECONDS.toSeconds(timeLeft) >= CooldownManager.DEFAULT_COOLDOWN) {
					if (!(args.length < 3)) {
						if (Bukkit.getPlayer(args[1]) != null) {
							if (Double.parseDouble(args[2]) >= 100) {
								String bountyTarget = Bukkit.getPlayer(args[1]).getUniqueId().toString();
								if (!player.equals(Bukkit.getPlayer(args[1]))) {
									if (!Bukkit.getPlayer(args[1]).hasPermission("skyprisoncore.command.bounty.bypass")) {
										if (bountyList.contains(bountyTarget)) {
											ArrayList arr = (ArrayList) bounty.getList(bountyTarget + ".bounty-contributors");
											if (!arr.contains(player.getName())) {
												arr.add(player.getUniqueId().toString());
												bounty.set(bountyTarget + ".bounty-contributors", arr);
											}
											bounty.set(bountyTarget + ".bounty-prize", bounty.getDouble(bountyTarget + ".bounty-prize") + round(Double.parseDouble(args[2]), 2));
											try {
												if (user.getBalance() >= Double.parseDouble(args[2])) {
													bounty.save(f);
													for (Player online : Bukkit.getServer().getOnlinePlayers()) {
														if (!online.hasPermission("skyprisoncore.command.bounty.silent")) {
															online.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "Bounties" + ChatColor.WHITE + "] " + ChatColor.YELLOW + player.getName() + " has increased the bounty on " + Bukkit.getPlayer(args[1]).getName() + " by " + ChatColor.GREEN + "$" + round(Double.parseDouble(args[2]), 2) + "!");
														}
													}
													Bukkit.getPlayer(args[1]).sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "Bounties" + ChatColor.WHITE + "] " + ChatColor.YELLOW + player.getName() + " has put a bounty on you!");
													Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "money take " + player.getName() + " " + round(Double.parseDouble(args[2]), 2));
													Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "cmi usermeta " + args[1] + " increment bounties_received +1 -s");
													Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "cmi usermeta " + player.getName() + " increment bounties_placed +1 -s");
													cooldownManager.setCooldown(player.getUniqueId(), System.currentTimeMillis());
												} else {
													player.sendMessage(ChatColor.RED + "You do not have enough money..");
												}
											} catch (final IOException e) {
												e.printStackTrace();
											}
										} else if (!bountyList.contains(bountyTarget)) {
											int page = 0;
											for (int i = 0; i < bountyList.size(); ) {
												ArrayList arr = new ArrayList();
												for (String bountyPlayer : bountyList) {
													if (bounty.getInt(bountyPlayer + ".page") == i) {
														arr.add(bountyPlayer);
													}
												}
												if (arr.size() <= 44) {
													page = i;
													break;
												} else {
													i++;
													continue;
												}
											}
											bounty.set(bountyTarget + ".bounty-prize", round(Double.parseDouble(args[2]), 2));
											bounty.set(bountyTarget + ".page", page);
											bounty.set(bountyTarget + ".bounty-contributors", new ArrayList(Collections.singleton(player.getUniqueId().toString())));
											try {
												if (user.getBalance() > Double.parseDouble(args[2])) {
													bounty.save(f);
													for (Player online : Bukkit.getServer().getOnlinePlayers()) {
														if (!online.hasPermission("skyprisoncore.command.bounty.silent")) {
															online.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "Bounties" + ChatColor.WHITE + "]" + ChatColor.YELLOW + " " + player.getName() + " has put a " + ChatColor.GREEN + "$" + round(Double.parseDouble(args[2]), 2) + ChatColor.YELLOW + " bounty on " + Bukkit.getPlayer(args[1]).getName() + "!");
														}
													}
													Bukkit.getPlayer(args[1]).sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "Bounties" + ChatColor.WHITE + "] " + ChatColor.YELLOW + player.getName() + " has put a bounty on you!");
													Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "money take " + player.getName() + " " + round(Double.parseDouble(args[2]), 2));
													Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "cmi usermeta " + args[1] + " increment bounties_received +1 -s");
													Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "cmi usermeta " + player.getName() + " increment bounties_placed +1 -s");
													cooldownManager.setCooldown(player.getUniqueId(), System.currentTimeMillis());
												} else {
													player.sendMessage(ChatColor.RED + "You do not have enough money..");
												}
											} catch (final IOException e) {
												e.printStackTrace();
											}
										}
									} else {
										player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "Bounties" + ChatColor.WHITE + "] " + ChatColor.RED + "You can't put a bounty on this player!");
									}
								} else {
									player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "Bounties" + ChatColor.WHITE + "] " + ChatColor.RED + "You can't put a bounty on yourself!");
								}
							} else {
								player.sendMessage(ChatColor.RED + "Bounty must be equal or higher than $100!");
							}
						} else {
							player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "Bounties" + ChatColor.WHITE + "] " + ChatColor.RED + "Player is not online or doesn't exist..");
						}
					} else {
						player.sendMessage(ChatColor.RED + "/bounty set <player> <amount>");
					}
				} else {
					Long timeRem = CooldownManager.DEFAULT_COOLDOWN - TimeUnit.MILLISECONDS.toSeconds(timeLeft);
					player.sendMessage(ChatColor.RED+ "" + timeRem + " seconds before you can use this again.");
				}
			} else if(args[0].equalsIgnoreCase("list")) {
				openGUI(player, 0);
			} else if(args[0].equalsIgnoreCase("help")) {
				player.sendMessage(bountyHelp);
			} else if(args[0].equalsIgnoreCase("mute")) {
				if(!player.hasPermission("skyprisoncore.command.bounty.silent")) {
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "lp user " + player.getName() + " permission set skyprisoncore.command.bounty.silent true");
					player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "Bounties" + ChatColor.WHITE + "] " + ChatColor.YELLOW + "Bounty messages muted!");
				} else {
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "lp user " + player.getName() + " permission set skyprisoncore.command.bounty.silent false");
					player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "Bounties" + ChatColor.WHITE + "] " + ChatColor.YELLOW + "Bounty messages unmuted!");
				}
			}
		}
		return true;
	}
}

