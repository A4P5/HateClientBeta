package me.hatesvartt.hateclient.client.module.impl.movement;

import me.hatesvartt.hateclient.client.module.*;
import me.hatesvartt.hateclient.client.module.Module;
import me.hatesvartt.hateclient.client.settings.BooleanSetting;
import me.hatesvartt.hateclient.client.settings.IntSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class StrafeModule extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    public StrafeModule() {
        super("Strafe", Category.MOVEMENT);

        this.addSetting(new IntSetting("Speed Boost", 35, 0, 200));
        this.addSetting(new BooleanSetting("Bhop", false));
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
        if (mc.player != null) {
            if ((mc.player.forwardSpeed != 0 || mc.player.sidewaysSpeed != 0)) {
                if (!mc.player.isSprinting()) {
                    mc.player.setSprinting(true);
                }

                int speedBoost = ((IntSetting) getSetting("Speed Boost")).getValue();
                boolean bhop = ((BooleanSetting) getSetting("Bhop")).getValue();

                mc.player.setVelocity(new Vec3d(0, mc.player.getVelocity().y, 0));
                mc.player.updateVelocity((float) speedBoost / 100, new Vec3d(mc.player.sidewaysSpeed, 0, mc.player.forwardSpeed));

                double vel = Math.abs(mc.player.getVelocity().getX()) + Math.abs(mc.player.getVelocity().getZ());

                if (vel >= 0.12 && mc.player.isOnGround()) {
                    mc.player.updateVelocity(0.1f, new Vec3d(mc.player.sidewaysSpeed, 0, mc.player.forwardSpeed));
                    if (bhop) {
                        mc.player.jump();
                    }
                }
            }
        }
    }
}