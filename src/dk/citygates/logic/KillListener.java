package dk.citygates.logic;

import dk.citygates.CityGates;
import dk.citygates.entitys.AbstractGate;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 *
 * @author Steven Hermans
 */
public class KillListener implements Listener {

    private Set<AbstractGate> gates = new HashSet();

    public void addGate(AbstractGate gate) {
        if (!gates.contains(gate)) {
            gates.add(gate);
        }
    }

    public void removeGate(AbstractGate gate) {
        gates.remove(gate);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onKillListener(EntityDeathEvent event) {
        if(event.getEntity().getKiller() == null){
            return;
        }
        if (!event.getEntity().getKiller().hasPermission("citygates.user.button") && !event.getEntity().getKiller().isOp()) {
            return;
        }
        for (AbstractGate gate : gates) {
            if (event.getEntityType().toString().equalsIgnoreCase(gate.getEntityType())) {
                if (gate.getKillArea1() == null || gate.getKillArea2() == null) {
                    kill(gate, event.getEntity().getKiller());
                } else {
                    Location loc = event.getEntity().getLocation();
                    boolean contains = Utils.contains(loc, gate.getKillArea1(), gate.getKillArea2());
                    if (contains) {
                        kill(gate, event.getEntity().getKiller());
                    }
                }
            }
        }
    }

    private void kill(AbstractGate gate, Player player) {
        String name = gate.getName();
        if (!Utils.isRestricted(gate, CityGates.getPlugin().getManager(), "kill", player)) {
            gate.open();
            if (gate.getKillMsg() != null) {
                player.sendMessage(gate.getKillMsg());
            }
        }
    }

    public void dispose() {
        gates.clear();
    }
    
    public Set<AbstractGate> getGates(){
        return gates;
    }

}
