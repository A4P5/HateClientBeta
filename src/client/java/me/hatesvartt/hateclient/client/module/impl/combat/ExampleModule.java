package me.hatesvartt.hateclient.client.module.impl.combat;

import me.hatesvartt.hateclient.client.module.*;
import me.hatesvartt.hateclient.client.module.Module;
import me.hatesvartt.hateclient.client.settings.BooleanSetting;
import net.minecraft.client.MinecraftClient;

public class ExampleModule extends Module {
    public ExampleModule() {
        super("Example Module", Category.COMBAT);

        this.addSetting(new BooleanSetting("example setting", true));
    }

    @Override
    public void onEnable() {
        //
    }

    @Override
    public void onDisable() {
        //
    }

    @Override
    public void onTick() {
        if (!isToggled()) return;

    }
}