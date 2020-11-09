package com.minecraft_among_us.plugin;

import com.minecraft_among_us.plugin.game.Game;
import com.minecraft_among_us.plugin.tasks.SimonTask;
import com.minecraft_among_us.plugin.tasks.Task;
import com.minecraft_among_us.plugin.tasks.TemperatureTask;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

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

    public AmongUsPlayer(UUID uuid, Color color) {
        this.uuid = uuid;
        setColor(color);
        this.impostor = false;
        this.tasks = new ArrayList<>(Arrays.asList(new SimonTask(this), new TemperatureTask(this)));
    }

    public UUID getUuid() {
        return uuid;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;

        ItemStack helmetItem = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta helmetItemMeta = (LeatherArmorMeta) helmetItem.getItemMeta();
        helmetItemMeta.setColor(org.bukkit.Color.fromRGB(color.red, color.green, color.blue));
        helmetItem.setItemMeta(helmetItemMeta);

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

        PlayerInventory inventory = ((Player) this.toBukkitPlayer()).getInventory();
        inventory.setHelmet(helmetItem);
        inventory.setChestplate(chestplateItem);
        inventory.setLeggings(leggingsItem);
        inventory.setBoots(bootsItem);
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

    public OfflinePlayer toBukkitPlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }
}
