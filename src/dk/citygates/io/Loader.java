package dk.citygates.io;

import dk.citygates.CityGates;
import dk.citygates.entitys.AbstractGate;
import dk.citygates.entitys.BlockHolder;
import dk.citygates.entitys.Gate;
import dk.citygates.entitys.Group;
import dk.citygates.logic.GateManager;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author Steven Hermans
 */
public class Loader {

    private final File SAVE_FOLDER = new File("plugins/Citygates");
    private final File GATES_FOLDER = new File(SAVE_FOLDER, "gates");
    private final File GROUPS_FOLDER = new File(SAVE_FOLDER, "groups");
    private GateManager manager;

    private Map<String, ArrayList<String>> temp = new HashMap();

    public Loader(GateManager manager) {
        this.manager = manager;
    }

    public void reload() throws IOException {
        temp.clear();
        File[] gateFiles = GATES_FOLDER.listFiles();
        File[] groupFiles = GROUPS_FOLDER.listFiles();
        int gatecount = 0;
        int groupcount = 0;
        if (gateFiles != null) {
            for (File f : gateFiles) {
                try {
                    loadGate(f);
                    gatecount++;
                } catch (Throwable e) {
                    CityGates.getPlugin().getLogger().warning("Failed to load gate " + f.getName().substring(0, f.getName().length() - 4));
                }
            }
        }
        if (groupFiles != null) {
            for (File f : groupFiles) {
                try {
                    loadGroup(f);
                    groupcount++;
                } catch (Throwable e) {
                    CityGates.getPlugin().getLogger().warning("Failed to load group " + f.getName().substring(0, f.getName().length() - 4));
                }
            }
        }
        for (String key : temp.keySet()) {
            manager.loadRelations(key, temp.get(key));
        }
        CityGates.getPlugin().getLogger().info("Succesfully loaded " + gatecount + " gate(s) and " + groupcount + " group(s)");
        temp.clear();
    }

    public void reload(String name) throws IOException {
        manager.dispose();
        temp.clear();
        File gateFile = new File(GATES_FOLDER, name + ".yml");
        if (gateFile.exists()) {
            try {
                loadGate(gateFile);
                //CityGates.getPlugin().getLogger().info("Succesfully loaded gate " + name);
            } catch (Throwable e) {
                CityGates.getPlugin().getLogger().warning("Failed to load gate " + name);
            }
        } else {
            File groupFile = new File(GROUPS_FOLDER, name + ".yml");
            if (groupFile.exists()) {
                try {
                    loadGroup(groupFile);
                    //CityGates.getPlugin().getLogger().info("Succesfully loaded group " + name);
                } catch (Throwable e) {
                    CityGates.getPlugin().getLogger().warning("Failed to load group " + name);
                }
            } else {
                CityGates.getPlugin().getLogger().warning("Could not find gate or group with name: " + name);
            }
        }
        for (String key : temp.keySet()) {
            manager.loadRelations(key, temp.get(key));
        }
        temp.clear();
    }

    private void loadGate(File file) throws IOException {
        //CityGates.getPlugin().getLogger().info("Load gate [" +file.getName() + "]");
        Yaml yaml = new Yaml();
        Map<String, Object> map = yaml.loadAs(new FileReader(file), Map.class);
        Gate gate = new Gate();
        gate.setName(file.getName().substring(0, file.getName().length() - 4));
        loadAbstract(map, gate);

        Object value = map.get("blocks");
        if (value != null) {
            if (value instanceof ArrayList) {
                ArrayList<Map> maps = (ArrayList) value;
                ArrayList<BlockHolder> bhs = new ArrayList();
                for (Map<String, Object> bmap : maps) {
                    String trace = gate.getName() + ".yml \'blocks\' \'";
                    int blockx = parseInt(bmap.get("x"), trace + "x\' > ");
                    int blocky = parseInt(bmap.get("y"), trace + "y\' > ");
                    int blockz = parseInt(bmap.get("z"), trace + "z\' > ");
                    String fm = parseString(bmap.get("fmaterial"), trace + "fmaterial\' > ");
                    int fmeta = parseInt(bmap.get("fmeta"), trace + "fmeta\' > ");
                    String sm = parseString(bmap.get("smaterial"), trace + "smaterial\' > ");
                    int smeta = parseInt(bmap.get("smeta"), trace + "smeta\' > ");
                    BlockHolder bh = new BlockHolder(blockx, blocky, blockz, gate.getWorld(), fm, fmeta, sm, smeta);
                    bhs.add(bh);
                }
                gate.initBlocks(bhs, gate.getWorld());
            } else {
                System.out.println("Block is " + value.getClass().getName());
            }
        }
        manager.load(gate);
    }

