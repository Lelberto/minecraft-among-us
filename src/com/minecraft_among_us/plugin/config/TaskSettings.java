package com.minecraft_among_us.plugin.config;

import com.minecraft_among_us.plugin.tasks.TaskType;
import org.bukkit.Location;

public class TaskSettings {

    public final int id;
    public final boolean enabled;
    public final String name;
    public final String description;
    public final TaskType type;
    public final Location location;

    public TaskSettings(int id, boolean enabled, String name, String description, TaskType type, Location location) {
        this.id = id;
        this.enabled = enabled;
        this.name = name;
        this.description = description;
        this.type = type;
        this.location = location;
    }

    @Override
    public String toString() {
        return "TaskSettings{" +
                "id=" + id +
                ", enabled=" + enabled +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", location=" + location +
                '}';
    }
}
