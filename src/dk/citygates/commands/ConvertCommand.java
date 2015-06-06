package dk.citygates.commands;

import dk.citygates.entitys.AbstractGate;
import dk.citygates.entitys.Direction;
import dk.citygates.entitys.Gate;
import dk.citygates.entitys.Group;
import dk.citygates.logic.GateManager;
import dk.citygates.logic.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Steven Hermans
 */
public class ConvertCommand implements CommandExecutor {

    private GateManager manager;

    public ConvertCommand(GateManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (cs.hasPermission("citygates.admin.setanimation") || cs.isOp()) {
            if (args.length == 2) {
                String gatename = args[0];
                AbstractGate gate = manager.getGate(gatename);
                if (gate != null) {
                    if (gate instanceof Gate) {
                        Direction dir = Direction.getDirection(args[1]);
                        if (dir != null) {
                            manager.convertGate((Gate) gate, dir);
                            Utils.sendMessage(cs, "Create animation gate " + gatename);
                            manager.save();
                        } else {
                            Utils.sendError(cs, "Unknown direction " + args[1] + " - Use N(orth), S(outh), E(ast), W(est), U(p), D(own)");
                        }
                    } else {
                        Utils.sendError(cs, "Cannot use a group for this command");
                    }
                } else {
                    Utils.sendError(cs, "Could not find gate " + gatename);
                }
            } else {
                return false;
            }
        } else {
            Utils.sendError(cs, "You don't have permission to use this command");
        }
        return true;
    }

}
