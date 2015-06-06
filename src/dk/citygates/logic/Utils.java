package dk.citygates.logic;

import dk.citygates.entitys.AbstractGate;
import dk.citygates.entitys.Direction;
import dk.citygates.entitys.Group;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 *
 * @author Steven Hermans
 */
public class Utils {

    /**
     * List the location in order
     *
     * @param levels The levels of location to be listed
     * @param invert Invert the order the levels are listed
     * @return
     */
    public static ArrayList<ArrayList<Location>> listLevels(HashMap<Integer, ArrayList<Location>> levels, boolean invert) {
        ArrayList<ArrayList<Location>> list = new ArrayList();
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (Integer key : levels.keySet()) {
            if (key < min) {
                min = key;
            }
            if (key > max) {
                max = key;
            }
        }
        if (!invert) {
            for (int i = min; i <= max; i++) {
                ArrayList<Location> locs = levels.get(i);
                if (locs != null) {
                    list.add(locs);
                }
            }
        } else {
            for (int i = max; i >= min; i--) {
                ArrayList<Location> locs = levels.get(i);
                if (locs != null) {
                    list.add(locs);
                }
            }
        }

        return list;
    }

    /**
     * Sort all locations in differend levels (gates)
     *
     * @param locations The locations of it blocks
     * @param dir The direction the gate opens
     * @return List of different levels
     */
    public static HashMap<Integer, ArrayList<Location>> sortLevels(Iterable<Location> locations, Direction dir) {
        HashMap<Integer, ArrayList<Location>> levels = new HashMap();
        if (dir.equals(Direction.NORTH) || dir.equals(Direction.SOUTH)) {
            for (Location loc : locations) {
                int y = loc.getBlockZ();
                ArrayList<Location> level = levels.get(y);
                if (level == null) {
                    level = new ArrayList();
                    levels.put(y, level);
                }
                level.add(loc);
            }
        } else if (dir.equals(Direction.WEST) || dir.equals(Direction.EAST)) {
            for (Location loc : locations) {
                int x = loc.getBlockX();
                ArrayList<Location> level = levels.get(x);
                if (level == null) {
                    level = new ArrayList();
                    levels.put(x, level);
                }
                level.add(loc);
            }
        } else if (dir.equals(Direction.UP) || dir.equals(Direction.DOWN)) {
            for (Location loc : locations) {
                int z = loc.getBlockY();
                ArrayList<Location> level = levels.get(z);
                if (level == null) {
                    level = new ArrayList();
                    System.out.println("add level: " + z);
                    levels.put(z, level);
                }
                level.add(loc);
            }
        }else{
            throw new java.lang.IllegalStateException("Unknown direction: " + dir.name());
        }
        return levels;
    }

    /**
     * Logics to get all the blocks between two points
     *
     * @param p1 point 1
     * @param p2 point 2
     * @return all the blocks between the two points
     */
    public static ArrayList<Location> getLocations(Location p1, Location p2) {
        if (!p1.getWorld().equals(p2.getWorld())) {
            throw new java.lang.IllegalStateException("Worlds not the same");
        }

        ArrayList<Location> locations = new ArrayList();
        int x1 = p1.getBlockX();
        int y1 = p1.getBlockY();
        int z1 = p1.getBlockZ();
        int x2 = p2.getBlockX();
        int y2 = p2.getBlockY();
        int z2 = p2.getBlockZ();

        int fx, fy, fz;
        int sx, sy, sz;

        if (x1 > x2) {
            fx = x2;
            sx = x1;
        } else {
            fx = x1;
            sx = x2;
        }

        if (y1 > y2) {
            fy = y2;
            sy = y1;
        } else {
            fy = y1;
            sy = y2;
        }

        if (z1 > z2) {
            fz = z2;
            sz = z1;
        } else {
            fz = z1;
            sz = z2;
        }

        for (int x = fx; x <= sx; x++) {
            for (int y = fy; y <= sy; y++) {
                for (int z = fz; z <= sz; z++) {
                    locations.add(new Location(p1.getWorld(), x, y, z));
                }
            }
        }

        return locations;
    }
    
