package com.minecraft_among_us.plugin.inventories;

import com.minecraft_among_us.plugin.Plugin;
import com.minecraft_among_us.plugin.game.AmongUsPlayer;
import com.minecraft_among_us.plugin.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * Game settings inventory class.
 */
public class GameSettingsInventory extends BaseInventory {

    /**
     * Creates a new game settings inventory.
     *
     * @param auPlayer Linked player
     */
    public GameSettingsInventory(AmongUsPlayer auPlayer) {
        super(auPlayer);
    }

    @Override
    public Inventory create() {
        Game game = Game.getInstance();
        Inventory inventory = Bukkit.createInventory(null, InventoryType.CHEST, "Game settings");

        ItemStack impostorsItem = new ItemStack(Material.STONE_SWORD);
        ItemMeta impostorsItemMeta = impostorsItem.getItemMeta();
        impostorsItemMeta.setDisplayName("Impostors");
        impostorsItemMeta.setLore(Arrays.asList("§7Number of impostors : §a" + game.getSettings().impostors));
        impostorsItem.setItemMeta(impostorsItemMeta);

        ItemStack confirmEjectsItem = new ItemStack(Material.FEATHER);
        ItemMeta confirmEjectsItemMeta = confirmEjectsItem.getItemMeta();
        confirmEjectsItemMeta.setDisplayName("Confirm ejects");
        confirmEjectsItemMeta.setLore(Arrays.asList("§7Show people role at ejection : §a" + (game.getSettings().confirmEjects ? "On" : "Off")));
        confirmEjectsItem.setItemMeta(confirmEjectsItemMeta);

        ItemStack emergencyMeetingsItem = new ItemStack(Material.CRIMSON_BUTTON);
        ItemMeta emergencyMeetingsItemMeta = emergencyMeetingsItem.getItemMeta();
        emergencyMeetingsItemMeta.setDisplayName("Emergency meetings");
        emergencyMeetingsItemMeta.setLore(Arrays.asList("§7Number of emergency meetings calls per person : §a" + game.getSettings().emergencyMeetings));
        emergencyMeetingsItem.setItemMeta(emergencyMeetingsItemMeta);

        ItemStack emergencyCooldownItem = new ItemStack(Material.SOUL_TORCH);
        ItemMeta emergencyCooldownItemMeta = emergencyCooldownItem.getItemMeta();
        emergencyCooldownItemMeta.setDisplayName("Emergency cooldown");
        emergencyCooldownItemMeta.setLore(Arrays.asList("§7Time (in seconds) before enable emergency calls : §a" + game.getSettings().emergencyCooldown));
        emergencyCooldownItem.setItemMeta(emergencyCooldownItemMeta);

        ItemStack discussionTimeItem = new ItemStack(Material.PAPER);
        ItemMeta discussionTimeItemMeta = discussionTimeItem.getItemMeta();
        discussionTimeItemMeta.setDisplayName("Discussion time");
        discussionTimeItemMeta.setLore(Arrays.asList("§7Time (in seconds) before enable voting time : §a" + game.getSettings().discussionTime));
        discussionTimeItem.setItemMeta(discussionTimeItemMeta);

        ItemStack votingTimeItem = new ItemStack(Material.FILLED_MAP);
        ItemMeta votingTimeItemMeta = votingTimeItem.getItemMeta();
        votingTimeItemMeta.setDisplayName("Voting time");
        votingTimeItemMeta.setLore(Arrays.asList("§7Time (in seconds) for voting time : §a" + game.getSettings().votingTime));
        votingTimeItem.setItemMeta(votingTimeItemMeta);

        ItemStack killCooldownItem = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta killCooldownItemMeta = killCooldownItem.getItemMeta();
        killCooldownItemMeta.setDisplayName("Kill cooldown");
        killCooldownItemMeta.setLore(Arrays.asList("§7Time (in seconds) between two kills for impostors : §a" + game.getSettings().killCooldown));
        killCooldownItem.setItemMeta(killCooldownItemMeta);

        ItemStack commonTasksItem = new ItemStack(Material.WARPED_SIGN);
        ItemMeta commonTasksItemMeta = commonTasksItem.getItemMeta();
        commonTasksItemMeta.setDisplayName("Common tasks");
        commonTasksItemMeta.setLore(Arrays.asList("§7Number of common tasks : §a" + game.getSettings().commonTasks));
        commonTasksItem.setItemMeta(commonTasksItemMeta);

        ItemStack longTasksItem = new ItemStack(Material.SPRUCE_SIGN);
        ItemMeta longTasksItemMeta = longTasksItem.getItemMeta();
        longTasksItemMeta.setDisplayName("Long tasks");
        longTasksItemMeta.setLore(Arrays.asList("§7Number of long tasks : §a" + game.getSettings().longTasks));
        longTasksItem.setItemMeta(longTasksItemMeta);

        ItemStack shortTasksItem = new ItemStack(Material.BIRCH_SIGN);
        ItemMeta shortTasksItemMeta = shortTasksItem.getItemMeta();
        shortTasksItemMeta.setDisplayName("Short tasks");
        shortTasksItemMeta.setLore(Arrays.asList("§7Number of short tasks : §a" + game.getSettings().shortTasks));
        shortTasksItem.setItemMeta(shortTasksItemMeta);

        ItemStack recommendedItem = new ItemStack(Material.SEA_LANTERN);
        ItemMeta recommendedItemMeta = recommendedItem.getItemMeta();
        recommendedItemMeta.setDisplayName("Recommended settings");
        recommendedItemMeta.setLore(Arrays.asList("§7Recommended settings for §a" + game.getPlayers().size() + "§7 players"));
        recommendedItem.setItemMeta(recommendedItemMeta);

        inventory.setItem(0, impostorsItem);
        inventory.setItem(1, confirmEjectsItem);
        inventory.setItem(2, emergencyMeetingsItem);
        inventory.setItem(3, emergencyCooldownItem);
        inventory.setItem(4, discussionTimeItem);
        inventory.setItem(5, votingTimeItem);
        inventory.setItem(6, killCooldownItem);
        inventory.setItem(7, commonTasksItem);
        inventory.setItem(8, longTasksItem);
        inventory.setItem(9, shortTasksItem);
        inventory.setItem(26, recommendedItem);
        return inventory;
    }


