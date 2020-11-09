package com.minecraft_among_us.plugin.inventories;

import com.minecraft_among_us.plugin.AmongUsPlayer;
import com.minecraft_among_us.plugin.Color;
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

public class ColorInventory extends BaseInventory {

    public ColorInventory(AmongUsPlayer auPlayer) {
        super(auPlayer);
    }

    @Override
    public Inventory create() {
        List<Color> availableColors = Game.getInstance().getAvailableColors();
        Inventory inventory = Bukkit.createInventory(null, InventoryType.CHEST, "Change color");

        ItemStack redItem = new ItemStack(availableColors.contains(Color.RED) ? Material.RED_WOOL : Material.BARRIER);
        ItemMeta redItemMeta = redItem.getItemMeta();
        redItemMeta.setDisplayName("§cRed");
        redItem.setItemMeta(redItemMeta);

        ItemStack blueItem = new ItemStack(availableColors.contains(Color.BLUE) ? Material.BLUE_WOOL : Material.BARRIER);
        ItemMeta blueItemMeta = blueItem.getItemMeta();
        blueItemMeta.setDisplayName("§1Blue");
        blueItem.setItemMeta(blueItemMeta);

        ItemStack greenItem = new ItemStack(availableColors.contains(Color.GREEN) ? Material.GREEN_WOOL : Material.BARRIER);
        ItemMeta greenItemMeta = greenItem.getItemMeta();
        greenItemMeta.setDisplayName("§2Green");
        greenItem.setItemMeta(greenItemMeta);

        ItemStack pinkItem = new ItemStack(availableColors.contains(Color.PINK) ? Material.PINK_WOOL : Material.BARRIER);
        ItemMeta pinkItemMeta = pinkItem.getItemMeta();
        pinkItemMeta.setDisplayName("§dPink");
        pinkItem.setItemMeta(pinkItemMeta);

        ItemStack orangeItem = new ItemStack(availableColors.contains(Color.ORANGE) ? Material.ORANGE_WOOL : Material.BARRIER);
        ItemMeta orangeItemMeta = orangeItem.getItemMeta();
        orangeItemMeta.setDisplayName("§6Orange");
        orangeItem.setItemMeta(orangeItemMeta);

        ItemStack yellowItem = new ItemStack(availableColors.contains(Color.YELLOW) ? Material.YELLOW_WOOL : Material.BARRIER);
        ItemMeta yellowItemMeta = yellowItem.getItemMeta();
        yellowItemMeta.setDisplayName("§eYellow");
        yellowItem.setItemMeta(yellowItemMeta);

        ItemStack blackItem = new ItemStack(availableColors.contains(Color.BLACK) ? Material.BLACK_WOOL : Material.BARRIER);
        ItemMeta blackItemMeta = blackItem.getItemMeta();
        blackItemMeta.setDisplayName("§0Black");
        blackItem.setItemMeta(blackItemMeta);

        ItemStack whiteItem = new ItemStack(availableColors.contains(Color.WHITE) ? Material.WHITE_WOOL : Material.BARRIER);
        ItemMeta whiteItemMeta = whiteItem.getItemMeta();
        whiteItemMeta.setDisplayName("§fWhite");
        whiteItem.setItemMeta(whiteItemMeta);

        ItemStack purpleItem = new ItemStack(availableColors.contains(Color.PURPLE) ? Material.PURPLE_WOOL : Material.BARRIER);
        ItemMeta purpleItemMeta = purpleItem.getItemMeta();
        purpleItemMeta.setDisplayName("§5Purple");
        purpleItem.setItemMeta(purpleItemMeta);

        ItemStack brownItem = new ItemStack(availableColors.contains(Color.BROWN) ? Material.BROWN_WOOL : Material.BARRIER);
        ItemMeta brownItemMeta = brownItem.getItemMeta();
        brownItemMeta.setDisplayName("§8Brown");
        brownItem.setItemMeta(brownItemMeta);

        ItemStack cyanItem = new ItemStack(availableColors.contains(Color.CYAN) ? Material.CYAN_WOOL : Material.BARRIER);
        ItemMeta cyanItemMeta = cyanItem.getItemMeta();
        cyanItemMeta.setDisplayName("§bCyan");
        cyanItem.setItemMeta(cyanItemMeta);

        ItemStack limeItem = new ItemStack(availableColors.contains(Color.LIME) ? Material.LIME_WOOL : Material.BARRIER);
        ItemMeta limeItemMeta = limeItem.getItemMeta();
        limeItemMeta.setDisplayName("§aLime");
        limeItem.setItemMeta(limeItemMeta);

        inventory.setItem(0, redItem);
        inventory.setItem(1, blueItem);
        inventory.setItem(2, greenItem);
        inventory.setItem(3, pinkItem);
        inventory.setItem(4, orangeItem);
        inventory.setItem(5, yellowItem);
        inventory.setItem(6, blackItem);
        inventory.setItem(7, whiteItem);
        inventory.setItem(8, purpleItem);
        inventory.setItem(9, brownItem);
        inventory.setItem(10, cyanItem);
        inventory.setItem(11, limeItem);
        return inventory;
    }

    public static class Listener implements org.bukkit.event.Listener {

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
                        }
                    }
                }
            }
        }
    }
}
