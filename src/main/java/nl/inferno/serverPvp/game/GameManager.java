package nl.inferno.serverPvp.game;

import nl.inferno.serverPvp.ServerPvp;
import nl.inferno.serverPvp.arena.Arena;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GameManager {
    private ServerPvp plugin;
    private GameState gameState;
    private Map<UUID, PlayerRole> playerRoles;
    private List<Player> alivePlayers;
    private List<Location> goldSpawns;
    private int countdown;
    private int gameTime;
    private Map<UUID, Integer> playersGold = new HashMap<>();

    public GameManager(ServerPvp plugin) {
        this.plugin = plugin;
        this.playerRoles = new HashMap<>();
        this.alivePlayers = new ArrayList<>();
        this.goldSpawns = new ArrayList<>();
        this.gameState = GameState.WAITING;
        this.countdown = 30;
        this.gameTime = 300; // 5 minutes
        startGoldSpawning();
    }

    private void startGoldSpawning() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameState == GameState.PLAYING) {
                    spawnGold();
                }
            }
        }.runTaskTimer(plugin, 0L, 200L); // Every 10 seconds
    }

    private void spawnGold() {
        for (Location loc : goldSpawns) {
            loc.getWorld().dropItem(loc, new ItemStack(Material.GOLD_INGOT));
        }
    }

    public void startGame() {
        if (alivePlayers.size() >= 8) {
            gameState = GameState.STARTING;
            startCountdown();
        }
    }

    private void startCountdown() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (countdown > 0) {
                    broadcastMessage("Game starting in " + countdown + " seconds!");
                    countdown--;
                } else {
                    cancel();
                    initializeGame();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void initializeGame() {
        gameState = GameState.PLAYING;
        assignRoles();
        distributeItems();
        startGameTimer();
    }
    private void startGameTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameTime > 0) {
                    gameTime--;
                    for(Player player : alivePlayers) {
                        plugin.getScoreboardManager().updateScoreboard(player);
                    }
                } else {
                    endGame(GameEndReason.TIME_UP);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }


    private void distributeItems() {
        for (Player player : alivePlayers) {
            PlayerRole role = playerRoles.get(player.getUniqueId());
            clearInventory(player);

            switch (role) {
                case MURDERER:
                    giveMurdererItems(player);
                    break;
                case DETECTIVE:
                    giveDetectiveItems(player);
                    break;
                case INNOCENT:
                    // Innocents start with nothing
                    break;
            }
        }
    }

    private void giveMurdererItems(Player player) {
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Murder Knife");
        sword.setItemMeta(meta);
        player.getInventory().addItem(sword);
    }

    private void giveDetectiveItems(Player player) {
        ItemStack bow = new ItemStack(Material.BOW);
        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName(ChatColor.BLUE + "Detective Bow");
        bow.setItemMeta(bowMeta);
        player.getInventory().addItem(bow);
        player.getInventory().addItem(arrow);
    }

    public void handlePlayerDeath(Player victim, Player killer) {
        alivePlayers.remove(victim);
        PlayerRole victimRole = playerRoles.get(victim.getUniqueId());

        if (victimRole == PlayerRole.DETECTIVE) {
            Location bowDrop = victim.getLocation();
            bowDrop.getWorld().dropItem(bowDrop, new ItemStack(Material.BOW));
        }

        checkGameEnd();
    }

    private void checkGameEnd() {
        int innocentCount = 0;
        Player murderer = null;

        for (Player player : alivePlayers) {
            PlayerRole role = playerRoles.get(player.getUniqueId());
            if (role == PlayerRole.MURDERER) {
                murderer = player;
            } else {
                innocentCount++;
            }
        }

        if (murderer == null) {
            endGame(GameEndReason.INNOCENTS_WIN);
        } else if (innocentCount == 0) {
            endGame(GameEndReason.MURDERER_WINS);
        }
    }

    private void endGame(GameEndReason reason) {
        gameState = GameState.ENDING;
        String message = "";

        switch (reason) {
            case MURDERER_WINS:
                message = "§c§lThe Murderer has won the game!";
                break;
            case INNOCENTS_WIN:
                message = "§a§lThe Innocents have won the game!";
                break;
            case TIME_UP:
                message = "§e§lTime's up! Innocents win!";
                break;
        }

        broadcastMessage(message);
        resetGame();
    }

    private void resetGame() {
        playerRoles.clear();
        alivePlayers.clear();
        countdown = 30;
        gameTime = 300;
        gameState = GameState.WAITING;
    }

    private void broadcastMessage(String message) {
        for (Player player : alivePlayers) {
            player.sendMessage(message);
        }
    }

    private void clearInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }

    // Getters and utility methods
    public GameState getGameState() {
        return gameState;
    }

    public PlayerRole getPlayerRole(Player player) {
        return playerRoles.get(player.getUniqueId());
    }

    public void addPlayer(Player player, Arena arena) {
        if(gameState == GameState.WAITING && !isPlayerInGame(player)){
            alivePlayers.add(player);
            player.teleport(arena.getSpawn());
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);

            if(alivePlayers.size() >= 8){
                startGame();
            }
        }
    }

    private Location getWaitingSpawn() {
        // Replace with actual waiting spawn location
        return new Location(Bukkit.getWorld("world"), 0, 64, 0);
    }

    private void assignRoles(){
        Random random = new Random();
        List<Player> players = new ArrayList<>(alivePlayers);
        Player murderer = players.remove(random.nextInt(players.size()));
        playerRoles.put(murderer.getUniqueId(), PlayerRole.MURDERER);

        Player detective = players.remove(random.nextInt(players.size()));
        playerRoles.put(detective.getUniqueId(), PlayerRole.DETECTIVE);

        for(Player player : players){
            playerRoles.put(player.getUniqueId(), PlayerRole.INNOCENT);
        }
    }

    public int addGoldToPlayer(Player player) {
        UUID playerId = player.getUniqueId();
        int currentGold = playersGold.getOrDefault(playerId, 0) + 1;
        playersGold.put(playerId, currentGold);
        return currentGold;
    }

    public void resetPlayerGold(Player player) {
        playersGold.put(player.getUniqueId(), 0);
    }

    public boolean isPlayerInGame(Player player) {
        return alivePlayers.contains(player);
    }

    public void removePlayer(Player player) {
        alivePlayers.remove(player);
        playerRoles.remove(player.getUniqueId());
        playersGold.remove(player.getUniqueId());
        player.getInventory().clear();
        player.teleport(player.getWorld().getSpawnLocation());

        if(gameState == GameState.PLAYING) {
            checkGameEnd();
        }
    }
}