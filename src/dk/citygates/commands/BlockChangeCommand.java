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
public class BlockChangeCommand implements CommandExecutor {

    private GateManager manager;

    public BlockChangeCommand(GateManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (cs.hasPermission("citygates.admin.changeblocks") || cs.isOp()) {
            if (args.length >= 2) {
                String gatename = args[0];
                String state = args[1];
                AbstractGate gate = manager.getGate(gatename);
                if (gate != null) {
                    if (state.equalsIgnoreCase("open")
                            || state.equalsIgnoreCase("o")
                            || state.equalsIgnoreCase("close")
                            || state.equalsIgnoreCase("c")) {
                        if (gate instanceof Group) {
                            if (args.length == 3) {
                                manager.changeBlocks(gate, (state.equalsIgnoreCase("open") || state.equalsIgnoreCase("o")));
                                Utils.sendMessage(cs, (state.equalsIgnoreCase("open") ? "Open " : "Close ") + "Blocks changed");
                                manager.saveGroup(gate);
                            } else {
                                Utils.sendError(cs, "If you want to change the children blocks of this group, you need to use the command: /gchangeblocks [gate] [state:open/close] <children>");
                            }
                        } else {
                            manager.changeBlocks(gate, (state.equalsIgnoreCase("open") || state.equalsIgnoreCase("o")));
                                Utils.sendMessage(cs, (state.equalsIgnoreCase("open") ? "Open " : "Close ") + "Blocks changed");
                            manager.save(gate);
                        }
                    } else {
                        Utils.sendError(cs, "Unknown state \'" + state + "\' [open/close]");
                    }
                } else {
                    Utils.sendError(cs, "Could not find gate " + gatename);
                }
            } else {
                return false;
            }
        } else {
            Utils.sendError(cs, "You don't have permission to change blocks");
        }
        return true;
    }

}
