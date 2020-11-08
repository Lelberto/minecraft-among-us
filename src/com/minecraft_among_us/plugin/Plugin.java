package com.minecraft_among_us.plugin;

import com.minecraft_among_us.plugin.game.Game;
import com.minecraft_among_us.plugin.inventories.ColorInventory;
import com.minecraft_among_us.plugin.inventories.ComputerInventory;
import com.minecraft_among_us.plugin.inventories.GameSettingsInventory;
import com.minecraft_among_us.plugin.inventories.HatInventory;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

    public static org.bukkit.plugin.Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin(Plugin.getPluginName());
    }

    public static void log(String msg) {
        System.out.println("[" + Plugin.getPluginName() + "] " + msg);
    }

    public static String getPluginName() {
        return "MinecraftAmongUs";
    }

    public static String getPluginNameChat() {
        return "§7[§aMinecraft§cAmongUs§7] §r";
    }

    public static World getDefaultWorld() {
        return Bukkit.getWorlds().get(0);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        // Listeners registration
        Bukkit.getPluginManager().registerEvents(new Game.Listener(), this);
        Bukkit.getPluginManager().registerEvents(new ComputerInventory.Listener(), this);
        Bukkit.getPluginManager().registerEvents(new HatInventory.Listener(), this);
        Bukkit.getPluginManager().registerEvents(new GameSettingsInventory.Listener(), this);
        Bukkit.getPluginManager().registerEvents(new ColorInventory.Listener(), this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        //ConfigurationManager.getInstance().save();
    }
}
