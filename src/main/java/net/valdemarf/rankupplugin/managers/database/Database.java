package net.valdemarf.rankupplugin.managers.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public interface Database {
    public void initialize();
    public void openConnection() throws SQLException;
    public PreparedStatement prepareStatement(String query);

    void createPlayer(UUID uuid);

    void updatePlayerData(UUID uuid) throws SQLException;

    List<Integer> getPlayerData(UUID uuid);

    HashSet<UUID> getTotalPlayers();
}
