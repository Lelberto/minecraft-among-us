package com.minecraft_among_us.plugin.config;

import com.minecraft_among_us.plugin.Plugin;
import com.minecraft_among_us.plugin.tasks.TaskType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigurationManager {

    private static ConfigurationManager INSTANCE;

    public static ConfigurationManager getInstance() {
        if (ConfigurationManager.INSTANCE == null) {
            ConfigurationManager.INSTANCE = new ConfigurationManager(new File(Bukkit.getPluginManager().getPlugin(Plugin.getPluginName()).getDataFolder(), "config.yml"));
            ConfigurationManager.INSTANCE.load();
        }
        return ConfigurationManager.INSTANCE;
    }

    private final File configFile;
    public Location hubSpawn;
    public List<Location> mapSpawns;
    public Location computerLocation;
    public Location emergencyLocation;
    public List<List<Location>> vents;
    public List<TaskSettings> taskSettings;

    private ConfigurationManager(File configFile) {
        this.configFile = configFile;
        this.hubSpawn = new Location(Plugin.getDefaultWorld(), 0, 0, 0);
        this.mapSpawns = Arrays.asList(
                new Location(Plugin.getDefaultWorld(), 0.0, 0.0, 0.0),
                new Location(Plugin.getDefaultWorld(), 0.0, 0.0, 0.0),
                new Location(Plugin.getDefaultWorld(), 0.0, 0.0, 0.0),
                new Location(Plugin.getDefaultWorld(), 0.0, 0.0, 0.0),
                new Location(Plugin.getDefaultWorld(), 0.0, 0.0, 0.0),
                new Location(Plugin.getDefaultWorld(), 0.0, 0.0, 0.0),
                new Location(Plugin.getDefaultWorld(), 0.0, 0.0, 0.0),
                new Location(Plugin.getDefaultWorld(), 0.0, 0.0, 0.0),
                new Location(Plugin.getDefaultWorld(), 0.0, 0.0, 0.0),
                new Location(Plugin.getDefaultWorld(), 0.0, 0.0, 0.0)
        );
        this.computerLocation = new Location(Plugin.getDefaultWorld(), 0.0, 0.0, 0.0);
        this.emergencyLocation = new Location(Plugin.getDefaultWorld(), 0.0, 0.0, 0.0);
        this.vents = Arrays.asList(
                Arrays.asList(
                        new Location(Plugin.getDefaultWorld(), 0.0, 0.0, 0.0),
                        new Location(Plugin.getDefaultWorld(), 0.0, 0.0, 0.0)
                ),
                Arrays.asList(
                        new Location(Plugin.getDefaultWorld(), 0.0, 0.0, 0.0),
                        new Location(Plugin.getDefaultWorld(), 0.0, 0.0, 0.0)
                )
        );
        this.taskSettings = Arrays.asList(
                new TaskSettings(0, true, "Lava temperature log", "Update the temperature log", TaskType.SHORT, new Location(Plugin.getDefaultWorld(), 0.0, 0.0, 0.0)),
                new TaskSettings(1, true, "Laboratory temperature log", "Update the temperature log", TaskType.SHORT, new Location(Plugin.getDefaultWorld(), 0.0, 0.0, 0.0)),
                new TaskSettings(2, true, "Simon", "Memorize and repeat the Simon", TaskType.LONG, new Location(Plugin.getDefaultWorld(), 0.0, 0.0, 0.0))
        );

        // Configuration file creation
        try {
            if (!configFile.exists()) {
                YamlConfiguration config = new YamlConfiguration();
                ConfigurationSection hubSection = config.createSection("hub");
                hubSection.set("spawn", this.hubSpawn);
                ConfigurationSection mapSection = config.createSection("map");
                mapSection.set("spawns", this.mapSpawns);
                ConfigurationSection computerSection = config.createSection("computer");
                computerSection.set("location", this.computerLocation);
                ConfigurationSection emergencySection = config.createSection("emergency");
                emergencySection.set("location", this.emergencyLocation);
                config.set("vents", this.vents);
                ConfigurationSection tasksSection = config.createSection("tasks");
                this.taskSettings.forEach(taskSettings -> {
                    ConfigurationSection taskSettingsSection = tasksSection.createSection("" + taskSettings.id);
                    taskSettingsSection.set("name", taskSettings.name);
                    taskSettingsSection.set("description", taskSettings.description);
                    taskSettingsSection.set("type", taskSettings.type.name().toLowerCase());
                    taskSettingsSection.set("location", taskSettings.location);
                });
                config.save(configFile);
            }
        } catch (IOException ex) {
            Plugin.log("Could not create the configuration file : " + ex.getMessage());
        }
    }

    public void load() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(this.configFile);
        ConfigurationSection hubSection = config.getConfigurationSection("hub");
        this.hubSpawn = hubSection.getLocation("spawn");
        ConfigurationSection mapSection = config.getConfigurationSection("map");
        this.mapSpawns = (List<Location>) mapSection.getList("spawns");
        ConfigurationSection computerSection = config.getConfigurationSection("computer");
        this.computerLocation = computerSection.getLocation("location");
        ConfigurationSection emergencySection = config.getConfigurationSection("emergency");
        this.emergencyLocation = emergencySection.getLocation("location");
        this.vents = (List<List<Location>>) config.get("vents");
        ConfigurationSection tasksSection = config.getConfigurationSection("tasks");
        this.taskSettings = new ArrayList<>();
        tasksSection.getKeys(false).forEach(taskId -> {
            ConfigurationSection taskSettingsSection = tasksSection.getConfigurationSection(taskId);
            this.taskSettings.add(new TaskSettings(
                    Integer.parseInt(taskId),
                    taskSettingsSection.getBoolean("enabled"),
                    taskSettingsSection.getString("name"),
                    taskSettingsSection.getString("description"),
                    TaskType.valueOf(taskSettingsSection.getString("type").toUpperCase()),
                    taskSettingsSection.getLocation("location")));
        });
    }
}
