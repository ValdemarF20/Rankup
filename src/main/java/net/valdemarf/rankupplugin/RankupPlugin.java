package net.valdemarf.rankupplugin;

import net.milkbowl.vault.economy.Economy;
import net.valdemarf.rankupplugin.Commands.DebugCommand;
import net.valdemarf.rankupplugin.Commands.PaymentCommand;
import net.valdemarf.rankupplugin.Commands.RankupCommand;
import net.valdemarf.rankupplugin.Events.PlayerJoinListener;
import net.valdemarf.rankupplugin.Events.PlayerLeaveListener;
import net.valdemarf.rankupplugin.GUI.Menu;
import net.valdemarf.rankupplugin.Managers.ConfigManager;
import net.valdemarf.rankupplugin.Managers.Database.DataMySQL;
import net.valdemarf.rankupplugin.Managers.Database.DataSQLite;
import net.valdemarf.rankupplugin.Managers.Database.Database;
import net.valdemarf.rankupplugin.Managers.PlayerManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;

public final class RankupPlugin extends JavaPlugin {
    public PlayerManager playerManager = new PlayerManager();
    public Menu menu = new Menu(playerManager);
    public ConfigManager configManager = new ConfigManager(this);
    public Database database;

    public int rankAmount;
    public static double startRankCost;
    public static double rankIncrement;
    public static String tableName;
    public static String DATABASE_PATH;

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
            System.out.println("\n\n");
            System.out.println((String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName())));
            System.out.println("\n\n");
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
        DATABASE_PATH = getDataFolder().getAbsolutePath() + File.separator + configManager.getString("databaselist.database") + ".db";
        System.out.println(DATABASE_PATH);

        if(configManager.getString("database-type").equalsIgnoreCase("mysql")) {
            System.out.println("MYSQL database has been chose in Config.yml \n\n");
            database = new DataMySQL(this);
        } else {
            database = new DataSQLite(this);
            System.out.println("SQLite database has been chose in Config.yml \n\n");
        }
        database.initialize();

        playerManager.setupRanks(rankAmount, rankIncrement, startRankCost);
        //Create the total players
        playerManager.setTotalPlayers(database.getTotalPlayers());
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            System.out.println("Vault is null");
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

    public void autoSave() {
        new BukkitRunnable() {
            @Override
            public void run() {
                save();
            }
        }.runTaskTimerAsynchronously(this, 1, 2400);
    }

    public void save() {
        for (UUID uuid : playerManager.getOnlinePlayers().keySet()) {
            try {
                database.updatePlayerData(uuid);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
