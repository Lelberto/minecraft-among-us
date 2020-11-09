package com.minecraft_among_us.plugin.tasks;

import com.minecraft_among_us.plugin.AmongUsPlayer;

public abstract class Task {

    protected final String name;
    protected final String description;
    protected final TaskType type;
    protected AmongUsPlayer auPlayer;
    protected boolean finished;

    public Task(String name, String description, TaskType type, AmongUsPlayer auPlayer) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.finished = false;
        this.auPlayer = auPlayer;
    }

    public abstract void execute();

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void finish() {
        this.finished = true;
    }

    public TaskType getType() {
        return type;
    }

    public boolean isFinished() {
        return finished;
    }
}
