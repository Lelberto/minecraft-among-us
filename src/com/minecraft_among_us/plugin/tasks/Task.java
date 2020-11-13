package com.minecraft_among_us.plugin.tasks;

import com.minecraft_among_us.plugin.game.AmongUsPlayer;
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * Task class.
 *
 * This class is used to create tasks by extending it.
 */
public abstract class Task {

    /**
     * Creates a task.
     *
     * @param auPlayer Player
     * @param taskId Task ID
     * @param fake Fake task (for impostors)
     * @return Created task, or {@code null} if the task ID is incorrect
     */
    public static Task createTask(AmongUsPlayer auPlayer, int taskId, boolean fake) {
        switch (taskId) {
            case TemperatureHotTask.ID: return new TemperatureHotTask(auPlayer, fake);
            case TemperatureColdTask.ID: return new TemperatureColdTask(auPlayer, fake);
            case SimonTask.ID: return new SimonTask(auPlayer, fake);
            default: return null;
        }
    }

    protected final int id;
    protected final TaskSettings settings;
    protected AmongUsPlayer auPlayer;
    protected boolean finished;
    protected final boolean fake;

    /**
     * Creates a new task.
     *
     * @param id Task ID
     * @param auPlayer Player
     * @param fake Fake task (for impostors)
     */
    public Task(int id, AmongUsPlayer auPlayer, boolean fake) {
        this.id = id;
        this.settings = Game.getInstance().getTaskSettings(id);
        this.auPlayer = auPlayer;
        this.finished = false;
        this.fake = fake;
    }

    /**
     * Executes the task.
     */
    public abstract void execute();

    /**
     * Finishes the task.
     *
     * This method calls the {@link TaskFinishEvent}.
     */
    public void finish() {
        this.finished = true;
        Bukkit.getPluginManager().callEvent(new TaskFinishEvent(this, this.auPlayer));
    }

    /**
     * Gets ID.
     *
     * @return ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets settings.
     *
     * @return Settings
     */
    public TaskSettings getSettings() {
        return this.settings;
    }

    /**
     * Gets the player.
     *
     * @return Player
     */
    public AmongUsPlayer getAmongUsPlayer() {
        return auPlayer;
    }

    /**
     * Checks if finished.
     *
     * @return True if the task is finished, false otherwise
     */
    public boolean isFinished() {
        return this.finished;
    }

    /**
     * Checks if the task is fake.
     *
     * A task can be fake for impostors.
     *
     * @return True if the task if fake, false otherwise
     */
    public boolean isFake() {
        return fake;
    }

    @Override
    public String toString() {
        return "Task{" +
                "auPlayer=" + auPlayer.toString() +
                "settings=" + settings +
                ", finished=" + finished +
                ", fake=" + fake +
                '}';
    }


    /**
     * Listener subclass.
     */
    public static class Listener implements org.bukkit.event.Listener {

        /**
         * Event triggered when a player launches a task.
         *
         * @param e Event
         */
        @EventHandler
        public void onLaunchTask(PlayerInteractEvent e) {
            if (e.getHand().equals(EquipmentSlot.HAND) && e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                Player player = e.getPlayer();
                AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
                Location blockLocation = e.getClickedBlock().getLocation();
                TaskSettings taskSettings = Game.getInstance().getTaskSettings(blockLocation);
                if (taskSettings != null) {
                    Task task = auPlayer.getTask(taskSettings.id);
                    if (task != null && !task.isFinished() && !task.isFake()) {
                        task.execute();
                    }
                }
            }
        }

        /**
         * Event triggered when a player finishes a task.
         *
         * @param e Event
         */
        @EventHandler
        public void onTaskFinish(TaskFinishEvent e) {
            AmongUsPlayer auPlayer = e.getAmongUsPlayer();
            Player player = (Player) auPlayer.toBukkitPlayer();
            player.sendTitle("§aTask completed", null, 5, 30, 5);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, SoundCategory.AMBIENT, 1.0F, 0.6F);
            Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(), () -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, SoundCategory.AMBIENT, 1.0F, 0.8F), 3L);
            Game.getInstance().checkEndGame();
        }
    }
}
