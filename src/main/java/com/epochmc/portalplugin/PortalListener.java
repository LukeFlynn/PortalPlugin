package com.epochmc.portalplugin;

import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import org.bukkit.DyeColor;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.Location;

import org.bukkit.material.Wool;

import java.lang.reflect.Array;
import java.util.Arrays;


public class PortalListener implements Listener {

    private final PortalPlugin plugin;
    private LegacyPortalListener legListener;

    public PortalListener(PortalPlugin instance) {
        plugin = instance;
    }

    public int getLimit() {
        int limit = plugin.config.getInt("border-limit", 2000);
        return limit;
    }

    public int getEncodeVal(int X, int Z, int Y, World w) {
        Block block = w.getBlockAt(X, Y, Z);
        Material mat = block.getType();
        if (mat == Material.WOOL) {
            Wool wool = (Wool) block.getState().getData();
            DyeColor color = wool.getColor();
            switch (color) {
                case ORANGE:
                    return 1;
                case MAGENTA:
                    return 2;
                case LIGHT_BLUE:
                    return 3;
                case YELLOW:
                    return 4;
                case LIME:
                    return 5;
                case PINK:
                    return 6;
                case GRAY:
                    return 7;
                case SILVER:
                    return 8;
                case CYAN:
                    return 9;
                case PURPLE:
                    return 10;
                case BLUE:
                    return 11;
                case BROWN:
                    return 12;
                case GREEN:
                    return 13;
                case RED:
                    return 14;
                case BLACK:
                    return 15;
                default:
                    return 0;
            }
        }
        return 0;
    }

    private int readColumn(int x, int z, int y, int depth, World w) {
        int[] codes = new int[depth];
        for (int i = 0; i < depth; i++) {
            codes[i] = getEncodeVal(x, z, y - i, w);
        }
        return convertBase(codes, depth);
    }

