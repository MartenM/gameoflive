package nl.martenm.gameoflive.commands;

import nl.martenm.gameoflive.GameOfLive;
import nl.martenm.gameoflive.objects.Game;
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
            try{
                height = Integer.parseInt(args[2]);
                width = Integer.parseInt(args[3]);
            } catch (NumberFormatException ex){
                sender.sendMessage(ChatColor.RED + "Those are not numbers >:|");
                return true;
            }

            Location loc = player.getLocation();
            Game game = new Game(plugin, args[1], height, width);
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

                case "remove":
                case "delete":
                    plugin.getGamesManager().removeById(game.getId());
                    sender.sendMessage(ChatColor.GREEN + "Successfully removed the arena with ID: " + ChatColor.WHITE + game.getId());
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
