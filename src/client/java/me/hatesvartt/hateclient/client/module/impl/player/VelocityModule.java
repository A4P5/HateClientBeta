package me.hatesvartt.hateclient.client.module.impl.player;

import me.hatesvartt.hateclient.client.chat.SendMessage;
import me.hatesvartt.hateclient.client.module.Category;
import me.hatesvartt.hateclient.client.module.Module;
import me.hatesvartt.hateclient.client.settings.BooleanSetting;
import net.minecraft.client.MinecraftClient;

/**
 * Cancels all velocity updates (anti-knockback).
 * Requires VelocityMixin to actually block packets.
 */
public class VelocityModule extends Module {

    private final MinecraftClient mc = MinecraftClient.getInstance();

    public static boolean cancelVelocity = false;
    public static boolean cancelExplosions = false;

    private final BooleanSetting explosions = new BooleanSetting("Explosions", true);
    private final BooleanSetting entities = new BooleanSetting("Entities", true);

    public VelocityModule() {
        super("Velocity", Category.PLAYER);
        this.addSetting(explosions);
        this.addSetting(entities);
    }

    @Override
    public void onEnable() {
        cancelVelocity = entities.getValue();
        cancelExplosions = explosions.getValue();
    }

    @Override
    public void onDisable() {
        cancelVelocity = false;
        cancelExplosions = false;
    }

    @Override
    public void onTick() {}
}
