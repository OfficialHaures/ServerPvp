package nl.inferno.serverPvp.arena;

import nl.inferno.serverPvp.ServerPvp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArenaManager {
    private ServerPvp plugin;
    private Map<String, Arena> arenas;

    public ArenaManager(ServerPvp plugin) {
        this.plugin = plugin;
        this.arenas = new HashMap<>();
        loadArenas();
    }

    private void loadArenas() {
        FileConfiguration config = plugin.getConfig();
        if(config.contains("arenas")) {
            for(String arenaName : config.getConfigurationSection("arenas").getKeys(false)) {
                String path = "arenas." + arenaName;

                Location spawn = new Location(
                    Bukkit.getWorld(config.getString(path + ".spawn.world")),
                    config.getDouble(path + ".spawn.x"),
                    config.getDouble(path + ".spawn.y"),
                    config.getDouble(path + ".spawn.z"),
                    (float) config.getDouble(path + ".spawn.yaw"),
                    (float) config.getDouble(path + ".spawn.pitch")
                );

                Arena arena = new Arena(arenaName, spawn);

                for(String spawnKey : config.getConfigurationSection(path + ".playerSpawns").getKeys(false)) {
                    Location playerSpawn = new Location(
                        Bukkit.getWorld(config.getString(path + ".playerSpawns." + spawnKey + ".world")),
                        config.getDouble(path + ".playerSpawns." + spawnKey + ".x"),
                        config.getDouble(path + ".playerSpawns." + spawnKey + ".y"),
                        config.getDouble(path + ".playerSpawns." + spawnKey + ".z")
                    );
                    arena.addPlayerSpawn(playerSpawn);
                }

                for(String goldKey : config.getConfigurationSection(path + ".goldSpawns").getKeys(false)) {
                    Location goldSpawn = new Location(
                        Bukkit.getWorld(config.getString(path + ".goldSpawns." + goldKey + ".world")),
                        config.getDouble(path + ".goldSpawns." + goldKey + ".x"),
                        config.getDouble(path + ".goldSpawns." + goldKey + ".y"),
                        config.getDouble(path + ".goldSpawns." + goldKey + ".z")
                    );
                    arena.addGoldSpawn(goldSpawn);
                }

                arena.setEnabled(config.getBoolean(path + ".enabled", false));
                arenas.put(arenaName, arena);
            }
        }
    }
    public Arena getRandomArena() {
        return arenas.values().stream()
                .filter(Arena::isEnabled)
                .findAny()
                .orElse(null);
    }

    public void createArena(String arenaName, Location location) {
        Arena arena = new Arena(arenaName, location);
        arenas.put(arenaName, arena);
        saveArenaToConfig(arena);
    }

    public void deleteArena(String arenaName) {
        arenas.remove(arenaName);
        plugin.getConfig().set("arenas." + arenaName, null);
        plugin.saveConfig();
    }

    public void setArenaSpawn(String arenaName, Location location) {
        Arena arena = arenas.get(arenaName);

        if(arena != null){
            arena.setSpawn(location);
            saveArenaToConfig(arena);
        }
    }
    private void saveArenaToConfig(Arena arena) {
        String path = "arenas." + arena.getName() + ".";
        Location spawn = arena.getSpawn();

        plugin.getConfig().set(path + "spawn.world", spawn.getWorld().getName());
        plugin.getConfig().set(path + "spawn.x", spawn.getX());
        plugin.getConfig().set(path + "spawn.y", spawn.getY());
        plugin.getConfig().set(path + "spawn.z", spawn.getZ());
        plugin.getConfig().set(path + "spawn.yaw", spawn.getYaw());
        plugin.getConfig().set(path + "spawn.pitch", spawn.getPitch());
        plugin.getConfig().set(path + "enabled", arena.isEnabled());

        plugin.saveConfig();
    }

    public void addGoldSpawn(String arenaName, Location location) {
        Arena arena = arenas.get(arenaName);

        if(arena != null){
            arena.addGoldSpawn(location);

            String path = "arenas." + arenaName + ".goldSpawns";
            List<Location> goldSpawns = arena.getGoldSpawns();
            int spawnIndex = goldSpawns.size() - 1;

            plugin.getConfig().set(path + "." + spawnIndex + ".world", location.getWorld().getName());
            plugin.getConfig().set(path + "." + spawnIndex + ".x", location.getX());
            plugin.getConfig().set(path + "." + spawnIndex + ".y", location.getY());
            plugin.getConfig().set(path + "." + spawnIndex + ".z", location.getZ());

            plugin.saveConfig();
        }
    }

    public void saveArenas() {
        for(Arena arena : arenas.values()) {
            String path = "arenas." + arena.getName() + ".";
            Location spawn = arena.getSpawn();

            plugin.getConfig().set(path + "spawn.world", spawn.getWorld().getName());
            plugin.getConfig().set(path + "spawn.x", spawn.getX());
            plugin.getConfig().set(path + "spawn.y", spawn.getY());
            plugin.getConfig().set(path + "spawn.z", spawn.getZ());
            plugin.getConfig().set(path + "spawn.yaw", spawn.getYaw());
            plugin.getConfig().set(path + "spawn.pitch", spawn.getPitch());

            List<Location> goldSpawns = arena.getGoldSpawns();
            for(int i = 0; i < goldSpawns.size(); i++){
                Location goldSpawn = goldSpawns.get(i);
                plugin.getConfig().set(path + "goldSpawns." + i + ".world", goldSpawn.getWorld().getName());
                plugin.getConfig().set(path + "goldSpawns." + i + ".x", goldSpawn.getX());
                plugin.getConfig().set(path + "goldSpawns." + i + ".y", goldSpawn.getY());
                plugin.getConfig().set(path + "goldSpawns." + i + ".z", goldSpawn.getZ());
            }

            plugin.getConfig().set(path + "enabled", arena.isEnabled());
        }
        plugin.saveConfig();
    }

    public Arena getArena(String arenaName) {
        return arenas.get(arenaName);
    }

    public Arena[] getArenas() {
        return arenas.values().toArray(new Arena[0]);
    }
}
