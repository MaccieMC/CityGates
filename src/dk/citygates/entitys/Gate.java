package dk.citygates.entitys;

import dk.citygates.CityGates;
import java.util.ArrayList;

/**
 *
 * @author Steven Hermans
 */
public class Gate extends AbstractGate {

    private ArrayList<BlockHolder> blocks;

    private static final int MAX_BLOCK_SIZE = 10;

    public Gate() {
        blocks = new ArrayList();
    }

    public void initBlocks(ArrayList<BlockHolder> blocks, String world) {
        this.blocks = blocks;
        setWorld(world);
    }

    @Override
    public void open() {
        super.open();
        int offset = 0;
        int count = 0;
        final ArrayList<ArrayList<BlockHolder>> subarray = new ArrayList();
        while (offset < blocks.size()) {
            subarray.add(new ArrayList());
            for (int i = offset; i < offset + MAX_BLOCK_SIZE; i++) {
                if (i < blocks.size()) {
                    subarray.get(count).add(blocks.get(i));
                } else {
                    break;
                }
            }
            offset += MAX_BLOCK_SIZE;
            count++;
        }
        CityGates.getPlugin().getServer().getScheduler().runTask(CityGates.getPlugin(), new Runnable() {
            private int chunk = 0;
            private int tick = 0;

            public void run() {
                tick++;
                long starttime = System.currentTimeMillis();
                for (int i = chunk; i < subarray.size(); i++) {
                    ArrayList<BlockHolder> bhs = subarray.get(i);
                    for (BlockHolder bh : bhs) {
                        bh.open();
                    }
                    chunk++;
                    long dif = System.currentTimeMillis() - starttime;
                    if (dif > 10) {
                        
                        CityGates.getPlugin().getServer().getScheduler().runTask(CityGates.getPlugin(), this);
                        return;
                    }
                }
                if(tick > 10){
                    CityGates.getPlugin().getLogger().info("Took " + tick + " ticks to close " + Gate.this.getName());
                }
            }
        });
    }

    @Override
    public void close() {
        super.close();
        int offset = 0;
        int count = 0;
        final ArrayList<ArrayList<BlockHolder>> subarray = new ArrayList();
        while (offset < blocks.size()) {
            subarray.add(new ArrayList());
            for (int i = offset; i < offset + MAX_BLOCK_SIZE; i++) {
                if (i < blocks.size()) {
                    subarray.get(count).add(blocks.get(i));
                } else {
                    break;
                }
            }
            offset += MAX_BLOCK_SIZE;
            count++;
        }
        CityGates.getPlugin().getServer().getScheduler().runTask(CityGates.getPlugin(), new Runnable() {
            private int chunk = 0;
            private int tick = 0;

            public void run() {
                tick++;
                long starttime = System.currentTimeMillis();
                for (int i = chunk; i < subarray.size(); i++) {
                    ArrayList<BlockHolder> bhs = subarray.get(i);
                    for (BlockHolder bh : bhs) {
                        bh.close();
                    }
                    chunk++;
                    long dif = System.currentTimeMillis() - starttime;
                    if (dif > 10) {
                        CityGates.getPlugin().getServer().getScheduler().runTaskLater(CityGates.getPlugin(), this, 1);
                        return;
                    }
                }
                if(tick > 10){
                    CityGates.getPlugin().getLogger().info("Took " + tick + " ticks to open " + Gate.this.getName());
                }
            }
        });
    }

    @Override
    public boolean isOpen() {
        for (BlockHolder bh : blocks) {
            if (bh.isOpen()) {
                return true;
            }
        }
        return false;
    }

    public BlockHolder[] getBlocks() {
        BlockHolder[] blks = new BlockHolder[blocks.size()];
        for (int i = 0; i < blks.length; i++) {
            blks[i] = blocks.get(i);
        }
        return blks;
    }

}
