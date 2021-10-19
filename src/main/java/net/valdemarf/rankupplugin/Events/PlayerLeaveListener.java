package net.valdemarf.rankupplugin.Events;

import net.valdemarf.rankupplugin.Managers.Database.Database;
import net.valdemarf.rankupplugin.Managers.PlayerManager;
import net.valdemarf.rankupplugin.RankupPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

public class PlayerLeaveListener implements Listener {
    private final PlayerManager playerManager;
    private final RankupPlugin rankupPlugin;

    public PlayerLeaveListener(PlayerManager playerManager, RankupPlugin rankupPlugin) {
        this.playerManager = playerManager;
        this.rankupPlugin = rankupPlugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        /* Database management */
        Database database = rankupPlugin.getDatabase();
        try {
            database.updatePlayerData(player.getUniqueId());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        // If the player is new on the server
        if(playerManager.getOnlinePlayers().containsKey(player.getUniqueId())) {
            playerManager.removePlayer(player.getUniqueId());
        } else {
            System.out.println("Tried to remove a non-existing player");
        }
    }
}
