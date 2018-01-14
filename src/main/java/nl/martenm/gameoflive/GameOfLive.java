package nl.martenm.gameoflive;

import nl.martenm.gameoflive.commands.LiveCommand;
import nl.martenm.gameoflive.objects.Game;
import nl.martenm.gameoflive.objects.GamesManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MartenM
 * @since 13-1-2018.
 */
public class GameOfLive extends JavaPlugin {

    private GamesManager gamesManager;

    @Override
    public void onEnable() {
        getLogger().info("Registering commands...");
        registerCommands();

        getLogger().info("Registering events...");
        registerEvents();

        gamesManager = new GamesManager(this);
    }

    @Override
    public void onDisable() {
        gamesManager.removeAllGames();
    }

    public void registerCommands(){
        getCommand("gameoflive").setExecutor(new LiveCommand(this));
    }

    public void registerEvents(){

    }

    public GamesManager getGamesManager() {
        return gamesManager;
    }
}
