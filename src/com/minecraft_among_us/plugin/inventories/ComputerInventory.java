package com.minecraft_among_us.plugin.inventories;

import com.minecraft_among_us.plugin.AmongUsPlayer;
import com.minecraft_among_us.plugin.game.Game;
import com.minecraft_among_us.plugin.game.GameState;
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

import java.util.Arrays;

public class ComputerInventory extends BaseInventory {

    public ComputerInventory(AmongUsPlayer auPlayer) {
        super(auPlayer);
    }

    public Inventory create() {
        int playerCount = Game.getInstance().getPlayers().size();
        Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, "Computer");

        ItemStack colorItem = new ItemStack(Material.RED_WOOL);
        ItemMeta colorItemMeta = colorItem.getItemMeta();
        colorItemMeta.setDisplayName("Change color");
        colorItem.setItemMeta(colorItemMeta);

        ItemStack hatItem = new ItemStack(Material.LEATHER_HELMET);
        ItemMeta hatItemMeta = hatItem.getItemMeta();
        hatItemMeta.setDisplayName("Hats");
        hatItem.setItemMeta(hatItemMeta);

        ItemStack settingsItem = new ItemStack(Material.REDSTONE);
        ItemMeta settingsItemMeta = settingsItem.getItemMeta();
        settingsItemMeta.setDisplayName("Game settings");
        settingsItem.setItemMeta(settingsItemMeta);

        ItemStack startItem = new ItemStack((playerCount >= Game.MIN_PLAYERS) ? Material.DIAMOND : Material.BARRIER);
        ItemMeta startItemMeta = startItem.getItemMeta();
        startItemMeta.setDisplayName("§aStart ➤");
        String startItemMetaLore = "§fPlayers : ";
        if (playerCount < Game.MIN_PLAYERS) {
            startItemMetaLore += "§c";
        } else if (playerCount == Game.MIN_PLAYERS) {
            startItemMetaLore += "§e";
        } else {
            startItemMetaLore += "§a";
        }
        startItemMetaLore += playerCount;
        startItemMeta.setLore(Arrays.asList(startItemMetaLore));
        startItem.setItemMeta(startItemMeta);

        inventory.setItem(0, colorItem);
        inventory.setItem(1, hatItem);
        inventory.setItem(2, settingsItem);
        inventory.setItem(4, startItem);
        return inventory;
    }

    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (e.getView().getTitle().equals("Computer")) {
                e.setCancelled(true);
                if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                    Player player = (Player) e.getWhoClicked();
                    AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
                    ItemStack currentItem = e.getCurrentItem();
                    if (currentItem != null) {
                        Material currentMaterial = currentItem.getType();
                        if (currentMaterial.equals(Material.LEATHER_HELMET)) {
                            player.openInventory(new HatInventory(auPlayer).create());
                        } else if (currentMaterial.equals(Material.REDSTONE)) {
                            player.openInventory(new GameSettingsInventory(auPlayer).create());
                        } else if (currentMaterial.equals(Material.RED_WOOL)) {
                            player.openInventory(new ColorInventory(auPlayer).create());
                        } else if (currentMaterial.equals(Material.DIAMOND)) {
                            player.closeInventory();
                            Game.getInstance().start();
                        }
                    }
                }
            }
        }
    }
}
