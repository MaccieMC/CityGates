package dk.citygates.commands;

import dk.citygates.logic.PointerManager;
import dk.citygates.logic.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Steven Hermans
 */
public class ShowCommand implements CommandExecutor {

    private PointerManager pointer;

    public ShowCommand(PointerManager pointer) {
        this.pointer = pointer;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (cs.hasPermission("citygates.admin.create") || cs.isOp()) {
            if (cs instanceof Player) {
                Player player = (Player) cs;
                String name = player.getPlayerListName();
                if (pointer.hasSelection(name)) {
                    Location p1 = pointer.getPoint1(name);
                    Location p2 = pointer.getPoint2(name);
                    if (p1.getWorld().equals(p2.getWorld())) {
                        pointer.showSelection(name);
                    } else {
                        Utils.sendError(cs, "You need to make a selection first: use /gp1 and /gp2");
                    }
                } else {
                    Utils.sendError(cs, "You need to make a selection first: use /gp1 and /gp2");
                }
            } else {
                Utils.sendError(cs, "Only a player can execute this command");
            }
            return true;
        } else {
            Utils.sendError(cs, "You don't have permission to make a selection");
            return true;
        }
    }
}
