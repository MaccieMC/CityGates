
package dk.citygates.entitys;

import dk.citygates.CityGates;
import java.util.ArrayList;

/**
 *
 * @author Steven Hermans
 */
public class Group extends AbstractGate{
    
    private ArrayList<AbstractGate> gates;
    private long delay = 0;
    private int taskId = -1;
    
    public Group(){
        gates = new ArrayList();
    }

    @Override
    public void open() {
        super.open();
        if(taskId > -1){
            CityGates.getPlugin().getServer().getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        taskId = CityGates.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(CityGates.getPlugin(), new Runnable() {
            private int tick = 0;
            @Override
            public void run() {
                if(tick >= gates.size()){
                    CityGates.getPlugin().getServer().getScheduler().cancelTask(taskId);
                }else{
                    if(delay > 0){
                        AbstractGate gate;
                        while((gate = gates.get(tick)).isOpen()){
                            tick++;
                            if(tick >= gates.size()){
                                return;
                            }
                        }
                        gate.open();
                        tick++;
                    }else{
                        for(AbstractGate gate : gates){
                            gate.open();
                        }
                        CityGates.getPlugin().getServer().getScheduler().cancelTask(taskId);
                    }
                }
            }
        }, 0, delay);
    }

    @Override
    public void close() {
        super.close();
        if(taskId > -1){
            CityGates.getPlugin().getServer().getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        taskId = CityGates.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(CityGates.getPlugin(), new Runnable() {
            private int tick = gates.size()-1;
            @Override
            public void run() {
                if(tick < 0){
                    CityGates.getPlugin().getServer().getScheduler().cancelTask(taskId);
                }else{
                    if(delay > 0){
                        AbstractGate gate;
                        while(!(gate = gates.get(tick)).isOpen()){
                            tick--;
                            if(tick < 0){
                                return;
                            }
                        }
                        gate.close();
                        tick--;
                    }else{
                        for(AbstractGate gate : gates){
                            gate.close();
                        }
                        CityGates.getPlugin().getServer().getScheduler().cancelTask(taskId);
                    }
                }
            }
        }, 0, delay);
    }

    @Override
    public boolean isOpen() {
        for (AbstractGate gate : gates) {
            if(gate.isOpen()){
                return true;
            }
        }
        return false;
    }
    
    public void addGate(AbstractGate gate){
        gates.add(gate);
        if(getWorld() == null){
            setWorld(gate.getWorld());
        }
    }
    
    public void removeGate(AbstractGate gate){
        gates.remove(gate);
    }
    
    public AbstractGate[] getGates(){
        AbstractGate[] childs = new AbstractGate[gates.size()];
        for(int i = 0; i < childs.length; i++){
            childs[i] = gates.get(i);
        }
        return childs;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

}
