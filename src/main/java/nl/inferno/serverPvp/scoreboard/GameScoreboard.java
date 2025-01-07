package nl.inferno.serverPvp.scoreboard;

import nl.inferno.serverPvp.ServerPvp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class GameScoreboard {
    private ServerPvp plugin;

    public void updateScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective("murder", "dummy");
        objective.setDisplayName(ChatColor.RED + "Murder Mystery");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score roleScore = objective.getScore(ChatColor.WHITE + "Role: " + plugin.getGameManager().getPlayerRole(player));
        roleScore.setScore(1);

        player.setScoreboard(board);
    }
}
