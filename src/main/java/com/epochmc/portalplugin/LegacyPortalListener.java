package com.epochmc.portalplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.material.Wool;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;

public class LegacyPortalListener implements Listener {
    private final PortalPlugin plugin;

    public enum Mode {Legacy, Wool}

    public LegacyPortalListener(PortalPlugin instance) {
        plugin = instance;
    }

    public int getEncodeVal(Mode m, int X, int Z, int Y, World w) {
        Material mat = w.getBlockAt(X, Y, Z).getType();
        if (m == Mode.Legacy) {
            switch (mat) {
                case DIRT:
                    return 0;
                case GRASS:
                    return 0;
                case WOOD:
                    return 1;
                case LOG:
                    return 2;
                case COBBLESTONE:
                    return 3;
                case WORKBENCH:
                    return 4;
                case FURNACE:
                    return 5;
                case CHEST:
                    return 6;
                case BOOKSHELF:
                    return 6;
                case GLASS:
                    return 7;
                case WOOL:
                    return 8;
                default:
                    return 0;
            }

        } else if (m == Mode.Wool) {
            Block block = w.getBlockAt(X, Y, Z);
            if (block.getType() == mat.WOOL) {
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
        }
        return 0;
    }
    private int find2dir(Mode m,int x, int z , int y, World w) {
        Material anchor;
        anchor = Material.JUKEBOX;
        if(m == Mode.Wool)
            anchor = Material.SPONGE;
        int dir = -1; //0 is for +x 1 is +z 2 is -x 3 is -z
        if(w.getBlockAt(x-1, y, z).getType().equals(anchor) || w.getBlockAt(x-2, y, z).getType().equals(anchor))
            dir = 0;
        if(w.getBlockAt(x+1, y, z).getType().equals(anchor) || w.getBlockAt(x+2, y, z).getType().equals(anchor))
            dir = 2;
        if(w.getBlockAt(x, y, z+1).getType().equals(anchor) || w.getBlockAt(x, y, z+2).getType().equals(anchor))
            dir = 3;
        if(w.getBlockAt(x, y, z-1).getType().equals(anchor) || w.getBlockAt(x, y, z-2).getType().equals(anchor))
            dir = 1;
        return dir;

    }
    private int convertBase(Mode m, int[] code,int codenum) {
        int total = 0;
        int base = 8;
        if(m == Mode.Wool)
            base = 16;
        for(int i = 0; i < codenum; i++) {
            total += code[i]*Math.pow(base, (codenum-1)-i);
        }
        return (int) Math.ceil(total);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (event.getPlayer().getWorld().getName().equals(plugin.getLegacyModeWorlds().getName())) {
            plugin.lastposition.remove(event.getPlayer());

        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        if (event.getPlayer().getWorld().getName().equals(plugin.getLegacyModeWorlds().getName())) {
            if(event.isCancelled())
                return;
            Location to = event.getTo();
            Location lastPos = plugin.lastposition.get(event.getPlayer());

            if(lastPos!=null) {
                //System.out.println("Player " + event.getPlayer().getName() + " moved to " + to);
                if(Math.abs(to.getBlockX()-lastPos.getBlockX()) < 1)
                    if(Math.abs(to.getBlockZ()-lastPos.getBlockZ()) < 1)
                        if(Math.abs(to.getBlockY()-lastPos.getBlockY()) < 1)
                            return;
            }
            World w = event.getPlayer().getWorld();
            plugin.lastposition.put(event.getPlayer(), to);
            //System.out.println("Player " + event.getPlayer().getName() + " moved a block");

            int modX = (int)(to.getX() < 0 ? to.getX()-1 : to.getX());
            int modZ = (int)(to.getZ() < 0 ? to.getZ()-1 : to.getZ());

            if(w.getBlockAt(modX, (int)to.getY(), modZ).getType().equals(Material.STONE_PLATE) || w.getBlockAt(modX, (int)to.getY(), modZ).getType().equals(Material.PORTAL))
            {

                //System.out.println("Player pushed a button");
                int orientationlevel = (int) to.getY()-1;
                //int oX = modX;
                //int oZ = modZ;
                int scode1 = 1;
                int scode2 = 1;
                //Change elevation for nether portal
                int readdirection = -1; //0 is for +x 1 is +z 2 is -x 3 is -z
                if(w.getBlockAt(modX, (int)to.getY(), modZ).getType().equals(Material.PORTAL))
                {
                    orientationlevel = (int) to.getY()-2;
                }

                if(w.getBlockAt(modX-1, orientationlevel, modZ).getType().equals(Material.JUKEBOX))
                    readdirection = 0;
                if(w.getBlockAt(modX+1, orientationlevel, modZ).getType().equals(Material.JUKEBOX))
                    readdirection = 2;
                if(w.getBlockAt(modX, orientationlevel, modZ+1).getType().equals(Material.JUKEBOX))
                    readdirection = 3;
                if(w.getBlockAt(modX, orientationlevel, modZ-1).getType().equals(Material.JUKEBOX))
                    readdirection = 1;
                Mode mode = Mode.Legacy;
                if(readdirection != -1)
                    mode = Mode.Legacy;

                if(readdirection == -1)
                {
                    if(w.getBlockAt(modX-1, orientationlevel, modZ).getType().equals(Material.SPONGE))
                        readdirection = 0;
                    if(w.getBlockAt(modX+1, orientationlevel, modZ).getType().equals(Material.SPONGE))
                        readdirection = 2;
                    if(w.getBlockAt(modX, orientationlevel, modZ+1).getType().equals(Material.SPONGE))
                        readdirection = 3;
                    if(w.getBlockAt(modX, orientationlevel, modZ-1).getType().equals(Material.SPONGE))
                        readdirection = 1;
                    mode = Mode.Wool;
                }


                if(readdirection == -1)
                    return;
                else
                {
                    float rotation = 0;
                    int options = 0;
                    boolean yset =false;
                    int[] code1 = new int[5];
                    int[] code2 = new int[5];
                    int[] codeY = new int[5];
                    int xcodenum=0;
                    switch (readdirection){
                        case -1:
                            return;
                        case 0:
                            code1[0] = getEncodeVal(mode,modX+1,modZ,orientationlevel,w);
                            code1[1] = getEncodeVal(mode,modX+1,modZ,orientationlevel-1,w);
                            code1[2] = getEncodeVal(mode,modX+1,modZ,orientationlevel-2,w);
                            code1[3] = getEncodeVal(mode,modX+1,modZ,orientationlevel-3,w);

                            rotation = getEncodeVal(mode,modX+1,modZ,orientationlevel-4,w)*45;
                            scode1 = -1;
                            xcodenum = 1;
                            code2[0] = getEncodeVal(mode,modX,modZ,orientationlevel-1,w);
                            code2[1] = getEncodeVal(mode,modX,modZ,orientationlevel-2,w);
                            code2[2] = getEncodeVal(mode,modX,modZ,orientationlevel-3,w);
                            code2[3] = getEncodeVal(mode,modX,modZ,orientationlevel-4,w);
                            if(find2dir(mode,modX, modZ, orientationlevel-3,w) != -1)
                                scode2 = -1;
                            else
                                scode2 = 1;

                            if(!w.getBlockAt(modX-1, orientationlevel-1, modZ).equals(Material.AIR))
                            {
                                codeY[0] = getEncodeVal(mode,modX-1,modZ,orientationlevel-1,w);
                                codeY[1] = getEncodeVal(mode,modX-1,modZ,orientationlevel-2,w);
                                codeY[2] = getEncodeVal(mode,modX-1,modZ,orientationlevel-3,w);
                                options = getEncodeVal(mode,modX-1,modZ,orientationlevel-4,w);
                                yset =true;
                            }
                            break;
                        case 1:
                            code1[0] = getEncodeVal(mode,modX,modZ+1,orientationlevel,w);
                            code1[1] = getEncodeVal(mode,modX,modZ+1,orientationlevel-1,w);
                            code1[2] = getEncodeVal(mode,modX,modZ+1,orientationlevel-2,w);
                            code1[3] = getEncodeVal(mode,modX,modZ+1,orientationlevel-3,w);

                            rotation = getEncodeVal(mode,modX,modZ+1,orientationlevel-4,w)*45;
                            scode1 = -1;
                            xcodenum = 2;
                            code2[0] = getEncodeVal(mode,modX,modZ,orientationlevel-1,w);
                            code2[1] = getEncodeVal(mode,modX,modZ,orientationlevel-2,w);
                            code2[2] = getEncodeVal(mode,modX,modZ,orientationlevel-3,w);
                            code2[3] = getEncodeVal(mode,modX,modZ,orientationlevel-4,w);
                            if(find2dir(mode,modX, modZ, orientationlevel-3,w) != -1)
                                scode2 = -1;
                            else
                                scode2 = 1;

                            if(!w.getBlockAt(modX, orientationlevel-1, modZ-1).equals(Material.AIR))
                            {
                                codeY[0] = getEncodeVal(mode,modX,modZ-1,orientationlevel-1,w);
                                codeY[1] = getEncodeVal(mode,modX,modZ-1,orientationlevel-2,w);
                                codeY[2] = getEncodeVal(mode,modX,modZ-1,orientationlevel-3,w);
                                options = getEncodeVal(mode,modX,modZ-1,orientationlevel-4,w);
                                yset =true;
                            }
                            break;
                        case 2:
                            code1[0] = getEncodeVal(mode,modX-1,modZ,orientationlevel,w);
                            code1[1] = getEncodeVal(mode,modX-1,modZ,orientationlevel-1,w);
                            code1[2] = getEncodeVal(mode,modX-1,modZ,orientationlevel-2,w);
                            code1[3] = getEncodeVal(mode,modX-1,modZ,orientationlevel-3,w);

                            rotation = getEncodeVal(mode,modX-1,modZ,orientationlevel-4,w)*45;
                            scode1 = 1;
                            xcodenum = 1;
                            code2[0] = getEncodeVal(mode,modX,modZ,orientationlevel-1,w);
                            code2[1] = getEncodeVal(mode,modX,modZ,orientationlevel-2,w);
                            code2[2] = getEncodeVal(mode,modX,modZ,orientationlevel-3,w);
                            code2[3] = getEncodeVal(mode,modX,modZ,orientationlevel-4,w);
                            if(find2dir(mode,modX, modZ, orientationlevel-3,w) != -1)
                                scode2 = 1;
                            else
                                scode2 = -1;

                            if(!w.getBlockAt(modX+1, orientationlevel-1, modZ).getType().equals(Material.AIR));
                        {
                            codeY[0] = getEncodeVal(mode,modX+1,modZ,orientationlevel-1,w);
                            codeY[1] = getEncodeVal(mode,modX+1,modZ,orientationlevel-2,w);
                            codeY[2] = getEncodeVal(mode,modX+1,modZ,orientationlevel-3,w);
                            options = getEncodeVal(mode,modX+1,modZ,orientationlevel-4,w);
                            yset =true;
                        }
                        break;
                        case 3:
                            code1[0] = getEncodeVal(mode,modX,modZ-1,orientationlevel,w);
                            code1[1] = getEncodeVal(mode,modX,modZ-1,orientationlevel-1,w);
                            code1[2] = getEncodeVal(mode,modX,modZ-1,orientationlevel-2,w);
                            code1[3] = getEncodeVal(mode,modX,modZ-1,orientationlevel-3,w);


                            rotation = getEncodeVal(mode,modX,modZ-1,orientationlevel-4,w)*45;
                            scode1 = 1;
                            xcodenum = 2;
                            code2[0] = getEncodeVal(mode,modX,modZ,orientationlevel-1,w);
                            code2[1] = getEncodeVal(mode,modX,modZ,orientationlevel-2,w);
                            code2[2] = getEncodeVal(mode,modX,modZ,orientationlevel-3,w);
                            code2[3] = getEncodeVal(mode,modX,modZ,orientationlevel-4,w);
                            if(find2dir(mode,modX, modZ, orientationlevel-3,w) != -1)
                                scode2 = 1;
                            else
                                scode2 = -1;

                            if(!w.getBlockAt(modX, orientationlevel-1, modZ+1).equals(Material.AIR))
                            {
                                codeY[0] = getEncodeVal(mode,modX,modZ+1,orientationlevel-1,w);
                                codeY[1] = getEncodeVal(mode,modX,modZ+1,orientationlevel-2,w);
                                codeY[2] = getEncodeVal(mode,modX,modZ+1,orientationlevel-3,w);
                                options = getEncodeVal(mode,modX,modZ+1,orientationlevel-4,w);
                                yset = true;
                            }
                            break;
                    }
                    int goto1 = convertBase(mode,code1,4)*scode1;
                    int goto2 = convertBase(mode,code2,4)*scode2;
                    Location loc = new Location(w, 0,0,0);
                    if(xcodenum == 1)
                    {
                        loc.setX(modX+goto1);
                        //loc.x = oX+goto1;
                        loc.setZ(modZ+goto2);
                        //loc.z = oZ+goto2;
                    }
                    if(xcodenum == 2)
                    {
                        loc.setX(modX+goto2);
                        //loc.x = oX+goto2;
                        loc.setZ(modZ+goto1);
                        //loc.z = oZ+goto1;
                    }
                    int gotoY = 126;
                    if(yset)
                        gotoY = 164-convertBase(mode,codeY,3);
                    if(gotoY <= 0)
                        gotoY = 2;
                    loc.setY(gotoY);
                    if(options == 0 || options == 2)
                        loc.setYaw(event.getPlayer().getLocation().getYaw() + rotation);
                    else
                        loc.setYaw(rotation);
                    loc.setPitch(event.getPlayer().getLocation().getPitch());

                    //if(loc.x < 0)
                    loc.setX(loc.getX() +0.5f);
                    //else
                    //	loc.x -= 0.5f;
                    //if(loc.z < 0)
                    loc.setZ(loc.getZ() +0.5f);
                    //else
                    //	loc.z += 0.5f;

                    int limit = plugin.getLimit();
                    if(Math.abs(loc.getX()-event.getPlayer().getWorld().getSpawnLocation().getX())<=limit
                            && Math.abs(loc.getZ()-event.getPlayer().getWorld().getSpawnLocation().getZ())<=limit)
                    {
                        event.getPlayer().sendMessage(ChatColor.DARK_GRAY+"Teleporting to "+loc.getX()+" "+loc.getY()+" "+loc.getZ());

                        //event.getPlayer().teleportTo(loc);
                        if(!event.getPlayer().teleport(loc)) {
                            event.getPlayer().sendMessage(ChatColor.RED+"Something went terribly wrong with the teleport! It didn't work!");
                            return;
                        }
                        event.setTo(loc);
                    } else {
                        event.getPlayer().sendMessage(ChatColor.RED+"Cannot Teleport you to "+loc.getX()+" "+
                                loc.getY()+" "+loc.getZ());
                    }

                }
            }

            return;
        }
    }
}