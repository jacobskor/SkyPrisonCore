package net.skyprison.skyprisoncore.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.skyprison.skyprisoncore.SkyPrisonCore;
import net.skyprison.skyprisoncore.utils.DailyMissions;
import net.skyprison.skyprisoncore.utils.DatabaseHook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import se.file14.procosmetics.ProCosmetics;
import se.file14.procosmetics.api.ProCosmeticsProvider;
import se.file14.procosmetics.cosmetic.AbstractCosmeticType;
import se.file14.procosmetics.cosmetic.CosmeticCategory;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerJoin implements Listener {

    private final SkyPrisonCore plugin;
    private final DatabaseHook db;
    private DiscordApi discApi;
    private DailyMissions dailyMissions;

    public PlayerJoin(SkyPrisonCore plugin, DatabaseHook db, DiscordApi discApi, DailyMissions dailyMissions) {
        this.plugin = plugin;
        this.db = db;
        this.discApi = discApi;
        this.dailyMissions = dailyMissions;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Player player = event.getPlayer();

            EmbedBuilder embedJoin;

            Connection conn;
            PreparedStatement ps;
            ResultSet rs;
            if(!player.hasPlayedBefore()) {
                embedJoin = new EmbedBuilder()
                        .setAuthor(player.getName() + " joined the server for the first time!", "",  "https://minotar.net/helm/" + player.getName())
                        .setColor(Color.YELLOW);

                String sqls = "INSERT INTO users (user_id, current_name, first_join) VALUES (?, ?, ?)";
                List<Object> params = new ArrayList<Object>() {{
                    add(player.getUniqueId().toString());
                    add(player.getName());
                    add(player.getFirstPlayed());
                }};
                db.sqlUpdate(sqls, params);
            } else {
                embedJoin = new EmbedBuilder()
                        .setAuthor(player.getName() + " joined the server", "",  "https://minotar.net/helm/" + player.getName())
                        .setColor(Color.GREEN);
                boolean noData = false;
                try {
                    conn = db.getSQLConnection();
                    ps = conn.prepareStatement("SELECT * FROM users WHERE user_id = '" + player.getUniqueId() + "'");
                    rs = ps.executeQuery();
                    if(!rs.isBeforeFirst()) {
                        noData = true;
                    }
                    db.close(ps, rs, conn);
                } catch (SQLException ignored) {
                }

                if(noData) {
                    String sqls = "INSERT INTO users (user_id, current_name, first_join) VALUES (?, ?, ?)";
                    List<Object> params = new ArrayList<Object>() {{
                        add(player.getUniqueId().toString());
                        add(player.getName());
                        add(player.getFirstPlayed());
                    }};
                    db.sqlUpdate(sqls, params);
                }
            }

            if(discApi != null)
                discApi.getTextChannelById("788108242797854751").get().sendMessage(embedJoin);


            if(player.getName().equalsIgnoreCase("DrakePork") && dailyMissions.getPlayerMissions(player).isEmpty()) {
                dailyMissions.setPlayerMissions(player);
            }

            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

            com.sk89q.worldedit.util.Location locWE = BukkitAdapter.adapt(player.getLocation());
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            RegionQuery query = container.createQuery();
            if(!player.getGameMode().equals(GameMode.CREATIVE) && !player.getGameMode().equals(GameMode.SPECTATOR)) {
                player.setAllowFlight(query.testState(locWE, localPlayer, plugin.claimPlugin.FLY));
            }

            if(!plugin.userTags.containsKey(player.getUniqueId())) {
                int tag_id = 0;
                try {
                    conn = db.getSQLConnection();
                    ps = conn.prepareStatement("SELECT active_tag FROM users WHERE user_id = '" + player.getUniqueId() + "'");
                    rs = ps.executeQuery();
                    while(rs.next()) {
                        tag_id = rs.getInt(1);
                    }
                    db.close(ps, rs, conn);
                } catch (SQLException ignored) {
                }

                if(tag_id != 0) {
                    String tagsDisplay = "";
                    String tagsEffect = "";
                    try {
                        conn = db.getSQLConnection();
                        ps = conn.prepareStatement("SELECT tags_display, tags_effect FROM tags WHERE tags_id = '" + tag_id + "'");
                        rs = ps.executeQuery();
                        while(rs.next()) {
                            tagsDisplay = rs.getString(1);
                            tagsEffect = rs.getString(2);
                        }
                        db.close(ps, rs, conn);
                    } catch (SQLException ignored) {
                    }
                    plugin.userTags.put(player.getUniqueId(), tagsDisplay);
                    ProCosmetics api = ProCosmeticsProvider.get();
                    if(tagsEffect != null && !tagsEffect.isEmpty()) {
                        for(Object cosmeticObject : CosmeticCategory.PARTICLE_EFFECTS.getCosmeticTypes()) {
                            AbstractCosmeticType cosmetic = (AbstractCosmeticType) cosmeticObject;
                            String name = ChatColor.stripColor(cosmetic.getName());
                            if(name.equalsIgnoreCase(tagsEffect)) {
                                cosmetic.equip(api.getUserManager().getUser(player), true);
                            }
                        }
                    }
                }
            }


            String pUUID = event.getPlayer().getUniqueId().toString();

            plugin.blockBreaks.put(pUUID, 0);
            try {
                conn = db.getSQLConnection();
                ps = conn.prepareStatement("SELECT blocks_mined FROM users WHERE user_id = '" + pUUID + "'");
                rs = ps.executeQuery();
                while(rs.next()) {
                    plugin.blockBreaks.put(pUUID, rs.getInt(1));
                }
                db.close(ps, rs, conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            plugin.tokensData.put(pUUID, 0);
            try {
                conn = db.getSQLConnection();
                ps = conn.prepareStatement("SELECT tokens FROM users WHERE user_id = '" + pUUID + "'");
                rs = ps.executeQuery();
                while(rs.next()) {
                    plugin.tokensData.put(pUUID, rs.getInt(1));
                }
                db.close(ps, rs, conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if(player.getWorld().getName().equalsIgnoreCase("world_prison") || player.getWorld().getName().equalsIgnoreCase("world_event") || player.getWorld().getName().equalsIgnoreCase("world_war")) {
                player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(16);
            } else {
                player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getDefaultValue());
            }

        });
    }
}
