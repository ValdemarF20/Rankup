package net.valdemarf.rankupplugin;

import net.milkbowl.vault.economy.Economy;
import net.valdemarf.rankupplugin.Managers.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PrisonPlayer {
    private Rank rank;
    private final Player spigotPlayer;
    private Rank nextRank;
    private int prestige = 1;

    private final Economy econ = RankupPlugin.getEcon();

    public PrisonPlayer(Rank rank, int prestige, Player spigotPlayer) {
        this.rank = rank;
        this.spigotPlayer = spigotPlayer;
        this.prestige = prestige;
    }

    /* Getters */
    public double getBalance() { return econ.getBalance(getSpigotPlayer()); }
    public Rank getRank() { return this.rank; }
    public Player getSpigotPlayer() { return this.spigotPlayer; }
    public int getPrestige() { return prestige; }


    /* Ranks */
    public void rankup(PlayerManager playerManager) {
        Rank currentRank = null;
        Rank nextRank;

        for (Rank rank : playerManager.getRanks()) {
            if (rank.equals(this.rank)) {
                currentRank = rank;
                break;
            }
        }
        if(currentRank == null) {
            System.out.println("Player does not have a rank");
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
            nextRank = playerManager.getRankFromIdentifer(rank.getIdentifier() + 1);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                rankup(playerManager);
            }
        }.runTaskTimer(plugin, 1, 2);
    }

    private Rank getNextRankP(Rank currentRank, PlayerManager playerManager) {
        boolean checker = false;
        Rank nRank = null;

        for (Rank rank : playerManager.getRanks()) {
            if(checker) {
                nRank = rank;
                break;
            }
            if(rank.equals(currentRank)) {
                checker = true;
            }
        }

        return nRank;
    }
}