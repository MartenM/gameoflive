package nl.martenm.gameoflive.objects;

import nl.martenm.gameoflive.GameOfLive;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MartenM
 * @since 13-1-2018.
 */
public class GamesManager {

    private List<Game> games = new ArrayList<>();

    private GameOfLive plugin;
    public GamesManager(GameOfLive plugin){
        this.plugin = plugin;
    }

    public boolean add(Game game){
        if(getGameById(game.getId()) != null){
            return false;
        }
        games.add(game);
        return true;
    }

    public Game getGameById(String id){
        for(Game game : games){
            if(game.getId().equalsIgnoreCase(id)){
                return game;
            }
        }
        return null;
    }

    public void removeAllGames(){
        for(Game game : games){
            game.buildBackground(Material.AIR, (byte) -1);
            game.clearTop();
        }
        games.clear();
    }

    public void removeById(String id){
        Game game = getGameById(id);
        if(game == null) return;

        game.stop();
        game.clearTop();
        game.buildBackground(Material.AIR, (byte) -1);
    }
}
