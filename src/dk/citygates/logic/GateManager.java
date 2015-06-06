package dk.citygates.logic;

import dk.citygates.CityGates;
import dk.citygates.entitys.AbstractGate;
import dk.citygates.entitys.BlockHolder;
import dk.citygates.entitys.Direction;
import dk.citygates.entitys.Gate;
import dk.citygates.entitys.Group;
import dk.citygates.io.Loader;
import dk.citygates.io.Saver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;

/**
 *
 * @author Steven Hermans
 */
public class GateManager {

    private Map<String, AbstractGate> gates;
    private Saver saver;
    private Loader loader;

    private final GateTimer timer;
    private final ButtonListener buttonListener;
    private final KillListener killListener;
    private final RedstoneListener redstoneListener;

    /**
     * Create new GateManager
     */
    public GateManager(GateTimer timer, ButtonListener buttonListener, KillListener killListener, RedstoneListener redstoneListener) {
        gates = new HashMap();
        saver = new Saver();
        loader = new Loader(this);
        this.timer = timer;
        this.buttonListener = buttonListener;
        this.killListener = killListener;
        this.redstoneListener = redstoneListener;
    }

    public void dispose() {
        gates.clear();
        buttonListener.dispose();
        timer.dispose();
        killListener.dispose();
        redstoneListener.dispose();
    }

    public void unload(AbstractGate gate) {
        gates.remove(gate.getName().toLowerCase());
        if (gate.isTimeGate()) {
            timer.removeTimer(gate);
        }
        buttonListener.removeListener(gate);
        redstoneListener.removeListener(gate);
        if (gate.getEntityType() != null) {
            killListener.removeGate(gate);
        }
        for (String key : gates.keySet()) {
            AbstractGate g = gates.get(key);
            if (g instanceof Group) {
                Group group = (Group) g;
                group.removeGate(gate);
            }
        }
    }

    public void load(AbstractGate gate) {
        gates.put(gate.getName().toLowerCase(), gate);
        if (gate.isTimeGate()) {
            timer.addTimer(gate);
        }
        for (Location loc : gate.getButtonLocations()) {
            buttonListener.addListener(loc, gate);
        }
        for (Location loc : gate.getRedstoneLocations()) {
            redstoneListener.addListener(loc, gate);
        }
        if (gate.getEntityType() != null) {
            killListener.addGate(gate);
        }
    }

    public void loadRelations(String name, ArrayList<String> childs) {
        AbstractGate gate = gates.get(name);
        if (gate != null) {
            if (gate instanceof Group) {
                Group group = (Group) gate;
                for (String str : childs) {
                    AbstractGate child = gates.get(str);
                    if (child != null) {
                        group.addGate(child);
                    }
                }
            }
        }
    }

