package com.minecraft_among_us.plugin;

import com.minecraft_among_us.plugin.game.Game;
import com.minecraft_among_us.plugin.tasks.SimonTask;
import com.minecraft_among_us.plugin.tasks.Task;
import com.minecraft_among_us.plugin.tasks.TemperatureTask;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AmongUsPlayer {

    public static AmongUsPlayer getPlayer(UUID uuid) {
        return Game.getInstance().getPlayers().stream().filter(auPlayer -> auPlayer.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    private final UUID uuid;
    private Color color;
    private boolean impostor;
    private List<Task> tasks;
    private List<Location> currentVentGroup;
    private ItemStack hat;

    public AmongUsPlayer(UUID uuid, Color color) {
        this.uuid = uuid;
        this.color = color;
        this.impostor = true;
        this.tasks = new ArrayList<>(Arrays.asList(new SimonTask(this), new TemperatureTask(this)));
        this.currentVentGroup = new ArrayList<>();
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
        this.refreshInventory();
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

    public void refreshInventory() {
        PlayerInventory inventory = ((Player) this.toBukkitPlayer()).getInventory();

        ItemStack separatorItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta separatorItemMeta = separatorItem.getItemMeta();
        separatorItemMeta.setDisplayName("-");
        separatorItem.setItemMeta(separatorItemMeta);

        if (this.impostor) {
            if (this.isInVent()) {
                ItemStack previousVentItem = new ItemStack(Material.GRAY_CONCRETE);
                ItemMeta previousVentItemMeta = previousVentItem.getItemMeta();
                previousVentItemMeta.setDisplayName("Previous vent");
                previousVentItem.setItemMeta(previousVentItemMeta);

                ItemStack nextVentItem = new ItemStack(Material.GRAY_CONCRETE);
                ItemMeta nextVentItemMeta = nextVentItem.getItemMeta();
                nextVentItemMeta.setDisplayName("Next vent");
                nextVentItem.setItemMeta(nextVentItemMeta);

                inventory.setItem(18, previousVentItem);
                inventory.setItem(20, nextVentItem);
            }
            inventory.setItem(13, separatorItem);
            inventory.setItem(22, separatorItem);
            inventory.setItem(31, separatorItem);
        } else {

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

    public List<Task> getTasks() {
        return tasks;
    }

    public Task getTask(String taskName) {
        return this.tasks.stream().filter(task -> task.getName().equals(taskName)).findFirst().orElse(null);
    }

    public List<Location> getCurrentVentGroup() {
        return currentVentGroup;
    }

    public void setCurrentVentGroup(List<Location> currentVentGroup) {
        this.currentVentGroup = currentVentGroup;
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
            if (e.getHand().equals(EquipmentSlot.HAND) && e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && Game.getInstance().getVentgroup(e.getClickedBlock().getLocation()) != null) {
                e.setCancelled(true);
                Player player = e.getPlayer();
                AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
                Block vent = e.getClickedBlock();
                Location ventLocation = vent.getLocation();
                if (auPlayer.isInVent()) {
                    auPlayer.setCurrentVentGroup(new ArrayList<>());
                    auPlayer.refreshEquipment();
                    player.setInvisible(false);
                    player.setWalkSpeed(0.2F);
                    player.setFoodLevel(20);
                    player.setCollidable(true);
                    player.removePotionEffect(PotionEffectType.JUMP);
                    player.teleport(ventLocation.add(new Vector(0.0, 0.5, 0.0)));
                } else {
                    auPlayer.setCurrentVentGroup(Game.getInstance().getVentgroup(ventLocation));
                    auPlayer.removeEquipment();
                    player.setInvisible(true);
                    player.setWalkSpeed(0.0F);
                    player.setFoodLevel(6);
                    player.setCollidable(false);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999, 200, false, false));
                    player.teleport(ventLocation.add(new Vector(0.0, 0.5, 0.0)));
                }
                auPlayer.refreshInventory();
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
        public void onOpenInventory(InventoryClickEvent e) {
            if (e.getView().getTitle().equals("Crafting")) {
                Bukkit.broadcastMessage("" + e.getSlot());
                e.setCancelled(true);
                if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {

                }
            }
        }
    }
}
