
package dk.citygates.logic;

import dk.citygates.entitys.AbstractGate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Steven Hermans
 */
public class GateTimer implements Runnable{
    
    private Plugin plugin;
    private Map<String, ArrayList<AbstractGate>> gates;
    private Map<String, Long> oldTime = new HashMap();
    private int sid;
    
    public GateTimer(Plugin plugin){
        this.plugin = plugin;
        gates = new HashMap();
        long tickTime = 20;
        sid = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, tickTime);
        if(sid == -1){
            throw new java.lang.IllegalStateException("Unable to schedule timer task");
        }
    }

    public void addTimer(AbstractGate gate){
        ArrayList<AbstractGate> gts = gates.get(gate.getWorld());
        if(gts == null){
            gts = new ArrayList();
            gates.put(gate.getWorld(), gts);
        }
        gts.add(gate);
    }
    
    public void removeTimer(AbstractGate gate){
        ArrayList<AbstractGate> gts = gates.get(gate.getWorld());
        if(gts != null){
            gts.remove(gate);
        }
    }

    @Override
    public void run() {
        for(World world: plugin.getServer().getWorlds()){
            long time = world.getTime();
            ArrayList<AbstractGate> gts = gates.get(world.getName());
            if(!oldTime.containsKey(world.getName())){
                oldTime.put(world.getName(), time);
            }
            long o = oldTime.get(world.getName());
            oldTime.put(world.getName(), time);
            if(gts != null){
                try{
                    for(AbstractGate gate : gts){
                        doGateLogics(gate, time, o);
                    }
                }catch(java.util.ConcurrentModificationException e){}
            }
        }
    }
    
    private void doGateLogics(AbstractGate gate, long time, long o){
        boolean isOpen = gate.isOpen();
        if(gate.getOpenTime() > gate.getCloseTime()){
            if(time >= gate.getCloseTime() && time < gate.getOpenTime() && (o >= gate.getOpenTime() || o < gate.getCloseTime())){
                gate.close();
            }else if(o >= gate.getCloseTime() && o < gate.getOpenTime() && (time >= gate.getOpenTime() || time < gate.getCloseTime())){
                gate.open();
            }
        }else if(gate.getOpenTime() < gate.getCloseTime()){
            if((time < gate.getOpenTime() || time >= gate.getCloseTime()) && o >= gate.getOpenTime() && o < gate.getCloseTime()){
                gate.close();
            }else if((o < gate.getOpenTime() || o >= gate.getCloseTime()) && time >= gate.getOpenTime() && time < gate.getCloseTime()){
                gate.open();
            }
        }
    }

    public void dispose() {
        gates.clear();
    }
    
    public Map<String, ArrayList<AbstractGate>> getMap(){
        return gates;
    }
    
}
