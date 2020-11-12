package com.minecraft_among_us.plugin.game;

import com.minecraft_among_us.plugin.AmongUsPlayer;
import com.minecraft_among_us.plugin.Color;
import com.minecraft_among_us.plugin.Plugin;
import com.minecraft_among_us.plugin.config.ConfigurationManager;
import com.minecraft_among_us.plugin.config.TaskSettings;
import com.minecraft_among_us.plugin.inventories.ComputerInventory;
import com.minecraft_among_us.plugin.tasks.Task;
import com.minecraft_among_us.plugin.tasks.TaskType;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

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
    private final BossBar taskBar;
    private int startCooldown;

    private Game() {
        this.settings = new GameSettings();
        this.players = new ArrayList<>();
        this.state = GameState.HUB;
        this.taskBar = Bukkit.createBossBar("Tasks completed", BarColor.GREEN, BarStyle.SEGMENTED_10);
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
                    this.selectTasks();
                    this.taskBar.setProgress(0.0);
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 0, false, false, false));
                        player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRIGGER, SoundCategory.AMBIENT, 1.0F, 0.75F);
                        player.sendTitle(
                                auPlayer.isCrewmate() ? "§bCrewmate" : "§4Impostor",
                                auPlayer.isCrewmate() ? "§7Finish your tasks or find impostor(s)" : "§7Kill crewmates without showing you",
                                20, 60, 20);
                        this.taskBar.addPlayer(player);
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
        Random rand = new Random();
        List<TaskSettings> tasks = ConfigurationManager.getInstance().taskSettings;
        List<TaskSettings> commonTasks = tasks.stream().filter(task -> task.enabled && task.type.equals(TaskType.COMMON)).collect(Collectors.toList());
        List<TaskSettings> shortTasks = tasks.stream().filter(task -> task.enabled && task.type.equals(TaskType.SHORT)).collect(Collectors.toList());
        List<TaskSettings> longTasks = tasks.stream().filter(task -> task.enabled && task.type.equals(TaskType.LONG)).collect(Collectors.toList());
        for (int i = 0; i < this.settings.commonTasks; i++) {
            TaskSettings commonTask = commonTasks.remove(rand.nextInt(commonTasks.size()));
            this.getPlayers().forEach(auPlayer -> auPlayer.getTasks().add(Task.createTask(auPlayer, commonTask.id, auPlayer.isImpostor())));
        }
        this.getPlayers().forEach(auPlayer -> {
            List<TaskSettings> tasksClone = new ArrayList<>(shortTasks);
            for (int i = 0; i < this.settings.shortTasks; i++) {
                auPlayer.getTasks().add(Task.createTask(auPlayer, tasksClone.remove(rand.nextInt(tasksClone.size())).id, auPlayer.isImpostor()));
            }
        });
        this.getPlayers().forEach(auPlayer -> {
            List<TaskSettings> tasksClone = new ArrayList<>(longTasks);
            for (int i = 0; i < this.settings.longTasks; i++) {
                auPlayer.getTasks().add(Task.createTask(auPlayer, tasksClone.get(rand.nextInt(tasksClone.size())).id, auPlayer.isImpostor()));
            }
        });
    }

    public void stop() {
        Bukkit.broadcastMessage("game ends");
    }

    public void createDead(AmongUsPlayer auPlayer) {
        Player player = (Player) auPlayer.toBukkitPlayer();
        ArmorStand as = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().subtract(new Vector(0.0, 1.4, 0.0)), EntityType.ARMOR_STAND);
        as.setMetadata("target", new FixedMetadataValue(Plugin.getPlugin(), player.getUniqueId().toString()));
        as.setGravity(false);
        as.setArms(true);
        as.setBasePlate(false);
        as.setBodyPose(new EulerAngle(275.0, 0.0, 0.0));
        as.setHeadPose(new EulerAngle(275.0, 0.0, 0.7));
        as.setLeftArmPose(new EulerAngle(275.0, 0.0, 0.0));
        as.setRightArmPose(new EulerAngle(275.0, 0.0, 0.0));

        ItemStack helmetItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta helmetItemMeta = (SkullMeta) helmetItem.getItemMeta();
        helmetItemMeta.setOwningPlayer(player);
        helmetItem.setItemMeta(helmetItemMeta);

        as.getEquipment().setHelmet(helmetItem);
        as.getEquipment().setChestplate(player.getEquipment().getChestplate());
    }

    public GameSettings getSettings() {
        return settings;
    }

    public List<AmongUsPlayer> getPlayers() {
        return players;
    }

    public List<AmongUsPlayer> getCrewmates() {
        return this.players.stream().filter(AmongUsPlayer::isCrewmate).collect(Collectors.toList());
    }

    public List<AmongUsPlayer> getImpostors() {
        return this.players.stream().filter(AmongUsPlayer::isImpostor).collect(Collectors.toList());
    }

    public GameState getState() {
        return state;
    }

    public BossBar getTaskBar() {
        return taskBar;
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        this.players.forEach(auPlayer -> tasks.addAll(auPlayer.getTasks()));
        return tasks;
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
                game.taskBar.addPlayer(player);
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
        public void onImpostorKill(EntityDamageByEntityEvent e) {
            if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
                e.setCancelled(true);
                Game game = Game.getInstance();
                Player impostor = (Player) e.getDamager();
                Player crewmate = (Player) e.getEntity();
                AmongUsPlayer auImpostor = AmongUsPlayer.getPlayer(impostor.getUniqueId());
                AmongUsPlayer auCrewmate = AmongUsPlayer.getPlayer(crewmate.getUniqueId());
                if (auImpostor.isImpostor() && auCrewmate.isCrewmate()) {
                    e.setCancelled(true);
                    crewmate.setGameMode(GameMode.SPECTATOR);
                    auCrewmate.setAlive(false);
                    game.createDead(auCrewmate);
                    if (game.getCrewmates().size() == game.getImpostors().size()) {
                        game.stop();
                    }
                }
            }
        }

        @EventHandler
        public void onDiscoverDeadBody(PlayerInteractAtEntityEvent e) {
            e.setCancelled(true);
            if (e.getHand().equals(EquipmentSlot.HAND) && e.getRightClicked().hasMetadata("target")) {
                AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(e.getPlayer().getUniqueId());
                AmongUsPlayer auTarget = AmongUsPlayer.getPlayer(UUID.fromString(e.getRightClicked().getMetadata("target").get(0).asString()));
                if (auPlayer.isAlive()) {
                    e.getRightClicked().remove();
                    Bukkit.broadcastMessage(auTarget.toBukkitPlayer().getName() + " finded by " + auPlayer.toBukkitPlayer().getName());
                }
            }
        }

        @EventHandler
        public void onFoodLevelChange(FoodLevelChangeEvent e) {
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
