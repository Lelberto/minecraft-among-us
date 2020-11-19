package com.minecraft_among_us.plugin;

import com.minecraft_among_us.plugin.commands.GameCommand;
import com.minecraft_among_us.plugin.commands.TestCommand;
import com.minecraft_among_us.plugin.game.AmongUsPlayer;
import com.minecraft_among_us.plugin.game.Game;
import com.minecraft_among_us.plugin.inventories.*;
import com.minecraft_among_us.plugin.tasks.SimonTask;
import com.minecraft_among_us.plugin.tasks.Task;
import com.minecraft_among_us.plugin.tasks.TemperatureColdTask;
import com.minecraft_among_us.plugin.tasks.TemperatureHotTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin class.
 *
 * This class is the plugin entry point.
 */
public class Plugin extends JavaPlugin {

    /**
     * Gets the plugin.
     *
     * @return Plugin
     */
    public static org.bukkit.plugin.Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin(Plugin.getPluginName());
    }

    /**
     * Logs a message.
     *
     * @param msg Message to log
     */
    public static void log(String msg) {
        System.out.println("[" + Plugin.getPluginName() + "] " + msg);
    }

    /**
     * Gets the plugin name.
     *
     * @return Plugin name
     */
    public static String getPluginName() {
        return "MinecraftAmongUs";
    }

    /**
     * Gets the plugin name for chat (with colors).
     *
     * @return Plugin name for chat
     */
    public static String getPluginNameChat() {
        return "§7[§9" + Plugin.getPluginName() + "§7] §r";
    }

    /**
     * Gets the default world.
     *
     * @return Default world
     */
    public static World getDefaultWorld() {
        return Bukkit.getWorlds().get(0);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        // NMS initialization
        Nms.init();

        // Commands registration
        this.getCommand("game").setExecutor(new GameCommand());
        this.getCommand("test").setExecutor(new TestCommand());

        // Listeners registration
        Bukkit.getPluginManager().registerEvents(new Game.Listener(), this);
        Bukkit.getPluginManager().registerEvents(new AmongUsPlayer.Listener(), this);
        Bukkit.getPluginManager().registerEvents(new ComputerInventory.Listener(), this);
        Bukkit.getPluginManager().registerEvents(new HatInventory.Listener(), this);
        Bukkit.getPluginManager().registerEvents(new GameSettingsInventory.Listener(), this);
        Bukkit.getPluginManager().registerEvents(new ColorInventory.Listener(), this);
        Bukkit.getPluginManager().registerEvents(new VoteInventory.Listener(), this);
        Bukkit.getPluginManager().registerEvents(new Task.Listener(), this);
        Bukkit.getPluginManager().registerEvents(new SimonTask.Listener(), this);
        Bukkit.getPluginManager().registerEvents(new TemperatureHotTask.Listener(), this);
        Bukkit.getPluginManager().registerEvents(new TemperatureColdTask.Listener(), this);

        // TODO Used for debug only, remove it in production
        // Add online players in game to avoid reconnect
        Game game = Game.getInstance();
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.setCollidable(false);
            game.getPlayers().add(new AmongUsPlayer(player.getUniqueId(), game.randomColor()));
            game.getTechnicalTeam().addEntry(player.getName());
        });
    }

    @Override
    public void onDisable() {
        super.onDisable();
        // TODO Used for debug only, remove it in production
        Game.getInstance().getTechnicalTeam().unregister();
        Game.getInstance().getTaskBar().removeAll();
    }
}
