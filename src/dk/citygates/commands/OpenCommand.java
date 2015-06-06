package dk.citygates.commands;

import dk.citygates.entitys.AbstractGate;
import dk.citygates.logic.GateManager;
import dk.citygates.logic.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Steven Hermans
 */
public class OpenCommand implements CommandExecutor {

    private GateManager manager;

    public OpenCommand(GateManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (!cs.hasPermission("citygates.user.open") && !cs.isOp()) {
            Utils.sendError(cs, "You don't have permission to open gates");
            return true;
        }
        String name;
        if (args.length >= 1) {
            name = args[0];
        } else {
            return false;
        }
        if (name != null) {
            AbstractGate gate = manager.getGate(name);
            if (gate != null) {
                if (!Utils.isRestricted(gate, manager, "open", cs)) {
                    gate.open();
                } else {
                    Utils.sendError(cs, "You don't have permission to open gate " + name);
                    return true;
                }
            } else {
                Utils.sendError(cs, "Could not find gate " + name);
            }
            return true;
        }
        return false;
    }

}
