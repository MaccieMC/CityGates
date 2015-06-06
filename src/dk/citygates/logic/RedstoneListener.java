package dk.citygates.logic;

import dk.citygates.entitys.AbstractGate;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

/**
 *
 * @author Steven Hermans
 */
public class RedstoneListener implements Listener {

    private Map<Location, AbstractGate> map = new HashMap();

    public boolean addListener(Location location, AbstractGate gate) {
        if (map.get(location) != null) {
            return false;
        } else {
            map.put(location, gate);
            return true;
        }
    }

    public boolean removeListener(Location location, AbstractGate gate) {
        AbstractGate g = map.get(location);
        if (g == null) {
            return false;
        } else {
            if (gate.getName().equalsIgnoreCase(g.getName())) {
                map.remove(location);
                return true;
            } else {
                return false;
            }
        }
    }

    public void removeListener(AbstractGate gate) {
        for (Location loc : gate.getRedstoneLocations()) {
            map.remove(loc);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRedstoneListener(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        AbstractGate gate = map.get(block.getLocation());
        if (gate != null) {
            boolean otherPowered = false;
            for(Location loc : gate.getRedstoneLocations()){
                if(loc.getBlock().isBlockPowered() && !loc.equals(event.getBlock().getLocation())){
                    otherPowered = true;
                    break;
                }
            }
            if (event.getNewCurrent() > 0 && event.getOldCurrent() == 0) {
                if(!otherPowered){
                    gate.open();
                }
            } else if (event.getNewCurrent() == 0 && event.getOldCurrent() > 0) {
                if(!otherPowered){
                    gate.close();
                }
            }
        }
    }

    public void dispose() {
        map.clear();
    }

    public Map<Location, AbstractGate> getMap() {
        return map;
    }
}
