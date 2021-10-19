package net.valdemarf.rankupplugin.Commands;

import net.valdemarf.rankupplugin.GUI.Menu;
import net.valdemarf.rankupplugin.Managers.PlayerManager;
import net.valdemarf.rankupplugin.PrisonPlayer;
import net.valdemarf.rankupplugin.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankupCommand implements CommandExecutor {
    private final PlayerManager playerManager;
    private final Menu menu;

    public RankupCommand(PlayerManager playerManager, Menu menu) {
        this.playerManager = playerManager;
        this.menu = menu;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) { return true; }

        Player spigotPlayer = (Player) sender;
        PrisonPlayer pPlayer = playerManager.getPlayer(spigotPlayer);

        if(pPlayer == null) {
            pPlayer = new PrisonPlayer(playerManager.getRanks().get(0), 1, spigotPlayer);
        }
        menu.createGUI(pPlayer).open(spigotPlayer);

        return true;
    }
}
