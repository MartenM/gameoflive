package nl.martenm.gameoflive.objects;

import nl.martenm.gameoflive.GameOfLive;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MartenM
 * @since 14-1-2018.
 */
public class Game3D extends Game {

    private int depth;

    public Game3D(GameOfLive plugin, String id, int height, int width, int depth) {
        super(plugin, id, height, width);
        this.depth = depth;
    }

    @Override
    public void update(){
        new BukkitRunnable(){
            @Override
            public void run() {
                Map<Point, Boolean> changes = new HashMap<>();

                // Compute changes
                for(int x = 0; x < getHeight(); x++){
                    for(int z = 0; z < getWidth(); z++ ) {
                        for (int y = 0; y < depth; y++) {
                            boolean isAlive = !(getWorld().getBlockAt(getOrigin().getX() + x, getOrigin().getY() + 1 + y, getOrigin().getZ() + z).getType() == Material.AIR);

                            // Count alive neighbours
                            int aliveNeighbours = 0;
                            for (int dx = -1; dx < 2; dx++) {
                                for (int dz = -1; dz < 2; dz++) {
                                    for (int dy = -1; dy < 2; dy++) {
                                        if (dx == 0 && dz == 0 && dy == 0) {
                                            continue;
                                        }

                                        if(getOrigin().getY() + y + dy + 1 <= getOrigin().getY()){
                                            continue;
                                        }

                                        if (getWorld().getBlockAt(getOrigin().getX() + x + dx, getOrigin().getY() + y + dy + 1, getOrigin().getZ() + z + dz).getType() != Material.AIR) {
                                            aliveNeighbours++;
                                        }
                                    }
                                }
                            }

                            if (isAlive) {
                                if (aliveNeighbours > 8 || aliveNeighbours < 2) {
                                    //DEAD
                                    changes.put(new Point(getOrigin().getX() + x, getOrigin().getY() + 1 + y, getOrigin().getZ() + z), false);
                                }
                            } else {
                                if (aliveNeighbours == 5) {
                                    changes.put(new Point(getOrigin().getX() + x, getOrigin().getY() + 1 + y, getOrigin().getZ() + z), true);
                                }
                            }
                        }
                    }
                }

                //Update board

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for(Map.Entry<Point, Boolean> set : changes.entrySet()){
                            if(set.getValue()){
                                Block block = getWorld().getBlockAt(set.getKey().getX(), set.getKey().getY(), set.getKey().getZ());
                                block.setType(Material.CONCRETE);
                                block.setData((byte) ((set.getKey().getY() % 14 + set.getKey().getZ() % 14 + set.getKey().getX() % 14) % 14));
                                getWorld().spawnParticle(Particle.REDSTONE, set.getKey().getX() + 0.5, set.getKey().getY() + 1.2, set.getKey().getZ() + 0.5, 0, 0.001, 1, 0);
                            } else{
                                Block block = getWorld().getBlockAt(set.getKey().getX(), set.getKey().getY(), set.getKey().getZ());
                                block.setType(Material.AIR);
                                getWorld().spawnParticle(Particle.REDSTONE, set.getKey().getX() + 0.5, set.getKey().getY() + 1.2, set.getKey().getZ() + 0.5, 0, 1, 0, 0);
                            }
                        }
                    }
                }.runTask(getPlugin());
            }
        }.runTaskAsynchronously(getPlugin());
    }

    @Override
    public void clearTop() {
        for (int x = 0; x < getHeight(); x++) {
            for (int z = 0; z < getWidth(); z++) {
                for (int y = 0; y < depth; y++) {
                    getWorld().getBlockAt(getOrigin().getX() + x, getOrigin().getY() + 1 + y, getOrigin().getZ() + z).setType(Material.AIR);
                }
            }
        }
    }
}
