package nl.martenm.gameoflive.objects;

import com.sun.org.apache.xpath.internal.operations.Bool;
import nl.martenm.gameoflive.GameOfLive;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MartenM
 * @since 13-1-2018.
 */
public class Game {

    private World world;

    private String id;

    private Point origin;
    private BukkitTask task;

    private int height;
    private int width;

    private int updateTime;

    private GameOfLive plugin;
    public Game(GameOfLive plugin, String id, int height, int width){
        this.plugin = plugin;
        this.id = id;
        this.height = height;
        this.width = width;

        this.updateTime = 20;
    }

    public void create(Location loc){
        world = loc.getWorld();
        Point point = new Point(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        point.add(-height / 2, -3, -width / 2);
        origin = point;

        buildBackground(Material.CONCRETE, (byte) 15);
    }

    public void start(){
        if(task != null){
            task.cancel();
        }

        task = new BukkitRunnable(){
            @Override
            public void run() {
                update();
            }
        }.runTaskTimer(plugin, 0, updateTime);
    }

    public void stop(){
        if(task != null){
            task.cancel();
            task = null;
        }
    }

    public void update(){
        Map<Point, Boolean> changes = new HashMap<>();

        // Compute changes
        for(int x = 0; x < height; x++){
            for(int z = 0; z < width; z++ ){
                boolean isAlive = !(world.getBlockAt(origin.getX() + x, origin.getY() + 1, origin.getZ() + z).getType() == Material.AIR);

                // Count alive neighbours
                int aliveNeighbours = 0;
                for(int dx = -1; dx < 2; dx++){
                    for(int dz = -1; dz < 2; dz++){
                        if(dx == 0 && dz == 0){
                            continue;
                        }

                        if(world.getBlockAt(origin.getX() + x + dx, origin.getY() + 1, origin.getZ() + z + dz).getType() != Material.AIR){
                            aliveNeighbours++;
                        }
                    }
                }

                if(isAlive){
                    if(aliveNeighbours > 3 || aliveNeighbours < 2){
                        //DEAD
                        changes.put(new Point(origin.getX() + x, origin.getY() + 1, origin.getZ() + z), false);
                    }
                } else{
                    if(aliveNeighbours == 3){
                        changes.put(new Point(origin.getX() + x, origin.getY() + 1, origin.getZ() + z), true);
                    }
                }
            }
        }

        //Update board

        for(Map.Entry<Point, Boolean> set : changes.entrySet()){
            if(set.getValue()){
                Block block = world.getBlockAt(set.getKey().getX(), set.getKey().getY(), set.getKey().getZ());
                block.setType(Material.CONCRETE);
                world.spawnParticle(Particle.REDSTONE, set.getKey().getX() + 0.5, set.getKey().getY() + 1.2, set.getKey().getZ() + 0.5, 0, 0.001, 1, 0);
            } else{
                Block block = world.getBlockAt(set.getKey().getX(), set.getKey().getY(), set.getKey().getZ());
                block.setType(Material.AIR);
                world.spawnParticle(Particle.REDSTONE, set.getKey().getX() + 0.5, set.getKey().getY() + 1.2, set.getKey().getZ() + 0.5, 0, 1, 0, 0);
            }
        }
    }

    public void buildBackground(Material material, byte magic){
        for(int x = 0; x < height; x++){
            for(int z = 0; z < width; z++ ){
                world.getBlockAt(origin.getX() + x, origin.getY(), origin.getZ() + z).setType(material);
                if(magic > 0) world.getBlockAt(origin.getX() + x, origin.getY(), origin.getZ() + z).setData(magic);
            }
        }
    }

    public void clearTop(){
        for(int x = 0; x < height; x++){
            for(int z = 0; z < width; z++ ){
                world.getBlockAt(origin.getX() + x, origin.getY() + 1, origin.getZ() + z).setType(Material.AIR);
            }
        }
    }

    public String getId() {
        return id;
    }

    public int getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;

        if(task != null){
            start();
        }
    }
}
