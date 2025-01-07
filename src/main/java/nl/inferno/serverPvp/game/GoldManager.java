package nl.inferno.serverPvp.game;

import nl.inferno.serverPvp.ServerPvp;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GoldManager {
    private List<Location> spawnPoints;
    private ServerPvp plugin;
    private Random random;

    public GoldManager(ServerPvp plugin) {
        this.plugin = plugin;
        this.spawnPoints = new ArrayList<>();
        this.random = new Random();
        startGoldSpawning();
    }

    private void startGoldSpawning() {
        new BukkitRunnable() {
            @Override
            public void run() {
                spawnRandomGold();
            }
        }.runTaskTimer(plugin, 0L, 100L);
    }

    private void spawnRandomGold() {
        if(spawnPoints.isEmpty()) return;
        Location spawnLoc = spawnPoints.get(random.nextInt(spawnPoints.size()));
        spawnLoc.getWorld().dropItem(spawnLoc, new ItemStack(Material.GOLD_INGOT));
    }
}
