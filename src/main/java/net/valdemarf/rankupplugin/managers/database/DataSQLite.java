package net.valdemarf.rankupplugin.managers.database;

import net.valdemarf.rankupplugin.PrisonPlayer;
import net.valdemarf.rankupplugin.RankupPlugin;
import net.valdemarf.rankupplugin.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class DataSQLite implements Database{
    private static Connection connection;

    private final RankupPlugin rankupPlugin;
    private final PlayerManager playerManager;

    public DataSQLite(RankupPlugin rankupPlugin) {
        this.rankupPlugin = rankupPlugin;
        playerManager = rankupPlugin.getPlayerManager();
    }
    @Override
    public void initialize() {
        if (!Files.exists(Paths.get(rankupPlugin.databasePath))) {
            try {
                openConnection();

                PreparedStatement statement = connection.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS `" + RankupPlugin.tableName +
                                "` (`uuid` VARCHAR(36), `rank` INTEGER, `prestige` INTEGER)");
                statement.execute();
                statement.close();
                rankupPlugin.getLogger().info("SQLite file created");
            } catch (SQLException e) {
                rankupPlugin.getLogger().severe("Something went wrong while creating the SQLite file");
                rankupPlugin.getLogger().log(Level.SEVERE, "", e);
            }
        } else {
            try {
                openConnection();
            } catch (SQLException e) {
                rankupPlugin.getLogger().log(Level.SEVERE, "", e);
            }
        }
    }

    @Override
    public void openConnection() throws SQLException {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + rankupPlugin.databasePath);
            rankupPlugin.getLogger().log(Level.INFO, "Connection to database success!");
        } catch(Exception e) {
            rankupPlugin.getLogger().log(Level.SEVERE, "", e);
        }
    }

    @Override
    public PreparedStatement prepareStatement(String query) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(query);
        } catch(SQLException e) {
            rankupPlugin.getLogger().log(Level.SEVERE, "", e);
        }
        return ps;
    }

    @Override
    public void createPlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        PrisonPlayer pPlayer = playerManager.getPlayer(player);
        if(pPlayer == null) {
            pPlayer = new PrisonPlayer(playerManager.getRanks().get(0), 1, player);
        }

        try {
            PreparedStatement ps2 = prepareStatement("INSERT OR IGNORE INTO data (uuid, rank, prestige) VALUES (?, ?, ?)");
            ps2.setString(1, uuid.toString());
            ps2.setInt(2, pPlayer.getRank().getIdentifier());
            ps2.setInt(3, pPlayer.getPrestige());
            ps2.executeUpdate();
        } catch (SQLException e) {
            rankupPlugin.getLogger().log(Level.SEVERE, "", e);
        }
    }

    @Override
    public void updatePlayerData(UUID uuid){
        try {
            PreparedStatement ps = prepareStatement("SELECT COUNT(*) FROM `" + RankupPlugin.tableName + "` WHERE uuid = ?;");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            rs.next();
            updateData(uuid);

        } catch(SQLException e) {
            rankupPlugin.getLogger().log(Level.SEVERE, "", e);
        }
    }

    @Override
    public List<Integer> getPlayerData(UUID uuid) {
        List<Integer> objects = new ArrayList<>();
        try {
            ResultSet rs = prepareStatement(
                    "SELECT COUNT(*) FROM `" + RankupPlugin.tableName +
                            "` WHERE uuid = '" + uuid.toString() + "';").executeQuery();
            rs.next();
            if(rs.getInt(1) == 0) {
                createPlayer(uuid);
            }
            ResultSet rs2 = prepareStatement("SELECT * FROM " + RankupPlugin.tableName + " WHERE uuid = '" + uuid.toString() + "';").executeQuery();
            rs2.next();

            int rankIdentifier = rs2.getInt("rank");
            int prestige = rs2.getInt("prestige");

            objects.add(rankIdentifier);
            objects.add(prestige);
        } catch (SQLException e) {
            rankupPlugin.getLogger().log(Level.SEVERE, "", e);
        }
        return objects;
    }

    @Override
    public HashSet<UUID> getTotalPlayers() {
        HashSet<UUID> uuids = new HashSet<>();
        try {
            ResultSet rs = prepareStatement("SELECT COUNT(uuid) FROM `" + RankupPlugin.tableName + "`;").executeQuery();

            int count = 0;
            while(rs.next()) {
                count = rs.getInt(1);
            }
            for(int i = 0; i <= count; i++) {
                ResultSet rs2 = prepareStatement("SELECT uuid FROM `" + RankupPlugin.tableName + "`;").executeQuery();
                if(rs2.next()) {
                    uuids.add(UUID.fromString(rs2.getString(1)));
                }
            }
        } catch (SQLException e) {
            rankupPlugin.getLogger().log(Level.SEVERE, "", e);
        }
        return uuids;
    }

    /* Private Methods */
    private void updateData(UUID uuid) {
        PrisonPlayer pPlayer = playerManager.getPlayer(Bukkit.getPlayer(uuid));

        try {
            PreparedStatement ps = prepareStatement("SELECT COUNT(*) FROM `" + RankupPlugin.tableName + "` WHERE uuid = ?;");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            if(rs.next()) { // THE UUID IS ALREADY IN THE COLUMN
                prepareStatement(
                        "UPDATE data SET rank = '"
                                + (pPlayer.getRank().getIdentifier())
                                + "', prestige = '"
                                + pPlayer.getPrestige()
                                + "' WHERE uuid = '"
                                + uuid.toString() + "';").executeUpdate();
            } else {
                prepareStatement(
                        "INSERT INTO data(uuid, rank, prestige) VALUES ('" + uuid.toString()
                                + "','" + (pPlayer.getRank().getIdentifier()) + "', " + pPlayer.getPrestige() + ");").executeUpdate();
            }

        } catch(SQLException e) {
            rankupPlugin.getLogger().log(Level.SEVERE, "", e);
        }
    }
}
