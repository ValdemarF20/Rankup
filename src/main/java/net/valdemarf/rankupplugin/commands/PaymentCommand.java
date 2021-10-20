package net.valdemarf.rankupplugin.commands;

import net.valdemarf.rankupplugin.managers.ConfigManager;
import net.valdemarf.rankupplugin.RankupPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PaymentCommand implements Listener {
    private final RankupPlugin rankupPlugin;
    private final ConfigManager configManager;
    public PaymentCommand(RankupPlugin rankupPlugin) {
        this.rankupPlugin = rankupPlugin;
        configManager = rankupPlugin.getConfigManager();
    }
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();

        for (String command : configManager.getList("ecocommands")) {
            if(e.getMessage().contains(command)) {
                rankupPlugin.getPlayerManager().getPlayer(player).autoRankup(rankupPlugin, rankupPlugin.getPlayerManager());
            }
        }
    }
}