package com.minecraft_among_us.plugin.inventories;

import com.minecraft_among_us.plugin.AmongUsPlayer;
import org.bukkit.inventory.Inventory;

public abstract class BaseInventory {

    protected AmongUsPlayer auPlayer;

    public BaseInventory(AmongUsPlayer auPlayer) {
        this.auPlayer = auPlayer;
    }

    public abstract Inventory create();
}
