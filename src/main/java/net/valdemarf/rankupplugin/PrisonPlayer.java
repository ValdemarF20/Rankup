package net.valdemarf.rankupplugin;

import net.milkbowl.vault.economy.Economy;
import net.valdemarf.rankupplugin.managers.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrisonPlayer {
    private Rank rank;
    private final Player spigotPlayer;
    private Rank nextRank;
    private int prestige = 1;

    private final Economy econ = RankupPlugin.getEcon();

    private static final Logger LOGGER = LoggerFactory.getLogger(PrisonPlayer.class);

    public PrisonPlayer(Rank rank, int prestige, Player spigotPlayer) {
        this.rank = rank;
        this.spigotPlayer = spigotPlayer;
        this.prestige = prestige;
    }

    /* Getters */
    public Rank getRank() { return this.rank; }
    public Player getSpigotPlayer() { return this.spigotPlayer; }
    public int getPrestige() { return prestige; }


    /* Ranks */
    public void rankup(PlayerManager playerManager) {
        Rank currentRank = rank;
        Rank nextRank;

        if(currentRank == null) {
            LOGGER.info( "Player does not have a rank");
            return;
        }

        nextRank = getNextRankP(currentRank, playerManager);

        if(nextRank == null) {
            this.prestige++;
            this.rank = playerManager.getRanks().get(0);
            return;
        }

        if(econ.getBalance(spigotPlayer) < nextRank.getPrice(this)) {
            return;
        }

        econ.withdrawPlayer(spigotPlayer, nextRank.getPrice(this));

        this.rank = nextRank;

        // Set a new nextRank
        this.nextRank = getNextRankP(currentRank, playerManager);
    }

    public void autoRankup(RankupPlugin plugin, PlayerManager playerManager) {
        if(nextRank == null) {
            nextRank = playerManager.getRank(rank.getIdentifier() + 1);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if(nextRank.getPrice(playerManager.getPlayer(spigotPlayer)) >= econ.getBalance(spigotPlayer)) {
                    this.cancel();
                }
                rankup(playerManager);
            }
        }.runTaskTimer(plugin, 1, 2);
    }

    private Rank getNextRankP(Rank currentRank, PlayerManager playerManager) {
        return playerManager.getRanks().get(currentRank.getIdentifier() + 1);
    }
}