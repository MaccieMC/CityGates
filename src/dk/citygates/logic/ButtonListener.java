package dk.citygates.logic;

import dk.citygates.CityGates;
import dk.citygates.entitys.AbstractGate;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Steven Hermans
 */
public class ButtonListener implements Listener {

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
            if(gate.getName().equalsIgnoreCase(g.getName())){
                map.remove(location);
                return true;
            }else{
                return false;
            }
        }
    }

    public void removeListener(AbstractGate gate) {
        for(Location loc : gate.getButtonLocations()){
            map.remove(loc);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onButtonClicked(PlayerInteractEvent event) {
        Player cs = event.getPlayer();
        if (!cs.hasPermission("citygates.user.button") && !cs.isOp()) {
            return;
        }
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && (event.getClickedBlock().getType().equals(Material.STONE_BUTTON) || event.getClickedBlock().getType().equals(Material.WOOD_BUTTON))) {
            AbstractGate gate = map.get(event.getClickedBlock().getLocation());
            if (gate != null) {
                String name = gate.getName();
                if (!Utils.isRestricted(gate, CityGates.getPlugin().getManager(), "button", event.getPlayer())) {
                    gate.open();
                    gate.closeDelay(gate.getCloseDelay());
                }
            }
        }
    }

    public void dispose() {
        map.clear();
    }
    
    public Map<Location, AbstractGate> getMap(){
        return map;
    }

}
