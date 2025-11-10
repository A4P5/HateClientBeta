package me.hatesvartt.hateclient.client.module.impl.movement;

import me.hatesvartt.hateclient.client.module.Category;
import me.hatesvartt.hateclient.client.module.Module;
import me.hatesvartt.hateclient.client.settings.IntSetting;
import me.hatesvartt.hateclient.client.settings.ModeSetting;
import me.hatesvartt.hateclient.client.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class BoatFlyModule extends Module {

    private final MinecraftClient mc = MinecraftClient.getInstance();

    // Mode setting as before
    private final ModeSetting modeSetting = new ModeSetting(
            "Mode",
            List.of(new String[]{"Vanilla", "Control", "Boost"}),
            "Vanilla"
    );

    // Plain fields for speed

    public BoatFlyModule() {
        super("BoatFly", Category.MOVEMENT);
        this.addSetting(modeSetting); // optional, you can still use ModeSetting

        this.addSetting(new IntSetting("Speed", 2, 0, 20));
        this.addSetting(new IntSetting("Boost Multiplier", 0, 0, 20));

    }

    @Override
    public void onEnable() {
        if (mc.player != null && mc.player.hasVehicle()) {
            //
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null && mc.player.getVehicle() instanceof BoatEntity boat) {
            boat.setVelocity(Vec3d.ZERO);
        }
    }

    @Override
    public void onTick() {

        int speed = ((IntSetting) getSetting("Speed")).getValue();
        int boostMultiplier = ((IntSetting) getSetting("Boost Multiplier")).getValue();

        if (mc.player == null || !(mc.player.getVehicle() instanceof BoatEntity boat)) return;

        String mode = modeSetting.getValue();
        double actualSpeed = switch (mode) {
            case "Boost" -> speed * boostMultiplier;
            default -> speed;
        };

        boat.setVelocity(Vec3d.ZERO);

        switch (mode) {
            case "Vanilla" -> handleVanilla(boat, actualSpeed);
            case "Control" -> handleControl(boat, actualSpeed);
            case "Boost" -> handleVanilla(boat, actualSpeed);
        }
    }

    private void handleVanilla(BoatEntity boat, double speed) {
        // Get player's look direction
        Vec3d look = mc.player.getRotationVec(1.0F);

        // Forward/backward movement
        if (mc.options.forwardKey.isPressed()) {
            boat.setVelocity(boat.getVelocity().add(look.multiply(speed)));
        }
        if (mc.options.backKey.isPressed()) {
            boat.setVelocity(boat.getVelocity().add(look.multiply(-speed)));
        }

        // Left/right strafing
        Vec3d left = look.crossProduct(new Vec3d(0, 1, 0)).normalize();
        if (mc.options.leftKey.isPressed()) {
            boat.setVelocity(boat.getVelocity().add(left.multiply(-speed)));
        }
        if (mc.options.rightKey.isPressed()) {
            boat.setVelocity(boat.getVelocity().add(left.multiply(speed)));
        }

        // Up/down movement
        if (mc.options.jumpKey.isPressed()) {
            boat.setVelocity(boat.getVelocity().add(0, speed, 0));
        }
        if (mc.options.sneakKey.isPressed()) {
            boat.setVelocity(boat.getVelocity().add(0, -speed, 0));
        }
    }

    private void handleControl(BoatEntity boat, double speed) {
        float yaw = mc.player.getYaw();
        double rad = Math.toRadians(yaw);

        double forward = 0, strafe = 0;
        if (mc.options.forwardKey.isPressed()) forward += speed;
        if (mc.options.backKey.isPressed()) forward -= speed;
        if (mc.options.leftKey.isPressed()) strafe += speed;
        if (mc.options.rightKey.isPressed()) strafe -= speed;

        double motionX = -Math.sin(rad) * forward + Math.cos(rad) * strafe;
        double motionZ = Math.cos(rad) * forward + Math.sin(rad) * strafe;

        double motionY = 0;
        if (mc.options.jumpKey.isPressed()) motionY += speed;
        if (mc.options.sneakKey.isPressed()) motionY -= speed;

        boat.setVelocity(new Vec3d(motionX, motionY, motionZ));
    }
}
