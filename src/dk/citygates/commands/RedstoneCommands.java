package dk.citygates.commands;

import dk.citygates.entitys.AbstractGate;
import dk.citygates.logic.GateManager;
import dk.citygates.logic.RedstoneListener;
import dk.citygates.logic.Utils;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Steven Hermans
 */
public class RedstoneCommands implements CommandExecutor {

    private GateManager manager;
    private RedstoneListener redstoneListener;

    public RedstoneCommands(GateManager manager, RedstoneListener redstoneListener) {
        this.manager = manager;
        this.redstoneListener = redstoneListener;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (args.length >= 1) {
            String subcommand = args[0];
            if (subcommand.equalsIgnoreCase("add")) {
                if (cs.hasPermission("citygates.admin.redstone.add") || cs.isOp()) {
                    if (cs instanceof Player) {
                        Player player = (Player) cs;
                        if (args.length >= 2) {
                            String gatename = args[1];
                            AbstractGate gate = manager.getGate(gatename);
                            if (gate != null) {
                                Block block = player.getTargetBlock(null, 50);
                                if (redstoneListener.addListener(block.getLocation(), gate)) {
                                    gate.getRedstoneLocations().add(block.getLocation());
                                    Utils.sendMessage(cs, "Redstone listener added for gate " + gatename);
                                    manager.save(gate);
                                } else {
                                    Utils.sendError(cs, "This Block is already used for a gate");
                                }
                            } else {
                                Utils.sendError(cs, "Could not find gate " + gatename);
                            }
                        } else {
                            Utils.sendError(cs, "/gredstone add [gate]");
                        }
                    } else {
                        Utils.sendError(cs, "Only players can use this command");
                    }
                } else {
                    Utils.sendError(cs, "You don't have permission to add redstone listeners");
                }
            } else if (subcommand.equalsIgnoreCase("remove")) {
                if (cs.hasPermission("citygates.admin.redstone.remove") || cs.isOp()) {
                    if (cs instanceof Player) {
                        Player player = (Player) cs;
                        if (args.length >= 2) {
                            String gatename = args[1];
                            AbstractGate gate = manager.getGate(gatename);
                            if (gate != null) {
                                Block block = player.getTargetBlock(null, 50);
                                if (redstoneListener.removeListener(block.getLocation(), gate)) {
                                    gate.getRedstoneLocations().remove(block.getLocation());
                                    Utils.sendMessage(cs, "Redstone listener removed for gate " + gatename);
                                    manager.save(gate);
                                } else {
                                    Utils.sendError(cs, "This location is not listening for gate " + gatename);
                                }
                            } else {
                                Utils.sendError(cs, "Could not find gate " + gatename);
                            }
                        } else {
                            Utils.sendError(cs, "/gredstone remove [gate]");
                        }
                    } else {
                        Utils.sendError(cs, "Only players can use this command");
                    }
                } else {
                    Utils.sendError(cs, "You don't have permission to remove redstone listeners");
                }
            } else {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

}
