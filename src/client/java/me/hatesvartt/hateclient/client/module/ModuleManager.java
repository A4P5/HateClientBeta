package me.hatesvartt.hateclient.client.module;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private static final List<Module> modules = new ArrayList<>();

    public static void register(Module module) {
        if (!modules.contains(module)) {
            modules.add(module);
        }
    }

    public static List<Module> getModules() {
        return modules;
    }

    public static <T extends Module> T getModule(Class<T> clazz) {
        for (Module m : modules) {
            if (clazz.isInstance(m)) return clazz.cast(m);
        }
        return null;
    }

    /**
     * Find a module by its name (case-sensitive)
     */
    public static Module getModuleByName(String name) {
        for (Module m : modules) {
            if (m.getName().equals(name)) return m;
        }
        return null;
    }
}
