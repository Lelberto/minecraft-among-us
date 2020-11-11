package com.minecraft_among_us.plugin.tasks;

import com.minecraft_among_us.plugin.AmongUsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class TemperatureTask extends Task {

    protected final int expectedTemperature;
    protected int currentTemperature;
    protected Inventory inventory;

    public TemperatureTask(int id, AmongUsPlayer auPlayer) {
        super(id, auPlayer);
        int[] temperatures = this.generateTemperatures();
        this.expectedTemperature = temperatures[0];
        this.currentTemperature = temperatures[1];
        this.inventory = this.createInventory();
    }

    protected abstract int[] generateTemperatures();

    @Override
    public void execute() {
        ((Player) auPlayer.toBukkitPlayer()).openInventory(this.inventory);
    }

    @Override
    public void finish() {
        super.finish();
        ((Player) this.auPlayer.toBukkitPlayer()).closeInventory();
    }

    private Inventory createInventory() {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.WORKBENCH, this.settings.name);

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

    protected void change(boolean add) {
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

    protected void refreshInventory() {
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
}
