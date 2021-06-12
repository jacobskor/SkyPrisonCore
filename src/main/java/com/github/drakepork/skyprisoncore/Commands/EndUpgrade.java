package com.github.drakepork.skyprisoncore.Commands;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.github.drakepork.skyprisoncore.Core;
import com.google.inject.Inject;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EndUpgrade implements CommandExecutor {
	private Core plugin;

	@Inject
	public EndUpgrade(Core plugin) {
		this.plugin = plugin;
	}

	public int upgradeCost(Player player, Boolean enchTransfer, Boolean repairReset) {
		int totalCost = 0;

		if(repairReset) {
			totalCost += 350000;
		}
		if(enchTransfer) {
			totalCost += 100000;
		}
		switch(player.getInventory().getItemInMainHand().getType().toString()) {
			case "DIAMOND_AXE":
				totalCost += 300000;
				break;
			case "DIAMOND_PICKAXE":
				totalCost += 300000;
				break;
			case "DIAMOND_SHOVEL":
				totalCost += 100000;
				break;
			case "DIAMOND_HOE":
				totalCost += 200000;
				break;
			case "DIAMOND_HELMET":
				totalCost += 500000;
				break;
			case "DIAMOND_CHESTPLATE":
				totalCost += 800000;
				break;
			case "DIAMOND_LEGGINGS":
				totalCost += 700000;
				break;
			case "DIAMOND_BOOTS":
				totalCost += 400000;
				break;
		}

		return totalCost;
	}


	public void confirmGUI(Player player, Boolean enchTransfer, Boolean repairReset) {
		Inventory confirmGUI = Bukkit.createInventory(null, 27, ChatColor.RED + "Confirm Upgrade");

		int totalCost = upgradeCost(player, enchTransfer, repairReset);

		ItemStack confirmButton = new ItemStack(Material.GREEN_CONCRETE);
		ItemMeta confirmMeta = confirmButton.getItemMeta();
		confirmMeta.setDisplayName(plugin.colourMessage("&a&LConfirm Upgrade"));

		ArrayList lore = new ArrayList();
		if (!player.hasPermission("skyprisoncore.command.endupgrade.first-time")) {
			lore.add(plugin.colourMessage("&7Total Cost: &a$" + plugin.formatNumber(totalCost)));
		} else {
			lore.add(plugin.colourMessage("&7Total Cost: &aFREE"));
		}

		NamespacedKey enchKey = new NamespacedKey(plugin, "ench-state");
		NamespacedKey repKey = new NamespacedKey(plugin, "repair-state");

		if(enchTransfer) {
			confirmMeta.getPersistentDataContainer().set(enchKey, PersistentDataType.INTEGER, 1);
			lore.add(plugin.colourMessage("&7Transferring enchants is &2&lENABLED"));
		} else {
			confirmMeta.getPersistentDataContainer().set(enchKey, PersistentDataType.INTEGER, 0);
			lore.add(plugin.colourMessage("&7Keeping enchants is &c&lNOT ENABLED"));
		}

		if(repairReset) {
			confirmMeta.getPersistentDataContainer().set(repKey, PersistentDataType.INTEGER, 1);
			lore.add(plugin.colourMessage("&7Resetting Repair Cost is &2&lENABLED"));
		} else {
			confirmMeta.getPersistentDataContainer().set(repKey, PersistentDataType.INTEGER, 0);
			lore.add(plugin.colourMessage("&7Resetting Repair Cost is &c&lNOT ENABLED"));
		}

		confirmMeta.setLore(lore);

		confirmButton.setItemMeta(confirmMeta);

		ItemStack cancelButton = new ItemStack(Material.RED_CONCRETE);
		ItemMeta cancelMeta = cancelButton.getItemMeta();
		cancelMeta.setDisplayName(plugin.colourMessage("&c&lCancel Upgrade"));
		cancelButton.setItemMeta(cancelMeta);

		ItemStack whitePane = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
		ItemMeta whiteMeta = whitePane.getItemMeta();
		whiteMeta.setDisplayName(" ");
		whitePane.setItemMeta(whiteMeta);


		player.openInventory(confirmGUI);

		for(int i = 0; i < 27; i++) {
			if(i == 0) {
				NamespacedKey key = new NamespacedKey(plugin, "stop-click");
				whiteMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
				NamespacedKey key1 = new NamespacedKey(plugin, "gui-type");
				whiteMeta.getPersistentDataContainer().set(key1, PersistentDataType.STRING, "confirm-endupgrade");
				whitePane.setItemMeta(whiteMeta);
				confirmGUI.setItem(i, whitePane);
			} else if(i == 4) {
				confirmGUI.setItem(i, player.getInventory().getItemInMainHand());
			} else if(i == 11) {
				confirmGUI.setItem(i, confirmButton);
			} else if(i == 15) {
				confirmGUI.setItem(i, cancelButton);
			} else {
				confirmGUI.setItem(i, whitePane);
			}
		}
	}

	public void openGUI(Player player, Boolean enchTransfer, Boolean repairReset) {

		CMIUser user = CMI.getInstance().getPlayerManager().getUser(player);
		Inventory endUpgradeGUI = Bukkit.createInventory(null, 54, ChatColor.RED + "End Upgrade Shop");
		ItemStack whitePane = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
		ItemMeta whiteMeta = whitePane.getItemMeta();
		whiteMeta.setDisplayName(" ");
		whitePane.setItemMeta(whiteMeta);

		int totalCost = upgradeCost(player, enchTransfer, repairReset);

		for (int i = 0; i < 54; i++) {
			if(i == 0) {
				NamespacedKey key = new NamespacedKey(plugin, "stop-click");
				whiteMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
				NamespacedKey key1 = new NamespacedKey(plugin, "gui-type");
				whiteMeta.getPersistentDataContainer().set(key1, PersistentDataType.STRING, "endupgrade");
				whitePane.setItemMeta(whiteMeta);
				endUpgradeGUI.setItem(i, whitePane);
			} else if (i == 13) {
				endUpgradeGUI.setItem(i, player.getInventory().getItemInMainHand());
			} else if (i == 20) {
				ItemStack transferEnch = new ItemStack(Material.ENCHANTED_BOOK);
				ItemMeta enchMeta = transferEnch.getItemMeta();
				ArrayList lore = new ArrayList();
				NamespacedKey enchKey = new NamespacedKey(plugin, "ench-state");
				lore.add(plugin.colourMessage("&7Cost: &a$100,000"));
				lore.add(plugin.colourMessage("&8-------"));
				if(enchTransfer) {
					enchMeta.getPersistentDataContainer().set(enchKey, PersistentDataType.INTEGER, 1);
					lore.add(plugin.colourMessage("&a&lEnabled"));
				} else {
					enchMeta.getPersistentDataContainer().set(enchKey, PersistentDataType.INTEGER, 0);
					lore.add(plugin.colourMessage("&c&lDisabled"));
				}
				enchMeta.setDisplayName(plugin.colourMessage("&e&lTransfer Enchants"));
				enchMeta.setLore(lore);
				transferEnch.setItemMeta(enchMeta);
				endUpgradeGUI.setItem(i, transferEnch);
			} else if (i == 24) {
				ItemStack repairCost = new ItemStack(Material.ENCHANTED_BOOK);
				ItemMeta repMeta = repairCost.getItemMeta();
				ArrayList lore = new ArrayList();
				NamespacedKey repKey = new NamespacedKey(plugin, "repair-state");
				lore.add(plugin.colourMessage("&7Cost: &a$350,000"));
				lore.add(plugin.colourMessage("&8-------"));
				if(repairReset) {
					repMeta.getPersistentDataContainer().set(repKey, PersistentDataType.INTEGER, 1);
					lore.add(plugin.colourMessage("&a&lEnabled"));
				} else {
					repMeta.getPersistentDataContainer().set(repKey, PersistentDataType.INTEGER, 0);
					lore.add(plugin.colourMessage("&c&lDisabled"));
				}
				repMeta.setDisplayName(plugin.colourMessage("&e&lReset Repair Cost"));
				repMeta.setLore(lore);
				repairCost.setItemMeta(repMeta);
				endUpgradeGUI.setItem(i, repairCost);
			} else if (i == 31) {
				if(user.getBalance() >= totalCost) {
					ItemStack confirmUpgrade = new ItemStack(Material.GREEN_CONCRETE);
					ArrayList lore = new ArrayList();
					ItemMeta confirmMeta = confirmUpgrade.getItemMeta();
					confirmMeta.setDisplayName(plugin.colourMessage("&3&lUpgrade Item"));
					lore.add(plugin.colourMessage("&7Total Cost: &a$" + plugin.formatNumber(totalCost)));
					confirmMeta.setLore(lore);
					confirmUpgrade.setItemMeta(confirmMeta);
					endUpgradeGUI.setItem(i, confirmUpgrade);
				} else {
					ItemStack confirmUpgrade = new ItemStack(Material.RED_CONCRETE);
					ArrayList lore = new ArrayList();
					ItemMeta confirmMeta = confirmUpgrade.getItemMeta();
					confirmMeta.setDisplayName(plugin.colourMessage("&3&lUpgrade Item"));
					lore.add(plugin.colourMessage("&7Total Cost: &a$" + plugin.formatNumber(totalCost)));
					lore.add(plugin.colourMessage("&8-------"));
					lore.add(plugin.colourMessage("&cYou can't afford this!"));
					confirmMeta.setLore(lore);
					confirmUpgrade.setItemMeta(confirmMeta);
					endUpgradeGUI.setItem(i, confirmUpgrade);
				}
			} else if(i == 45) {
				ItemStack info = new ItemStack(Material.BOOK);
				ItemMeta iMeta = info.getItemMeta();
				iMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Information");
				ArrayList lore = new ArrayList();
				lore.add(plugin.colourMessage(""));
				iMeta.setLore(lore);
				info.setItemMeta(iMeta);
				endUpgradeGUI.setItem(i, info);
			}  else if(i == 49) {
				ItemStack balance = new ItemStack(Material.NETHER_STAR);
				ItemMeta bMeta = balance.getItemMeta();
				bMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Your Balance");
				bMeta.setLore(Arrays.asList(ChatColor.GRAY + "" + PlaceholderAPI.setPlaceholders(player, "%cmi_user_balance_formatted%")));
				balance.setItemMeta(bMeta);
				endUpgradeGUI.setItem(i, balance);
			} else {
				endUpgradeGUI.setItem(i, whitePane);
			}
		}
		player.openInventory(endUpgradeGUI);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(player.hasPermission("skyprisoncore.command.endupgrade.quest-complete")) {
				List upItems = Arrays.asList(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
						Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_HOE, Material.DIAMOND_SHOVEL);
				ItemStack item = player.getInventory().getItemInMainHand();
				Material iMat = item.getType();
				if (upItems.contains(iMat)) {
					if(!player.hasPermission("skyprisoncore.command.endupgrade.first-time")) {
						openGUI(player, false, false);
					} else {
						confirmGUI(player, true, true);
					}
				} else {
					player.sendMessage(plugin.colourMessage("&cYou can't upgrade this item!"));
				}
			} else {
				player.sendMessage(plugin.colourMessage("&cYou need to finish the Blacksmith quest before you can use this shop!"));
			}
		}
		return true;
	}
}
