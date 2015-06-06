
package dk.citygates;

import dk.citygates.commands.*;
import dk.citygates.logic.ButtonListener;
import dk.citygates.logic.GateManager;
import dk.citygates.logic.GateTimer;
import dk.citygates.logic.GriefListener;
import dk.citygates.logic.KillListener;
import dk.citygates.logic.PointerManager;
import dk.citygates.logic.RedstoneListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Steven Hermans
 */
public class CityGates extends JavaPlugin{
    
    private static CityGates plugin;
    
    private PointerManager pointer;
    private GateManager manager;
    private GateTimer timer;
    private ButtonListener buttonListener;
    private KillListener killListener;
    private RedstoneListener redstoneListener;
    private GriefListener griefListener;
    
    public void onEnable(){
        plugin = this;
        
        pointer = new PointerManager();
        timer = new GateTimer(this);
        buttonListener = new ButtonListener();
        killListener = new KillListener();
        redstoneListener = new RedstoneListener();
        griefListener = new GriefListener();
        manager = new GateManager(timer, buttonListener, killListener, redstoneListener);
        
        registerListeners();
        registerCommands();
        manager.reload();
    }
    
    private void registerListeners(){
        this.getServer().getPluginManager().registerEvents(buttonListener, this);
        this.getServer().getPluginManager().registerEvents(killListener, this);
        this.getServer().getPluginManager().registerEvents(redstoneListener, this);
        this.getServer().getPluginManager().registerEvents(griefListener, this);
    }
    
    /**
     * register commands actions
     */
    private void registerCommands(){
        this.getCommand("gcreate").setExecutor(new CreateCommand(pointer, manager));
        this.getCommand("gsetanimation").setExecutor(new ConvertCommand(manager));
        this.getCommand("gchangeblocks").setExecutor(new BlockChangeCommand(manager));
        this.getCommand("gchangeblock").setExecutor(new BlockChangeCommand(manager));
        this.getCommand("gp1").setExecutor(new SelectionCommand(pointer));
        this.getCommand("gp2").setExecutor(new SelectionCommand(pointer));
        this.getCommand("gshow").setExecutor(new ShowCommand(pointer));
        this.getCommand("gopen").setExecutor(new OpenCommand(manager));
        this.getCommand("gclose").setExecutor(new CloseCommand(manager));
        this.getCommand("glist").setExecutor(new ListCommand(manager));
        this.getCommand("ginfo").setExecutor(new InfoCommand(manager));
        this.getCommand("gdelete").setExecutor(new DeleteCommand(manager));
        this.getCommand("ggroup").setExecutor(new GroupCommands(manager));
        this.getCommand("gperm").setExecutor(new PermissionCommand(manager));
        this.getCommand("gprotect").setExecutor(new ProtectCommand(manager));
        this.getCommand("gtimer").setExecutor(new TimerCommands(manager, timer));
        this.getCommand("gbutton").setExecutor(new ButtonCommands(manager, buttonListener));
        this.getCommand("gredstone").setExecutor(new RedstoneCommands(manager, redstoneListener));
        this.getCommand("gkill").setExecutor(new KillCommands(manager, killListener, pointer));
        this.getCommand("gsave").setExecutor(new SaveCommand(manager));
        this.getCommand("greload").setExecutor(new LoadCommand(manager));
        this.getCommand("gdebug").setExecutor(new DebugCommand(manager));
    }
    
    public void onDisable(){
        manager.save();
        manager.dispose();
    }
    
    public static CityGates getPlugin(){
        return plugin;
    }
    
    public GateManager getManager(){
        return manager;
    }
    
}
