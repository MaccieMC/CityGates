package dk.citygates.commands;

import dk.citygates.entitys.AbstractGate;
import dk.citygates.logic.ButtonListener;
import dk.citygates.logic.GateManager;
import dk.citygates.logic.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Steven Hermans
 */
public class ButtonCommands implements CommandExecutor {

    private GateManager manager;
    private ButtonListener buttonListener;

    public ButtonCommands(GateManager manager, ButtonListener buttonListener) {
        this.manager = manager;
        this.buttonListener = buttonListener;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (args.length >= 1) {
            String subcommand = args[0];
            if (subcommand.equalsIgnoreCase("add")) {
                if (cs.hasPermission("citygates.admin.button.add") || cs.isOp()) {
                    if (cs instanceof Player) {
                        Player player = (Player) cs;
                        if (args.length >= 2) {
                            String gatename = args[1];
                            AbstractGate gate = manager.getGate(gatename);
                            if (gate != null) {
                                Block block = player.getTargetBlock(null, 50);
                                if (block.getType().equals(Material.STONE_BUTTON) || block.getType().equals(Material.WOOD_BUTTON)) {
                                    if (buttonListener.addListener(block.getLocation(), gate)) {
                                        gate.getButtonLocations().add(block.getLocation());
                                        Utils.sendMessage(cs, "Button listener added for gate " + gatename);
                                        manager.save(gate);
                                    } else {
                                        Utils.sendError(cs, "This button is already used for a gate");
                                    }
                                } else {
                                    Utils.sendError(cs, "You should look at a button");
                                }
                            } else {
                                Utils.sendError(cs, "Could not find gate " + gatename);
                            }
                        } else {
                            Utils.sendMessage(cs, "/gbutton add [gate]");
                        }
                    } else {
                        Utils.sendError(cs, "Only players can use this command");
                    }
                } else {
                    Utils.sendError(cs, "You don't have permission to add button listeners");
                }
            } else if (subcommand.equalsIgnoreCase("remove")) {
                if (cs.hasPermission("citygates.admin.button.remove") || cs.isOp()) {
                    if (cs instanceof Player) {
                        Player player = (Player) cs;
                        if (args.length >= 2) {
                            String gatename = args[1];
                            AbstractGate gate = manager.getGate(gatename);
                            if (gate != null) {
                                Block block = player.getTargetBlock(null, 50);
                                if (block.getType().equals(Material.STONE_BUTTON) || block.getType().equals(Material.WOOD_BUTTON)) {
                                    if (buttonListener.removeListener(block.getLocation(), gate)) {
                                        gate.getButtonLocations().remove(block.getLocation());
                                        Utils.sendMessage(cs, "Button listener removed for gate " + gatename);
                                        manager.save(gate);
                                    } else {
                                        Utils.sendError(cs, "This button is not listening for gate " + gatename);
                                    }
                                } else {
                                    Utils.sendError(cs, "You should look at a button");
                                }
                            } else {
                                Utils.sendError(cs, "Could not find gate " + gatename);
                            }
                        } else {
                            Utils.sendMessage(cs, "/gbutton add [gate]");
                        }
                    } else {
                        Utils.sendError(cs, "Only players can use this command");
                    }
                } else {
                    Utils.sendError(cs, "You don't have permission to remove button listeners");
                }
            } else if (subcommand.equalsIgnoreCase("delay")) {
                if (cs.hasPermission("citygates.admin.button.delay") || cs.isOp()) {
                    if (args.length >= 3) {
                        String gatename = args[1];
                        AbstractGate gate = manager.getGate(gatename);
                        if (gate != null) {
                            try {
                                int ms = Integer.parseInt(args[2]);
                                gate.setCloseDelay(ms);
                                Utils.sendMessage(cs, "Change button close delay to " + ms);
                                manager.save(gate);
                            } catch (java.lang.NumberFormatException e) {
                                Utils.sendMessage(cs, "/gbutton delay [gate] [time]");
                            }
                        } else {
                            Utils.sendError(cs, "Could not find gate " + gatename);
                        }
                    } else {
                        Utils.sendError(cs, "/gbutton delay [gate] [time]");
                    }
                } else {
                    Utils.sendError(cs, "You don't have permission to change the close delay");
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
