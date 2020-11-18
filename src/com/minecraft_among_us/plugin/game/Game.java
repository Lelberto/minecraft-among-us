package com.minecraft_among_us.plugin.game;

import com.minecraft_among_us.plugin.Plugin;
import com.minecraft_among_us.plugin.config.ConfigurationManager;
import com.minecraft_among_us.plugin.config.TaskSettings;
import com.minecraft_among_us.plugin.tasks.Task;
import com.minecraft_among_us.plugin.tasks.TaskType;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Game class.
 *
 * This is the game core.
 */
public class Game {

    public static final int MIN_PLAYERS = 4;
    public static final int MAX_PLAYERS = 10;
    public static final String TECHNICAL_TTEAM_NAME = "technical_team";
    private static Game INSTANCE;

    /**
     * Gets the game instance.
     *
     * @return Game instance
     */
    public static Game getInstance() {
        if (Game.INSTANCE == null) {
            Game.INSTANCE = new Game();
        }
        return Game.INSTANCE;
    }

    private boolean devMode;
    private GameSettings settings;
    private GameState state;
    private final List<AmongUsPlayer> players;
    private VoteSystem currentVoteSystem;
    private final Team technicalTeam;
    private final BossBar taskBar;
    private final BossBar votingBar;
    private int startCooldown;

    /**
     * Creates a new game.
     */
    private Game() {
        this.devMode = false;
        this.settings = new GameSettings();
        this.state = GameState.HUB;
        this.players = new ArrayList<>();
        this.currentVoteSystem = null;
        this.technicalTeam = this.registerTechnicalTeam();
        this.taskBar = Bukkit.createBossBar("Tasks completed", BarColor.GREEN, BarStyle.SEGMENTED_10, BarFlag.DARKEN_SKY, BarFlag.CREATE_FOG);
        this.votingBar = Bukkit.createBossBar("Voting time", BarColor.WHITE, BarStyle.SOLID);
        this.startCooldown = 5;
    }

    /**
     * Registers the technical team.
     *
     * @return Technical team
     */
    private Team registerTechnicalTeam() {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(Game.TECHNICAL_TTEAM_NAME);
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        return team;
    }

    /**
     * Starts the game.
     */
    public void start() {
        //if (this.players.size() >= Game.MIN_PLAYERS) {
            this.state = GameState.IN_PROGRESS;
            Bukkit.getScheduler().runTaskTimer(Plugin.getPlugin(), (task) -> {
                if (this.startCooldown > 0) {
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendTitle("§e" + this.startCooldown, null, 5, 10, 5));
                    this.startCooldown--;
                } else {
                    this.selectRoles();
                    this.selectTasks();
                    this.taskBar.setProgress(0.0);

                    Bukkit.getOnlinePlayers().forEach(player -> {
                        AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 0, false, false, false));
                        player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRIGGER, SoundCategory.AMBIENT, 1.0F, 0.7F);
                        player.sendTitle(
                                auPlayer.isCrewmate() ? "§bCrewmate" : "§4Impostor",
                                auPlayer.isCrewmate() ? "§7Finish your tasks or find impostor(s)" : "§7Kill crewmates without showing you",
                                20, 60, 20);
                        this.taskBar.addPlayer(player);
                    });

                    // Teleportation to the map
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

    /**
     * Stops the game.
     */
    public void stop() {
        Bukkit.broadcastMessage("game ends");
        this.technicalTeam.unregister();
        this.taskBar.removeAll();
    }

    /**
     * Selects roles randomly for players.
     */
    private void selectRoles() {
        Random rand = new Random();
        List<AmongUsPlayer> impostors = new ArrayList<>();
        while (impostors.size() != this.settings.impostors) {
            AmongUsPlayer selectedImpostor = this.players.get(rand.nextInt(this.players.size()));
            if (!impostors.contains(selectedImpostor)) {
                impostors.add(selectedImpostor);
                selectedImpostor.setImpostor();
            }
        }
        this.players.stream().filter(auPlayer -> !impostors.contains(auPlayer)).forEach(AmongUsPlayer::setCrewmate);
    }

