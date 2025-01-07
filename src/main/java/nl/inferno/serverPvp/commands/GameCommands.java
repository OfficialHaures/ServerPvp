package nl.inferno.serverPvp.commands;

import nl.inferno.serverPvp.ServerPvp;
import nl.inferno.serverPvp.game.GameState;
import nl.inferno.serverPvp.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCommands implements CommandExecutor {
    private ServerPvp plugin;

    public GameCommands(ServerPvp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if(command.getName().equalsIgnoreCase("murdermystery")) {
            if(args.length == 0) {
                sendHelpMessage(player);
                return true;
            }

            switch(args[0].toLowerCase()) {
                case "join":
                    handleJoinCommand(player, args);
                    break;

                case "leave":
                    handleLeaveCommand(player);
                    break;

                case "start":
                    handleStartCommand(player);
                    break;

                case "arena":
                    handleArenaCommand(player, args);
                    break;

                case "list":
                    handleListCommand(player);
                    break;

                case "stats":
                    handleStatsCommand(player);
                    break;

                default:
                    sendHelpMessage(player);
                    break;
            }
            return true;
        }
        return false;
    }

    private void handleJoinCommand(Player player, String[] args) {
        if(plugin.getGameManager().isPlayerInGame(player)) {
            player.sendMessage(ChatColor.RED + "You are already in a game!");
            return;
        }

        if(args.length > 1) {
            String arenaName = args[1];
            Arena arena = plugin.getArenaManager().getArena(arenaName);
            if(arena != null && arena.isEnabled()) {
                plugin.getGameManager().addPlayer(player, arena);
                player.sendMessage(ChatColor.GREEN + "You joined arena: " + arenaName);
            } else {
                player.sendMessage(ChatColor.RED + "Arena not found or disabled!");
            }
        } else {
            Arena randomArena = plugin.getArenaManager().getRandomArena();
            if(randomArena != null) {
                plugin.getGameManager().addPlayer(player, randomArena);
                player.sendMessage(ChatColor.GREEN + "You joined a random arena!");
            } else {
                player.sendMessage(ChatColor.RED + "No available arenas found!");
            }
        }
    }

    private void handleLeaveCommand(Player player) {
        if(plugin.getGameManager().isPlayerInGame(player)) {
            plugin.getGameManager().removePlayer(player);
            player.sendMessage(ChatColor.YELLOW + "You left the game!");
        } else {
            player.sendMessage(ChatColor.RED + "You are not in a game!");
        }
    }

    private void handleStartCommand(Player player) {
        if(!player.hasPermission("murdermystery.admin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission!");
            return;
        }

        if(plugin.getGameManager().getGameState() == GameState.WAITING) {
            plugin.getGameManager().startGame();
            player.sendMessage(ChatColor.GREEN + "Game started!");
        } else {
            player.sendMessage(ChatColor.RED + "Cannot start game right now!");
        }
    }

    private void handleArenaCommand(Player player, String[] args) {
        if(!player.hasPermission("murdermystery.admin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission!");
            return;
        }

        if(args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /mm arena <create|delete|setspawn> <name>");
            return;
        }

        String action = args[1];
        String arenaName = args[2];

        switch(action.toLowerCase()) {
            case "create":
                plugin.getArenaManager().createArena(arenaName, player.getLocation());
                player.sendMessage(ChatColor.GREEN + "Arena " + arenaName + " created!");
                break;

            case "delete":
                plugin.getArenaManager().deleteArena(arenaName);
                player.sendMessage(ChatColor.GREEN + "Arena " + arenaName + " deleted!");
                break;

            case "setspawn":
                plugin.getArenaManager().setArenaSpawn(arenaName, player.getLocation());
                player.sendMessage(ChatColor.GREEN + "Arena spawn set!");
                break;

            case "addgold":
                plugin.getArenaManager().addGoldSpawn(arenaName, player.getLocation());
                player.sendMessage(ChatColor.GREEN + "Gold spawn added!");
                break;
        }
    }

    private void handleListCommand(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Available Arenas ===");
        for(Arena arena : plugin.getArenaManager().getArenas()) {
            String status = arena.isEnabled() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled";
            player.sendMessage(ChatColor.YELLOW + arena.getName() + " - " + status);
        }
    }

    private void handleStatsCommand(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Your Stats ===");
        player.sendMessage(ChatColor.YELLOW + "Games Played: " + plugin.getStatsManager().getGamesPlayed(player));
        player.sendMessage(ChatColor.YELLOW + "Wins: " + plugin.getStatsManager().getWins(player));
        player.sendMessage(ChatColor.YELLOW + "Kills: " + plugin.getStatsManager().getKills(player));
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Murder Mystery Commands ===");
        player.sendMessage(ChatColor.YELLOW + "/mm join [arena] " + ChatColor.WHITE + "- Join a game");
        player.sendMessage(ChatColor.YELLOW + "/mm leave " + ChatColor.WHITE + "- Leave the game");
        player.sendMessage(ChatColor.YELLOW + "/mm list " + ChatColor.WHITE + "- List available arenas");
        player.sendMessage(ChatColor.YELLOW + "/mm stats " + ChatColor.WHITE + "- View your stats");

        if(player.hasPermission("murdermystery.admin")) {
            player.sendMessage(ChatColor.GOLD + "=== Admin Commands ===");
            player.sendMessage(ChatColor.YELLOW + "/mm start " + ChatColor.WHITE + "- Force start the game");
            player.sendMessage(ChatColor.YELLOW + "/mm arena create <name> " + ChatColor.WHITE + "- Create arena");
            player.sendMessage(ChatColor.YELLOW + "/mm arena delete <name> " + ChatColor.WHITE + "- Delete arena");
            player.sendMessage(ChatColor.YELLOW + "/mm arena setspawn <name> " + ChatColor.WHITE + "- Set arena spawn");
            player.sendMessage(ChatColor.YELLOW + "/mm arena addgold <name> " + ChatColor.WHITE + "- Add gold spawn");
        }
    }
}