package com.minecraft_among_us.plugin.inventories;

import com.minecraft_among_us.plugin.AmongUsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HatInventory extends BaseInventory {

    public HatInventory(AmongUsPlayer auPlayer) {
        super(auPlayer);
    }

    @Override
    public Inventory create() {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.CHEST, "Hats");
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
                new ItemStack(Material.OAK_SLAB)
        });
        return inventory;
    }

    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (e.getView().getTitle().equals("Hats")) {
                e.setCancelled(true);
                Player player = (Player) e.getWhoClicked();
                AmongUsPlayer auPlayer = AmongUsPlayer.getPlayer(player.getUniqueId());
                ItemStack currentItem = e.getCurrentItem();
                if (currentItem != null) {
                    player.getInventory().setHelmet(currentItem);
                    player.closeInventory();
                }
            }
        }
    }
}
