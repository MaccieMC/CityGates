package dk.citygates.commands;

import dk.citygates.entitys.AbstractGate;
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
public class InfoCommand implements CommandExecutor {

    private GateManager manager;

    public InfoCommand(GateManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (cs.hasPermission("citygates.user.info") || cs.isOp()) {
            if (args.length == 1) {
                String gatename = args[0];
                AbstractGate gate = manager.getGate(gatename);
                if (gate != null) {
                    if (gate instanceof Gate) {
                        String[] output = new String[]{
                            "Gate[" + gate.getName() + "] info: ",
                            "-------------------------",
                            "world:        " + gate.getWorld(),
                            "state:        " + (gate.isOpen() ? "open" : "close"),
                            "protected:    " + gate.isProtect(),
                            "has timer:    " + gate.isTimeGate(),
                            "has buttons:  " + !gate.getButtonLocations().isEmpty(),
                            "has redstone: " + !gate.getRedstoneLocations().isEmpty(),
                            "has kill:     " + (gate.getEntityType() != null)
                        };
                        Utils.sendMessage(cs, output);
                    } else {
                        Group group = (Group) gate;
                        String children = "<none>";
                        if (group.getGates().length > 0) {
                            children = group.getGates()[0].getName();
                            for (int i = 1; i < group.getGates().length; i++) {
                                children += "," + group.getGates()[i].getName();
                            }
                        }
                        String[] output = new String[]{
                            "Group[" + gate.getName() + "] info: ",
                            "-------------------------",
                            "world:        " + gate.getWorld(),
                            "state:        " + (gate.isOpen() ? "open" : "close"),
                            "protected:    " + gate.isProtect(),
                            "has timer:    " + gate.isTimeGate(),
                            "has buttons:  " + !gate.getButtonLocations().isEmpty(),
                            "has redstone: " + !gate.getRedstoneLocations().isEmpty(),
                            "has kill:     " + (gate.getEntityType() != null),
                            "delay:        " + group.getDelay(),
                            "children:     [" + children + "]"
                        };
                        Utils.sendMessage(cs, output);
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