    private int convertBase(int[] code, int codenum) {
        int total = 0;
        int base = 16;
        for (int i = 0; i < codenum; i++) {
            total += code[i] * Math.pow(base, (codenum - 1) - i);
        }
        return (int) Math.ceil(total);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!event.getPlayer().getWorld().getName().equals(plugin.getLegacyModeWorlds().getName())) {
            plugin.lastposition.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.getPlayer().getWorld().getName().equals(plugin.getLegacyModeWorlds().getName())) {
            if (event.isCancelled()) {
                return;
            }
            Location to = event.getTo();
            Location lastPos = plugin.lastposition.get(event.getPlayer());

            if (lastPos != null) {
                //System.out.println("Player " + event.getPlayer().getName() + " moved to " + to);
                if (Math.abs(to.getBlockX() - lastPos.getBlockX()) < 1) {
                    if (Math.abs(to.getBlockZ() - lastPos.getBlockZ()) < 1) {
                        if (Math.abs(to.getBlockY() - lastPos.getBlockY()) < 1) {
                            return;
                        }
                    }
                }
            }
            World w = event.getPlayer().getWorld();
            plugin.lastposition.put(event.getPlayer(), to);

            int modX = (int) (to.getX() < 0 ? to.getX() - 1 : to.getX());
            int modZ = (int) (to.getZ() < 0 ? to.getZ() - 1 : to.getZ());

            Material block = w.getBlockAt(modX, (int) to.getY(), modZ).getType();
            Material anchor = Material.SPONGE;
            Material netherPortal = Material.PORTAL;

            if (block.equals(Material.STONE_PLATE) || (block.equals(Material.WOOD_PLATE)) || (block.equals(Material.ENDER_PORTAL)) || (block.equals(Material.PORTAL))) {

                int orientationlevel = (int) to.getY() - 1;

                //Change elevation for nether portal
                int readdirection = -1; //0 is for +x 1 is +z 2 is -x 3 is -z
                if (block.equals(netherPortal)) {
                    orientationlevel = (int) to.getY() - 2;
                }

                if (readdirection == -1) {
                    if (w.getBlockAt(modX - 1, orientationlevel, modZ).getType().equals(anchor)) {
                        readdirection = 0;
                    }
                    if (w.getBlockAt(modX + 1, orientationlevel, modZ).getType().equals(anchor)) {
                        readdirection = 2;
                    }
                    if (w.getBlockAt(modX, orientationlevel, modZ + 1).getType().equals(anchor)) {
                        readdirection = 3;
                    }
                    if (w.getBlockAt(modX, orientationlevel, modZ - 1).getType().equals(anchor)) {
                        readdirection = 1;
                    }
                }


                if (readdirection == -1) {
                    return;
                } else {
                    float rotation = 0;
                    int xcodenum = 0;
                    int goto1 = 0;
                    int goto2 = 0;
                    int gotoY = 126;
                    switch (readdirection) {
                        case -1:
                            return;
                        case 0:
                            goto1 = -readColumn(modX + 1, modZ, orientationlevel - 1, 4, w);
                            rotation = readColumn(modX - 1, modZ, orientationlevel - 3, 1, w) * 45;
                            xcodenum = 1;
                            if (w.getBlockAt(modX - 1, orientationlevel - 4, modZ).getType().equals(anchor)) {
                                goto2 = -readColumn(modX, modZ, orientationlevel - 1, 4, w);
                            } else {
                                goto2 = readColumn(modX, modZ, orientationlevel - 1, 4, w);
                            }

                            gotoY = 255 - readColumn(modX - 1, modZ, orientationlevel - 1, 2, w);
                            break;
                        case 1:
                            goto1 = -readColumn(modX, modZ + 1, orientationlevel - 1, 4, w);
                            rotation = readColumn(modX, modZ - 1, orientationlevel - 3, 1, w) * 45;
                            xcodenum = 2;
                            if (w.getBlockAt(modX, orientationlevel - 4, modZ - 1).getType().equals(anchor)) {
                                goto2 = -readColumn(modX, modZ, orientationlevel - 1, 4, w);
                            } else {
                                goto2 = readColumn(modX, modZ, orientationlevel - 1, 4, w);
                            }

                            gotoY = 255 - readColumn(modX, modZ - 1, orientationlevel - 1, 2, w);
                            break;
                        case 2:
                            goto1 = readColumn(modX - 1, modZ, orientationlevel - 1, 4, w);
                            rotation = readColumn(modX + 1, modZ, orientationlevel - 3, 1, w) * 45;
                            xcodenum = 1;
                            if (w.getBlockAt(modX + 1, orientationlevel - 4, modZ).getType().equals(anchor)) {
                                goto2 = readColumn(modX, modZ, orientationlevel - 1, 4, w);
                            } else {
                                goto2 = -readColumn(modX, modZ, orientationlevel - 1, 4, w);
                            }

                            gotoY = 255 - readColumn(modX + 1, modZ, orientationlevel - 1, 2, w);
                            break;
                        case 3:
                            goto1 = readColumn(modX, modZ - 1, orientationlevel - 1, 4, w);
                            rotation = readColumn(modX, modZ + 1, orientationlevel - 3, 1, w) * 45;
                            xcodenum = 2;
                            if (w.getBlockAt(modX, orientationlevel - 4, modZ + 1).getType().equals(anchor)) {
                                goto2 = readColumn(modX, modZ, orientationlevel - 1, 4, w);
                            } else {
                                goto2 = -readColumn(modX, modZ, orientationlevel - 1, 4, w);
                            }

                            gotoY = 255 - readColumn(modX, modZ + 1, orientationlevel - 1, 2, w);
                            break;
                    }
                    Location loc = new Location(w, 0, 0, 0);
                    if (xcodenum == 1) {
                        loc.setX(modX + goto1);
                        loc.setZ(modZ + goto2);
                    }
                    if (xcodenum == 2) {
                        loc.setX(modX + goto2);
                        loc.setZ(modZ + goto1);
                    }
                    if (gotoY <= 0) {
                        gotoY = 2;
                    }
                    loc.setY(gotoY);
                    if (rotation < 360) {
                        loc.setYaw(event.getPlayer().getLocation().getYaw() + rotation);
                    } else {
                        loc.setYaw(rotation);
                    }
                    loc.setPitch(event.getPlayer().getLocation().getPitch());

                    //if(loc.x < 0)
                    loc.setX(loc.getX() + 0.5f);
                    //else
                    //	loc.x -= 0.5f;
                    //if(loc.z < 0)
                    loc.setZ(loc.getZ() + 0.5f);
                    //else
                    //	loc.z += 0.5f;



                    int limit = getLimit();
                    Location center = null;
                    if (plugin.config.getBoolean("center-origin", false)) {
                        center = new Location(event.getPlayer().getWorld(), 0, 0, 0);
                    } else {
                        center = event.getPlayer().getWorld().getSpawnLocation();
                    }
                    if (Math.abs(loc.getX() - center.getX()) <= limit
                            && Math.abs(loc.getZ() - center.getZ()) <= limit) {
                        event.getPlayer().sendMessage(ChatColor.DARK_GRAY + "Teleporting to " + loc.getX() + " " + loc.getY() + " " + loc.getZ());
                        System.out.println("[" + plugin.pdf.getName() + "] Teleporting " + event.getPlayer().getName() +  " to " + loc.getX() + " " + loc.getY() + " " + loc.getZ());
                        event.getPlayer().teleport(loc, TeleportCause.PLUGIN);
                    } else {
                        event.getPlayer().sendMessage(ChatColor.RED + "Cannot Teleport you to " + loc.getX() + " "
                                + loc.getY() + " " + loc.getZ());
                    }
                }
            }
            return;
        }
        return;
    }
}