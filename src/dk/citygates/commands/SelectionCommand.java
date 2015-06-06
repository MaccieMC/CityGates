
package dk.citygates.commands;

import dk.citygates.logic.PointerManager;
import dk.citygates.logic.Utils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Steven Hermans
 */
public class SelectionCommand  implements CommandExecutor {

    private PointerManager pointer;

    public SelectionCommand(PointerManager pointer) {
        this.pointer = pointer;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (cs.hasPermission("citygates.admin.create") || cs.isOp()) {
            if(cs instanceof Player){
                Player player = (Player) cs;
                Block block = player.getTargetBlock(null, 50);
                if(block != null){
                    Location loc = block.getLocation();
                    if(cmnd.getName().equalsIgnoreCase("gp1")){
                        pointer.setPoint1(player.getPlayerListName(), loc);
                        pointer.showPoint1(player.getPlayerListName());
                        Utils.sendMessage(cs, "Point 1 selected");
                    }else if(cmnd.getName().equalsIgnoreCase("gp2")){
                        pointer.setPoint2(player.getPlayerListName(), loc);
                        pointer.showPoint2(player.getPlayerListName());
                        Utils.sendMessage(cs, "Point 2 selected");
                    }else{
                        return false;
                    }
                }else{
                    Utils.sendError(cs, "You have to look at a block to make a selection");
                }
            }else{
                Utils.sendError(cs, "Only a player can execute this command");
            }
            return true;
        }else{
            Utils.sendError(cs, "You don't have permission to make a selection");
            return true;
        }
    }
}
