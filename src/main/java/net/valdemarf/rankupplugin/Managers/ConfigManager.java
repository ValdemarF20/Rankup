package net.valdemarf.rankupplugin.Managers;

import net.valdemarf.rankupplugin.RankupPlugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ConfigManager {
    private final RankupPlugin rankupPlugin;
    public FileConfiguration config;

    public ConfigManager(RankupPlugin rankupPlugin) {
        this.rankupPlugin = rankupPlugin;

        config = rankupPlugin.getConfig();
    }


    public void instantiate() {
        setupConfig();

        new BukkitRunnable() {
            @Override
            public void run() {
                saveConfig();
            }
        }.runTaskTimerAsynchronously(rankupPlugin, 1, 1200);
    }

    public void setupConfig() {
        config.options().copyDefaults(true);
        rankupPlugin.saveDefaultConfig();
    }

    public void saveConfig() {
        config.options().copyDefaults();
        rankupPlugin.saveConfig();
    }

    public String getString(String string) {
        return config.getString(string);
    }

    public int getInt(String string) {
        return config.getInt(string);
    }

    public double getDouble(String string) {
        return config.getDouble(string);
    }

    public List<String> getList(String path) {
        return config.getStringList(path);
    }
}
