package com.minecraft_among_us.plugin.config;

import com.minecraft_among_us.plugin.Plugin;
import com.minecraft_among_us.plugin.tasks.TaskType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
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
    public Location computerLocation;
    public List<List<Location>> vents;
    public TaskSettings temperatureHotTaskSettings;
    public TaskSettings temperatureColdTaskSettings;
    public TaskSettings simonTaskSettings;

    private ConfigurationManager(File configFile) {
        this.configFile = configFile;
        this.hubSpawn = new Location(Plugin.getDefaultWorld(), 0, 100, 0);
        this.computerLocation = new Location(Plugin.getDefaultWorld(), 0, 100, 0);
        this.vents = Arrays.asList(
                Arrays.asList(
                        new Location(Plugin.getDefaultWorld(), 10, 100, 0),
                        new Location(Plugin.getDefaultWorld(), 20, 100, 0)
                ),
                Arrays.asList(
                        new Location(Plugin.getDefaultWorld(), 0, 100, 10),
                        new Location(Plugin.getDefaultWorld(), 0, 100, 20)
                )
        );
        this.temperatureHotTaskSettings = new TaskSettings("Lava temperature log", "Update the temperature log", TaskType.SHORT, new Location(Plugin.getDefaultWorld(), 0, 100, 0));
        this.temperatureColdTaskSettings = new TaskSettings("Laboratory temperature log", "Update the temperature log", TaskType.SHORT, new Location(Plugin.getDefaultWorld(), 0, 100, 0));
        this.simonTaskSettings = new TaskSettings("Simon", "Memorize and repeat the Simon", TaskType.LONG, new Location(Plugin.getDefaultWorld(), 0.0, 100.0, 0.0));

        // Configuration file creation
        try {
            if (!configFile.exists()) {
                YamlConfiguration config = new YamlConfiguration();
                ConfigurationSection hubSection = config.createSection("hub");
                hubSection.set("spawn", this.hubSpawn);
                ConfigurationSection computerSection = config.createSection("computer");
                computerSection.set("location", this.computerLocation);
                config.set("vents", this.vents);
                ConfigurationSection tasksSection = config.createSection("tasks");
                ConfigurationSection temperatureHotTaskSection = tasksSection.createSection("temperatureHot");
                temperatureHotTaskSection.set("name", this.temperatureHotTaskSettings.name);
                temperatureHotTaskSection.set("description", this.temperatureHotTaskSettings.description);
                temperatureHotTaskSection.set("type", this.temperatureHotTaskSettings.type.name().toLowerCase());
                temperatureHotTaskSection.set("location", this.temperatureHotTaskSettings.location);
                ConfigurationSection temperatureColdTaskSection = tasksSection.createSection("temperatureCold");
                temperatureColdTaskSection.set("name", this.temperatureColdTaskSettings.name);
                temperatureColdTaskSection.set("description", this.temperatureColdTaskSettings.description);
                temperatureColdTaskSection.set("type", this.temperatureColdTaskSettings.type.name().toLowerCase());
                temperatureColdTaskSection.set("location", this.temperatureColdTaskSettings.location);
                ConfigurationSection simonTaskSection = tasksSection.createSection("simon");
                simonTaskSection.set("name", simonTaskSettings.name);
                simonTaskSection.set("description", simonTaskSettings.description);
                simonTaskSection.set("type", simonTaskSettings.type.name().toLowerCase());
                simonTaskSection.set("location", simonTaskSettings.location);
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
        ConfigurationSection computerSection = config.getConfigurationSection("computer");
        this.computerLocation = computerSection.getLocation("location");
        this.vents = (List<List<Location>>) config.get("vents");
        ConfigurationSection tasksSection = config.getConfigurationSection("tasks");
        ConfigurationSection temperatureHotTaskSection = tasksSection.getConfigurationSection("temperatureHot");
        this.temperatureHotTaskSettings = new TaskSettings(
                temperatureHotTaskSection.getString("name"),
                temperatureHotTaskSection.getString("description"),
                TaskType.valueOf(temperatureHotTaskSection.getString("type").toUpperCase()),
                temperatureHotTaskSection.getLocation("location"));
        ConfigurationSection temperatureColdTaskSection = tasksSection.getConfigurationSection("temperatureCold");
        this.temperatureColdTaskSettings = new TaskSettings(
                temperatureColdTaskSection.getString("name"),
                temperatureColdTaskSection.getString("description"),
                TaskType.valueOf(temperatureColdTaskSection.getString("type").toUpperCase()),
                temperatureColdTaskSection.getLocation("location"));
        ConfigurationSection simonTaskSection = tasksSection.getConfigurationSection("simon");
        this.simonTaskSettings = new TaskSettings(
                simonTaskSection.getString("name"),
                simonTaskSection.getString("description"),
                TaskType.valueOf(simonTaskSection.getString("type").toUpperCase()),
                simonTaskSection.getLocation("location")
        );
        Bukkit.broadcastMessage(ConfigurationManager.getInstance().simonTaskSettings.location.toString());
    }
}
