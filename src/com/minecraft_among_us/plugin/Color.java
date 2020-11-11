package com.minecraft_among_us.plugin;

public enum Color {
    RED(255, 0, 0),
    BLUE(0, 0, 255),
    GREEN(0, 255, 0),
    PINK(255, 150, 255),
    ORANGE(255, 125, 0),
    YELLOW(255, 255, 0),
    BLACK(30, 30, 30),
    WHITE(220, 220, 220),
    PURPLE(150, 0, 150),
    BROWN(130, 50, 0),
    CYAN(30, 200, 200),
    LIME(100, 230, 50);

    public final int red;
    public final int green;
    public final int blue;

    Color(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
}
