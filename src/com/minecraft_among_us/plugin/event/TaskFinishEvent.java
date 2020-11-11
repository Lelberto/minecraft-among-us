package com.minecraft_among_us.plugin.event;

import com.minecraft_among_us.plugin.AmongUsPlayer;
import com.minecraft_among_us.plugin.tasks.Task;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TaskFinishEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final Task task;
    private final AmongUsPlayer auPlayer;

    public TaskFinishEvent(Task task, AmongUsPlayer auPlayer) {
        this.task = task;
        this.auPlayer = auPlayer;
    }

    public Task getTask() {
        return task;
    }

    public AmongUsPlayer getAmongUsPlayer() {
        return auPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
