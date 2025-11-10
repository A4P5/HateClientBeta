package me.hatesvartt.hateclient.util;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.Optional;

public class ModInfoUtil {
    public static String getModName() {
        Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer("hateclient");
        return mod.map(m -> m.getMetadata().getName()).orElse("HateClient");
    }

    public static String getModVersion() {
        Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer("hateclient");
        return mod.map(m -> m.getMetadata().getVersion().getFriendlyString()).orElse("1.0");
    }
}