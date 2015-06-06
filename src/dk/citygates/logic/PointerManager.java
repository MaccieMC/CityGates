package dk.citygates.logic;

import dk.citygates.CityGates;
import dk.citygates.entitys.BlockHolder;
import dk.citygates.entitys.Gate;
import dk.citygates.entitys.Pointer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author Steven Hermans
 */
public class PointerManager {

    private Map<String, Pointer> pointers;
    private static final int SHOW_DELAY = 20;

    public PointerManager() {
        pointers = new HashMap();
    }

    /**
     * Set a new point 1 for a player
     *
     * @param username playername
     * @param point location
     */
    public void setPoint1(String username, Location point) {
        Pointer pointer = pointers.get(username);
        if (pointer == null) {
            pointer = new Pointer();
            pointers.put(username, pointer);
        }
        pointer.setP1(point);
    }

    /**
     * Set a new point 2 for a player
     *
     * @param username playername
     * @param point location
     */
    public void setPoint2(String username, Location point) {
        Pointer pointer = pointers.get(username);
        if (pointer == null) {
            pointer = new Pointer();
            pointers.put(username, pointer);
        }
        pointer.setP2(point);
    }

    /**
     * Get point 1 from a player
     *
     * @param username player name
     * @return location
     */
    public Location getPoint1(String username) {
        Pointer pointer = pointers.get(username);
        if (pointer == null) {
            return null;
        } else {
            return pointer.getP1();
        }
    }

    /**
     * Get point 2 from a player
     *
     * @param username player name
     * @return location
     */
    public Location getPoint2(String username) {
        Pointer pointer = pointers.get(username);
        if (pointer == null) {
            return null;
        } else {
            return pointer.getP2();
        }
    }

    /**
     * Check if the player has a vallid selection
     *
     * @param username player name
     * @return true if the player has a vallid selection, otherwise false
     */
    public boolean hasSelection(String username) {
        Pointer pointer = pointers.get(username);
        if (pointer == null) {
            return false;
        } else {
            return pointer.getP1() != null && pointer.getP2() != null;
        }
    }

    /**
     * Show selection in GLASS for two seconds
     *
     * @param username player name
     * @return true if the player has a vallid selection, otherwise false
     */
    public boolean showSelection(String username) {
        Location p1 = getPoint1(username);
        Location p2 = getPoint2(username);
        if (p1 == null || p2 == null) {
            return false;
        }
        if (!p1.getWorld().equals(p2.getWorld())) {
            return false;
        }
        ArrayList<Location> locs = Utils.getLocations(p1, p2);
        if(locs.isEmpty()){
            return false;
        }
        final ArrayList<BlockHolder> blocks = new ArrayList();
        Gate tempgate = new Gate();
        for (Location l : locs) {
            BlockHolder bh = new BlockHolder(l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getWorld().getName(), l.getBlock().getType().name(), l.getBlock().getData(), Material.GLASS.name(), 0);
            blocks.add(bh);
        }
        tempgate.initBlocks(blocks, blocks.get(0).getWorld());
        tempgate.open();
        tempgate.closeDelay(SHOW_DELAY);
        return true;
    }

    /**
     * Show point 1 in GLASS for two seconds
     *
     * @param username player name
     * @return true if the player has selected point 1
     */
    public boolean showPoint1(String username) {
        Location p = getPoint1(username);
        if (p == null) {
            return false;
        }
        final BlockHolder block = new BlockHolder(p.getBlockX(), p.getBlockY(), p.getBlockZ(), p.getWorld().getName(), p.getBlock().getType().name(), p.getBlock().getData(), Material.GLASS.name(), 0);
        block.open();
        CityGates.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(CityGates.getPlugin(), new Runnable() {
            public void run() {
                block.close();
            }
        }, SHOW_DELAY);
        return true;
    }

    /**
     * Show point 2 in GLASS for two seconds
     *
     * @param username player name
     * @return true if the player has selected point 2
     */
    public boolean showPoint2(String username) {
        Location p = getPoint2(username);
        if (p == null) {
            return false;
        }
        final BlockHolder block = new BlockHolder(p.getBlockX(), p.getBlockY(), p.getBlockZ(), p.getWorld().getName(), p.getBlock().getType().name(), p.getBlock().getData(), Material.GLASS.name(), 0);
        block.open();
        CityGates.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(CityGates.getPlugin(), new Runnable() {
            public void run() {
                block.close();
            }
        }, SHOW_DELAY);
        return true;
    }

}
