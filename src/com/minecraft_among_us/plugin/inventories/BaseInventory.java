package com.minecraft_among_us.plugin.inventories;

import com.minecraft_among_us.plugin.game.AmongUsPlayer;
import org.bukkit.inventory.Inventory;

/**
 * Base inventory class.
 *
 * This class is the base class for inventories.
 */
public abstract class BaseInventory {

    protected AmongUsPlayer auPlayer;

    /**
     * Creates a new base inventory.
     *
     * @param auPlayer Linked player
     */
    public BaseInventory(AmongUsPlayer auPlayer) {
        this.auPlayer = auPlayer;
    }

    /**
     * Creates and returns the inventory.
     *
     * @return Created inventory
     */
    public abstract Inventory create();
}
