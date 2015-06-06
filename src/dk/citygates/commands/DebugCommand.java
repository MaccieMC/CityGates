package dk.citygates.commands;

import dk.citygates.entitys.AbstractGate;
import dk.citygates.entitys.Gate;
import dk.citygates.entitys.Group;
import dk.citygates.logic.GateManager;
import dk.citygates.logic.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Steven Hermans
 */
public class DebugCommand implements CommandExecutor {

    private GateManager manager;

    public DebugCommand(GateManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (!(cs instanceof Player)) {
            if (args.length >= 1) {
                String subcommand = args[0];
                if (subcommand.equalsIgnoreCase("redstate")) {
                    Map<Location, AbstractGate> map = manager.getRedstoneListener().getMap();
                    Set<Location> keys = map.keySet();
                    String[] output = new String[keys.size()];
                    int i = 0;
                    for (Location key : keys) {
                        AbstractGate gate = map.get(key);
                        String name = gate.getName() + ":";
                        while (name.length() < 11) {
                            name += " ";
                        }
                        output[i] = name + "[" + key.getBlockX() + "," + key.getBlockY() + "," + key.getBlockZ() + "," + key.getWorld().getName() + "]";
                        i++;
                    }
                    Arrays.sort(output);
                    Utils.sendMessage(cs, new String[]{"Redstone Listeners:", "-------------------------"});
                    if (output.length == 0) {
                        Utils.sendMessage(cs, "<none>");
                    } else {
                        Utils.sendMessage(cs, output);
                    }
                } else if (subcommand.equalsIgnoreCase("buttonstate")) {
                    Map<Location, AbstractGate> map = manager.getButtonListener().getMap();
                    Set<Location> keys = map.keySet();
                    String[] output = new String[keys.size()];
                    int i = 0;
                    for (Location key : keys) {
                        AbstractGate gate = map.get(key);
                        String name = gate.getName() + ":";
                        while (name.length() < 11) {
                            name += " ";
                        }
                        output[i] = name + "[" + key.getBlockX() + "," + key.getBlockY() + "," + key.getBlockZ() + "," + key.getWorld().getName() + "], delay:" + gate.getCloseDelay() + "ms";
                        i++;
                    }
                    Arrays.sort(output);
                    Utils.sendMessage(cs, new String[]{"Button Listeners:", "-------------------------"});
                    if (output.length == 0) {
                        Utils.sendMessage(cs, "<none>");
                    } else {
                        Utils.sendMessage(cs, output);
                    }
                } else if (subcommand.equalsIgnoreCase("killstate")) {
                    Set<AbstractGate> keys = manager.getKillListener().getGates();
                    String[] output = new String[keys.size()];
                    int i = 0;
                    for (AbstractGate gate : keys) {
                        String name = gate.getName() + ":";
                        while (name.length() < 11) {
                            name += " ";
                        }
                        output[i] = name + "entity:" + gate.getEntityType() + "";
                        if (gate.getKillArea1() != null && gate.getKillArea2() != null) {
                            Location loc1 = gate.getKillArea1();
                            output[i] += ", loc1:[" + loc1.getBlockX() + "," + loc1.getBlockY() + "," + loc1.getBlockZ() + "," + loc1.getWorld().getName() + "]";
                            Location loc2 = gate.getKillArea1();
                            output[i] += ", loc2:[" + loc2.getBlockX() + "," + loc2.getBlockY() + "," + loc2.getBlockZ() + "," + loc2.getWorld().getName() + "]";
                        }
                        if (gate.getKillMsg() != null) {
                            output[i] += ", msg:true";
                        } else {
                            output[i] += ", msg:false";
                        }
                        i++;
                    }
                    Arrays.sort(output);
                    Utils.sendMessage(cs, new String[]{"Kill Listeners:", "-------------------------"});
                    if (output.length == 0) {
                        Utils.sendMessage(cs, "<none>");
                    } else {
                        Utils.sendMessage(cs, output);
                    }
                } else if (subcommand.equalsIgnoreCase("timerstate")) {
                    Map<String, ArrayList<AbstractGate>> map = manager.getGateTimer().getMap();
                    Set<String> keys = map.keySet();
                    int longest = 0;
                    for (String world : keys) {
                        if (world.length() > longest) {
                            longest = world.length();
                        }
                    }

                    Utils.sendMessage(cs, new String[]{"Gate Timer:", "-------------------------"});
                    for (String world : keys) {
                        String name = world + ":";
                        while (name.length() < longest + 1) {
                            name += " ";
                        }
                        String empty = "";
                        while (empty.length() < longest + 1) {
                            empty += " ";
                        }
                        ArrayList<AbstractGate> gates = map.get(world);
                        boolean d = false;
                        for (AbstractGate gate : gates) {
                            if (!d) {
                                Utils.sendMessage(cs, name + " " + gate.getName() + "open: " + gate.getOpenTime() + ", close: " + gate.getCloseTime());
                                d = true;
                            } else {
                                Utils.sendMessage(cs, empty + " " + gate.getName() + "open: " + gate.getOpenTime() + ", close: " + gate.getCloseTime());
                            }
                        }
                        if (gates.isEmpty()) {
                            Utils.sendMessage(cs, name + " <none>");
                        }
                    }
                } else if (subcommand.equalsIgnoreCase("perm")) {
                    if(args.length > 1){
                        String gatename = args[1];
                        AbstractGate gate = manager.getGate(gatename);
                        if(gate != null){
                            String[] output = new String[]{
                                "Gate["+gate.getName()+"] permissions: ",
                                "-------------------------",
                                "open:   " + gate.perm.isPermOpen(),
                                "close:  " + gate.perm.isPermClose(),
                                "button: " + gate.perm.isPermButton(),
                                "kill:   " + gate.perm.isPermKill()
                            };
                            Utils.sendMessage(cs, output);
                        }else{
                            Utils.sendError(cs, "Could not find gate " + gatename);
                        }
                    }else{
                        return false;
                    }
                } else if (subcommand.equalsIgnoreCase("info")) {
                    if(args.length > 1){
                        String gatename = args[1];
                        AbstractGate gate = manager.getGate(gatename);
                        if(gate != null){
                            if(gate instanceof Gate){
                                String[] output = new String[]{
                                    "Gate["+gate.getName()+"] info: ",
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
                            }else{
                                Group group = (Group) gate;
                                String children = "<none>";
                                if(group.getGates().length > 0){
                                    children = group.getGates()[0].getName();
                                    for(int i = 1; i < group.getGates().length; i++){
                                        children += "," + group.getGates()[i].getName();
                                    }
                                }
                                String[] output = new String[]{
                                    "Group["+gate.getName()+"] info: ",
                                    "-------------------------",
                                    "world:        " + gate.getWorld(),
                                    "state:        " + (gate.isOpen() ? "open" : "close"),
                                    "has timer:    " + gate.isTimeGate(),
                                    "has buttons:  " + !gate.getButtonLocations().isEmpty(),
                                    "has redstone: " + !gate.getRedstoneLocations().isEmpty(),
                                    "has kill:     " + (gate.getEntityType() != null),
                                    "delay:        " + group.getDelay(),
                                    "children:     [" + children + "]"
                                };
                                Utils.sendMessage(cs, output);
                            }
                        }else{
                            Utils.sendError(cs, "Could not find gate " + gatename);
                        }
                    }else{
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            Utils.sendError(cs, "You don'"
                    + "t have permission for this command");
        }
        return true;
    }

}