    /**
     * Selects tasks randomly for players.
     */
    private void selectTasks() {
        Random rand = new Random();
        List<TaskSettings> tasks = ConfigurationManager.getInstance().taskSettings;
        List<TaskSettings> commonTasks = tasks.stream().filter(task -> task.enabled && task.type.equals(TaskType.COMMON)).collect(Collectors.toList());
        List<TaskSettings> shortTasks = tasks.stream().filter(task -> task.enabled && task.type.equals(TaskType.SHORT)).collect(Collectors.toList());
        List<TaskSettings> longTasks = tasks.stream().filter(task -> task.enabled && task.type.equals(TaskType.LONG)).collect(Collectors.toList());

        // Common tasks
        for (int i = 0; i < this.settings.commonTasks; i++) {
            TaskSettings commonTask = commonTasks.remove(rand.nextInt(commonTasks.size()));
            this.getPlayers().forEach(auPlayer -> auPlayer.getTasks().add(Task.createTask(auPlayer, commonTask.id, auPlayer.isImpostor())));
        }

        // Short tasks
        this.getPlayers().forEach(auPlayer -> {
            List<TaskSettings> tasksClone = new ArrayList<>(shortTasks);
            for (int i = 0; i < this.settings.shortTasks; i++) {
                auPlayer.getTasks().add(Task.createTask(auPlayer, tasksClone.remove(rand.nextInt(tasksClone.size())).id, auPlayer.isImpostor()));
            }
        });

        // Long tasks
        this.getPlayers().forEach(auPlayer -> {
            List<TaskSettings> tasksClone = new ArrayList<>(longTasks);
            for (int i = 0; i < this.settings.longTasks; i++) {
                auPlayer.getTasks().add(Task.createTask(auPlayer, tasksClone.get(rand.nextInt(tasksClone.size())).id, auPlayer.isImpostor()));
            }
        });
    }

    /**
     * Checks if the game is ended.
     */
    public void checkEndGame() {
        List<Task> allTasks = this.getAllTasks().stream().filter(currentTask -> !currentTask.isFake()).collect(Collectors.toList());
        List<Task> finishedTasks = allTasks.stream().filter(Task::isFinished).collect(Collectors.toList());
        this.getTaskBar().setProgress((double) finishedTasks.size() / (double) allTasks.size());
        if (this.getCrewmates().size() == this.getImpostors().size()) {
            // TODO Impostors wins
            this.stop();
        } else if (allTasks.stream().filter(currentTask -> !currentTask.isFake()).count() == finishedTasks.size()) {
            // TODO Crewmates wins
        }
    }

    /**
     * Gets dev mode.
     *
     * @return Dev mode
     */
    public boolean isDevMode() {
        return this.devMode;
    }

    /**
     * Sets dev mode.
     *
     * @param devMode Dev mode
     */
    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

    /**
     * Gets the game settings.
     *
     * @return Game settings
     */
    public GameSettings getSettings() {
        return settings;
    }

    /**
     * Gets the game state.
     *
     * @return Game state
     */
    public GameState getState() {
        return state;
    }

    /**
     * Sets the game state.
     *
     * @param state Game state to set
     */
    public void setState(GameState state) {
        this.state = state;
    }

    /**
     * Gets all players in the game.
     *
     * @return Players in the game
     */
    public List<AmongUsPlayer> getPlayers() {
        return players;
    }

    /**
     * Gets crewmates.
     *
     * @return Crewmates
     */
    public List<AmongUsPlayer> getCrewmates() {
        return this.players.stream().filter(AmongUsPlayer::isCrewmate).collect(Collectors.toList());
    }

    /**
     * Gets impostors.
     *
     * @return Impostor
     */
    public List<AmongUsPlayer> getImpostors() {
        return this.players.stream().filter(AmongUsPlayer::isImpostor).collect(Collectors.toList());
    }

    /**
     * Gets the current vote system.
     *
     * @return Current vote system
     */
    public VoteSystem getCurrentVoteSystem() {
        return currentVoteSystem;
    }

    /**
     * Sets the current vote system.
     *
     * @param currentVoteSystem Current vote system
     */
    public void setCurrentVoteSystem(VoteSystem currentVoteSystem) {
        this.currentVoteSystem = currentVoteSystem;
    }

