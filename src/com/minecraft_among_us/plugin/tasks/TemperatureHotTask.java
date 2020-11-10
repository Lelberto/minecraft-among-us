package com.minecraft_among_us.plugin.tasks;

import com.minecraft_among_us.plugin.AmongUsPlayer;
import com.minecraft_among_us.plugin.config.ConfigurationManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class TemperatureHotTask extends TemperatureTask {

    public TemperatureHotTask(AmongUsPlayer auPlayer) {
        super(ConfigurationManager.getInstance().temperatureHotTaskSettings.name, ConfigurationManager.getInstance().temperatureHotTaskSettings.description, TaskType.SHORT, auPlayer);
    }

    @Override
    protected int[] generateTemperatures() {
        int[] temperatures = new int[2];
        Random rand = new Random();
        temperatures[0] = rand.nextInt(32) + 32;
        temperatures[1] = temperatures[0] - (rand.nextInt(16) + 10);
        return temperatures;
    }

    @Override
    public void execute() {
        ((Player) auPlayer.toBukkitPlayer()).openInventory(this.inventory);
    }

    @Override
    public void finish() {
        super.finish();
        ((Player) this.auPlayer.toBukkitPlayer()).closeInventory();
    }

    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onTaskLaunch(PlayerInteractEvent e) {
            if (e.getHand().equals(EquipmentSlot.HAND) && e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getLocation().equals(ConfigurationManager.getInstance().temperatureHotTaskSettings.location)) {
                Player player = e.getPlayer();
                AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
                Task task = auPlayer.getTask(ConfigurationManager.getInstance().temperatureHotTaskSettings.name);
                if (task != null && !task.isFinished()) {
                    task.execute();
                }
            }
        }

        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (e.getView().getTitle().equals(ConfigurationManager.getInstance().temperatureHotTaskSettings.name)) {
                e.setCancelled(true);
                if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                    Player player = (Player) e.getWhoClicked();
                    AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
                    ItemStack currentItem = e.getCurrentItem();
                    if (currentItem != null) {
                        TemperatureHotTask task = (TemperatureHotTask) auPlayer.getTask(ConfigurationManager.getInstance().temperatureHotTaskSettings.name);
                        Material currentMaterial = currentItem.getType();
                        if (currentMaterial.equals(Material.GREEN_CONCRETE)) {
                            task.change(true);
                        } else if (currentMaterial.equals(Material.RED_CONCRETE)) {
                            task.change(false);
                        }
                    }
                }
            }
        }
    }
}
