package me.hatesvartt.hateclient.client.settings;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class KeybindSetting extends Setting<Integer> {
    private boolean listening = false;
    private boolean wasPressedLastTick = false;

    public KeybindSetting(String name) {
        super(name, -1); // -1 = None
    }

    public void startListening() { listening = true; }
    public void stopListening() { listening = false; }
    public boolean isListening() { return listening; }

    public void setKey(int key) {
        setValue(key);
        stopListening();
        wasPressedLastTick = false;
    }

    public String getKeyName() {
        if (getValue() == null || getValue() == -1) return "None";
        String keyName = GLFW.glfwGetKeyName(getValue(), 0);
        return keyName != null ? keyName.toUpperCase() : "Unknown";
    }

    /**
     * Consume press for toggle detection
     */
    public boolean consumePress() {
        long window = MinecraftClient.getInstance().getWindow().getHandle();
        boolean currentlyPressed = getValue() != null && getValue() != -1 &&
                GLFW.glfwGetKey(window, getValue()) == GLFW.GLFW_PRESS;

        boolean pressedOnce = currentlyPressed && !wasPressedLastTick;
        wasPressedLastTick = currentlyPressed;
        return pressedOnce;
    }

    /**
     * Assign key with special key handling
     */
    public void assignKey(int keyCode) {
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_SPACE) {
            setValue(-1); // None
        } else {
            setKey(keyCode);
        }
    }
}