    /**
     * Gets the technical team.
     *
     * @return Technical team
     */
    public Team getTechnicalTeam() {
        return technicalTeam;
    }

    /**
     * Gets the task bar.
     *
     * @return Task bar
     */
    public BossBar getTaskBar() {
        return taskBar;
    }

    /**
     * Gets the voting bar.
     *
     * @return Voting bar
     */
    public BossBar getVotingBar() {
        return votingBar;
    }

    /**
     * Gets the task settings for a task ID.
     *
     * @param taskId Task ID
     * @return task settings for the specified task ID
     */
    public TaskSettings getTaskSettings(int taskId) {
        for (TaskSettings settings : ConfigurationManager.getInstance().taskSettings) {
            if (settings.id == taskId) {
                return settings;
            }
        }
        return null;
    }

    /**
     * Gets the task settings for a task location.
     *
     * @param taskLocation Task location
     * @return task settings for the specified task location
     */
    public TaskSettings getTaskSettings(Location taskLocation) {
        for (TaskSettings settings : ConfigurationManager.getInstance().taskSettings) {
            if (settings.location.equals(taskLocation)) {
                return settings;
            }
        }
        return null;
    }

    /**
     * Gets all tasks.
     *
     * The list returned contains all tasks given to players, not all tasks registered in the game configuration.
     *
     * @return Current tasks
     */
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        this.players.forEach(auPlayer -> tasks.addAll(auPlayer.getTasks()));
        return tasks;
    }

    /**
     * Gets available colors.
     *
     * A color is available when nobody select it.
     *
     * @return Available colors
     */
    public List<Color> getAvailableColors() {
        List<Color> availableColors = new ArrayList<>(Arrays.asList(Color.values()));
        players.stream().forEach(player -> availableColors.remove(player.getColor()));
        return availableColors;
    }

    /**
     * Returns a random available color.
     *
     * @return Random available color
     */
    public Color randomColor() {
        List<Color> availableColors = getAvailableColors();
        Random r = new Random();
        return availableColors.get(r.nextInt(availableColors.size()));
    }

    /**
     * Gets vent group of a vent location.
     *
     * @param ventLocation Vent location
     * @return Vent group of the specified vent location, or {@code null} if no vent group has been found
     */
    public List<Location> getVentgroup(Location ventLocation) {
        for (List<Location> ventGroup : ConfigurationManager.getInstance().vents) {
            if (ventGroup.contains(ventLocation)) {
                return ventGroup;
            }
        }
        return null;
    }


    /**
     * Listener subclass.
     */
    public static class Listener implements org.bukkit.event.Listener {

        /**
         * Event triggered when the food level changes.
         *
         * @param e Event
         */
        @EventHandler
        public void onFoodLevelChange(FoodLevelChangeEvent e) {
            e.setCancelled(true);
        }

        /**
         * Event triggered when an entity damage another entity.
         *
         * @param e Event
         */
        @EventHandler
        public void onDamage(EntityDamageByEntityEvent e) {
            if (!Game.getInstance().isDevMode()) {
                e.setCancelled(true);
            }
        }

        /**
         * Event triggered when a player interacts with an entity.
         *
         * @param e Event
         */
        @EventHandler
        public void onDamage(PlayerInteractAtEntityEvent e) {
            if (!Game.getInstance().isDevMode()) {
                e.setCancelled(true);
            }
        }

        /**
         * Event triggered when a player interacts with a block.
         *
         * @param e Event
         */
        @EventHandler
        public void onDamage(PlayerInteractEvent e) {
            if (!Game.getInstance().isDevMode()) {
                e.setCancelled(true);
            }
        }

        /**
         * Event triggered when a player swaps items in hand.
         *
         * @param e Event
         */
        @EventHandler
        public void onSwapHand(PlayerSwapHandItemsEvent e) {
            if (!Game.getInstance().isDevMode()) {
                e.setCancelled(true);
            }
        }

        /**
         * Event triggered when a player drops an item.
         *
         * @param e Event
         */
        @EventHandler
        public void onDrop(PlayerDropItemEvent e) {
            if (!Game.getInstance().isDevMode()) {
                e.setCancelled(true);
            }
        }
    }
}
