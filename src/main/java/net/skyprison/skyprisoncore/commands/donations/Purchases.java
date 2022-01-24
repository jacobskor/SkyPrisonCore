package net.skyprison.skyprisoncore.commands.donations;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class Purchases implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length < 1){
				File f = new File(Bukkit.getServer().getPluginManager().getPlugin("SkyPrisonCore")
						.getDataFolder() + "/donations/" + player.getUniqueId().toString() + ".yml");
				if (!f.exists()) {
					player.sendMessage(ChatColor.RED + "You have not donated!");
				} else {
					FileConfiguration playerPurchases = YamlConfiguration.loadConfiguration(f);
					Set setList = playerPurchases.getKeys(false);
					player.sendMessage(ChatColor.YELLOW + "----====" + ChatColor.GOLD + " Purchases " + ChatColor.YELLOW + "====-----");
					double total = 0;
					for (int i = 0; i < setList.size()-1; i++) {
						if (playerPurchases.contains(i + ".item-bought")) {
							String itemBought = playerPurchases.getString(i + ".item-bought");
							double itemPrice = playerPurchases.getDouble(i + ".item-price");
							String[] boughtDate = playerPurchases.getString(i + ".bought-date").split(" ");
							// String itemQuantity = playerPurchases.getString(i + ".item-quantity"); + " " + ChatColor.GOLD + itemQuantity + "x"
							player.sendMessage(ChatColor.DARK_AQUA + itemBought + ChatColor.WHITE + " -" + ChatColor.GREEN + " $" + itemPrice + ChatColor.YELLOW + " " + boughtDate[0]);
							total += playerPurchases.getDouble(i + ".item-price");
						}
					}

					player.sendMessage(ChatColor.YELLOW + "Total: " + ChatColor.GOLD + "$" + total);
				}
			} else if(args[0].equalsIgnoreCase("giveranks")) {
				if(player.hasPermission("skyprisoncore.donations.giveranks")) {
					File f = new File(Bukkit.getServer().getPluginManager().getPlugin("SkyPrisonCore")
							.getDataFolder() + "/donations");
					File[] donorFiles = f.listFiles();
					for (File file : donorFiles) {
						String playerUUID = file.getName().split("\\.")[0];
						if (!playerUUID.equalsIgnoreCase("test")) {
							double total = 0;
							FileConfiguration playerPurchases = YamlConfiguration.loadConfiguration(file);
							Set setList = playerPurchases.getKeys(false);
							for (int i = 0; i < setList.size(); i++) {
								if (playerPurchases.contains(i + ".item-bought")) {
									total += playerPurchases.getDouble(i + ".item-price");
								} else {
									break;
								}
							}
							OfflinePlayer donor = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
							if (total >= 0) {
								Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "lp user " + donor.getName() + " parent add donor");

							}
						}
					}
				}
			}
		}
		return true;
	}
}
