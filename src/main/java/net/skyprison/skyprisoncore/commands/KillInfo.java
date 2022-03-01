package net.skyprison.skyprisoncore.commands;

import net.skyprison.skyprisoncore.SkyPrisonCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class KillInfo implements CommandExecutor {
	private SkyPrisonCore plugin;

	public KillInfo(SkyPrisonCore plugin) {
		this.plugin = plugin;
	}


	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();
		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			File f = new File(plugin.getDataFolder() + File.separator + "recentkills.yml");
			FileConfiguration kills = YamlConfiguration.loadConfiguration(f);
			if(!kills.isConfigurationSection(player.getUniqueId().toString())) {
				kills.set(player.getUniqueId() + ".pvpdeaths", 0);
				kills.set(player.getUniqueId() + ".pvpkills", 0);
				kills.set(player.getUniqueId() + ".pvpkillstreak", 0);
				try {
					kills.save(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			int deaths = kills.getInt(player.getUniqueId() + ".pvpdeaths");
			int pKills = kills.getInt(player.getUniqueId() + ".pvpkills");
			int streak = kills.getInt(player.getUniqueId() + ".pvpkillstreak");
			double KSRatio;
			if(deaths == 0 && pKills == 0) {
				KSRatio = 0.0;
			} else if(deaths == 0) {
				KSRatio = round(pKills, 2);
			} else {
				KSRatio = round((double) pKills/deaths, 2);
			}
			player.sendMessage(ChatColor.RED + "--= PvP Stats =--" +
					ChatColor.GRAY + "\nPvP Kills: " + ChatColor.RED + pKills +
					ChatColor.GRAY + "\nPvP Deaths: " + ChatColor.RED + deaths +
					ChatColor.GRAY + "\nKill Streak: " + ChatColor.RED + streak +
					ChatColor.GRAY + "\nK/D Ratio: " + ChatColor.RED + KSRatio);
		}
		return true;
	}
}
