package me.hatesvartt.hateclient.client.utils;

public class MapIdStorage {
    // Stores the last received map ID
    private static int lastMapId = -1;

    // Getter
    public static int getLastMapId() {
        return lastMapId;
    }

    // Setter
    public static void setLastMapId(int id) {
        lastMapId = id;
    }
}