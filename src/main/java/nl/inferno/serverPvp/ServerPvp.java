package nl.inferno.serverPvp;

import nl.inferno.serverPvp.arena.ArenaManager;
import nl.inferno.serverPvp.commands.GameCommands;
import nl.inferno.serverPvp.game.GameManager;
import nl.inferno.serverPvp.game.StatsManager;
import nl.inferno.serverPvp.listeners.GameListener;
import nl.inferno.serverPvp.scoreboard.GameScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ServerPvp extends JavaPlugin {

    private static ServerPvp instance;
    private GameManager gameManager;
    private ArenaManager arenaManager;
    private StatsManager statsManager;
    private GameScoreboard scoreboardManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize managers
        this.gameManager = new GameManager(this);
        this.arenaManager = new ArenaManager(this);
        this.statsManager = new StatsManager(this);
        this.scoreboardManager = new GameScoreboard();

        // Register events
        Bukkit.getPluginManager().registerEvents(new GameListener(this), this);

        // Register commands
        getCommand("murdermystery").setExecutor(new GameCommands(this));

        // Load configuration
        saveDefaultConfig();

        getLogger().info("Murder Mystery has been enabled!");
    }

    @Override
    public void onDisable() {
        // Save all data
        arenaManager.saveArenas();
        statsManager.saveStats();

        getLogger().info("Murder Mystery has been disabled!");
    }

    public static ServerPvp getInstance() {
        return instance;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public StatsManager getStatsManager() {
        return statsManager;
    }

    public GameScoreboard getScoreboardManager() {
        return scoreboardManager;
    }
}