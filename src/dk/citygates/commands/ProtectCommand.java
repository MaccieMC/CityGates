
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
public class ProtectCommand implements CommandExecutor {

    private GateManager manager;

    public ProtectCommand(GateManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (cs.hasPermission("citygates.admin.protect") || cs.isOp()) {
            if(args.length == 2){
                String gatename = args[0];
                boolean state = Boolean.parseBoolean(args[1]);
                AbstractGate gate = manager.getGate(gatename);
                if(gate != null){
                    if(state != gate.isProtect()){
                    gate.setProtect(state);
                    if(state){
                        Utils.sendMessage(cs, gatename + " is now protected");
                    }else{
                        Utils.sendMessage(cs, gatename + " is no longer protected");
                    }
                    }else{
                        if(state){
                            Utils.sendError(cs, gatename + " is already protected");
                        }else{
                            Utils.sendMessage(cs, gatename + " is no longer protected");
                        }
                    }
                }else{
                    Utils.sendError(cs, "Could not find gate " + gatename);
                }
            }else{
                return false;
            }
        }else{
            Utils.sendError(cs, "You don't have permission to change protection state");
        }
        return true;
    }

}
