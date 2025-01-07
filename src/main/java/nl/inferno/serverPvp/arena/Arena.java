package nl.inferno.serverPvp.arena;

import org.bukkit.Location;
import java.util.ArrayList;
import java.util.List;

public class Arena {
    private String name;
    private Location spawn;
    private List<Location> playerSpawns;
    private List<Location> goldSpawns;
    private boolean enabled;

    public Arena(String name, Location spawn) {
        this.name = name;
        this.spawn = spawn;
        this.playerSpawns = new ArrayList<>();
        this.goldSpawns = new ArrayList<>();
        this.enabled = false;
    }

    public void addPlayerSpawn(Location loc) {
        playerSpawns.add(loc);
    }

    public void addGoldSpawn(Location loc) {
        goldSpawns.add(loc);
    }

    public Location getRandomSpawn() {
        return playerSpawns.get((int)(Math.random() * playerSpawns.size()));
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setSpawn(Location location) {
        this.spawn = location;
    }

    public Location getSpawn() {
        return spawn;
    }

    public List<Location> getGoldSpawns() {
        return goldSpawns;
    }
}
