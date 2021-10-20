package net.valdemarf.rankupplugin;

import net.milkbowl.vault.economy.Economy;
import net.valdemarf.rankupplugin.commands.DebugCommand;
import net.valdemarf.rankupplugin.commands.PaymentCommand;
import net.valdemarf.rankupplugin.commands.RankupCommand;
import net.valdemarf.rankupplugin.events.PlayerJoinListener;
import net.valdemarf.rankupplugin.events.PlayerLeaveListener;
import net.valdemarf.rankupplugin.gui.Menu;
import net.valdemarf.rankupplugin.managers.ConfigManager;
import net.valdemarf.rankupplugin.managers.database.DataMySQL;
import net.valdemarf.rankupplugin.managers.database.DataSQLite;
import net.valdemarf.rankupplugin.managers.database.Database;
import net.valdemarf.rankupplugin.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public final class RankupPlugin extends JavaPlugin {
    private final PlayerManager playerManager = new PlayerManager();
    private final Menu menu = new Menu(playerManager);
    private final ConfigManager configManager = new ConfigManager(this);
    private Database database;

    public static int rankAmount;
    public static double startRankCost;
    public static double rankIncrement;
    public static String tableName;
    public String databasePath;

    /* Economy */
    private static Economy econ = null;


    @Override
    public void onEnable() {
        // Config Management
        configManager.instantiate();
        rankAmount = configManager.getInt("rank-amount");
        startRankCost = configManager.getDouble("default-rank-price");
        rankIncrement = configManager.getDouble("rank-increment");
        tableName = configManager.getString("database-table-name");

        saveDefaultConfig();
        setupServer();

        if (!setupEconomy() ) {
            Bukkit.getLogger().log(Level.SEVERE,
                    String.format("[%s] - Disabled due to no Vault dependency found!",
                    getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                playerManager.autoRankup();
            }
        }.runTaskTimerAsynchronously(this, 1, 600);


        /* Objects */
        playerManager.setMenu(menu);

        /* Commands */
        this.getCommand("rankup").setExecutor(new RankupCommand(playerManager, menu));
        this.getCommand("debug").setExecutor(new DebugCommand(playerManager));

        /* Events */
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(playerManager, this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerLeaveListener(playerManager, this), this);
        this.getServer().getPluginManager().registerEvents(new PaymentCommand(this), this);

        // Start autosave function
        autoSave();
    }

    @Override
    public void onDisable() {
        // Save data to database
        save();
    }

    private void setupServer() {
        /* Setup of Database management */
        databasePath = getDataFolder().getAbsolutePath() + File.separator + configManager.getString("databaselist.database") + ".db";
        Bukkit.getLogger().log(Level.INFO, databasePath);

        if(configManager.getString("database-type").equalsIgnoreCase("mysql")) {
            Bukkit.getLogger().log(Level.INFO, "MYSQL database has been chose in Config.yml \n\n");
            database = new DataMySQL(this);
        } else {
            database = new DataSQLite(this);
            Bukkit.getLogger().log(Level.INFO, "SQLite database has been chose in Config.yml \n\n");
        }
        database.initialize();

        playerManager.setupRanks(rankAmount, rankIncrement, startRankCost);
        //Create the total players
        playerManager.setTotalPlayers(database.getTotalPlayers());
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Vault is null");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEcon() {
        return econ;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Database getDatabase() {
        return database;
    }

    private void autoSave() {
        new BukkitRunnable() {
            @Override
            public void run() {
                save();
            }
        }.runTaskTimer(this, 1, 2400);
    }

    private void save() {
        for (UUID uuid : playerManager.getOnlinePlayers().keySet()) {
            try {
                database.updatePlayerData(uuid);
            } catch (SQLException e) {
                this.getLogger().log(Level.SEVERE, "", e);
            }
        }
    }

    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }
}
