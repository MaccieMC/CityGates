package dk.citygates.commands;

import dk.citygates.entitys.AbstractGate;
import dk.citygates.logic.GateManager;
import dk.citygates.logic.KillListener;
import dk.citygates.logic.PointerManager;
import dk.citygates.logic.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 *
 * @author Steven Hermans
 */
public class KillCommands implements CommandExecutor {

    private GateManager manager;
    private KillListener killListener;
    private PointerManager pointer;

    public KillCommands(GateManager manager, KillListener killListener, PointerManager pointer) {
        this.manager = manager;
        this.killListener = killListener;
        this.pointer = pointer;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (args.length >= 1) {
            String subcommand = args[0];
            if (subcommand.equalsIgnoreCase("set")) {
                if (cs.hasPermission("citygates.admin.kill.set") || cs.isOp()) {
                    if (args.length >= 3) {
                        String gatename = args[1];
                        AbstractGate gate = manager.getGate(gatename);
                        if (gate != null) {
                            String entityname = args[2];
                            EntityType type = null;
                            try {
                                type = EntityType.valueOf(entityname.toUpperCase());
                            } catch (IllegalArgumentException e) {
                            }
                            if (type == null) {
                                type = EntityType.fromName(entityname);
                            }
                            if (type == null) {
                                try {
                                    type = EntityType.fromId(Integer.parseInt(entityname));
                                } catch (java.lang.NumberFormatException e) {
                                }
                            }
                            if (type != null) {
                                gate.setEntityType(type.toString());
                                killListener.addGate(gate);
                                Utils.sendMessage(cs, "Gate " + gatename + " opens when a " + type.toString() + " is slayed");
                                manager.save(gate);
                            } else {
                                Utils.sendError(cs, "Could not find mob " + entityname);
                            }
                        } else {
                            Utils.sendMessage(cs, "Could not find gate " + gatename);
                        }
                    } else {
                        Utils.sendMessage(cs, "/kill set [gate] [mob name]");
                    }
                } else {
                    Utils.sendError(cs, "You don't have permission to set kill listeners");
                }
            } else if (subcommand.equalsIgnoreCase("remove")) {
                if (cs.hasPermission("citygates.admin.kill.remove") || cs.isOp()) {
                    if (args.length >= 2) {
                        String gatename = args[1];
                        AbstractGate gate = manager.getGate(gatename);
                        if (gate != null) {
                            gate.setEntityType(null);
                            killListener.removeGate(gate);
                            Utils.sendMessage(cs, "Gate " + gatename + " no longer opens when a entity is slayed");
                        } else {
                            Utils.sendMessage(cs, "Could not find gate " + gatename);
                        }
                    } else {
                        Utils.sendMessage(cs, "/kill remove [gate]");
                    }
                } else {
                    Utils.sendError(cs, "You don't have permission to remove kill listeners");
                }
            } else if (subcommand.equalsIgnoreCase("location") || subcommand.equalsIgnoreCase("loc")) {
                if (cs.hasPermission("citygates.admin.kill.loc") || cs.isOp()) {
                    if (args.length >= 2) {
                        String gatename = args[1];
                        AbstractGate gate = manager.getGate(gatename);
                        if (gate != null) {
                            if (args.length >= 3) {
                                if (args[2].equals("none")) {
                                    gate.setKillArea1(null);
                                    gate.setKillArea2(null);
                                    Utils.sendMessage(cs, "Kill location for " + gatename + " is removed");
                                    manager.save(gate);
                                } else {
                                    Utils.sendMessage(cs, "/kill loc [gate] <none>");
                                }
                            } else {
                                if (cs instanceof Player) {
                                    Player player = (Player) cs;
                                    String name = player.getPlayerListName();
                                    if (pointer.hasSelection(name)) {
                                        gate.setKillArea1(pointer.getPoint1(name));
                                        gate.setKillArea2(pointer.getPoint2(name));
                                        Utils.sendMessage(cs, "Kill location for " + gatename + " is set");
                                        manager.save(gate);
                                    } else {
                                        Utils.sendError(cs, "You need to make a selection first: use /gp1 and /gp2");
                                    }
                                } else {
                                    Utils.sendError(cs, "Only players can use this command");
                                }
                            }
                        } else {
                            Utils.sendMessage(cs, "Could not find gate " + gatename);
                        }
                    } else {
                        Utils.sendMessage(cs, "/kill loc [gate] <none>");
                    }
                } else {
                    Utils.sendError(cs, "You don't have permission to set the kill location");
                }
            } else if (subcommand.equalsIgnoreCase("message") || subcommand.equalsIgnoreCase("msg")) {
                if (cs.hasPermission("citygates.admin.kill.msg") || cs.isOp()) {
                    if (args.length >= 3) {
                        String gatename = args[1];
                        AbstractGate gate = manager.getGate(gatename);
                        if (gate != null) {
                            String msg = args[2];
                            if (msg.equalsIgnoreCase("none")) {
                                gate.setKillMsg(null);
                                Utils.sendMessage(cs, "Kill message for " + gatename + " is removed");
                                manager.save(gate);
                            } else {
                                if (args.length > 3) {
                                    for (int i = 3; i < args.length; i++) {
                                        msg += " " + args[i];
                                    }
                                }
                                gate.setKillMsg(msg);
                                Utils.sendMessage(cs, "Kill message for " + gatename + " is set");
                                manager.save(gate);
                            }
                        } else {
                            Utils.sendMessage(cs, "Could not find gate " + gatename);
                        }
                    } else {
                        Utils.sendMessage(cs, "/kill msg [gate] [msg]");
                    }
                } else {
                    Utils.sendError(cs, "You don't have permission to set the kill message");
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
