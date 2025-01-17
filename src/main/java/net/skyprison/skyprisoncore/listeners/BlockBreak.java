package net.skyprison.skyprisoncore.listeners;

import com.Zrips.CMI.CMI;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.coreprotect.CoreProtect;
import net.skyprison.skyprisoncore.SkyPrisonCore;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.Random;

public class BlockBreak implements Listener {
    private final SkyPrisonCore plugin;

    public BlockBreak(SkyPrisonCore plugin) {
        this.plugin = plugin;
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onblockBreak(BlockBreakEvent event) {
        Block b = event.getBlock();
        Location loc = b.getLocation();
        Player player = event.getPlayer();
        if(!event.isCancelled()) {
            if(loc.getWorld().getName().equalsIgnoreCase("world_event")) {
                if(b.getType().equals(Material.SNOW_BLOCK))
                    event.setDropItems(false);
                if(!player.isOp()) {
                    if(b.getType().equals(Material.TNT)) {
                        event.setCancelled(true);
                    }
                }
            } else if(loc.getWorld().getName().equalsIgnoreCase("world_prison")) {
                if (b.getType().equals(Material.SNOW_BLOCK)) {
                    event.setDropItems(false);
                    Location cob = loc.add(0.5D, 0.0D, 0.5D);
                    ItemStack snowblock = new ItemStack(Material.SNOW_BLOCK, 1);
                    loc.getWorld().dropItem(cob, snowblock);
                }
            }

            if (!CoreProtect.getInstance().getAPI().hasPlaced(player.getName(), event.getBlock(), 300, 0) && !loc.getWorld().getName().equalsIgnoreCase("world_event")) {
                String pUUID = player.getUniqueId().toString();
                int brokeBlocks = plugin.blockBreaks.get(pUUID);
                if (brokeBlocks >= 2000) {
                    plugin.blockBreaks.put(pUUID, 0);
                    Random rand = new Random();
                    int tReward = rand.nextInt(25 - 10 + 1) + 10;
                    plugin.tokens.addTokens(CMI.getInstance().getPlayerManager().getUser(player), tReward);
                    player.sendMessage(ChatColor.GRAY + "You've mined 2,000 blocks and have received some tokens!");
                } else {
                    plugin.blockBreaks.put(pUUID, brokeBlocks + 1);
                }
            }
        } else {
            if(loc.getWorld().getName().equalsIgnoreCase("world_prison")) {
                if (b.getType().equals(Material.SUGAR_CANE)) {
                    final RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    final RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(player.getWorld()));
                    final ApplicableRegionSet regionList = regionManager.getApplicableRegions(BlockVector3.at(b.getLocation().getX(),
                            b.getLocation().getY(), b.getLocation().getZ()));
                    for(ProtectedRegion region : regionList.getRegions()) {
                        if(region.getId().equalsIgnoreCase("snow-island1")) {
                            if(loc.subtract(0,1,0).getBlock().getType().equals(Material.SUGAR_CANE))
                                event.setCancelled(false);
                            break;
                        }
                    }
                } else if (b.getType().equals(Material.CACTUS)) {
                    final RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    final RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(player.getWorld()));
                    final ApplicableRegionSet regionList = regionManager.getApplicableRegions(BlockVector3.at(b.getLocation().getX(),
                            b.getLocation().getY(), b.getLocation().getZ()));
                    for(ProtectedRegion region : regionList.getRegions()) {
                        if(region.getId().equalsIgnoreCase("desert-nofly")) {
                            if(loc.subtract(0,1,0).getBlock().getType().equals(Material.CACTUS))
                                event.setCancelled(false);
                            break;
                        }
                    }
                } else if (b.getType().equals(Material.BAMBOO)) {
                    final RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    final RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(player.getWorld()));
                    final ApplicableRegionSet regionList = regionManager.getApplicableRegions(BlockVector3.at(b.getLocation().getX(),
                            b.getLocation().getY(), b.getLocation().getZ()));
                    for(ProtectedRegion region : regionList.getRegions()) {
                        if(region.getId().equalsIgnoreCase("desert-nofly")) {
                            if(loc.subtract(0,1,0).getBlock().getType().equals(Material.BAMBOO))
                                event.setCancelled(false);
                            break;
                        }
                    }
                } else if (b.getType().equals(Material.BIRCH_LOG)) {
                    final RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    final RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(player.getWorld()));
                    final ApplicableRegionSet regionList = regionManager.getApplicableRegions(BlockVector3.at(b.getLocation().getX(),
                            b.getLocation().getY(), b.getLocation().getZ()));
                    for(ProtectedRegion region : regionList.getRegions()) {
                        if(region.getId().equalsIgnoreCase("grass-nofly")) {
                            ArrayList<Material> axes = new ArrayList<>();
                            axes.add(Material.DIAMOND_AXE);
                            axes.add(Material.GOLDEN_AXE);
                            axes.add(Material.IRON_AXE);
                            axes.add(Material.STONE_AXE);
                            axes.add(Material.WOODEN_AXE);
                            axes.add(Material.NETHERITE_AXE);

                            event.setCancelled(false);
                            if (axes.contains(player.getInventory().getItemInMainHand().getType())) {
                                if (!player.isSneaking()) {
                                    boolean birchDown = true;
                                    int birchDrops = 0;
                                    Location birchLoc;
                                    Location saplingLoc;
                                    int i = 0;
                                    while (birchDown) {
                                        birchLoc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY() - i, loc.getBlockZ());
                                        if (birchLoc.getBlock().getType() == Material.BIRCH_LOG) {
                                            birchLoc.getBlock().breakNaturally();
                                            birchDrops++;
                                            i++;
                                        } else {
                                            saplingLoc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY() - i + 1, loc.getBlockZ());
                                            Location finalSaplingLoc = saplingLoc;
                                            if (birchLoc.getBlock().getType() == Material.GRASS_BLOCK || birchLoc.getBlock().getType() == Material.DIRT) {
                                                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> finalSaplingLoc.getBlock().setType(Material.BIRCH_SAPLING), 2L);
                                            }
                                            birchDown = false;
                                        }
                                    }
                                    boolean birchUp = true;
                                    int x = 1;
                                    while (birchUp) {
                                        birchLoc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY() + x, loc.getBlockZ());
                                        if (birchLoc.getBlock().getType() == Material.BIRCH_LOG) {
                                            birchLoc.getBlock().breakNaturally();
                                            birchDrops++;
                                            x++;
                                        } else {
                                            birchUp = false;
                                        }
                                    }

                                    ItemStack item = player.getInventory().getItemInMainHand();
                                    Damageable im = (Damageable) item.getItemMeta();
                                    Material axe = item.getType();
                                    int dmg = im.getDamage();
                                    if (item.containsEnchantment(Enchantment.DURABILITY)) {
                                        int enchantLevel = item.getEnchantmentLevel(Enchantment.DURABILITY);
                                        if (birchDrops / enchantLevel + dmg > axe.getMaxDurability()) {
                                            player.getInventory().remove(item);
                                        } else {
                                            im.setDamage(birchDrops / enchantLevel + dmg);
                                            item.setItemMeta(im);
                                        }
                                    } else {
                                        if (birchDrops + dmg > axe.getMaxDurability()) {
                                            player.getInventory().remove(item);
                                        } else {
                                            im.setDamage(birchDrops + dmg);
                                            item.setItemMeta(im);
                                        }
                                    }
                                } else {
                                    Location newLoc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());
                                    if (newLoc.getBlock().getType() == Material.GRASS_BLOCK || newLoc.getBlock().getType() == Material.DIRT) {
                                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> loc.getBlock().setType(Material.BIRCH_SAPLING), 2L);
                                    }
                                }
                            } else {
                                Location newLoc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());
                                if (newLoc.getBlock().getType() == Material.GRASS_BLOCK || newLoc.getBlock().getType() == Material.DIRT) {
                                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> loc.getBlock().setType(Material.BIRCH_SAPLING), 2L);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
