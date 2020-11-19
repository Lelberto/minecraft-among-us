package com.minecraft_among_us.plugin.inventories;

import com.minecraft_among_us.plugin.game.AmongUsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Hat inventory class.
 */
public class HatInventory extends BaseInventory {

    /**
     * Creates the hat inventory.
     *
     * @param auPlayer Linked player
     */
    public HatInventory(AmongUsPlayer auPlayer) {
        super(auPlayer);
    }

    @Override
    public Inventory create() {
        Inventory inventory = Bukkit.createInventory(null, 36, "Hats");
        inventory.setContents(new ItemStack[]{
                new ItemStack(Material.GRASS_BLOCK),
                new ItemStack(Material.BEACON),
                new ItemStack(Material.TNT),
                new ItemStack(Material.DISPENSER),
                new ItemStack(Material.DRAGON_HEAD),
                new ItemStack(Material.OAK_LEAVES),
                new ItemStack(Material.COAL_ORE),
                new ItemStack(Material.IRON_ORE),
                new ItemStack(Material.GOLD_ORE),
                new ItemStack(Material.DIAMOND_ORE),
                new ItemStack(Material.LAPIS_ORE),
                new ItemStack(Material.REDSTONE_ORE),
                new ItemStack(Material.EMERALD_ORE),
                new ItemStack(Material.BOOKSHELF),
                new ItemStack(Material.MYCELIUM),
                new ItemStack(Material.WARPED_NYLIUM),
                new ItemStack(Material.WHITE_CARPET),
                new ItemStack(Material.OAK_STAIRS),
                new ItemStack(Material.OAK_SLAB),
                new ItemStack(Material.BEDROCK),
                new ItemStack(Material.OAK_LOG),
                new ItemStack(Material.MELON),
                new ItemStack(Material.DRIED_KELP_BLOCK),
                new ItemStack(Material.WHITE_STAINED_GLASS),
                new ItemStack(Material.SPONGE),
                new ItemStack(Material.TARGET)
        });

        ItemStack removeItem = new ItemStack(Material.BARRIER);
        ItemMeta removeItemMeta = removeItem.getItemMeta();
        removeItemMeta.setDisplayName("§cRemove hat");
        removeItem.setItemMeta(removeItemMeta);
        inventory.setItem(34, removeItem);

        ItemStack backItem = new ItemStack(Material.STICK);
        ItemMeta backItemMeta = backItem.getItemMeta();
        backItemMeta.setDisplayName("§cBack");
        backItem.setItemMeta(backItemMeta);
        inventory.setItem(35, backItem);

        return inventory;
    }


    /**
     * Listener subclass.
     */
    public static class Listener implements org.bukkit.event.Listener {

        /**
         * Event triggered when a player interacts with the hat inventory.
         *
         * @param e Event
         */
        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (e.getView().getTitle().equals("Hats")) {
                e.setCancelled(true);
                if (e.getAction().equals(InventoryAction.PICKUP_ALL)) {
                    Player player = (Player) e.getWhoClicked();
                    AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
                    ItemStack currentItem = e.getCurrentItem();
                    if (currentItem != null) {
                        Material currentMaterial = currentItem.getType();
                        if (currentMaterial.equals(Material.BARRIER)) {
                            auPlayer.setHat(null);
                            auPlayer.refresh();
                            player.closeInventory();
                        } else if (currentMaterial.equals(Material.STICK)) {
                            player.openInventory(new ComputerInventory(auPlayer).create());
                        } else {
                            auPlayer.setHat(currentItem);
                            auPlayer.refresh();
                            player.closeInventory();
                        }
                    }
                }
            }
        }
    }
}
