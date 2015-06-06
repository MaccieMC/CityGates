package dk.citygates.logic;

import dk.citygates.CityGates;
import dk.citygates.entitys.AbstractGate;
import dk.citygates.entitys.BlockHolder;
import dk.citygates.entitys.Gate;
import dk.citygates.entitys.Group;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 *
 * @author Steven Hermans
 */
public class GriefListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlaced(BlockPlaceEvent event) {
        checkGrief(event, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        checkGrief(event, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockDamage(BlockDamageEvent event) {
        checkGrief(event, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onFireDestroy(BlockBurnEvent event) {
        checkGrief(event, null);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onFire(BlockIgniteEvent event) {
        checkGrief(event, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onFireSpread(BlockSpreadEvent event) {
        checkGrief(event, null);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBuild(BlockPlaceEvent event) {
        checkGrief(event, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityExplode(EntityExplodeEvent event) {
        List<Block> destroyed = event.blockList();
        Iterator<Block> it = destroyed.iterator();
        while (it.hasNext()) {
            Block block = it.next();
            for (AbstractGate ag : CityGates.getPlugin().getManager().getGates()) {
                if (ag instanceof Gate) {
                    Gate gate = (Gate) ag;
                    if (isProtected(gate)) {
                        if (isInside(gate, block.getLocation())) {
                            it.remove();
                            break;
                        }
                    }
                }
            }
        }
    }

    public void checkGrief(BlockEvent event, Player player) {
        if (event instanceof Cancellable) {
            Cancellable e = (Cancellable) event;
            Block block = event.getBlock();
            for (AbstractGate ag : CityGates.getPlugin().getManager().getGates()) {
                if (ag instanceof Gate) {
                    Gate gate = (Gate) ag;
                    if (player != null) {
                        if (!Utils.isRestricted(ag, CityGates.getPlugin().getManager(), "break", player)) {
                            continue;
                        }
                    }
                    if (isProtected(gate)) {
                        if (isInside(gate, block.getLocation())) {
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    private boolean isProtected(AbstractGate gate) {
        if (gate.isProtect()) {
            return true;
        }

        for (AbstractGate ag : CityGates.getPlugin().getManager().getGates()) {
            if (ag instanceof Group) {
                Group group = (Group) ag;
                for (AbstractGate child : group.getGates()) {
                    if (child.equals(gate)) {
                        if (isProtected(group)) {
                            return true;
                        }
                        break;
                    }
                }
            }
        }
        return false;
    }

    private boolean isInside(Gate gate, Location loc) {
        for (BlockHolder block : gate.getBlocks()) {
            if (loc.getBlockX() == block.getX()
                    && loc.getBlockY() == block.getY()
                    && loc.getBlockZ() == block.getZ()
                    && loc.getWorld().getName().equalsIgnoreCase(block.getWorld())) {
                return true;
            }
        }
        return false;
    }

}
