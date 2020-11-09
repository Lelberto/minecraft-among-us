package com.minecraft_among_us.plugin.tasks;

import com.minecraft_among_us.plugin.AmongUsPlayer;
import com.minecraft_among_us.plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimonTask extends Task {

    private Inventory inventory;
    private List<Integer> slots;
    private boolean showingSteps;
    private int timesMaster;
    private int timesPlayer;

    public SimonTask(AmongUsPlayer auPlayer) {
        super("Simon", "Memorize and repeat the Simon", TaskType.LONG, auPlayer);
        this.inventory = this.createInventory();
        this.slots = new ArrayList<>();
        this.showingSteps = false;
        this.timesMaster = 0;
        this.timesPlayer = 0;
    }

    @Override
    public void execute() {
        Player player = (Player) auPlayer.toBukkitPlayer();
        player.openInventory(this.inventory);
        nextStep();
    }

    private void showSteps() {
        this.showingSteps = true;
        Bukkit.getScheduler().runTaskTimer(Plugin.getPlugin(), (task) -> {
            this.inventory.setItem(this.slots.get(this.timesMaster), new ItemStack(Material.LIGHT_BLUE_CONCRETE));
            Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(), () -> {
                this.inventory.setItem(this.slots.get(this.timesMaster), new ItemStack(Material.LIGHT_GRAY_CONCRETE));
                if (this.timesMaster++ == this.slots.size() || this.timesMaster == 5) {
                    this.showingSteps = false;
                    Bukkit.broadcastMessage("Stop");
                    task.cancel();
                }
            }, 5L);
        }, 20L, 10L);
    }

    private void nextStep() {
        this.timesMaster = 0;
        this.timesPlayer = 0;
        int slot = new Random().nextInt(9);
        this.slots.add(slot);
        showSteps();
    }

    public void resetStep() {
        this.timesMaster = 0;
        this.timesPlayer = 0;
    }

    private Inventory createInventory() {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.DROPPER, this.name);

        for (int i = 0; i < 9; i++) {
            ItemStack item = new ItemStack(Material.LIGHT_GRAY_CONCRETE);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName("" + (i + 1));
            item.setItemMeta(itemMeta);
            inventory.setItem(i, item);
        }

        return inventory;
    }

    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onTaskLaunch(PlayerInteractEvent e) {
            if (e.getHand().equals(EquipmentSlot.HAND) && e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getLocation().equals(new Location(Plugin.getDefaultWorld(), -314, 31, -161))) {
                Player player = e.getPlayer();
                AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
                Task task = auPlayer.getTask("Simon");
                if (task != null) {
                    task.execute();
                }
            }
        }

        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (e.getView().getTitle().equals("Simon")) {
                e.setCancelled(true);
                Player player = (Player) e.getWhoClicked();
                AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
                ItemStack currentItem = e.getCurrentItem();
                if (currentItem != null) {
                    SimonTask task = (SimonTask) auPlayer.getTask("Simon");
                    if (!task.showingSteps) {
                        if (e.getSlot() == task.slots.get(task.timesPlayer++)) {
                            task.nextStep();
                        } else {
                            task.resetStep();
                        }
                    }
                }
            }
        }
    }
}
