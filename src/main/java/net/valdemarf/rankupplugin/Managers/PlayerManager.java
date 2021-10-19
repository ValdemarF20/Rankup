package net.valdemarf.rankupplugin.Managers;

import net.valdemarf.rankupplugin.GUI.Menu;
import net.valdemarf.rankupplugin.PrisonPlayer;
import net.valdemarf.rankupplugin.Rank;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerManager {
    public List<Rank> ranks = new ArrayList<>();
    public HashMap<UUID, PrisonPlayer> onlinePlayers = new HashMap<>();
    public HashSet<UUID> totalPlayers = new HashSet<>();

    public Menu menu;

    /* Rank configuration */
    public List<Rank> getRanks() { return ranks; }
    public void addRank(Rank rank) { ranks.add(rank); }

    /* Player Configuration */
    public PrisonPlayer getPlayer(Player spigotPlayer) {
        for (PrisonPlayer player : onlinePlayers.values()) {
            if (player.getSpigotPlayer().getUniqueId().equals(spigotPlayer.getUniqueId())) {
                return player;
            }
        }
        System.out.println("There are no players stored with that UUID");
        return null;
    }

    public HashMap<UUID, PrisonPlayer> getOnlinePlayers() { return onlinePlayers; }
    public HashSet<UUID> getTotalPlayers() { return totalPlayers; }
    public void setTotalPlayers(HashSet<UUID> uuids) { this.totalPlayers = uuids; }

    public void addPlayer(UUID uuid, PrisonPlayer player) {
        onlinePlayers.putIfAbsent(uuid, player);
        if(totalPlayers.contains(uuid)) {
            return;
        }
        totalPlayers.add(uuid);
    }

    public void removePlayer(UUID uuid) {
        if(onlinePlayers.containsKey(uuid)) {
            onlinePlayers.remove(uuid);
        } else {
            System.out.println("Player is not online");
        }
    }

    /* GUI Management */
    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public void autoRankup() {
        for (PrisonPlayer pPlayer : onlinePlayers.values()) {
            pPlayer.rankup(this);
        }
    }

    public void setupRanks(int rankAmount, double increment, double startPrice) {
        for(int i = 0; i < rankAmount; i++) {
            if(getRanks().size() == 0) {
                addRank(new Rank(i, String.valueOf(i + 1), startPrice));
            } else {
                addRank(new Rank(i, String.valueOf(i + 1), getRanks().get(i - 1).getStartPrice() * increment));
            }
        }
    }

    public Rank getRankFromIdentifer(int identifier) {
        for (Rank rank : ranks) {
            if(rank.getIdentifier() == identifier) {
                return rank;
            }
        }
        return null;
    }
}