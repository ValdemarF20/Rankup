package net.valdemarf.rankupplugin.managers;

import net.valdemarf.rankupplugin.RankupPlugin;
import net.valdemarf.rankupplugin.gui.Menu;
import net.valdemarf.rankupplugin.PrisonPlayer;
import net.valdemarf.rankupplugin.Rank;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
    public final Map<Integer, Rank> ranks = new HashMap<>(RankupPlugin.rankAmount);
    public Map<UUID, PrisonPlayer> onlinePlayers = new ConcurrentHashMap<>();
    public Set<UUID> totalPlayers = new HashSet<>();

    public Menu menu;

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerManager.class);

    /* Rank configuration */
    public Map<Integer, Rank> getRanks() { return ranks; }
    public void addRank(Rank rank) { ranks.putIfAbsent(ranks.size(), rank); }

    /* Player Configuration */
    public PrisonPlayer getPlayer(Player spigotPlayer) {
        for (PrisonPlayer player : onlinePlayers.values()) {
            if (player.getSpigotPlayer().getUniqueId().equals(spigotPlayer.getUniqueId())) {
                return player;
            }
        }
        LOGGER.warn("There are no players stored with that UUID");
        return null;
    }

    public Map<UUID, PrisonPlayer> getOnlinePlayers() { return onlinePlayers; }
    public Set<UUID> getTotalPlayers() { return totalPlayers; }
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
            LOGGER.warn("Player is not online");
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

    public Rank getRank(int identifier) {
        ranks.get(identifier);
        return null;
    }
}