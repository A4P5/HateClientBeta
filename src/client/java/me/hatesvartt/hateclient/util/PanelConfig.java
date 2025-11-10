package me.hatesvartt.hateclient.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.hatesvartt.hateclient.client.gui.HudPanel;
import me.hatesvartt.hateclient.client.gui.ModuleButton;
import me.hatesvartt.hateclient.client.settings.*;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PanelConfig {
    private static final String FILE = "hateclient_panels.json";
    private static final Gson gson = new Gson();

    // Data class to store panel info
    public static class PanelData {
        public String title;
        public float x, y;
        public List<Boolean> expandedStates;
        public List<List<SettingData>> settingsData; // NEW

        public PanelData(String title, float x, float y, List<Boolean> expandedStates, List<List<SettingData>> settingsData) {
            this.title = title;
            this.x = x;
            this.y = y;
            this.expandedStates = expandedStates;
            this.settingsData = settingsData;
        }
    }

    // Represents a single setting
    public static class SettingData {
        public String name;
        public Object value;

        public SettingData(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }

    public static void save(List<HudPanel> panels) {
        List<PanelData> data = new ArrayList<>();
        for (HudPanel p : panels) {
            List<Boolean> states = new ArrayList<>();
            List<List<SettingData>> allSettings = new ArrayList<>();

            for (ModuleButton b : p.getButtons()) {
                states.add(b.isExpanded());

                List<SettingData> buttonSettings = new ArrayList<>();
                for (var s : b.getModule().getSettings()) {
                    if (s instanceof BooleanSetting bSet) buttonSettings.add(new SettingData(bSet.getName(), bSet.getValue()));
                    else if (s instanceof IntSetting iSet) buttonSettings.add(new SettingData(iSet.getName(), iSet.getValue()));
                    else if (s instanceof ModeSetting mSet) buttonSettings.add(new SettingData(mSet.getName(), mSet.getValue()));
                    else if (s instanceof KeybindSetting kSet) buttonSettings.add(new SettingData(kSet.getName(), kSet.getValue()));
                    else if (s instanceof TextSetting tSet) buttonSettings.add(new SettingData(tSet.getName(), tSet.getValue())); // NEW
                }
                allSettings.add(buttonSettings);
            }

            data.add(new PanelData(p.getTitle(), p.getX(), p.getY(), states, allSettings));
        }

        try (FileWriter writer = new FileWriter(FILE)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load(List<HudPanel> panels) {
        File f = new File(FILE);
        if (!f.exists()) return;

        try (FileReader reader = new FileReader(f)) {
            Type type = new TypeToken<List<PanelData>>() {
            }.getType();
            List<PanelData> data = gson.fromJson(reader, type);
            for (HudPanel p : panels) {
                for (PanelData d : data) {
                    if (p.getTitle().equals(d.title)) {
                        p.setPosition(d.x, d.y);

                        List<ModuleButton> buttons = p.getButtons();

                        // Restore expanded states
                        if (d.expandedStates != null) {
                            for (int i = 0; i < buttons.size() && i < d.expandedStates.size(); i++) {
                                buttons.get(i).setExpanded(d.expandedStates.get(i));
                            }
                        }

                        // Restore settings
                        if (d.settingsData != null) {
                            for (int i = 0; i < buttons.size() && i < d.settingsData.size(); i++) {
                                ModuleButton b = buttons.get(i);
                                List<SettingData> buttonSettings = d.settingsData.get(i);
                                for (SettingData sd : buttonSettings) {
                                    for (Setting<?> s : b.getModule().getSettings()) {
                                        if (s.getName().equals(sd.name)) {
                                            if (s instanceof BooleanSetting boolS) boolS.setValue((Boolean) sd.value);
                                            else if (s instanceof IntSetting intS) intS.setValue(((Double) sd.value).intValue());
                                            else if (s instanceof ModeSetting modeS) modeS.setValue((String) sd.value);
                                            else if (s instanceof KeybindSetting keyS) keyS.setKey(((Double) sd.value).intValue());
                                            else if (s instanceof TextSetting textS) textS.setValue((String) sd.value); // NEW
                                        }
                                    }
                                }
                            }
                        }

                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}