    /**
     * Listener subclass.
     */
    public static class Listener implements org.bukkit.event.Listener {

        /**
         * Event triggered when a player interacts with the computer inventory.
         *
         * @param e Event
         */
        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (e.getView().getTitle().equals("Game settings")) {
                e.setCancelled(true);
                if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                    ItemStack currentItem = e.getCurrentItem();
                    if (currentItem != null) {
                        Game game = Game.getInstance();
                        Material currentMaterial = currentItem.getType();
                        if (currentMaterial.equals(Material.STONE_SWORD)) {
                            if (game.getSettings().impostors == 3) {
                                game.getSettings().impostors = 1;
                            } else {
                                game.getSettings().impostors++;
                            }
                            ItemMeta currentItemItemMeta = currentItem.getItemMeta();
                            currentItemItemMeta.setLore(Arrays.asList("§7Number of impostors : §a" + game.getSettings().impostors));
                            currentItem.setItemMeta(currentItemItemMeta);
                        } else if (currentMaterial.equals(Material.FEATHER)) {
                            game.getSettings().confirmEjects = !game.getSettings().confirmEjects;
                            ItemMeta currentItemItemMeta = currentItem.getItemMeta();
                            currentItemItemMeta.setLore(Arrays.asList("§7Show people role at ejection : §a" + (game.getSettings().confirmEjects ? "On" : "Off")));
                            currentItem.setItemMeta(currentItemItemMeta);
                        } else if (currentMaterial.equals(Material.CRIMSON_BUTTON)) {
                            if (game.getSettings().emergencyMeetings == 9) {
                                game.getSettings().emergencyMeetings = 0;
                            } else {
                                game.getSettings().emergencyMeetings++;
                            }
                            ItemMeta currentItemItemMeta = currentItem.getItemMeta();
                            currentItemItemMeta.setLore(Arrays.asList("§7Number of emergency meetings calls per person : §a" + game.getSettings().emergencyMeetings));
                            currentItem.setItemMeta(currentItemItemMeta);
                        } else if (currentMaterial.equals(Material.SOUL_TORCH)) {
                            if (game.getSettings().emergencyCooldown == 40) {
                                game.getSettings().emergencyCooldown = 10;
                            } else {
                                game.getSettings().emergencyCooldown += 2.5;
                            }
                            ItemMeta currentItemItemMeta = currentItem.getItemMeta();
                            currentItemItemMeta.setLore(Arrays.asList("§7Time (in seconds) before enable emergency calls : §a" + game.getSettings().emergencyCooldown));
                            currentItem.setItemMeta(currentItemItemMeta);
                        } else if (currentMaterial.equals(Material.PAPER)) {
                            if (game.getSettings().discussionTime == 30) {
                                game.getSettings().discussionTime = 5;
                            } else {
                                game.getSettings().discussionTime += 5;
                            }
                            ItemMeta currentItemItemMeta = currentItem.getItemMeta();
                            currentItemItemMeta.setLore(Arrays.asList("§7Time (in seconds) before enable voting time : §a" + game.getSettings().discussionTime));
                            currentItem.setItemMeta(currentItemItemMeta);
                        } else if (currentMaterial.equals(Material.FILLED_MAP)) {
                            if (game.getSettings().votingTime == 240) {
                                game.getSettings().votingTime = 30;
                            } else {
                                game.getSettings().votingTime += 15;
                            }
                            ItemMeta currentItemItemMeta = currentItem.getItemMeta();
                            currentItemItemMeta.setLore(Arrays.asList("§7Time (in seconds) for voting time : §a" + game.getSettings().votingTime));
                            currentItem.setItemMeta(currentItemItemMeta);
                        } else if (currentMaterial.equals(Material.GOLDEN_SWORD)) {
                            if (game.getSettings().killCooldown == 40.0) {
                                game.getSettings().killCooldown = 10.0;
                            } else {
                                game.getSettings().killCooldown += 2.5;
                            }
                            ItemMeta currentItemItemMeta = currentItem.getItemMeta();
                            currentItemItemMeta.setLore(Arrays.asList("§7Time (in seconds) between two kills for impostors : §a" + game.getSettings().killCooldown));
                            currentItem.setItemMeta(currentItemItemMeta);
                        } else if (currentMaterial.equals(Material.WARPED_SIGN)) {
                            if (game.getSettings().commonTasks == 2) {
                                game.getSettings().commonTasks = 0;
                            } else {
                                game.getSettings().commonTasks++;
                            }
                            ItemMeta currentItemItemMeta = currentItem.getItemMeta();
                            currentItemItemMeta.setLore(Arrays.asList("§7Number of common tasks : §a" + game.getSettings().commonTasks));
                            currentItem.setItemMeta(currentItemItemMeta);
                        } else if (currentMaterial.equals(Material.SPRUCE_SIGN)) {
                            if (game.getSettings().longTasks == 3) {
                                game.getSettings().longTasks = 0;
                            } else {
                                game.getSettings().longTasks++;
                            }
                            ItemMeta currentItemItemMeta = currentItem.getItemMeta();
                            currentItemItemMeta.setLore(Arrays.asList("§7Number of long tasks : §a" + game.getSettings().longTasks));
                            currentItem.setItemMeta(currentItemItemMeta);
                        } else if (currentMaterial.equals(Material.BIRCH_SIGN)) {
                            if (game.getSettings().shortTasks == 5) {
                                game.getSettings().shortTasks = 0;
                            } else {
                                game.getSettings().shortTasks++;
                            }
                            ItemMeta currentItemItemMeta = currentItem.getItemMeta();
                            currentItemItemMeta.setLore(Arrays.asList("§7Number of short tasks : §a" + game.getSettings().shortTasks));
                            currentItem.setItemMeta(currentItemItemMeta);
                        } else if (currentMaterial.equals(Material.SEA_LANTERN)) {
                            Player player = (Player) e.getWhoClicked();
                            game.getSettings().recommended(game.getPlayers().size());
                            player.sendMessage(Plugin.getPluginNameChat() + "Recommended settings for §a" + Bukkit.getOnlinePlayers().size() + "§r players");
                            player.closeInventory();
                        }
                    }
                }
            }
        }
    }
}
