package com.epochmc.portalplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.configuration.file.FileConfiguration;

public class PortalPlugin extends JavaPlugin {
    public FileConfiguration config;
    private final PortalListener listener = new PortalListener(this);
    private final LegacyPortalListener legListener = new LegacyPortalListener(this);
    public final HashMap<Player, Location> lastposition = new HashMap<Player, Location>();
    public PluginDescriptionFile pdf;

    public void onDisable() {
        System.out.println("[" + pdf.getName() + "] disabled!");
    }

    public World getLegacyModeWorlds() {
        List<String> worlds = config.getStringList("legacy-mode-worlds");
        StringBuilder sbworld = new StringBuilder();

        for (String world : worlds) {
            sbworld.append(world);
        }

        World world = getServer().getWorld(sbworld.toString());
        return world;
    }

    public void loadConfiguration() {
        String limit = "border-limit";
        String legacyMode = "legacy-mode-worlds";
        getConfig().addDefault(limit, 2000);
        getConfig().addDefault(legacyMode, "[]");
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public void onEnable() {
        loadConfiguration();
        pdf = this.getDescription();
        config = getConfig();
        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(listener, this);
        pm.registerEvents(legListener, this);
        getServer().getLogger().info(pdf.getName() + " version " + pdf.getVersion() + " is enabled! " + "Border Limit: " + listener.getLimit());
        if (getServer().getWorlds().contains(getLegacyModeWorlds())) {
            getServer().getLogger().info(pdf.getName() + " version " + pdf.getVersion() + " Legacy mode is enabled in worlds(s) " +  getLegacyModeWorlds().getName() + " Border Limit: " + legListener.getLimit());
        } else {
            getServer().getLogger().info(pdf.getName() + " version " + pdf.getVersion() + " Did not find any world(s) (" + getLegacyModeWorlds() + ") eligible for legacy mode!");
        }

    }
}
