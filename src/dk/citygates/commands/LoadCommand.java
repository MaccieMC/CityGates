package dk.citygates.commands;

import dk.citygates.logic.GateManager;
import dk.citygates.logic.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Steven Hermans
 */
public class LoadCommand implements CommandExecutor {

    private GateManager manager;
    
    public LoadCommand(GateManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (cs.hasPermission("citygates.admin.load") || cs.isOp()) {
            if(args.length > 0){
                String name = args[0];
                manager.reload(name);
                Utils.sendMessage(cs, "Reload " + name);
            }else{
                manager.reload();
                Utils.sendMessage(cs, "Reload all gates and groups");
            }
            return true;
        } else {
            Utils.sendError(cs, "You don't have permission to use this command");
            return true;
        }
    }
}
