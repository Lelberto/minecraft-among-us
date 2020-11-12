package com.minecraft_among_us.plugin;

import com.minecraft_among_us.plugin.game.Game;
import com.minecraft_among_us.plugin.tasks.Task;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AmongUsPlayer {

    public static AmongUsPlayer getPlayer(UUID uuid) {
        return Game.getInstance().getPlayers().stream().filter(auPlayer -> auPlayer.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    private final UUID uuid;
    private Color color;
    private boolean impostor;
    private boolean alive;
    private List<Task> tasks;
    private List<Location> currentVentGroup;
    private Location currentVent;
    private ItemStack hat;

    public AmongUsPlayer(UUID uuid, Color color) {
        this.uuid = uuid;
        this.color = color;
        this.impostor = false;
        this.alive = true;
        this.tasks = new ArrayList<>();
        this.currentVentGroup = new ArrayList<>();
        this.currentVent = null;
        this.hat = null;
        this.refreshAll();
    }

    public UUID getUuid() {
        return uuid;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void refreshAll() {
        this.refreshEquipment();
        this.refreshBar();
    }

    public void refreshEquipment() {
        PlayerInventory inventory = ((Player) this.toBukkitPlayer()).getInventory();

        ItemStack helmetItem;
        if (this.hat == null) {
            helmetItem = new ItemStack(Material.LEATHER_HELMET);
            LeatherArmorMeta helmetItemMeta = (LeatherArmorMeta) helmetItem.getItemMeta();
            helmetItemMeta.setColor(org.bukkit.Color.fromRGB(color.red, color.green, color.blue));
            helmetItem.setItemMeta(helmetItemMeta);
        } else {
            helmetItem = this.hat.clone();
        }

        ItemStack chestplateItem = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestplateItemMeta = (LeatherArmorMeta) chestplateItem.getItemMeta();
        chestplateItemMeta.setColor(org.bukkit.Color.fromRGB(color.red, color.green, color.blue));
        chestplateItem.setItemMeta(chestplateItemMeta);

        ItemStack leggingsItem = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta leggingsItemMeta = (LeatherArmorMeta) leggingsItem.getItemMeta();
        leggingsItemMeta.setColor(org.bukkit.Color.fromRGB(color.red, color.green, color.blue));
        leggingsItem.setItemMeta(leggingsItemMeta);

        ItemStack bootsItem = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta bootsItemMeta = (LeatherArmorMeta) bootsItem.getItemMeta();
        bootsItemMeta.setColor(org.bukkit.Color.fromRGB(color.red, color.green, color.blue));
        bootsItem.setItemMeta(bootsItemMeta);

        inventory.setHelmet(helmetItem);
        inventory.setChestplate(chestplateItem);
        inventory.setLeggings(leggingsItem);
        inventory.setBoots(bootsItem);
    }

    public void removeEquipment() {
        PlayerInventory inventory = ((Player) this.toBukkitPlayer()).getInventory();
        inventory.setHelmet(null);
        inventory.setChestplate(null);
        inventory.setLeggings(null);
        inventory.setBoots(null);
    }

    public void refreshBar() {
        PlayerInventory inventory = ((Player) this.toBukkitPlayer()).getInventory();

        for (int i = 0; i < 9; i++) {
            ItemStack barItem;
            switch (this.color) {
                case RED:
                    barItem = new ItemStack(Material.RED_DYE);
                    break;
                case BLUE:
                    barItem = new ItemStack(Material.BLUE_DYE);
                    break;
                case GREEN:
                    barItem = new ItemStack(Material.GREEN_DYE);
                    break;
                case PINK:
                    barItem = new ItemStack(Material.PINK_DYE);
                    break;
                case ORANGE:
                    barItem = new ItemStack(Material.ORANGE_DYE);
                    break;
                case YELLOW:
                    barItem = new ItemStack(Material.YELLOW_DYE);
                    break;
                case BLACK:
                    barItem = new ItemStack(Material.BLACK_DYE);
                    break;
                case WHITE:
                    barItem = new ItemStack(Material.WHITE_DYE);
                    break;
                case PURPLE:
                    barItem = new ItemStack(Material.PURPLE_DYE);
                    break;
                case BROWN:
                    barItem = new ItemStack(Material.BROWN_DYE);
                    break;
                case CYAN:
                    barItem = new ItemStack(Material.CYAN_DYE);
                    break;
                case LIME:
                    barItem = new ItemStack(Material.LIME_DYE);
                    break;
                default:
                    barItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                    break;
            }
            ItemMeta barItemMeta = barItem.getItemMeta();
            if (this.impostor) {
                switch (i) {
                    case 0:
                        barItemMeta.setDisplayName("Sabotage reactor");
                        break;
                    default:
                        barItemMeta.setDisplayName("-");
                        break;
                }
            } else {
                barItemMeta.setDisplayName("-");
            }
            barItem.setItemMeta(barItemMeta);
            inventory.setItem(i, barItem);
        }
    }

    public void removeBar() {
        PlayerInventory inventory = ((Player) this.toBukkitPlayer()).getInventory();
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, null);
        }
    }

    public boolean isCrewmate() {
        return !this.impostor;
    }

    public void setCrewmate() {
        this.impostor = false;
    }

    public boolean isImpostor() {
        return this.impostor;
    }

    public void setImpostor() {
        this.impostor = true;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public Task getTask(int taskId) {
        return this.tasks.stream().filter(task -> task.getId() == taskId).findFirst().orElse(null);
    }

    public Task getTask(String taskName) {
        return this.tasks.stream().filter(task -> task.getSettings().name.equals(taskName)).findFirst().orElse(null);
    }

    public List<Location> getCurrentVentGroup() {
        return currentVentGroup;
    }

    public void setCurrentVentGroup(List<Location> currentVentGroup) {
        this.currentVentGroup = currentVentGroup;
    }

    public Location getCurrentVent() {
        return currentVent;
    }

    public void setCurrentVent(Location currentVent) {
        this.currentVent = currentVent;
    }

    public boolean isInVent() {
        return !currentVentGroup.isEmpty();
    }

    public ItemStack getHat() {
        return hat;
    }

    public void setHat(ItemStack hat) {
        this.hat = hat;
    }

    public OfflinePlayer toBukkitPlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onVent(PlayerInteractEvent e) {
            Player player = e.getPlayer();
            AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
            if (auPlayer.isImpostor() && auPlayer.isAlive() && e.getHand().equals(EquipmentSlot.HAND) && e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && Game.getInstance().getVentgroup(e.getClickedBlock().getLocation()) != null) {
                e.setCancelled(true);
                Block vent = e.getClickedBlock();
                Location ventLocation = vent.getLocation();
                if (auPlayer.isInVent()) {
                    auPlayer.setCurrentVentGroup(new ArrayList<>());
                    auPlayer.setCurrentVent(null);
                    auPlayer.refreshEquipment();
                    auPlayer.refreshBar();
                    player.setInvisible(false);
                    player.setWalkSpeed(0.2F);
                    player.setFoodLevel(20);
                    player.removePotionEffect(PotionEffectType.JUMP);
                } else {
                    auPlayer.setCurrentVentGroup(Game.getInstance().getVentgroup(ventLocation));
                    auPlayer.setCurrentVent(ventLocation);
                    auPlayer.removeEquipment();
                    auPlayer.removeBar();
                    player.setInvisible(true);
                    player.setWalkSpeed(0.0F);
                    player.setFoodLevel(6);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999, 200, false, false, false));
                    player.teleport(ventLocation.clone().add(new Vector(0.5, 0.1, 0.5)));
                }
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
