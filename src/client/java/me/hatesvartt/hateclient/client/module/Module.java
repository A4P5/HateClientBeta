package me.hatesvartt.hateclient.client.module;

import me.hatesvartt.hateclient.client.settings.Setting;
import me.hatesvartt.hateclient.client.settings.KeybindSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    private final String name;
    private final Category category;
    private boolean toggled = false;
    private final List<Setting<?>> settings = new ArrayList<>();
    private KeybindSetting keybind = new KeybindSetting("Keybind"); // default Keybind

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
        addSetting(keybind); // auto add keybind to settings
    }

    // ----------------------
    // Basic getters/setters
    // ----------------------
    public String getName() { return name; }
    public Category getCategory() { return category; }
    public boolean isToggled() { return toggled; }
    public void setToggled(boolean toggled) {
        if (this.toggled == toggled) return;
        this.toggled = toggled;
        if (toggled) onEnable(); else onDisable();
    }
    public void toggle() { setToggled(!isToggled()); }

    public void addSetting(Setting<?> s) { settings.add(s); }
    public List<Setting<?>> getSettings() { return settings; }
    public Setting<?> getSetting(String name) {
        for (Setting<?> s : settings)
            if (s.getName().equalsIgnoreCase(name)) return s;
        return null;
    }

    public KeybindSetting getKeybind() { return keybind; }
    public void setKeybind(KeybindSetting kb) { this.keybind = kb; }

    // ----------------------
    // Keybind handling
    // ----------------------
    public void checkKeybind() {
        // Check old KeyBinding (if used)
        // optional: if you use KeyBinding objects

        // Check KeybindSetting
        if (keybind != null && keybind.consumePress()) {
            toggle();
        }
    }

    // ----------------------
    // Module hooks
    // ----------------------
    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}
    public String onRender(DrawContext context, float tickDelta) {
        return null; // default implementation
    }
}
