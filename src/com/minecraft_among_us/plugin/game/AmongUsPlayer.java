package com.minecraft_among_us.plugin.game;

import com.minecraft_among_us.plugin.Plugin;
import com.minecraft_among_us.plugin.config.ConfigurationManager;
import com.minecraft_among_us.plugin.tasks.Task;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * AmongUs player class.
 *
 * This class is used to manage players in the game.
 */
public class AmongUsPlayer implements Comparable<AmongUsPlayer> {

    /**
     * Gets a player by his UUID.
     *
     * The player to search must be registered in the game (by joining).
     * A player who quits the game is unregistered.
     *
     * @param uuid UUID
     * @return Player with the specified UUID, or {@code null} if the player is not registered
     */
    public static AmongUsPlayer getPlayer(UUID uuid) {
        return Game.getInstance().getPlayers().stream().filter(auPlayer -> auPlayer.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    /**
     * Gets a player by his color.
     *
     * The player to search must be registered in the game (by joining).
     * A player who quits the game is unregistered.
     *
     * @param color Color
     * @return Player with the specified color, or {@code null} if the color is not used or if the player is not registered
     */
    public static AmongUsPlayer getPlayerByColor(Color color) {
        return Game.getInstance().getPlayers().stream().filter(auPlayer -> auPlayer.getColor().equals(color)).findFirst().orElse(null);
    }

    private final UUID uuid;
    private Color color;
    private ItemStack hat;
    private boolean alive;
    private boolean impostor;
    private List<Task> tasks;
    private List<Location> currentVentGroup;
    private Location currentVent;

    /**
     * Creates a new AmongUs player.
     *
     * @param uuid UUID provided from the Bukkit player
     * @param color Color
     */
    public AmongUsPlayer(UUID uuid, Color color) {
        this.uuid = uuid;
        this.color = color;
        this.hat = null;
        this.alive = true;
        this.impostor = false;
        this.tasks = new ArrayList<>();
        this.currentVentGroup = new ArrayList<>();
        this.currentVent = null;
        this.refresh();
    }

    /**
     * Refreshes the player (equipment, hotbar, attributes)
     */
    public void refresh() {
        this.refreshEquipment();
        this.refreshHotbar();
        this.refreshAttributes();
    }

    /**
     * Refreshes the equipment.
     */
    private void refreshEquipment() {
        PlayerInventory inventory = ((Player) this.toBukkitPlayer()).getInventory();

        if (!this.alive || this.isInVent()) {
            inventory.setHelmet(null);
            inventory.setChestplate(null);
            inventory.setLeggings(null);
            inventory.setBoots(null);
        } else {
            ItemStack helmetItem;
            if (this.hat == null) {
                helmetItem = new ItemStack(Material.LEATHER_HELMET);
                LeatherArmorMeta helmetItemMeta = (LeatherArmorMeta) helmetItem.getItemMeta();
                helmetItemMeta.setColor(org.bukkit.Color.fromRGB(color.red, color.green, color.blue));
                helmetItem.setItemMeta(helmetItemMeta);
            } else {
                helmetItem = this.hat.clone();
            }
            inventory.setHelmet(helmetItem);

            ItemStack chestplateItem = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta chestplateItemMeta = (LeatherArmorMeta) chestplateItem.getItemMeta();
            chestplateItemMeta.setColor(org.bukkit.Color.fromRGB(color.red, color.green, color.blue));
            chestplateItem.setItemMeta(chestplateItemMeta);
            inventory.setChestplate(chestplateItem);

            ItemStack leggingsItem = new ItemStack(Material.LEATHER_LEGGINGS);
            LeatherArmorMeta leggingsItemMeta = (LeatherArmorMeta) leggingsItem.getItemMeta();
            leggingsItemMeta.setColor(org.bukkit.Color.fromRGB(color.red, color.green, color.blue));
            leggingsItem.setItemMeta(leggingsItemMeta);
            inventory.setLeggings(leggingsItem);

            ItemStack bootsItem = new ItemStack(Material.LEATHER_BOOTS);
            LeatherArmorMeta bootsItemMeta = (LeatherArmorMeta) bootsItem.getItemMeta();
            bootsItemMeta.setColor(org.bukkit.Color.fromRGB(color.red, color.green, color.blue));
            bootsItem.setItemMeta(bootsItemMeta);
            inventory.setBoots(bootsItem);
        }
    }

    /**
     * Refreshes the hotbar.
     */
    private void refreshHotbar() {
        PlayerInventory inventory = ((Player) this.toBukkitPlayer()).getInventory();

        for (int i = 0; i < 9; i++) {
            if (!this.alive) {
                inventory.setItem(i, null);
            } else {
                if (GameState.isVote(Game.getInstance().getState())) {
                    if (i == 0) {
                        ItemStack voteItem = new ItemStack(Material.PAPER);
                        ItemMeta voteItemMeta = voteItem.getItemMeta();
                        voteItemMeta.setDisplayName("Vote");
                        voteItem.setItemMeta(voteItemMeta);
                        inventory.setItem(i, voteItem);
                    } else {
                        inventory.setItem(i, null);
                    }
                } else {
                    if (this.isInVent()) {
                        inventory.setItem(i, null);
                    } else {
                        ItemStack slotItem = new ItemStack(color.dye);
                        ItemMeta slotItemMeta = slotItem.getItemMeta();
                        if (this.impostor) {
                            switch (i) {
                                case 0:
                                    slotItemMeta.setDisplayName("Sabotage reactor");
                                    break;
                                default:
                                    slotItemMeta.setDisplayName("§f");
                                    break;
                            }
                        } else {
                            slotItemMeta.setDisplayName("§f");
                        }
                        slotItem.setItemMeta(slotItemMeta);
                        inventory.setItem(i, slotItem);
                    }
                }
            }
        }
    }

    /**
     * Refreshes attributes (potion effects, speed, etc...)
     */
    private void refreshAttributes() {
        Player player = (Player) this.toBukkitPlayer();

        if (this.alive && GameState.isVote(Game.getInstance().getState())) {
            player.setWalkSpeed(0.0F);
            player.setFoodLevel(6);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999, 200, false, false, false));
        } else {
            if (this.isInVent()) {
                player.setInvisible(true);
                player.setWalkSpeed(0.0F);
                player.setFoodLevel(6);
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999, 200, false, false, false));
            } else {
                player.setInvisible(false);
                player.setWalkSpeed(0.2F);
                player.setFoodLevel(20);
                player.removePotionEffect(PotionEffectType.JUMP);
            }
        }
    }

    /**
     * Creates a dead body with the player's equipment.
     */
    public void createDeadBody() {
        Player player = (Player) this.toBukkitPlayer();

        ArmorStand as = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().subtract(new Vector(0.0, 1.4, 0.0)), EntityType.ARMOR_STAND);
        as.setMetadata("dead_body", new FixedMetadataValue(Plugin.getPlugin(), player.getUniqueId().toString()));
        as.setGravity(false);
        as.setArms(true);
        as.setBasePlate(false);
        as.setBodyPose(new EulerAngle(275.0, 0.0, 0.0));
        as.setHeadPose(new EulerAngle(275.0, 0.0, 0.7));
        as.setLeftArmPose(new EulerAngle(275.0, 0.0, 0.0));
        as.setRightArmPose(new EulerAngle(275.0, 0.0, 0.0));

        ItemStack helmetItem;
        if (this.hat == null) {
            helmetItem = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta helmetItemMeta = (SkullMeta) helmetItem.getItemMeta();
            helmetItemMeta.setOwningPlayer(player);
            helmetItem.setItemMeta(helmetItemMeta);
        } else {
            helmetItem = this.hat.clone();
        }

        as.getEquipment().setHelmet(helmetItem);
        as.getEquipment().setChestplate(player.getEquipment().getChestplate());
    }

    /**
     * Gets UUID provided from the Bukkit player.
     *
     * @return UUID
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Gets the color.
     *
     * @return Color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color.
     *
     * @param color Color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Gets the hat.
     *
     * @return Hat, or {@code null} if the player has no hat
     */
    public ItemStack getHat() {
        return hat;
    }

    /**
     * Sets the hat.
     *
     * @param hat Hat
     */
    public void setHat(ItemStack hat) {
        this.hat = hat;
    }

    /**
     * Checks if the player is alive.
     *
     * @return True if the player is alive, false otherwise
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Sets alive.
     *
     * @param alive Alive
     */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     * Checks if the player is crewmate.
     *
     * @return True if the player is crewmate, false otherwise
     */
    public boolean isCrewmate() {
        return !this.impostor;
    }

    /**
     * Sets crewmate.
     */
    public void setCrewmate() {
        this.impostor = false;
    }

    /**
     * Checks if the player is impostor.
     *
     * @return True if the player is impostor, false otherwise
     */
    public boolean isImpostor() {
        return this.impostor;
    }

    /**
     * Sets crewmate.
     */
    public void setImpostor() {
        this.impostor = true;
    }

    /**
     * Gets tasks.
     *
     * @return Tasks
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Gets a task by it's ID.
     *
     * @param taskId Task ID
     * @return Task, or {@code null} if no task have been found
     */
    public Task getTask(int taskId) {
        return this.tasks.stream().filter(task -> task.getId() == taskId).findFirst().orElse(null);
    }

    /**
     * Gets a task by it's name.
     *
     * @param taskName Task name
     * @return Task, or {@code null} if no task have been found
     */
    public Task getTask(String taskName) {
        return this.tasks.stream().filter(task -> task.getSettings().name.equals(taskName)).findFirst().orElse(null);
    }

    /**
     * Gets the current vent group the player is in.
     *
     * @return Current vent group
     */
    public List<Location> getCurrentVentGroup() {
        return currentVentGroup;
    }

    /**
     * Sets the current vent group the player is in.
     *
     * @param currentVentGroup Current vent group
     */
    public void setCurrentVentGroup(List<Location> currentVentGroup) {
        this.currentVentGroup = currentVentGroup;
    }

    /**
     * Gets the current vent the player is in.
     *
     * @return Current vent the player is in
     */
    public Location getCurrentVent() {
        return currentVent;
    }

    /**
     * Sets the current vent the player is in.
     *
     * @param currentVent Current vent
     */
    public void setCurrentVent(Location currentVent) {
        this.currentVent = currentVent;
    }

    /**
     * Checks if the player is in vent.
     *
     * @return True if the player is in vent, false otherwise
     */
    public boolean isInVent() {
        return !currentVentGroup.isEmpty();
    }

    /**
     * Returns the Bukkit player associated with the player.
     *
     * @return Bukkit player associated with the player
     */
    public OfflinePlayer toBukkitPlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    @Override
    public int compareTo(AmongUsPlayer target) {
        if (this.alive && !target.alive) {
            return -1;
        }
        if (!this.alive && target.alive) {
            return 1;
        }
        return 0;
    }


    /**
     * Listener subclass.
     */
    public static class Listener implements org.bukkit.event.Listener {

        /**
         * Event triggered when a player joins the game.
         *
         * @param e Event
         */
        @EventHandler
        public void onJoin(PlayerJoinEvent e) {
            Player player = e.getPlayer();
            Game game = Game.getInstance();
            game.getTechnicalTeam().addEntry(player.getName());
            if (game.getState() == GameState.HUB) {
                e.setJoinMessage("§7[§a+§7]§r §6" + player.getName());
                player.setGameMode(GameMode.ADVENTURE);
                game.getPlayers().add(new AmongUsPlayer(player.getUniqueId(), game.randomColor()));
                player.teleport(ConfigurationManager.getInstance().hubSpawn);
                Bukkit.getOnlinePlayers().forEach(currentPlayer -> currentPlayer.playSound(currentPlayer.getLocation(), Sound.ENTITY_BAT_AMBIENT, SoundCategory.AMBIENT, 1.0F, 0.0F));
            } else {
                e.setJoinMessage(null);
                player.setGameMode(GameMode.SPECTATOR);
                game.getTaskBar().addPlayer(player);
            }
        }

        /**
         * Event triggered when a player quits the game.
         *
         * @param e Event
         */
        @EventHandler
        public void onQuit(PlayerQuitEvent e) {
            Game game = Game.getInstance();
            Player player = e.getPlayer();
            game.getPlayers().remove(AmongUsPlayer.getPlayer(player.getUniqueId()));
            if (game.getState().equals(GameState.HUB)) {
                e.setQuitMessage("§7[§c-§7]§r §6" + player.getName());
            } else {
                e.setQuitMessage(null);
                game.checkEndGame();
            }
        }

        /**
         * Event triggered when a player chats.
         *
         * @param e Event
         */
        @EventHandler
        public void onChat(AsyncPlayerChatEvent e) {
            Game game = Game.getInstance();
            Player player = e.getPlayer();
            AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
            if (!auPlayer.isAlive()) {
                e.setCancelled(true);
                game.getPlayers().stream().filter(currentAuPlayer -> !currentAuPlayer.isAlive()).forEach(currentAuPlayer -> ((Player) currentAuPlayer.toBukkitPlayer()).sendMessage("§4✖ " + auPlayer.getColor().code + "§m" + player.getName() + "§f: " + e.getMessage()));
            } else if (!game.getState().equals(GameState.IN_PROGRESS)) {
                e.setFormat(auPlayer.getColor().code + player.getName() + "§f: " + e.getMessage());
            } else {
                e.setCancelled(true);
            }
        }

        /**
         * Event triggered when an impostor kills a crewmate.
         *
         * @param e Event
         */
        @EventHandler
        public void onImpostorKill(EntityDamageByEntityEvent e) {
            if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
                Game game = Game.getInstance();
                Player impostor = (Player) e.getDamager();
                Player crewmate = (Player) e.getEntity();
                AmongUsPlayer auImpostor = AmongUsPlayer.getPlayer(impostor.getUniqueId());
                AmongUsPlayer auCrewmate = AmongUsPlayer.getPlayer(crewmate.getUniqueId());
                if (game.getState().equals(GameState.IN_PROGRESS) && auImpostor.isImpostor() && auCrewmate.isCrewmate()) {
                    crewmate.setGameMode(GameMode.SPECTATOR);
                    if (!crewmate.getOpenInventory().getTitle().equals("Crafting")) {
                        crewmate.closeInventory();
                    }
                    auCrewmate.setAlive(false);
                    auCrewmate.createDeadBody();
                    auCrewmate.refresh();
                    game.checkEndGame();
                }
            }
        }

        /**
         * Event triggered when a player discovers a dead body.
         *
         * @param e Event
         */
        @EventHandler
        public void onDiscoverDeadBody(PlayerInteractAtEntityEvent e) {
            if (e.getHand().equals(EquipmentSlot.HAND) && e.getRightClicked().hasMetadata("dead_body")) {
                AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(e.getPlayer().getUniqueId());
                if (auPlayer.isAlive()) {
                    Game game = Game.getInstance();
                    if (game.getState().equals(GameState.IN_PROGRESS)) {
                        new VoteSystem(auPlayer, false).start();
                    }
                }
            }
        }

        /**
         * Event triggered when a player calls an emergency alert.
         *
         * @param e Event
         */
        @EventHandler
        public void onEmergencyCall(PlayerInteractEvent e) {
            Game game = Game.getInstance();
            Player player = e.getPlayer();
            AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
            if (game.getState().equals(GameState.IN_PROGRESS) && auPlayer.isAlive() && e.getHand().equals(EquipmentSlot.HAND) && e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getLocation().equals(ConfigurationManager.getInstance().emergencyLocation)) {
                new VoteSystem(auPlayer, true).start();
            }
        }

        /**
         * Event triggered when a player enters in a vent.
         *
         * @param e Event
         */
        @EventHandler
        public void onVent(PlayerInteractEvent e) {
            Player player = e.getPlayer();
            AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
            if (auPlayer.isImpostor() && auPlayer.isAlive() && e.getHand().equals(EquipmentSlot.HAND) && e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && Game.getInstance().getVentgroup(e.getClickedBlock().getLocation()) != null) {
                Block vent = e.getClickedBlock();
                Location ventLocation = vent.getLocation();
                if (auPlayer.isInVent()) {
                    auPlayer.setCurrentVentGroup(new ArrayList<>());
                    auPlayer.setCurrentVent(null);
                } else {
                    auPlayer.setCurrentVentGroup(Game.getInstance().getVentgroup(ventLocation));
                    auPlayer.setCurrentVent(ventLocation);
                    player.teleport(ventLocation.clone().add(new Vector(0.5, 0.1, 0.5)));
                }
                auPlayer.refresh();
                if (vent.getBlockData() instanceof Openable) {
                    Openable data = (Openable) vent.getBlockData();
                    data.setOpen(true);
                    vent.setBlockData(data);
                    Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.playSound(ventLocation, Sound.BLOCK_WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1.0F, 1.0F));
                    Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(), () -> {
                        data.setOpen(false);
                        vent.setBlockData(data);
                        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.playSound(ventLocation, Sound.BLOCK_WOODEN_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, 1.0F));
                    }, 15L);
                }
            }
        }

        /**
         * Event triggered when a player switches vents.
         *
         * @param e Event
         */
        @EventHandler
        public void onventSwitch(PlayerItemHeldEvent e) {
            Player player = e.getPlayer();
            AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
            if (auPlayer.isInVent()) {
                List<Location> ventGroup = auPlayer.getCurrentVentGroup();
                Location currentVent = auPlayer.getCurrentVent();
                Location newVent = null;
                int previousSlot = e.getPreviousSlot();
                int newSlot = e.getNewSlot();
                if (newSlot < previousSlot) {
                    if (ventGroup.indexOf(currentVent) == 0) {
                        newVent = ventGroup.get(ventGroup.size() - 1);
                    } else {
                        newVent = ventGroup.get(ventGroup.indexOf(currentVent) - 1);
                    }
                } else if (newSlot > previousSlot) {
                    if (ventGroup.indexOf(currentVent) == ventGroup.size() - 1) {
                        newVent = ventGroup.get(0);
                    } else {
                        newVent = ventGroup.get(ventGroup.indexOf(currentVent) + 1);
                    }
                }
                player.teleport(newVent.clone().add(new Vector(0.5, 0.1, 0.5)).setDirection(player.getLocation().getDirection()));
                auPlayer.setCurrentVent(newVent);
            }
        }
    }
}
