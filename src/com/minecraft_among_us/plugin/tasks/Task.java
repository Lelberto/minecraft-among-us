package com.minecraft_among_us.plugin.tasks;

import com.minecraft_among_us.plugin.AmongUsPlayer;
import com.minecraft_among_us.plugin.Plugin;
import com.minecraft_among_us.plugin.config.TaskSettings;
import com.minecraft_among_us.plugin.event.TaskFinishEvent;
import com.minecraft_among_us.plugin.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public abstract class Task {

    protected final int id;
    protected final TaskSettings settings;
    protected AmongUsPlayer auPlayer;
    protected boolean finished;

    public Task(int id, AmongUsPlayer auPlayer) {
        this.id = id;
        this.settings = Game.getInstance().getTaskSettings(id);
        this.finished = false;
        this.auPlayer = auPlayer;
    }

    public abstract void execute();

    public int getId() {
        return this.id;
    }

    public TaskSettings getSettings() {
        return this.settings;
    }

    public void finish() {
        this.finished = true;
        Bukkit.getPluginManager().callEvent(new TaskFinishEvent(this, this.auPlayer));
    }

    public boolean isFinished() {
        return this.finished;
    }

    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onInteract(PlayerInteractEvent e) {
            if (e.getHand().equals(EquipmentSlot.HAND) && e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                Player player = e.getPlayer();
                AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
                Location blockLocation = e.getClickedBlock().getLocation();
                TaskSettings taskSettings = Game.getInstance().getTaskSettings(blockLocation);
                if (taskSettings != null) {
                    Task task = auPlayer.getTask(taskSettings.id);
                    if (task != null && !task.isFinished()) {
                        task.execute();
                    }
                }
            }
        }

        @EventHandler
        public void onTaskFinish(TaskFinishEvent e) {
            Task task = e.getTask();
            AmongUsPlayer auPlayer = e.getAmongUsPlayer();
            Player player = (Player) auPlayer.toBukkitPlayer();
            player.sendTitle("§aTask completed", null, 5, 30, 5);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, SoundCategory.AMBIENT, 1.0F, 0.6F);
            Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(), () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, SoundCategory.AMBIENT, 1.0F, 0.8F), 3L);
        }
    }
}
