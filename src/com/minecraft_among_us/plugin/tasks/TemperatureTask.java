package com.minecraft_among_us.plugin.tasks;

import com.minecraft_among_us.plugin.AmongUsPlayer;
import com.minecraft_among_us.plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class TemperatureTask extends Task {

    private final int expectedTemperature;
    private int currentTemperature;
    private Inventory inventory;

    public TemperatureTask(AmongUsPlayer auPlayer) {
        super("Temperature log", "Ouais", TaskType.SHORT, auPlayer);
        Random rand = new Random();
        this.expectedTemperature = rand.nextInt(32) + 16;
        this.currentTemperature = rand.nextBoolean() ? (this.expectedTemperature + rand.nextInt(16) + 1) : (this.expectedTemperature - rand.nextInt(16) + 1);
        this.inventory = this.createInventory();
    }

    @Override
    public void execute() {
        ((Player) auPlayer.toBukkitPlayer()).openInventory(this.inventory);
    }

    @Override
    public void finish() {
        super.finish();
        ((Player) this.auPlayer.toBukkitPlayer()).closeInventory();
        Bukkit.broadcastMessage("Point faible, TRO FORT");
    }

    private Inventory createInventory() {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.WORKBENCH, this.name);

        ItemStack plusItem = new ItemStack(Material.GREEN_CONCRETE);
        ItemMeta plusItemMeta = plusItem.getItemMeta();
        plusItemMeta.setDisplayName("§a+");
        plusItem.setItemMeta(plusItemMeta);

        ItemStack minusItem = new ItemStack(Material.RED_CONCRETE);
        ItemMeta minusItemMeta = minusItem.getItemMeta();
        minusItemMeta.setDisplayName("§c-");
        minusItem.setItemMeta(minusItemMeta);

        ItemStack currentTemperatureItem = new ItemStack(Material.ORANGE_CONCRETE, this.currentTemperature);
        ItemMeta currentTemperatureItemMeta = currentTemperatureItem.getItemMeta();
        currentTemperatureItemMeta.setDisplayName("§6Current temperature");
        currentTemperatureItem.setItemMeta(currentTemperatureItemMeta);

        ItemStack expectedTemperatureItem = new ItemStack(Material.ORANGE_CONCRETE, this.expectedTemperature);
        ItemMeta expectedTemperatureItemMeta = expectedTemperatureItem.getItemMeta();
        expectedTemperatureItemMeta.setDisplayName("§6Expected temperature");
        expectedTemperatureItem.setItemMeta(expectedTemperatureItemMeta);

        inventory.setItem(2, plusItem);
        inventory.setItem(5, currentTemperatureItem);
        inventory.setItem(0, expectedTemperatureItem);
        inventory.setItem(8, minusItem);
        return inventory;
    }

    private void change(AmongUsPlayer auPlayer, boolean add) {
        if (add && this.currentTemperature < 64) {
            this.currentTemperature++;
        } else if (!add && this.currentTemperature > 1) {
            this.currentTemperature--;
        }
        if (this.expectedTemperature == this.currentTemperature) {
            this.finish();
        } else {
            this.refreshInventory();
        }
    }

    private void refreshInventory() {
        ItemStack currentTemperatureItem = new ItemStack(Material.ORANGE_CONCRETE, this.currentTemperature);
        ItemMeta currentTemperatureItemMeta = currentTemperatureItem.getItemMeta();
        currentTemperatureItemMeta.setDisplayName("§6Current temperature");
        currentTemperatureItem.setItemMeta(currentTemperatureItemMeta);

        ItemStack expectedTemperatureItem = new ItemStack(Material.ORANGE_CONCRETE, this.expectedTemperature);
        ItemMeta expectedTemperatureItemMeta = expectedTemperatureItem.getItemMeta();
        expectedTemperatureItemMeta.setDisplayName("§6Expected temperature");
        expectedTemperatureItem.setItemMeta(expectedTemperatureItemMeta);

        this.inventory.setItem(5, currentTemperatureItem);
        this.inventory.setItem(0, expectedTemperatureItem);
    }

    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onTaskLaunch(PlayerInteractEvent e) {
            if (e.getHand().equals(EquipmentSlot.HAND) && e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getLocation().equals(new Location(Plugin.getDefaultWorld(), -299, 31, -148))) {
                Player player = e.getPlayer();
                AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
                Task task = auPlayer.getTask("Temperature log");
                if (task != null) {
                    task.execute();
                }
            }
        }

        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (e.getView().getTitle().equals("Temperature log")) {
                e.setCancelled(true);
                if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                    Player player = (Player) e.getWhoClicked();
                    AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
                    ItemStack currentItem = e.getCurrentItem();
                    if (currentItem != null) {
                        TemperatureTask task = (TemperatureTask) auPlayer.getTask("Temperature log");
                        Material currentMaterial = currentItem.getType();
                        if (currentMaterial.equals(Material.GREEN_CONCRETE)) {
                            task.change(auPlayer, true);
                        } else if (currentMaterial.equals(Material.RED_CONCRETE)) {
                            task.change(auPlayer, false);
                        }
                    }
                }
            }
        }
    }
}
