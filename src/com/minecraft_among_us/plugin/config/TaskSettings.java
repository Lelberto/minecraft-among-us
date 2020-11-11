package com.minecraft_among_us.plugin.config;

import com.minecraft_among_us.plugin.tasks.TaskType;
import org.bukkit.Location;

public class TaskSettings {

    public final String name;
    public final String description;
    public final TaskType type;
    public final Location location;

    public TaskSettings(String name, String description, TaskType type, Location location) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.location = location;
    }
}
