package com.minecraft_among_us.plugin.game;

import com.minecraft_among_us.plugin.AmongUsPlayer;
import com.minecraft_among_us.plugin.Color;
import com.minecraft_among_us.plugin.Plugin;
import com.minecraft_among_us.plugin.config.ConfigurationManager;
import com.minecraft_among_us.plugin.config.TaskSettings;
import com.minecraft_among_us.plugin.inventories.ComputerInventory;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Game {

    public static final int MIN_PLAYERS = 4;
    public static final int MAX_PLAYERS = 10;
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
    private int startCooldown;

    private Game() {
        this.settings = new GameSettings();
        this.players = new ArrayList<>();
        this.state = GameState.HUB;
        this.startCooldown = 5;
    }

    public void start() {
        //if (this.players.size() >= Game.MIN_PLAYERS) {
            this.state = GameState.IN_PROGRESS;
            Bukkit.getScheduler().runTaskTimer(Plugin.getPlugin(), (task) -> {
                if (this.startCooldown > 0) {
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("§e" + this.startCooldown, null, 5, 10, 5));
                    this.startCooldown--;
                } else {
                    this.selectRoles();
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 0, false, false, false));
                        player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRIGGER, SoundCategory.AMBIENT, 1.0F, 0.75F);
                        player.sendTitle(
                                auPlayer.isCrewmate() ? "§bCrewmate" : "§4Impostor",
                                auPlayer.isCrewmate() ? "§7Finish your tasks or find impostor(s)" : "§7Kill crewmates without showing you",
                                20, 60, 20);
                    });
                    Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(), () -> {
                        List<Location> mapSpawns = ConfigurationManager.getInstance().mapSpawns;
                        int i = 0;
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.teleport(mapSpawns.get(i++));
                        }
                    }, 100L);
                    task.cancel();
                }
            }, 0L, 20L);
        //} else {
        //    Bukkit.broadcastMessage(Plugin.getPluginNameChat() + "4 players minimum are required to start the game");
        //}
    }

    private void selectRoles() {
        List<AmongUsPlayer> impostors = new ArrayList<>();
        Random rand = new Random();
        while (impostors.size() != this.settings.impostors) {
            AmongUsPlayer selectedImpostor = this.players.get(rand.nextInt(this.players.size()));
            if (!impostors.contains(selectedImpostor)) {
                impostors.add(selectedImpostor);
                selectedImpostor.setImpostor();
            }
        }
        this.players.stream().filter(auPlayer -> !impostors.contains(auPlayer)).forEach(AmongUsPlayer::setCrewmate);
    }

    private void selectTasks() {

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

    public List<Location> getVentgroup(Location ventLocation) {
        for (List<Location> ventGroup : ConfigurationManager.getInstance().vents) {
            if (ventGroup.contains(ventLocation)) {
                return ventGroup;
            }
        }
        return null;
    }

    public TaskSettings getTaskSettings(int taskId) {
        for (TaskSettings settings : ConfigurationManager.getInstance().taskSettings) {
            if (settings.id == taskId) {
                return settings;
            }
        }
        return null;
    }

    public TaskSettings getTaskSettings(Location taskLocation) {
        for (TaskSettings settings : ConfigurationManager.getInstance().taskSettings) {
            if (settings.location.equals(taskLocation)) {
                return settings;
            }
        }
        return null;
    }

    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onJoin(PlayerJoinEvent e) {
            Player player = e.getPlayer();
            Game game = Game.getInstance();
            if (game.getState() == GameState.HUB) {
                e.setJoinMessage("§7[§a+§7]§r §6" + player.getName());
                player.teleport(ConfigurationManager.getInstance().hubSpawn);
                player.setCollidable(false);
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
        public void onSprint(FoodLevelChangeEvent e) {
            e.setCancelled(true);
        }

        @EventHandler
        public void onOpenComputer(PlayerInteractEvent e) {
            if (Game.getInstance().getState().equals(GameState.HUB) && e.getHand().equals(EquipmentSlot.HAND) && e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getLocation().equals(ConfigurationManager.getInstance().computerLocation)) {
                Player player = e.getPlayer();
                player.openInventory(new ComputerInventory(AmongUsPlayer.getPlayer(player.getUniqueId())).create());
            }
        }
    }
}
