
package dk.citygates.commands;

import dk.citygates.entitys.AbstractGate;
import dk.citygates.entitys.Gate;
import dk.citygates.entitys.Group;
import dk.citygates.logic.GateManager;
import dk.citygates.logic.Utils;
import java.util.ArrayList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Steven Hermans
 */
public class ListCommand implements CommandExecutor {

    private GateManager manager;

    public ListCommand(GateManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if(cs.hasPermission("citygates.user.list") || cs.isOp()){
            AbstractGate[] g = manager.getGates();
            ArrayList<Gate> gates = new ArrayList();
            ArrayList<Group> groups = new ArrayList();
            for(AbstractGate ag : g){
                if(ag instanceof Gate){
                    gates.add((Gate) ag);
                }else{
                    groups.add((Group)ag);
                }
            }
            ArrayList<String> output = new ArrayList();
            if(gates.isEmpty()){
                output.add("Gates: <none>");
            }else{
                output.add("Gates: 1 - " + gates.get(0).getName());
                for(int i = 1; i < gates.size(); i++){
                    int j = i+1;
                    output.add("       " + j + " - " + gates.get(i).getName());
                }
            }
            if(groups.isEmpty()){
                output.add("Groups: <none>");
            }else{
                output.add("Groups: 1 - " + groups.get(0).getName());
                for(int i = 1; i < groups.size(); i++){
                    int j = i+1;
                    output.add("        " + j + " - " + groups.get(i).getName());
                }
            }
            String[] sout = new String[output.size()];
            for(int i = 0; i < sout.length; i++){
                sout[i] = output.get(i);
            }
            Utils.sendMessage(cs, sout);
        }else{
            Utils.sendError(cs, "You don't have permission for this command");
        }
        return true;
    }

}
