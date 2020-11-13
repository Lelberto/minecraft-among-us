package com.minecraft_among_us.plugin.game;

import org.bukkit.Material;

/**
 * Color enumeration.
 */
public enum Color {
    RED(255, 0, 0, Material.RED_DYE),
    BLUE(0, 0, 255, Material.BLUE_DYE),
    GREEN(0, 255, 0, Material.GREEN_DYE),
    PINK(255, 150, 255, Material.PINK_DYE),
    ORANGE(255, 125, 0, Material.ORANGE_DYE),
    YELLOW(255, 255, 0, Material.YELLOW_DYE),
    BLACK(30, 30, 30, Material.BLACK_DYE),
    WHITE(220, 220, 220, Material.WHITE_DYE),
    PURPLE(150, 0, 150, Material.PURPLE_DYE),
    BROWN(130, 50, 0, Material.BROWN_DYE),
    CYAN(30, 200, 200, Material.CYAN_DYE),
    LIME(100, 230, 50, Material.LIME_DYE);

    public final int red;
    public final int green;
    public final int blue;
    public final Material dye;

    Color(int red, int green, int blue, Material dye) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.dye = dye;
    }
}
