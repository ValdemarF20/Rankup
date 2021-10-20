package net.valdemarf.rankupplugin.commands;

import net.valdemarf.rankupplugin.managers.PlayerManager;
import net.valdemarf.rankupplugin.PrisonPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugCommand implements CommandExecutor {
    private final PlayerManager playerManager;
    private static final Logger LOGGER = LoggerFactory.getLogger(DebugCommand.class);

    public DebugCommand(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String labels, String[] args) {
        Player player = (Player) sender;
        PrisonPlayer pPlayer = playerManager.getPlayer(player);

        LOGGER.debug("\n\n");

        LOGGER.debug("Online Players: " + playerManager.getOnlinePlayers());
        LOGGER.debug("Total Players: " + playerManager.getTotalPlayers());
        LOGGER.debug("Prison Player: " + pPlayer);
        LOGGER.debug("Available Ranks: " + playerManager.getRanks().size());
        LOGGER.debug("Prison Player current rank: " + pPlayer.getRank().getName());

        LOGGER.debug("\n\n");

        return true;
    }
}
