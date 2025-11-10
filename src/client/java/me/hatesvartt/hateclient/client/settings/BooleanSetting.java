package me.hatesvartt.hateclient.client.settings;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, boolean defaultValue) {
        super(name, defaultValue); // Pass default value to parent
    }
}