    /**
     * Save gate or group
     */
    public void save(AbstractGate gate) {
        try {
            saver.save(gate);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Save all gates and groups
     */
    public void save() {
        try {
            saver.save(gates);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public void saveGroup(AbstractGate gate) {
        save(gate);
        if(gate instanceof Group){
            Group group = (Group) gate;
            for(AbstractGate child : group.getGates()){
                saveGroup(child);
            }
        }
    }

    /**
     * Reload gate or group from config
     */
    public void reload(String name) {
        try {
            loader.reload(name);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Reload all gates and groups from config
     */
    public void reload() {
        try {
            loader.reload();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Register a gate with name
     *
     * @param gate An AbstractGate that has a name
     */
    public void addGate(AbstractGate gate) {
        if (gate.getName() == null) {
            throw new java.lang.NullPointerException("Cannot register gate without name");
        }
        gates.put(gate.getName().toLowerCase(), gate);
    }

    /**
     * Remove a gate from memory
     *
     * @param gate Remove a gate with name
     */
    public void removeGate(AbstractGate gate) {
        gate.close();
        gates.remove(gate.getName());
        timer.removeTimer(gate);
        buttonListener.removeListener(gate);
        redstoneListener.removeListener(gate);
        killListener.removeGate(gate);
        for (String name : gates.keySet()) {
            AbstractGate ag = gates.get(name);
            if (ag instanceof Group) {
                Group group = (Group) ag;
                group.removeGate(gate);
            }
        }
    }

    /**
     * Create a new gate from a name and block locations
     *
     * @param name The name of the gate
     * @param locations The locations of it blocks
     * @return The gate that is created
     */
    public Gate createGate(String name, ArrayList<Location> locations) {
        if (gates.get(name) != null) {
            throw new java.lang.IllegalStateException("Gate " + name + " already exists");
        }
        if (locations.isEmpty()) {
            throw new java.lang.IllegalStateException("No locations");
        }
        Gate gate = new Gate();
        gate.setName(name.toLowerCase());
        addGate(gate);
        ArrayList<BlockHolder> blocks = new ArrayList();
        for (Location loc : locations) {
            BlockHolder bh = new BlockHolder(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName(), loc.getBlock().getType().name(), loc.getBlock().getData());
            blocks.add(bh);
        }
        gate.initBlocks(blocks, blocks.get(0).getWorld());
        return gate;
    }

    /**
     * Create an animation gate
     *
     * @param name The name of the gate
     * @param locations the locations of it blocks
     * @param dir The direction the gate opens
     * @return A group of al levels of the gate
     */
    public Group createGate(String name, ArrayList<Location> locations, Direction dir) {
        if (gates.get(name) != null) {
            throw new java.lang.IllegalStateException("Gate " + name + " already exists");
        }
        if (locations.isEmpty()) {
            throw new java.lang.IllegalStateException("No locations");
        }

        HashMap<Integer, ArrayList<Location>> levels = Utils.sortLevels(locations, dir);
        ArrayList<ArrayList<Location>> listed = Utils.listLevels(levels, dir.ordinal() % 2 != 0);

        Group group = new Group();
        group.setName(name.toLowerCase());
        group.setDelay(20);
        int index = 1;
        for (ArrayList<Location> level : listed) {
            while (gates.get(name.toLowerCase() + "_" + index) != null) {
                index++;
            }
            Gate gate = createGate(name.toLowerCase() + "_" + index, level);
            group.addGate(gate);
        }
        gates.put(name.toLowerCase(), group);
        return group;
    }

    public void convertGate(Gate gate, Direction dir) {
        BlockHolder[] blocks = gate.getBlocks();
        Map<Location, BlockHolder> map = new HashMap();
        for (BlockHolder block : blocks) {
            map.put(new Location(CityGates.getPlugin().getServer().getWorld(gate.getWorld()), block.getX(), block.getY(), block.getZ()), block);
        }
        HashMap<Integer, ArrayList<Location>> levels = Utils.sortLevels(map.keySet(), dir);
        ArrayList<ArrayList<Location>> listed = Utils.listLevels(levels, dir.ordinal() % 2 != 0);

        Group group = new Group();
        group.perm = gate.perm;
        group.setName(gate.getName());
        group.setWorld(gate.getWorld());
        group.setDelay(20);
        group.setButtonLocations(gate.getButtonLocations());
        group.setRedstoneLocations(gate.getRedstoneLocations());
        group.setCloseDelay(gate.getCloseDelay());
        group.setCloseTime(gate.getCloseTime());
        group.setOpenTime(gate.getOpenTime());
        group.setTimeGate(gate.isTimeGate());
        group.setEntityType(gate.getEntityType());
        group.setKillArea1(gate.getKillArea1());
        group.setKillArea2(gate.getKillArea2());
        group.setKillMsg(gate.getKillMsg());

        for (int i = 0; i < listed.size(); i++) {
            ArrayList<Location> lvl = listed.get(i);
            Gate child = new Gate();
            int j = i;
            while (gates.containsKey(gate.getName() + "_" + j)) {
                j++;
            }
            child.setName(gate.getName().toLowerCase() + "_" + i);
            ArrayList<BlockHolder> bhs = new ArrayList();
            for (Location loc : lvl) {
                bhs.add(map.get(loc));
            }
            child.initBlocks(bhs, gate.getWorld());
            group.addGate(child);
            gates.put(child.getName().toLowerCase(), child);
        }

        unload(gate);
        load(group);
    }

    public void changeBlocks(AbstractGate g, boolean openstate) {
        if (g instanceof Gate) {
            Gate gate = (Gate) g;
            BlockHolder[] blocks = gate.getBlocks();
            for (BlockHolder block : blocks) {
                Location loc = new Location(CityGates.getPlugin().getServer().getWorld(block.getWorld()), block.getX(), block.getY(), block.getZ());
                if (openstate) {
                    block.setSecondMaterial(loc.getBlock().getType().name());
                    block.setSecondMeta(loc.getBlock().getData());
                } else {
                    block.setFirstMaterial(loc.getBlock().getType().name());
                    block.setFirstMeta(loc.getBlock().getData());
                }
            }
        }else{
            Group group = (Group) g;
            for(AbstractGate child : group.getGates()){
                changeBlocks(child, openstate);
            }
        }
    }

    public AbstractGate getGate(String name) {
        return gates.get(name.toLowerCase());
    }

    public AbstractGate[] getGates() {
        AbstractGate[] g = new AbstractGate[gates.size()];
        int i = 0;
        for (String key : gates.keySet()) {
            g[i] = gates.get(key);
            i++;
        }
        return g;
    }

    public boolean createGroup(String name) {
        if (gates.get(name) == null) {
            Group group = new Group();
            group.setName(name.toLowerCase());
            gates.put(name.toLowerCase(), group);
            return true;
        } else {
            return false;
        }
    }

    public void deleteGroup(String name) {
        AbstractGate gate = gates.get(name.toLowerCase());
        if (gate != null) {
            if (gate instanceof Group) {
                Group group = (Group) gate;
                for (AbstractGate g : group.getGates()) {
                    group.removeGate(g);
                }
                removeGate(group);
            }
        }
    }

    public RedstoneListener getRedstoneListener() {
        return redstoneListener;
    }

    public ButtonListener getButtonListener() {
        return buttonListener;
    }

    public KillListener getKillListener() {
        return killListener;
    }

    public GateTimer getGateTimer() {
        return timer;
    }

}
