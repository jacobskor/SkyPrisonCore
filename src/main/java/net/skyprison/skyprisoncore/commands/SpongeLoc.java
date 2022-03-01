package net.skyprison.skyprisoncore.commands;

import net.skyprison.skyprisoncore.SkyPrisonCore;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class SpongeLoc implements CommandExecutor {
	private SkyPrisonCore plugin;

	public SpongeLoc(SkyPrisonCore plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			File f = new File(plugin.getDataFolder() + File.separator + "spongelocations.yml");
			YamlConfiguration yamlf = YamlConfiguration.loadConfiguration(f);
			if (args.length < 1) {
				Set<String> setList = yamlf.getConfigurationSection("locations").getKeys(false);
				Double playerX = player.getLocation().getX();
				Double playerY = player.getLocation().getY();
				Double playerZ = player.getLocation().getZ();
				for (int i = 0; i < setList.size()+2; i++) {
					Double spongeX = yamlf.getDouble("locations." + i + ".x");
					Double spongeY = yamlf.getDouble("locations." + i + ".y");
					Double spongeZ = yamlf.getDouble("locations." + i + ".z");
					if(playerX.equals(spongeX) && playerY.equals(spongeY) && playerZ.equals(spongeZ)) {
						player.sendMessage(ChatColor.WHITE + "[" + ChatColor.YELLOW + "Sponge" + ChatColor.WHITE + "]" + ChatColor.RED + " There is already a sponge location here!");
						break;
					} else {
						if (!yamlf.contains("locations." + i)) {
							yamlf.set("locations." + i + ".world", player.getLocation().getWorld().getName());
							yamlf.set("locations." + i + ".x", player.getLocation().getX());
							yamlf.set("locations." + i + ".y", player.getLocation().getY());
							yamlf.set("locations." + i + ".z", player.getLocation().getZ());
							try {
								yamlf.save(f);
							} catch (IOException e) {
								e.printStackTrace();
							}
							break;
						}
					}
				}
				setList = yamlf.getConfigurationSection("locations").getKeys(false);
				String world;
				Double x;
				Double y;
				Double z;
				ArrayList<ArrayList> arr = new ArrayList<>();
				for (String spongeOrder : setList) {
					ArrayList arr2 = new ArrayList<String>();
					world = yamlf.getString("locations." + spongeOrder + ".world");
					x = yamlf.getDouble("locations." + spongeOrder + ".x");
					y = yamlf.getDouble("locations." + spongeOrder + ".y");
					z = yamlf.getDouble("locations." + spongeOrder + ".z");
					arr2.add(world);
					arr2.add(x);
					arr2.add(y);
					arr2.add(z);
					arr.add(arr2);
				}
				yamlf.set("locations", null);
				try {
					yamlf.save(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
				for(int i = 0; i < arr.size(); i++) {
					yamlf.set("locations." + i + ".world", arr.get(i).get(0));
					yamlf.set("locations." + i + ".x", arr.get(i).get(1));
					yamlf.set("locations." + i + ".y", arr.get(i).get(2));
					yamlf.set("locations." + i + ".z", arr.get(i).get(3));
					try {
						yamlf.save(f);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				player.sendMessage(ChatColor.WHITE + "[" + ChatColor.YELLOW + "Sponge" + ChatColor.WHITE + "]" + ChatColor.GREEN + " Sponge location set at your location");
			} else if (args[0].equalsIgnoreCase("list")) {
				for (String key : yamlf.getConfigurationSection("locations").getKeys(false)) {
					player.sendMessage(ChatColor.YELLOW + key + ChatColor.GREEN + ": X: " + yamlf.getInt("locations." + key + ".x") + " Y: " + yamlf.getInt("locations." + key + ".y") + " Z: " + yamlf.getInt("locations." + key + ".z"));
				}
			} else if (args[0].equalsIgnoreCase("delete")) {
				if (args.length < 2) {
					player.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.AQUA + "Sponge" + ChatColor.DARK_RED + "]" + ChatColor.RED + " Please specify a location id... ");
				} else if (!yamlf.getConfigurationSection("locations").contains(args[1])) {
					player.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.AQUA + "Sponge" + ChatColor.DARK_RED + "]" + ChatColor.RED + " Sponge location with id '" + args[1] + "' does not exist...");
				} else {
					yamlf.getConfigurationSection("locations").set(args[1], null);
					try {
						yamlf.save(f);
						Set<String> setList = yamlf.getConfigurationSection("locations").getKeys(false);
						String world = "";
						Double x = 0.0;
						Double y = 0.0;
						Double z = 0.0;
						ArrayList<ArrayList> arr = new ArrayList<>();
						for (String spongeOrder : setList) {
							ArrayList arr2 = new ArrayList<String>();
							world = yamlf.getString("locations." + spongeOrder + ".world");
							x = yamlf.getDouble("locations." + spongeOrder + ".x");
							y = yamlf.getDouble("locations." + spongeOrder + ".y");
							z = yamlf.getDouble("locations." + spongeOrder + ".z");
							arr2.add(world);
							arr2.add(x);
							arr2.add(y);
							arr2.add(z);
							arr.add(arr2);
						}
						yamlf.set("locations", null);
						try {
							yamlf.save(f);
						} catch (IOException e) {
							e.printStackTrace();
						}
						for(int i = 0; i < arr.size(); i++) {
							yamlf.set("locations." + i + ".world", arr.get(i).get(0));
							yamlf.set("locations." + i + ".x", arr.get(i).get(1));
							yamlf.set("locations." + i + ".y", arr.get(i).get(2));
							yamlf.set("locations." + i + ".z", arr.get(i).get(3));
							try {
								yamlf.save(f);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					player.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.AQUA + "Sponge" + ChatColor.DARK_RED + "]" + ChatColor.GREEN + " Sponge location with id '" + args[1] + "' successfully deleted...");
				}
			} else if (args[0].equalsIgnoreCase("tp")) {
				if (args.length < 2) {
					player.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.AQUA + "Sponge" + ChatColor.DARK_RED + "]" + ChatColor.RED + " Please specify a location id... ");
				} else if (!yamlf.getConfigurationSection("locations").contains(args[1])) {
					player.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.AQUA + "Sponge" + ChatColor.DARK_RED + "]" + ChatColor.RED + " Sponge location with id '" + args[1] + "' does not exist...");
				} else {
					World w = Bukkit.getServer().getWorld(yamlf.getString("locations." + args[1] + ".world"));
					Location spongeLoc = new Location(w, yamlf.getDouble("locations." + args[1] + ".x"), yamlf.getDouble("locations." + args[1] + ".y"), yamlf.getDouble("locations." + args[1] + ".z"));
					player.teleportAsync(spongeLoc);
				}
			} else if (args[0].equalsIgnoreCase("help")) {
				player.sendMessage("/spongeloc\n/spongeloc tp <id>\n/spongeloc list\n/spongeloc delete <id>");
			} else if (args[0].equalsIgnoreCase(("place"))) {
				Set setList = yamlf.getConfigurationSection("locations").getKeys(false);
				for(int i = 0; i < setList.size(); i++) {
					if (yamlf.contains("locations." + i)) {
						World w = Bukkit.getServer().getWorld(yamlf.getString("locations." + i + ".world"));
						Location spongeLoc = new Location(w, yamlf.getDouble("locations." + i + ".x"), yamlf.getDouble("locations." + i + ".y"), yamlf.getDouble("locations." + i + ".z"));
						if (spongeLoc.getBlock().getType() == Material.SPONGE) {
							spongeLoc.getBlock().setType(Material.AIR);
							break;
						}
					}
				}
				Random random = new Random();
				int rand = random.nextInt(setList.size());
				World w = Bukkit.getServer().getWorld(yamlf.getString("locations." + rand + ".world"));
				Location placeSponge = new Location(w, yamlf.getDouble("locations." + rand + ".x"), yamlf.getDouble("locations." + rand + ".y"), yamlf.getDouble("locations." + rand + ".z"));
				placeSponge = placeSponge.getBlock().getLocation();
				placeSponge.getBlock().setType(Material.SPONGE);
			} else {
				player.sendMessage("/spongeloc\n/spongeloc tp <id>\n/spongeloc list\n/spongeloc delete <id>");
			}
		} else {
			if (args[0].equalsIgnoreCase(("place"))) {
				File f = new File(plugin.getDataFolder() + File.separator + "spongelocations.yml");
				YamlConfiguration yamlf = YamlConfiguration.loadConfiguration(f);
				Set setList = yamlf.getConfigurationSection("locations").getKeys(false);
				for (int i = 0; i < setList.size(); i++) {
					if (yamlf.contains("locations." + i)) {
						World w = Bukkit.getServer().getWorld(yamlf.getString("locations." + i + ".world"));
						Location spongeLoc = new Location(w, yamlf.getDouble("locations." + i + ".x"), yamlf.getDouble("locations." + i + ".y"), yamlf.getDouble("locations." + i + ".z"));
						if (spongeLoc.getBlock().getType() == Material.SPONGE) {
							spongeLoc.getBlock().setType(Material.AIR);
							break;
						}
					}
				}
				Random random = new Random();
				int rand = random.nextInt(setList.size());
				World w = Bukkit.getServer().getWorld(yamlf.getString("locations." + rand + ".world"));
				Location placeSponge = new Location(w, yamlf.getDouble("locations." + rand + ".x"), yamlf.getDouble("locations." + rand + ".y"), yamlf.getDouble("locations." + rand + ".z"));
				placeSponge = placeSponge.getBlock().getLocation();
				placeSponge.getBlock().setType(Material.SPONGE);
			}
		}
		return true;
	}
}
