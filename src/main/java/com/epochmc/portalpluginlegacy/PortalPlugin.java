package com.epochmc.portalpluginlegacy;

import java.util.HashMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.configuration.file.FileConfiguration;

public class PortalPlugin extends JavaPlugin {
    public FileConfiguration config;
    private final PortalListener listener = new PortalListener(this);
    public final HashMap<Player, Location> lastposition = new HashMap<Player, Location>();
    public PluginDescriptionFile pdf;

    public void onDisable() {
        System.out.println("[" + pdf.getName() + "] disabled!");
    }

    public void loadConfiguration() {
        String limit = "border-limit";
        getConfig().addDefault(limit, "50000000");
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
        getServer().getLogger().info(pdf.getName() + " version " + pdf.getVersion() + " is enabled!");
    }
}
