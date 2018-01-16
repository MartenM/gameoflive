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
public class GameFrames extends Game {

    private int maxFrames;
    private int currentFrame;

    public GameFrames(GameOfLive plugin, String id, int height, int width, int maxFrames) {
        super(plugin, id, height, width);
        this.maxFrames = maxFrames;
    }

    public void start(){
        if(task != null){
            task.cancel();
        }

        task = new BukkitRunnable(){
            @Override
            public void run() {
                if(currentFrame >= maxFrames){
                    this.cancel();
                    return;
                }
                update();
                currentFrame++;
            }
        }.runTaskTimer(getPlugin(), 0, getUpdateTime());
    }

    @Override
    public void update(){
        Map<Point, Boolean> changes = new HashMap<>();

        // Compute changes
        for(int x = 0; x < getHeight(); x++){
            for(int z = 0; z < getWidth(); z++ ){
                boolean isAlive = !(getWorld().getBlockAt(getOrigin().getX() + x, getOrigin().getY() + 1 + currentFrame, getOrigin().getZ() + z).getType() == Material.AIR);

                // Count alive neighbours
                int aliveNeighbours = 0;
                for(int dx = -1; dx < 2; dx++){
                    for(int dz = -1; dz < 2; dz++){
                        if(dx == 0 && dz == 0){
                            continue;
                        }

                        if(getWorld().getBlockAt(getOrigin().getX() + x + dx, getOrigin().getY() + 1 + currentFrame, getOrigin().getZ() + z + dz).getType() != Material.AIR){
                            aliveNeighbours++;
                        }
                    }
                }

                if(isAlive){
                    if(aliveNeighbours > 3 || aliveNeighbours < 2){
                        //Dies so don't place again
                    } else{
                        //Stays alive
                        changes.put(new Point(getOrigin().getX() + x, getOrigin().getY() + 1 + currentFrame, getOrigin().getZ() + z), true);
                    }

                } else{
                    if(aliveNeighbours == 3){
                        changes.put(new Point(getOrigin().getX() + x, getOrigin().getY() + 1 + currentFrame, getOrigin().getZ() + z), true);
                    }
                }
            }
        }

        //Update board
        if(changes.size() == 0){
            for(int x = 0; x < getHeight(); x++){
                for(int z = 0; z < getWidth(); z++ ) {
                    for (int y = 0; y <= currentFrame; y++) {
                        getWorld().spawnParticle(Particle.VILLAGER_ANGRY, getOrigin().getX() + 0.5, getOrigin().getY() + 1.2 + 1 + currentFrame,getOrigin().getZ() + 0.5, 0, 0.001, 1, 0);
                    }
                }
            }
            stop();
            return;
        }

        for(Map.Entry<Point, Boolean> set : changes.entrySet()){
            if(set.getValue()){
                Block block = getWorld().getBlockAt(set.getKey().getX(), set.getKey().getY() + 1, set.getKey().getZ());
                block.setType(Material.CONCRETE);
                getWorld().spawnParticle(Particle.REDSTONE, set.getKey().getX() + 0.5, set.getKey().getY() + 1.2 + 1, set.getKey().getZ() + 0.5, 0, 0.001, 1, 0);
            } else{
                Block block = getWorld().getBlockAt(set.getKey().getX(), set.getKey().getY() + 1, set.getKey().getZ());
                block.setType(Material.AIR);
                getWorld().spawnParticle(Particle.REDSTONE, set.getKey().getX() + 0.5, set.getKey().getY() + 1.2 + 1, set.getKey().getZ() + 0.5, 0, 1, 0, 0);
            }
        }
    }

    public void clearTop(){
        for(int x = 0; x < getHeight(); x++){
            for(int z = 0; z < getWidth(); z++ ) {
                for (int y = 0; y <= currentFrame; y++) {
                    getWorld().getBlockAt(getOrigin().getX() + x, getOrigin().getY() + 1 + y, getOrigin().getZ() + z).setType(Material.AIR);
                }
            }
        }
        currentFrame = 0;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    public int getMaxFrames() {
        return maxFrames;
    }

    public void setMaxFrames(int maxFrames) {
        this.maxFrames = maxFrames;
    }
}