    private void loadGroup(File file) throws IOException {
       // CityGates.getPlugin().getLogger().info("Load group [" +file.getName() + "]");
        Yaml yaml = new Yaml();
        Map<String, Object> map = yaml.loadAs(new FileReader(file), Map.class);
        Group group = new Group();
        group.setName(file.getName().substring(0, file.getName().length() - 4));
        loadAbstract(map, group);

        long t = parseLong(map.get("delay"), group.getName() + ".yml \'delay\' > ");
        if (t > -1) {
            group.setDelay(t);
        }
        Object obj = map.get("children");
        if (obj != null) {
            if (obj instanceof ArrayList) {
                ArrayList<String> childs = (ArrayList) obj;
                temp.put(group.getName(), childs);
            }
        }
        manager.load(group);
    }

    private void loadAbstract(Map<String, Object> map, AbstractGate gate) {
        String trace = gate.getName() + ".yml \'";

        gate.setWorld(parseString(map.get("world"), trace + "world\' > "));
        gate.setProtect(parseBoolean(map.get("protect"), trace + "protect\' > "));
        gate.setTimeGate(parseBoolean(map.get("timegate"), trace + "timegate\' > "));

        long t = parseLong(map.get("opentime"), trace + "opentime\' > ");
        if (t > -1) {
            gate.setOpenTime(t);
        }

        t = parseLong(map.get("closetime"), trace + "closetime\' > ");
        if (t > -1) {
            gate.setCloseTime(t);
        }

        t = parseLong(map.get("closedelay"), trace + "closedelay\' > ");
        if (t > -1) {
            gate.setCloseDelay(t);
        }

        gate.setEntityType(parseString(map.get("entitytype"), trace + "entitytype\' > "));
        gate.setKillMsg(parseString(map.get("killmsg"), trace + "killmsg\' > "));
        gate.setKillArea1(parseLocation(map.get("kill_loc1"), gate.getWorld(), trace + "kill_loc1\' > "));
        gate.setKillArea2(parseLocation(map.get("kill_loc2"), gate.getWorld(), trace + "kill_loc2\' > "));
        gate.setButtonLocations(parseLocations(map.get("buttonlocations"), gate.getWorld(), trace + "buttonlocations\' > "));
        gate.setRedstoneLocations(parseLocations(map.get("redstonelocations"), gate.getWorld(), trace + "redstonelocations\' > "));
        gate.perm.setPermOpen(parseBoolean(map.get("permopen"), trace + "permopen\' > "));
        gate.perm.setPermClose(parseBoolean(map.get("permclose"), trace + "permclose\' > "));
        gate.perm.setPermButton(parseBoolean(map.get("permbutton"), trace + "permbutton\' > "));
        gate.perm.setPermKill(parseBoolean(map.get("permkill"), trace + "permkill\' > "));
    }

    private boolean parseBoolean(Object obj, String trace) {
        if (obj == null) {
            //System.out.println(trace + " Not found");
            return false;
        }
        try {
            return (boolean) obj;
        } catch (Throwable e) {
            //System.out.println(trace + " Invalid value " + obj + " [true/false]");
            return false;
        }
    }

    private String parseString(Object obj, String trace) {
        if (obj == null) {
            //System.out.println(trace + " Not found");
            return null;
        }
        try {
            return (String) obj;
        } catch (Throwable e) {
            //System.out.println(trace + " Invalid value " + obj + " [String]");
            return null;
        }
    }

    private int parseInt(Object obj, String trace) {
        if (obj == null) {
            //System.out.println(trace + " Not found");
            return 0;
        } else {
            try {
                return (int) obj;
            } catch (Throwable e) {
                e.printStackTrace();
                //System.out.println(trace + " Invalid value " + obj + " [Number]");
                return 0;
            }
        }
    }

    private long parseLong(Object obj, String trace) {
        if (obj == null) {
            //System.out.println(trace + " Not found");
            return -1;
        } else {
            try {
                return (int) obj;
            } catch (Throwable e) {
                e.printStackTrace();
                //System.out.println(trace + " Invalid value " + obj + " [Number]");
                return -1;
            }
        }
    }

    private Location parseLocation(Object obj, String world, String trace) {
        if (obj == null) {
            return null;
        }
        try {
            String str = obj.toString();
            String[] split = str.split(",");
            if (split.length == 3) {
                Location loc = new Location(CityGates.getPlugin().getServer().getWorld(world), Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                return loc;
            } else if (split.length == 4) {
                Location loc = new Location(CityGates.getPlugin().getServer().getWorld(split[3]), Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                return loc;
            } else {
                return null;
            }
        } catch (Throwable e) {
            return null;
        }
    }

    private ArrayList<Location> parseLocations(Object obj, String world, String trace) {
        ArrayList<Location> locs = new ArrayList();
        if (obj != null) {
            if (obj instanceof ArrayList) {
                ArrayList<String> str = (ArrayList<String>) obj;
                for (String s : str) {
                    Location loc = parseLocation(s, world, trace);
                    if (loc != null) {
                        locs.add(loc);
                    }
                }
            } else {
                //System.out.println(trace + " Invalid value " + obj + " [Array]");
            }
        } else {
            //System.out.println(trace + " Not found");
        }
        return locs;
    }

}
