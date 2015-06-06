package dk.citygates.io;

import dk.citygates.CityGates;
import dk.citygates.entitys.AbstractGate;
import dk.citygates.entitys.BlockHolder;
import dk.citygates.entitys.Gate;
import dk.citygates.entitys.Group;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author Steven Hermans
 */
public class Saver {

    private final File SAVE_FOLDER = new File("plugins/Citygates");
    private final File GATES_FOLDER = new File(SAVE_FOLDER, "gates");
    private final File GROUPS_FOLDER = new File(SAVE_FOLDER, "groups");

    /**
     * Save gatedata into json file
     *
     * @param gates
     */
    public void save(Map<String, AbstractGate> gates) throws IOException {
        int gatecount = 0;
        int groupcount = 0;
        
        for (String name : gates.keySet()) {
            AbstractGate gate = gates.get(name);
            try {
                if(save(gate)){
                    if(gate instanceof Gate){
                        gatecount++;
                    }
                    if(gate instanceof Group){
                        groupcount++;
                    }
                }
            } catch (Throwable e) {
                CityGates.getPlugin().getLogger().warning("Failed to save " + (gate instanceof Gate ? "gate" : "group") + name);
                CityGates.getPlugin().getLogger().throwing("citygates.io.Saver", "save", e);
            }
        }

        File[] files = GATES_FOLDER.listFiles();
        if (files != null) {
            for (File file : files) {
                String name = file.getName().substring(0, file.getName().length() - 4);
                AbstractGate g = gates.get(name);
                if (g != null) {
                    if (!(g instanceof Gate)) {
                        file.delete();
                    }
                } else {
                    file.delete();
                }
            }
        }
        files = GROUPS_FOLDER.listFiles();
        if (files != null) {
            for (File file : files) {
                String name = file.getName().substring(0, file.getName().length() - 4);
                AbstractGate g = gates.get(name);
                if (g != null) {
                    if (!(g instanceof Group)) {
                        file.delete();
                    }
                } else {
                    file.delete();
                }
            }
        }
        CityGates.getPlugin().getLogger().info("Saved " + gatecount + " gate(s) and " + groupcount + " group(s)");
    }

    public boolean save(AbstractGate gate) throws IOException {
        //CityGates.getPlugin().getLogger().info("Save " + (gate instanceof Gate ? "gate" : "group") + " [" + gate.getName() + ".yml]");
        if (!SAVE_FOLDER.isFile()) {
            SAVE_FOLDER.delete();
            SAVE_FOLDER.mkdirs();
        }
        if (!GATES_FOLDER.isFile()) {
            GATES_FOLDER.delete();
            GATES_FOLDER.mkdirs();
        }
        if (!GROUPS_FOLDER.isFile()) {
            GROUPS_FOLDER.delete();
            GROUPS_FOLDER.mkdirs();
        }

        Yaml yml = new Yaml();
        Map<String, Object> obj = new HashMap();

        obj.put("world", gate.getWorld());
        obj.put("protect", gate.isProtect());
        obj.put("timegate", gate.isTimeGate());
        obj.put("opentime", gate.getOpenTime());
        obj.put("closetime", gate.getCloseTime());
        obj.put("closedelay", gate.getCloseDelay());
        if (gate.getEntityType() != null) {
            obj.put("entitytype", gate.getEntityType());
        }
        if (gate.getKillMsg() != null) {
            obj.put("killmsg", gate.getKillMsg());
        }
        String[] locs = new String[gate.getButtonLocations().size()];
        for (int i = 0; i < gate.getButtonLocations().size(); i++) {
            locs[i] = (parseLocation(gate.getButtonLocations().get(i)));
        }
        obj.put("buttonlocations", locs);
        String[] rlocs = new String[gate.getRedstoneLocations().size()];
        for (int i = 0; i < gate.getRedstoneLocations().size(); i++) {
            rlocs[i] = (parseLocation(gate.getRedstoneLocations().get(i)));
        }
        obj.put("redstonelocations", rlocs);
        if (gate.getKillArea1() != null) {
            obj.put("kill_loc1", parseLocation(gate.getKillArea1()));
        }
        if (gate.getKillArea2() != null) {
            obj.put("kill_loc2", parseLocation(gate.getKillArea2()));
        }

        if (gate.perm.isPermOpen()) {
            obj.put("permopen", true);
        }
        if (gate.perm.isPermClose()) {
            obj.put("permclose", true);
        }
        if (gate.perm.isPermButton()) {
            obj.put("permbutton", true);
        }
        if (gate.perm.isPermKill()) {
            obj.put("permkill", true);
        }

        if (gate instanceof Gate) {
            Gate g = (Gate) gate;
            BlockHolder[] bhs = g.getBlocks();
            Map[] maps = new Map[bhs.length];
            for (int i = 0; i < bhs.length; i++) {
                BlockHolder block = bhs[i];
                Map<String, Object> bobj = new HashMap();
                bobj.put("x", block.getX());
                bobj.put("y", block.getY());
                bobj.put("z", block.getZ());
                bobj.put("fmaterial", block.getFirstMaterial());
                bobj.put("fmeta", block.getFirstMeta());
                bobj.put("smaterial", block.getSecondMaterial());
                bobj.put("smeta", block.getSecondMeta());
                maps[i] = bobj;
            }
            obj.put("blocks", maps);
        } else if (gate instanceof Group) {
            Group group = (Group) gate;
            obj.put("delay", group.getDelay());
            String[] childs = new String[group.getGates().length];
            for (int i = 0; i < childs.length; i++) {
                childs[i] = group.getGates()[i].getName();
            }
            obj.put("children", childs);
        } else {
            new java.lang.IllegalStateException("Unknown type AbstractGate: " + gate).printStackTrace();
        }
        String output = yml.dump(obj);

        File saveFile = new File((gate instanceof Gate ? GATES_FOLDER : GROUPS_FOLDER), gate.getName() + ".yml");
        //backup file
        File backupFile = new File(saveFile.getAbsolutePath() + ".tmp");
        boolean madeBackup = false;
        if (saveFile.exists() && saveFile.isFile()) {
            backupFile.delete();
            if (saveFile.renameTo(backupFile)) {
                madeBackup = true;
            }
        }
        if (saveFile.isDirectory()) {
            if (!saveFile.delete()) {
                throw new java.io.IOException("Unable to save -> savefile is directory: " + saveFile.getAbsolutePath());
            }
        }

        try {
            FileWriter writer = new FileWriter(saveFile);
            writer.write(output);
            writer.flush();
            writer.close();
            backupFile.delete();
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            if (madeBackup) {
                saveFile.delete();
                if (backupFile.renameTo(saveFile)) {
                    System.out.println("Backup restored");
                } else {
                    System.out.println("Could not restore backup");
                }
            }
            return false;
        }
    }

    private String parseLocation(Location loc) {
        if (loc.getWorld() == null) {
            return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
        } else {
            return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getWorld().getName();
        }
    }

}
