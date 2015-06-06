
package dk.citygates.entitys;

import dk.citygates.CityGates;
import java.util.ArrayList;
import org.bukkit.Location;

/**
 *
 * @author Steven Hermans
 */
public abstract class AbstractGate implements Animatable{

    private String world;
    private String name;
    public Permissions perm = new Permissions();
    private boolean protect = true;
    
    private boolean timeGate = false;
    private long openTime = 23000;
    private long closeTime = 13000;
    
    private long closeDelay = 5000;
    private ArrayList<Location> buttonLocations = new ArrayList();
    private ArrayList<Location> redstoneLocations = new ArrayList();
    
    private String entityType;
    private String killMsg;
    private Location killArea1;
    private Location killArea2;
    
    //runtime vars
    private int taskId = -1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the time to open the gate
     * @return Open time
     */
    public long getOpenTime() {
        return openTime;
    }

    /**
     * Get the time to close the gate
     * @return Close time
     */
    public long getCloseTime() {
        return closeTime;
    }
    
    /**
     * Set the time to open the gate
     * @param opentime Open time
     */
    public void setOpenTime(long opentime){
        this.openTime = opentime;
    }
    
    /**
     * Set the time to close the gate
     * @param closetime Close time
     */
    public void setCloseTime(long closetime){
        this.closeTime = closetime;
    }

    @Override
    public String getWorld() {
        return world;
    }
    
    public void setWorld(String name){
        world = name;
    }

    public boolean isTimeGate() {
        return timeGate;
    }

    public void setTimeGate(boolean isTimeGate) {
        this.timeGate = isTimeGate;
    }

    public long getCloseDelay() {
        return closeDelay;
    }

    public void setCloseDelay(long closeDelay) {
        this.closeDelay = closeDelay;
    }

    public ArrayList<Location> getButtonLocations() {
        return buttonLocations;
    }

    public void setButtonLocations(ArrayList<Location> buttonLocations) {
        this.buttonLocations = buttonLocations;
    }

    public ArrayList<Location> getRedstoneLocations() {
        return redstoneLocations;
    }

    public void setRedstoneLocations(ArrayList<Location> redstoneLocations) {
        this.redstoneLocations = redstoneLocations;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getKillMsg() {
        return killMsg;
    }

    public void setKillMsg(String killMsg) {
        this.killMsg = killMsg;
    }

    public Location getKillArea1() {
        return killArea1;
    }

    public void setKillArea1(Location killArea1) {
        this.killArea1 = killArea1;
    }

    public Location getKillArea2() {
        return killArea2;
    }

    public void setKillArea2(Location killArea2) {
        this.killArea2 = killArea2;
    }

    public boolean isProtect() {
        return protect;
    }

    public void setProtect(boolean protect) {
        this.protect = protect;
    }
    
    public void open(){
        if(taskId > -1){
            CityGates.getPlugin().getServer().getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }
    
    public void close(){
        if(taskId > -1){
            CityGates.getPlugin().getServer().getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }
    
    public void closeDelay(long delay){
        if(taskId > -1){
            CityGates.getPlugin().getServer().getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        taskId = CityGates.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(CityGates.getPlugin(), new Runnable(){

            @Override
            public void run() {
                close();
            }
        }, delay/50);
    }
    
    public void openDelay(long delay){
        if(taskId > -1){
            CityGates.getPlugin().getServer().getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        taskId = CityGates.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(CityGates.getPlugin(), new Runnable(){

            @Override
            public void run() {
                open();
            }
        }, delay/50);
    }

}
