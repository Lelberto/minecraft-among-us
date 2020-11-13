package com.minecraft_among_us.plugin.event;

import com.minecraft_among_us.plugin.game.AmongUsPlayer;
import com.minecraft_among_us.plugin.tasks.Task;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Task finish event class.
 *
 * This event is called when a player finishes a task.
 */
public class TaskFinishEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final Task task;
    private final AmongUsPlayer auPlayer;

    /**
     * Creates a new task finish event.
     *
     * @param task Finished task
     * @param auPlayer Player who finished task
     */
    public TaskFinishEvent(Task task, AmongUsPlayer auPlayer) {
        this.task = task;
        this.auPlayer = auPlayer;
    }

    /**
     * Returns the finished task.
     *
     * @return Finished task
     */
    public Task getTask() {
        return task;
    }

    /**
     * Returns the player.
     *
     * @return Player
     */
    public AmongUsPlayer getAmongUsPlayer() {
        return auPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
