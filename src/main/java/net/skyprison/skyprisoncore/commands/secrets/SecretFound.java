package net.skyprison.skyprisoncore.commands.secrets;

import net.skyprison.skyprisoncore.SkyPrisonCore;
import com.google.inject.Inject;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class SecretFound implements CommandExecutor {
	private final SkyPrisonCore plugin;

	@Inject
	public SecretFound(SkyPrisonCore plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		File f = new File(plugin.getDataFolder() + File.separator + "secrets.yml");
		File secretsDataFile = new File(plugin.getDataFolder() + File.separator
				+ "secretsdata.yml");
		YamlConfiguration pData = YamlConfiguration.loadConfiguration(secretsDataFile);
		YamlConfiguration yamlf = YamlConfiguration.loadConfiguration(f);
		Set<String> secrets = yamlf.getKeys(true);
		String secretId;
		String guiType = "";

		for(String secretKey : secrets) {
			if(secretKey.endsWith(".id")) {
				String[] getId = secretKey.split("[.]");
				secretId = getId[0] + "." + getId[1] + "." + getId[2];
				if(Objects.requireNonNull(yamlf.getString(secretId + ".id")).equalsIgnoreCase(args[0])) {
					guiType =  getId[1];
					int amountFound = pData.getInt(player.getUniqueId() + ".secrets-found." + guiType + "." + args[0] + ".times-found");
					pData.set(player.getUniqueId() + ".secrets-found." + guiType + "." + args[0] + ".times-found", amountFound + 1);
					try {
						pData.save(secretsDataFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
		secrets = Objects.requireNonNull(yamlf.getConfigurationSection("inventory." + guiType)).getKeys(false);

		int secretsFound = 0;
		int totalSecrets = 0;

		for(String secretKey : secrets) {
			if(yamlf.isSet("inventory." + guiType + "." + secretKey + ".id")) {
				totalSecrets += 1;
			}
		}
		if(pData.isSet(player.getUniqueId() + ".secrets-found." + guiType)) {
			secretsFound = Objects.requireNonNull(pData.getConfigurationSection(player.getUniqueId() + ".secrets-found." + guiType)).getKeys(false).size();
		}
		if (secretsFound == totalSecrets) {
			if(!pData.isSet(player.getUniqueId() + ".rewards." + "first-time-" + guiType)) {
				String name = guiType.substring(0, 1).toUpperCase() + guiType.substring(1);
				player.sendMessage("You have found all secrets in this category! Check out Rewards to collect your reward!");
				pData.set(player.getUniqueId() + ".rewards." + "first-time-" + guiType + ".collected", false);
				pData.set(player.getUniqueId() + ".rewards." + "first-time-" + guiType + ".name", name + " First Time Reward");
				pData.set(player.getUniqueId() + ".rewards." + "first-time-" + guiType + ".lore", Arrays.asList("--", "Reward: 5 Points"));
				try {
					pData.save(secretsDataFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return true;
	}
}

