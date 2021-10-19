package net.valdemarf.rankupplugin.Events;

import net.valdemarf.rankupplugin.Managers.Database.Database;
import net.valdemarf.rankupplugin.Managers.PlayerManager;
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
        if(objects.size() == 0) {
            database.createPlayer(uuid);
        }
        Rank newRank = null;
        for (Rank rank : playerManager.getRanks()) {
            if(rank.getIdentifier() == objects.get(0)) {
                newRank = rank;   
            }
        }
        if(newRank == null) {
            newRank = new Rank(0, "1", RankupPlugin.startRankCost);
        }

        PrisonPlayer prisonPlayer = new PrisonPlayer(newRank, objects.get(1), player);
        playerManager.addPlayer(uuid, prisonPlayer);
    }
}