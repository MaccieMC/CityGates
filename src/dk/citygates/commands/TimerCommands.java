package dk.citygates.commands;

import dk.citygates.entitys.AbstractGate;
import dk.citygates.logic.GateManager;
import dk.citygates.logic.GateTimer;
import dk.citygates.logic.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Steven Hermans
 */
public class TimerCommands implements CommandExecutor {

    private GateManager manager;
    private GateTimer timer;

    public TimerCommands(GateManager manager, GateTimer timer) {
        this.manager = manager;
        this.timer = timer;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (args.length >= 1) {
            String subcommand = args[0];
            if (subcommand.equalsIgnoreCase("add")) {
                if (cs.hasPermission("citygates.admin.timer.add") || cs.isOp()) {
                    if (args.length >= 1) {
                        String gatename = args[1];
                        AbstractGate gate = manager.getGate(gatename);
                        if (gate != null) {
                            timer.addTimer(gate);
                            gate.setTimeGate(true);
                            Utils.sendMessage(cs, new String[]{
                                "Gate " + gatename + " is now a time gate",
                                gatename + " opens at " + gate.getOpenTime() + " and closes at " + gate.getCloseTime()});
                            manager.save(gate);
                        } else {
                            Utils.sendError(cs, "Could not find gate " + gatename);
                        }
                    } else {
                        Utils.sendMessage(cs, "/gtimer add [gate]");
                    }
                } else {
                    Utils.sendError(cs, "You don't have permission to add timegates");
                }
            } else if (subcommand.equalsIgnoreCase("remove")) {
                if (cs.hasPermission("citygates.admin.timer.remove") || cs.isOp()) {
                    if (args.length >= 1) {
                        String gatename = args[1];
                        AbstractGate gate = manager.getGate(gatename);
                        if (gate != null) {
                            timer.removeTimer(gate);
                            gate.setTimeGate(false);
                            Utils.sendMessage(cs, "Gate " + gatename + " is no longer a time gate");
                            manager.save(gate);
                        } else {
                            Utils.sendError(cs, "Could not find gate " + gatename);
                        }
                    } else {
                        Utils.sendMessage(cs, "/gtimer remove [gate]");
                    }
                } else {
                    Utils.sendError(cs, "You don't have permission to remove timegates");
                }
            } else if (subcommand.equalsIgnoreCase("opentime")) {
                if (cs.hasPermission("citygates.admin.timer.open") || cs.isOp()) {
                    if (args.length >= 1) {
                        String gatename = args[1];
                        AbstractGate gate = manager.getGate(gatename);
                        if (gate != null) {
                            if (args.length >= 3) {
                                try {
                                    int ms = Integer.parseInt(args[2]);
                                    gate.setOpenTime(ms);
                                    Utils.sendMessage(cs, "Change open time to " + ms);
                                    manager.save(gate);
                                } catch (java.lang.NumberFormatException e) {
                                    Utils.sendMessage(cs, "/gtimer opentime [gate] [time]");
                                }
                            } else {
                                Utils.sendMessage(cs, "/gtimer opentime [gate] [time]");
                            }
                        } else {
                            Utils.sendError(cs, "Could not find gate " + gatename);
                        }
                    } else {
                        Utils.sendMessage(cs, "/gtimer opentime [gate] [time]");
                    }
                } else {
                    Utils.sendError(cs, "You don't have permission to change the open time");
                }
            } else if (subcommand.equalsIgnoreCase("closetime")) {
                if (cs.hasPermission("citygates.admin.timer.close") || cs.isOp()) {
                    if (args.length >= 1) {
                        String gatename = args[1];
                        AbstractGate gate = manager.getGate(gatename);
                        if (gate != null) {
                            if (args.length >= 3) {
                                try {
                                    int ms = Integer.parseInt(args[2]);
                                    gate.setCloseTime(ms);
                                    Utils.sendMessage(cs, "Change close time to " + ms);
                                    manager.save(gate);
                                } catch (java.lang.NumberFormatException e) {
                                    Utils.sendMessage(cs, "/gtimer closetime [gate] [time]");
                                }
                            } else {
                                Utils.sendMessage(cs, "/gtimer closetime [gate] [time]");
                            }
                        } else {
                            Utils.sendError(cs, "Could not find gate " + gatename);
                        }
                    } else {
                        Utils.sendMessage(cs, "/gtimer closetime [gate] [time]");
                    }
                } else {
                    Utils.sendError(cs, "You don't have permission to change the close time");
                }
            } else {
                return false;
            }
            return true;
        }
        return false;
    }
}
