package com.minecraft_among_us.plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_16_R3.EnumGamemode;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Net Minecraft Server class.
 *
 * NMS is the low-level of Bukkit servers.
 */
public class Nms {

    /**
     * Initializes the NMS uses for this plugin.
     */
    public static void init() {
        // Change player name in player list when spectator
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Plugin.getPlugin(), PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent e) {
                PacketPlayOutPlayerInfo packet = (PacketPlayOutPlayerInfo) e.getPacket().getHandle();
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction action = (PacketPlayOutPlayerInfo.EnumPlayerInfoAction) getDeclaredField(packet, "a");
                if (action.equals(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE) || action.equals(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER)) {
                    List<PacketPlayOutPlayerInfo.PlayerInfoData> infoList = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getDeclaredField(packet, "b");
                    for (PacketPlayOutPlayerInfo.PlayerInfoData infoData : infoList) {
                        if (infoData.c().equals(EnumGamemode.SPECTATOR) && !(infoData.a().getId().equals(e.getPlayer().getUniqueId()))) {
                            try {
                                modifyFinalField(PacketPlayOutPlayerInfo.PlayerInfoData.class.getDeclaredField("c"), infoData, EnumGamemode.ADVENTURE);
                            } catch (NoSuchFieldException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Gets a field by reflexion.
     *
     * @param object Owner object
     * @param fieldName Field of the owner object
     * @return Field by reflexion
     */
    private static Object getDeclaredField(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Modifies a final field by reflexion.
     *
     * @param field Field to modify
     * @param target Object owner
     * @param newValue New value to set
     */
    private static void modifyFinalField(Field field, Object target, Object newValue) {
        try {
            field.setAccessible(true);
            Field modifierField = Field.class.getDeclaredField("modifiers");
            modifierField.setAccessible(true);
            modifierField.setInt(field, field.getModifiers() & ~ Modifier.FINAL);
            field.set(target, newValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Private constructor.
     *
     * This class can't be instantiated.
     */
    private Nms() {}
}
