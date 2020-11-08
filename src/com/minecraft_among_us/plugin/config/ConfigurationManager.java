package com.minecraft_among_us.plugin.config;

import com.minecraft_among_us.plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

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

    private ConfigurationManager(File configFile) {
        this.configFile = configFile;
        this.hubSpawn = new Location(Plugin.getDefaultWorld(), 0, 100, 0);

        // Configuration file creation
        try {
            YamlConfiguration config = new YamlConfiguration();
            ConfigurationSection hubSection = config.createSection("hub");
            hubSection.set("spawn", this.hubSpawn);
            config.save(configFile);
        } catch (IOException ex) {
            Plugin.log("Could not create the configuration file : " + ex.getMessage());
        }
    }

    public void load() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(this.configFile);
        ConfigurationSection hubSection = config.getConfigurationSection("hub");
        this.hubSpawn = hubSection.getLocation("spawn");
    }

    public void save() {
        YamlConfiguration config = new YamlConfiguration();
        ConfigurationSection hubSection = config.createSection("hub");
        hubSection.set("spawn", this.hubSpawn);

        try {
            config.save(this.configFile);
        } catch (IOException ex) {
            Plugin.log("Could not save the configuration file : " + ex.getMessage());
        }
    }
}
