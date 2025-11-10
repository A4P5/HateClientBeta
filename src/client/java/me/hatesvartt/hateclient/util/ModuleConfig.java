package me.hatesvartt.hateclient.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.hatesvartt.hateclient.client.module.Module;
import me.hatesvartt.hateclient.client.module.ModuleManager;
import me.hatesvartt.hateclient.client.settings.Setting;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class ModuleConfig {

    private static final String FILE = "hateclient_modules.json";
    private static final Gson gson = new Gson();

    /**
     * Save all modules with their toggled states and settings
     */
    public static void save() {
        List<Module> modules = ModuleManager.getModules();
        try (FileWriter writer = new FileWriter(FILE)) {
            gson.toJson(modules.stream().map(m -> {
                return Map.of(
                        "name", m.getName(),
                        "toggled", m.isToggled(),
                        "settings", m.getSettings().stream().collect(
                                java.util.stream.Collectors.toMap(
                                        Setting::getName,
                                        Setting::getValue
                                )
                        )
                );
            }).toList(), writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load modules: toggled states + settings
     */
    public static void load() {
        loadFromFile(true);
    }

    /**
     * Load modules: only settings, without toggling modules
     */
    public static void loadSettingsOnly() {
        loadFromFile(false);
    }

    /**
     * Internal load helper
     */
    @SuppressWarnings("unchecked")
    private static void loadFromFile(boolean loadToggled) {
        File file = new File(FILE);
        if (!file.exists()) return;

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<List<Map<String, Object>>>() {}.getType();
            List<Map<String, Object>> moduleList = gson.fromJson(reader, type);

            for (Map<String, Object> moduleData : moduleList) {
                String moduleName = (String) moduleData.get("name");
                Module m = ModuleManager.getModuleByName(moduleName); // your method
                if (m == null) continue;

                if (loadToggled) {
                    Object toggledVal = moduleData.get("toggled");
                    if (toggledVal instanceof Boolean) m.setToggled((Boolean) toggledVal);
                }

                if (moduleData.containsKey("settings")) {
                    Map<String, Object> settingsMap = (Map<String, Object>) moduleData.get("settings");
                    for (Setting<?> s : m.getSettings()) {
                        Object val = settingsMap.get(s.getName());
                        if (val != null) {
                            ((Setting) s).setValueObject(val); // cast raw type to allow wildcard
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
