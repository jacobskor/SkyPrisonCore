package net.skyprison.skyprisoncore.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoryOpen implements Listener {
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType().equals(InventoryType.MERCHANT)) {
            Player player = (Player) event.getPlayer();
            player.sendMessage(ChatColor.RED + "Villager trading has been disabled");
            if(!player.isOp()) {
                event.setCancelled(true);
            }
        } else if(event.getInventory().getType().equals(InventoryType.SMITHING)) {
            event.setCancelled(true);
        }
    }
}
