
package dk.citygates.entitys;

import dk.citygates.CityGates;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 *
 * @author Steven Hermans
 */
public class BlockHolder implements Animatable{
    
    private int x;
    private int y;
    private int z;
    private String world;
    private String firstMaterial;
    private int firstMeta;
    private String secondMaterial;
    private int secondMeta;
    
    /**
     * Create new BlockHolder
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @param world the world of the block
     * @param fMaterial the block name of the initiale block
     * @param fmeta the meta number of the initiale block
     */
    public BlockHolder(int x, int y, int z, String world, String fMaterial, int fmeta){
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.firstMaterial = fMaterial;
        this.firstMeta = fmeta;
        this.secondMaterial = "AIR";
        this.secondMeta = 0;
    }
    
    /**
     * Create new BlockHolder
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @param world the world of the block
     * @param fMaterial the block name of the initiale block
     * @param fmeta the meta number of the initiale block
     * @param sMaterial the block name of the second state
     * @param smeta the meta number of the second state
     */
    public BlockHolder(int x, int y, int z, String world, String fMaterial, int fmeta, String sMaterial, int smeta){
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.firstMaterial = fMaterial;
        this.firstMeta = fmeta;
        this.secondMaterial = sMaterial;
        this.secondMeta = smeta;
    }

    @Override
    public void open() {
        World w = CityGates.getPlugin().getServer().getWorld(world);
        if(w != null){
            Block block = w.getBlockAt(x, y, z);
            block.setType(Material.getMaterial(secondMaterial));
            block.setData((byte) secondMeta);
        }else{
            throw new java.lang.NullPointerException("Could not find world: " + world);
        }
    }

    @Override
    public void close() {
        World w = CityGates.getPlugin().getServer().getWorld(world);
        if(w != null){
            Block block = w.getBlockAt(x, y, z);
            block.setType(Material.getMaterial(firstMaterial));
            block.setData((byte) firstMeta);
        }else{
            throw new java.lang.NullPointerException("Could not find world: " + world);
        }
    }

    @Override
    public boolean isOpen() {
        World w = CityGates.getPlugin().getServer().getWorld(world);
        if(w != null){
            Block block = w.getBlockAt(x, y, z);
            return !block.getType().equals(Material.getMaterial(firstMaterial));
        }else{
            throw new java.lang.NullPointerException("Could not find world: " + world);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    public int getFirstMeta() {
        return firstMeta;
    }

    public void setFirstMeta(int firstMeta) {
        this.firstMeta = firstMeta;
    }

    public int getSecondMeta() {
        return secondMeta;
    }

    public void setSecondMeta(int secondMeta) {
        this.secondMeta = secondMeta;
    }

    public String getFirstMaterial() {
        return firstMaterial;
    }

    public void setFirstMaterial(String firstMaterial) {
        this.firstMaterial = firstMaterial;
    }

    public String getSecondMaterial() {
        return secondMaterial;
    }

    public void setSecondMaterial(String secondMaterial) {
        this.secondMaterial = secondMaterial;
    }
    
}
