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
public class SaveCommand implements CommandExecutor {

    private GateManager manager;

    public SaveCommand(GateManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (cs.hasPermission("citygates.admin.save") || cs.isOp()) {
            if(args.length > 0){
                String name = args[0];
                AbstractGate gate = manager.getGate(name);
                if(gate != null){
                    manager.save(gate);
                    Utils.sendMessage(cs, "Save " + name);
                }else{
                    Utils.sendError(cs, "Could not find gate " + name);
                }
            }else{
                manager.save();
                Utils.sendMessage(cs, "Save all gates and groups");
            }
            return true;
        } else {
            Utils.sendError(cs, "You don't have permission to use this command");
            return true;
        }
    }
}
