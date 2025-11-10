package me.hatesvartt.hateclient.client.module.impl.movement;

import me.hatesvartt.hateclient.client.module.*;
import me.hatesvartt.hateclient.client.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class NoGravityModule extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public NoGravityModule() {
        super("No Gravity", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        ClientPlayerEntity player = mc.player;
        if (player != null) {
            player.setNoGravity(true); // DISABLE gravity when enabled
        }
    }

    @Override
    public void onDisable() {
        ClientPlayerEntity player = mc.player;
        if (player != null) {
            player.setNoGravity(false); // RESTORE normal gravity
        }
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || !isToggled()) return;

        // Keep gravity disabled every tick
        player.setNoGravity(true);
    }
}
