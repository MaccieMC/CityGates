package dk.citygates.commands;

import dk.citygates.entitys.AbstractGate;
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
public class DeleteCommand implements CommandExecutor {

    private GateManager manager;

    public DeleteCommand(GateManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (cs.hasPermission("citygates.admin.delete") || cs.isOp()) {
            if (args.length >= 1) {
                String name = args[0];
                AbstractGate gate = manager.getGate(name);
                if (gate == null) {
                    Utils.sendError(cs, "Could not find gate " + name);
                } else {
                    int type = 0;
                    if (args.length >= 2) {
                        if (args[1].equalsIgnoreCase("none")) {
                            type = 0;
                        } else if (args[1].equalsIgnoreCase("all")) {
                            type = 1;
                        } else {
                            return false;
                        }
                    }
                    if (gate instanceof Group && type == 1) {
                        Group group = (Group) gate;
                        for (AbstractGate child : group.getGates()) {
                            removeChilds(child);
                            manager.removeGate(child);
                        }
                    }
                    manager.removeGate(gate);
                    if(type == 0){
                        Utils.sendMessage(cs, "Remove gate " + name);
                    }else{
                        Utils.sendMessage(cs, "Remove gate " + name + " with al its children");
                    }
                    manager.save();
                }
                return true;
            } else {
                return false;
            }
        } else {
            Utils.sendError(cs, "You don't have permission to delete a gates");
            return true;
        }
    }

    private void removeChilds(AbstractGate child) {
        if (child instanceof Group) {
            Group group = (Group) child;
            for (AbstractGate c : group.getGates()) {
                removeChilds(c);
                manager.removeGate(c);
            }
        }
    }

}
