package nl.martenm.gameoflive.commands;

import nl.martenm.gameoflive.GameOfLive;
import nl.martenm.gameoflive.objects.Game;
import nl.martenm.gameoflive.objects.Game3D;
import nl.martenm.gameoflive.objects.GameFrames;
import nl.martenm.gameoflive.objects.Point;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author MartenM
 * @since 13-1-2018.
 */
public class LiveCommand implements CommandExecutor {

    private GameOfLive plugin;
    public LiveCommand(GameOfLive plugin){
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "Sorry, player only command...");
            return true;
        }

        if(!sender.hasPermission("gameoflive.admin")){
            sender.sendMessage(ChatColor.RED + "How did you even know this command existed???");
            return true;
        }

        Player player = (Player) sender;

        if(args.length < 1){
            sender.sendMessage(ChatColor.RED + "Valid arguments: " + ChatColor.WHITE + "create, game");
            return true;
        }

        if(args[0].equalsIgnoreCase("create")){
            if(args.length < 4){
                sender.sendMessage(ChatColor.RED + "Valid arguments: " + ChatColor.WHITE + "/gol create height width");
                return true;
            }

            int height;
            int width;
            int depth = 0;
            try{
                height = Integer.parseInt(args[2]);
                width = Integer.parseInt(args[3]);

                if(args.length == 5){
                    depth = Integer.parseInt(args[4]);
                }
            } catch (NumberFormatException ex){
                sender.sendMessage(ChatColor.RED + "Those are not numbers >:|");
                return true;
            }

            Location loc = player.getLocation();
            Game game = null;
            if(args.length == 5){
                game = new Game3D(plugin, args[1], height, width, depth);
            } else if(args.length > 5) game = new GameFrames(plugin, args[1], height, width, depth);
            else game = new Game(plugin, args[1], height, width);

            if(!plugin.getGamesManager().add(game)){
                sender.sendMessage(ChatColor.RED + "Already a game with such an id!");
                return true;
            }

            game.create(loc);

            sender.sendMessage(ChatColor.GREEN + "Creating game of live..");
            return true;
        }

        else if(args[0].equalsIgnoreCase("game")){
            if(args.length < 3){
                sender.sendMessage(ChatColor.RED + "Valid arguments: " + ChatColor.WHITE + "/gol game <id> <start/stop/time/delete>");
                return true;
            }

            Game game = plugin.getGamesManager().getGameById(args[1]);
            System.out.println(args[1] + "  ");
            if(game == null){
                sender.sendMessage(ChatColor.RED + "Could not find a game with that ID!");
                return true;
            }

            switch (args[2]) {
                case "start":
                    sender.sendMessage(ChatColor.GREEN + "Starting Game Of Live for arena: " + ChatColor.WHITE + game.getId());
                    game.start();
                    break;

                case "stop":
                    sender.sendMessage(ChatColor.YELLOW + "Stopping Game Of Live for arena: " + ChatColor.WHITE + game.getId());
                    game.stop();
                    break;

                case "time":
                    try {
                        game.setUpdateTime(Integer.parseInt(args[3]));
                        sender.sendMessage(ChatColor.GREEN + "Successfully set the update time for arena: " + ChatColor.WHITE + game.getId());
                    } catch (Exception ex) {
                        sender.sendMessage(ChatColor.RED + "Those are not numbers >:|");
                    }
                    break;

                case "clear":
                    game.clearTop();
                    sender.sendMessage(ChatColor.GREEN + "Successfully cleared the top of arena: " + ChatColor.WHITE + game.getId());
                    break;

                case "remove":
                case "delete":
                    plugin.getGamesManager().removeById(game.getId());
                    sender.sendMessage(ChatColor.GREEN + "Successfully removed the arena with ID: " + ChatColor.WHITE + game.getId());
                    break;

                case "maxframes":
                    if(!(game instanceof GameFrames)){
                        sender.sendMessage(ChatColor.RED + "This can only be used on games that have this option :P");
                        break;
                    }

                    GameFrames gameFrames = (GameFrames) game;
                    try {
                        gameFrames.setMaxFrames(Integer.parseInt(args[3]));
                    } catch (Exception ex) {
                        sender.sendMessage(ChatColor.RED + "No number given or not even a valid one :3");
                    }
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "Valid arguments: " + ChatColor.WHITE + "/gol game <id> <start/stop/time/delete>");
                    break;
            }
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Valid arguments: " + ChatColor.WHITE + "create");
        return true;
    }
}
