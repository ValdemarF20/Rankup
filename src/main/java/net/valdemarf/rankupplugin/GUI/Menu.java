package net.valdemarf.rankupplugin.GUI;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.valdemarf.rankupplugin.Managers.PlayerManager;
import net.valdemarf.rankupplugin.PrisonPlayer;
import net.valdemarf.rankupplugin.Rank;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private final PlayerManager playerManager;

    public Menu(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public Gui createGUI(PrisonPlayer player) {
        // Create the GUI
        Gui gui = Gui.gui()
                .title(Component.text("Rank Menu - Current Prestige: " + player.getPrestige()))
                .rows(6)
                .create();

        // Create the items
        for (Rank rank : playerManager.getRanks()) {
            ItemStack item;

            if(rank.equals(player.getRank())) {
                item = new ItemStack(Material.DIAMOND_BLOCK);
            } else {
                item = new ItemStack(Material.STONE);
            }
            // Configure the meta
            ItemMeta meta = item.getItemMeta();

            if(rank.equals(player.getRank())) {
                meta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            // Change the lore
            List<String> lore = new ArrayList<>();
            lore.add("Price: " + rank.getPrice(player));
            if(rank.equals(player.getRank())) {
                lore.add("Current Rank");
            }

            meta.setLore(lore);

            // Change display name
            meta.setDisplayName("Rank: " + rank.getName());

            // Finished meta
            item.setItemMeta(meta);

            GuiItem guiItem = ItemBuilder.from(item).asGuiItem();
            gui.addItem(guiItem);
        }
        gui.setDefaultClickAction(event -> event.setCancelled(true));

        return gui;
    }
}
