package nl.inferno.serverPvp.listeners;

import nl.inferno.serverPvp.ServerPvp;
import nl.inferno.serverPvp.arena.Arena;
import nl.inferno.serverPvp.game.PlayerRole;
import nl.inferno.serverPvp.game.GameState;
import nl.inferno.serverPvp.items.GameItems;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class GameListener implements Listener {
    private ServerPvp plugin;

    public GameListener(ServerPvp plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.getGameManager().getGameState() == GameState.WAITING) {
            Arena randomArena = plugin.getArenaManager().getRandomArena();
            if (randomArena != null) {
                plugin.getGameManager().addPlayer(player, randomArena);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(plugin.getGameManager().isPlayerInGame(player)) {
            plugin.getGameManager().removePlayer(player);
        }
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        plugin.getGameManager().handlePlayerDeath(victim, killer);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;

        Player damaged = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        PlayerRole damagerRole = plugin.getGameManager().getPlayerRole(damager);

        if (damagerRole != PlayerRole.MURDERER) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onGoldPickup(PlayerPickupItemEvent event) {
        if (event.getItem().getItemStack().getType() == Material.GOLD_INGOT) {
            Player player = event.getPlayer();
            PlayerRole role = plugin.getGameManager().getPlayerRole(player);

            int currentGold = plugin.getGameManager().addGoldToPlayer(player);

            if (currentGold >= 10 && role == PlayerRole.INNOCENT) {
                player.getInventory().addItem(GameItems.getDetectiveBow());
                player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                player.sendMessage(ChatColor.GREEN + "You collected enough gold for a bow!");
                plugin.getGameManager().resetPlayerGold(player);
            }

            plugin.getScoreboardManager().updateScoreboard(player);
        }
    }
}