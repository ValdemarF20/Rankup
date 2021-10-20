package net.valdemarf.rankupplugin.events;

import net.valdemarf.rankupplugin.managers.database.Database;
import net.valdemarf.rankupplugin.managers.PlayerManager;
import net.valdemarf.rankupplugin.PrisonPlayer;
import net.valdemarf.rankupplugin.Rank;
import net.valdemarf.rankupplugin.RankupPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    private final PlayerManager playerManager;
    private final RankupPlugin rankupPlugin;

    public PlayerJoinListener(PlayerManager playerManager, RankupPlugin rankupPlugin) {
        this.playerManager = playerManager;
        this.rankupPlugin = rankupPlugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        /* Database management */
        Database database = rankupPlugin.getDatabase();


        List<Integer> objects = database.getPlayerData(uuid);

        Rank newRank = playerManager.getRank(objects.get(0));

        if(newRank == null) {
            newRank = new Rank(0, "1", RankupPlugin.startRankCost);
        }

        PrisonPlayer prisonPlayer = new PrisonPlayer(newRank, objects.get(1), player);
        playerManager.addPlayer(uuid, prisonPlayer);
    }
}