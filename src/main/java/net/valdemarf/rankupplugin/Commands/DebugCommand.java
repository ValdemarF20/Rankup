package net.valdemarf.rankupplugin.Commands;

import net.valdemarf.rankupplugin.Managers.PlayerManager;
import net.valdemarf.rankupplugin.PrisonPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DebugCommand implements CommandExecutor {
    private final PlayerManager playerManager;
    public DebugCommand(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String labels, String[] args) {
        Player player = (Player) sender;
        PrisonPlayer pPlayer = playerManager.getPlayer(player);

        System.out.println("\n\n");

        System.out.println("Online Players: " + playerManager.getOnlinePlayers());
        System.out.println("Total Players: " + playerManager.getTotalPlayers());
        System.out.println("Prison Player: " + pPlayer);
        System.out.println("Available Ranks: " + playerManager.getRanks().size());
        System.out.println("Prison Player current rank: " + pPlayer.getRank().getName());

        System.out.println("\n\n");

        return true;
    }
}
