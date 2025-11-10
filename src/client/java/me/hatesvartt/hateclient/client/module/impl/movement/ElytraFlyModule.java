package me.hatesvartt.hateclient.client.module.impl.movement;

import me.hatesvartt.hateclient.client.module.*;
import me.hatesvartt.hateclient.client.module.Module;
import me.hatesvartt.hateclient.client.settings.BooleanSetting;
import me.hatesvartt.hateclient.client.settings.IntSetting;
import me.hatesvartt.hateclient.client.settings.ModeSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class ElytraFlyModule extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public ElytraFlyModule() {
        super("Elytra Fly", Category.MOVEMENT);

        this.addSetting(modeSetting);
        this.addSetting(speedSetting);
        this.addSetting(verticalControl);
    }

    // Elytra flight modes
    public enum ElytraMode {
        NORMAL,
        BOOST,
        CONTROL
    }

    // Settings
    private final ModeSetting modeSetting = new ModeSetting(
            "Mode",
            List.of("Normal", "Boost", "Control"),
            "Normal"
    );

    // Override getDisplayString to show multiplier instead of raw value
    private final IntSetting speedSetting = new IntSetting("Speed", 275, 10, 800) {
        public String getDisplayString() {
            return String.format("%s: %.3f", getName(), getValue() / 100.0);
        }
    };

    private final BooleanSetting verticalControl = new BooleanSetting("Vertical Control", true);

    private ElytraMode getMode() {
        return switch (modeSetting.getValue()) {
            case "Boost" -> ElytraMode.BOOST;
            case "Control" -> ElytraMode.CONTROL;
            default -> ElytraMode.NORMAL;
        };
    }

    // Returns the speed multiplier (e.g., 2.000 for 200)
    public double getFormattedSpeed() {
        return speedSetting.getValue() / 100.0;
    }

    @Override
    public void onEnable() {
        //
    }

    @Override
    public void onDisable() {
        ClientPlayerEntity player = mc.player;
        assert player != null;
        player.setNoGravity(false);
    }

    private ElytraMode lastMode = null;

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || !player.checkGliding()) return;

        ElytraMode mode = getMode();

        // Reset gravity if we just left CONTROL mode
        if (lastMode == ElytraMode.CONTROL && mode != ElytraMode.CONTROL) {
            player.setNoGravity(false);
        }

        double speedMultiplier = getFormattedSpeed();
        Vec3d vel = player.getVelocity();

        switch (mode) {
            case NORMAL -> {
                Vec3d forward = player.getRotationVec(0f).multiply(speedMultiplier * 1.5);
                player.setVelocity(forward.x, vel.y, forward.z);
                player.velocityModified = true;
            }

            case BOOST -> {
                Vec3d forward = player.getRotationVec(0f).multiply(speedMultiplier * 1.5);
                double yVel = vel.y + 0.05;
                player.setVelocity(forward.x, yVel, forward.z);
                player.velocityModified = true;
            }

            case CONTROL -> {
                float forwardInput = player.forwardSpeed;
                float sidewaysInput = player.sidewaysSpeed;
                float yawRad = (float) Math.toRadians(player.getYaw());

                double xVel = (forwardInput * -Math.sin(yawRad) + sidewaysInput * Math.cos(yawRad)) * speedMultiplier;
                double zVel = (forwardInput * Math.cos(yawRad) + sidewaysInput * Math.sin(yawRad)) * speedMultiplier;

                double yVel = 0; // hover
                if (verticalControl.getValue()) {
                    if (mc.options.jumpKey.isPressed()) yVel = speedMultiplier;
                    else if (mc.options.sneakKey.isPressed()) yVel = -speedMultiplier;
                }

                player.setVelocity(xVel, yVel, zVel);
                player.velocityModified = true;

                player.fallDistance = 0;
                player.setOnGround(false);
                player.setNoGravity(true);
            }
        }

        lastMode = mode; // update mode tracker
    }
}
