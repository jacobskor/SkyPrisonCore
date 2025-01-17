package net.skyprison.skyprisoncore.listeners.advancedregionmarket;

import net.alex9849.arm.events.UnsellRegionEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.maxgamer.quickshop.api.QuickShopAPI;
import org.maxgamer.quickshop.api.shop.Shop;

public class UnsellRegion implements Listener {
    @EventHandler
    public void onUnsellRegion(UnsellRegionEvent event) {
        if (event.getRegion().getRegionworld().getName().equalsIgnoreCase("world_skycity")) {
            World world = event.getRegion().getRegionworld();
            Location locMax = event.getRegion().getRegion().getMaxPoint().toLocation(world);
            Location locMin = event.getRegion().getRegion().getMinPoint().toLocation(world);
            Plugin quickshopPlugin = Bukkit.getPluginManager().getPlugin("QuickShop");
            if (quickshopPlugin != null && quickshopPlugin.isEnabled()) {
                QuickShopAPI quickshopApi = (QuickShopAPI) quickshopPlugin;
                for(Shop shop : quickshopApi.getShopManager().getPlayerAllShops(event.getRegion().getOwner())) {
                    Location shopLoc = shop.getLocation();
                    if ((shopLoc.getBlockX() >= locMax.getBlockX() && shopLoc.getBlockX() <= locMin.getBlockX()) || (shopLoc.getBlockX() <= locMax.getBlockX() && shopLoc.getBlockX() >= locMin.getBlockX())) {
                        if ((shopLoc.getBlockZ() >= locMax.getBlockZ() && shopLoc.getBlockZ() <= locMin.getBlockZ()) || (shopLoc.getBlockZ() <= locMax.getBlockZ() && shopLoc.getBlockZ() >= locMin.getBlockZ())) {
                            if ((shopLoc.getBlockY() >= locMax.getBlockY() && shopLoc.getBlockY() <= locMin.getBlockY()) || (shopLoc.getBlockY() <= locMax.getBlockY() && shopLoc.getBlockY() >= locMin.getBlockY())) {
                                shop.delete();
                            }
                        }
                    }
                }
            }
        }
    }
}
