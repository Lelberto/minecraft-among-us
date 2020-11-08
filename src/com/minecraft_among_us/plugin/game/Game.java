package com.minecraft_among_us.plugin.game;

import com.minecraft_among_us.plugin.AmongUsPlayer;
import com.minecraft_among_us.plugin.Color;
import com.minecraft_among_us.plugin.config.ConfigurationManager;
import com.minecraft_among_us.plugin.inventories.ComputerInventory;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Game {

    private static Game INSTANCE;

    public static Game getInstance() {
        if (Game.INSTANCE == null) {
            Game.INSTANCE = new Game();
        }
        return Game.INSTANCE;
    }

    private GameSettings settings;
    private final List<AmongUsPlayer> players;
    private GameState state;

    private Game() {
        this.settings = new GameSettings();
        this.players = new ArrayList<>();
        this.state = GameState.HUB;
    }

    public void start() {

    }

    public void stop() {

    }

    public GameSettings getSettings() {
        return settings;
    }

    public List<AmongUsPlayer> getPlayers() {
        return players;
    }

    public GameState getState() {
        return state;
    }

    public List<Color> getAvailableColors() {
        List<Color> availableColors = new ArrayList<>(Arrays.asList(Color.values()));
        players.stream().forEach(player -> availableColors.remove(player.getColor()));
        return availableColors;
    }

    public Color randomColor() {
        List<Color> availableColors = getAvailableColors();
        Random r = new Random();
        return availableColors.get(r.nextInt(availableColors.size()));
    }

    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onJoin(PlayerJoinEvent e) {
            Player player = e.getPlayer();
            Game game = Game.getInstance();
            if (game.getState() == GameState.HUB) {
                e.setJoinMessage("§7[§a+§7]§r §6" + player.getName());
                player.teleport(ConfigurationManager.getInstance().hubSpawn);
                game.players.add(new AmongUsPlayer(player.getUniqueId(), game.randomColor()));
            } else {
                e.setJoinMessage(null);
                player.setGameMode(GameMode.SPECTATOR);
            }
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent e) {
            Game game = Game.getInstance();
            Player player = e.getPlayer();
            game.players.remove(AmongUsPlayer.getPlayer(player.getUniqueId()));
            e.setQuitMessage("§7[§c-§7]§r §6" + player.getName());
        }

        @EventHandler
        public void onOpenComputer(PlayerInteractEvent e) {
            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getLocation().equals(ConfigurationManager.getInstance().computerLocation)) {
                Player player = e.getPlayer();
                player.openInventory(new ComputerInventory(AmongUsPlayer.getPlayer(player.getUniqueId())).create());
            }
        }
    }
}
