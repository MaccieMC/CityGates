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
public class PermissionCommand implements CommandExecutor {

    private GateManager manager;

    public PermissionCommand(GateManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (cs.hasPermission("citygates.admin.perm") || cs.isOp()) {
            if (args.length >= 3) {
                String name = args[0];
                String type = args[1];
                String bln = args[2];
                AbstractGate gate = manager.getGate(name);
                if(gate != null){
                    boolean b = false;
                    if("true".equalsIgnoreCase(bln) || "1".equalsIgnoreCase(bln)){
                        b = true;
                    }else if("false".equalsIgnoreCase(bln) || "0".equalsIgnoreCase(bln)){
                        b = false;
                    }else{
                        return false;
                    }
                    if("all".equalsIgnoreCase(type)){
                        gate.perm.setAll(b);
                    }else if("open".equalsIgnoreCase(type)){
                        gate.perm.setPermOpen(b);
                    }else if("close".equalsIgnoreCase(type)){
                        gate.perm.setPermClose(b);
                    }else if("kill".equalsIgnoreCase(type)){
                        gate.perm.setPermKill(b);
                    }else if("button".equalsIgnoreCase(type)){
                        gate.perm.setPermButton(b);
                    }else{
                        return false;
                    }
                    Utils.sendMessage(cs, "permission changed for " + name);
                    manager.save(gate);
                    return true;
                }else{
                    Utils.sendError(cs, "Could not find gate " + name);
                    return true;
                }
            } else {
                return false;
            }
        } else {
            Utils.sendError(cs, "You don't have permission to use this command");
            return true;
        }
    }

}
