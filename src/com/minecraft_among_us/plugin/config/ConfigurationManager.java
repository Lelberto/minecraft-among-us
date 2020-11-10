package com.minecraft_among_us.plugin.config;

import com.minecraft_among_us.plugin.Plugin;
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
    public Location computerLocation;
    public List<List<Location>> vents;

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

        // Configuration file creation
        try {
            if (!configFile.exists()) {
                YamlConfiguration config = new YamlConfiguration();
                ConfigurationSection hubSection = config.createSection("hub");
                hubSection.set("spawn", this.hubSpawn);
                ConfigurationSection computerSection = config.createSection("computer");
                computerSection.set("location", this.computerLocation);
                config.set("vents", this.vents);
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
    }
}
