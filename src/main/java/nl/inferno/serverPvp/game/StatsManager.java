package nl.inferno.serverPvp.game;

import nl.inferno.serverPvp.ServerPvp;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatsManager {
    private ServerPvp plugin;
    private File statsFile;
    private FileConfiguration statsConfig;
    private Map<UUID, PlayerStats> playerStats;

    public StatsManager(ServerPvp plugin) {
        this.plugin = plugin;
        this.playerStats = new HashMap<>();
        loadStats();
    }

    private void loadStats() {
        statsFile = new File(plugin.getDataFolder(), "stats.yml");
        if (!statsFile.exists()) {
            plugin.saveResource("stats.yml", false);
        }
        statsConfig = YamlConfiguration.loadConfiguration(statsFile);

        if (statsConfig.isConfigurationSection("players")) {
            ConfigurationSection playersSection = statsConfig.getConfigurationSection("players");
            for (String uuid : playersSection.getKeys(false)) {
                try {
                    UUID playerUUID = UUID.fromString(uuid);
                    String path = "players." + uuid + ".";

                    PlayerStats stats = new PlayerStats(
                            statsConfig.getInt(path + "gamesPlayed"),
                            statsConfig.getInt(path + "wins"),
                            statsConfig.getInt(path + "kills"),
                            statsConfig.getInt(path + "deaths")
                    );

                    playerStats.put(playerUUID, stats);
                } catch (IllegalArgumentException ignored) {

                }
            }
        }
    }

    public void saveStats() {
        for (Map.Entry<UUID, PlayerStats> entry : playerStats.entrySet()) {
            String path = "players." + entry.getKey().toString() + ".";
            PlayerStats stats = entry.getValue();

            statsConfig.set(path + "gamesPlayed", stats.getGamesPlayed());
            statsConfig.set(path + "wins", stats.getWins());
            statsConfig.set(path + "kills", stats.getKills());
            statsConfig.set(path + "deaths", stats.getDeaths());
        }

        try {
            statsConfig.save(statsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save stats file!");
        }
    }

    public int getGamesPlayed(Player player) {
        return getStats(player).getGamesPlayed();
    }

    public int getWins(Player player) {
        return getStats(player).getWins();
    }

    public int getKills(Player player) {
        return getStats(player).getKills();
    }

    public int getDeaths(Player player) {
        return getStats(player).getDeaths();
    }

    public void addGamePlayed(Player player) {
        getStats(player).addGamePlayed();
    }

    public void addWin(Player player) {
        getStats(player).addWin();
    }

    public void addKill(Player player) {
        getStats(player).addKill();
    }

    public void addDeath(Player player) {
        getStats(player).addDeath();
    }

    private PlayerStats getStats(Player player) {
        return playerStats.computeIfAbsent(player.getUniqueId(), k -> new PlayerStats());
    }

    private static class PlayerStats {
        private int gamesPlayed;
        private int wins;
        private int kills;
        private int deaths;

        public PlayerStats() {
            this(0, 0, 0, 0);
        }

        public PlayerStats(int gamesPlayed, int wins, int kills, int deaths) {
            this.gamesPlayed = gamesPlayed;
            this.wins = wins;
            this.kills = kills;
            this.deaths = deaths;
        }

        public int getGamesPlayed() { return gamesPlayed; }
        public int getWins() { return wins; }
        public int getKills() { return kills; }
        public int getDeaths() { return deaths; }

        public void addGamePlayed() { gamesPlayed++; }
        public void addWin() { wins++; }
        public void addKill() { kills++; }
        public void addDeath() { deaths++; }
    }
}
