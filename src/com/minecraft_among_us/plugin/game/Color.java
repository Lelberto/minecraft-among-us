package com.minecraft_among_us.plugin.game;

import org.bukkit.Material;

import java.util.Arrays;

/**
 * Color enumeration.
 */
public enum Color {
    RED(255, 0, 0, "§c", Material.RED_DYE, Material.RED_WOOL),
    BLUE(60, 60, 255, "§9", Material.BLUE_DYE, Material.BLUE_WOOL),
    GREEN(40, 140, 50, "§2", Material.GREEN_DYE, Material.GREEN_WOOL),
    PINK(255, 150, 255, "§d", Material.PINK_DYE, Material.PINK_WOOL),
    ORANGE(255, 125, 0, "§6", Material.ORANGE_DYE, Material.ORANGE_WOOL),
    YELLOW(255, 255, 0, "§e", Material.YELLOW_DYE, Material.YELLOW_WOOL),
    BLACK(30, 30, 30, "§8", Material.BLACK_DYE, Material.BLACK_WOOL),
    WHITE(220, 220, 220, "§f", Material.WHITE_DYE, Material.WHITE_WOOL),
    PURPLE(150, 0, 150, "§5", Material.PURPLE_DYE, Material.PURPLE_WOOL),
    BROWN(130, 50, 0, "§7", Material.BROWN_DYE, Material.BROWN_WOOL),
    CYAN(30, 200, 200, "§b", Material.CYAN_DYE, Material.CYAN_WOOL),
    LIME(0, 255, 0, "§a", Material.LIME_DYE, Material.LIME_WOOL);

    /**
     * Gets a color by it's wool.
     *
     * @param wool Wool
     * @return Color with the wool, or {@code null} if the wool is invalid
     */
    public static Color getColorByWool(Material wool) {
        return Arrays.stream(Color.values()).filter(color -> color.wool.equals(wool)).findFirst().orElse(null);
    }

    public final String name;
    public final int red;
    public final int green;
    public final int blue;
    public final String code;
    public final Material dye;
    public final Material wool;

    Color(int red, int green, int blue, String code, Material dye, Material wool) {
        this.name = this.name().substring(0, 1) + this.name().substring(1).toLowerCase();
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.code = code;
        this.dye = dye;
        this.wool = wool;
    }
}
