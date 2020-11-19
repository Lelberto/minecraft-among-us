package com.minecraft_among_us.plugin.inventories;

import com.minecraft_among_us.plugin.game.AmongUsPlayer;
import com.minecraft_among_us.plugin.game.Color;
import com.minecraft_among_us.plugin.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Color inventory class.
 */
public class ColorInventory extends BaseInventory {

    /**
     * Creates a new color inventory.
     *
     * @param auPlayer Linked player
     */
    public ColorInventory(AmongUsPlayer auPlayer) {
        super(auPlayer);
    }

    @Override
    public Inventory create() {
        List<Color> availableColors = Game.getInstance().getAvailableColors();
        Inventory inventory = Bukkit.createInventory(null, InventoryType.CHEST, "Change color");

        int i = 0;
        for (Color color : Color.values()) {
            ItemStack colorItem = new ItemStack(availableColors.contains(color) ? color.wool : Material.BARRIER);
            ItemMeta colorItemMeta = colorItem.getItemMeta();
            colorItemMeta.setDisplayName(color.code + color.name);
            colorItem.setItemMeta(colorItemMeta);
            inventory.setItem(i++, colorItem);
        }

        ItemStack backItem = new ItemStack(Material.STICK);
        ItemMeta backItemMeta = backItem.getItemMeta();
        backItemMeta.setDisplayName("§cBack");
        backItem.setItemMeta(backItemMeta);
        inventory.setItem(26, backItem);

        return inventory;
    }


    /**
     * Listener subclass.
     */
    public static class Listener implements org.bukkit.event.Listener {

        /**
         * Event triggered when a player interacts with the color inventory.
         *
         * @param e Event
         */
        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (e.getView().getTitle().equals("Change color")) {
                e.setCancelled(true);
                if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                    Player player = (Player) e.getWhoClicked();
                    AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
                    ItemStack currentItem = e.getCurrentItem();
                    if (currentItem != null) {
                        List<Color> availableColors = Game.getInstance().getAvailableColors();
                        Material currentMaterial = currentItem.getType();
                        if (currentMaterial.equals(Material.RED_WOOL) && availableColors.contains(Color.RED)) {
                            auPlayer.setColor(Color.RED);
                            player.closeInventory();
                        } else if (currentMaterial.equals(Material.BLUE_WOOL) && availableColors.contains(Color.BLUE)) {
                            auPlayer.setColor(Color.BLUE);
                            player.closeInventory();
                        } else if (currentMaterial.equals(Material.GREEN_WOOL) && availableColors.contains(Color.GREEN)) {
                            auPlayer.setColor(Color.GREEN);
                            player.closeInventory();
                        } else if (currentMaterial.equals(Material.PINK_WOOL) && availableColors.contains(Color.PINK)) {
                            auPlayer.setColor(Color.PINK);
                            player.closeInventory();
                        } else if (currentMaterial.equals(Material.ORANGE_WOOL) && availableColors.contains(Color.ORANGE)) {
                            auPlayer.setColor(Color.ORANGE);
                            player.closeInventory();
                        } else if (currentMaterial.equals(Material.YELLOW_WOOL) && availableColors.contains(Color.YELLOW)) {
                            auPlayer.setColor(Color.YELLOW);
                            player.closeInventory();
                        } else if (currentMaterial.equals(Material.BLACK_WOOL) && availableColors.contains(Color.BLACK)) {
                            auPlayer.setColor(Color.BLACK);
                            player.closeInventory();
                        } else if (currentMaterial.equals(Material.WHITE_WOOL) && availableColors.contains(Color.WHITE)) {
                            auPlayer.setColor(Color.WHITE);
                            player.closeInventory();
                        } else if (currentMaterial.equals(Material.PURPLE_WOOL) && availableColors.contains(Color.PURPLE)) {
                            auPlayer.setColor(Color.PURPLE);
                            player.closeInventory();
                        } else if (currentMaterial.equals(Material.BROWN_WOOL) && availableColors.contains(Color.BROWN)) {
                            auPlayer.setColor(Color.BROWN);
                            player.closeInventory();
                        } else if (currentMaterial.equals(Material.CYAN_WOOL) && availableColors.contains(Color.CYAN)) {
                            auPlayer.setColor(Color.CYAN);
                            player.closeInventory();
                        } else if (currentMaterial.equals(Material.LIME_WOOL) && availableColors.contains(Color.LIME)) {
                            auPlayer.setColor(Color.LIME);
                            player.closeInventory();
                        } else if (currentMaterial.equals(Material.STICK)) {
                            player.openInventory(new ComputerInventory(auPlayer).create());
                        }
                        auPlayer.refresh();
                    }
                }
            }
        }
    }
}
