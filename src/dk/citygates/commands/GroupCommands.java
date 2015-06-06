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
public class GroupCommands implements CommandExecutor {

    private GateManager manager;

    public GroupCommands(GateManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (args.length >= 1) {
            String subcommand = args[0];
            if (subcommand.equalsIgnoreCase("create")) {
                if (cs.hasPermission("citygates.admin.group.create") || cs.isOp()) {
                    if (args.length >= 2) {
                        String name = args[1];
                        if (manager.getGate(name) == null) {
                            manager.createGroup(name);
                            Utils.sendMessage(cs, "Create group " + name);
                            manager.save(manager.getGate(name));
                        } else {
                            Utils.sendError(cs, "Group " + name + " already exists");
                        }
                    } else {
                        Utils.sendMessage(cs, "/ggroup create [group name]");
                    }
                } else {
                    Utils.sendError(cs, "You don't have permission to create groups");
                }
            } else if (subcommand.equalsIgnoreCase("delete")) {
                if (cs.hasPermission("citygates.admin.group.delete") || cs.isOp()) {
                    if (args.length >= 2) {
                        String name = args[1];
                        AbstractGate gate = manager.getGate(name);
                        if (gate != null) {
                            if (gate instanceof Group) {
                                Group group = (Group) gate;
                                manager.deleteGroup(name);
                                Utils.sendMessage(cs, "Delete group " + name);
                                manager.save();
                            } else {
                                Utils.sendError(cs, "Could not find group " + name);
                            }
                        } else {
                            Utils.sendError(cs, "Could not find group " + name);
                        }
                    } else {
                        Utils.sendMessage(cs, "/ggroup delete [group name]");
                    }
                } else {
                    Utils.sendError(cs, "You don't have permission to delete groups");
                }
            } else if (subcommand.equalsIgnoreCase("add")) {
                if (cs.hasPermission("citygates.admin.group.add") || cs.isOp()) {
                    if (args.length >= 3) {
                        String groupname = args[1];
                        String childname = args[2];
                        AbstractGate gate = manager.getGate(groupname);
                        if (gate != null) {
                            if (gate instanceof Group) {
                                Group group = (Group) gate;
                                gate = manager.getGate(childname);
                                if(gate != null){
                                    for(AbstractGate chld : group.getGates()){
                                        if(childname.equalsIgnoreCase(chld.getName())){
                                            Utils.sendError(cs, childname + " is already added to " + groupname);
                                            return true;
                                        }
                                    }
                                    group.addGate(gate);
                                    Utils.sendMessage(cs, childname + " is added to group " + groupname);
                                    manager.save(group);
                                }else{
                                    Utils.sendError(cs, "Could not find gate " + childname);
                                }
                            } else {
                                Utils.sendError(cs, "Could not find group " + groupname);
                            }
                        } else {
                            Utils.sendError(cs, "Could not find group " + groupname);
                        }
                    } else {
                        Utils.sendMessage(cs, "/ggroup add [group name] [child name]");
                    }
                } else {
                    Utils.sendError(cs, "You don't have permission to add childs to groups");
                }
            } else if (subcommand.equalsIgnoreCase("remove")) {
                if (cs.hasPermission("citygates.admin.group.remove") || cs.isOp()) {
                    if (args.length >= 3) {
                        String groupname = args[1];
                        String childname = args[2];
                        AbstractGate gate = manager.getGate(groupname);
                        if (gate != null) {
                            if (gate instanceof Group) {
                                Group group = (Group) gate;
                                gate = manager.getGate(childname);
                                if(gate != null){
                                    group.removeGate(gate);
                                    Utils.sendMessage(cs, childname + " is removed from group " + groupname);
                                    manager.save(group);
                                }else{
                                    Utils.sendError(cs, "Could not find gate " + childname);
                                }
                            } else {
                                Utils.sendError(cs, "Could not find group " + groupname);
                            }
                        } else {
                            Utils.sendError(cs, "Could not find group " + groupname);
                        }
                    } else {
                        Utils.sendMessage(cs, "/ggroup remove [group name] [child name]");
                    }
                } else {
                    Utils.sendError(cs, "You don't have permission to remove childs from groups");
                }
            } else if (subcommand.equalsIgnoreCase("delay")) {
                if (cs.hasPermission("citygates.admin.group.delay") || cs.isOp()) {
                    if (args.length >= 3) {
                        String groupname = args[1];
                        String delay = args[2];
                        AbstractGate gate = manager.getGate(groupname);
                        if (gate != null) {
                            if (gate instanceof Group) {
                                Group group = (Group) gate;
                                try{
                                    int dly = Integer.parseInt(delay);
                                    group.setDelay(dly);
                                    Utils.sendMessage(cs, "Change delay to " + dly + " for group " + groupname);
                                    manager.save();
                                }catch(java.lang.NumberFormatException e){
                                    return false;
                                }
                            } else {
                                Utils.sendError(cs, "Could not find group " + groupname);
                            }
                        } else {
                            Utils.sendError(cs, "Could not find group " + groupname);
                        }
                    } else {
                        Utils.sendMessage(cs, "/ggroup delay [group name] [delay]");
                    }
                } else {
                    Utils.sendError(cs, "You don't have permission to set the delay");
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
