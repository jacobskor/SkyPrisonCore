package net.skyprison.skyprisoncore.listeners.mcmmo;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import net.skyprison.skyprisoncore.SkyPrisonCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class McMMOLevelUp implements Listener {

    private final SkyPrisonCore plugin;

    public McMMOLevelUp(SkyPrisonCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMcMMOLevelUp(McMMOPlayerLevelUpEvent event) {
        McMMOPlayer mcPlayer = com.gmail.nossr50.util.player.UserManager.getPlayer(event.getPlayer());
        if(mcPlayer.getPowerLevel() == 250) {
            plugin.asConsole("lp user " + event.getPlayer().getName() + " permission set skyprisoncore.tag.44");
        } else if(mcPlayer.getPowerLevel() == 500) {
            plugin.asConsole("lp user " + event.getPlayer().getName() + " permission set skyprisoncore.tag.45");
        } else if(mcPlayer.getPowerLevel() == 750) {
            plugin.asConsole("lp user " + event.getPlayer().getName() + " permission set skyprisoncore.tag.46");
        } else if(mcPlayer.getPowerLevel() == 1000) {
            plugin.asConsole("lp user " + event.getPlayer().getName() + " permission set skyprisoncore.tag.47");
        } else if(mcPlayer.getPowerLevel() == 1500) {
            plugin.asConsole("lp user " + event.getPlayer().getName() + " permission set skyprisoncore.tag.48");
        } else if(mcPlayer.getPowerLevel() == 2000) {
            plugin.asConsole("lp user " + event.getPlayer().getName() + " permission set skyprisoncore.tag.49");
        }
        if(event.getSkillLevel() == 100) {
            plugin.asConsole("lp user " + event.getPlayer().getName() + " permission set deluxetags.tag." + event.getSkill().getName());
        } else if(event.getSkillLevel() == 200) {
            plugin.asConsole("lp user " + event.getPlayer().getName() + " permission set deluxetags.tag." + event.getSkill().getName() + "2");
        } else if(event.getSkillLevel() == 300) {
            plugin.asConsole("lp user " + event.getPlayer().getName() + " permission set deluxetags.tag." + event.getSkill().getName() + "3");
        }
    }
}