    public static boolean isRestricted(AbstractGate gate, GateManager manager, String type, CommandSender player){
        String name = gate.getName();
        if("open".equalsIgnoreCase(type)){
            if(!gate.perm.isPermOpen()|| 
                        (player.hasPermission("citygates.user.open.*") || 
                         player.hasPermission("citygates.user.open." + name.toLowerCase()) || 
                         player.hasPermission("citygates.user.interact.*") || 
                         player.hasPermission("citygates.user.interact."+name.toLowerCase()) ||
                         player.isOp())){
                return false;
            }
        }else if("close".equalsIgnoreCase(type)){
            if(!gate.perm.isPermClose() || 
                        (player.hasPermission("citygates.user.close.*") || 
                         player.hasPermission("citygates.user.close." + name.toLowerCase()) || 
                         player.hasPermission("citygates.user.interact.*") || 
                         player.hasPermission("citygates.user.interact."+name.toLowerCase()) ||
                         player.isOp())){
                return false;
            }
        }else if("button".equalsIgnoreCase(type)){
            if(!gate.perm.isPermButton() || 
                        (player.hasPermission("citygates.user.button.*") || 
                         player.hasPermission("citygates.user.button." + name.toLowerCase()) || 
                         player.hasPermission("citygates.user.interact.*") || 
                         player.hasPermission("citygates.user.interact."+name.toLowerCase()) ||
                         player.isOp())){
                return false;
            }
        }else if("kill".equalsIgnoreCase(type)){
            if(!gate.perm.isPermKill()|| 
                        (player.hasPermission("citygates.user.kill.*") || 
                         player.hasPermission("citygates.user.kill." + name.toLowerCase()) || 
                         player.hasPermission("citygates.user.interact.*") || 
                         player.hasPermission("citygates.user.interact."+name.toLowerCase()) ||
                         player.isOp())){
                return false;
            }
        }else if("break".equalsIgnoreCase(type)){
            if(!gate.isProtect() || 
                        (player.hasPermission("citygates.user.break.*") || 
                         player.hasPermission("citygates.user.break." + name.toLowerCase()) || 
                         player.hasPermission("citygates.user.interact.*") || 
                         player.hasPermission("citygates.user.interact."+name.toLowerCase()) ||
                         player.isOp())){
                return false;
            }
        }
        for(AbstractGate g : manager.getGates()){
            if(g instanceof Group){
                Group group = (Group) g;
                for(AbstractGate child : group.getGates()){
                    if(child.getName().equalsIgnoreCase(gate.getName())){
                        if(!isRestricted(group, manager, type, player)){
                            return false;
                        }
                        break;
                    }
                }
            }
        }
        return true;
    }

    public static boolean contains(Location loc, Location p1, Location p2) {
        int x1 = (p1.getBlockX() > p2.getBlockX() ? p2.getBlockX() : p1.getBlockX());
        int x2 = (p1.getBlockX() > p2.getBlockX() ? p1.getBlockX() : p2.getBlockX());
        int y1 = (p1.getBlockY() > p2.getBlockY() ? p2.getBlockY() : p1.getBlockY());
        int y2 = (p1.getBlockY() > p2.getBlockY() ? p1.getBlockY() : p2.getBlockY());
        int z1 = (p1.getBlockZ() > p2.getBlockZ() ? p2.getBlockZ() : p1.getBlockZ());
        int z2 = (p1.getBlockZ() > p2.getBlockZ() ? p1.getBlockZ() : p2.getBlockZ());
        
        if(x1 <= loc.getBlockX() && x2 >= loc.getBlockX() &&
                y1 <= loc.getBlockY() && y2 >= loc.getBlockY() &&
                z1 <= loc.getBlockZ() && z2 >= loc.getBlockZ()){
            return true;
        }
        return false;
    }
    
    public static void sendMessage(CommandSender cs, String msg){
        cs.sendMessage(ChatColor.AQUA+"[CityGates] " + msg);
    }
    
    public static void sendMessage(CommandSender cs, String[] msg){
        for(int i = 0; i < msg.length; i++){
            msg[i] = ChatColor.AQUA+"[CityGates] " + msg[i];
        }
        cs.sendMessage(msg);
    }
    
    public static void sendError(CommandSender cs, String msg){
        cs.sendMessage(ChatColor.RED+"[CityGates] " + msg);
    }
    
    public static void sendError(CommandSender cs, String[] msg){
        for(int i = 0; i < msg.length; i++){
            msg[i] = ChatColor.RED+"[CityGates] " + msg[i];
        }
        cs.sendMessage(msg);
    }
}
