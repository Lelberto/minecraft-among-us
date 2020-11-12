package com.minecraft_among_us.plugin.tasks;

import com.minecraft_among_us.plugin.AmongUsPlayer;
import com.minecraft_among_us.plugin.config.TaskSettings;
import com.minecraft_among_us.plugin.game.Game;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class TemperatureColdTask extends TemperatureTask {

    public static final int ID = 1;

    public TemperatureColdTask(AmongUsPlayer auPlayer, boolean fake) {
        super(ID, auPlayer, fake);
    }

    @Override
    protected int[] generateTemperatures() {
        int[] temperatures = new int[2];
        Random rand = new Random();
        temperatures[0] = 32 - new Random().nextInt(32);
        temperatures[1] = temperatures[0] + (rand.nextInt(16) + 10);
        return temperatures;
    }

    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onClick(InventoryClickEvent e) {
            TaskSettings settings = Game.getInstance().getTaskSettings(ID);
            if (e.getView().getTitle().equals(settings.name)) {
                e.setCancelled(true);
                if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                    Player player = (Player) e.getWhoClicked();
                    AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
                    ItemStack currentItem = e.getCurrentItem();
                    if (currentItem != null) {
                        TemperatureColdTask task = (TemperatureColdTask) auPlayer.getTask(settings.name);
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
