package dk.citygates.commands;

import dk.citygates.entitys.AbstractGate;
import dk.citygates.entitys.Direction;
import dk.citygates.entitys.Gate;
import dk.citygates.entitys.Group;
import dk.citygates.logic.GateManager;
import dk.citygates.logic.PointerManager;
import dk.citygates.logic.Utils;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Steven Hermans
 */
public class CreateCommand implements CommandExecutor {

    private PointerManager pointer;
    private GateManager manager;

    public CreateCommand(PointerManager pointer, GateManager manager) {
        this.pointer = pointer;
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (cs.hasPermission("citygates.admin.create") || cs.isOp()) {
            if (args.length == 1 || args.length == 2) {
                if (cs instanceof Player) {
                    Player player = (Player) cs;
                    String name = player.getPlayerListName();
                    if (pointer.hasSelection(name)) {
                        Location p1 = pointer.getPoint1(name);
                        Location p2 = pointer.getPoint2(name);
                        if (p1.getWorld().equals(p2.getWorld())) {
                            String gateName = args[0];
                            if (manager.getGate(gateName) == null) {
                                ArrayList<Location> locs = Utils.getLocations(p1, p2);
                                if (args.length == 1) {
                                    Gate gate = manager.createGate(gateName, locs);
                                    Utils.sendMessage(cs, "Create gate " + gateName);
                                    pointer.showSelection(name);
                                    manager.save(gate);
                                } else if (args.length == 2) {
                                    Direction dir = Direction.getDirection(args[1]);
                                    if (dir != null) {
                                        Group group = manager.createGate(gateName, locs, dir);
                                        Utils.sendMessage(cs, "Create animation gate " + gateName);
                                        pointer.showSelection(name);
                                        manager.save(group);
                                        for(AbstractGate gate : group.getGates()){
                                            manager.save(gate);
                                        }
                                    } else {
                                        Utils.sendError(cs, "Unknown direction " + args[1] + " - Use N(orth), S(outh), E(ast), W(est), U(p), D(own)");
                                    }
                                } else {
                                    return false;
                                }
                            } else {
                                Utils.sendError(cs, "Gate " + gateName + " already exists");
                            }
                        } else {
                            Utils.sendError(cs, "You need to make a selection first: use /gp1 and /gp2");
                        }
                    } else {
                        Utils.sendError(cs, "You need to make a selection first: use /gp1 and /gp2");
                    }
                } else {
                    Utils.sendError(cs, "Only a player can execute this command");
                }
                return true;
            } else {
                return false;
            }
        } else {
            Utils.sendError(cs, "You don't have permission to create gates");
            return true;
        }
    }
}
