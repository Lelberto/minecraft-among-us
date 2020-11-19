package com.minecraft_among_us.plugin.commands;

import com.minecraft_among_us.plugin.Plugin;
import com.minecraft_among_us.plugin.game.Game;
import com.minecraft_among_us.plugin.game.GameState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * Game command class.
 *
 * This command is used to manage the game.
 */
public class GameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender || sender.isOp()) {
            Game game = Game.getInstance();
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("start")) {
                    if (game.getState().equals(GameState.HUB)) {
                        game.start();
                        return true;
                    }
                    sender.sendMessage(Plugin.getPluginNameChat() + "§cGame is already started");
                    return false;
                }
                if (args[0].equalsIgnoreCase("devmode")) {
                    if (args.length == 1) {
                        sender.sendMessage(Plugin.getPluginNameChat() + "Dev mode is " + game.isDevMode());
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("on")) {
                        game.setDevMode(true);
                        sender.sendMessage(Plugin.getPluginNameChat() + "Dev mode is now on");
                        return true;
                    } else if (args[1].equalsIgnoreCase("off")) {
                        game.setDevMode(false);
                        sender.sendMessage(Plugin.getPluginNameChat() + "Dev mode is now off");
                        return true;
                    }
                    sender.sendMessage(Plugin.getPluginNameChat() + "Dev mode is " + game.isDevMode());
                    return false;
                }
            }
            sender.sendMessage("§cIncorrect usage");
            return false;
        }
        sender.sendMessage("§cPermission denied");
        return false;
    }
}
